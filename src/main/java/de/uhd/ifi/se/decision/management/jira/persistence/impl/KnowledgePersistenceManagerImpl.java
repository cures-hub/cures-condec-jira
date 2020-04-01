package de.uhd.ifi.se.decision.management.jira.persistence.impl;

import java.util.ArrayList;
import java.util.List;

import com.atlassian.jira.user.ApplicationUser;

import de.uhd.ifi.se.decision.management.jira.model.DocumentationLocation;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeGraph;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeStatus;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.model.Link;
import de.uhd.ifi.se.decision.management.jira.model.LinkType;
import de.uhd.ifi.se.decision.management.jira.model.impl.KnowledgeElementImpl;
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
 */
public class KnowledgePersistenceManagerImpl implements KnowledgePersistenceManager {

	private String projectKey;
	private JiraIssuePersistenceManager jiraIssuePersistenceManager;
	private JiraIssueTextPersistenceManager jiraIssueTextPersistenceManager;
	private List<AbstractPersistenceManagerForSingleLocation> activePersistenceManagersForSingleLocations;

	public KnowledgePersistenceManagerImpl(String projectKey) {
		this.projectKey = projectKey;
		this.jiraIssuePersistenceManager = new JiraIssuePersistenceManager(projectKey);
		this.jiraIssueTextPersistenceManager = new JiraIssueTextPersistenceManager(projectKey);
		this.activePersistenceManagersForSingleLocations = initActivePersistenceManagersForSinleLocations();
	}

	private List<AbstractPersistenceManagerForSingleLocation> initActivePersistenceManagersForSinleLocations() {
		List<AbstractPersistenceManagerForSingleLocation> activePersistenceManagersForSinleLocations = new ArrayList<AbstractPersistenceManagerForSingleLocation>();
		activePersistenceManagersForSinleLocations.add(jiraIssueTextPersistenceManager);
		activePersistenceManagersForSinleLocations.add(jiraIssuePersistenceManager);
		return activePersistenceManagersForSinleLocations;
	}

	@Override
	public List<KnowledgeElement> getDecisionKnowledgeElements() {
		List<KnowledgeElement> elements = new ArrayList<KnowledgeElement>();
		activePersistenceManagersForSingleLocations
				.forEach(manager -> elements.addAll(manager.getDecisionKnowledgeElements()));

		// remove irrelevant sentences from graph
		elements.removeIf(
				element -> (element instanceof PartOfJiraIssueText && !((PartOfJiraIssueText) element).isRelevant()));
		return elements;
	}

