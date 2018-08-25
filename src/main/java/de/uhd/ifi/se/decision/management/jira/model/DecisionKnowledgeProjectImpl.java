package de.uhd.ifi.se.decision.management.jira.model;

import java.util.HashSet;
import java.util.Set;

import de.uhd.ifi.se.decision.management.jira.persistence.AbstractPersistenceStrategy;
import de.uhd.ifi.se.decision.management.jira.persistence.ConfigPersistence;
import de.uhd.ifi.se.decision.management.jira.webhook.WebConnector;

/**
 * Model class for a project and its configuration
 */
public class DecisionKnowledgeProjectImpl implements DecisionKnowledgeProject {

	private String projectKey;
	private String projectName;
	private AbstractPersistenceStrategy persistenceStrategy;

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
		return ConfigPersistence.isActivated(this.getProjectKey());
	}

	@Override
	public void setActivated(boolean isActivated) {
		ConfigPersistence.setActivated(this.getProjectKey(), isActivated);
	}

	@Override
	public boolean isIssueStrategy() {
		return ConfigPersistence.isIssueStrategy(this.getProjectKey());
	}

	@Override
	public void setIssueStrategy(boolean isIssueStrategy) {
		ConfigPersistence.setIssueStrategy(this.getProjectKey(), isIssueStrategy);
	}

	@Override
	public AbstractPersistenceStrategy getPersistenceStrategy() {
		if (this.persistenceStrategy == null) {
			this.persistenceStrategy = DecisionKnowledgeProject.getPersistenceStrategy(this.projectKey);
		}
		return this.persistenceStrategy;
	}

	@Override
	public Set<KnowledgeType> getKnowledgeTypes() {
		Set<KnowledgeType> knowledgeTypes = new HashSet<KnowledgeType>();
		for (KnowledgeType knowledgeType : KnowledgeType.values()) {
			boolean isEnabled = ConfigPersistence.isKnowledgeTypeEnabled(this.projectKey, knowledgeType);
			if (isEnabled) {
				knowledgeTypes.add(knowledgeType);
			}
		}
		return knowledgeTypes;
	}

	@Override
	public boolean isKnowledgeExtractedFromGit() {
		return ConfigPersistence.isKnowledgeExtractedFromGit(projectKey);
	}

	@Override
	public void setKnowledgeExtractedFromGit(boolean isKnowledgeExtractedFromGit) {
		ConfigPersistence.setKnowledgeExtractedFromGit(projectKey, isKnowledgeExtractedFromGit);
	}

	@Override
	public boolean isKnowledgeExtractedFromIssues() {
		return ConfigPersistence.isKnowledgeExtractedFromIssues(projectKey);
	}

	@Override
	public void setKnowledgeExtractedFromIssues(boolean isKnowledgeExtractedFromIssues) {
		ConfigPersistence.setKnowledgeExtractedFromIssues(projectKey, isKnowledgeExtractedFromIssues);
	}

	@Override
	public String getGitAddress() {
		return ConfigPersistence.getGitAddress(projectKey);
	}

	@Override
	public void setWebhookData(String webhookUrl, String webhookSecret){
		if(webhookUrl == null || webhookSecret == null){
			return;
		}
		ConfigPersistence.setWebhookUrl(projectKey, webhookUrl);
		ConfigPersistence.setWebhookSecret(projectKey, webhookSecret);
	}

	@Override
	public String getWebhookUrl(){
		return ConfigPersistence.getWebhookUrl(projectKey);
	}

	@Override
	public  String getWebhookSecret(){
		return  ConfigPersistence.getWebhookSecret(projectKey);
	}
}