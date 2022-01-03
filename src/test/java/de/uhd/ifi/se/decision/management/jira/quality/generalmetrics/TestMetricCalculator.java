package de.uhd.ifi.se.decision.management.jira.quality.generalmetrics;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.filtering.FilterSettings;
import de.uhd.ifi.se.decision.management.jira.git.gitclient.TestSetUpGit;
import de.uhd.ifi.se.decision.management.jira.quality.completeness.DefinitionOfDone;
import net.java.ao.test.jdbc.NonTransactional;

public class TestMetricCalculator extends TestSetUpGit {

	private GeneralMetricCalculator calculator;

	@Override
	@Before
	public void setUp() {
		init();
		String projectKey = "TEST";
		FilterSettings filterSettings = new FilterSettings(projectKey, "");
		DefinitionOfDone definitionOfDone = new DefinitionOfDone();
		definitionOfDone.setMaximumLinkDistanceToDecisions(0);
		definitionOfDone.setMinimumDecisionsWithinLinkDistance(1);
		filterSettings.setDefinitionOfDone(definitionOfDone);
		calculator = new GeneralMetricCalculator(filterSettings);
	}

	@Test
	@NonTransactional
	public void testGeneralMetricsCalculator() {
		assertNotNull(calculator);
	}

	@Test
	@NonTransactional
	public void testGetNumberOfCommentsPerIssue() {
		assertEquals(1, calculator.getNumberOfCommentsMap().size());
	}

	@Test
	@NonTransactional
	public void testGetDistributionOfKnowledgeTypes() {
		assertEquals(calculator.getDistributionOfKnowledgeTypes().size(), 4);
	}

	@Test
	@NonTransactional
	public void testGetReqAndClassSummary() {
		assertEquals(calculator.getReqAndClassSummary().size(), 2);
	}

	@Test
	@NonTransactional
	public void testGetElementsFromDifferentOrigins() {
		assertEquals(calculator.getElementsFromDifferentOrigins().size(), 4);
	}

	@Test
	@NonTransactional
	public void testGetNumberOfRelevantComments() {
		assertEquals(calculator.getNumberOfRelevantComments().size(), 2);
	}

	@Test
	@NonTransactional
	public void testGetNumberOfCommits() {
		assertEquals(0, calculator.getNumberOfCommitsMap().size());
	}

	@Test
	@NonTransactional
	public void testGetDefinitionOfDoneCheckResults() {
		assertEquals(calculator.getDefinitionOfDoneCheckResults().size(), 2);
	}

	@Test
	@NonTransactional
	public void testGetDefinitionOfDoneCheckResultsFail() {
		String projectKey = "TEST";
		FilterSettings filterSettings = new FilterSettings(projectKey, "");
		DefinitionOfDone definitionOfDone = new DefinitionOfDone();
		definitionOfDone.setMaximumLinkDistanceToDecisions(6);
		definitionOfDone.setMinimumDecisionsWithinLinkDistance(0);
		filterSettings.setDefinitionOfDone(definitionOfDone);
		assertEquals((new GeneralMetricCalculator(filterSettings)).getDefinitionOfDoneCheckResults().size(), 2);
	}

}
