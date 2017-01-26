package com.pa.common;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.CheckedTextView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.kbeanie.imagechooser.api.ChooserType;
import com.kbeanie.imagechooser.api.ChosenImage;
import com.kbeanie.imagechooser.api.ImageChooserListener;
import com.kbeanie.imagechooser.api.ImageChooserManager;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.pa.job.FragmentPostOpenBid;
import com.pa.parser.ParserParamCache;
import com.pa.pojo.TimeDiff;
import com.coolfindservices.androidconsumer.R;

public class MyFragment extends Fragment implements ImageChooserListener {
    private final String TAG = "MyFragment";

	protected ProgressDialog loadingDialog;
	protected ProgressDialog loadingInternetDialog;
    protected ProgressDialog forceLoadingInternetDialog;
	protected AppPreferences pref;

	protected LayoutInflater inflater;
	protected FragmentManager fm;

	protected String[] country_prefix = { "+65", "+60", "+62" };
    protected String malaysia_prefix = "Malaysia, ";
	protected String[] city = { "Singapore" };
	protected String[] state = { "Singapore" };
	protected String[] state_short = { "" };
	protected String[] package_category = { "" };

	protected String[] country = { "Singapore" };
	protected String[] country2 = { "Singapore", "Malaysia" };

	protected String[] prefer_time = { "00:00-03:00", "03:00-06:00",
			"06:00-09:00", "09:00-12:00", "12:00-15:00", "15:00-18:00",
			"18:00-21:00", "21:00-24:00" };
	protected String[] rating = { "1 star", "2 stars", "3 stars", "4 stars",
			"5 stars" };

	protected String[] BID_PERIOD = { "3 hours", "6 hours",
			"12 hours", "1 day", "2 days", "3 days" };
	protected int[] BID_PERIOD_LENGTH = { 3, 6, 12, 24, 48, 72 };
	
	protected Analytic analytic;

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		if (savedInstanceState != null) {
			try{
			if (savedInstanceState.containsKey("chooser_type")) {
				chooserType = savedInstanceState.getInt("chooser_type");
			}

			if (savedInstanceState.containsKey("media_path")) {
				filePath = savedInstanceState.getString("media_path");
			}
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		analytic=new Analytic(getActivity());
		

		super.onActivityCreated(savedInstanceState);

	}

	protected String getServiceDetailFromParamCache(String paramCache) {
		String strDetail = "";
		// String strDetail = paramCache;
		// strDetail = strDetail.replace("\"", "").replace("_", " ")
		// .replace("{", "").replace("}", "");
		// String[] arrStrDetail = strDetail.split(",");
		// strDetail = "";
		// for (String str : arrStrDetail) {
		// strDetail += "- " + str + "\n";
		// }

		// strDetail="";
		// HashMap<String, Object> hash=new ParserParamCache(new
		// String(paramCache)).data;
		// for (Entry<String, Object> entry : hash.entrySet()) {
		// String key = entry.getKey();
		// Object value = entry.getValue();
		// if(! (value instanceof HashMap<?, ?>)){
		// strDetail+="- "+key+"\n  "+ value.toString()+"\n";
		// }else{
		// //strDetail+="  "+key+" : "+
		// getServiceDetailFromParamCache(value.toString())+"\n";
		// strDetail+="- "+key+"\n  "+ value.toString()+"\n";
		//
		// }
		//
		// }

		strDetail = "";
		ArrayList<HashMap<String, JSONObject>> arrHash = new ParserParamCache(
				new String(paramCache)).arrData;

		for (HashMap<String, JSONObject> hash : arrHash) {
			for (Entry<String, JSONObject> entry : hash.entrySet()) {
				String key = entry.getKey();
				Object value = entry.getValue();
				if (!(value instanceof HashMap<?, ?>)) {
					//strDetail += "- " + key + " : " + value.toString() + "\n";
					if(value instanceof ArrayList<?>){

					    strDetail+="- "+key+" : "+ TextUtils.join("", (ArrayList<?>)value)+"\n";

			    	}else
				    strDetail+="- "+key+" : "+ value.toString()+"\n";

				} else {
					// strDetail+="• "+key+" : "+
					// getServiceDetailFromParamCache(value.toString())+"\n";
					strDetail += "  " + key + "\n";
					HashMap<String, Object> secondHash = (HashMap<String, Object>) value;
					Tracer.d(secondHash.toString());
					for (Entry<String, Object> entry2 : secondHash.entrySet()) {
						String key2 = entry2.getKey();
						Object objValue2 = entry2.getValue();
						String value2="";
						if(objValue2 instanceof String){
							value2=objValue2.toString();
						}else
						if(objValue2 instanceof ArrayList<?>)
						{
							value2=TextUtils.join(",", (ArrayList<?>) objValue2);
						}else{
						value2=objValue2.toString();
						}
						value2=value2.replace("[","").replace("]", "");
						strDetail += "    • " + key2 + " : " + value2 + "\n";

					}

				}

			}
		}

		return strDetail;
	}

	Dialog dialogWeb;

	
	public void showLocalWebDialog(String data) {
		WebView w = new WebView(getActivity());
		w.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT));
		dialogWeb = new Dialog(getActivity(), R.style.PauseDialog);
		dialogWeb.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		dialogWeb.getWindow().setLayout(
				WindowManager.LayoutParams.MATCH_PARENT,
				WindowManager.LayoutParams.MATCH_PARENT);
		dialogWeb.setContentView(w);
		dialogWeb.getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

		dialogWeb.show();

