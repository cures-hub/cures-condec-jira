package de.uhd.ifi.se.decision.management.jira.view.decisiontable;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.atlassian.jira.mock.servlet.MockHttpServletRequest;
import com.atlassian.jira.user.ApplicationUser;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.filtering.FilterSettings;
import de.uhd.ifi.se.decision.management.jira.model.Alternative;
import de.uhd.ifi.se.decision.management.jira.model.Argument;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
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
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setAttribute("user", user);
		KnowledgeElement issue = KnowledgeElements.getTestKnowledgeElements().get(4);
		decisionTable.setDecisionTableForIssue(issue, user);
		assertTrue(decisionTable.getAlternatives().size() > 0);

		Alternative alternative1 = decisionTable.getAlternatives().get(0);

		assertNotNull(alternative1);

		assertEquals(1, alternative1.getArguments().size());

		Argument argument = alternative1.getArguments().get(0);
		assertNotNull(argument);
		assertNotNull(argument.getCriterion());
	}

	@Test
	public void testGetArgumentCriteriaOnIssueDirectly() {
		List<KnowledgeElement> criteria = new ArrayList<>();
		Argument argument = new Argument(KnowledgeElements.getTestKnowledgeElements().get(7));
		decisionTable.getArgumentCriteria(argument, criteria);
		assertNotNull(argument.getCriterion());
		assertEquals(2, criteria.size());
	}
}
