package de.uhd.ifi.se.decision.management.jira.persistence;

import java.util.List;

import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.text.PartOfJiraIssueText;

public interface PersistenceInterface {

	public static List<DecisionKnowledgeElement> getDecisionKnowledgeElements(String projectKey) {
		AbstractPersistenceManager strategy = AbstractPersistenceManager.getDefaultPersistenceStrategy(projectKey);
		List<DecisionKnowledgeElement> elements = strategy.getDecisionKnowledgeElements();
		AbstractPersistenceManager jiraIssueCommentPersistenceManager = new JiraIssueTextPersistenceManager(projectKey);

		elements.addAll(jiraIssueCommentPersistenceManager.getDecisionKnowledgeElements());

		// remove irrelevant sentences from graph
		elements.removeIf(e -> (e instanceof PartOfJiraIssueText && !((PartOfJiraIssueText) e).isRelevant()));
		return elements;
	}

}
