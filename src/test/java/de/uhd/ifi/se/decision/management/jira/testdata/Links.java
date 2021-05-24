package de.uhd.ifi.se.decision.management.jira.testdata;

import java.util.ArrayList;
import java.util.List;

import com.atlassian.jira.issue.link.IssueLink;
import com.atlassian.jira.project.Project;

import de.uhd.ifi.se.decision.management.jira.model.Link;

public class Links {

	public static Link getTestLink() {
		return getTestLinks().get(0);
	}

	public static List<Link> getTestLinks() {
		return createLinks(JiraProjects.getTestProject());
	}

	public static List<Link> createLinks(Project project) {
		List<Link> links = new ArrayList<Link>();
		List<IssueLink> jiraIssueLinks = JiraIssueLinks.getTestJiraIssueLinks();
		for (IssueLink issueLink : jiraIssueLinks) {
			links.add(new Link(issueLink));
		}
		return links;
	}
}
