package de.uhd.ifi.se.decision.management.jira.config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import com.atlassian.jira.mock.servlet.MockHttpServletResponse;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.templaterenderer.TemplateRenderer;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.mocks.MockTemplateRenderer;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraUsers;
import net.java.ao.test.jdbc.NonTransactional;

public class TestSettingsOfSingleProject {

	private static SettingsOfSingleProject servlet;
	private static HttpServletRequest request;
	private static HttpServletResponse response;
	private static ApplicationUser user;

	@BeforeClass
	public static void setUp() {
		TestSetUp.init();

		request = new MockHttpServletRequest();
		response = new MockHttpServletResponse();

		((MockHttpServletRequest) request).setParameter("projectKey", "TEST");
		TemplateRenderer renderer = new MockTemplateRenderer();
		servlet = new SettingsOfSingleProject(renderer);

		user = JiraUsers.SYS_ADMIN.getApplicationUser();
	}

	@Test
	public void testRequestNullResponseNull() {
		assertFalse(servlet.isValidParameters(null, null));
	}

	@Test
	public void testRequestFilledUserNull() {
		request.setAttribute("user", null);
		assertFalse(servlet.isValidUser(request));
	}

	@Test
	public void testRequestNullResponseFilled() {
		assertFalse(servlet.isValidParameters(null, response));
	}

	@Test
	public void testRequestFilledResponseFilled() {
		request.setAttribute("user", null);
		assertFalse(servlet.isValidUser(request));
	}

	@Test
	public void testNoUserManager() {
		((MockHttpServletRequest) request).setQueryString("Test");
		request.setAttribute("user", null);
		assertFalse(servlet.isValidUser(request));
	}

	@Test
	public void testNoUserManagerQueryNull() {
		((MockHttpServletRequest) request).setQueryString(null);
		request.setAttribute("user", null);
		assertFalse(servlet.isValidUser(request));
	}

	@Test
	public void testUserManager() {
		request.setAttribute("user", user);
		assertTrue(servlet.isValidUser(request));
	}

	@Test
	public void testGetTemplatePath() {
		assertEquals("templates/settings/settingsForSingleProject.vm", servlet.getTemplatePath());
	}

	@Test
	public void testGetVelocityParametersNull() {
		assertEquals(0, servlet.getVelocityParameters(null).size());
	}

	@Test
	@NonTransactional
	public void testGetVelocityParametersFilled() {
		request.setAttribute("projectKey", "TEST");
		assertEquals(17, servlet.getVelocityParameters(request).size());
	}
}