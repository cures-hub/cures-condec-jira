package de.uhd.ifi.se.decision.management.jira.rest.definitionofdonecheckingrest;

import static org.junit.Assert.assertEquals;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response.Status;

import org.junit.Before;
import org.junit.Test;

import com.atlassian.jira.mock.servlet.MockHttpServletRequest;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.quality.DefinitionOfDone;
import de.uhd.ifi.se.decision.management.jira.rest.DefinitionOfDoneCheckingRest;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraUsers;

public class TestSetDefinitionOfDone extends TestSetUp {

	protected HttpServletRequest request;
	protected DefinitionOfDoneCheckingRest dodCheckingRest;

	@Before
	public void setUp() {
		init();
		dodCheckingRest = new DefinitionOfDoneCheckingRest();
		request = new MockHttpServletRequest();
		request.setAttribute("user", JiraUsers.SYS_ADMIN.getApplicationUser());
	}

	@Test
	public void testRequestValidProjectValidDoDValid() {
		DefinitionOfDone definitionOfDone = new DefinitionOfDone();
		assertEquals(Status.OK.getStatusCode(),
				dodCheckingRest.setDefinitionOfDone(request, "TEST", definitionOfDone).getStatus());
	}

	@Test
	public void testRequestValidProjectInvalidDoDValid() {
		DefinitionOfDone definitionOfDone = new DefinitionOfDone();
		assertEquals(Status.BAD_REQUEST.getStatusCode(),
				dodCheckingRest.setDefinitionOfDone(request, "", definitionOfDone).getStatus());
	}

	@Test
	public void testRequestValidProjectInvalidDoDNull() {
		assertEquals(Status.BAD_REQUEST.getStatusCode(),
				dodCheckingRest.setDefinitionOfDone(request, "TEST", null).getStatus());
	}
}