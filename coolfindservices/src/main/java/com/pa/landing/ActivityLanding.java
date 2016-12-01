package com.pa.landing;

import org.apache.http.Header;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.facebook.appevents.AppEventsLogger;
import com.hipmob.android.HipmobCore;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.pa.bid.FragmentBid;
import com.pa.bid.FragmentBidDetail;
import com.pa.common.Config;
import com.pa.common.GlobalVar;
import com.pa.common.ImageHelper;
import com.pa.common.MyActivity;
import com.pa.common.OnFragmentChangeListener;
import com.pa.common.PARestClient;
import com.pa.common.ProfilUtils;
import com.pa.common.Tracer;
import com.pa.contactus.FragmentContactUs;
import com.pa.free_credit.FragmentEarnFreeCredits;
import com.pa.job.FragmentPostOpenBid;
import com.pa.order.FragmentOrder;
import com.pa.parser.ParserAppVersion;
import com.pa.parser.ParserUserORM;
import com.pa.pojo.AppVersion;
import com.pa.pojo.UserORM;
import com.pa.profil_new.FragmentProfilLandingNew;
import com.coolfindservices.android.SplashActivity;
import com.coolfindservices.androidconsumer.R;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabReselectListener;
import com.roughike.bottombar.OnTabSelectListener;

import java.util.HashMap;
import java.util.Map;

import io.intercom.android.sdk.Intercom;

//we hide the action bar since it need API 11, if we can increase the API support to >11, we can display this

public class ActivityLanding extends MyActivity implements OnClickListener,
		OnFragmentChangeListener {
	private DrawerLayout mDrawerLayout;
	private View mDrawerList;
	private ActionBarDrawerToggle mDrawerToggle;

	private CharSequence mDrawerTitle;
	private CharSequence mTitle;
	private String[] mMenuTitles = { "1" };

	private Button btnSideMenu;
	ImageView profilePic;
	TextView txtUsername,txtEmail;
	TextView version;
	public String username4Fragment = "";

	private BottomBar bottomBar;


	void checkVersionData(){
		RequestParams params=new RequestParams();
		params.add("type","android");


		PARestClient.get(pref.getPref(Config.SERVER),
				Config.API_CEK_APP_VERSION, params,
				new AsyncHttpResponseHandler() {
					@Override
					public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
						// TODO Auto-generated method stub
						super.onSuccess(arg0, arg1, arg2);

						ParserAppVersion parser = new ParserAppVersion(
								new String(arg2));
						AppVersion appV = parser.getData();

						try {
							double version=getPackageManager()
									.getPackageInfo(getPackageName(), 0).versionCode;
							if (Double.parseDouble(appV.customer_version_android) > version) {
//								AlertDialog.Builder builder=new AlertDialog.Builder(ActivityLanding.this);
//
//								builder.setTitle("Warning");
//								builder.setMessage("New version available. Please update to continue using PA Merchant App.");
//								builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
//
//									@Override
//									public void onClick(DialogInterface dialog, int which) {
//										// TODO Auto-generated method stub
//										if("0".equals(pref.getPref(Config.SERVER))){
//											openUrl("http://bit.ly/pa_merc_staging");
//										}else{
//											openUrl("http://bit.ly/pa_merc");
//										}
//										SplashActivity.this.finish();
//									}
//								});
//
//
//								builder.show();

								showUpdateDialog();


							} 						} catch (NameNotFoundException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

					}

					@Override
					public void onStart() {
						// TODO Auto-generated method stub
						super.onStart();
						loadingInternetDialog.show();
					}

					@Override
					public void onFinish() {
						// TODO Auto-generated method stub
						super.onFinish();
						loadingInternetDialog.dismiss();
					}
				});

	}

	Dialog dialogUpdate;
	void showUpdateDialog(){
		View v=inflater.inflate(R.layout.activity_splash, null);

		v.findViewById(R.id.btnUpdate).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if("2".equals(getResources().getString(R.string.server))){
//					openUrl(PARestClient.getAbsoluteUrl(pref.getPref(Config.SERVER), "home/update-app?platform=android"));
					openUrl("https://play.google.com/store/apps/details?id=com.pageadvisor.androidconsumer");
				}
				else
				if(PARestClient.getAbsoluteUrl(pref.getPref(Config.SERVER), "").contains(Config.DOMAIN_DEV)){
					openUrl("http://bit.ly/pa_dev");
				}
				else{
					openUrl("http://bit.ly/pa_staging");
				}

			}
		});

		dialogUpdate = new Dialog(this, R.style.PauseDialog);

		dialogUpdate.setCancelable(true);
		dialogUpdate.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		dialogUpdate.getWindow().setLayout(
				WindowManager.LayoutParams.MATCH_PARENT,
				WindowManager.LayoutParams.MATCH_PARENT);
		dialogUpdate.setContentView(v);
		dialogUpdate.getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

		dialogUpdate.show();
	}

	/**
	 * 	Edited on 30 June 2016 for data caching
	 * 	getting rid of over reliance on calling data from backend
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub

		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {

			if (fm.findFragmentById(R.id.content_frame) instanceof FragmentRevisedLanding/*FragmentNewLanding*/
					|| fm.findFragmentById(R.id.content_frame) instanceof FragmentBid
