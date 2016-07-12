package com.pa.common;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;

public class AsyncHttpPost extends AsyncTask<String, String, String> {
	Context a;
	boolean isServerError = false;
	OnSuccessListener onSuccess = null;
	HttpPost post;
	List<NameValuePair> entity;// =new ArrayList<NameValuePair>();

	public AsyncHttpPost(Context a, List<NameValuePair> entity) {
		this.a = a;
		this.entity = entity;
	}

	@Override
	protected String doInBackground(String... uri) {
		//Tracer.d("doInBackground");
		String responseString = null;

		try {
			post = new HttpPost(uri[0]);
			post.setEntity(new UrlEncodedFormEntity(entity));
			//System.out.println(post.getRequestLine());
			HttpClient httpclient = new DefaultHttpClient();
			// HttpHost httpproxy = new HttpHost("127.0.0.1",8888);
			// httpclient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY,
			// httpproxy);

			HttpResponse response;

			while (responseString == null) {
				if (isCancelled())
					break;

				if (isNetworkAvailable()) {
					try {
						response = httpclient.execute(post);
						StatusLine statusLine = response.getStatusLine();
						//Tracer.d("post", statusLine.toString());
						if (statusLine.getStatusCode() == HttpStatus.SC_OK
								|| statusLine.getStatusCode() == HttpStatus.SC_INTERNAL_SERVER_ERROR) {

							ByteArrayOutputStream out = new ByteArrayOutputStream();
							response.getEntity().writeTo(out);
							out.close();
							responseString = out.toString();
						} else {
							// Closes the connection.
							response.getEntity().getContent().close();

							isServerError = true;
						}
					} catch (ClientProtocolException e) {
						// TODO Handle problems..
						// new WriteToLog().write(e.toString());
						isServerError = true;
						//Log.e("post", e.toString());
						break;
					} catch (IOException ex) {
						// handle network error here
						isServerError = true;
						//Log.e("post", ex.toString());
						ex.printStackTrace();
						break;
					} catch (Exception ex) {
						// handle RuntimeException and others here
						// (You weren't just going to ignore them, were you?)
						isServerError = true;
						//Log.e("post", ex.toString());
						break;
					} 
//					finally {
//						// httpclient.getConnectionManager().shutdown();
//
//					}
				} else {
					isServerError=true;
					break;
				}
			}
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			isServerError=true;

		}

		return responseString;
	}

	@Override
	protected void onPostExecute(String result) {
		super.onPostExecute(result);
		// Do anything with response..
		//Tracer.d("On Post Execute");
		if (!isNetworkAvailable()) {
			try {
				onSuccess.doError();
			} catch (Exception e) {
				e.printStackTrace();
			}
			;
		} else if (isServerError) {
			onSuccess.doError();
		} else if (result != null && isNetworkAvailable()) {
			try {
				onSuccess.doSuccess(result);
			} catch (Exception e) {
				e.printStackTrace();
			}
			;
		} else {
			try {
				onSuccess.doError();
			} catch (Exception e) {
				e.printStackTrace();
			}
			;
		}
	}

	public void setOnSuccessListener(OnSuccessListener listener) {
		onSuccess = listener;
	}

	public interface OnSuccessListener {
		public abstract void doSuccess(String result);

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