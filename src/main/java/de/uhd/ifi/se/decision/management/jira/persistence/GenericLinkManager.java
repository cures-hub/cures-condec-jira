package de.uhd.ifi.se.decision.management.jira.persistence;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.sal.api.transaction.TransactionCallback;

import de.uhd.ifi.se.decision.management.jira.ComponentGetter;
import de.uhd.ifi.se.decision.management.jira.extraction.model.GenericLink;
import de.uhd.ifi.se.decision.management.jira.extraction.model.impl.GenericLinkImpl;
import de.uhd.ifi.se.decision.management.jira.extraction.persistence.LinkBetweenDifferentEntitiesEntity;
import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeElement;

public class GenericLinkManager {

	private static ActiveObjects ao;

	public static void init() {
		if (ao == null) {
			ao = ComponentGetter.getActiveObjects();
		}
	}

	public static boolean deleteGenericLink(GenericLink link) {
		init();
		return ao.executeInTransaction(new TransactionCallback<Boolean>() {
			@Override
			public Boolean doInTransaction() {
				for (LinkBetweenDifferentEntitiesEntity linkEntity : ao
						.find(LinkBetweenDifferentEntitiesEntity.class)) {
					if (link.getIdOfDestinationElement().equals(linkEntity.getIdOfDestinationElement())
							&& link.getIdOfSourceElement().equals(linkEntity.getIdOfSourceElement())) {
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
		});
	}

	public static long insertGenericLink(GenericLink link, ApplicationUser user) {
		init();
		return ao.executeInTransaction(new TransactionCallback<Long>() {
			@Override
			public Long doInTransaction() {
				for (LinkBetweenDifferentEntitiesEntity linkEntity : ao
						.find(LinkBetweenDifferentEntitiesEntity.class)) {
					if (linkEntity.getIdOfSourceElement() == link.getIdOfSourceElement()
							&& linkEntity.getIdOfDestinationElement() == link.getIdOfDestinationElement()
							|| linkEntity.getIdOfDestinationElement() == link.getIdOfSourceElement()// Check inverse
																									// link
									&& linkEntity.getIdOfSourceElement() == link.getIdOfDestinationElement()) {
						return linkEntity.getId();
					}
				}

				final LinkBetweenDifferentEntitiesEntity genericLink = ao
						.create(LinkBetweenDifferentEntitiesEntity.class);
				genericLink.setIdOfSourceElement(link.getIdOfSourceElement());
				genericLink.setIdOfDestinationElement(link.getIdOfDestinationElement());
				genericLink.setType(link.getType());
				genericLink.save();

				return genericLink.getId();
			}
		});
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
	public static List<GenericLink> getGenericLinksForElement(String targetId, boolean getOnlyOutwardLink) {
		init();
		List<GenericLink> links = new ArrayList<GenericLink>();
		ao.executeInTransaction(new TransactionCallback<LinkBetweenDifferentEntitiesEntity>() {
			@Override
			public LinkBetweenDifferentEntitiesEntity doInTransaction() {
				LinkBetweenDifferentEntitiesEntity[] linkElements = ao.find(LinkBetweenDifferentEntitiesEntity.class);
				for (LinkBetweenDifferentEntitiesEntity linkElement : linkElements) {
					GenericLink link = new GenericLinkImpl(linkElement.getIdOfDestinationElement(),
							linkElement.getIdOfSourceElement());
					// if(link.isValid()) { @issue: Function is very slow. @alternative: run this as
					// a service
					if (!getOnlyOutwardLink && linkElement.getIdOfDestinationElement().equals(targetId)) {
						links.add(link);
					}
					if (linkElement.getIdOfSourceElement().equals(targetId)) {
						links.add(link);
					}

				}
				return null;
			}
		});
		return links;
	}

	public static void clearInValidLinks() {
		init();
		ao.executeInTransaction(new TransactionCallback<LinkBetweenDifferentEntitiesEntity>() {
			@Override
			public LinkBetweenDifferentEntitiesEntity doInTransaction() {
				LinkBetweenDifferentEntitiesEntity[] linkElements = ao.find(LinkBetweenDifferentEntitiesEntity.class);
				for (LinkBetweenDifferentEntitiesEntity linkElement : linkElements) {
					GenericLink link = new GenericLinkImpl(linkElement.getIdOfDestinationElement(),
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
		ao.executeInTransaction(new TransactionCallback<LinkBetweenDifferentEntitiesEntity>() {
			@Override
			public LinkBetweenDifferentEntitiesEntity doInTransaction() {
				LinkBetweenDifferentEntitiesEntity[] linkElements = ao.find(LinkBetweenDifferentEntitiesEntity.class);
				for (LinkBetweenDifferentEntitiesEntity linkElement : linkElements) {
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

}
