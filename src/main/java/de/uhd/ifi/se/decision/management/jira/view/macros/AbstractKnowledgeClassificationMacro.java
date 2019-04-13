package de.uhd.ifi.se.decision.management.jira.view.macros;

import java.util.Map;

import com.atlassian.jira.issue.IssueImpl;
import com.atlassian.jira.issue.fields.renderer.IssueRenderContext;
import com.atlassian.renderer.RenderContext;
import com.atlassian.renderer.v2.RenderMode;
import com.atlassian.renderer.v2.macro.BaseMacro;
import com.atlassian.renderer.v2.macro.MacroException;

import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.persistence.ConfigPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.persistence.JiraIssueTextPersistenceManager;

public abstract class AbstractKnowledgeClassificationMacro extends BaseMacro {

	@Override
	public String execute(Map<String, Object> parameters, String body, RenderContext renderContext)
			throws MacroException {

		if (!ConfigPersistenceManager.isKnowledgeExtractedFromIssues(getProjectKey(renderContext))) {
			return body;
		}
		
		KnowledgeType knowledgeType = getKnowledgeType();		
		if (Boolean.TRUE.equals(renderContext.getParam(IssueRenderContext.WYSIWYG_PARAM))) {
			return putTypeInBrackets(knowledgeType) + body + putTypeInBrackets(knowledgeType);
		}
		
		String color = getColor();
		return this.getCommentBody(body, renderContext, knowledgeType, color);
	}

	public String getColor() {
		return "#FFFFFF";
	}

	public KnowledgeType getKnowledgeType() {
		return KnowledgeType.ISSUE;
	}

	private String getCommentBody(String body, RenderContext renderContext, KnowledgeType knowledgeType,
			String colorCode) {
		String icon = getIconHTML(knowledgeType);
		String newBody = body.replaceFirst("<p>", "");
		String elementId = getElementId(renderContext, newBody, knowledgeType);
		return "<p " + elementId + "style='background-color:" + colorCode + "; padding: 3px;'>" + icon + " "
				+ newBody;
	}
	
	private String getIconHTML(KnowledgeType knowledgeType) {
		return "<img src='" + knowledgeType.getIconUrl() + "'>";
	}

	@Override
	public RenderMode getBodyRenderMode() {
		return RenderMode.allow(RenderMode.F_ALL);
	}

	@Override
	public boolean hasBody() {
		return true;
	}

	/**
	 * Return key of current JIRA project.
	 * 
	 * @param renderContext
	 *            context in which the macro is rendered.
	 * @return key of current JIRA project.
	 */
	private String getProjectKey(RenderContext renderContext) {
		return renderContext.getParams().get("jira.issue").toString().split("-")[0];
	}

	/**
	 * Static function for other Macro Classes
	 * 
	 * @param renderContext
	 *            context in which the macro is rendered.
	 * @param body
	 * @param type
	 * @return the js context menu call for comment tab panel
	 */
	protected String getElementId(RenderContext renderContext, String body, KnowledgeType type) {
		long id = 0;
		if (renderContext.getParams().get("jira.issue") instanceof IssueImpl) {
			id = JiraIssueTextPersistenceManager.getIdOfSentenceForMacro(body.replace("<p>", "").replace("</p>", ""),
					((IssueImpl) (renderContext.getParams().get("jira.issue"))).getId(), type.toString(),
					getProjectKey(renderContext));
		}
		if (id == 0) {
			// LOGGER.debug("No sentence object found for: " + body);
			return "";
		}
		return "id=\"commentnode-" + id + "\"";
	}

	protected String putTypeInBrackets(KnowledgeType type) {
		return "\\" + getTag(type);
	}

	public static String getTag(String type) {
		if (type == null || type.equals("") || type.equalsIgnoreCase("other")) {
			return "";
		}
		return "{" + type + "}";
	}

	public static String getTag(KnowledgeType type) {
		if (type == KnowledgeType.OTHER) {
			return "";
		}
		return getTag(type.name().toLowerCase());
	}
}