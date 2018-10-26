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

package com.liferay.mobile.screens.webcontent.display.interactor;

import com.liferay.mobile.screens.util.ServiceProvider;
import com.liferay.mobile.screens.webcontent.display.WebContentDisplayScreenlet;
import com.liferay.mobile.screens.webcontent.display.connector.JournalContentConnector;
import com.liferay.mobile.screens.webcontent.display.connector.ScreensJournalContentConnector;
import java.util.Locale;

import static com.liferay.mobile.screens.cache.Cache.SEPARATOR;

/**
 * @author Jose Manuel Navarro
 */
public class WebContentDisplayFromArticleIdInteractor extends WebContentDisplayBaseInteractor {

    @Override
    public WebContentDisplayEvent execute(Object... args) throws Exception {

        String articleId = (String) args[0];
        Long templateId = (Long) args[1];

        validate(groupId, articleId, locale);

        String content = getContent(articleId, templateId);
        return new WebContentDisplayEvent(content);
    }

    @Override
    public void onFailure(WebContentDisplayEvent event) {
        getListener().error(event.getException(), WebContentDisplayScreenlet.WEB_CONTENT_BY_ARTICLE_ID);
    }

    private String getContent(String articleId, Long templateId) throws Exception {
        if (templateId == null || templateId == 0) {
            JournalContentConnector connector = getJournalArticleService();
            return connector.getArticleContent(groupId, articleId, locale.toString(), null);
        } else {
            ScreensJournalContentConnector connector = getScreensJournalArticleService();
            return connector.getJournalArticleContent(groupId, articleId, templateId, locale.toString());
        }
    }

    @Override
    public void onSuccess(WebContentDisplayEvent event) {
        getListener().onWebContentReceived(event.getWebContent());
    }

    @Override
    protected String getIdFromArgs(Object... args) {
        String articleId = (String) args[0];
        Long templateId = (Long) args[1];
        return articleId + (templateId == null ? SEPARATOR : templateId);
    }

    protected JournalContentConnector getJournalArticleService() {
        return ServiceProvider.getInstance().getJournalContentConnector(getSession());
    }

    protected ScreensJournalContentConnector getScreensJournalArticleService() {
        return ServiceProvider.getInstance().getScreensJournalContentConnector(getSession());
    }

    protected void validate(long groupId, String articleId, Locale locale) {
        super.validate(locale);

        if (groupId <= 0) {
            throw new IllegalArgumentException("GroupId cannot be 0 or negative");
        } else if (articleId == null || articleId.isEmpty()) {
            throw new IllegalArgumentException("ArticleId cannot be empty");
        }
    }
}