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

package com.liferay.mobile.screens.ddl.form.interactor.formload;

import com.liferay.mobile.android.service.Session;
import com.liferay.mobile.screens.base.interactor.BaseCachedRemoteInteractor;
import com.liferay.mobile.screens.cache.Cache;
import com.liferay.mobile.screens.cache.DefaultCachedType;
import com.liferay.mobile.screens.cache.OfflinePolicy;
import com.liferay.mobile.screens.cache.ddl.form.DDLFormCache;
import com.liferay.mobile.screens.cache.ddl.form.RecordCache;
import com.liferay.mobile.screens.cache.sql.CacheSQL;
import com.liferay.mobile.screens.context.SessionContext;
import com.liferay.mobile.screens.ddl.form.DDLFormListener;
import com.liferay.mobile.screens.ddl.model.Record;
import com.liferay.mobile.screens.service.v7.DDMStructureService;

import org.json.JSONException;

/**
 * @author Jose Manuel Navarro
 */
public class DDLFormLoadInteractorImpl
	extends BaseCachedRemoteInteractor<DDLFormListener, DDLFormLoadEvent>
	implements DDLFormLoadInteractor {

	public DDLFormLoadInteractorImpl(int targetScreenletId, OfflinePolicy offlinePolicy) {
		super(targetScreenletId, offlinePolicy);
	}

	@Override
	public void load(final Record record) throws Exception {

		validate(record);

		processWithCache(record);
	}

	public void onEvent(DDLFormLoadEvent event) {
		if (!isValidEvent(event)) {
			return;
		}

		onEventWithCache(event, event.getRecord());

		if (!event.isFailed()) {

			try {
				String json = event.getJSONObject().getString("definition");
				long userId = event.getJSONObject().getLong("userId");

				Record formRecord = event.getRecord();

				formRecord.parseJson(json);

				if (formRecord.getCreatorUserId() == 0) {
					formRecord.setCreatorUserId(userId);
				}

				getListener().onDDLFormLoaded(formRecord);
			}
			catch (JSONException e) {
				getListener().onDDLFormLoadFailed(e);
			}
		}
	}

	@Override
	protected void online(Object[] args) throws Exception {

		Record record = (Record) args[0];

		getDDMStructureService(record).getStructure(record.getStructureId());
	}

	@Override
	protected void notifyError(DDLFormLoadEvent event) {
		getListener().onDDLFormLoadFailed(event.getException());
	}

	@Override
	protected boolean cached(Object[] args) throws Exception {

		Record record = (Record) args[0];

		Cache cache = CacheSQL.getInstance();
		RecordCache recordCache = (RecordCache) cache.getById(
			DefaultCachedType.DDL_FORM, String.valueOf(record.getRecordSetId()));

		if (recordCache != null) {
			onEvent(new DDLFormLoadEvent(getTargetScreenletId(), record, recordCache.getJSONContent()));
			return true;
		}
		return false;
	}

	@Override
	protected void storeToCache(DDLFormLoadEvent event, Object... args) {
		CacheSQL.getInstance().set(new DDLFormCache(event.getRecord(), event.getJSONObject()));
	}

	protected DDMStructureService getDDMStructureService(Record record) {
		Session session = SessionContext.createSessionFromCurrentSession();
		session.setCallback(new DDLFormLoadCallback(getTargetScreenletId(), record));
		return new DDMStructureService(session);
	}

	protected void validate(Record record) {
		if (record == null) {
			throw new IllegalArgumentException("record cannot be empty");
		}

		if (record.getStructureId() <= 0) {
			throw new IllegalArgumentException("Record's structureId cannot be 0 or negative");
		}

		if (record.getLocale() == null) {
			throw new IllegalArgumentException("Record's Locale cannot be empty");
		}
	}

}