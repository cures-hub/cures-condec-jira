package de.uhd.ifi.se.decision.management.jira.rest.textclassificationrest;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;

import org.junit.Before;
import org.junit.Test;

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
import net.java.ao.test.jdbc.NonTransactional;

public class TestGetAllNonValidatedElements extends TestSetUp {

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
				classificationRest.getAllNonValidatedElements(request, null).getStatus());

	}

	@Test
	public void testValidWithNoNonValidatedElements() {
		Response response = classificationRest.getAllNonValidatedElements(request,
				jiraIssues.get(0).getProjectObject().getKey());

		ImmutableMap<String, List<KnowledgeElement>> expected = ImmutableMap.of("nonValidatedElements",
				new ArrayList<>());

		assertEquals(expected, response.getEntity());
	}

	@Test
	@NonTransactional
	public void testValidWithNonValidatedElements() {
		Issue issue = jiraIssues.get(0);

		PartOfJiraIssueText nonValidatedElement1 = JiraIssues.addElementToDataBase(12234, KnowledgeType.ARGUMENT);
		nonValidatedElement1.setJiraIssue(issue);
		nonValidatedElement1.setValidated(false);

		PartOfJiraIssueText nonValidatedElement2 = JiraIssues.addElementToDataBase(12235, KnowledgeType.ARGUMENT);
		nonValidatedElement2.setJiraIssue(issue);
		nonValidatedElement2.setValidated(false);

		JiraIssueTextPersistenceManager manager = new JiraIssueTextPersistenceManager(
				issue.getProjectObject().getKey());
		manager.updateInDatabase(nonValidatedElement1);
		manager.updateInDatabase(nonValidatedElement2);

		List<KnowledgeElement> expectedElements = Arrays.asList(nonValidatedElement1, nonValidatedElement2);

		Response response = classificationRest.getAllNonValidatedElements(request, "TEST");
		ImmutableMap<String, List<KnowledgeElement>> expected = ImmutableMap.of("nonValidatedElements",
				expectedElements);

		assertEquals(expected, response.getEntity());

	}

}
