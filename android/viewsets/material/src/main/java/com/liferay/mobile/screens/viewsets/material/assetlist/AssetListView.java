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

package com.liferay.mobile.screens.viewsets.material.assetlist;

import android.content.Context;
import android.util.AttributeSet;
import com.liferay.mobile.screens.asset.AssetEntry;
import com.liferay.mobile.screens.asset.list.view.AssetListViewModel;
import com.liferay.mobile.screens.base.list.BaseListScreenletView;
import com.liferay.mobile.screens.viewsets.R;
import com.liferay.mobile.screens.viewsets.defaultviews.asset.list.AssetListAdapter;

/**
 * @author Silvio Santos
 */
public class AssetListView extends BaseListScreenletView<AssetEntry, AssetListAdapter.ViewHolder, AssetListAdapter>
    implements AssetListViewModel {

    public AssetListView(Context context) {
        super(context);
    }

    public AssetListView(Context context, AttributeSet attributes) {
        super(context, attributes);
    }

    public AssetListView(Context context, AttributeSet attributes, int defaultStyle) {
        super(context, attributes, defaultStyle);
    }

    @Override
    protected AssetListAdapter createListAdapter(int itemLayoutId, int itemProgressLayoutId) {
        return new AssetListAdapter(itemLayoutId, itemProgressLayoutId, this);
    }

    @Override
    protected int getItemLayoutId() {
        return R.layout.list_item_material;
    }

    @Override
    protected int getItemProgressLayoutId() {
        return R.layout.list_item_progress_material;
    }
}