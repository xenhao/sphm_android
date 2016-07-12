package com.pa.merchantlist;

import java.util.ArrayList;

import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.pa.common.Config;
import com.pa.common.MyFragment;
import com.pa.common.OnFragmentChangeListener;
import com.pa.common.PARestClient;
import com.pa.common.Tracer;
import com.pa.job.FragmentPostOpenBid;
import com.pa.pojo.Merchant;
import com.pa.pojo.ParentServiceCategory;
import com.pa.pojo.ServiceCategory;
import com.pa.quick_action_dialog.ActionItem;
import com.pa.quick_action_dialog.QuickAction;
import com.coolfindservices.androidconsumer.R;

@SuppressLint("ValidFragment")
public class FragmentMerchantDetail extends MyFragment implements Config,
		OnClickListener {
	Merchant merchant;
	OnFragmentChangeListener listener;

	TextView txtName, txtService, txtAddress;
	
	LinearLayout wrapperVerified;
	View learnMore;
	LinearLayout wrapperAbout,wrapperBranch,wrapperGallery;
	RatingBar ratingBar;
	
	TextView txtAbout;
	ImageView btnFav;
	QuickAction mQuickAction;
	
	public FragmentMerchantDetail(){
		
	}
	
	public FragmentMerchantDetail(Merchant merchant) {
		this.merchant = merchant;
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
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		try {
			txtAbout.setText(merchant.co_about);
			txtName.setText(merchant.co_name);
			txtAddress.setText(merchant.co_address_1);
			txtService.setText(merchant.bulk_children_array.replace("[", "")
					.replace("]", "").replace("\"", ""));
			ratingBar.setRating(Float.parseFloat(merchant.co_overall_rating));

			
			
			if(merchant.isVerified()){
				wrapperVerified.setVisibility(View.VISIBLE);
			}else{
				wrapperVerified.setVisibility(View.GONE);

			}

			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		OnClickListener accordionListener=
				new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				View parent = (View) v.getParent();
				View content = parent
						.findViewById(R.id.categoryWrapper);
				TextView t = (TextView) v;

				if (content.getVisibility() == View.VISIBLE) {
					content.setVisibility(View.GONE);
					t.setCompoundDrawablesWithIntrinsicBounds(
							null,
							null,
							getResources().getDrawable(
									R.drawable.icon_arrow_down), null);
				} else {
					content.setVisibility(View.VISIBLE);
					t.setCompoundDrawablesWithIntrinsicBounds(
							null,
							null,
							getResources().getDrawable(
									R.drawable.icon_arrow_up), null);

				}
			}
		};
		
		wrapperAbout.findViewById(R.id.catParent).setOnClickListener(accordionListener);
		wrapperBranch.findViewById(R.id.catParent).setOnClickListener(accordionListener);
		wrapperGallery.findViewById(R.id.catParent).setOnClickListener(accordionListener);

		btnFav.setOnClickListener(this);
		if(merchant.isFavourite()){
			btnFav.setImageResource(R.drawable.fav_on);
		}else{
			btnFav.setImageResource(R.drawable.fav_off);
			
		}
		
		ActionItem addItem 		= new ActionItem(1, "Add", getActivity().getResources().getDrawable(R.drawable.icon_add_photo));
		mQuickAction 	= new QuickAction(getActivity());
		
		mQuickAction.addActionItem(addItem);
		
	}

	void toggleFav(){
		String is_favourite="";
		if(merchant.isFavourite()){
			is_favourite="N";
		}else{
			is_favourite="Y";
		}

		loadingInternetDialog.show();
		//AsyncHttpClient client=new AsyncHttpClient();
		RequestParams params=new RequestParams();
		params.add("session_username",pref.getPref(PREF_USERNAME));
		params.add("active_session_token",pref.getPref(PREF_ACTIVE_SESSION_TOKEN));
		params.add("merchant_username", merchant.username);
		params.add("is_favourite",is_favourite);
		
		PARestClient.post(pref.getPref(Config.SERVER),API_SET_FAVE_MERCHANT, params,new AsyncHttpResponseHandler(){
			@Override
			public void onFailure(int statusCode, Throwable error,
					String content) {
				// TODO Auto-generated method stub
				super.onFailure(statusCode, error, content);
				
				loadingInternetDialog.dismiss();
			}
			
			@Override
			public void onSuccess(int statusCode, String content) {
				// TODO Auto-generated method stub
				super.onSuccess(statusCode, content);
				try{
					JSONObject obj=new JSONObject(content);
					if("success".equals(obj.getString("status"))){
						if(merchant.isFavourite()){
							merchant.is_favourite="N";
							btnFav.setImageResource(R.drawable.fav_off);
						}else{
							merchant.is_favourite="Y";
							btnFav.setImageResource(R.drawable.fav_on);

						}

					}
				}catch(Exception e){
					e.printStackTrace();
				}
				loadingInternetDialog.dismiss();
			}
		});
		

	}
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View v = inflater.inflate(R.layout.fragment_merchant_detail, null);

		v.findViewById(R.id.btnCloseBid).setOnClickListener(this);
		v.findViewById(R.id.btnOpenBid).setOnClickListener(this);
		v.findViewById(R.id.btnBack).setOnClickListener(this);

		txtName = (TextView) v.findViewById(R.id.txtName);
		txtService = (TextView) v.findViewById(R.id.txtCatService);
		txtAddress = (TextView) v.findViewById(R.id.txtAddr);

		wrapperBranch=(LinearLayout)v.findViewById(R.id.wrapper_branch);
		wrapperGallery=(LinearLayout)v.findViewById(R.id.wrapper_image_gallery);
		wrapperAbout=(LinearLayout)v.findViewById(R.id.wrapper_about);
		
		ratingBar=(RatingBar)v.findViewById(R.id.ratingBar1);
		txtAbout=(TextView)v.findViewById(R.id.txt_about);
		btnFav=(ImageView)v.findViewById(R.id.btnFav);
		
		learnMore=v.findViewById(R.id.learn_more);
		learnMore.setOnClickListener(this);
		
		wrapperVerified=(LinearLayout)v.findViewById(R.id.wrapper_verified);

		return v;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.learn_more:
			//open quick action setting
			mQuickAction.setRootViewId(R.layout.quickaction_for_verified_badge);
			mQuickAction.show(v);
			mQuickAction.setAnimStyle(QuickAction.ANIM_GROW_FROM_CENTER);
			
			
			break;
		case R.id.btnFav:
			toggleFav();
			break;
		case R.id.btnBack:
			getActivity().getSupportFragmentManager().popBackStack();

			break;
		case R.id.btnOpenBid: {

			ArrayList<ServiceCategory> arr = new ArrayList<ServiceCategory>();
			Tracer.d("Open Bid");
			for (int i = 0; i < merchant.arrService.size(); i++) {
				ParentServiceCategory item = merchant.arrService.get(i);
				Tracer.d(item.service_name);

				ServiceCategory sc = new ServiceCategory();
				sc.id = item.id;
				sc.service_name = item.service_name;
				arr.add(sc);
			}

			listener.doFragmentChange(new FragmentPostOpenBid(arr, true, 2),
					true, "");

		}
			break;
		case R.id.btnCloseBid: {
			ArrayList<ServiceCategory> arr = new ArrayList<ServiceCategory>();
			Tracer.d("Open Bid");
			for (int i = 0; i < merchant.arrService.size(); i++) {
				ParentServiceCategory item = merchant.arrService.get(i);
				Tracer.d(item.service_name);

				ServiceCategory sc = new ServiceCategory();
				sc.id = item.id;
				sc.service_name = item.service_name;
				arr.add(sc);

			}

			FragmentPostOpenBid f = new FragmentPostOpenBid(arr, false, 2);
			f.setMerchant(merchant);
			// listener.doFragmentChange(new FragmentPostOpenBid(arr,false,2),
			// true, "");
			listener.doFragmentChange(new FragmentPostOpenBid(merchant, false,
					2), true, "");

		}
			break;

		}

	}
	
}
