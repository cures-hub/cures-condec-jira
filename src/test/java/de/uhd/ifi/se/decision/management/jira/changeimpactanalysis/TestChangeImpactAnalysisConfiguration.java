package de.uhd.ifi.se.decision.management.jira.changeimpactanalysis;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;

public class TestChangeImpactAnalysisConfiguration extends TestSetUp {

	private ChangeImpactAnalysisConfiguration config;

	@Before
	public void setUp() {
		init();
		config = new ChangeImpactAnalysisConfiguration();
	}

	@Test
	public void testContext() {
		// default value
		assertEquals(0, config.getContext());
		config.setContext(1);
		assertEquals(1, config.getContext());
	}

	@Test
	public void testLinkImpact() {
		// default value
		assertTrue(config.getLinkImpact().size() > 5);
		config.setLinkImpact(Map.of("test", 1.0f));
		assertEquals(1, config.getLinkImpact().size());
	}

	@Test
	public void testDecayValue() {
		// default value
		assertEquals(0.25, config.getDecayValue(), 0.01);
		config.setDecayValue(0.8f);
		assertEquals(0.8, config.getDecayValue(), 0.01);
	}

	@Test
	public void testThresholdValue() {
		// default value
		assertEquals(0.25, config.getThreshold(), 0.01);
		config.setThreshold(0.2f);
		assertEquals(0.2, config.getThreshold(), 0.01);
	}

	@Test
	public void testPropagationRules() {
		// default value
		assertEquals(7, config.getPropagationRules().size());
		config.setPropagationRules(List.of(ChangePropagationRule.STOP_AT_SAME_ELEMENT_TYPE.getDescription()));
		assertEquals(1, config.getPropagationRules().size());
		config.setPropagationRules(null);
		assertEquals(0, config.getPropagationRules().size());
	}

	@Test
	public void testPropagationRulesAsStrings() {
		assertEquals(7, config.getPropagationRulesAsStrings().size());
		assertEquals("Stop at elements with the same type as the selected element",
				config.getPropagationRulesAsStrings().get(0));
	}
}