package de.uhd.ifi.se.decision.management.jira.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;

import com.atlassian.core.util.ClassLoaderUtils;
import com.atlassian.jira.avatar.Avatar;
import com.atlassian.jira.avatar.AvatarImpl;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.config.ConstantsManager;
import com.atlassian.jira.config.IssueTypeManager;
import com.atlassian.jira.exception.DataAccessException;
import com.atlassian.jira.icon.IconType;
import com.atlassian.jira.issue.IssueFieldConstants;
import com.atlassian.jira.issue.fields.config.FieldConfigScheme;
import com.atlassian.jira.issue.fields.config.manager.IssueTypeSchemeManager;
import com.atlassian.jira.issue.fields.option.OptionSet;
import com.atlassian.jira.issue.fields.option.OptionSetManager;
import com.atlassian.jira.issue.issuetype.IssueType;
import com.atlassian.jira.issue.link.IssueLinkType;
import com.atlassian.jira.issue.link.IssueLinkTypeManager;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.scheme.Scheme;
import com.atlassian.jira.security.JiraAuthenticationContext;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.workflow.AssignableWorkflowScheme;
import com.atlassian.jira.workflow.ConfigurableJiraWorkflow;
import com.atlassian.jira.workflow.JiraWorkflow;
import com.atlassian.jira.workflow.WorkflowManager;
import com.atlassian.jira.workflow.WorkflowSchemeManager;
import com.atlassian.jira.workflow.WorkflowUtil;
import com.opensymphony.workflow.FactoryException;
import com.opensymphony.workflow.loader.WorkflowDescriptor;

import de.uhd.ifi.se.decision.management.jira.ComponentGetter;
import de.uhd.ifi.se.decision.management.jira.config.workflows.AlternativeWorkflow;
import de.uhd.ifi.se.decision.management.jira.config.workflows.DecisionWorkflow;
import de.uhd.ifi.se.decision.management.jira.config.workflows.IssueWorkflow;
import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeProject;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.model.LinkType;

/**
 * Handles plug-in initialization
 */
@Named("PluginInitializer")
public class PluginInitializer implements InitializingBean {
	private static final Logger LOGGER = LoggerFactory.getLogger(PluginInitializer.class);

	@Override
	public void afterPropertiesSet() {
		createDecisionKnowledgeIssueTypes();
		createDecisionKnowledgeLinkTypes();
	}

	public void createDecisionKnowledgeIssueTypes() {
		List<String> missingDecisionKnowledgeIssueTypeNames = findMissingDecisionKnowledgeIssueTypes();
		for (String issueTypeName : missingDecisionKnowledgeIssueTypeNames) {
			createIssueType(issueTypeName);
		}
	}

	public List<String> findMissingDecisionKnowledgeIssueTypes() {
		List<String> knowledgeTypes = new ArrayList<String>();
		for (KnowledgeType type : KnowledgeType.getDefaultTypes()) {
			knowledgeTypes.add(type.toString());
		}
		for (String issueTypeName : getNamesOfExistingIssueTypes()) {
			knowledgeTypes.remove(issueTypeName);
		}
		return knowledgeTypes;
	}

	public List<String> getNamesOfExistingIssueTypes() {
		List<String> existingIssueTypeNames = new ArrayList<String>();
		ConstantsManager constantsManager = ComponentAccessor.getConstantsManager();
		Collection<IssueType> issueTypes = constantsManager.getAllIssueTypeObjects();
		for (IssueType issueType : issueTypes) {
			existingIssueTypeNames.add(issueType.getName());
		}
		return existingIssueTypeNames;
	}

	public static IssueType createIssueType(String issueTypeName) {
		IssueTypeManager issueTypeManager = ComponentAccessor.getComponent(IssueTypeManager.class);
		Collection<IssueType> types = issueTypeManager.getIssueTypes();
		if (types != null) {
			for (IssueType type : types) {
				if (type.getName().equals(issueTypeName)) {
					return type;
				}
			}
		}

		String issueTypeFileName = getFileName(issueTypeName);

		InputStream inputStream = ClassLoaderUtils.getResourceAsStream("images/" + issueTypeFileName,
				PluginInitializer.class);
		Avatar tmpAvatar = AvatarImpl.createCustomAvatar(issueTypeFileName, "image/png", "0",
				IconType.ISSUE_TYPE_ICON_TYPE);
		Avatar issueAvatar = null;

		IssueType newIssueType = null;
		try {
			issueAvatar = ComponentAccessor.getAvatarManager().create(tmpAvatar, inputStream, null);
			if (issueAvatar != null) {
				newIssueType = issueTypeManager.createIssueType(issueTypeName, issueTypeName, issueAvatar.getId());
			}
		} catch (DataAccessException | IOException e) {
			LOGGER.error("Issue type " + issueTypeName + " could not be created.");
			LOGGER.error(e.getMessage());
		}

		return newIssueType;
	}

