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

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;

import com.liferay.mobile.screens.viewsets.defaultviews.DefaultTheme;
import com.liferay.mobile.screens.viewsets.defaultviews.LiferayCrouton;

import de.keyboardsurfer.android.widget.crouton.Crouton;

/**
 * @author Javier Gamarra
 */
public abstract class ThemeActivity extends ActionBarActivity {

	@Override
	protected void onCreate(Bundle state) {
		super.onCreate(state);
		currentTheme = getIntent().getIntExtra("theme", DefaultTheme.getDefaultTheme());

		int color = isDefaultTheme() ? R.color.default_primary_blue : R.color.material_primary;

		new LiferayCrouton.Builder().withInfoColor(color).build();

		setTheme(currentTheme);
	}

	protected void error(String message, Exception e) {
		LiferayCrouton.error(this, message, e);
	}

	protected void info(String message) {
		LiferayCrouton.info(this, message);
	}

	protected Intent getIntentWithTheme(Class destinationClass) {
		Intent intent = new Intent(this, destinationClass);
		intent.putExtra("theme", currentTheme);
		return intent;
	}

	protected View getActiveScreenlet(int defaultId, int materialId) {
		return isDefaultTheme() ? findViewById(defaultId) : findViewById(materialId);
	}

	protected void hideInactiveScreenlet(int defaultId, int materialId) {
		View view = isDefaultTheme() ? findViewById(materialId) : findViewById(defaultId);
		view.setVisibility(View.GONE);
	}

	protected boolean isDefaultTheme() {
		return currentTheme == R.style.default_theme;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		Crouton.clearCroutonsForActivity(this);
	}

	protected Integer currentTheme;
}
