package com.liferay.mobile.screens.testapp;

import android.os.Bundle;
import com.liferay.mobile.screens.assetdisplay.AssetDisplayListener;
import com.liferay.mobile.screens.assetdisplay.AssetDisplayScreenlet;
import com.liferay.mobile.screens.assetlist.AssetEntry;
import com.liferay.mobile.screens.context.SessionContext;

/**
 * @author Sarai Díaz García
 */
public class AssetDisplayActivity extends ThemeActivity implements AssetDisplayListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.asset_display);

		_screenlet = ((AssetDisplayScreenlet) findViewById(R.id.asset_display_screenlet));

		_screenlet.setEntryId(getIntent().getLongExtra("entryId", 0));
		_screenlet.setAutoLoad(true);
		_screenlet.setListener(this);

	}

	@Override
	protected void onResume() {
		super.onResume();

		_screenlet.loadAsset();
	}

	@Override
	public void onRetrieveAssetFailure(Exception e) {
		error("Could not receive asset entry", e);
	}

	@Override
	public void onRetrieveAssetSuccess(AssetEntry assetEntry) {
		info("Asset entry received! -> " + assetEntry.getTitle());
	}

	private AssetDisplayScreenlet _screenlet;
}
