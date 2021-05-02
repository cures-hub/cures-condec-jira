package de.uhd.ifi.se.decision.management.jira.quality.completeness;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import org.junit.Before;
import org.junit.Test;

import com.atlassian.jira.user.ApplicationUser;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.filtering.FilterSettings;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraIssueTypes;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraUsers;
import de.uhd.ifi.se.decision.management.jira.testdata.KnowledgeElements;
import net.java.ao.test.jdbc.NonTransactional;

public class TestRationaleCoverageCalculator extends TestSetUp {

	protected RationaleCoverageCalculator calculator;

	@Before
	public void setUp() {
		init();
		List<KnowledgeElement> knowledgeElements = KnowledgeElements.getTestKnowledgeElements();
		assertEquals(22, knowledgeElements.size());
		ApplicationUser user = JiraUsers.SYS_ADMIN.getApplicationUser();
		FilterSettings filterSettings = new FilterSettings("TEST", "");
		filterSettings.setLinkDistance(3);
		calculator = new RationaleCoverageCalculator(user, filterSettings);
	}

	@Test
	@NonTransactional
	public void testGetKnowledgeElementsWithNeighborsOfOtherTypeNull() {
		Set<String> knowledgeTypes = new HashSet<>();
		knowledgeTypes.add("Task");
		assertNull(calculator.getKnowledgeElementsWithNeighborsOfOtherType(knowledgeTypes, null));
	}

	@Test
	@NonTransactional
	public void testGetKnowledgeElementsWithNeighborsOfOtherTypeFilled() {
		Set<String> knowledgeTypes = new HashSet<>();
		knowledgeTypes.add("Task");
		Map<String, String> calculation = calculator
				.getKnowledgeElementsWithNeighborsOfOtherType(knowledgeTypes, KnowledgeType.ISSUE);

		assertTrue(calculation.containsKey("More than 2 links from selected Jira issue types to Issue"));
		assertFalse(calculation.get("More than 2 links from selected Jira issue types to Issue").isEmpty());
		assertTrue(calculation.containsKey("Less than 2 links from selected Jira issue types to Issue"));
		assertFalse(calculation.get("Less than 2 links from selected Jira issue types to Issue").isEmpty());
		assertTrue(calculation.containsKey("No links from selected Jira issue types to Issue"));
		assertTrue(calculation.get("No links from selected Jira issue types to Issue").isEmpty());
	}

	@Test
	@NonTransactional
	public void testGetNumberOfDecisionKnowledgeElementsForKnowledgeElementsNull() {
		assertNull(calculator.getNumberOfDecisionKnowledgeElementsForKnowledgeElements(null, null));
	}

	@Test
	@NonTransactional
	public void testGetNumberOfDecisionKnowledgeElementsForKnowledgeElementsFilled() {
		Set<String> knowledgeTypes = new HashSet<>();
		knowledgeTypes.add("Task");
		assertEquals(5, calculator.getNumberOfDecisionKnowledgeElementsForKnowledgeElements(knowledgeTypes, KnowledgeType.ISSUE).size());
	}

}
