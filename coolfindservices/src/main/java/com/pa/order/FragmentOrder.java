package com.pa.order;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
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
import com.pa.parser.ParserJob;
import com.pa.pojo.JobItem;
import com.coolfindservices.androidconsumer.R;

import org.json.JSONObject;

import io.intercom.android.sdk.Intercom;

public class FragmentOrder extends MyFragment implements OnClickListener, Config {

    private static final String LOG_TAG = "FragmentOrder";
    protected ArrayList<JobItem> arr = new ArrayList<>();
    private boolean hasNext = true;
    private boolean isLoadingMoreContent;
    private int totalData;
    private String service_order_status = "ongoing";
    private String page;
	private String isMethod = "SR";
    private boolean isComplete;
    protected String MY_URI, DEAL_URL;

	protected ListView list;
    protected TextView title;
    protected TextView txtBidRequest, txtPackage;
//	protected TextView txtPromotion;
//	protected LinearLayout jobOngoing;
//	protected RelativeLayout jobCompleted;
//	protected View bidWidget;
//	protected BadgeView badge;

    protected OnFragmentChangeListener listener;
    protected ImageLoader imageLoader;

	private Boolean gotData;

	public FragmentOrder(){
		service_order_status = "ongoing";
	}
	
	@SuppressLint("ValidFragment")
	public FragmentOrder(boolean isComplete){
		this(isComplete, "SR");
	}

    @SuppressLint("ValidFragment")
    public FragmentOrder(boolean isComplete, String isMethod) {
		this.service_order_status 	= (isComplete) ? "completed" : "ongoing";
		this.isComplete 			= isComplete;
		this.isMethod				= isMethod;
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

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
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
		MY_URI      = PARestClient.getAbsoluteUrl(pref.getPref(Config.SERVER), "");
		DEAL_URL    = PARestClient.getDealAbsoluteUrl(pref.getPref(Config.SERVER), "");

		getData();
		gotData = true;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View v = inflater.inflate(R.layout.fragment_job_list, null);

		v.findViewById(R.id.btnMenu).setOnClickListener(this);
		v.findViewById(R.id.btnIntercom).setOnClickListener(this);
        v.findViewById(R.id.refresh).setOnClickListener(this);
        v.findViewById(R.id.btnHome).setOnClickListener(this);

		list            = (ListView) v.findViewById(R.id.list);
        title           = (TextView) v.findViewById(R.id.title);
        txtBidRequest   = (TextView) v.findViewById(R.id.txt_bid_request);
		txtPackage		= (TextView) v.findViewById(R.id.txt_package);
//        txtPromotion    = (TextView) v.findViewById(R.id.txt_promotion);

//        txtPromotion.setOnClickListener(this);
        txtBidRequest.setOnClickListener(this);
		txtPackage.setOnClickListener(this);

		changeTabUI(isMethod);


//		jobOngoing = (LinearLayout) v.findViewById(R.id.job_ongoing);
//		jobCompleted = (RelativeLayout) v.findViewById(R.id.job_completed);
//		bidWidget = v.findViewById(R.id.bid_widget);
//
//		jobOngoing.setOnClickListener(this);
//		jobCompleted.setOnClickListener(this);

		v.setOnKeyListener(new View.OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {

				if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK){

					// handle back button
					for(int i = getActivity().getSupportFragmentManager().getBackStackEntryCount(); i > 1; i--)
						getActivity().getSupportFragmentManager().popBackStackImmediate();
					return true;

				}

				return false;
			}
		});

		return v;

	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
