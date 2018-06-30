package de.uhd.ifi.se.decision.documentation.jira.mocks;

import java.util.Collection;

import com.atlassian.jira.config.IssueTypeManager;
import com.atlassian.jira.issue.issuetype.IssueType;

public class MockIssueTypeManager implements IssueTypeManager {

	@Override
	public IssueType createIssueType(String arg0, String arg1, String arg2) {
		return null;
	}

	@Override
	public IssueType createIssueType(String arg0, String arg1, Long arg2) {
		return null;
	}

	@Override
	public IssueType createSubTaskIssueType(String arg0, String arg1, String arg2) {
		return null;
	}

	@Override
	public IssueType createSubTaskIssueType(String arg0, String arg1, Long arg2) {
		return null;
	}

	@Override
	public void editIssueType(IssueType arg0, String arg1, String arg2, String arg3) {
		// method empty since not used for testing
	}

	@Override
	public Collection<IssueType> getAvailableIssueTypes(IssueType arg0) {
		return null;
	}

	@Override
	public IssueType getIssueType(String arg0) {
		return null;
	}

	@Override
	public Collection<IssueType> getIssueTypes() {
		return null;
	}

	@Override
	public boolean hasAssociatedIssues(IssueType arg0) {
		return false;
	}

	@Override
	public void removeIssueType(String arg0, String arg1) {
		// method empty since not used for testing
	}

	@Override
	public void updateIssueType(IssueType arg0, String arg1, String arg2, Long arg3) {
		// method empty since not used for testing
	}

}
