package com.pa.common;

import com.paypal.android.sdk.payments.PayPalConfiguration;

public interface Config {
	int DEFAULT_SERVER=2;

//	String GOOGLE_ID="UA-60229011-1";
	String GOOGLE_ID="UA-60229011-4";
	String ADWORDS_ID="962505856";
	String APP_PREFERENCES = "com.pageadvisor.android";
	String ROOT = ".pa_cons";
	String TMP_IMG_ROOT = ROOT + "/.TMP";
	
	String SERVER=APP_PREFERENCES+".SERVER";
	//
	String DOMAIN_CURRENT="pageadvisor.bounche.com";
	String DOMAIN_DEV="dev.pageadvisor.com";
	String DOMAIN_STAGING="staging.pageadvisor.com";
	String DOMAIN_LIVE="api.pageadvisor.com";
	
	String DOMAIN = DOMAIN_CURRENT;

			String MAIN_URI =
	// "http://"+DOMAIN+"/pageadvisor3/public/"
	"http://" + DOMAIN + "/";
	String API_MERCHANT_IMAGE=MAIN_URI+"user/merchant-image?image_name=";
	//	new probably temporary merchant images URL prefix
	String MERCHANT_IMAGE_PREFIX = "http://api.pageadvisor.com/img/merchant.php?src=";
	String API_CHANGE_USER_PASSWORD = MAIN_URI + "user/user-change-password";

	String API_SERVICE_CATEGORY_HIERARCHICAL = MAIN_URI
			+ "service-search/service-category";
	String API_REGISTER_MERCHANT = MAIN_URI + "home/register";
	String API_BID_LIST = MAIN_URI + "transaction/service-request";
	String API_BID_DETAIL = MAIN_URI + "transaction/service-request-detail";
	String API_BID_SEND_PROPOSAL = MAIN_URI + "transaction/proposal";
	String API_GET_USER_DATA = MAIN_URI + "user/user-data-own";

	String API_CHECK_USERNAME = MAIN_URI + "home/check-username";
	String API_LOGIN = MAIN_URI + "user/login";
	String API_EDIT_USER = MAIN_URI + "user/edit-user-own-by-field";
	String API_GET_SERVICE_ORDER = MAIN_URI + "transaction/service-order";
	String API_GET_SERVICE_ORDER_DETAIL = MAIN_URI
			+ "transaction/service-order-detail";
	String API_GET_ALL_SERVICE_REQUEST = MAIN_URI
			+ "transaction/all-service-request";
	String API_READ_STATUS_CHANGE = MAIN_URI + "transaction/read-status-change";

    String API_BID_COMMENTS = MAIN_URI + "comment/comment";
    String API_POST_BID_COMMENT = MAIN_URI + "comment/input-comment";

	//
	String PREF_LAST_USERNAME=APP_PREFERENCES+".LASTUSERNAME";
	String PREF_LAST_COUNTRY=APP_PREFERENCES+".LASTCOUNTRY";
	String PREF_LAST_STATE=APP_PREFERENCES+".LASTSTATE";
	String PREF_LAST_STATE_SHORT=APP_PREFERENCES+".LASTSTATESHORT";

	String PREF_EMAIL=APP_PREFERENCES+".EMAIL";
	String PREF_USERNAME = APP_PREFERENCES + ".USERNAME";
	String PREF_USER = APP_PREFERENCES + ".USER";
	String PREF_ACTIVE_SESSION_TOKEN = APP_PREFERENCES
			+ ".ACTIVE_SESSION_TOKEN";
	String API_LOCATION = MAIN_URI + "location/location-list";
	String API_AUTO_COMPLETE_SERVICE = MAIN_URI
			+ "service-search/auto-complete";
	String API_SEARCH = MAIN_URI + "service-search/search";

	String API_FORM_FIELD = MAIN_URI + "transaction/service-form";
	String API_CREATE_AUCTION = MAIN_URI + "transaction/create-auction";
	String API_CREATE_QUOTATION = MAIN_URI + "transaction/create-quotation";
	String API_CREATE_TRANSACTION = MAIN_URI + "transaction/create-transaction";
	String API_GET_CUSTOMER_SERVICE_REQUEST = MAIN_URI
			+ "transaction/customer-service-request";
	String API_CANCEL_BID = MAIN_URI + "transaction/cancel-request";
	String API_CLOSE_BID = MAIN_URI + "transaction/close-service-request";
	String API_GET_PROPOSAL = MAIN_URI + "transaction/service-proposal";
	String API_SEND_ORDER = MAIN_URI + "transaction/order";
	String API_CANCEL_JOB = MAIN_URI + "transaction/cancel-order";
	String API_CLOSE_JOB = MAIN_URI + "transaction/set-service-order-status";
	String API_VERIFY_MERCHANT = MAIN_URI
			+ "transaction/check-merchant-verification-code";
	String API_RATE_MERCHANT = MAIN_URI + "user/user-submit-rating";
	String API_SERVICE_IMAGE = MAIN_URI + "transaction/service-image";
	String API_SET_FAVE_MERCHANT = MAIN_URI + "user/user-favourite-status";
	String API_GET_FAVE_MERCHANT = MAIN_URI + "user/user-favourite-list";
	String API_GET_LAST_ADDRESS = MAIN_URI
			+ "transaction/get-last-delivery-address";
	String API_GET_REFERRER_CODE = MAIN_URI + "user/own-referrer-code";
	String API_GET_USER_FREE_CREDITS = MAIN_URI + "user/user-freecredit";
    String API_DEDUCT_CREDITS = MAIN_URI + "user/deduct-credit";
	String API_CHECK_PROMO_CODE = MAIN_URI + "transaction/validate-promo-code";
	String API_REGISTER_DEVICE=MAIN_URI+"user/register-device";
	String API_CEK_APP_VERSION=MAIN_URI+"home/current-version";
	String API_FORGOT_PASS_TOKEN=MAIN_URI+"user/user-forgot-password";
	String API_FORGOT_PASS_UPDATE=MAIN_URI+"user/user-forgot-password-update";

