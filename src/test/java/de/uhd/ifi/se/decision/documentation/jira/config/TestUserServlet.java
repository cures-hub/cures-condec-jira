package de.uhd.ifi.se.decision.documentation.jira.config;

import com.atlassian.activeobjects.test.TestActiveObjects;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.mock.MockProjectRoleManager;
import com.atlassian.jira.mock.servlet.MockHttpServletResponse;
import com.atlassian.jira.security.roles.ProjectRole;
import com.atlassian.jira.security.roles.ProjectRoleManager;
import com.atlassian.sal.api.auth.LoginUriProvider;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.templaterenderer.TemplateRenderer;
import de.uhd.ifi.se.decision.documentation.jira.ComponentGetter;
import de.uhd.ifi.se.decision.documentation.jira.TestSetUp;
import de.uhd.ifi.se.decision.documentation.jira.mocks.*;
import net.java.ao.EntityManager;
import net.java.ao.test.junit.ActiveObjectsJUnitRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.mock.web.MockHttpServletRequest;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RunWith(ActiveObjectsJUnitRunner.class)
public class TestUserServlet extends TestSetUp{

    private EntityManager entityManager;
    private UserServlet servlet;
    private HttpServletRequest req;
    private HttpServletResponse res;

    @Before
    public void setUp() {
        initialization();
        new ComponentGetter().init(new TestActiveObjects(entityManager), new MockTransactionTemplate(), new MockDefaultUserManager());

        req = new MockHttpServletRequest();
        res = new MockHttpServletResponse();

        ((MockHttpServletRequest)req).setParameter("projectKey", "TEST");
        LoginUriProvider login = new MockLoginUriProvider();
        TemplateRenderer renderer = new MockTemplateRenderer();
        UserManager userManager = new MockAdminUserManager();
        servlet = new UserServlet(userManager,login,renderer);

        ProjectRole admin = new MockProjectRoleManager.MockProjectRole(1234321,"Administrators", "TEST");
        ProjectRoleManager projectRoleManager = ComponentAccessor.getComponent(ProjectRoleManager.class);
        ((MockProjectRoleManager)projectRoleManager).addRole(admin);
    }
    @Test
    public void testReqNullResNull() throws IOException {
        servlet.doGet(null,null);
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
