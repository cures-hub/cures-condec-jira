package de.uhd.ifi.se.decision.management.jira.quality.completeness;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;

public class TestRationaleCoverageMetric {

	private RationaleCoverageMetric metric;

	@Before
	public void setUp() {
		metric = new RationaleCoverageMetric(KnowledgeType.DECISION);
	}

	@Test
	public void testTargetType() {
		assertEquals("Decision", metric.getTargetElementType());
	}

	@Test
	public void testCoverageMap() {
		metric.getCoverageMap().put(0, List.of(new KnowledgeElement()));
		assertEquals(1, metric.getCoverageMap().size());
	}

	@Test
	public void testMinimumRequiredCoverage() {
		metric.setMinimumRequiredCoverage(1);
		assertEquals(1, metric.getMinimumRequiredCoverage());
	}
}