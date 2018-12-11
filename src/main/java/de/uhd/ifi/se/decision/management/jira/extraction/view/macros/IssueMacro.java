package de.uhd.ifi.se.decision.management.jira.extraction.view.macros;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atlassian.jira.issue.IssueImpl;
import com.atlassian.jira.issue.fields.renderer.IssueRenderContext;
import com.atlassian.renderer.RenderContext;
import com.atlassian.renderer.v2.RenderMode;
import com.atlassian.renderer.v2.macro.BaseMacro;

import de.uhd.ifi.se.decision.management.jira.ComponentGetter;
import de.uhd.ifi.se.decision.management.jira.extraction.persistence.ActiveObjectsManager;
import de.uhd.ifi.se.decision.management.jira.persistence.ConfigPersistenceManager;

public class IssueMacro extends BaseMacro {

	private static final Logger LOGGER = LoggerFactory.getLogger(IssueMacro.class);

	@Override
	public boolean hasBody() {
		return true;
	}

	@Override
	public RenderMode getBodyRenderMode() {
		return RenderMode.allow(RenderMode.F_ALL);
	}

	@Override
	public String execute(Map<String, Object> parameters, String body, RenderContext renderContext) {
		if (!ConfigPersistenceManager.isKnowledgeExtractedFromIssues(IssueMacro.getProjectKey(renderContext))) {
			return body;
		}
		if (Boolean.TRUE.equals(renderContext.getParam(IssueRenderContext.WYSIWYG_PARAM))) {
			return "\\{issue}" + body + "\\{issue}";
		}
		String newBody = IssueMacro.reformatCommentBody(body);
		String icon = "<img src=\"" + ComponentGetter.getUrlOfImageFolder() + "issue.png" + "\">";
		String contextMenuCall = IssueMacro.getContextMenuCall(renderContext, newBody, "Issue");
		return icon + "<span " + contextMenuCall + "style =  \"background-color:#F2F5A9\">" + newBody + "</span>";
	}

	/**
	 * Static function for other Macro Classes
	 * 
	 * @param inputBody
	 * @return Body without html p tags
	 */
	public static String reformatCommentBody(String inputBody) {
		String body = inputBody.replace("<p>", "");
		body = body.replace("</p>", "");
		while (body.startsWith(" ")) {
			body = body.substring(1);
		}
		while (body.endsWith(" ")) {
			body = body.substring(0, body.length() - 1);
		}
		return body;
	}

	/**
	 * Static function for other Macro Classes
	 * 
	 * @param renderContext
	 * @return
	 */
	public static String getProjectKey(RenderContext renderContext) {
		return renderContext.getParams().get("jira.issue").toString().split("-")[0];
	}

	/**
	 * Static function for other Macro Classes
	 * 
	 * @param renderContext
	 * @param body
	 * @param type
	 * @return the js context menu call for comment tab panel
	 */
	public static String getContextMenuCall(RenderContext renderContext, String body, String type) {
		long id = 0;
		if (renderContext.getParams().get("jira.issue") instanceof IssueImpl) {
			id = ActiveObjectsManager.getIdOfSentenceForMacro(body.replace("<p>", "").replace("</p>", ""),
					((IssueImpl) (renderContext.getParams().get("jira.issue"))).getId(), type,
					getProjectKey(renderContext));
		}
		if (id == 0) {
			// LOGGER.debug("No sentence object found for: " + body);
			return "";
		}
		return "oncontextmenu=\"conDecContextMenu.createContextMenuForSentences(this.offsetLeft, this.offsetTop, " + id
				+ "); return false;\"";
	}

}
