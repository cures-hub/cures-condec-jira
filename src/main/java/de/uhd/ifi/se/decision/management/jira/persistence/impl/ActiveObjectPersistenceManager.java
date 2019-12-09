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
import de.uhd.ifi.se.decision.management.jira.persistence.KnowledgePersistenceManager;
import de.uhd.ifi.se.decision.management.jira.persistence.tables.DecisionKnowledgeElementInDatabase;
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
		databaseEntry.setStatus(element.getStatusAsString());
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
	public List<Link> getInwardLinks(DecisionKnowledgeElement element) {
		return GenericLinkManager.getInwardLinks(element);
	}

	@Override
	public List<Link> getOutwardLinks(DecisionKnowledgeElement element) {
		return GenericLinkManager.getOutwardLinks(element);
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
		KnowledgePersistenceManager.insertStatus(element);
		// KnowledgePersistenceManager.updateGraphNode(element);
		return element;
	}

	@Override
	public boolean updateDecisionKnowledgeElement(DecisionKnowledgeElement element, ApplicationUser user) {
		for (DecisionKnowledgeElementInDatabase databaseEntry : ACTIVE_OBJECTS
				.find(DecisionKnowledgeElementInDatabase.class)) {
			if (databaseEntry.getId() == element.getId()) {
				if (KnowledgeType.getKnowledgeType(databaseEntry.getType()).equals(KnowledgeType.DECISION)
						&& element.getType().equals(KnowledgeType.ALTERNATIVE)) {
					StatusPersistenceManager.setStatusForElement(element, KnowledgeStatus.REJECTED);
				}
				if (KnowledgeType.getKnowledgeType(databaseEntry.getType()).equals(KnowledgeType.ALTERNATIVE)
						&& element.getType().equals(KnowledgeType.DECISION)) {
					StatusPersistenceManager.setStatusForElement(element, KnowledgeStatus.DECIDED);
				}
				setParameters(element, databaseEntry);
				databaseEntry.save();
				new WebhookConnector(projectKey).sendElementChanges(element);
				// KnowledgePersistenceManager.updateGraphNode(element);
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
				// KnowledgePersistenceManager.updateGraphNode(element);
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