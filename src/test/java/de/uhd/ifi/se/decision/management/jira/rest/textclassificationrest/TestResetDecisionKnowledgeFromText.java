package de.uhd.ifi.se.decision.management.jira.rest.textclassificationrest;

import static org.junit.Assert.assertEquals;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response.Status;

import org.junit.Before;
import org.junit.Test;

import com.atlassian.jira.mock.servlet.MockHttpServletRequest;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.rest.TextClassificationRest;
import net.java.ao.test.jdbc.NonTransactional;

public class TestResetDecisionKnowledgeFromText extends TestSetUp {
	private TextClassificationRest textClassificationRest;
	private HttpServletRequest request;

	@Before
	public void setUp() {
		init();
		textClassificationRest = new TextClassificationRest();
		request = new MockHttpServletRequest();
	}

	@Test
	@NonTransactional
	public void testRequestValidJiraIssueValid() {
		assertEquals(Status.OK.getStatusCode(),
				textClassificationRest.resetDecisionKnowledgeFromText(request, 1L).getStatus());
	}

	@Test
	public void testRequestValidJiraIssueNotExisting() {
		assertEquals(Status.BAD_REQUEST.getStatusCode(),
				textClassificationRest.resetDecisionKnowledgeFromText(request, 119283L).getStatus());
	}

	@Test
	public void testRequestValidJiraIssueNull() {
		assertEquals(Status.BAD_REQUEST.getStatusCode(),
				textClassificationRest.resetDecisionKnowledgeFromText(request, null).getStatus());
	}

	@Test
	public void testRequestNullJiraIssueValid() {
		assertEquals(Status.BAD_REQUEST.getStatusCode(),
				textClassificationRest.resetDecisionKnowledgeFromText(null, 1L).getStatus());
	}
}
