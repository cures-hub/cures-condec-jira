package de.uhd.ifi.se.decision.management.jira.webhook;

import static org.junit.jupiter.api.Assertions.assertTrue;

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
}
