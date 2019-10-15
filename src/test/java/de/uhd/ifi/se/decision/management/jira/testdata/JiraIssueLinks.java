package de.uhd.ifi.se.decision.management.jira.testdata;

import com.atlassian.jira.issue.MutableIssue;
import com.atlassian.jira.issue.link.IssueLink;
import com.atlassian.jira.user.ApplicationUser;
import de.uhd.ifi.se.decision.management.jira.mocks.MockIssueLink;

import java.util.ArrayList;
import java.util.List;

public class JiraIssueLinks {

	private static List<IssueLink> jiraIssueLinks;

	public static List<IssueLink> getTestIssueLinks() {
		if(jiraIssueLinks == null || jiraIssueLinks.isEmpty()) {
			jiraIssueLinks = createIssueLinks();
		}
		return jiraIssueLinks;
	}

	private static List<IssueLink> createIssueLinks() {
		List<IssueLink> issueLinks = new ArrayList<>();
		IssueLink issueLink = createJiraIssueLink(2, 4);
		issueLinks.add(issueLink);
		issueLink = createJiraIssueLink(4,2);
		issueLinks.add(issueLink);
		issueLink = createJiraIssueLink(4,30);
		issueLinks.add(issueLink);
		issueLink = createJiraIssueLink(2, 3);
		issueLinks.add(issueLink);
		issueLink = createJiraIssueLink(4,5);
		issueLinks.add(issueLink);
		issueLink = createJiraIssueLink(4,14);
		issueLinks.add(issueLink);
		return issueLinks;
	}

	private static  IssueLink createJiraIssueLink(long sourceIssueId, long destIssueId) {
		return  new MockIssueLink(sourceIssueId,destIssueId);

	}
}
