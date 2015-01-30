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

package com.liferay.mobile.screens.auth.login.interactor;

import com.liferay.mobile.android.service.Session;
import com.liferay.mobile.android.v62.user.UserService;
import com.liferay.mobile.screens.auth.login.LoginListener;
import com.liferay.mobile.screens.base.interactor.BaseInteractor;
import com.liferay.mobile.screens.base.interactor.JSONObjectEvent;
import com.liferay.mobile.screens.context.LiferayServerContext;
import com.liferay.mobile.screens.context.SessionContext;

/**
 * @author Silvio Santos
 */
public class LoginInteractorImpl
	extends BaseInteractor<LoginListener>
	implements LoginInteractor {

	public LoginInteractorImpl(int targetScreenletId) {
		super(targetScreenletId);
	}

	public void login(String login, String password, AuthMethod authMethod) {
		validate(login, password, authMethod);

		switch (authMethod) {
			case EMAIL:
				sendGetUserByEmailRequest(login, password);

				break;

			case USER_ID:
				sendGetUserByIdRequest(Long.parseLong(login), password);

				break;

			case SCREEN_NAME:
				sendGetUserByScreenName(login, password);

				break;
		}
	}

	public void onEvent(JSONObjectEvent event) {
		if (!isValidEvent(event)) {
			return;
		}

		if (event.isFailed()) {
			SessionContext.clearSession();
			getListener().onLoginFailure(event.getException());
		}
		else {
			getListener().onLoginSuccess(event.getJSONObject());
		}
	}

	protected UserService getUserService(String login, String password) {
		Session session = SessionContext.createSession(login, password);
		session.setCallback(new LoginCallback(getTargetScreenletId()));

		return new UserService(session);
	}

	protected void sendGetUserByEmailRequest(String email, String password) {
		UserService service = getUserService(email, password);

		try {
			service.getUserByEmailAddress(
				LiferayServerContext.getCompanyId(), email);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected void sendGetUserByIdRequest(long userId, String password) {
		UserService service = getUserService(String.valueOf(userId), password);

		try {
			service.getUserById(userId);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected void sendGetUserByScreenName(String screenName, String password) {
		UserService service = getUserService(screenName, password);

		try {
			service.getUserByScreenName(
				LiferayServerContext.getCompanyId(), screenName);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected void validate(
		String login, String password, AuthMethod authMethod) {

		if (login == null) {
			throw new IllegalArgumentException("Login cannot be null");
		}

		if (password == null) {
			throw new IllegalArgumentException("Password cannot be null");
		}

		if (authMethod == null) {
			throw new IllegalArgumentException("AuthMethod cannot be null");
		}
	}

}