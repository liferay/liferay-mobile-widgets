package com.liferay.mobile.screens.viewsets.defaultviews.filedisplay;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.VideoView;
import com.liferay.mobile.screens.R;
import com.liferay.mobile.screens.base.BaseScreenlet;
import com.liferay.mobile.screens.filedisplay.BaseFileDisplayViewModel;
import com.liferay.mobile.screens.filedisplay.FileEntry;
import com.liferay.mobile.screens.util.LiferayLogger;

/**
 * @author Sarai Díaz García
 */
public class VideoDisplayView extends LinearLayout implements BaseFileDisplayViewModel {

	public VideoDisplayView(Context context) {
		super(context);
	}

	public VideoDisplayView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public VideoDisplayView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	@Override
	public void showStartOperation(String actionName) {
	}

	@Override
	public void showFinishOperation(String actionName) {
		throw new UnsupportedOperationException("showFinishOperation(String) is not supported."
			+ " Use showFinishOperation(FileEntry) instead.");
	}

	@Override
	public void showFailedOperation(String actionName, Exception e) {
		LiferayLogger.e("Could not load file asset: " + e.getMessage());
	}

	@Override
	public BaseScreenlet getScreenlet() {
		return _screenlet;
	}

	@Override
	public void setScreenlet(BaseScreenlet screenlet) {
		this._screenlet = screenlet;
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();

		_videoView = (VideoView) findViewById(R.id.liferay_video_asset);
	}

	@Override
	public void showFinishOperation(FileEntry fileEntry) {
		_fileEntry = fileEntry;
		loadVideo();
	}

	private void loadVideo() {
		_videoView.setVideoPath(getResources().getString(R.string.liferay_server) + _fileEntry.getUrl());
		_videoView.setMediaController(new MediaController(getContext()));
		_videoView.setZOrderOnTop(true);
		_videoView.requestFocus();
		_videoView.start();
	}

	private BaseScreenlet _screenlet;
	private FileEntry _fileEntry;
	private VideoView _videoView;
}
