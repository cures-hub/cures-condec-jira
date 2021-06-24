package de.uhd.ifi.se.decision.management.jira.quality;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.quality.checktriggers.PromptingEventConfiguration;

public class TestPromptingEventConfiguration {

	private PromptingEventConfiguration config;

	@Before
	public void setUp() {
		config = new PromptingEventConfiguration("TEST");
	}

	@Test
	public void testPromptEventForLinkSuggestion() {
		config.setPromptEvent("linkRecommendation", "done", true);
		assertTrue(config.isPromptEventActivated("linkRecommendation", "done"));

		config.setPromptEvent("linkRecommendation", "done", false);
		assertFalse(config.isPromptEventActivated("linkRecommendation", "done"));
	}

	@Test
	public void testPromptEventForDefinitionOfDoneChecking() {
		config.setPromptEvent("definitionOfDoneChecking", "done", true);
		assertTrue(config.isPromptEventActivated("definitionOfDoneChecking", "done"));

		config.setPromptEvent("definitionOfDoneChecking", "done", false);
		assertFalse(config.isPromptEventActivated("definitionOfDoneChecking", "done"));
	}

}