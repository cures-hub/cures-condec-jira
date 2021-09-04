package de.uhd.ifi.se.decision.management.jira.config.workflows;

import java.util.Collection;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.config.ConstantsManager;
import com.atlassian.jira.config.StatusManager;
import com.atlassian.jira.issue.issuetype.IssueType;
import com.atlassian.jira.issue.status.Status;

import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;

/**
 * @issue How can we create workflows (i.e. status of knowledge elements and
 *        their transitions) programmatically?
 * @decision We programmatically create workflows of decision knowledge elements
 *           when the plugin is installed using XML descriptors!
 */
public class WorkflowXMLDescriptorProvider {

	public static String getXMLWorkflowDescriptor(IssueType jiraIssueType) {
		switch (jiraIssueType.getName()) {
		case "Issue":
			return IssueWorkflow.getXMLDescriptor();
		case "Problem":
			return IssueWorkflow.getXMLDescriptor();
		case "Decision":
			return DecisionWorkflow.getXMLDescriptor();
		case "Solution":
			return DecisionWorkflow.getXMLDescriptor();
		case "Alternative":
			return AlternativeWorkflow.getXMLDescriptor();
		default:
			return null;
		}
	}

	public static String getStatusId(String statusName) {
		ConstantsManager constantsManager = ComponentAccessor.getConstantsManager();
		Collection<Status> listOfJiraStatus = constantsManager.getStatuses();
		for (Status status : listOfJiraStatus) {
			if (status.getName().equalsIgnoreCase(statusName)) {
				return status.getId();
			}
		}
		Status newStatus = createNewStatus(statusName);
		if (newStatus == null) {
			return "";
		}
		return newStatus.getId();
	}

	private static Status createNewStatus(String statusName) {
		StatusManager statusManager = ComponentAccessor.getComponent(StatusManager.class);
		return statusManager.createStatus(statusName, "", KnowledgeType.OTHER.getIconUrl());
	}
}
