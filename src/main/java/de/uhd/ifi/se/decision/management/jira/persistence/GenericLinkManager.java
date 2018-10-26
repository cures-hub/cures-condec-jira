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

	private static ActiveObjects ao;

	public static void init() {
		if (ao == null) {
			ao = ComponentGetter.getActiveObjects();
		}
	}

	public static boolean deleteGenericLink(Link link) {
		init();
		return deleteGenericLink(link.getIdOfSourceElementWithPrefix(), link.getIdOfDestinationElementWithPrefix());
	}

	public static boolean deleteGenericLink(String source, String target) {
		init();
		return ao.executeInTransaction(new TransactionCallback<Boolean>() {
			@Override
			public Boolean doInTransaction() {
				return deleteGenericLinkWithoutTransaction(source,target);
			}
		});
	}

	public static Boolean deleteGenericLinkWithoutTransaction(String source, String target) {
		for (LinkInDatabase linkEntity : ao.find(LinkInDatabase.class)) {
			if (linkEntity.getIdOfDestinationElement().equals(target)
					&& linkEntity.getIdOfSourceElement().equals(source)) {
				try {
					linkEntity.getEntityManager().delete(linkEntity);
					return true;
				} catch (SQLException e) {
					return false;
				}
			}
		}
		return false;
	}

	public static long insertGenericLink(Link link, ApplicationUser user) {
		init();
		return ao.executeInTransaction(new TransactionCallback<Long>() {
			@Override
			public Long doInTransaction() {
				return insertGenericLinkWithoutTransactionCallback(link);
			}
		});
	}
	
	public static long insertGenericLinkWithoutTransactionCallback(Link link) {
		for (LinkInDatabase linkEntity : ao.find(LinkInDatabase.class)) {
			if (linkEntity.getIdOfSourceElement() == link.getIdOfSourceElementWithPrefix()
					&& linkEntity.getIdOfDestinationElement() == link.getIdOfDestinationElementWithPrefix()
					|| linkEntity.getIdOfDestinationElement() == link.getIdOfSourceElementWithPrefix()// Check inverse
																							// link
							&& linkEntity.getIdOfSourceElement() == link.getIdOfDestinationElementWithPrefix()) {
				return linkEntity.getId();
			}
		}

		final LinkInDatabase genericLink = ao.create(LinkInDatabase.class);
		genericLink.setIdOfSourceElement(link.getIdOfSourceElementWithPrefix());
		genericLink.setIdOfDestinationElement(link.getIdOfDestinationElementWithPrefix());
		genericLink.setType(link.getType());
		genericLink.save();
		ao.find(LinkInDatabase.class);
		return genericLink.getId();
	}

	/**
	 * Gets the generic links for element.
	 *
	 * @param targetId
	 *            the target id with identifier. Example: "i1234" for Issue, "s1337"
	 *            for sentence. "1337" will not work
	 * @param getOnlyOutwardLink
	 *            if false, checks both directions
	 * @return the generic links for element
	 */
	public static List<Link> getLinksForElement(String targetId, boolean getOnlyOutwardLink) {
		init();
		List<Link> links = new ArrayList<Link>();
				LinkInDatabase[] linkElements = ao.find(LinkInDatabase.class);
				for (LinkInDatabase linkElement : linkElements) {
					Link link = new LinkImpl(linkElement.getIdOfDestinationElement(),
							linkElement.getIdOfSourceElement());
					link.setId(linkElement.getId());
					// if(link.isValid()) { @issue: Function is very slow. @alternative: run this as
					// a service
					if (!getOnlyOutwardLink && linkElement.getIdOfDestinationElement().equals(targetId)) {
						links.add(link);
					}
					if (linkElement.getIdOfSourceElement().equals(targetId)) {
						links.add(link);
					}

				}
	
		return links;
	}

	public static void clearInValidLinks() {
		init();
		ao.executeInTransaction(new TransactionCallback<LinkInDatabase>() {
			@Override
			public LinkInDatabase doInTransaction() {
				LinkInDatabase[] linkElements = ao.find(LinkInDatabase.class);
				for (LinkInDatabase linkElement : linkElements) {
					Link link = new LinkImpl(linkElement.getIdOfDestinationElement(),
							linkElement.getIdOfSourceElement());
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

	public static void deleteLinksForElementIfExisting(String id) {
		init();
		ao.executeInTransaction(new TransactionCallback<LinkInDatabase>() {
			@Override
			public LinkInDatabase doInTransaction() {
				LinkInDatabase[] linkElements = ao.find(LinkInDatabase.class);
				for (LinkInDatabase linkElement : linkElements) {
					if (linkElement.getIdOfDestinationElement().equals(id)
							|| linkElement.getIdOfSourceElement().equals(id)) {
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
