package de.uhd.ifi.se.decision.documentation.jira.persistence;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.project.ProjectManager;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.sal.api.transaction.TransactionCallback;
import com.google.common.collect.ImmutableMap;

import de.uhd.ifi.se.decision.documentation.jira.view.treants.Node;
import de.uhd.ifi.se.decision.documentation.jira.view.treeviewer.Core;
import de.uhd.ifi.se.decision.documentation.jira.view.treeviewer.Data;
import de.uhd.ifi.se.decision.documentation.jira.view.treeviewer.NodeInfo;
import de.uhd.ifi.se.decision.documentation.jira.decisionknowledge.IDecisionKnowledgeElementEntity;
import de.uhd.ifi.se.decision.documentation.jira.decisionknowledge.DecisionKnowledgeElement;
import de.uhd.ifi.se.decision.documentation.jira.decisionknowledge.ILinkEntity;
import de.uhd.ifi.se.decision.documentation.jira.decisionknowledge.Link;
import de.uhd.ifi.se.decision.documentation.jira.util.ComponentGetter;
import de.uhd.ifi.se.decision.documentation.jira.util.KeyValuePairList;
import de.uhd.ifi.se.decision.documentation.jira.util.Pair;
import net.java.ao.Query;

/**
 * @author Ewald Rode
 * @description Implements the IPersistenceStrategy interface. Uses the active
 *              object framework to store decision knowledge.
 */
public class ActiveObjectStrategy implements IPersistenceStrategy {
	private static final Logger LOGGER = LoggerFactory.getLogger(ActiveObjectStrategy.class);

	@Override
	public Data createDecisionComponent(final DecisionKnowledgeElement dec, ApplicationUser user) {
		if (dec == null) {
			LOGGER.error("AOStrategy createDecisionComponent the DecisionRepresentation is null");
			return null;
		}
		if (user == null) {
			LOGGER.error("AOStrategy createDecisionComponent the ApplicationUser is null");
			return null;
		}
		final ActiveObjects ao = ComponentGetter.getAo();
		System.out.println(ao);
		IDecisionKnowledgeElementEntity decComponent = ao
				.executeInTransaction(new TransactionCallback<IDecisionKnowledgeElementEntity>() {
					@Override
					public IDecisionKnowledgeElementEntity doInTransaction() {
						final IDecisionKnowledgeElementEntity decComponent = ao
								.create(IDecisionKnowledgeElementEntity.class);
						decComponent.setKey(dec.getProjectKey().toUpperCase() + "-" + decComponent.getID());
						decComponent.setName(dec.getName());
						decComponent.setDescription(dec.getDescription());
						decComponent.setType(dec.getType());
						decComponent.setProjectKey(dec.getProjectKey());
						decComponent.save();
						return decComponent;
					}
				});
		if (decComponent != null) {
			Data data = new Data();

			data.setText(decComponent.getKey() + " / " + decComponent.getName());
			data.setId(String.valueOf(decComponent.getID()));

			NodeInfo nodeInfo = new NodeInfo();
			nodeInfo.setId(Long.toString(decComponent.getID()));
			nodeInfo.setKey(decComponent.getKey());
			nodeInfo.setIssueType(decComponent.getType());
			nodeInfo.setDescription(decComponent.getDescription());
			nodeInfo.setSummary(decComponent.getName());
			data.setNodeInfo(nodeInfo);

			return data;
		} else {
			return null;
		}
	}

	@Override
	public Data editDecisionComponent(final DecisionKnowledgeElement dec, ApplicationUser user) {
		final ActiveObjects ao = ComponentGetter.getAo();
		IDecisionKnowledgeElementEntity decComponent = ao
				.executeInTransaction(new TransactionCallback<IDecisionKnowledgeElementEntity>() {
					@Override
					public IDecisionKnowledgeElementEntity doInTransaction() {
						for (IDecisionKnowledgeElementEntity decComponent : ao
								.find(IDecisionKnowledgeElementEntity.class)) {
							if (decComponent.getID() == dec.getId()) {
								decComponent.setDescription(dec.getDescription());
								decComponent.save();
								return decComponent;
							}
						}
						return null;
					}
				});
		if (decComponent != null) {
			Data data = new Data();

			data.setText(decComponent.getKey() + " / " + decComponent.getName());
			data.setId(String.valueOf(decComponent.getID()));

			NodeInfo nodeInfo = new NodeInfo();
			nodeInfo.setId(Long.toString(decComponent.getID()));
			nodeInfo.setKey(decComponent.getKey());
			nodeInfo.setIssueType(decComponent.getType());
			nodeInfo.setDescription(decComponent.getDescription());
			nodeInfo.setSummary(decComponent.getName());
			data.setNodeInfo(nodeInfo);

			return data;
		} else {
			return null;
		}
	}

