package de.uhd.ifi.se.decision.management.jira.extraction.view.macros;

import java.util.Map;

import com.atlassian.jira.issue.fields.renderer.IssueRenderContext;
import com.atlassian.renderer.RenderContext;
import com.atlassian.renderer.v2.RenderMode;
import com.atlassian.renderer.v2.macro.BaseMacro;

import de.uhd.ifi.se.decision.management.jira.ComponentGetter;

public class AlternativeMacro extends BaseMacro {
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
		if (Boolean.TRUE.equals(renderContext.getParam(IssueRenderContext.WYSIWYG_PARAM))) {
			return "\\{alternative}"+body+"\\{alternative}";
        }
		body = IssueMacro.reformatCommentBody(body);
		String icon = "<img src=\"" + ComponentGetter.getUrlOfImageFolder() + "alternative.png" + "\">";
		return icon + "<span style =  \"background-color:#f1ccf9\">" + body + "</span>";
	}

}
