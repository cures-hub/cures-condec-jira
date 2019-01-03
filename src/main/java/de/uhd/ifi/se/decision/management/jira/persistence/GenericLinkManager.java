package de.uhd.ifi.se.decision.management.jira.persistence;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.jira.user.ApplicationUser;

import de.uhd.ifi.se.decision.management.jira.ComponentGetter;
import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.DocumentationLocation;
import de.uhd.ifi.se.decision.management.jira.model.Link;
import de.uhd.ifi.se.decision.management.jira.model.LinkImpl;
import de.uhd.ifi.se.decision.management.jira.persistence.tables.LinkInDatabase;
import net.java.ao.Query;

public class GenericLinkManager {

	private static final ActiveObjects ACTIVE_OBJECTS = ComponentGetter.getActiveObjects();

	public static void clearInvalidLinks() {
		LinkInDatabase[] linksInDatabase = ACTIVE_OBJECTS.find(LinkInDatabase.class);
		for (LinkInDatabase databaseEntry : linksInDatabase) {
			try {
				Link link = new LinkImpl(databaseEntry);
				if (!link.isValid()) {
					deleteLinkElementFromDatabase(databaseEntry);
				}
			} catch (Exception e) {
				deleteLinkElementFromDatabase(databaseEntry);
			}
		}
	}

	public static boolean deleteLink(Link link) {
		boolean isDeleted = false;
		for (LinkInDatabase linkInDatabase : ACTIVE_OBJECTS.find(LinkInDatabase.class)) {
			if (link.equals(linkInDatabase)) {
				isDeleted = LinkInDatabase.deleteLink(linkInDatabase);
			}
		}
		return isDeleted;
	}

	private static void deleteLinkElementFromDatabase(LinkInDatabase linkElement) {
		try {
			linkElement.getEntityManager().delete(linkElement);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void deleteLinksForElement(long elementId, DocumentationLocation documentationLocation) {
		String identifier = documentationLocation.getIdentifier();
		LinkInDatabase[] linksInDatabase = ACTIVE_OBJECTS.find(LinkInDatabase.class);
		for (LinkInDatabase link : linksInDatabase) {
			if (link.getDestinationId() == elementId && link.getDestDocumentationLocation().equals(identifier)
					|| link.getSourceId() == elementId && link.getSourceDocumentationLocation().equals(identifier)) {
				LinkInDatabase.deleteLink(link);
			}
		}
	}

	public static long getId(String idWithPrefix) {
		return (long) Integer.parseInt(idWithPrefix.substring(1));
	}

	public static List<Link> getLinksForElement(DecisionKnowledgeElement element) {
		String elementIdWithPrefix = element.getDocumentationLocationAsString() + element.getId();
		LinkInDatabase[] linksInDatabase = ACTIVE_OBJECTS.find(LinkInDatabase.class, Query.select().where(
				"ID_OF_DESTINATION_ELEMENT = ? OR ID_OF_SOURCE_ELEMENT = ?", elementIdWithPrefix, elementIdWithPrefix));

		List<Link> links = new ArrayList<Link>();
		for (LinkInDatabase linkInDatabase : linksInDatabase) {
			Link link = new LinkImpl(linkInDatabase);
			links.add(link);
		}
		return links;
	}

	/**
	 * Gets all links from an element.
	 *
	 * @param elementIdWithPrefix
	 *            the id of an decision knowledge element with identifier. Example:
	 *            "i1234" for Issue, "s1337" for sentence. "1337" will not work
	 * @return the generic links for element
	 */
	public static List<Link> getLinksForElement(String elementIdWithPrefix) {
		LinkInDatabase[] linksInDatabase = ACTIVE_OBJECTS.find(LinkInDatabase.class, Query.select().where(
				"ID_OF_DESTINATION_ELEMENT = ? OR ID_OF_SOURCE_ELEMENT = ?", elementIdWithPrefix, elementIdWithPrefix));

		List<Link> links = new ArrayList<Link>();
		for (LinkInDatabase linkInDatabase : linksInDatabase) {
			Link link = new LinkImpl(linkInDatabase);
			links.add(link);
		}
		return links;
	}

	public static long insertLink(Link link, ApplicationUser user) {
		if (isLinkAlreadyInDatabase(link) != -1) {
			return isLinkAlreadyInDatabase(link);
		}
		if (!link.isValid()) {
			return -1;
		}

		final LinkInDatabase linkInDatabase = ACTIVE_OBJECTS.create(LinkInDatabase.class);
		DecisionKnowledgeElement sourceElement = link.getSourceElement();
		String documentationLocationOfSourceElement = sourceElement.getDocumentationLocation().getIdentifier();
		linkInDatabase.setIdOfSourceElement(documentationLocationOfSourceElement + sourceElement.getId());
		linkInDatabase.setSourceDocumentationLocation(documentationLocationOfSourceElement);
		linkInDatabase.setSourceId(sourceElement.getId());

		DecisionKnowledgeElement destinationElement = link.getDestinationElement();
		String documentationLocationOfDestinationElement = destinationElement.getDocumentationLocation()
				.getIdentifier();
		linkInDatabase
				.setIdOfDestinationElement(documentationLocationOfDestinationElement + destinationElement.getId());
		linkInDatabase.setDestinationId(destinationElement.getId());

		linkInDatabase.setDestDocumentationLocation(documentationLocationOfDestinationElement);
		linkInDatabase.setType(link.getType());
		linkInDatabase.save();
		ACTIVE_OBJECTS.find(LinkInDatabase.class);
		return linkInDatabase.getId();
	}

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
