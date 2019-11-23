package de.uhd.ifi.se.decision.management.jira.persistence.impl;

import java.util.ArrayList;
import java.util.List;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.jira.issue.link.IssueLink;
import com.atlassian.jira.issue.link.IssueLinkManager;
import com.atlassian.jira.user.ApplicationUser;

import de.uhd.ifi.se.decision.management.jira.ComponentGetter;
import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.DocumentationLocation;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeGraph;
import de.uhd.ifi.se.decision.management.jira.model.Link;
import de.uhd.ifi.se.decision.management.jira.model.impl.LinkImpl;
import de.uhd.ifi.se.decision.management.jira.persistence.KnowledgePersistenceManager;
import de.uhd.ifi.se.decision.management.jira.persistence.tables.LinkInDatabase;
import net.java.ao.Query;

/**
 * Class responsible for links (=edges) between all kinds of nodes in the
 * {@link KnowledgeGraph}, except of Jira issue links. Jira {@link IssueLink}s
 * are stored in the internal database of Jira and managed by the Jira
 * {@link IssueLinkManager}. If you are not sure whether your link is a Jira
 * issue link or not, use the methods of the {@link KnowledgePersistenceManager}
 * interface.
 * 
 * @see KnowledgePersistenceManager
 * @see LinkInDatabase
 * @see JiraIssuePersistenceManager
 */
public class GenericLinkManager {

	private static final ActiveObjects ACTIVE_OBJECTS = ComponentGetter.getActiveObjects();

	public static void clearInvalidLinks() {
		LinkInDatabase[] linksInDatabase = ACTIVE_OBJECTS.find(LinkInDatabase.class);
		for (LinkInDatabase databaseEntry : linksInDatabase) {
			Link link = new LinkImpl(databaseEntry);
			if (!link.isValid()) {
				LinkInDatabase.deleteLink(databaseEntry);
			}
		}
	}

	/**
	 * Deletes a link (=edge) between all kinds of nodes in the
	 * {@link KnowledgeGraph}, except of Jira issue links. If you want to delete a
	 * Jira {@link IssueLink}, use the method
	 * {@link JiraIssuePersistenceManager#deleteLink(Link, ApplicationUser)}
	 * instead. If you are not sure what kind of link it is, use
	 * {@link KnowledgePersistenceManager#deleteLink(Link, ApplicationUser)}.
	 * 
	 * @param link
	 *            (=edge) between a source and a destination decision knowledge
	 *            element as a {@link Link} object. The link must not be Jira
	 *            {@link IssueLink}.
	 * @return true if deletion was successful, false otherwise.
	 */
	public static boolean deleteLink(Link link) {
		if (link == null) {
			return false;
		}
		boolean isDeleted = false;
		for (LinkInDatabase linkInDatabase : ACTIVE_OBJECTS.find(LinkInDatabase.class)) {
			if (link.equals(linkInDatabase)) {
				isDeleted = LinkInDatabase.deleteLink(linkInDatabase);
			}
		}
		return isDeleted;
	}

	/**
	 * Deletes all incoming and outgoing links (=edges) for a specific element
	 * (=node) in the {@link KnowledgeGraph} except for Jira {@link IssueLink}s. If
	 * you want to delete a Jira {@link IssueLink}, use the method
	 * {@link JiraIssuePersistenceManager#deleteLink(Link, ApplicationUser)}. If you
	 * are not sure what kind of link it is, use
	 * {@link KnowledgePersistenceManager#deleteLink(Link, ApplicationUser)}.
	 * 
	 * @param elementId
	 *            id of the node.
	 * @param documentationLocation
	 *            {@link DocumentationLocation} of the knowledge element.
	 * @return true if at least one link was deleted, false if no link was deleted.
	 * @see DecisionKnowledgeElement
	 */
	public static boolean deleteLinksForElement(long elementId, DocumentationLocation documentationLocation) {
		if (elementId <= 0 || documentationLocation == null) {
			return false;
		}
		boolean isLinkDeleted = false;
		List<Link> linksForElement = getLinksForElement(elementId, documentationLocation);
		for (Link link : linksForElement) {
			isLinkDeleted = deleteLink(link);
			KnowledgeGraph.getOrCreate(link.getSource().getProject()).removeEdge(link);
		}
		return isLinkDeleted;
	}

