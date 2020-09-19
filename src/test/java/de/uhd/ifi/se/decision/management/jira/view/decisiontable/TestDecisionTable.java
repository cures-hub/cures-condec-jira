package de.uhd.ifi.se.decision.management.jira.view.decisiontable;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.atlassian.jira.mock.servlet.MockHttpServletRequest;
import com.atlassian.jira.user.ApplicationUser;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.filtering.FilterSettings;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeGraph;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraUsers;
import de.uhd.ifi.se.decision.management.jira.testdata.KnowledgeElements;

public class TestDecisionTable extends TestSetUp {

	private DecisionTable decisionTable;
	private ApplicationUser user;
	private FilterSettings filterSettings;

	@Before
	public void setUp() {
		init();
		decisionTable = new DecisionTable("TEST");
		user = JiraUsers.SYS_ADMIN.getApplicationUser();
		filterSettings = new FilterSettings("TEST", null);
		filterSettings.setLinkDistance(3);
	}

	@Test
	public void testGetEmptyDecisionIssues() {
		filterSettings.setSelectedElement("TEST-30");
		filterSettings.setLinkDistance(0);
		decisionTable.setIssues(filterSettings, user);
		assertEquals(0, decisionTable.getIssues().size());
	}

	@Test
	public void testGetDecisionIssueOnIssueDirectly() {
		filterSettings.setSelectedElement("TEST-1");
		decisionTable.setIssues(filterSettings, user);
		assertEquals(2, decisionTable.getIssues().size());
	}

	@Test
	public void testGetDecisionIssueLinkDistanceZero() {
		filterSettings.setSelectedElement("TEST-1");
		filterSettings.setLinkDistance(0);
		decisionTable.setIssues(filterSettings, user);
		assertEquals(0, decisionTable.getIssues().size());
	}

	@Test
	public void testGetAlternativesOnIssueDirectly() {
		assertEquals(15, KnowledgeGraph.getOrCreate("TEST").edgeSet().size());
		assertEquals(10, KnowledgeGraph.getOrCreate("TEST").vertexSet().size());
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setAttribute("user", user);
		KnowledgeElement issue = KnowledgeElements.getTestKnowledgeElements().get(3);
		decisionTable.setDecisionTableForIssue(issue, user);
		Map<String, List<DecisionTableElement>> decisionTableData = decisionTable.getDecisionTableData();

		assertEquals(2, decisionTableData.get("alternatives").size());

		Alternative alternative1 = (Alternative) decisionTableData.get("alternatives").get(0);
		Alternative alternative2 = (Alternative) decisionTableData.get("alternatives").get(1);

		assertNotNull(alternative1);
		assertNotNull(alternative2);

		assertEquals(1, alternative1.getArguments().size());
		assertEquals(1, alternative2.getArguments().size());

		Argument argument = alternative1.getArguments().get(0);
		assertNotNull(argument);
		assertNotNull(argument.getCriterion());
	}

	@Test
	public void testGetArgumentsOnIssueDirectly() {
		KnowledgeElement knowledgeElement;
		Map<String, List<DecisionTableElement>> decisionTableData = new HashMap<>();
		decisionTableData.put("alternatives", new ArrayList<>());
		decisionTableData.put("criteria", new ArrayList<>());

		knowledgeElement = KnowledgeElements.getTestKnowledgeElements().get(3);
		decisionTableData.get("alternatives").add(new Alternative(knowledgeElement));
		decisionTable.getArguments(knowledgeElement);
		Alternative alternative1 = (Alternative) decisionTableData.get("alternatives").get(0);
		assertEquals(0, alternative1.getArguments().size());

		knowledgeElement = KnowledgeElements.getTestKnowledgeElements().get(4);
		decisionTableData.get("alternatives").add(new Alternative(knowledgeElement));
		decisionTable.getArguments(knowledgeElement);
		Alternative alternative2 = (Alternative) decisionTableData.get("alternatives").get(1);
		assertEquals(0, alternative2.getArguments().size());
	}

	@Test
	public void testGetArgumentCriteriaOnIssueDirectly() {
		List<DecisionTableElement> criteriaList = new ArrayList<>();
		Argument argument;
		argument = new Argument(KnowledgeElements.getTestKnowledgeElements().get(5));
		decisionTable.getArgumentCriteria(argument, criteriaList);
		assertNotNull(argument.getCriterion());
		assertEquals(2, criteriaList.size());
	}
}
