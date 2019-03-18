package de.uhd.ifi.se.decision.management.jira.model.text.impl;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.IssueManager;

import de.uhd.ifi.se.decision.management.jira.model.DocumentationLocation;
import de.uhd.ifi.se.decision.management.jira.model.text.PartOfDescription;

public class PartOfDescriptionImpl extends PartOfTextImpl implements PartOfDescription {

	public PartOfDescriptionImpl() {
		super();
		this.documentationLocation = DocumentationLocation.JIRAISSUEDESCRIPTION;
	}

	@Override
	public String getText() {
		String body = getJiraIssueDescription().substring(this.getStartSubstringCount(), this.getEndSubstringCount());
		return body.replaceAll("\\{.*?\\}", "");
	}

	@Override
	public String getJiraIssueDescription() {
		IssueManager issueManager = ComponentAccessor.getIssueManager();
		Issue issue = issueManager.getIssueObject(this.getJiraIssueId());
		if (issue == null) {
			return super.getSummary();
		}
		return issue.getDescription();
	}
}
