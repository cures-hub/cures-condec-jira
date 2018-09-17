package de.uhd.ifi.se.decision.management.jira.extraction.persistence;

import java.util.ArrayList;
import java.util.List;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.sal.api.transaction.TransactionCallback;

import de.uhd.ifi.se.decision.management.jira.ComponentGetter;
import de.uhd.ifi.se.decision.management.jira.extraction.model.GenericLink;
import de.uhd.ifi.se.decision.management.jira.extraction.model.GenericLinkImpl;

public class GenericLinkManager {
	
	private static ActiveObjects ao;

	public static void init() {
		if (ao == null) {
			ao = ComponentGetter.getActiveObjects();
		}
	}
	
	public static List<GenericLink> getGenericLinksForElement(String targetId, boolean getOnlyOutwardLink) {
		init();
		List<GenericLink> links = new ArrayList<GenericLink>();
		ao.executeInTransaction(new TransactionCallback<LinkBetweenDifferentEntitiesEntity>() {
			@Override
			public LinkBetweenDifferentEntitiesEntity doInTransaction() {
				LinkBetweenDifferentEntitiesEntity[] linkElements = ao.find(LinkBetweenDifferentEntitiesEntity.class);
				for (LinkBetweenDifferentEntitiesEntity linkElement : linkElements) {
					if (linkElement.getIdOfDestinationElement().equals(targetId)) {
						links.add(new GenericLinkImpl(targetId, linkElement.getIdOfSourceElement()));
					}
					if (!getOnlyOutwardLink && linkElement.getIdOfSourceElement().equals(targetId)) {
						links.add(new GenericLinkImpl(targetId, linkElement.getIdOfDestinationElement()));
					}
				}
				return null;
			}
		});
		return links;
	}

	public static long insertGenericLink(GenericLink link, ApplicationUser user) {
		init();
		return ao.executeInTransaction(new TransactionCallback<Long>() {
			@Override
			public Long doInTransaction() {
				for (LinkBetweenDifferentEntitiesEntity linkEntity : ao
						.find(LinkBetweenDifferentEntitiesEntity.class)) {
					if (linkEntity.getIdOfSourceElement() == link.getIdOfSourceElement()
							&& linkEntity.getIdOfDestinationElement() == link.getIdOfDestinationElement()// Check inverse link
							|| linkEntity.getIdOfDestinationElement() == link.getIdOfSourceElement()
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
}
