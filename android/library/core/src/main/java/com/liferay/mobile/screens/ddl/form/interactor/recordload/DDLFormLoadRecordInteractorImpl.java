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

package com.liferay.mobile.screens.ddl.form.interactor.recordload;

import com.liferay.mobile.android.service.Session;
import com.liferay.mobile.screens.base.interactor.BaseCachedRemoteInteractor;
import com.liferay.mobile.screens.cache.Cache;
import com.liferay.mobile.screens.cache.CachePolicy;
import com.liferay.mobile.screens.cache.DefaultCachedType;
import com.liferay.mobile.screens.cache.ddl.form.DDLRecordCache;
import com.liferay.mobile.screens.cache.sql.CacheSQL;
import com.liferay.mobile.screens.context.SessionContext;
import com.liferay.mobile.screens.ddl.form.DDLFormListener;
import com.liferay.mobile.screens.ddl.model.Record;
import com.liferay.mobile.screens.service.v62.ScreensddlrecordService;
import com.liferay.mobile.screens.util.JSONUtil;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author Jose Manuel Navarro
 */
public class DDLFormLoadRecordInteractorImpl
	extends BaseCachedRemoteInteractor<DDLFormListener, DDLFormLoadRecordEvent>
	implements DDLFormLoadRecordInteractor {

	public DDLFormLoadRecordInteractorImpl(int targetScreenletId, CachePolicy cachePolicy) {
		super(targetScreenletId, cachePolicy);
	}

	@Override
	public void loadRecord(final Record record) throws Exception {

		validate(record);

		processWithCache(record);
	}

	public void onEvent(DDLFormLoadRecordEvent event) {
		if (!isValidEvent(event)) {
			return;
		}

		onEventWithCache(event);

		if (!event.isFailed()) {
			try {
				JSONObject jsonObject = event.getJSONObject();
				event.getRecord().setValuesAndAttributes(JSONUtil.toMap(jsonObject));
				event.getRecord().refresh();

				getListener().onDDLFormRecordLoaded(event.getRecord());
			}
			catch (JSONException e) {
				getListener().onDDLFormRecordLoadFailed(e);
			}
		}
	}

	@Override
	protected void online(Object[] args) throws Exception {

		Record record = (Record) args[0];

		getDDLRecordService(record).getDdlRecord(
			record.getRecordId(), record.getLocale().toString());
	}

	@Override
	protected void notifyError(DDLFormLoadRecordEvent event) {
		getListener().onDDLFormRecordLoadFailed(event.getException());
	}

	@Override
	protected boolean cached(Object[] args) throws Exception {

		Record record = (Record) args[0];

		Cache cache = CacheSQL.getInstance();
		DDLRecordCache recordCache = (DDLRecordCache) cache.getById(
			DefaultCachedType.DDL_RECORD, String.valueOf(record.getRecordId()));

		if (recordCache != null) {
			onEvent(new DDLFormLoadRecordEvent(getTargetScreenletId(), record, recordCache.getJSONContent()));
			return true;
		}
		return false;
	}

	@Override
	protected void storeToCache(DDLFormLoadRecordEvent event, Object... args) {
		CacheSQL.getInstance().set(new DDLRecordCache(null, event.getRecord(), event.getJSONObject()));
	}

	protected ScreensddlrecordService getDDLRecordService(Record record) {
		Session session = SessionContext.createSessionFromCurrentSession();

		session.setCallback(new DDLFormLoadRecordCallback(getTargetScreenletId(), record));

		return new ScreensddlrecordService(session);
	}

	protected void validate(Record record) {
		if (record == null) {
			throw new IllegalArgumentException("record cannot be empty");
		}

		if (record.getRecordId() <= 0) {
			throw new IllegalArgumentException("Record's recordId cannot be 0 or negative");
		}
	}

}