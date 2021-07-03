package de.uhd.ifi.se.decision.management.jira.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
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

import de.uhd.ifi.se.decision.management.jira.changeimpactanalysis.ChangeImpactAnalysisConfiguration;
import de.uhd.ifi.se.decision.management.jira.classification.TextClassificationConfiguration;
import de.uhd.ifi.se.decision.management.jira.classification.TextClassifier;
import de.uhd.ifi.se.decision.management.jira.config.BasicConfiguration;
import de.uhd.ifi.se.decision.management.jira.git.config.GitConfiguration;
import de.uhd.ifi.se.decision.management.jira.persistence.ConfigPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.quality.completeness.DefinitionOfDone;
import de.uhd.ifi.se.decision.management.jira.recommendation.decisionguidance.DecisionGuidanceConfiguration;
import de.uhd.ifi.se.decision.management.jira.recommendation.linkrecommendation.LinkRecommendationConfiguration;
import de.uhd.ifi.se.decision.management.jira.recommendation.prompts.PromptingEventConfiguration;
import de.uhd.ifi.se.decision.management.jira.webhook.WebhookConfiguration;

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
	 * @return configuration information for the basic settings for the ConDec
	 *         plug-in, e.g. whether it is activated and which
	 *         {@link KnowledgeType}s are documented.
	 */
	public BasicConfiguration getBasicConfiguration() {
		return ConfigPersistenceManager.getBasicConfiguration(getProjectKey());
	}

	/**
	 * @return names of {@link KnowledgeType}s that are used in this project as
	 *         Strings.
	 */
	public Set<String> getNamesOfConDecKnowledgeTypes() {
		Set<KnowledgeType> knowledgeTypes = getBasicConfiguration().getActivatedKnowledgeTypes();
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
	 * @return configuration information of the git connection including the git
	 *         repositories for this project as a {@link GitConfiguration} object.
	 */
	public GitConfiguration getGitConfiguration() {
		return ConfigPersistenceManager.getGitConfiguration(getProjectKey());
	}

	/**
	 * @return configuration of the {@link TextClassifier} to automatically classify
	 *         the text of Jira issue descriptions and comments.
	 */
	public TextClassificationConfiguration getTextClassificationConfiguration() {
		return ConfigPersistenceManager.getTextClassificationConfiguration(getProjectKey());
	}

	/**
	 * @return configuration for the {@link DefinitionOfDone} for knowledge
	 *         documentation for this project.
	 */
	public DefinitionOfDone getDefinitionOfDone() {
		return ConfigPersistenceManager.getDefinitionOfDone(getProjectKey());
	}

	/**
	 * @return configuration for the recommendation of knowledge elements from
	 *         external knowledge sources.
	 */
	public DecisionGuidanceConfiguration getDecisionGuidanceConfiguration() {
		return ConfigPersistenceManager.getDecisionGuidanceConfiguration(getProjectKey());
	}

	/**
	 * @return configuration for the link suggestion and duplicate recognition
	 *         within one project.
	 */
	public LinkRecommendationConfiguration getLinkSuggestionConfiguration() {
		return ConfigPersistenceManager.getLinkRecommendationConfiguration(getProjectKey());
	}

	/**
	 * @return configuration for just-in-time prompts (for developer nudging).
	 */
	public PromptingEventConfiguration getPromptingEventConfiguration() {
		return ConfigPersistenceManager.getPromptingEventConfiguration(getProjectKey());
	}

	/**
	 * @return configuration information of the webhook to post changed decision
	 *         knowledge to a given URL (e.g. chat channel).
	 */
	public WebhookConfiguration getWebhookConfiguration() {
		return ConfigPersistenceManager.getWebhookConfiguration(getProjectKey());
	}

	/**
	 * @return Jira issue types available in the project.
	 */
	public Set<IssueType> getJiraIssueTypes() {
		if (jiraProject == null) {
			return new HashSet<>();
		}
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
		allLinkTypes.add("Other");
		allLinkTypes.add("Transitive");
		return allLinkTypes;
	}

	/**
	 * @return names of the inward and outward links of all {@link LinkType}s, e.g.
	 *         "supports" (outward) and "is supported by" (inward).
	 */
	public static Set<String> getInwardAndOutwardNamesOfLinkTypes() {
		Collection<IssueLinkType> types = getJiraIssueLinkTypes();
		Set<String> allLinkTypes = new HashSet<>();
		allLinkTypes.addAll(types.stream().map(IssueLinkType::getInward).collect(Collectors.toSet()));
		allLinkTypes.addAll(types.stream().map(IssueLinkType::getOutward).collect(Collectors.toSet()));
		// The "transitive" link type for transitive link creation is not included
		// because change impact analysis is performed prior to transitive linking.
		allLinkTypes.add("other");
		return allLinkTypes;
	}

	/**
	 * @return Jira issue link types available in the project.
	 */
	public static Collection<IssueLinkType> getJiraIssueLinkTypes() {
		IssueLinkTypeManager linkTypeManager = ComponentAccessor.getComponent(IssueLinkTypeManager.class);
		return linkTypeManager.getIssueLinkTypes(false);
	}

	/**
	 * @return configuration information for change impact analysis.
	 */
	public ChangeImpactAnalysisConfiguration getChangeImpactAnalysisConfiguration() {
		return ConfigPersistenceManager.getChangeImpactAnalysisConfiguration(getProjectKey());
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
			if (ConfigPersistenceManager.getBasicConfiguration(project.getKey()).isActivated() && hasPermission) {
				projects.add(project);
			}
		}
		return projects;
	}
}