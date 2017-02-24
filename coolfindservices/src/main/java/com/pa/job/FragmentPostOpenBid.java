package com.pa.job;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.DatePicker;
import android.widget.DatePicker.OnDateChangedListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.TimePicker;
import android.widget.TimePicker.OnTimeChangedListener;
import android.widget.ViewFlipper;

import com.coolfindservices.android.SplashActivity;
import com.kbeanie.imagechooser.api.ChooserType;
import com.kbeanie.imagechooser.api.ImageChooserListener;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nanigans.android.sdk.NanigansEventManager;
import com.nanigans.android.sdk.NanigansEventParameter;
import com.pa.bid.FragmentBid;
import com.pa.bid.FragmentBidDetail;
import com.pa.common.Config;
import com.pa.common.CustomTimePicker;
import com.pa.common.CustomTimePickerDialog;
import com.pa.common.EditTextImeBackListener;
import com.pa.common.FormUtils;
import com.pa.common.GlobalVar;
import com.pa.common.ImageHelper;
import com.pa.common.MyFragment;
import com.pa.common.OnFragmentChangeListener;
import com.pa.common.PARestClient;
import com.pa.common.ProfilUtils;
import com.pa.common.TimeUtils;
import com.pa.common.Tracer;
import com.pa.common.TypefaceEditText;
import com.pa.landing.ActivityLanding;
import com.pa.landing.FragmentNewLanding;
import com.pa.landing.FragmentRevisedLanding;
import com.pa.parser.ParserCountry;
import com.pa.parser.ParserFormParam;
import com.pa.parser.ParserMerchant;
import com.pa.parser.ParserParentServiceCategoryNew;
import com.pa.pojo.FormByService;
import com.pa.pojo.FormParam;
import com.pa.pojo.Merchant;
import com.pa.pojo.ParentServiceCategory;
import com.pa.pojo.QuestionUnit;
import com.pa.pojo.ServiceCategory;
import com.pa.pojo.UserORM;
import com.pa.quick_action_dialog.QuickAction;
import com.coolfindservices.androidconsumer.R;

import io.intercom.android.sdk.Intercom;

