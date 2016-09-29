package com.liferay.mobile.screens.comment.display.interactor.delete;

import com.liferay.mobile.android.v7.commentmanagerjsonws.CommentmanagerjsonwsService;
import com.liferay.mobile.screens.base.interactor.BaseCacheWriteInteractor;
import com.liferay.mobile.screens.comment.display.CommentDisplayScreenlet;
import com.liferay.mobile.screens.comment.display.interactor.CommentDisplayInteractorListener;
import com.liferay.mobile.screens.comment.display.interactor.CommentEvent;

/**
 * @author Alejandro Hernández
 */
public class CommentDeleteInteractor extends BaseCacheWriteInteractor<CommentDisplayInteractorListener, CommentEvent> {

	@Override
	public CommentEvent execute(CommentEvent event) throws Exception {

		long commentId = event.getCommentId();

		validate(commentId);

		CommentmanagerjsonwsService service = new CommentmanagerjsonwsService(getSession());

		service.deleteComment(commentId);

		return event;
	}

	@Override
	public void onSuccess(CommentEvent event) throws Exception {
		getListener().onDeleteCommentSuccess();
	}

	@Override
	protected void onFailure(CommentEvent event) {
		getListener().error(event.getException(), CommentDisplayScreenlet.DELETE_COMMENT_ACTION);
	}

	protected void validate(long commentId) {
		if (commentId <= 0) {
			throw new IllegalArgumentException("commentId cannot be 0 or negative");
		}
	}
}
