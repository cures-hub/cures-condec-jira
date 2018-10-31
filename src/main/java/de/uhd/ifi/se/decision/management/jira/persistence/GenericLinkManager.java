package de.uhd.ifi.se.decision.management.jira.persistence;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.sal.api.transaction.TransactionCallback;

import de.uhd.ifi.se.decision.management.jira.ComponentGetter;
import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.DocumentationLocation;
import de.uhd.ifi.se.decision.management.jira.model.Link;
import de.uhd.ifi.se.decision.management.jira.model.LinkImpl;

public class GenericLinkManager {

	private static ActiveObjects activeObjects;

	public static void init() {
		if (activeObjects == null) {
			activeObjects = ComponentGetter.getActiveObjects();
		}
	}

	public static boolean deleteGenericLink(Link link) {
		init();
		return deleteLink(link.getIdOfSourceElementWithPrefix(), link.getIdOfDestinationElementWithPrefix());
	}

	public static boolean deleteLink(String sourceIdWithPrefix, String targetIdWithPrefix) {
		init();
		return activeObjects.executeInTransaction(new TransactionCallback<Boolean>() {
			@Override
			public Boolean doInTransaction() {
				return deleteLinkWithoutTransaction(sourceIdWithPrefix, targetIdWithPrefix);
			}
		});
	}

	private static Boolean deleteLinkWithoutTransaction(String sourceIdWithPrefix, String targetIdWithPrefix) {
		init();
		for (LinkInDatabase linkInDatabase : activeObjects.find(LinkInDatabase.class)) {
			if (linkInDatabase.getIdOfDestinationElement().equals(targetIdWithPrefix)
					&& linkInDatabase.getIdOfSourceElement().equals(sourceIdWithPrefix)) {
				try {
					linkInDatabase.getEntityManager().delete(linkInDatabase);
					return true;
				} catch (SQLException e) {
					return false;
				}
			}
		}
		return false;
	}

	public static long insertLink(Link link, ApplicationUser user) {
		init();
		return activeObjects.executeInTransaction(new TransactionCallback<Long>() {
			@Override
			public Long doInTransaction() {
				return insertLinkWithoutTransaction(link);
			}
		});
	}

	public static long insertLinkWithoutTransaction(Link link) {
		init();
		if (isLinkAlreadyInDatabase(link) != -1) {
			return isLinkAlreadyInDatabase(link);
		}
		if (!link.isValid()) {
			return -1;
		}

		final LinkInDatabase linkInDatabase = activeObjects.create(LinkInDatabase.class);
		linkInDatabase.setIdOfSourceElement(link.getIdOfSourceElementWithPrefix());
		linkInDatabase.setIdOfDestinationElement(link.getIdOfDestinationElementWithPrefix());
		linkInDatabase.setType(link.getType());
		linkInDatabase.save();
		activeObjects.find(LinkInDatabase.class);
		return linkInDatabase.getId();
	}

	private static long isLinkAlreadyInDatabase(Link link) {
		init();
		for (LinkInDatabase linkInDatabase : activeObjects.find(LinkInDatabase.class)) {
			// also checks the inverse link
			if (linkInDatabase.getIdOfSourceElement().equals(link.getIdOfSourceElementWithPrefix())
					&& linkInDatabase.getIdOfDestinationElement().equals(link.getIdOfDestinationElementWithPrefix())
					|| linkInDatabase.getIdOfDestinationElement().equals(link.getIdOfSourceElementWithPrefix())
							&& linkInDatabase.getIdOfSourceElement()
									.equals(link.getIdOfDestinationElementWithPrefix())) {
				return linkInDatabase.getId();
			}
		}
		return -1;
	}

	/**
	 * Gets all links from an element.
	 *
	 * @param elementIdWithPrefix
	 *            the id of an decision knowledge element with identifier. Example:
	 *            "i1234" for Issue, "s1337" for sentence. "1337" will not work
	 * @param getOnlyOutwardLink
	 *            if false, checks both directions
	 * @return the generic links for element
	 */
	public static List<Link> getLinksForElement(String elementIdWithPrefix) {
		init();
		List<Link> links = new ArrayList<Link>();
		LinkInDatabase[] linksInDatabase = activeObjects.find(LinkInDatabase.class);
		for (LinkInDatabase linkInDatabase : linksInDatabase) {
			if (linkInDatabase.getIdOfDestinationElement().equals(elementIdWithPrefix)
					|| linkInDatabase.getIdOfSourceElement().equals(elementIdWithPrefix)) {
				Link link = new LinkImpl(linkInDatabase.getIdOfSourceElement(),
						linkInDatabase.getIdOfDestinationElement());
				link.setId(linkInDatabase.getId());
				links.add(link);
			}
		}
		return links;
	}

	public static void clearInvalidLinks() {
		init();
		activeObjects.executeInTransaction(new TransactionCallback<LinkInDatabase>() {
			@Override
			public LinkInDatabase doInTransaction() {
				LinkInDatabase[] linkElements = activeObjects.find(LinkInDatabase.class);
				for (LinkInDatabase linkElement : linkElements) {
					Link link = new LinkImpl(linkElement.getIdOfSourceElement(),
							linkElement.getIdOfDestinationElement());
					if (!link.isValid()) {
						try {
							linkElement.getEntityManager().delete(linkElement);
						} catch (SQLException e) {
						}
					}
				}
				return null;
			}
		});
	}

	public static void deleteLinksForElement(String elementIdWithPrefix) {
		init();
		activeObjects.executeInTransaction(new TransactionCallback<LinkInDatabase>() {
			@Override
			public LinkInDatabase doInTransaction() {
				LinkInDatabase[] linksInDatabase = activeObjects.find(LinkInDatabase.class);
				for (LinkInDatabase linkInDatabase : linksInDatabase) {
					if (linkInDatabase.getIdOfDestinationElement().equals(elementIdWithPrefix)
							|| linkInDatabase.getIdOfSourceElement().equals(elementIdWithPrefix)) {
						try {
							linkInDatabase.getEntityManager().delete(linkInDatabase);
						} catch (SQLException e) {
						}
					}
				}
				return null;
			}
		});
	}

	public static DecisionKnowledgeElement getIssueFromAOTable(long dkeId) {
		ActiveObjectStrategy aos = new ActiveObjectStrategy("");
		return aos.getDecisionKnowledgeElement(dkeId);
	}

	public static boolean isIssueLink(Link link) {
		return link.getSourceElement().getDocumentationLocation() == DocumentationLocation.JIRAISSUE
				&& link.getDestinationElement().getDocumentationLocation() == DocumentationLocation.JIRAISSUE;
	}

	public static long getId(String idWithPrefix) {
		return (long) Integer.parseInt(idWithPrefix.substring(1));
	}
}
