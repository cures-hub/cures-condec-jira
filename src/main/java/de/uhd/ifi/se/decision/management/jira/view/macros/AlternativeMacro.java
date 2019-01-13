package de.uhd.ifi.se.decision.management.jira.view.macros;

import java.util.Map;

import com.atlassian.renderer.RenderContext;
import com.atlassian.renderer.v2.macro.MacroException;

import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;

public class AlternativeMacro extends AbstractKnowledgeClassificationMacro {

	private String knowledgeType = KnowledgeType.ALTERNATIVE.toString().toLowerCase();

	@Override
	public String execute(Map<String, Object> parameters, String body, RenderContext renderContext)
			throws MacroException {
		return super.execute(parameters, body, renderContext, knowledgeType, "#f1ccf9");
	}
}