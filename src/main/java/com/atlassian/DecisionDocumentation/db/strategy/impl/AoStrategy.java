package com.atlassian.DecisionDocumentation.db.strategy.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atlassian.DecisionDocumentation.db.DecisionComponentEntity;
import com.atlassian.DecisionDocumentation.db.LinkEntity;
import com.atlassian.DecisionDocumentation.db.strategy.Strategy;
import com.atlassian.DecisionDocumentation.rest.Decisions.model.DecisionRepresentation;
import com.atlassian.DecisionDocumentation.rest.Decisions.model.LinkRepresentation;
import com.atlassian.DecisionDocumentation.rest.Decisions.model.SimpleDecisionRepresentation;
import com.atlassian.DecisionDocumentation.rest.treants.TreantKeyValuePairList;
import com.atlassian.DecisionDocumentation.rest.treants.model.Chart;
import com.atlassian.DecisionDocumentation.rest.treants.model.Node;
import com.atlassian.DecisionDocumentation.rest.treants.model.Treant;
import com.atlassian.DecisionDocumentation.rest.treeviewer.TreeViewerKVPairList;
import com.atlassian.DecisionDocumentation.rest.treeviewer.model.Core;
import com.atlassian.DecisionDocumentation.rest.treeviewer.model.Data;
import com.atlassian.DecisionDocumentation.rest.treeviewer.model.NodeInfo;
import com.atlassian.DecisionDocumentation.util.ComponentGetter;
import com.atlassian.DecisionDocumentation.util.Pair;
import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.config.properties.APKeys;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.project.ProjectManager;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.sal.api.transaction.TransactionCallback;
import com.google.common.collect.ImmutableMap;

import net.java.ao.Query;

/**
 * @author Ewald Rode
 * @description
 */
