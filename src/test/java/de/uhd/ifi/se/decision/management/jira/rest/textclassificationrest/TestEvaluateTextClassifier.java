package de.uhd.ifi.se.decision.management.jira.rest.textclassificationrest;

import static org.junit.Assert.assertEquals;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;

import org.junit.Before;
import org.junit.Test;

import com.atlassian.jira.mock.servlet.MockHttpServletRequest;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.classification.ClassifierTrainer;
import de.uhd.ifi.se.decision.management.jira.classification.TestClassifierTrainer;
import de.uhd.ifi.se.decision.management.jira.rest.TextClassificationRest;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraUsers;

public class TestEvaluateTextClassifier extends TestSetUp {

	private HttpServletRequest request;
	private TextClassificationRest classificationRest;

	@Before
	public void setUp() {
		init();
		classificationRest = new TextClassificationRest();
		request = new MockHttpServletRequest();
		request.setAttribute("user", JiraUsers.SYS_ADMIN.getApplicationUser());
	}

	@Test
	public void testRequestNullProjectKeyNullGroundTruthNull() {
		assertEquals(Response.Status.BAD_REQUEST.getStatusCode(),
				classificationRest.evaluateTextClassifier(null, null, null, 3).getStatus());
	}

	@Test
	public void testRequestValidProjectKeyExistsTrainedClassifier() {
		ClassifierTrainer trainer = new ClassifierTrainer("TEST");
		trainer.setTrainingFile(TestClassifierTrainer.getTestTrainingDataFile());
		trainer.train();
		assertEquals(Response.Status.OK.getStatusCode(),
				classificationRest.evaluateTextClassifier(request, "TEST", "defaultTrainingData.csv", -1).getStatus());
	}

	@Test
	public void testRequestValidProjectKeyGroundTruthValidThreeFoldCrossValidation() {
		assertEquals(Response.Status.OK.getStatusCode(),
				classificationRest.evaluateTextClassifier(request, "TEST", "defaultTrainingData.csv", 3).getStatus());
	}
}