		w.loadData(data, "text/html", "UTF-8");
	}

	public void showWebDialog(String url){
		showWebDialog(url, null);
	}
	public void showWebDialog(String url,String title) {
		View v = inflater.inflate(R.layout.dialog_web, null);
		TextView pageTitle=(TextView)v.findViewById(R.id.title);
		if(null!=title){
			pageTitle.setVisibility(View.VISIBLE);
			pageTitle.setText(title);
		}
		
		final ViewSwitcher vs = (ViewSwitcher) v.findViewById(R.id.vs);
		v.findViewById(R.id.btnCancel2).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						dialogWeb.dismiss();
					}
				});

		WebView w = (WebView) v.findViewById(R.id.web);
		w.setWebViewClient(new WebViewClient() {
			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {

				super.onPageStarted(view, url, favicon);
				vs.setDisplayedChild(1);
			}

			@Override
			public void onPageFinished(WebView view, String url) {
				vs.setDisplayedChild(0);
			}

		});
		 w.getSettings().setJavaScriptEnabled(true);

		// Enable WebView to play iframe video
		w.setWebChromeClient(new WebChromeClient());

		dialogWeb = new Dialog(getActivity(), R.style.PauseDialog);
		dialogWeb.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		dialogWeb.getWindow().setLayout(
				WindowManager.LayoutParams.MATCH_PARENT,
				WindowManager.LayoutParams.MATCH_PARENT);
		dialogWeb.setContentView(v);
		dialogWeb.getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

		dialogWeb.show();

		w.loadUrl(url);
	}

	public String getPreferredTime(String time) {
		if (time.contains("-")) {
			return Config.NA;
		}

		String date = Config.NA;
		try {
			date = dateToStr(new Date(1000 * Long.parseLong(time)))
					+ ", "
					+ getTime(new Date(1000 * Long.parseLong(time)))
					//+ " - "
					//+ getTime(new Date(
					//		3 * 3600000 + 1000 * Long.parseLong(time)))
							;
		} catch (Exception e) {
			date = Config.NA;
		}

		return date;
	}

	public String getPreferredTime(String time1, String time2) {
		String time = "";
		if (time1.contains("-"))
			time = time2;
		else
			time = time1;

		if (time.contains("-")) {
			return Config.NA;
		}

		String date = Config.NA;
		try {
			date = dateToStr(new Date(1000 * Long.parseLong(time)))
					+ ", "
					+

					getTime(new Date(1000 * Long.parseLong(time)))
					//+ " - "
					//+ getTime(new Date(
					//		3 * 3600000 + 1000 * Long.parseLong(time)))
					;

		} catch (Exception e) {
			date = Config.NA;
		}

		return date;
	}

	public String getJobTitle(String s) {
		if (s.indexOf("from") > 0) {
			return s.substring(0, s.indexOf("from"));
		} else {
			return s;
		}
	}

	public String getMoneyValueWithoutZero(String value) {
		if (Float.parseFloat(value) == 0) {
			return Config.NA;
		}

		return value;
	}

	public String getCityAddress(String city, String state, String country,
			String postalCode) {
		if (city.equals(state) || city.equals(country)) {
			return city + ", " + postalCode;

		} else
			return city + ", " + state + ", " + country + ", " + postalCode;
	}

	protected String getTime(Date date) {
		SimpleDateFormat fmtOut = new SimpleDateFormat("HH:mm");
		return fmtOut.format(date);
	}

	protected void displayExitFormDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle("Warning");
		builder.setMessage("Are you sure want to leave this form? Any data has been input will be lost");
		builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				dialog.dismiss();
				// finish();
				FragmentManager myFM = getActivity()
						.getSupportFragmentManager();
				myFM.popBackStackImmediate();
			}
		});

		builder.setNegativeButton("No", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				dialog.dismiss();
			}
		});
		builder.show();
	}

	protected void getCountry(final Handler h) {
		final ArrayList<String> arrCountry = new ArrayList<String>();
		loadingInternetDialog.show();
		// AsyncHttpClient client = new AsyncHttpClient();
		RequestParams params = new RequestParams();
		params.add("country_only", "true");
		PARestClient.post(pref.getPref(Config.SERVER), Config.API_LOCATION, params,
				new AsyncHttpResponseHandler() {
					@Override
					public void onFinish() {
						// TODO Auto-generated method stub
						super.onFinish();
						loadingInternetDialog.dismiss();
					}

					@Override
					public void onSuccess(String content) {
						// TODO Auto-generated method stub
						super.onSuccess(content);
						loadingInternetDialog.dismiss();
						try {
							JSONObject json = new JSONObject(content);
							String status = json.getString("status");
							if ("success".equals(status)) {

								String result = json.getString("result");
								JSONArray arr = new JSONArray(result);

								for (int i = 0; i < arr.length(); i++) {
									JSONObject obj = arr.getJSONObject(i);
									String country = obj.getString("country");
									arrCountry.add(country);
								}
								country = new String[arrCountry.size()];
								for (int i = 0; i < country.length; i++) {
									country[i] = arrCountry.get(i);
								}
								if (h != null)
									h.sendEmptyMessage(0);
							} else {
								simpleToast("Failed to get country, please try again");
							}

						} catch (Exception e) {

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

	protected void getState(String country, final Handler h) {
		Tracer.d(country);
		final ArrayList<String> arrState = new ArrayList<String>();
		final ArrayList<String> arrStateShort = new ArrayList<String>();

		// AsyncHttpClient client = new AsyncHttpClient();
		RequestParams params = new RequestParams();
		params.add("country", country);
		loadingInternetDialog.show();

		PARestClient.post(pref.getPref(Config.SERVER),Config.API_LOCATION, params,
				new AsyncHttpResponseHandler() {
					@Override
					public void onSuccess(String content) {
						// TODO Auto-generated method stub
						super.onSuccess(content);
						try {
							JSONObject json = new JSONObject(content);
							String status = json.getString("status");
							if ("success".equals(status)) {

								String result = json.getString("result");
								JSONArray arr = new JSONArray(result);

								for (int i = 0; i < arr.length(); i++) {
									JSONObject obj = arr.getJSONObject(i);
									String state = obj.getString("state_long");
									arrState.add(state);

									String stateShort = obj
											.getString("state_short");
									arrStateShort.add(stateShort);

								}

								state = new String[arrState.size()];
								state_short = new String[arrState.size()];

								for (int i = 0; i < state.length; i++) {
									state[i] = arrState.get(i);
									state_short[i] = arrStateShort.get(i);
								}
								if (h != null)
									h.sendEmptyMessage(1);
							} else {
								simpleToast("Failed to get state, please try again");
							}

						} catch (Exception e) {

						}
						loadingInternetDialog.dismiss();
					}

					@Override
					public void onFailure(Throwable error, String content) {
						// TODO Auto-generated method stub
						super.onFailure(error, content);
					}

				});

	}

	public void getCity(String strState, final Handler h) {
//		Tracer.d(strState);
		final ArrayList<String> arrState = new ArrayList<String>();

		// AsyncHttpClient client = new AsyncHttpClient();
		RequestParams params = new RequestParams();
		params.add("state_short", strState);
		loadingInternetDialog.show();
		PARestClient.post(pref.getPref(Config.SERVER),Config.API_LOCATION, params,
				new AsyncHttpResponseHandler() {
					@Override
					public void onSuccess(String content) {
						// TODO Auto-generated method stub
						super.onSuccess(content);
						try {
							JSONObject json = new JSONObject(content);
							String status = json.getString("status");
							if ("success".equals(status)) {

								String result = json.getString("result");
								JSONArray arr = new JSONArray(result);

								for (int i = 0; i < arr.length(); i++) {
									JSONObject obj = arr.getJSONObject(i);
									String city = obj.getString("city");
									arrState.add(city);
								}

								city = new String[arrState.size()];
								for (int i = 0; i < city.length; i++) {
									city[i] = arrState.get(i);
								}
								if (h != null)
									h.sendEmptyMessage(2);
							} else {
								simpleToast("Failed to get city, please try again");
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

					}

				});

	}

	public void getCityWithLongState(String strState, final Handler h) {
		Tracer.d(strState);
		final ArrayList<String> arrState = new ArrayList<String>();

		// AsyncHttpClient client = new AsyncHttpClient();
		RequestParams params = new RequestParams();
		params.add("state_long", strState);
		loadingInternetDialog.show();
		PARestClient.post(pref.getPref(Config.SERVER),Config.API_LOCATION, params,
				new AsyncHttpResponseHandler() {
					@Override
					public void onSuccess(String content) {
						// TODO Auto-generated method stub
						super.onSuccess(content);
						Tracer.d(content);
						try {
							JSONObject json = new JSONObject(content);
							String status = json.getString("status");
							if ("success".equals(status)) {

								String result = json.getString("result");
								JSONArray arr = new JSONArray(result);

								for (int i = 0; i < arr.length(); i++) {
									JSONObject obj = arr.getJSONObject(i);
									String city = obj.getString("city");
									arrState.add(city);
								}

								city = new String[arrState.size()];
								for (int i = 0; i < city.length; i++) {
									city[i] = arrState.get(i);
								}
								if (h != null)
									h.sendEmptyMessage(2);
							} else {
								simpleToast("Failed to get city, please try again");
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

					}

				});

	}

	public float spToPixels(Context context, Float sp) {
		float scaledDensity = context.getResources().getDisplayMetrics().scaledDensity;
		return (sp * scaledDensity);
	}

	public TimeDiff getTimeDiff(Date start, Date end) {
		long diff = end.getTime() - start.getTime();
		int Days = (int) (diff / (1000 * 60 * 60 * 24));
		long timeLeft = diff - Days * (1000 * 60 * 60 * 24);

		int Hours = (int) (timeLeft / (1000 * 60 * 60));
		timeLeft = timeLeft - Hours * (1000 * 60 * 60);
		int Mins = (int) (timeLeft / (1000 * 60));
		timeLeft = timeLeft - Mins * (1000 * 60);
		int seconds = (int) (timeLeft / 1000);

		return new TimeDiff(diff, Days, Hours, Mins, seconds);

	}

	public boolean isStatusSuccess(String status) {

		return "success".equals(status);
	}

	protected String strTimeDiff(Date start, Date end) {
		long diff = end.getTime() - start.getTime();

		int Days = (int) (diff / (1000 * 60 * 60 * 24));
		long timeLeft = diff - Days * (1000 * 60 * 60 * 24);

		int Hours = (int) (timeLeft / (1000 * 60 * 60));
		timeLeft = timeLeft - Hours * (1000 * 60 * 60);
		int Mins = (int) (timeLeft / (1000 * 60));

		// return (String)
		// DateUtils.getRelativeTimeSpanString(end.getTime()-start.getTime());
		// return Days + " ds " + Hours + "hrs " + Mins + "mins";

		String str = "";
		if (Mins > 0) str = Mins + " minutes ";
		if (Hours > 0) str = Hours + " hours " + str;
		if (Days > 0) str = Days + " days " + str;

		return str;

	}

	protected String dateToStr(Date date) {
		SimpleDateFormat fmtOut = new SimpleDateFormat("dd MMM yyyy");
		return fmtOut.format(date);
	}

	protected Date strToDate(String dateString) {
		// String dateString = "03/26/2012 11:49:00 AM";
		SimpleDateFormat dateFormat = new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss");
		Date convertedDate = new Date();
		try {
			convertedDate = dateFormat.parse(dateString);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			convertedDate = null;
		}

		return convertedDate;
	}

	public static Bitmap getBitmapFromURL(String src) {
		try {
			URL url = new URL(src);
			HttpURLConnection connection = (HttpURLConnection) url
					.openConnection();
			connection.setDoInput(true);
			connection.connect();
			InputStream input = connection.getInputStream();
			Bitmap myBitmap = BitmapFactory.decodeStream(input);
			return myBitmap;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		inflater = getActivity().getLayoutInflater();
		pref = new AppPreferences(getActivity());
		loadingDialog = new ProgressDialog(getActivity());
		loadingDialog.setIndeterminate(true);
		loadingDialog.setIndeterminateDrawable(getResources().getDrawable(R.drawable.intercom_progress_wheel));
		loadingDialog.setMessage("Please wait...");
		loadingDialog.setTitle("Loading");

		loadingInternetDialog = new ProgressDialog(getActivity());
		loadingInternetDialog.setIndeterminate(true);
		loadingInternetDialog.setIndeterminateDrawable(getResources().getDrawable(R.drawable.intercom_progress_wheel));
		loadingInternetDialog.setCancelable(true);
		loadingInternetDialog
				.setMessage("This feature using internet connection");
		loadingInternetDialog.setTitle("Loading");

		forceLoadingInternetDialog = new ProgressDialog(getActivity());
		forceLoadingInternetDialog.setIndeterminate(true);
		forceLoadingInternetDialog.setIndeterminateDrawable(getResources().getDrawable(R.drawable.intercom_progress_wheel));
		forceLoadingInternetDialog.setCancelable(false);
		forceLoadingInternetDialog
				.setMessage("This feature using internet connection");
		forceLoadingInternetDialog.setTitle("Loading");

		fm = getChildFragmentManager();
		getActivity().getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
	}

	
	
	protected void hideKeyboard(EditText editText) {
		InputMethodManager imm = (InputMethodManager) getActivity()
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
	}

	protected void hideKeyboard() {
		View view = getActivity().getCurrentFocus();
		if (view != null) {
			InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
		}
	}

	protected void showKeyboard(EditText editText) {
		editText.requestFocus();
		InputMethodManager imm = (InputMethodManager) getActivity()
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
	}

	protected boolean hasFB() {
		try {
			ApplicationInfo info = getActivity().getPackageManager()
					.getApplicationInfo("com.facebook.katana", 0);
			return true;
		} catch (PackageManager.NameNotFoundException e) {
			return false;
		}
		// return true;
	}

	protected boolean isMailValid(String s) {
		boolean valid = false;
		String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
				+ "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
		// String
		// EMAIL_PATTERN="[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]*(\\.[A-Za-z]{2,})$";

		if (s.matches(EMAIL_PATTERN) && s.length() > 0) {
			valid = true;
		} else {
			valid = false;
		}

		return valid;

	}

	protected String ReadFromfile(String fileName, Context context) {
		Tracer.d(fileName);
		StringBuilder ReturnString = new StringBuilder();
		InputStream fIn = null;
		InputStreamReader isr = null;
		BufferedReader input = null;
		try {
			fIn = context.getResources().getAssets()
					.open(fileName, context.MODE_WORLD_READABLE);
			isr = new InputStreamReader(fIn);
			input = new BufferedReader(isr);
			String line = "";
			while ((line = input.readLine()) != null) {
				ReturnString.append(line);
			}
		} catch (Exception e) {
			e.getMessage();
		} finally {
			try {
				if (isr != null)
					isr.close();
				if (fIn != null)
					fIn.close();
				if (input != null)
					input.close();
			} catch (Exception e2) {
				e2.getMessage();
			}
		}
		return ReturnString.toString();
	}

	public String getDeviceName() {
		String manufacturer = Build.MANUFACTURER;
		String model = Build.MODEL;
		if (model.startsWith(manufacturer)) {
			return capitalize(model);
		} else {
			return capitalize(manufacturer) + " " + model;
		}
	}

	private String capitalize(String s) {
		if (s == null || s.length() == 0) {
			return "";
		}
		char first = s.charAt(0);
		if (Character.isUpperCase(first)) {
			return s;
		} else {
			return Character.toUpperCase(first) + s.substring(1);
		}
	}

	protected void openUrl(String url) {
		// String url = "http://www.example.com";
		Intent i = new Intent(Intent.ACTION_VIEW);
		i.setData(Uri.parse(url));
		startActivity(i);
	}

	void back() {

	}

	protected void dialNumber(String url) {
		Intent intent = new Intent(Intent.ACTION_DIAL);
		intent.setData(Uri.parse(url));
		startActivity(intent);
	}

	protected void comingSoon() {
		Toast.makeText(
				getActivity(),
				"Thanx for your attention.This feature will be ready for you soon",
				Toast.LENGTH_SHORT).show();
	}

	protected boolean formCheckString(String s, String fieldName) {
		boolean flag = false;

		flag = isValidString(s);
		if (!flag) {
			simpleToast("Please fill the " + fieldName);
		}

		return flag;
	}

	protected boolean formCheckStringWithInlineError(EditText textField, String fieldName) {
		boolean flag = isValidString(textField.getText().toString());
		if (!flag) {
			textField.setError("Please fill the " + fieldName);
		}

		return flag;
	}

	protected boolean isValidString(String s) {
		try {
//			if (s != null && s.length() > 0) {
//				return true;
//			} else
//				return false;
			return !TextUtils.isEmpty(s);
		} catch (Exception e) {
			return false;
		}
	}

	protected boolean isLogedIn() {
		boolean flag = false;
		if (isValidString(pref.getPref(Config.PREF_ACTIVE_SESSION_TOKEN))) {
			flag = true;
		}
		return flag;
	}

	protected void simpleToast(String s) {
		Toast.makeText(getActivity(), s, Toast.LENGTH_SHORT).show();
	}

	public void replaceFragment(int id, Fragment f) {
		if (fm != null) {
			FragmentTransaction ft = fm.beginTransaction();
			ft.replace(id, f);
			ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
			ft.addToBackStack(null);
			ft.commit();

		}
	}

	public void replaceFragment(int id, Fragment f, boolean history) {
		if (fm != null) {
			FragmentTransaction ft = fm.beginTransaction();
			ft.replace(id, f);
			ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
			// ft.setCustomAnimations(android.R.animator.fade_in,
			// android.R.animator.fade_out);
			if (history)
				ft.addToBackStack(null);
			ft.commit();

		}
	}

	public void replaceFragment(Fragment f) {
		if (fm != null) {
			FragmentTransaction ft = fm.beginTransaction();
			// ft.replace(R.id.FragmentContainer, f);
			ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
			ft.addToBackStack(null);
			ft.commit();

		}
	}

	public void replaceFragment(Fragment f, boolean history) {
		if (fm != null) {
			FragmentTransaction ft = fm.beginTransaction();
			// ft.replace(R.id.FragmentContainer, f);
			ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
			if (history)
				ft.addToBackStack(null);
			ft.commit();

		}
	}

	protected void addFragment(int id, Fragment f) {
		if (fm != null) {
			FragmentTransaction ft = fm.beginTransaction();
			ft.add(id, f);
			ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
			// ft.addToBackStack(null);
			ft.commit();

		}
	}

	protected boolean isMyServiceRunning() {
		ActivityManager manager = (ActivityManager) getActivity()
				.getSystemService(getActivity().ACTIVITY_SERVICE);
		for (RunningServiceInfo service : manager
				.getRunningServices(Integer.MAX_VALUE)) {
			if ((getActivity().getPackageName() + ".WatcherService")
					.equals(service.service.getClassName())) {
				return true;
			}
		}
		return false;
	}

	protected Calendar getFirstDateOfThisMonth() {
		// get today and clear time of day
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.HOUR_OF_DAY, 0); // ! clear would not reset the hour of
											// day !
		cal.clear(Calendar.MINUTE);
		cal.clear(Calendar.SECOND);
		cal.clear(Calendar.MILLISECOND);

		// get start of the month
		cal.set(Calendar.DAY_OF_MONTH, 1);
		// System.out.println("Start of the month:       " + cal.getTime());
		// System.out.println("... in milliseconds:      " +
		// cal.getTimeInMillis());

		// get start of the next month
		// cal.add(Calendar.MONTH, 1);
		// System.out.println("Start of the next month:  " + cal.getTime());
		// System.out.println("... in milliseconds:      " +
		// cal.getTimeInMillis());

		return cal;
	}

	protected String durationFormat(long l) {
		String s = "";
		long hh = 0, mm = 0, ss = 0;

		hh = (long) Math.floor((l / 3600) * 1.0f);
		mm = (long) Math.floor(((l - hh * 3600) / 60) * 1.0f);
		ss = l - (hh * 3600) - (mm * 60);
		// Tracer.d(hh+":"+mm+":"+ss);
		// if(hh<10)s="0"+hh;
		// else
		s = "" + hh;

		if (mm < 10)
			s = s + ":0" + mm;
		else
			s = s + ":" + mm;

		if (ss < 10)
			s = s + ":0" + ss;
		else
			s = s + ":" + ss;

		// s = hh + ":" + mm + ":" + ss;

		return s;
	}

	// protected void showFaq(String url) {
	// View v = inflater.inflate(R.layout.setting_faq, null);
	//
	// // TextView tv=(TextView)v.findViewById(R.id.content);
	// // tv.setText(Html.fromHtml(getString("html/faq.txt")));
	//
	// WebView wv = (WebView) v.findViewById(R.id.content);
	// wv.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
	// wv.setOnTouchListener(new View.OnTouchListener() {
	//
	// public boolean onTouch(View v, MotionEvent event) {
	// return (event.getAction() == MotionEvent.ACTION_MOVE);
	// }
	// });
	// wv.loadUrl("file:///android_asset/html/" + url);
	//
	// Dialog d = new Dialog(getActivity(), R.style.PauseDialog);
	// d.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
	// d.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
	// WindowManager.LayoutParams.MATCH_PARENT);
	// d.setContentView(v);
	// d.getWindow().setSoftInputMode(
	// WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
	//
	// d.show();
	//
	// }

	protected static boolean copyAssetFolder(AssetManager assetManager,
			String fromAssetPath, String toPath) {
		try {
			String[] files = assetManager.list(fromAssetPath);
			new File(Environment.getExternalStorageDirectory(), toPath)
					.mkdirs();
			boolean res = true;
			for (String file : files)
				if (file.contains("."))
					res &= copyAsset(assetManager, fromAssetPath + "/" + file,
							toPath + "/" + file);
				else
					res &= copyAssetFolder(assetManager, fromAssetPath + "/"
							+ file, toPath + "/" + file);
			return res;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	private static boolean copyAsset(AssetManager assetManager,
			String fromAssetPath, String toPath) {
		InputStream in = null;
		OutputStream out = null;
		try {
			in = assetManager.open(fromAssetPath);
			File file = new File(Environment.getExternalStorageDirectory(),
					toPath);
			file.createNewFile();
			out = new FileOutputStream(file);
			copyFile(in, out);
			in.close();
			in = null;
			out.flush();
			out.close();
			out = null;
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	private static void copyFile(InputStream in, OutputStream out)
			throws IOException {
		byte[] buffer = new byte[1024];
		int read;
		while ((read = in.read(buffer)) != -1) {
			out.write(buffer, 0, read);
		}
	}

	protected void clearMyFolder() {
		// DeleteRecursive(new File(Environment.getExternalStorageDirectory(),
		// Config.PROFILE_PATH));

	}

	protected void clearMyExam() {
		// DeleteRecursive(new File(Environment.getExternalStorageDirectory(),
		// Config.EXAM_PATH));

	}

	void DeleteRecursive(File fileOrDirectory) {
		if (fileOrDirectory.isDirectory())
			for (File child : fileOrDirectory.listFiles())
				DeleteRecursive(child);

		fileOrDirectory.delete();
	}

	protected AlertDialog generalDialog;

	protected void showSpinnerSelection(final String[] strSelection,
			final TextView tv, String title) {
		showSpinnerSelection(strSelection, tv, title, null);
	};

	protected void showSpinnerSelection(final String[] strSelection,
			final TextView tv, String title, final Handler h) {
		// TODO Auto-generated method stub
		try {
			class MyAdapter extends BaseAdapter {
				String[] str;

				public MyAdapter(String[] str) {
					this.str = str;
				}

				@Override
				public int getCount() {
					// TODO Auto-generated method stub
					return str.length;
				}

				@Override
				public Object getItem(int position) {
					// TODO Auto-generated method stub
					return str[position];
				}

				@Override
				public long getItemId(int position) {
					// TODO Auto-generated method stub
					return 0;
				}

				@Override
				public View getView(int position, View convertView,
						ViewGroup parent) {
					// TODO Auto-generated method stub
					View v = inflater.inflate(
							R.layout.simple_spinner_item_white_text, null);
					TextView tv = (TextView) v.findViewById(R.id.text1);
					tv.setText(str[position]);

					return v;
				}

			}

			// ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(
			// getActivity(), R.layout.simple_spinner_item_white_text, sort);
			// dataAdapter
			// .setDropDownViewResource(R.layout.simple_spinner_dropdown_item);

			ListView list = (ListView) inflater
					.inflate(R.layout.listview, null);

			MyAdapter dataAdapter = new MyAdapter(strSelection);
			list.setAdapter(dataAdapter);

			list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					// TODO Auto-generated method stub


					// getCity(strSelection[arg2]);

					try {
						generalDialog.hide();
						tv.setText(strSelection[arg2]);
						tv.setTag(arg2);


						if (Arrays.equals(strSelection, state)) {
							tv.setTag(R.id.city, state_short[arg2]);
						}

						final TextView tv1 = (TextView) tv
								.getTag(R.id.co_state);
						final TextView tv2 = (TextView) tv.getTag(R.id.co_city);

						if (tv1 != null && tv2 != null
								&& Arrays.equals(strSelection, country)) {
							// tv2 = (TextView) tv.getTag(R.id.co_city);
							Tracer.d("debug", "country" + strSelection[arg2]);
							if ("Singapore".equals(strSelection[arg2])) {
								tv1.setText("Singapore");
                                tv1.setVisibility(View.GONE); // Hide State whenever it's Singapore
								tv2.setText("Singapore");
                                tv2.setVisibility(View.GONE); // Hide City whenever it's Singapore

							} else {
								tv1.setText("");
								tv1.setHint("Select your state");

								tv2.setText("");
								tv2.setHint("Select your city");
							}
						}

						if (tv2 != null && Arrays.equals(strSelection, state)) {
							// tv2 = (TextView) tv.getTag(R.id.co_city);
							if ("Singapore".equals(strSelection[arg2])) {
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

					if (h != null)
						h.sendEmptyMessage(arg2);
				}
			});

			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

			builder.setView(list);
			builder.setTitle(title);

			generalDialog = builder.create();
			generalDialog.show();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

    protected void showSpinnerSelection(final String[] strSelection,
                                        String title,
                                        AdapterView.OnItemClickListener listener) {
        // TODO Auto-generated method stub
        try {
            class MyAdapter extends BaseAdapter {
                String[] str;

                public MyAdapter(String[] str) {
                    this.str = str;
                }

                @Override
                public int getCount() {
                    // TODO Auto-generated method stub
                    return str.length;
                }

                @Override
                public Object getItem(int position) {
                    // TODO Auto-generated method stub
                    return str[position];
                }

                @Override
                public long getItemId(int position) {
                    // TODO Auto-generated method stub
                    return 0;
                }

                @Override
                public View getView(int position, View convertView,
                                    ViewGroup parent) {
                    // TODO Auto-generated method stub
                    View v = inflater.inflate(
                            R.layout.simple_spinner_item_white_text, null);
                    TextView tv = (TextView) v.findViewById(R.id.text1);
                    tv.setText(str[position]);

                    return v;
                }

            }

            // ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(
            // getActivity(), R.layout.simple_spinner_item_white_text, sort);
            // dataAdapter
            // .setDropDownViewResource(R.layout.simple_spinner_dropdown_item);

            ListView list = (ListView) inflater
                    .inflate(R.layout.listview, null);

            MyAdapter dataAdapter = new MyAdapter(strSelection);
            list.setAdapter(dataAdapter);

            list.setOnItemClickListener(listener);

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

            builder.setView(list);
            builder.setTitle(title);

            generalDialog = builder.create();
            generalDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

	protected void showSpinnerSelectionForDropdownForm(final String[] strValue,
			final String[] strSelection, final TextView tv, String title) {
		// TODO Auto-generated method stub
		try {
			class MyAdapter extends BaseAdapter {
				String[] str;

				public MyAdapter(String[] str) {
					this.str = str;
				}

				@Override
				public int getCount() {
					// TODO Auto-generated method stub
					return str.length;
				}

				@Override
				public Object getItem(int position) {
					// TODO Auto-generated method stub
					return str[position];
				}

				@Override
				public long getItemId(int position) {
					// TODO Auto-generated method stub
					return 0;
				}

				@Override
				public View getView(int position, View convertView,
						ViewGroup parent) {
					// TODO Auto-generated method stub
					View v = inflater.inflate(
							R.layout.simple_spinner_item_white_text, null);
					TextView tv = (TextView) v.findViewById(R.id.text1);
					tv.setText(str[position]);

					return v;
				}

			}

			ListView list = (ListView) inflater
					.inflate(R.layout.listview, null);

			MyAdapter dataAdapter = new MyAdapter(strSelection);
			list.setAdapter(dataAdapter);

			list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					// TODO Auto-generated method stub
					generalDialog.hide();
					tv.setText(strSelection[arg2]);
					tv.setTag(strValue[arg2]);

				}
			});

			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

			builder.setView(list);
			builder.setTitle(title);

			generalDialog = builder.create();
			generalDialog.show();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	boolean[] itemChecked;

	protected void showSpinnerSelectionForMultiSelectForm(
			final String[] strValue, final String[] strSelection,
			final TextView tv, String title, final Handler h) {
		// TODO Auto-generated method stub
		try {
			class MyAdapter extends BaseAdapter {
				String[] str;
				MyHolder holder;

				public MyAdapter(String[] str) {
					this.str = str;
					itemChecked = new boolean[str.length];
				}

				@Override
				public int getCount() {
					// TODO Auto-generated method stub
					return str.length;
				}

				@Override
				public Object getItem(int position) {
					// TODO Auto-generated method stub
					return str[position];
				}

				@Override
				public long getItemId(int position) {
					// TODO Auto-generated method stub
					return 0;
				}

				@Override
				public View getView(int position, View v, ViewGroup parent) {
					// TODO Auto-generated method stub
					if (v == null) {
						v = inflater
								.inflate(
										R.layout.simple_spinner_item_white_text_with_checkbox,
										null);
						holder = new MyHolder();
						holder.ctv = (CheckedTextView) v
								.findViewById(R.id.text1);
						v.setTag(holder);
					} else {
						holder = (MyHolder) v.getTag();
					}
					holder.ctv.setText(str[position]);

					holder.ctv.setChecked(itemChecked[position]);
					holder.ctv.setTag(position);
					holder.ctv.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							int position = (Integer) v.getTag();
							CheckedTextView ctv = (CheckedTextView) v;
							if (ctv.isChecked()) {
								itemChecked[position] = false;
								ctv.setChecked(false);

							} else {
								itemChecked[position] = true;
								ctv.setChecked(true);

							}

						}
					});

					return v;
				}

				class MyHolder {
					CheckedTextView ctv;
				}

			}

			ListView list = (ListView) inflater
					.inflate(R.layout.listview, null);

			MyAdapter dataAdapter = new MyAdapter(strSelection);
			list.setAdapter(dataAdapter);
			// list.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
			// list.setOnItemClickListener(new AdapterView.OnItemClickListener()
			// {
			//
			// @Override
			// public void onItemClick(AdapterView<?> arg0, View arg1,
			// int arg2, long arg3) {
			// // TODO Auto-generated method stub
			// generalDialog.hide();
			// tv.setText(strSelection[arg2]);
			// tv.setTag(strValue[arg2]);
			//
			// }
			// });

			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

			builder.setView(list);
			builder.setTitle(title);

			builder.setPositiveButton("OK",
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							String selection = "", value = "";
							for (int i = 0; i < itemChecked.length; i++) {
								if (itemChecked[i]) {
									selection += strSelection[i] + ",";
									value += strValue[i] + ",";
								}
							}
							if (selection.length() > 0) {
								selection = selection.substring(0,
										selection.length() - 1);
								value = value.substring(0, value.length() - 1);
								tv.setText(selection);
								tv.setTag(value);

								Message m = new Message();
								m.obj = itemChecked;
								h.sendMessage(m);
								dialog.dismiss();

							} else {
								simpleToast("Please choose one");
							}
						}
					});

			generalDialog = builder.create();
			generalDialog.show();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	protected void createTimePickerDialog(final TextView v) {
		// TODO Auto-generated method stub
		OnTimeSetListener timePickerListener = new OnTimeSetListener() {

			@Override
			public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
				// TODO Auto-generated method stub
				String strHourDay, strMinute;
				strHourDay = hourOfDay < 10 ? "0" + hourOfDay : "" + hourOfDay;
				strMinute = minute < 10 ? "0" + minute : "" + minute;

				v.setText(strHourDay + ":" + strMinute + ":00");
			}
		};

		TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(),
				timePickerListener, 8, 0, false);

		timePickerDialog.show();

	}

	public static String dateToDDMMMYY(int year, int monthOfYear, int dayOfMonth) {
		Calendar cal = Calendar.getInstance();
		cal.set(year, monthOfYear, dayOfMonth);

		Date date = new Date(cal.getTimeInMillis());

		SimpleDateFormat fmtOut = new SimpleDateFormat("dd MMM yyyy");
		return fmtOut.format(date);

	}

	protected void createDatePickerDialog(final TextView v) {
		// TODO Auto-generated method stub
		OnDateSetListener timePickerListener = new OnDateSetListener() {

			@Override
			public void onDateSet(DatePicker view, int year, int monthOfYear,
					int dayOfMonth) {
				// TODO Auto-generated method stub
				// v.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
				v.setText(dateToDDMMMYY(year, monthOfYear, dayOfMonth));
				v.setTag(year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);
			}
		};

		Calendar c = Calendar.getInstance();

		int dayOfMonth = c.get(Calendar.DAY_OF_MONTH);
		int monthOfYear = c.get(Calendar.MONTH);
		int year = c.get(Calendar.YEAR);

		DatePickerDialog timePickerDialog = new DatePickerDialog(getActivity(),
				timePickerListener, year, monthOfYear, dayOfMonth);

		timePickerDialog.show();

	}

	protected void changeReadStatus(String serial, String type) {
		changeReadStatus(serial, type, "read");
	}

	protected void changeReadStatus(String serial, String type, String status) {
		// AsyncHttpClient client = new AsyncHttpClient();
		RequestParams params = new RequestParams();
		params.add("username", pref.getPref(Config.PREF_USERNAME));
		params.add("document_serial", serial);
		params.add("document_type", type);
		params.add("document_status", status);
		params.add("active_session_token",
				pref.getPref(Config.PREF_ACTIVE_SESSION_TOKEN));
		params.add("session_username", pref.getPref(Config.PREF_USERNAME));

		PARestClient.post(pref.getPref(Config.SERVER),Config.API_READ_STATUS_CHANGE, params,
				new AsyncHttpResponseHandler() {
					@Override
					public void onSuccess(String content) {
						// TODO Auto-generated method stub
						super.onSuccess(content);
						Tracer.d(content);
					}

					@Override
					public void onFailure(int statusCode, Throwable error,
							String content) {
						// TODO Auto-generated method stub
						super.onFailure(statusCode, error, content);
					}
				});

	}

	// foto
	private Uri mImageCaptureUri;
	private Bitmap photo;
	private AlertDialog photoDialogMyF;
	protected ImageView generalIMG;
	protected View generalView;
	protected String MF_IMG_PROFILE = "";

	protected void createMyFragmentPhotoDialog() {

		final String[] items = new String[] { "Pick from camera",
				"Pick from gallery" };
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
				android.R.layout.select_dialog_item, items);
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

		builder.setTitle("Choose picture");
		builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int item) { // pick from
																	// camera
				if (item == 0) {
					Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

					mImageCaptureUri = Uri.fromFile(new File(Environment
							.getExternalStorageDirectory(), ".tmp_avatar_"
							+ String.valueOf(System.currentTimeMillis())
							+ ".jpg"));

					intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT,
							mImageCaptureUri);

					try {
						intent.putExtra("return-data", true);

						startActivityForResult(intent, Config.PICK_FROM_CAMERA);
					} catch (ActivityNotFoundException e) {
						e.printStackTrace();
					}
				} else { // pick from file
					Intent intent = new Intent();

					intent.setType("image/*");
					intent.setAction(Intent.ACTION_GET_CONTENT);

					startActivityForResult(Intent.createChooser(intent,
							"Complete action using"), Config.PICK_FROM_FILE);
				}
			}
		});
		photoDialogMyF = builder.create();
		photoDialogMyF.show();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Tracer.d("OnActRes Home");
		// Toast.makeText(getActivity(),
		// "OnActivity Result Result:"+resultCode+" request:"+requestCode,
		// Toast.LENGTH_SHORT).show();
		// if (resultCode != getActivity().RESULT_OK)
		// return;
		//
		// switch (requestCode) {
		// case Config.PICK_FROM_CAMERA:
		// doCrop();
		//
		// break;
		//
		// case Config.PICK_FROM_FILE:
		// mImageCaptureUri = data.getData();
		//
		// doCrop();
		//
		// break;
		//
		// case Config.CROP_FROM_CAMERA:
		// Bundle extras = data.getExtras();
		//
		// if (extras != null) {
		// Log.v("cameracrop", "yippie");
		// photo = null;
		// photo = extras.getParcelable("data");
		//
		// // mImageView.setImageBitmap(photo);
		// Drawable photoDraw = new BitmapDrawable(getResources(), photo);
		// // Bitmap bmp = new ImageHelper().getRoundedCornerBitmap(photo);
		//
		// generalIMG.setImageBitmap(photo);
		//
		// // saveProfilePhoto(bmp);
		// // new AsyncSaveProfile().execute(bmp);
		//
		// } else {
		// Toast.makeText(getActivity(), "extras = null",
		// Toast.LENGTH_SHORT).show();
		// }
		//
		// File f = new File(mImageCaptureUri.getPath());
		//
		// if (f.exists())
		// f.delete();
		//
		// break;
		//
		// }

		try{
		if (resultCode == getActivity().RESULT_OK
				&& (requestCode == ChooserType.REQUEST_PICK_PICTURE || requestCode == ChooserType.REQUEST_CAPTURE_PICTURE)) {
			if (imageChooserManager == null) {
				reinitializeImageChooser();
			}
			imageChooserManager.submit(requestCode, data);
		} 
		if (resultCode == getActivity().RESULT_CANCELED
				&& (requestCode == ChooserType.REQUEST_PICK_PICTURE || requestCode == ChooserType.REQUEST_CAPTURE_PICTURE)) {
			simpleToast("Cancel");
			loadingDialog.hide();
			FragmentPostOpenBid f=(FragmentPostOpenBid) this;
			f.removePhotoFromWrapper(generalView);
		}
		
		else {
			loadingDialog.hide();

		}
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	private void saveProfilePhoto(Bitmap bitmap) {
		File root;
		root = Environment.getExternalStorageDirectory();

		// create a File object for the output file
		File dir = new File(root, ""/* Config.PROFILE_PATH */);
		File file = new File(dir, MF_IMG_PROFILE);
		// now attach the OutputStream to the file object, instead of a String
		// representation

		dir.mkdirs();
		dir.setWritable(true);

		if (!file.exists())
			try {
				file.createNewFile();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		try {
			FileOutputStream ostream = new FileOutputStream(file);
			bitmap.compress(CompressFormat.PNG, 100, (OutputStream) ostream);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void doCrop() {
		// TODO Auto-generated method stub

		int aspectX = 1;
		int aspectY = 1;
		int imageWidth = 500;
		int imageHeight = 500;
		final ArrayList<CropOption> cropOptions = new ArrayList<CropOption>();

		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setType("image/*");

		List<ResolveInfo> list = getActivity().getPackageManager()
				.queryIntentActivities(intent, 0);

		int size = list.size();

		if (size == 0) {
			Toast.makeText(getActivity(), "Can not find image crop app",
					Toast.LENGTH_SHORT).show();

			return;
		} else {
			intent.setData(mImageCaptureUri);

			intent.putExtra("outputX", imageWidth);
			intent.putExtra("outputY", imageHeight);
			intent.putExtra("aspectX", 1);
			intent.putExtra("aspectY", 1);
			intent.putExtra("scale", true);
			intent.putExtra("return-data", true);

			if (size == 1) {
				Intent i = new Intent(intent);
				ResolveInfo res = list.get(0);

				i.setComponent(new ComponentName(res.activityInfo.packageName,
						res.activityInfo.name));

				startActivityForResult(i, Config.CROP_FROM_CAMERA);
			} else {
				for (ResolveInfo res : list) {
					final CropOption co = new CropOption();

					co.title = getActivity().getPackageManager()
							.getApplicationLabel(
									res.activityInfo.applicationInfo);
					co.icon = getActivity().getPackageManager()
							.getApplicationIcon(
									res.activityInfo.applicationInfo);
					co.appIntent = new Intent(intent);

					co.appIntent
							.setComponent(new ComponentName(
									res.activityInfo.packageName,
									res.activityInfo.name));

					cropOptions.add(co);
				}

				CropOptionAdapter adapter = new CropOptionAdapter(
						getActivity(), cropOptions);

				AlertDialog.Builder builder = new AlertDialog.Builder(
						getActivity());
				builder.setTitle("Choose Crop App");
				builder.setAdapter(adapter,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int item) {
								startActivityForResult(
										cropOptions.get(item).appIntent,
										Config.CROP_FROM_CAMERA);
							}
						});

				builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
					@Override
					public void onCancel(DialogInterface dialog) {

						if (mImageCaptureUri != null) {
							getActivity().getContentResolver().delete(
									mImageCaptureUri, null, null);
							mImageCaptureUri = null;
						}
					}
				});

				AlertDialog alert = builder.create();

				alert.show();
			}
		}

		// Tracer.d("debug","doCrop");
		// try {
		// final Uri imageUri = mImageCaptureUri;
		// final InputStream imageStream =
		// getActivity().getContentResolver().openInputStream(imageUri);
		// final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
		// generalIMG.setImageBitmap(selectedImage);
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
		//
		// Tracer.d("debug","doCropFinish");
	}

	public void shareIntent(String subject, String message) {
		Intent intent = new Intent(android.content.Intent.ACTION_SEND);
		intent.setType("text/plain");
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);

		// Add data to the intent, the receiving app will decide what to do with
		// it.
		intent.putExtra(Intent.EXTRA_SUBJECT, subject);
		intent.putExtra(Intent.EXTRA_TEXT, message);
		getActivity().startActivity(
				Intent.createChooser(intent, "How do you want to share?"));

	}

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

		v.findViewById(R.id.btnNext2).setVisibility(View.GONE);
		// setOnClickListener(new OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		// // TODO Auto-generated method stub
		// dialogPhotoPreview.dismiss();
		// // removePhotoFromWrapper(currentView);
		// }
		// });

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

	
	
	@Override
	public void onImageChosen(final ChosenImage image) {
		// TODO Auto-generated method stub
		getActivity().runOnUiThread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				Tracer.d("ImageChoosen");
				try {
					if (image != null) {
						File filePhotoProduct = new File(image
								.getFileThumbnailSmall());
						generalIMG.setImageURI(Uri.parse(filePhotoProduct
								.toString()));
						Tracer.d("DrawImage");

					}
					loadingDialog.hide();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	@Override
	public void onError(final String reason) {
		// TODO Auto-generated method stub
		// FormUtils.simpleToast(getActivity(), reason);
		getActivity().runOnUiThread(new Runnable() {

			@Override
			public void run() {
				
				loadingDialog.hide();
				Toast.makeText(getActivity(), reason,
						Toast.LENGTH_LONG).show();
			}}
		);
	}

	void reset() {
	}

	private void reinitializeImageChooser() {
		try{
		imageChooserManager = new ImageChooserManager(this, chooserType, ""
				+ Config.TMP_IMG_ROOT, true);
		imageChooserManager.setImageChooserListener(this);
		imageChooserManager.reinitialize(filePath);
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	// foto
	protected ImageView generalImgProfile;

	protected boolean createPhotoDialog(final FragmentPostOpenBid f) {

		final String[] items = new String[] { "Camera",
				"Gallery" };
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
				android.R.layout.select_dialog_item, items);
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

		builder.setTitle("Upload Photo")
				.setCancelable(false)
				.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {


						setBool(false);
						dialog.cancel();

					}
				})
				.setOnCancelListener(new DialogInterface.OnCancelListener() {
					@Override
					public void onCancel(DialogInterface dialog) {
						try {
//					FragmentPostOpenBid f = (FragmentPostOpenBid) getParentFragment().getActivity();
							f.removePhotoFromWrapper(generalView);
						}catch(Exception e){
							e.printStackTrace();
						}
						Log.v("CANCEL DIALOG", "DIALOG CANCELED");
//						Toast.makeText(getActivity(), "View Removed", Toast.LENGTH_SHORT).show();
					}
				});
		builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int item) { // pick from
																	// camera
				if (item == 0) {
					// pick from camera
					takePicture();
//					setBool(true);
//					FragmentPostOpenBid f = (FragmentPostOpenBid) getParentFragment();
//					f.checkChildCount();
				} else { // pick from file
					chooseImage();
//					setBool(true);
//					FragmentPostOpenBid f = (FragmentPostOpenBid) getParentFragment();
//					f.checkChildCount();
				}
			}
		});
		photoDialogMyF = builder.create();
		photoDialogMyF.show();

		return didSelectImage();
	}

	private boolean isSelected = true;
	private void setBool(boolean b){
		isSelected = b;
	}

	public boolean didSelectImage(){
		return isSelected;
	}

	private ImageChooserManager imageChooserManager;
	private String filePath;
	private ProgressBar pbar;
	private int chooserType;

	private void chooseImage() {
		try {

		chooserType = ChooserType.REQUEST_PICK_PICTURE;
		imageChooserManager = new ImageChooserManager(this,
				ChooserType.REQUEST_PICK_PICTURE, "" + Config.TMP_IMG_ROOT,
				true);
		imageChooserManager.setImageChooserListener(this);
			loadingDialog.show();
			filePath = imageChooserManager.choose();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void takePicture() {
		try {

		chooserType = ChooserType.REQUEST_CAPTURE_PICTURE;
		imageChooserManager = new ImageChooserManager(this,
				ChooserType.REQUEST_CAPTURE_PICTURE, "" + Config.TMP_IMG_ROOT,
				true);
		imageChooserManager.setImageChooserListener(this);
			loadingDialog.show();
			filePath = imageChooserManager.choose();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	ProgressDialog uploadDialog;

	@Override
	public void onSaveInstanceState(Bundle outState) {
		outState.putInt("chooser_type", chooserType);
		outState.putString("media_path", filePath);
		super.onSaveInstanceState(outState);
	}

	public void showTNC(String title, String s) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle(title);
		builder.setMessage(s);
		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                dialog.dismiss();
            }
        });
		builder.show();
	}



    protected void showMessageDialog(String message) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                getActivity());

        // set title
        alertDialogBuilder.setMessage(message);

        // set dialog message
        alertDialogBuilder
                .setCancelable(true)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(
                                    DialogInterface dialog,
                                    int id) {
                                dialog.dismiss();
                            }
                        });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

}