	/**
	 * Returns all links (=edges) between all kinds of nodes in the
	 * {@link KnowledgeGraph}, except of Jira issue links. If you want to get the
	 * Jira {@link IssueLink}s only, use
	 * {@link JiraIssuePersistenceManager#getLinks(DecisionKnowledgeElement)}.
	 * 
	 * @param element
	 *            node in the {@link KnowledgeGraph}.
	 * @return list of {@link} objects, does not contain Jira {@link IssueLink}s.
	 *         (=edges) between all kinds of nodes in the {@link KnowledgeGraph},
	 *         except of Jira issue links. If you want to get the Jira
	 *         {@link IssueLink}s only, use
	 *         {@link JiraIssuePersistenceManager#getLinks(DecisionKnowledgeElement)}.
	 * 
	 * @see DecisionKnowledgeElement
	 */
	public static List<Link> getLinksForElement(DecisionKnowledgeElement element) {
		if (element == null) {
			return new ArrayList<Link>();
		}
		return getLinksForElement(element.getId(), element.getDocumentationLocation());
	}

	/**
	 * Returns all links (=edges) between all kinds of nodes in the
	 * {@link KnowledgeGraph}, except of Jira issue links. If you want to get the
	 * Jira {@link IssueLink}s only, use
	 * {@link JiraIssuePersistenceManager#getLinks(DecisionKnowledgeElement)}.
	 * 
	 * @param elementId
	 *            id of the node.
	 * @param documentationLocation
	 *            {@link DocumentationLocation} of the knowledge element.
	 * @return list of {@link} objects, does not contain Jira {@link IssueLink}s.
	 *         (=edges) between all kinds of nodes in the {@link KnowledgeGraph},
	 *         except of Jira issue links.
	 * 
	 * @see DecisionKnowledgeElement
	 */
	public static List<Link> getLinksForElement(long elementId, DocumentationLocation documentationLocation) {
		List<Link> links = new ArrayList<Link>();
		if (elementId <= 0 || documentationLocation == null) {
			return links;
		}
		String identifier = documentationLocation.getIdentifier();
		LinkInDatabase[] linksInDatabase = ACTIVE_OBJECTS.find(LinkInDatabase.class, Query.select().where(
				"DESTINATION_ID = ? AND DEST_DOCUMENTATION_LOCATION = ? OR SOURCE_ID = ? AND SOURCE_DOCUMENTATION_LOCATION = ?",
				elementId, identifier, elementId, identifier));
		for (LinkInDatabase linkInDatabase : linksInDatabase) {
			Link link = new LinkImpl(linkInDatabase);
			links.add(link);
		}
		return links;
	}

	/**
	 * Returns all outgoing links (=edges) from a node in the
	 * {@link KnowledgeGraph}, except of Jira issue links. If you want to get the
	 * Jira {@link IssueLink}s only, use
	 * {@link JiraIssuePersistenceManager#getOutwardLinks(DecisionKnowledgeElement)}.
	 * 
	 * @param element
	 *            node in the {@link KnowledgeGraph}.
	 * @return list of {@link} objects, does not contain Jira {@link IssueLink}s.
	 *         (=edges) between all kinds of nodes in the {@link KnowledgeGraph},
	 *         except of Jira issue links. If you want to get the Jira
	 *         {@link IssueLink}s only, use
	 *         {@link JiraIssuePersistenceManager#getLinks(DecisionKnowledgeElement)}.
	 * 
	 * @see DecisionKnowledgeElement
	 */
	public static List<Link> getOutwardLinks(DecisionKnowledgeElement element) {
		String identifier = element.getDocumentationLocation().getIdentifier();
		LinkInDatabase[] linksInDatabase = ACTIVE_OBJECTS.find(LinkInDatabase.class, Query.select()
				.where("SOURCE_ID = ? AND SOURCE_DOCUMENTATION_LOCATION = ?", element.getId(), identifier));

		List<Link> links = new ArrayList<Link>();
		for (LinkInDatabase linkInDatabase : linksInDatabase) {
			Link link = new LinkImpl(linkInDatabase);
			links.add(link);
		}
		return links;
	}

