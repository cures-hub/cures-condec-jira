package de.uhd.ifi.se.decision.management.jira.persistence.impl;

import java.util.List;

import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.text.PartOfJiraIssueText;
import de.uhd.ifi.se.decision.management.jira.persistence.ConfigPersistenceManager;

public class IntegratedPersistenceManager {

	private String projectKey;
	private JiraIssuePersistenceManager jiraIssuePersistenceManager;
	private ActiveObjectPersistenceManager activeObjectPersistenceManager;
	public JiraIssuePersistenceManager getJiraIssuePersistenceManager() {
		return jiraIssuePersistenceManager;
	}

	public ActiveObjectPersistenceManager getActiveObjectPersistenceManager() {
		return activeObjectPersistenceManager;
	}

	private JiraIssueTextPersistenceManager jiraIssueTextPersistenceManager;

	public IntegratedPersistenceManager(String projectKey) {
		this.projectKey = projectKey;
		this.jiraIssuePersistenceManager = new JiraIssuePersistenceManager(projectKey);
		this.activeObjectPersistenceManager = new ActiveObjectPersistenceManager(projectKey);
		this.jiraIssueTextPersistenceManager = new JiraIssueTextPersistenceManager(projectKey);
	}

	public List<DecisionKnowledgeElement> getDecisionKnowledgeElements() {
		List<DecisionKnowledgeElement> elements = getDefaultPersistenceManager().getDecisionKnowledgeElements();
		elements.addAll(jiraIssueTextPersistenceManager.getDecisionKnowledgeElements());

		// remove irrelevant sentences from graph
		elements.removeIf(e -> (e instanceof PartOfJiraIssueText && !((PartOfJiraIssueText) e).isRelevant()));
		return elements;
	}

	public String getProjectKey() {
		return projectKey;
	}

	public AbstractPersistenceManager getDefaultPersistenceManager() {
		if (projectKey == null) {
			throw new IllegalArgumentException("The project key cannot be null.");
		}

		boolean isIssueStrategy = ConfigPersistenceManager.isIssueStrategy(projectKey);
		if (isIssueStrategy) {
			return jiraIssuePersistenceManager;
		}
		return activeObjectPersistenceManager;
	}

	public JiraIssueTextPersistenceManager getJiraIssueTextPersistenceManager() {
		return jiraIssueTextPersistenceManager;
	}
}
