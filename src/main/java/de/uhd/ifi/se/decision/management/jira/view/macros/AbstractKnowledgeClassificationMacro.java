package de.uhd.ifi.se.decision.management.jira.view.macros;

import java.util.Map;

import com.atlassian.jira.issue.IssueImpl;
import com.atlassian.jira.issue.fields.renderer.IssueRenderContext;
import com.atlassian.renderer.RenderContext;
import com.atlassian.renderer.v2.RenderMode;
import com.atlassian.renderer.v2.macro.BaseMacro;
import com.atlassian.renderer.v2.macro.MacroException;

import de.uhd.ifi.se.decision.management.jira.classification.TextClassifier;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.persistence.KnowledgePersistenceManager;
import de.uhd.ifi.se.decision.management.jira.persistence.singlelocations.JiraIssueTextPersistenceManager;

/**
 * Macros to mark (i.e. annotate/classifiy) text in the description or comments
 * of a Jira issue as a decision knowledge element (= rationale element). Each
 * macro class needs to be added in the atlassian-plugin.xml file.
 * 
 * Currently, only five decision knowledge types can be used to classify text as
 * decision knowledge:
 * 
 * @see IssueMacro
 * @see AlternativeMacro
 * @see DecisionMacro
 * @see ProMacro
 * @see ConMacro
 * 
 * @see JiraIssueTextPersistenceManager
 * @see TextClassifier
 */
public abstract class AbstractKnowledgeClassificationMacro extends BaseMacro {

	@Override
	public String execute(Map<String, Object> parameters, String body, RenderContext renderContext)
			throws MacroException {
		KnowledgeType knowledgeType = getKnowledgeType();
		if (Boolean.TRUE.equals(renderContext.getParam(IssueRenderContext.WYSIWYG_PARAM))) {
			return putTypeInBrackets(knowledgeType) + body + putTypeInBrackets(knowledgeType);
		}

		String color = getKnowledgeType().getColor();
		return getCommentBody(body, renderContext, knowledgeType, color);
	}

	protected abstract KnowledgeType getKnowledgeType();

	private String getCommentBody(String body, RenderContext renderContext, KnowledgeType knowledgeType,
			String colorCode) {
		String icon = getIconHTML(knowledgeType);
		long elementId = getElementId(renderContext, body, knowledgeType);
		long jiraIssueId = getJiraIssueId(renderContext);
		String eventCode = getOnContextMenuEventListener(elementId, jiraIssueId);
		return "<p " + eventCode + "style='background-color:" + colorCode + "; padding:3px;'>" + icon + " "
		+ body.replace("<p>", "").replace("</p>", "") + "</p>";
	}

	private String getIconHTML(KnowledgeType knowledgeType) {
		return "<img class='emoticon' width='16' height='16' align='absmiddle' src='" + knowledgeType.getIconUrl()
				+ "'>";
	}

	@Override
	public RenderMode getBodyRenderMode() {
		return RenderMode.allow(RenderMode.F_LINEBREAKS);
	}

	@Override
	public boolean hasBody() {
		return true;
	}

	/**
	 * @param renderContext
	 *            context in which the macro is rendered.
	 * @param body
	 * @param type
	 * @return the js context menu call for comment tab panel
	 */
	protected long getElementId(RenderContext renderContext, String body, KnowledgeType type) {
		long id = 0;
		if (renderContext.getParams().get("jira.issue") instanceof IssueImpl) {
			String projectKey = getProjectKey(renderContext);
			JiraIssueTextPersistenceManager persistenceManager = KnowledgePersistenceManager.getOrCreate(projectKey)
					.getJiraIssueTextManager();
			String summary = body.replace("<p>", "").replace("</p>", "").trim().replaceAll("<[^>]*>", "");
			long jiraIssueId = getJiraIssueId(renderContext);
			id = persistenceManager.getIdOfElement(summary, jiraIssueId, type);
		}
		return id;
	}

	private String getOnContextMenuEventListener(long id, long jiraIssueId) {
		if (id <= 0) {
			return "";
		}
		return "id=\"commentnode-" + id + "\" oncontextmenu=\"conDecContextMenu.createContextMenu(" + id
				+ ",'s',this,null," + jiraIssueId + ",'i'); return false;\" ";
	}

	/**
	 * @param renderContext
	 *            context in which the macro is rendered.
	 * @return key of current Jira project.
	 */
	private String getProjectKey(RenderContext renderContext) {
		return renderContext.getParams().get("jira.issue").toString().split("-")[0];
	}

	/**
	 * @param renderContext
	 *            context in which the macro is rendered.
	 * @return id of current Jira issue.
	 */
	private long getJiraIssueId(RenderContext renderContext) {
		if (renderContext.getParams().get("jira.issue") instanceof IssueImpl) {
			return ((IssueImpl) (renderContext.getParams().get("jira.issue"))).getId();
		}
		return 0;
	}

	protected String putTypeInBrackets(KnowledgeType type) {
		return type != null ? "\\" + type.getTag() : "";
	}
}