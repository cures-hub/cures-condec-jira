package de.uhd.ifi.se.decision.management.jira.persistence.impl;

import java.util.List;

import com.atlassian.jira.user.ApplicationUser;

import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.DocumentationLocation;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeGraph;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeStatus;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.model.Link;
import de.uhd.ifi.se.decision.management.jira.model.LinkType;
import de.uhd.ifi.se.decision.management.jira.model.impl.DecisionKnowledgeElementImpl;
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
		List<DecisionKnowledgeElement> elements = getDefaultManagerForSingleLocation().getDecisionKnowledgeElements();
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
	public AbstractPersistenceManagerForSingleLocation getDefaultManagerForSingleLocation() {
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
		List<DecisionKnowledgeElement> elements = getDefaultManagerForSingleLocation()
				.getDecisionKnowledgeElements(type);
		elements.addAll(jiraIssueTextPersistenceManager.getDecisionKnowledgeElements(type));
		return elements;
	}

	@Override
	public AbstractPersistenceManagerForSingleLocation getManagerForSingleLocation(
			String documentationLocationIdentifier) {
		if (documentationLocationIdentifier == null) {
			return getDefaultManagerForSingleLocation();
		}
		DocumentationLocation documentationLocation = DocumentationLocation
				.getDocumentationLocationFromIdentifier(documentationLocationIdentifier);
		return getManagerForSingleLocation(documentationLocation);
	}

	@Override
	public AbstractPersistenceManagerForSingleLocation getManagerForSingleLocation(
			DocumentationLocation documentationLocation) {
		if (documentationLocation == null) {
			return getDefaultManagerForSingleLocation();
		}
		switch (documentationLocation) {
		case JIRAISSUE:
			return jiraIssuePersistenceManager;
		case ACTIVEOBJECT:
			return activeObjectPersistenceManager;
		case JIRAISSUETEXT:
			return jiraIssueTextPersistenceManager;
		default:
			return getDefaultManagerForSingleLocation();
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
	public boolean updateIssueStatus(DecisionKnowledgeElement existingElement, DecisionKnowledgeElement newElement,
			ApplicationUser user) {
		if (KnowledgeStatus.isIssueResolved(existingElement, newElement)) {
			existingElement.setStatus(KnowledgeStatus.RESOLVED);
			updateDecisionKnowledgeElement(existingElement, user);
			return true;
		}
		return false;
	}

	@Override
	public long insertLink(DecisionKnowledgeElement parentElement, DecisionKnowledgeElement childElement,
			ApplicationUser user) {
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

	@Override
	public long updateLink(DecisionKnowledgeElement element, KnowledgeType formerKnowledgeType, long idOfParentElement,
			String documentationLocationOfParentElement, ApplicationUser user) {

		if (LinkType.linkTypesAreEqual(formerKnowledgeType, element.getType()) || idOfParentElement == 0) {
			return -1;
		}

		LinkType formerLinkType = LinkType.getLinkTypeForKnowledgeType(formerKnowledgeType);
		LinkType linkType = LinkType.getLinkTypeForKnowledgeType(element.getType());

		DecisionKnowledgeElement parentElement = new DecisionKnowledgeElementImpl();
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
	public boolean deleteDecisionKnowledgeElement(DecisionKnowledgeElement element, ApplicationUser user) {
		AbstractPersistenceManagerForSingleLocation persistenceManager = KnowledgePersistenceManager
				.getManagerForSingleLocation(element);
		KnowledgeGraph.getOrCreate(projectKey).removeVertex(element);
		return persistenceManager.deleteDecisionKnowledgeElement(element, user);
	}

	@Override
	public boolean updateDecisionKnowledgeElement(DecisionKnowledgeElement element, ApplicationUser user) {
		AbstractPersistenceManagerForSingleLocation persistenceManager = KnowledgePersistenceManager
				.getManagerForSingleLocation(element);
		boolean isUpdated = persistenceManager.updateDecisionKnowledgeElement(element, user);
		if (isUpdated) {
			DecisionKnowledgeElement updatedElement = persistenceManager.getDecisionKnowledgeElement(element.getId());
			KnowledgeGraph.getOrCreate(projectKey).updateNode(updatedElement);
		}
		return isUpdated;
	}

	@Override
	public DecisionKnowledgeElement insertDecisionKnowledgeElement(DecisionKnowledgeElement element,
			ApplicationUser user, DecisionKnowledgeElement parentElement) {
		if (element.getStatus() == KnowledgeStatus.UNDEFINED) {
			element.setStatus(KnowledgeStatus.getDefaultStatus(element.getType()));
		}
		AbstractPersistenceManagerForSingleLocation persistenceManager = KnowledgePersistenceManager
				.getManagerForSingleLocation(element);
		DecisionKnowledgeElement elementWithId = persistenceManager.insertDecisionKnowledgeElement(element, user,
				parentElement);
		KnowledgeGraph.getOrCreate(projectKey).addVertex(elementWithId);
		return elementWithId;
	}

	@Override
	public DecisionKnowledgeElement insertDecisionKnowledgeElement(DecisionKnowledgeElement element,
			ApplicationUser user) {
		return insertDecisionKnowledgeElement(element, user, null);
	}

	@Override
	public DecisionKnowledgeElement getDecisionKnowledgeElement(long id, DocumentationLocation documentationLocation) {
		AbstractPersistenceManagerForSingleLocation persistenceManager = getManagerForSingleLocation(
				documentationLocation);
		DecisionKnowledgeElement element = persistenceManager.getDecisionKnowledgeElement(id);
		if (element == null) {
			return new DecisionKnowledgeElementImpl();
		}
		return element;
	}
}
