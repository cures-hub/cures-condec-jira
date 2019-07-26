package de.uhd.ifi.se.decision.management.jira.config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Collection;

import javax.servlet.http.HttpServletRequest;

import org.junit.BeforeClass;
import org.junit.Test;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.mock.servlet.MockHttpServletRequest;
import com.atlassian.jira.security.roles.ProjectRole;
import com.atlassian.jira.user.ApplicationUser;

import de.uhd.ifi.se.decision.management.jira.TestSetUpWithIssues;

public class TestAuthenticationManager {
	private static HttpServletRequest request;
	private static ApplicationUser user;

	@BeforeClass
	public static void setUp() {
		TestSetUpWithIssues.initialization();

		user = ComponentAccessor.getUserManager().getUserByName("SysAdmin");
		request = new MockHttpServletRequest();		
		request.setAttribute("user", user);
		((MockHttpServletRequest) request).setParameter("projectKey", "TEST");
	}

	@Test
	public void testIsProjectAdminByRequestSuccess() {
		assertTrue(AuthenticationManager.isProjectAdmin(request));
	}

	@Test
	public void testIsProjectAdminByRequestFails() {
		HttpServletRequest request = new MockHttpServletRequest();
		request.setAttribute("user", null);
		assertFalse(AuthenticationManager.isProjectAdmin(request));
	}

	@Test
	public void testIsSystemAdminByRequestSuccess() {
		assertTrue(AuthenticationManager.isSystemAdmin(request));
	}

	@Test
	public void testIsProjectAdminSuccess() {
		assertTrue(AuthenticationManager.isProjectAdmin("SysAdmin", "TEST"));
	}

	@Test
	public void testIsProjectAdminFails() {
		assertFalse(AuthenticationManager.isProjectAdmin("NoSysAdmin", "TEST"));
	}

	@Test
	public void testIsSystemAdminRequestSuccess() {
		assertTrue(AuthenticationManager.isSystemAdmin("SysAdmin"));
	}

	@Test
	public void testIsSystemAdminRequestFails() {
		assertFalse(AuthenticationManager.isSystemAdmin("NoSysAdmin"));
	}

	@Test
	public void testGetUsername() {
		assertEquals(AuthenticationManager.getUsername(request), "SysAdmin");
	}

	@Test
	public void testGetUserFromUsername() {
		ApplicationUser user = AuthenticationManager.getUser("SysAdmin");
		assertEquals(user.getUsername(), "SysAdmin");
	}
	
	@Test
	public void testGetUsernameFromRequest() {
		String usernameFromRequest = AuthenticationManager.getUsername(request);
		assertEquals("SysAdmin", usernameFromRequest);
	}

	@Test
	public void testGetUserFromRequest() {
		ApplicationUser userFromRequest = AuthenticationManager.getUser(request);
		assertEquals(user, userFromRequest);
		assertEquals("SysAdmin", user.getUsername());
	}

	@Test
	public void testGetRolesInProject() {
		Collection<ProjectRole> roles = AuthenticationManager.getRolesInProject("TEST", "SysAdmin");
		assertEquals(roles.size(), 1);
	}

	@Test
	public void testGetRolesInProjectFail() {
		Collection<ProjectRole> roles = AuthenticationManager.getRolesInProject("TEST", "NoSysAdmin");
		assertEquals(roles.size(), 0);
	}
}
