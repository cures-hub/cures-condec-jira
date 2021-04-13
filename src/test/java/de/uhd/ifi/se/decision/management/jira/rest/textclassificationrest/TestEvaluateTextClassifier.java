package de.uhd.ifi.se.decision.management.jira.rest.textclassificationrest;

import static org.junit.Assert.assertEquals;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;

import org.junit.Before;
import org.junit.Test;

import com.atlassian.jira.mock.servlet.MockHttpServletRequest;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.classification.TestGroundTruthData;
import de.uhd.ifi.se.decision.management.jira.classification.TextClassifier;
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
	public void testRequestNullProjectKeyNullGroundTruthNullDefaultClassifierTypes() {
		assertEquals(Response.Status.BAD_REQUEST.getStatusCode(),
				classificationRest.evaluateTextClassifier(null, null, null, 3, null, null).getStatus());
	}

	@Test
	public void testRequestValidProjectKeyExistsTrainedClassifier() {
		TextClassifier trainer = TextClassifier.getInstance("TEST");
		trainer.setGroundTruthFile(TestGroundTruthData.getTestGroundTruthDataFile());
		trainer.train();
		assertEquals(Response.Status.OK.getStatusCode(), classificationRest
				.evaluateTextClassifier(request, "TEST", "defaultTrainingData.csv", -1, null, null).getStatus());
	}

	@Test
	public void testRequestValidProjectKeyGroundTruthValidThreeFoldCrossValidationDefaultClassifierTypes() {
		assertEquals(Response.Status.OK.getStatusCode(), classificationRest
				.evaluateTextClassifier(request, "TEST", "defaultTrainingData.csv", 3, null, "").getStatus());
	}

	@Test
	public void testRequestValidProjectKeyGroundTruthValidThreeFoldCrossValidationSVMClassifiers() {
		assertEquals(Response.Status.OK.getStatusCode(), classificationRest
				.evaluateTextClassifier(request, "TEST", "defaultTrainingData.csv", 3, "SVM", "SVM").getStatus());
	}
}
