package com.pa.bid;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.Spannable;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.appsflyer.AppsFlyerLib;
import com.braintreepayments.api.dropin.BraintreePaymentActivity;
import com.braintreepayments.api.dropin.Customization;
import com.braintreepayments.api.dropin.Customization.CustomizationBuilder;
import com.bumptech.glide.Glide;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
//import com.molpay.molpaylib.MOLPayActivity;
//import com.molpay.molpaylib.settings.MerchantInfo;
import com.nanigans.android.sdk.NanigansEventManager;
import com.nanigans.android.sdk.NanigansEventParameter;
import com.pa.common.AccordionGroup;
import com.pa.common.Codes;
import com.pa.common.Config;
import com.pa.common.DocumentType;
import com.pa.common.FormUtils;
import com.pa.common.GlideImageLoader;
import com.pa.common.ImageLoader;
import com.pa.common.ImageLoaderSecure;
import com.pa.common.ImageViewFitWidth;
import com.pa.common.MyFragment;
import com.pa.common.OnFragmentChangeListener;
import com.pa.common.PARestClient;
import com.pa.common.ProfilUtils;
import com.pa.common.TimeUtils;
import com.pa.common.Tracer;
import com.pa.job.FragmentPostOpenBid;
import com.pa.order.FragmentOrder;
import com.pa.order.FragmentOrderDetail;
import com.pa.parser.ParserActivationStatus;
import com.pa.parser.ParserBasicResult;
import com.pa.parser.ParserBidDetail;
import com.pa.parser.ParserBraintreeClientToken;
import com.pa.parser.ParserComment;
import com.pa.parser.ParserDeductCredits;
import com.pa.parser.ParserJob;
import com.pa.parser.ParserMerchantProfile;
import com.pa.parser.ParserPromoCode;
import com.pa.parser.ParserProposal;
import com.pa.parser.ParserServiceRequestDetail;
import com.pa.parser.ParserUserFreeCredits;
import com.pa.parser.ParserValidatePromoCode;
import com.pa.payment.WebscreenPaypal;
import com.pa.pojo.ActivationStatus;
import com.pa.pojo.BidDetail;
import com.pa.pojo.BidItem;
import com.pa.pojo.BraintreeClientToken;
import com.pa.pojo.CommentResult;
import com.pa.pojo.DeductCreditsResult;
import com.pa.pojo.JobItem;
import com.pa.pojo.MerchantProfile;
import com.pa.pojo.MerchantReview;
import com.pa.pojo.PromoCode;
import com.pa.pojo.PromoCodeResult;
import com.pa.pojo.Proposal;
import com.pa.pojo.ProposalComparator;
import com.pa.pojo.ProposalHighestPriceComparator;
import com.pa.pojo.ProposalHighestRatingsComparator;
import com.pa.pojo.ServiceCategory;
import com.pa.pojo.UserFreeCreditsResult;
import com.pa.pojo.UserORM;
import com.pa.quick_action_dialog.ActionItem;
import com.pa.quick_action_dialog.QuickAction;
import com.coolfindservices.androidconsumer.R;
import com.paypal.android.sdk.payments.PayPalAuthorization;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalFuturePaymentActivity;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalProfileSharingActivity;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;

import io.intercom.android.sdk.Intercom;

