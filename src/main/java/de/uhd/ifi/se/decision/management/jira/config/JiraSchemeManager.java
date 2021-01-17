package de.uhd.ifi.se.decision.management.jira.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atlassian.core.util.ClassLoaderUtils;
import com.atlassian.jira.avatar.Avatar;
import com.atlassian.jira.avatar.AvatarImpl;
import com.atlassian.jira.component.ComponentAccessor;
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
import de.uhd.ifi.se.decision.management.jira.config.workflows.WorkflowXMLDescriptorProvider;
import de.uhd.ifi.se.decision.management.jira.model.LinkType;

/**
 * Handles the creation and removal of Jira issues types, Jira link types, and
 * workflows.
 * 
 * Adds and removes Jira issue types to the issue type scheme of a project.
 * 
 * Adds and removes Jira workflows to the workflow scheme of a project.
 */
public class JiraSchemeManager {
	private static final Logger LOGGER = LoggerFactory.getLogger(JiraSchemeManager.class);
	private String projectKey;

	public JiraSchemeManager(String projectKey) {
		this.projectKey = projectKey;
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
				JiraSchemeManager.class);
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
			LOGGER.error("Issue type " + issueTypeName + " could not be created: " + e.getMessage());
		}

		return newIssueType;
	}

	public static boolean createLinkType(String linkTypeName) {
		IssueLinkTypeManager linkTypeManager = ComponentAccessor.getComponent(IssueLinkTypeManager.class);
		Collection<IssueLinkType> types = linkTypeManager.getIssueLinkTypes();
		if (types == null) {
			return false;
		}
		Optional<IssueLinkType> type = types.stream().filter(entry -> entry.getName().equals(linkTypeName)).findFirst();
		if (!type.isEmpty()) {
			return false;
		}
		LinkType linktype = LinkType.getLinkType(linkTypeName);
		linkTypeManager.createIssueLinkType(linktype.getName(), linktype.getOutwardName(), linktype.getInwardName(),
				linktype.getStyle());
		return true;
	}

	public static boolean removeLinkType(String linkTypeName) {
		IssueLinkTypeManager linkTypeManager = ComponentAccessor.getComponent(IssueLinkTypeManager.class);
		Collection<IssueLinkType> types = linkTypeManager.getIssueLinkTypes();
		if (types == null) {
			return false;
		}
		Optional<IssueLinkType> type = types.stream().filter(entry -> entry.getName().equals(linkTypeName)).findFirst();
		type.ifPresent(issueLinkType -> linkTypeManager.removeIssueLinkType(issueLinkType.getId()));
		return true;
	}

	public boolean addLinkTypeToScheme(String linkTypeName) {
		if (linkTypeName == null) {
			return false;
		}
		// TODO: Umsetzen wenn https://jira.atlassian.com/browse/JRASERVER-16325
		return createLinkType(linkTypeName);
	}

	public boolean removeLinkTypeFromScheme(String linkTypeName) {
		if (linkTypeName == null) {
			return false;
		}
		// TODO: Umsetzen wenn https://jira.atlassian.com/browse/JRASERVER-16325
		return removeLinkType(linkTypeName);
	}

	public static String getFileName(String issueTypeName) {
		return issueTypeName.toLowerCase() + ".png";
	}

	public static String getIconUrl(String issueTypeName) {
		return ComponentGetter.getUrlOfImageFolder() + getFileName(issueTypeName);
	}

	public boolean addIssueTypeToScheme(IssueType jiraIssueType) {
		if (jiraIssueType == null) {
			return false;
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
		}
		return true;
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
		String workflowDescriptor = WorkflowXMLDescriptorProvider.getXMLWorkflowDescriptor(jiraIssueType);
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

	public boolean removeIssueTypeFromScheme(String issueTypeName) {
		IssueTypeManager issueTypeManager = ComponentAccessor.getComponent(IssueTypeManager.class);
		Collection<IssueType> issueTypes = issueTypeManager.getIssueTypes();
		if (issueTypeName == null || issueTypes == null) {
			return false;
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
			}
		}
		return true;
	}

	public Collection<IssueType> getJiraIssueTypes() {
		if (projectKey == null) {
			return new ArrayList<IssueType>();
		}
		IssueTypeSchemeManager issueTypeSchemeManager = ComponentAccessor.getIssueTypeSchemeManager();
		Project project = ComponentAccessor.getProjectManager().getProjectObjByKey(projectKey);
		return issueTypeSchemeManager.getIssueTypesForProject(project);
	}

	public static String getJiraIssueTypeName(String typeId) {
		IssueType issueType = getJiraIssueType(typeId);
		if (issueType == null) {
			return "";
		}
		return issueType.getName();
	}

	public static IssueType getJiraIssueType(String typeId) {
		if (typeId == null || typeId.isBlank()) {
			return null;
		}
		IssueType issueType = ComponentAccessor.getConstantsManager().getIssueType(typeId);
		return issueType;
	}

	public static Collection<IssueType> getJiraIssueTypes(long projectId) {
		if (projectId <= 0) {
			return new ArrayList<IssueType>();
		}
		IssueTypeSchemeManager issueTypeSchemeManager = ComponentAccessor.getIssueTypeSchemeManager();
		Project project = ComponentAccessor.getProjectManager().getProjectObj(projectId);
		return issueTypeSchemeManager.getIssueTypesForProject(project);
	}
}