@SuppressLint({ "ValidFragment", "NewApi" })
public class FragmentPostOpenBid extends MyFragment implements OnClickListener,
		Config, ImageChooserListener {
	OnFragmentChangeListener listener;
	ArrayList<ServiceCategory> arrServiceCategory = new ArrayList<ServiceCategory>();
	ArrayList<ServiceCategory> arrSubServiceCategory = new ArrayList<ServiceCategory>();
	ArrayList<ServiceCategory> arrSubServiceCategoryAfterDelete = new ArrayList<ServiceCategory>();

	ListView listService;
	ServiceListAdapter serviceListAdapter;

	ListView listSubService;
	SubServiceListAdapter subServiceListAdapter;

	View step1, step2, step3, step4, step5, step6, step7,
			stepFindMerchantByService, stepFindMerchantDetail;
	ViewFlipper pageFlipper;
	int[] selectedSubService;

	ArrayList<ArrayList<FormParam>> paramsCollection;
	ArrayList<QuestionHolder> arrHolder;
	Handler handler;
	boolean isGate0 = false; // category belum dipilih, start dari service
	boolean isGate1 = false; // category uda dipilih dulu
	boolean isGate2 = false; // pilih merchant dulu

	boolean isBrowseMerchantByService = false;
	boolean isOpenBid = true;
	String f_merchant_username, f_service_description;
	LinearLayout wrapperPhoto;

	Merchant merchant;

	Stack<Integer> pageHistory = new Stack<Integer>();
	int prev_page = 1;
	private QuickAction mQuickAction;

	TextView pageTitle;
	RatingBar preferred_rating;
	CustomTimePickerDialog timePickerDialog;

	String country2 = "sg";
    String userAddressCountry = "", userAddressState = "", userAddressStateShort = "", userAddressCity = "";
	String backupCountry;


	public FragmentPostOpenBid(ArrayList<ServiceCategory> arr, String country) {
		backupCountry = country;

		initial(arr, country, "", "", "", "");
	}

    public FragmentPostOpenBid(ArrayList<ServiceCategory> arr,
                               String country,
                               String serviceRequestCountry,
                               String serviceRequestState,
                               String stateShort,
                               String serviceRequestCity) {
        initial(arr, country, serviceRequestCountry, serviceRequestState, stateShort, serviceRequestCity);
    }


	public void initial(ArrayList<ServiceCategory> arr,
                               String country,
                               String serviceRequestCountry,
                               String serviceRequestState,
                               String stateShort,
                               String serviceRequestCity) {
		arrServiceCategory = new ArrayList<ServiceCategory>(arr);
		selectedService = 0;
		isOpenBid = true;
		setGate(1);
		Tracer.d("Category id:" + arr.get(0).id);

		if ("sgd".equals(country.toLowerCase())) {
			country = "sg";
		} else if ("myr".equals(country.toLowerCase())) {
			country = "my";
		}

		country2 = country;
        userAddressCountry = !"".equals(serviceRequestCountry) ? serviceRequestCountry : GlobalVar.country;
		Tracer.d("serviceRequestCountry: " + serviceRequestCountry + "\nGlobalVar.country: " + GlobalVar.country);
//		simpleToast("serviceRequestCountry: " + serviceRequestCountry + "\nGlobalVar.country: " + GlobalVar.country);
//		if(country != null)	userAddressCountry = country;
        userAddressState = !"".equals(serviceRequestState) ? serviceRequestState : GlobalVar.state;
        userAddressStateShort = !"".equals(stateShort) ? stateShort : GlobalVar.state_short;
        userAddressCity = !"".equals(serviceRequestCity) ? serviceRequestCity : "";
	}

	public FragmentPostOpenBid(Merchant merchant, boolean openBid, int gate) {

		ArrayList<ServiceCategory> arr = new ArrayList<ServiceCategory>();
		Tracer.d("Open Bid" + merchant.arrService.size());
		for (int i = 0; i < merchant.arrService.size(); i++) {
			ParentServiceCategory item = merchant.arrService.get(i);
			Tracer.d(item.service_name);

			ServiceCategory sc = new ServiceCategory();
			sc.id = item.id;
			sc.service_name = item.service_name;
			arr.add(sc);

		}
		arrServiceCategory = arr;
		this.merchant = merchant;
		selectedService = 0;
		isOpenBid = openBid;
		setGate(gate);
	}

	public void setMerchant(Merchant merchant) {
		this.merchant = merchant;
	}

	public FragmentPostOpenBid(ArrayList<ServiceCategory> arr, boolean openBid,
			int gate) {
		arrServiceCategory = new ArrayList<ServiceCategory>(arr);
		selectedService = 0;
		isOpenBid = openBid;

		setGate(gate);
	}

	private void setGate(int gate) {
		// TODO Auto-generated method stub
		isGate0 = false;
		isGate1 = false;
		isGate2 = false;
		switch (gate) {
		case 0:
			isGate0 = true;
			break;
		case 1:
			isGate1 = true;
			break;
		case 2:
			isGate2 = true;
			break;

		}
	}

	public FragmentPostOpenBid() {
		isOpenBid = true;
		setGate(0);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View v = inflater.inflate(R.layout.fragment_post_open_bid, null);

		step1 = v.findViewById(R.id.step1);
		step2 = v.findViewById(R.id.step2);
		step3 = v.findViewById(R.id.step3);
		step4 = v.findViewById(R.id.step4);
		step5 = v.findViewById(R.id.step5);
		step6 = v.findViewById(R.id.step6);
		stepFindMerchantByService = v
				.findViewById(R.id.stepFindMerchantByService);

		stepFindMerchantDetail = v
				.findViewById(R.id.stepFindMerchantDetailByService);

		listService = (ListView) step1.findViewById(R.id.list);
		listSubService = (ListView) step2.findViewById(R.id.list);

		v.findViewById(R.id.btnBack).setOnClickListener(this);
		v.findViewById(R.id.btnIntercom).setOnClickListener(this);
		step1.findViewById(R.id.btnCancel2).setOnClickListener(step1listener);
		step1.findViewById(R.id.btnNext2).setOnClickListener(step1listener);

		pageFlipper = (ViewFlipper) v.findViewById(R.id.flipper);

		pageTitle = (TextView) v.findViewById(R.id.page_title);

		//	override back button to only backtrack on steps in this fragment
		v.setFocusableInTouchMode(true);
		v.requestFocus();
		v.setOnKeyListener( new View.OnKeyListener()
		{
			@Override
			public boolean onKey( View v, int keyCode, KeyEvent event )
			{
				if( keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN )
				{	Log.d("OPEN BID BACK PRESS", "[BACK PRESSED]");
					try {
						if (!pageHistory.isEmpty()) {
							int step = pageHistory.pop();
							changeStep(step, false);
						} else {
//							getParentFragment().getChildFragmentManager()
//									.popBackStackImmediate();
							for(int i = getActivity().getSupportFragmentManager().getBackStackEntryCount(); i > 1; i--)
								getActivity().getSupportFragmentManager().popBackStackImmediate();
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
					return true;
				}
				return false;
			}
		} );

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
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btnBack:
			// displayExitFormDialog();
			// getActivity().getSupportFragmentManager().popBackStackImmediate();
			pageBack();
            break;

		case R.id.btnIntercom:
            Intercom.client().displayConversationsList();
			break;
		}
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		Tracer.d("on activity created");
		serviceListAdapter = new ServiceListAdapter();
		listService.setAdapter(serviceListAdapter);

		subServiceListAdapter = new SubServiceListAdapter();
		listSubService.setAdapter(subServiceListAdapter);

		if (isGate0) {
			getServiceData(0);
			changeStep(1);
		} else if (isGate2) {
			changeStep(1);

		} else {
			changeStep(2, true);
		}

		mQuickAction = new QuickAction(getActivity());

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		handler = new Handler() {

			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				super.handleMessage(msg);
				switch (msg.what) {
				case 0:
					showSpinnerSelection(country, txtCountry, "Select country");

					break;
				case 1:
					showSpinnerSelection(state, txtState, "Select state");
					break;
				case 2:
					showSpinnerSelection(city, txtCity, "Select city");

					break;

				}

			}

		};

	}

	void getServiceData(final int parentId) {
		loadingInternetDialog.show();
		// AsyncHttpClient get = new AsyncHttpClient();

		RequestParams params = new RequestParams();
		params.add("parent_id", "[" + parentId + "]");
		params.add("country", this.country2);
		params.add("session_username", pref.getPref(Config.PREF_USERNAME));
		params.add("active_session_token", pref.getPref(Config.PREF_ACTIVE_SESSION_TOKEN));

		PARestClient.get(pref.getPref(Config.SERVER),
				API_SERVICE_CATEGORY_HIERARCHICAL, params,
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
					public void onSuccess(int statusCode, String result) {
						// TODO Auto-generated method stub
						super.onSuccess(statusCode, result);

						try {
							System.out.println(result);
							ParserParentServiceCategoryNew parser = new ParserParentServiceCategoryNew(
									result);
							if ("success".equals(parser.getStatus())) {
								ArrayList<ServiceCategory> arrTmp = new ArrayList<ServiceCategory>();
								arrTmp = parser.getArr().get(0).children;

								for (int i = 0; i < arrTmp.size(); i++) {

								}

								arrServiceCategory = parser.getArr().get(0).children;

								Tracer.d("Service size:"
										+ arrServiceCategory.size());
								// drawCategory();
								serviceListAdapter.notifyDataSetChanged();
							}
						} catch (Exception e) {

						}
						loadingInternetDialog.dismiss();
					}

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
											public void onClick(
													DialogInterface dialog,
													int id) {
												// if this button is clicked,
												// close
												// current activity
												getServiceData(parentId);
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

	String selection_mode = "";

	void getSubServiceData(final String parentId) {
		loadingInternetDialog.show();
		String ssCountry	= "";
		String ssState		= "";
		if(this.country2.equalsIgnoreCase("singapore")){
			ssCountry	= "sg";
			ssState		= "";
		}else{
			ssCountry	= "my";
			ssState		= pref.getPref(Config.PREF_LAST_STATE).replace(malaysia_prefix, "");
		}

		// AsyncHttpClient get = new AsyncHttpClient();
		RequestParams params = new RequestParams();
		params.add("parent_id", "[" + parentId + "]");
		params.add("country", ssCountry);
        params.add("state", ssState);
		params.add("session_username", pref.getPref(Config.PREF_USERNAME));
		params.add("active_session_token", pref.getPref(Config.PREF_ACTIVE_SESSION_TOKEN));

		PARestClient.get(pref.getPref(Config.SERVER),
				API_SERVICE_CATEGORY_HIERARCHICAL, params,
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
					public void onSuccess(int statusCode, String result) {
						// TODO Auto-generated method stub
						super.onSuccess(statusCode, result);
						try {
							// TODO Auto-generated method stub
							ParserParentServiceCategoryNew parser = new ParserParentServiceCategoryNew(
									result);
							if ("success".equals(parser.getStatus())) {
								selection_mode = parser.getArr().get(0).selection_mode;
								arrSubServiceCategory = parser.getArr().get(0).children;
								selectedSubService = new int[arrSubServiceCategory
										.size()];
								if (selectedSubService.length == 1)
									for (int j = 0; j < selectedSubService.length; j++) {
										selectedSubService[j] = 1;
									}

								// drawCategory();
								subServiceListAdapter.notifyDataSetChanged();
							}

						} catch (Exception e) {
							e.printStackTrace();
						}
						loadingInternetDialog.dismiss();

					}

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
											public void onClick(
													DialogInterface dialog,
													int id) {
												// if this button is clicked,
												// close
												// current activity
												getSubServiceData(parentId);
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

	int selectedService = -1;

	class ServiceListAdapter extends BaseAdapter {
		ServiceListHolder holder;

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return arrServiceCategory.size();
		}

		@Override
		public Object getItem(int paramInt) {
			// TODO Auto-generated method stub
			return arrServiceCategory.get(paramInt);
		}

		@Override
		public long getItemId(int paramInt) {
			// TODO Auto-generated method stub
			return paramInt;
		}

		@Override
		public View getView(int position, View v, ViewGroup paramViewGroup) {
			// TODO Auto-generated method stub
			if (v == null) {
				v = inflater.inflate(R.layout.item_category, null);
				holder = new ServiceListHolder();
				holder.chk = (CheckBox) v.findViewById(R.id.chk);
				holder.service = (TextView) v.findViewById(R.id.name);
				v.setTag(holder);

			} else {
				holder = (ServiceListHolder) v.getTag();
			}
			ServiceCategory item = (ServiceCategory) getItem(position);

			holder.service.setText(item.service_name);
			holder.chk.setOnCheckedChangeListener(null);
			holder.chk.setChecked(false);
			if (position == selectedService)
				holder.chk.setChecked(true);

			holder.chk.setTag(R.id.action_settings, position);
			holder.chk
					.setOnCheckedChangeListener(new OnCheckedChangeListener() {

						@Override
						public void onCheckedChanged(
								CompoundButton paramCompoundButton,
								boolean paramBoolean) {
							// TODO Auto-generated method stub

							int pos = (Integer) paramCompoundButton
									.getTag(R.id.action_settings);
							Tracer.d("Check" + pos);
							if (paramBoolean) {
								selectedService = pos;
							} else {
								selectedService = -1;
							}
							Tracer.d("Check" + pos + selectedService);

							serviceListAdapter.notifyDataSetChanged();

						}
					});

			return v;
		}

		class ServiceListHolder {
			TextView service;
			CheckBox chk;
		}

	}

	class SubServiceListAdapter extends BaseAdapter {

		ServiceListHolder holder;

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return arrSubServiceCategory.size();
		}

		@Override
		public Object getItem(int paramInt) {
			// TODO Auto-generated method stub
			return arrSubServiceCategory.get(paramInt);
		}

		@Override
		public long getItemId(int paramInt) {
			// TODO Auto-generated method stub
			return paramInt;
		}

		@Override
		public View getView(int position, View v, ViewGroup paramViewGroup) {
			// TODO Auto-generated method stub
			if (v == null) {
				v = inflater.inflate(R.layout.item_category, null);
				holder = new ServiceListHolder();
				holder.chk = (CheckBox) v.findViewById(R.id.chk);
				holder.service = (TextView) v.findViewById(R.id.name);
				v.setTag(holder);

			} else {
				holder = (ServiceListHolder) v.getTag();
			}
			ServiceCategory item = (ServiceCategory) getItem(position);

			holder.service.setText(item.service_name);
			holder.chk.setOnCheckedChangeListener(null);

			holder.chk.setChecked(false);
			if (selectedSubService[position] == 1)
				holder.chk.setChecked(true);

			v.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					//	disable checkbox
					v.findViewById(R.id.chk).performClick();

					//	next step right away, bypassing next button
					if(GlobalVar.isGuest){
						AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
								getActivity());
						alertDialogBuilder.setTitle("Sign In");
						alertDialogBuilder
								.setMessage("Sign In or Register to Proceed?")
								.setCancelable(false)
								.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog, int id) {
										Intent intent = new Intent(getActivity(), SplashActivity.class);
										intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
										GlobalVar.isResumeGuest = true;
										startActivity(intent);
									}
								})
								.setNegativeButton("No", new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog, int id) {
										dialog.cancel();
									}
								});
						AlertDialog alertDialog = alertDialogBuilder.create();
						alertDialog.show();
					}else {
						if (isValidStep1()) {
							pageHistory.add(prev_page);

							changeStep(3);
						}
					}
				}
			});

			holder.chk.setTag(R.id.action_settings, position);
			holder.chk
					.setOnCheckedChangeListener(new OnCheckedChangeListener() {

						@Override
						public void onCheckedChanged(
								CompoundButton paramCompoundButton,
								boolean paramBoolean) {
							// TODO Auto-generated method stub

							int pos = (Integer) paramCompoundButton
									.getTag(R.id.action_settings);
							Tracer.d("Check here" + pos);

							if (!selection_mode.equals("multi_select_option")) {
								for (int x = 0; x < selectedSubService.length; x++) {
									selectedSubService[x] = 0;
								}
								if (paramBoolean == true) {
									selectedSubService[pos] = 1;
								}
							} else {
								if (paramBoolean == true) {
									selectedSubService[pos] = 1;
								} else {
									selectedSubService[pos] = 0;
								}
							}
							subServiceListAdapter.notifyDataSetChanged();
						}
					});

			return v;
		}

		class ServiceListHolder {
			TextView service;
			CheckBox chk;
		}

	}

	OnClickListener step1listener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
				case R.id.btnCancel2:
					// displayExitFormDialog();
					getActivity().getSupportFragmentManager()
							.popBackStackImmediate();
					break;
				case R.id.btnNext2:
					if(GlobalVar.isGuest){
						AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
								getActivity());
						alertDialogBuilder.setTitle("Sign In");
						alertDialogBuilder
								.setMessage("Sign In or Register to Proceed?")
								.setCancelable(false)
								.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog, int id) {
										Intent intent = new Intent(getActivity(), SplashActivity.class);
										intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
										GlobalVar.isResumeGuest = true;
										startActivity(intent);
									}
								})
								.setNegativeButton("No", new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog, int id) {
										dialog.cancel();
									}
								});
						AlertDialog alertDialog = alertDialogBuilder.create();
						alertDialog.show();
					}else {
						if (isValidStep1()) {
							pageHistory.add(prev_page);

							changeStep(2);
						}
					}
					break;
			}
		}
	};

	void showStep1(boolean redo) {
		pageTitle.setText("POST A JOB");
		TextView merchant_username = (TextView) step1
				.findViewById(R.id.merchant_name);
		if (merchant != null && merchant.username.length() > 0) {
			merchant_username.setText(merchant.co_name);
			merchant_username.setVisibility(View.VISIBLE);
		} else {
			merchant_username.setVisibility(View.GONE);
		}
	}

	void changeStep(int page, boolean redo) {
		Tracer.d("step", "Change step to: " + page);
		pageTitle.setText("");
		prev_page = page;
		pageFlipper.setDisplayedChild(page - 1);
		switch (page) {
		case 1:
			showStep1(redo);
			break;
		case 2:
			//	hide bottom navigation bar
			((ActivityLanding) getActivity()).showBottomBar(false);
			showStep2(redo);
			break;
		case 3:
			showStep3(redo);
			break;
		case 4:
			showStep4(redo);
			break;
		case 5:
			showStep5(redo);
			break;
		case 6:
			showStep6(redo);
			break;

		case 7:
			showStepFindMerchantByService(redo);
			break;
		case 8:
			showStepMerchantDetail(redo);
			break;

		}

	}

	TextView findService;
	ListView listMerchantByService;

	private void showStepFindMerchantByService(boolean redo) {
		pageTitle.setText("POST A JOB");

		// TODO Auto-generated method stub
		if (redo) {
			offsetMerchantService = 1;
			limitMerchantService = 10;
			findService = (TextView) stepFindMerchantByService
					.findViewById(R.id.find_service);

			findService.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					showDialogChooseSubService();
				}
			});

			listMerchantByService = (ListView) stepFindMerchantByService
					.findViewById(R.id.listview);
			adapterMerchantService = new ListAdapterMerchantService();
			listMerchantByService.setEmptyView(stepFindMerchantByService
					.findViewById(R.id.empty));

			listMerchantByService.setAdapter(adapterMerchantService);

			findService
					.setText(arrServiceCategory.get(selectedService).service_name);
			// keyword = findService.getText().toString();
			keyword = "";
			offsetMerchantService = 1;
			for (int i = 0; i < selectedSubService.length; i++) {
				if (selectedSubService[i] == 1) {
					keyword = keyword
							+ arrSubServiceCategory.get(i).service_name + ",";
				}
			}
			if (keyword.length() > 0)
				keyword = keyword.substring(0, keyword.length() - 1);
			arrMerchant.clear();
			getMerchantDataByService();

		}
	}

	Dialog dialogChooseSubService;

	void showDialogChooseSubService() {
		View v = inflater.inflate(R.layout.dialog_choose_sub_service, null);
		v.findViewById(R.id.btnNext2).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialogChooseSubService.dismiss();
				keyword = "";
				offsetMerchantService = 1;
				arrMerchant.clear();
				for (int i = 0; i < selectedSubService.length; i++) {
					if (selectedSubService[i] == 1) {
						keyword = keyword
								+ arrSubServiceCategory.get(i).service_name
								+ ",";

					}
				}
				if (keyword.length() > 0)
					keyword = keyword.substring(0, keyword.length() - 1);

				getMerchantDataByService();
			}
		});

		ListView list = (ListView) v.findViewById(R.id.list);
		list.setAdapter(subServiceListAdapter);

		dialogChooseSubService = new Dialog(getActivity(), R.style.PauseDialog);
		dialogChooseSubService.getWindow().requestFeature(
				Window.FEATURE_NO_TITLE);
		dialogChooseSubService.getWindow().setLayout(
				WindowManager.LayoutParams.MATCH_PARENT,
				WindowManager.LayoutParams.MATCH_PARENT);
		dialogChooseSubService.setContentView(v);
		dialogChooseSubService.getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

		dialogChooseSubService.show();
	};

	void toggleFav() {
		String is_favourite = "";
		if (merchant.isFavourite()) {
			is_favourite = "N";
		} else {
			is_favourite = "Y";
		}

		loadingInternetDialog.show();
		// AsyncHttpClient client = new AsyncHttpClient();
		RequestParams params = new RequestParams();
		params.add("session_username", pref.getPref(PREF_USERNAME));
		params.add("active_session_token",
				pref.getPref(PREF_ACTIVE_SESSION_TOKEN));
		params.add("merchant_username", merchant.username);
		params.add("is_favourite", is_favourite);

		PARestClient.post(pref.getPref(Config.SERVER), API_SET_FAVE_MERCHANT,
				params, new AsyncHttpResponseHandler() {
					@Override
					public void onFailure(int statusCode, Throwable error,
							String content) {
						// TODO Auto-generated method stub
						super.onFailure(statusCode, error, content);

						loadingInternetDialog.dismiss();
					}

					@Override
					public void onSuccess(int statusCode, String content) {
						// TODO Auto-generated method stub
						super.onSuccess(statusCode, content);
						try {
							JSONObject obj = new JSONObject(content);
							if ("success".equals(obj.getString("status"))) {
								if (merchant.isFavourite()) {
									merchant.is_favourite = "N";
									btnFavMerchant
											.setImageResource(R.drawable.fav_off);
								} else {
									merchant.is_favourite = "Y";
									btnFavMerchant
											.setImageResource(R.drawable.fav_on);

								}

							}
						} catch (Exception e) {
							e.printStackTrace();
						}
						loadingInternetDialog.dismiss();
					}
				});

	}

	ImageView btnFavMerchant;

	void showStepMerchantDetail(boolean redo) {
		pageTitle.setText("POST A JOB");

		if (redo) {
			try {

				stepFindMerchantDetail.findViewById(R.id.learn_more)
						.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
								// TODO Auto-generated method stub
								mQuickAction
										.setRootViewId(R.layout.quickaction_for_verified_badge);
								mQuickAction.show(v);
								mQuickAction
										.setAnimStyle(QuickAction.ANIM_GROW_FROM_CENTER);
							}
						});

				stepFindMerchantDetail.findViewById(R.id.btnCloseBid)
						.setOnClickListener(stepMerchantDetailListener);
				stepFindMerchantDetail.findViewById(R.id.btnCancel2)
						.setOnClickListener(stepMerchantDetailListener);

				// stepFindMerchantDetail.findViewById(R.id.btnBack)
				// .setOnClickListener(stepMerchantDetailListener);

				TextView txtName = (TextView) stepFindMerchantDetail
						.findViewById(R.id.txtName);
				TextView txtService = (TextView) stepFindMerchantDetail
						.findViewById(R.id.txtCatService);
				TextView txtAddress = (TextView) stepFindMerchantDetail
						.findViewById(R.id.txtAddr);
				RatingBar rating = (RatingBar) stepFindMerchantDetail
						.findViewById(R.id.ratingBar1);
				btnFavMerchant = (ImageView) stepFindMerchantDetail
						.findViewById(R.id.btnFav);

				btnFavMerchant.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						toggleFav();
					}
				});

				if (merchant.isFavourite()) {
					btnFavMerchant.setImageResource(R.drawable.fav_on);
				} else {
					btnFavMerchant.setImageResource(R.drawable.fav_off);

				}
				LinearLayout wrapperVerified = (LinearLayout) stepFindMerchantDetail
						.findViewById(R.id.wrapper_verified);

				if (merchant.isVerified()) {
					wrapperVerified.setVisibility(View.VISIBLE);
				} else {
					wrapperVerified.setVisibility(View.GONE);

				}

				txtName.setText(merchant.co_name);
				txtAddress.setText(merchant.co_address_1);
				txtService.setText(merchant.bulk_children_array
						.replace("[", "").replace("]", "").replace("\"", ""));
				rating.setRating(Float.parseFloat(merchant.co_overall_rating));

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	OnClickListener stepMerchantDetailListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.btnCloseBid:
				isOpenBid = false;
				pageHistory.add(prev_page);

				//	hide bottom navigation bar
				((ActivityLanding) getActivity()).showBottomBar(false);
				changeStep(3);
				break;
			case R.id.btnCancel2:
			case R.id.btnBack:
				changeStep(7);
				pageHistory.pop();
				break;
			}
		}
	};

	public void pageBack() {
		try {
			if (!pageHistory.isEmpty()) {
				int step = pageHistory.pop();
				changeStep(step, false);
			} else {
				getActivity().getSupportFragmentManager()
						.popBackStackImmediate();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	void changeStep(int page) {
		changeStep(page, true);
	}

	protected boolean isValidStep1() {
		// TODO Auto-generated method stub
		boolean flag = false;

		if (selectedService == -1) {
			simpleToast("Please select a service!");
		} else {
			flag = true;
		}

		return flag;
	}

	private void showStep2(boolean search) {
		analytic.trackScreen("Service Sub Cat");

		pageTitle.setText("POST A JOB");

		// TODO Auto-generated method stub
		try {
//			TextView serviceName = (TextView) step2
//					.findViewById(R.id.txtService);
//
//			serviceName
//					.setText(arrServiceCategory.get(selectedService).service_name);
			step2.findViewById(R.id.btnCancel2).setOnClickListener(
					step2listener);
			step2.findViewById(R.id.btnCancel2).setOnClickListener(
					step2listener);
			step2.findViewById(R.id.btnNext2).setOnClickListener(step2listener);

			if (isGate2) {
				arrSubServiceCategory = merchant.arrService
						.get(selectedService).children;
				selectedSubService = new int[arrSubServiceCategory.size()];

				if (selectedSubService.length == 1)
					for (int j = 0; j < selectedSubService.length; j++) {
						selectedSubService[j] = 1;
					}
				// drawCategory();
				subServiceListAdapter.notifyDataSetChanged();
			} else if (search) {
				getSubServiceData(arrServiceCategory.get(selectedService).id);
			}

			// if (isGate2) {
			// step2.findViewById(R.id.btnCancel).setVisibility(View.GONE);
			// step2.findViewById(R.id.btnNext).setVisibility(View.GONE);
			// step2.findViewById(R.id.btnNext2).setVisibility(View.VISIBLE);
			// } else
			{
				// step2.findViewById(R.id.btnNext2).setVisibility(View.GONE);
//				step2.findViewById(R.id.btnCancel2).setVisibility(View.VISIBLE);
				step2.findViewById(R.id.btnNext2).setVisibility(View.GONE);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	OnClickListener step2listener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
				case R.id.btnCancel2:
				case R.id.btnBack:
					if (!isGate1) {
						changeStep(1);
						pageHistory.pop();
					} else {
						// displayExitFormDialog();
						getActivity().getSupportFragmentManager()
								.popBackStackImmediate();
					}
					break;

				// case R.id.btnCancel:
				// // simpleToast("Soon");
				// if (isValidStep2()) {
				//
				// isOpenBid = false;
				// isBrowseMerchantByService = true;
				// pageHistory.add(prev_page);
				//
				// changeStep(7);
				// }
				// break;
				// case R.id.btnNext2:
				// if (isValidStep2()) {
				// pageHistory.add(prev_page);
				//
				// changeStep(3);
				// }
				//
				// break;
				case R.id.btnNext2:
					// changeStep(3);
					if(GlobalVar.isGuest){
						AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
								getActivity());
						alertDialogBuilder.setTitle("Sign In");
						alertDialogBuilder
								.setMessage("Sign In or Register to Proceed?")
								.setCancelable(false)
								.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog, int id) {
										Intent intent = new Intent(getActivity(), SplashActivity.class);
										intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
										GlobalVar.isResumeGuest = true;
										startActivity(intent);
									}
								})
								.setNegativeButton("No", new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog, int id) {
										dialog.cancel();
									}
								});
						AlertDialog alertDialog = alertDialogBuilder.create();
						alertDialog.show();
					}else {
						isBrowseMerchantByService = false;

						if (isValidStep2()) {
							pageHistory.add(prev_page);

							changeStep(3);

						}
					}
					break;
			}
		}
	};

	boolean isValidStep2() {
		boolean flag = false;

		for (int i = 0; selectedSubService != null
				&& i < selectedSubService.length; i++) {
			if (selectedSubService[i] == 1) {
				flag = true;
				analytic.trackCustomDimension("Create Bid",2,
						arrSubServiceCategory.get(i).service_name);

				// break;
			}
		}

		if (!flag) {
			simpleToast("Please select a sub service");
		}

		return flag;
	}

	EditText e_txt_service_description;
	TextView e_txt_service_title;
	EditText e_other_remarks;
	View addPhoto;

	void showStep3(boolean redo) {
		pageTitle.setText("JOB DETAILS");
		analytic.trackScreen("Service Fields Details");
		try {
			if (redo) {
				getField();
				step3.findViewById(R.id.btnCancel2).setOnClickListener(
						step3listener);
				step3.findViewById(R.id.btnNext2).setOnClickListener(
						step3listener);
				e_txt_service_description = (EditText) step3
						.findViewById(R.id.txt_service_description);
				e_txt_service_title = (TextView) step3
						.findViewById(R.id.txt_service_title);
				e_other_remarks = (EditText) step3
						.findViewById(R.id.txt_other_remark);
				wrapperPhoto = (LinearLayout) step3
						.findViewById(R.id.wrapperPhoto);
				wrapperPhoto.removeAllViews();
				addPhoto = step3.findViewById(R.id.add);

				addPhoto.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						addPhotoToWrapper();
						analytic.trackScreen("Service Fields Upload Picture");
					}
				});
				String title = "";
				title += "["
						+ arrServiceCategory.get(selectedService).service_name
						+ "]";
				title += " [";
                String tempTitle = "";
				for (int i = 0; i < selectedSubService.length; i++) {
					if (selectedSubService[i] == 1) {
                        tempTitle += " "
								+ arrSubServiceCategory.get(i).service_name
								+ "/";
					}
				}
                title += tempTitle.trim();
				title = title.substring(0, title.length() - 1);
				title += "]";
				title += " from "
						+ new ProfilUtils(getActivity()).getUser().result.cs_name;// pref.getPref(PREF_USERNAME);
				e_txt_service_title.setText(title);
				Tracer.d("JOB TITLE" + title);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	int getVisibleChild(LinearLayout parent) {
		int childCount = parent.getChildCount();
		int count = 0;
		for (int i = 0; i < childCount; i++) {
			if (parent.getChildAt(i).getVisibility() == View.VISIBLE) {
				count++;
			}
		}
		return count+1;
	}

	void addPhotoToWrapper() {
		//	select image method
		if (wrapperPhoto.getChildCount() < 4) {
			View v = inflater.inflate(R.layout.item_upload_photo_small, null);
			ImageView img = (ImageView) v.findViewById(R.id.img);
			generalIMG = img;
			generalView = v;
			// createMyFragmentPhotoDialog();
			boolean didPickImage = createPhotoDialog(this);

			v.setVisibility(View.GONE);
//			if(didPickImage) {
			wrapperPhoto.addView(v);
//			}
			v.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					showDialogPhotoPreview(v);
				}
			});
