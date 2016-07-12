package com.pa.landing;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.Header;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
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
import com.pa.deals.PackageFragment;
import com.pa.deals.PackagePromoFragment;
import com.pa.merchantlist.FragmentMerchantList;
import com.pa.parser.ParserAdaptiveSearch;
import com.pa.parser.ParserPendingRating;
import com.pa.parser.ParserState;
import com.pa.pojo.AdaptiveSearch;
import com.pa.pojo.JobItem;
import com.pa.pojo.PendingRating;
import com.pa.pojo.State;
import com.coolfindservices.android.RegisterActivity;
import com.coolfindservices.androidconsumer.R;

import io.intercom.android.sdk.Intercom;

public class FragmentNewLanding extends MyFragment implements OnClickListener,
		Config {
	OnFragmentChangeListener listener;
	AutoCompleteTextView txtSearch;
	String f_keyword;
	View btnSearch;
	AutocompleteCustomArrayAdapter myAdapter;
	TextView txtCountry,txtCountry2;
	String[] countrySelection;
    ArrayList<State> storedStates;

	public FragmentNewLanding(){
		
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
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		getStateData();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View v = inflater.inflate(R.layout.fragment_new_landing, null);

		v.findViewById(R.id.btnBrowse).setOnClickListener(this);
		v.findViewById(R.id.btnPromotion).setOnClickListener(this);
		v.findViewById(R.id.btnDeals).setOnClickListener(this);
		v.findViewById(R.id.btnSearch).setOnClickListener(this);
		v.findViewById(R.id.btnMenu).setOnClickListener(this);
		v.findViewById(R.id.btnIntercom).setOnClickListener(this);
		v.findViewById(R.id.txtGettingStarted).setOnClickListener(this);
		v.findViewById(R.id.txtHelp).setOnClickListener(this);
		v.findViewById(R.id.txtListYourService).setOnClickListener(this);
//		v.findViewById(R.id.btnJobRequest).setOnClickListener(this);
//		v.findViewById(R.id.btnAppointment).setOnClickListener(this);
		v.findViewById(R.id.privacy_policy).setOnClickListener(this);
		v.findViewById(R.id.term_of_use).setOnClickListener(this);
		txtSearch = (AutoCompleteTextView) v.findViewById(R.id.txtServiceProvider);
		btnSearch = v.findViewById(R.id.btnSearch);
		txtCountry=(TextView)v.findViewById(R.id.country);
		txtCountry.setOnClickListener(this);

		txtCountry2=(TextView)v.findViewById(R.id.country2);
		txtCountry2.setOnClickListener(this);

		// Preset Singapore For Live only
		if ("2".equals(getActivity().getResources().getString(R.string.server))) {
			txtCountry2.setText("Singapore");
			countrySelection = country;
		} else {
			countrySelection = country2;
		}

		//		Toast.makeText(getActivity(), "Backstack entry count: " + String.valueOf(getActivity().getSupportFragmentManager().getBackStackEntryCount()), Toast.LENGTH_LONG).show();

		return v;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		System.gc();

		//	clear off fragments in backstack
		fm.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

		txtSearch.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				final int DRAWABLE_LEFT = 0;
				final int DRAWABLE_TOP = 1;
				final int DRAWABLE_RIGHT = 2;
				final int DRAWABLE_BOTTOM = 3;

				if (event.getAction() == MotionEvent.ACTION_UP) {
					if (event.getX() >= (txtSearch.getRight() - 1.5 * txtSearch
							.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds()
							.width())) {
						// your action here
						btnSearch.performClick();
					}
				}
				return false;
			}
		});


		try {
			txtSearch.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View arg1,
										int pos, long id) {

					RelativeLayout rl = (RelativeLayout) arg1;
					TextView tv = (TextView) rl.getChildAt(0);
					txtSearch.setText(tv.getText().toString().trim());
					btnSearch.performClick();
				}

			});

			// add the listener so it will tries to suggest while the user types

			txtSearch
					.addTextChangedListener(new CustomAutoCompleteTextChangedListener(
							getActivity()));

			// ObjectItemData has no value at first
			MyObject[] ObjectItemData = new MyObject[0];

			// set the custom ArrayAdapter
			myAdapter = new AutocompleteCustomArrayAdapter(getActivity(),
					R.layout.list_view_row_item, ObjectItemData);
			txtSearch.setAdapter(myAdapter);

			if(!TextUtils.isEmpty(f_country)){
				txtCountry.setText(f_country);
			}

			if(!TextUtils.isEmpty(pref.getPref(Config.PREF_LAST_COUNTRY))){
				txtCountry2.setText(pref.getPref(Config.PREF_LAST_COUNTRY));
			}



		} catch (Exception e) {
			e.printStackTrace();
		}

		getPendingRating();

		doRegisGCM();

		analytic.trackScreen("Select a Service");
	}

	@Override
	public void onResume() {
		super.onResume();

//		getStateData();
	}
	
	void getPendingRating(){
		RequestParams params=new RequestParams();
		params.add("session_username", pref.getPref(Config.PREF_USERNAME));
		params.add("active_session_token", pref.getPref(Config.PREF_ACTIVE_SESSION_TOKEN));
		
		AsyncHttpResponseHandler responseHandler=new AsyncHttpResponseHandler(){
			@Override
			public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
				// TODO Auto-generated method stub
				super.onSuccess(arg0, arg1, arg2);
				try{
				ParserPendingRating parser=new ParserPendingRating(new String(arg2));
				PendingRating pr=parser.getData();
				if("N".equals(pr.rated)){
					job=new JobItem();
					
					job.request_title=pr.rating_data.request_title;
					job.serial=pr.rating_data.serial;
					job.preferred_time1_start=pr.rating_data.preferred_time1_start;
					job.preferred_time2_start=-1+"";
					job.company_name=pr.rating_data.company_name;
					job.merchant_username=pr.rating_data.merchant_username;
					
					showRateMerchant();
				}
				}catch(Exception e){
					
				}
				
			}
		};
		
		PARestClient.get(pref.getPref(Config.SERVER),"user/pending-rating", params, responseHandler);

        GlobalVar.state = pref.getPref(Config.PREF_LAST_STATE);
        GlobalVar.state_short = pref.getPref(Config.PREF_LAST_STATE_SHORT);
	}

    void getStateData(){
        RequestParams params=new RequestParams();
        params.add("session_username", pref.getPref(Config.PREF_USERNAME));
        params.add("active_session_token", pref.getPref(Config.PREF_ACTIVE_SESSION_TOKEN));
//        params.add("country_only", "true");
        params.add("state_only", "true");

        AsyncHttpResponseHandler responseHandler=new AsyncHttpResponseHandler(){

            @Override
            public void onStart() {
                super.onStart();
                loadingInternetDialog.show();
            }

            @Override
            public void onSuccess(int statusCode, String content) {
                super.onSuccess(statusCode, content);
                Log.d("Country", content);
                ParserState parser = new ParserState(content);
                if (parser.getStatus().equalsIgnoreCase("success")) {
                    // Merge states to Malaysia
                    ArrayList<String> merged = new ArrayList<String>();
                    merged.add("Singapore");

                    storedStates = parser.getStates();

                    Iterator<State> iterator = storedStates.iterator();
                    State state;
                    while (iterator.hasNext()) {
                        state = iterator.next();
                        merged.add(malaysia_prefix + state.state_long);
                    }

                    countrySelection = merged.toArray(new String[merged.size()]);
                }
            }

            @Override
            public void onFinish() {
                super.onFinish();
                loadingInternetDialog.hide();
            }
        };

        PARestClient.post(pref.getPref(Config.SERVER), Config.API_LOCATION, params, responseHandler);
    }

    FragmentHomeCategory fragmentHomeCategory;
	
	@Override
	public void onClick(final View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btnSearch:
			 if (isValid()) {
				 
			 listener.doFragmentChange(new FragmentMerchantList(f_keyword,txtCountry.getText().toString()),
			 true, "");
			 }
			
//			Intent intent = new Intent(getActivity().getApplicationContext(),CaptureActivity.class);
//			intent.setAction("com.google.zxing.client.android.SCAN");
//			// this stops saving ur barcode in barcode scanner app's history
//			intent.putExtra("SAVE_HISTORY", false);
//			startActivityForResult(intent, 0);
			break;
		case R.id.btnBrowse:
			if(isValid2()){
				 pref.savePref(Config.PREF_LAST_COUNTRY,f_country2);
				 pref.savePref(Config.PREF_LAST_STATE,GlobalVar.state);
				 pref.savePref(Config.PREF_LAST_STATE_SHORT,GlobalVar.state_short);

                if (f_country2.toLowerCase().contains("malaysia"))
                    GlobalVar.country = "Malaysia";
                else
				    GlobalVar.country=f_country2;

                fragmentHomeCategory = FragmentHomeCategory.newInstance(f_country2);

			listener.doFragmentChange(fragmentHomeCategory, true, "");
			}
			break;
		case R.id.btnPromotion:

			if (isValid2())
			{
				pref.savePref(Config.PREF_LAST_COUNTRY,f_country2);
				pref.savePref(Config.PREF_LAST_STATE,GlobalVar.state);
				pref.savePref(Config.PREF_LAST_STATE_SHORT,GlobalVar.state_short);

				GlobalVar.country = (f_country2.toLowerCase().contains("malaysia")) ?
										"Malaysia" : f_country2;

//				listener.doFragmentChange(PromotionFragment.newInstance(f_country2), true, "");

				String mCountry = (GlobalVar.country.equalsIgnoreCase("Singapore")) ? "sg" : "my";
				String mState 	= (GlobalVar.country.equalsIgnoreCase("Singapore")) ? "" :
										f_country2.replace(malaysia_prefix, "");
				PackagePromoFragment mFragment = PackagePromoFragment.newInstance(mCountry, mState);
				listener.doFragmentChange(mFragment, true, "");
			}

			break;

		case R.id.btnDeals:

			if (isValid2())
			{
				pref.savePref(Config.PREF_LAST_COUNTRY,f_country2);
				pref.savePref(Config.PREF_LAST_STATE,GlobalVar.state);
				pref.savePref(Config.PREF_LAST_STATE_SHORT,GlobalVar.state_short);

				GlobalVar.country = (f_country2.toLowerCase().contains("malaysia")) ?
						"Malaysia" : f_country2;

				listener.doFragmentChange(PackageFragment.newInstance(f_country2), true, "");
			}

			break;
		case R.id.btnMenu:
			ActivityLanding parent = (ActivityLanding) getActivity();
			parent.menuClick();
			break;

		case R.id.btnIntercom:
            Intercom.client().displayConversationsList();
			break;
		
		case R.id.country2:
		case R.id.country:
			analytic.trackScreen("Select Country for Service");
			showSpinnerSelection(countrySelection

					, "Select country", new AdapterView.OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
										int arg2, long arg3) {
					try {
						generalDialog.hide();
						((TextView) v).setText(countrySelection[arg2]);
						v.setTag(arg2);

						State selectedState = storedStates.get(arg2 - 1);
						GlobalVar.state = selectedState.state_long;
						GlobalVar.state_short = selectedState.state_short;


						if (Arrays.equals(country2, state)) {
							v.setTag(R.id.city, state_short[arg2]);
						}

						final TextView tv1 = (TextView) v
								.getTag(R.id.co_state);
						final TextView tv2 = (TextView) v.getTag(R.id.co_city);

						if (tv1 != null && tv2 != null
								&& Arrays.equals(country2, country)) {
							// tv2 = (TextView) tv.getTag(R.id.co_city);
							Tracer.d("debug", "country" + country2[arg2]);
							if ("Singapore".equals(country2[arg2])) {
								tv1.setText("Singapore");
								tv2.setText("Singapore");

							} else {
								tv1.setText("");
								tv1.setHint("Select your state");

								tv2.setText("");
								tv2.setHint("Select your city");
							}

						}

						if (tv2 != null && Arrays.equals(country2, state)) {
							// tv2 = (TextView) tv.getTag(R.id.co_city);
							if ("Singapore".equals(country2[arg2])) {
								tv1.setText("Singapore");
								tv2.setText("Singapore");

							} else {
								tv2.setText("");
								tv2.setHint("Select your city");
							}
						}

					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
			break;

		case R.id.txtGettingStarted:
			showWebDialog("http://www.pageadvisor.com/getting-started/");
			break;

		case R.id.txtHelp:
			showWebDialog("http://help.pageadvisor.com");
			break;

		case R.id.txtListYourService:
			showWebDialog("http://www.pageadvisor.com/register-your-business/");
			break;

//		case R.id.btnJobRequest:
//			((ActivityLanding) getActivity()).selectItem(1);
//			break;
//
//		case R.id.btnAppointment:
//			((ActivityLanding) getActivity()).selectItem(2);
//			break;

        case R.id.privacy_policy:
            showWebDialog("http://services.cool-find.com/privacy-polic");
            break;

        case R.id.term_of_use:
            showWebDialog("http://services.cool-find.com/terms-of-use");
            break;
		}

	}
	
 
	String f_country;
	String f_country2;
	boolean isValid() {
		f_keyword = txtSearch.getText().toString();
		f_country=txtCountry.getText().toString();
		
		if(f_country.equals("Select country")){
			f_country="";
		}
		
		boolean flag = false;
		if (!formCheckString(f_keyword, "service provider")) {
			flag = false;
		} else {
			flag = true;
		}
		return flag;
	}
	
	boolean isValid2(){
		f_country2=txtCountry2.getText().toString();
		
		boolean flag=false;
		if(f_country2.equals("Select country")){
			f_country2="";
		}
		
		if(!formCheckString(f_country2, "country")){
			
		}else{
			flag=true;
		}
		return flag;
	}

    void doRegisGCM(){
		RegisterActivity register=new RegisterActivity(getActivity());
		
		RequestParams params=new RequestParams();
		params.add("device_os","android");
		params.add("device_token",	register.getRegistrationId());
		params.add("active_session_token",pref.getPref(Config.PREF_ACTIVE_SESSION_TOKEN));
		params.add("session_username",pref.getPref(Config.PREF_USERNAME));
		
		
		AsyncHttpResponseHandler responseHandler=new AsyncHttpResponseHandler(){};
		PARestClient.post(pref.getPref(Config.SERVER), Config.API_REGISTER_DEVICE, params, responseHandler);
		
	}
	
	
	protected void performSearch() {
		// TODO Auto-generated method stub
		
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

		PARestClient.get(pref.getPref(Config.SERVER),"service-search/merchant-simple", params,
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

							txtSearch.setAdapter(myAdapter);

						}
					}

				});
	}

	
	JobItem job=null;
	Dialog dialogRateMerchant;
	EditText txtRatingMsg;
	TextView txtRatingServiceName;
	RatingBar ratingPoint;
	String f_rating, f_rating_message;

	void showRateMerchant() {
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
		dialogRateMerchant.setCancelable(false);
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
								dialogRateMerchant.dismiss();
								//reDraw();
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

	
	
}
