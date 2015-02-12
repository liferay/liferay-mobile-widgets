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

package com.liferay.mobile.screens.webcontentdisplay.interactor;

import com.liferay.mobile.android.service.Session;
import com.liferay.mobile.android.v62.journalarticle.JournalArticleService;
import com.liferay.mobile.screens.base.interactor.BaseRemoteInteractor;
import com.liferay.mobile.screens.context.SessionContext;
import com.liferay.mobile.screens.webcontentdisplay.WebContentDisplayListener;

import java.util.Locale;

/**
 * @author Jose Manuel Navarro
 */
public class WebContentDisplayInteractorImpl
	extends BaseRemoteInteractor<WebContentDisplayListener>
	implements WebContentDisplayInteractor {

	public WebContentDisplayInteractorImpl(int targetScreenletId) {
		super(targetScreenletId);
	}

	public void load(long groupId, String articleId, Locale locale)
		throws Exception {

		JournalArticleService service = getJournalArticleService();

		service.getArticleContent(groupId, articleId, locale.toString(), null);
	}

	public void onEvent(WebContentDisplayEvent event) {
		if (!isValidEvent(event)) {
			return;
		}

		if (event.isFailed()) {
			getListener().onWebContentFailure(event.getException());
		}
		else {
			getListener().onWebContentReceived(event.getHtml());
		}
	}

	protected JournalArticleService getJournalArticleService() {
		Session session = SessionContext.createSessionFromCurrentSession();
		session.setCallback(
			new WebContentDisplayCallback(getTargetScreenletId()));

		return new JournalArticleService(session);
	}

}