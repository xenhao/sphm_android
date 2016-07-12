package com.pa.order;

import java.util.ArrayList;

import jim.h.common.android.lib.zxing.integrator.IntentIntegrator;
import jim.h.common.android.lib.zxing.integrator.IntentResult;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.text.Spannable;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.pa.common.Config;
import com.pa.common.FormUtils;
import com.pa.common.ImageLoader;
import com.pa.common.ImageLoaderSecure;
import com.pa.common.MyFragment;
import com.pa.common.OnFragmentChangeListener;
import com.pa.common.PARestClient;
import com.pa.common.Tracer;
import com.pa.parser.ParserJobDetail;
import com.pa.parser.ParserMerchant;
import com.pa.pojo.JobDetail;
import com.pa.pojo.JobItem;
import com.pa.pojo.Merchant;
import com.coolfindservices.androidconsumer.R;

@SuppressLint("ValidFragment")
public class FragmentOrderDetail extends MyFragment implements OnClickListener {

	JobItem job, jobAtHeader;
	ArrayList<JobDetail> arrDetail;
	TextView wrapperService;
	OnFragmentChangeListener listener;

	TextView txtDescription, txtServiceTime, txtStatus, txtServiceProvider,
			txtAddress, txtServiceDescription;
	TextView txtSerial,txtSerialVO,txtRelatedSerial;
	TextView changeAppointment;
	String f_merchant_verification_code;

	TextView btnDoJob, btnCancelJob;

	LinearLayout wrapperJobCompleted;
	TextView txtCompleteServiceProvider;
	RatingBar completeServiceRating;

	TextView txtServiceAddress;

	TextView wrapperCancellationPolicy;
	TextView txtCancellation;

	TextView wrapperAppoinment;
	TextView txtMerchantContact, txtMerchantPrice;
	LinearLayout wrapperGallery, contentGallery;
	TextView galleryParent;
	ImageLoaderSecure imageLoader;
	TextView txtOtherComment;

	ImageView img;
	ImageLoader imageLoaderNormal;

	TextView tnc;
	String MY_URI;
	View layoutVO;
	TextView txtVO;
	
	LinearLayout layout_vo_id,layout_normal_id;
	
