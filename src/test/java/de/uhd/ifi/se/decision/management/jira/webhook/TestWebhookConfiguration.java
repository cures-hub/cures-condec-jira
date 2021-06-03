package de.uhd.ifi.se.decision.management.jira.webhook;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

public class TestWebhookConfiguration {

	private WebhookConfiguration config;

	@Before
	public void setUp() {
		config = new WebhookConfiguration();
	}

	@Test
	public void testWebhookActivation() {
		config.setActivated(true);
		assertTrue(config.isActivated());
	}

	@Test
	public void testWebhookUrl() {
		config.setWebhookUrl("https://");
		assertEquals("https://", config.getWebhookUrl());
	}

	@Test
	public void testWebhookSecret() {
		config.setWebhookSecret("42");
		assertEquals("42", config.getWebhookSecret());
	}

	@Test
	public void testObservedTypes() {
		config.setKnowledgeTypeObserved("Issue", true);
		assertEquals(1, config.getObservedKnowledgeTypes().size());
		assertTrue(config.isKnowledgeTypeObserved("Issue"));
		assertFalse(config.isKnowledgeTypeObserved("Epic"));

		config.setKnowledgeTypeObserved("Issue", false);
		assertEquals(0, config.getObservedKnowledgeTypes().size());
	}
}
