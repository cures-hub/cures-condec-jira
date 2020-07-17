package de.uhd.ifi.se.decision.management.jira.view.decisiontable;

import static org.junit.Assert.assertEquals;

import javax.servlet.http.HttpServletRequest;

import org.junit.Before;
import org.junit.Test;

import com.atlassian.jira.mock.servlet.MockHttpServletRequest;
import com.atlassian.jira.user.ApplicationUser;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.config.AuthenticationManager;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.persistence.KnowledgePersistenceManager;
import de.uhd.ifi.se.decision.management.jira.persistence.singlelocations.JiraIssuePersistenceManager;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraUsers;

public class TestDecisionTable extends TestSetUp {

	private DecisionTable decisionTable;
	final private String projectKey = "TEST";
	private HttpServletRequest request;

	@Before
	public void setUp() {
		init();
		request = new MockHttpServletRequest();
		request.setAttribute("user", JiraUsers.SYS_ADMIN.getApplicationUser());
		ApplicationUser user = AuthenticationManager.getUser(request);

		JiraIssuePersistenceManager pm = KnowledgePersistenceManager.getOrCreate(projectKey).getJiraIssueManager();

		KnowledgeElement jiraIssue = new KnowledgeElement();
		jiraIssue.setProject(projectKey);
		jiraIssue.setType(KnowledgeType.OTHER);

		KnowledgeElement decisionProblem = new KnowledgeElement();
		decisionProblem.setProject(projectKey);
		decisionProblem.setType(KnowledgeType.ISSUE);

		KnowledgeElement alternative = new KnowledgeElement();
		alternative.setProject(projectKey);
		alternative.setType(KnowledgeType.ALTERNATIVE);

		KnowledgeElement argument = new KnowledgeElement();
		argument.setProject(projectKey);
		argument.setType(KnowledgeType.PRO);

		pm.insertKnowledgeElement(jiraIssue, user);
		pm.insertKnowledgeElement(decisionProblem, user);
		pm.insertKnowledgeElement(alternative, user);
		pm.insertKnowledgeElement(argument, user);

		this.decisionTable = new DecisionTable(projectKey);
	}

	@Test
	public void testGetEmptyDecisionIssues() {
		decisionTable.setIssues("TEST-1");
		assertEquals(0, decisionTable.getIssues().size());
	}

	@Test
	public void testGetDecisionIssueOnIssueDirectly() {
		decisionTable.setIssues("TEST-2");
		assertEquals(1, decisionTable.getIssues().size());
	}
}
