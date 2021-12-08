package de.uhd.ifi.se.decision.management.jira.view.matrix;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.testdata.KnowledgeElements;

public class TestElementWithHighlighting extends TestSetUp {

	private ElementWithHighlighting elementWithHighlighting;

	@Before
	public void setUp() {
		init();
		elementWithHighlighting = new ElementWithHighlighting(KnowledgeElements.getTestKnowledgeElement());
	}

	@Test
	public void testQualityHighlighting() {
		elementWithHighlighting.setQualityColor("crimson");
		elementWithHighlighting.setQualityProblemExplanation("Decision coverage is too low.");
		assertEquals("crimson", elementWithHighlighting.getQualityColor());
		assertEquals("Decision coverage is too low.", elementWithHighlighting.getQualityProblemExplanation());
	}

	@Test
	public void testChangeImpactHighlighting() {
		elementWithHighlighting.setChangeImpactColor("green");
		assertEquals("green", elementWithHighlighting.getChangeImpactColor());
	}
}