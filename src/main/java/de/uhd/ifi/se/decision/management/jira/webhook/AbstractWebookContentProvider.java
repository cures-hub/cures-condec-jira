package de.uhd.ifi.se.decision.management.jira.webhook;

import org.apache.commons.httpclient.methods.PostMethod;

/**
 * Creates the content submitted via the webhook. The content depends on the
 * {@link WebhookType}.
 */
public abstract class AbstractWebookContentProvider {

	protected String projectKey;
	protected WebhookType type;

	public abstract PostMethod createPostMethod();

	public abstract PostMethod createTestPostMethod();
}