	public void createDecisionKnowledgeLinkTypes() {
		IssueLinkTypeManager issueLinkTypeManager = ComponentAccessor.getComponent(IssueLinkTypeManager.class);
		Set<String> existingIssueLinkTypeNames = DecisionKnowledgeProject.getNamesOfLinkTypes();

		for (LinkType linkType : LinkType.getDefaultTypes()) {
			if (existingIssueLinkTypeNames.contains(linkType.getName())) {
				continue;
			}
			// TODO Use "createLinkType" method from below
			issueLinkTypeManager.createIssueLinkType(linkType.getName(), linkType.getOutwardName(),
					linkType.getInwardName(), linkType.getStyle());
		}
	}

	public static void createLinkType(String linkTypeName) {
		IssueLinkTypeManager linkTypeManager = ComponentAccessor.getComponent(IssueLinkTypeManager.class);
		Collection<IssueLinkType> types = linkTypeManager.getIssueLinkTypes();
		if (types == null) {
			return;
		}
		Optional<IssueLinkType> type = types.stream().filter(entry -> entry.getName().equals(linkTypeName)).findFirst();
		if (!type.isEmpty()) {
			return;
		}
		LinkType linktype = LinkType.getLinkType(linkTypeName);
		linkTypeManager.createIssueLinkType(linktype.getName(), linktype.getOutwardName(), linktype.getInwardName(),
				linktype.getStyle());
	}

	public static void removeLinkType(String linkTypeName) {
		IssueLinkTypeManager linkTypeManager = ComponentAccessor.getComponent(IssueLinkTypeManager.class);
		Collection<IssueLinkType> types = linkTypeManager.getIssueLinkTypes();
		if (types == null) {
			return;
		}
		Optional<IssueLinkType> type = types.stream().filter(entry -> entry.getName().equals(linkTypeName)).findFirst();
		type.ifPresent(issueLinkType -> linkTypeManager.removeIssueLinkType(issueLinkType.getId()));
	}

	public static void addLinkTypeToScheme(String linkTypeName, String projectKey) {
		IssueTypeManager issueTypeManager = ComponentAccessor.getComponent(IssueTypeManager.class);
		Collection<IssueType> issueTypes = issueTypeManager.getIssueTypes();
		if (issueTypes == null || projectKey == null) {
			return;
		}
		// TODO: Umsetzen wenn https://jira.atlassian.com/browse/JRASERVER-16325
		createLinkType(linkTypeName);
	}

	public static void removeLinkTypeFromScheme(String linkTypeName, String projectKey) {
		IssueTypeManager issueTypeManager = ComponentAccessor.getComponent(IssueTypeManager.class);
		Collection<IssueType> issueTypes = issueTypeManager.getIssueTypes();
		if (issueTypes == null || projectKey == null) {
			return;
		}
		// TODO: Umsetzen wenn https://jira.atlassian.com/browse/JRASERVER-16325
		removeLinkType(linkTypeName);
	}

	public static String getFileName(String issueTypeName) {
		return issueTypeName.toLowerCase() + ".png";
	}

	public static String getIconUrl(String issueTypeName) {
		return ComponentGetter.getUrlOfImageFolder() + getFileName(issueTypeName);
	}

	public static void addIssueTypeToScheme(IssueType jiraIssueType, String projectKey) {
		if (jiraIssueType == null || projectKey == null) {
			return;
		}

		Project project = ComponentAccessor.getProjectManager().getProjectByCurrentKey(projectKey);
		addWorkflowToWorkflowScheme(jiraIssueType, project);
		IssueTypeSchemeManager issueTypeSchemeManager = ComponentAccessor.getIssueTypeSchemeManager();

		if (!issueTypeSchemeManager.getIssueTypesForProject(project).contains(jiraIssueType)) {
			FieldConfigScheme configScheme = issueTypeSchemeManager.getConfigScheme(project);
			OptionSetManager optionSetManager = ComponentAccessor.getComponent(OptionSetManager.class);
			final OptionSet options = optionSetManager.getOptionsForConfig(configScheme.getOneAndOnlyConfig());
			options.addOption(IssueFieldConstants.ISSUE_TYPE, jiraIssueType.getId());
			issueTypeSchemeManager.update(configScheme, options.getOptionIds());
			return;
		}
	}

