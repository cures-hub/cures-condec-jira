package de.uhd.ifi.se.decision.management.jira.rest.textclassificationrest;

import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.mock.servlet.MockHttpServletRequest;
import com.google.common.collect.ImmutableMap;
import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.model.PartOfJiraIssueText;
import de.uhd.ifi.se.decision.management.jira.persistence.singlelocations.JiraIssueTextPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.rest.TextClassificationRest;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraIssues;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraUsers;
import org.junit.Before;
import org.junit.Test;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

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

		ImmutableMap<String, List<KnowledgeElement>> expected = ImmutableMap.of("nonValidatedElements", new ArrayList<>());

		assertEquals(expected, response.getEntity());
	}

//	@Test
//	public void testValidWithNonValidatedElements() {
//		Issue issue = jiraIssues.get(4);
//
//		PartOfJiraIssueText validatedElement = JiraIssues.addElementToDataBase(12234, KnowledgeType.ARGUMENT);
//		validatedElement.setJiraIssue(issue);
//		validatedElement.setValidated(true);
//		JiraIssues.updateElementInDatabase(validatedElement);
//
//		PartOfJiraIssueText nonValidatedElement = JiraIssues.addElementToDataBase(12235, KnowledgeType.ARGUMENT);
//		nonValidatedElement.setJiraIssue(issue);
//		nonValidatedElement.setValidated(false);
//		nonValidatedElement.setDescription("Dummy argument!");
//
//
//		List<KnowledgeElement> expectedElement = new ArrayList<>();
//		expectedElement.add(nonValidatedElement);
//
//		Response response = classificationRest.getNonValidatedElements(request, jiraIssues.get(4).getProjectObject().getKey(), jiraIssues.get(4).getKey());
//
//		ImmutableMap<String, List<KnowledgeElement>> expected = ImmutableMap.of("nonValidatedElements", expectedElement);
//		assertEquals(expected, response.getEntity());
//
//	}

}
