package com.pa.landing;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pa.common.Config;
import com.pa.common.MyFragment;
import com.pa.common.OnFragmentChangeListener;
import com.pa.deals.PackageFragment;
import com.pa.job.FragmentPostOpenBid;
import com.pa.pojo.ServiceCategory;
import com.coolfindservices.androidconsumer.R;

import java.util.ArrayList;

/**
 * Created by VintedgeMac on 27/09/2016.
 */

//@SuppressLint("ValidFragment")
public class FragmentCategoryTab extends MyFragment implements View.OnClickListener, Config {

    OnFragmentChangeListener listener;

    private TextView packageTab, quoteTab;
    private boolean isPackage;

    String serviceCountry, serviceName, serviceID;
    private ArrayList<ServiceCategory> serviceCategoryArrayList;
    //  mandatory empty constructor
    public FragmentCategoryTab(){}

    @SuppressLint("ValidFragment")
    public FragmentCategoryTab(String ServiceName, String ServiceId, String country, ArrayList<ServiceCategory> arrayList) {
//        initial(arr, country, "", "", "", "");
        serviceName = ServiceName;
        serviceID = ServiceId;
        serviceCountry = country;
        serviceCategoryArrayList = arrayList;
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

        try {
            //  default view (packages)
            replaceFragment(R.id.tabContainer, PackageFragment.newInstance(serviceID, serviceCountry, true), true);
            //  set tab state
//            tabState(isPackage);
            isPackage = true;
        }catch(Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.fragment_category_tab, null);
        packageTab  = (TextView) v.findViewById(R.id.package_Tab);
        quoteTab    = (TextView) v.findViewById(R.id.quote_Tab);

        packageTab.setOnClickListener(this);
        quoteTab.setOnClickListener(this);
            //  set tab state
            tabState(isPackage);
            onReturnView(isPackage);
        return v;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode,
                                 android.content.Intent data) {
        try {
            super.onActivityResult(requestCode, resultCode, data); //simpleToast("requestcode: " + requestCode);
//            simpleToast("FragmentCategoryTab\nrequestCode: " + requestCode + "\ngetTargetRequestCode: " + getTargetRequestCode());
            Fragment f = fm.findFragmentById(R.id.tabContainer);
            f.onActivityResult(requestCode, resultCode, data);
        }catch(Exception e){
            e.printStackTrace();
        }

    }

    private void tabState(boolean isPackageState){
        if(isPackageState){
            packageTab.setBackgroundColor(getResources().getColor(R.color.pa_orange));
            packageTab.setTextColor(getResources().getColor(R.color.white));

            quoteTab.setBackgroundColor(getResources().getColor(R.color.white));
            quoteTab.setTextColor(getResources().getColor(R.color.pa_orange));
        }else{
            packageTab.setBackgroundColor(getResources().getColor(R.color.white));
            packageTab.setTextColor(getResources().getColor(R.color.pa_orange));

            quoteTab.setBackgroundColor(getResources().getColor(R.color.pa_orange));
            quoteTab.setTextColor(getResources().getColor(R.color.white));
        }
    }

    private void swapContent(){

    }



//    @Override
//    public void onDestroyView() {
//        super.onDestroyView();
//        mTabHost = null;
//    }

    private void onReturnView(boolean b){
        if(b){      //  default package tab is on, do nothing

        }else{      //  show quote tab
            replaceFragment(R.id.tabContainer, new FragmentPostOpenBid(serviceCategoryArrayList, serviceCountry), true);
        }
    }

    private void simulatePackageTap(){
        if(!isPackage) {
            isPackage = true;
            tabState(isPackage);
            fm.popBackStack();
        }
    }

    public void simulatequoteTap(){
        if(isPackage) {
            isPackage = false;
            tabState(isPackage);
            try {
                replaceFragment(R.id.tabContainer, new FragmentPostOpenBid(serviceCategoryArrayList, serviceCountry), true);
//                        listener.doFragmentChange(new FragmentPostOpenBid(serviceCategoryArrayList, serviceCountry), true, "");
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onClick(final View v) {
        switch(v.getId()){
            case R.id.package_Tab:
                simulatePackageTap();
                break;

            case R.id.quote_Tab:
                simulatequoteTap();
                break;
        }
    }
}
