package de.uhd.ifi.se.decision.management.jira.recommendation.prompts;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class TestFeatureWithPrompt {

	@Test
	public void testValueOf() {
		assertEquals(FeatureWithPrompt.LINK_RECOMMENDATION, FeatureWithPrompt.valueOf("LINK_RECOMMENDATION"));
	}

}