@SuppressLint("ValidFragment")
public class FragmentBidDetail extends MyFragment implements OnClickListener,
		Config {
	private static final int PAYPALWEB = 222;
	private static final int BRAINTREE = 100;
	DisplayMetrics dm;
	OnFragmentChangeListener listener;
	BidItem item;
	BidDetail detail;
	ArrayList<BidDetail> arrDetail;
	String serial, service_order_serial = "";

	TextView txtSerial, txtBidStatus, txtCloseTime, txtDescription,
			txtTotalBid, txtLowest, txtHighest;

	LinearLayout serviceDetailContent;
	LinearLayout wrapper_merchant_offer;
	LinearLayout contentMerchantOffer;
	LinearLayout wrapperBidResponse;
	LinearLayout wrapper_service_detail;
	LinearLayout wrapperAction;
	LinearLayout wrapperServiceDelivery;
	TextView txtBudget;
	String cancellationPolicy;

	LinearLayout wrapperGallery;
	TextView galleryParent;
	LinearLayout wrapperComment;
	TextView commentParent;
	TextView viewMoreComments;
	LinearLayout contentGallery;
	ImageLoaderSecure imageLoader;
	ImageLoader imageLoaderNormal;
	private GlideImageLoader glideImageLoader;
	QuickAction mQuickAction;
	TextView txtOtherComment;
	ImageViewFitWidth imgLogo;
	TextView tnc;

	String MY_URI;

	// for VO
	TextView txtJobDesc, txtPrice, txtRelatedSerial, txtCompanyName,
			txtCompanyEmail, txtCompanyContact;

	public FragmentBidDetail(BidItem item) {
		this.item = item;
		// serial=item.serial;
	}

	public void pageBack() {
//		listener.doFragmentChange(new FragmentBid(), false, "Fragment Bid");
		getActivity().getSupportFragmentManager().popBackStackImmediate();
//		for(int i = getActivity().getSupportFragmentManager().getBackStackEntryCount(); i > 1; i--)
//			getActivity().getSupportFragmentManager().popBackStackImmediate();
	}

	public FragmentBidDetail(String serial) {
		this.serial = serial;
	}

	public FragmentBidDetail() {

	}

	OnClickListener globalAccordionListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			View parent = (View) v.getParent();
			View content = parent.findViewById((Integer) v.getTag());
			TextView t = (TextView) v;

			if (content.getVisibility() == View.VISIBLE) {
				content.setVisibility(View.GONE);
				t.setCompoundDrawablesWithIntrinsicBounds(null, null,
						getResources().getDrawable(R.drawable.icon_arrow_down),
						null);
			} else {
				content.setVisibility(View.VISIBLE);
				t.setCompoundDrawablesWithIntrinsicBounds(null, null,
						getResources().getDrawable(R.drawable.icon_arrow_up),
						null);

			}
		}
	};

	void getDataV2() {
		Log.d("Flow", "getDataV2() has called");
		loadingInternetDialog.show();
		// Tracer.d(Config.API_BID_DETAIL + "?service_request_serial=" +
		// serial);
		AsyncHttpClient client = new AsyncHttpClient();

		RequestParams params = new RequestParams();
		params.add("service_request_serial", serial);
		params.add("session_username", pref.getPref(Config.PREF_USERNAME));
		params.add("active_session_token",
				pref.getPref(Config.PREF_ACTIVE_SESSION_TOKEN));

		PARestClient.get(pref.getPref(Config.SERVER), Config.API_BID_DETAIL,
				params, new AsyncHttpResponseHandler() {

					@Override
					public void onSuccess(String result) {
						// TODO Auto-generated method stub
						super.onSuccess(result);
						Tracer.d(result);
						loadingInternetDialog.dismiss();

						if ("authentication fail".equals(result)) {
							listener.doLogout();
						} else {
							// TODO Auto-generated method stub
							Tracer.d(result);
							try {

								ParserServiceRequestDetail parser = new ParserServiceRequestDetail(
										result);
								if ("success".equals(parser.status)) {
									Tracer.d(result);
									item = parser.getData();

									String url = MY_URI
											+ parser.service_icon
											+ "?session_username="
											+ pref.getPref(Config.PREF_USERNAME)
											+ "&active_session_token="
											+ pref.getPref(Config.PREF_ACTIVE_SESSION_TOKEN);

//									imageLoaderNormal.DisplayImage(url,
//											getActivity(), imgLogo);
//									imgLogo.setTag(url);
									//	change to Glide for image loading
									glideImageLoader.displayImageGlide(getActivity(), url, R.drawable.default_img, imgLogo);
//									if(TextUtils.isEmpty(parser.service_icon))
//										Glide.with(getActivity()).load(R.drawable.default_img).into(imgLogo);
//									else
//										Glide.with(getActivity()).load(url).into(imgLogo);
									detail = item.arrBidDetail.get(0);
									arrDetail = item.arrBidDetail;
									drawData(detail);

									txtCloseTime
											.setText("Expiring in"
													+ strTimeDiff(
													new Date(),
													new Date(
															1000 * Long
																	.parseLong(item.service_request_stop))));

									txtAddress
											.setText(item.service_request_address
													+ " , "
													+ getCityAddress(
													item.service_request_city,
													item.service_request_state,
													item.service_request_country,
													item.service_request_postal_code));
									txtFirstDate
											.setText(getPreferredTime(item.preferred_time1_start));
									txtSecondDate
											.setText(getPreferredTime(item.preferred_time2_start));
									txtBudget
											.setText(item.service_budget_highest);

									txtSerial.setText(item.serial);
									txtDescription.setText(item.title);
									View view = inflater.inflate(
											R.layout.quickaction_for_title,
											null);
									TextView tv = (TextView) view
											.findViewById(R.id.txt_content);
									tv.setText(item.title);
									mQuickAction.setContentView(view);

									if ("submitted"
											.equals(item.service_request_status)
											|| "under_consideration"
											.equals(item.service_request_status)) {
										txtBidStatus
												.setBackgroundColor(getResources()
														.getColor(
																R.color.pa_blue));
										txtBidStatus.setText("OPEN");
									} else {
										txtBidStatus
												.setBackgroundColor(getResources()
														.getColor(R.color.red));
										txtBidStatus
												.setText(item.service_request_status);
										getProposal();
									}
									cancellationPolicy = parser.cancellation_policy;

									for (String uri : item.arrAttachment) {
										View attach = inflater.inflate(
												R.layout.item_upload_photo,
												null);
										ImageView img = (ImageView) attach
												.findViewById(R.id.img);

										String url2 = PARestClient.getAbsoluteUrl(
												pref.getPref(Config.SERVER),
												Config.API_SERVICE_IMAGE)
												+ "/"
												+ uri
												+ "?active_session_token="
												+ pref.getPref(Config.PREF_ACTIVE_SESSION_TOKEN)
												+ "&session_username="
												+ pref.getPref(Config.PREF_USERNAME);
//										imageLoader.DisplayImage(url2,
//												getActivity(), img);
//										img.setTag(url2);
										//	change to Glide for image loading
										glideImageLoader.displayImageGlide(getActivity(), url2, R.drawable.default_img, img);
//										if(TextUtils.isEmpty(uri))
//											Glide.with(getActivity()).load(R.drawable.default_img).into(img);
//										else
//											Glide.with(getActivity()).load(url2).into(img);

										Tracer.d("debug", url);
										contentGallery.addView(attach);

										attach.setOnClickListener(new OnClickListener() {

											@Override
											public void onClick(View v) {
												// TODO Auto-generated method
												// stub
												showDialogPhotoPreview(v);
											}
										});
									}

									Tracer.d("debug", "attachment size:"
											+ item.arrAttachment.size());
									if (item.arrAttachment.size() == 0) {
										wrapperGallery.setVisibility(View.GONE);
									}
									txtOtherComment.setText(FormUtils
											.printEmptyFormValue(item.other_remarks));

								}

							} catch (Exception e) {
								e.printStackTrace();
							}

						}
					}

					@Override
					public void onFailure(int arg0, Header[] arg1, byte[] arg2,
										  Throwable arg3) {
						// TODO Auto-generated method stub
						super.onFailure(arg0, arg1, arg2, arg3);

						// TODO Auto-generated method stub
						loadingInternetDialog.dismiss();
						AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
								getActivity());

						// set title
						alertDialogBuilder.setTitle("Network Error");

						// set dialog message
						alertDialogBuilder
								.setMessage("Click yes to reload!")
								.setCancelable(true)
								.setPositiveButton("Yes",
										new DialogInterface.OnClickListener() {
											public void onClick(
													DialogInterface dialog,
													int id) {
												// if this button is clicked,
												// close
												// current activity
												getData();
											}
										})
								.setNegativeButton("No",
										new DialogInterface.OnClickListener() {
											public void onClick(
													DialogInterface dialog,
													int id) {
												// if this button is clicked,
												// just close
												// the dialog box and do nothing
												dialog.cancel();
											}
										});

						// create alert dialog
						AlertDialog alertDialog = alertDialogBuilder.create();
						alertDialog.show();

					}

					@Override
					public void onFinish() {
						// TODO Auto-generated method stub
						super.onFinish();
						loadingInternetDialog.dismiss();
					}
				});

	}

	ArrayList<CommentResult.Comment> bidComments = new ArrayList<>();

	void getComments() {
		Log.d("Flow", "getComments() has called");

		RequestParams params = new RequestParams();
		params.add("service_request_serial", item.serial);
		params.add("session_username", pref.getPref(Config.PREF_USERNAME));
		params.add("active_session_token",
				pref.getPref(Config.PREF_ACTIVE_SESSION_TOKEN));

		PARestClient.get(pref.getPref(Config.SERVER),
				Config.API_BID_COMMENTS, params,
				new AsyncHttpResponseHandler() {
					@Override
					public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
						super.onSuccess(statusCode, headers, responseBody);

						Log.d(TAG, "Response " + new String(responseBody));

						CommentResult result = new ParserComment(new String(responseBody))
								.getData();

						bidComments = result.result;

						if (bidComments.size() == 0) return;

						int numberOfCommentToDisplay = 2;
						int start = bidComments.size() - numberOfCommentToDisplay;
						if (start < 0) start = 0;

						for (int i = start; i < bidComments.size(); i++) {
							CommentResult.Comment comment = bidComments.get(i);

							View commentView = View.inflate(getActivity(), R.layout.item_comment, null);

							TextView author = ((TextView) commentView.findViewById(R.id.commentAuthor));
							author.setTextColor(getResources().getColor(R.color.black));
							if (comment.author.equalsIgnoreCase("consumer"))
								author.setText("You");
							else
								author.setText(comment.author);
							((TextView) commentView.findViewById(R.id.commentText)).setText(comment.comment);
							((TextView) commentView.findViewById(R.id.commentDate)).setText(comment.date);

							wrapperComment.addView(commentView);
						}

						if (bidComments.size() > numberOfCommentToDisplay) {
							viewMoreComments.setVisibility(View.VISIBLE);
							viewMoreComments.setText(Html.fromHtml("<u>View " + (bidComments.size() - numberOfCommentToDisplay) + " earlier comments</u>"));
						}

					}

					@Override
					public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
						super.onFailure(statusCode, headers, responseBody, error);

						String errorContent = new String(responseBody);
						Log.d("Comments", "Error " + errorContent);
					}
				});
	}

	void getData() {
		Log.d("Flow", "getData() has called");
		try {
			loadingInternetDialog.show();
			Tracer.d(Config.API_BID_DETAIL + "?service_request_serial="
					+ item.serial);
			AsyncHttpClient client = new AsyncHttpClient();

			RequestParams params = new RequestParams();
			params.add("service_request_serial", item.serial);
			params.add("session_username", pref.getPref(Config.PREF_USERNAME));
			params.add("active_session_token",
					pref.getPref(Config.PREF_ACTIVE_SESSION_TOKEN));

			PARestClient.get(pref.getPref(Config.SERVER),
					Config.API_BID_DETAIL, params,
					new AsyncHttpResponseHandler() {

						@Override
						public void onSuccess(String result) {
							// TODO Auto-generated method stub
							super.onSuccess(result);
							loadingInternetDialog.dismiss();

							if ("authentication fail".equals(result)) {
								listener.doLogout();
							} else {
								// TODO Auto-generated method stub
								Tracer.d(result);

								ParserBidDetail parser = new ParserBidDetail(
										result);
								if ("success".equals(parser.getStatus())) {
									try {
										String url = MY_URI
												+ parser.service_icon
												+ "?session_username="
												+ pref.getPref(Config.PREF_USERNAME)
												+ "&active_session_token="
												+ pref.getPref(Config.PREF_ACTIVE_SESSION_TOKEN);
										Tracer.d("Detail" + url);
//										imgLogo.setTag(url);
//										imageLoaderNormal.DisplayImage(url,
//												getActivity(), imgLogo);
										//	change to Glide for image loading
										glideImageLoader.displayImageGlide(getActivity(),url, R.drawable.default_img, imgLogo);
//										if(TextUtils.isEmpty(parser.service_icon))
//											Glide.with(getActivity()).load(R.drawable.default_img).into(imgLogo);
//										else
//											Glide.with(getActivity()).load(url).into(imgLogo);

										Tracer.d("Sukses bid detail");
										Tracer.d(result);
										Tracer.d("Size"
												+ parser.getArr().size());
										// detail = parser.getArr().get(0);
										item.arrBidDetail = parser.getArr();// new
										// ArrayList<BidDetail>();
										// item.arrBidDetail.add(detail);
										arrDetail = item.arrBidDetail;

										item.highest_bid = (parser.highest_bid);
										item.lowest_bid = (parser.lowest_bid);
										item.bid_count = (parser.bid_count);
										item.state_short = parser.state_short;
										cancellationPolicy = parser.cancellation_policy;

										drawData(detail);
									} catch (Exception e) {
										e.printStackTrace();
									}

								}
							}
						}

						@Override
						public void onFailure(int arg0, Header[] arg1,
											  byte[] arg2, Throwable arg3) {
							// TODO Auto-generated method stub
							super.onFailure(arg0, arg1, arg2, arg3);

							// TODO Auto-generated method stub
							loadingInternetDialog.dismiss();
							AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
									getActivity());

							// set title
							alertDialogBuilder.setTitle("Network Error");

							// set dialog message
							alertDialogBuilder
									.setMessage("Click yes to reload!")
									.setCancelable(true)
									.setPositiveButton(
											"Yes",
											new DialogInterface.OnClickListener() {
												public void onClick(
														DialogInterface dialog,
														int id) {
													// if this button is
													// clicked,
													// close
													// current activity
													getData();
												}
											})
									.setNegativeButton(
											"No",
											new DialogInterface.OnClickListener() {
												public void onClick(
														DialogInterface dialog,
														int id) {
													// if this button is
													// clicked,
													// just close
													// the dialog box and do
													// nothing
													dialog.cancel();
												}
											});

							// create alert dialog
							AlertDialog alertDialog = alertDialogBuilder
									.create();
							alertDialog.show();

						}
					});

			// AsyncHttpGet get = new AsyncHttpGet(getActivity());
			// get.execute(Config.API_BID_DETAIL + "?service_request_serial="
			// + item.serial);
			// get.setOnSuccessListener(new OnSuccessListener() {
			//
			// @Override
			// public void doSuccess(String result) {}
			//
			// @Override
			// public void doServerError() {
			// // TODO Auto-generated method stub
			// loadingInternetDialog.dismiss();
			//
			// }
			//
			// @Override
			// public void doError() {}
			// });
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void drawData(BidDetail detail) {
		analytic.trackScreen("Job Details with Merchant Offers");
		String currency = item.currency;

		// TODO Auto-generated method stub

		// txtPreferredServiceTime.setText(dateToStr(new Date(1000 * Long
		// .parseLong(item.service_request_preferred_time))));
		//
		// txtPriceRange.setText(item.service_budget_lowest + " - "
		// + item.service_budget_highest);
		// txtClosingDate.setText(dateToStr(new Date(1000 * Long
		// .parseLong(item.service_request_stop))));
		// txtTimeLeft.setText(strTimeDiff(new Date(),
		// new Date(1000 * Long.parseLong(item.service_request_stop))));

		txtLowest.setText(FormUtils.printPrice(currency, item.lowest_bid));
		txtHighest.setText(FormUtils.printPrice(currency, item.highest_bid));
		txtTotalBid.setText(item.bid_count);
		serviceDetailContent.removeAllViews();
		if (arrDetail != null) {
			for (int i = 0; i < arrDetail.size(); i++) {
				View v = inflater.inflate(R.layout.item_service_list_v3, null);

				TextView serviceDetail = (TextView) v
						.findViewById(R.id.service_detail);
				Tracer.d(arrDetail.get(i).param_cache);
				serviceDetail.setText(getServiceDetailFromParamCache(arrDetail
						.get(i).param_cache));

				TextView service = (TextView) v.findViewById(R.id.service);
				service.setText(arrDetail.get(i).service_detail_name);

				service.setTag(i + "");
				service.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						// int i = Integer.parseInt((String) v.getTag());
						// showServiceDetail(arrDetail.get(i).service_detail_name,
						// arrDetail.get(i).service_detail_description);
					}

				});

				View view = v.findViewById(R.id.view);

				view.setTag(i + "");
				view.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						int i = Integer.parseInt((String) v.getTag());
						showServiceDetail(arrDetail.get(i).service_detail_name,
								arrDetail.get(i).service_detail_description);
					}

				});

				serviceDetailContent.addView(v);
			}

			analytic.trackScreen("Job Request Details - Open With Bid Submitted");
		}

	}

	Dialog dialogServiceDetailDescription;
	private TextView txtAddress;
	private TextView txtFirstDate;
	private TextView txtSecondDate;

	private void showServiceDetail(String service_name,
								   String service_detail_description) {
		OnClickListener clickListener = new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				switch (v.getId()) {
					case R.id.btnBack:
						dialogServiceDetailDescription.dismiss();
						break;
				}
			}
		};

		View v = inflater.inflate(
				R.layout.fragment_bid_detail_service_description, null);
		v.findViewById(R.id.btnBack).setOnClickListener(clickListener);

		TextView sname, sdescription;
		sname = (TextView) v.findViewById(R.id.txtServiceName);
		sdescription = (TextView) v.findViewById(R.id.txtServiceDescription);

		sname.setText(service_name);
		sdescription.setText(service_detail_description);

		// TODO Auto-generated method stub
		dialogServiceDetailDescription = new Dialog(getActivity(),
				R.style.PauseDialog);

		dialogServiceDetailDescription.getWindow().requestFeature(
				Window.FEATURE_NO_TITLE);
		dialogServiceDetailDescription.getWindow().setLayout(
				WindowManager.LayoutParams.MATCH_PARENT,
				WindowManager.LayoutParams.MATCH_PARENT);
		dialogServiceDetailDescription.setContentView(v);
		dialogServiceDetailDescription.getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

		dialogServiceDetailDescription.show();

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View v = inflater.inflate(R.layout.fragment_service_request_detail,
				null);
		if (null != item && !item.serial.contains("VP-")
				|| !TextUtils.isEmpty(serial)) {
			imgLogo = (ImageViewFitWidth) v.findViewById(R.id.img);

			txtSerial = (TextView) v.findViewById(R.id.txtSerial);
			txtBidStatus = (TextView) v.findViewById(R.id.txtBidStatus);
			txtCloseTime = (TextView) v.findViewById(R.id.txtCloseTime);
			txtDescription = (TextView) v.findViewById(R.id.txtDescription);
			txtHighest = (TextView) v.findViewById(R.id.txtBidHighest);
			txtLowest = (TextView) v.findViewById(R.id.txtBidLowest);
			txtTotalBid = (TextView) v.findViewById(R.id.txtTotalBid);
			txtAddress = (TextView) v.findViewById(R.id.txtAddress);
			txtFirstDate = (TextView) v.findViewById(R.id.txtFirstDate);
			txtSecondDate = (TextView) v.findViewById(R.id.txtSecondDate);
			txtBudget = (TextView) v.findViewById(R.id.txt_budget);
			serviceDetailContent = (LinearLayout) v
					.findViewById(R.id.service_detail_content);

			txtOtherComment = (TextView) v.findViewById(R.id.txtOtherComments);

			v.findViewById(R.id.btnBack).setOnClickListener(this);
			v.findViewById(R.id.btnHome).setOnClickListener(this);
			v.findViewById(R.id.btnCancel2).setOnClickListener(this);
			v.findViewById(R.id.btnNext2).setOnClickListener(this);

			wrapper_merchant_offer = (LinearLayout) v
					.findViewById(R.id.wrapper_merchant_offer);
			contentMerchantOffer = (LinearLayout) v
					.findViewById(R.id.contentMerchantOffer);

			wrapperBidResponse = (LinearLayout) v
					.findViewById(R.id.wrapper_bid_response);
			wrapper_service_detail = (LinearLayout) v
					.findViewById(R.id.wrapper_service_detail);
			wrapperAction = (LinearLayout) v.findViewById(R.id.wrapperAction);
			wrapperServiceDelivery = (LinearLayout) v
					.findViewById(R.id.wrapper_service_delivery_detail);
			wrapperGallery = (LinearLayout) v
					.findViewById(R.id.wrapper_gallery);
			contentGallery = (LinearLayout) v
					.findViewById(R.id.gallery_content);
			galleryParent = (TextView) v.findViewById(R.id.galleryParent);
			wrapperComment = (LinearLayout) v
					.findViewById(R.id.wrapper_comment);
			commentParent = (TextView) v.findViewById(R.id.commentParent);
			viewMoreComments = (TextView) v.findViewById(R.id.view_more_comments);
			viewMoreComments.setOnClickListener(this);
			v.findViewById(R.id.post_new_comment).setOnClickListener(this);

			txtDescription.setOnClickListener(this);
			v.findViewById(R.id.wrapper_refresh).setOnClickListener(
					refreshListener);
			v.findViewById(R.id.refresh).setOnClickListener(refreshListener);
		} else {
			v = inflater.inflate(R.layout.fragment_service_request_detail_vo,
					null);

			v.findViewById(R.id.btnBack).setOnClickListener(this);
			imgLogo = (ImageViewFitWidth) v.findViewById(R.id.img);

			txtSerial = (TextView) v.findViewById(R.id.txtSerial);
			txtBidStatus = (TextView) v.findViewById(R.id.txtBidStatus);
			txtDescription = (TextView) v.findViewById(R.id.txtDescription);
			txtJobDesc = (TextView) v.findViewById(R.id.txtJobDesc);
			txtPrice = (TextView) v.findViewById(R.id.txtPrice);
			txtRelatedSerial = (TextView) v
					.findViewById(R.id.txt_related_serial);
			txtCompanyName = (TextView) v.findViewById(R.id.txtCompanyName);
			txtCompanyEmail = (TextView) v.findViewById(R.id.txtMerchantEmail);
			txtCompanyContact = (TextView) v
					.findViewById(R.id.txtMerchantContact);
			txtCloseTime = (TextView) v.findViewById(R.id.txtCloseTime);
			v.findViewById(R.id.btnOfferVO).setOnClickListener(this);
		}
		return v;
	}

	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
		try {
			if (activity instanceof OnFragmentChangeListener) {
				listener = (OnFragmentChangeListener) activity;
			} else {
				throw new ClassCastException(activity.toString()
						+ " must implemenet OnFragmentChangeListener");

			}
		} catch (Exception e) {
		}

	}

	OnClickListener refreshListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			simpleToast("Refresh Proposal.");
			try {
				// listener.doFragmentChange(
				// new FragmentBidDetail(item),
				// false, "");
				// getProposal();
				if (TextUtils.isEmpty(serial)) {
					serial = item.serial;
				}
				contentGallery.removeAllViews();
				getDataV2();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	};

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		if ((null != item && !item.serial.contains("VP-"))
				|| !TextUtils.isEmpty(this.serial)) {

			init();

			// showPaymentDialog();

			OnClickListener accordionListener = new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					View parent = (View) v.getParent();
					View content = parent.findViewById(R.id.categoryWrapper);
					if (v instanceof TextView) {
						TextView t = (TextView) v;

						if (content.getVisibility() == View.VISIBLE) {
							content.setVisibility(View.GONE);
							t.setCompoundDrawablesWithIntrinsicBounds(
									null,
									null,
									getResources().getDrawable(
											R.drawable.icon_arrow_down), null);
						} else {
							content.setVisibility(View.VISIBLE);
							t.setCompoundDrawablesWithIntrinsicBounds(
									null,
									null,
									getResources().getDrawable(
											R.drawable.icon_arrow_up), null);

						}
					}

					if (v instanceof LinearLayout) {

						LinearLayout l = (LinearLayout) v;
						ImageView arrow = (ImageView) l
								.findViewById(R.id.arrow);

						if (content.getVisibility() == View.VISIBLE) {
							content.setVisibility(View.GONE);
							arrow.setImageResource(R.drawable.icon_arrow_down);
						} else {
							content.setVisibility(View.VISIBLE);
							arrow.setImageResource(R.drawable.icon_arrow_up);

						}
					}

				}
			};
			ActionItem addItem = new ActionItem(1, "Add", getActivity()
					.getResources().getDrawable(R.drawable.icon_add_photo));
			mQuickAction = new QuickAction(getActivity());
			mQuickAction.addActionItem(addItem);

			// wrapper_merchant_offer.findViewById(R.id.catParent).setOnClickListener(
			// accordionListener);

			wrapperBidResponse.findViewById(R.id.catParent).setOnClickListener(
					accordionListener);

			wrapper_service_detail.findViewById(R.id.catParent)
					.setOnClickListener(accordionListener);
			wrapperServiceDelivery.findViewById(R.id.catParent)
					.setOnClickListener(accordionListener);

			galleryParent.setOnClickListener(accordionListener);
			commentParent.setOnClickListener(accordionListener);

			// showMerchantProfilDialog();
			// getMerchantProfil("aircon1");
		} else {
			drawItemVO();
		}
	}

	void init() {
		try {
			if (item != null) {
				drawItem();
				getData();
				getComments();

			} else if (serial.length() > 0) {
				try {
					getDataV2();
					getComments();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	void drawItem() {

		String currency = item.currency;

		for (String uri : item.arrAttachment) {
			View v = inflater.inflate(R.layout.item_upload_photo, null);
			ImageView img = (ImageView) v.findViewById(R.id.img);

			String url = PARestClient.getAbsoluteUrl(
					pref.getPref(Config.SERVER), Config.API_SERVICE_IMAGE)
					+ "/"
					+ uri
					+ "?active_session_token="
					+ pref.getPref(Config.PREF_ACTIVE_SESSION_TOKEN)
					+ "&session_username=" + pref.getPref(Config.PREF_USERNAME);
//			imageLoader.DisplayImage(url, getActivity(), img);
//			img.setTag(url);
			//	change to Glide for image loading
			glideImageLoader.displayImageGlide(getActivity(), uri, R.drawable.default_img, img);
//			if(TextUtils.isEmpty(uri))
//				Glide.with(getActivity()).load(R.drawable.default_img).into(img);
//			else
//				Glide.with(getActivity()).load(url).into(img);

			Tracer.d("debug", url);
			contentGallery.addView(v);

			v.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					showDialogPhotoPreview(v);
				}
			});
		}

		Tracer.d("debug", "attachment size:" + item.arrAttachment.size());
		if (item.arrAttachment.size() == 0) {
			wrapperGallery.setVisibility(View.GONE);
		}
		txtSerial.setText(item.serial);
		txtDescription.setText(item.title);

		if ("submitted".equals(item.service_request_status)
				|| "under_consideration".equals(item.service_request_status)) {
			txtBidStatus.setBackgroundColor(getResources().getColor(
					R.color.pa_blue));
			txtBidStatus.setText("OPEN");
			txtCloseTime.setVisibility(View.VISIBLE);
		} else if ("closed".equals(item.service_request_status)) {
			txtBidStatus.setBackgroundColor(getResources()
					.getColor(R.color.red));
			txtBidStatus.setText("select offer");
		} else {
			txtBidStatus.setBackgroundColor(getResources()
					.getColor(R.color.red));

			txtBidStatus.setText(item.service_request_status);

			txtCloseTime.setVisibility(View.GONE);
		}

		if ("taken".equals(item.service_request_status)
				|| "closed".equals(item.service_request_status)
				|| "canceled".equals(item.service_request_status)
				|| "expired".equals(item.service_request_status)) {
			wrapperAction.setVisibility(View.GONE);
			getProposal();
		} else {
			wrapperAction.setVisibility(View.VISIBLE);

		}

		Long closingTime;
		if ("closed".equals(item.service_request_status))
			closingTime = Long.parseLong(item.updated_at) + 86400; // Add 1 day for offer selection countdown
		else
			closingTime = Long.parseLong(item.service_request_stop);

		txtCloseTime.setText("closing in "
				+ strTimeDiff(
				new Date(),
				new Date(1000 * closingTime)));

		txtAddress.setText(item.service_request_address
				+ " , "
				+ getCityAddress(item.service_request_city,
				item.service_request_state,
				item.service_request_country,
				item.service_request_postal_code));
		txtFirstDate.setText(getPreferredTime(item.preferred_time1_start));
		txtSecondDate.setText(getPreferredTime(item.preferred_time2_start));
		txtBudget.setText(currency + " "
				+ getMoneyValueWithoutZero(item.service_budget_highest));
		// View view=inflater.inflate(R.layout.quickaction_for_title, null);
		// TextView tv=(TextView)view.findViewById(R.id.txt_content);
		// tv.setText(item.title);
		// mQuickAction.setContentView(view);

		// View otherComments=inflater.inflate(R.layout.field_other_comments,
		// null);
		// TextView
		// txtOtherComment=(TextView)otherComments.findViewById(R.id.txtOtherComments);
		txtOtherComment.setText(FormUtils
				.printEmptyFormValue(item.other_remarks));
		// wrapper_service_detail.addView(otherComments);

	}

	void drawItemVO() {
		txtSerial.setText(item.serial);
		txtDescription.setText(item.title);
		txtBidStatus.setBackgroundColor(getResources()
				.getColor(R.color.pa_blue));
		txtBidStatus.setText("OPEN");
		txtCloseTime.setVisibility(View.GONE);

		txtJobDesc.setText(item.description);
		txtPrice.setText(item.currency + " " + item.price);
		txtRelatedSerial.setText(item.service_order_serial);
		txtCompanyName.setText(item.merchant_company_name);
		txtCompanyEmail.setText(item.merchant_email);
		txtCompanyContact.setText(item.merchant_contact);

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		// getData();
		if (item != null)
			changeReadStatus(item.serial, DocumentType.SERVICE_REQUEST);

		imageLoader = new ImageLoaderSecure(getActivity());
		imageLoaderNormal = new ImageLoader(getActivity());
		glideImageLoader = new GlideImageLoader();

//		if ("0".equals(pref.getPref(Config.SERVER))) {
//			MY_URI = "http://" + DOMAIN_DEV + "/";
//
//		} else if ("1".equals(pref.getPref(Config.SERVER))) {
//
//			MY_URI = "http://" + DOMAIN_STAGING + "/";
//
//		}
//		else if ("2".equals(pref.getPref(Config.SERVER))) {
//
//			MY_URI = "http://" + DOMAIN_LIVE + "/";
//
//		}

		MY_URI=PARestClient.getAbsoluteUrl(pref.getPref(Config.SERVER), "");
	}

	@Override
	public void onClick(View v) {		// REAL ONCLICK
		// TODO Auto-generated method stub
		switch (v.getId()) {
			case R.id.btnOfferVO:
				doPayWithBraintree();
				break;
			case R.id.btnBack:
				// if (item != null) {
				// getActivity().getSupportFragmentManager().popBackStack();
				// } else
			{
//			listener.doFragmentChange(new FragmentBid(), true, "");
				getActivity().getSupportFragmentManager().popBackStackImmediate();
			}
			break;

			case R.id.btnHome:
//			((ActivityLanding) getActivity()).selectItem(0);
				for(int i = getActivity().getSupportFragmentManager().getBackStackEntryCount(); i > 1; i--)
					getActivity().getSupportFragmentManager().popBackStackImmediate();
				break;

			case R.id.btnCancel2:
				// close bid
				showCloseDialog();
				break;
			case R.id.btnNext2:
				// cancel Request
				showCancelBid();
				break;
			case R.id.txtDescription:
				View view = inflater.inflate(R.layout.quickaction_for_title, null);
				TextView tv = (TextView) view.findViewById(R.id.txt_content);
				tv.setText(item.title);
				mQuickAction.setRootView(view);

				mQuickAction.show(v);
				break;
			case R.id.post_new_comment:
			case R.id.view_more_comments:
				Fragment fragment = new FragmentBidComment();
				Bundle bundle = new Bundle();
				bundle.putParcelableArrayList(FragmentBidComment.ARG_COMMENTS, bidComments);
				bundle.putString(FragmentBidComment.ARG_SERIAL, item.serial);
				fragment.setArguments(bundle);

				listener.doFragmentChange(fragment, true, "Bid Comment");
				break;
		}
	}

	ArrayList<Proposal> arrProposal = new ArrayList<Proposal>();

	void getProposal() {
		Tracer.d("get proposal");
		loadingInternetDialog.show();
		RequestParams params = new RequestParams();
		params.add("customer_username", pref.getPref(Config.PREF_USERNAME));
		params.add("service_request_serial", item.serial);
		params.add("get_merchant", "true");
		params.add("session_username", pref.getPref(Config.PREF_USERNAME));
		params.add("active_session_token",
				pref.getPref(Config.PREF_ACTIVE_SESSION_TOKEN));

		// AsyncHttpClient client = new AsyncHttpClient();
		PARestClient.get(pref.getPref(Config.SERVER), Config.API_GET_PROPOSAL,
				params, new AsyncHttpResponseHandler() {

					@Override
					public void onFailure(Throwable error, String content) {
						// TODO Auto-generated method stub
						super.onFailure(error, content);
						loadingInternetDialog.dismiss();
					}

					@Override
					public void onSuccess(int statusCode, String content) {
						// TODO Auto-generated method stub
						super.onSuccess(statusCode, content);
						try {
							Tracer.d("Proposal");
							ParserProposal parser = new ParserProposal(content);
							Log.d("Proposal", content);
							if ("success".equals(parser.status)) {
								arrProposal.clear();
								arrProposal = parser.getData();
								Collections.sort(arrProposal,
										new ProposalComparator());

								Log.d("Proposal", "status " + item.service_request_status);
								if (!"expired".equals(item.service_request_status) && !"taken".equals(item.service_request_status))
									drawProposal();
								loadingInternetDialog.dismiss();
							} else

							{
								wrapper_merchant_offer
										.setVisibility(View.VISIBLE);
								if (arrProposal == null
										|| arrProposal.size() == 0) {
									View v = inflater.inflate(
											R.layout.dialog_no_bid, null);
									v.findViewById(R.id.btnNext2)
											.setOnClickListener(
													new OnClickListener() {

														@Override
														public void onClick(
																View v) {
															// TODO
															// Auto-generated
															// method stub
															// listener.doFragmentChange(
															// new
															// FragmentPostOpenBid(),
															// true,
															// "Post open bid");

															ServiceCategory sc = new ServiceCategory();
															sc.id = item.id;
															sc.service_name = item.service_category_name;
															ArrayList<ServiceCategory> arr = new ArrayList<ServiceCategory>();
															arr.add(sc);

															listener.doFragmentChange(new FragmentPostOpenBid(
																	arr,
																	item.currency,
																	item.service_request_country,
																	item.service_request_state,
																	item.state_short,
																	item.service_request_city), true, "");

														}
													});

									((LinearLayout) wrapper_merchant_offer
											.findViewById(R.id.categoryWrapper))
											.addView(v);
								}

							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
	}

	int selectedProposal;
	int currentProposalPage;

	void drawProposal(int page) {
		currentProposalPage = page;

		// Tracer.d("Proposal:" + arrProposal.size());
		if (arrProposal.size() == 0) {
			View v = inflater.inflate(R.layout.dialog_no_bid, null);
			v.findViewById(R.id.btnNext2).setOnClickListener(
					new OnClickListener() {

						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							// listener.doFragmentChange(
							// new FragmentPostOpenBid(), true,
							// "Post open bid");
							Tracer.d("POST REVISE" + detail.service_id);
							ServiceCategory sc = new ServiceCategory();
							sc.id = item.id;
							sc.service_name = item.service_category_name;
							ArrayList<ServiceCategory> arr = new ArrayList<ServiceCategory>();
							arr.add(sc);

							listener.doFragmentChange(new FragmentPostOpenBid(
									arr,
									item.currency,
									item.service_request_country,
									item.service_request_state,
									item.state_short,
									item.service_request_city), true, "");

						}
					});

			((LinearLayout) wrapper_merchant_offer
					.findViewById(R.id.categoryWrapper)).addView(v);

			wrapper_merchant_offer
					.findViewById(R.id.merchantListFooter).setVisibility(View.GONE);

			analytic.trackScreen("Job Request Details - Open - Zero Bid");
		} else {
			String currency = item.currency;

			wrapper_merchant_offer.setVisibility(View.VISIBLE);
			((LinearLayout) wrapper_merchant_offer
					.findViewById(R.id.categoryWrapper)).removeAllViews();

			wrapper_merchant_offer
					.findViewById(R.id.merchantListFooter).setVisibility(View.VISIBLE);

			int rowPerPage = 3;
			int proposalSize = (arrProposal.size() > 30) ? 30 : arrProposal.size(); // Display max 30 merchant offers
			int start = (page-1) * rowPerPage;
			int end = ((start + rowPerPage) > proposalSize) ? proposalSize : (start + rowPerPage);

			for (int i = start; i < end; i++) {
				Proposal item = arrProposal.get(i);
				View v = inflater.inflate(R.layout.item_proposal_list, null);
				TextView name = (TextView) v.findViewById(R.id.txtName);
				RatingBar rating = (RatingBar) v.findViewById(R.id.ratingBar1);
				TextView txtPrice = (TextView) v.findViewById(R.id.txtPrice);

				name.setText(item.merchant_company_name);
				rating.setRating(Float.parseFloat(item.co_overall_rating));
				txtPrice.setText(FormUtils.printPrice(currency, item.total));

				if (i % 2 == 1) {
					v.setBackgroundColor(getResources().getColor(
							R.color.merchant_list_row_oven));
				} else {
					v.setBackgroundColor(getResources().getColor(
							R.color.pa_grey));

				}
				v.setTag(i);
				v.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						selectedProposal = (Integer) v.getTag();
						showMerchantOffer();
					}
				});

				((LinearLayout) wrapper_merchant_offer
						.findViewById(R.id.categoryWrapper)).addView(v);
			}

			// Set selected dot/page
			int numOfDots = (int) Math.ceil((double) proposalSize / rowPerPage);

			LinearLayout.LayoutParams dotLayoutParams = new LinearLayout.LayoutParams(getActivity().getResources().getDimensionPixelSize(R.dimen.pagination_dots),
					getActivity().getResources().getDimensionPixelSize(R.dimen.pagination_dots));

			LinearLayout dotsContainer = ((LinearLayout) wrapper_merchant_offer
					.findViewById(R.id.pagination_dots_container));
			dotsContainer.removeAllViews();
			for (int k = 0; k < numOfDots; k++) {
				ImageView dot = new ImageView(getActivity());
				dot.setLayoutParams(dotLayoutParams);
				if ((k+1) == currentProposalPage)
					dot.setBackgroundResource(R.drawable.button2);
				else
					dot.setBackgroundResource(R.drawable.button);

				dotsContainer.addView(dot);
			}

			// Set total offers text
			if (proposalSize > 1)
				((TextView) wrapper_merchant_offer
						.findViewById(R.id.pagination_text)).setText(arrProposal.size()+" merchant offers");
			else
				((TextView) wrapper_merchant_offer
						.findViewById(R.id.pagination_text)).setText(arrProposal.size()+" merchant offer");

			// Set previous page
			RelativeLayout prevBtn = ((RelativeLayout) wrapper_merchant_offer
					.findViewById(R.id.pagination_prev_btn));
			if (start == 0) {
				prevBtn.setVisibility(View.INVISIBLE);
			} else {
				prevBtn.setVisibility(View.VISIBLE);
				prevBtn.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						drawProposal(currentProposalPage-1);
					}
				});
			}

			// Set next page
			RelativeLayout nextBtn = ((RelativeLayout) wrapper_merchant_offer
					.findViewById(R.id.pagination_next_btn));
			if (end == proposalSize) {
				nextBtn.setVisibility(View.INVISIBLE);
			} else {
				nextBtn.setVisibility(View.VISIBLE);
				nextBtn.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						drawProposal(currentProposalPage+1);
					}
				});
			}
		}
	}

	Dialog dialogMerchantListSort;

	void drawProposal() {
		analytic.trackScreen("Job Request Details - Closed With Merchant Offer");

		drawProposal(1);
		wrapper_merchant_offer
				.findViewById(R.id.btnSortMerchantList).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				View dialog = inflater.inflate(R.layout.dialog_merchant_list_sort, null);
				dialog.findViewById(R.id.btnHighestRatings).setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						Collections.sort(arrProposal,
								new ProposalHighestRatingsComparator());
						drawProposal();
						dialogMerchantListSort.hide();
					}
				});

				dialog.findViewById(R.id.btnHighestPrice).setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						Collections.sort(arrProposal,
								new ProposalHighestPriceComparator());
						drawProposal();
						dialogMerchantListSort.hide();
					}
				});

				dialog.findViewById(R.id.btnLowestPrice).setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						Collections.sort(arrProposal,
								new ProposalComparator());
						drawProposal();
						dialogMerchantListSort.hide();
					}
				});


				dialogMerchantListSort = new Dialog(getActivity(), R.style.PauseDialog);
				dialogMerchantListSort.getWindow().requestFeature(
						Window.FEATURE_NO_TITLE);
				dialogMerchantListSort.getWindow().setLayout(
						WindowManager.LayoutParams.MATCH_PARENT,
						WindowManager.LayoutParams.MATCH_PARENT);
				dialogMerchantListSort.setContentView(dialog);
				dialogMerchantListSort.getWindow().setSoftInputMode(
						WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

				dialogMerchantListSort.show();
			}
		});
	}

	Dialog dialogMerchantOffer;
	EditText inputPromoCode;
	float discountedAmount = 0;
	String validatedPromoCode = "", creditSerial = "";
	View containerFreeCredits, containerPromoCode;
	TextView freeCredits;
	Button btnCredits;
	AccordionGroup accordionGroup;
	boolean isNoPayment = false;

	void showMerchantOffer() {
		analytic.trackScreen("Merchant Offer Summary");

		final Proposal proposal = arrProposal.get(selectedProposal);

		discountedAmount = 0;
		validatedPromoCode = "";
		creditSerial = "";

		View v = inflater.inflate(R.layout.dialog_merchant_offer_detail, null);
		tnc = (TextView) v.findViewById(R.id.tnc);
		final String strTnc = "Cancellation policy";
		Spannable span = Spannable.Factory.getInstance().newSpannable(strTnc);
		span.setSpan(new ClickableSpan() {
			@Override
			public void onClick(View v) {
				// Log.d("main", "link clicked");
				// simpleToast("Tnc");
				// showTNC(link1,getResources().getString(R.string.link1));
				String cancel_id = "";
				for (BidDetail bd : arrDetail) {
					cancel_id += bd.service_id + ",";
				}

				showWebDialog(
						"http://pageadvisor.bounche.com/transaction/cancellation-policy?service_id="
								+ cancel_id, strTnc);
			}
		}, 0, strTnc.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

		tnc.setText(span);
		tnc.setMovementMethod(LinkMovementMethod.getInstance());

		TextView titleService = (TextView) v.findViewById(R.id.title_service);
		TextView titleServiceDelivery = (TextView) v
				.findViewById(R.id.title_service_detail);
		TextView titleCancel = (TextView) v.findViewById(R.id.title_cancel);

		titleService.setTag(R.id.wrapper_service2);
		titleServiceDelivery.setTag(R.id.wrapper_service_detail2);
		titleCancel.setTag(R.id.wrapper_cancellation2);

		titleService.setOnClickListener(globalAccordionListener);
		titleServiceDelivery.setOnClickListener(globalAccordionListener);
		titleCancel.setOnClickListener(globalAccordionListener);

		TextView txtName = (TextView) v.findViewById(R.id.txtName);
		txtName.setText(proposal.merchant_company_name);

		RatingBar rating = (RatingBar) v.findViewById(R.id.ratingBar1);
		rating.setNumStars(5);
		rating.setRating(Float.parseFloat(proposal.co_overall_rating));
		LayerDrawable stars = (LayerDrawable) rating.getProgressDrawable();
		stars.getDrawable(2).setColorFilter(
				getResources().getColor(R.color.pa_orange),
				PorterDuff.Mode.SRC_ATOP);

		TextView ratingMsg = (TextView) v.findViewById(R.id.rating_msg);
		ratingMsg.setText(proposal.co_overall_rating + " out of 5");

		TextView totalReview = (TextView) v.findViewById(R.id.txtTotalReview);
		totalReview.setText(proposal.co_rating_count + " review(s)");

		UserORM user = new ProfilUtils(getActivity()).getUser();
		String currency = "";
		if (user != null) {
			currency = user.result.currency;
		}

		if (TextUtils.isEmpty(currency)) {
			currency = item.currency;
		}

		final String displayedPrice = currency + " " + proposal.total;
		final TextView txtPrice = (TextView) v.findViewById(R.id.txtPrice);
		txtPrice.setText(displayedPrice);

		TextView txtRemarks = (TextView) v.findViewById(R.id.txtRemarks);
		txtRemarks.setText(TextUtils.isEmpty(proposal.service_description) ?
				"No remarks from merchant" :
				proposal.service_description);

		TextView txtCancel = (TextView) v.findViewById(R.id.txtCancel);
		txtCancel.setText(FormUtils.printEmptyFormValue(cancellationPolicy));

		v.findViewById(R.id.btnOffer).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				if (isNoPayment) {
					doNoPayment();
					return;
				}

				// TODO Auto-generated method stub
				// showConfirmAMerchant();
				// dialogMerchantOffer.hide();
				AlertDialog.Builder builder = new AlertDialog.Builder(
						getActivity());
				builder.setTitle("Alert");
				builder.setMessage("By Accepting this offer you will be charged ( "
						+ txtPrice.getText().toString()
						+ " ) for this job to your credit card. Click yes to confirm.");
				builder.setNegativeButton("YES",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
												int which) {
								// TODO Auto-generated method stub
								// doOrder();
								doPayment();
							}
						});

				builder.setPositiveButton("NO",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
												int which) {
								// TODO Auto-generated method stub
								dialog.dismiss();
							}
						});

				builder.show();
			}
		});

		if ("taken".equals(item.service_request_status)) {
			v.findViewById(R.id.btnOffer).setVisibility(View.GONE);
		}

		// Proposal proposal = arrProposal.get(selectedProposal);

		TextView txtAddress = (TextView) v.findViewById(R.id.txt_address);
		TextView txtFirstPreferDate = (TextView) v
				.findViewById(R.id.txt_first_prefer_date);
		LinearLayout wrapperServiceDetail = (LinearLayout) v
				.findViewById(R.id.wrapperServiceDetail);
		txtAddress.setText(item.service_request_address
				+ ","
				+ getCityAddress(item.service_request_city,
				item.service_request_state,
				item.service_request_country,
				item.service_request_postal_code));

		txtFirstPreferDate
				.setText(getPreferredTime(proposal.preferred_time1_start,
						proposal.preferred_time2_start));

		TextView txtDate = (TextView) v.findViewById(R.id.txtDate);
		txtDate.setText(getPreferredTime(proposal.preferred_time1_start,
				proposal.preferred_time2_start));

		try {
			for (int i = 0; i < arrDetail.size(); i++) {
				View v2 = inflater.inflate(R.layout.item_service_detail, null);

				TextView service = (TextView) v2.findViewById(R.id.txtName);
				service.setText(arrDetail.get(i).service_detail_name);

				TextView description = (TextView) v2
						.findViewById(R.id.txtDescription);
				description.setText(getServiceDetailFromParamCache(arrDetail
						.get(i).param_cache));

				wrapperServiceDetail.addView(v2);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		View otherComments = inflater.inflate(R.layout.field_other_comments,
				null);
		TextView txtOtherComment = (TextView) otherComments
				.findViewById(R.id.txtOtherComments);
		txtOtherComment.setText(FormUtils
				.printEmptyFormValue(item.other_remarks));
		wrapperServiceDetail.addView(otherComments);


		//verified
		View verified=v.findViewById(R.id.verified);
		if("Y".equals(proposal.is_verified)){
			verified.setVisibility(View.VISIBLE);
		}else{
			verified.setVisibility(View.GONE);

		}
		v.findViewById(R.id.btnBack).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialogMerchantOffer.hide();
				hideKeyboard(inputPromoCode);
			}
		});
		v.findViewById(R.id.btnBack2).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialogMerchantOffer.hide();
			}
		});
		v.findViewById(R.id.btnMerchantProfile).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						getMerchantProfil(proposal.merchant_username);
					}
				});

		LinearLayout merchantGallery = (LinearLayout) v.findViewById(R.id.merchant_gallery);
		for (String uri : proposal.arrAttachment) {
			View vPhoto = inflater.inflate(R.layout.item_upload_photo, null);
			ImageView img = (ImageView) vPhoto.findViewById(R.id.img);

			String url = PARestClient.getAbsoluteUrl(
					pref.getPref(Config.SERVER), Config.API_SERVICE_IMAGE)
					+ "/"
					+ uri
					+ "?active_session_token="
					+ pref.getPref(Config.PREF_ACTIVE_SESSION_TOKEN)
					+ "&session_username=" + pref.getPref(Config.PREF_USERNAME);
//            imageLoader.DisplayImage(url, getActivity(), img);
//            img.setTag(url);
			//	change to Glide for image loading
			glideImageLoader.displayImageGlide(getActivity(), url, R.drawable.default_img, img);
//			if(TextUtils.isEmpty(uri))
//				Glide.with(getActivity()).load(R.drawable.default_img).into(img);
//			else
//				Glide.with(getActivity()).load(url).into(img);

			Tracer.d("debug", url);
			merchantGallery.addView(vPhoto);

			vPhoto.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					showDialogPhotoPreview(v);
				}
			});
		}

		if (merchantGallery.getChildCount() < 1)
			((View)merchantGallery.getParent()).setVisibility(View.GONE);

		inputPromoCode = (EditText) v.findViewById(R.id.inputPromoCode);
		v.findViewById(R.id.btnPromoCode).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String promoCode = inputPromoCode.getText().toString();

				if ("".equals(promoCode)) {
					showMessageDialog("The Promo Code field is required");
					return;
				}

				hideKeyboard(inputPromoCode);

				validatePromoCode(promoCode, v.getRootView());
			}
		});

		accordionGroup = new AccordionGroup((LinearLayout) v.findViewById(R.id.accordion));
		accordionGroup.setup();

		containerFreeCredits = v.findViewById(R.id.containerFreeCredits);
		containerPromoCode = v.findViewById(R.id.containerPromoCode);
		freeCredits = (TextView) v.findViewById(R.id.freeCredits);
		btnCredits = (Button) v.findViewById(R.id.btnCredits);
		btnCredits.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				deductCredits(v.getRootView());
			}
		});

		dialogMerchantOffer = new Dialog(getActivity(), R.style.PauseDialog);

		dialogMerchantOffer.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		dialogMerchantOffer.getWindow().setLayout(
				WindowManager.LayoutParams.MATCH_PARENT,
				WindowManager.LayoutParams.MATCH_PARENT);
		dialogMerchantOffer.setContentView(v);
		dialogMerchantOffer.getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

		dialogMerchantOffer.show();

		getUserFreeCredits();
	}

	private void getUserFreeCredits() {
		RequestParams params = new RequestParams();
		params.add("session_username", pref.getPref(Config.PREF_USERNAME));
		params.add("active_session_token",
				pref.getPref(Config.PREF_ACTIVE_SESSION_TOKEN));

		if ("sgd".equals(item.currency.toLowerCase())) {
			params.add("country", "sg");
		} else if ("myr".equals(item.currency.toLowerCase())) {
			params.add("country", "my");
		}

		Log.d("FreeCredits", "getUserFreeCredits");

		PARestClient.get(pref.getPref(Config.SERVER),
				Config.API_GET_USER_FREE_CREDITS,
				params,
				new AsyncHttpResponseHandler() {
					@Override
					public void onStart() {
						super.onStart();
						loadingInternetDialog.show();
					}

					@Override
					public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
						super.onSuccess(arg0, arg1, arg2);
						Log.d("FreeCredits", new String(arg2));
						UserFreeCreditsResult result = new ParserUserFreeCredits(new String(arg2))
								.getData();
						if ("success".equals(result.status)) {
							freeCredits.setText(item.currency + " " + result.result.cs_credit);
						}
						loadingInternetDialog.hide();
					}

					@Override
					public void onFailure(int arg0, Header[] arg1, byte[] arg2,
										  Throwable arg3) {
						super.onFailure(arg0, arg1, arg2, arg3);
						loadingInternetDialog.dismiss();
					}
				});
	}

	private void deductCredits(final View parentView) {
		RequestParams params = new RequestParams();
		params.add("service_proposal_serial", arrProposal.get(selectedProposal).serial);
		params.add("session_username", pref.getPref(Config.PREF_USERNAME));
		params.add("active_session_token",
				pref.getPref(Config.PREF_ACTIVE_SESSION_TOKEN));

		Log.d("FreeCredits", "deductCredits");

		PARestClient.post(pref.getPref(Config.SERVER),
				Config.API_DEDUCT_CREDITS,
				params,
				new AsyncHttpResponseHandler() {
					@Override
					public void onStart() {
						super.onStart();
						btnCredits.setEnabled(false);
						loadingInternetDialog.show();
					}

					@Override
					public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
						super.onSuccess(arg0, arg1, arg2);
						Log.d("FreeCredits", new String(arg2));
						DeductCreditsResult dcr = new ParserDeductCredits(new String(arg2))
								.getData();
						if ("success".equals(dcr.status)) {
							freeCredits.setText(item.currency + " " + dcr.result.balance_credit);

							discountedAmount = Float.parseFloat(dcr.result.final_amount);

							TextView txtPrice = (TextView) parentView.findViewById(R.id.txtPrice);
							txtPrice.setText(item.currency + " " + dcr.result.final_amount);
							txtPrice.setTextColor(getResources().getColor(R.color.red));

							parentView.findViewById(R.id.txtCredits).setVisibility(View.VISIBLE);
							parentView.findViewById(R.id.txtBalanceCredits).setVisibility(View.VISIBLE);
							parentView.findViewById(R.id.txtAvailableCredits).setVisibility(View.GONE);
							parentView.findViewById(R.id.btnCredits).setVisibility(View.GONE);

							creditSerial = dcr.result.credit_serial;

							accordionGroup.disable();

							isNoPayment = Codes.NO_PAYMENT.equals(dcr.result.condition);
						} else if ("failed".equals(dcr.status)) {
							showMessageDialog(dcr.reason);
						}
						btnCredits.setEnabled(true);
						loadingInternetDialog.hide();
					}

					@Override
					public void onFailure(int arg0, Header[] arg1, byte[] arg2,
										  Throwable arg3) {
						super.onFailure(arg0, arg1, arg2, arg3);
						Log.d("FreeCredits", "Error " + new String(arg2));
						btnCredits.setEnabled(true);
						loadingInternetDialog.dismiss();
					}
				});
	}

	private void doNoPayment() {
		String country = "sg";

		if ("sgd".equals(item.currency.toLowerCase())) {
			country = "sg";
		} else if ("myr".equals(item.currency.toLowerCase())) {
			country = "my";
		}

		String amount, serial;

		if (item.serial.contains("VP-")) {
			amount = item.price;
			serial = item.serial;
		} else {
			amount = getAmount();
			serial = (arrProposal.get(selectedProposal).serial);
		}

		RequestParams params = new RequestParams();
		params.add("session_username", pref.getPref(Config.PREF_USERNAME));
		params.add("active_session_token",
				pref.getPref(Config.PREF_ACTIVE_SESSION_TOKEN));
		params.add("amount", amount);
		params.add("service_proposal_serial", serial);
		params.add("country", country);
		params.add("promocode", validatedPromoCode);
		params.add("credit_serial", creditSerial);

		PARestClient.post(pref.getPref(Config.SERVER), Config.API_CREATE_TRANSACTION,
				params, new AsyncHttpResponseHandler() {

					@Override
					public void onStart() {
						super.onStart();
						loadingInternetDialog.show();
					}

					public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
						ParserBasicResult parser = new ParserBasicResult(
								new String(arg2));
						Log.d("Payment", "Result " + new String(arg2));
						if ("success".equals(parser.getStatus())) {

							showDialogOrderSuccess();

						} else {
							Log.d("Payment", "Cancel because it is not success");

							showDialogOrderCancel();

						}
						loadingInternetDialog.dismiss();
					}

					;

					@Override
					public void onFailure(int arg0, Header[] arg1, byte[] arg2,
										  Throwable arg3) {
						// TODO Auto-generated method stub
						super.onFailure(arg0, arg1, arg2, arg3);
						showDialogOrderSuccess();
						loadingInternetDialog.dismiss();

					}

				});
	}

	private void doPayment(){

		analytic.trackScreen("Payment");

		if ("SGD".equals(item.currency)) {
			// doPayWithPaypal();

			// doPayWithPaypalWeb();
			doPayWithBraintree();
		} else if ("MYR".equals(item.currency)) {
			doPayWithBraintree();
			// doPayViaMolpay();
			// doPayWithPaypalMYR();
		}
	}

	String braintreeClientToken = "";
	BraintreeClientToken braintree;

	void doPayWithBraintree() {
		analytic.trackScreen("Job Offer - Payment");

		UserORM user 	= new ProfilUtils(getActivity()).getUser();
		String country 	= "sg";
		if ("sgd".equals(item.currency.toLowerCase())) {
			country = "sg";
		} else if ("myr".equals(item.currency.toLowerCase())) {
			country = "my";
		}

		RequestParams params = new RequestParams();
		params.add("session_username", pref.getPref(Config.PREF_USERNAME));
		params.add("active_session_token",
				pref.getPref(Config.PREF_ACTIVE_SESSION_TOKEN));
		params.add("country", country);

		AsyncHttpResponseHandler responseHandler = new AsyncHttpResponseHandler() {
			@Override
			public void onStart() {
				// TODO Auto-generated method stub
				super.onStart();
				forceLoadingInternetDialog.show();
			}

			@Override
			public void onFailure(int statusCode, Header[] headers,
								  Throwable error, String content) {
				// TODO Auto-generated method stub
				super.onFailure(statusCode, headers, error, content);
				forceLoadingInternetDialog.hide();
			}

			@Override
			public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
				// TODO Auto-generated method stub
				super.onSuccess(arg0, arg1, arg2);
				forceLoadingInternetDialog.hide();

				ParserBraintreeClientToken parser = new ParserBraintreeClientToken(
						new String(arg2));

				BraintreeClientToken bt = parser.getData();

				if (bt != null && "success".equals(bt.status)) {
					braintree = bt;
					braintreeClientToken = bt.result;
					onBraintreeSubmit();
				} else {
					simpleToast("Failed");
				}
			}

			@Override
			public void onFailure(int arg0, Header[] arg1, byte[] arg2,
								  Throwable arg3) {
				// TODO Auto-generated method stub
				super.onFailure(arg0, arg1, arg2, arg3);
				simpleToast("Failed " + new String(arg2));

			}

		};

		PARestClient.get(pref.getPref(Config.SERVER), "braintree/client-token",
				params, responseHandler);

	}

	private String getAmount() {
		if (item.serial.contains("VP-"))
			return item.price;
		else
			return "" + (discountedAmount > 0 ?
					discountedAmount :
					Float.parseFloat(arrProposal.get(selectedProposal).total));
	}

	public void onBraintreeSubmit() {
		String amount = getAmount();

		Intent intent = new Intent(getActivity(),
				BraintreePaymentActivity.class);
		Customization customization = new CustomizationBuilder()
				.primaryDescription("Page Advisor | Order Id:" + item.serial)
				.secondaryDescription(item.title)
				.amount(item.currency + " " + amount)
				.submitButtonText("Pay Now").build();
		intent.putExtra(BraintreePaymentActivity.EXTRA_CUSTOMIZATION,
				customization);

		intent.putExtra(BraintreePaymentActivity.EXTRA_CLIENT_TOKEN,
				braintreeClientToken);

		startActivityForResult(intent, BRAINTREE);
	}

	void doPayWithPaypalWeb() {
		WebscreenPaypal wsPaypal = new WebscreenPaypal();
		wsPaypal.init(MY_URI + "paypal/session-call?service_proposal_serial="
				+ arrProposal.get(selectedProposal).serial);
		startActivityForResult(wsPaypal.show(getActivity()), PAYPALWEB);
	}

	Dialog dialogConfirmAMerchant;

	void showConfirmAMerchant() {
		try {
			View v = inflater.inflate(R.layout.dialog_merchant_offer_confirm,
					null);

			Proposal proposal = arrProposal.get(selectedProposal);

			TextView txtAddress = (TextView) v.findViewById(R.id.txt_address);
			TextView txtFirstPreferDate = (TextView) v
					.findViewById(R.id.txt_first_prefer_date);
			TextView txtSecondPreferDate = (TextView) v
					.findViewById(R.id.txt_second_prefer_date);
			LinearLayout wrapperServiceDetail = (LinearLayout) v
					.findViewById(R.id.wrapperServiceDetail);
			TextView txtCat = (TextView) v.findViewById(R.id.txtCat);

			txtCat.setText(item.title);
			txtAddress.setText(proposal.merchant_address);

			txtFirstPreferDate
					.setText(getPreferredTime(proposal.preferred_time1_start));
			txtSecondPreferDate
					.setText(getPreferredTime(proposal.preferred_time2_start));

			for (int i = 0; i < arrDetail.size(); i++) {
				View v2 = inflater.inflate(R.layout.item_service_detail, null);

				TextView service = (TextView) v2.findViewById(R.id.txtName);
				service.setText(arrDetail.get(i).service_detail_name);

				TextView description = (TextView) v2
						.findViewById(R.id.txtDescription);
				description.setText(getServiceDetailFromParamCache(arrDetail
						.get(i).param_cache));

				wrapperServiceDetail.addView(v2);
			}

			v.findViewById(R.id.btnBack).setOnClickListener(
					new OnClickListener() {

						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							dialogConfirmAMerchant.hide();
							showMerchantOffer();
						}
					});
			v.findViewById(R.id.btnCancel2).setOnClickListener(
					new OnClickListener() {

						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							dialogConfirmAMerchant.hide();
							showMerchantOffer();
						}
					});

			v.findViewById(R.id.btnNext2).setOnClickListener(
					new OnClickListener() {

						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							// dialogConfirmAMerchant.hide();
							doOrder();
							// showPromoCodeDialog();

						}
					});

			dialogConfirmAMerchant = new Dialog(getActivity(),
					R.style.PauseDialog);

			dialogConfirmAMerchant.getWindow().requestFeature(
					Window.FEATURE_NO_TITLE);
			dialogConfirmAMerchant.getWindow().setLayout(
					WindowManager.LayoutParams.MATCH_PARENT,
					WindowManager.LayoutParams.MATCH_PARENT);
			dialogConfirmAMerchant.setContentView(v);
			dialogConfirmAMerchant.getWindow().setSoftInputMode(
					WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

			dialogConfirmAMerchant.show();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void validatePromoCode(final String promoCode, final View parentView) {
		RequestParams params = new RequestParams();
		params.add("service_proposal_serial", arrProposal.get(selectedProposal).serial);
		params.add("session_username", pref.getPref(Config.PREF_USERNAME));
		params.add("active_session_token",
				pref.getPref(Config.PREF_ACTIVE_SESSION_TOKEN));
		params.add("promocode", promoCode);
		params.add("app", "PA");

		Log.d("PromoCode", "validatePromoCode");

		PARestClient.post(pref.getPref(Config.SERVER),
				Config.API_CHECK_PROMO_CODE,
				params,
				new AsyncHttpResponseHandler() {
					@Override
					public void onStart() {
						super.onStart();
						loadingInternetDialog.show();
					}

					@Override
					public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
						super.onSuccess(arg0, arg1, arg2);
						Log.d("PromoCode", new String(arg2));
						PromoCodeResult pcr = new ParserValidatePromoCode(new String(arg2))
								.getData();
						if ("success".equals(pcr.status)) {
							Log.d("PromoCode", pcr.result.new_amt);

							discountedAmount = Float.parseFloat(pcr.result.new_amt);
							UserORM user = new ProfilUtils(getActivity()).getUser();
							String currency = "";
							if (user != null) {
								currency = user.result.currency;
							}

							if (TextUtils.isEmpty(currency)) {
								currency = item.currency;
							}

							TextView txtPrice = (TextView) parentView.findViewById(R.id.txtPrice);
							txtPrice.setText(currency + " " + pcr.result.new_amt);
							txtPrice.setTextColor(getResources().getColor(R.color.red));

							((View)parentView.findViewById(R.id.inputPromoCode).getParent()).setVisibility(View.INVISIBLE);
							parentView.findViewById(R.id.txtPromoCode).setVisibility(View.VISIBLE);

							validatedPromoCode = promoCode;

							accordionGroup.disable();
						} else if ("failed".equals(pcr.status)) {
							showMessageDialog(pcr.message);
						}
						loadingInternetDialog.hide();
					}

					@Override
					public void onFailure(int arg0, Header[] arg1, byte[] arg2,
										  Throwable arg3) {
						super.onFailure(arg0, arg1, arg2, arg3);
						loadingInternetDialog.dismiss();
						AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
								getActivity());

						// set title
						alertDialogBuilder.setTitle("Network Error");

						// set dialog message
						alertDialogBuilder
								.setMessage("Click yes to retry!")
								.setCancelable(true)
								.setPositiveButton("Yes",
										new DialogInterface.OnClickListener() {
											public void onClick(
													DialogInterface dialog,
													int id) {
												// if this button is clicked,
												// close
												// current activity
												validatePromoCode(promoCode, parentView);
											}
										})
								.setNegativeButton("No",
										new DialogInterface.OnClickListener() {
											public void onClick(
													DialogInterface dialog,
													int id) {
												// if this button is clicked,
												// just close
												// the dialog box and do nothing
												dialog.cancel();
											}
										});

						// create alert dialog
						AlertDialog alertDialog = alertDialogBuilder.create();
						alertDialog.show();
					}
				});
	}

	void checkPromoCode() {
		// AsyncHttpClient client = new AsyncHttpClient();
		RequestParams params = new RequestParams();
		params.add("merchant_username",
				arrProposal.get(selectedProposal).merchant_username);
		params.add("voucher_serial", f_promo_code);
		PARestClient.post(pref.getPref(Config.SERVER),
				Config.API_CHECK_PROMO_CODE, params,
				new AsyncHttpResponseHandler() {
					@Override
					public void onStart() {
						// TODO Auto-generated method stub
						super.onStart();
						loadingInternetDialog.show();
					}

					@Override
					public void onFinish() {
						// TODO Auto-generated method stub
						super.onFinish();
						loadingInternetDialog.dismiss();
					}

					@Override
					public void onSuccess(int statusCode, Header[] headers,
										  String content) {
						// TODO Auto-generated method stub
						super.onSuccess(statusCode, headers, content);
						promoCode = new ParserPromoCode(new String(content))
								.getData();
						// if("success".equals(promoCode.status)){
						showPromoCodeStatusDialog();
						// }else{
						// simpleToast("Failed:"+promoCode.reason.toString().replace("[",
						// "").replace("]", ""));
						// showPromoCodeStatusDialog(false,promoCode.reason.toString().replace("[",
						// "").replace("]", ""));

						// }
					}

					@Override
					public void onFailure(int arg0, Header[] arg1, byte[] arg2,
										  Throwable arg3) {
						// TODO Auto-generated method stub
						super.onFailure(arg0, arg1, arg2, arg3);

					}
				});

	}

	PromoCode promoCode;
	Dialog dialogPromoCode;
	EditText txtPromoCode;
	String f_promo_code;

	protected void showPromoCodeDialog() {
		// TODO Auto-generated method stub
		View v = inflater.inflate(R.layout.fragment_input_promo_code, null);
		txtPromoCode = (EditText) v.findViewById(R.id.txtCode);
		v.findViewById(R.id.btnMenu).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialogPromoCode.dismiss();
			}
		});

		v.findViewById(R.id.btn_ok).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (isValidPromoCode()) {
					// showPromoCodeStatusDialog();
					checkPromoCode();
				}
			}

			private boolean isValidPromoCode() {
				// TODO Auto-generated method stub
				boolean flag = false;

				f_promo_code = txtPromoCode.getText().toString();
				if (!formCheckString(f_promo_code, "Promo Code")) {

				} else {
					flag = true;
				}

				return flag;
			}
		});

		v.findViewById(R.id.btn_skip).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				promoCode = null;
				showPromoCodeStatusDialog();
			}
		});

		dialogPromoCode = new Dialog(getActivity(), R.style.PauseDialog);

		dialogPromoCode.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		dialogPromoCode.getWindow().setLayout(
				WindowManager.LayoutParams.MATCH_PARENT,
				WindowManager.LayoutParams.MATCH_PARENT);
		dialogPromoCode.setContentView(v);
		dialogPromoCode.getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

		dialogPromoCode.show();
	}

	Dialog dialogPromoCodeStatus;

	protected void showPromoCodeStatusDialog() {
		showPromoCodeStatusDialog(true, "");
	}

	protected void showPromoCodeStatusDialog(boolean isValid, String reason) {
		// TODO Auto-generated method stub
		View v = inflater.inflate(R.layout.fragment_promo_code_status, null);

		TextView txtPromoCode = (TextView) v.findViewById(R.id.txt_promo_code);
		TextView txtVoucherStatus = (TextView) v
				.findViewById(R.id.txt_status_code);
		TextView txtTotalPayment = (TextView) v
				.findViewById(R.id.total_payment);
		TextView txtVoucherValue = (TextView) v
				.findViewById(R.id.voucher_value);
		TextView txtFinalCost = (TextView) v.findViewById(R.id.final_cost);
		String status = "";
		double voucherValue = 0;

		if (promoCode != null) {
			txtPromoCode.setText(f_promo_code);

			if ("success".equals(promoCode.status)) {
				status = "Successfully applied";
				voucherValue = Double.parseDouble(promoCode.result.value);

			} else {
				status = "Code not valid";
				voucherValue = 0;

			}
		} else {
			txtPromoCode.setText("-");
			txtVoucherStatus.setText("NA");
		}

		DecimalFormat df = new DecimalFormat("#.00");
		txtVoucherStatus.setText(status);
		double totalPayment = 10000f;

		double finalCost = totalPayment - voucherValue;

		txtTotalPayment.setText(df.format(totalPayment));
		txtVoucherValue.setText(df.format(voucherValue));
		txtFinalCost.setText(df.format(finalCost));

		v.findViewById(R.id.menu).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialogPromoCodeStatus.dismiss();
			}
		});

		v.findViewById(R.id.btn_ok).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				doOrder();
			}
		});

		v.findViewById(R.id.btn_skip).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialogPromoCodeStatus.dismiss();
			}
		});

		dialogPromoCodeStatus = new Dialog(getActivity(), R.style.PauseDialog);

		dialogPromoCodeStatus.getWindow().requestFeature(
				Window.FEATURE_NO_TITLE);
		dialogPromoCodeStatus.getWindow().setLayout(
				WindowManager.LayoutParams.MATCH_PARENT,
				WindowManager.LayoutParams.MATCH_PARENT);
		dialogPromoCodeStatus.setContentView(v);
		dialogPromoCodeStatus.getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

		dialogPromoCodeStatus.show();
	}

	boolean isSend = false;

	void doOrder() {
		loadingInternetDialog.show();
		RequestParams params = new RequestParams();
		params.add("session_username", pref.getPref(Config.PREF_USERNAME));
		params.add("active_session_token",
				pref.getPref(Config.PREF_ACTIVE_SESSION_TOKEN));
		params.add("merchant_username",
				arrProposal.get(selectedProposal).merchant_username);
		params.add("customer_username",
				arrProposal.get(selectedProposal).customer_username);
		params.add("service_proposal_serial",
				arrProposal.get(selectedProposal).serial);
		params.add("service_order_type",
				arrProposal.get(selectedProposal).service_proposal_type);
		params.add("service_description",
				arrProposal.get(selectedProposal).service_description);

		// AsyncHttpClient client = new AsyncHttpClient();

		if (!isSend)
			PARestClient.post(pref.getPref(Config.SERVER),
					Config.API_SEND_ORDER, params,
					new AsyncHttpResponseHandler() {
						@Override
						public void onStart() {
							// TODO Auto-generated method stub
							super.onStart();
							loadingInternetDialog.show();

							if (!isSend) {
								isSend = true;
							}
						}

						@Override
						public void onFinish() {
							// TODO Auto-generated method stub
							super.onFinish();
							isSend = false;
							loadingInternetDialog.dismiss();
						}

						@Override
						public void onFailure(Throwable error, String content) {
							// TODO Auto-generated method stub
							super.onFailure(error, content);
							Handler delay = new Handler();
							dialogMerchantOffer.dismiss();
							loadingInternetDialog.dismiss();
							simpleToast("Failed to process your request. Please try again");
						}

						@Override
						public void onSuccess(int arg0, Header[] arg1,
											  byte[] arg2) {
							// TODO Auto-generated method stub
							super.onSuccess(arg0, arg1, arg2);
							// Tracer.d(content);
							// 08-26 14:10:52.505: D/System.out.println(28864):
							// {"status":"success",
							// "reason":{"merchant_verification_code":"MC-NFQPMS4M8I","customer_approval_code":"CC-GEKHYWHST5"}}
							try {
								// dialogMerchantOffer.dismiss();
								// dialogPromoCode.dismiss();
								// dialogPromoCodeStatus.dismiss();

								ActivationStatus status = new ParserActivationStatus(
										new String(arg2)).getData();
								// JSONObject obj = new JSONObject(content);
								if ("success".equals(status.status)) {
									item.service_request_status = "taken";
									HashMap<String, String> hash = (HashMap<String, String>) status.reason;
									service_order_serial = hash
											.get("service_order_serial");
									// doPayViaMolpay();
									showDialogOrderSuccess();

								} else {
									simpleToast("Error:"
											+ status.reason.toString());
								}

								// loadingInternetDialog.dismiss();
							} catch (Exception e) {
								e.printStackTrace();
							}

						}
					});
	}

	//	molpay not using
