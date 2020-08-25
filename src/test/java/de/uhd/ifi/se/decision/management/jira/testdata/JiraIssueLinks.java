package de.uhd.ifi.se.decision.management.jira.testdata;

import java.util.ArrayList;
import java.util.List;

import com.atlassian.jira.issue.link.IssueLink;

import de.uhd.ifi.se.decision.management.jira.mocks.MockIssueLink;

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

		// link between WI and issue
		issueLink = createJiraIssueLink(1, 2, 12);
		issueLinks.add(issueLink);
		issueLink = createJiraIssueLink(1, 12, 13);
		issueLinks.add(issueLink);

		// link between issue and and alternative/decision
		issueLink = createJiraIssueLink(12, 4, 14);
		issueLinks.add(issueLink);
		// issueLink = createJiraIssueLink(12, 3, 15);
		// issueLinks.add(issueLink);
		issueLink = createJiraIssueLink(3, 2, 5);
		issueLinks.add(issueLink);

		// link between argument and criteria
		issueLink = createJiraIssueLink(5, 6, 16);
		issueLinks.add(issueLink);

		return issueLinks;
	}

	private static IssueLink createJiraIssueLink(long sourceIssueId, long destIssueId, long issueLinkId) {
		return new MockIssueLink(sourceIssueId, destIssueId, issueLinkId);

	}
}
