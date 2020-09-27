package de.uhd.ifi.se.decision.management.jira.config.workflows;

import com.atlassian.jira.issue.issuetype.IssueType;

/**
 * @issue How can we create workflows programmatically?
 * @issue How can we read XML files from the resources folder?
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

}