	public FragmentOrderDetail(){
		
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		imageLoader = new ImageLoaderSecure(getActivity());
		imageLoaderNormal = new ImageLoader(getActivity());
//		if ("0".equals(pref.getPref(Config.SERVER))) {
//			MY_URI = "http://" + Config.DOMAIN_DEV + "/";
//
//		} else if ("1".equals(pref.getPref(Config.SERVER))) {
//
//			MY_URI = "http://" + Config.DOMAIN_STAGING + "/";
//
//		}
//		else if ("2".equals(pref.getPref(Config.SERVER))) {
//
//			MY_URI = "http://" + Config.DOMAIN_LIVE + "/";
//
//		}
		
		MY_URI=PARestClient.getAbsoluteUrl(pref.getPref(Config.SERVER), "");
		
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

	public FragmentOrderDetail(JobItem job) {
		this.job = job;
	}

	boolean isCompleted = false;

	public FragmentOrderDetail(JobItem job, boolean isCompleted) {
		this.job = job;// new JobItem();
		// this.job.serial=job.serial;
		this.isCompleted = isCompleted;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View v = inflater.inflate(R.layout.fragment_job_detail, null);
		
		layout_vo_id=(LinearLayout) v.findViewById(R.id.vo_id);
		layout_normal_id=(LinearLayout) v.findViewById(R.id.normal_id);
		
		txtCall=(TextView) v.findViewById(R.id.txtContactCall);
		txtDescription = (TextView) v.findViewById(R.id.txtDescription);
		txtServiceTime = (TextView) v.findViewById(R.id.txtServiceTime);
		txtStatus = (TextView) v.findViewById(R.id.txtBidStatus);
		txtServiceProvider = (TextView) v.findViewById(R.id.txtServiceProvider);
		txtMerchantContact = (TextView) v.findViewById(R.id.txtMerchantContact);
		txtMerchantPrice = (TextView) v.findViewById(R.id.txtMerchantPrice);

		txtSerial = (TextView) v.findViewById(R.id.txtSerial);
		txtSerialVO=(TextView)v.findViewById(R.id.txtSerialVO);
		txtRelatedSerial = (TextView) v.findViewById(R.id.txtRelatedSerial);

		
		wrapperService = (TextView) v.findViewById(R.id.catParent);
		txtServiceAddress = (TextView) v.findViewById(R.id.txtServiceAddress);
		txtServiceDescription = (TextView) v
				.findViewById(R.id.txtServiceDescription);
		v.findViewById(R.id.btnBack).setOnClickListener(this);
		v.findViewById(R.id.do_job).setOnClickListener(this);
		v.findViewById(R.id.cancel_job).setOnClickListener(this);

		btnDoJob = (TextView) v.findViewById(R.id.do_job);
		btnCancelJob = (TextView) v.findViewById(R.id.cancel_job);
		wrapperJobCompleted = (LinearLayout) v
				.findViewById(R.id.wrapper_job_complete);
		txtCompleteServiceProvider = (TextView) v
				.findViewById(R.id.txtCompleteServiceProvider);
		completeServiceRating = (RatingBar) v
				.findViewById(R.id.txtCompleteRatingBar);

		wrapperCancellationPolicy = (TextView) v
				.findViewById(R.id.catParentCancel);
		txtCancellation = (TextView) v.findViewById(R.id.txt_policy);

		wrapperAppoinment = (TextView) v.findViewById(R.id.appointmentParent);
		wrapperGallery = (LinearLayout) v.findViewById(R.id.wrapper_gallery);
		contentGallery = (LinearLayout) v.findViewById(R.id.gallery_content);
		galleryParent = (TextView) v.findViewById(R.id.galleryParent);

		txtOtherComment = (TextView) v.findViewById(R.id.txtOtherComments);
		img = (ImageView) v.findViewById(R.id.img);
		tnc=(TextView) v.findViewById(R.id.tnc);
		layoutVO=v.findViewById(R.id.layout_vo);
		txtVO=(TextView) layoutVO.findViewById(R.id.txtVO);
		
		if(job!=null && job.serial.contains("VO-")){
			layout_vo_id.setVisibility(View.VISIBLE);
			layout_normal_id.setVisibility(View.GONE);
		}else{
			layout_vo_id.setVisibility(View.GONE);
			layout_normal_id.setVisibility(View.VISIBLE);
			
		}
		
		return v;

	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);

		wrapperService.findViewById(R.id.catParent).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						View parent = (View) v.getParent();
						View content = parent
								.findViewById(R.id.categoryWrapper);
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
				});

		wrapperCancellationPolicy.findViewById(R.id.catParentCancel)
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						View parent = (View) v.getParent();
						View content = parent
								.findViewById(R.id.categoryWrapperCancel);
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
				});

		wrapperGallery.findViewById(R.id.galleryParent).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						View parent = (View) v.getParent();
						View content = parent.findViewById(R.id.galleryContent);
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
				});

		wrapperAppoinment.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				View parent = (View) v.getParent();
				View content = parent.findViewById(R.id.appointmentWrapper);
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
					t.setCompoundDrawablesWithIntrinsicBounds(null, null,
							getResources()
									.getDrawable(R.drawable.icon_arrow_up),
							null);

				}
			}
		});
		reDraw();
		
		//showRateMerchant();

	}

	void reDraw() {
		try {
			drawDetail();

			checkJobStatus();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	void checkJobStatus() {
		wrapperJobCompleted.setVisibility(View.GONE);

		// job.service_order_status="completed";
		if ( //"Pending Service Delivery"
				!"closed".equals(job.service_order_status)
				&&
				!"completed".equals(job.service_order_status)
				) {
			txtStatus.setBackgroundColor(getResources().getColor(
					R.color.pa_blue));

			if ("false".equals(job.is_verified)) {
				analytic.trackScreen("Job Details - Pending Service Delivery");
				
				//txtStatus.setText("Pending Service Delivery");
				btnDoJob.setText("VERIFY MERCHANT");
				btnDoJob.setBackgroundResource(R.drawable.btn_effect_blue_light);

				btnDoJob.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						showVerifyMerchant();
					}
				});
				btnCancelJob.setVisibility(View.VISIBLE);
				btnDoJob.setVisibility(View.VISIBLE);

			} else {
				analytic.trackScreen("Job Details - Job Started");
				txtStatus.setText("Job In Progress");
				btnDoJob.setText("COMPLETE JOB");

				btnDoJob.setBackgroundResource(R.drawable.btn_effect_blue_light);

				btnDoJob.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						showCloseJob();
					}
				});
				btnCancelJob.setVisibility(View.GONE);
				btnDoJob.setVisibility(View.GONE);
			}

		} else {
			analytic.trackScreen("Job Details - Job Completed");
			txtStatus.setBackgroundColor(getResources().getColor(R.color.red));
			txtStatus.setText("Job Completed");

//			wrapperJobCompleted.setVisibility(View.VISIBLE);
			btnDoJob.setText("BACK TO HOME");
			btnDoJob.setBackgroundResource(R.drawable.btn_effect_gray);
			btnDoJob.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					// getActivity().getSupportFragmentManager()
					// .popBackStackImmediate();
					listener.doFragmentChange(new FragmentOrder(isCompleted),
							false, "");

				}
			});

			btnCancelJob.setVisibility(View.GONE);
			btnDoJob.setVisibility(View.GONE);
			getMerchantData();

		}
	}

	private void drawDetail() {
		// TODO Auto-generated method stub
		getData();
		
//		if(!job.serial.contains("VO-")){
//		getData();
//		}else{
//			jobAtHeader=job;
//			drawJobItem();
//			((ViewGroup)wrapperService.getParent()).setVisibility(View.GONE);
//		}
	}

	TextView txtCall;

	void drawJobItem() {
		String url = MY_URI + job.service_icon + "?session_username="
				+ pref.getPref(Config.PREF_USERNAME) + "&active_session_token="
				+ pref.getPref(Config.PREF_ACTIVE_SESSION_TOKEN);

		img.setTag(url);
		imageLoaderNormal.DisplayImage(url, getActivity(), img);

		txtServiceAddress.setText(job.service_order_address
				+ " , "
				+ getCityAddress(job.service_order_city,
						job.service_order_state, job.service_order_country,
						job.service_order_postal_code));
		txtServiceDescription.setText(job.service_description);

		txtSerial.setText(job.serial);

		if(job!=null && job.serial.contains("VO-")){
		txtSerialVO.setText(job.serial);
		txtRelatedSerial.setText(job.related_job_id);
		}
		
		txtDescription.setText(getJobTitle(job.request_title));
		txtStatus.setText(job.service_order_status);
		
		if("completed".equals(job.service_order_status)){
			txtStatus.setText("Job Completed");
		}
		
		txtServiceProvider.setText(job.company_name);

		Spannable span = Spannable.Factory
				.getInstance()
				.newSpannable(
						FormUtils
								.printEmptyFormValue(jobAtHeader.co_main_contact_number));

		if (!TextUtils.isEmpty(jobAtHeader.co_main_contact_number)) {
			String s = FormUtils
					.printEmptyFormValue(jobAtHeader.co_main_contact_number)
					+ "";
			span = Spannable.Factory.getInstance().newSpannable(s);

			// span.setSpan(new ClickableSpan() {
			// @Override
			// public void onClick(View v) {
			// FormUtils.dialNumber(getActivity(),
			// jobAtHeader.co_main_contact_number);
			// }
			// }, s.indexOf("Call"), s.length(),
			// Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

			txtCall.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					FormUtils.dialNumber(getActivity(),
							jobAtHeader.co_main_contact_number);
				}
			});
		}

		txtMerchantContact.setText(span);
		txtMerchantContact.setMovementMethod(LinkMovementMethod.getInstance());

		String currency = jobAtHeader.currency;
		String total = jobAtHeader.total;
		txtMerchantPrice.setText(FormUtils.printPrice(currency, total));
		// Tracer.d(job.preferred_time1_start + " " +
		// job.preferred_time2_start);
		txtServiceTime.setText(getPreferredTime(job.preferred_time1_start,
				job.preferred_time2_start));
		// if (!"false".equals(job.preferred_time1_start)) {
		// txtServiceTime.setText(getPreferredTime(job.preferred_time1_start));
		// } else if (!"false".equals(job.preferred_time2_start)) {
		// txtServiceTime.setText(getPreferredTime(job.preferred_time2_start));
		// }

		for (String uri : job.arrAttachment) {
			View v = inflater.inflate(R.layout.item_upload_photo, null);
			ImageView img = (ImageView) v.findViewById(R.id.img);

			url = PARestClient.getAbsoluteUrl(pref.getPref(Config.SERVER),Config.API_SERVICE_IMAGE) + "/" + uri
					+ "?active_session_token="
					+ pref.getPref(Config.PREF_ACTIVE_SESSION_TOKEN)
					+ "&session_username=" + pref.getPref(Config.PREF_USERNAME);
			img.setTag(url);
			imageLoader.DisplayImage(url, getActivity(), img);
			img.setTag(url);

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

		Tracer.d("debug", "attachment size:" + job.arrAttachment.size());
		if (job.arrAttachment.size() == 0) {
			wrapperGallery.setVisibility(View.GONE);
		}

	}

	void getData() {
		// AsyncHttpClient client = new AsyncHttpClient();
		RequestParams params = new RequestParams();
		params.add("session_username", pref.getPref(Config.PREF_USERNAME));
		params.add("active_session_token",
				pref.getPref(Config.PREF_ACTIVE_SESSION_TOKEN));
		params.add("serial", job.serial);
		loadingInternetDialog.show();
		PARestClient.get(pref.getPref(Config.SERVER),Config.API_GET_SERVICE_ORDER_DETAIL, params,
				new AsyncHttpResponseHandler() {

					@Override
					public void onSuccess(int statusCode, String content) {
						// TODO Auto-generated method stub
						super.onSuccess(statusCode, content);
						loadingInternetDialog.dismiss();
						Tracer.d(content);
						ParserJobDetail parser = new ParserJobDetail(content);
						jobAtHeader = parser.item;
						if ("authentication fail".equals(content)) {
							listener.doLogout();

						} else if ("success".equals(parser.getStatus())) {
							txtCancellation.setText(FormUtils
									.printEmptyFormValue(parser.cancellation_policy));
							txtOtherComment.setText(jobAtHeader.other_remarks);
							arrDetail = parser.getArr();
							
							drawService();
							drawJobItem();
						}

					}

					@Override
					public void onFailure(Throwable error, String content) {
						// TODO Auto-generated method stub
						super.onFailure(error, content);
						loadingInternetDialog.dismiss();
					}
				});
	}

	void drawService() {
		try {
			Tracer.d("Array detail:" + arrDetail.size());
			String strVO="";
			for (int i = 0; i < arrDetail.size(); i++) {
				View v = inflater.inflate(R.layout.item_service_list_v3, null);
				strVO+=arrDetail.get(i).service_detail_description;
				if(i<arrDetail.size()-1){
					strVO+="\n\n";
				}
				
				TextView serviceDetail = (TextView) v
						.findViewById(R.id.service_detail);
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

				((LinearLayout) ((View) wrapperService.getParent())
						.findViewById(R.id.service_detail_content)).removeAllViews();
                ((LinearLayout) ((View) wrapperService.getParent())
                        .findViewById(R.id.service_detail_content)).addView(v);
			}
			
			
			//for VO
			if(job.serial.contains("VO-")){
				layoutVO.setVisibility(View.VISIBLE);
				txtVO.setText(strVO);
			}
			
			final String strTnc="Cancellation policy";
			Spannable span = Spannable.Factory.getInstance().newSpannable(strTnc);   
			span.setSpan(new ClickableSpan() {  
			    @Override
			    public void onClick(View v) {  
			        //Log.d("main", "link clicked");
			        //simpleToast("Tnc");
			        //showTNC(link1,getResources().getString(R.string.link1));
			    	String cancel_id="";
			    	for(JobDetail bd:arrDetail){
			    		cancel_id+=bd.service_id+",";
			    		}
			    	
			    			        showWebDialog("http://pageadvisor.bounche.com/transaction/cancellation-policy?service_id="+cancel_id, strTnc);

			        
			    } }, 0, strTnc.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
			
			tnc.setText(span);
			tnc.setMovementMethod(LinkMovementMethod.getInstance());

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	Dialog dialogChangeAppointment;
	EditText txtCode;

	private void showDialogChangeAppointment() {
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

		TextView txtStatus;
		View v = inflater.inflate(
				R.layout.fragment_job_change_appointment_status, null);
		v.findViewById(R.id.btnBack).setOnClickListener(clickListener);
		txtStatus = (TextView) v.findViewById(R.id.txtStatus);
		txtCode = (EditText) v.findViewById(R.id.txtCode);

		v.findViewById(R.id.btnSubmit).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						if (isValidCode()) {
							doSubmitCode();
						}
					}
				});

		// TODO Auto-generated method stub
		dialogChangeAppointment = new Dialog(getActivity(), R.style.PauseDialog);

		dialogChangeAppointment.getWindow().requestFeature(
				Window.FEATURE_NO_TITLE);
		dialogChangeAppointment.getWindow().setLayout(
				WindowManager.LayoutParams.MATCH_PARENT,
				WindowManager.LayoutParams.MATCH_PARENT);
		dialogChangeAppointment.setContentView(v);
		dialogChangeAppointment.getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

		dialogChangeAppointment.show();

	}

	protected void doSubmitCode() {
		// TODO Auto-generated method stub
		loadingInternetDialog.show();
		AsyncHttpClient client = new AsyncHttpClient();
		RequestParams params = new RequestParams();
		params.add("service_order_serial", job.serial);
		params.add("customer_approval_code", f_code);
		// client.post(Config.API_INPUT_CUSTOMER_APPROVAL_CODE, params, new
		// AsyncHttpResponseHandler(){
		// @Override
		// public void onSuccess(int statusCode, String content) {
		// // TODO Auto-generated method stub
		// super.onSuccess(statusCode, content);
		// loadingInternetDialog.dismiss();
		//
		// }
		//
		// @Override
		// public void onFailure(Throwable error, String content) {
		// // TODO Auto-generated method stub
		// super.onFailure(error, content);
		// loadingInternetDialog.dismiss();
		//
		// }
		// });
	}

	String f_code;

	protected boolean isValidCode() {
		// TODO Auto-generated method stub
		boolean flag = false;
		f_code = txtCode.getText().toString();
		if (!formCheckString(f_code, "code")) {

		} else {
			flag = true;
		}

		return flag;
	}

	Dialog dialogServiceDetailDescription;

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

	Dialog dialogVerifyMerchant;
	EditText txtPasscode;
	TextView msgVerifyMerchant;

	private void showVerifyMerchant() {
		analytic.trackScreen("Verify Merchant");
		
		View v = inflater.inflate(R.layout.dialog_verify_merchant, null);
		txtPasscode = (EditText) v.findViewById(R.id.txtPasscode);
		msgVerifyMerchant = (TextView) v.findViewById(R.id.msg);

		msgVerifyMerchant.setVisibility(View.GONE);

		v.findViewById(R.id.btnCancel2).setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						dialogVerifyMerchant.dismiss();
					}
				});

		v.findViewById(R.id.btnNext2).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (isValidVerifyMerchant()) {
					doVerifyMerchant();
				}
			}
		});

		v.findViewById(R.id.btnScanQR).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						// Intent intent = new
						// Intent("com.google.zxing.client.android.SCAN");
						// intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
						// startActivityForResult(intent, SCANNER_REQUEST_CODE);

						// Intent intent = new
						// Intent(getActivity().getApplicationContext(),CaptureActivity.class);
						// intent.setAction("com.google.zxing.client.android.SCAN");
						// // this stops saving ur barcode in barcode scanner
						// app's history
						// intent.putExtra("SAVE_HISTORY", false);
						// startActivityForResult(intent, 0);
						try {
							IntentIntegrator.initiateScan(getActivity(),
									R.layout.capture, R.id.viewfinder_view,
									R.id.preview_view, true);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});

		dialogVerifyMerchant = new Dialog(getActivity(), R.style.PauseDialog);

		dialogVerifyMerchant.getWindow()
				.requestFeature(Window.FEATURE_NO_TITLE);
		dialogVerifyMerchant.getWindow().setLayout(
				WindowManager.LayoutParams.MATCH_PARENT,
				WindowManager.LayoutParams.MATCH_PARENT);
		dialogVerifyMerchant.setContentView(v);
		dialogVerifyMerchant.getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

		dialogVerifyMerchant.show();
	}

	public int SCANNER_REQUEST_CODE = 123;

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		Tracer.d("FragmentOrderDetail onactivity result");
		// if (requestCode == SCANNER_REQUEST_CODE) {
		// // Handle scan intent
		// if (resultCode == Activity.RESULT_OK) {
		// // Handle successful scan
		// String contents = intent.getStringExtra("SCAN_RESULT");
		// String formatName = intent.getStringExtra("SCAN_RESULT_FORMAT");
		// byte[] rawBytes = intent.getByteArrayExtra("SCAN_RESULT_BYTES");
		// int intentOrientation = intent.getIntExtra(
		// "SCAN_RESULT_ORIENTATION", Integer.MIN_VALUE);
		// Integer orientation = (intentOrientation == Integer.MIN_VALUE) ? null
		// : intentOrientation;
		// String errorCorrectionLevel = intent
		// .getStringExtra("SCAN_RESULT_ERROR_CORRECTION_LEVEL");
		//
		// simpleToast("Verification code: " + contents /*
		// * + "\n\n" +
		// * formatName
		// */);
		// txtPasscode.setText(contents);
		// } else if (resultCode == Activity.RESULT_CANCELED) {
		// // Handle cancel
		// }
		// } else

		if (requestCode == IntentIntegrator.REQUEST_CODE) {

			IntentResult scanResult = IntentIntegrator.parseActivityResult(
					requestCode, resultCode, intent);
			if (scanResult == null) {
				return;
			}
			final String result = scanResult.getContents();
			if (result != null) {
				simpleToast("Verification code: " + result /*
															 * + "\n\n" +
															 * formatName
															 */);
				txtPasscode.setText(result);
			}

			// Handle other intents
		}

	}

	Dialog dialogRateMerchant;
	EditText txtRatingMsg;
	TextView txtRatingServiceName;
	RatingBar ratingPoint;
	String f_rating, f_rating_message;

	void showRateMerchant() {
		
		analytic.trackScreen("Reviews & Ratings");
		View v = inflater.inflate(R.layout.dialog_rate, null);

		TextView title=(TextView)v.findViewById(R.id.title);
		TextView order_id=(TextView)v.findViewById(R.id.txtOrderId);
		TextView date=(TextView)v.findViewById(R.id.txtDate);
		
		title.setText(job.request_title);
		order_id.setText(job.serial);
		date.setText(getPreferredTime(job.preferred_time1_start,
				job.preferred_time2_start));
		
		
		ratingPoint = (RatingBar) v.findViewById(R.id.ratingBar1);
		txtRatingMsg = (EditText) v.findViewById(R.id.txtRatingMessage);
		txtRatingServiceName = (TextView) v.findViewById(R.id.txtServiceName);
		txtRatingServiceName.setText(job.company_name);

		
		LayerDrawable stars = (LayerDrawable) ratingPoint.getProgressDrawable();
		stars.getDrawable(2).setColorFilter(getResources().getColor(R.color.pa_orange), PorterDuff.Mode.SRC_ATOP);
		
		ratingPoint.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_UP) {
					float touchPositionX = event.getX();
					float width = ratingPoint.getWidth();
					float starsf = (touchPositionX / width) * 5.0f;
					int stars = (int) starsf + 1;
					ratingPoint.setRating(stars);

					// Toast.makeText(MainActivity.this, String.valueOf("test"),
					// Toast.LENGTH_SHORT).show();
					v.setPressed(false);
				}
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					v.setPressed(true);
				}

				if (event.getAction() == MotionEvent.ACTION_CANCEL) {
					v.setPressed(false);
				}

				return true;
			}
		});

		v.findViewById(R.id.btnCancel2).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						dialogRateMerchant.dismiss();
					}
				});

		v.findViewById(R.id.btnCancel2).setVisibility(View.GONE);

		v.findViewById(R.id.btnNext2).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (isValidRate()) {
					doRate();
					dialogRateMerchant.dismiss();
				}
			}
		});

		
		dialogRateMerchant = new Dialog(getActivity(), R.style.PauseDialog);
		//dialogRateMerchant.setCancelable(false);
		dialogRateMerchant.setOnCancelListener(new DialogInterface.OnCancelListener() {         
		    @Override
		    public void onCancel(DialogInterface dialog) {
		        //do whatever you want the back key to do
		    	simpleToast("You must give the rating to continue using app");
		    }
		});
		
		dialogRateMerchant.setOnKeyListener(new OnKeyListener() {
			
			@Override
			public boolean onKey(DialogInterface paramDialogInterface, int paramInt,
					KeyEvent paramKeyEvent) {
				// TODO Auto-generated method stub
				if (paramKeyEvent.getKeyCode()== KeyEvent.KEYCODE_BACK){
			    	simpleToast("You must give the rating to continue using app");
			    	return true;
				}
				
				return false;
			}
		});
		
		dialogRateMerchant.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		dialogRateMerchant.getWindow().setLayout(
				WindowManager.LayoutParams.MATCH_PARENT,
				WindowManager.LayoutParams.MATCH_PARENT);
		dialogRateMerchant.setContentView(v);
		dialogRateMerchant.getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

		dialogRateMerchant.show();

	}

	protected void doRate() {
		// TODO Auto-generated method stub
		RequestParams params = new RequestParams();

		params.add("session_username", pref.getPref(Config.PREF_USERNAME));
		params.add("active_session_token",
				pref.getPref(Config.PREF_ACTIVE_SESSION_TOKEN));
		params.add("merchant_username", job.merchant_username);
		params.add("review_score", f_rating);
		params.add("review_description", f_rating_message);
		params.add("service_order_serial", job.serial);

		AsyncHttpClient client = new AsyncHttpClient();
		PARestClient.post(pref.getPref(Config.SERVER),Config.API_RATE_MERCHANT, params,
				new AsyncHttpResponseHandler() {

					@Override
					public void onStart() {
						// TODO Auto-generated method stub
						super.onStart();
						loadingInternetDialog.show();
					}

					@Override
					public void onSuccess(int statusCode, String content) {
						// TODO Auto-generated method stub
						super.onSuccess(statusCode, content);

						try {
							JSONObject obj = new JSONObject(content);
							if ("success".equals(obj.getString("status"))) {
								dialogRateMerchant.hide();
								reDraw();
							} else {
								simpleToast("Error" + obj.getString("reason"));
							}
						} catch (Exception e) {

						}
						loadingInternetDialog.dismiss();

					}

					@Override
					public void onFailure(int statusCode, Throwable error,
							String content) {
						// TODO Auto-generated method stub
						super.onFailure(statusCode, error, content);
						simpleToast("Error");

						loadingInternetDialog.dismiss();

					}
				});
	}

	protected boolean isValidRate() {
		boolean flag = true;
		// TODO Auto-generated method stub
		f_rating = ratingPoint.getRating() + "";
		f_rating_message = txtRatingMsg.getText().toString();

		if (ratingPoint.getRating() <= 3) {
			if (!formCheckString(f_rating_message, "Rating message")) {
				flag = false;
			}
		}

		return flag;

	}

	protected boolean isValidVerifyMerchant() {
		// TODO Auto-generated method stub
		boolean flag = false;
		f_merchant_verification_code = txtPasscode.getText().toString();

		if (!formCheckString(f_merchant_verification_code, "passcode")) {

		} else {
			flag = true;
		}

		return flag;
	}

	void getMerchantData() {
		loadingInternetDialog.show();

		AsyncHttpClient client = new AsyncHttpClient();
		String uri = Config.API_SEARCH + "?merchant_username="
				+ job.merchant_username + "&page=" + "1" + "&limit=" + "1";
		Tracer.d(uri);
		PARestClient.get(pref.getPref(Config.SERVER),uri, null, new AsyncHttpResponseHandler() {

			@Override
			public void onSuccess(int statusCode, String content) {
				// TODO Auto-generated method stub
				super.onSuccess(statusCode, content);
				Tracer.d(content);
				loadingInternetDialog.dismiss();
				try {
					ParserMerchant parser = new ParserMerchant(content);
					if ("success".equals(parser.status)) {
						Merchant merchant = parser.getData().get(0);
						txtCompleteServiceProvider.setText(merchant.co_name);
						completeServiceRating.setRating(Float
								.parseFloat(merchant.co_overall_rating));

					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	protected void doVerifyMerchant() {
		// TODO Auto-generated method stub

		// TODO Auto-generated method stub
		loadingInternetDialog.show();
		RequestParams params = new RequestParams();
		params.add("session_username", pref.getPref(Config.PREF_USERNAME));
		params.add("active_session_token",
				pref.getPref(Config.PREF_ACTIVE_SESSION_TOKEN));
		params.add("serial", job.serial);
		params.add("merchant_username", job.merchant_username);
		params.add("merchant_verification_code", f_merchant_verification_code);

		Tracer.d(params.toString());
		// AsyncHttpClient client = new AsyncHttpClient();
		PARestClient.post(pref.getPref(Config.SERVER),Config.API_VERIFY_MERCHANT, params,
				new AsyncHttpResponseHandler() {

					@Override
					public void onSuccess(int statusCode, String content) {
						// TODO Auto-generated method stub
						super.onSuccess(statusCode, content);
						Tracer.d(content);

						try {
							JSONObject obj = new JSONObject(content);

							if ("success".equals(obj.getString("status"))) {
								dialogVerifyMerchant.dismiss();
								showVerifiedMerchant();
								job.is_verified = "Y";
								reDraw();
							} else {
								// simpleToast("Error: " +
								// obj.getString("result"));
								// showVerifiedMerchant();
								// job.is_verified="Y";
								// reDraw();
								msgVerifyMerchant.setVisibility(View.VISIBLE);
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
						loadingInternetDialog.dismiss();

					}

					@Override
					public void onFailure(int statusCode, Throwable error,
							String content) {
						// TODO Auto-generated method stub
						super.onFailure(statusCode, error, content);
						loadingInternetDialog.dismiss();
						showLocalWebDialog(content);

					}

				});

	}

	Dialog dialogVerifiedMerchant;

	private void showVerifiedMerchant() {
		View v = inflater.inflate(R.layout.dialog_verified_merchant, null);

		v.findViewById(R.id.btnNext2).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialogVerifiedMerchant.dismiss();
				// refresh screen

			}
		});

		dialogVerifiedMerchant = new Dialog(getActivity(), R.style.PauseDialog);

		dialogVerifiedMerchant.getWindow().requestFeature(
				Window.FEATURE_NO_TITLE);
		dialogVerifiedMerchant.getWindow().setLayout(
				WindowManager.LayoutParams.MATCH_PARENT,
				WindowManager.LayoutParams.MATCH_PARENT);
		dialogVerifiedMerchant.setContentView(v);
		dialogVerifiedMerchant.getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

		dialogVerifiedMerchant.show();
	}

	Dialog dialogCloseJob;

	private void showCloseJob() {
		View v = inflater.inflate(R.layout.dialog_close_job, null);

		v.findViewById(R.id.btnCancel2).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						dialogCloseJob.hide();
					}
				});

		v.findViewById(R.id.btnNext2).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				doCloseJob();
				// simpleToast("Close Job");
			}
		});

		dialogCloseJob = new Dialog(getActivity(), R.style.PauseDialog);

		dialogCloseJob.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		dialogCloseJob.getWindow().setLayout(
				WindowManager.LayoutParams.MATCH_PARENT,
				WindowManager.LayoutParams.MATCH_PARENT);
		dialogCloseJob.setContentView(v);
		dialogCloseJob.getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

		dialogCloseJob.show();
	}

	void doCloseJob() {
		loadingInternetDialog.show();
		RequestParams params = new RequestParams();
		params.add("status", "completed");
		params.add("service_order_serial", job.serial);
		params.add("session_username", pref.getPref(Config.PREF_USERNAME));
		params.add("active_session_token",
				pref.getPref(Config.PREF_ACTIVE_SESSION_TOKEN));

		// AsyncHttpClient client = new AsyncHttpClient();
		Tracer.d(params.toString());
		PARestClient.post(pref.getPref(Config.SERVER),Config.API_CLOSE_JOB, params,
				new AsyncHttpResponseHandler() {
					@Override
					public void onSuccess(int statusCode, String content) {
						// TODO Auto-generated method stub
						super.onSuccess(statusCode, content);
						try {
							JSONObject obj = new JSONObject(content);
							if ("success".equals(obj.getString("status"))) {
								simpleToast("Job successfully completed");
								job.service_order_status = "completed";
								reDraw();
								showRateMerchant();
								dialogCloseJob.hide();
							} else {
								simpleToast("Fail to complete job"
										+ obj.getString("reason"));

							}

						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();

						}
						loadingInternetDialog.dismiss();

					}

					@Override
					public void onFailure(int statusCode, Throwable error,
							String content) {
						// TODO Auto-generated method stub
						super.onFailure(statusCode, error, content);
						loadingInternetDialog.dismiss();
						showLocalWebDialog(content);
						dialogCloseJob.hide();

					}
				});
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btnBack:
			getActivity().getSupportFragmentManager().popBackStackImmediate();
//			listener.doFragmentChange(new FragmentOrder(isCompleted), false, "");
			break;
		case R.id.cancel_job:
			showDialogCancel();
			break;
		case R.id.do_job:
			break;
		}
	}

	Dialog dialogCancelJob;

	private void showDialogCancel() {
		// TODO Auto-generated method stub
		View v = inflater.inflate(R.layout.dialog_cancel_job, null);

		v.findViewById(R.id.btnCancel2).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						dialogCancelJob.dismiss();
					}
				});

		v.findViewById(R.id.btnNext2).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialogCancelJob.dismiss();
				doCancelJob();
			}
		});

		dialogCancelJob = new Dialog(getActivity(), R.style.PauseDialog);

		dialogCancelJob.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		dialogCancelJob.getWindow().setLayout(
				WindowManager.LayoutParams.MATCH_PARENT,
				WindowManager.LayoutParams.MATCH_PARENT);
		dialogCancelJob.setContentView(v);
		dialogCancelJob.getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

		dialogCancelJob.show();
	}

	protected void doCancelJob() {
		// TODO Auto-generated method stub
		loadingInternetDialog.show();
		RequestParams params = new RequestParams();
		params.add("session_username", pref.getPref(Config.PREF_USERNAME));
		params.add("active_session_token",
				pref.getPref(Config.PREF_ACTIVE_SESSION_TOKEN));
		params.add("serial", job.serial);

		// AsyncHttpClient client = new AsyncHttpClient();
		PARestClient.post(pref.getPref(Config.SERVER),Config.API_CANCEL_JOB, params,
				new AsyncHttpResponseHandler() {
					@Override
					public void onStart() {
						// TODO Auto-generated method stub
						super.onStart();
						loadingInternetDialog.show();
					}

					@Override
					public void onSuccess(int statusCode, String content) {
						// TODO Auto-generated method stub
						super.onSuccess(statusCode, content);
						Tracer.d(content);

						try {
							JSONObject obj = new JSONObject(content);
							if ("success".equals(obj.getString("status"))) {
								// getActivity().getSupportFragmentManager().popBackStackImmediate();
								listener.doFragmentChange(new FragmentOrder(),
										false, "");
								simpleToast("Success.Job has been cancelled");
							} else {

							}
						} catch (Exception e) {

						}

						loadingInternetDialog.dismiss();

					}

					@Override
					public void onFailure(int statusCode, Throwable error,
							String content) {
						// TODO Auto-generated method stub
						super.onFailure(statusCode, error, content);
						loadingInternetDialog.dismiss();

					}

				});
	}

}
