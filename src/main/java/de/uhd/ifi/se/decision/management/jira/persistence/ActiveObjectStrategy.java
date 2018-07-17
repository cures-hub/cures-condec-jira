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
 */
@JsonAutoDetect
public class ActiveObjectStrategy extends AbstractPersistenceStrategy {
	private static final Logger LOGGER = LoggerFactory.getLogger(ActiveObjectStrategy.class);
	private static final ActiveObjects ACTIVE_OBJECTS = ComponentGetter.getActiveObjects();

	@Override
	public DecisionKnowledgeElement insertDecisionKnowledgeElement(DecisionKnowledgeElement decisionKnowledgeElement,
			ApplicationUser user) {
		if (decisionKnowledgeElement == null) {
			LOGGER.error("AOStrategy insertDecisionKnowledgeElement the DecisionRepresentation is null");
			return null;
		}
		if (user == null) {
			LOGGER.error("AOStrategy insertDecisionKnowledgeElement the ApplicationUser is null");
			return null;
		}
		DecisionKnowledgeElementEntity databaseEntry = ACTIVE_OBJECTS
				.executeInTransaction(new TransactionCallback<DecisionKnowledgeElementEntity>() {
					@Override
					public DecisionKnowledgeElementEntity doInTransaction() {
						DecisionKnowledgeElementEntity databaseEntry = ACTIVE_OBJECTS
								.create(DecisionKnowledgeElementEntity.class);
						databaseEntry.setKey(decisionKnowledgeElement.getProject().getProjectKey().toUpperCase(Locale.ENGLISH) + "-"
								+ databaseEntry.getId());
						databaseEntry.setSummary(decisionKnowledgeElement.getSummary());
						databaseEntry.setDescription(decisionKnowledgeElement.getDescription());
						databaseEntry.setType(decisionKnowledgeElement.getType().toString());
						databaseEntry.setProjectKey(decisionKnowledgeElement.getProject().getProjectKey());
						databaseEntry.save();
						return databaseEntry;
					}
				});
		if (databaseEntry == null) {
			return null;
		}
		decisionKnowledgeElement.setId(databaseEntry.getId());
		decisionKnowledgeElement.setKey(databaseEntry.getKey());
		return decisionKnowledgeElement;
	}

