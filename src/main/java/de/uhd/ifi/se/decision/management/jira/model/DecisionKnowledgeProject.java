package de.uhd.ifi.se.decision.management.jira.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.fields.config.manager.IssueTypeSchemeManager;
import com.atlassian.jira.issue.issuetype.IssueType;
import com.atlassian.jira.issue.link.IssueLinkType;
import com.atlassian.jira.issue.link.IssueLinkTypeManager;
import com.atlassian.jira.permission.ProjectPermissions;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.user.ApplicationUser;

import de.uhd.ifi.se.decision.management.jira.classification.TextClassificationConfiguration;
import de.uhd.ifi.se.decision.management.jira.extraction.versioncontrol.GitRepositoryConfiguration;
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
		return jiraProject != null ? jiraProject.getKey() : "";
	}

	/**
	 * @return name of the Jira project.
	 */
	public String getProjectName() {
		return jiraProject != null ? jiraProject.getName() : "";
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
		enabledKnowledgeTypes.remove(KnowledgeType.OTHER);
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
	 * @return configuration information of the git repositories for this project as
	 *         a {@link List<GitRepositoryConfiguration>} (if it is set, otherwise
	 *         an empty list).
	 */
	public List<GitRepositoryConfiguration> getGitRepositoryConfigurations() {
		return ConfigPersistenceManager.getGitRepositoryConfigurations(getProjectKey());
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
	 * @return configuration of the text classifier to automatically classify the
	 *         text of Jira issue descriptions and comments.
	 */
	public TextClassificationConfiguration getTextClassificationConfiguration() {
		return ConfigPersistenceManager.getTextClassificationConfiguration(getProjectKey());
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

	/**
	 * @param user
	 *            authenticated Jira {@link ApplicationUser}.
	 * @return list of all Jira projects in that the ConDec plugin is activated and
	 *         for that the user has the rights to browse the project, i.e., view
	 *         its content.
	 */
	public static List<Project> getProjectsWithConDecActivatedAndAccessableForUser(ApplicationUser user) {
		List<Project> projects = new ArrayList<Project>();
		for (Project project : ComponentAccessor.getProjectManager().getProjects()) {
			boolean hasPermission = ComponentAccessor.getPermissionManager()
					.hasPermission(ProjectPermissions.BROWSE_PROJECTS, project, user);
			if (ConfigPersistenceManager.isActivated(project.getKey()) && hasPermission) {
				projects.add(project);
			}
		}
		return projects;
	}
}