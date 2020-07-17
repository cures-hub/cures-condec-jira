package de.uhd.ifi.se.decision.management.jira.view.decisiontable;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.atlassian.jira.mock.servlet.MockHttpServletRequest;
import com.atlassian.jira.user.ApplicationUser;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.model.DocumentationLocation;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.persistence.KnowledgePersistenceManager;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraUsers;

public class TestDecisionTable extends TestSetUp {

	private DecisionTable decisionTable;
	final private String projectKey = "TEST";

	@Before
	public void setUp() {
		init();
		this.decisionTable = new DecisionTable(projectKey);
	}

	@Test
	public void testGetEmptyDecisionIssues() {
		decisionTable.setIssues("TEST-30");
		assertEquals(0, decisionTable.getIssues().size());
	}

	@Test
	public void testGetDecisionIssueOnIssueDirectly() {
		decisionTable.setIssues("TEST-1");
		assertEquals(2, decisionTable.getIssues().size());
	}

	@Test
	public void testGetAlternativesOnIssueDirectly() {
		ApplicationUser user = JiraUsers.SYS_ADMIN.getApplicationUser();
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setAttribute("user", user);
		decisionTable.setDecisionTableForIssue(2, DocumentationLocation.JIRAISSUE.getIdentifier(), user);
		Map<String, List<DecisionTableElement>> decisionTableData = decisionTable.getDecisionTableData();

		assertEquals(3, decisionTableData.get("alternatives").size());
		
		Alternative alternative1 = (Alternative) decisionTableData.get("alternatives").get(0);
		Alternative alternative2 = (Alternative) decisionTableData.get("alternatives").get(1);
		Alternative alternative3 = (Alternative) decisionTableData.get("alternatives").get(2);

		assertNotNull(alternative1);
		assertNotNull(alternative2);
		assertNotNull(alternative3);

		assertEquals(1, alternative1.getArguments().size());
		assertEquals(0, alternative2.getArguments().size());
		assertEquals(0, alternative3.getArguments().size());

		Argument argument = (Argument) alternative1.getArguments().get(0);
		assertNotNull(argument);
		assertNotNull(argument.getCriterion());
	}
	
	@Test
	public void testGetArgumentsOnIssueDirectly() {
		KnowledgeElement knowledgeElement;
		Map<String, List<DecisionTableElement>> decisionTableData = new HashMap<>();
		decisionTableData.put("alternatives", new ArrayList<>());

		knowledgeElement = KnowledgePersistenceManager.getOrCreate(projectKey).getJiraIssueManager()
				.getKnowledgeElement(2);
		decisionTableData.get("alternatives").add(new Alternative(knowledgeElement));
		decisionTable.getArguments(2, decisionTableData, DocumentationLocation.JIRAISSUE.getIdentifier());
		Alternative alternative1 = (Alternative) decisionTableData.get("alternatives").get(0);
		assertEquals(1, alternative1.getArguments().size());

		knowledgeElement = KnowledgePersistenceManager.getOrCreate(projectKey).getJiraIssueManager()
				.getKnowledgeElement(12);
		decisionTableData.get("alternatives").add(new Alternative(knowledgeElement));
		decisionTable.getArguments(12, decisionTableData, DocumentationLocation.JIRAISSUE.getIdentifier());
		Alternative alternative2 = (Alternative) decisionTableData.get("alternatives").get(1);
		assertEquals(1, alternative2.getArguments().size());
	}

	@Test
	public void testGetArgumentCriteriaOnIssueDirectly() {
		List<DecisionTableElement> criteriaList = new ArrayList<>();
		Argument argument;
		argument = new Argument(
				KnowledgePersistenceManager.getOrCreate(projectKey).getJiraIssueManager().getKnowledgeElement(5));
		decisionTable.getArgumentCriteria(argument, criteriaList);
		assertNotNull(argument.getCriterion());
		assertEquals(1, criteriaList.size());
	}
}
