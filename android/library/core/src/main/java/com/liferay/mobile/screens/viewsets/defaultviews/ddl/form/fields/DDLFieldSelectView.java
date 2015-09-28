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

package com.liferay.mobile.screens.viewsets.defaultviews.ddl.form.fields;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.InputType;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.liferay.mobile.screens.R;
import com.liferay.mobile.screens.ddl.model.StringWithOptionsField;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Jose Manuel Navarro
 */
public class DDLFieldSelectView extends BaseDDLFieldTextView<StringWithOptionsField>
	implements View.OnClickListener {

	public DDLFieldSelectView(Context context) {
		super(context);
	}

	public DDLFieldSelectView(Context context, AttributeSet attributes) {
		super(context, attributes);
	}

	public DDLFieldSelectView(Context context, AttributeSet attributes, int defaultStyle) {
		super(context, attributes, defaultStyle);
	}

	@Override
	public void onClick(View view) {
		createAlertDialog();
		_alertDialog.show();
	}

	@Override
	public void setPositionInParent(int position) {

	}

	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();

		// Avoid WindowLeak error on orientation changes
		if (_alertDialog != null) {
			_alertDialog.dismiss();
			_alertDialog = null;
		}
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();

		EditText editText = getTextEditText();
		editText.setCursorVisible(false);
		editText.setFocusableInTouchMode(false);
		editText.setOnClickListener(this);
		editText.setInputType(InputType.TYPE_NULL);
	}

	@Override
	protected void onTextChanged(String text) {
	}

	protected void createAlertDialog() {
		List<String> labels = getOptionsLabels();

		AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

		LayoutInflater factory = LayoutInflater.from(getContext());
		final View customDialogView = factory.inflate(
			R.layout.ddlfield_select_dialog_default, null);
		TextView title = (TextView) customDialogView.findViewById(R.id.liferay_dialog_title);
		title.setText(getField().getLabel());

		DialogInterface.OnClickListener selectOptionHandler = getAlertDialogListener();

		builder.setCustomTitle(customDialogView);
		builder.setItems(labels.toArray(new String[labels.size()]), selectOptionHandler);

		_alertDialog = builder.create();
	}

	protected DialogInterface.OnClickListener getAlertDialogListener() {
		return new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				getField().selectOption(getField().getAvailableOptions().get(which));
				refresh();
			}
		};
	}

	protected List<String> getOptionsLabels() {
		List<String> result = new ArrayList<>();

		for (StringWithOptionsField.Option opt : getField().getAvailableOptions()) {
			result.add(opt.label);
		}

		return result;
	}
	private AlertDialog _alertDialog;
}