package com.pa.merchantlist;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.Header;

import android.annotation.SuppressLint;
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
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.pa.common.AutocompleteCustomArrayAdapter;
import com.pa.common.Config;
import com.pa.common.GlobalVar;
import com.pa.common.MyFragment;
import com.pa.common.MyObject;
import com.pa.common.OnFragmentChangeListener;
import com.pa.common.PARestClient;
import com.pa.common.Tracer;
import com.pa.job.FragmentGuestJob;
import com.pa.job.FragmentUserPostAJob;
import com.pa.landing.ActivityLanding;
import com.pa.parser.ParserAdaptiveSearch;
import com.pa.parser.ParserMerchant;
import com.pa.pojo.AdaptiveSearch;
import com.pa.pojo.Merchant;
import com.pa.splash.ActivityLogin;
import com.coolfindservices.androidconsumer.R;

@SuppressLint("ValidFragment")
public class FragmentMerchantList extends MyFragment implements OnClickListener {
	String keyword;
	int offset, limit;
	private boolean hasNext = true;
	private boolean isLoadingMoreContent;
	ArrayList<Merchant> arrMerchant = new ArrayList<Merchant>();
	ListView list;
	ListAdapter adapter;
	LinearLayout bottomMenu1, bottomMenu2;
	ImageView btnAppointment;

	TextView findService;
	AutoCompleteTextView findMerchant;

	Integer catId = null;
	OnFragmentChangeListener listener;
	View empty;
	AutocompleteCustomArrayAdapter myAdapter;
	String country;

