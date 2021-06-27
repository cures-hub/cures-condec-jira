package de.uhd.ifi.se.decision.management.jira.recommendation.prompts;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;

public class TestPromptingEventConfiguration extends TestSetUp {

	private PromptingEventConfiguration config;

	@Before
	public void setUp() {
		init();
		config = new PromptingEventConfiguration("TEST");
	}

	@Test
	public void testIsPromptEventActivatedTrue() {
		config.setPromptEvent(FeatureWithPrompt.LINK_RECOMMENDATION, "done", true);
		assertTrue(config.isPromptEventActivated(FeatureWithPrompt.LINK_RECOMMENDATION, "done"));
	}

	@Test
	public void testIsPromptEventActivatedFalse() {
		config.setPromptEvent(FeatureWithPrompt.LINK_RECOMMENDATION, "done", false);
		assertFalse(config.isPromptEventActivated(FeatureWithPrompt.LINK_RECOMMENDATION, "done"));
	}

	@Test
	public void testIsPromptEventActivatedInvalidFeature() {
		assertFalse(config.isPromptEventActivated("", "done"));
	}

	@Test
	public void testIsValidFeatureFeatureNull() {
		assertFalse(config.isValidFeature(null));
	}

	@Test
	public void testIsValidFeatureFeatureNotInMap() {
		config.getPromptingEventsForFeature().remove(FeatureWithPrompt.LINK_RECOMMENDATION);
		assertTrue(config.isValidFeature(FeatureWithPrompt.LINK_RECOMMENDATION));
	}

	@Test
	public void testSetPromptEventForInvalidFeature() {
		config.setPromptEvent(null, "done", true);
		assertFalse(config.isPromptEventActivated((FeatureWithPrompt) null, "done"));
	}

	@Test
	public void testIsValidFeatureMapNull() {
		config.setPromptingEventsForFeature(null);
		assertTrue(config.isValidFeature(FeatureWithPrompt.LINK_RECOMMENDATION));
	}
}