package de.uhd.ifi.se.decision.management.jira.recommendation.prompts;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class TestFeatureWithPrompt {

	@Test
	public void testGetFeatureByName() {
		assertEquals(FeatureWithPrompt.LINK_RECOMMENDATION, FeatureWithPrompt.getFeatureByName("LINK_RECOMMENDATION"));
		assertEquals(FeatureWithPrompt.LINK_RECOMMENDATION, FeatureWithPrompt.getFeatureByName("link_recommendation"));
		assertEquals(null, FeatureWithPrompt.getFeatureByName(null));
		assertEquals(null, FeatureWithPrompt.getFeatureByName(""));
		assertEquals(null, FeatureWithPrompt.getFeatureByName("CHANGE_IMPACT_ANALYSIS"));
	}
}