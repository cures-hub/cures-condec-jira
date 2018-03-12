package de.uhd.ifi.se.decision.documentation.jira.persistence;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.project.ProjectManager;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.sal.api.transaction.TransactionCallback;

import de.uhd.ifi.se.decision.documentation.jira.decisionknowledge.DecisionKnowledgeElement;
import de.uhd.ifi.se.decision.documentation.jira.decisionknowledge.IDecisionKnowledgeElementEntity;
import de.uhd.ifi.se.decision.documentation.jira.decisionknowledge.ILinkEntity;
import de.uhd.ifi.se.decision.documentation.jira.decisionknowledge.Link;
import de.uhd.ifi.se.decision.documentation.jira.util.ComponentGetter;
import net.java.ao.Query;

/**
 * @description Implements the PersistenceStrategy abstract class. Uses the
 *              active object framework to store decision knowledge.
 */
public class ActiveObjectStrategy extends PersistenceStrategy {
	private static final Logger LOGGER = LoggerFactory.getLogger(ActiveObjectStrategy.class);
	private static final ActiveObjects ao = ComponentGetter.getAo();

	@Override
	public DecisionKnowledgeElement insertDecisionKnowledgeElement(DecisionKnowledgeElement dec, ApplicationUser user) {
		if (dec == null) {
			LOGGER.error("AOStrategy insertDecisionKnowledgeElement the DecisionRepresentation is null");
			return null;
		}
		if (user == null) {
			LOGGER.error("AOStrategy insertDecisionKnowledgeElement the ApplicationUser is null");
			return null;
		}
		IDecisionKnowledgeElementEntity decComponent = ao
				.executeInTransaction(new TransactionCallback<IDecisionKnowledgeElementEntity>() {
					@Override
					public IDecisionKnowledgeElementEntity doInTransaction() {
						IDecisionKnowledgeElementEntity decComponent = ao.create(IDecisionKnowledgeElementEntity.class);
						// decComponent.setKey(dec.getProjectKey().toUpperCase() + "-" +
						// decComponent.getId());
						decComponent.setName(dec.getName());
						decComponent.setDescription(dec.getDescription());
						decComponent.setType(dec.getType());
						decComponent.setProjectKey(dec.getProjectKey());
						decComponent.save();
						return decComponent;
					}
				});
		if (decComponent == null) {
			return null;
		}
		dec.setId(decComponent.getId());
		return dec;
	}

