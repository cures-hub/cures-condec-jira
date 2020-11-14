package de.uhd.ifi.se.decision.management.jira.webhook;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class TestWebhookType {

	@Test
	public void testSlackType() {
		WebhookType type = WebhookType.getTypeFromUrl("https://hooks.slack.com/services/T2E2");
		assertEquals(WebhookType.SLACK, type);
	}

	@Test
	public void testTreantType() {
		WebhookType type = WebhookType
				.getTypeFromUrl("https://cuu-staging.ase.in.tum.de/api/v1/projects/ConDecDev/integrations/conDec");
		assertEquals(WebhookType.TREANT, type);
	}
}
