package de.uhd.ifi.se.decision.management.jira.persistence;

import java.util.List;

import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.text.PartOfJiraIssueText;

public class PersistenceInterfaceImpl implements PersistenceInterface {

	private String projectKey;
	private AbstractPersistenceManager defaultPersistenceManager;
	private AbstractPersistenceManager jiraIssueTextPersistenceManager;

	public PersistenceInterfaceImpl(String projectKey) {
		this.projectKey = projectKey;
		this.defaultPersistenceManager = AbstractPersistenceManager.getDefaultPersistenceStrategy(projectKey);
		this.jiraIssueTextPersistenceManager = new JiraIssueTextPersistenceManager(projectKey);
	}

	public List<DecisionKnowledgeElement> getDecisionKnowledgeElements() {
		List<DecisionKnowledgeElement> elements = defaultPersistenceManager.getDecisionKnowledgeElements();
		elements.addAll(jiraIssueTextPersistenceManager.getDecisionKnowledgeElements());

		// remove irrelevant sentences from graph
		elements.removeIf(e -> (e instanceof PartOfJiraIssueText && !((PartOfJiraIssueText) e).isRelevant()));
		return elements;
	}

	public String getProjectKey() {
		return projectKey;
	}

	public AbstractPersistenceManager getDefaultPersistenceManager() {
		return defaultPersistenceManager;
	}

	public AbstractPersistenceManager getJiraIssueTextPersistenceManager() {
		return jiraIssueTextPersistenceManager;
	}
}
