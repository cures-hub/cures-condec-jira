package de.uhd.ifi.se.decision.management.jira.extraction.view.macros;

import com.atlassian.jira.issue.fields.renderer.IssueRenderContext;
import com.atlassian.renderer.RenderContext;
import com.atlassian.renderer.v2.RenderMode;
import com.atlassian.renderer.v2.macro.BaseMacro;

import de.uhd.ifi.se.decision.management.jira.ComponentGetter;
import de.uhd.ifi.se.decision.management.jira.persistence.ConfigPersistenceManager;

import java.util.Map;

public class IssueMacro extends BaseMacro {
	@Override
	public boolean hasBody() {
		return true;
	}

	@Override
	public RenderMode getBodyRenderMode() {
		return RenderMode.allow(RenderMode.F_ALL);
	}

	@Override
	public String execute(Map<String, Object> parameters, String body, RenderContext renderContext)	{
		if(!ConfigPersistenceManager.isKnowledgeExtractedFromIssues(IssueMacro.getProjectKey(renderContext))) {
			return body;
		}
		if (Boolean.TRUE.equals(renderContext.getParam(IssueRenderContext.WYSIWYG_PARAM))) {
			return "\\{issue}"+body+"\\{issue}";
        }
		String newBody = IssueMacro.reformatCommentBody(body);
		String icon = "<img src=\"" + ComponentGetter.getUrlOfImageFolder() + "issue.png" + "\">";
		return icon + "<span style =  \"background-color:#F2F5A9\">" + newBody + "</span>";
	}

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
	
	public static String getProjectKey(RenderContext renderContext) {
		return renderContext.getParams().get("jira.issue").toString().split("-")[0];
	}

}
