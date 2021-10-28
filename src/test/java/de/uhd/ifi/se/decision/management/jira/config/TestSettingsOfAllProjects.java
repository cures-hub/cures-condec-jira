package de.uhd.ifi.se.decision.management.jira.config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import com.atlassian.jira.mock.MockProjectManager;
import com.atlassian.jira.mock.servlet.MockHttpServletResponse;
import com.atlassian.jira.project.MockProject;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.project.ProjectManager;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.templaterenderer.TemplateRenderer;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.mocks.MockTemplateRenderer;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraUsers;

public class TestSettingsOfAllProjects {

	private static ProjectManager projectManager;
	private static SettingsOfAllProjects servlet;
	private static HttpServletRequest request;
	private static HttpServletResponse response;
	private static ApplicationUser user;

	@BeforeClass
	public static void setUp() {
		TestSetUp.init();
		request = new MockHttpServletRequest();
		response = new MockHttpServletResponse();
		TemplateRenderer renderer = new MockTemplateRenderer();
		servlet = new SettingsOfAllProjects(renderer);

		projectManager = new MockProjectManager();
		user = JiraUsers.SYS_ADMIN.getApplicationUser();
	}

	@Test
	public void testDoGetNullNull() throws IOException {
		servlet.doGet(null, null);
	}

	@Test
	public void testDoGetNullFilled() throws IOException {
		servlet.doGet(null, response);
	}

	@Test
	public void testDoGetFilledNull() throws IOException {
		servlet.doGet(request, null);
	}

	@Test
	public void testDoGetNoSysFilled() throws IOException {
		request.setAttribute("user", null);
		servlet.doGet(request, response);
	}

	@Test
	public void testDoGetSysFilled() throws IOException {
		request.setAttribute("user", user);
		servlet.doGet(request, response);
	}

	@Test
	public void testRequestNullResponseNull() throws IOException, ServletException {
		assertFalse(servlet.isValidParameters(null, null));
	}

	@Test
	public void testRequestFilledResponseNull() throws IOException, ServletException {
		request.setAttribute("user", null);
		assertFalse(servlet.isValidUser(request));
	}

	@Test
	public void testRequestNullResponseFilled() throws IOException, ServletException {
		assertFalse(servlet.isValidParameters(null, response));
	}

	@Test
	public void testRequestFilledResponseFilled() throws IOException, ServletException {
		request.setAttribute("user", null);
		assertFalse(servlet.isValidUser(request));
	}

	@Test
	public void testNoUserManager() throws IOException, ServletException {
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
	public void testGetProjectMapNoProject() {
		assertEquals(4, SettingsOfAllProjects.getProjects().size());
	}

	@Test
	public void testGetProjectMapProjects() {
		Project project = new MockProject(1, "TEST");
		((MockProject) project).setKey("TEST");
		((MockProjectManager) projectManager).addProject(project);
		assertTrue(SettingsOfAllProjects.getProjects().size() >= 0);
	}

	@Test
	public void testGetTemplatePath() {
		assertEquals("templates/settings/settingsForAllProjects.vm", servlet.getTemplatePath());
	}

	@Test
	public void testGetVerlocityParametersNull() {
		assertEquals(0, servlet.getVelocityParameters(null).size());
	}

	@Test
	public void testGetVelocityParametersFilled() {
		Project project = new MockProject(1, "TEST");
		((MockProject) project).setKey("TEST");
		((MockProjectManager) projectManager).addProject(project);
		assertEquals(2, servlet.getVelocityParameters(request).size());
	}
}