package de.uhd.ifi.se.decision.management.jira.metric;

import static org.junit.Assert.assertEquals;

import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.filtering.FilterSettings;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.testdata.CodeFiles;
import de.uhd.ifi.se.decision.management.jira.testdata.KnowledgeElements;
import net.java.ao.test.jdbc.NonTransactional;

public class TestRationaleCoverageCalculator extends TestSetUp {

	private RationaleCoverageCalculator rationaleCoverageCalculator;
	private FilterSettings filterSettings;

	@Before
	public void setUp() {
		init();
		CodeFiles.addCodeFilesToKnowledgeGraph();
		filterSettings = new FilterSettings("TEST", "");
		filterSettings.setKnowledgeTypesToBeCoveredWithRationale(Set.of("Task"));
		rationaleCoverageCalculator = new RationaleCoverageCalculator(filterSettings);
	}

	@Test
	@NonTransactional
	public void testGetDecisionCoverageMetric() {
		assertEquals(2, rationaleCoverageCalculator.getDecisionCoverageMetric().size());
	}

	@Test
	@NonTransactional
	public void testGetIssueCoverageMetric() {
		assertEquals(2, rationaleCoverageCalculator.getIssueCoverageMetric().size());
	}

	@Test
	@NonTransactional
	public void testGetReachableElementsOfType() {
		assertEquals(1,
				RationaleCoverageCalculator.getReachableElementsOfType(KnowledgeElements.getTestKnowledgeElement(),
						KnowledgeType.DECISION, filterSettings).size());
		assertEquals(2,
				RationaleCoverageCalculator.getReachableElementsOfType(KnowledgeElements.getTestKnowledgeElement(),
						KnowledgeType.ISSUE, filterSettings).size());
		assertEquals(0, RationaleCoverageCalculator
				.getReachableElementsOfType(KnowledgeElements.getTestKnowledgeElement(), null, filterSettings).size());
	}
}
