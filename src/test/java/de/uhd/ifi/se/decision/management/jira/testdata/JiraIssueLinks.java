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

		// link between issue and decision
		IssueLink issueLink = createJiraIssueLink(2, 4, 2);
		issueLinks.add(issueLink);
		issueLink = createJiraIssueLink(12, 4, 14);
		issueLinks.add(issueLink);

		// link between decision and argument
		issueLink = createJiraIssueLink(5, 4, 6);
		issueLinks.add(issueLink);

		// links between WI and issue
		issueLink = createJiraIssueLink(1, 2, 12);
		issueLinks.add(issueLink);
		issueLink = createJiraIssueLink(1, 12, 13);
		issueLinks.add(issueLink);

		// link between issue and and alternative
		issueLink = createJiraIssueLink(2, 3, 5);
		issueLinks.add(issueLink);

		// link between argument and criteria
		issueLink = createJiraIssueLink(5, 6, 16);
		issueLinks.add(issueLink);

		// link between alternative and WI (wrong link, WI should be linked to issue)
		issueLink = createJiraIssueLink(14, 3, 8);
		issueLinks.add(issueLink);
		issueLink = createJiraIssueLink(3, 1, 9);
		issueLinks.add(issueLink);

		// link between argument and WI (wrong link: WI should be linked to issue)
		issueLink = createJiraIssueLink(1, 5, 10);
		issueLinks.add(issueLink);

		// link between WIs (wrong link, WI should be linked to issue and requirement)
		issueLink = createJiraIssueLink(1, 14, 11);
		issueLinks.add(issueLink);
		issueLink = createJiraIssueLink(14, 30, 11);
		issueLinks.add(issueLink);

		// link between decision and WI (wrong link, WI should be linked to issue)
		issueLink = createJiraIssueLink(4, 30, 4);
		issueLinks.add(issueLink);
		issueLink = createJiraIssueLink(4, 14, 7);
		issueLinks.add(issueLink);

		return issueLinks;
	}

	private static IssueLink createJiraIssueLink(long sourceIssueId, long destIssueId, long issueLinkId) {
		return new MockIssueLink(sourceIssueId, destIssueId, issueLinkId);

	}
}
