package de.uhd.ifi.se.decision.management.jira.view.macros;

import java.util.Map;

import com.atlassian.renderer.RenderContext;
import com.atlassian.renderer.v2.RenderMode;
import com.atlassian.renderer.v2.macro.BaseMacro;
import com.atlassian.renderer.v2.macro.MacroException;

import de.uhd.ifi.se.decision.management.jira.ComponentGetter;

public class CodeSummarizationMacro extends BaseMacro {

	@Override
	public String execute(Map<String, Object> parameters, String body, RenderContext renderContext)
			throws MacroException {
		String newBody = body.replaceFirst("<p>", "");
		String icon = "<img class='emoticon' width='16' height='16' align='absmiddle' src='"
				+ ComponentGetter.getUrlOfImageFolder() + "codesummarization.png" + "'>";
		return "<p style='background-color:#DDF2FF; padding: 3px;'>" + icon + " " + newBody;
	}

	@Override
	public RenderMode getBodyRenderMode() {
		return RenderMode.allow(RenderMode.F_ALL);
	}

	@Override
	public boolean hasBody() {
		return true;
	}
}