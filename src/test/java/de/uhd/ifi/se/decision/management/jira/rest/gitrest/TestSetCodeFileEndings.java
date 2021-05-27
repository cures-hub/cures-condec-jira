package de.uhd.ifi.se.decision.management.jira.rest.gitrest;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response.Status;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

import com.atlassian.jira.mock.servlet.MockHttpServletRequest;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.mocks.MockPluginSettings;
import de.uhd.ifi.se.decision.management.jira.mocks.MockPluginSettingsFactory;
import de.uhd.ifi.se.decision.management.jira.rest.GitRest;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraUsers;

public class TestSetCodeFileEndings extends TestSetUp {

	protected HttpServletRequest request;
	protected GitRest gitRest;
	protected Map<String, String> codeFileEndings;

	@Before
	public void setUp() {
		init();
		gitRest = new GitRest();
		request = new MockHttpServletRequest();
		request.setAttribute("user", JiraUsers.SYS_ADMIN.getApplicationUser());
		codeFileEndings = new HashMap<String, String>();
		codeFileEndings.put("JAVA_C", "java");
	}

	@Test
	public void testRequestNullProjectKeyNullCodeFileEndingsNull() {
		assertEquals(Status.BAD_REQUEST.getStatusCode(), gitRest.setCodeFileEndings(null, null, null).getStatus());
	}

	@Test
	public void testRequestNullProjectKeyNullCodeFileEndingsProvided() {
		assertEquals(Status.BAD_REQUEST.getStatusCode(),
				gitRest.setCodeFileEndings(null, null, codeFileEndings).getStatus());
	}

	@Test
	public void testRequestValidProjectKeyValidCodeFileEndingsNull() {
		assertEquals(Status.BAD_REQUEST.getStatusCode(),
				gitRest.setCodeFileEndings(request, "TEST", null).getStatus());
	}

	@Test
	public void testEverythingJustFine() {
		assertEquals(Status.OK.getStatusCode(),
				gitRest.setCodeFileEndings(request, "TEST", codeFileEndings).getStatus());
	}

	@AfterClass
	public static void tearDown() {
		// reset plugin settings to default settings
		MockPluginSettingsFactory.pluginSettings = new MockPluginSettings();
	}

}
