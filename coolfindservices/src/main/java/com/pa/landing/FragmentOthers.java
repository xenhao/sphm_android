package com.pa.landing;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pa.common.Config;
import com.pa.common.GlobalVar;
import com.pa.common.MyFragment;
import com.coolfindservices.android.SplashActivity;
import com.coolfindservices.androidconsumer.R;

/**
 * Created by lenovo on 10/24/2016.
 */

public class FragmentOthers extends MyFragment implements View.OnClickListener{

    TextView txtUsername,txtEmail;
    TextView version;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.fragment_others, container, false);

//        ((ActivityLanding)getActivity()).checkVersionData();

        version=(TextView) v.findViewById(R.id.version);

        try
        {
            String app_ver = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0).versionName;
            version.setText("Version:\n"+app_ver);

        }
        catch (PackageManager.NameNotFoundException e)
        {
            // Log.e(tag, e.getMessage());
//            simpleToast("version number error: " + e.getMessage() + "\n" + e);
            e.printStackTrace();
        }

        setupPage(v);

        return v;
    }

    private void setupPage(View v){
        if(GlobalVar.isGuest){
//            v.findViewById(R.id.menu_jobs_list).setOnClickListener(this);
            v.findViewById(R.id.menu_login).setOnClickListener(this);
            //	hide all unneeded for guest mode
            v.findViewById(R.id.menu_about).setVisibility(View.GONE);
            v.findViewById(R.id.menu_help).setVisibility(View.GONE);
            v.findViewById(R.id.menu_completed_jobs).setVisibility(View.GONE);
            v.findViewById(R.id.menu_settings).setVisibility(View.GONE);
            v.findViewById(R.id.menu_free_credit).setVisibility(View.GONE);
            v.findViewById(R.id.menu_logout).setVisibility(View.GONE);
            v.findViewById(R.id.menu_chat).setVisibility(View.GONE);
            v.findViewById(R.id.menu_faq).setVisibility(View.GONE);
            v.findViewById(R.id.menu_privacy).setVisibility(View.GONE);
            v.findViewById(R.id.menu_terms).setVisibility(View.GONE);
            v.findViewById(R.id.menu_contact_us).setVisibility(View.GONE);

            v.findViewById(R.id.line4).setVisibility(View.GONE);
            v.findViewById(R.id.line5).setVisibility(View.GONE);
            v.findViewById(R.id.line6).setVisibility(View.GONE);
            v.findViewById(R.id.line7).setVisibility(View.GONE);
            v.findViewById(R.id.line8).setVisibility(View.GONE);
            v.findViewById(R.id.line81).setVisibility(View.GONE);
            v.findViewById(R.id.line82).setVisibility(View.GONE);
            v.findViewById(R.id.line9).setVisibility(View.GONE);
            v.findViewById(R.id.line10).setVisibility(View.GONE);

            txtUsername = (TextView) v.findViewById(R.id.username);
            txtUsername.setText("Guest");
        }else {
//			mDrawerList.findViewById(R.id.menu_about).setVisibility(View.VISIBLE);
//			mDrawerList.findViewById(R.id.menu_help).setVisibility(View.VISIBLE);
            v.findViewById(R.id.menu_completed_jobs).setVisibility(View.VISIBLE);
            v.findViewById(R.id.menu_settings).setVisibility(View.VISIBLE);
            v.findViewById(R.id.menu_free_credit).setVisibility(View.VISIBLE);
            v.findViewById(R.id.menu_logout).setVisibility(View.VISIBLE);
            v.findViewById(R.id.menu_chat).setVisibility(View.VISIBLE);
            v.findViewById(R.id.menu_faq).setVisibility(View.VISIBLE);
            v.findViewById(R.id.menu_privacy).setVisibility(View.VISIBLE);
            v.findViewById(R.id.menu_terms).setVisibility(View.VISIBLE);
            v.findViewById(R.id.menu_contact_us).setVisibility(View.VISIBLE);

            v.findViewById(R.id.line4).setVisibility(View.VISIBLE);
            v.findViewById(R.id.line5).setVisibility(View.VISIBLE);
            v.findViewById(R.id.line6).setVisibility(View.VISIBLE);
            v.findViewById(R.id.line7).setVisibility(View.VISIBLE);
            v.findViewById(R.id.line8).setVisibility(View.VISIBLE);
            v.findViewById(R.id.line81).setVisibility(View.VISIBLE);
            v.findViewById(R.id.line82).setVisibility(View.VISIBLE);
            v.findViewById(R.id.line9).setVisibility(View.VISIBLE);
            v.findViewById(R.id.line10).setVisibility(View.VISIBLE);

            v.findViewById(R.id.menu_login).setVisibility(View.GONE);

            v.findViewById(R.id.menu_about).setOnClickListener(this);
            v.findViewById(R.id.menu_help).setOnClickListener(this);
            v.findViewById(R.id.menu_completed_jobs)
                    .setOnClickListener(this);

            v.findViewById(R.id.menu_settings).setOnClickListener(
                    this);

            v.findViewById(R.id.menu_free_credit).setOnClickListener(
                    this);

            v.findViewById(R.id.menu_logout).setOnClickListener(this);
            v.findViewById(R.id.menu_chat).setOnClickListener(this);
            v.findViewById(R.id.menu_faq).setOnClickListener(this);
            v.findViewById(R.id.menu_privacy).setOnClickListener(this);
            v.findViewById(R.id.menu_terms).setOnClickListener(this);
            v.findViewById(R.id.menu_contact_us).setOnClickListener(this);

            txtEmail = (TextView) v.findViewById(R.id.email);
            txtEmail.setText(pref.getPref(Config.PREF_USERNAME));
            txtUsername = (TextView) v.findViewById(R.id.username);
            txtUsername.setText(((ActivityLanding)getActivity()).username4Fragment);
//            getUserData();
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        analytic.trackScreen("Others Page");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.menu_completed_jobs:
                ((ActivityLanding)getActivity()).selectItem(R.id.menu_completed_jobs);
                break;
            case R.id.menu_history:
                ((ActivityLanding)getActivity()).selectItem(3);
                break;
            case R.id.menu_settings:
                ((ActivityLanding)getActivity()).selectItem(4);
                break;
            case R.id.menu_help:
                ((ActivityLanding)getActivity()).selectItem(5);
                break;
            case R.id.menu_about:
                ((ActivityLanding)getActivity()).selectItem(6);
                break;
            case R.id.menu_contact_us:
                ((ActivityLanding)getActivity()).selectItem(8);
                break;
            case R.id.menu_logout:
                GlobalVar.isGuest = false;
                GlobalVar.isResumeGuest = false;
                ((ActivityLanding)getActivity()).logout();
                startActivity(new Intent(getActivity(), SplashActivity.class));
                getActivity().finish();
                break;
            case R.id.profile_pic:
                ((ActivityLanding)getActivity()).selectItem(7);
                break;

            case R.id.menu_free_credit:
                ((ActivityLanding)getActivity()).selectItem(R.id.menu_free_credit);
                break;

            case R.id.menu_login:
                Intent intent = new Intent(getActivity(), SplashActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                GlobalVar.isResumeGuest = true;
                startActivity(intent);
                break;

            case R.id.menu_faq:
            case R.id.menu_chat:
                ((ActivityLanding)getActivity()).selectItem(v.getId());
                break;

            case R.id.menu_privacy:
                showWebDialog("http://services.cool-find.com/privacy-policy");
                break;

            case R.id.menu_terms:
                showWebDialog("http://services.cool-find.com/terms-of-use");
                break;
        }
    }

    @Override
    public void onResume(){
        super.onResume();
        setupPage(getView());
    }
}
