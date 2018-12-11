package de.uhd.ifi.se.decision.management.jira.extraction.view.macros;

import java.util.Map;

import com.atlassian.jira.issue.fields.renderer.IssueRenderContext;
import com.atlassian.renderer.RenderContext;
import com.atlassian.renderer.v2.RenderMode;
import com.atlassian.renderer.v2.macro.BaseMacro;

import de.uhd.ifi.se.decision.management.jira.ComponentGetter;
import de.uhd.ifi.se.decision.management.jira.persistence.ConfigPersistenceManager;

public class ProMacro extends BaseMacro {

	@Override
	public boolean hasBody() {
		return true;
	}

	@Override
	public RenderMode getBodyRenderMode() {
		return RenderMode.allow(RenderMode.F_ALL);
	}

	@Override
	public String execute(Map<String, Object> parameters, String body, RenderContext renderContext){
		if(!ConfigPersistenceManager.isKnowledgeExtractedFromIssues(IssueMacro.getProjectKey(renderContext))) {
			return body;
		}
		if (Boolean.TRUE.equals(renderContext.getParam(IssueRenderContext.WYSIWYG_PARAM))) {
			return "\\{pro}"+body+"\\{pro}";
        }
		System.out.println(IssueMacro.getProjectKey(renderContext));
		String newBody = IssueMacro.reformatCommentBody(body);
		String icon = "<img src=\"" + ComponentGetter.getUrlOfImageFolder() + "argument_pro.png" + "\">";
		String contextMenuCall = IssueMacro.getContextMenuCall(renderContext, newBody, "Pro");
		return icon + "<span "+contextMenuCall+"style =  \"background-color:#b9f7c0\">" + newBody + "</span>";
	}

}
