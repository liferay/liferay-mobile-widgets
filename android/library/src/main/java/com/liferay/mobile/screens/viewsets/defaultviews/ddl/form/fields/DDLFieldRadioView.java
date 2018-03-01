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

import android.content.Context;
import android.graphics.Typeface;
import android.os.Build;
import android.support.v7.widget.AppCompatRadioButton;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import com.liferay.mobile.screens.R;
import com.liferay.mobile.screens.ddl.form.view.DDLFieldViewModel;
import com.liferay.mobile.screens.ddl.model.StringWithOptionsField;
import java.util.List;

/**
 * @author Jose Manuel Navarro
 */
public class DDLFieldRadioView extends RadioGroup
	implements DDLFieldViewModel<StringWithOptionsField>, CompoundButton.OnCheckedChangeListener {

	protected View parentView;
	private StringWithOptionsField field;

	public DDLFieldRadioView(Context context) {
		super(context);
	}

	public DDLFieldRadioView(Context context, AttributeSet attributes) {
		super(context, attributes);
	}

	@Override
	public StringWithOptionsField getField() {
		return field;
	}

	@Override
	public void setField(StringWithOptionsField field) {
		this.field = field;

		if (this.field.isShowLabel()) {
			TextView label = (TextView) findViewById(R.id.liferay_ddl_label);

			label.setText(field.getLabel());
			label.setVisibility(VISIBLE);
		}

		LayoutParams layoutParams =
			new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

		List<StringWithOptionsField.Option> availableOptions = field.getAvailableOptions();

		for (int i = 0; i < availableOptions.size(); ++i) {
			StringWithOptionsField.Option opt = availableOptions.get(i);

			AppCompatRadioButton radioButton = new AppCompatRadioButton(getContext());
			radioButton.setLayoutParams(layoutParams);
			radioButton.setText(opt.label);
			radioButton.setTag(opt);
			radioButton.setOnCheckedChangeListener(this);
			radioButton.setTypeface(getTypeface());
			radioButton.setSaveEnabled(true);
			addView(radioButton);
		}

		refresh();
	}

	@Override
	public void refresh() {
		List<StringWithOptionsField.Option> selectedOptions = field.getCurrentValue();

		if (selectedOptions != null) {
			for (StringWithOptionsField.Option opt : selectedOptions) {
				AppCompatRadioButton radioButton = (AppCompatRadioButton) findViewWithTag(opt);

				if (radioButton != null) {
					radioButton.setChecked(true);
				}
			}
		}
	}

	@Override
	public void onPostValidation(boolean valid) {
		String errorText = valid ? null : getContext().getString(R.string.required_value);

		if (field.isShowLabel()) {
			TextView label = (TextView) findViewById(R.id.liferay_ddl_label);
			label.setError(errorText);
		} else {
			List<StringWithOptionsField.Option> availableOptions = field.getAvailableOptions();
			StringWithOptionsField.Option opt = availableOptions.get(0);
			AppCompatRadioButton radioButton = (AppCompatRadioButton) findViewWithTag(opt);
			if (radioButton != null) {
				radioButton.setError(errorText);
			}
		}
	}

	@Override
	public View getParentView() {
		return parentView;
	}

	@Override
	public void setParentView(View view) {
		parentView = view;
	}

	@Override
	public void setUpdateMode(boolean enabled) {
		setEnabled(enabled);
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		AppCompatRadioButton radioButton = (AppCompatRadioButton) buttonView;

		StringWithOptionsField.Option opt = (StringWithOptionsField.Option) radioButton.getTag();
		if (isChecked) {
			field.selectOption(opt);
		} else {
			field.clearOption(opt);
		}
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();

		setSaveEnabled(true);
	}

	private Typeface getTypeface() {
		//FIXME replace with constructor with styles when we have the drawables
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
			return Typeface.DEFAULT;
		}
		return Typeface.create("sans-serif-light", Typeface.NORMAL);
	}
}