//	protected void doPayViaMolpay() {
//		// TODO Auto-generated method stub
//
//		UserORM user = new ProfilUtils(getActivity()).getUser();
//		String country = item.currency;// user.result.cs_country;
//
//		MolData molData = PayHelper.getMolData(country);
//		if (Config.DOMAIN.equals(DOMAIN_STAGING)) {
//			molData = PayHelper.getMolData(item.currency);
//		} else {
//			molData = PayHelper.getMolData("sandbox");
//		}
//
//		Intent intent = new Intent(getActivity(), MOLPayActivity.class);
//		Bundle b = new Bundle();
//
//		b.putString("MerchantId", molData.merchantId);
//		b.putString("AppName", molData.appName);
//		b.putString("VerifyKey", molData.verifyKey);
//		b.putString("Username", molData.apiKey);
//		b.putString("Password", molData.apiPass);
//
//		Random r = new Random();
//		int i1 = r.nextInt(500000 - 1) + 1;
//		b.putString("OrderId", arrProposal.get(selectedProposal).serial);
//
//		b.putString("BillName", user.result.cs_name);
//		b.putString("BillDesc", item.title);
//		b.putString("BillMobile", user.result.cs_mobile_number);
//		b.putString("BillEmail", user.result.cs_email);
//		b.putString("Channel", "multi");
//		b.putString("Currency", "MYR");
//		b.putString("Country", "MY");
//		b.putFloat("Amount",
//				Float.parseFloat(arrProposal.get(selectedProposal).total));
//		b.putBoolean("debug", true);
//		b.putBoolean("editable", true);
//
//		intent.putExtras(b);
//		startActivityForResult(intent, MOLPAY_REQUEST_CODE);
//	}

	void postNonceToServer(String nonce) {
		String country = "sg";

		if ("sgd".equals(item.currency.toLowerCase())) {
			country = "sg";
		} else if ("myr".equals(item.currency.toLowerCase())) {
			country = "my";
		}

		String amount, serial;

		if (item.serial.contains("VP-")) {
			amount = item.price;
			serial = item.serial;
		} else {
			amount = getAmount();
			serial = (arrProposal.get(selectedProposal).serial);
		}

		RequestParams params = new RequestParams();
		params.add("amount", amount);
		params.put("nonce", nonce);
		params.add("service_proposal_serial", serial);
		params.add("customerId", braintree.customerId);
		params.add("country", country);
//        if (!item.serial.contains("VP-"))
		params.add("promocode", validatedPromoCode);
		params.add("credit_serial", creditSerial);

		PARestClient.get(pref.getPref(Config.SERVER), "braintree/create-sales",
				params, new AsyncHttpResponseHandler() {

					@Override
					public void onStart() {
						super.onStart();
						forceLoadingInternetDialog.show();
					}

					public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
						ParserBasicResult parser = new ParserBasicResult(
								new String(arg2));
						Log.d("Payment", "Result " + new String(arg2));
						if ("success".equals(parser.getStatus())) {

							showDialogOrderSuccess();

						} else {
							Log.d("Payment", "Cancel because it is not success");

							showDialogOrderCancel();

						}
						forceLoadingInternetDialog.dismiss();
					}

					;

					@Override
					public void onFailure(int arg0, Header[] arg1, byte[] arg2,
										  Throwable arg3) {
						// TODO Auto-generated method stub
						super.onFailure(arg0, arg1, arg2, arg3);
						showDialogOrderCancel();
						forceLoadingInternetDialog.setCancelable(true);

					}

				});
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {

		System.out.println(requestCode + " " + resultCode);
		if (requestCode == BRAINTREE) {
			if (resultCode == BraintreePaymentActivity.RESULT_OK) {
				String paymentMethodNonce = data
						.getStringExtra(BraintreePaymentActivity.EXTRA_PAYMENT_METHOD_NONCE);
				postNonceToServer(paymentMethodNonce);
			}
		} else if (requestCode == PAYPALWEB) {

		}
		//	molpay removed
//		else if (resultCode == Activity.RESULT_OK
//				&& requestCode == MOLPAY_REQUEST_CODE) {
//
//			Bundle bundle = data.getExtras().getBundle("bundle");
//
//			if (bundle != null) {
//				String amount = bundle.getString(MerchantInfo.PAY_AMOUNT);
//				String transaction_id = bundle.getString(MerchantInfo.TXN_ID);
//				String transaction_status = bundle
//						.getString(MerchantInfo.STATUS_CODE);
//				String error_desc = bundle.getString(MerchantInfo.ERR_DESC);
//				String bill_name = bundle.getString(MerchantInfo.BILL_NAME);
//				String bill_email = bundle.getString(MerchantInfo.BILL_EMAIL);
//				String bill_mobile = bundle.getString(MerchantInfo.BILL_MOBILE);
//				String bill_desc = bundle.getString(MerchantInfo.BILL_DESC);
//
//				// name.setText(bill_name);
//				// email.setText(bill_email);
//				// mobile.setText(bill_mobile);
//				// desc.setText(bill_desc);
//				// trans.setText(transaction_id);
//				// amt.setText("MYR "+amount);
//				//
//
//				// String Result = "the name is "+bill_name+"\nthe mobile is " +
//				// bill_mobile +"\nthe email is " + bill_email
//				// +"\nthe description is " + bill_desc +"\nthe amount is " +
//				// amount + "\nthe transaction id is " + transaction_id ;
//
//				if (transaction_status.equals("00")) {
//					// stat.setText("Success");
//					showDialogOrderSuccess();
//				} else if (transaction_status.equals("11")) {
//					// stat.setText("Failed");
//                    Log.d("Payment", "Cancel because it is 11");
//					showDialogOrderCancel();
//				} else if (transaction_status.equals("22")) {
//					// stat.setText("Pending");
//					showDialogOrderSuccess();
//				}
//
//				// result.setText(""+Result);
//			}
//		}
		else if (resultCode == Activity.RESULT_CANCELED
				&& requestCode == MOLPAY_REQUEST_CODE) {
			Log.d("Payment", "Cancel because result is canceled");
			showDialogOrderCancel();

		} else if (requestCode == REQUEST_CODE_PAYMENT) {
			if (resultCode == Activity.RESULT_OK) {
				PaymentConfirmation confirm = data
						.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
				if (confirm != null) {
					try {
						Log.i(TAG, confirm.toJSONObject().toString(4));
						Log.i(TAG, confirm.getPayment().toJSONObject()
								.toString(4));
						/**
						 * TODO: send 'confirm' (and possibly
						 * confirm.getPayment() to your server for verification
						 * or consent completion. See
						 * https://developer.paypal.com
						 * /webapps/developer/docs/integration
						 * /mobile/verify-mobile-payment/ for more details.
						 *
						 * For sample mobile backend interactions, see
						 * https://github
						 * .com/paypal/rest-api-sdk-python/tree/master
						 * /samples/mobile_backend
						 */
						Toast.makeText(
								getActivity(),
								"PaymentConfirmation info received from PayPal",
								Toast.LENGTH_LONG).show();
						// showDialogOrderSuccess();
						sendPaypalVerification(
								arrProposal.get(selectedProposal).serial,
								confirm.toJSONObject().toString());
					} catch (JSONException e) {
						Log.e(TAG, "an extremely unlikely failure occurred: ",
								e);
						showDialogOrderCancel();
					}
				}
			} else if (resultCode == Activity.RESULT_CANCELED) {
				Log.i(TAG, "The user canceled.");
				showDialogOrderCancel();
			} else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID) {
				Log.i(TAG,
						"An invalid Payment or PayPalConfiguration was submitted. Please see the docs.");
				showDialogOrderCancel();
			}
		} else if (requestCode == REQUEST_CODE_FUTURE_PAYMENT) {
			if (resultCode == Activity.RESULT_OK) {
				PayPalAuthorization auth = data
						.getParcelableExtra(PayPalFuturePaymentActivity.EXTRA_RESULT_AUTHORIZATION);
				if (auth != null) {
					try {
						Log.i("FuturePaymentExample", auth.toJSONObject()
								.toString(4));

						String authorization_code = auth.getAuthorizationCode();
						Log.i("FuturePaymentExample", authorization_code);

						sendAuthorizationToServer(auth);
						Toast.makeText(getActivity(),
								"Future Payment code received from PayPal",
								Toast.LENGTH_LONG).show();

					} catch (JSONException e) {
						Log.e("FuturePaymentExample",
								"an extremely unlikely failure occurred: ", e);
					}
				}
			} else if (resultCode == Activity.RESULT_CANCELED) {
				Log.i("FuturePaymentExample", "The user canceled.");
				showDialogOrderCancel();
			} else if (resultCode == PayPalFuturePaymentActivity.RESULT_EXTRAS_INVALID) {
				Log.i("FuturePaymentExample",
						"Probably the attempt to previously start the PayPalService had an invalid PayPalConfiguration. Please see the docs.");
			}
		} else if (requestCode == REQUEST_CODE_PROFILE_SHARING) {
			if (resultCode == Activity.RESULT_OK) {
				PayPalAuthorization auth = data
						.getParcelableExtra(PayPalProfileSharingActivity.EXTRA_RESULT_AUTHORIZATION);
				if (auth != null) {
					try {
						Log.i("ProfileSharingExample", auth.toJSONObject()
								.toString(4));

						String authorization_code = auth.getAuthorizationCode();
						Log.i("ProfileSharingExample", authorization_code);

						sendAuthorizationToServer(auth);
						Toast.makeText(getActivity(),
								"Profile Sharing code received from PayPal",
								Toast.LENGTH_LONG).show();

					} catch (JSONException e) {
						Log.e("ProfileSharingExample",
								"an extremely unlikely failure occurred: ", e);
					}
				}
			} else if (resultCode == Activity.RESULT_CANCELED) {
				Log.i("ProfileSharingExample", "The user canceled.");
			} else if (resultCode == PayPalFuturePaymentActivity.RESULT_EXTRAS_INVALID) {
				Log.i("ProfileSharingExample",
						"Probably the attempt to previously start the PayPalService had an invalid PayPalConfiguration. Please see the docs.");
			}
		}
	}

	private void getOrderData() {

		loadingInternetDialog.show();
		// AsyncHttpClient client = new AsyncHttpClient();
		RequestParams params = new RequestParams();
		params.add("customer_username", pref.getPref(Config.PREF_USERNAME));
		params.add("session_username", pref.getPref(Config.PREF_USERNAME));
		params.add("active_session_token",
				pref.getPref(Config.PREF_ACTIVE_SESSION_TOKEN));
		params.add("service_order_status_string", "ongoing");
		params.add("page", "1");
		params.add("limit", "10");

		PARestClient.get(pref.getPref(Config.SERVER),
				Config.API_GET_SERVICE_ORDER

				, params, new AsyncHttpResponseHandler() {
					@Override
					public void onSuccess(String content) {
						// TODO Auto-generated method stub
						super.onSuccess(content);
						Tracer.d(content);
						loadingInternetDialog.dismiss();
						if ("authentication fail".equals(content)) {
							listener.doLogout();
						} else {
							ParserJob parser = new ParserJob(content);
							if ("success".equals(parser.status)) {
								ArrayList<JobItem> arrJob = parser.getData();
								// Tracer.d("Size:" + arr.size());
								String serial="";
								if(item!=null && item.serial.contains("VP-")){
									serial=item.serial;
								}else{
									serial=arrProposal
											.get(selectedProposal).serial;

								}

								for (JobItem tmpJob : arrJob) {
									if (tmpJob.service_proposal_serial.equals(serial)) {
										dialogOrderSuccess.dismiss();		Log.i("!!!!!!: ", "ORDER DETAILS CALLED HERE!!!");
										listener.doFragmentChange(
												new FragmentOrderDetail(tmpJob),
												false, "");
										break;
									}
								}
							}

						}
					}

					@Override
					public void onFailure(int statusCode, Throwable error,
										  String content) {
						// TODO Auto-generated method stub
						super.onFailure(statusCode, error, content);
						loadingInternetDialog.dismiss();
						simpleToast("Failed to get your order due to network error. Please try again");
					}
				});

	}

	private void showDialogOrderCancel() {
		View v = inflater.inflate(R.layout.dialog_order_cancel, null);
		TextView txtServiceRequestID = (TextView) v
				.findViewById(R.id.txtServiceRequestID);

		txtServiceRequestID.setText(item.serial);

		v.findViewById(R.id.btnReturnToPayment).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialogOrderSuccess.hide();
				doPayment();
			}
		});

		v.findViewById(R.id.btnBack).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialogOrderSuccess.hide();
			}
		});

		v.findViewById(R.id.btnNext2).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialogOrderSuccess.hide();
				// listener.doFragmentChange(new FragmentOrder(), false, "");
			}
		});

		dialogOrderSuccess = new Dialog(getActivity(), R.style.PauseDialog);
		dialogOrderSuccess.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss(DialogInterface dialog) {
				// TODO Auto-generated method stub
				// dialogOrderSuccess.hide();
				// listener.doFragmentChange(new FragmentOrder(), false, "");

			}
		});
		dialogOrderSuccess.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		dialogOrderSuccess.getWindow().setLayout(
				WindowManager.LayoutParams.MATCH_PARENT,
				WindowManager.LayoutParams.MATCH_PARENT);
		dialogOrderSuccess.setContentView(v);
		dialogOrderSuccess.getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

		dialogOrderSuccess.show();

	}

	Dialog dialogOrderSuccess;

	void showDialogOrderSuccess() {
		analytic.trackScreen("Job Offer - Thank You");
		try {
			dialogMerchantOffer.dismiss();
		} catch (Exception e) {

		}
		Map eventData = new HashMap<>();
		eventData.put("job_request_id", item.serial);
		Intercom.client().logEvent("job-request-payment", eventData);

		String subServices = "";
		ArrayList<String> skuArrayList = new ArrayList<>();
		if (arrDetail != null) {
			for (int i  = 0; i < arrDetail.size(); i++) {
				if (i > 0)
					subServices += ",";
				subServices += arrDetail.get(i).service_detail_name;
				skuArrayList.add(arrDetail.get(i).service_detail_name);
			}
		} else {
			subServices = item.service_category_name;
		}

		Log.d(TAG, "Sub Services: " + subServices);
		Log.d(TAG, "Price: " + getAmount());
		Log.d(TAG, "Currency: " + item.currency);

		AppsFlyerLib.setCurrencyCode(item.currency);
		Map<String,Object> event = new HashMap<>();
		event.put("Price",getAmount());
		event.put("Subservices", subServices);
		AppsFlyerLib.trackEvent(getActivity().getApplicationContext(), "Appointment", event);

		DecimalFormat df = new DecimalFormat("0.00");
		Log.d(TAG, "Formatted Price: " + df.format(Double.parseDouble(getAmount())).replace(".", ""));

		String[] skuArray = skuArrayList.toArray(new String[skuArrayList.size()]);
		String[] valueArray = new String[skuArrayList.size()];
		Log.d(TAG, "valueArray length: " + valueArray.length);
		for (int k=0; k<valueArray.length; k++) {
			valueArray[k] = (k == 0 ? df.format(Double.parseDouble(getAmount())).replace(".", "") : "000");
		}

		NanigansEventManager.getInstance().trackNanigansEvent(NanigansEventManager.TYPE.PURCHASE, "appointment",
				new NanigansEventParameter("sku", skuArray),
				new NanigansEventParameter("value", valueArray),
				new NanigansEventParameter("unique", item.serial),
				new NanigansEventParameter("currency", item.currency)
		);

		View v;
		if(!(item!=null && item.serial.contains("VP-"))){

			v = inflater.inflate(R.layout.dialog_order_success, null);
			TextView txtServiceRequestID = (TextView) v
					.findViewById(R.id.txtServiceRequestID);

			if (item.serial.contains("VP-")) {
				txtServiceRequestID.setText(item.serial);

			} else {
				txtServiceRequestID
						.setText(arrProposal.get(selectedProposal).serial);
			}
			v.findViewById(R.id.btnBack).setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
//                    listener.doFragmentChange(new FragmentBid(), true, "");
					getActivity().getSupportFragmentManager().popBackStackImmediate();
					dialogOrderSuccess.hide();
				}
			});

			v.findViewById(R.id.btnNext2).setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					getOrderData();
					// dialogOrderSuccess.hide();
					// JobItem job=new JobItem();
					// job.serial=service_order_serial;
					// listener.doFragmentChange(new FragmentOrderDetail(job),
					// false, "");
				}
			});

			Map eventData2 = new HashMap<>();
			eventData2.put("job_request_id", item.serial);
			eventData2.put("job_proposal_id", arrProposal.get(selectedProposal).serial);
			Intercom.client().logEvent("job-appointment", eventData2);

			analytic.trackECommerce(arrProposal.get(selectedProposal).serial,
					arrProposal.get(selectedProposal).merchant_company_name,
					getAmount(),
					item.currency,
					getJobTitle(item.title),
					item.service_category_name
			);
		}else{
			v=inflater.inflate(R.layout.fragment_service_request_detail_vo_success, null);

			TextView localtxtSerial = (TextView) v.findViewById(R.id.txtSerial);
			TextView localtxtBidStatus = (TextView) v.findViewById(R.id.txtBidStatus);
			TextView localtxtDescription = (TextView) v.findViewById(R.id.txtDescription);
			TextView localtxtJobDesc = (TextView) v.findViewById(R.id.txtJobDesc);
			TextView localtxtPrice = (TextView) v.findViewById(R.id.txtPrice);
			TextView localtxtRelatedSerial = (TextView) v
					.findViewById(R.id.txt_related_serial);
			TextView localtxtCompanyName = (TextView) v.findViewById(R.id.txtCompanyName);
			TextView localtxtCompanyEmail = (TextView) v.findViewById(R.id.txtMerchantEmail);
			TextView localtxtCompanyContact = (TextView) v
					.findViewById(R.id.txtMerchantContact);


			localtxtSerial.setText(item.serial);
			localtxtJobDesc.setText(item.description);
			localtxtPrice.setText(item.currency + " " + item.price);
			localtxtRelatedSerial.setText(item.service_order_serial);
			localtxtCompanyName.setText(item.merchant_company_name);
			localtxtCompanyEmail.setText(item.merchant_email);
			localtxtCompanyContact.setText(item.merchant_contact);


			v.findViewById(R.id.btnBack).setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
//                    listener.doFragmentChange(new FragmentBid(), true, "");
					getActivity().getSupportFragmentManager().popBackStackImmediate();
					dialogOrderSuccess.hide();
				}
			});

			v.findViewById(R.id.btnOfferVO).setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					getOrderData();
					// dialogOrderSuccess.hide();
					// JobItem job=new JobItem();
					// job.serial=service_order_serial;
					// listener.doFragmentChange(new FragmentOrderDetail(job),
					// false, "");
				}
			});

			analytic.trackECommerce(item.serial,
					item.merchant_company_name,
					getAmount(),
					item.currency,
					getJobTitle(item.title),
					item.service_category_name
			);
		}

		dialogOrderSuccess = new Dialog(getActivity(), R.style.PauseDialog);
		dialogOrderSuccess.setOnCancelListener(new OnCancelListener() {

			@Override
			public void onCancel(DialogInterface dialog) {
				// TODO Auto-generated method stub
				listener.doFragmentChange(new FragmentOrder(), false, "");

			}
		});
		dialogOrderSuccess.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss(DialogInterface dialog) {
				// TODO Auto-generated method stub
				// dialogOrderSuccess.hide();
				// listener.doFragmentChange(new FragmentOrder(), false, "");

			}
		});
		dialogOrderSuccess.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		dialogOrderSuccess.getWindow().setLayout(
				WindowManager.LayoutParams.MATCH_PARENT,
				WindowManager.LayoutParams.MATCH_PARENT);
		dialogOrderSuccess.setContentView(v);
		dialogOrderSuccess.getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

		dialogOrderSuccess.show();

	}

	Dialog dialogCloseBid;
	CountDownTimer timerBid;
	TextView txtBidMsg;

	void showCloseDialog() {
		try {
			timerBid = new CountDownTimer(11000, 1000) {

				@Override
				public void onTick(long millisUntilFinished) {
					// TODO Auto-generated method stub
					try {
						String str = Config.BID_CLOSE_MSG;

						int second = (int) millisUntilFinished / 1000;
						str = str.replace("@", "" + second);
						txtBidMsg.setText(str);
					} catch (Exception e) {

					}
				}

				@Override
				public void onFinish() {
					// TODO Auto-generated method stub
					dialogCloseBid.dismiss();
					doCloseBid();
					;
				}
			};

			View v = inflater.inflate(R.layout.dialog_close_bid, null);
			txtBidMsg = (TextView) v.findViewById(R.id.txtMsg);
			v.findViewById(R.id.btnCancel2).setOnClickListener(
					new OnClickListener() {

						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							dialogCloseBid.dismiss();
						}
					});
			v.findViewById(R.id.btnNext2).setOnClickListener(
					new OnClickListener() {

						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub

							dialogCloseBid.dismiss();
							doCloseBid();
						}
					});

			dialogCloseBid = new Dialog(getActivity(), R.style.PauseDialog);
			dialogCloseBid.setOnDismissListener(new OnDismissListener() {

				@Override
				public void onDismiss(DialogInterface dialog) {
					// TODO Auto-generated method stub
					timerBid.cancel();
				}
			});
			dialogCloseBid.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
			dialogCloseBid.getWindow().setLayout(
					WindowManager.LayoutParams.MATCH_PARENT,
					WindowManager.LayoutParams.MATCH_PARENT);
			dialogCloseBid.setContentView(v);
			dialogCloseBid.getWindow().setSoftInputMode(
					WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

			dialogCloseBid.show();
			timerBid.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	void doCloseBid() {
		RequestParams params = new RequestParams();
		params.add("session_username", pref.getPref(Config.PREF_USERNAME));
		params.add("active_session_token",
				pref.getPref(Config.PREF_ACTIVE_SESSION_TOKEN));
		params.add("service_request_serial", item.serial);
		// AsyncHttpClient client = new AsyncHttpClient();
		PARestClient.post(pref.getPref(Config.SERVER), Config.API_CLOSE_BID,
				params, new AsyncHttpResponseHandler() {
					@Override
					public void onStart() {
						// TODO Auto-generated method stub
						super.onStart();
						loadingInternetDialog.show();
					}

					@Override
					public void onFinish() {
						// TODO Auto-generated method stub
						super.onFinish();
						loadingInternetDialog.dismiss();
					}

					@Override
					public void onSuccess(int statusCode, String content) {
						// TODO Auto-generated method stub
						super.onSuccess(statusCode, content);
						// Tracer.d(content);
						JSONObject obj;
						try {
							obj = new JSONObject(content);
							if ("success".equals(obj.getString("status"))) {
								simpleToast("Success. Your bid has been closed");
								item.service_request_status = "closed";
								init();
								loadingInternetDialog.dismiss();

								Map eventData = new HashMap<>();
								eventData.put("job_request_id", item.serial);
								Intercom.client().logEvent("job-request-close", eventData);

							}
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

					}

					@Override
					public void onFailure(Throwable error, String content) {
						// TODO Auto-generated method stub
						super.onFailure(error, content);
						loadingInternetDialog.dismiss();
						simpleToast("Failed,please try again");
					}

				});

		// item.service_request_status = "closed";
		// init();

	}

	void doCancelBid() {
		// loadingInternetDialog.show();
		RequestParams params = new RequestParams();
		params.add("session_username", pref.getPref(Config.PREF_USERNAME));
		params.add("active_session_token",
				pref.getPref(Config.PREF_ACTIVE_SESSION_TOKEN));
		params.add("serial", item.serial);

		// AsyncHttpClient client = new AsyncHttpClient();
		PARestClient.post(pref.getPref(Config.SERVER), Config.API_CANCEL_BID,
				params, new AsyncHttpResponseHandler() {
					@Override
					public void onStart() {
						// TODO Auto-generated method stub
						super.onStart();
						loadingInternetDialog.show();
					}

					@Override
					public void onFinish() {
						// TODO Auto-generated method stub
						super.onFinish();
						loadingInternetDialog.dismiss();
					}

					@Override
					public void onSuccess(int statusCode, String content) {
						// TODO Auto-generated method stub
						super.onSuccess(statusCode, content);
						Tracer.d(content);
						// fm.popBackStackImmediate();
						listener.doFragmentChange(new FragmentBid(), false, "");
						loadingInternetDialog.dismiss();
						simpleToast("Success. Your bid has been cancelled");

					}

					@Override
					public void onFailure(Throwable error, String content) {
						// TODO Auto-generated method stub
						super.onFailure(error, content);
						loadingInternetDialog.dismiss();
						simpleToast("Failed");
						// showNe
					}

				});

	}

	void showCancelBid() {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle("Warning");
		builder.setMessage("Are you sure that you want to cancel this bid?");
		builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				dialog.dismiss();
				doCancelBid();
			}
		});
		builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				dialog.dismiss();
			}
		});
		builder.show();
	}

	/* FOR PAYPAL */
	public void doPayWithPaypal() {

		// PAYMENT_INTENT_SALE will cause the payment to complete immediately.
		// Change PAYMENT_INTENT_SALE to
		// - PAYMENT_INTENT_AUTHORIZE to only authorize payment and capture
		// funds later.
		// - PAYMENT_INTENT_ORDER to create a payment for authorization and
		// capture
		// later via calls from your server.

		PayPalPayment payment = new PayPalPayment(new BigDecimal(
				arrProposal.get(selectedProposal).total), "USD",
				arrProposal.get(selectedProposal).serial,
				PayPalPayment.PAYMENT_INTENT_SALE);

		Intent intent = new Intent(getActivity(), PaymentActivity.class);

		// send the same configuration for restart resiliency
		intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);

		intent.putExtra(PaymentActivity.EXTRA_PAYMENT, payment);

		startActivityForResult(intent, REQUEST_CODE_PAYMENT);
	}

	public void doPayWithPaypalMYR() {

		// PAYMENT_INTENT_SALE will cause the payment to complete immediately.
		// Change PAYMENT_INTENT_SALE to
		// - PAYMENT_INTENT_AUTHORIZE to only authorize payment and capture
		// funds later.
		// - PAYMENT_INTENT_ORDER to create a payment for authorization and
		// capture
		// later via calls from your server.

		PayPalPayment payment = new PayPalPayment(new BigDecimal(
				arrProposal.get(selectedProposal).total), "USD",
				arrProposal.get(selectedProposal).serial,
				PayPalPayment.PAYMENT_INTENT_SALE);

		Intent intent = new Intent(getActivity(), PaymentActivity.class);

		// send the same configuration for restart resiliency
		intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);

		intent.putExtra(PaymentActivity.EXTRA_PAYMENT, payment);

		startActivityForResult(intent, REQUEST_CODE_PAYMENT);
	}

	private static final String TAG = "paymentExample";

	public static final int REQUEST_CODE_PAYMENT = 661;
	public static final int REQUEST_CODE_FUTURE_PAYMENT = 662;
	public static final int REQUEST_CODE_PROFILE_SHARING = 663;

	private static PayPalConfiguration config = new PayPalConfiguration()
			.environment(CONFIG_ENVIRONMENT)
			.clientId(CONFIG_CLIENT_ID)
			// The following are only used in PayPalFuturePaymentActivity.
			.merchantName("PageAdvisor")
			.merchantPrivacyPolicyUri(
					Uri.parse("https://www.example.com/privacy"))
			.merchantUserAgreementUri(
					Uri.parse("https://www.example.com/legal"))
			// .acceptCreditCards(false)
			;

	private void sendAuthorizationToServer(PayPalAuthorization authorization) {

		/**
		 * TODO: Send the authorization response to your server, where it can
		 * exchange the authorization code for OAuth access and refresh tokens.
		 *
		 * Your server must then store these tokens, so that your server code
		 * can execute payments for this user in the future.
		 *
		 * A more complete example that includes the required app-server to
		 * PayPal-server integration is available from
		 * https://github.com/paypal/
		 * rest-api-sdk-python/tree/master/samples/mobile_backend
		 */

	}

	@Override
	public void onDestroy() {
		// Stop service when done
		getActivity().stopService(
				new Intent(getActivity(), PayPalService.class));
		super.onDestroy();
	}

	void sendPaypalVerification(String proposal, String json) {
		RequestParams params = new RequestParams();
		params.add("service_proposal_serial", proposal);
		params.add("json_data", json);
		PARestClient.post(pref.getPref(Config.SERVER),
				"transaction/paypal-payment-processing", params,
				new AsyncHttpResponseHandler() {
					@Override
					public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
						// TODO Auto-generated method stub
						super.onSuccess(arg0, arg1, arg2);
						showDialogOrderSuccess();
					}

					@Override
					public void onFailure(int arg0, Header[] arg1, byte[] arg2,
										  Throwable arg3) {
						// TODO Auto-generated method stub
						super.onFailure(arg0, arg1, arg2, arg3);
					}

				});
	}

	Dialog dialogMerchantProfil;
	ViewFlipper flipper;

	void showMerchantProfilDialog() {
		analytic.trackScreen("Merchant Profile");

		View v = inflater.inflate(R.layout.dialog_merchant_profil, null);
		v.findViewById(R.id.btnBack).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialogMerchantProfil.dismiss();
			}
		});
		flipper = (ViewFlipper) v.findViewById(R.id.flipper);

		ListView list = (ListView) v.findViewById(R.id.list);
		GridView grid = (GridView) v.findViewById(R.id.grid);

		list.setAdapter(new ReviewAdapter());
		grid.setAdapter(new GridAdapter());

		View menu1, menu2, menu3, menu1b, menu2b, menu3b;

		menu1 = v.findViewById(R.id.menu1);
		menu2 = v.findViewById(R.id.menu2);
		menu3 = v.findViewById(R.id.menu3);

		menu1b = v.findViewById(R.id.menu1b);
		menu2b = v.findViewById(R.id.menu2b);
		menu3b = v.findViewById(R.id.menu3b);

		flipper.setTag(R.id.menu1b, menu1b);
		flipper.setTag(R.id.menu2b, menu2b);
		flipper.setTag(R.id.menu3b, menu3b);

		setActive(flipper, 0);

		OnClickListener menuClick = new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				switch (v.getId()) {
					case R.id.menu1:
						setActive(flipper, 0);
						analytic.trackScreen("Merchant Profile - About");
						break;
					case R.id.menu2:
						setActive(flipper, 1);
						analytic.trackScreen("Merchant Profile - Reviews");

						break;
					case R.id.menu3:
						setActive(flipper, 2);
						analytic.trackScreen("Merchant Profile - Gallery");

						break;
				}
			}
		};

		menu1.setOnClickListener(menuClick);
		menu2.setOnClickListener(menuClick);
		menu3.setOnClickListener(menuClick);

		ImageView cover_timeline = (ImageView) v
				.findViewById(R.id.img_cover_timeline);
		TextView txtName = (TextView) v.findViewById(R.id.txtName);
		TextView ratingCount = (TextView) v.findViewById(R.id.ratingCount);
		TextView ratingCount1 = (TextView) v.findViewById(R.id.ratingCount1);

		RatingBar ratingBar = (RatingBar) v.findViewById(R.id.ratingBar1);

		TextView about = (TextView) v.findViewById(R.id.about);
		TextView service = (TextView) v.findViewById(R.id.service);

