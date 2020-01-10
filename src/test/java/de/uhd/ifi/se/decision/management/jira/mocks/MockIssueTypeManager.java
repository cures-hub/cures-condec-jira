package de.uhd.ifi.se.decision.management.jira.mocks;

import com.atlassian.jira.config.ConstantsManager;
import com.atlassian.jira.config.IssueTypeManager;
import com.atlassian.jira.exception.CreateException;
import com.atlassian.jira.issue.issuetype.IssueType;
import com.atlassian.jira.mock.MockConstantsManager;
import de.uhd.ifi.se.decision.management.jira.config.TestPluginInitializer;

import java.util.ArrayList;
import java.util.Collection;

public class MockIssueTypeManager implements IssueTypeManager {

	private Collection<IssueType> types;

	public MockIssueTypeManager() {
		super();
		types = new ArrayList<>();
		addingAllIssueTypes();
	}

	public void addingAllIssueTypes() {
		ConstantsManager constManager = new MockConstantsManager();
		try {
			TestPluginInitializer.addAllIssueTypesToConstantsManager(constManager);
		} catch (CreateException e) {
			e.printStackTrace();
		}
		types.addAll(constManager.getAllIssueTypeObjects());
	}

	public void addIssueType(Collection<IssueType> issueTypes) {
		types.addAll(issueTypes);
	}

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
		return types;
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
