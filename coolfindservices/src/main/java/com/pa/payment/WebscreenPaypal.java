package com.pa.payment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;

import com.coolfindservices.androidconsumer.R;

public class WebscreenPaypal extends Activity {

	public static final String URL = "";
	private static final String TAG = "Class Webscreen";
	private static LinearLayout linearLayout;
	private static String trackUrl, failUrl;
	private static WebView webview;

	private static ProgressDialog dialog;
	private static int increment = 20;
	private static String PAYPAL_URI = "";

	char returnStatus;

	// MyCount close8sec = new MyCount(4000,1000);

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		if (!isTaskRoot()) {
			final Intent intent = getIntent();
			final String intentAction = intent.getAction();
			if (intent.hasCategory(Intent.CATEGORY_LAUNCHER)
					&& intentAction != null
					&& intentAction.equals(Intent.ACTION_MAIN)) {
				finish();
			}
		}
		super.onCreate(savedInstanceState);
		setContentView(R.layout.webscreenpaypal);
		String turl = getIntent().getStringExtra(URL);
		final AlertDialog alertDialog = new AlertDialog.Builder(this).create();

		dialog = new ProgressDialog(this);
		dialog.setMessage("Loading...");
		dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		dialog.setCancelable(false);
		dialog.show();

		Thread background = new Thread(new Runnable() {
			public void run() {
				try {
					while (dialog.getProgress() <= dialog.getMax()) {
						Thread.sleep(500);
						progressHandler.sendMessage(progressHandler
								.obtainMessage());
					}
				} catch (java.lang.InterruptedException e) {
					Log.v(TAG, "Exception dialog");
					// if something fails do something smart
				}
			}
		});

		// start the background thread
		background.start();

		isNetworkAvailable(); // FOR NETWORK CONNECTION TEST

		linearLayout = (LinearLayout) this.findViewById(R.id.ly);

		webview = (WebView) findViewById(R.id.webview);
		// webview.getSettings().setJavaScriptEnabled(true);

		webview.setVerticalScrollBarEnabled(false);

		webview.setWebViewClient(new WebViewClient() {

			public void onLoadResource(WebView view, String url) {
				// dialog.show();
				Log.v(TAG, "trackUrlload" + url);

			}

			public void onPageStarted(WebView view, String url, Bitmap favicon)
			/*     */{
				dialog.show();
				/*     */}

			// FOR TRACK URL PATTERN WHEN PAGE FINISH LOAD OUT
			public void onPageFinished(WebView view, String url) {

				dialog.dismiss();
				trackUrl = url;
				Log.v(TAG, "trackUrl" + trackUrl);
				// 10-14 14:08:39.839: VERBOSE/Class Webscreen(12788):
				// trackUrlhttps://secure2.gsc.com.my/epayappdev/pub/emreq/paypal_expchkout_resp.do?a=confirm&inv_no=6152116&amt=12.00&token=EC-75H71981NB7962227&PayerID=LC9J7AYNF9W8C
				// 10-14 14:11:48.593: VERBOSE/Class Webscreen(13939):
				// trackUrlhttps://secure2.gsc.com.my/epayappdev/pub/emreq/paypal_expchkout_resp.do?a=cancel&inv_no=6152194&token=EC-78S86648XY0700839
				if (trackUrl.contains("a=confirm")) {
					returnStatus = 'c';
					Intent intent = new Intent();
					intent.putExtra("returnKey", "c");
					if (getParent() == null) {
						setResult(Activity.RESULT_OK, intent);
					} else {
						getParent().setResult(Activity.RESULT_OK, intent);
					}

					dialog.dismiss();
					finish();
					// close8sec.start();

				} else if (trackUrl.contains("a=cancel")) {

					returnStatus = 'i';
					Intent intent = new Intent();
					intent.putExtra("returnKey", "i");
					if (getParent() == null) {
						setResult(Activity.RESULT_OK, intent);
					} else {
						getParent().setResult(Activity.RESULT_OK, intent);
					}

					dialog.dismiss();
					finish();
					// close8sec.start();

				}

				webview.requestFocus(View.FOCUS_DOWN);
				webview.setOnTouchListener(new View.OnTouchListener() {

					public boolean onTouch(View v, MotionEvent event) {
						switch (event.getAction()) {
						case MotionEvent.ACTION_DOWN:
						case MotionEvent.ACTION_UP:
							if (!v.hasFocus()) {
								v.requestFocus();
							}
							break;
						}
						return false;
					}
				});

				linearLayout.setVisibility(View.VISIBLE);
				webview.setVisibility(View.VISIBLE);

			}

			// FOR HANDLE ERROR
			public void onReceivedError(WebView view, int errorCode,
					String description, String failingUrl) {
				failUrl = failingUrl;
				linearLayout.removeView(webview);
				linearLayout.setVisibility(View.INVISIBLE);
				Log.v(TAG, "ERROR INFORMATON view:" + view);
				Log.v(TAG, "ERROR INFORMATON errorCode:" + errorCode);
				Log.v(TAG, "ERROR INFORMATON description:" + description);
				Log.v(TAG, "ERROR INFORMATON failingUrl:" + failingUrl);

				alertDialog.setTitle("Error");
				alertDialog.setCancelable(false);
				alertDialog.setMessage(description);
				alertDialog.setButton("OK",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								if (failUrl.contains("m2uThankYou.do")) {

								} else {

								}
								dialog.dismiss();
								finish();
								return;
							}
						});
				alertDialog.show();

			}

			// FOR HANDLE HTTPS
//			public void onReceivedSslError(WebView view,
//					SslErrorHandler handler, SslError error) {
//				handler.proceed();
//			}

		});
		webview.setWebChromeClient(new WebChromeClient());
		webview.getSettings().setJavaScriptEnabled(true);

		webview.loadUrl(turl);

	}

	public void init(String uri) {
		this.PAYPAL_URI = uri;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK)) {
			Log.v(TAG, "KEYCODE_BACK" + keyCode);
			if (trackUrl.contains("a=confirm")) {
				returnStatus = 'c';
				Intent intent = new Intent();
				intent.putExtra("returnKey", "c");
				if (getParent() == null) {
					setResult(Activity.RESULT_OK, intent);
				} else {
					getParent().setResult(Activity.RESULT_OK, intent);
				}

			} else
			// if(trackUrl.contains("a=cancel"))
			{

				returnStatus = 'i';
				Intent intent = new Intent();
				intent.putExtra("returnKey", "i");
				if (getParent() == null) {
					setResult(Activity.RESULT_OK, intent);
				} else {
					getParent().setResult(Activity.RESULT_OK, intent);
				}

			}

			finish();
		}
		return super.onKeyDown(keyCode, event);
	}

	public class MyCount extends CountDownTimer {

		public MyCount(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);
		}

		@Override
		public void onFinish() {
			// disTime.setText("done!");
			Log.v(TAG, "TIMES UP!!!!!!!!!");

			finish();

		}

		@Override
		public void onTick(long millisUntilFinished) {

			// disTime.setText("Left: " + millisUntilFinished/1000);

		}

	}

	private boolean isNetworkAvailable() {
		ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager
				.getActiveNetworkInfo();
		return activeNetworkInfo != null;

	}

	Handler progressHandler = new Handler() {
		public void handleMessage(Message msg) {
			dialog.incrementProgressBy(increment);
		}
	};

	public Intent show(Context con) {
		Intent k = new Intent(con, WebscreenPaypal.class);
		k.putExtra(URL, PAYPAL_URI);
		return k;
	}

}
