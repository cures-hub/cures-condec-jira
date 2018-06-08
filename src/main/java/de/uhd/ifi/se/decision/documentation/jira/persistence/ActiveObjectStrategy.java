package de.uhd.ifi.se.decision.documentation.jira.persistence;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.sal.api.transaction.TransactionCallback;

import de.uhd.ifi.se.decision.documentation.jira.ComponentGetter;
import de.uhd.ifi.se.decision.documentation.jira.model.*;
import net.java.ao.Query;

/**
 * @description Extends the abstract class PersistenceStrategy. Uses the active
 *              object framework to store decision knowledge.
 */
@JsonAutoDetect
public class ActiveObjectStrategy extends PersistenceStrategy {
	private static final Logger LOGGER = LoggerFactory.getLogger(ActiveObjectStrategy.class);
	private static final ActiveObjects activeObjects = ComponentGetter.getActiveObjects();

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
		DecisionKnowledgeElementEntity databaseEntry = activeObjects
				.executeInTransaction(new TransactionCallback<DecisionKnowledgeElementEntity>() {
					@Override
					public DecisionKnowledgeElementEntity doInTransaction() {
						DecisionKnowledgeElementEntity databaseEntry = activeObjects
								.create(DecisionKnowledgeElementEntity.class);
						databaseEntry.setKey(
								decisionKnowledgeElement.getProjectKey().toUpperCase() + "-" + databaseEntry.getId());
						databaseEntry.setSummary(decisionKnowledgeElement.getSummary());
						databaseEntry.setDescription(decisionKnowledgeElement.getDescription());
						databaseEntry.setType(decisionKnowledgeElement.getType());
						databaseEntry.setProjectKey(decisionKnowledgeElement.getProjectKey());
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
		DecisionKnowledgeElementEntity databaseEntry = activeObjects
				.executeInTransaction(new TransactionCallback<DecisionKnowledgeElementEntity>() {
					@Override
					public DecisionKnowledgeElementEntity doInTransaction() {
						for (DecisionKnowledgeElementEntity databaseEntry : activeObjects
								.find(DecisionKnowledgeElementEntity.class)) {
							if (databaseEntry.getId() == decisionKnowledgeElement.getId()) {
								databaseEntry.setSummary(decisionKnowledgeElement.getSummary());
								databaseEntry.setDescription(decisionKnowledgeElement.getDescription());
								databaseEntry.setType(decisionKnowledgeElement.getType());
								databaseEntry.save();
								return databaseEntry;
							}
						}
						return null;
					}
				});
		if (databaseEntry != null) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean deleteDecisionKnowledgeElement(DecisionKnowledgeElement decisionKnowledgeElement,
			ApplicationUser user) {
		return activeObjects.executeInTransaction(new TransactionCallback<Boolean>() {
			@Override
			public Boolean doInTransaction() {
				for (DecisionKnowledgeElementEntity databaseEntry : activeObjects
						.find(DecisionKnowledgeElementEntity.class)) {
					if (databaseEntry.getId() == decisionKnowledgeElement.getId()) {
						try {
							databaseEntry.getEntityManager().delete(databaseEntry);
						} catch (SQLException e) {
							return false;
						} finally {
							for (LinkEntity linkEntity : activeObjects.find(LinkEntity.class)) {
								if (linkEntity.getIngoingId() == decisionKnowledgeElement.getId()
										|| linkEntity.getOutgoingId() == decisionKnowledgeElement.getId()) {
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
			decisionKnowledgeElements = activeObjects
					.executeInTransaction(new TransactionCallback<List<DecisionKnowledgeElement>>() {
						@Override
						public List<DecisionKnowledgeElement> doInTransaction() {
							final List<DecisionKnowledgeElement> decisionKnowledgeElements = new ArrayList<DecisionKnowledgeElement>();
							// Returns all instances of interface DecisionKnowledgeElementEntity for the
							// given project key
							DecisionKnowledgeElementEntity[] decisionArray = activeObjects.find(
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
			e.printStackTrace();
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
		DecisionKnowledgeElementEntity decisionKnowledgeElement = activeObjects
				.executeInTransaction(new TransactionCallback<DecisionKnowledgeElementEntity>() {
					@Override
					public DecisionKnowledgeElementEntity doInTransaction() {
						DecisionKnowledgeElementEntity[] decisionKnowledgeElement = activeObjects
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
	public List<DecisionKnowledgeElement> getElementsLinkedWithInwardLinks(DecisionKnowledgeElement decisionKnowledgeElement) {
		List<Link> inwardLinks = this.getInwardLinks(decisionKnowledgeElement);
		List<DecisionKnowledgeElement> children = new ArrayList<DecisionKnowledgeElement>();
		for (Link inwardLink : inwardLinks) {
			children.add(castToDecisionKnowledgeElement(
					activeObjects.executeInTransaction(new TransactionCallback<DecisionKnowledgeElementEntity>() {
						@Override
						public DecisionKnowledgeElementEntity doInTransaction() {
							DecisionKnowledgeElementEntity[] entityList = activeObjects.find(
									DecisionKnowledgeElementEntity.class,
									Query.select().where("ID = ?", inwardLink.getIngoingId()));
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
	public List<DecisionKnowledgeElement> getElementsLinkedWithOutwardLinks(DecisionKnowledgeElement decisionKnowledgeElement) {
		List<Link> outwardLinks = this.getOutwardLinks(decisionKnowledgeElement);
		List<DecisionKnowledgeElement> parents = new ArrayList<DecisionKnowledgeElement>();
		for (Link outwardLink : outwardLinks) {
			parents.add(castToDecisionKnowledgeElement(
					activeObjects.executeInTransaction(new TransactionCallback<DecisionKnowledgeElementEntity>() {
						@Override
						public DecisionKnowledgeElementEntity doInTransaction() {
							DecisionKnowledgeElementEntity[] entityList = activeObjects.find(
									DecisionKnowledgeElementEntity.class,
									Query.select().where("ID = ?", outwardLink.getOutgoingId()));
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
		return activeObjects.executeInTransaction(new TransactionCallback<Long>() {
			@Override
			public Long doInTransaction() {
				boolean linkAlreadyExists = false;
				long linkId = 0;
				for (LinkEntity linkEntity : activeObjects.find(LinkEntity.class)) {
					if (linkEntity.getIngoingId() == link.getIngoingId()
							&& linkEntity.getOutgoingId() == link.getOutgoingId()) {
						linkAlreadyExists = true;
						linkId = linkEntity.getId();
					}
				}
				if (!linkAlreadyExists) {
					DecisionKnowledgeElementEntity decCompIngoing;
					DecisionKnowledgeElementEntity[] decCompIngoingArray = activeObjects.find(
							DecisionKnowledgeElementEntity.class, Query.select().where("ID = ?", link.getIngoingId()));
					if (decCompIngoingArray.length == 1) {
						decCompIngoing = decCompIngoingArray[0];
					} else {
						// entity with ingoingId does not exist
						decCompIngoing = null;
					}

					DecisionKnowledgeElementEntity decCompOutgoing;
					DecisionKnowledgeElementEntity[] decCompOutgoingArray = activeObjects.find(
							DecisionKnowledgeElementEntity.class, Query.select().where("ID = ?", link.getOutgoingId()));
					if (decCompOutgoingArray.length == 1) {
						decCompOutgoing = decCompOutgoingArray[0];
					} else {
						// entity with outgoingId does not exist
						decCompOutgoing = null;
					}
					if (decCompIngoing != null && decCompOutgoing != null) {
						if (decCompIngoing.getProjectKey().equals(decCompOutgoing.getProjectKey())) {
							// entities exist and are in the same project
							final LinkEntity linkEntity = activeObjects.create(LinkEntity.class);
							linkEntity.setIngoingId(link.getIngoingId());
							linkEntity.setOutgoingId(link.getOutgoingId());
							linkEntity.setLinkType(link.getLinkType());
							linkEntity.save();
							linkId = linkEntity.getId();
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
		return activeObjects.executeInTransaction(new TransactionCallback<Boolean>() {
			@Override
			public Boolean doInTransaction() {
				for (LinkEntity linkEntity : activeObjects.find(LinkEntity.class)) {
					if (link.getLinkType() == linkEntity.getLinkType()
							&& link.getIngoingId() == linkEntity.getIngoingId()
							&& link.getOutgoingId() == linkEntity.getOutgoingId()) {
						try {
							linkEntity.getEntityManager().delete(linkEntity);
							return true;
						} catch (SQLException e) {
							LOGGER.error("LinkEntity could not be deleted");
							e.printStackTrace();
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
		LinkEntity[] links = activeObjects.find(LinkEntity.class,
				Query.select().where("OUTGOING_ID = ?", decisionKnowledgeElement.getId()));
		for (LinkEntity link : links) {
			Link inwardLink = new LinkImpl(link);
			inwardLink.setDestinationObject(decisionKnowledgeElement);
			inwardLink.setSourceObject(this.getDecisionKnowledgeElement(link.getIngoingId()));
			inwardLinks.add(inwardLink);
		}
		return inwardLinks;
	}

	@Override
	public List<Link> getOutwardLinks(DecisionKnowledgeElement decisionKnowledgeElement) {
		List<Link> outwardLinks = new ArrayList<>();
		LinkEntity[] links = activeObjects.find(LinkEntity.class,
				Query.select().where("INGOING_ID = ?", decisionKnowledgeElement.getId()));
		for (LinkEntity link : links) {
			Link outwardLink = new LinkImpl(link);
			outwardLink.setSourceObject(decisionKnowledgeElement);
			outwardLink.setDestinationObject(this.getDecisionKnowledgeElement(link.getOutgoingId()));
			outwardLinks.add(outwardLink);
		}
		return outwardLinks;
	}

	// Converting the Entity to a DecisionKnowledgeElementImpl for future use
	private DecisionKnowledgeElement castToDecisionKnowledgeElement(DecisionKnowledgeElementEntity entity) {
		DecisionKnowledgeElement element = new DecisionKnowledgeElementImpl(entity.getId(), entity.getSummary(),
				entity.getDescription(), entity.getType(), entity.getProjectKey(), entity.getKey());
		return element;
	}
}