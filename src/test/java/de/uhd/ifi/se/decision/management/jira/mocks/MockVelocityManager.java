package de.uhd.ifi.se.decision.management.jira.mocks;

import com.atlassian.velocity.VelocityManager;
import org.apache.velocity.context.Context;
import org.apache.velocity.exception.VelocityException;

import java.io.IOException;
import java.io.Writer;
import java.text.DateFormat;
import java.util.Map;

public class MockVelocityManager implements VelocityManager {
	@Override
	public String getBody(String templateDirectory, String template, Map<String, Object> contextParameters)
			throws VelocityException {
		return null;
	}

	@Override
	public String getBody(String templateDirectory, String template, String baseurl,
			Map<String, Object> contextParameters) throws VelocityException {
		return null;
	}

	@Override
	public String getEncodedBody(String templateDirectory, String template, String encoding,
			Map<String, Object> contextParameters) throws VelocityException {
		return "Test";
	}

	@Override
	public String getEncodedBody(String templateDirectory, String template, String baseurl, String encoding,
			Map<String, Object> contextParameters) throws VelocityException {
		return "Test";
	}

	@Override
	public String getEncodedBodyForContent(String content, String baseurl, Map<String, Object> contextParameters)
			throws VelocityException {
		return null;
	}

	@Override
	public DateFormat getDateFormat() {
		return null;
	}

	@Override
	public String getEncodedBody(String templateDirectory, String template, String baseurl, String encoding,
			Context context) throws VelocityException {
		return null;
	}

	@Override
	public void writeEncodedBodyForContent(Writer writer, String contentFragment, Context context)
			throws VelocityException, IOException {
		// method empty since not used for testing
	}

	@Override
	public void writeEncodedBody(Writer writer, String templateDirectory, String template, String encoding,
			Context context) throws VelocityException, IOException {
		// method empty since not used for testing
	}
}
