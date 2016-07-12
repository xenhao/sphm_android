package com.pa.free_credit;

import org.apache.http.Header;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.pa.common.Config;
import com.pa.common.MyFragment;
import com.pa.common.OnFragmentChangeListener;
import com.pa.common.PARestClient;
import com.pa.landing.ActivityLanding;
import com.pa.parser.ParserReferralCode;
import com.pa.pojo.ReferralCode;
import com.coolfindservices.androidconsumer.R;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.tweetcomposer.TweetComposer;

import java.io.IOException;
import java.net.URL;

import io.fabric.sdk.android.Fabric;

public class FragmentEarnFreeCredits extends MyFragment implements
		OnClickListener, Config {

    final private String TAG = "FragmentEarnFreeCredits";
	OnFragmentChangeListener listener;
	String code="", promoUrl = "";
	TextView txtCode, txtDesc, txtAvailableFreeCredit;

    String emailSubject = "", emailBody = "", smsBody = "", fbShareTitle = "", fbShareText = "", twitterShareText = "";
	
	public FragmentEarnFreeCredits(){
		
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_refer_to_friend, null);
		v.findViewById(R.id.btnMenu).setOnClickListener(this);

		txtCode=(TextView)v.findViewById(R.id.code);
        txtDesc=(TextView)v.findViewById(R.id.txtDescription);
        txtAvailableFreeCredit=(TextView)v.findViewById(R.id.txtAvailableFreeCredit);

        v.findViewById(R.id.share_fb).setOnClickListener(this);
        v.findViewById(R.id.share_twitter).setOnClickListener(this);
        v.findViewById(R.id.share_txt).setOnClickListener(this);
        v.findViewById(R.id.share_email).setOnClickListener(this);

		return v;
	}

	@Override
	public void onAttach(Activity activity) {
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
		super.onCreate(savedInstanceState);
		loadingInternetDialog.show();

        getFreeCreditsInformation();

        TwitterAuthConfig authConfig =  new TwitterAuthConfig(Config.TWITTER_KEY, Config.TWITTER_SECRET);
        Fabric.with(getActivity(), new TwitterCore(authConfig), new TweetComposer());
		
	}

    private void getFreeCreditsInformation() {
        RequestParams params=new RequestParams();
        params.add("active_session_token", pref.getPref(PREF_ACTIVE_SESSION_TOKEN));
        params.add("session_username", pref.getPref(PREF_USERNAME));
//        params.add("source_type", "sphm");

        PARestClient.get(pref.getPref(Config.SERVER),Config.API_GET_REFERRER_CODE,params,new AsyncHttpResponseHandler(){

            @Override
            public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
                super.onSuccess(arg0, arg1, arg2);
                loadingInternetDialog.dismiss();
                Log.d(TAG, "Response " + new String(arg2));
                ParserReferralCode parser=new ParserReferralCode(new String(arg2));
                ReferralCode referalCode=parser.getData();
                if("success".equals(referalCode.status)){
                    code=referalCode.result.serial;
                    txtCode.setText(code);

                    Log.i("URL", Config.API_GET_REFERRER_CODE);
                    Log.i("session_username", pref.getPref(PREF_USERNAME));
                    Log.i("active_session_token", pref.getPref(PREF_ACTIVE_SESSION_TOKEN));

                    promoUrl = referalCode.result.url;

                    String amount = referalCode.result.currency + " " + referalCode.result.reward;

                    String desc1 = getResources().getString(R.string.refer_page_advisor_to_friends_and_get_10_credits_1);
                    desc1 = desc1.replace("[PROMOCODE]", "<b>"+code+"</b>");
                    desc1 = desc1.replace("[AMOUNT]", "<b>"+amount+"</b>");

                    String desc2 = getResources().getString(R.string.refer_page_advisor_to_friends_and_get_10_credits_2);
                    desc2 = desc2.replace("[AMOUNT]", "<b>"+amount+"</b>");

                    String desc3 = getResources().getString(R.string.refer_page_advisor_to_friends_and_get_10_credits_3);
                    desc3 = desc3.replace("[MAX_AMOUNT]", "<b>" + referalCode.result.currency +
                            " " + referalCode.result.reward_limit + "</b>");

                    txtDesc.setText(Html.fromHtml(desc1 + "<br /><br />" + desc2 + "<br /><br />" + desc3));

                    txtAvailableFreeCredit.setText(referalCode.result.currency +
                            " " + referalCode.result.cs_credit);

                    emailSubject = "Try Cool-Find & Earn FREE "+ amount + " Credit!";
                    emailBody = "Hey, register for Cool-Find, Southeast Asia's largest mobile marketplace for home & lifestyle services, and earn " + amount + " in FREE credit using my referral code: " + code + "\n" +
                            "\n" +
                            "Register using this link:\n" +
                            referalCode.result.url;

                    fbShareTitle = "Try Cool-Find & Earn FREE " + amount + " Credit!";
                    fbShareText = "Sign-up for Cool-Find, Southeast Asia's largest mobile marketplace for home & lifestyle services, and earn " + amount + " in FREE credit using my referral code: " + code;

                    smsBody = "Sign-up for Cool-Find, Southeast Asia's largest mobile marketplace for home & lifestyle services, and earn " + amount + " in FREE credit using my referral code: " + code + "\n" +
                            "\n" +
                            "Register using this link:\n" +
                            referalCode.result.url;

                    twitterShareText = "Try Cool-Find,SEA largest marketplace for home & lifestyle svcs.Earn " + amount + " credit using my referral code:" + code;
                }else{
                    Toast.makeText(getActivity(), "Failed to get code", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int arg0, Header[] arg1, byte[] arg2,
                                  Throwable arg3) {
                super.onFailure(arg0, arg1, arg2, arg3);
//                Log.d(TAG, "Error " + new String(arg2));
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
                                        getFreeCreditsInformation();
                                    }
                                })
                        .setNegativeButton("No",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(
                                            DialogInterface dialog,
                                            int id) {
                                        dialog.cancel();
                                        ((ActivityLanding)getActivity()).gotoHome();

                                    }
                                });

                // create alert dialog
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
        });
    }

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnMenu:
			ActivityLanding parent = (ActivityLanding) getActivity();
			parent.menuClick();

			break;

        case R.id.share_fb:
            ShareLinkContent content = new ShareLinkContent.Builder()
                    .setContentTitle(fbShareTitle)
                    .setContentDescription(fbShareText)
                    .setContentUrl(Uri.parse(promoUrl))
                    .build();
            ShareDialog.show(this, content);
            break;

        case R.id.share_twitter:
            try {
                Uri uriBuilder = Uri.parse(promoUrl).buildUpon().build();

                TweetComposer.Builder builder = new TweetComposer.Builder(getActivity())
                        .text(twitterShareText)
                        .url(new URL(uriBuilder.toString()));
//                builder.show();
                Intent intent = builder.createIntent();
                startActivityForResult(intent, 100);
            } catch (IOException e) {
                Log.e(TAG, "MalformedURLException", e);
            }
            break;

        case R.id.share_txt:
            Intent sendIntent = new Intent(Intent.ACTION_VIEW);
            sendIntent.putExtra("sms_body", smsBody);
            sendIntent.setType("vnd.android-dir/mms-sms");
            startActivity(sendIntent);
            break;

        case R.id.share_email:
            Intent emailIntent = new Intent(Intent.ACTION_SEND);

            emailIntent.setData(Uri.parse("mailto:"));
            emailIntent.setType("text/plain");
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, emailSubject);
            emailIntent.putExtra(Intent.EXTRA_TEXT, emailBody);

            startActivity(emailIntent);
            break;
        }
	}

	

}
