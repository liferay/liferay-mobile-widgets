package com.liferay.mobile.screens.comment.display.interactor.load;

import com.liferay.mobile.android.service.Session;
import com.liferay.mobile.screens.base.thread.BaseRemoteInteractorNew;
import com.liferay.mobile.screens.comment.CommentEntry;
import com.liferay.mobile.screens.comment.display.CommentDisplayScreenlet;
import com.liferay.mobile.screens.comment.display.interactor.CommentDisplayInteractorListener;
import com.liferay.mobile.screens.context.SessionContext;
import com.liferay.mobile.screens.service.v70.CommentmanagerjsonwsService;
import com.liferay.mobile.screens.util.JSONUtil;
import org.json.JSONObject;

/**
 * @author Alejandro Hernández
 */
public class CommentLoadInteractorImpl
	extends BaseRemoteInteractorNew<CommentDisplayInteractorListener, CommentLoadEvent> {

	@Override
	public CommentLoadEvent execute(Object... args) throws Exception {

		long groupId = (long) args[0];
		long commentId = (long) args[1];

		validate(groupId, commentId);

		Session session = SessionContext.createSessionFromCurrentSession();
		CommentmanagerjsonwsService commentService = new CommentmanagerjsonwsService(session);
		JSONObject jsonObject = commentService.getComment(groupId, commentId);

		return new CommentLoadEvent(new CommentEntry(JSONUtil.toMap(jsonObject)));
	}

	@Override
	public void onSuccess(CommentLoadEvent event) throws Exception {
		getListener().onLoadCommentSuccess(event.getCommentEntry());
	}

	@Override
	public void onFailure(Exception e) {
		getListener().error(e, CommentDisplayScreenlet.LOAD_COMMENT_ACTION);
	}

	private void validate(long groupId, long commentId) {
		if (groupId <= 0) {
			throw new IllegalArgumentException("groupId must be greater than 0");
		} else if (commentId <= 0) {
			throw new IllegalArgumentException("commentId cannot be 0 or negative");
		}
	}
}