//					|| fm.findFragmentById(R.id.content_frame) instanceof FragmentOrder
					|| fm.findFragmentById(R.id.content_frame) instanceof FragmentOthers
					/*|| fm.findFragmentById(R.id.content_frame) instanceof  FragmentOthers*/) {
				Tracer.d("Exit1");
				displayExitDialog();
			} else if (fm.findFragmentById(R.id.content_frame) instanceof FragmentBidDetail) {
				FragmentBidDetail fragment = (FragmentBidDetail) fm
						.findFragmentById(R.id.content_frame);
				fragment.pageBack();

			} else if (fm.findFragmentById(R.id.content_frame) instanceof FragmentPostOpenBid) {
				// displayExitFormDialog();

				FragmentPostOpenBid fragment = (FragmentPostOpenBid) fm
						.findFragmentById(R.id.content_frame);
				fragment.pageBack();
			} 	//	edit for data caching
			else if (/*fm.findFragmentById(R.id.content_frame) instanceof FragmentBid*/
//					|| fm.findFragmentById(R.id.content_frame) instanceof FragmentOrder
//					|| fm.findFragmentById(R.id.content_frame) instanceof FragmentProfilLandingNew
//					|| fm.findFragmentById(R.id.content_frame) instanceof FragmentContactUs
//					|| fm.findFragmentById(R.id.content_frame) instanceof FragmentEarnFreeCredits
					/*||*/ fm.findFragmentById(R.id.content_frame) instanceof FragmentCategoryTab
					/*|| fm.findFragmentById(R.id.content_frame) instanceof FragmentOthers*/) {
				for(int i = this.getSupportFragmentManager().getBackStackEntryCount(); i > 1; i--)
					this.getSupportFragmentManager().popBackStackImmediate();
				bottomBar.selectTabAtPosition(0);
			} else if(fm.findFragmentById(R.id.content_frame) instanceof FragmentProfilLandingNew
					|| fm.findFragmentById(R.id.content_frame) instanceof FragmentContactUs
					|| fm.findFragmentById(R.id.content_frame) instanceof FragmentEarnFreeCredits
					/*|| fm.findFragmentById(R.id.content_frame) instanceof FragmentCategoryTab*/){
				this.getSupportFragmentManager().popBackStackImmediate();
				bottomBar.selectTabAtPosition(3);
			} else if(fm.findFragmentById(R.id.content_frame) instanceof FragmentOrder){
				FragmentOrder fragment = (FragmentOrder) fm
						.findFragmentById(R.id.content_frame);
				if(fragment.getTitle().equalsIgnoreCase("JOB HISTORY")){
					//	go back to others setting page
					this.getSupportFragmentManager().popBackStackImmediate();
					bottomBar.selectTabAtPosition(3);
				}else{	//	fragment title is "APPOINTMENT LIST"
					//	show exit dialog as this is now same level as homepage
					Tracer.d("Exit1");
					displayExitDialog();
				}
			} else {

				if (!back(R.id.content_frame)) {
					Tracer.d("Exit2");

					displayExitDialog();
				}
			}

			return true;
		}

		return super.onKeyDown(keyCode, event);
	}

	public void setBottomBar(int iconIndex){
		bottomBar.selectTabAtPosition(iconIndex);
	}

	public void showBottomBar(boolean show){
		bottomBar.setVisibility(show ? View.VISIBLE : View.GONE);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		try {
			setContentView(R.layout.landing);
//			findViewById(R.id.menu).setOnClickListener(this);

			mTitle = mDrawerTitle = getTitle();
			mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
			mDrawerList = findViewById(R.id.left_drawer);
			// set a custom shadow that overlays the main content when the
			// drawer
			// opens
			mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow,
					GravityCompat.START);
			mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
			// set up the drawer's list view with items and click listener

			// enable ActionBar app icon to behave as action to toggle nav
			// drawer
			// getActionBar().setDisplayHomeAsUpEnabled(true);
			// getActionBar().setHomeButtonEnabled(true);

			// ActionBarDrawerToggle ties together the the proper interactions
			// between the sliding drawer and the action bar app icon
			mDrawerToggle = new ActionBarDrawerToggle(this, /* host Activity */
					mDrawerLayout, /* DrawerLayout object */
					R.drawable.ic_launcher, /* nav drawer image to replace 'Up' caret */
					R.string.drawer_open, /* "open drawer" description for accessibility */
					R.string.drawer_close /* "close drawer" description for accessibility */
			) {
				public void onDrawerClosed(View view) {
					// getActionBar().setTitle(mTitle);
					// invalidateOptionsMenu(); // creates call to
					// onPrepareOptionsMenu()

				}

				public void onDrawerOpened(View drawerView) {
					// getActionBar().setTitle(mDrawerTitle);
					// invalidateOptionsMenu(); // creates call to
					// onPrepareOptionsMenu()
					// invalidateOptionsMenu();
				}
			};
			mDrawerLayout.setDrawerListener(mDrawerToggle);

			mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

			setMenu();

//			if(GlobalVar.isGuest){
//				mDrawerList.findViewById(R.id.menu_jobs_list).setOnClickListener(this);
//				mDrawerList.findViewById(R.id.menu_login).setOnClickListener(this);
//				//	hide all unneeded for guest mode
//				mDrawerList.findViewById(R.id.menu_about).setVisibility(View.GONE);
//				mDrawerList.findViewById(R.id.menu_bid).setVisibility(View.GONE);
//				mDrawerList.findViewById(R.id.menu_help).setVisibility(View.GONE);
//				mDrawerList.findViewById(R.id.menu_history).setVisibility(View.GONE);
//				mDrawerList.findViewById(R.id.menu_quotation).setVisibility(View.GONE);
//				mDrawerList.findViewById(R.id.menu_completed_jobs).setVisibility(View.GONE);
//				mDrawerList.findViewById(R.id.menu_settings).setVisibility(View.GONE);
//				mDrawerList.findViewById(R.id.menu_free_credit).setVisibility(View.GONE);
//				mDrawerList.findViewById(R.id.menu_logout).setVisibility(View.GONE);
//				mDrawerList.findViewById(R.id.menu_chat).setVisibility(View.GONE);
//				mDrawerList.findViewById(R.id.menu_faq).setVisibility(View.GONE);
//				mDrawerList.findViewById(R.id.menu_contact_us).setVisibility(View.GONE);
//
//				txtUsername = (TextView) findViewById(R.id.username);
//				txtUsername.setText("Guest");
//			}else {
//				mDrawerList.findViewById(R.id.menu_about).setOnClickListener(this);
//				mDrawerList.findViewById(R.id.menu_bid).setOnClickListener(this);
//				mDrawerList.findViewById(R.id.menu_help).setOnClickListener(this);
//				mDrawerList.findViewById(R.id.menu_history)
//						.setOnClickListener(this);
//				mDrawerList.findViewById(R.id.menu_jobs_list).setOnClickListener(
//						this);
//				mDrawerList.findViewById(R.id.menu_quotation).setOnClickListener(
//						this);
//				mDrawerList.findViewById(R.id.menu_completed_jobs)
//						.setOnClickListener(this);
//
//				mDrawerList.findViewById(R.id.menu_settings).setOnClickListener(
//						this);
//
//				mDrawerList.findViewById(R.id.menu_free_credit).setOnClickListener(
//						this);
//
//				mDrawerList.findViewById(R.id.menu_logout).setOnClickListener(this);
//				mDrawerList.findViewById(R.id.menu_chat).setOnClickListener(this);
//				mDrawerList.findViewById(R.id.menu_faq).setOnClickListener(this);
//				mDrawerList.findViewById(R.id.menu_contact_us).setOnClickListener(this);
//
//				txtEmail = (TextView) findViewById(R.id.email);
//				txtEmail.setText(pref.getPref(Config.PREF_USERNAME));
//				getUserData();
//			}

			findViewById(R.id.profile_pic).setOnClickListener(this);
			profilePic = (ImageView) findViewById(R.id.profile_pic);

			profilePic.setImageBitmap(new ImageHelper()
					.getCircleBitmap(new BitmapFactory().decodeResource(
							getResources(), R.drawable.default_profile)));

			//	display home page
//			if (savedInstanceState == null) {
//				selectItem(0);		Log.i("getList Log", "display home called");
//			}

			//	setup bottom navigation
			bottomBar = (BottomBar) findViewById(R.id.bottomBar);
			bottomBar.setOnTabSelectListener(new OnTabSelectListener() {
				@Override
				public void onTabSelected(@IdRes int tabId) {
					switch(tabId){
						case (R.id.tab_home):
							selectItem(0);		Log.i("getList Log", "display home called");
							break;
						case (R.id.tab_jobrequest):
							selectItem(1);
							break;
						case (R.id.tab_appointments):
							selectItem(2);
							break;
						case (R.id.tab_others):
							selectItem(R.id.tab_others);
//							simpleToast("display all others options");
							break;
					}
				}
			});

			//	enable bottom navigation reselect
			bottomBar.setOnTabReselectListener(new OnTabReselectListener() {
				@Override
				public void onTabReSelected(@IdRes int tabId) {
					switch(tabId){
						case (R.id.tab_home):
							//	trigger reselect only if no same page
							if (!(fm.findFragmentById(R.id.content_frame) instanceof FragmentRevisedLanding))
								selectItem(0);		Log.i("getList Log", "reselect display home called");
							break;
						case (R.id.tab_jobrequest):
							//	trigger reselect only if no same page
							if (!(fm.findFragmentById(R.id.content_frame) instanceof FragmentBid))
								selectItem(1);
							break;
						case (R.id.tab_appointments):
							//	trigger reselect only if not on the same page
							if (!(fm.findFragmentById(R.id.content_frame) instanceof FragmentOrder)) {
								if (((FragmentOrder) fm.findFragmentById(R.id.content_frame)).getTitle().equalsIgnoreCase("APPOINTMENT LIST"))
									selectItem(2);
							}
							break;
						case (R.id.tab_others):
							//	trigger reselect only if no same page
							if (!(fm.findFragmentById(R.id.content_frame) instanceof FragmentOthers))
								selectItem(R.id.tab_others);
//							simpleToast("display all others options");
							break;
					}
				}
			});




//			txtUsername = (TextView) findViewById(R.id.username);
//			txtEmail = (TextView) findViewById(R.id.email);
//			txtEmail.setText(pref.getPref(Config.PREF_USERNAME));
			version=(TextView)findViewById(R.id.version);

			try
			{
				String app_ver = this.getPackageManager().getPackageInfo(this.getPackageName(), 0).versionName;
				version.setText("Version:\n"+app_ver);

			}
			catch (NameNotFoundException e)
			{
				// Log.e(tag, e.getMessage());
			}
//			if(!GlobalVar.isGuest) {
//				txtEmail.setText(pref.getPref(Config.PREF_USERNAME));
//				getUserData();
//			}else{
//				txtUsername.setText("Guest");
//			}
		} catch (Exception e) {
			e.printStackTrace();
		}


