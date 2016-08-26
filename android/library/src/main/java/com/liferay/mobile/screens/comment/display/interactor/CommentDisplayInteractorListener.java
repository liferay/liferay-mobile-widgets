package com.liferay.mobile.screens.comment.display.interactor;

import com.liferay.mobile.screens.base.thread.listener.OfflineListenerNew;
import com.liferay.mobile.screens.comment.CommentEntry;

/**
 * @author Alejandro Hernández
 */
public interface CommentDisplayInteractorListener extends OfflineListenerNew {

	void onLoadCommentSuccess(CommentEntry commentEntry);

	void onDeleteCommentSuccess();

	void onUpdateCommentSuccess(CommentEntry commentEntry);
}
