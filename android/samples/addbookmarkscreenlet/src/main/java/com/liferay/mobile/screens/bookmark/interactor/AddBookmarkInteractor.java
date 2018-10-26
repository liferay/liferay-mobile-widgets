package com.liferay.mobile.screens.bookmark.interactor;

import android.support.annotation.NonNull;
import android.webkit.URLUtil;
import com.liferay.mobile.screens.base.interactor.BaseRemoteInteractor;
import com.liferay.mobile.screens.base.interactor.event.BasicEvent;
import com.liferay.mobile.screens.bookmark.interactor.v62.BookmarksEntryService;
import com.liferay.mobile.screens.context.LiferayServerContext;
import org.json.JSONObject;

/**
 * @author Javier Gamarra
 */
public class AddBookmarkInteractor extends BaseRemoteInteractor<AddBookmarkListener, BasicEvent> {

    @Override
    public BasicEvent execute(Object[] args) throws Exception {
        String url = (String) args[0];
        String title = (String) args[1];
        long folderId = (long) args[2];

        validate(url, folderId);

        JSONObject jsonObject = getJSONObject(url, title, folderId);
        return new BasicEvent(jsonObject);
    }

    @Override
    public void onSuccess(BasicEvent event) {
        getListener().onAddBookmarkSuccess();
    }

    @Override
    public void onFailure(BasicEvent event) {
        getListener().onAddBookmarkFailure(event.getException());
    }

    private void validate(String url, long folderId) {
        if (url == null || url.isEmpty() || !URLUtil.isValidUrl(url)) {
            throw new IllegalArgumentException("Invalid url");
        } else if (folderId < 0) {
            throw new IllegalArgumentException("Invalid folderId");
        }
    }

    @NonNull
    private JSONObject getJSONObject(String url, String title, long folderId) throws Exception {
        if (LiferayServerContext.isLiferay7()) {
            return new BookmarksEntryService(getSession()).addEntry(LiferayServerContext.getGroupId(), folderId, title,
                url, "", null);
        } else {
            return new com.liferay.mobile.screens.bookmark.interactor.v62.BookmarksEntryService(getSession()).addEntry(
                LiferayServerContext.getGroupId(), folderId, title, url, "", null);
        }
    }
}

