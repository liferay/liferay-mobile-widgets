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

package com.liferay.mobile.screens.auth.forgotpassword;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

import com.liferay.mobile.screens.R;
import com.liferay.mobile.screens.auth.BasicAuthMethod;
import com.liferay.mobile.screens.auth.forgotpassword.interactor.ForgotPasswordInteractorImpl;
import com.liferay.mobile.screens.auth.forgotpassword.view.ForgotPasswordViewModel;
import com.liferay.mobile.screens.base.BaseScreenlet;
import com.liferay.mobile.screens.context.LiferayServerContext;

/**
 * @author Silvio Santos
 */
public class ForgotPasswordScreenlet
	extends BaseScreenlet<ForgotPasswordViewModel, ForgotPasswordInteractorImpl>
	implements ForgotPasswordListener {

	public ForgotPasswordScreenlet(Context context) {
		super(context);
	}

	public ForgotPasswordScreenlet(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public ForgotPasswordScreenlet(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	public ForgotPasswordScreenlet(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
		super(context, attrs, defStyleAttr, defStyleRes);
	}

	@Override
	public void onForgotPasswordRequestFailure(Exception e) {
		getViewModel().showFailedOperation(null, e);

		if (_listener != null) {
			_listener.onForgotPasswordRequestFailure(e);
		}
	}

	@Override
	public void onForgotPasswordRequestSuccess(boolean passwordSent) {
		getViewModel().showFinishOperation(passwordSent);

		if (_listener != null) {
			_listener.onForgotPasswordRequestSuccess(passwordSent);
		}
	}

	public String getAnonymousApiPassword() {
		return _anonymousApiPassword;
	}

	public void setAnonymousApiPassword(String value) {
		_anonymousApiPassword = value;
	}

	public String getAnonymousApiUserName() {
		return _anonymousApiUserName;
	}

	public void setAnonymousApiUserName(String value) {
		_anonymousApiUserName = value;
	}

	public long getCompanyId() {
		return _companyId;
	}

	public void setCompanyId(long value) {
		_companyId = value;
	}

	public BasicAuthMethod getBasicAuthMethod() {
		return _basicAuthMethod;
	}

	public void setBasicAuthMethod(BasicAuthMethod basicAuthMethod) {
		_basicAuthMethod = basicAuthMethod;
		getViewModel().setBasicAuthMethod(basicAuthMethod);
	}

	public ForgotPasswordListener getListener() {
		return _listener;
	}

	public void setListener(ForgotPasswordListener listener) {
		_listener = listener;
	}

	@Override
	protected View createScreenletView(Context context, AttributeSet attributes) {
		TypedArray typedArray = context.getTheme().obtainStyledAttributes(
			attributes, R.styleable.ForgotPasswordScreenlet, 0, 0);

		_companyId = castToLongOrUseDefault(typedArray.getString(
			R.styleable.ForgotPasswordScreenlet_companyId),
			LiferayServerContext.getCompanyId());

		_anonymousApiUserName = typedArray.getString(
			R.styleable.ForgotPasswordScreenlet_anonymousApiUserName);

		_anonymousApiPassword = typedArray.getString(
			R.styleable.ForgotPasswordScreenlet_anonymousApiPassword);

		int layoutId = typedArray.getResourceId(
			R.styleable.ForgotPasswordScreenlet_layoutId, getDefaultLayoutId());

		View view = LayoutInflater.from(context).inflate(layoutId, null);

		int authMethod = typedArray.getInt(R.styleable.ForgotPasswordScreenlet_basicAuthMethod, 0);
		_basicAuthMethod = BasicAuthMethod.getValue(authMethod);
		((ForgotPasswordViewModel) view).setBasicAuthMethod(_basicAuthMethod);

		typedArray.recycle();

		return view;
	}

	@Override
	protected ForgotPasswordInteractorImpl createInteractor(String actionName) {
		return new ForgotPasswordInteractorImpl();
	}

	@Override
	protected void onUserAction(String userActionName, ForgotPasswordInteractorImpl interactor, Object... args) {

		ForgotPasswordViewModel viewModel = getViewModel();

		String login = viewModel.getLogin();
		BasicAuthMethod method = viewModel.getBasicAuthMethod();

		try {
			interactor.start(
				_companyId, login, method, _anonymousApiUserName, _anonymousApiPassword);
		}
		catch (Exception e) {
			onForgotPasswordRequestFailure(e);
		}
	}

	private String _anonymousApiPassword;
	private String _anonymousApiUserName;
	private long _companyId;
	private BasicAuthMethod _basicAuthMethod;
	private ForgotPasswordListener _listener;

}