//		badge = new BadgeView(getActivity(), bidWidget);
//		// badge.setText("100");
//		badge.setBadgePosition(BadgeView.POSITION_TOP_RIGHT);
//		badge.hide();
//		page = "1";
		Tracer.d(this.getClass().toString());
		if(!gotData)	getData();
		setAdapter();
		if(isComplete){
			title.setText("JOB HISTORY");
			analytic.trackScreen("History List");
		}else{
			title.setText("APPOINTMENT LIST");
			analytic.trackScreen("Appointment List");
		}
		

	}

    public void getData() {
        if (isMethod.equalsIgnoreCase("PR")) {
            getPromoData();
        }else if (isMethod.equalsIgnoreCase("PA")){
			getPackageData();
		}
		else {
            getBidData();
        }
    }

	private void setAdapter(){
		list.setAdapter(new MyListAdapter());
	}

    public void getPromoData() {
        loadingInternetDialog.show();

        AsyncHttpResponseHandler promoHandler = new AsyncHttpResponseHandler(){
            @Override
            public void onSuccess(String content) {
                loadingInternetDialog.dismiss();

                try {
                    JSONObject object = new JSONObject(content);
                    if (object.has("status_code") && object.getInt("status_code") >= 500) {
                        listener.doLogout();
                        return;
                    }

                    ParserJob parser = new ParserJob(content);
                    if (!parser.status.equalsIgnoreCase("success")) {
                        return;
                    }

                    arr = parser.getPromoData();
//                    list.setAdapter(new MyListAdapter());
					setAdapter();
                    if (parser.has_next) {
                        page += 1;
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Throwable error, String content) {
                super.onFailure(statusCode, error, content);
                loadingInternetDialog.dismiss();
            }
        };

        RequestParams params = new RequestParams();
        params.add("customer_username", pref.getPref(Config.PREF_USERNAME));
        params.add("session_username", pref.getPref(Config.PREF_USERNAME));
        params.add("active_session_token", pref.getPref(Config.PREF_ACTIVE_SESSION_TOKEN));
        params.add("status", service_order_status);
        params.add("page", page);
        params.add("limit", "100");

        PARestClient.dealGet(pref.getPref(Config.SERVER), Config.DEAL_API_GET_PROMO_ORDER, params, promoHandler);
    }
	
	public void getBidData() {
		loadingInternetDialog.show();
		//AsyncHttpClient client = new AsyncHttpClient();
		RequestParams params = new RequestParams();
		params.add("customer_username", pref.getPref(Config.PREF_USERNAME));
		params.add("session_username", pref.getPref(Config.PREF_USERNAME));
		params.add("active_session_token",
				pref.getPref(Config.PREF_ACTIVE_SESSION_TOKEN));
		params.add("service_order_status_string", service_order_status);
		params.add("page", page);
		params.add("limit", "10");

		PARestClient.get(pref.getPref(Config.SERVER), Config.API_GET_SERVICE_ORDER

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
						arr = parser.getData();
						Tracer.d("Size:" + arr.size());
//						list.setAdapter(new MyListAdapter());
						setAdapter();
						totalData = parser.total_unread;
						if (parser.has_next) {
							page += 1;
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
			}
		});
	}

	public void getPackageData() {
		loadingInternetDialog.show();

		AsyncHttpResponseHandler promoHandler = new AsyncHttpResponseHandler(){
			@Override
			public void onSuccess(String content) {
				loadingInternetDialog.dismiss();

				try {
					JSONObject object = new JSONObject(content);
					if (object.has("status_code") && object.getInt("status_code") >= 500) {
						listener.doLogout();
						return;
					}

					ParserJob parser = new ParserJob(content);
					if (!parser.status.equalsIgnoreCase("success")) {
						return;
					}

					arr = parser.getPackageData();
//					list.setAdapter(new MyListAdapter());
					setAdapter();
					if (parser.has_next) {
						page += 1;
					}

				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onFailure(int statusCode, Throwable error, String content) {
				super.onFailure(statusCode, error, content);
				loadingInternetDialog.dismiss();
			}
		};

		RequestParams params = new RequestParams();
		params.add("customer_username", pref.getPref(Config.PREF_USERNAME));
		params.add("session_username", pref.getPref(Config.PREF_USERNAME));
		params.add("active_session_token", pref.getPref(Config.PREF_ACTIVE_SESSION_TOKEN));
		params.add("status", service_order_status);
		params.add("page", page);
		params.add("limit", "100");

		PARestClient.dealGet(pref.getPref(Config.SERVER), Config.DEAL_API_GET_PACKAGE_ORDER, params, promoHandler);


	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btnMenu:
			ActivityLanding parent = (ActivityLanding) getActivity();
			parent.menuClick();
			
			break;
        case R.id.btnIntercom:
            Intercom.client().displayConversationsList();
            break;

        case R.id.btnHome:
//            ((ActivityLanding) getActivity()).selectItem(0);
			for(int i = getActivity().getSupportFragmentManager().getBackStackEntryCount(); i > 1; i--)
				getActivity().getSupportFragmentManager().popBackStackImmediate();
            break;

//        case R.id.txt_promotion:
        case R.id.txt_bid_request:
		case R.id.txt_package:
            selectTab(v.getId());
            break;

//		case R.id.job_ongoing:
//		case R.id.job_completed:
//			selectTab(v.getId());
//			break;
			
		case R.id.refresh:
			arr.clear();
			page = "1";
			getData();
			break;
		}
	}

	private void changeTabUI(String method) {
//		if (txtPromotion == null || txtBidRequest == null || txtPackage == null) {
//			return;
//		}
		if (txtBidRequest == null || txtPackage == null) {
			return;
		}

		int padding 		= txtBidRequest.getPaddingBottom();
		int drawablePromo	= (method.equals("PR")) ? R.drawable.btn_dark_orange_off : R.drawable.btn_light_orange_off;
		int drawableBid		= (method.equals("SR")) ? R.drawable.btn_dark_orange_off : R.drawable.btn_light_orange_off;
		int drawablePackage	= (method.equals("PA")) ? R.drawable.btn_dark_orange_off : R.drawable.btn_light_orange_off;

//		txtPromotion.setBackgroundResource(drawablePromo);
		txtBidRequest.setBackgroundResource(drawableBid);
		txtPackage.setBackgroundResource(drawablePackage);

//		txtPromotion.setPadding(padding, padding, padding, padding);
		txtBidRequest.setPadding(padding, padding, padding, padding);
		txtPackage.setPadding(padding, padding, padding, padding);
	}

    private void selectTab(int tab) {
		switch (tab) {
//		case R.id.job_ongoing:
//			jobOngoing.setBackgroundResource(R.drawable.btn_dark_orange_off);
//			jobCompleted.setBackgroundResource(R.drawable.btn_light_orange_off);
//			service_order_status = "ongoing";
//			break;
//		case R.id.job_completed:
//			jobOngoing.setBackgroundResource(R.drawable.btn_light_orange_off);
//			jobCompleted.setBackgroundResource(R.drawable.btn_dark_orange_off);
//			service_order_status = "completed";
//			break;
//            case R.id.txt_promotion:
//				changeTabUI("PR");
//				isMethod = "PR";
//                break;
            case R.id.txt_bid_request:
				changeTabUI("SR");
				isMethod = "SR";
				break;
			case R.id.txt_package:
				changeTabUI("PA");
				isMethod = "PA";
				break;
		}

		arr.clear();
		page = "1";
		getData();
	}

	private class MyListAdapter extends BaseAdapter {
		JobListHolder holder;
		View mLoadingItem;

		public MyListAdapter() {
			mLoadingItem = inflater.inflate(R.layout.list_loadmore, null);
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return arr.size();
		}

		@Override
		public JobItem getItem(int paramInt) {
			// TODO Auto-generated method stub
			return arr.get(paramInt);
		}

		@Override
		public long getItemId(int paramInt) {
			// TODO Auto-generated method stub
			return paramInt;
		}

		@Override
		public View getView(int position, View convertView,
				ViewGroup paramViewGroup) {
			// TODO Auto-generated method stub
			if (hasNext && position == arr.size() && position > 0) {
				convertView = mLoadingItem;
				if (!isLoadingMoreContent) {
					// Toast.makeText(getApplicationContext(),
					// "Loading more item", Toast.LENGTH_SHORT).show();
					// Log.i("sale", "get data");
					// if(position!=0)
					// getData(GlobalData.categoryId,GlobalData.stateId,GlobalData.locationId,GlobalData.sortId,GlobalData.search);
					Log.i(LOG_TAG, "Loading More Content");
                    getData();
				}
				return convertView;
			}

			if (convertView == null || convertView == mLoadingItem
					|| (!hasNext && convertView != null)) {
				convertView = inflater.inflate(R.layout.item_job_list, null);
				holder = new JobListHolder();
				holder.img = (ImageView) convertView.findViewById(R.id.img);
				holder.txtDate = (TextView) convertView.findViewById(R.id.date);
				holder.txtServiceSerial = (TextView) convertView
						.findViewById(R.id.txt_service_serial);
				holder.txtServiceName = (TextView) convertView
						.findViewById(R.id.service_name);
				holder.txtStatus = (TextView) convertView
						.findViewById(R.id.status);
				holder.jobId=(TextView) convertView.findViewById(R.id.job_id);
				convertView.setTag(holder);

			} else {
				holder = (JobListHolder) convertView.getTag();
			}

			try {
				if (convertView != mLoadingItem) {

					JobItem item = (JobItem) getItem(position);
					String name=getJobTitle(item.request_title);
					if(item.serial.contains("VO-")){
						name="[VO]"+name;
					}
					holder.txtServiceName.setText(name);

					long time=0,tmpTime=0;
				
					try{
						tmpTime=1000 * Long
						.parseLong(item.preferred_time1_start);
						if(tmpTime>0)time=tmpTime;
					}catch(Exception e){

					}

					try{
						tmpTime=1000 * Long
						.parseLong(item.preferred_time2_start);
						if(tmpTime>0 && tmpTime<time)time=tmpTime;
					}catch(Exception e){

					}

					
					try {
						if (isMethod.equalsIgnoreCase("SR")) {
							holder.txtDate.setText(getPreferredTime(
									item.preferred_time1_start, item.preferred_time2_start));
						} else {
							String date = (item.preferred_time1_start.trim().
												equalsIgnoreCase("0000-00-00 00:00:00")) ?
											"Not Applicable" : item.preferred_time1_start;
							holder.txtDate.setText(date);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
					holder.jobId.setText("Job ID "+item.serial);
					holder.txtServiceSerial.setText(item.customer_username);
					String status=item.service_order_status;
					//holder.txtStatus.setText(status);

//					if ("ongoing".equals(item.service_order_status)) {
//						if ("false".equals(item.is_verified)) {
//							//verify job
//							holder.txtStatus.setText("Pending service delivery");
//							
//						} else {
//							//open job
//							holder.txtStatus.setText("Job started");
//
//						}
//
//
//					}else{
//						//complete job
//						holder.txtStatus.setText("Job completed");
//
//					}
					holder.txtStatus.setText(item.service_order_status);
					convertView.setTag(R.id.action_settings, item);
					convertView.setTag(R.id.ba_number, position);

					convertView.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							// replaceFragment(R.id.content_frame, new
							// FragmentBidDetail());
							Tracer.d("Job Detail");
							int position = (Integer) v.getTag(R.id.ba_number);
							JobItem item = (JobItem) v
									.getTag(R.id.action_settings);
							item.read_status = "read";
							arr.set(position, item);

							if (isMethod.equalsIgnoreCase("PR")) {
								Fragment f = FragmentPromoOrderDetail.newInstance(item, isComplete);
								listener.doFragmentChange(f, true, "");
								gotData = true;
							} else if (isMethod.equalsIgnoreCase("PA")) {
								Fragment f = FragmentPackageOrderDetail.newInstance(item, isComplete);
								listener.doFragmentChange(f, true, "");
								gotData = true;
							} else {
								listener.doFragmentChange(new FragmentOrderDetail(
										item, isComplete), true, "");
								gotData = true;
							}
						}
					});

					if (position % 2 == 0) {
						convertView.setBackgroundColor(getResources().getColor(
								R.color.merchant_list_row_odd));
					} else {
						convertView.setBackgroundColor(getResources().getColor(
								R.color.merchant_list_row_oven));

					}
					holder.img.setVisibility(View.INVISIBLE);

					if (isMethod.equalsIgnoreCase("SR")) {
						if (UNREAD.equals(item.read_status)) {
							holder.img.setVisibility(View.VISIBLE);
							holder.img.setImageResource(R.drawable.icon_notif_new);
						} else {
							holder.img.setVisibility(View.VISIBLE);
							String url = MY_URI + item.service_icon
									+ "?session_username=" + pref.getPref(Config.PREF_USERNAME)
									+ "&active_session_token=" + pref.getPref(Config.PREF_ACTIVE_SESSION_TOKEN);
							holder.img.setTag(url);
							imageLoader.DisplayImage(url, getActivity(), holder.img);
						}
					} else {
						holder.img.setVisibility(View.VISIBLE);
						holder.img.setImageResource(R.drawable.promo2_placeholder);
					}

				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			return convertView;
		}

		class JobListHolder {
			ImageView img;
			TextView txtServiceName, txtStatus, txtServiceSerial, txtDate;
			TextView jobId;
		}
	}

}
