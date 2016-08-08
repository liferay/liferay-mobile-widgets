package com.liferay.mobile.screens.rating;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import com.liferay.mobile.screens.R;
import com.liferay.mobile.screens.base.BaseScreenlet;
import com.liferay.mobile.screens.base.interactor.Interactor;
import com.liferay.mobile.screens.context.SessionContext;
import com.liferay.mobile.screens.rating.interactor.delete.RatingDeleteInteractor;
import com.liferay.mobile.screens.rating.interactor.delete.RatingDeleteInteractorImpl;
import com.liferay.mobile.screens.rating.interactor.load.RatingLoadInteractor;
import com.liferay.mobile.screens.rating.interactor.load.RatingLoadInteractorImpl;
import com.liferay.mobile.screens.rating.interactor.update.RatingUpdateInteractor;
import com.liferay.mobile.screens.rating.interactor.update.RatingUpdateInteractorImpl;
import com.liferay.mobile.screens.rating.view.RatingViewModel;
import com.liferay.mobile.screens.util.LiferayLogger;

/**
 * @author Alejandro Hernández
 */

public class RatingScreenlet extends BaseScreenlet<RatingViewModel, Interactor> implements RatingListener {

	public static final String LOAD_RATINGS_ACTION = "LOAD_RATINGS";
	public static final String UPDATE_RATING_ACTION = "UPDATE_RATING";
	public static final String DELETE_RATING_ACTION = "DELETE_RATING";

	public RatingScreenlet(Context context) {
		super(context);
	}

	public RatingScreenlet(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public RatingScreenlet(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	public RatingScreenlet(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
		super(context, attrs, defStyleAttr, defStyleRes);
	}

	public void enableEdition(boolean editable) {
		getViewModel().enableEdition(editable);
	}

	@Override
	protected void onScreenletAttached() {
		super.onScreenletAttached();

		if (autoLoad) {
			autoLoad();
		}
	}

	@Override
	protected View createScreenletView(Context context, AttributeSet attributes) {
		TypedArray typedArray =
			context.getTheme().obtainStyledAttributes(attributes, R.styleable.RatingScreenlet, 0, 0);

		int layoutId = typedArray.getResourceId(R.styleable.RatingScreenlet_layoutId, 0);

		autoLoad = typedArray.getBoolean(R.styleable.RatingScreenlet_autoLoad, true);
		editable = typedArray.getBoolean(R.styleable.RatingScreenlet_editable, true);

		RatingViewModel view = (RatingViewModel) LayoutInflater.from(context).inflate(layoutId, null);
		view.enableEdition(editable);

		entryId = castToLong(typedArray.getString(R.styleable.RatingScreenlet_entryId));
		className = typedArray.getString(R.styleable.RatingScreenlet_className);
		classPK = castToLong(typedArray.getString(R.styleable.RatingScreenlet_classPK));

		ratingsGroupCount = typedArray.getInt(R.styleable.RatingScreenlet_ratingsGroupCount, 2);

		typedArray.recycle();

		return (View) view;
	}

	@Override
	protected Interactor createInteractor(String actionName) {
		switch (actionName) {
			case LOAD_RATINGS_ACTION:
				return new RatingLoadInteractorImpl(getScreenletId());
			case DELETE_RATING_ACTION:
				return new RatingDeleteInteractorImpl(getScreenletId());
			case UPDATE_RATING_ACTION:
				return new RatingUpdateInteractorImpl(getScreenletId());
			default:
				return null;
		}
	}

	@Override
	protected void onUserAction(String userActionName, Interactor interactor, Object... args) {
		try {
			switch (userActionName) {
				case LOAD_RATINGS_ACTION:
					((RatingLoadInteractor) interactor).loadRatings(entryId, classPK, className, ratingsGroupCount);
					break;
				case UPDATE_RATING_ACTION:
					double score = (double) args[0];
					((RatingUpdateInteractor) interactor).updateRating(classPK, className, score, ratingsGroupCount);
					break;
				case DELETE_RATING_ACTION:
					((RatingDeleteInteractor) interactor).deleteRating(classPK, className, ratingsGroupCount);
					break;
				default:
					break;
			}
		} catch (Exception e) {
			LiferayLogger.e(e.getMessage());
			onRatingOperationFailure(e);
		}
	}

	protected void autoLoad() {
		if (SessionContext.isLoggedIn()) {
			try {
				load();
			} catch (Exception e) {
				onRatingOperationFailure(e);
			}
		}
	}

	public void load() {
		performUserAction(LOAD_RATINGS_ACTION);
	}

	@Override
	public void onRatingOperationFailure(Exception exception) {
		getViewModel().showFailedOperation(null, exception);

		if (listener != null) {
			listener.onRatingOperationFailure(exception);
		}
	}

	@Override
	public void onRatingOperationSuccess(AssetRating assetRating) {

		getViewModel().showFinishOperation(null, assetRating);

		classPK = assetRating.getClassPK();
		className = assetRating.getClassName();

		if (listener != null) {
			listener.onRatingOperationSuccess(assetRating);
		}
	}

	public RatingListener getListener() {
		return listener;
	}

	public void setListener(RatingListener listener) {
		this.listener = listener;
	}

	public boolean isAutoLoad() {
		return autoLoad;
	}

	public void setAutoLoad(boolean autoLoad) {
		this.autoLoad = autoLoad;
	}

	public long getEntryId() {
		return entryId;
	}

	public void setEntryId(long entryId) {
		this.entryId = entryId;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public long getClassPK() {
		return classPK;
	}

	public void setClassPK(long classPK) {
		this.classPK = classPK;
	}

	public boolean isEditable() {
		return editable;
	}

	public int getRatingsGroupCount() {
		return ratingsGroupCount;
	}

	public void setRatingsGroupCount(int ratingsGroupCount) {
		this.ratingsGroupCount = ratingsGroupCount;
	}

	private RatingListener listener;
	private long entryId;
	private int ratingsGroupCount;
	private boolean autoLoad;
	private boolean editable;
	private String className;
	private long classPK;
}
