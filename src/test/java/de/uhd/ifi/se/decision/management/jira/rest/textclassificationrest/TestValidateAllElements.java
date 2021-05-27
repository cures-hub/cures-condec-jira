package de.uhd.ifi.se.decision.management.jira.rest.textclassificationrest;

import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.mock.servlet.MockHttpServletRequest;
import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.model.PartOfJiraIssueText;
import de.uhd.ifi.se.decision.management.jira.persistence.singlelocations.JiraIssueTextPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.rest.TextClassificationRest;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraIssues;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraUsers;
import net.java.ao.test.jdbc.NonTransactional;
import org.junit.Before;
import org.junit.Test;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TestValidateAllElements extends TestSetUp {

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
			classificationRest.validateAllElements(request, null, null).getStatus());

	}

	@Test
	@NonTransactional
	public void testValidRequestWithNonValidatedElements() {
		Issue issue = jiraIssues.get(0);


		PartOfJiraIssueText element1 = JiraIssues.addElementToDataBase(12234, KnowledgeType.ARGUMENT);
		element1.setJiraIssue(issue);
		element1.setValidated(false);

		PartOfJiraIssueText element2 = JiraIssues.addElementToDataBase(12235, KnowledgeType.ARGUMENT);
		element2.setJiraIssue(issue);
		element2.setValidated(false);

		JiraIssueTextPersistenceManager manager = new JiraIssueTextPersistenceManager(issue.getProjectObject().getKey());
		manager.updateInDatabase(element1);
		manager.updateInDatabase(element2);


		classificationRest.validateAllElements(request, issue.getProjectObject().getKey(), issue.getKey());
		List<KnowledgeElement> result = manager.getElementsInJiraIssue(issue.getId());

		assertTrue(((PartOfJiraIssueText) result.get(0)).isValidated());
		assertTrue(((PartOfJiraIssueText) result.get(1)).isValidated());

	}
}
