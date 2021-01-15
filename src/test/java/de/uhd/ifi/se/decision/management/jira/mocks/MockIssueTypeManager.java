package de.uhd.ifi.se.decision.management.jira.mocks;

import java.util.ArrayList;
import java.util.Collection;

import com.atlassian.jira.config.ConstantsManager;
import com.atlassian.jira.config.IssueTypeManager;
import com.atlassian.jira.exception.CreateException;
import com.atlassian.jira.issue.issuetype.IssueType;
import com.atlassian.jira.mock.MockConstantsManager;

public class MockIssueTypeManager implements IssueTypeManager {

	private Collection<IssueType> types;

	public MockIssueTypeManager() {
		super();
		types = new ArrayList<>();
		addingAllIssueTypes();
	}

	public void addingAllIssueTypes() {
		ConstantsManager constantsManager = new MockConstantsManager();
		try {
			constantsManager.insertIssueType("Decision", (long) 20, "Test", "Test", (long) 12290);
			constantsManager.insertIssueType("Alternative", (long) 20, "Test", "Test", (long) 12290);
			constantsManager.insertIssueType("Argument", (long) 20, "Test", "Test", (long) 12290);
			constantsManager.insertIssueType("Assessment", (long) 20, "Test", "Test", (long) 12290);
			constantsManager.insertIssueType("Assumption", (long) 20, "Test", "Test", (long) 12290);
			constantsManager.insertIssueType("Claim", (long) 20, "Test", "Test", (long) 12290);
			constantsManager.insertIssueType("Constraint", (long) 20, "Test", "Test", (long) 12290);
			constantsManager.insertIssueType("Context", (long) 20, "Test", "Test", (long) 12290);
			constantsManager.insertIssueType("Goal", (long) 20, "Test", "Test", (long) 12290);
			constantsManager.insertIssueType("Implication", (long) 20, "Test", "Test", (long) 12290);
			constantsManager.insertIssueType("Issue", (long) 20, "Test", "Test", (long) 12290);
			constantsManager.insertIssueType("Problem", (long) 20, "Test", "Test", (long) 12290);
			constantsManager.insertIssueType("Solution", (long) 20, "Test", "Test", (long) 12290);
		} catch (CreateException e) {
			System.err.println(e.getMessage());
		}
		types.addAll(constantsManager.getAllIssueTypeObjects());
	}

	public static void addAllIssueTypesToConstantsManager(ConstantsManager constantsManager) throws CreateException {

		// Adding all Issue Types

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
