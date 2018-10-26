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

package com.liferay.mobile.screens.service.v70;

import com.liferay.mobile.android.service.BaseService;
import com.liferay.mobile.android.service.JSONObjectWrapper;
import com.liferay.mobile.android.service.Session;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author Bruno Farache
 */
public class ScreensassetentryService extends BaseService {

    public ScreensassetentryService(Session session) {
        super(session);
    }

    public JSONObject getAssetEntry(String className, long classPK, String locale) throws Exception {
        JSONObject _command = new JSONObject();

        try {
            JSONObject _params = new JSONObject();

            _params.put("className", checkNull(className));
            _params.put("classPK", classPK);
            _params.put("locale", checkNull(locale));

            _command.put("/screens.screensassetentry/get-asset-entry", _params);
        } catch (JSONException _je) {
            throw new Exception(_je);
        }

        JSONArray _result = session.invoke(_command);

        if (_result == null) {
            return null;
        }

        return _result.getJSONObject(0);
    }

    public JSONObject getAssetEntry(long entryId, String locale) throws Exception {
        JSONObject _command = new JSONObject();

        try {
            JSONObject _params = new JSONObject();

            _params.put("entryId", entryId);
            _params.put("locale", checkNull(locale));

            _command.put("/screens.screensassetentry/get-asset-entry", _params);
        } catch (JSONException _je) {
            throw new Exception(_je);
        }

        JSONArray _result = session.invoke(_command);

        if (_result == null) {
            return null;
        }

        return _result.getJSONObject(0);
    }

    public JSONArray getAssetEntries(long companyId, long groupId, String portletItemName, String locale, int max)
        throws Exception {
        JSONObject _command = new JSONObject();

        try {
            JSONObject _params = new JSONObject();

            _params.put("companyId", companyId);
            _params.put("groupId", groupId);
            _params.put("portletItemName", checkNull(portletItemName));
            _params.put("locale", checkNull(locale));
            _params.put("max", max);

            _command.put("/screens.screensassetentry/get-asset-entries", _params);
        } catch (JSONException _je) {
            throw new Exception(_je);
        }

        JSONArray _result = session.invoke(_command);

        if (_result == null) {
            return null;
        }

        return _result.getJSONArray(0);
    }

    public JSONArray getAssetEntries(JSONObjectWrapper assetEntryQuery, String locale) throws Exception {
        JSONObject _command = new JSONObject();

        try {
            JSONObject _params = new JSONObject();

            mangleWrapper(_params, "assetEntryQuery", "com.liferay.asset.kernel.service.persistence.AssetEntryQuery",
                assetEntryQuery);
            _params.put("locale", checkNull(locale));

            _command.put("/screens.screensassetentry/get-asset-entries", _params);
        } catch (JSONException _je) {
            throw new Exception(_je);
        }

        JSONArray _result = session.invoke(_command);

        if (_result == null) {
            return null;
        }

        return _result.getJSONArray(0);
    }
}