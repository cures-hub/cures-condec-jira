package de.uhd.ifi.se.decision.management.jira.rest.textclassificationrest;

import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.mock.servlet.MockHttpServletRequest;
import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.rest.TextClassificationRest;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraIssues;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraUsers;
import org.junit.Before;
import org.junit.Test;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class TestGetNonValidatedElements extends TestSetUp {

	private HttpServletRequest request;
	private TextClassificationRest classificationRest;
	private List<Issue> jiraIssues;

	@Before
	public void setUp() {
		init();
		classificationRest = new TextClassificationRest();
		request = new MockHttpServletRequest();
		request.setAttribute("user", JiraUsers.SYS_ADMIN.getApplicationUser());
		jiraIssues = JiraIssues.getTestJiraIssues();

	}

	@Test
	public void testValidRequestNullProjectKeyNullIssueKey() {
		assertEquals(Response.Status.BAD_REQUEST.getStatusCode(),
			classificationRest.getNonValidatedElements(request, null, null).getStatus());

	}

	@Test
	public void testValidWithNoNonValidatedElements() {
		Response response = classificationRest.getNonValidatedElements(request, jiraIssues.get(0).getProjectObject().getKey(), jiraIssues.get(0).getKey());

		Map<String, List<KnowledgeElement>> expected = Map.of("nonValidatedElements", new ArrayList<>());

		assertEquals(expected, response.getEntity());
	}

}
