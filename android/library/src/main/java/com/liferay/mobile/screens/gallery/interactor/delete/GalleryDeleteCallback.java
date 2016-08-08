package com.liferay.mobile.screens.gallery.interactor.delete;

import com.liferay.mobile.screens.base.interactor.BasicEvent;
import com.liferay.mobile.screens.base.interactor.JSONObjectCallback;
import org.json.JSONObject;

public class GalleryDeleteCallback extends JSONObjectCallback {

	public GalleryDeleteCallback(int targetScreenletId, long imageEntryId) {
		super(targetScreenletId);

		this.imageEntryId = imageEntryId;
	}

	@Override
	public JSONObject transform(Object obj) throws Exception {
		return new JSONObject();
	}

	@Override
	protected BasicEvent createEvent(int targetScreenletId, JSONObject result) {
		return new GalleryDeleteEvent(targetScreenletId, imageEntryId);
	}

	@Override
	protected BasicEvent createEvent(int targetScreenletId, Exception e) {
		return new GalleryDeleteEvent(targetScreenletId, e);
	}

	private long imageEntryId;
}
