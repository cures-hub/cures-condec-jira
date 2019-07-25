package de.uhd.ifi.se.decision.management.jira.config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import com.atlassian.jira.mock.servlet.MockHttpServletResponse;
import com.atlassian.templaterenderer.TemplateRenderer;

import de.uhd.ifi.se.decision.management.jira.TestSetUpWithIssues;
import de.uhd.ifi.se.decision.management.jira.mocks.MockTemplateRenderer;
import net.java.ao.test.jdbc.NonTransactional;

public class TestSettingsOfSingleProject {

	private SettingsOfSingleProject servlet;
	private HttpServletRequest request;
	private HttpServletResponse response;

	@Before
	public void setUp() {
		TestSetUpWithIssues.initialization();

		request = new MockHttpServletRequest();
		response = new MockHttpServletResponse();

		((MockHttpServletRequest) request).setParameter("projectKey", "TEST");
		TemplateRenderer renderer = new MockTemplateRenderer();
		servlet = new SettingsOfSingleProject(renderer);
	}

	@Test
	public void testRequestNullResponseNull() {
		assertFalse(servlet.isValidParameters(null, null));
	}

	@Test
	public void testRequestFilledResponseNull() {
		request.setAttribute("NoSysAdmin", false);
		request.setAttribute("SysAdmin", false);
		assertFalse(servlet.isValidUser(request));
	}

	@Test
	public void testRequestNullResponseFilled() {
		assertFalse(servlet.isValidParameters(null, response));
	}

	@Test
	public void testRequestFilledResponseFilled() {
		request.setAttribute("NoSysAdmin", false);
		request.setAttribute("SysAdmin", false);
		assertFalse(servlet.isValidUser(request));
	}

	@Test
	public void testNoUserManager() {
		((MockHttpServletRequest) request).setQueryString("Test");
		request.setAttribute("NoSysAdmin", true);
		request.setAttribute("SysAdmin", false);
		assertFalse(servlet.isValidUser(request));
	}

	@Test
	public void testNoUserManagerQueryNull() {
		((MockHttpServletRequest) request).setQueryString(null);
		request.setAttribute("NoSysAdmin", true);
		request.setAttribute("SysAdmin", false);
		assertFalse(servlet.isValidUser(request));
	}

	@Test
	public void testUserManager() {
		request.setAttribute("NoSysAdmin", false);
		request.setAttribute("SysAdmin", true);
		assertTrue(servlet.isValidUser(request));
	}

	@Test
	public void testGetTemplatePath() {
		assertEquals("templates/settingsForSingleProject.vm", servlet.getTemplatePath());
	}

	@Test
	public void testGetVelocityParametersNull() {
		assertEquals(0, servlet.getVelocityParameters(null).size());
	}

	@Test
	@NonTransactional
	public void testGetVelocityParametersFilled() {
		request.setAttribute("projectKey", "TEST");
		assertEquals(8, servlet.getVelocityParameters(request).size());
	}
}