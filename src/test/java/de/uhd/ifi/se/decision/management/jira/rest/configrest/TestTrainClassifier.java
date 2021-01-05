package de.uhd.ifi.se.decision.management.jira.rest.configrest;

import static org.junit.Assert.assertEquals;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;

import org.junit.Before;
import org.junit.Test;

import com.atlassian.jira.mock.servlet.MockHttpServletRequest;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.rest.ConfigRest;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraUsers;

public class TestTrainClassifier extends TestSetUp {

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
	public void testRequestNullProjectKeyNullTrainingFileNull() {
		assertEquals(Response.Status.BAD_REQUEST.getStatusCode(),
				configRest.trainClassifier(null, null, null).getStatus());
	}

	@Test
	public void testRequestNullProjectKeyNullTrainingFileProvided() {
		assertEquals(Response.Status.BAD_REQUEST.getStatusCode(),
				configRest.trainClassifier(null, null, "trainingData.csv").getStatus());
	}

	@Test
	public void testRequestValidProjectKeyExistsTrainingFileNull() {
		assertEquals(Response.Status.BAD_REQUEST.getStatusCode(),
				configRest.trainClassifier(request, "TEST", null).getStatus());
	}

	@Test
	public void testRequestValidProjectKeyExistsTrainingFileEmpty() {
		assertEquals(Response.Status.BAD_REQUEST.getStatusCode(),
				configRest.trainClassifier(request, "TEST", "").getStatus());
	}

	@Test
	public void testRequestValidProjectKeyExistsTrainingFileNonExistent() {
		assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(),
				configRest.trainClassifier(request, "TEST", "fake.csv").getStatus());
	}
}
