package de.uhd.ifi.se.decision.management.jira.webhook;

/**
 * Determines the format of the posted data via webhook.
 */
public enum WebhookType {
	SLACK("https://hooks.slack.com(\\S*)"), TREANT("");

	String url;

	WebhookType(String url) {
		this.url = url;
	}

	public static WebhookType getTypeFromUrl(String url) {
		if (url != null && url.matches(SLACK.url)) {
			return SLACK;
		}
		return TREANT;
	}
}
