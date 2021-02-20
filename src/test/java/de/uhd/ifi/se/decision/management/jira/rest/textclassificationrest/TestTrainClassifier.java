package de.uhd.ifi.se.decision.management.jira.rest.textclassificationrest;

import static org.junit.Assert.assertEquals;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response.Status;

import org.junit.Before;
import org.junit.Test;

import com.atlassian.jira.mock.servlet.MockHttpServletRequest;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
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
	}

	@Test
	public void testRequestNullProjectKeyNullTrainingFileNull() {
		assertEquals(Status.BAD_REQUEST.getStatusCode(),
				classificationRest.trainClassifier(null, null, null).getStatus());
	}

	@Test
	public void testRequestNullProjectKeyNullTrainingFileProvided() {
		assertEquals(Status.BAD_REQUEST.getStatusCode(),
				classificationRest.trainClassifier(null, null, "trainingData.csv").getStatus());
	}

	@Test
	public void testRequestValidProjectKeyExistsTrainingFileNull() {
		assertEquals(Status.BAD_REQUEST.getStatusCode(),
				classificationRest.trainClassifier(request, "TEST", null).getStatus());
	}

	@Test
	public void testRequestValidProjectKeyExistsTrainingFileEmpty() {
		assertEquals(Status.BAD_REQUEST.getStatusCode(),
				classificationRest.trainClassifier(request, "TEST", "").getStatus());
	}

	@Test
	public void testRequestValidProjectKeyExistsTrainingFileNonExistent() {
		// ok because it falls back on the default training data
		assertEquals(Status.OK.getStatusCode(),
				classificationRest.trainClassifier(request, "TEST", "fake.csv").getStatus());
	}
}
