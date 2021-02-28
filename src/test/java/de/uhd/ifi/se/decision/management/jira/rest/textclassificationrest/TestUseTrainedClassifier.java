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

public class TestUseTrainedClassifier extends TestSetUp {

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
	public void testRequestNullProjectKeyNullTrainedClassifierNull() {
		assertEquals(Status.BAD_REQUEST.getStatusCode(),
				classificationRest.useTrainedClassifier(request, null, null).getStatus());
	}

	@Test
	public void testRequestNullProjectKeyValidTrainedClassifierNull() {
		assertEquals(Status.BAD_REQUEST.getStatusCode(),
				classificationRest.useTrainedClassifier(request, "TEST", null).getStatus());
	}

	@Test
	public void testRequestNullProjectKeyValidTrainedClassifierEmpty() {
		assertEquals(Status.BAD_REQUEST.getStatusCode(),
				classificationRest.useTrainedClassifier(request, "TEST", "").getStatus());
	}

	@Test
	public void testRequestNullProjectKeyValidTrainedClassifierValid() {
		assertEquals(Status.OK.getStatusCode(),
				classificationRest.useTrainedClassifier(request, "TEST", "defaultTrainingData").getStatus());
	}

}