//		if (savedInstanceState == null) {
//			selectItem(0);
//		}

		checkVersionData();

//        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
//            @Override
//            public void uncaughtException(Thread paramThread, Throwable paramThrowable) {
//                Log.e("Alert", "Lets See if it Works !!!");
//                Log.e("Alert", paramThrowable.getLocalizedMessage());
//            }
//        });
	}

	private void setMenu(){
		if(GlobalVar.isGuest){
//			mDrawerList.findViewById(R.id.menu_jobs_list).setOnClickListener(this);
//			mDrawerList.findViewById(R.id.menu_login).setOnClickListener(this);
//			//	hide all unneeded for guest mode
//			mDrawerList.findViewById(R.id.menu_about).setVisibility(View.GONE);
//			mDrawerList.findViewById(R.id.menu_bid).setVisibility(View.GONE);
//			mDrawerList.findViewById(R.id.menu_help).setVisibility(View.GONE);
//			mDrawerList.findViewById(R.id.menu_history).setVisibility(View.GONE);
//			mDrawerList.findViewById(R.id.menu_quotation).setVisibility(View.GONE);
//			mDrawerList.findViewById(R.id.menu_completed_jobs).setVisibility(View.GONE);
//			mDrawerList.findViewById(R.id.menu_settings).setVisibility(View.GONE);
//			mDrawerList.findViewById(R.id.menu_free_credit).setVisibility(View.GONE);
//			mDrawerList.findViewById(R.id.menu_logout).setVisibility(View.GONE);
//			mDrawerList.findViewById(R.id.menu_chat).setVisibility(View.GONE);
//			mDrawerList.findViewById(R.id.menu_faq).setVisibility(View.GONE);
//			mDrawerList.findViewById(R.id.menu_contact_us).setVisibility(View.GONE);
//
//			mDrawerList.findViewById(R.id.line2).setVisibility(View.GONE);
//			mDrawerList.findViewById(R.id.line3).setVisibility(View.GONE);
//			mDrawerList.findViewById(R.id.line4).setVisibility(View.GONE);
//			mDrawerList.findViewById(R.id.line5).setVisibility(View.GONE);
//			mDrawerList.findViewById(R.id.line6).setVisibility(View.GONE);
//			mDrawerList.findViewById(R.id.line7).setVisibility(View.GONE);
//			mDrawerList.findViewById(R.id.line8).setVisibility(View.GONE);
//			mDrawerList.findViewById(R.id.line9).setVisibility(View.GONE);
//			mDrawerList.findViewById(R.id.line10).setVisibility(View.GONE);

			txtUsername = (TextView) findViewById(R.id.username);
			txtUsername.setText("Guest");
		}else {
//			mDrawerList.findViewById(R.id.menu_bid).setVisibility(View.VISIBLE);
//			mDrawerList.findViewById(R.id.menu_quotation).setVisibility(View.VISIBLE);
//			mDrawerList.findViewById(R.id.menu_completed_jobs).setVisibility(View.VISIBLE);
//			mDrawerList.findViewById(R.id.menu_settings).setVisibility(View.VISIBLE);
//			mDrawerList.findViewById(R.id.menu_free_credit).setVisibility(View.VISIBLE);
//			mDrawerList.findViewById(R.id.menu_logout).setVisibility(View.VISIBLE);
//			mDrawerList.findViewById(R.id.menu_chat).setVisibility(View.VISIBLE);
//			mDrawerList.findViewById(R.id.menu_faq).setVisibility(View.VISIBLE);
//			mDrawerList.findViewById(R.id.menu_contact_us).setVisibility(View.VISIBLE);
//
//			mDrawerList.findViewById(R.id.line2).setVisibility(View.VISIBLE);
//			mDrawerList.findViewById(R.id.line3).setVisibility(View.VISIBLE);
//			mDrawerList.findViewById(R.id.line4).setVisibility(View.VISIBLE);
//			mDrawerList.findViewById(R.id.line5).setVisibility(View.VISIBLE);
//			mDrawerList.findViewById(R.id.line6).setVisibility(View.VISIBLE);
//			mDrawerList.findViewById(R.id.line7).setVisibility(View.VISIBLE);
//			mDrawerList.findViewById(R.id.line8).setVisibility(View.VISIBLE);
//			mDrawerList.findViewById(R.id.line9).setVisibility(View.VISIBLE);
//			mDrawerList.findViewById(R.id.line10).setVisibility(View.VISIBLE);
//
//			mDrawerList.findViewById(R.id.menu_login).setVisibility(View.GONE);
//
//			mDrawerList.findViewById(R.id.menu_about).setOnClickListener(this);
//			mDrawerList.findViewById(R.id.menu_bid).setOnClickListener(this);
//			mDrawerList.findViewById(R.id.menu_help).setOnClickListener(this);
//			mDrawerList.findViewById(R.id.menu_history)
//					.setOnClickListener(this);
//			mDrawerList.findViewById(R.id.menu_jobs_list).setOnClickListener(
//					this);
//			mDrawerList.findViewById(R.id.menu_quotation).setOnClickListener(
//					this);
//			mDrawerList.findViewById(R.id.menu_completed_jobs)
//					.setOnClickListener(this);
//
//			mDrawerList.findViewById(R.id.menu_settings).setOnClickListener(
//					this);
//
//			mDrawerList.findViewById(R.id.menu_free_credit).setOnClickListener(
//					this);
//
//			mDrawerList.findViewById(R.id.menu_logout).setOnClickListener(this);
//			mDrawerList.findViewById(R.id.menu_chat).setOnClickListener(this);
//			mDrawerList.findViewById(R.id.menu_faq).setOnClickListener(this);
//			mDrawerList.findViewById(R.id.menu_contact_us).setOnClickListener(this);

			txtEmail = (TextView) findViewById(R.id.email);
			txtEmail.setText(pref.getPref(Config.PREF_USERNAME));
			txtUsername = (TextView) findViewById(R.id.username);
			getUserData();
		}
	}

	void getUserData() {
		final Activity activity = this;
		// AsyncHttpClient client=new AsyncHttpClient();
		RequestParams params = new RequestParams();
		params.add("session_username", pref.getPref(Config.PREF_USERNAME));
		params.add("active_session_token",
				pref.getPref(Config.PREF_ACTIVE_SESSION_TOKEN));

		PARestClient.get(pref.getPref(Config.SERVER),Config.API_GET_USER_DATA, params,
				new AsyncHttpResponseHandler() {
					@Override
					public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
						// TODO Auto-generated method stub
						super.onSuccess(arg0, arg1, arg2);
						UserORM user = new ParserUserORM(new String(arg2))
								.getData();
						new ProfilUtils(activity).saveUser(user);
						txtUsername.setText(user.result.cs_name);
						//	save user name for fragment use
						username4Fragment = user.result.cs_name;

						Map userMap = new HashMap<>();
						userMap.put("name", user.result.cs_name);
						userMap.put("email", user.result.cs_email);
						userMap.put("signed_up_at", user.result.created_at);
						userMap.put("last_seen_ip", user.result.ip);

						Map customAttributes = new HashMap <>();
						customAttributes.put("user_type", "consumer");
						customAttributes.put("mobile_no", user.result.cs_mobile_number);
						userMap.put("custom_attributes", customAttributes);

						Intercom.client().updateUser(userMap);
					}
				});
	}

	// @Override
	// public boolean onCreateOptionsMenu(Menu menu) {
	// MenuInflater inflater = getMenuInflater();
	// inflater.inflate(R.menu.main, menu);
	// return super.onCreateOptionsMenu(menu);
	// }
	//
	/* Called whenever we call invalidateOptionsMenu() */
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		// If the nav drawer is open, hide action items related to the content
		// view
		boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
		// menu.findItem(R.id.action_websearch).setVisible(!drawerOpen);
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// The action bar home/up action should open or close the drawer.
		// ActionBarDrawerToggle will take care of this.
		if (mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}
		// Handle action buttons
		switch (item.getItemId()) {
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	/* The click listner for ListView in the navigation drawer */
	private class DrawerItemClickListener implements
			ListView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
								long id) {
			selectItem(position);
		}
	}

	public void selectItem(int position) {
		// update the main content by replacing fragments
		/**
		 * 	Edited on 30 June 2016 for data caching
		 * 	getting rid of over reliance on calling data from backend
		 */

		switch (position) {
			case 0:

				if(this.getSupportFragmentManager().getBackStackEntryCount() > 1){
					for(int i = this.getSupportFragmentManager().getBackStackEntryCount(); i > 1; i--)
						this.getSupportFragmentManager().popBackStackImmediate();
					Log.i("Backstack entry count0: ", String.valueOf(this.getSupportFragmentManager().getBackStackEntryCount()));
				} else{
					fm.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
//				listener.doFragmentChange(new FragmentNewLanding(), true, "");
					replaceFragment(R.id.content_frame, new FragmentRevisedLanding());
					Log.i("Backstack entry count1: ", String.valueOf(this.getSupportFragmentManager().getBackStackEntryCount()));
				}
//			Toast.makeText(ActivityLanding.this, "Backstack entry count: " + String.valueOf(this.getSupportFragmentManager().getBackStackEntryCount()), Toast.LENGTH_LONG).show();
				break;

			case 1:
//			fm.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
				replaceFragment(R.id.content_frame, new FragmentBid(), true);
				Log.i("BackStack entry count: ", String.valueOf(this.getSupportFragmentManager().getBackStackEntryCount()));
				break;

			case 2:
//			fm.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
				replaceFragment(R.id.content_frame, new FragmentOrder(), true);
				break;

			case 4:
//			fm.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
//			replaceFragment(R.id.content_frame, new FragmentProfilLandingNew());
				replaceFragment(R.id.content_frame, new FragmentProfilLandingNew(), true);
				break;
			case 8:
//			fm.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
//			replaceFragment(R.id.content_frame, new FragmentContactUs());
				replaceFragment(R.id.content_frame, new FragmentContactUs(), true);
				break;
			case R.id.menu_free_credit:
//			fm.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
//			replaceFragment(R.id.content_frame, new FragmentEarnFreeCredits());
				replaceFragment(R.id.content_frame, new FragmentEarnFreeCredits(), true);
				break;
			case R.id.menu_completed_jobs:
//			fm.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
				replaceFragment(R.id.content_frame, new FragmentOrder(true) ,true);

				break;
			case R.id.menu_faq:
				doFAQ();
				analytic.trackScreen("Consumer FAQ");
				break;
			case R.id.menu_chat:
				doChat();
				analytic.trackScreen("Consumer Chat");
				break;
			case R.id.tab_others:
				replaceFragment(R.id.content_frame, new FragmentOthers() ,true);
				break;
			default:
				simpleToast("Coming Soon");
				break;
		}

		// update selected item and title, then close the drawer
		// mDrawerList.setItemChecked(position, true);
		// setTitle(mMenuTitles[position]);
		mDrawerLayout.closeDrawer(mDrawerList);
	}

	public void gotoHome(){
		selectItem(0);
	}

	private void doChat() {
		UserORM user=new ProfilUtils(this).getUser();

		// TODO Auto-generated method stub
//		Intent i = new Intent(this, com.hipmob.android.HipmobCore.class);
//
//        // REQUIRED: set the appid to the key you're provided
//        i.putExtra(HipmobCore.KEY_APPID, Config.HIPMOB_APPID);
//
//        // REQUIRED: pass the host user identifier.
//        i.putExtra(HipmobCore.KEY_USERID, user.result.username);
//        i.putExtra(HipmobCore.KEY_NAME, user.result.cs_name);
//
//        // launch the chat window
//        startActivity(i);
		Intercom.client().displayConversationsList();
	}

	void doFAQ(){
		UserORM user=new ProfilUtils(this).getUser();

		Intent i = new Intent(this, com.hipmob.android.HipmobHelpDeskSearchActivity.class);

		// REQUIRED: set the appid to the key you're provided
		i.putExtra(com.hipmob.android.HipmobHelpDeskSearchActivity.KEY_APPID, Config.HIPMOB_APPID);

		// OPTIONAL: pass the context so we know where the search originated
		//i.putExtra(com.hipmob.android.HipmobHelpDeskSearchActivity.KEY_CONTEXT, "Bill Payment");

		// OPTIONAL: set the window title
		i.putExtra(com.hipmob.android.HipmobHelpDeskSearchActivity.KEY_TITLE, "Frequently Asked Question");

		// OPTIONAL: set the default search query that controls what should get shown first
		i.putExtra(com.hipmob.android.HipmobHelpDeskSearchActivity.KEY_DEFAULT_QUERY, "page");

		// OPTIONAL: set the user identifier
		i.putExtra(com.hipmob.android.HipmobCore.KEY_USERID, user.result.username);
		i.putExtra(HipmobCore.KEY_NAME, user.result.cs_name);

		// launch the search view
		startActivity(i);
	}

	@Override
	public void setTitle(CharSequence title) {
		mTitle = title;
		// getActionBar().setTitle(mTitle);
	}

	/**
	 * When using the ActionBarDrawerToggle, you must call it during
	 * onPostCreate() and onConfigurationChanged()...
	 */

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
//		mDrawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		// Pass any configuration change to the drawer toggls
		mDrawerToggle.onConfigurationChanged(newConfig);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
			case R.id.menu_jobs_list:
				selectItem(0);
				break;
			case R.id.menu_bid:
				selectItem(1);
				break;
			case R.id.menu_quotation:
				selectItem(2);
				break;
			case R.id.menu_completed_jobs:
				selectItem(R.id.menu_completed_jobs);
				break;
			case R.id.menu_history:
				selectItem(3);
				break;
			case R.id.menu_settings:
				selectItem(4);
				break;
			case R.id.menu_help:
				selectItem(5);
				break;
			case R.id.menu_about:
				selectItem(6);
				break;
			case R.id.menu_contact_us:
				selectItem(8);
				break;
			case R.id.menu_logout:
				GlobalVar.isGuest = false;
				GlobalVar.isResumeGuest = false;
				logout();
				startActivity(new Intent(this, SplashActivity.class));
				this.finish();
				break;
			case R.id.menu:
