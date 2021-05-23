package de.uhd.ifi.se.decision.management.jira.quality.completeness;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import de.uhd.ifi.se.decision.management.jira.filtering.FilterSettings;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.testdata.KnowledgeElements;
import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;

import net.java.ao.test.jdbc.NonTransactional;

public class TestCoverageHandler  extends TestSetUp {

	private FilterSettings filterSettings;

	@Before
	public void setUp() {
		init();
		filterSettings = new FilterSettings("TEST", "");
	}

	@Test
	@NonTransactional
	public void testCoverageHandlerDoesNotHaveMinimumCoverageTrue() {
		System.out.println(KnowledgeElements.getTestKnowledgeElement().getType());
		assertTrue(CoverageHandler.doesNotHaveMinimumCoverage(KnowledgeElements.getTestKnowledgeElement(), KnowledgeType.DECISION, filterSettings));
	}

	@Test
	@NonTransactional
	public void testCoverageHandlerDoesNotHaveMinimumCoverageFalse() {
		assertFalse(CoverageHandler.doesNotHaveMinimumCoverage(KnowledgeElements.getTestKnowledgeElement(), KnowledgeType.OTHER, filterSettings));
	}
}
