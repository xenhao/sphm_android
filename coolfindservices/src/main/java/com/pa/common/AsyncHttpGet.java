package com.pa.common;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.view.View;

public class AsyncHttpGet extends AsyncTask<String, String, String> {
	Activity a;
	boolean isServerError = false;
	OnSuccessListener onSuccess = null;
	customOnSuccessListener customOnSuccess = null;

	public View v;

	public AsyncHttpGet(Activity a) {
		this.a = a;
	}

	@Override
	protected String doInBackground(String... uri) {

		//System.out.println("doInBackground");
		HttpClient httpclient = new DefaultHttpClient();
		HttpResponse response;
		String responseString = null;

		while (responseString == null) {
			if (isCancelled())
				break;

			if (isNetworkAvailable()) {
				try {
					response = httpclient.execute(new HttpGet(uri[0]));
					StatusLine statusLine = response.getStatusLine();
					if (statusLine.getStatusCode() == HttpStatus.SC_OK) {

						ByteArrayOutputStream out = new ByteArrayOutputStream();
						response.getEntity().writeTo(out);
						out.close();
						responseString = out.toString();
					} else {
						// Closes the connection.
						response.getEntity().getContent().close();
						// throw new IOException(statusLine.getReasonPhrase());
						// add handling for server error here
						isServerError = true;
					}
				} catch (ClientProtocolException e) {
					// TODO Handle problems..
					isServerError = true;

				} catch (IOException ex) {
					// handle network error here
					isServerError = true;

				} catch (Exception ex) {
					// handle RuntimeException and others here
					// (You weren't just going to ignore them, were you?)
					isServerError = true;

				} finally {
					httpclient.getConnectionManager().shutdown();

				}
			} else {
				break;
			}
		}

		return responseString;
	}

	@Override
	protected void onPostExecute(String result) {
		super.onPostExecute(result);
		// Do anything with response..
		if (onSuccess != null) {
			if (!isNetworkAvailable()) {
				try {
					onSuccess.doError();
				} catch (Exception e) {
				}
				;
			} else if (isServerError) {
				onSuccess.doError();
			} else if (result != null && isNetworkAvailable()) {
				try {
					onSuccess.doSuccess(result);
				} catch (Exception e) {
				}
				;
			} else {
				try {
					onSuccess.doError();
				} catch (Exception e) {
				}
				;
			}
		} else if (customOnSuccess != null) {

			if (!isNetworkAvailable()) {
				try {
					customOnSuccess.doError();
				} catch (Exception e) {
				}
				;
			} else if (isServerError) {
				customOnSuccess.doError();
			} else if (result != null && isNetworkAvailable()) {
				try {
					customOnSuccess.doSuccess(result,v);
				} catch (Exception e) {
				}
				;
			} else {
				try {
					customOnSuccess.doError();
				} catch (Exception e) {
				}
				;
			}
		
		}
	}

	public void setOnSuccessListener(OnSuccessListener listener) {
		onSuccess = listener;
	}
	public void setCustomOnSuccessListener(customOnSuccessListener listener) {
		customOnSuccess = listener;
	}

	public interface OnSuccessListener {
		public abstract void doSuccess(String result);

		public abstract void doError();

		public abstract void doServerError();

	}

	public interface customOnSuccessListener {
		public abstract void doSuccess(String result, View v);

		public abstract void doError();

		public abstract void doServerError();

	}

	private boolean isNetworkAvailable() {
		ConnectivityManager connectivityManager = (ConnectivityManager) a
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager
				.getActiveNetworkInfo();
		return activeNetworkInfo != null;
	}

}