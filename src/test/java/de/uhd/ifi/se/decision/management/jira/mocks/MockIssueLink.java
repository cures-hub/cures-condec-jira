package de.uhd.ifi.se.decision.management.jira.mocks;

import java.sql.Timestamp;

import org.ofbiz.core.entity.GenericValue;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.issuetype.IssueType;
import com.atlassian.jira.issue.issuetype.MockIssueType;
import com.atlassian.jira.issue.link.IssueLink;
import com.atlassian.jira.issue.link.IssueLinkType;
import com.atlassian.jira.mock.issue.MockIssue;
import com.atlassian.jira.project.MockProject;
import com.atlassian.jira.project.Project;

public class MockIssueLink implements IssueLink {

	private long sourceId;
	private long destinationId;
	private long sequence;
	private long linkId;

	public MockIssueLink(long sourceId, long destinationId, long linkId) {
		this.sourceId = sourceId;
		this.destinationId = destinationId;
		this.linkId = linkId;
	}

	@Override
	public GenericValue getGenericValue() {
		return null;
	}

	@Override
	public Long getLong(String arg0) {
		return null;
	}

	@Override
	public String getString(String arg0) {
		return null;
	}

	@Override
	public Timestamp getTimestamp(String arg0) {
		return null;
	}

	@Override
	public void store() {
		// method empty since not used for testing
	}

	@Override
	public Long getDestinationId() {
		return destinationId;
	}

	@Override
	public Issue getDestinationObject() {
		Issue issue = ComponentAccessor.getIssueManager().getIssueObject(destinationId);
		if (issue == null) {
			issue = new MockIssue(200, "TEST-200");
			IssueType issueType = new MockIssueType(4, "Argument");
			((MockIssue) issue).setIssueType(issueType);
			((MockIssue) issue).setDescription("Test");
			((MockIssue) issue).setSummary("Test");
			Project project = new MockProject(1, "TEST");
			((MockProject) project).setKey("TEST");
			((MockIssue) issue).setProjectObject(project);
		}
		return issue;
	}

	@Override
	public Long getId() {
		return linkId;
	}

	@Override
	public IssueLinkType getIssueLinkType() {
		return new MockIssueLinkType((long) 123);
	}

	@Override
	public Long getLinkTypeId() {
		return null;
	}

	@Override
	public Long getSequence() {
		return sequence;
	}

	public void setSequence(long sequence) {
		this.sequence = sequence;
	}

	@Override
	public Long getSourceId() {
		return sourceId;
	}

	@Override
	public Issue getSourceObject() {
		Issue issue = ComponentAccessor.getIssueManager().getIssueObject(sourceId);
		if (issue == null) {
			issue = new MockIssue(300, "TEST-300");
			IssueType issueType = new MockIssueType(4, "Argument");
			((MockIssue) issue).setIssueType(issueType);
			((MockIssue) issue).setDescription("Test");
			((MockIssue) issue).setSummary("Test");
			Project project = new MockProject(1, "TEST");
			((MockProject) project).setKey("TEST");
			((MockIssue) issue).setProjectObject(project);
		}
		return issue;
	}

	@Override
	public boolean isSystemLink() {
		return false;
	}

}