//			if (getVisibleChild(wrapperPhoto) >= 4) {
//				addPhoto.setVisibility(View.GONE);
//			}
		} else {
//			addPhoto.setVisibility(View.GONE);
			simpleToast("Max 4 photos");
		}

	}

	public void removePhotoFromWrapper(View v) {
		addPhoto.setVisibility(View.VISIBLE);
		wrapperPhoto.removeView(v);

	}

	@Override
	public void onActivityResult(int requestCode, int resultCode,
			android.content.Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case ChooserType.REQUEST_PICK_PICTURE:
		case ChooserType.REQUEST_CAPTURE_PICTURE:
		case Config.CROP_FROM_CAMERA:
			View view = (View) generalIMG.getParent();
			view.setVisibility(View.VISIBLE);
			break;
		}

	};

	Dialog dialogPhotoPreview;

	protected void showDialogPhotoPreview(final View currentView) {
		View v = inflater.inflate(R.layout.dialog_view_photo, null);
		ImageView currentImg = (ImageView) currentView.findViewById(R.id.img);

		ImageView img = (ImageView) v.findViewById(R.id.img);
		img.setImageDrawable(currentImg.getDrawable());

		v.findViewById(R.id.btnCancel2).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						dialogPhotoPreview.dismiss();
					}
				});

		v.findViewById(R.id.btnNext2).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialogPhotoPreview.dismiss();
				removePhotoFromWrapper(currentView);
			}
		});

		// TODO Auto-generated method stub
		dialogPhotoPreview = new Dialog(getActivity(), R.style.PauseDialog);
		dialogPhotoPreview.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		dialogPhotoPreview.getWindow().setLayout(
				WindowManager.LayoutParams.MATCH_PARENT,
				WindowManager.LayoutParams.MATCH_PARENT);
		dialogPhotoPreview.setContentView(v);
		dialogPhotoPreview.getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

		dialogPhotoPreview.show();
	}

	private void getField() {
		String subservice = "";
		for (int i = 0; i < selectedSubService.length; i++) {
			if (selectedSubService[i] == 1) {
				subservice = subservice + arrSubServiceCategory.get(i).id + ",";
			}
		}
		if (subservice.length() > 0)
			subservice = subservice.substring(0, subservice.length() - 1);
		loadingInternetDialog.show();
		paramsCollection = new ArrayList<ArrayList<FormParam>>();
		// TODO Auto-generated method stub
		// AsyncHttpClient client = new AsyncHttpClient();
		RequestParams params = new RequestParams();
		// subservice = "27";
		params.add("service_id_array", "[" + subservice + "]");
		Tracer.d(subservice);
		PARestClient.post(pref.getPref(Config.SERVER), Config.API_FORM_FIELD,
				params, new AsyncHttpResponseHandler() {
					@Override
					public void onSuccess(int statusCode, String content) {
						// TODO Auto-generated method stub
						super.onSuccess(statusCode, content);
						loadingInternetDialog.dismiss();
						ParserFormParam parser = new ParserFormParam(content);
						Tracer.d(content + parser.status);

						if ("success".equals(parser.status)) {
							ArrayList<FormByService> arrFormByService = parser
									.getData();

							// arrFormByService.get(0).form_data
							// .addAll(arrFormByService.get(0).form_data);
							// arrFormByService.get(0).form_data
							// .addAll(arrFormByService.get(0).form_data);

							arrSubServiceCategoryAfterDelete.clear();
							for (int i = 0; i < selectedSubService.length; i++) {
								if (selectedSubService[i] == 1) {
									boolean hasData = false;
									for (int j = 0; j < arrFormByService.size(); j++) {
										if (arrFormByService.get(j).service_id
												.equals(arrSubServiceCategory
														.get(i).id)) {
											paramsCollection
													.add(new ArrayList<FormParam>(
															arrFormByService
																	.get(j).form_data));
											hasData = true;
											// break;
										}
									}
									if (!hasData) {
										// paramsCollection
										// .add(new ArrayList<FormParam>(
										// arrFormByService.get(0).form_data));

									}
									Tracer.d("debug", "Service Id added:"
											+ arrSubServiceCategory.get(i).id);
									arrSubServiceCategoryAfterDelete
											.add(arrSubServiceCategory.get(i));
								}
							}
							Tracer.d("" + parser.getData().size());
							drawStep3();

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

	void drawStep3() {
		int k = 0;
		arrHolder = new ArrayList<FragmentPostOpenBid.QuestionHolder>();

		final LinearLayout wrapper = (LinearLayout) step3
				.findViewById(R.id.wrapper);
		wrapper.removeAllViews();
		for (int i = 0; i < selectedSubService.length; i++) {
			Tracer.d("Selected sub:" + i + " " + selectedSubService[i]);
			if (selectedSubService[i] == 1) {
				View catWrapper = inflater.inflate(
						R.layout.layout_category_wrapper, null);
				TextView catParent = (TextView) catWrapper
						.findViewById(R.id.catParent);
				catParent.setText(arrSubServiceCategory.get(i).service_name);

				LinearLayout content = (LinearLayout) catWrapper
						.findViewById(R.id.categoryWrapper);

				catParent.setOnClickListener(new OnClickListener() {

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

				content.removeAllViews();
				try {
					Tracer.d("Param collections :" + k + " "
							+ paramsCollection.get(k).size());
					ArrayList<FormParam> params = paramsCollection.get(k);

					for (int j = 0; j < params.size(); j++) {
						FormParam param = params.get(j);
						if ("string".equals(param.param_type)) {
							// View v = inflater.inflate(R.layout.form_string,
							// null);
							// TextView question = (TextView) v
							// .findViewById(R.id.txt_tag);
							// question.setText(param.paramName);

							// content.addView(v);

							QuestionHolder qh1 = new QuestionHolder();
							qh1.serviceId = arrSubServiceCategory.get(i).id;
							qh1.questionType = param.param_type;
							qh1.questionId = param.param_name;
							qh1.tag = param.param_description;
							qh1.key = param.param_name;
							View v = createTextBox(qh1.key, qh1.tag);
							v.setTag(R.id.questionhierarchy, 0);
							qh1.v = v.findViewById(R.id.txt_value);
							arrHolder.add(qh1);

							qh1.v.setTag(R.id.arrow, arrHolder.indexOf(qh1));
							qh1.v.setTag(R.id.arrow_down,
									arrSubServiceCategory.get(i).id);

							content.addView(v);

						} else if ("number".equals(param.param_type)) {
							// View v = inflater.inflate(R.layout.form_number,
							// null);
							// TextView question = (TextView) v
							// .findViewById(R.id.txt_tag);
							// question.setText(param.paramName);
							//
							// content.addView(v);

							QuestionHolder qh1 = new QuestionHolder();
							qh1.serviceId = arrSubServiceCategory.get(i).id;
							qh1.questionType = param.param_type;
							qh1.questionId = param.param_name;
							qh1.tag = param.param_description;
							qh1.key = param.param_name;

							View v = createNumberBox(qh1.key, qh1.tag);
							v.setTag(R.id.questionhierarchy, 0);

							qh1.v = v.findViewById(R.id.txt_value);
							arrHolder.add(qh1);
							qh1.v.setTag(R.id.arrow, arrHolder.indexOf(qh1));
							qh1.v.setTag(R.id.arrow_down,
									arrSubServiceCategory.get(i).id);

							content.addView(v);

						} else if ("option".equals(param.param_type)) {
							try {
								QuestionHolder qh1 = new QuestionHolder();
								qh1.serviceId = arrSubServiceCategory.get(i).id;
								qh1.questionType = param.param_type;
								qh1.questionId = param.param_name;
								qh1.tag = param.param_description;
								qh1.key = param.param_name;

								View v = createDropDown(param.param_name,
										param.param_description,
										param.param_options, false);
								v.setTag(R.id.questionhierarchy, 0);

								qh1.v = v.findViewById(R.id.value);
								arrHolder.add(qh1);
								qh1.v.setTag(R.id.arrow, arrHolder.indexOf(qh1));
								qh1.v.setTag(R.id.arrow_down,
										arrSubServiceCategory.get(i).id);

								Tracer.d("Test index dropdown:"
										+ qh1.v.getTag(R.id.arrow));
								LinearLayout kid = (LinearLayout) v
										.findViewById(R.id.kid);

								content.addView(v);

							} catch (Exception e) {
								e.printStackTrace();
							}

						}

						else if ("multi_select_option".equals(param.param_type)) {
							try {
								QuestionHolder qh1 = new QuestionHolder();
								qh1.serviceId = arrSubServiceCategory.get(i).id;
								qh1.questionType = param.param_type;
								qh1.questionId = param.param_name;
								qh1.tag = param.param_description;
								qh1.key = param.param_name;

								View v = createDropDown(param.param_name,
										param.param_description,
										param.param_options, true);
								v.setTag(R.id.questionhierarchy, 0);

								qh1.v = v.findViewById(R.id.value);
								arrHolder.add(qh1);
								qh1.v.setTag(R.id.arrow, arrHolder.indexOf(qh1));
								qh1.v.setTag(R.id.arrow_down,
										arrSubServiceCategory.get(i).id);

								LinearLayout kid = (LinearLayout) v
										.findViewById(R.id.kid);

								content.addView(v);

							} catch (Exception e) {
								e.printStackTrace();
							}

						}

						if (j > 0) {
							View v = content.getChildAt(j);
							v.setVisibility(View.GONE);
						}

						// option
					}

					View btnDelete = inflater
							.inflate(R.layout.btn_delete, null);
					btnDelete.setTag(i);
					btnDelete.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View arg0) {
							// TODO Auto-generated method stub
							// simpleToast("Btn delete");
							if (getVisibleChild(wrapper) > 1) {
								LinearLayout parent = (LinearLayout) arg0
										.getParent().getParent();
								parent.setVisibility(View.GONE);
								// wrapper.removeView(parent);

								int pos = (Integer) arg0.getTag();
								String id = arrSubServiceCategory.get(pos).id;
								Tracer.d("" + arrHolder.size());
								for (int i = 0; i < arrHolder.size(); i++) {
									Tracer.d("" + i);
									Tracer.d("Id:" + id + "  questionid:"
											+ arrHolder.get(i).serviceId);
									if (id.equals(arrHolder.get(i).serviceId)) {
										arrHolder.remove(i);
										i -= 1;
									}
								}

								for (int i = 0; i < arrSubServiceCategoryAfterDelete
										.size(); i++) {
									if (arrSubServiceCategoryAfterDelete.get(i).id == id) {
										arrSubServiceCategoryAfterDelete
												.remove(i);
									}
								}
							} else {
								simpleToast("You must have at least 1 service");
							}

						}
					});

					content.addView(btnDelete);
				} catch (Exception e) {
					e.printStackTrace();
				}

				wrapper.addView(catWrapper);
				k++;
			}
		}

	}

	OnClickListener step3listener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.btnCancel2:
				if (isBrowseMerchantByService) {
					changeStep(8, false);
				} else {
					changeStep(2, false);
				}
				pageHistory.pop();
				break;
			case R.id.btnNext2:
				try {
					if (isValidStep3()) {
						Tracer.d(generateServiceData());
						pageHistory.add(prev_page);

						changeStep(4);

					}
				} catch (Exception e) {
					e.printStackTrace();
				}

				break;
			}

		}
	};
	String f_other_remarks, f_service_title;

	boolean isValidStep3() {
		boolean flag = true;
		try {
			f_other_remarks = e_other_remarks.getText().toString();
			f_service_description = e_txt_service_description.getText()
					.toString();
			f_service_title = e_txt_service_title.getText().toString();

			hideKeyboard(e_other_remarks);

			if (!formCheckString(f_service_title, "service title")) {
				flag = false;
			}

			// else if (!formCheckString(f_service_description,
			// "service description")) {
			// flag = false;
			// }

			// else if (!formCheckString(f_other_remarks, "other remark")) {
			// flag = false;
			// }

			else {
				for (int i = 0; i < arrHolder.size(); i++) {
					Tracer.d(arrHolder.get(i).serviceId + " "
							+ arrHolder.get(i).tag);
					QuestionHolder holder = arrHolder.get(i);

					if ("string".equals(holder.questionType)
							|| "number".equals(holder.questionType)) {
						// cek kosong
						EditText et = (EditText) holder.v;
						hideKeyboard(et);
						if (!formCheckString(et.getText().toString(),
								holder.key)) {
							flag = false;
							et.requestFocus();
							et.requestFocusFromTouch();
							break;
						}
					} else if ("option".equals(holder.questionType)
							|| "multi_select_option"
									.equals(holder.questionType)) {
						TextView t = (TextView) holder.v;
						if (!formCheckString(t.getText().toString(), holder.key)) {
							flag = false;
							t.requestFocus();
							t.requestFocusFromTouch();
							break;
						}

						try {
							// param_cache.put(arrHolder.get(j).key, value);
							// Tracer.d("Get kids for "+arrHolder.get(j).key);
							LinearLayout kids = (LinearLayout) ((View) holder.v
									.getParent().getParent())
									.findViewById(R.id.kid);
							try {
								if (kids != null) {

									for (int l = 0; l < kids.getChildCount(); l++) {
										LinearLayout content = (LinearLayout) kids
												.getChildAt(l).findViewById(
														R.id.content);

										for (int k = 0; k < content
												.getChildCount(); k++) {
											QuestionHolder qh = (QuestionHolder) content
													.getChildAt(k).getTag(
															R.id.add);

											if ("string"
													.equals(qh.questionType)
													|| "number"
															.equals(qh.questionType)) {
												EditText et = (EditText) qh.v;
												// value =
												// et.getText().toString();
												if (!formCheckString(et
														.getText().toString(),
														qh.key)) {
													flag = false;
													et.requestFocus();
													et.requestFocusFromTouch();
													break;
												}
											} else if ("option"
													.equals(qh.questionType)

											) {
												// TextView t2 = (TextView) qh.v
												// .findViewById(R.id.value);
												// String[] arrVal = (String[])
												// ((View)
												// qh.v
												// .getParent().getParent()).getTag();
												// int pos = (Integer)
												// t2.getTag();
												// value = arrVal[pos];
												TextView t2 = (TextView) qh.v;
												if (!formCheckString(t2
														.getText().toString(),
														qh.key)) {
													flag = false;
													t2.requestFocus();
													t2.requestFocusFromTouch();
													break;
												}

											}

											else if ("multi_select_option"
													.equals(qh.questionType)

											) {
												TextView t2 = (TextView) qh.v
														.findViewById(R.id.value);
												String[] arrVal = (String[]) ((View) qh.v
														.getParent()
														.getParent()).getTag();
												// int pos = (Integer)
												// t.getTag();
												// value =
												// (String)t.getTag();//arrVal[pos];
												if (!formCheckString(t2
														.getText().toString(),
														qh.key)) {
													flag = false;
													t2.requestFocus();
													t2.requestFocusFromTouch();
													break;
												}

											}

											// param_cache.put(qh.key, value);

										}
									}
									Tracer.d("Kids count:"
											+ kids.getChildCount());
								}
							} catch (Exception e) {
								e.printStackTrace();
							}

						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

					}

				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return flag;
	}

	boolean validateOneChild(int position) {
		Tracer.d("Form position:" + position);
		boolean flag = true;

		for (int i = position; i < position + 1; i++) {
			Tracer.d(arrHolder.get(i).serviceId + " " + arrHolder.get(i).tag);
			QuestionHolder holder = arrHolder.get(i);

			if ("string".equals(holder.questionType)
					|| "number".equals(holder.questionType)) {
				// cek kosong
				EditText et = (EditText) holder.v;
				hideKeyboard(et);
				if (TextUtils.isEmpty(et.getText().toString())) {
					flag = false;
					et.requestFocus();
					et.requestFocusFromTouch();
					break;
				}
			} else if ("option".equals(holder.questionType)
					|| "multi_select_option".equals(holder.questionType)) {
				TextView t = (TextView) holder.v;
				if (TextUtils.isEmpty(t.getText().toString())) {
					flag = false;
					t.requestFocus();
					t.requestFocusFromTouch();
					break;
				}

				try {
					// param_cache.put(arrHolder.get(j).key, value);
					// Tracer.d("Get kids for "+arrHolder.get(j).key);
					LinearLayout kids = (LinearLayout) ((View) holder.v
							.getParent().getParent()).findViewById(R.id.kid);
					try {
						if (kids != null) {
							for (int l = 0; l < kids.getChildCount(); l++) {
								LinearLayout content = (LinearLayout) kids
										.getChildAt(l).findViewById(
												R.id.content);

								for (int k = 0; k < content.getChildCount(); k++) {
									QuestionHolder qh = (QuestionHolder) content
											.getChildAt(k).getTag(R.id.add);
									Tracer.d(k + "." + qh.key);
									if ("string".equals(qh.questionType)
											|| "number".equals(qh.questionType)) {
										EditText et = (EditText) qh.v;
										// value = et.getText().toString();
										if (TextUtils.isEmpty(et.getText()
												.toString())
										// !formCheckString(et.getText()
										// .toString(), qh.tag)
										) {
											flag = false;
											et.requestFocus();
											et.requestFocusFromTouch();
											break;
										}
									} else if ("option".equals(qh.questionType)

									) {
										// TextView t2 = (TextView) qh.v
										// .findViewById(R.id.value);
										// String[] arrVal = (String[]) ((View)
										// qh.v
										// .getParent().getParent()).getTag();
										// int pos = (Integer) t2.getTag();
										// value = arrVal[pos];
										TextView t2 = (TextView) qh.v;
										Tracer.d(t2.getText().toString());
										if (TextUtils.isEmpty(t2.getText()
												.toString())
										// !formCheckString(t2.getText()
										// .toString(), qh.tag)
										) {
											flag = false;
											t2.requestFocus();
											t2.requestFocusFromTouch();
											break;
										}

									}

									else if ("multi_select_option"
											.equals(qh.questionType)

									) {
										TextView t2 = (TextView) qh.v
												.findViewById(R.id.value);
										String[] arrVal = (String[]) ((View) qh.v
												.getParent().getParent())
												.getTag();
										// int pos = (Integer) t.getTag();
										// value =
										// (String)t.getTag();//arrVal[pos];
										if (TextUtils.isEmpty(t2.getText()
												.toString())
										// !formCheckString(t2.getText()
										// .toString(), qh.tag)
										) {
											flag = false;
											t2.requestFocus();
											t2.requestFocusFromTouch();
											break;
										}

									}

									// param_cache.put(qh.key, value);
									Tracer.d("Validate one child:" + l + "-"
											+ k + " " + flag);

								}
							}
							Tracer.d("Kids count:" + kids.getChildCount());
						}
					} catch (Exception e) {
						e.printStackTrace();
					}

				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}

		}
		return flag;
	}

	private class QuestionHolder {
		String serviceId;
		String questionId;
		String questionType;
		String questionCompulsoryMark;
		String tag;
		String key;
		View v;

		ArrayList<View> arrSubV;

		public QuestionHolder() {
			arrSubV = new ArrayList<View>();
		}
	}

	private View createTextBox(String param_name, String q) {
		View v;
		v = inflater.inflate(R.layout.form_string, null);
		TextView tag = (TextView) v.findViewById(R.id.txt_tag);
		tag.setText(param_name.replace("_", " "));
		// view=new View(getActivity());
		// questionArea.addView(v);
		final View done = v.findViewById(R.id.done);
		done.setVisibility(View.GONE);
		TypefaceEditText edit = (TypefaceEditText) v
				.findViewById(R.id.txt_value);
		edit.setHint(q.replace("_", ""));
		edit.setOnEditorActionListener(new OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId,
                                          KeyEvent event) {
                // TODO Auto-generated method stub
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    done.performClick();
                    return true;
                }
                return false;
            }
        });
		edit.setOnEditTextImeBackListener(new EditTextImeBackListener() {

            @Override
            public void onImeBack(TypefaceEditText ctrl, String text) {
                // TODO Auto-generated method stub
                done.performClick();

            }
        });

        edit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                displayNextQuestion(done);
            }
        });

		v.findViewById(R.id.done).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				View parent = (View) v.getParent();

				TextView tv = (TextView) parent.findViewById(R.id.txt_value);
				hideKeyboard((EditText) parent.findViewById(R.id.txt_value));
				if (formCheckString(tv.getText().toString(), "this field")) {
					Log.d("Step", "Yes, in here");
					displayNextQuestion(v);

				}

			}
		});

		return v;// v.findViewById(R.id.txt_value);

	}

	private View createNumberBox(String param_name, String q) {
		View v;
		v = inflater.inflate(R.layout.form_number, null);
		final TextView tag = (TextView) v.findViewById(R.id.txt_tag);
		tag.setText(param_name.replace("_", " "));
		// view=new View(getActivity());
		// questionArea.addView(v);

		final View done = v.findViewById(R.id.done);
		done.setVisibility(View.GONE);
		TypefaceEditText edit = (TypefaceEditText) v
				.findViewById(R.id.txt_value);
		edit.setHint(q.replace("_", " "));
		edit.setOnEditorActionListener(new OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId,
                                          KeyEvent event) {
                // TODO Auto-generated method stub
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    done.performClick();
                    return true;
                }
                return false;
            }
        });

        edit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                displayNextQuestion(done);
            }
        });

		v.findViewById(R.id.done).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(final View v) {
                View parent = (View) v.getParent();

                TextView tv = (TextView) parent.findViewById(R.id.txt_value);
                hideKeyboard((EditText) parent.findViewById(R.id.txt_value));

                if (formCheckString(tv.getText().toString(), "this field")) {
                    displayNextQuestion(v);
                }
            }
        });

		return v;// v.findViewById(R.id.txt_value);

    }

    String f_country, f_state, f_city;
    TextView txtCountry, txtState, txtCity;
    TextView e_prefer_date;
    TextView e_rating, e_prefer_time;
    TextView e_second_prefer_date, e_second_prefer_time;
	EditText e_m_address, e_m_postal_code;

	void showStep4(boolean redo) {
		pageTitle.setText("SERVICE DELIVERY DETAILS");
		analytic.trackScreen("Service Delivery Details");
		if (redo) {
			txtCountry = (TextView) step4.findViewById(R.id.country);
			txtState = (TextView) step4.findViewById(R.id.state);
			txtCity = (TextView) step4.findViewById(R.id.city);

			txtCountry.setTag(R.id.co_state, txtState);
			txtCountry.setTag(R.id.co_city, txtCity);
			txtState.setTag(R.id.co_city, txtCity);

			txtCountry.setOnClickListener(step4listener);
			txtState.setOnClickListener(step4listener);
			txtCity.setOnClickListener(step4listener);

			e_prefer_date = (TextView) step4
					.findViewById(R.id.first_prefer_date);
			e_prefer_date.setOnClickListener(step4listener);

			e_second_prefer_date = (TextView) step4
					.findViewById(R.id.second_prefer_date);
			e_second_prefer_date.setOnClickListener(step4listener);

			e_rating = (TextView) step4.findViewById(R.id.txtRating);
			e_prefer_time = (TextView) step4
					.findViewById(R.id.first_prefer_time);
			e_second_prefer_time = (TextView) step4
					.findViewById(R.id.second_prefer_time);

			e_rating.setOnClickListener(step4listener);
			e_prefer_time.setOnClickListener(step4listener);
			e_second_prefer_time.setOnClickListener(step4listener);

			e_m_address = (EditText) step4.findViewById(R.id.txtAddress);
			e_m_postal_code = (EditText) step4.findViewById(R.id.txtPostalCode);
			step4.findViewById(R.id.btnNext2).setOnClickListener(step4listener);
			step4.findViewById(R.id.btnCancel2).setOnClickListener(
                    step4listener);

            // Preset country and state based on user selection on landing page
            txtCountry.setTag(R.id.co_state, txtState);
            txtCountry.setTag(R.id.co_city, txtCity);

            txtState.setTag(R.id.co_city, txtCity);

			if(TextUtils.isEmpty(userAddressCountry)){
				if("singapore".equalsIgnoreCase(GlobalVar.country))
					txtCountry.setText(GlobalVar.country);
				else
					txtCountry.setText("Malaysia");
			}else {
				if("singapore".equalsIgnoreCase(userAddressCountry))
					txtCountry.setText(userAddressCountry);
				else
					txtCountry.setText("Malaysia");
			}	//simpleToast("txtCountry: " + txtCountry.getText().toString() + "\nuserAddressCountry: " + userAddressCountry);

            if ("Singapore".equalsIgnoreCase(txtCountry.getText().toString())) {
                txtState.setVisibility(View.GONE); // Hide State whenever it's Singapore
                txtState.setText(txtCountry.getText().toString());
                txtCity.setVisibility(View.GONE); // Hide City whenever it's Singapore
                txtCity.setText(txtCountry.getText().toString());
            } else {
                txtState.setText(userAddressState);
                txtState.setTag(R.id.city, userAddressStateShort);
                if (!"".equals(userAddressCity)) txtCity.setText(userAddressCity);
            }

            txtCountry.setEnabled(false);
            txtCountry.setCompoundDrawables(null, null, null, null);

            txtState.setEnabled(false);
            txtState.setCompoundDrawables(null, null, null, null);


            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN){
                txtCountry.setBackground(null);
                txtState.setBackground(null);
            } else {
                txtCountry.setBackgroundDrawable(null);
                txtState.setBackgroundDrawable(null);
            }

            getLastAddress();
		}
	}

	void createTimePickerDialog(final View v) {
		timePickerDialog = new CustomTimePickerDialog(getActivity(),
				new CustomTimePickerDialog.OnTimeSetListener() {

					@Override
					public void onTimeSet(TimePicker view, int hourOfDay,
							int minute) {
						// TODO Auto-generated method stub
						TextView tv = (TextView) v;
						String strHour = ("" + hourOfDay).length() == 1 ? "0"
								+ hourOfDay : "" + hourOfDay;
						String strMinute = ("" + minute).length() == 1 ? "0"
								+ minute : "" + minute;

						tv.setText(strHour + ":" + strMinute);
					}
				}, 0, 0, false);
		timePickerDialog.show();

	}

	OnClickListener step4listener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.txtRating:
				showSpinnerSelection(rating, e_rating, "Select rating");
				break;
			case R.id.second_prefer_time:
			case R.id.first_prefer_time:
				// showSpinnerSelection(prefer_time, (TextView) v,
				// "Select time");
				createTimePickerDialog(v);
				break;
			case R.id.second_prefer_date:
			case R.id.first_prefer_date:
				// createDatePickerDialog((TextView) v);
				showCustomDateTimeDialog((TextView) v);
				break;

			// case R.id.start_time:
			// case R.id.end_time:
			// createTimePickerDialog((TextView) v);
			// break;

			case R.id.btnCancel2:
				changeStep(3, false);
				pageHistory.pop();
				break;
			case R.id.btnNext2:
				if (isValidStep4()) {
					pageHistory.add(prev_page);

					try {
						changeStep(5);
					}catch(Exception e){
						e.printStackTrace();
					}
				}
				break;

			case R.id.city:
				// showSpinnerSelection(city, step1_co_city, "Select state");
				getCity((String) txtState.getTag(R.id.city), handler);
				break;

			case R.id.state:
				// showSpinnerSelection(country, step1_co_state,
				// "Select country");
				getState(txtCountry.getText().toString(), handler);

				break;
			case R.id.country:
				// showSpinnerSelection(country, step1_co_state,
				// "Select country");
				getCountry(handler);

				break;

			}
		}
	};

	String f_prefer_date, f_prefer_time, f_second_prefer_date,
			f_second_prefer_time;
	String f_rating, f_merchant_address, f_m_country, f_m_state, f_m_city,
			f_m_postal_code;

	boolean isValidStep4() {
		boolean flag = false;

		f_prefer_date = e_prefer_date.getTag() + "";
		f_prefer_date.trim();
		Tracer.d("First date" + f_prefer_date + " " + f_prefer_date.length());
		// f_prefer_time = e_prefer_time.getText().toString();
		f_second_prefer_date = e_second_prefer_date.getTag() + "";
		Tracer.d("Second date" + f_second_prefer_date + " "
				+ f_second_prefer_date.length());

		// f_second_prefer_time = e_second_prefer_time.getText().toString();
		// if (f_prefer_time.length() > 0)
		// f_prefer_time = f_prefer_time.substring(0, 5);
		// if (f_second_prefer_time.length() > 0)
		// f_second_prefer_time = f_second_prefer_time.substring(0, 5);

		// f_rating = (1+ (Integer)e_rating.getTag())+"";
		f_m_country = txtCountry.getText().toString();
		f_m_state = txtState.getText().toString();
		f_m_city = txtCity.getText().toString();

		f_merchant_address = e_m_address.getText().toString();
		f_m_postal_code = e_m_postal_code.getText().toString();

		hideKeyboard(e_m_address);
		hideKeyboard(e_m_postal_code);

		if (!formCheckString(e_prefer_date.getText().toString(),
				"1st Preferred service date")) {
		}
		// else if (!formCheckString(f_prefer_time,
		// "1st Preferred service time")) {
		// }
		// else
		// if (!formCheckString(f_second_prefer_date,
		// "2nd Preferred service date")) {
		// } else if (!formCheckString(f_second_prefer_time,
		// "2nd Preferred service time")) {
		// }
		// else if (!formCheckString(f_rating, "Merchant rating")) {
		// }
		else if (!formCheckString(f_merchant_address, "Delivery address")) {
		} else if (!formCheckString(f_m_country, "Delivery country")) {
		} else if (!formCheckString(f_m_state, "Delivery state")) {
		} else if (!formCheckString(f_m_city, "Delivery city")) {
		} else if (!formCheckString(f_m_postal_code, "Delivery postal code")) {
		} else {
			UserORM user = new ProfilUtils(getActivity()).getUser();
			if (user != null) {
				if (TextUtils.isEmpty(user.result.currency)) {
					getCurrency(f_m_country);
				}
			}

			flag = true;
		}

		return flag;
	}

	private void getCurrency(String f_m_country2) {
		// TODO Auto-generated method stub
		RequestParams params = new RequestParams();
		params.add("country", f_m_country2);
		PARestClient.get(pref.getPref(Config.SERVER), "location/country-data",
				params, new AsyncHttpResponseHandler() {
					@Override
					public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
						// TODO Auto-generated method stub
						super.onSuccess(arg0, arg1, arg2);

						ParserCountry parser = new ParserCountry(new String(
								arg2));
						currency_default = parser.getData().country_currency;
					}

					@Override
					public void onFailure(int arg0, Header[] arg1, byte[] arg2,
							Throwable arg3) {
						// TODO Auto-generated method stub
						super.onFailure(arg0, arg1, arg2, arg3);
					}

				});
	}

	EditText e_max_budget;
	TextView txtBidPeriod;

	void showStep5(boolean redo) {
		try {
			pageTitle.setText("BID PERIOD AND BUDGET");
			analytic.trackScreen("Bid Period and Budget");
			if (redo) {
				txtBidPeriod = (TextView) step5.findViewById(R.id.txtBidPeriod);
				e_max_budget = (EditText) step5.findViewById(R.id.txtBudget);
				step5.findViewById(R.id.btnCancel2).setOnClickListener(
						step5listener);
				step5.findViewById(R.id.btnNext2).setOnClickListener(step5listener);

				txtBidPeriod.setOnClickListener(step5listener);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	OnClickListener step5listener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.btnCancel2:
				changeStep(4, false);
				pageHistory.pop();
				break;
			case R.id.btnNext2:
				if (isValidStep5()) {
					pageHistory.add(prev_page);

					changeStep(6);
				}
				break;
			case R.id.txtBidPeriod:
				try {
					showSpinnerSelection(BID_PERIOD, (TextView) v,
							"Select bid period");
				}catch(Exception e){
					e.printStackTrace();
				}
				break;
			}
		}
	};

	String f_bid_period, f_max_budget;

	boolean isValidStep5() {
		boolean flag = false;
		f_max_budget = e_max_budget.getText().toString();
		f_bid_period = "";

		hideKeyboard(e_max_budget);
		try {
			int pos = (Integer) txtBidPeriod.getTag();
			Tracer.d("position" + pos);
			f_bid_period = BID_PERIOD_LENGTH[pos] + "";
		} catch (Exception e) {
			e.printStackTrace();
		}
		// if (!formCheckString(f_max_budget, "Max budget")) {
		// flag = false;
		// } else
		if (!formCheckString(f_bid_period, "Bid period")) {
			flag = false;

		} else {
			flag = true;
		}
		return flag;
	}

	String f_service_data;
	TextView ov_txtServiceCat, ov_txtFirstPreferDate, ov_txtSecondPreferDate,
			ov_txtAddress;
	TextView ov_txtServiceTitle, ov_txtServiceDescription,
			ov_txtServiceRemarks, ov_txtMaxBudget;
	LinearLayout wrapperServiceDetail;
	private String currency_default;

	void showStep6(boolean redo) {
		pageTitle.setText("JOB REQUEST REVIEW");
		analytic.trackScreen("Job Request Review");
		UserORM user = new ProfilUtils(getActivity()).getUser();
		String currency = "";

		if (user != null) {
			currency = user.result.currency;
		}
		if (TextUtils.isEmpty(currency)) {
			currency = currency_default;
		}

		try {
			if (redo) {
				step6.findViewById(R.id.btnCancel2).setOnClickListener(
						step6listener);
				step6.findViewById(R.id.btnNext2).setOnClickListener(
						step6listener);
				ov_txtServiceCat = (TextView) step6
						.findViewById(R.id.txtServiceCategory);
				ov_txtFirstPreferDate = (TextView) step6
						.findViewById(R.id.txt_first_prefer_date);
				ov_txtSecondPreferDate = (TextView) step6
						.findViewById(R.id.txt_second_prefer_date);
				ov_txtAddress = (TextView) step6.findViewById(R.id.txt_address);
				ov_txtServiceTitle = (TextView) step6
						.findViewById(R.id.txt_title);
				ov_txtServiceDescription = (TextView) step6
						.findViewById(R.id.txt_description);
				ov_txtServiceRemarks = (TextView) step6
						.findViewById(R.id.txt_remarks);
				ov_txtMaxBudget = (TextView) step6
						.findViewById(R.id.txt_max_budget);

				ov_txtServiceTitle.setText(f_service_title);
				ov_txtServiceDescription.setText(f_service_description);
				ov_txtServiceRemarks.setText(FormUtils
						.printEmptyFormValue(f_other_remarks));

				if (!TextUtils.isEmpty(f_max_budget)) {
					ov_txtMaxBudget.setText(currency + " " + f_max_budget);
				} else {
					ov_txtMaxBudget.setText("No budget specified");

				}
				ov_txtServiceCat.setText(arrServiceCategory
						.get(selectedService).service_name);
				ov_txtFirstPreferDate.setText(e_prefer_date.getText()
						.toString()
				// + ", "
				// + e_prefer_time.getText().toString()
						);
				if (isValidString(e_second_prefer_date.getText().toString())) {
					ov_txtSecondPreferDate.setText(e_second_prefer_date
							.getText().toString()
					// + ", "
					// + e_second_prefer_time.getText().toString()

							);
				} else {
					ov_txtSecondPreferDate.setText("NA");
				}
				ov_txtAddress.setText(e_m_address.getText().toString()
						+ " , "
						+ getCityAddress(txtCity.getText().toString(), txtState
								.getText().toString(), txtCountry.getText()
								.toString(), e_m_postal_code.getText()
								.toString())

				);

				preferred_rating = (RatingBar) step6
						.findViewById(R.id.preferred_rating);
				// preferred_rating.setMax(5);
				// preferred_rating.setRating(Float.parseFloat(f_rating));

				wrapperServiceDetail = (LinearLayout) step6
						.findViewById(R.id.wrapperServiceDetail);
				wrapperServiceDetail.removeAllViews();
				for (int i = 0; i < arrSubServiceCategoryAfterDelete.size(); i++) {
					View vService = inflater.inflate(
							R.layout.layout_service_requested, null);
					TextView txtService = (TextView) vService
							.findViewById(R.id.txtService);
					TextView txtDescription = (TextView) vService
							.findViewById(R.id.txtServiceDescription);

					txtService
							.setText(arrSubServiceCategoryAfterDelete.get(i).service_name);
					String id = arrSubServiceCategoryAfterDelete.get(i).id;

					String description = "";
					Tracer.d("debug", "Service Id:" + id);
					boolean isMultiSelect = false;
					Tracer.d("debug", "ArrHolderSize:" + arrHolder.size());
					for (int j = 0; j < arrHolder.size(); j++) {
						Tracer.d("debug", "ArrHOlder Id:"
								+ arrHolder.get(j).serviceId);
						isMultiSelect = false;
						if (id.equals(arrHolder.get(j).serviceId)) {

							if ("string".equals(arrHolder.get(j).questionType)
									|| "number"
											.equals(arrHolder.get(j).questionType)) {
								EditText answer = (EditText) arrHolder.get(j).v;
								description = description
										+ "- "
										+ arrHolder.get(j).key
												.replace("_", " ") + ":"
										+ answer.getText().toString() + "\n";
							} else if ("option"
									.equals(arrHolder.get(j).questionType)) {
								TextView t = (TextView) arrHolder.get(j).v;
								String[] arrVal = (String[]) ((View) arrHolder
										.get(j).v.getParent().getParent())
										.getTag();
								int pos = (Integer) t.getTag();
								description = description
										+ "- "
										+ arrHolder.get(j).key
												.replace("_", " ") + ":"
										+ arrVal[pos] + "\n";

							} else if ("multi_select_option".equals(arrHolder
									.get(j).questionType)

							) {
								isMultiSelect = true;
								TextView t = (TextView) arrHolder.get(j).v;
								String[] arrVal = (String[]) ((View) arrHolder
										.get(j).v.getParent().getParent())
										.getTag();
								// int pos = (Integer) t.getTag();
								String value = (String) t.getTag();
								description = description
										+ "- "
										+ arrHolder.get(j).key
												.replace("_", " ") + ":"
										+ value + "\n";
							}

							LinearLayout kids = (LinearLayout) ((View) arrHolder
									.get(j).v.getParent().getParent())
									.findViewById(R.id.kid);
							try {
								if (kids != null) {
									String value = "";

									for (int l = 0; l < kids.getChildCount(); l++) {
										LinearLayout content = (LinearLayout) kids
												.getChildAt(l).findViewById(
														R.id.content);
										TextView kidTitle = (TextView) kids
												.getChildAt(l).findViewById(
														R.id.title);
										if (isMultiSelect)
											description = description
													+ "  "
													+ kidTitle.getText()
															.toString() + "\n";

										for (int k = 0; k < content
												.getChildCount(); k++) {
											QuestionHolder qh = (QuestionHolder) content
													.getChildAt(k).getTag(
															R.id.add);
											if ("string"
													.equals(qh.questionType)
													|| "number"
															.equals(qh.questionType)) {
												EditText et = (EditText) qh.v;
												value = et.getText().toString();
												description = description
														+ "  • "
														+ qh.key.replace("_",
																" ") + ":"
														+ value + "\n";

											} else if ("option"
													.equals(qh.questionType)

											) {
												TextView t = (TextView) qh.v
														.findViewById(R.id.value);
												String[] arrVal = (String[]) ((View) qh.v
														.getParent()
														.getParent()).getTag();
												int pos = (Integer) t.getTag();
												value = arrVal[pos];
												description = description
														+ "  • "
														+ qh.key.replace("_",
																" ") + ":"
														+ arrVal[pos] + "\n";

											} else if ("multi_select_option"
													.equals(qh.questionType)

											) {
												TextView t = (TextView) qh.v
														.findViewById(R.id.value);
												String[] arrVal = (String[]) ((View) qh.v
														.getParent()
														.getParent()).getTag();
												// int pos = (Integer)
												// t.getTag();
												value = (String) t.getTag();// arrVal[pos];
												description = description
														+ "  • "
														+ qh.key.replace("_",
																" ") + ":"
														+ value + "\n";

											}

										}
									}
									Tracer.d("Kids count:"
											+ kids.getChildCount());
								}
							} catch (Exception e) {
								e.printStackTrace();
							}

						}
					}
					if (description.length() > 0)
						description = description.substring(0,
								description.length() - 1);

					// description=getServiceDetailFromParamCache();
					txtDescription.setText(description);
					// Tracer.d("debug","description:"+description);
					// Tracer.d("debug",generateServiceData());
					wrapperServiceDetail.addView(vService);

				}

				int childCount = wrapperPhoto.getChildCount();
				int count = 0;
				LinearLayout wrapperPhotoSummary = (LinearLayout) inflater
						.inflate(R.layout.wrapper_photo, null);
				for (int i = 0; i < childCount; i++) {
					if (wrapperPhoto.getChildAt(i).getVisibility() == View.VISIBLE) {
						count++;
						View v = wrapperPhoto.getChildAt(i);
						ImageView iv = (ImageView) v.findViewById(R.id.img);

						View v2 = inflater.inflate(R.layout.item_upload_photo,
								null);
						ImageView img = (ImageView) v2.findViewById(R.id.img);

						Drawable d = iv.getDrawable();
						img.setImageDrawable(d);
						wrapperPhotoSummary.addView(v2);
					}
				}

				wrapperServiceDetail.addView(wrapperPhotoSummary);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	OnClickListener step6listener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.btnCancel2:
				changeStep(5, false);
				pageHistory.pop();
				break;
			case R.id.btnNext2:
				// simpleToast("Wait");
                AlertDialog.Builder builder = new AlertDialog.Builder(
                        getActivity());
                builder.setTitle("Do you wish to proceed to submit this job request?");
                builder.setMessage("While you have no obligation to select a merchant once bids are received, please note that we do monitor all jobs for unusual activity.");


                builder.setNegativeButton("Yes",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(
									DialogInterface dialog,
									int which) {
								dialog.dismiss();
                                doSubmit();
							}
						});
				builder.setPositiveButton("No",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(
									DialogInterface dialog,
                                    int which) {
								dialog.dismiss();
							}
						});
                builder.show();
                break;

			}
		}
	};

	void getLastAddress() {
		loadingInternetDialog.show();
		// AsyncHttpClient client = new AsyncHttpClient();
		RequestParams params = new RequestParams();
		params.add("session_username", pref.getPref(Config.PREF_USERNAME));
		params.add("active_session_token",
				pref.getPref(Config.PREF_ACTIVE_SESSION_TOKEN));

		PARestClient.get(pref.getPref(Config.SERVER),
				Config.API_GET_LAST_ADDRESS, params,
				new AsyncHttpResponseHandler() {
					@Override
					public void onFailure(int statusCode, Throwable error,
							String content) {
						// TODO Auto-generated method stub
						super.onFailure(statusCode, error, content);
						loadingInternetDialog.dismiss();
						// showLocalWebDialog(content);
					}

					@Override
					public void onSuccess(int statusCode, String content) {
						// TODO Auto-generated method stub
						super.onSuccess(statusCode, content);
						loadingInternetDialog.dismiss();
						Tracer.d(content);
						try {
							JSONObject obj = new JSONObject(content);
							if ("success".equals(obj.getString("status"))) {
								JSONObject result = obj.getJSONObject("result");

								f_m_country = result
										.getString("service_request_country");
								if (GlobalVar.country.toLowerCase().contains(f_m_country.toLowerCase())) {

									f_m_postal_code = result
											.getString("service_request_postal_code");

									f_merchant_address = result
											.getString("service_request_address");

									f_m_city = result
											.getString("service_request_city");
									f_m_state = result
											.getString("service_request_state");

//									txtCity.setText(f_m_city);
//									txtState.setText(f_m_state);
//									txtCountry.setText(f_m_country);
									e_m_address.setText(f_merchant_address);
									e_m_postal_code.setText(f_m_postal_code);

								}
							}

						} catch (Exception e) {
							e.printStackTrace();
						}

					}
				});
	}

	String generateServiceData() {
		return generateServiceData(false);
	}

	String generateServiceData(boolean onlyParam) {
		try {
			String str = "";
			JSONArray json = new JSONArray();
			Tracer.d("s 1");
			for (int i = 0; i < arrSubServiceCategoryAfterDelete.size(); i++) {
				Tracer.d("s 2 " + i);
				JSONObject obj = new JSONObject();
				String id = arrSubServiceCategoryAfterDelete.get(i).id;

				JSONArray param_cache = new JSONArray();
				boolean isMultiSelect = false;
				for (int j = 0; j < arrHolder.size(); j++) {
					isMultiSelect = false;
					if (id.equals(arrHolder.get(j).serviceId)) {
						Object value = "";
						if ("string".equals(arrHolder.get(j).questionType)
								|| "number"
										.equals(arrHolder.get(j).questionType)) {
							EditText et = (EditText) arrHolder.get(j).v;
							value = et.getText().toString();
						} else if ("option"
								.equals(arrHolder.get(j).questionType)) {
							TextView t = (TextView) arrHolder.get(j).v
									.findViewById(R.id.value);
							String[] arrVal = (String[]) ((View) arrHolder
									.get(j).v.getParent().getParent()).getTag();
							int pos = (Integer) t.getTag();

							value = arrVal[pos];
						} else if ("multi_select_option".equals(arrHolder
								.get(j).questionType)

						) {
							isMultiSelect = true;
							TextView t = (TextView) arrHolder.get(j).v
									.findViewById(R.id.value);
							String[] arrVal = (String[]) ((View) arrHolder
									.get(j).v.getParent().getParent()).getTag();
							// int pos = (Integer) t.getTag();
							String[] tmpArr = ((String) t.getTag()).split(",");
							try {
								// value = new JSONArray(tmpArr);
								value=new JSONArray();
								for(String s:tmpArr){
									((JSONArray) value).put(s);
								}

							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}

							// value = (String) t.getTag();// arrVal[pos];
							// param_cache.put(qh.key, value);

						}

						try {
							Tracer.d("Get kids for " + arrHolder.get(j).key);
							LinearLayout kids = (LinearLayout) ((View) arrHolder
									.get(j).v.getParent().getParent())
									.findViewById(R.id.kid);
							param_cache.put(new JSONObject().put(
									arrHolder.get(j).key, value));

							try {
								if (kids != null) {
									Object valueKid = "";

									for (int l = 0; l < kids.getChildCount(); l++) {
										JSONObject kid = new JSONObject();
										LinearLayout content = (LinearLayout) kids
												.getChildAt(l).findViewById(
														R.id.content);

										for (int k = 0; k < content
												.getChildCount(); k++) {

											QuestionHolder qh = (QuestionHolder) content
													.getChildAt(k).getTag(
															R.id.add);

											if ("string"
													.equals(qh.questionType)
													|| "number"
															.equals(qh.questionType)) {
												EditText et = (EditText) qh.v;
												valueKid = et.getText()
														.toString();

												// param_cache.put(qh.key,
												// value);

											} else if ("option"
													.equals(qh.questionType)

											) {
												TextView t = (TextView) qh.v
														.findViewById(R.id.value);
												String[] arrVal = (String[]) ((View) qh.v
														.getParent()
														.getParent()).getTag();
												int pos = (Integer) t.getTag();
												valueKid = arrVal[pos];
												// param_cache.put(qh.key,
												// value);

											}

											else if ("multi_select_option"
													.equals(qh.questionType)

											) {
												TextView t = (TextView) qh.v
														.findViewById(R.id.value);
												String[] arrVal = (String[]) ((View) qh.v
														.getParent()
														.getParent()).getTag();
												// int pos = (Integer)
												// t.getTag();
												// valueKid = (String)
												// t.getTag();//
												// arrVal[pos];
												// param_cache.put(qh.key,
												// value);

												String[] tmpArr = ((String) t
														.getTag()).split(",");
												try {
//													valueKid = new JSONArray(
//															tmpArr);

													valueKid=new JSONArray();
													for(String s:tmpArr){
														((JSONArray) valueKid).put(s);
													}


												} catch (Exception e) {
													// TODO Auto-generated catch
													// block
													e.printStackTrace();
												}

											}

											if (isMultiSelect) {
												kid.put(qh.key, valueKid);
											} else {
												param_cache
														.put(new JSONObject()
																.put(qh.key,
																		valueKid));

											}

										}
										if (isMultiSelect) {
											TextView title = (TextView) kids
													.getChildAt(l)
													.findViewById(R.id.title);

											param_cache.put(new JSONObject()
													.put(title.getText()
															.toString(), kid));
										}

									}
									Tracer.d("Kids count:"
											+ kids.getChildCount());
								}

								// param_cache.put(new
								// JSONObject().put(arrHolder.get(j).key,
								// value));

							} catch (Exception e) {
								e.printStackTrace();
							}

						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}

				try {
					obj.put("service_id",
							arrSubServiceCategoryAfterDelete.get(i).id);
					obj.put("quantity", "1");
					obj.put("service_detail_name",
							arrSubServiceCategoryAfterDelete.get(i).service_name);
					obj.put("service_detail_description", "-");
					obj.put("param_cache", param_cache);

				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				json.put(obj);
			}
			return json.toString();
		} catch (Exception e) {
			e.printStackTrace();
			return "{}";
		}
	}

	void getImageParams(RequestParams params) {
		int childCount = wrapperPhoto.getChildCount();
		int count = 0;
		for (int i = 0; i < childCount; i++) {
			if (wrapperPhoto.getChildAt(i).getVisibility() == View.VISIBLE) {
				count++;
				View v = wrapperPhoto.getChildAt(i);
				ImageView iv = (ImageView) v.findViewById(R.id.img);

				Drawable d = iv.getDrawable();
				Bitmap bitmap = ImageHelper.getResizedBitmap(
						((BitmapDrawable) d).getBitmap(), 600);
				ByteArrayOutputStream stream = new ByteArrayOutputStream();
				bitmap.compress(Bitmap.CompressFormat.PNG, 50, stream);
				byte[] bitmapdata = stream.toByteArray();

				params.put("attachments[" + i + "]", new ByteArrayInputStream(
						bitmapdata), "test_image.png");
				d = null;
				bitmap = null;
				stream = null;
				bitmapdata = null;
			}
		}
	}

	ProgressDialog uploadDialog;

	void doSubmit() {

		uploadDialog = new ProgressDialog(getActivity());
		uploadDialog.setCancelable(false);
		uploadDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		uploadDialog.setMessage("Please wait...");
		uploadDialog.setTitle("Sending data");
		uploadDialog.setIndeterminate(false);
		uploadDialog.setProgress(0);
		uploadDialog.setMax(100);

		Tracer.d(generateServiceData());

		uploadDialog.show();
		String uri = "";

		// byte[] myByteArray = BitmapFactory.de;

		// AsyncHttpClient client = new AsyncHttpClient();
		RequestParams params = new RequestParams();
		// getImageParams(params);
		int childCount = wrapperPhoto.getChildCount();
		int count = 0;

		// for(int j=0;j<30;j++)
		for (int i = 0; i < childCount; i++) {
			if (wrapperPhoto.getChildAt(i).getVisibility() == View.VISIBLE) {
				View v = wrapperPhoto.getChildAt(i);
				ImageView iv = (ImageView) v.findViewById(R.id.img);

				Drawable d = iv.getDrawable();
				Bitmap bitmap = ((BitmapDrawable) d).getBitmap();
				ByteArrayOutputStream stream = new ByteArrayOutputStream();
				bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
				byte[] bitmapdata = stream.toByteArray();

				params.put("attachments[" + count + "]",
						new ByteArrayInputStream(bitmapdata), "test_image.png");
				d = null;
				bitmap = null;
				stream = null;
				bitmapdata = null;
				count++;

			}
		}

		if (count > 0) {
			uploadDialog
					.setMessage("Uploading Picture(s), this will take some time. ");
		}

		params.add("other_remarks", f_other_remarks);
		params.add("title", f_service_title);
		params.add("service_description", "-");
		if (!TextUtils.isEmpty(f_max_budget)) {
			params.add("service_budget_lowest", "0");

			params.add("service_budget_highest", f_max_budget);
		}

		params.add("service_request_address", f_merchant_address);
		params.add("service_request_country", f_m_country);
		params.add("service_request_state", f_m_state);
		params.add("service_request_city", f_m_city);
		params.add("service_request_postal_code", f_m_postal_code);
		params.add("service_data", generateServiceData());
		params.add("username", pref.getPref(PREF_USERNAME));
		params.add("session_username", pref.getPref(PREF_USERNAME));
		params.add("active_session_token",
				pref.getPref(PREF_ACTIVE_SESSION_TOKEN));

		params.add("service_category_id",
				arrServiceCategory.get(selectedService).id);
		params.add("preferred_time1_start", TimeUtils
				.strToDate(f_prefer_date /*
										 * + " " + f_prefer_time + ":00"
										 */).getTime() / 1000 + "");

		params.add("service_request_bid_duration", f_bid_period);

		if (isValidString(e_second_prefer_date.getText().toString())) {
			params.add("preferred_time2_start",
					TimeUtils.strToDate(f_second_prefer_date /*
															 * + " " +
															 * f_second_prefer_time
															 * + ":00"
															 */).getTime()
							/ 1000 + "");
		} else {
			params.add("preferred_time2_start", "-1");

		}

		if (isOpenBid) {
			params.add("service_request_type", "bid");
			uri = Config.API_CREATE_AUCTION;
		} else {
			params.add("service_request_type", "quotation");
			params.add("merchant_username", merchant.co_name);
			uri = Config.API_CREATE_QUOTATION;
		}

		Tracer.d("debug", params.toString());
		PARestClient.post(pref.getPref(Config.SERVER), uri, params,
				new AsyncHttpResponseHandler() {

					@Override
					public void onSuccess(int statusCode, String content) {
						// TODO Auto-generated method stub
						super.onSuccess(statusCode, content);
						Tracer.d(content);
						uploadDialog.hide();
						if ("authentication fail".equals(content)) {
							// simpleToast("authentication fail.");

							// listener.doLogout();

							AlertDialog.Builder builder = new AlertDialog.Builder(
									getActivity());
							builder.setTitle("Error");
							builder.setMessage("Authentication Fail. Do you want to relogin?");
							builder.setPositiveButton("Yes",
									new DialogInterface.OnClickListener() {

										@Override
										public void onClick(
												DialogInterface dialog,
												int which) {
											// TODO Auto-generated method stub
											dialog.dismiss();
											listener.doLogout();
										}
									});

							builder.setNegativeButton("No",
									new DialogInterface.OnClickListener() {

										@Override
										public void onClick(
												DialogInterface dialog,
												int which) {
											// TODO Auto-generated method stub
											dialog.dismiss();
										}
									});
							builder.show();
						} else {
							JSONObject obj;
							try {
								obj = new JSONObject(content);
								if ("success".equals(obj.getString("status"))) {
									simpleToast("Success");

                                    String reason = obj.getString("reason");

                                    Map eventData = new HashMap<>();
                                    eventData.put("job_request_id", reason);
                                    Intercom.client().logEvent("job-request", eventData);

                                    Map eventData2 = new HashMap<>();
                                    eventData2.put("category_name", arrServiceCategory.get(selectedService).service_name);

                                    String subServices = "";
                                    ArrayList<String> skuArrayList = new ArrayList<>();
                                    for (int i = 0; selectedSubService != null && i < selectedSubService.length; i++) {
                                        if (selectedSubService[i] == 1) {
                                            if (subServices.length() > 0){
                                                subServices += ",";
                                            }
                                            subServices += arrSubServiceCategory.get(i).service_name;
                                            skuArrayList.add(arrSubServiceCategory.get(i).service_name);
                                        }
                                    }

                                    eventData2.put("sub_category_name", subServices);
                                    Intercom.client().logEvent("subcategory-service", eventData2);

                                    // AppsFlyerLib.sendTrackingWithEvent(getActivity().getApplicationContext(), "Job Request", subServices);
                                    String[] skuArray = skuArrayList.toArray(new String[skuArrayList.size()]);
                                    NanigansEventManager.getInstance().trackNanigansEvent(NanigansEventManager.TYPE.USER, "request",
                                            new NanigansEventParameter("sku", skuArray)
                                            );

									showSuccess(reason);

								} else {
									simpleToast(obj.getString("reason"));
								}
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}

					@Override
					public void onFailure(Throwable error, String content) {
						// TODO Auto-generated method stub
						super.onFailure(error, content);
						// Tracer.d(content);
						uploadDialog.hide();
						simpleToast("Failed. Please try again");
					}

					@Override
					public void onProgress(int bytesWritten, int totalSize) {
						// TODO Auto-generated method stub
						super.onProgress(bytesWritten, totalSize);
						int progress = (int) ((bytesWritten * 100.0f) / (totalSize * 1.0f));

						uploadDialog.setProgress(progress);
						uploadDialog.setMax(100);

						Tracer.d("progress", "pos: " + bytesWritten + " len: "
								+ totalSize);
					}

				});
	}

	Dialog dialogBidSuccess;
	int flagForDialogBid;

	void showSuccess(final String service_request_serial) {
		analytic.trackScreen("Job Request Sent");
		flagForDialogBid = -1;
		View v = inflater.inflate(R.layout.openbid_success, null);

		v.findViewById(R.id.btnBack).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				flagForDialogBid = 0;
//				listener.doFragmentChange(new FragmentNewLanding(), false,
//						"Home");
						listener.doFragmentChange(new FragmentRevisedLanding(), false,
								"Home");
				dialogBidSuccess.dismiss();

			}
		});

		final ActivityLanding activityLanding = (ActivityLanding) getActivity();

		v.findViewById(R.id.btnViewJobList).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						activityLanding.setBottomBar(2);
						flagForDialogBid = 1;
//						listener.doFragmentChange(new FragmentBid(), false,
//								"Bid");
						((ActivityLanding) getActivity()).replaceFragment(new FragmentBid(), false);
						dialogBidSuccess.dismiss();

					}
				});

		v.findViewById(R.id.btnViewJobDetail).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						activityLanding.setBottomBar(2);
						flagForDialogBid = 2;
