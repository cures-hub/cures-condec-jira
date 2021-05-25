package de.uhd.ifi.se.decision.management.jira.testdata;

import java.util.ArrayList;
import java.util.List;

import com.atlassian.jira.issue.Issue;

import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;

public class KnowledgeElements {

	public static List<KnowledgeElement> elements = new ArrayList<>();

	public static List<KnowledgeElement> getTestKnowledgeElements() {
		if (elements == null || elements.isEmpty()) {
			elements = createKnowledgeElements();
		}
		return elements;
	}

	public static KnowledgeElement getTestKnowledgeElement() {
		return getTestKnowledgeElements().get(0);
	}

	public static KnowledgeElement getSolvedDecisionProblem() {
		return getTestKnowledgeElements().get(4);
	}

	public static KnowledgeElement getUnsolvedDecisionProblem() {
		return getTestKnowledgeElements().get(6);
	}

	public static KnowledgeElement getAlternative() {
		return getTestKnowledgeElements().get(7);
	}

	public static KnowledgeElement getProArgument() {
		return getTestKnowledgeElements().get(11);
	}

	public static KnowledgeElement getDecision() {
		return getTestKnowledgeElements().get(10);
	}

	public static KnowledgeElement getOtherWorkItem() {
		return getTestKnowledgeElements().get(3);
	}

	private static List<KnowledgeElement> createKnowledgeElements() {
		List<KnowledgeElement> elements = new ArrayList<>();
		List<Issue> jiraIssues = JiraIssues.getTestJiraIssues();
		for (Issue jiraIssue : jiraIssues) {
			elements.add(new KnowledgeElement(jiraIssue));
		}
		return elements;
	}
}