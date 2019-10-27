package de.uhd.ifi.se.decision.management.jira.persistence.impl;

import java.util.List;

import com.atlassian.jira.user.ApplicationUser;

import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.DocumentationLocation;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeGraph;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.model.Link;
import de.uhd.ifi.se.decision.management.jira.model.text.PartOfJiraIssueText;
import de.uhd.ifi.se.decision.management.jira.persistence.ConfigPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.persistence.KnowledgePersistenceManager;
import de.uhd.ifi.se.decision.management.jira.webhook.WebhookConnector;

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
public class KnowledgePersistenceManagerImpl implements KnowledgePersistenceManager {

	private String projectKey;
	private JiraIssuePersistenceManager jiraIssuePersistenceManager;
	private ActiveObjectPersistenceManager activeObjectPersistenceManager;
	private JiraIssueTextPersistenceManager jiraIssueTextPersistenceManager;

	public KnowledgePersistenceManagerImpl(String projectKey) {
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
	public AbstractPersistenceManagerForSingleLocation getPersistenceManager(String documentationLocationIdentifier) {
		if (documentationLocationIdentifier == null) {
			return getDefaultPersistenceManager();
		}
		DocumentationLocation documentationLocation = DocumentationLocation
				.getDocumentationLocationFromIdentifier(documentationLocationIdentifier);
		return getPersistenceManager(documentationLocation);
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
	public long insertLink(Link link, ApplicationUser user) {
		if (link.containsUnknownDocumentationLocation()) {
			link.setDefaultDocumentationLocation(projectKey);
		}

		long databaseId = 0;

		if (link.isIssueLink()) {
			databaseId = JiraIssuePersistenceManager.insertLink(link, user);
			if (databaseId > 0) {
				link.setId(databaseId);
				KnowledgeGraph.getOrCreate(projectKey).addEdge(link);
			}
			return databaseId;
		}

		if (ConfigPersistenceManager.isWebhookEnabled(projectKey)) {
			DecisionKnowledgeElement sourceElement = link.getSource();
			new WebhookConnector(projectKey).sendElementChanges(sourceElement);
		}
		databaseId = GenericLinkManager.insertLink(link, user);
		if (databaseId > 0) {
			link.setId(databaseId);
			KnowledgeGraph.getOrCreate(projectKey).addEdge(link);
		}
		return databaseId;
	}

	@Override
	public boolean deleteLink(Link link, ApplicationUser user) {
		if (link.containsUnknownDocumentationLocation()) {
			link.setDefaultDocumentationLocation(projectKey);
		}

		KnowledgeGraph.getOrCreate(projectKey).removeEdge(link);

		boolean isDeleted = false;
		if (link.isIssueLink()) {
			isDeleted = JiraIssuePersistenceManager.deleteLink(link, user);
			if (!isDeleted) {
				isDeleted = JiraIssuePersistenceManager.deleteLink(link.flip(), user);
			}
			return isDeleted;
		}
		isDeleted = GenericLinkManager.deleteLink(link);
		if (!isDeleted) {
			isDeleted = GenericLinkManager.deleteLink(link.flip());
		}

		if (isDeleted && ConfigPersistenceManager.isWebhookEnabled(projectKey)) {
			DecisionKnowledgeElement sourceElement = link.getSource();
			new WebhookConnector(projectKey).sendElementChanges(sourceElement);
		}

		return isDeleted;
	}
}
