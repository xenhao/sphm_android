package com.pa.bid;

import java.util.ArrayList;
import java.util.Date;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.pa.common.Config;
import com.pa.common.ImageLoader;
import com.pa.common.MyFragment;
import com.pa.common.OnFragmentChangeListener;
import com.pa.common.PARestClient;
import com.pa.common.Tracer;
import com.pa.landing.ActivityLanding;
import com.pa.parser.ParserBidList;
import com.pa.pojo.BidItem;
import com.pa.splash.ActivityRegister;
import com.coolfindservices.androidconsumer.R;
import com.readystatesoftware.viewbadger.BadgeView;

import io.intercom.android.sdk.Intercom;

public class FragmentBid extends MyFragment implements OnClickListener, Config {
	DisplayMetrics dm;
	ArrayList<BidItem> bidList = new ArrayList<BidItem>();
	OnFragmentChangeListener listener;
	ListView list;
	MyBidListAdapter adapter;
	int offset, limit;
	private boolean hasNext = true;
	private boolean isLoadingMoreContent;

	LinearLayout job;
	RelativeLayout bid;
	boolean isBid = true;
	View bidWidget;
	int totalData;
	BadgeView badge;
	ImageLoader imageLoader;
	
	public FragmentBid(){
		
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

	String MY_URI = "";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		// for (int i = 0; i < 20; i++) {
		// BidItem item = new BidItem("Car Grooming", "PA_" + i,
		// "Wednesday,30 March 2014", "5 days 22hrs 3 mins");
		// bidList.add(item);
		// }
		bidList.clear();
		offset = 1;
		limit = 10;
		imageLoader = new ImageLoader(getActivity());
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

		//	data caching
		if (hasNext)
			getData();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View v = inflater.inflate(R.layout.fragment_bid_list, null);
		v.findViewById(R.id.btnMenu).setOnClickListener(this);
		v.findViewById(R.id.btnIntercom).setOnClickListener(this);
		list = (ListView) v.findViewById(R.id.list);
		job = (LinearLayout) v.findViewById(R.id.job);
		bid = (RelativeLayout) v.findViewById(R.id.bid);

		job.setOnClickListener(this);
		bid.setOnClickListener(this);

		bidWidget = v.findViewById(R.id.bid_widget);

		v.findViewById(R.id.refresh).setOnClickListener(this);
		v.findViewById(R.id.btnHome).setOnClickListener(this);
		return v;
	}


	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		adapter = new MyBidListAdapter();
		list.setAdapter(adapter);
//		if (hasNext)
//			getData();

		badge = new BadgeView(getActivity(), bidWidget);
		// badge.setText("100");
		badge.setBadgePosition(BadgeView.POSITION_TOP_RIGHT);

		badge.hide();
		
