package de.uhd.ifi.se.decision.management.jira.rest.configrest;

import static org.junit.Assert.assertEquals;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;

import org.junit.Before;
import org.junit.Test;

import com.atlassian.jira.mock.servlet.MockHttpServletRequest;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.classification.TestClassifierTrainer;
import de.uhd.ifi.se.decision.management.jira.classification.implementation.ClassifierTrainer;
import de.uhd.ifi.se.decision.management.jira.rest.ConfigRest;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraUsers;

public class TestEvaluateModel extends TestSetUp {

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
		assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), configRest.evaluateModel(null, null).getStatus());
	}

	// @Test
	// public void testRequestValidProjectKeyExistsUntrainedClassifier() {
	// assertEquals(Response.Status.SERVICE_UNAVAILABLE.getStatusCode(),
	// configRest.evaluateModel(request, "TEST"));
	// }

	@Test
	public void testRequestValidProjectKeyExistsTrainedClassifier() {
		ClassifierTrainer trainer = new ClassifierTrainer("TEST");
		trainer.setTrainingFile(TestClassifierTrainer.getTestTrainingDataFile());
		trainer.train();
		assertEquals(Response.Status.OK.getStatusCode(), configRest.evaluateModel(request, "TEST").getStatus());
	}
}