	@Override
	public String getProjectKey() {
		return projectKey;
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
	public AbstractPersistenceManagerForSingleLocation getManagerForSingleLocation(
			String documentationLocationIdentifier) {
		if (documentationLocationIdentifier == null) {
			return null;
		}
		DocumentationLocation documentationLocation = DocumentationLocation
				.getDocumentationLocationFromIdentifier(documentationLocationIdentifier);
		return getManagerForSingleLocation(documentationLocation);
	}

	@Override
	public AbstractPersistenceManagerForSingleLocation getManagerForSingleLocation(
			DocumentationLocation documentationLocation) {
		if (documentationLocation == null) {
			return null;
		}
		switch (documentationLocation) {
		case JIRAISSUE:
			return jiraIssuePersistenceManager;
		case JIRAISSUETEXT:
			return jiraIssueTextPersistenceManager;
		default:
			return null;
		}
	}

	@Override
	public long insertLink(Link link, ApplicationUser user) {
		if (link.containsUnknownDocumentationLocation()) {
			return 0;
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
			KnowledgeElement sourceElement = link.getSource();
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
	public boolean updateIssueStatus(KnowledgeElement parentElement, KnowledgeElement childElement,
			ApplicationUser user) {
		if (KnowledgeStatus.isIssueResolved(parentElement, childElement)) {
			parentElement.setStatus(KnowledgeStatus.RESOLVED);
			updateDecisionKnowledgeElement(parentElement, user);
			return true;
		}
		return false;
	}

	@Override
	public long insertLink(KnowledgeElement parentElement, KnowledgeElement childElement, ApplicationUser user) {
		if (parentElement == null || childElement == null) {
			return 0;
		}
		Link link = Link.instantiateDirectedLink(parentElement, childElement);
		long linkId = insertLink(link, user);
		return linkId;
	}

	@Override
	public boolean deleteLink(Link link, ApplicationUser user) {
		if (link.containsUnknownDocumentationLocation()) {
			return false;
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
			KnowledgeElement sourceElement = link.getSource();
			new WebhookConnector(projectKey).sendElementChanges(sourceElement);
		}

		return isDeleted;
	}

	@Override
	public long updateLink(KnowledgeElement element, KnowledgeType formerKnowledgeType, long idOfParentElement,
			String documentationLocationOfParentElement, ApplicationUser user) {

		if (LinkType.linkTypesAreEqual(formerKnowledgeType, element.getType()) || idOfParentElement == 0) {
			return -1;
		}

		LinkType formerLinkType = LinkType.getLinkTypeForKnowledgeType(formerKnowledgeType);
		LinkType linkType = LinkType.getLinkTypeForKnowledgeType(element.getType());

		KnowledgeElement parentElement = new KnowledgeElementImpl();
		parentElement.setId(idOfParentElement);
		parentElement.setDocumentationLocation(documentationLocationOfParentElement);
		parentElement.setProject(projectKey);

		Link formerLink = Link.instantiateDirectedLink(parentElement, element, formerLinkType);
		if (!this.deleteLink(formerLink, user)) {
			return 0;
		}
		KnowledgeGraph.getOrCreate(projectKey).removeEdge(formerLink);

		Link link = Link.instantiateDirectedLink(parentElement, element, linkType);
		KnowledgeGraph.getOrCreate(projectKey).addEdge(link);

		return this.insertLink(link, user);
	}

	@Override
	public boolean deleteDecisionKnowledgeElement(KnowledgeElement element, ApplicationUser user) {
		AbstractPersistenceManagerForSingleLocation persistenceManager = KnowledgePersistenceManager
				.getManagerForSingleLocation(element);
		KnowledgeGraph.getOrCreate(projectKey).removeVertex(element);
		return persistenceManager.deleteDecisionKnowledgeElement(element, user);
	}

	@Override
	public boolean updateDecisionKnowledgeElement(KnowledgeElement element, ApplicationUser user) {
		AbstractPersistenceManagerForSingleLocation persistenceManager = KnowledgePersistenceManager
				.getManagerForSingleLocation(element);
		boolean isUpdated = persistenceManager.updateDecisionKnowledgeElement(element, user);
		if (isUpdated) {
			KnowledgeElement updatedElement = persistenceManager.getDecisionKnowledgeElement(element.getId());
			KnowledgeGraph.getOrCreate(projectKey).updateElement(updatedElement);
		}

		if (isUpdated && ConfigPersistenceManager.isWebhookEnabled(projectKey)) {
			new WebhookConnector(projectKey).sendElementChanges(element);
		}

		return isUpdated;
	}

	@Override
	public KnowledgeElement insertDecisionKnowledgeElement(KnowledgeElement element, ApplicationUser user,
			KnowledgeElement parentElement) {
		if (element.getStatus() == KnowledgeStatus.UNDEFINED) {
			element.setStatus(KnowledgeStatus.getDefaultStatus(element.getType()));
		}
		AbstractPersistenceManagerForSingleLocation persistenceManager = KnowledgePersistenceManager
				.getManagerForSingleLocation(element);
		KnowledgeElement elementWithId = persistenceManager.insertDecisionKnowledgeElement(element, user,
				parentElement);
		KnowledgeGraph.getOrCreate(projectKey).addVertex(elementWithId);

		if (elementWithId != null && ConfigPersistenceManager.isWebhookEnabled(projectKey)) {
			new WebhookConnector(projectKey).sendElementChanges(element);
		}

		return elementWithId;
	}

	@Override
	public KnowledgeElement insertDecisionKnowledgeElement(KnowledgeElement element, ApplicationUser user) {
		return insertDecisionKnowledgeElement(element, user, null);
	}

	@Override
	public KnowledgeElement getDecisionKnowledgeElement(long id, DocumentationLocation documentationLocation) {
		AbstractPersistenceManagerForSingleLocation persistenceManager = getManagerForSingleLocation(
				documentationLocation);
		if (persistenceManager == null) {
			return null;
		}
		return persistenceManager.getDecisionKnowledgeElement(id);
	}

	@Override
	public KnowledgeElement getDecisionKnowledgeElement(long id, String documentationLocationIdentifier) {
		DocumentationLocation documentationLocation = DocumentationLocation
				.getDocumentationLocationFromIdentifier(documentationLocationIdentifier);
		return getDecisionKnowledgeElement(id, documentationLocation);
	}

	@Override
	public List<Link> getLinks(KnowledgeElement element) {
		List<Link> links = new ArrayList<Link>();
		activePersistenceManagersForSingleLocations.forEach(manager -> links.addAll(manager.getInwardLinks(element)));
		activePersistenceManagersForSingleLocations.forEach(manager -> links.addAll(manager.getOutwardLinks(element)));
		return links;
	}
}