	/**
	 * @issue Should we do the workflow migration programmatically if issue types
	 *        already existed?
	 * @decision We do not do the workflow migration programmatically if issue types
	 *           already existed!
	 * @pro No extra implementation needed.
	 * @con Requires that the system admin does the migration manually in the admin
	 *      page for workflow schemes.
	 * @alternative We could do the workflow migration programmatically if issue
	 *              types already existed!
	 * @pro This would
	 * @con This might be error prone and we do not now the former status to be
	 *      migrated.
	 * 
	 * @param jiraIssueType
	 *            to add a workflow (i.e. status and their transitions) for.
	 * @param project
	 *            Jira project.
	 */
	private static void addWorkflowToWorkflowScheme(IssueType jiraIssueType, Project project) {
		JiraWorkflow jiraWorkflow = createWorkflow(jiraIssueType);
		if (jiraWorkflow == null) {
			return;
		}
		WorkflowSchemeManager workflowSchemeManager = ComponentAccessor.getComponent(WorkflowSchemeManager.class);
		Scheme scheme = workflowSchemeManager.getSchemeFor(project);
		AssignableWorkflowScheme myWorkflowScheme = workflowSchemeManager.getWorkflowSchemeObj(scheme.getName());
		AssignableWorkflowScheme.Builder myWorkflowSchemeBuilder = myWorkflowScheme.builder();
		myWorkflowSchemeBuilder.setMapping(jiraIssueType.getId(), jiraWorkflow.getName());

		workflowSchemeManager.updateWorkflowScheme(myWorkflowSchemeBuilder.build());
		workflowSchemeManager.addSchemeToProject(project,
				workflowSchemeManager.getSchemeObject(myWorkflowScheme.getId()));
	}

	private static JiraWorkflow createWorkflow(IssueType jiraIssueType) {
		String workflowDescriptor = getXMLWorkflowDescriptor(jiraIssueType);
		if (workflowDescriptor == null) {
			return null;
		}
		// System.out.println(content);
		JiraAuthenticationContext jiraAuthenticationContext = ComponentAccessor.getJiraAuthenticationContext();
		ApplicationUser user = jiraAuthenticationContext.getLoggedInUser();

		WorkflowManager workflowManager = ComponentAccessor.getComponent(WorkflowManager.class);

		JiraWorkflow jiraWorkflow = null;
		try {
			WorkflowDescriptor myWorkflowDescriptor = WorkflowUtil.convertXMLtoWorkflowDescriptor(workflowDescriptor);
			jiraWorkflow = new ConfigurableJiraWorkflow("ConDec " + jiraIssueType.getName() + " Workflow",
					myWorkflowDescriptor, workflowManager);
			workflowManager.createWorkflow(user, jiraWorkflow);
		} catch (FactoryException e) {
			LOGGER.error("Workflow could not be created. " + e.getMessage());
		}

		return jiraWorkflow;
	}

	private static String getXMLWorkflowDescriptor(IssueType jiraIssueType) {
		switch (jiraIssueType.getName()) {
		case "Issue":
			return IssueWorkflow.getXMLDescriptor();
		case "Decision":
			return DecisionWorkflow.getXMLDescriptor();
		case "Alternative":
			return AlternativeWorkflow.getXMLDescriptor();
		default:
			return null;
		}
	}

	public static void removeIssueTypeFromScheme(String issueTypeName, String projectKey) {
		IssueTypeManager issueTypeManager = ComponentAccessor.getComponent(IssueTypeManager.class);
		Collection<IssueType> issueTypes = issueTypeManager.getIssueTypes();
		if (issueTypes == null || projectKey == null) {
			return;
		}

		IssueTypeSchemeManager issueTypeSchemeManager = ComponentAccessor.getIssueTypeSchemeManager();
		Project project = ComponentAccessor.getProjectManager().getProjectByCurrentKey(projectKey);

		for (IssueType issueType : issueTypes) {
			if (issueType.getName().equals(issueTypeName)
					&& issueTypeSchemeManager.getIssueTypesForProject(project).contains(issueType)) {
				FieldConfigScheme configScheme = issueTypeSchemeManager.getConfigScheme(project);
				OptionSetManager optionSetManager = ComponentAccessor.getComponent(OptionSetManager.class);
				final OptionSet options = optionSetManager.getOptionsForConfig(configScheme.getOneAndOnlyConfig());
				Collection<String> optionIds = options.getOptionIds();
				for (String optionId : optionIds) {
					if (optionId == issueType.getId()) {
						optionIds.remove(optionId);
					}
				}
				issueTypeSchemeManager.update(configScheme, optionIds);
				return;
			}
		}
	}
}
