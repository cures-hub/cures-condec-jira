package de.uhd.ifi.se.decision.management.jira.model.impl;

import java.util.HashSet;
import java.util.Set;

import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeProject;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.persistence.ConfigPersistenceManager;

/**
 * Model class for a project and its configuration
 */
public class DecisionKnowledgeProjectImpl implements DecisionKnowledgeProject {

	private String projectKey;
	private String projectName;

	public DecisionKnowledgeProjectImpl(String projectKey) {
		this.projectKey = projectKey;
	}

	public DecisionKnowledgeProjectImpl(String projectKey, String projectName) {
		this(projectKey);
		this.projectName = projectName;
	}

	@Override
	public String getProjectKey() {
		return projectKey;
	}

	@Override
	public void setProjectKey(String projectKey) {
		this.projectKey = projectKey;
	}

	@Override
	public String getProjectName() {
		return projectName;
	}

	@Override
	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	@Override
	public boolean isActivated() {
		return ConfigPersistenceManager.isActivated(this.getProjectKey());
	}

	@Override
	public void setActivated(boolean isActivated) {
		ConfigPersistenceManager.setActivated(this.getProjectKey(), isActivated);
	}

	@Override
	public boolean isIssueStrategy() {
		return ConfigPersistenceManager.isIssueStrategy(this.getProjectKey());
	}

	@Override
	public void setIssueStrategy(boolean isIssueStrategy) {
		ConfigPersistenceManager.setIssueStrategy(this.getProjectKey(), isIssueStrategy);
	}

	@Override
	public Set<KnowledgeType> getKnowledgeTypes() {
		Set<KnowledgeType> knowledgeTypes = new HashSet<KnowledgeType>();
		for (KnowledgeType knowledgeType : KnowledgeType.values()) {
			boolean isEnabled = ConfigPersistenceManager.isKnowledgeTypeEnabled(this.projectKey, knowledgeType);
			if (isEnabled) {
				knowledgeTypes.add(knowledgeType);
			}
		}
		return knowledgeTypes;
	}

	@Override
	public boolean isKnowledgeExtractedFromGit() {
		return ConfigPersistenceManager.isKnowledgeExtractedFromGit(projectKey);
	}

	@Override
	public boolean isPostSquashedCommitsActivated() {
		return ConfigPersistenceManager.isPostSquashedCommitsActivated(projectKey);
	}

	@Override
	public boolean isPostFeatureBranchCommitsActivated() {
		return ConfigPersistenceManager.isPostFeatureBranchCommitsActivated(projectKey);
	}

	@Override
	public void setKnowledgeExtractedFromGit(boolean isKnowledgeExtractedFromGit) {
		ConfigPersistenceManager.setKnowledgeExtractedFromGit(projectKey, isKnowledgeExtractedFromGit);
	}

	@Override
	public String getGitUri() {
		return ConfigPersistenceManager.getGitUri(projectKey);
	}

	@Override
	public void setWebhookEnabled(boolean isWebhookEnabled) {
		ConfigPersistenceManager.setWebhookEnabled(projectKey, isWebhookEnabled);
	}

	@Override
	public boolean isWebhookEnabled() {
		return ConfigPersistenceManager.isWebhookEnabled(projectKey);
	}

	@Override
	public void setWebhookData(String webhookUrl, String webhookSecret) {
		if (webhookUrl == null || webhookSecret == null) {
			return;
		}
		ConfigPersistenceManager.setWebhookUrl(projectKey, webhookUrl);
		ConfigPersistenceManager.setWebhookSecret(projectKey, webhookSecret);
	}

	@Override
	public String getWebhookUrl() {
		return ConfigPersistenceManager.getWebhookUrl(projectKey);
	}

	@Override
	public String getWebhookSecret() {
		return ConfigPersistenceManager.getWebhookSecret(projectKey);
	}

	@Override
	public boolean isWebhookTypeEnabled(String issueType) {
		return ConfigPersistenceManager.isWebhookTypeEnabled(projectKey, issueType);
	}

	@Override
	public boolean isIconParsingEnabled() {
		return ConfigPersistenceManager.isIconParsing(projectKey);
	}

	@Override
	public boolean isClassifierUsedForIssueComments() {
		return ConfigPersistenceManager.isUseClassiferForIssueComments(projectKey);
	}
}