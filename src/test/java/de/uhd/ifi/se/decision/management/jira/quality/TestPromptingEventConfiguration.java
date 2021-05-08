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
		config = new PromptingEventConfiguration();
	}

	@Test
	public void testPromptEventForLinkSuggestion() {
		config.setPromptEventForLinkSuggestion("done", true);
		assertTrue(config.isPromptEventForLinkSuggestionActivated("done"));

		config.setPromptEventForLinkSuggestion("done", false);
		assertFalse(config.isPromptEventForLinkSuggestionActivated("done"));
	}

	@Test
	public void testPromptEventForDefinitionOfDoneChecking() {
		config.setPromptEventForDefinitionOfDoneChecking("done", true);
		assertTrue(config.isPromptEventForDefinitionOfDoneCheckingActivated("done"));

		config.setPromptEventForDefinitionOfDoneChecking("done", false);
		assertFalse(config.isPromptEventForDefinitionOfDoneCheckingActivated("done"));
	}

}