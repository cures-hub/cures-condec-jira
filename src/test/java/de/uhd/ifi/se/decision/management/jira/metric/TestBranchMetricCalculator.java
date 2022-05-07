package de.uhd.ifi.se.decision.management.jira.metric;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.filtering.FilterSettings;
import de.uhd.ifi.se.decision.management.jira.git.config.GitConfiguration;
import de.uhd.ifi.se.decision.management.jira.git.gitclient.TestSetUpGit;
import de.uhd.ifi.se.decision.management.jira.persistence.ConfigPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.testdata.CodeFiles;
import net.java.ao.test.jdbc.NonTransactional;

public class TestBranchMetricCalculator extends TestSetUpGit {

	private BranchMetricCalculator branchMetricsCalculator;
	private FilterSettings filterSettings;

	@Before
	public void setUp() {
		init();
		super.mockGitRepository();
		GitConfiguration config = ConfigPersistenceManager.getGitConfiguration("TEST");
		config.setActivated(true);
		ConfigPersistenceManager.saveGitConfiguration("TEST", config);
		CodeFiles.addCodeFilesToKnowledgeGraph();
		filterSettings = new FilterSettings("TEST", "");
		branchMetricsCalculator = new BranchMetricCalculator(filterSettings);
	}

	@Test
	@NonTransactional
	public void testBranchStatusMap() {
		assertEquals(3, branchMetricsCalculator.getBranchStatusMap().size());
	}

	@Test
	@NonTransactional
	public void testQualityProblemMap() {
		assertEquals(0, branchMetricsCalculator.getQualityProblemMap().size());
	}

	@Test
	@NonTransactional
	public void testJiraIssueMap() {
		super.mockGitRepository();
		assertEquals(1, branchMetricsCalculator.getJiraIssueMap().size());
	}

	@Test
	@NonTransactional
	public void testNumberOfElementsOfTypeMap() {
		super.mockGitRepository();
		assertEquals(2, branchMetricsCalculator.getNumberOfIssuesMap().size());
		assertEquals(2, branchMetricsCalculator.getNumberOfDecisionsMap().size());
		assertEquals(2, branchMetricsCalculator.getNumberOfAlternativesMap().size());
		assertEquals(2, branchMetricsCalculator.getNumberOfProsMap().size());
		assertEquals(1, branchMetricsCalculator.getNumberOfConsMap().size());
	}

	@Test
	@NonTransactional
	public void testGitConnectionDisabled() {
		GitConfiguration config = ConfigPersistenceManager.getGitConfiguration("TEST");
		config.setActivated(false);
		ConfigPersistenceManager.saveGitConfiguration("TEST", config);
		BranchMetricCalculator branchMetricsCalculator = new BranchMetricCalculator(filterSettings);
		assertNull(branchMetricsCalculator.getBranchesForProject());
	}
}
