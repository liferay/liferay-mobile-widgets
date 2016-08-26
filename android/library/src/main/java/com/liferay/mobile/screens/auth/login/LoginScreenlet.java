/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 * <p>
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * <p>
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.mobile.screens.auth.login;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

import com.liferay.mobile.android.oauth.OAuthConfig;
import com.liferay.mobile.android.oauth.activity.OAuthActivity;
import com.liferay.mobile.screens.R;
import com.liferay.mobile.screens.auth.BasicAuthMethod;
import com.liferay.mobile.screens.auth.login.interactor.BaseLoginInteractor;
import com.liferay.mobile.screens.auth.login.interactor.LoginBasicInteractor;
import com.liferay.mobile.screens.auth.login.interactor.LoginOAuthInteractor;
import com.liferay.mobile.screens.auth.login.view.LoginViewModel;
import com.liferay.mobile.screens.base.BaseScreenlet;
import com.liferay.mobile.screens.context.AuthenticationType;
import com.liferay.mobile.screens.context.LiferayScreensContext;
import com.liferay.mobile.screens.context.LiferayServerContext;
import com.liferay.mobile.screens.context.SessionContext;
import com.liferay.mobile.screens.context.User;

import static com.liferay.mobile.screens.context.storage.CredentialsStorageBuilder.StorageType;

/**
 * @author Silvio Santos
 */
