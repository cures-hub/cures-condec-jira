package de.uhd.ifi.se.decision.management.jira.webhook;

import java.util.HashSet;
import java.util.Set;

import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeProject;

/**
 * Contains the configuration details for the webhook for one Jira project (see
 * {@link DecisionKnowledgeProject}). The webhook posts changed decision
 * knowledge to a given URL (e.g. chat channel).
 */
public class WebhookConfiguration {

	private boolean isActivated;
	private String webhookUrl;
	private String webhookSecret;
	private Set<String> observedTypes;

	public WebhookConfiguration() {
		setActivated(false);
		setWebhookUrl("");
		setWebhookSecret("");
		setObservedTypes(new HashSet<>());
	}

	/**
	 * @return true if the webhook is enabled for this project.
	 */
	public boolean isActivated() {
		return isActivated;
	}

	/**
	 * @param isActivated
	 *            true if the webhook is enabled for this project.
	 */
	public void setActivated(boolean isActivated) {
		this.isActivated = isActivated;
	}

	/**
	 * @return webhook URL where the decision knowledge is sent to if the webhook is
	 *         enabled.
	 */
	public String getWebhookUrl() {
		return webhookUrl;
	}

	/**
	 * @param webhookUrl
	 *            where the decision knowledge is sent to if the webhook is enabled.
	 */
	public void setWebhookUrl(String webhookUrl) {
		this.webhookUrl = webhookUrl;
	}

	/**
	 * @return secret key for the submission of the decision knowledge via webhook.
	 */
	public String getWebhookSecret() {
		return webhookSecret;
	}

	/**
	 * @param webhookSecret
	 *            secret key for the submission of the decision knowledge via
	 *            webhook.
	 */
	public void setWebhookSecret(String webhookSecret) {
		this.webhookSecret = webhookSecret;
	}

	public Set<String> getObservedTypes() {
		return observedTypes;
	}

	public void setObservedTypes(Set<String> observedTypes) {
		this.observedTypes = observedTypes;
	}

	public boolean isWebhookTypeEnabled(String webhookType) {
		if (webhookType == null || webhookType.isBlank()) {
			return false;
		}
		return observedTypes.contains(webhookType);
	}

	public void setWebhookType(String webhookType, boolean isWebhookTypeEnabled) {
		if (webhookType == null || webhookType.isBlank()) {
			return;
		}
		if (isWebhookTypeEnabled) {
			observedTypes.add(webhookType);
		} else {
			observedTypes.remove(webhookType);
		}
	}
}