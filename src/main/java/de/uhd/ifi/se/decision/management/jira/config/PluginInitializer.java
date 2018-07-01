package de.uhd.ifi.se.decision.management.jira.config;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.inject.Named;

import org.springframework.beans.factory.InitializingBean;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.config.ConstantsManager;
import com.atlassian.jira.config.IssueTypeManager;
import com.atlassian.jira.issue.issuetype.IssueType;
import com.atlassian.jira.issue.link.IssueLinkType;
import com.atlassian.jira.issue.link.IssueLinkTypeManager;

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
		List<String> knowledgeTypes = KnowledgeType.toList();
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

	public void createIssueType(String issueTypeName) {
		IssueTypeManager issueTypeManager = ComponentAccessor.getComponent(IssueTypeManager.class);
		issueTypeManager.createIssueType(issueTypeName, issueTypeName, (long) 10300);
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
