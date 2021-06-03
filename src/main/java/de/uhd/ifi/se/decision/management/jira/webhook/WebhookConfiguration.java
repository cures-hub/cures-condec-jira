package de.uhd.ifi.se.decision.management.jira.webhook;

import java.util.HashSet;
import java.util.Set;

import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeProject;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;

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
		setObservedKnowledgeTypes(new HashSet<>());
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

	/**
	 * @return observed {@link KnowledgeType}s. If a {@link KnowledgeElement} of an
	 *         observed type changes, the webhook is fired. Also, if a {@link Link}
	 *         to or from a {@link KnowledgeElement} of an observed type is created
	 *         or deleted, the webhook is fired.
	 */
	public Set<String> getObservedKnowledgeTypes() {
		return observedTypes;
	}

	/**
	 * @param observedTypes
	 *            observed {@link KnowledgeType}s. If a {@link KnowledgeElement} of
	 *            an observed type changes, the webhook is fired. Also, if a
	 *            {@link Link} to or from a {@link KnowledgeElement} of an observed
	 *            type is created or deleted, the webhook is fired.
	 */
	public void setObservedKnowledgeTypes(Set<String> observedTypes) {
		this.observedTypes = observedTypes;
	}

	/**
	 * @param observedType
	 *            {@link KnowledgeType} as a String, e.g. "Issue".
	 * @return true if the {@link KnowledgeType} is observed, see
	 *         {@link #getObservedKnowledgeTypes()}.
	 */
	public boolean isKnowledgeTypeObserved(String observedType) {
		return observedTypes.contains(observedType);
	}

	/**
	 * @param knowledgeType
	 *            {@link KnowledgeType} as a String, e.g. "Issue".
	 * @param isTypeObserved
	 *            true if the {@link KnowledgeType} should be observed, see
	 *            {@link #getObservedKnowledgeTypes()}.
	 */
	public void setKnowledgeTypeObserved(String knowledgeType, boolean isTypeObserved) {
		if (isTypeObserved) {
			observedTypes.add(knowledgeType);
		} else {
			observedTypes.remove(knowledgeType);
		}
	}
}