package de.uhd.ifi.se.decision.management.jira.model;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.fields.config.manager.IssueTypeSchemeManager;
import com.atlassian.jira.issue.issuetype.IssueType;
import com.atlassian.jira.issue.link.IssueLinkType;
import com.atlassian.jira.issue.link.IssueLinkTypeManager;
import com.atlassian.jira.project.Project;

import de.uhd.ifi.se.decision.management.jira.persistence.ConfigPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.persistence.singlelocations.JiraIssuePersistenceManager;

/**
 * Models a Jira project and its configuration. The Jira project is extended
 * with settings for this plug-in, for example, whether the plug-in is activated
 * for the project.
 * 
 * This class provides read-only access to the settings. To change the settings,
 * use the {@link ConfigPersistenceManager}.
 * 
 * @issue Should the DecisionKnowledgeProject class extend the Jira project
 *        class?
 * @decision The DecisionKnowledgeProject does not extend the Jira project class
 *           (ProjectImpl) but holds the Jira project as an attribute instead!
 * @alternative The DecisionKnowledgeProject could extend/inherit from the Jira
 *              project class (ProjectImpl)!
 * @con The constructor of the Jira project class is hard to use since it gets a
 *      generic value as a parameter.
 */
public class DecisionKnowledgeProject {

	private Project jiraProject;

	public DecisionKnowledgeProject(Project jiraProject) {
		this.jiraProject = jiraProject;
	}

	public DecisionKnowledgeProject(String projectKey) {
		if (projectKey != null && !projectKey.isBlank()) {
			this.jiraProject = ComponentAccessor.getProjectManager().getProjectByCurrentKey(projectKey);
		}
	}

	/**
	 * @return underlying Jira project.
	 */
	public Project getJiraProject() {
		return jiraProject;
	}

	/**
	 * @return key of the Jira project.
	 */
	public String getProjectKey() {
		if (jiraProject == null) {
			return "";
		}
		return jiraProject.getKey();
	}

	/**
	 * @return name of the Jira project.
	 */
	public String getProjectName() {
		if (jiraProject == null) {
			return "";
		}
		return jiraProject.getName();
	}

	/**
	 * @return true if the ConDec plug-in is activated for the Jira project.
	 */
	public boolean isActivated() {
		return ConfigPersistenceManager.isActivated(this.getProjectKey());
	}

	/**
	 * @see JiraIssuePersistenceManager
	 * @return true if decision knowledge is stored in entire Jira issues in this
	 *         Jira project. If this is true, you need make sure that the project is
	 *         associated with the decision knowledge issue type scheme.
	 */
	public boolean isIssueStrategy() {
		return ConfigPersistenceManager.isIssueStrategy(this.getProjectKey());
	}

	/**
	 * @return {@link KnowledgeType}s that are used in this project.
	 */
	public Set<KnowledgeType> getConDecKnowledgeTypes() {
		Set<KnowledgeType> enabledKnowledgeTypes = new LinkedHashSet<KnowledgeType>();
		for (KnowledgeType knowledgeType : KnowledgeType.values()) {
			boolean isEnabled = ConfigPersistenceManager.isKnowledgeTypeEnabled(getProjectKey(), knowledgeType);
			if (isEnabled) {
				enabledKnowledgeTypes.add(knowledgeType);
			}
		}
		return enabledKnowledgeTypes;
	}

	/**
	 * @return names of {@link KnowledgeType}s that are used in this project as
	 *         Strings.
	 */
	public Set<String> getNamesOfConDecKnowledgeTypes() {
		Set<KnowledgeType> knowledgeTypes = getConDecKnowledgeTypes();
		Set<String> knowledgeTypesAsString = knowledgeTypes.stream().map(KnowledgeType::toString)
				.collect(Collectors.toSet());
		return knowledgeTypesAsString;
	}

	/**
	 * @return names of decision knowledge types and Jira issue types that are used
	 *         in this project as Strings.
	 */
	public Set<String> getNamesOfKnowledgeTypes() {
		Set<String> jiraIssueTypes = getJiraIssueTypeNames();
		Set<String> knowledgeTypes = getNamesOfConDecKnowledgeTypes();
		knowledgeTypes.addAll(jiraIssueTypes);
		return knowledgeTypes;
	}

	/**
	 * @return true if decision knowledge is extracted from git commit messages.
	 */
	public boolean isKnowledgeExtractedFromGit() {
		return ConfigPersistenceManager.isKnowledgeExtractedFromGit(getProjectKey());
	}