    // DEAL Server
    String DOMAIN_DEAL_DEV 		= "dealdev.pageadvisor.com";
    String DOMAIN_DEAL_STAGING 	= "dealstaging.pageadvisor.com";
    String DOMAIN_DEAL_LIVE 	= "deal.pageadvisor.com";

	// DEAL PATH
    String DEAL_IMAGE_PATH 						= "image/promotion";
    String DEAL_API_GET_PROMOTION 				= "promotion/promotion";
	String DEAL_API_GET_PROMO_ORDER 			= "promo-order/order";
	String DEAL_API_GET_PROMO_ORDER_DETAIL 		= "promo-order/order-detail";
	String DEAL_API_POST_PROMO_ORDER 			= "promo-order/create-order";
	String DEAL_API_POST_COMPLETE_PROMO_ORDER 	= "promo-order/complete-order";
	String DEAL_API_CHECK_PROMO				 	= "package-order/check-promo";
	String DEAL_API_USE_CREDIT				 	= "order/deduct-credit";

	// PACKAGE PATH
	String PACKAGE_IMAGE_PATH					="image/package";
	String DEAL_API_GET_PACKAGE					="package/package";
	String DEAL_API_GET_PACKAGE_CATEGORY		="package/package-category";
	String DEAL_API_GET_PACKAGE_DETAIL 			="package/option-detail";
	String DEAL_API_GET_PACKAGE_DETAIL_LIST		="package/package-option";
	String DEAL_API_POST_PACKAGE_ORDER 			="package-order/create-order";
	String DEAL_API_GET_PACKAGE_SCHEDULE		="package/schedule";
	String DEAL_API_GET_PACKAGE_ORDER			="package-order/order";
	String DEAL_API_GET_PACKAGE_ORDER_DETAIL 	="package-order/order-detail";
	String DEAL_API_COMPLETE_PACKAGE_DETAIL 	="package-order/complete-order";
	
	// message status
	String UNREAD = "unread";

	String BID_CLOSE_MSG = "Bid window will be closed in the next @ seconds";

	// get image for profile
	int PICK_FROM_CAMERA = 1;
	int CROP_FROM_CAMERA = 2;
	int PICK_FROM_FILE = 3;
	
	String NA="Not Available";

	
	//MOLPAY
	int MOLPAY_REQUEST_CODE = 313; 
	
	
	//PAYPAL
	  /**
     * - Set to PayPalConfiguration.ENVIRONMENT_PRODUCTION to move real money.
     * 
     * - Set to PayPalConfiguration.ENVIRONMENT_SANDBOX to use your test credentials
     * from https://developer.paypal.com
     * 
     * - Set to PayPalConfiguration.ENVIRONMENT_NO_NETWORK to kick the tires
     * without communicating to PayPal's servers.
     */
    String CONFIG_ENVIRONMENT = PayPalConfiguration.ENVIRONMENT_SANDBOX;

    	// note that these credentials will differ between live & sandbox environments.
    String CONFIG_CLIENT_ID = //"Ad4doxAFYseNhGBWilxwrtakeSBitlY57Vb11hlagufwKe_dbeL9Xlh4IwtT";//
    	"AajDhxDPBXhrX0XwfBvRyr8Qr_f27bHPCGP41zxEO-qRSM_EFcV4aqXxyiP2";

    
    String HIPMOB_APPID="d71d75fe4a544c678454551bb528262b";
	String APP_USER_FB_ID = APP_PREFERENCES+".FBID";


    String TWITTER_KEY = "lrST2P515VyliMqsyC4uMRbYX";
    String TWITTER_SECRET = "j0AF8VwKt5kwtnin6c7D0Vvc0wjLPTrKYvZfYVKTfTBN0SbEHy";

    int NANIGANS_APP_ID = 309130;

}
