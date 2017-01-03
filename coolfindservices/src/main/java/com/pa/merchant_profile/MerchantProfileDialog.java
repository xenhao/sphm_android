package com.pa.merchant_profile;

import android.app.Dialog;
import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.pa.pojo.MerchantProfile;
import com.pa.pojo.MerchantReview;
import com.coolfindservices.androidconsumer.R;

import java.util.ArrayList;

/**
 * Created by lenovo on 27-Dec-16.
 */

public class MerchantProfileDialog extends Dialog implements View.OnClickListener{

    private Dialog mMerchantDialog;
    private RecyclerView profileView;
    private ArrayList<MerchantProfileData> allData;

    private MerchantProfile merchantProfile;
    private ArrayList<String> arr_image;
    private ArrayList<MerchantReview> arr_review;

    public MerchantProfileDialog(Context context, MerchantProfile mMerchantProfile, ArrayList<String> arr_image, ArrayList<MerchantReview> arr_review) {
        super(context);

        View v = LayoutInflater.from(context).inflate(R.layout.dialog_merch_profile_new, null);
        v.findViewById(R.id.btnBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMerchantDialog.dismiss();
            }
        });

        this.merchantProfile = mMerchantProfile;
        this.arr_image = arr_image;
        this.arr_review = arr_review;

        //  dialog styling & setup
        mMerchantDialog = new Dialog(getContext(), R.style.PauseDialog);

        mMerchantDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        mMerchantDialog.getWindow().setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT);
        mMerchantDialog.setContentView(v);
        mMerchantDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        allData = new ArrayList<MerchantProfileData>();

        //  create view data
        createData();

        //  setup recycler view
        profileView = (RecyclerView) mMerchantDialog.findViewById(R.id.merchant_profile_view);

        profileView.setHasFixedSize(true);

        MerchantDataAdapter adapter = new MerchantDataAdapter(getContext(), allData);

        profileView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

        profileView.setAdapter(adapter);

        //  dialog execution
        mMerchantDialog.show();

    }

    private void createData(){
        final int dataSection = 4;
        for (int i = 1; i <= dataSection; i++) {

            MerchantProfileData profileData = new MerchantProfileData();

            profileData.setHeaderTitle("Section " + i);
            profileData.setViewType(String.valueOf(i));

            switch(i){
                case 1:
                    profileData.cover_photo     = merchantProfile.cover_photo;
                    profileData.rating_count    = merchantProfile.rating_count;
                    profileData.merchant_name   = merchantProfile.merchant_name;
                    profileData.overall_rating  = merchantProfile.overall_rating;
                    break;

                case 2:
                    profileData.about           = merchantProfile.about;
                    profileData.service_list    = merchantProfile.service_list;
                    break;

                case 3:
                    profileData.reviews     = merchantProfile.reviews;
                    break;

                case 4:
                    profileData.gallery_image   = merchantProfile.gallery_image;
                    break;
            }



            allData.add(profileData);

        }
    }

    @Override
    public void onClick(View v) {

    }
}
