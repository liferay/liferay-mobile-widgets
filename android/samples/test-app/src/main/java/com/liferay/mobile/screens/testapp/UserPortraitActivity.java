/*
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

package com.liferay.mobile.screens.testapp;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.view.View;
import com.liferay.mobile.screens.userportrait.UserPortraitListener;
import com.liferay.mobile.screens.userportrait.UserPortraitScreenlet;

/**
 * @author Javier Gamarra
 */
public class UserPortraitActivity extends ThemeActivity implements UserPortraitListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.user_portrait);

		_screenlet = (UserPortraitScreenlet) findViewById(R.id.user_portrait_screenlet);
		_screenlet.setListener(this);
	}

	@Override
	protected void onResume() {
		super.onResume();

		_screenlet.load();
	}

	@Override
	public Bitmap onUserPortraitLoadReceived(Bitmap bitmap) {
		info("User portrait received!");

		return null;
	}

	@Override
	public void onUserPortraitUploaded() {

	}

	@Override
	public void loadingFromCache(boolean success) {
		View content = findViewById(android.R.id.content);
		Snackbar.make(content, "Trying to load from cache: " + success, Snackbar.LENGTH_SHORT).show();
	}

	@Override
	public void retrievingOnline(boolean triedInCache, Exception e) {

	}

	@Override
	public void storingToCache(Object object) {
		View content = findViewById(android.R.id.content);
		Snackbar.make(content, "Storing to cache...", Snackbar.LENGTH_SHORT).show();
	}

	@Override
	public void error(Exception e, String userAction) {

	}

	private UserPortraitScreenlet _screenlet;
}