	@Override
	public boolean updateDecisionKnowledgeElement(DecisionKnowledgeElement decisionKnowledgeElement,
			ApplicationUser user) {
		DecisionKnowledgeElementEntity databaseEntry = ACTIVE_OBJECTS
				.executeInTransaction(new TransactionCallback<DecisionKnowledgeElementEntity>() {
					@Override
					public DecisionKnowledgeElementEntity doInTransaction() {
						for (DecisionKnowledgeElementEntity databaseEntry : ACTIVE_OBJECTS
								.find(DecisionKnowledgeElementEntity.class)) {
							if (databaseEntry.getId() == decisionKnowledgeElement.getId()) {
								databaseEntry.setSummary(decisionKnowledgeElement.getSummary());
								databaseEntry.setDescription(decisionKnowledgeElement.getDescription());
								databaseEntry.setType(decisionKnowledgeElement.getType().toString());
								databaseEntry.save();
								return databaseEntry;
							}
						}
						return null;
					}
				});
		return databaseEntry != null;
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
							for (LinkEntity linkEntity : ACTIVE_OBJECTS.find(LinkEntity.class)) {
								if (linkEntity.getIdOfSourceElement() == decisionKnowledgeElement.getId()
										|| linkEntity.getIdOfDestinationElement() == decisionKnowledgeElement.getId()) {
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
	public List<DecisionKnowledgeElement> getDecisionKnowledgeElements(String projectKey) {
		List<DecisionKnowledgeElement> decisionKnowledgeElements = null;
		if (projectKey != null) {
			decisionKnowledgeElements = ACTIVE_OBJECTS
					.executeInTransaction(new TransactionCallback<List<DecisionKnowledgeElement>>() {
						@Override
						public List<DecisionKnowledgeElement> doInTransaction() {
							final List<DecisionKnowledgeElement> decisionKnowledgeElements = new ArrayList<DecisionKnowledgeElement>();
							// Returns all instances of interface DecisionKnowledgeElementEntity for the
							// given project key
							DecisionKnowledgeElementEntity[] decisionArray = ACTIVE_OBJECTS.find(
									DecisionKnowledgeElementEntity.class,
									Query.select().where("PROJECT_KEY = ?", projectKey));
							for (DecisionKnowledgeElementEntity entity : decisionArray) {
								decisionKnowledgeElements.add(castToDecisionKnowledgeElement(entity));
							}
							return decisionKnowledgeElements;
						}
					});
		}
		return decisionKnowledgeElements;
	}

	@Override
	public DecisionKnowledgeElement getDecisionKnowledgeElement(String key) {
		// Split key into project key and id
		DecisionKnowledgeElement decisionKnowledgeElement = null;
		String idAsString = null;
		try {
			idAsString = key.split("-")[1];
		} catch (ArrayIndexOutOfBoundsException e) {
			LOGGER.error("Key cannot be split into the project key and id.");
		}
		if (idAsString != null) {
			long knowledgeElementId = Long.parseLong(idAsString);
			decisionKnowledgeElement = getDecisionKnowledgeElement(knowledgeElementId);
		}
		if (decisionKnowledgeElement == null) {
			LOGGER.error("No decision knowledge element with " + key + " could be found.");
		}
		return decisionKnowledgeElement;
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
	public List<DecisionKnowledgeElement> getElementsLinkedWithInwardLinks(
			DecisionKnowledgeElement decisionKnowledgeElement) {
		List<Link> inwardLinks = this.getInwardLinks(decisionKnowledgeElement);
		List<DecisionKnowledgeElement> children = new ArrayList<DecisionKnowledgeElement>();
		for (Link inwardLink : inwardLinks) {
			children.add(castToDecisionKnowledgeElement(
					ACTIVE_OBJECTS.executeInTransaction(new TransactionCallback<DecisionKnowledgeElementEntity>() {
						@Override
						public DecisionKnowledgeElementEntity doInTransaction() {
							DecisionKnowledgeElementEntity[] entityList = ACTIVE_OBJECTS.find(
									DecisionKnowledgeElementEntity.class,
									Query.select().where("ID = ?", inwardLink.getIdOfSourceElement()));
							if (entityList.length == 1) {
								return entityList[0];
							}
							LOGGER.error("Inward Link has no Element to return");
							return null;
						}
					})));
		}
		return children;
	}

	@Override
	public List<DecisionKnowledgeElement> getElementsLinkedWithOutwardLinks(
			DecisionKnowledgeElement decisionKnowledgeElement) {
		List<Link> outwardLinks = this.getOutwardLinks(decisionKnowledgeElement);
		List<DecisionKnowledgeElement> parents = new ArrayList<DecisionKnowledgeElement>();
		for (Link outwardLink : outwardLinks) {
			parents.add(castToDecisionKnowledgeElement(
					ACTIVE_OBJECTS.executeInTransaction(new TransactionCallback<DecisionKnowledgeElementEntity>() {
						@Override
						public DecisionKnowledgeElementEntity doInTransaction() {
							DecisionKnowledgeElementEntity[] entityList = ACTIVE_OBJECTS.find(
									DecisionKnowledgeElementEntity.class,
									Query.select().where("ID = ?", outwardLink.getIdOfDestinationElement()));
							if (entityList.length == 1) {
								return entityList[0];
							}
							LOGGER.error("Outward Link has no Element to return");
							return null;
						}
					})));
		}
		return parents;
	}

	@Override
	public long insertLink(Link link, ApplicationUser user) {
		return ACTIVE_OBJECTS.executeInTransaction(new TransactionCallback<Long>() {
			@Override
			public Long doInTransaction() {
				boolean linkAlreadyExists = false;
				long linkId = 0;
				for (LinkEntity linkEntity : ACTIVE_OBJECTS.find(LinkEntity.class)) {
					if (linkEntity.getIdOfSourceElement() == link.getIdOfSourceElement()
							&& linkEntity.getIdOfDestinationElement() == link.getIdOfDestinationElement()) {
						linkAlreadyExists = true;
						linkId = linkEntity.getLinkId();
					}
				}
				if (!linkAlreadyExists) {
					DecisionKnowledgeElementEntity decCompIngoing;
					DecisionKnowledgeElementEntity[] decCompIngoingArray = ACTIVE_OBJECTS.find(
							DecisionKnowledgeElementEntity.class, Query.select().where("ID = ?", link.getIdOfSourceElement()));
					if (decCompIngoingArray.length == 1) {
						decCompIngoing = decCompIngoingArray[0];
					} else {
						// entity with ingoingId does not exist
						decCompIngoing = null;
					}

					DecisionKnowledgeElementEntity decCompOutgoing;
					DecisionKnowledgeElementEntity[] decCompOutgoingArray = ACTIVE_OBJECTS.find(
							DecisionKnowledgeElementEntity.class, Query.select().where("ID = ?", link.getIdOfDestinationElement()));
					if (decCompOutgoingArray.length == 1) {
						decCompOutgoing = decCompOutgoingArray[0];
					} else {
						// entity with outgoingId does not exist
						decCompOutgoing = null;
					}
					if (decCompIngoing != null && decCompOutgoing != null) {
						if (decCompIngoing.getProjectKey().equals(decCompOutgoing.getProjectKey())) {
							// entities exist and are in the same project
							final LinkEntity linkEntity = ACTIVE_OBJECTS.create(LinkEntity.class);
							linkEntity.setIdOfSourceElement(link.getIdOfSourceElement());
							linkEntity.setIdOfDestinationElement(link.getIdOfDestinationElement());
							linkEntity.setLinkType(link.getLinkType());
							linkEntity.save();
							linkId = linkEntity.getLinkId();
						} else {
							LOGGER.error("entities to be linked are not in the same project");
							return (long) 0;
						}
					} else {
						LOGGER.error("one of the entities to be linked does not exist");
						return (long) 0;
					}
				} else {
					LOGGER.error("Link already exists");
					return linkId;
				}
				return linkId;
			}
		});
	}

	@Override
	public boolean deleteLink(Link link, ApplicationUser user) {
		return ACTIVE_OBJECTS.executeInTransaction(new TransactionCallback<Boolean>() {
			@Override
			public Boolean doInTransaction() {
				for (LinkEntity linkEntity : ACTIVE_OBJECTS.find(LinkEntity.class)) {
					if (link.getIdOfSourceElement() == linkEntity.getIdOfSourceElement()
							&& link.getIdOfDestinationElement() == linkEntity.getIdOfDestinationElement()
							|| link.getIdOfDestinationElement() == linkEntity.getIdOfSourceElement()
									&& link.getIdOfSourceElement() == linkEntity.getIdOfDestinationElement()) {
						try {
							linkEntity.getEntityManager().delete(linkEntity);
							return true;
						} catch (SQLException e) {
							LOGGER.error("LinkEntity could not be deleted");
						}
					}
				}
				return false;
			}
		});
	}

	@Override
	public List<Link> getInwardLinks(DecisionKnowledgeElement decisionKnowledgeElement) {
		List<Link> inwardLinks = new ArrayList<>();
		LinkEntity[] links = ACTIVE_OBJECTS.find(LinkEntity.class,
				Query.select().where("OUTGOING_ID = ?", decisionKnowledgeElement.getId()));
		for (LinkEntity link : links) {
			Link inwardLink = new LinkImpl(link);
			inwardLink.setDestinationElement(decisionKnowledgeElement);
			inwardLink.setSourceElement(this.getDecisionKnowledgeElement(link.getIdOfSourceElement()));
			inwardLinks.add(inwardLink);
		}
		return inwardLinks;
	}

	@Override
	public List<Link> getOutwardLinks(DecisionKnowledgeElement decisionKnowledgeElement) {
		List<Link> outwardLinks = new ArrayList<>();
		LinkEntity[] links = ACTIVE_OBJECTS.find(LinkEntity.class,
				Query.select().where("INGOING_ID = ?", decisionKnowledgeElement.getId()));
		for (LinkEntity link : links) {
			Link outwardLink = new LinkImpl(link);
			outwardLink.setSourceElement(decisionKnowledgeElement);
			outwardLink.setDestinationElement(this.getDecisionKnowledgeElement(link.getIdOfDestinationElement()));
			outwardLinks.add(outwardLink);
		}
		return outwardLinks;
	}

	// Converting the Entity to a DecisionKnowledgeElementImpl for future use
	private DecisionKnowledgeElement castToDecisionKnowledgeElement(DecisionKnowledgeElementEntity entity) {
		return new DecisionKnowledgeElementImpl(entity.getId(), entity.getSummary(), entity.getDescription(),
				entity.getType(), entity.getProjectKey(), entity.getKey());
	}
}