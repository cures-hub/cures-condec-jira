package de.uhd.ifi.se.decision.management.jira.persistence;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.sal.api.transaction.TransactionCallback;

import de.uhd.ifi.se.decision.management.jira.ComponentGetter;
import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeElementImpl;
import de.uhd.ifi.se.decision.management.jira.model.Link;
import de.uhd.ifi.se.decision.management.jira.model.LinkImpl;
import net.java.ao.Query;

/**
 * Extends the abstract class AbstractPersistenceStrategy. Uses the active
 * object framework to store decision knowledge.
 *
 * @see AbstractPersistenceStrategy
 */
@JsonAutoDetect
public class ActiveObjectStrategy extends AbstractPersistenceStrategy {
	private static final Logger LOGGER = LoggerFactory.getLogger(ActiveObjectStrategy.class);
	private static final ActiveObjects ACTIVE_OBJECTS = ComponentGetter.getActiveObjects();

	private String projectKey;

	public ActiveObjectStrategy(String projectKey) {
		this.projectKey = projectKey;
	}

	@Override
	public boolean deleteDecisionKnowledgeElement(DecisionKnowledgeElement decisionKnowledgeElement,
			ApplicationUser user) {
		return ACTIVE_OBJECTS.executeInTransaction(new TransactionCallback<Boolean>() {
			@Override
			public Boolean doInTransaction() {
				for (DecisionKnowledgeElementEntity databaseEntry : ACTIVE_OBJECTS
						.find(DecisionKnowledgeElementEntity.class)) {
					if (databaseEntry.getId() == decisionKnowledgeElement.getId()) {
						try {
							databaseEntry.getEntityManager().delete(databaseEntry);
						} catch (SQLException e) {
							return false;
						} finally {
							for (LinkBetweenDifferentEntitiesEntity linkEntity : ACTIVE_OBJECTS
									.find(LinkBetweenDifferentEntitiesEntity.class)) {
								if (linkEntity.getIdOfSourceElement().equals("a" + decisionKnowledgeElement.getId())
										|| linkEntity.getIdOfDestinationElement()
												.equals("a" + decisionKnowledgeElement.getId())) {
									try {
										linkEntity.getEntityManager().delete(linkEntity);
									} catch (SQLException e) {
										return false;
									}
								}
							}
						}
						return true;
					}
				}
				return false;
			}
		});
	}

	@Override
	public boolean deleteLink(Link link, ApplicationUser user) {
		return GenericLinkManager.deleteGenericLink("a" + link.getSourceElement().getId(),
				"a" + link.getDestinationElement().getId());
	}

	@Override
	public DecisionKnowledgeElement getDecisionKnowledgeElement(long id) {
		DecisionKnowledgeElementEntity decisionKnowledgeElement = ACTIVE_OBJECTS
				.executeInTransaction(new TransactionCallback<DecisionKnowledgeElementEntity>() {
					@Override
					public DecisionKnowledgeElementEntity doInTransaction() {
						DecisionKnowledgeElementEntity[] decisionKnowledgeElement = ACTIVE_OBJECTS
								.find(DecisionKnowledgeElementEntity.class, Query.select().where("ID = ?", id));
						// 0 or 1 decision knowledge elements might be returned by this query
						if (decisionKnowledgeElement.length == 1) {
							return decisionKnowledgeElement[0];
						}
						return null;
					}
				});
		if (decisionKnowledgeElement != null) {
			return new DecisionKnowledgeElementImpl(decisionKnowledgeElement.getId(),
					decisionKnowledgeElement.getSummary(), decisionKnowledgeElement.getDescription(),
					decisionKnowledgeElement.getType(), decisionKnowledgeElement.getProjectKey(),
					decisionKnowledgeElement.getKey());
		}
		return null;
	}

