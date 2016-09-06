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

package com.liferay.mobile.screens.base.list;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import com.liferay.mobile.screens.R;
import com.liferay.mobile.screens.base.BaseScreenlet;
import com.liferay.mobile.screens.base.list.view.BaseListViewModel;
import com.liferay.mobile.screens.util.LiferayLogger;
import com.liferay.mobile.screens.viewsets.defaultviews.ddl.list.DividerItemDecoration;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Javier Gamarra
 * @author Silvio Santos
 */
public abstract class BaseListScreenletView<E extends Parcelable, H extends BaseListAdapter.ViewHolder, A extends BaseListAdapter<E, H>>
	extends FrameLayout implements BaseListViewModel<E>, BaseListAdapterListener {

	public BaseListScreenletView(Context context) {
		super(context);
	}

	public BaseListScreenletView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public BaseListScreenletView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	public BaseListScreenletView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
		super(context, attrs, defStyleAttr, defStyleRes);
	}

	@Override
	public void onItemClick(int position, View view) {
		BaseListScreenlet screenlet = (BaseListScreenlet) getScreenlet();
		List<E> entries = getAdapter().getEntries();

		// we do not want to crash if the user manages to do a phantom click
		if (!entries.isEmpty() && entries.size() > position && screenlet.getListener() != null) {
			screenlet.getListener().onListItemSelected(entries.get(position), view);
		}
	}

	@Override
	public void showStartOperation(String actionName) {
		_progressBar.setVisibility(View.VISIBLE);
		_recyclerView.setVisibility(View.GONE);
		LiferayLogger.i("loading list");
	}

	@Override
	public void showFinishOperation(String actionName) {
		throw new AssertionError("Use showFinishOperation(page, entries, rowCount) instead");
	}

	@Override
	public void showFinishOperation(int startRow, int endRow, List<E> serverEntries, int totalRowCount) {
		LiferayLogger.i("loaded page " + startRow + " of list with " + serverEntries);

		_progressBar.setVisibility(View.GONE);
		_recyclerView.setVisibility(View.VISIBLE);

		BaseListScreenlet screenlet = (BaseListScreenlet) getScreenlet();

		A adapter = getAdapter();

		// current entries in the adapter
		List<E> entries = adapter.getEntries();

		// we have to take into account that we start requesting the 2 page (and we don't want the first 2 ones)
		int rowToFill = startRow - _firstRow;
		int realRowCount = totalRowCount - _firstRow;

		if (entries.isEmpty()) {

			_firstRow = rowToFill;
			realRowCount = totalRowCount - _firstRow;
			rowToFill = 0;

			// we fill all entries in the first load
			for (int i = 0; i < realRowCount; i++) {
				entries.add(null);
			}
		}

		if (realRowCount != entries.size()) {
			if (realRowCount > entries.size()) {
				for (int i = entries.size(); i < realRowCount; i++) {
					entries.add(null);
				}
			} else {
				for (int i = realRowCount; i < entries.size(); i++) {
					entries.remove(i);
				}
			}
		}

		for (int i = 0; i < serverEntries.size() && entries.size() > i + rowToFill; i++) {
			entries.set(i + rowToFill, serverEntries.get(i));
		}

		adapter.setRowCount(realRowCount);
		adapter.notifyDataSetChanged();
		adapter.setLabelFields(screenlet.getLabelFields());
	}

	@Override
	public void showFailedOperation(String actionName, Exception e) {
		throw new AssertionError("Use showFinishOperation(page, entries, rowCount) instead");
	}

	@Override
	public void showFinishOperation(int startRow, int endRow, Exception e) {
		_progressBar.setVisibility(View.GONE);
		_recyclerView.setVisibility(View.VISIBLE);
		LiferayLogger.e(getContext().getString(R.string.loading_list_error), e);
	}

	public A getAdapter() {
		return (A) _recyclerView.getAdapter();
	}

	@Override
	public BaseScreenlet getScreenlet() {
		return _screenlet;
	}

	@Override
	public void setScreenlet(BaseScreenlet screenlet) {
		_screenlet = screenlet;
	}

	@Override
	public void onPageNotFound(int row) {
		BaseListScreenlet screenlet = (BaseListScreenlet) getScreenlet();

		screenlet.loadPageForRow(row + _firstRow);
	}

	@Override
	protected void onRestoreInstanceState(Parcelable inState) {
		Bundle state = (Bundle) inState;
		Parcelable superState = state.getParcelable(_STATE_SUPER);

		super.onRestoreInstanceState(superState);

		List<E> entries = state.getParcelableArrayList(_STATE_ENTRIES);

		A adapter = getAdapter();
		adapter.setRowCount(state.getInt(_STATE_ROW_COUNT));
		adapter.getEntries().addAll(entries == null ? new ArrayList<E>() : entries);
		adapter.notifyDataSetChanged();

		List labelFields = state.getStringArrayList(_STATE_LABEL_FIELDS);

		getAdapter().setLabelFields(labelFields);

		_firstRow = state.getInt(_STATE_FIRST_ROW);
	}

	@Override
	protected Parcelable onSaveInstanceState() {
		Parcelable superState = super.onSaveInstanceState();

		A adapter = getAdapter();
		ArrayList<E> entries = (ArrayList<E>) adapter.getEntries();

		Bundle state = new Bundle();
		state.putParcelableArrayList(_STATE_ENTRIES, entries);
		state.putSerializable(_STATE_ROW_COUNT, adapter.getItemCount());
		state.putParcelable(_STATE_SUPER, superState);
		state.putStringArrayList(_STATE_LABEL_FIELDS,
			(ArrayList<String>) ((BaseListScreenlet) getScreenlet()).getLabelFields());
		state.putInt(_STATE_FIRST_ROW, _firstRow);

		return state;
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();

		int itemLayoutId = getItemLayoutId();
		int itemProgressLayoutId = getItemProgressLayoutId();

		_recyclerView = (RecyclerView) findViewById(R.id.liferay_recycler_list);
		_progressBar = (ProgressBar) findViewById(R.id.liferay_progress);

		A adapter = createListAdapter(itemLayoutId, itemProgressLayoutId);
		_recyclerView.setHasFixedSize(true);
		_recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
		_recyclerView.setAdapter(adapter);

		RecyclerView.ItemDecoration dividerItemDecoration = getDividerDecoration();
		if (dividerItemDecoration != null) {
			_recyclerView.addItemDecoration(getDividerDecoration());
		}
	}

	protected RecyclerView.ItemDecoration getDividerDecoration() {
		return new DividerItemDecoration(ContextCompat.getDrawable(getContext(), R.drawable.pixel_grey));
	}

	protected int getItemLayoutId() {
		return R.layout.list_item_default;
	}

	protected int getItemProgressLayoutId() {
		return R.layout.list_item_progress_default;
	}

	protected ProgressBar getProgressBar() {
		return _progressBar;
	}

	protected void setProgressBar(ProgressBar value) {
		_progressBar = value;
	}

	protected RecyclerView getRecyclerView() {
		return _recyclerView;
	}

	protected void setRecyclerView(RecyclerView value) {
		_recyclerView = value;
	}

	protected abstract A createListAdapter(int itemLayoutId, int itemProgressLayoutId);

	protected int _firstRow = 0;
	protected ProgressBar _progressBar;
	protected RecyclerView _recyclerView;
	private static final String _STATE_ENTRIES = "entries";
	private static final String _STATE_ROW_COUNT = "rowCount";
	private static final String _STATE_SUPER = "super";
	private static final String _STATE_FIRST_ROW = "firstRow";
	private static final String _STATE_LABEL_FIELDS = "label_fields";
	private BaseScreenlet _screenlet;
}