	@Override
	public boolean deleteDecisionComponent(final DecisionKnowledgeElement dec, final ApplicationUser user) {
		final ActiveObjects ao = ComponentGetter.getAo();
		return ao.executeInTransaction(new TransactionCallback<Boolean>() {
			@Override
			public Boolean doInTransaction() {
				for (IDecisionKnowledgeElementEntity decComponent : ao.find(IDecisionKnowledgeElementEntity.class)) {
					if (decComponent.getID() == dec.getId()) {
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
	public Long createLink(final Link link, ApplicationUser user) {
		final ActiveObjects ao = ComponentGetter.getAo();
		return ao.executeInTransaction(new TransactionCallback<Long>() {
			@Override
			public Long doInTransaction() {
				boolean linkAlreadyExists = false;
				long linkId = 0;
				for (ILinkEntity linkEntity : ao.find(ILinkEntity.class)) {
					if (linkEntity.getIngoingId() == link.getIngoingId()
							&& linkEntity.getOutgoingId() == link.getOutgoingId()) {
						linkAlreadyExists = true;
						linkId = linkEntity.getID();
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
							linkId = linkEntity.getID();
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
	public List<DecisionKnowledgeElement> getUnlinkedDecisionComponents(final long id, String projectKey) {
		List<DecisionKnowledgeElement> decList = null;
		ProjectManager projectManager = ComponentAccessor.getProjectManager();
		Project project = projectManager.getProjectObjByKey(projectKey);
		if (project != null) {
			final ActiveObjects ao = ComponentGetter.getAo();
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
								simpleDec.setId(decisionComponent.getID());
								simpleDec.setText(decisionComponent.getKey() + " / " + decisionComponent.getName()
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

	/* TreeViewerRest */
	@Override
	public Core createCore(Project project) {
		Core core = new Core();
		core.setMultiple(false);
		core.setCheckCallback(true);
		core.setThemes(ImmutableMap.of("icons", false));
		final ActiveObjects ao = ComponentGetter.getAo();
		final HashSet<Data> dataSet = new HashSet<Data>();
		ao.executeInTransaction(new TransactionCallback<Void>() {
			@Override
			public Void doInTransaction() {
				for (IDecisionKnowledgeElementEntity decComponent : ao.find(IDecisionKnowledgeElementEntity.class)) {
					if (decComponent.getType().equalsIgnoreCase("Decision")) {
						KeyValuePairList.keyValuePairList = new ArrayList<Pair<String, String>>();
						Pair<String, String> kvp = new Pair<String, String>("root",
								Long.toString(decComponent.getID()));
						KeyValuePairList.keyValuePairList.add(kvp);
						dataSet.add(createData(decComponent));
					}
				}
				return null;
			}
		});
		core.setData(dataSet);
		return core;
	}

	// New Implementation
	@Override
	public DecisionKnowledgeElement getDecisionKnowledgeElement(String key) {
		final ActiveObjects ao = ComponentGetter.getAo();
		IDecisionKnowledgeElementEntity dec = ao
				.executeInTransaction(new TransactionCallback<IDecisionKnowledgeElementEntity>() {
					@Override
					public IDecisionKnowledgeElementEntity doInTransaction() {
						IDecisionKnowledgeElementEntity[] decisionsArray = ao
								.find(IDecisionKnowledgeElementEntity.class, Query.select().where("KEY = ?", key));
						// id is primaryKey for DecisionComponents therefore there can be 0-1
						// decisioncomponent returned by this query
						IDecisionKnowledgeElementEntity decComponent = null;
						if (decisionsArray.length == 1) {
							decComponent = decisionsArray[0];
						}
						return decComponent;
					}
				});
		if (dec != null) {
			DecisionKnowledgeElement decisionKnowledgeElement = new DecisionKnowledgeElement(dec.getID(),dec.getName(),
					dec.getDescription(),dec.getType(),dec.getProjectKey(),dec.getKey(),dec.getSummary());
			return decisionKnowledgeElement;
		}
		return null;
	}

	private Data createData(final IDecisionKnowledgeElementEntity decComponent) {
		Data data = new Data();

		data.setText(decComponent.getKey() + " / " + decComponent.getName());
		data.setId(String.valueOf(decComponent.getID()));

		NodeInfo nodeInfo = new NodeInfo();
		nodeInfo.setId(Long.toString(decComponent.getID()));
		nodeInfo.setKey(decComponent.getKey());
		nodeInfo.setIssueType(decComponent.getType());
		nodeInfo.setDescription(decComponent.getDescription());
		nodeInfo.setSummary(decComponent.getName());
		data.setNodeInfo(nodeInfo);

		List<Data> children = new ArrayList<Data>();
		final ActiveObjects ao = ComponentGetter.getAo();
		List<IDecisionKnowledgeElementEntity> targetList = ao
				.executeInTransaction(new TransactionCallback<List<IDecisionKnowledgeElementEntity>>() {
					@Override
					public List<IDecisionKnowledgeElementEntity> doInTransaction() {
						final List<IDecisionKnowledgeElementEntity> decisionList = new ArrayList<IDecisionKnowledgeElementEntity>();
						for (ILinkEntity link : ao.find(ILinkEntity.class,
								Query.select().where("INGOING_ID = ?", decComponent.getID()))) {
							for (IDecisionKnowledgeElementEntity dec : ao.find(IDecisionKnowledgeElementEntity.class,
									Query.select().where("ID = ?", link.getOutgoingId()))) {
								decisionList.add(dec);
							}
						}
						return decisionList;
					}
				});
		for (IDecisionKnowledgeElementEntity target : targetList) {
			Pair<String, String> newKVP = new Pair<String, String>(decComponent.getKey(), target.getKey());
			Pair<String, String> newKVPReverse = new Pair<String, String>(target.getKey(), decComponent.getKey());
			boolean boolvar = false;
			for (int counter = 0; counter < KeyValuePairList.keyValuePairList.size(); ++counter) {
				Pair<String, String> globalInst = KeyValuePairList.keyValuePairList.get(counter);
				if (newKVP.equals(globalInst)) {
					boolvar = true;
				}
			}
			if (!boolvar) {
				KeyValuePairList.keyValuePairList.add(newKVP);
				KeyValuePairList.keyValuePairList.add(newKVPReverse);
				children.add(createData(target));
			}
		}

		List<IDecisionKnowledgeElementEntity> sourceList = ao
				.executeInTransaction(new TransactionCallback<List<IDecisionKnowledgeElementEntity>>() {
					@Override
					public List<IDecisionKnowledgeElementEntity> doInTransaction() {
						final List<IDecisionKnowledgeElementEntity> decisionList = new ArrayList<IDecisionKnowledgeElementEntity>();
						for (ILinkEntity link : ao.find(ILinkEntity.class,
								Query.select().where("OUTGOING_ID = ?", decComponent.getID()))) {
							for (IDecisionKnowledgeElementEntity dec : ao.find(IDecisionKnowledgeElementEntity.class,
									Query.select().where("ID = ?", link.getIngoingId()))) {
								decisionList.add(dec);
							}
						}
						return decisionList;
					}
				});
		for (IDecisionKnowledgeElementEntity source : sourceList) {
			Pair<String, String> newKVP = new Pair<String, String>(decComponent.getKey(), source.getKey());
			Pair<String, String> newKVPReverse = new Pair<String, String>(source.getKey(), decComponent.getKey());
			boolean boolvar = false;
			for (int counter = 0; counter < KeyValuePairList.keyValuePairList.size(); ++counter) {
				Pair<String, String> globalInst = KeyValuePairList.keyValuePairList.get(counter);
				if (newKVP.equals(globalInst)) {
					boolvar = true;
				}
			}
			if (!boolvar) {
				KeyValuePairList.keyValuePairList.add(newKVP);
				KeyValuePairList.keyValuePairList.add(newKVPReverse);
				children.add(createData(source));
			}
		}

		data.setChildren(children);

		return data;
	}

	@Override
	public List<DecisionKnowledgeElement> getDecisionsInProject(Project project) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<DecisionKnowledgeElement> getChildren(DecisionKnowledgeElement decisionKnowledgeElement) {
		// TODO Auto-generated method stub
		return null;
	}
}