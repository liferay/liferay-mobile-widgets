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

import com.liferay.mobile.screens.auth.login.LoginListener;
import com.liferay.mobile.screens.base.thread.BaseRemoteInteractorNew;
import com.liferay.mobile.screens.base.thread.event.BasicThreadEvent;
import com.liferay.mobile.screens.context.SessionContext;
import com.liferay.mobile.screens.context.User;


public abstract class BaseLoginInteractor
	extends BaseRemoteInteractorNew<LoginListener, BasicThreadEvent> {

	@Override
	public void onSuccess(BasicThreadEvent event) throws Exception {
		User user = new User(event.getJSONObject());
		SessionContext.setCurrentUser(user);
		getListener().onLoginSuccess(user);
	}

	@Override
	public void onFailure(Exception e) {
		SessionContext.logout();
		getListener().onLoginFailure(e);
	}

}