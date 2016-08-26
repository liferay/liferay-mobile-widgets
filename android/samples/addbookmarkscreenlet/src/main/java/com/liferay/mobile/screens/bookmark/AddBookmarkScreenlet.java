package com.liferay.mobile.screens.bookmark;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

import com.liferay.mobile.screens.base.BaseScreenlet;
import com.liferay.mobile.screens.bookmark.interactor.AddBookmarkInteractorImpl;
import com.liferay.mobile.screens.bookmark.interactor.AddBookmarkListener;
import com.liferay.mobile.screens.bookmark.view.AddBookmarkViewModel;

/**
 * @author Javier Gamarra
 */
public class AddBookmarkScreenlet
	extends BaseScreenlet<AddBookmarkViewModel, AddBookmarkInteractorImpl>
	implements AddBookmarkListener {

	public AddBookmarkScreenlet(Context context) {
		super(context);
	}

	public AddBookmarkScreenlet(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public AddBookmarkScreenlet(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	public AddBookmarkScreenlet(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
		super(context, attrs, defStyleAttr, defStyleRes);
	}

	public void onAddBookmarkSuccess() {
		getViewModel().showFinishOperation(null);

		if (_listener != null) {
			_listener.onAddBookmarkSuccess();
		}
	}

	public void onAddBookmarkFailure(Exception e) {
		getViewModel().showFailedOperation(null, e);

		if (_listener != null) {
			_listener.onAddBookmarkFailure(e);
		}
	}

	public long getFolderId() {
		return _folderId;
	}

	public void setFolderId(long folderId) {
		_folderId = folderId;
	}

	public AddBookmarkListener getListener() {
		return _listener;
	}

	public void setListener(AddBookmarkListener listener) {
		_listener = listener;
	}

	@Override
	protected View createScreenletView(Context context, AttributeSet attributes) {
		TypedArray typedArray = context.getTheme().obtainStyledAttributes(attributes, R.styleable.AddBookmarkScreenlet, 0, 0);

		int layoutId = typedArray.getResourceId(R.styleable.AddBookmarkScreenlet_layoutId, 0);

		View view = LayoutInflater.from(context).inflate(layoutId, null);

		String defaultTitle = typedArray.getString(R.styleable.AddBookmarkScreenlet_defaultTitle);

		_folderId = castToLong(typedArray.getString(R.styleable.AddBookmarkScreenlet_folderId));

		typedArray.recycle();

		AddBookmarkViewModel viewModel = (AddBookmarkViewModel) view;
		viewModel.setTitle(defaultTitle);

		return view;
	}

	@Override
	protected AddBookmarkInteractorImpl createInteractor(String actionName) {
		return new AddBookmarkInteractorImpl();
	}

	@Override
	protected void onUserAction(String userActionName, AddBookmarkInteractorImpl interactor, Object... args) {
		AddBookmarkViewModel viewModel = getViewModel();
		String url = viewModel.getURL();
		String title = viewModel.getTitle();

		try {
			interactor.start(url, title, _folderId);
		}
		catch (Exception e) {
			onAddBookmarkFailure(e);
		}
	}

	private long _folderId;
	private AddBookmarkListener _listener;

}
