package de.uhd.ifi.se.decision.management.jira.rest.textclassificationrest;

import static org.junit.Assert.assertEquals;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response.Status;

import org.junit.Before;
import org.junit.Test;

import com.atlassian.jira.mock.servlet.MockHttpServletRequest;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.classification.FileManager;
import de.uhd.ifi.se.decision.management.jira.rest.TextClassificationRest;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraUsers;

public class TestTrainClassifier extends TestSetUp {

	private HttpServletRequest request;
	private TextClassificationRest classificationRest;

	@Before
	public void setUp() {
		init();
		classificationRest = new TextClassificationRest();
		request = new MockHttpServletRequest();
		request.setAttribute("user", JiraUsers.SYS_ADMIN.getApplicationUser());
		FileManager.copyDefaultTrainingDataToClassifierDirectory();
	}

	@Test
	public void testRequestNullProjectKeyNullTrainingFileNullDefaultClassifierTypes() {
		assertEquals(Status.BAD_REQUEST.getStatusCode(),
				classificationRest.trainClassifier(null, null, null, null, null).getStatus());
	}

	@Test
	public void testRequestNullProjectKeyNullTrainingFileProvidedDefaultClassifierTypes() {
		assertEquals(Status.BAD_REQUEST.getStatusCode(),
				classificationRest.trainClassifier(null, null, "trainingData.csv", null, null).getStatus());
	}

	@Test
	public void testRequestValidProjectKeyExistsTrainingFileNullDefaultClassifierTypes() {
		assertEquals(Status.BAD_REQUEST.getStatusCode(),
				classificationRest.trainClassifier(request, "TEST", null, null, null).getStatus());
	}

	@Test
	public void testRequestValidProjectKeyExistsTrainingFileEmptyDefaultClassifierTypes() {
		assertEquals(Status.BAD_REQUEST.getStatusCode(),
				classificationRest.trainClassifier(request, "TEST", "", null, null).getStatus());
	}

	@Test
	public void testRequestValidProjectKeyExistsTrainingFileNonExistentDefaultClassifierTypes() {
		assertEquals(Status.BAD_REQUEST.getStatusCode(),
				classificationRest.trainClassifier(request, "TEST", "fake.csv", "", null).getStatus());
	}

	@Test
	public void testRequestValidProjectKeyExistsTrainingFileExistentClassifierTypesSelected() {
		assertEquals(Status.OK.getStatusCode(), classificationRest
				.trainClassifier(request, "TEST", "defaultTrainingData.csv", "LR", "SVM").getStatus());
	}

	@Test
	public void testRequestValidProjectKeyExistsTrainingFileExistentClassifierTypesSelectedMLP() {
		assertEquals(Status.OK.getStatusCode(), classificationRest
				.trainClassifier(request, "TEST", "defaultTrainingData.csv", "MLP", "MLP").getStatus());
	}
}