public class LoginScreenlet
	extends BaseScreenlet<LoginViewModel, BaseLoginInteractor>
	implements LoginListener {

	public static final String OAUTH = "OAUTH";
	public static final String BASIC_AUTH = "BASIC_AUTH";
	public static final int REQUEST_OAUTH_CODE = 1;
	public static final String LOGIN_SUCCESSFUL = "com.liferay.mobile.screens.auth.login.success";

	public LoginScreenlet(Context context) {
		super(context);
	}

	public LoginScreenlet(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public LoginScreenlet(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	public LoginScreenlet(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
		super(context, attrs, defStyleAttr, defStyleRes);
	}

	@Override
	public void onLoginFailure(Exception e) {
		getViewModel().showFailedOperation(null, e);

		if (_listener != null) {
			_listener.onLoginFailure(e);
		}
	}

	@Override
	public void onLoginSuccess(User user) {
		getViewModel().showFinishOperation(user);

		if (_listener != null) {
			_listener.onLoginSuccess(user);
		}

		getContext().sendBroadcast(new Intent(LOGIN_SUCCESSFUL));

		SessionContext.storeCredentials(_credentialsStorage);
	}

	public void sendOAuthResult(int result, Intent intent) {
		if (result == Activity.RESULT_OK) {
			try {
				OAuthConfig oauthConfig = (OAuthConfig) intent.getSerializableExtra(
					OAuthActivity.EXTRA_OAUTH_CONFIG);

				BaseLoginInteractor oauthInteractor = getInteractor(OAUTH);
				oauthInteractor.start(oauthConfig);
			}
			catch (Exception e) {
				onLoginFailure(e);
			}
		}
		else if (result == Activity.RESULT_CANCELED && intent != null) {
			Exception exception = (Exception) intent.getSerializableExtra(
				OAuthActivity.EXTRA_EXCEPTION);
			onLoginFailure(exception);
		}
	}

	public void setListener(LoginListener listener) {
		_listener = listener;
	}

	public BasicAuthMethod getAuthMethod() {
		return _basicAuthMethod;
	}

	public StorageType getCredentialsStorage() {
		return _credentialsStorage;
	}

	public void setCredentialsStorage(StorageType value) {
		_credentialsStorage = value;
	}

	public String getOAuthConsumerSecret() {
		return _oauthConsumerSecret;
	}

	public void setOAuthConsumerSecret(String value) {
		_oauthConsumerSecret = value;
	}

	public String getOAuthConsumerKey() {
		return _oauthConsumerKey;
	}

	public void setOAuthConsumerKey(String value) {
		_oauthConsumerKey = value;
	}

	public BasicAuthMethod getBasicAuthMethod() {
		return _basicAuthMethod;
	}

	public void setBasicAuthMethod(BasicAuthMethod basicAuthMethod) {
		_basicAuthMethod = basicAuthMethod;

		getViewModel().setBasicAuthMethod(_basicAuthMethod);
	}

	public String getOauthConsumerKey() {
		return _oauthConsumerKey;
	}

	public void setOauthConsumerKey(String oauthConsumerKey) {
		_oauthConsumerKey = oauthConsumerKey;
	}

	public String getOauthConsumerSecret() {
		return _oauthConsumerSecret;
	}

	public void setOauthConsumerSecret(String oauthConsumerSecret) {
		_oauthConsumerSecret = oauthConsumerSecret;
	}

	@Override
	protected View createScreenletView(Context context, AttributeSet attributes) {
		TypedArray typedArray = context.getTheme().obtainStyledAttributes(
			attributes, R.styleable.LoginScreenlet, 0, 0);

		int storeValue = typedArray.getInt(R.styleable.LoginScreenlet_credentialsStorage,
			StorageType.NONE.toInt());

		_credentialsStorage = StorageType.valueOf(storeValue);

		_oauthConsumerKey =
			typedArray.getString(R.styleable.LoginScreenlet_oauthConsumerKey);
		_oauthConsumerSecret =
			typedArray.getString(R.styleable.LoginScreenlet_oauthConsumerSecret);

		int layoutId = typedArray.getResourceId(
			R.styleable.LoginScreenlet_layoutId, getDefaultLayoutId());

		View view = LayoutInflater.from(context).inflate(layoutId, null);

		LoginViewModel loginViewModel = (LoginViewModel) view;

		if (_oauthConsumerKey != null && _oauthConsumerSecret != null) {
			loginViewModel.setAuthenticationType(AuthenticationType.OAUTH);
		}
		else {
			int authMethodId = typedArray.getInt(R.styleable.LoginScreenlet_basicAuthMethod, 0);

			_basicAuthMethod = BasicAuthMethod.getValue(authMethodId);
			loginViewModel.setBasicAuthMethod(_basicAuthMethod);

			loginViewModel.setAuthenticationType(AuthenticationType.BASIC);
		}

		typedArray.recycle();

		return view;
	}

	@Override
	protected BaseLoginInteractor createInteractor(String actionName) {
		if (BASIC_AUTH.equals(actionName)) {
			return new LoginBasicInteractor();
		}
		else {
			LoginOAuthInteractor oauthInteractor = new LoginOAuthInteractor();

			OAuthConfig config = new OAuthConfig(
				LiferayServerContext.getServer(),
				_oauthConsumerKey, _oauthConsumerSecret);

			oauthInteractor.setOAuthConfig(config);

			return oauthInteractor;
		}
	}

	@Override
	protected void onUserAction(String userActionName, BaseLoginInteractor interactor, Object... args) {
		if (BASIC_AUTH.equals(userActionName)) {

			LoginViewModel viewModel = getViewModel();
			interactor.start(viewModel.getLogin(), viewModel.getPassword(), viewModel.getBasicAuthMethod());
		}
		else {

			LoginOAuthInteractor oauthInteractor = (LoginOAuthInteractor) interactor;
			Intent intent = new Intent(getContext(), OAuthActivity.class);
			intent.putExtra(OAuthActivity.EXTRA_OAUTH_CONFIG, oauthInteractor.getOAuthConfig());
			LiferayScreensContext.getActivityFromContext(getContext()).startActivityForResult(intent, REQUEST_OAUTH_CODE);
		}
	}

	private LoginListener _listener;
	private BasicAuthMethod _basicAuthMethod;
	private StorageType _credentialsStorage;

	private String _oauthConsumerKey;
	private String _oauthConsumerSecret;

}