/**
 * Copyright 2012 Facebook
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.pa.common;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginBehavior;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;

public class SessionLoginFragment extends MyFragment {
	private static final String URL_PREFIX_FRIENDS = "https://graph.facebook.com/me/friends?access_token=";

	private TextView textInstructionsOrLink;
	protected Button buttonLoginLogout;
    CallbackManager callbackManager;
	List<String> PERMISSIONS = Arrays.asList("email"/* ,"user_birthday" */);

	protected void doneLoginFB(String email, String name, String fbid,
			String last_name) {

	};

	static boolean isFirst = true;
	boolean isLoginClicked = false;

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);

        callbackManager = CallbackManager.Factory.create();

        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        updateView();
                    }

                    @Override
                    public void onCancel() {
                        // App code
                        Log.d("FB", "onCancel");
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        // App code
                        Log.d("FB", "onError " + exception.getMessage());
                    }
                });

		if (hasFB() && buttonLoginLogout != null)
			buttonLoginLogout.setOnClickListener(new View.OnClickListener() {
				public void onClick(View view) {
					onClickLogin();
				}
			});

	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}

	protected void updateView() {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        if (!accessToken.isExpired()) {
            if (isLoginClicked) {
                // request graph
                isLoginClicked = false;
                loadingInternetDialog.show();

                GraphRequest request = GraphRequest.newMeRequest(
                        accessToken,
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(
                                    JSONObject object,
                                    GraphResponse response) {
                                try {
                                    Log.v(getClass().getName(),
                                            response.toString());
                                    String email = object
                                            .getString("email");
                                    if (email.length() > 0) {
                                        pref.savePref(Config.PREF_EMAIL,
                                                email);
                                    }

                                } catch (Exception e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                } finally {
                                    System.out.println("email"
                                            + pref.getPref(Config.PREF_EMAIL));

                                    try {
                                        doneLoginFB(
                                                pref.getPref(Config.PREF_EMAIL),
                                                object.getString("name"), object.getString("id"),
                                                object.getString("last_name"));
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                }
                                loadingInternetDialog.hide();
                            }
                        });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,last_name,email");
                request.setParameters(parameters);
                request.executeAsync();
            } else {
                isFirst = false;
            }
        }
	}

	protected void onClickLogin() {
		isLoginClicked = true;
		// dialog.show();
		System.out.println("Click Login");
		onClickLogout();
        LoginManager.getInstance().setLoginBehavior(LoginBehavior.WEB_ONLY);
        LoginManager.getInstance().logInWithReadPermissions(this, PERMISSIONS);
	}

	protected void onClickLogout() {
		System.out.println("Click Logout");

        LoginManager.getInstance().logOut();

	}

}