	/**
	 * @return true if git commit messages of squashed commits should be posted as
	 *         Jira issue comments.
	 */
	public boolean isPostSquashedCommitsActivated() {
		return ConfigPersistenceManager.isPostSquashedCommitsActivated(getProjectKey());
	}

	/**
	 * @return true if git commit messages of feature branch commits should be
	 *         posted as Jira issue comments.
	 */
	public boolean isPostFeatureBranchCommitsActivated() {
		return ConfigPersistenceManager.isPostFeatureBranchCommitsActivated(getProjectKey());
	}

	/**
	 * @return uniform resource identifiers of the git repositories for this project
	 *         as a List<String> (if it is set, otherwise an empty List).
	 */
	public List<String> getGitUris() {
		return ConfigPersistenceManager.getGitUris(getProjectKey());
	}

	/**
	 * @return default branches as Map<String,String> with the uniform resource
	 *         identifiers of the git repositories for this project as key and the
	 *         name of default branch as value.
	 */
	public Map<String, String> getDefaultBranches() {
		return ConfigPersistenceManager.getDefaultBranches(getProjectKey());
	}

	/**
	 * @return authentification methods as Map<String,String> with the uniform resource
	 *         identifiers of the git repositories for this project as key and the
	 *         identifier of the authentification method as value.
	 */
	public Map<String, String> getAuthMethods() {
		return ConfigPersistenceManager.getAuthMethods(getProjectKey());
	}

	/**
	 * @return user names as Map<String,String> with the uniform resource
	 *         identifiers of the git repositories for this project as key and the
	 *         user names as value.
	 */
	public Map<String, String> getUsernames() {
		return ConfigPersistenceManager.getUsernames(getProjectKey());
	}

	/**
	 * @return tokens or passwords as Map<String,String> with the uniform resource
	 *         identifiers of the git repositories for this project as key and the
	 *         tokens or passwords as value.
	 */
	public Map<String, String> getTokens() {
		return ConfigPersistenceManager.getTokens(getProjectKey());
	}

	/**
	 * @return true if the webhook is enabled for this project.
	 */
	public boolean isWebhookEnabled() {
		return ConfigPersistenceManager.isWebhookEnabled(getProjectKey());
	}

	/**
	 * @return webhook URL where the decision knowledge is sent to if the webhook is
	 *         enabled.
	 */
	public String getWebhookUrl() {
		return ConfigPersistenceManager.getWebhookUrl(getProjectKey());
	}

	/**
	 * @return secret key for the submission of the decision knowledge via webhook.
	 */
	public String getWebhookSecret() {
		return ConfigPersistenceManager.getWebhookSecret(getProjectKey());
	}

	/**
	 * @return true, if icon parsing in Jira issue comments is enabled.
	 */
	public boolean isIconParsingEnabled() {
		return ConfigPersistenceManager.isIconParsing(getProjectKey());
	}

	/**
	 * @return true, if the classifier is used for Jira issue comments.
	 */
	public boolean isClassifierEnabled() {
		return ConfigPersistenceManager.isClassifierEnabled(getProjectKey());
	}

	/**
	 * @return Jira issue types available in the project.
	 */
	public Set<IssueType> getJiraIssueTypes() {
		IssueTypeSchemeManager issueTypeSchemeManager = ComponentAccessor.getIssueTypeSchemeManager();
		Collection<IssueType> types = issueTypeSchemeManager.getIssueTypesForProject(jiraProject);
		return new HashSet<>(types);
	}

	/**
	 * @return names of Jira issue types available in the project.
	 */
	public Set<String> getJiraIssueTypeNames() {
		Set<String> issueTypes = new HashSet<String>();
		for (IssueType type : getJiraIssueTypes()) {
			issueTypes.add(type.getNameTranslation());
		}
		issueTypes.add("Other");
		return issueTypes;
	}

	/**
	 * @return names of Jira issue link types available in the project.
	 */
	public static Set<String> getNamesOfLinkTypes() {
		Collection<IssueLinkType> types = getJiraIssueLinkTypes();
		Set<String> namesOfJiraIssueLinkTypes = types.stream().map(IssueLinkType::getName).collect(Collectors.toSet());
		Set<String> allLinkTypes = namesOfJiraIssueLinkTypes;
		// In the future, there will also be a "transitive" link type for transitive
		// link creation.
		allLinkTypes.add("Other");
		return allLinkTypes;
	}

	/**
	 * @return Jira issue links available in the project.
	 */
	public static Collection<IssueLinkType> getJiraIssueLinkTypes() {
		IssueLinkTypeManager linkTypeManager = ComponentAccessor.getComponent(IssueLinkTypeManager.class);
		return linkTypeManager.getIssueLinkTypes(false);
	}
}