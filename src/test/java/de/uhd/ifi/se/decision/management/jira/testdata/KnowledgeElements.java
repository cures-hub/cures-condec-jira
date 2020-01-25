package de.uhd.ifi.se.decision.management.jira.testdata;

import java.util.ArrayList;
import java.util.List;

import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.MutableIssue;

import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.impl.KnowledgeElementImpl;

public class KnowledgeElements {

	public static List<KnowledgeElement> elements = new ArrayList<KnowledgeElement>();

	public static List<KnowledgeElement> getTestKnowledgeElements() {
		if (elements == null || elements.isEmpty()) {
			elements = createKnowledgeElements();
		}
		return elements;
	}

	public static KnowledgeElement getTestKnowledgeElement() {
		return getTestKnowledgeElements().get(0);
	}

	private static List<KnowledgeElement> createKnowledgeElements() {
		List<KnowledgeElement> elements = new ArrayList<KnowledgeElement>();
		List<MutableIssue> jiraIssues = JiraIssues.getTestJiraIssues();

		for (Issue jiraIssue : jiraIssues) {
			elements.add(new KnowledgeElementImpl(jiraIssue));
		}

		return elements;
	}
}