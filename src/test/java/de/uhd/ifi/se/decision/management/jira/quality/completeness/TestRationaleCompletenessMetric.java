package de.uhd.ifi.se.decision.management.jira.quality.completeness;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;

public class TestRationaleCompletenessMetric {

	private RationaleCompletenessMetric metric;

	@Before
	public void setUp() {
		metric = new RationaleCompletenessMetric(KnowledgeType.DECISION, KnowledgeType.ARGUMENT);
	}

	@Test
	public void testSourceType() {
		assertEquals("Decision", metric.getSourceElementType());
	}

	@Test
	public void testTargetType() {
		assertEquals("Argument", metric.getTargetElementType());
	}

	@Test
	public void testCompleteElements() {
		metric.addCompleteElement(new KnowledgeElement());
		assertEquals(1, metric.getCompleteElements().size());
	}

	@Test
	public void testIncompleteElements() {
		metric.addIncompleteElement(new KnowledgeElement());
		assertEquals(1, metric.getIncompleteElements().size());
	}
}