package com.liferay.mobile.screens.webcontent.display.interactor;

import com.liferay.mobile.android.service.Session;
import com.liferay.mobile.screens.context.SessionContext;
import com.liferay.mobile.screens.ddl.form.connector.DDMStructureConnector;
import com.liferay.mobile.screens.util.JSONUtil;
import com.liferay.mobile.screens.util.ServiceProvider;
import com.liferay.mobile.screens.webcontent.WebContent;
import com.liferay.mobile.screens.webcontent.display.connector.JournalContentConnector;
import java.util.Locale;
import org.json.JSONObject;

/**
 * @author Javier Gamarra
 */
public class WebContentDisplayFromStructureInteractorImpl extends WebContentDisplayBaseInteractorImpl {

	@Override
	public WebContentDisplayEvent execute(Object... args) throws Exception {

		Long structureId = (Long) args[0];
		String articleId = (String) args[1];

		validate(structureId, groupId, articleId, locale);

		Session session = SessionContext.createSessionFromCurrentSession();

		JournalContentConnector journalArticleConnector =
			ServiceProvider.getInstance().getJournalContentConnector(session);
		JSONObject article = journalArticleConnector.getArticle(groupId, articleId);

		DDMStructureConnector structureConnector = ServiceProvider.getInstance().getDDMStructureConnector(session);
		JSONObject ddmStructure = structureConnector.getStructure(structureId);

		article.put("DDMStructure", ddmStructure);
		WebContent webContent = new WebContent(JSONUtil.toMap(article), locale);

		return new WebContentDisplayEvent(structureId, articleId, webContent);
	}

	@Override
	public void onSuccess(WebContentDisplayEvent event) throws Exception {
		getListener().onWebContentReceived(event.getWebContent());
	}

	@Override
	protected WebContentDisplayEvent createEventFromArgs(Object... args) throws Exception {

		Long structureId = (Long) args[0];
		String articleId = (String) args[1];

		return new WebContentDisplayEvent(structureId, articleId, null);
	}

	private void validate(Long structureId, long groupId, String articleId, Locale locale) {
		super.validate(locale);

		if (groupId == 0) {
			throw new IllegalArgumentException("GroupId cannot be null");
		} else if (structureId == null || structureId == 0L) {
			throw new IllegalArgumentException("StructureId cannot be null");
		} else if (articleId == null || articleId.isEmpty()) {
			throw new IllegalArgumentException("ArticleId cannot be null");
		}
	}
}