public class AoStrategy implements Strategy {
	private static final Logger LOGGER = LoggerFactory.getLogger(AoStrategy.class);
	//TODO use LOGGER
	@Override
	public Data createDecisionComponent(final DecisionRepresentation dec, ApplicationUser user) {
		final ActiveObjects ao = ComponentGetter.getAo();
		DecisionComponentEntity decComponent = ao.executeInTransaction(new TransactionCallback<DecisionComponentEntity>()
        {
            @Override
            public DecisionComponentEntity doInTransaction()
            {
                final DecisionComponentEntity decComponent = ao.create(DecisionComponentEntity.class);
                decComponent.setKey(dec.getProjectKey().toUpperCase() + "-" + decComponent.getID());
                decComponent.setName(dec.getName());
                decComponent.setDescription(dec.getDescription());
                decComponent.setType(dec.getType());
                decComponent.setProjectKey(dec.getProjectKey());
                decComponent.save();
                return decComponent;
            }
        });
		if(decComponent != null) {
			Data data = new Data();
			
			data.setText(decComponent.getKey() + " / " + decComponent.getName());
			data.setId(String.valueOf(decComponent.getID()));
			
			NodeInfo nodeInfo = new NodeInfo();
			nodeInfo.setId(Long.toString(decComponent.getID()));
			nodeInfo.setKey(decComponent.getKey());
			nodeInfo.setSelfUrl(ComponentAccessor.getApplicationProperties().getString(APKeys.JIRA_BASEURL) + "/rest/api/latest/issue/" + decComponent.getID());//TODO change
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
	public void editDecisionComponent(final DecisionRepresentation dec, ApplicationUser user) {
		final ActiveObjects ao = ComponentGetter.getAo();
		ao.executeInTransaction(new TransactionCallback<Void>()
        {
			@Override
            public Void doInTransaction()
            {
				for (DecisionComponentEntity decComponent : ao.find(DecisionComponentEntity.class))
                {
                    if(decComponent.getID() == dec.getId()) {
                    	decComponent.setDescription(dec.getDescription());
                    	decComponent.save();
                    }
                }
                return null;
            }
        });
	}
	//TODO implement
	@Override
	public void deleteDecisionComponent(DecisionRepresentation dec, ApplicationUser user) {
		
	}

	@Override
	public Long createLink(final LinkRepresentation link, ApplicationUser user) {
		final ActiveObjects ao = ComponentGetter.getAo();
		return ao.executeInTransaction(new TransactionCallback<Long>()
        {
            @Override
            public Long doInTransaction()
            {
            	boolean linkAlreadyExists = false;
            	long linkId = 0;
                for (LinkEntity linkEntity : ao.find(LinkEntity.class))
                {
                	if(linkEntity.getIngoingId() == link.getIngoingId() && linkEntity.getOutgoingId() == link.getOutgoingId()) {
                		linkAlreadyExists = true;
                		linkId = linkEntity.getID();
                	}
                }
                if(!linkAlreadyExists) {
                	DecisionComponentEntity decCompIngoing;
                	DecisionComponentEntity[] decCompIngoingArray = ao.find(DecisionComponentEntity.class, Query.select().where("ID = ?", link.getIngoingId()));
                	if(decCompIngoingArray.length == 1) {
                		decCompIngoing = decCompIngoingArray[0];
                	} else {
                		//entity with ingoingId does not exist
                		decCompIngoing = null;
                	}
                	
                	DecisionComponentEntity decCompOutgoing;
                	DecisionComponentEntity[] decCompOutgoingArray = ao.find(DecisionComponentEntity.class, Query.select().where("ID = ?", link.getOutgoingId()));
                	if(decCompOutgoingArray.length == 1) {
                		decCompOutgoing = decCompOutgoingArray[0];
                	} else {
                		//entity with outgoingId does not exist
                		decCompOutgoing = null;
                	}
                	if(decCompIngoing != null && decCompOutgoing != null) {
                		if(decCompIngoing.getProjectKey().equals(decCompOutgoing.getProjectKey())) {
                			// entities exist and are in the same project
                        	final LinkEntity linkEntity = ao.create(LinkEntity.class);
                        	linkEntity.setIngoingId(link.getIngoingId());
                        	linkEntity.setOutgoingId(link.getOutgoingId());
                        	linkEntity.setType(link.getLinkType());
                        	linkEntity.save();
                        	linkId = linkEntity.getID();
                		} else {
                			// entities to be linked are not in the same project, TODO ignore request
                			return (long) 0;
                		}
                	} else {
                		// one of the entities to be linked does not exist, TODO ignore request
                		return (long) 0;
                	}
                } else {
                	return linkId;
                }
                return linkId;
            }
        });
	}
	//TODO implement
	@Override
	public void deleteLink(LinkRepresentation link, ApplicationUser user) {
		
	}
	//TODO TEST
	@Override
	public List<SimpleDecisionRepresentation> searchUnlinkedDecisionComponents(final long id, String projectKey) {
		List<SimpleDecisionRepresentation> decList = null;
		ProjectManager projectManager = ComponentAccessor.getProjectManager();
		Project project = projectManager.getProjectObjByKey(projectKey);
		if (project != null) {
			final ActiveObjects ao = ComponentGetter.getAo();
			decList = ao.executeInTransaction(new TransactionCallback<List<SimpleDecisionRepresentation>>(){
				@Override
	            public List<SimpleDecisionRepresentation> doInTransaction(){
					final List<SimpleDecisionRepresentation> decList = new ArrayList<SimpleDecisionRepresentation>();
					DecisionComponentEntity[] decisionsArray = ao.find(DecisionComponentEntity.class, Query.select().where("ID = ?", id));
					//id is primaryKey for DecisionComponents therefore there can be 0-1 decisioncomponent returned by this query
					DecisionComponentEntity decComponent = null;
					if (decisionsArray.length == 1){
						decComponent = decisionsArray[0];
	                }
					if(decComponent != null) {
						final List<DecisionComponentEntity> linkedDecList = new ArrayList<DecisionComponentEntity>();
						for(LinkEntity link : ao.find(LinkEntity.class, Query.select().where("INGOING_ID != ? AND OUTGOING_ID = ?", id, id))) {
							for(DecisionComponentEntity decisionComponent : ao.find(DecisionComponentEntity.class, 
									Query.select().where("ID = ? AND PROJECT_KEY = ?", link.getIngoingId(), decComponent.getProjectKey()))){
								linkedDecList.add(decisionComponent);
							}
						}
						for(LinkEntity link : ao.find(LinkEntity.class, Query.select().where("INGOING_ID = ? AND OUTGOING_ID != ?", id, id))) {
							for(DecisionComponentEntity decisionComponent : ao.find(DecisionComponentEntity.class, 
									Query.select().where("ID = ? AND PROJECT_KEY = ?", link.getOutgoingId(), decComponent.getProjectKey()))){
								linkedDecList.add(decisionComponent);
							}
						}
						DecisionComponentEntity[] decisionArray = ao.find(DecisionComponentEntity.class, 
								Query.select().where("ID != ? AND PROJECT_KEY = ?", id, decComponent.getProjectKey()));
						for(DecisionComponentEntity decisionComponent: decisionArray) {
							if(!linkedDecList.contains(decisionComponent)) {
								SimpleDecisionRepresentation simpleDec = new SimpleDecisionRepresentation();
								simpleDec.setId(decisionComponent.getID());
								simpleDec.setText(decisionComponent.getKey() + " / " + decisionComponent.getName() + " / " + decisionComponent.getType());
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
	public Core createCore(Project project) {
		Core core = new Core();
		core.setMultiple(false);
		core.setCheck_callback(true);
		core.setThemes(ImmutableMap.of("icons", false));
		final ActiveObjects ao = ComponentGetter.getAo();
		final HashSet<Data> dataSet =  new HashSet<Data>();
		ao.executeInTransaction(new TransactionCallback<Void>()
        {
            @Override
            public Void doInTransaction()
            {
                for (DecisionComponentEntity decComponent : ao.find(DecisionComponentEntity.class))
                {
                    if(decComponent.getType().equalsIgnoreCase("Decision")) {
                    	TreeViewerKVPairList.kvpList = new ArrayList<Pair<String, String>>();
            			Pair<String,String> kvp = new Pair<String,String>("root", Long.toString(decComponent.getID()));
            			TreeViewerKVPairList.kvpList.add(kvp);
                    	dataSet.add(createData(decComponent));
                    }
                }
                return null;
            }
        });
		core.setData(dataSet);
		return core;
	}
	
	private Data createData(final DecisionComponentEntity decComponent) {
		Data data = new Data();
		
		data.setText(decComponent.getKey() + " / " + decComponent.getName());
		data.setId(String.valueOf(decComponent.getID()));
		
		NodeInfo nodeInfo = new NodeInfo();
		nodeInfo.setId(Long.toString(decComponent.getID()));
		nodeInfo.setKey(decComponent.getKey());
		nodeInfo.setSelfUrl(ComponentAccessor.getApplicationProperties().getString(APKeys.JIRA_BASEURL) + "/rest/api/latest/issue/" + decComponent.getID());//TODO change
		nodeInfo.setIssueType(decComponent.getType());
		nodeInfo.setDescription(decComponent.getDescription());
		nodeInfo.setSummary(decComponent.getName());
		data.setNodeInfo(nodeInfo);
		
		
		List<Data> children = new ArrayList<Data>();
		final ActiveObjects ao = ComponentGetter.getAo();
		List<DecisionComponentEntity> targetList = ao.executeInTransaction(new TransactionCallback<List<DecisionComponentEntity>>()
        {
            @Override
            public List<DecisionComponentEntity> doInTransaction()
            {
                final List<DecisionComponentEntity> decisionList = new ArrayList<DecisionComponentEntity>();
                for (LinkEntity link : ao.find(LinkEntity.class, Query.select().where("INGOING_ID = ?", decComponent.getID()))) {
            		for (DecisionComponentEntity dec : ao.find(DecisionComponentEntity.class, Query.select().where("ID = ?", link.getOutgoingId()))) {
                		decisionList.add(dec);
                    }
                }
                return decisionList;
            }
        });
		for (DecisionComponentEntity target : targetList) {
			Pair<String, String> newKVP = new Pair<String, String>(decComponent.getKey(), target.getKey());
			Pair<String, String> newKVPReverse = new Pair<String, String>(target.getKey(), decComponent.getKey());
			boolean boolvar = false;
			for(int counter = 0; counter<TreeViewerKVPairList.kvpList.size(); ++counter){
				Pair<String, String> globalInst = TreeViewerKVPairList.kvpList.get(counter);
				if (newKVP.equals(globalInst)){
					boolvar = true;
				}
			}
			if(!boolvar){
				TreeViewerKVPairList.kvpList.add(newKVP);
				TreeViewerKVPairList.kvpList.add(newKVPReverse);
				children.add(createData(target));
			}
		}
		
		List<DecisionComponentEntity> sourceList = ao.executeInTransaction(new TransactionCallback<List<DecisionComponentEntity>>()
        {
            @Override
            public List<DecisionComponentEntity> doInTransaction()
            {
            	final List<DecisionComponentEntity> decisionList = new ArrayList<DecisionComponentEntity>();
                for (LinkEntity link : ao.find(LinkEntity.class, Query.select().where("OUTGOING_ID = ?", decComponent.getID()))) {
                	for (DecisionComponentEntity dec : ao.find(DecisionComponentEntity.class, Query.select().where("ID = ?", link.getIngoingId()))) {
                		decisionList.add(dec);
                    }
                }
                return decisionList;
            }
        });
		for (DecisionComponentEntity source : sourceList) {
			Pair<String, String> newKVP = new Pair<String, String>(decComponent.getKey(), source.getKey());
			Pair<String, String> newKVPReverse = new Pair<String, String>(source.getKey(), decComponent.getKey());
			boolean boolvar = false;
			for(int counter = 0; counter<TreeViewerKVPairList.kvpList.size(); ++counter){
				Pair<String, String> globalInst = TreeViewerKVPairList.kvpList.get(counter);
				if (newKVP.equals(globalInst)){
					boolvar = true;
				}
			}
			if(!boolvar){
				TreeViewerKVPairList.kvpList.add(newKVP);
				TreeViewerKVPairList.kvpList.add(newKVPReverse);
				children.add(createData(source));
			}
		}
		
		data.setChildren(children);
		
		return data;
	}

	@Override
	public Treant createTreant(final String issueKey, int depth) {
		Treant treant = new Treant();
		treant.setChart(new Chart());
		treant.setNodeStructure(createNodeStructure(issueKey, depth));
		return treant;
	}
	
	private Node createNodeStructure(final String issueKey, final int depth) {
		Node node = new Node();
		final ActiveObjects ao = ComponentGetter.getAo();
		DecisionComponentEntity dec = ao.executeInTransaction(new TransactionCallback<DecisionComponentEntity>(){
			@Override
            public DecisionComponentEntity doInTransaction(){
				DecisionComponentEntity[] decisionsArray = ao.find(DecisionComponentEntity.class, Query.select().where("KEY = ?", issueKey));
				//id is primaryKey for DecisionComponents therefore there can be 0-1 decisioncomponent returned by this query
				DecisionComponentEntity decComponent = null;
				if (decisionsArray.length == 1){
					decComponent = decisionsArray[0];
                }
                return decComponent;
            }
        });
		
		if (dec != null) {
			Map<String, String> nodeContent = ImmutableMap.of("name", dec.getKey() + " / " + dec.getName(),
					"title", dec.getType());
			node.setNodeContent(nodeContent);
			
			//Map<String, String> link = ImmutableMap.of("href", ComponentAccessor.getApplicationProperties().getString(APKeys.JIRA_BASEURL) + 
			//		"/browse/" + dec.getKey()); //TODO change
					//node.setLink(link);
			
			String htmlClass;
			String issueType = dec.getType().toLowerCase();
			if (issueType.equals("constraint")||issueType.equals("assumption")||issueType.equals("implication")||issueType.equals("context")){
				htmlClass="context";
			} else if (issueType.equals("problem")||issueType.equals("issue")||issueType.equals("goal")){
				htmlClass="problem";
			} else if (issueType.equals("solution")||issueType.equals("claim")||issueType.equals("alternative")){
				htmlClass="solution";
			} else {
				htmlClass="rationale";
			}
			node.setHtmlClass(htmlClass);
			
			long htmlId = dec.getID();
			node.setHtmlId(htmlId);
			
			List<Node> children = new ArrayList<Node>();
			TreantKeyValuePairList.kvpList = new ArrayList<Pair<String, String>>();
			final List<DecisionComponentEntity> inwardLinkedDecList = new ArrayList<DecisionComponentEntity>();
			for(LinkEntity linkEntity : ao.find(LinkEntity.class, Query.select().where("INGOING_ID != ? AND OUTGOING_ID = ?", dec.getID(), dec.getID()))) {
				for(DecisionComponentEntity decisionComponent : ao.find(DecisionComponentEntity.class, 
						Query.select().where("ID = ? AND PROJECT_KEY = ?", linkEntity.getIngoingId(), dec.getProjectKey()))){
					inwardLinkedDecList.add(decisionComponent);
				}
			}
			
			final List<DecisionComponentEntity> outwardLinkedDecList = new ArrayList<DecisionComponentEntity>();
			for(LinkEntity linkEntity : ao.find(LinkEntity.class, Query.select().where("INGOING_ID = ? AND OUTGOING_ID != ?", dec.getID(), dec.getID()))) {
				for(DecisionComponentEntity decisionComponent : ao.find(DecisionComponentEntity.class, 
						Query.select().where("ID = ? AND PROJECT_KEY = ?", linkEntity.getOutgoingId(), dec.getProjectKey()))){
					outwardLinkedDecList.add(decisionComponent);
				}
			}
			
			if(inwardLinkedDecList.size()>0){
				for (int i=0; i<inwardLinkedDecList.size(); i++) {
					DecisionComponentEntity decisionComponent = inwardLinkedDecList.get(i);
					Pair<String,String> kvp = new Pair<String,String>(dec.getKey(), decisionComponent.getKey());
					Pair<String,String> kvp2 = new Pair<String,String>(decisionComponent.getKey(), dec.getKey());
					TreantKeyValuePairList.kvpList.add(kvp);
					TreantKeyValuePairList.kvpList.add(kvp2);
					children.add(createNode(decisionComponent, depth, 0));
				}
			}
			
			if(outwardLinkedDecList.size()>0){
				for (int i=0; i<outwardLinkedDecList.size(); i++) {
					DecisionComponentEntity decisionComponent = outwardLinkedDecList.get(i);
					Pair<String,String> kvp = new Pair<String,String>(dec.getKey(), decisionComponent.getKey());
					Pair<String,String> kvp2 = new Pair<String,String>(decisionComponent.getKey(), dec.getKey());
					TreantKeyValuePairList.kvpList.add(kvp);
					TreantKeyValuePairList.kvpList.add(kvp2);
					children.add(createNode(decisionComponent, depth, 0));
				}
			}
			
			node.setChildren(children);
		}		
		return node;
	}
	
	private Node createNode(DecisionComponentEntity dec, int depth, int currentDepth) {
		Node node = new Node();
		final ActiveObjects ao = ComponentGetter.getAo();
		if (dec != null) {
			Map<String, String> nodeContent = ImmutableMap.of("name", dec.getName(),
					"title", dec.getType(),
					"desc", dec.getKey());
			node.setNodeContent(nodeContent);
			
			//Map<String, String> link = ImmutableMap.of("href", ComponentAccessor.getApplicationProperties().getString(APKeys.JIRA_BASEURL) + 
			//		"/browse/" + dec.getKey()); //TODO change
			//node.setLink(link);
			
			String htmlClass;
			String issueType = dec.getType().toLowerCase();
			if (issueType.equals("constraint")||issueType.equals("assumption")||issueType.equals("implication")||issueType.equals("context")){
				htmlClass="context";
			} else if (issueType.equals("problem")||issueType.equals("issue")||issueType.equals("goal")){
				htmlClass="problem";
			} else if (issueType.equals("solution")||issueType.equals("claim")||issueType.equals("alternative")){
				htmlClass="solution";
			} else {
				htmlClass="rationale";
			}
			node.setHtmlClass(htmlClass);
			
			if(currentDepth<depth){
				List<Node> children = new ArrayList<Node>();
				final List<DecisionComponentEntity> inwardLinkedDecList = new ArrayList<DecisionComponentEntity>();
				for(LinkEntity linkEntity : ao.find(LinkEntity.class, Query.select().where("INGOING_ID != ? AND OUTGOING_ID = ?", dec.getID(), dec.getID()))) {
					for(DecisionComponentEntity decisionComponent : ao.find(DecisionComponentEntity.class, 
							Query.select().where("ID = ? AND PROJECT_KEY = ?", linkEntity.getIngoingId(), dec.getProjectKey()))){
						inwardLinkedDecList.add(decisionComponent);
					}
				}
				final List<DecisionComponentEntity> outwardLinkedDecList = new ArrayList<DecisionComponentEntity>();
				for(LinkEntity linkEntity : ao.find(LinkEntity.class, Query.select().where("INGOING_ID = ? AND OUTGOING_ID != ?", dec.getID(), dec.getID()))) {
					for(DecisionComponentEntity decisionComponent : ao.find(DecisionComponentEntity.class, 
							Query.select().where("ID = ? AND PROJECT_KEY = ?", linkEntity.getOutgoingId(), dec.getProjectKey()))){
						outwardLinkedDecList.add(decisionComponent);
					}
				}
				List<DecisionComponentEntity> toBeAddedToChildren = new ArrayList<DecisionComponentEntity>();
				for (int i=0; i<outwardLinkedDecList.size(); ++i) {
					DecisionComponentEntity decisionComponent = outwardLinkedDecList.get(i);
					Pair<String, String> newKVP = new Pair<String, String>(dec.getKey(), decisionComponent.getKey());
					Pair<String, String> newKVPReverse = new Pair<String, String>(decisionComponent.getKey(), dec.getKey());
					boolean boolvar = false;
					for(int counter = 0; counter<TreantKeyValuePairList.kvpList.size(); ++counter){
						Pair<String, String> globalInst = TreantKeyValuePairList.kvpList.get(counter);
						if (newKVP.equals(globalInst) || newKVPReverse.equals(globalInst)){
							boolvar = true;
						}
					}
					if(!boolvar){
						TreantKeyValuePairList.kvpList.add(newKVP);
						TreantKeyValuePairList.kvpList.add(newKVPReverse);
						toBeAddedToChildren.add(decisionComponent);
					}
				}
				for (int i=0; i<inwardLinkedDecList.size(); ++i) {
					DecisionComponentEntity decisionComponent = inwardLinkedDecList.get(i);
					Pair<String, String> newKVP = new Pair<String, String>(dec.getKey(), decisionComponent.getKey());
					Pair<String, String> newKVPReverse = new Pair<String, String>(decisionComponent.getKey(), dec.getKey());
					boolean boolvar = false;
					for(int counter = 0; counter<TreantKeyValuePairList.kvpList.size(); ++counter){
						Pair<String, String> globalInst = TreantKeyValuePairList.kvpList.get(counter);
						if (newKVP.equals(globalInst) || newKVPReverse.equals(globalInst)){
							boolvar = true;
						}
					}
					if(!boolvar){
						TreantKeyValuePairList.kvpList.add(newKVP);
						TreantKeyValuePairList.kvpList.add(newKVPReverse);
						toBeAddedToChildren.add(decisionComponent);
					}
				}
				for (int index = 0; index < toBeAddedToChildren.size(); ++index){
					children.add(createNode(toBeAddedToChildren.get(index), depth, currentDepth+1));
				}
				node.setChildren(children);
			}
		}
		return node;
	}
}