	/**
	 * Returns all ingoing links (=edges) to a node in the {@link KnowledgeGraph},
	 * except of Jira issue links. If you want to get the Jira {@link IssueLink}s
	 * only, use
	 * {@link JiraIssuePersistenceManager#getOutwardLinks(DecisionKnowledgeElement)}.
	 * 
	 * @param element
	 *            node in the {@link KnowledgeGraph}.
	 * @return list of {@link} objects, does not contain Jira {@link IssueLink}s.
	 *         (=edges) between all kinds of nodes in the {@link KnowledgeGraph},
	 *         except of Jira issue links. If you want to get the Jira
	 *         {@link IssueLink}s only, use
	 *         {@link JiraIssuePersistenceManager#getLinks(DecisionKnowledgeElement)}.
	 * 
	 * @see DecisionKnowledgeElement
	 */
	public static List<Link> getInwardLinks(DecisionKnowledgeElement element) {
		String identifier = element.getDocumentationLocation().getIdentifier();
		LinkInDatabase[] linksInDatabase = ACTIVE_OBJECTS.find(LinkInDatabase.class, Query.select()
				.where("DESTINATION_ID = ? AND DEST_DOCUMENTATION_LOCATION = ?", element.getId(), identifier));

		List<Link> links = new ArrayList<Link>();
		for (LinkInDatabase linkInDatabase : linksInDatabase) {
			Link link = new LinkImpl(linkInDatabase);
			links.add(link);
		}
		return links;
	}

	/**
	 * Inserts a new link (=egde) into database that is not a Jira issue links. The
	 * link can be between any kinds of nodes in the {@link KnowledgeGraph}. If you
	 * want to insert a Jira {@link IssueLink}, use the method
	 * {@link JiraIssuePersistenceManager#insertLink(Link, ApplicationUser)}
	 * instead. If you are not sure what kind of link it is, use
	 * {@link KnowledgePersistenceManager#insertLink(Link, ApplicationUser)}.
	 *
	 * @param link
	 *            link (=edge) between a source and a destination decision knowledge
	 *            element as a {@link Link} object. The link must not be a Jira
	 *            {@link IssueLink}.
	 * @param user
	 *            authenticated JIRA {@link ApplicationUser}.
	 * @return internal database id of inserted link, -1 if insertion failed.
	 */
	public static long insertLink(Link link, ApplicationUser user) {
		if (isLinkAlreadyInDatabase(link) != -1) {
			return isLinkAlreadyInDatabase(link);
		}
		if (!link.isValid()) {
			return -1;
		}

		final LinkInDatabase linkInDatabase = ACTIVE_OBJECTS.create(LinkInDatabase.class);
		DecisionKnowledgeElement sourceElement = link.getSource();
		String documentationLocationOfSourceElement = sourceElement.getDocumentationLocation().getIdentifier();
		linkInDatabase.setSourceDocumentationLocation(documentationLocationOfSourceElement);
		linkInDatabase.setSourceId(sourceElement.getId());

		DecisionKnowledgeElement destinationElement = link.getTarget();
		String documentationLocationOfDestinationElement = destinationElement.getDocumentationLocation()
				.getIdentifier();
		linkInDatabase.setDestinationId(destinationElement.getId());

		linkInDatabase.setDestDocumentationLocation(documentationLocationOfDestinationElement);
		linkInDatabase.setType(link.getType());
		linkInDatabase.save();
		return linkInDatabase.getId();
	}

	/**
	 * Returns the link id if the link already exists in database, otherwise -1.
	 * 
	 * @param link
	 *            {@link Link} object. The link must not be a Jira
	 *            {@link IssueLink}.
	 * @return link id if the link already exists in database, otherwise -1.
	 */
	public static long isLinkAlreadyInDatabase(Link link) {
		long linkId = -1;
		Link flippedLink = link.flip();
		for (LinkInDatabase linkInDatabase : ACTIVE_OBJECTS.find(LinkInDatabase.class)) {
			if (link.equals(linkInDatabase) || flippedLink.equals(linkInDatabase)) {
				linkId = linkInDatabase.getId();
			}
		}
		return linkId;
	}
}