//		cover_timeline.setTag(MY_URI + "user/merchant-image?image_name="
//				+ mp.cover_photo);
//		imageLoader.DisplayImage(MY_URI + "user/merchant-image?image_name="
//				+ mp.cover_photo, getActivity(), cover_timeline);
		//	change to Glide for image loading
		glideImageLoader.displayImageGlide(getActivity(), MY_URI + "user/merchant-image?image_name=" + mp.cover_photo, R.drawable.promo_placeholder, cover_timeline);
//		if(TextUtils.isEmpty(mp.cover_photo))
//			Glide.with(getActivity()).load(R.drawable.default_cover_timeline).into(cover_timeline);
//		else
//			Glide
//					.with(getActivity())
//					.load(MY_URI + "user/merchant-image?image_name=" + mp.cover_photo)
//					.into(cover_timeline);

		txtName.setText(mp.merchant_name);
		ratingCount.setText(" " + mp.overall_rating + " out of 5");
		ratingCount1.setText(mp.rating_count + " review(s)");

		ratingBar.setRating(Float.parseFloat(mp.overall_rating));
		about.setText(mp.about);
		service.setText(TextUtils.join("\n", mp.service_list));

		dialogMerchantProfil = new Dialog(getActivity(), R.style.PauseDialog);

		dialogMerchantProfil.getWindow()
				.requestFeature(Window.FEATURE_NO_TITLE);
		dialogMerchantProfil.getWindow().setLayout(
				WindowManager.LayoutParams.MATCH_PARENT,
				WindowManager.LayoutParams.MATCH_PARENT);
		dialogMerchantProfil.setContentView(v);
		dialogMerchantProfil.getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

		dialogMerchantProfil.show();
	}

	private void setActive(ViewFlipper flipper, int i) {
		// TODO Auto-generated method stub
		View menu1b = (View) flipper.getTag(R.id.menu1b);
		View menu2b = (View) flipper.getTag(R.id.menu2b);
		View menu3b = (View) flipper.getTag(R.id.menu3b);

		menu1b.setVisibility(View.INVISIBLE);
		menu2b.setVisibility(View.INVISIBLE);
		menu3b.setVisibility(View.INVISIBLE);

		switch (i) {
			case 0:
				menu1b.setVisibility(View.VISIBLE);
				break;
			case 1:
				menu2b.setVisibility(View.VISIBLE);
				break;
			case 2:
				menu3b.setVisibility(View.VISIBLE);
				break;

		}

		flipper.setDisplayedChild(i);
	}

	ArrayList<MerchantReview> arr_review = new ArrayList<MerchantReview>();

	class ReviewAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return arr_review.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		MyHolder holder;

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			if (convertView == null) {
				convertView = inflater.inflate(R.layout.item_merchant_review,
						null);

				holder = new MyHolder();
				holder.ratingBar = (RatingBar) convertView
						.findViewById(R.id.ratingBar1);
				holder.name = (TextView) convertView.findViewById(R.id.txtName);
				holder.comment = (TextView) convertView
						.findViewById(R.id.services);

				convertView.setTag(holder);

			} else {
				holder = (MyHolder) convertView.getTag();
			}

			MerchantReview tmp = arr_review.get(position);
			try {
				holder.ratingBar.setRating((int) Float
						.parseFloat(tmp.review_score));
				holder.name.setText("Reviewed by "
						+ tmp.customer_username
						+ " on"
						+ TimeUtils.timeStampToDDMMYY("" + tmp.createdtime
						* 1000));
				holder.comment.setText(tmp.service_description);

			} catch (Exception e) {
				e.printStackTrace();
			}

			return convertView;
		}

		class MyHolder {
			RatingBar ratingBar;
			TextView name, comment;
		}
	}

	ArrayList<String> arr_image = new ArrayList<String>();

	class GridAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return arr_image.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		MyHolder holder;

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub

			if (convertView == null) {
				convertView = inflater.inflate(R.layout.item_merchant_gallery,
						null);

				holder = new MyHolder();

				holder.img = (ImageView) convertView.findViewById(R.id.img);
				convertView.setTag(holder);
			} else {
				holder = (MyHolder) convertView.getTag();
			}
			String uri = arr_image.get(position);
