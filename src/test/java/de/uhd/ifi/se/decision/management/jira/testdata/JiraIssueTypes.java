package de.uhd.ifi.se.decision.management.jira.testdata;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.atlassian.jira.issue.issuetype.IssueType;
import com.atlassian.jira.issue.issuetype.MockIssueType;

import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;

public class JiraIssueTypes {

	public static List<IssueType> getTestJiraIssueTypes() {
		// return
		// ComponentAccessor.getConstantsManager().getAllIssueTypeObjects().toArray();
		return createJiraIssueTypesForDecisionKnowledgeTypes();
	}

	public static List<IssueType> createJiraIssueTypesForDecisionKnowledgeTypes() {
		List<IssueType> jiraIssueTypes = new ArrayList<IssueType>();
		int i = 0;
		for (KnowledgeType type : KnowledgeType.values()) {
			IssueType issueType = new MockIssueType(i, type.name().toLowerCase(Locale.ENGLISH));
			jiraIssueTypes.add(issueType);
			i++;
		}
		return jiraIssueTypes;
	}

}
