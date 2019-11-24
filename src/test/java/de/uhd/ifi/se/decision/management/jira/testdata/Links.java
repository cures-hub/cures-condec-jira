package de.uhd.ifi.se.decision.management.jira.testdata;

import java.util.ArrayList;
import java.util.List;

import com.atlassian.jira.issue.link.IssueLink;
import com.atlassian.jira.project.Project;

import de.uhd.ifi.se.decision.management.jira.model.Link;
import de.uhd.ifi.se.decision.management.jira.model.impl.LinkImpl;

public class Links {

	private static List<Link> links;

	public static List<Link> getTestLinks() {
		if (links == null || links.isEmpty()) {
			links = createLinks(JiraProjects.TEST.createJiraProject(1));
		}
		return links;
	}

	public static List<Link> createLinks(Project project) {
		List<Link> links = new ArrayList<Link>();
		List<IssueLink> jiraIssueLinks = JiraIssueLinks.getTestJiraIssueLinks();
		for (IssueLink issueLink : jiraIssueLinks) {
			links.add(new LinkImpl(issueLink));
		}
		return links;
	}
}
