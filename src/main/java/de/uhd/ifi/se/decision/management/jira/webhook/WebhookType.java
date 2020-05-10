package de.uhd.ifi.se.decision.management.jira.webhook;

/**
 * Determines the format of the posted data via webhook. Is used in the
 * {@link AbstractWebookContentProvider}.
 */
public enum WebhookType {
	SLACK("https://hooks.slack.com(\\S*)"), // used for knowledge sharing in a Slack channel
	TREANT(""); // used for integration with CuuSE platform

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
