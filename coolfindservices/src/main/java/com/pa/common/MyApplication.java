package com.pa.common;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import com.appsflyer.AppsFlyerLib;
import com.facebook.FacebookSdk;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;

import java.nio.charset.Charset;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import io.intercom.android.sdk.Intercom;

/**
 * Created by sky on 5/19/15.
 */
public class MyApplication extends Application {

    public static GoogleAnalytics analytics;
    public static Tracker tracker;

    @Override
    public void onCreate() {
        super.onCreate();

        FacebookSdk.sdkInitialize(getApplicationContext());

        /*
         Intercom
          */

        String result = "";
        try {
            KeyGenerator kg = KeyGenerator.getInstance("HMACSHA256");
            SecretKey sk = kg.generateKey();

            // Get instance of Mac object implementing HMAC-MD5, and
            // initialize it with the above secret key
            Mac mac = Mac.getInstance("HMACSHA256");
            mac.init(sk);
            byte[] mac_data = mac.doFinal("The quick brown fox jumps over the lazy dog".getBytes());

            for (final byte element : mac_data)
            {
                result += Integer.toString((element & 0xff) + 0x100, 16).substring(1);
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }
//        Intercom.initialize(this, "android_sdk-5b6a6a6a17c2f5a1433e13f69d6f97f041710721", "l3a9xonh");
        Intercom.initialize(this, "android_sdk-fea1cdcc3fbeca8188c0cd6b0aa5d39b69b68c34", "iuxct6jo");
        Intercom.client().setSecureMode(result, "The quick brown fox jumps over the lazy dog");

        /*
         AppsFlyer
          */

//        AppsFlyerLib.setAppsFlyerKey("gCwydBJJJC6xGFfLmGpULC");

        /*
         Google Analytics
          */

        analytics = GoogleAnalytics.getInstance(this);
        analytics.setLocalDispatchPeriod(0); // Enable manual dispatch

        tracker = analytics.newTracker(Config.GOOGLE_ID);
    }

    /**
     * Access to the global Analytics singleton. If this method returns null you forgot to either
     * set android:name="&lt;this.class.name&gt;" attribute on your application element in
     * AndroidManifest.xml or you are not setting this.analytics field in onCreate method override.
     */
    public static GoogleAnalytics analytics() {
        return analytics;
    }

    /**
     * The default app tracker. If this method returns null you forgot to either set
     * android:name="&lt;this.class.name&gt;" attribute on your application element in
     * AndroidManifest.xml or you are not setting this.tracker field in onCreate method override.
     */
    public static Tracker tracker() {
        return tracker;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}
