package com.liferay.mobile.screens.bookmark.interactor;

import com.liferay.mobile.screens.base.interactor.BaseRemoteInteractor;
import com.liferay.mobile.screens.util.EventBusUtil;
import com.liferay.mobile.screens.util.LiferayLogger;
import com.squareup.okhttp.Headers;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;

import java.io.IOException;

/**
 * @author Javier Gamarra
 */
public class AddDeliciousInteractorImpl
	extends BaseRemoteInteractor<AddBookmarkListener>
	implements AddBookmarkInteractor {

	public AddDeliciousInteractorImpl(int targetScreenletId) {
		super(targetScreenletId);
	}

	public void addBookmark(final String url, final String title, long folderId) throws Exception {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {

					Headers headers = Headers.of("Authorization", "Bearer 11336429-c2378f3c44a29f593e31aa4f5521e4dd");

					OkHttpClient client = new OkHttpClient();

					Request add = new Request.Builder()
						.url("https://api.del.icio.us/api/v1/posts/add?url=" + url + "&description=" + title)
						.headers(headers)
						.build();

					client.newCall(add).execute();

					Request get = new Request.Builder()
						.url("https://api.del.icio.us/api/v1/posts/get")
						.headers(headers)
						.build();

					com.squareup.okhttp.Response response = client.newCall(get).execute();

					String text = response.body().string();

					if (text.contains(url)) {
						LiferayLogger.i(text);
						EventBusUtil.post(new BookmarkAdded(text));
					}
					else {
						EventBusUtil.post(new BookmarkAdded(new Exception("sth went wrong")));
					}

				}
				catch (IOException e) {
					LiferayLogger.e("Error sending", e);
					EventBusUtil.post(new BookmarkAdded(e));
				}
			}
		}).start();
	}

	public void onEvent(BookmarkAdded bookmarkAdded) {
		if (bookmarkAdded.hasError()) {
			getListener().onAddBookmarkFailure(bookmarkAdded.getE());
		}
		else {
			getListener().onAddBookmarkSuccess();
		}
	}

	class BookmarkAdded {

		public BookmarkAdded(String text) {
			this.text = text;
		}

		public BookmarkAdded(Exception e) {
			this._e = e;
		}

		public boolean hasError() {
			return _e != null;
		}

		public Exception getE() {
			return _e;
		}

		private String text;
		private Exception _e;
	}

}

