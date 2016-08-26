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

package com.liferay.mobile.screens.base.thread.event;

import org.json.JSONObject;

public class BasicThreadEvent {

	public BasicThreadEvent() {
		super();
	}

	public BasicThreadEvent(JSONObject jsonObject) {
		super();
		_jsonObject = jsonObject;
	}

	public BasicThreadEvent(Exception exception) {
		_exception = exception;
	}

	public Exception getException() {
		return _exception;
	}

	public int getTargetScreenletId() {
		return _targetScreenletId;
	}

	public void setJSONObject(JSONObject jsonObject) {
		_jsonObject = jsonObject;
	}

	public void setTargetScreenletId(int targetScreenletId) {
		_targetScreenletId = targetScreenletId;
	}

	public boolean isFailed() {
		return _exception != null;
	}

	public JSONObject getJSONObject() {
		return _jsonObject;
	}

	private JSONObject _jsonObject;
	private Exception _exception;
	private int _targetScreenletId;
}