	public FragmentMerchantList(){
		
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

	public FragmentMerchantList(String keyword,String country) {
		this.keyword = keyword;
		this.country=country;
	}

	public FragmentMerchantList(String keyword, int catId) {
		this.keyword = keyword;
		this.catId = catId;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View v = inflater.inflate(R.layout.fragment_list_job, null);
		list = (ListView) v.findViewById(R.id.listview);
		findService = (TextView) v.findViewById(R.id.find_service);
		findMerchant = (AutoCompleteTextView) v
				.findViewById(R.id.find_merchant);

		btnAppointment = (ImageView) v.findViewById(R.id.btn_appointment);
		bottomMenu1 = (LinearLayout) v.findViewById(R.id.bottom_menu_1);
		bottomMenu2 = (LinearLayout) v.findViewById(R.id.bottom_menu_2);
		v.findViewById(R.id.btnMenu).setOnClickListener((OnClickListener) this);
		empty = v.findViewById(R.id.empty);
		return v;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		try {
			arrMerchant.clear();
			offset = 1;
			limit = 10;
			adapter = new ListAdapter();
			getMerchantData();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	void initLogedIn() {
		btnAppointment.setImageResource(R.drawable.icon_appointment);
		btnAppointment.setOnClickListener(userListener);
		bottomMenu1.setOnClickListener(userListener);

	}

	void initGuest() {
		btnAppointment.setImageResource(R.drawable.icon_register);
		btnAppointment.setOnClickListener(guestListener);

		bottomMenu1.setOnClickListener(guestListener);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		if (isLogedIn()) {
			initLogedIn();
		} else {
			initGuest();
		}
		if (catId == null) {
			findService.setVisibility(View.GONE);
			findMerchant.setVisibility(View.VISIBLE);
			findMerchant.setText(keyword);

		} else {
			findService.setVisibility(View.VISIBLE);
			findMerchant.setVisibility(View.GONE);

		}

		findMerchant.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				final int DRAWABLE_LEFT = 0;
				final int DRAWABLE_TOP = 1;
				final int DRAWABLE_RIGHT = 2;
				final int DRAWABLE_BOTTOM = 3;

				if (event.getAction() == MotionEvent.ACTION_UP) {
					if (event.getX() >= (findMerchant.getRight() - 1.5 * findMerchant
							.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds()
							.width())) {
						// your action here
						// simpleToast("searching");
						// listener.doFragmentChange(new
						// FragmentMerchantList(findService.getText().toString()),
						// true, "Merchant List");

						// listener.doFragmentChange(new FragmentPostOpenBid());
						performSearch();
					}
				}
				return false;
			}
		});

		try {
			findMerchant.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View arg1,
						int pos, long id) {

					RelativeLayout rl = (RelativeLayout) arg1;
					TextView tv = (TextView) rl.getChildAt(0);
					findMerchant.setText(tv.getText().toString().trim());
					performSearch();
				}

			});

			// add the listener so it will tries to suggest while the user types
			findMerchant
					.addTextChangedListener(new CustomAutoCompleteTextChangedListener(
							getActivity()));

			// ObjectItemData has no value at first
			MyObject[] ObjectItemData = new MyObject[0];

			// set the custom ArrayAdapter
			myAdapter = new AutocompleteCustomArrayAdapter(getActivity(),
					R.layout.list_view_row_item, ObjectItemData);
			findMerchant.setAdapter(myAdapter);

		} catch (Exception e) {
			e.printStackTrace();
		}

		list.setEmptyView(empty);
		list.setAdapter(adapter);

	}

	protected void performSearch() {
		// TODO Auto-generated method stub
		keyword = findMerchant.getText().toString();
		arrMerchant.clear();
		getMerchantData();
	}

	void getMerchantData() {
		isLoadingMoreContent = true;
		loadingInternetDialog.show();

		//AsyncHttpClient client = new AsyncHttpClient();
		String uri = Config.API_SEARCH;// + "?co_name=" + keyword + "&page="
				//+ offset + "&limit=" + limit;
		Tracer.d(uri);
		
		RequestParams params=new RequestParams();
		params.add("co_name", keyword);
		params.add("page", offset+"");
		params.add("limit",limit+"");
		params.add("country",country);
		PARestClient.get(pref.getPref(Config.SERVER),uri,params, new AsyncHttpResponseHandler() {

			@Override
			public void onSuccess(int statusCode, String content) {
				// TODO Auto-generated method stub
				super.onSuccess(statusCode, content);
				Tracer.d(content);
				isLoadingMoreContent = false;
				loadingInternetDialog.dismiss();
				try {
					ParserMerchant parser = new ParserMerchant(content);
					if ("success".equals(parser.status)) {
						hasNext = parser.isNextPage();
						if (hasNext) {
							offset += 1;
							Tracer.d("Ada next page");
						}
						arrMerchant.addAll(parser.getData());
						adapter.notifyDataSetChanged();

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
				isLoadingMoreContent = false;
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
										getMerchantData();
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
		});

	}

	class ListAdapter extends BaseAdapter {

		View mLoadingItem;
		ListHolder holder;

		public ListAdapter() {
			mLoadingItem = inflater.inflate(R.layout.list_loadmore, null);
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			if (hasNext && arrMerchant.size() != 0)
				return arrMerchant.size() + 1;
			else
				return arrMerchant.size();
		}

		@Override
		public Object getItem(int paramInt) {
			// TODO Auto-generated method stub
			return arrMerchant.get(paramInt);
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

			if (hasNext && position == arrMerchant.size() && position > 0) {
				convertView = mLoadingItem;
				if (!isLoadingMoreContent) {
					getMerchantData();
				}
				return convertView;
			} else if (convertView == null || convertView == mLoadingItem
					|| (!hasNext && convertView != null)) {
				convertView = inflater.inflate(R.layout.item_merchant, null);
				holder = new ListHolder();
				holder.status = (ImageView) convertView
						.findViewById(R.id.status);
				holder.rating = (RatingBar) convertView
						.findViewById(R.id.ratingBar1);
				holder.service = (TextView) convertView
						.findViewById(R.id.services);
				holder.name = (TextView) convertView.findViewById(R.id.txtName);
				holder.verified = (ImageView) convertView
						.findViewById(R.id.verified);
				convertView.setTag(holder);

			} else {
				holder = (ListHolder) convertView.getTag();
			}

			try {

				if (convertView != mLoadingItem) {
					Merchant item = (Merchant) getItem(position);
					holder.name.setText(item.co_name);

					String services = item.bulk_children_array;
					services = services.replace("[", "").replace("]", "")
							.replace("\"", "");
					holder.service.setText(services);
					holder.rating.setRating(Float
							.parseFloat(item.co_overall_rating));

					if (item.isVerified()) {
						holder.verified.setVisibility(View.VISIBLE);
					} else {
						holder.verified.setVisibility(View.INVISIBLE);

					}

				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			if (position % 2 == 1) {
				convertView.setBackgroundColor(getResources().getColor(
						R.color.merchant_list_row_oven));
			} else {
				convertView.setBackgroundColor(getResources().getColor(
						R.color.merchant_list_row_odd));

			}
			convertView.setTag(R.id.action_settings, position);
			convertView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View paramView) {
					// TODO Auto-generated method stub
					// simpleToast("Detail");
					int pos = (Integer) paramView.getTag(R.id.action_settings);
					Merchant merchant = arrMerchant.get(pos);
					listener.doFragmentChange(new FragmentMerchantDetail(
							merchant), true, "Merchant Detail");
				}
			});

			return convertView;

		}

		private class ListHolder {
			ImageView status;
			TextView name;
			RatingBar rating;
			TextView service;
			ImageView verified;
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
			case R.id.btnMenu:
				ActivityLanding parent = (ActivityLanding) getActivity();
				parent.menuClick();

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
				// simpleToast("coming soon");
				// listener.doFragmentChange(new FragmentNewLanding(), true,
				// "Browse service provider");
				break;

			case R.id.bottom_menu_1:
				listener.doFragmentChange(new FragmentUserPostAJob(), true,
						"Guest post a job");
				break;

			}
		}
	};

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btnMenu:
			ActivityLanding parent = (ActivityLanding) getActivity();
			parent.menuClick();

			break;
		}
	}

	boolean runningAutoComplete = false;

	public class CustomAutoCompleteTextChangedListener implements TextWatcher {

		public static final String TAG = "CustomAutoCompleteTextChangedListener.java";
		Context context;
		Timer timer = null;

		public CustomAutoCompleteTextChangedListener(Context context) {
			this.context = context;
		}

		@Override
		public void afterTextChanged(Editable s) {
			// TODO Auto-generated method stub

		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onTextChanged(final CharSequence userInput, int start,
				int before, int count) {
			if (timer != null) {
				timer.cancel();
			}
			timer = new Timer();
			timer.schedule(new TimerTask() {
				@Override
				public void run() {
					if (!runningAutoComplete) {
						getActivity().runOnUiThread(new Runnable() {
							
							@Override
							public void run() {
								// TODO Auto-generated method stub
								try {

									// if you want to see in the logcat what the user
									// types
									Log.e(TAG, "User input: " + userInput);

									// MainActivity mainActivity = ((MainActivity)
									// context);
									getSearchSuggestion(userInput.toString());

								} catch (NullPointerException e) {
									e.printStackTrace();
								} catch (Exception e) {
									e.printStackTrace();
								}
								
							}
						});

					}
				}
			}, 400);

		}
	}

	void getSearchSuggestion(String keyword) {
		RequestParams params = new RequestParams();
		params.add("keyword", keyword);

		PARestClient.get(pref.getPref(Config.SERVER),"service-search/auto-complete", params,
				new AsyncHttpResponseHandler() {
					@Override
					public void onStart() {
						// TODO Auto-generated method stub
						super.onStart();
						runningAutoComplete = true;
					}

					@Override
					public void onFinish() {
						// TODO Auto-generated method stub
						super.onFinish();
						runningAutoComplete = false;
					}

					@Override
					public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
						// TODO Auto-generated method stub
						super.onSuccess(arg0, arg1, arg2);
						Tracer.d(new String(arg2));
						AdaptiveSearch adaptiveSearch = new ParserAdaptiveSearch(
								new String(arg2)).getData();

						if ("success".equals(adaptiveSearch.status)) {
							MyObject[] myObject;
							try {
								myObject = new MyObject[adaptiveSearch.result
										.size()];
								for (int i = 0; i < myObject.length; i++) {
									myObject[i] = new MyObject(
											adaptiveSearch.result.get(i));
								}
							} catch (Exception e) {
								myObject = new MyObject[0];
								e.printStackTrace();
							}

							// update the adapater
							myAdapter.notifyDataSetChanged();

							// update the adapter
							myAdapter = new AutocompleteCustomArrayAdapter(
									getActivity(), R.layout.list_view_row_item,
									myObject);

							findMerchant.setAdapter(myAdapter);

						}
					}

				});
	}
}
