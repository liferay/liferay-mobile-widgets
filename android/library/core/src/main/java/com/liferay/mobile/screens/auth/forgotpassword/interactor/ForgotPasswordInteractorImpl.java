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

package com.liferay.mobile.screens.auth.forgotpassword.interactor;

import com.liferay.mobile.android.auth.Authentication;
import com.liferay.mobile.android.auth.basic.BasicAuthentication;
import com.liferay.mobile.android.service.Session;
import com.liferay.mobile.android.service.SessionImpl;
import com.liferay.mobile.screens.auth.BasicAuthMethod;
import com.liferay.mobile.screens.auth.forgotpassword.ForgotPasswordListener;
import com.liferay.mobile.screens.base.interactor.BaseRemoteInteractor;
import com.liferay.mobile.screens.context.LiferayServerContext;
import com.liferay.mobile.screens.service.v7.ScreensuserService;

/**
 * @author Jose Manuel Navarro
 */
public class ForgotPasswordInteractorImpl
	extends BaseRemoteInteractor<ForgotPasswordListener>
	implements ForgotPasswordInteractor {

	public ForgotPasswordInteractorImpl(int targetScreenletId) {
		super(targetScreenletId);
	}

	public void onEvent(ForgotPasswordEvent event) {
		if (!isValidEvent(event)) {
			return;
		}

		if (event.isFailed()) {
			getListener().onForgotPasswordRequestFailure(event.getException());
		}
		else {
			getListener().onForgotPasswordRequestSuccess(
				event.isPasswordSent());
		}
	}

	@Override
	public void requestPassword(
		long companyId, String login, BasicAuthMethod basicAuthMethod,
		String anonymousApiUserName, String anonymousApiPassword)
		throws Exception {

		validate(
			companyId, login, basicAuthMethod, anonymousApiUserName,
			anonymousApiPassword);

		ScreensuserService service = getScreensUserService(
			anonymousApiUserName, anonymousApiPassword);

		switch (basicAuthMethod) {
			case EMAIL:
				sendForgotPasswordByEmailRequest(service, companyId, login);
				break;

			case USER_ID:
				sendForgotPasswordByIdRequest(service, Long.parseLong(login));
				break;

			case SCREEN_NAME:
				sendForgotPasswordByScreenNameRequest(
					service, companyId, login);

				break;
		}
	}

	protected ScreensuserService getScreensUserService(
		String anonymousApiUserName, String anonymousApiPassword) {

		Authentication authentication = new BasicAuthentication(
			anonymousApiUserName, anonymousApiPassword);

		Session anonymousSession = new SessionImpl(
			LiferayServerContext.getServer(), authentication);

		anonymousSession.setCallback(
			new ForgotPasswordCallback(getTargetScreenletId()));

		return new ScreensuserService(anonymousSession);
	}

	protected void sendForgotPasswordByEmailRequest(
		ScreensuserService service, long companyId,
		String emailAddress)
		throws Exception {

		service.sendPasswordByEmailAddress(companyId, emailAddress);
	}

	protected void sendForgotPasswordByIdRequest(
		ScreensuserService service, long userId)
		throws Exception {

		service.sendPasswordByUserId(userId);
	}

	protected void sendForgotPasswordByScreenNameRequest(
		ScreensuserService service, long companyId, String screenName)
		throws Exception {

		service.sendPasswordByScreenName(companyId, screenName);
	}

	protected void validate(
		long companyId, String login, BasicAuthMethod basicAuthMethod,
		String anonymousApiUserName, String anonymousApiPassword) {

		if ((companyId <= 0) && (basicAuthMethod != BasicAuthMethod.USER_ID)) {
			throw new IllegalArgumentException(
				"CompanyId cannot be 0 or negative");
		}

		if (login == null) {
			throw new IllegalArgumentException("Login cannot be empty");
		}

		if (basicAuthMethod == null) {
			throw new IllegalArgumentException("BasicAuthMethod cannot be empty");
		}

		if (anonymousApiUserName == null) {
			throw new IllegalArgumentException(
				"Anonymous api user name cannot be empty");
		}

		if (anonymousApiPassword == null) {
			throw new IllegalArgumentException(
				"Anonymous api password cannot be empty");
		}
	}

}