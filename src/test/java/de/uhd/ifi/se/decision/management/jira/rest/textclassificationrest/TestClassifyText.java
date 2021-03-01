package de.uhd.ifi.se.decision.management.jira.rest.textclassificationrest;

import static org.junit.Assert.assertEquals;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response.Status;

import org.junit.Before;
import org.junit.Test;

import com.atlassian.jira.mock.servlet.MockHttpServletRequest;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.classification.TestTextClassifier;
import de.uhd.ifi.se.decision.management.jira.classification.TextClassifier;
import de.uhd.ifi.se.decision.management.jira.rest.TextClassificationRest;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraUsers;

public class TestClassifyText extends TestSetUp {

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
	public void testRequestNullProjectKeyNullTextNull() {
		assertEquals(Status.BAD_REQUEST.getStatusCode(), classificationRest.classifyText(null, null, null).getStatus());
	}

	@Test
	public void testRequestValidProjectKeyValidTextValid() {
		TextClassifier classifier = TextClassifier.getInstance("TEST");
		classifier.setTrainingFile(TestTextClassifier.getTestTrainingDataFile());
		classifier.train();
		assertEquals(Status.OK.getStatusCode(),
				classificationRest.classifyText(request, "TEST", "How can we implement?").getStatus());
	}

}
