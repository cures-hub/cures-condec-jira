package de.uhd.ifi.se.decision.management.jira.testdata;

import java.util.ArrayList;
import java.util.List;

import com.atlassian.jira.issue.issuetype.IssueType;
import com.atlassian.jira.issue.issuetype.MockIssueType;

import de.uhd.ifi.se.decision.management.jira.mocks.MockComponentAccessor;

/**
 * Class for the JIRA issue types used in the unit tests. There are types for
 * every decision knowledge type. The test types are included in the
 * ComponentAccessor.getConstantsManager().
 * 
 * @see MockComponentAccessor
 */
public class JiraIssueTypes {

	private static List<IssueType> jiraIssueTypes;

	public static List<IssueType> getTestTypes() {
		if (jiraIssueTypes == null || jiraIssueTypes.isEmpty()) {
			jiraIssueTypes = new ArrayList<IssueType>();
			jiraIssueTypes.add(new MockIssueType(0, "Task"));
			jiraIssueTypes.add(new MockIssueType(1, "Issue"));
			jiraIssueTypes.add(new MockIssueType(2, "Alternative"));
			jiraIssueTypes.add(new MockIssueType(3, "Decision"));
			jiraIssueTypes.add(new MockIssueType(4, "Argument"));
			jiraIssueTypes.add(new MockIssueType(5, "Non functional requirement"));
			jiraIssueTypes.add(new MockIssueType(6, "Bug"));
		}
		return jiraIssueTypes;
	}
}
