package com.pa.common;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.analytics.HitBuilders;

public class Analytic {

	Activity a;

	public Analytic(Activity a) {
		this.a = a;
	}

	public void trackScreen(String page) {
		new AsyncTrackScreen().execute(page);
	}

	class AsyncTrackScreen extends AsyncTask<String, String, String> {

		@Override
		protected String doInBackground(String... params) {
            MyApplication.tracker().setScreenName(params[0]);
            MyApplication.tracker().send(new HitBuilders.ScreenViewBuilder().build());
            MyApplication.analytics().dispatchLocalHits();
			return params[0];
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			try {
				Log.i("analytic", result);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	public void trackCustomDimension(String page, int dimension, String value) {
		new AsyncTrackCustomDimension().execute(page, dimension, value);
	}

	
	
	
	class AsyncTrackCustomDimension extends AsyncTask<Object, String, String> {

		@Override
		protected String doInBackground(Object... params) {
            MyApplication.tracker().setScreenName(params[0] + "");
            MyApplication.tracker().send(new HitBuilders.ScreenViewBuilder()
                            .setCustomDimension(
                                    Integer.parseInt(params[1] + ""),
                                    params[2] + "")
                            .build()
            );
            MyApplication.analytics().dispatchLocalHits();
			return params[0] + "";
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			try {
				Log.i("analytic", result);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	
	public void trackEvent(String category, String action, String label) {
		new AsyncTrackCustomEvent().execute(category, action, label);
	}

	
	
	class AsyncTrackCustomEvent extends AsyncTask<String, String, String> {

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.os.AsyncTask#doInBackground(java.lang.Object[])
		 */
		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub

			
			try {
//				GoogleAnalytics myInstance = GoogleAnalytics.getInstance(a
//						.getApplicationContext());
//				Tracker tracker = myInstance.getTracker(Config.GOOGLE_ID);
//
//
//				tracker.trackEvent(params[0], params[1], params[2], (long) 0);
//
//				GAServiceManager.getInstance().dispatch();

			} catch (Exception e) {
				Log.i(getClass().getName(), e.toString());
			}


			
			return null;
		}
	}



    public void trackECommerce(String orderId,
                               String storeName,
                               String revenue,
                               String currency,
                               String productName,
                               String category) {
        new AsyncTrackECommerce().execute(orderId, storeName, revenue, currency, productName, category);
    }




    class AsyncTrackECommerce extends AsyncTask<Object, String, String> {

        @Override
        protected String doInBackground(Object... params) {
            Log.d("eCommerce",
                    "TransactionId: " + params[0] +
                    ", Affiliation: " + params[1] +
                    ", Revenue: " + Double.parseDouble(params[2] + "") +
                    ", CurrencyCode: " + params[3] +
                    ", Name: " + params[4] +
                    ", Sku: " + params[0] +
                    ", Category: " + params[5] +
                    ", Price: " + Double.parseDouble(params[2] + "") +
                    ", Category: " + params[5]
            );

            MyApplication.tracker().send(new HitBuilders.TransactionBuilder()
                    .setTransactionId(params[0] + "")
                    .setAffiliation(params[1] + "")
                    .setRevenue(Double.parseDouble(params[2] + ""))
                    .setTax(0)
                    .setShipping(0)
                    .setCurrencyCode(params[3] + "")
                    .build());

            MyApplication.tracker().send(new HitBuilders.ItemBuilder()
                    .setTransactionId(params[0] + "")
                    .setName(params[4] + "")
                    .setSku(params[0] + "")
                    .setCategory(params[5] + "")
                    .setPrice(Double.parseDouble(params[2] + ""))
                    .setQuantity(1)
                    .setCurrencyCode(params[3] + "")
                    .build());

            MyApplication.analytics().dispatchLocalHits();
            return params[0] + "";
        }

        @Override
        protected void onPostExecute(String result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            try {
                Log.i("analytic", result);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }
}
