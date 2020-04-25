package de.uhd.ifi.se.decision.management.jira.model;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.fields.config.manager.IssueTypeSchemeManager;
import com.atlassian.jira.issue.issuetype.IssueType;
import com.atlassian.jira.project.Project;

import de.uhd.ifi.se.decision.management.jira.persistence.ConfigPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.persistence.singlelocations.JiraIssuePersistenceManager;

/**
 * Models a project and its configuration. The project is a Jira project that is
 * extended with settings for this plug-in, for example, whether the plug-in is
 * activated for the project.
 * 
 * This class provides read-only access to the settings. To change the settings,
 * use the {@link ConfigPersistenceManager}.
 * 
 * @issue Should the DecisionKnowledgeProject class extend the Jira project
 *        class?
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
	 * @param projectKey
	 *            of the Jira project. The project is a Jira project that is
	 *            extended with settings for this plug-in, for example, whether the
	 *            plug-in is activated for the project.
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
	 * @param projectName
	 *            of the Jira project. The project is a Jira project that is
	 *            extended with settings for this plug-in, for example, whether the
	 *            plug-in is activated for the project.
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
	 * @see JiraIssuePersistenceManager
	 * @return true if decision knowledge is stored in entire Jira issues for this
	 *         project. If this is true, you need make sure that the project is
	 *         associated with the decision knowledge issue type scheme.
	 */
	public boolean isIssueStrategy() {
		return ConfigPersistenceManager.isIssueStrategy(this.getProjectKey());
	}

	/**
	 * @return {@link KnowledgeType}s that are used in this project.
	 */
	public Set<KnowledgeType> getDecisionKnowledgeTypes() {
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
	 * @return uniform resource identifiers of the git repositories for this project
	 *         as a List<String> (if it is set, otherwise an empty List).
	 */
	public List<String> getGitUris() {
		return ConfigPersistenceManager.getGitUris(projectKey);
	}

	/**
	 * @return default branches as Map<String,String> with the uniform resource
	 *         identifiers of the git repositories for this project as key and the
	 *         name of default branch as value.
	 */
	public Map<String, String> getDefaultBranches() {
		return ConfigPersistenceManager.getDefaultBranches(projectKey);
	}

	/**
	 * @return true if the webhook is enabled for this project.
	 */
	public boolean isWebhookEnabled() {
		return ConfigPersistenceManager.isWebhookEnabled(projectKey);
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
	 * @return true, if the classifier is used for Jira issue comments.
	 */
	public boolean isClassifierEnabled() {
		return ConfigPersistenceManager.isUseClassiferForIssueComments(projectKey);
	}

	private Project getJiraProject() {
		return ComponentAccessor.getProjectManager().getProjectByCurrentKey(projectKey);
	}

	/**
	 * @return names of Jira issue types available in the project.
	 */
	public Set<String> getJiraIssueTypeNames() {
		Project project = getJiraProject();
		IssueTypeSchemeManager issueTypeSchemeManager = ComponentAccessor.getIssueTypeSchemeManager();
		Collection<IssueType> types = issueTypeSchemeManager.getIssueTypesForProject(project);
		Set<String> issueTypes = new HashSet<String>();
		for (IssueType type : types) {
			issueTypes.add(type.getName());
		}
		return issueTypes;
	}
}