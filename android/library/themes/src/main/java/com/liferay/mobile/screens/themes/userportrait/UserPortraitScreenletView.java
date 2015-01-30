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

package com.liferay.mobile.screens.themes.userportrait;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.liferay.mobile.screens.base.view.BaseViewModel;
import com.liferay.mobile.screens.userportrait.UserPortraitListener;

/**
 * @author Javier Gamarra
 * @author Jose Manuel Navarro
 */
public class UserPortraitScreenletView extends ImageView
	implements BaseViewModel, UserPortraitListener {

	public UserPortraitScreenletView(Context context) {
		this(context, null);
	}

	public UserPortraitScreenletView(
		Context context, AttributeSet attributes) {

		this(context, attributes, 0);
	}

	public UserPortraitScreenletView(
		Context context, AttributeSet attributes, int defaultStyle) {

		super(context, attributes, defaultStyle);
	}


	@Override
	public String onUserPortraitReceived(Bitmap bitmap) {
		setImageBitmap(bitmap);
		return null;
	}

	@Override
	public void onUserPortraitFailure(Exception e) {
		//TODO show default image??
	}
}