//			mDrawerLayout.openDrawer(mDrawerList);
				break;
			case R.id.profile_pic:
				selectItem(7);
				break;

			case R.id.menu_free_credit:
				selectItem(R.id.menu_free_credit);
				break;

			case R.id.menu_login:
				Intent intent = new Intent(this, SplashActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
				GlobalVar.isResumeGuest = true;
				startActivity(intent);
				break;

			case R.id.menu_faq:
			case R.id.menu_chat:selectItem(v.getId());
				break;
		}
	}

	public void menuClick() {
		mDrawerLayout.openDrawer(mDrawerList);

	}

	@Override
	public void doFragmentChange(Fragment f, boolean history, String title) {
		// TODO Auto-generated method stub
		replaceFragment(R.id.content_frame, f, history);
	}

	@Override
	public void doLogout() {
		// TODO Auto-generated method stub
		simpleToast("Session timeout,please login again");
		logout();
		startActivity(new Intent(this, SplashActivity.class));
		this.finish();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode,
									Intent intent) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, intent);
		Tracer.d("OnActivityResult ActivityLanding"+requestCode+" "+resultCode);
//		if (requestCode == IntentIntegrator.REQUEST_CODE) {
		Fragment f = fm.findFragmentById(R.id.content_frame);
		f.onActivityResult(requestCode, resultCode, intent);
//		}
//		else
//		if (requestCode == FragmentBidDetail.REQUEST_CODE_PAYMENT||requestCode == FragmentBidDetail.REQUEST_CODE_PROFILE_SHARING|| requestCode == FragmentBidDetail.REQUEST_CODE_FUTURE_PAYMENT) {
//				Fragment f = fm.findFragmentById(R.id.content_frame);
//				f.onActivityResult(requestCode, resultCode, intent);
//
//			}			

	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		AppEventsLogger.activateApp(this);
		setMenu();
	}

}
