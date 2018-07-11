package de.uhd.ifi.se.decision.management.jira.model;

import java.util.HashSet;
import java.util.Set;

import de.uhd.ifi.se.decision.management.jira.persistence.AbstractPersistenceStrategy;
import de.uhd.ifi.se.decision.management.jira.persistence.ConfigPersistence;
import de.uhd.ifi.se.decision.management.jira.persistence.StrategyProvider;

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

	public String getProjectKey() {
		return projectKey;
	}

	public void setProjectKey(String projectKey) {
		this.projectKey = projectKey;
	}

	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	public boolean isIssueStrategy() {
		return ConfigPersistence.isIssueStrategy(this.getProjectKey());
	}

	public void setIssueStrategy(boolean isIssueStrategy) {
		ConfigPersistence.setIssueStrategy(this.getProjectKey(), isIssueStrategy);
	}

	public boolean isActivated() {
		return ConfigPersistence.isActivated(this.getProjectKey());
	}

	public void setActivated(boolean isActivated) {
		ConfigPersistence.setActivated(this.getProjectKey(), isActivated);
	}

	public boolean isKnowledgeExtractedFromGit() {
		return ConfigPersistence.isKnowledgeExtractedFromGit(projectKey);
	}

	public void setKnowledgeExtractedFromGit(boolean isKnowledgeExtractedFromGit) {
		ConfigPersistence.setKnowledgeExtractedFromGit(projectKey, isKnowledgeExtractedFromGit);
	}

	public boolean isKnowledgeExtractedFromIssues() {
		return ConfigPersistence.isKnowledgeExtractedFromIssues(projectKey);
	}

	public void setKnowledgeExtractedFromIssues(boolean isKnowledgeExtractedFromIssues) {
		ConfigPersistence.setKnowledgeExtractedFromIssues(projectKey, isKnowledgeExtractedFromIssues);
	}

	public AbstractPersistenceStrategy getPersistenceStrategy() {
		StrategyProvider strategyProvider = new StrategyProvider();
		return strategyProvider.getStrategy(this.projectKey);
	}

	public Set<KnowledgeType> getKnowledgeTypes() {
		Set<KnowledgeType> knowledgeTypes = new HashSet<KnowledgeType>();
		for(KnowledgeType knowledgeType : KnowledgeType.values()) {
			boolean isEnabled = ConfigPersistence.isKnowledgeTypeEnabled(this.projectKey, knowledgeType);
			if(isEnabled) {
				knowledgeTypes.add(knowledgeType);
			}
		}
		return knowledgeTypes;
	}
}