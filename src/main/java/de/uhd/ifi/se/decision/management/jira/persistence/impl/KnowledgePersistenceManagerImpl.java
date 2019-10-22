package de.uhd.ifi.se.decision.management.jira.persistence.impl;

import java.util.List;

import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.DocumentationLocation;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.model.text.PartOfJiraIssueText;
import de.uhd.ifi.se.decision.management.jira.persistence.ConfigPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.persistence.PersistenceManager;

/**
 * Class that integates all available persistence managers for single
 * documentation locations for a given project.
 * 
 * @issue How can we integrate knowledge from different documentation locations?
 * @alternative Use the decorator design pattern to integrate persistence
 *              managers for different documentation locations!
 * @alternative Manually implement methods such as getDecisionKnowledgeElements!
 * 
 * @see AbstractPersistenceManagerForSingleLocation
 * @see JiraIssuePersistenceManager
 * @see JiraIssueTextPersistenceManager
 * @see ActiveObjectPersistenceManager
 */
public class PersistenceManagerImpl implements PersistenceManager {

	private String projectKey;
	private JiraIssuePersistenceManager jiraIssuePersistenceManager;
	private ActiveObjectPersistenceManager activeObjectPersistenceManager;
	private JiraIssueTextPersistenceManager jiraIssueTextPersistenceManager;

	public PersistenceManagerImpl(String projectKey) {
		this.projectKey = projectKey;
		this.jiraIssuePersistenceManager = new JiraIssuePersistenceManager(projectKey);
		this.activeObjectPersistenceManager = new ActiveObjectPersistenceManager(projectKey);
		this.jiraIssueTextPersistenceManager = new JiraIssueTextPersistenceManager(projectKey);
	}

	@Override
	public List<DecisionKnowledgeElement> getDecisionKnowledgeElements() {
		List<DecisionKnowledgeElement> elements = getDefaultPersistenceManager().getDecisionKnowledgeElements();
		elements.addAll(jiraIssueTextPersistenceManager.getDecisionKnowledgeElements());

		// remove irrelevant sentences from graph
		elements.removeIf(e -> (e instanceof PartOfJiraIssueText && !((PartOfJiraIssueText) e).isRelevant()));
		return elements;
	}

	@Override
	public String getProjectKey() {
		return projectKey;
	}

	@Override
	public AbstractPersistenceManagerForSingleLocation getDefaultPersistenceManager() {
		if (projectKey == null) {
			throw new IllegalArgumentException("The project key cannot be null.");
		}

		boolean isIssueStrategy = ConfigPersistenceManager.isIssueStrategy(projectKey);
		if (isIssueStrategy) {
			return jiraIssuePersistenceManager;
		}
		return activeObjectPersistenceManager;
	}

	@Override
	public JiraIssueTextPersistenceManager getJiraIssueTextManager() {
		return jiraIssueTextPersistenceManager;
	}

	@Override
	public JiraIssuePersistenceManager getJiraIssueManager() {
		return jiraIssuePersistenceManager;
	}

	@Override
	public ActiveObjectPersistenceManager getActiveObjectManager() {
		return activeObjectPersistenceManager;
	}

	@Override
	public List<DecisionKnowledgeElement> getDecisionKnowledgeElements(KnowledgeType type) {
		List<DecisionKnowledgeElement> elements = getDefaultPersistenceManager().getDecisionKnowledgeElements(type);
		elements.addAll(jiraIssueTextPersistenceManager.getDecisionKnowledgeElements(type));
		return elements;
	}

	@Override
	public AbstractPersistenceManagerForSingleLocation getPersistenceManager(
			DocumentationLocation documentationLocation) {
		if (documentationLocation == null) {
			return getDefaultPersistenceManager();
		}
		switch (documentationLocation) {
		case JIRAISSUE:
			return jiraIssuePersistenceManager;
		case ACTIVEOBJECT:
			return activeObjectPersistenceManager;
		case JIRAISSUETEXT:
			return jiraIssueTextPersistenceManager;
		default:
			return getDefaultPersistenceManager();
		}
	}

	@Override
	public AbstractPersistenceManagerForSingleLocation getPersistenceManager(String documentationLocationIdentifier) {
		if (documentationLocationIdentifier == null) {
			return getDefaultPersistenceManager();
		}
		DocumentationLocation documentationLocation = DocumentationLocation
				.getDocumentationLocationFromIdentifier(documentationLocationIdentifier);
		return getPersistenceManager(documentationLocation);
	}
}
