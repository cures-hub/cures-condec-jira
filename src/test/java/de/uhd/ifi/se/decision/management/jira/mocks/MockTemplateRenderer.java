package de.uhd.ifi.se.decision.management.jira.mocks;

import java.io.Writer;
import java.util.Map;

import com.atlassian.templaterenderer.RenderingException;
import com.atlassian.templaterenderer.TemplateRenderer;

public class MockTemplateRenderer implements TemplateRenderer {

	@Override
	public void render(String templateName, Writer writer) throws RenderingException {
		// method empty since not used for testing
	}

	@Override
	public void render(String templateName, Map<String, Object> context, Writer writer) throws RenderingException {
		// method empty since not used for testing
	}

	@Override
	public String renderFragment(String fragment, Map<String, Object> context) throws RenderingException {
		return null;
	}

	@Override
	public boolean resolve(String templateName) {
		return false;
	}

}
