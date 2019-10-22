package de.uhd.ifi.se.decision.management.jira.persistence.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.jira.user.ApplicationUser;

import de.uhd.ifi.se.decision.management.jira.ComponentGetter;
import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.DocumentationLocation;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeStatus;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.model.Link;
import de.uhd.ifi.se.decision.management.jira.model.impl.DecisionKnowledgeElementImpl;
import de.uhd.ifi.se.decision.management.jira.model.impl.LinkImpl;
import de.uhd.ifi.se.decision.management.jira.persistence.PersistenceManager;
import de.uhd.ifi.se.decision.management.jira.persistence.tables.DecisionKnowledgeElementInDatabase;
import de.uhd.ifi.se.decision.management.jira.persistence.tables.LinkInDatabase;
import de.uhd.ifi.se.decision.management.jira.webhook.WebhookConnector;
import net.java.ao.Query;

/**
 * Extends the abstract class
 * {@link AbstractPersistenceManagerForSingleLocation}. Uses object relational
 * mapping with the help of the active object framework to store decision
 * knowledge.
 *
 * @see AbstractPersistenceManagerForSingleLocation
 */
@JsonAutoDetect
public class ActiveObjectPersistenceManager extends AbstractPersistenceManagerForSingleLocation {
	private static final Logger LOGGER = LoggerFactory.getLogger(ActiveObjectPersistenceManager.class);
	private static final ActiveObjects ACTIVE_OBJECTS = ComponentGetter.getActiveObjects();
	private static final String PREFIX = DocumentationLocation.getIdentifier(DocumentationLocation.ACTIVEOBJECT);

	private static void setParameters(DecisionKnowledgeElement element,
			DecisionKnowledgeElementInDatabase databaseEntry) {
		String summary = element.getSummary();
		if (summary != null) {
			databaseEntry.setSummary(summary);
		}
		String description = element.getDescription();
		if (description != null) {
			databaseEntry.setDescription(description);
		}
		databaseEntry.setType(element.getType().replaceProAndConWithArgument().toString());
	}

	public ActiveObjectPersistenceManager(String projectKey) {
		this.projectKey = projectKey;
		this.documentationLocation = DocumentationLocation.ACTIVEOBJECT;
	}

	@Override
	public boolean deleteDecisionKnowledgeElement(DecisionKnowledgeElement element, ApplicationUser user) {
		if (element == null) {
			return false;
		}
		new WebhookConnector(projectKey).sendElementChanges(element);
		return deleteDecisionKnowledgeElement(element.getId(), user);
	}

	@Override
	public boolean deleteDecisionKnowledgeElement(long id, ApplicationUser user) {
		boolean isDeleted = false;
		for (DecisionKnowledgeElementInDatabase databaseEntry : ACTIVE_OBJECTS
				.find(DecisionKnowledgeElementInDatabase.class, Query.select().where("ID = ?", id))) {
			KnowledgeStatusManager.deleteStatus(new DecisionKnowledgeElementImpl(databaseEntry));
			GenericLinkManager.deleteLinksForElement(id, DocumentationLocation.ACTIVEOBJECT);
			isDeleted = DecisionKnowledgeElementInDatabase.deleteElement(databaseEntry);
		}
		return isDeleted;
	}

	@Override
	public DecisionKnowledgeElement getDecisionKnowledgeElement(long id) {
		DecisionKnowledgeElement element = null;
		for (DecisionKnowledgeElementInDatabase databaseEntry : ACTIVE_OBJECTS
				.find(DecisionKnowledgeElementInDatabase.class, Query.select().where("ID = ?", id))) {
			element = new DecisionKnowledgeElementImpl(databaseEntry);
		}
		return element;
	}

	@Override
	public DecisionKnowledgeElement getDecisionKnowledgeElement(String key) {
		// Split key into project key and id
		String idAsString = null;
		try {
			idAsString = key.split("-")[1];
		} catch (ArrayIndexOutOfBoundsException e) {
			LOGGER.error("Key cannot be split into the project key and id. Message: " + e.getMessage());
		}
		if (idAsString != null) {
			long id = Long.parseLong(idAsString);
			DecisionKnowledgeElement element = getDecisionKnowledgeElement(id);
			if (element != null) {
				return element;
			}
		}
		LOGGER.error("No decision knowledge element with " + key + " could be found.");
		return null;
	}

	@Override
	public List<DecisionKnowledgeElement> getDecisionKnowledgeElements() {
		List<DecisionKnowledgeElement> decisionKnowledgeElements = new ArrayList<DecisionKnowledgeElement>();
		DecisionKnowledgeElementInDatabase[] databaseEntries = ACTIVE_OBJECTS
				.find(DecisionKnowledgeElementInDatabase.class, Query.select().where("PROJECT_KEY = ?", projectKey));
		for (DecisionKnowledgeElementInDatabase databaseEntry : databaseEntries) {
			decisionKnowledgeElements.add(new DecisionKnowledgeElementImpl(databaseEntry));
		}
		return decisionKnowledgeElements;
	}

	@Override
	public List<DecisionKnowledgeElement> getElementsLinkedWithInwardLinks(
			DecisionKnowledgeElement decisionKnowledgeElement) {
		List<Link> inwardLinks = this.getInwardLinks(decisionKnowledgeElement);
		List<DecisionKnowledgeElement> sourceElements = new ArrayList<DecisionKnowledgeElement>();
		for (Link link : inwardLinks) {
			DecisionKnowledgeElementInDatabase[] entityList = ACTIVE_OBJECTS.find(
					DecisionKnowledgeElementInDatabase.class,
					Query.select().where("ID = ?", link.getSourceElement().getId()));
			if (entityList.length == 1) {
				sourceElements.add(new DecisionKnowledgeElementImpl(entityList[0]));
			}
		}
		return sourceElements;
	}

