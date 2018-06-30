package de.uhd.ifi.se.decision.documentation.jira.config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.mock.web.MockHttpServletRequest;

import com.atlassian.activeobjects.test.TestActiveObjects;
import com.atlassian.jira.mock.MockProjectManager;
import com.atlassian.jira.mock.component.MockComponentWorker;
import com.atlassian.jira.mock.servlet.MockHttpServletResponse;
import com.atlassian.jira.project.MockProject;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.project.ProjectManager;
import com.atlassian.sal.api.auth.LoginUriProvider;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.templaterenderer.TemplateRenderer;

import de.uhd.ifi.se.decision.documentation.jira.TestComponentGetter;
import de.uhd.ifi.se.decision.documentation.jira.TestSetUp;
import de.uhd.ifi.se.decision.documentation.jira.mocks.MockAdminUserManager;
import de.uhd.ifi.se.decision.documentation.jira.mocks.MockDefaultUserManager;
import de.uhd.ifi.se.decision.documentation.jira.mocks.MockLoginUriProvider;
import de.uhd.ifi.se.decision.documentation.jira.mocks.MockTemplateRenderer;
import de.uhd.ifi.se.decision.documentation.jira.mocks.MockTransactionTemplate;
import de.uhd.ifi.se.decision.documentation.jira.model.DecisionKnowledgeProject;
import net.java.ao.EntityManager;
import net.java.ao.test.junit.ActiveObjectsJUnitRunner;

@RunWith(ActiveObjectsJUnitRunner.class)
public class TestSettingsOfAllProjects extends TestSetUp {

	private EntityManager entityManager;
	private ProjectManager projectManager;
	private SettingsOfAllProjects servlet;
	private HttpServletRequest request;
	private HttpServletResponse response;

	@Before
	public void setUp() {
		initialization();
		TestComponentGetter.init(new TestActiveObjects(entityManager), new MockTransactionTemplate(),
				new MockDefaultUserManager());
		request = new MockHttpServletRequest();
		response = new MockHttpServletResponse();
		LoginUriProvider login = new MockLoginUriProvider();
		TemplateRenderer renderer = new MockTemplateRenderer();
		UserManager userManager = new MockAdminUserManager();
		servlet = new SettingsOfAllProjects(userManager, login, renderer);

		projectManager = new MockProjectManager();
		new MockComponentWorker().init().addMock(ProjectManager.class, projectManager);
	}

	@Test
	public void testRequestNullResponseNull() throws IOException, ServletException {
		assertFalse(servlet.isValidParameters(null, null));
	}

	@Test
	public void testRequestFilledResponseNull() throws IOException, ServletException {
		request.setAttribute("NoSysAdmin", false);
		request.setAttribute("SysAdmin", false);
		assertFalse(servlet.isValidUser(request));
	}

	@Test
	public void testRequestNullResponseFilled() throws IOException, ServletException {
		assertFalse(servlet.isValidParameters(null, response));
	}

	@Test
	public void testRequestFilledResponseFilled() throws IOException, ServletException {
		request.setAttribute("NoSysAdmin", false);
		request.setAttribute("SysAdmin", false);
		assertFalse(servlet.isValidUser(request));
		assertTrue(servlet.isValidParameters(request, response));
	}

	@Test
	public void testNoUserManager() throws IOException, ServletException {
		((MockHttpServletRequest) request).setQueryString("Test");
		request.setAttribute("NoSysAdmin", true);
		assertFalse(servlet.isValidUser(request));
		assertTrue(servlet.isValidParameters(request, response));
	}

	@Test
	public void testNoUserManagerQueryNull() throws IOException, ServletException {
		((MockHttpServletRequest) request).setQueryString(null);
		request.setAttribute("NoSysAdmin", true);
		assertFalse(servlet.isValidUser(request));
		assertTrue(servlet.isValidParameters(request, response));
	}

	@Test
	public void testUserManager() throws IOException, ServletException {
		request.setAttribute("NoSysAdmin", false);
		request.setAttribute("SysAdmin", true);
		assertTrue(servlet.isValidUser(request));
		assertTrue(servlet.isValidParameters(request, response));
	}

	@Test
	public void testGetProjectMapNoProject() {
		Map<String, DecisionKnowledgeProject> map = new ConcurrentHashMap<String, DecisionKnowledgeProject>();
		assertEquals(map, SettingsOfAllProjects.getProjectsMap());
	}

	@Test
	public void testGetProjectMapProjects() {
		Project project = new MockProject(1, "TEST");
		((MockProject) project).setKey("TEST");
		((MockProjectManager) projectManager).addProject(project);
		assertTrue(SettingsOfAllProjects.getProjectsMap().size() >= 0);
	}
}