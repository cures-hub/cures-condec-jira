package de.uhd.ifi.se.decision.management.jira.config;

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
import de.uhd.ifi.se.decision.management.jira.ComponentGetter;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.model.LinkType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;

import javax.inject.Named;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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

	public static void createIssueType(String issueTypeName) {
		IssueTypeManager issueTypeManager = ComponentAccessor.getComponent(IssueTypeManager.class);
		Collection<IssueType> types = issueTypeManager.getIssueTypes();
		if (types != null) {
			for (IssueType type : types) {
				if (type.getName().equals(issueTypeName)) {
					return;
				}
			}
		}

		String issueTypeFileName = getFileName(issueTypeName);

		InputStream inputStream = ClassLoaderUtils.getResourceAsStream("images/" + issueTypeFileName,
				PluginInitializer.class);
		Avatar tmpAvatar = AvatarImpl.createCustomAvatar(issueTypeFileName, "image/png", "0",
				IconType.ISSUE_TYPE_ICON_TYPE);
		Avatar issueAvatar = null;

		try {
			issueAvatar = ComponentAccessor.getAvatarManager().create(tmpAvatar, inputStream, null);
			if (issueAvatar != null) {
				issueTypeManager.createIssueType(issueTypeName, issueTypeName, issueAvatar.getId());
			}
		} catch (DataAccessException | IOException e) {
			LOGGER.error("Issue type " + issueTypeName + " could not be created.");
			e.printStackTrace();
		}
	}

	public static String getFileName(String issueTypeName) {
		return issueTypeName.toLowerCase() + ".png";
	}

	public static String getIconUrl(String issueTypeName) {
		return ComponentGetter.getUrlOfImageFolder() + getFileName(issueTypeName);
	}

	public static void addIssueTypeToScheme(String issueTypeName, String projectKey) {
		IssueTypeManager issueTypeManager = ComponentAccessor.getComponent(IssueTypeManager.class);
		Collection<IssueType> issueTypes = issueTypeManager.getIssueTypes();
		if (issueTypes == null || projectKey == null) {
			return;
		}

		IssueTypeSchemeManager issueTypeSchemeManager = ComponentAccessor.getIssueTypeSchemeManager();
		Project project = ComponentAccessor.getProjectManager().getProjectByCurrentKey(projectKey);

		for (IssueType issueType : issueTypes) {
			if (issueType.getName().equals(issueTypeName)
					&& !issueTypeSchemeManager.getIssueTypesForProject(project).contains(issueType)) {
				FieldConfigScheme configScheme = issueTypeSchemeManager.getConfigScheme(project);
				OptionSetManager optionSetManager = ComponentAccessor.getComponent(OptionSetManager.class);
				final OptionSet options = optionSetManager.getOptionsForConfig(configScheme.getOneAndOnlyConfig());
				options.addOption(IssueFieldConstants.ISSUE_TYPE, issueType.getId());
				issueTypeSchemeManager.update(configScheme, options.getOptionIds());
				return;
			}
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

	public void createDecisionKnowledgeLinkTypes() {
		IssueLinkTypeManager issueLinkTypeManager = ComponentAccessor.getComponent(IssueLinkTypeManager.class);
		List<String> existingIssueLinkTypeNames = getNamesOfExistingIssueLinkTypes();

		for (LinkType linkType: LinkType.values()) {
			if (!existingIssueLinkTypeNames.contains(linkType.getName())) {
				issueLinkTypeManager.createIssueLinkType(linkType.getName(), linkType.getOutwardLink(), linkType.getInwardLink(), linkType.getStyle());
			}
		}

	}

	public List<String> getNamesOfExistingIssueLinkTypes() {
		List<String> existingIssueLinkTypeNames = new ArrayList<String>();
		IssueLinkTypeManager issueLinkTypeManager = ComponentAccessor.getComponent(IssueLinkTypeManager.class);
		Collection<IssueLinkType> issueLinkTypes = issueLinkTypeManager.getIssueLinkTypes(true);
		for (IssueLinkType issueLinkType : issueLinkTypes) {
			existingIssueLinkTypeNames.add(issueLinkType.getName());
		}
		return existingIssueLinkTypeNames;
	}
}