	@Override
	public boolean updateDecisionKnowledgeElement(final DecisionKnowledgeElement dec, ApplicationUser user) {
		IDecisionKnowledgeElementEntity decComponent = ao
				.executeInTransaction(new TransactionCallback<IDecisionKnowledgeElementEntity>() {
					@Override
					public IDecisionKnowledgeElementEntity doInTransaction() {
						for (IDecisionKnowledgeElementEntity decComponent : ao
								.find(IDecisionKnowledgeElementEntity.class)) {
							if (decComponent.getId() == dec.getId()) {
								decComponent.setDescription(dec.getDescription());
								decComponent.save();
								return decComponent;
							}
						}
						return null;
					}
				});
		if (decComponent != null) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean deleteDecisionKnowledgeElement(final DecisionKnowledgeElement dec, final ApplicationUser user) {
		return ao.executeInTransaction(new TransactionCallback<Boolean>() {
			@Override
			public Boolean doInTransaction() {
				for (IDecisionKnowledgeElementEntity decComponent : ao.find(IDecisionKnowledgeElementEntity.class)) {
					if (decComponent.getId() == dec.getId()) {
						try {
							decComponent.getEntityManager().delete(decComponent);
						} catch (SQLException e) {
							return false;
						} finally {
							for (ILinkEntity linkEntity : ao.find(ILinkEntity.class)) {
								if (linkEntity.getIngoingId() == dec.getId()
										|| linkEntity.getOutgoingId() == dec.getId()) {
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
			decisionKnowledgeElements = ao
					.executeInTransaction(new TransactionCallback<List<DecisionKnowledgeElement>>() {
						@Override
						public List<DecisionKnowledgeElement> doInTransaction() {
							final List<DecisionKnowledgeElement> decisionKnowledgeElements = new ArrayList<>();
							// Returns all instances of interface IDecisionKnowledgeElementEntity for the
							// given project key
							IDecisionKnowledgeElementEntity[] decisionArray = ao.find(
									IDecisionKnowledgeElementEntity.class,
									Query.select().where("PROJECT_KEY = ?", projectKey));
							for (IDecisionKnowledgeElementEntity entity : decisionArray) {
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
		IDecisionKnowledgeElementEntity decisionKnowledgeElement = ao
				.executeInTransaction(new TransactionCallback<IDecisionKnowledgeElementEntity>() {
					@Override
					public IDecisionKnowledgeElementEntity doInTransaction() {
						IDecisionKnowledgeElementEntity[] decisionKnowledgeElement = ao
								.find(IDecisionKnowledgeElementEntity.class, Query.select().where("KEY = ?", key));
						// 0 or 1 decision knowledge elements might be returned by this query
						if (decisionKnowledgeElement.length == 1) {
							return decisionKnowledgeElement[0];
						}
						return null;
					}
				});
		if (decisionKnowledgeElement != null) {
			return new DecisionKnowledgeElement(decisionKnowledgeElement.getId(), decisionKnowledgeElement.getName(),
					decisionKnowledgeElement.getDescription(), decisionKnowledgeElement.getType(),
					decisionKnowledgeElement.getProjectKey(), decisionKnowledgeElement.getKey(),
					decisionKnowledgeElement.getSummary());
		}
		return null;
	}

	@Override
	public DecisionKnowledgeElement getDecisionKnowledgeElement(long id) {
		IDecisionKnowledgeElementEntity decisionKnowledgeElement = ao
				.executeInTransaction(new TransactionCallback<IDecisionKnowledgeElementEntity>() {
					@Override
					public IDecisionKnowledgeElementEntity doInTransaction() {
						IDecisionKnowledgeElementEntity[] decisionKnowledgeElement = ao
								.find(IDecisionKnowledgeElementEntity.class, Query.select().where("ID = ?", id));
						// 0 or 1 decision knowledge elements might be returned by this query
						if (decisionKnowledgeElement.length == 1) {
							return decisionKnowledgeElement[0];
						}
						return null;
					}
				});
		if (decisionKnowledgeElement != null) {
			return new DecisionKnowledgeElement(decisionKnowledgeElement.getId(), decisionKnowledgeElement.getName(),
					decisionKnowledgeElement.getDescription(), decisionKnowledgeElement.getType(),
					decisionKnowledgeElement.getProjectKey(), decisionKnowledgeElement.getKey(),
					decisionKnowledgeElement.getSummary());
		}
		return null;
	}

	@Override
	public List<DecisionKnowledgeElement> getChildren(DecisionKnowledgeElement decisionKnowledgeElement) {
		List<Link> inwardLinks = this.getInwardLinks(decisionKnowledgeElement);
		List<Link> outwardLinks = this.getOutwardLinks(decisionKnowledgeElement);
		List<DecisionKnowledgeElement> children = new ArrayList<>();

		// //Getting all Inward Element from the Parent Object
		// for(Link inwardLink:inwardLinks)
		// children.add(castToDecisionKnowledgeElement(ao.executeInTransaction(new
		// TransactionCallback<IDecisionKnowledgeElementEntity>() {
		// @Override
		// public IDecisionKnowledgeElementEntity doInTransaction() {
		// IDecisionKnowledgeElementEntity[]
		// entityList=ao.find(IDecisionKnowledgeElementEntity.class,
		// Query.select().where("ID = ?", inwardLink.getIngoingId()));
		// if(entityList.length==1){
		// return entityList[0];
		// }
		// LOGGER.error("Inward Link has no Element to return");
		// return null;
		// }
		// })));
		// Gets all outward elements from the Parent Object
		for (Link outwardLink : outwardLinks) {
			children.add(castToDecisionKnowledgeElement(
					ao.executeInTransaction(new TransactionCallback<IDecisionKnowledgeElementEntity>() {
						@Override
						public IDecisionKnowledgeElementEntity doInTransaction() {
							IDecisionKnowledgeElementEntity[] entityList = ao.find(
									IDecisionKnowledgeElementEntity.class,
									Query.select().where("ID = ?", outwardLink.getOutgoingId()));
							if (entityList.length == 1) {
								return entityList[0];
							}
							LOGGER.error("Outward Link has no Element to return");
							return null;
						}
					})));
		}
		return children;
	}

	@Override
	public List<DecisionKnowledgeElement> getParents(DecisionKnowledgeElement decisionKnowledgeElement) {
		return null;
	}

	@Override
	public List<DecisionKnowledgeElement> getUnlinkedDecisionComponents(final long id, String projectKey) {
		List<DecisionKnowledgeElement> decList = null;
		ProjectManager projectManager = ComponentAccessor.getProjectManager();
		Project project = projectManager.getProjectObjByKey(projectKey);
		if (project != null) {
			decList = ao.executeInTransaction(new TransactionCallback<List<DecisionKnowledgeElement>>() {
				@Override
				public List<DecisionKnowledgeElement> doInTransaction() {
					final List<DecisionKnowledgeElement> decList = new ArrayList<DecisionKnowledgeElement>();
					IDecisionKnowledgeElementEntity[] decisionsArray = ao.find(IDecisionKnowledgeElementEntity.class,
							Query.select().where("ID = ?", id));
					// id is primaryKey for DecisionComponents therefore there can be 0-1
					// decisioncomponent returned by this query
					IDecisionKnowledgeElementEntity decComponent = null;
					if (decisionsArray.length == 1) {
						decComponent = decisionsArray[0];
					}
					if (decComponent != null) {
						final List<IDecisionKnowledgeElementEntity> linkedDecList = new ArrayList<IDecisionKnowledgeElementEntity>();
						for (ILinkEntity link : ao.find(ILinkEntity.class,
								Query.select().where("INGOING_ID != ? AND OUTGOING_ID = ?", id, id))) {
							for (IDecisionKnowledgeElementEntity decisionComponent : ao.find(
									IDecisionKnowledgeElementEntity.class,
									Query.select().where("ID = ? AND PROJECT_KEY = ?", link.getIngoingId(),
											decComponent.getProjectKey()))) {
								linkedDecList.add(decisionComponent);
							}
						}
						for (ILinkEntity link : ao.find(ILinkEntity.class,
								Query.select().where("INGOING_ID = ? AND OUTGOING_ID != ?", id, id))) {
							for (IDecisionKnowledgeElementEntity decisionComponent : ao.find(
									IDecisionKnowledgeElementEntity.class,
									Query.select().where("ID = ? AND PROJECT_KEY = ?", link.getOutgoingId(),
											decComponent.getProjectKey()))) {
								linkedDecList.add(decisionComponent);
							}
						}
						IDecisionKnowledgeElementEntity[] decisionArray = ao.find(IDecisionKnowledgeElementEntity.class,
								Query.select().where("ID != ? AND PROJECT_KEY = ?", id, decComponent.getProjectKey()));
						for (IDecisionKnowledgeElementEntity decisionComponent : decisionArray) {
							if (!linkedDecList.contains(decisionComponent)) {
								DecisionKnowledgeElement simpleDec = new DecisionKnowledgeElement();
								simpleDec.setId(decisionComponent.getId());
								simpleDec.setSummary(decisionComponent.getKey() + " / " + decisionComponent.getName()
										+ " / " + decisionComponent.getType());
								decList.add(simpleDec);
							}
						}
					}
					return decList;
				}
			});
		}
		return decList;
	}

	@Override
	public long insertLink(final Link link, ApplicationUser user) {
		return ao.executeInTransaction(new TransactionCallback<Long>() {
			@Override
			public Long doInTransaction() {
				boolean linkAlreadyExists = false;
				long linkId = 0;
				for (ILinkEntity linkEntity : ao.find(ILinkEntity.class)) {
					if (linkEntity.getIngoingId() == link.getIngoingId()
							&& linkEntity.getOutgoingId() == link.getOutgoingId()) {
						linkAlreadyExists = true;
						linkId = linkEntity.getId();
					}
				}
				if (!linkAlreadyExists) {
					IDecisionKnowledgeElementEntity decCompIngoing;
					IDecisionKnowledgeElementEntity[] decCompIngoingArray = ao.find(
							IDecisionKnowledgeElementEntity.class, Query.select().where("ID = ?", link.getIngoingId()));
					if (decCompIngoingArray.length == 1) {
						decCompIngoing = decCompIngoingArray[0];
					} else {
						// entity with ingoingId does not exist
						decCompIngoing = null;
					}

					IDecisionKnowledgeElementEntity decCompOutgoing;
					IDecisionKnowledgeElementEntity[] decCompOutgoingArray = ao.find(
							IDecisionKnowledgeElementEntity.class,
							Query.select().where("ID = ?", link.getOutgoingId()));
					if (decCompOutgoingArray.length == 1) {
						decCompOutgoing = decCompOutgoingArray[0];
					} else {
						// entity with outgoingId does not exist
						decCompOutgoing = null;
					}
					if (decCompIngoing != null && decCompOutgoing != null) {
						if (decCompIngoing.getProjectKey().equals(decCompOutgoing.getProjectKey())) {
							// entities exist and are in the same project
							final ILinkEntity linkEntity = ao.create(ILinkEntity.class);
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
	public void deleteLink(Link link, ApplicationUser user) {
		ao.executeInTransaction(new TransactionCallback<Void>() {
			@Override
			public Void doInTransaction() {
				for (ILinkEntity linkEntity : ao.find(ILinkEntity.class)) {
					if (link.getLinkType() == linkEntity.getLinkType()
							&& link.getIngoingId() == linkEntity.getIngoingId()
							&& link.getOutgoingId() == linkEntity.getOutgoingId()) {
						try {
							linkEntity.getEntityManager().delete(linkEntity);
						} catch (SQLException e) {
							LOGGER.error("ILinkEntity could not be deleted");
							e.printStackTrace();
						}
					}
				}
				return null;
			}
		});
	}

	@Override
	public List<Link> getInwardLinks(DecisionKnowledgeElement element) {
		List<Link> inwardLinks = new ArrayList<>();
		ILinkEntity[] links = ao.find(ILinkEntity.class, Query.select().where("OUTGOING_ID = ?", element.getId()));
		for (ILinkEntity link : links) {
			Link inwardLink = new Link(link);
			inwardLinks.add(inwardLink);
		}
		return inwardLinks;
	}

	@Override
	public List<Link> getOutwardLinks(DecisionKnowledgeElement element) {
		List<Link> outwardLinks = new ArrayList<>();
		ILinkEntity[] links = ao.find(ILinkEntity.class, Query.select().where("INGOING_ID = ?", element.getId()));
		for (ILinkEntity link : links) {
			Link outwardLink = new Link(link);
			outwardLinks.add(outwardLink);
		}
		return outwardLinks;
	}

	// Converting the Entity to a DecisionKnowledgeElement for future use
	private DecisionKnowledgeElement castToDecisionKnowledgeElement(IDecisionKnowledgeElementEntity entity) {
		DecisionKnowledgeElement element = new DecisionKnowledgeElement(entity.getId(), entity.getName(),
				entity.getDescription(), entity.getType(), entity.getProjectKey(), entity.getKey(),
				entity.getSummary());
		return element;
	}
}