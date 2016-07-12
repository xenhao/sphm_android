package com.pa.landing;

import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.pa.common.Config;
import com.pa.common.GlobalVar;
import com.pa.common.ImageLoader;
import com.pa.common.MyFragment;
import com.pa.common.OnFragmentChangeListener;
import com.pa.common.PARestClient;
import com.pa.common.Tracer;
import com.pa.job.FragmentGuestJob;
import com.pa.job.FragmentPostOpenBid;
import com.pa.job.FragmentUserPostAJob;
import com.pa.parser.ParserParentServiceCategoryNew;
import com.pa.pojo.ServiceCategory;
import com.pa.splash.ActivityLogin;
import com.coolfindservices.androidconsumer.R;

import io.intercom.android.sdk.Intercom;

public class FragmentHomeCategory extends MyFragment implements
		OnClickListener, Config {
	ImageView btnAppointment;
	ArrayList<String> arrSuggest = new ArrayList<String>();
	static ArrayList<ServiceCategory> arrServiceCategory = new ArrayList<ServiceCategory>();
	AutoCompleteTextView findService;
	private Date _lastTypeTime = null;
	ServiceAutoCompleteAdapter adapter;
	OnFragmentChangeListener listener;
	LinearLayout bottomMenu1, bottomMenu2;
	GridView gridCat;
	GridAdapter gridAdapter;
	ImageLoader loader;
	String country, state = "";

	public FragmentHomeCategory(){

	}

    public static FragmentHomeCategory newInstance(String country) {
        FragmentHomeCategory f = new FragmentHomeCategory();

        // Supply index input as an argument.
        Bundle args = new Bundle();
        args.putString("country", country);
        f.setArguments(args);

        return f;
    }

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_home, null);
		v.findViewById(R.id.btnMenu).setOnClickListener(this);
		v.findViewById(R.id.btnIntercom).setOnClickListener(this);
		btnAppointment = (ImageView) v.findViewById(R.id.btn_appointment);
		findService = (AutoCompleteTextView) v.findViewById(R.id.find_service);
		bottomMenu1 = (LinearLayout) v.findViewById(R.id.bottom_menu_1);
		bottomMenu2 = (LinearLayout) v.findViewById(R.id.bottom_menu_2);
		gridCat = (GridView) v.findViewById(R.id.gridCategory);

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

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		getServiceData(0);
		gridAdapter = new GridAdapter();
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		analytic.trackScreen("Service Main Cat");
		Tracer.d("Size arr list"+arrServiceCategory.size());;
		
		loader=new ImageLoader(getActivity());
		
		if (isLogedIn()) {
			initLogedIn();
		} else {
			initGuest();
		}

		
		gridCat.setAdapter(gridAdapter);


		adapter = new ServiceAutoCompleteAdapter(getActivity(),
				R.layout.list_item_pa, arrSuggest);
		//findService.setAdapter(adapter);
		findService
				.setOnItemClickListener(new AdapterView.OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> paramAdapterView,
							View paramView, int paramInt, long paramLong) {
						// TODO Auto-generated method stub
						// simpleToast("ID:"+arrServiceCategory.get(paramInt).id);
						prevKeyword = findService.getText().toString();
					}
				});
		findService.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence paramCharSequence,
					int paramInt1, int paramInt2, int paramInt3) {
				// TODO Auto-generated method stub

			}

			@Override
			public void beforeTextChanged(CharSequence paramCharSequence,
					int paramInt1, int paramInt2, int paramInt3) {
				// TODO Auto-generated method stub
				_lastTypeTime = new Date();
			}

			@Override
			public void afterTextChanged(Editable paramEditable) {
				// TODO Auto-generated method stub
				// dispatch after done typing (1 sec after)
				final String str = paramEditable.toString();
				Timer t = new Timer();
				TimerTask tt = new TimerTask() {
					@Override
					public void run() {
						Date myRunTime = new Date();

						if ((_lastTypeTime.getTime() + 1000) <= myRunTime
								.getTime()) {
							findService.post(new Runnable() {

								@Override
								public void run() {
									// TODO Auto-generated method stub
									Log.d("<tag>", "typing finished!!!" + str);
									getSuggestion(str);

								}
							});
						} else {
							Log.d("<tag>", "Canceled");
						}
					}
				};

				t.schedule(tt, 1000);
			}
		});

		findService.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				final int DRAWABLE_LEFT = 0;
				final int DRAWABLE_TOP = 1;
				final int DRAWABLE_RIGHT = 2;
				final int DRAWABLE_BOTTOM = 3;

				if (event.getAction() == MotionEvent.ACTION_UP) {
					if (event.getX() <= (findService.getLeft() + 1.5 * findService
							.getCompoundDrawables()[DRAWABLE_LEFT].getBounds()
							.width())) {
						// your action here
						// simpleToast("searching");
						// listener.doFragmentChange(new
						// FragmentMerchantList(findService.getText().toString()),
						// true, "Merchant List");

						// listener.doFragmentChange(new FragmentPostOpenBid());
					}
				}
				return false;
			}
		});
	}

    void initLogedIn() {
		// btnAppointment.setImageResource(R.drawable.icon_appointment);
		// btnAppointment.setOnClickListener(userListener);
		bottomMenu1.setOnClickListener(userListener);

	}

	void initGuest() {
		// btnAppointment.setImageResource(R.drawable.icon_register);
		// btnAppointment.setOnClickListener(guestListener);

		bottomMenu1.setOnClickListener(guestListener);
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
		}
	}

	String prevKeyword = "";

	void getSuggestion(String keyword) {

		//arrServiceCategory.clear();
		arrSuggest.clear();
		adapter.resultList = arrSuggest;
		adapter.notifyDataSetChanged();
		findService.dismissDropDown();

		String uri = Config.API_AUTO_COMPLETE_SERVICE + "?keyword=" + keyword;
		//AsyncHttpClient client = new AsyncHttpClient();
		if (!keyword.equals(prevKeyword)) {
			PARestClient.get(pref.getPref(Config.SERVER),uri,null, new AsyncHttpResponseHandler() {

				@Override
				public void onSuccess(String content) {
					// TODO Auto-generated method stub
					super.onSuccess(content);
					Tracer.d(content);
					try {
						JSONObject obj = new JSONObject(content);
						if ("success".equals(obj.getString("status"))) {
							JSONArray arr = new JSONArray(obj
									.getString("result"));
							for (int i = 0; i < arr.length(); i++) {
								JSONObject item = arr.getJSONObject(i);
								ServiceCategory sc = new ServiceCategory();
								sc.id = item.getString("id");
								sc.service_name = item.getString("service_name");

								arrServiceCategory.add(sc);
								arrSuggest.add(sc.service_name);

							}
							adapter.resultList = arrSuggest;
							adapter.notifyDataSetChanged();

							findService.showDropDown();
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

				@Override
				public void onFailure(int statusCode, Throwable error,
						String content) {
					// TODO Auto-generated method stub
					super.onFailure(statusCode, error, content);
				}
			});
			prevKeyword = keyword;
		}
	}

	OnClickListener guestListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.btn_appointment:
				GlobalVar.animateLogo = false;
				Tracer.d("Appointment pressed");
				Intent i = new Intent(getActivity(), ActivityLogin.class);
				i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(i);

				break;

			case R.id.bottom_menu_1:
				listener.doFragmentChange(new FragmentGuestJob(), true,
						"Guest post a job");
				break;
			}
		}
	};

	OnClickListener userListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.btn_appointment:
				simpleToast("coming soon");
				break;

			case R.id.bottom_menu_1:
				listener.doFragmentChange(new FragmentUserPostAJob(), true,
						"Guest post a job");
				break;

			}
		}
	};

	private class ServiceAutoCompleteAdapter extends ArrayAdapter<String>
			implements Filterable {
		private ArrayList<String> resultList;

		public ServiceAutoCompleteAdapter(Context context,
				int textViewResourceId, ArrayList<String> arr) {
			super(context, textViewResourceId);
			resultList = new ArrayList<String>(arr);
		}

		@Override
		public int getCount() {
			return resultList.size();
		}

		@Override
		public String getItem(int index) {
			return resultList.get(index);
		}

		@Override
		public Filter getFilter() {
			Filter filter = new Filter() {
				@Override
				protected FilterResults performFiltering(CharSequence constraint) {
					FilterResults filterResults = new FilterResults();
					if (constraint != null) {
						// Retrieve the autocomplete results.
						// resultList = autocomplete(constraint.toString());

						// Assign the data to the FilterResults
						filterResults.values = resultList;
						filterResults.count = resultList.size();
					}
					return filterResults;
				}

				@Override
				protected void publishResults(CharSequence constraint,
						FilterResults results) {
					if (results != null && results.count > 0) {
						notifyDataSetChanged();
					} else {
						notifyDataSetInvalidated();
					}
				}
			};
			return filter;
		}
	}

	void getServiceData(int parentId) {
		loadingInternetDialog.show();

        String argState = getArguments().getString("country");

        if("singapore".equalsIgnoreCase(argState)){
            country="sg";
            state = "";
        }else{
            country="my";
            state = argState.replace(malaysia_prefix, "");
        }

		Tracer.d(API_SERVICE_CATEGORY_HIERARCHICAL + "?parent_id=[" + parentId
				+ "]");
		RequestParams params=new RequestParams();
		params.add("parent_id", "[" + parentId + "]");
		params.add("country", country);
		params.add("state", state);
		params.add("session_username", pref.getPref(Config.PREF_USERNAME));
		params.add("active_session_token", pref.getPref(Config.PREF_ACTIVE_SESSION_TOKEN));
		PARestClient.get(pref.getPref(Config.SERVER),API_SERVICE_CATEGORY_HIERARCHICAL , params,new AsyncHttpResponseHandler() {
			@Override
			public void onFailure(Throwable error, String content) {
				// TODO Auto-generated method stub
				super.onFailure(error, content);
				loadingInternetDialog.dismiss();
				AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
						getActivity());

				// set title
				alertDialogBuilder.setTitle("Network Error");

				// set dialog message
				alertDialogBuilder
						.setMessage("Click yes to reload!")
						.setCancelable(false)
						.setPositiveButton("Yes",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int id) {
										// if this button is clicked, close
										// current activity
										getServiceData(0);
									}
								})
						.setNegativeButton("No",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int id) {
										// if this button is clicked, just close
										// the dialog box and do nothing
										dialog.cancel();
									}
								});

				// create alert dialog
				AlertDialog alertDialog = alertDialogBuilder.create();
				alertDialog.show();


			}

			@Override
			public void onSuccess(int statusCode, String result) {
				// TODO Auto-generated method stub
				super.onSuccess(statusCode, result);
				try {
					System.out.println(result);
					ParserParentServiceCategoryNew parser = new ParserParentServiceCategoryNew(
							result);
					if ("success".equals(parser.status)) {
                        if (parser.arr.get(0).children == null)
                            arrServiceCategory = new ArrayList<ServiceCategory>();
                        else
						    arrServiceCategory = parser.arr.get(0).children;

						Tracer.d("Service size:" + arrServiceCategory.size());
						// drawCategory();
						gridAdapter.notifyDataSetChanged();
					}
				} catch (Exception e) {

				}
//				loadingInternetDialog.dismiss();
			}
			
			@Override
					public void onFinish() {
						// TODO Auto-generated method stub
						super.onFinish();
						loadingInternetDialog.dismiss();
			}
		});

	

	}

	class GridAdapter extends BaseAdapter {
		GridHolder holder;

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return arrServiceCategory.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return arrServiceCategory.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			try{
			if (convertView == null) {
				convertView = inflater.inflate(R.layout.item_grid_category,
						null);
				holder = new GridHolder();
				holder.t = (TextView) convertView.findViewById(R.id.txtCat);
				holder.img = (ImageView) convertView.findViewById(R.id.img);
				convertView.setTag(holder);

			} else {
				holder = (GridHolder) convertView.getTag();
			}

			String uri="";
			if ("0".equals(pref.getPref(Config.SERVER))) {
				uri = "http://" + DOMAIN_DEV + "/";

			} else if ("1".equals(pref.getPref(Config.SERVER))) {

				uri = "http://" + DOMAIN_STAGING + "/";

			}
			else if ("2".equals(pref.getPref(Config.SERVER))) {

				uri = "http://" + DOMAIN_LIVE + "/";

			}
			
			String url=uri+arrServiceCategory.get(position).icon_image
					+"?session_username="+pref.getPref(Config.PREF_USERNAME)
					+"&active_session_token="+pref.getPref(Config.PREF_ACTIVE_SESSION_TOKEN)
					;
			Tracer.d(url);
			holder.img.setTag(url);
			loader.DisplayImage(url, getActivity(), holder.img,false);
			
			holder.t.setText(arrServiceCategory.get(position).service_name);

			convertView.setTag(R.id.action_settings, position);
			convertView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					int pos = (Integer) v.getTag(R.id.action_settings);
					ServiceCategory sc=arrServiceCategory.get(pos);
					ArrayList<ServiceCategory> arr = new ArrayList<ServiceCategory>();
					arr.add(sc);
					listener.doFragmentChange(new FragmentPostOpenBid(arr,country),
							true, "");
					
					analytic.trackCustomDimension("Category",1, arrServiceCategory.get(pos).service_name);

				}
			});
			
			if(url.contains("default")){
				holder.img.setVisibility(View.GONE);
				holder.t.setVisibility(View.VISIBLE);
			}else{
				holder.img.setVisibility(View.VISIBLE);
				holder.t.setVisibility(View.GONE);

			}
			
			
		
			}catch(Exception e){
				e.printStackTrace();
			}
			return convertView;
		}

		class GridHolder {
			TextView t;
			ImageView img;
		}

	}

}
