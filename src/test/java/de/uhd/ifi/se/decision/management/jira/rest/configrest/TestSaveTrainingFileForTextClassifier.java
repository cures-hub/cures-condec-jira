package de.uhd.ifi.se.decision.management.jira.rest.configrest;

import static org.junit.Assert.assertEquals;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response.Status;

import org.junit.Before;
import org.junit.Test;

import com.atlassian.jira.mock.servlet.MockHttpServletRequest;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.rest.ConfigRest;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraIssues;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraUsers;
import net.java.ao.test.jdbc.NonTransactional;

public class TestSaveTrainingFileForTextClassifier extends TestSetUp {

	private HttpServletRequest request;
	private ConfigRest configRest;

	@Before
	public void setUp() {
		init();
		configRest = new ConfigRest();
		request = new MockHttpServletRequest();
		request.setAttribute("user", JiraUsers.SYS_ADMIN.getApplicationUser());
	}

	@Test
	public void testRequestNullProjectKeyNull() {
		assertEquals(Status.BAD_REQUEST.getStatusCode(),
				configRest.saveTrainingFileForTextClassifier(null, null).getStatus());
	}

	@Test
	public void testRequestNullProjectKeyExists() {
		assertEquals(Status.BAD_REQUEST.getStatusCode(),
				configRest.saveTrainingFileForTextClassifier(null, "TEST").getStatus());
	}

	@Test
	public void testRequestValidProjectKeyNull() {
		assertEquals(Status.BAD_REQUEST.getStatusCode(),
				configRest.saveTrainingFileForTextClassifier(request, null).getStatus());
	}

	@Test
	@NonTransactional
	public void testRequestValidProjectKeyExists() {
		JiraIssues.addElementToDataBase();
		assertEquals(Status.OK.getStatusCode(),
				configRest.saveTrainingFileForTextClassifier(request, "TEST").getStatus());
	}
}
