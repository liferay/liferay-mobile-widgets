/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 * <p/>
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * <p/>
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.mobile.screens.ddl.form.interactor.add;

import com.liferay.mobile.android.service.JSONObjectWrapper;
import com.liferay.mobile.screens.base.interactor.BaseCacheWriteInteractor;
import com.liferay.mobile.screens.ddl.form.DDLFormListener;
import com.liferay.mobile.screens.ddl.form.DDLFormScreenlet;
import com.liferay.mobile.screens.ddl.form.connector.DDLRecordConnector;
import com.liferay.mobile.screens.ddl.form.interactor.DDLFormEvent;
import com.liferay.mobile.screens.ddl.model.Record;
import com.liferay.mobile.screens.util.ServiceProvider;
import org.json.JSONObject;

/**
 * @author Jose Manuel Navarro
 */
public class DDLFormAddRecordInteractor extends BaseCacheWriteInteractor<DDLFormListener, DDLFormEvent> {

    @Override
    public DDLFormEvent execute(DDLFormEvent event) throws Exception {

        validate(event.getGroupId(), event.getRecord());

        DDLRecordConnector ddlRecordConnector = ServiceProvider.getInstance().getDDLRecordConnector(getSession());

        Record record = event.getRecord();

        final JSONObject fieldsValues = new JSONObject(record.getData());

        final JSONObject serviceContextAttributes = new JSONObject();
        serviceContextAttributes.put("userId", record.getCreatorUserId());
        serviceContextAttributes.put("scopeGroupId", event.getGroupId());

        JSONObjectWrapper serviceContextWrapper = new JSONObjectWrapper(serviceContextAttributes);

        JSONObject jsonObject =
            ddlRecordConnector.addRecord(event.getGroupId(), record.getRecordSetId(), 0, fieldsValues,
                serviceContextWrapper);

        event.setJSONObject(jsonObject);

        return event;
    }

    @Override
    public void onSuccess(DDLFormEvent event) {
        if (event.getJSONObject().has("recordId")) {
            //This is opt long because we check that this key exists
            long recordId = event.getJSONObject().optLong("recordId");
            event.getRecord().setRecordId(recordId);
        }
        getListener().onDDLFormRecordAdded(event.getRecord());
    }

    @Override
    public void onFailure(DDLFormEvent event) {
        getListener().error(event.getException(), DDLFormScreenlet.ADD_RECORD_ACTION);
    }

    protected void validate(long groupId, Record record) {
        if (groupId <= 0) {
            throw new IllegalArgumentException("groupId cannot be 0 or negative");
        } else if (record == null) {
            throw new IllegalArgumentException("record cannot be empty");
        } else if (record.getFieldCount() == 0) {
            throw new IllegalArgumentException("Record's fields cannot be empty");
        } else if (record.getCreatorUserId() <= 0) {
            throw new IllegalArgumentException("Record's userId cannot be 0 or negative");
        } else if (record.getRecordSetId() <= 0) {
            throw new IllegalArgumentException("Record's recordSetId cannot be 0 or negative");
        }
    }
}