//			holder.img.setTag(MY_URI + "user/merchant-image?image_name=" + uri);
//			imageLoader.DisplayImage(MY_URI + "user/merchant-image?image_name="
//					+ uri, getActivity(), holder.img);
			//	change to Glide for image loading
			glideImageLoader.displayImageGlide(getActivity(), MY_URI + "user/merchant-image?image_name=" + uri, R.drawable.default_img, holder.img);
//			if(TextUtils.isEmpty(uri))
//				Glide.with(getActivity()).load(R.drawable.default_img).into(holder.img);
//			else
//				Glide.with(getActivity())
//						.load(MY_URI + "user/merchant-image?image_name=" + uri)
//						.into(holder.img);

			holder.img.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View paramView) {
					// TODO Auto-generated method stub
					showDialogPhotoPreview(paramView);
				}
			});
			return convertView;
		}

		class MyHolder {
			public ImageView img;
		}

	}

	MerchantProfile mp;

	void getMerchantProfil(String merchant_username) {
		AsyncHttpResponseHandler responseHandler = new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
				// TODO Auto-generated method stub
				super.onSuccess(arg0, arg1, arg2);
				ParserMerchantProfile parser = new ParserMerchantProfile(
						new String(arg2));
				mp = parser.getData();
				arr_image = mp.gallery_image;
				arr_review = mp.reviews;
				showMerchantProfilDialog();
			}

			@Override
			public void onFailure(int arg0, Header[] arg1, byte[] arg2,
								  Throwable arg3) {
				// TODO Auto-generated method stub
				super.onFailure(arg0, arg1, arg2, arg3);
			}

			@Override
			public void onStart() {
				// TODO Auto-generated method stub
				super.onStart();
				loadingInternetDialog.show();
			}

			@Override
			public void onFinish() {
				// TODO Auto-generated method stub
				super.onFinish();
				loadingInternetDialog.dismiss();
			}
		};

		RequestParams params = new RequestParams();
		params.add("merchant_username", merchant_username);
		params.add("session_username", pref.getPref(Config.PREF_USERNAME));
		params.add("active_session_token",
				pref.getPref(Config.PREF_ACTIVE_SESSION_TOKEN));

		PARestClient.get(pref.getPref(Config.SERVER), "user/merchant-profile",
				params, responseHandler);

	}

}