	@Override
	public DecisionKnowledgeElement getDecisionKnowledgeElement(String key) {
		// Split key into project key and id
		String idAsString = null;
		try {
			idAsString = key.split("-")[1];
		} catch (ArrayIndexOutOfBoundsException e) {
			LOGGER.error("Key cannot be split into the project key and id.");
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
		List<DecisionKnowledgeElement> decisionKnowledgeElements = null;
		if (this.projectKey != null) {
			decisionKnowledgeElements = ACTIVE_OBJECTS
					.executeInTransaction(new TransactionCallback<List<DecisionKnowledgeElement>>() {
						@Override
						public List<DecisionKnowledgeElement> doInTransaction() {
							final List<DecisionKnowledgeElement> decisionKnowledgeElements = new ArrayList<DecisionKnowledgeElement>();
							DecisionKnowledgeElementEntity[] decisionArray = ACTIVE_OBJECTS.find(
									DecisionKnowledgeElementEntity.class,
									Query.select().where("PROJECT_KEY = ?", projectKey));
							for (DecisionKnowledgeElementEntity entity : decisionArray) {
								decisionKnowledgeElements.add(new DecisionKnowledgeElementImpl(entity));
							}
							return decisionKnowledgeElements;
						}
					});
		}
		return decisionKnowledgeElements;
	}

	@Override
	public List<DecisionKnowledgeElement> getElementsLinkedWithInwardLinks(
			DecisionKnowledgeElement decisionKnowledgeElement) {
		List<Link> inwardLinks = this.getInwardLinks(decisionKnowledgeElement);
		List<DecisionKnowledgeElement> sourceElements = new ArrayList<DecisionKnowledgeElement>();
		for (Link link : inwardLinks) {
			sourceElements.add(new DecisionKnowledgeElementImpl(
					ACTIVE_OBJECTS.executeInTransaction(new TransactionCallback<DecisionKnowledgeElementEntity>() {
						@Override
						public DecisionKnowledgeElementEntity doInTransaction() {
							DecisionKnowledgeElementEntity[] entityList = ACTIVE_OBJECTS.find(
									DecisionKnowledgeElementEntity.class,
									Query.select().where("ID = ?", link.getSourceElement().getId()));
							if (entityList.length == 1) {
								return entityList[0];
							}
							LOGGER.error("Inward link has no element to return.");
							return null;
						}
					})));
		}
		return sourceElements;
	}

	@Override
	public List<DecisionKnowledgeElement> getElementsLinkedWithOutwardLinks(
			DecisionKnowledgeElement decisionKnowledgeElement) {
		List<Link> outwardLinks = this.getOutwardLinks(decisionKnowledgeElement);
		List<DecisionKnowledgeElement> destinationElements = new ArrayList<DecisionKnowledgeElement>();
		for (Link link : outwardLinks) {
			destinationElements.add(new DecisionKnowledgeElementImpl(
					ACTIVE_OBJECTS.executeInTransaction(new TransactionCallback<DecisionKnowledgeElementEntity>() {
						@Override
						public DecisionKnowledgeElementEntity doInTransaction() {
							DecisionKnowledgeElementEntity[] entityList = ACTIVE_OBJECTS.find(
									DecisionKnowledgeElementEntity.class,
									Query.select().where("ID = ?", link.getDestinationElement().getId()));
							if (entityList.length == 1) {
								return entityList[0];
							}
							LOGGER.error("Outward link has no element to return.");
							return null;
						}
					})));
		}
		return destinationElements;
	}

	@Override
	public List<Link> getInwardLinks(DecisionKnowledgeElement element) {
		List<Link> inwardLinks = new ArrayList<>();
		LinkBetweenDifferentEntitiesEntity[] links = ACTIVE_OBJECTS.find(LinkBetweenDifferentEntitiesEntity.class,
				Query.select().where("ID_OF_DESTINATION_ELEMENT = ?", "a" + element.getId()));
		for (LinkBetweenDifferentEntitiesEntity link : links) {
			Link inwardLink = new LinkImpl(link);
			inwardLink.setDestinationElement(element);
			long elementId = (long) Integer.parseInt(link.getIdOfSourceElement().substring(1));
			inwardLink.setSourceElement(this.getDecisionKnowledgeElement(elementId));
			inwardLinks.add(inwardLink);
		}
		return inwardLinks;
	}

	@Override
	public List<Link> getOutwardLinks(DecisionKnowledgeElement element) {
		List<Link> outwardLinks = new ArrayList<>();
		LinkBetweenDifferentEntitiesEntity[] links = ACTIVE_OBJECTS.find(LinkBetweenDifferentEntitiesEntity.class,
				Query.select().where("ID_OF_SOURCE_ELEMENT = ?", "a" + element.getId()));
		for (LinkBetweenDifferentEntitiesEntity link : links) {
			Link outwardLink = new LinkImpl(link);
			outwardLink.setSourceElement(element);
			long elementId = (long) Integer.parseInt(link.getIdOfDestinationElement().substring(1));
			outwardLink.setDestinationElement(this.getDecisionKnowledgeElement(elementId));
			outwardLinks.add(outwardLink);
		}
		return outwardLinks;
	}

	@Override
	public DecisionKnowledgeElement insertDecisionKnowledgeElement(DecisionKnowledgeElement element,
			ApplicationUser user) {
		DecisionKnowledgeElementEntity databaseEntry = ACTIVE_OBJECTS
				.executeInTransaction(new TransactionCallback<DecisionKnowledgeElementEntity>() {
					@Override
					public DecisionKnowledgeElementEntity doInTransaction() {
						DecisionKnowledgeElementEntity databaseEntry = ACTIVE_OBJECTS
								.create(DecisionKnowledgeElementEntity.class);
						databaseEntry.setKey(element.getProject().getProjectKey().toUpperCase(Locale.ENGLISH) + "-"
								+ databaseEntry.getId());
						databaseEntry.setSummary(element.getSummary());
						databaseEntry.setDescription(element.getDescription());
						databaseEntry.setType(element.getType().toString());
						databaseEntry.setProjectKey(element.getProject().getProjectKey());
						databaseEntry.save();
						return databaseEntry;
					}
				});
		if (databaseEntry == null) {
			LOGGER.error("Insertion of decision knowledge element into database failed.");
			return null;
		}
		element.setId(databaseEntry.getId());
		element.setKey(databaseEntry.getKey());
		return element;
	}

	@Override
	public long insertLink(Link link, ApplicationUser user) {
		return ACTIVE_OBJECTS.executeInTransaction(new TransactionCallback<Long>() {
			@Override
			public Long doInTransaction() {
				for (LinkBetweenDifferentEntitiesEntity linkEntity : ACTIVE_OBJECTS
						.find(LinkBetweenDifferentEntitiesEntity.class)) {
					if (linkEntity.getIdOfSourceElement().substring(1).equals(link.getSourceElement().getId() + "")
							&& linkEntity.getIdOfDestinationElement().substring(1)
									.equals(link.getDestinationElement().getId() + "")) {
						LOGGER.error("Link does already exist.");
						return linkEntity.getId();
					}
				}

				DecisionKnowledgeElementEntity sourceElement = null;
				DecisionKnowledgeElementEntity[] sourceElements = ACTIVE_OBJECTS.find(
						DecisionKnowledgeElementEntity.class,
						Query.select().where("ID = ?", link.getSourceElement().getId()));
				if (sourceElements.length == 1) {
					sourceElement = sourceElements[0];
				}

				DecisionKnowledgeElementEntity destinationElement = null;
				DecisionKnowledgeElementEntity[] destinationElements = ACTIVE_OBJECTS.find(
						DecisionKnowledgeElementEntity.class,
						Query.select().where("ID = ?", link.getDestinationElement().getId()));
				if (destinationElements.length == 1) {
					destinationElement = destinationElements[0];
				}
				if (sourceElement == null || destinationElement == null) {
					LOGGER.error("One of the elements to be linked does not exist.");
					return (long) 0;
				}

				// elements exist
				Link newLink = new LinkImpl("a" + link.getDestinationElement().getId(),
						"a" + link.getSourceElement().getId());
				newLink.setType(link.getType());
				return GenericLinkManager.insertGenericLink(newLink, user);
			}
		});
	}

	@Override
	public boolean updateDecisionKnowledgeElement(DecisionKnowledgeElement element, ApplicationUser user) {
		DecisionKnowledgeElementEntity databaseEntry = ACTIVE_OBJECTS
				.executeInTransaction(new TransactionCallback<DecisionKnowledgeElementEntity>() {
					@Override
					public DecisionKnowledgeElementEntity doInTransaction() {
						for (DecisionKnowledgeElementEntity databaseEntry : ACTIVE_OBJECTS
								.find(DecisionKnowledgeElementEntity.class)) {
							if (databaseEntry.getId() == element.getId()) {
								databaseEntry.setSummary(element.getSummary());
								databaseEntry.setDescription(element.getDescription());
								databaseEntry.setType(element.getType().toString());
								databaseEntry.save();
								return databaseEntry;
							}
						}
						return null;
					}
				});
		if (databaseEntry == null) {
			LOGGER.error("Updating of decision knowledge element in database failed.");
			return false;
		}
		return true;
	}
}