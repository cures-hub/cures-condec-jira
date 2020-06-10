package de.uhd.ifi.se.decision.management.jira.testdata;

import com.atlassian.jira.issue.link.IssueLink;
import de.uhd.ifi.se.decision.management.jira.mocks.MockIssueLink;

import java.util.ArrayList;
import java.util.List;

public class JiraIssueLinks {

	private static List<IssueLink> jiraIssueLinks;

	public static List<IssueLink> getTestJiraIssueLinks() {
		if (jiraIssueLinks == null || jiraIssueLinks.isEmpty()) {
			jiraIssueLinks = createIssueLinks();
		}
		return jiraIssueLinks;
	}

	private static List<IssueLink> createIssueLinks() {
		List<IssueLink> issueLinks = new ArrayList<>();
		IssueLink issueLink = createJiraIssueLink(2, 4, 2);
		issueLinks.add(issueLink);
		issueLink = createJiraIssueLink(4, 2, 3);
		issueLinks.add(issueLink);
		issueLink = createJiraIssueLink(4, 30, 4);
		issueLinks.add(issueLink);
		issueLink = createJiraIssueLink(2, 3, 5);
		issueLinks.add(issueLink);
		issueLink = createJiraIssueLink(4, 5, 6);
		issueLinks.add(issueLink);
		issueLink = createJiraIssueLink(4, 14, 7);
		issueLinks.add(issueLink);
		issueLink = createJiraIssueLink(14, 3, 8);
		issueLinks.add(issueLink);
		issueLink = createJiraIssueLink(3, 1, 9);
		issueLinks.add(issueLink);
		issueLink = createJiraIssueLink(1, 5, 10);
		issueLinks.add(issueLink);
		issueLink = createJiraIssueLink(1, 14, 11);
		issueLinks.add(issueLink);
		issueLink = createJiraIssueLink(14, 30, 11);
		issueLinks.add(issueLink);
		return issueLinks;
	}

	private static IssueLink createJiraIssueLink(long sourceIssueId, long destIssueId, long issueLinkId) {
		return new MockIssueLink(sourceIssueId, destIssueId, issueLinkId);

	}
}
