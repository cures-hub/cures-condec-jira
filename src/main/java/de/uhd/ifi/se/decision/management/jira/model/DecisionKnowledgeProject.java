package de.uhd.ifi.se.decision.management.jira.model;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.uhd.ifi.se.decision.management.jira.persistence.ConfigPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.persistence.impl.AbstractPersistenceManagerForSingleLocation;
import de.uhd.ifi.se.decision.management.jira.persistence.impl.JiraIssuePersistenceManager;

/**
 * Models a project and its configuration. The project is a JIRA project that is
 * extended with settings for this plug-in, for example, whether the plug-in is
 * activated for the project.
 */
public class DecisionKnowledgeProject {

	private String projectKey;
	private String projectName;

	public DecisionKnowledgeProject(String projectKey) {
		this.projectKey = projectKey;
	}

	public DecisionKnowledgeProject(String projectKey, String projectName) {
		this(projectKey);
		this.projectName = projectName;
	}

	/**
	 * @return key of the Jira project. The project is a Jira project that is
	 *         extended with settings for this plug-in, for example, whether the
	 *         plug-in is activated for the project.
	 */
	public String getProjectKey() {
		return projectKey;
	}

	/**
	 * Sets the key of the project. The project is a Jira project that is extended
	 * with settings for this plug-in, for example, whether the plug-in is activated
	 * for the project.
	 *
	 * @param projectKey
	 *            of the Jira project.
	 */
	public void setProjectKey(String projectKey) {
		this.projectKey = projectKey;
	}

	/**
	 * @return name of the project. The project is a Jira project that is extended
	 *         with settings for this plug-in, for example, whether the plug-in is
	 *         activated for the project.
	 */
	public String getProjectName() {
		return projectName;
	}

	/**
	 * Sets the name of the project. The project is a Jira project that is extended
	 * with settings for this plug-in, for example, whether the plug-in is activated
	 * for the project.
	 *
	 * @param projectName
	 *            of the Jira project.
	 */
	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	/**
	 * @return true if the plug-in is activated for this project.
	 */
	public boolean isActivated() {
		return ConfigPersistenceManager.isActivated(this.getProjectKey());
	}

	/**
	 * Sets whether the plug-in is activated for this project.
	 *
	 * @param isActivated
	 *            true if the plug-in should be activated for this project.
	 */
	public void setActivated(boolean isActivated) {
		ConfigPersistenceManager.setActivated(this.getProjectKey(), isActivated);
	}

	/**
	 * Determines whether decision knowledge is stored in Jira issues for this
	 * project. If you choose the issue strategy, you need to associate the project
	 * with the decision knowledge issue type scheme.
	 *
	 * @see AbstractPersistenceManagerForSingleLocation
	 * @see JiraIssuePersistenceManager
	 * @return true if decision knowledge is stored in Jira issues for this project
	 *         (issue strategy). Otherwise object relational mapping is used (active
	 *         object strategy).
	 */
	public boolean isIssueStrategy() {
		return ConfigPersistenceManager.isIssueStrategy(this.getProjectKey());
	}

	/**
	 * Sets whether decision knowledge is stored in Jira issues for this project. If
	 * you choose the issue strategy, you need to associate the project with the
	 * decision knowledge issue type scheme.
	 *
	 * @see AbstractPersistenceManagerForSingleLocation
	 * @see JiraIssuePersistenceManager
	 * @param isIssueStrategy
	 *            true if decision knowledge should be stored in Jira issues for
	 *            this project (issue strategy). Otherwise object relational mapping
	 *            is used (active object strategy).
	 */
	public void setIssueStrategy(boolean isIssueStrategy) {
		ConfigPersistenceManager.setIssueStrategy(this.getProjectKey(), isIssueStrategy);
	}

	/**
	 * @return {@link KnowledgeType} of decision knowledge that are used in this
	 *         project.
	 */
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

