package de.uhd.ifi.se.decision.documentation.jira.config;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.mock.web.MockHttpServletRequest;

import com.atlassian.activeobjects.test.TestActiveObjects;
import com.atlassian.jira.mock.servlet.MockHttpServletResponse;
import com.atlassian.sal.api.auth.LoginUriProvider;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.templaterenderer.TemplateRenderer;

import de.uhd.ifi.se.decision.documentation.jira.ComponentGetter;
import de.uhd.ifi.se.decision.documentation.jira.TestSetUp;
import de.uhd.ifi.se.decision.documentation.jira.config.SettingsOfAllProjects;
import de.uhd.ifi.se.decision.documentation.jira.mocks.MockAdminUserManager;
import de.uhd.ifi.se.decision.documentation.jira.mocks.MockDefaultUserManager;
import de.uhd.ifi.se.decision.documentation.jira.mocks.MockLoginUriProvider;
import de.uhd.ifi.se.decision.documentation.jira.mocks.MockTemplateRenderer;
import de.uhd.ifi.se.decision.documentation.jira.mocks.MockTransactionTemplate;
import net.java.ao.EntityManager;
import net.java.ao.test.junit.ActiveObjectsJUnitRunner;

@RunWith(ActiveObjectsJUnitRunner.class)
public class TestAdminServlet extends TestSetUp {

	private EntityManager entityManager;
	private SettingsOfAllProjects servlet;
	private HttpServletRequest req;
	private HttpServletResponse res;

	@Before
	public void setUp() {
		initialization();
		new ComponentGetter().init(new TestActiveObjects(entityManager), new MockTransactionTemplate(), new MockDefaultUserManager());

		req = new MockHttpServletRequest();
		res = new MockHttpServletResponse();
		LoginUriProvider login = new MockLoginUriProvider();
		TemplateRenderer renderer = new MockTemplateRenderer();
		UserManager userManager = new MockAdminUserManager();
		servlet = new SettingsOfAllProjects(userManager, login, renderer);
	}

	@Test
	public void testRequestNullResponseNull() throws IOException, ServletException {
		servlet.doGet(null, null);
	}

	@Test
	public void testRequestFilledResponseNull() throws IOException, ServletException {
		req.setAttribute("NoSysAdmin", false);
		req.setAttribute("SysAdmin", false);
		servlet.doGet(req, null);
	}

	@Test
	public void testRequestNullResponseFilled() throws IOException, ServletException {
		servlet.doGet(null, res);
	}
	@Test
	public void testRequestFilledResponseFilled() throws IOException, ServletException {
		req.setAttribute("NoSysAdmin", false);
		req.setAttribute("SysAdmin", false);
		servlet.doGet(req, res);
	}

	@Test
	public void testNoUserManager() throws IOException, ServletException {
		((MockHttpServletRequest)req).setQueryString("Test");
		req.setAttribute("NoSysAdmin", true);
		servlet.doGet(req, res);
	}

	@Test
	public void testNoUserManagerQueryNull() throws IOException, ServletException {
		((MockHttpServletRequest)req).setQueryString(null);
		req.setAttribute("NoSysAdmin", true);
		servlet.doGet(req, res);
	}

	@Test
	public void testUserManager() throws IOException, ServletException {
		req.setAttribute("NoSysAdmin", false);
		req.setAttribute("SysAdmin", true);
		servlet.doGet(req, res);
	}
}
