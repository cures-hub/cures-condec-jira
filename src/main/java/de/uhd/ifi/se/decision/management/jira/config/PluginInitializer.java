package de.uhd.ifi.se.decision.management.jira.config;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.inject.Named;

import org.springframework.beans.factory.InitializingBean;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.config.ConstantsManager;
import com.atlassian.jira.config.IssueTypeManager;
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

/**
 * Handles plug-in initialization
 */
@Named("PluginInitializer")
public class PluginInitializer implements InitializingBean {

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
		for (KnowledgeType type : KnowledgeType.getDefaulTypes()) {
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
		// IssueTypeManager issueTypeManager =
		// ComponentAccessor.getComponent(IssueTypeManager.class);
		// issueTypeManager.createIssueType(issueTypeName, issueTypeName, (long) 10300);

		// ConstantsManager constantsManager = ComponentAccessor.getConstantsManager();
		// constantsManager.validateCreateIssueType(issueTypeName, null, issueTypeName,
		// iconUrl, null, null);
		IssueTypeManager issueTypeManager = ComponentAccessor.getComponent(IssueTypeManager.class);
		String iconUrl = getIconUrl(issueTypeName);
		issueTypeManager.createIssueType(issueTypeName, issueTypeName + " (decision knowledge element)", iconUrl);
	}

	public static void associateIssueType(String issueTypeName, String projectKey) {
		IssueTypeManager issueTypeManager = ComponentAccessor.getComponent(IssueTypeManager.class);
		IssueType issueType = issueTypeManager.getIssueType(issueTypeName);

		IssueTypeSchemeManager issueTypeSchemeManager = ComponentAccessor.getIssueTypeSchemeManager();
		Project project = ComponentAccessor.getProjectManager().getProjectByCurrentKey(projectKey);
		FieldConfigScheme configScheme = issueTypeSchemeManager.getConfigScheme(project);
		OptionSetManager optionSetManager = ComponentAccessor.getComponent(OptionSetManager.class);
		if (!configScheme.getAssociatedIssueTypes().contains(issueType)) {
            final OptionSet options = optionSetManager.getOptionsForConfig(configScheme.getOneAndOnlyConfig());
            options.addOption(IssueFieldConstants.ISSUE_TYPE, issueType.getId());
            issueTypeSchemeManager.update(configScheme, options.getOptionIds());
        }
	}

	public static String getIconUrl(String issueTypeName) {
		return ComponentGetter.getUrlOfImageFolder() + issueTypeName.toLowerCase() + ".png";
	}

	public void createDecisionKnowledgeLinkTypes() {
		IssueLinkTypeManager issueLinkTypeManager = ComponentAccessor.getComponent(IssueLinkTypeManager.class);
		List<String> existingIssueLinkTypeNames = getNamesOfExistingIssueLinkTypes();

		if (!existingIssueLinkTypeNames.contains("contain")) {
			issueLinkTypeManager.createIssueLinkType("contain", "contains", "is contained by", "contain_style");
		}
		if (!existingIssueLinkTypeNames.contains("attack")) {
			issueLinkTypeManager.createIssueLinkType("attack", "attacks", "is attacked by", "contain_style");
		}
		if (!existingIssueLinkTypeNames.contains("support")) {
			issueLinkTypeManager.createIssueLinkType("support", "supports", "is supported by", "contain_style");
		}
		if (!existingIssueLinkTypeNames.contains("comment")) {
			issueLinkTypeManager.createIssueLinkType("comment", "comments on", "is commented on by", "contain_style");
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