	@Override
	public List<DecisionKnowledgeElement> getElementsLinkedWithOutwardLinks(
			DecisionKnowledgeElement decisionKnowledgeElement) {
		List<Link> outwardLinks = this.getOutwardLinks(decisionKnowledgeElement);
		List<DecisionKnowledgeElement> destinationElements = new ArrayList<DecisionKnowledgeElement>();

		ACTIVE_OBJECTS.find(LinkInDatabase.class);
		for (Link link : outwardLinks) {
			DecisionKnowledgeElementInDatabase[] entityList = ACTIVE_OBJECTS.find(
					DecisionKnowledgeElementInDatabase.class,
					Query.select().where("ID = ?", link.getDestinationElement().getId()));
			if (entityList.length == 1) {
				destinationElements.add(new DecisionKnowledgeElementImpl(entityList[0]));
			}
		}
		return destinationElements;
	}

	@Override
	public List<Link> getInwardLinks(DecisionKnowledgeElement element) {
		List<Link> inwardLinks = new ArrayList<Link>();
		LinkInDatabase[] links = ACTIVE_OBJECTS.find(LinkInDatabase.class, Query.select()
				.where("DESTINATION_ID = ? AND DEST_DOCUMENTATION_LOCATION = ?", element.getId(), PREFIX));
		for (LinkInDatabase link : links) {
			Link inwardLink = new LinkImpl(link);
			inwardLink.setDestinationElement(element);
			long elementId = link.getSourceId();
			inwardLink.setSourceElement(this.getDecisionKnowledgeElement(elementId));
			inwardLinks.add(inwardLink);
		}
		return inwardLinks;
	}

	@Override
	public List<Link> getOutwardLinks(DecisionKnowledgeElement element) {
		List<Link> outwardLinks = new ArrayList<Link>();
		LinkInDatabase[] links = ACTIVE_OBJECTS.find(LinkInDatabase.class,
				Query.select().where("SOURCE_ID = ? AND SOURCE_DOCUMENTATION_LOCATION = ?", element.getId(), PREFIX));
		for (LinkInDatabase link : links) {
			Link outwardLink = new LinkImpl(link);
			outwardLink.setSourceElement(element);
			long elementId = link.getDestinationId();
			outwardLink.setDestinationElement(this.getDecisionKnowledgeElement(elementId));
			outwardLinks.add(outwardLink);
		}
		return outwardLinks;
	}

	@Override
	public DecisionKnowledgeElement insertDecisionKnowledgeElement(DecisionKnowledgeElement element,
			ApplicationUser user) {
		DecisionKnowledgeElementInDatabase databaseEntry = ACTIVE_OBJECTS
				.create(DecisionKnowledgeElementInDatabase.class);
		databaseEntry
				.setKey(element.getProject().getProjectKey().toUpperCase(Locale.ENGLISH) + "-" + databaseEntry.getId());
		setParameters(element, databaseEntry);
		databaseEntry.setProjectKey(element.getProject().getProjectKey());
		databaseEntry.save();
		element.setId(databaseEntry.getId());
		element.setKey(databaseEntry.getKey());
		new WebhookConnector(projectKey).sendElementChanges(element);
		element.setDocumentationLocation(DocumentationLocation.ACTIVEOBJECT);
		PersistenceManager.insertStatus(element);
		return element;
	}

	@Override
	public boolean updateDecisionKnowledgeElement(DecisionKnowledgeElement element, ApplicationUser user) {
		for (DecisionKnowledgeElementInDatabase databaseEntry : ACTIVE_OBJECTS
				.find(DecisionKnowledgeElementInDatabase.class)) {
			if (databaseEntry.getId() == element.getId()) {
				if (KnowledgeType.getKnowledgeType(databaseEntry.getType()).equals(KnowledgeType.DECISION)
						&& element.getType().equals(KnowledgeType.ALTERNATIVE)) {
					KnowledgeStatusManager.setStatusForElement(element, KnowledgeStatus.REJECTED);
				}
				if (KnowledgeType.getKnowledgeType(databaseEntry.getType()).equals(KnowledgeType.ALTERNATIVE)
						&& element.getType().equals(KnowledgeType.DECISION)) {
					KnowledgeStatusManager.deleteStatus(element);
				}
				setParameters(element, databaseEntry);
				databaseEntry.save();
				new WebhookConnector(projectKey).sendElementChanges(element);
				return true;
			}
		}
		LOGGER.error("Updating of decision knowledge element in database failed.");
		return false;
	}

	@Override
	public boolean updateDecisionKnowledgeElementWithoutStatusChange(DecisionKnowledgeElement element,
			ApplicationUser user) {
		for (DecisionKnowledgeElementInDatabase databaseEntry : ACTIVE_OBJECTS
				.find(DecisionKnowledgeElementInDatabase.class)) {
			if (databaseEntry.getId() == element.getId()) {
				setParameters(element, databaseEntry);
				databaseEntry.save();
				new WebhookConnector(projectKey).sendElementChanges(element);
				return true;
			}
		}
		LOGGER.error("Updating of decision knowledge element in database failed.");
		return false;
	}

	@Override
	public ApplicationUser getCreator(DecisionKnowledgeElement element) {
		return null;
	}
}