	/**
	 * @return true if decision knowledge is extracted from git commit messages.
	 */
	public boolean isKnowledgeExtractedFromGit() {
		return ConfigPersistenceManager.isKnowledgeExtractedFromGit(projectKey);
	}

	/**
	 * @return true if git commit messages of squashed commits should be posted as
	 *         Jira issue comments.
	 */
	public boolean isPostSquashedCommitsActivated() {
		return ConfigPersistenceManager.isPostSquashedCommitsActivated(projectKey);
	}

	/**
	 * @return true if git commit messages of feature branch commits should be
	 *         posted as Jira issue comments.
	 */
	public boolean isPostFeatureBranchCommitsActivated() {
		return ConfigPersistenceManager.isPostFeatureBranchCommitsActivated(projectKey);
	}

	/**
	 * Sets whether decision knowledge is extracted from git commit messages.
	 *
	 * @param isKnowledgeExtractedFromGit
	 *            true if decision knowledge should be extracted from git commit
	 *            messages.
	 */
	public void setKnowledgeExtractedFromGit(boolean isKnowledgeExtractedFromGit) {
		ConfigPersistenceManager.setKnowledgeExtractedFromGit(projectKey, isKnowledgeExtractedFromGit);
	}

	/**
	 * @return uniform resource identifiers of the git repositories for this project
	 *         as a List<String> (if it is set, otherwise an empty List).
	 */
	public List<String> getGitUris() {
		return ConfigPersistenceManager.getGitUris(projectKey);
	}

	/**
	 * Returns the a map with uniform resource identifiers of the git repositories
	 * for this project as keys and name of default branch as Value.
	 *
	 * @return default branches as Map<String,String>.
	 */
	public Map<String, String> getDefaultBranches() {
		return ConfigPersistenceManager.getDefaultBranches(projectKey);
	}

	/**
	 * Sets whether the webhook is enabled for this project.
	 *
	 * @param isWebhookEnabled
	 *            true if the webhook is enabled for this project.
	 */
	public void setWebhookEnabled(boolean isWebhookEnabled) {
		ConfigPersistenceManager.setWebhookEnabled(projectKey, isWebhookEnabled);
	}

	/**
	 * @return true if the webhook is enabled for this project.
	 */
	public boolean isWebhookEnabled() {
		return ConfigPersistenceManager.isWebhookEnabled(projectKey);
	}

	/**
	 * Sets the URL where the decision knowledge should be sent and the secret key
	 * for the submission.
	 *
	 * @param webhookUrl
	 *            URL of the webhook
	 * @param webhookSecret
	 *            secret key
	 */
	public void setWebhookData(String webhookUrl, String webhookSecret) {
		if (webhookUrl == null || webhookSecret == null) {
			return;
		}
		ConfigPersistenceManager.setWebhookUrl(projectKey, webhookUrl);
		ConfigPersistenceManager.setWebhookSecret(projectKey, webhookSecret);
	}

	/**
	 * @return webhook URL where the decision knowledge is sent to if the webhook is
	 *         enabled.
	 */
	public String getWebhookUrl() {
		return ConfigPersistenceManager.getWebhookUrl(projectKey);
	}

	/**
	 * @return secret key for the submission of the decision knowledge via webhook.
	 */
	public String getWebhookSecret() {
		return ConfigPersistenceManager.getWebhookSecret(projectKey);
	}

	/**
	 * @return type of webhook root element.
	 */
	public boolean isWebhookTypeEnabled(String issueType) {
		return ConfigPersistenceManager.isWebhookTypeEnabled(projectKey, issueType);
	}

	/**
	 * @return true, if icon parsing in Jira issue comments is enabled.
	 */
	public boolean isIconParsingEnabled() {
		return ConfigPersistenceManager.isIconParsing(projectKey);
	}

	/**
	 * @return true, if is classifier used for Jira issue comments.
	 */
	public boolean isClassifierUsedForIssueComments() {
		return ConfigPersistenceManager.isUseClassiferForIssueComments(projectKey);
	}

}