		analytic.trackScreen("Job Request List");

	}

	void getData() {
		isLoadingMoreContent = true;
		loadingInternetDialog.show();

		String username = "";
		String strUri = "";
		// if (isBid)
		// {
		// username = pref.getPref(Config.PREF_USERNAME);
		// strUri = Config.API_GET_CUSTOMER_SERVICE_REQUEST +
		// "?active_session_token="
		// + pref.getPref(Config.PREF_ACTIVE_SESSION_TOKEN)
		// + "&session_username=" + pref.getPref(Config.PREF_USERNAME)
		// + "&merchant_username=" + username + "&offset=" + offset
		// + "&limit=" + limit + " &trans_type=bid";
		//
		// }
		// else {
		// username = "";
		// strUri = Config.API_GET_ALL_SERVICE_REQUEST
		// + "?active_session_token="
		// + pref.getPref(Config.PREF_ACTIVE_SESSION_TOKEN)
		// + "&session_username=" + pref.getPref(Config.PREF_USERNAME)
		// + "&offset=" + offset + "&limit=" + limit
		// + " &trans_type=bid";
		//
		// strUri = Config.API_GET_ALL_SERVICE_REQUEST
		// + "?active_session_token="
		// + pref.getPref(Config.PREF_ACTIVE_SESSION_TOKEN)
		// + "&session_username=" + pref.getPref(Config.PREF_USERNAME)
		// + "&offset=" + offset + "&limit=" + limit;
		//
		// }

		RequestParams params = new RequestParams();
		params.add("active_session_token",
				pref.getPref(Config.PREF_ACTIVE_SESSION_TOKEN));
		params.add("session_username", pref.getPref(Config.PREF_USERNAME));
		params.add("customer_username", pref.getPref(Config.PREF_USERNAME));
		params.add("page", "" + offset);
//		params.add("limit", "" + limit);
		params.add("is_taken", "N");
		params.add("is_expired", "Y");

		strUri = Config.API_GET_CUSTOMER_SERVICE_REQUEST;
		// AsyncHttpClient client = new AsyncHttpClient();
		Tracer.d(strUri);
		PARestClient.get(pref.getPref(Config.SERVER), strUri, params,
				new AsyncHttpResponseHandler() {
					@Override
					public void onFailure(int statusCode, Throwable error,
							String content) {
						// TODO Auto-generated method stub
						super.onFailure(statusCode, error, content);

						// TODO Auto-generated method stub
						isLoadingMoreContent = false;
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
					public void onSuccess(String result) {
						// TODO Auto-generated method stub
						super.onSuccess(result);

						Tracer.d(result);
						// TODO Auto-generated method stub
						try {
							if ("authentication fail".equals(result)) {
								listener.doLogout();
							} else {

								ParserBidList parser = new ParserBidList(result);
								if ("success".equals(parser.getStatus())) {

									totalData = parser.total_unread;
									bidList.addAll(parser.getArr());
									hasNext = parser.isNext_page();

									//	data caching edit
									displayData();

//									badge.setText("" + totalData);
//									badge.show();
//
//									Tracer.d("BID LIST SIZE:" + bidList.size());
//									adapter.notifyDataSetChanged();
//
//									hasNext = parser.isNext_page();
//									if (hasNext) {
//										offset += 1;
//
//									}
								} else {
									if (bidList.size() == 0) {
										simpleToast("There's no data for now");
									}
								}
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
						isLoadingMoreContent = false;
						loadingInternetDialog.dismiss();

					}
				});

	}

	private void displayData(){
		badge.setText("" + totalData);
		badge.show();

		Tracer.d("BID LIST SIZE:" + bidList.size());
		adapter.notifyDataSetChanged();

		if (hasNext) {
			offset += 1;

		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btnRegister:
			startActivity(new Intent(getActivity(), ActivityRegister.class));
			break;

		case R.id.btnMenu:
			ActivityLanding parent = (ActivityLanding) getActivity();
			parent.menuClick();
            break;

		case R.id.btnHome:
//			((ActivityLanding) getActivity()).selectItem(0);
			for(int i = getActivity().getSupportFragmentManager().getBackStackEntryCount(); i > 1; i--)
				getActivity().getSupportFragmentManager().popBackStackImmediate();
//			fm.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
			Log.i("backstack entry count: ", String.valueOf(getActivity().getSupportFragmentManager().getBackStackEntryCount()));
            break;

		case R.id.btnIntercom:
            Intercom.client().displayConversationsList();

			break;
		case R.id.job:
			chooseBid(0);
			break;
		case R.id.bid:
			chooseBid(1);
			break;

		case R.id.refresh:
			offset = 1;
			bidList.clear();
			getData();
			break;
		}

	}

	void chooseBid(int type) {
		switch (type) {
		case 0:// job
			job.setBackgroundResource(R.drawable.bg_bid_tab_on);
			bid.setBackgroundResource(R.drawable.bg_bid_tab_off);
			bidList.clear();
			adapter.notifyDataSetChanged();

			hasNext = true;
			isBid = false;
			getData();

			break;
		case 1:// job
			job.setBackgroundResource(R.drawable.bg_bid_tab_off);
			bid.setBackgroundResource(R.drawable.bg_bid_tab_on);
			bidList.clear();
			adapter.notifyDataSetChanged();

			hasNext = true;
			isBid = true;
			getData();

			break;

		}
	}

	class MyBidListAdapter extends BaseAdapter {

		View mLoadingItem;
		BidListHolder holder;

		public MyBidListAdapter() {
			mLoadingItem = inflater.inflate(R.layout.list_loadmore, null);
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			if (hasNext && bidList.size() != 0)
				return bidList.size() + 1;
			else
				return bidList.size();
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return bidList.get(arg0);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			if (hasNext && position == bidList.size() && position > 0) {
				convertView = mLoadingItem;
				if (!isLoadingMoreContent) {
					// Toast.makeText(getApplicationContext(),
					// "Loading more item", Toast.LENGTH_SHORT).show();
					// Log.i("sale", "get data");
					// if(position!=0)
					// getData(GlobalData.categoryId,GlobalData.stateId,GlobalData.locationId,GlobalData.sortId,GlobalData.search);
					getData();
				}
				return convertView;
			} else if (convertView == null || convertView == mLoadingItem
					|| (!hasNext && convertView != null)) {
				convertView = inflater.inflate(R.layout.item_bid_list, null);
				holder = new BidListHolder();
				holder.img = (ImageView) convertView.findViewById(R.id.img);
				holder.txtCat = (TextView) convertView
						.findViewById(R.id.txtCat);
				holder.txtId = (TextView) convertView.findViewById(R.id.txtID);
				holder.txtDate = (TextView) convertView.findViewById(R.id.date);
				holder.txtTime = (TextView) convertView.findViewById(R.id.time);
				holder.txtBidCount = (TextView) convertView
						.findViewById(R.id.txtTotalBid);
				holder.txtBidStatus = (TextView) convertView
						.findViewById(R.id.txtBidStatus);
				convertView.setTag(holder);

			} else {
				holder = (BidListHolder) convertView.getTag();
			}

			try {

				if (convertView != mLoadingItem) {
					BidItem item = (BidItem) getItem(position);

					holder.txtCat.setText(getJobTitle(item.title));
					holder.txtId.setText(item.serial);

					try {
						holder.txtDate.setText(dateToStr(new Date(1000 * Long
								.parseLong(item.preferred_time1_start))));
						holder.txtTime
								.setText(strTimeDiff(
										new Date(),
										new Date(
												1000 * Long
														.parseLong(item.service_request_stop))));

					} catch (Exception e) {
						e.printStackTrace();
					}

					convertView.setTag(R.id.action_settings, item);
					convertView.setTag(R.id.ba_number, position);

					convertView.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							// replaceFragment(R.id.content_frame, new
							// FragmentBidDetail());
							int position = (Integer) v.getTag(R.id.ba_number);
							BidItem item = (BidItem) v
									.getTag(R.id.action_settings);
							item.read_status = "read";
							bidList.set(position, item);

							listener.doFragmentChange(new FragmentBidDetail(
									item), true, "Bid Change");
							// listener.doFragmentChange(new FragmentBidDetail(
							// item.serial), false, "Bid");

						}
					});

					if (position % 2 == 1) {
						convertView.setBackgroundColor(getResources().getColor(
								R.color.merchant_list_row_oven));
					} else {
						convertView.setBackgroundColor(getResources().getColor(
								R.color.merchant_list_row_odd));

					}
					if (!item.serial.contains("VP-")) {
						String bid_count = item.bid_count + "\nBid(s)";
						Spannable span = new SpannableString(bid_count);
						span.setSpan(new RelativeSizeSpan(0.5f),
								bid_count.indexOf("Bid"), bid_count.length(),
								Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
						holder.txtBidCount.setText(span);

						if ("submitted".equals(item.service_request_status)
								|| "under_consideration"
										.equals(item.service_request_status)) {
							holder.txtBidStatus.setText("OPEN");
							// holder.txtBidCount.setBackgroundColor(0xfff6b26b);
							holder.txtBidCount
									.setBackgroundResource(R.drawable.open_up);
							holder.txtBidStatus
									.setBackgroundResource(R.drawable.open_down);

						} else {
                            String serviceRequestStatus = item.service_request_status.toUpperCase();

							holder.txtBidStatus
									.setText(serviceRequestStatus);

                            if (serviceRequestStatus.equals("EXPIRED") ||
                                    serviceRequestStatus.equals("CANCELED") ||
                                    serviceRequestStatus.equals("CANCELLED")) { // Prevent cancelled word to be fix
                                holder.txtBidCount
                                        .setBackgroundResource(R.drawable.expired_up);
                                holder.txtBidStatus
                                        .setBackgroundResource(R.drawable.expired_down);
                            } else {
                                holder.txtBidCount
                                        .setBackgroundResource(R.drawable.close_up);
                                holder.txtBidStatus
                                        .setBackgroundResource(R.drawable.close_down);
                            }
						}

					} else {
						holder.txtBidCount.setText("VO");
						holder.txtBidStatus.setText("OPEN");
						// holder.txtBidCount.setBackgroundColor(0xfff6b26b);
						holder.txtBidCount
								.setBackgroundResource(R.drawable.open_up);
						holder.txtBidStatus
								.setBackgroundResource(R.drawable.open_down);
					}
					holder.img.setVisibility(View.INVISIBLE);
					if (UNREAD.equals(item.read_status)) {
						holder.img.setVisibility(View.VISIBLE);
						holder.img.setImageResource(R.drawable.icon_notif_new);
					} else {
						holder.img.setVisibility(View.INVISIBLE);

						String url = MY_URI
								+ item.service_icon
								+ "?session_username="
								+ pref.getPref(Config.PREF_USERNAME)
								+ "&active_session_token="
								+ pref.getPref(Config.PREF_ACTIVE_SESSION_TOKEN);
						holder.img.setTag(url);
						imageLoader
								.DisplayImage(url, getActivity(), holder.img);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			return convertView;
		}

		class BidListHolder {
			ImageView img;
			TextView txtCat, txtId, txtDate, txtTime, txtBidCount,
					txtBidStatus;
		}

	}

}