//						listener.doFragmentChange(new FragmentBidDetail(
//								service_request_serial), false, "Bid");
						((ActivityLanding) getActivity()).replaceFragment(new FragmentBidDetail(service_request_serial), false);
						dialogBidSuccess.dismiss();

					}
				});

		dialogBidSuccess = new Dialog(getActivity(), R.style.PauseDialog);
		dialogBidSuccess.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		dialogBidSuccess.getWindow().setLayout(
				WindowManager.LayoutParams.MATCH_PARENT,
				WindowManager.LayoutParams.MATCH_PARENT);
		dialogBidSuccess.setContentView(v);
		dialogBidSuccess.getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

		dialogBidSuccess.show();

		dialogBidSuccess.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss(DialogInterface dialog) {
				// TODO Auto-generated method stub
				if (flagForDialogBid == -1) {
//					listener.doFragmentChange(new FragmentNewLanding(), false,
//							"Home");
							listener.doFragmentChange(new FragmentRevisedLanding(), false,
									"Home");

				}
			}
		});

	}

	Dialog dialogCustomDateTimeDialog;
	String tmpDate, tmpTime, tmpTagDate, tmpTagTime;

	void showCustomDateTimeDialog(final TextView tv) {
		View v = inflater.inflate(R.layout.custom_datepicker, null);

		v.findViewById(R.id.done).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialogCustomDateTimeDialog.dismiss();
			}
		});

		final DatePicker datePicker = (DatePicker) v
				.findViewById(R.id.datePicker1);
		final TimePicker timePicker = (TimePicker) v
				.findViewById(R.id.timePicker1);

		Calendar c = Calendar.getInstance();

		int dayOfMonth = c.get(Calendar.DAY_OF_MONTH);
		int monthOfYear = c.get(Calendar.MONTH);
		int year = c.get(Calendar.YEAR);

		datePicker.init(year, monthOfYear, dayOfMonth,
				new OnDateChangedListener() {

					@Override
					public void onDateChanged(DatePicker view, int year,
							int monthOfYear, int dayOfMonth) {
						// TODO Auto-generated method stub
						tmpDate = dateToDDMMMYY(year, monthOfYear, dayOfMonth);
						tmpTagDate = year + "-" + (monthOfYear + 1) + "-"
								+ dayOfMonth;

					}
				});
		datePicker.setMinDate(System.currentTimeMillis() - 1000);

		timePicker.setOnTimeChangedListener(new OnTimeChangedListener() {

			@Override
			public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
				// TODO Auto-generated method stub
				String strHourDay, strMinute;
				strHourDay = hourOfDay < 10 ? "0" + hourOfDay : "" + hourOfDay;
				strMinute = minute < 10 ? "0" + minute : "" + minute;

				tmpTime = (strHourDay + ":" + strMinute + ":00");

			}
		});

		v.findViewById(R.id.done).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				int year = datePicker.getYear();
				int monthOfYear = datePicker.getMonth();
				int dayOfMonth = datePicker.getDayOfMonth();
				tmpDate = dateToDDMMMYY(year, monthOfYear, dayOfMonth);
				tmpTagDate = year + "-" + (monthOfYear + 1) + "-" + dayOfMonth;

				timePicker.clearFocus();
				String strHourDay, strMinute;
				int hourOfDay = timePicker.getCurrentHour();
				int minute = timePicker.getCurrentMinute()
						* CustomTimePicker.TIME_PICKER_INTERVAL;
				strHourDay = timePicker.getCurrentHour() + "";
				strMinute = timePicker.getCurrentMinute() + "";
				strHourDay = hourOfDay < 10 ? "0" + hourOfDay : "" + hourOfDay;
				strMinute = minute < 10 ? "0" + minute : "" + minute;

				tmpTime = (strHourDay + ":" + strMinute);
				tmpTagTime = (strHourDay + ":" + strMinute + ":00");

				tv.setText(tmpDate + ", " + tmpTime);
				tv.setTag(tmpTagDate + " " + tmpTagTime);
				dialogCustomDateTimeDialog.dismiss();
			}
		});

		dialogCustomDateTimeDialog = new Dialog(getActivity(),
				R.style.PauseDialog);
		dialogCustomDateTimeDialog.getWindow().requestFeature(
				Window.FEATURE_NO_TITLE);
		dialogCustomDateTimeDialog.getWindow().setLayout(
				WindowManager.LayoutParams.MATCH_PARENT,
				WindowManager.LayoutParams.MATCH_PARENT);
		dialogCustomDateTimeDialog.setContentView(v);
		dialogCustomDateTimeDialog.getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

		dialogCustomDateTimeDialog.show();

	}

	void getMerchantDataByService() {
		isLoadingMoreContent = true;
		loadingInternetDialog.show();

		AsyncHttpClient client = new AsyncHttpClient();
		String uri = Config.API_SEARCH;
		// + "?service=" + keyword + "&page="
		// + offsetMerchantService + "&limit=" + limitMerchantService;
		// Tracer.d(uri);

		RequestParams params = new RequestParams();
		params.add("service", keyword);
		params.add("page", "" + offsetMerchantService);
		params.add("limit", "" + limitMerchantService);
		Log.v("debug", params.toString());
		PARestClient.get(pref.getPref(Config.SERVER), uri, params,
				new AsyncHttpResponseHandler() {

					@Override
					public void onSuccess(int statusCode, String content) {
						// TODO Auto-generated method stub
						super.onSuccess(statusCode, content);
						Tracer.d(content);
						try {
							ParserMerchant parser = new ParserMerchant(content);
							if ("success".equals(parser.status)) {
								arrMerchant.addAll(parser.getData());
								adapterMerchantService.notifyDataSetChanged();
								hasNext = parser.isNextPage;
								if (hasNext) {
									offsetMerchantService += 1;
								}
							}

						} catch (Exception e) {
							e.printStackTrace();
						}
						isLoadingMoreContent = false;
						loadingInternetDialog.dismiss();

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
											public void onClick(
													DialogInterface dialog,
													int id) {
												// if this button is clicked,
												// close
												// current activity
												getMerchantDataByService();
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

	String keyword;
	int offsetMerchantService, limitMerchantService;
	private boolean hasNext = true;
	private boolean isLoadingMoreContent;
	ArrayList<Merchant> arrMerchant = new ArrayList<Merchant>();
	ListView list;
	ListAdapterMerchantService adapterMerchantService;

	class ListAdapterMerchantService extends BaseAdapter {

		View mLoadingItem;
		ListHolder holder;

		public ListAdapterMerchantService() {
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
					getMerchantDataByService();
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
					merchant = arrMerchant.get(pos);
					pageHistory.add(prev_page);
					changeStep(8);
				}
			});

			return convertView;

		}

		private class ListHolder {
			ImageView status, verified;
			TextView name;
			RatingBar rating;
			TextView service;
		}

	}

	private View createDropDown(final String param_name, final String q,
			final ArrayList<QuestionUnit> param_options,
			final boolean isMultiSelect) {
		View root;
		root = inflater.inflate(R.layout.drop_down, null);
		TextView tag = (TextView) root.findViewById(R.id.txt_tag);
		tag.setText(param_name.replace("_", " "));
		// questionArea.addView(v);

		final String[] strings = new String[param_options.size()];
		final String[] stringValue = new String[param_options.size()];

		ArrayList<String> list = new ArrayList<String>();
		for (int i = 0; i < param_options.size(); i++) {
			list.add(param_options.get(i).getLabel());
			strings[i] = param_options.get(i).getLabel();
			stringValue[i] = param_options.get(i).getValue();
		}

		// ArrayAdapter<String> adp = new ArrayAdapter<String>(getActivity(),
		// R.layout.custom_simple_spinner_item, list);
		// adp.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		//
		// Spinner value = (Spinner) v.findViewById(R.id.value);
		// // value.setBackgroundResource(R.drawable.spinner_bg);
		// value.setPrompt(q);
		// value.setAdapter(adp);
		// value.setTag(strings);

		TextView tv = (TextView) root.findViewById(R.id.value);
		tv.setHint(/* "Select your " + */q);
		tv.setTag(R.id.aa, root);
		tv.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(final View v) {
				// TODO Auto-generated method stub
				// showSpinnerSelectionForDropdownForm(stringValue,strings,
				// (TextView)v, "Select "+q);
				Handler h = new Handler() {

					@Override
					public void handleMessage(Message msg) {

						try {
							// TODO Auto-generated method stub
							super.handleMessage(msg);
							boolean[] itemChecked;
							if (msg.obj != null) {
								itemChecked = (boolean[]) msg.obj;

							} else {
								itemChecked = new boolean[param_options.size()];
								itemChecked[msg.what] = true;
							}
							View parent = (View) v.getTag(R.id.aa);
							LinearLayout kid = (LinearLayout) parent
									.findViewById(R.id.kid);

							// ArrayList<FormParam> arrKid=param.child;
							// for (int x = 0; x < param_options.size(); x++)
							int hierarchy = (Integer) parent
									.getTag(R.id.questionhierarchy);

							if (1 != hierarchy) {
								kid.removeAllViews();
								resetQuestion(v);
								kid.setTag(null);
							}

							boolean hasKid = false;

							for (int y = 0; y < itemChecked.length; y++) {
								// if (null == param_options.get(x).child)
								// continue;
								// Tracer.d("Kid" + j + " " + x);

								View wrapper_multi_child = inflater.inflate(
										R.layout.view_mutli_child, null);

								LinearLayout child_content = (LinearLayout) wrapper_multi_child
										.findViewById(R.id.content);
								TextView child_title = (TextView) wrapper_multi_child
										.findViewById(R.id.title);
								try {

									if (!isMultiSelect) {
										child_title.setVisibility(View.GONE);
									}
									Tracer.d(param_options.get(y).value);
									child_title.setText(""
											+ param_options.get(y).value);
								} catch (Exception e) {
									e.printStackTrace();
								}

								for (int z = 0; param_options.get(y).children_multi != null
										&& z < param_options.get(y).children_multi
												.size(); z++) {
									FormParam param2 = param_options.get(y).children_multi
											.get(z);
									if (itemChecked[y] && param2 != null) {
										hasKid = true;
										if ("string".equals(param2.param_type)) {
											// View v =
											// inflater.inflate(R.layout.form_string,
											// null);
											// TextView question = (TextView) v
											// .findViewById(R.id.txt_tag);
											// question.setText(param2.paramName);

											// content.addView(v);

											QuestionHolder kidQh1 = new QuestionHolder();
											kidQh1.serviceId = "";// arrSubServiceCategory.get(i).id;
											kidQh1.questionType = param2.param_type;
											kidQh1.questionId = param2.param_name;
											kidQh1.tag = param2.param_description;
											kidQh1.key = param2.param_name;
											View view = createTextBox(
													kidQh1.key, kidQh1.tag);
											view.setTag(R.id.questionhierarchy,
													1);

											kidQh1.v = view
													.findViewById(R.id.txt_value);
											// arrHolder.add(kidQh1);
											view.setTag(R.id.add, kidQh1);

											child_content.addView(view);

											displayNextQuestion(v);

										} else if ("number"
												.equals(param2.param_type)) {
											// View v =
											// inflater.inflate(R.layout.form_number,
											// null);
											// TextView question = (TextView) v
											// .findViewById(R.id.txt_tag);
											// question.setText(param2.paramName);
											//
											// content.addView(v);

											QuestionHolder kidQh1 = new QuestionHolder();
											kidQh1.serviceId = "";// arrSubServiceCategory.get(i).id;
											kidQh1.questionType = param2.param_type;
											kidQh1.questionId = param2.param_name;
											kidQh1.tag = param2.param_description;
											kidQh1.key = param2.param_name;

											View view = createNumberBox(
													kidQh1.key, kidQh1.tag);
											view.setTag(R.id.questionhierarchy,
													1);

											kidQh1.v = view
													.findViewById(R.id.txt_value);
											// arrHolder.add(kidQh1);
											view.setTag(R.id.add, kidQh1);
											child_content.addView(view);

										} else if ("option"
												.equals(param2.param_type)) {
											try {
												QuestionHolder kidQh1 = new QuestionHolder();
												kidQh1.serviceId = "";
												// arrSubServiceCategory.get(i).id;
												kidQh1.questionType = param2.param_type;
												kidQh1.questionId = param2.param_name;
												kidQh1.tag = param2.param_description;
												kidQh1.key = param2.param_name;

												View view = createDropDown(
														param2.param_name,
														param2.param_description,
														param2.param_options,
														false);
												view.setTag(
														R.id.questionhierarchy,
														1);

												kidQh1.v = view
														.findViewById(R.id.value);
												// arrHolder.add(kidQh1);
												kid.setTag(kidQh1);
												view.setTag(R.id.add, kidQh1);
												child_content.addView(view);

											} catch (Exception e) {
												e.printStackTrace();
											}

										} else if ("multi_select_option"
												.equals(param2.param_type)) {
											try {
												QuestionHolder kidQh1 = new QuestionHolder();
												kidQh1.serviceId = "";
												// arrSubServiceCategory.get(i).id;
												kidQh1.questionType = param2.param_type;
												kidQh1.questionId = param2.param_name;
												kidQh1.tag = param2.param_description;
												kidQh1.key = param2.param_name;

												View view = createDropDown(
														param2.param_name,
														param2.param_description,
														param2.param_options,
														true);
												view.setTag(
														R.id.questionhierarchy,
														1);

												kidQh1.v = view
														.findViewById(R.id.value);
												// arrHolder.add(kidQh1);
												kid.setTag(kidQh1);
												view.setTag(R.id.add, kidQh1);
												child_content.addView(view);

											} catch (Exception e) {
												e.printStackTrace();
											}

										}

										// option
									}

								}
								if (child_content.getChildCount() > 0)
									kid.addView(wrapper_multi_child);

							}

							if (!hasKid) {
								// go to next child
								Tracer.d("No kid, go to next");
								displayNextQuestion(v);
							}

						} catch (Exception e) {
							e.printStackTrace();
						}
					}

				};

				if (isMultiSelect) {
					showSpinnerSelectionForMultiSelectForm(strings,
							stringValue, (TextView) v, "" + q, h);

				} else {
					showSpinnerSelection(strings, (TextView) v, "" + q, h);
				}

			}
		});
		root.setTag(stringValue);
		return root;// v.findViewById(R.id.value);

	}

	void displayNextQuestion(View v) {
		View parent = (View) v.getParent().getParent();
		int hierarchy = (Integer) parent.getTag(R.id.questionhierarchy);
		ViewGroup rootCategory = null;
		View rootParam = null;
		if (1 == hierarchy) {
			rootCategory = (ViewGroup) parent.getParent().getParent()
					.getParent().getParent().getParent();
			rootParam = (View) parent.getParent().getParent().getParent()
					.getParent();
		} else if (0 == hierarchy) {
			rootCategory = (ViewGroup) parent.getParent();
			rootParam = (View) parent;

		}

		for (int i = rootCategory.indexOfChild(rootParam) + 1; i < rootCategory
				.getChildCount() - 1; i++) {
			View current = rootCategory.getChildAt(i);
			Tracer.d("Root param" + i);

			if (current != null) {
				current.setVisibility(View.GONE);
				LinearLayout kid = (LinearLayout) current
						.findViewById(R.id.kid);
				if (null != kid)
					kid.removeAllViews();
			}

			// indexnya salah, harusnya ga pake nomor, tapi mesti cari lagi
			QuestionHolder holder = null;// = arrHolder.get(i);

			// for (QuestionHolder tmp : arrHolder) {
			//
			// if (tmp.v.equals(current)) {
			// holder = tmp;
			//
			// break;
			// }
			// }

			try {
				int position = (Integer) current.findViewById(R.id.value)
						.getTag(R.id.arrow);
				holder = arrHolder.get(position);

			} catch (Exception e) {

			}

			if (holder != null) {
				if ("string".equals(holder.questionType)
						|| "number".equals(holder.questionType)) {
					// cek kosong
					EditText et = (EditText) holder.v;
					hideKeyboard(et);
					et.setText("");

				} else if ("option".equals(holder.questionType)) {
					TextView t = (TextView) holder.v;
					t.setText("");
				}
			}

		}

		if (true
		// validateOneChild(rootCategory.indexOfChild(rootParam))
		) {
			View next = rootCategory.getChildAt(rootCategory
					.indexOfChild(rootParam) + 1);

			if (next != null)
				next.setVisibility(View.VISIBLE);

			rootCategory.getChildAt(rootCategory.getChildCount() - 1)
					.setVisibility(View.VISIBLE);
		}
	}

	void resetQuestion(final View v) {
		View parent = (View) v.getParent().getParent();
		int hierarchy = (Integer) parent.getTag(R.id.questionhierarchy);
		ViewGroup rootCategory = null;
		View rootParam = null;
		if (1 == hierarchy) {
			rootCategory = (ViewGroup) parent.getParent().getParent()
					.getParent();
			rootParam = (View) parent.getParent().getParent();
		} else if (0 == hierarchy) {
			rootCategory = (ViewGroup) parent.getParent();
			rootParam = (View) parent;

		}

		for (int i = rootCategory.indexOfChild(rootParam) + 1; i < rootCategory
				.getChildCount(); i++) {
			View current = rootCategory.getChildAt(i);
			if (current != null) {
				current.setVisibility(View.GONE);
				LinearLayout kid = (LinearLayout) current
						.findViewById(R.id.kid);
				if (null != kid)
					kid.removeAllViews();
			}
			// if (i < arrHolder.size()) {

			// juga salah, perlu ambil dari dynamic position
			QuestionHolder holder = null;// arrHolder.get(i);
			// for(QuestionHolder tmp:arrHolder){
			// if(tmp.v.equals(parent)){
			// holder=tmp;
			// }
			// }
			try {
				int position = (Integer) current.findViewById(R.id.value)
						.getTag(R.id.arrow);
				holder = arrHolder.get(position);

			} catch (Exception e) {

			}

			if (holder != null)
				if ("string".equals(holder.questionType)
						|| "number".equals(holder.questionType)) {
					// cek kosong
					EditText et = (EditText) holder.v;
					hideKeyboard(et);
					et.setText("");

				} else if ("option".equals(holder.questionType)) {
					TextView t = (TextView) holder.v;
					t.setText("");
				}
			// }
		}

	}

}
