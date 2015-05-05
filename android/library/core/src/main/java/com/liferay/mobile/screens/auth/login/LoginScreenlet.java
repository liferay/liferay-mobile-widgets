/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
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
import com.liferay.mobile.screens.auth.AuthMethod;
import com.liferay.mobile.screens.auth.login.interactor.LoginInteractor;
import com.liferay.mobile.screens.auth.login.interactor.LoginInteractorImpl;
import com.liferay.mobile.screens.auth.login.view.LoginViewModel;
import com.liferay.mobile.screens.base.BaseScreenlet;
import com.liferay.mobile.screens.context.LiferayServerContext;
import com.liferay.mobile.screens.context.SessionContext;
import com.liferay.mobile.screens.context.User;

import static com.liferay.mobile.screens.context.storage.CredentialsStoreBuilder.StorageType;

/**
 * @author Silvio Santos
 */
public class LoginScreenlet
	extends BaseScreenlet<LoginViewModel, LoginInteractor>
	implements LoginListener {

	public static final int REQUEST_OAUTH_CODE = 1;

	public LoginScreenlet(Context context) {
		super(context);
	}

	public LoginScreenlet(Context context, AttributeSet attributes) {
		super(context, attributes);
	}

	public LoginScreenlet(Context context, AttributeSet attributes, int defaultStyle) {
		super(context, attributes, defaultStyle);
	}

	public static final String OAUTH = "OAUTH";
	public static final String BASIC_AUTH = "BASIC_AUTH";

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

		SessionContext.storeSession(_credentialsStore);
	}

	public void sendResult(int result, Intent intent) {
		if (result == Activity.RESULT_OK) {
			try {
				OAuthConfig oAuthConfig = (OAuthConfig) intent.getSerializableExtra(OAuthActivity.EXTRA_OAUTH_CONFIG);
				getInteractor().loginWithOAuth(oAuthConfig);
			}
			catch (Exception e) {
				onLoginFailure(e);
			}
		}
		else if (result == Activity.RESULT_CANCELED) {
			Exception exception = (Exception) intent.getSerializableExtra(
				OAuthActivity.EXTRA_EXCEPTION);
			onLoginFailure(exception);
		}
	}

	public void setListener(LoginListener listener) {
		_listener = listener;
	}

	public AuthMethod getAuthMethod() {
		return _authMethod;
	}

	public void setAuthMethod(AuthMethod authMethod) {
		_authMethod = authMethod;

		getViewModel().setAuthMethod(_authMethod);
	}

	public StorageType getCredentialsStore() {
		return _credentialsStore;
	}

	public void setCredentialsStore(StorageType value) {
		_credentialsStore = value;
	}

	@Override
	protected View createScreenletView(Context context, AttributeSet attributes) {
		TypedArray typedArray = context.getTheme().obtainStyledAttributes(
			attributes, R.styleable.LoginScreenlet, 0, 0);

		int storeValue = typedArray.getInt(R.styleable.LoginScreenlet_credentialsStore,
			StorageType.NONE.toInt());

		_credentialsStore = StorageType.valueOf(storeValue);


		int layoutId = typedArray.getResourceId(
			R.styleable.LoginScreenlet_layoutId, getDefaultLayoutId());

		View view = LayoutInflater.from(context).inflate(layoutId, null);

		int authMethodId = typedArray.getInt(R.styleable.LoginScreenlet_authMethod, 0);

		_authMethod = AuthMethod.getValue(authMethodId);
		LoginViewModel loginViewModel = (LoginViewModel) view;
		loginViewModel.setAuthMethod(_authMethod);
		loginViewModel.setAuthenticationType(LiferayServerContext.getAuthenticationType());

		typedArray.recycle();

		return view;
	}

	@Override
	protected LoginInteractor createInteractor(String actionName) {
		return new LoginInteractorImpl(getScreenletId());
	}

	@Override
	protected void onUserAction(String userActionName, LoginInteractor interactor, Object... args) {
		if (BASIC_AUTH.equals(userActionName)) {
			LoginViewModel viewModel = getViewModel();

			viewModel.showStartOperation(userActionName);

			String login = viewModel.getLogin();
			String password = viewModel.getPassword();
			AuthMethod method = viewModel.getAuthMethod();

			try {
				interactor.login(login, password, method);
			}
			catch (Exception e) {
				onLoginFailure(e);
			}
		}
		else {
			String server = LiferayServerContext.getServer();
			String consumerKey = LiferayServerContext.getConsumerKey();
			String consumerSecret = LiferayServerContext.getConsumerSecret();
			OAuthConfig config = new OAuthConfig(server, consumerKey, consumerSecret);

			Intent intent = new Intent(getContext(), OAuthActivity.class);
			intent.putExtra(OAuthActivity.EXTRA_OAUTH_CONFIG, config);
			((Activity) getContext()).startActivityForResult(intent, REQUEST_OAUTH_CODE);
		}
	}

	private LoginListener _listener;
	private AuthMethod _authMethod;
	private StorageType _credentialsStore;

}