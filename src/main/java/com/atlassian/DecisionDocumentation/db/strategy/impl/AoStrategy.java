package com.atlassian.DecisionDocumentation.db.strategy.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atlassian.DecisionDocumentation.db.DecisionComponentEntity;
import com.atlassian.DecisionDocumentation.db.LinkEntity;
import com.atlassian.DecisionDocumentation.db.strategy.Strategy;
import com.atlassian.DecisionDocumentation.rest.Decisions.model.DecisionRepresentation;
import com.atlassian.DecisionDocumentation.rest.Decisions.model.LinkRepresentation;
import com.atlassian.DecisionDocumentation.rest.Decisions.model.SimpleDecisionRepresentation;
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
	
	@Override
	public long createDecisionComponent(final DecisionRepresentation dec, ApplicationUser user) {
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
		return decComponent.getID();
	}

	@Override
	public void editDecisionComponent(final DecisionRepresentation dec, ApplicationUser user) {
		final ActiveObjects ao = ComponentGetter.getAo();
		ao.executeInTransaction(new TransactionCallback<Void>()
        {
			@Override
            public Void doInTransaction()
            {
				for (DecisionComponentEntity decComponent : ao.find(DecisionComponentEntity.class)) // (2)
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
	public void createLink(LinkRepresentation link, ApplicationUser user) {
		final ActiveObjects ao = ComponentGetter.getAo();
		ao.executeInTransaction(new TransactionCallback<Void>()
        {
            @Override
            public Void doInTransaction()
            {
            	boolean linkAlreadyExists = false;
                for (LinkEntity linkEntity : ao.find(LinkEntity.class))
                {
                	if(linkEntity.getIngoingId() == link.getIngoingId() && linkEntity.getOutgoingId() == link.getOutgoingId()) {
                		linkAlreadyExists = true;
                	}
                }
                if(!linkAlreadyExists) {
                	link.getOutgoingId();
                	DecisionComponentEntity decCompIngoing;
                	DecisionComponentEntity[] decCompIngoingArray = ao.find(DecisionComponentEntity.class, Query.select().where("ID = ?", link.getIngoingId()));
                	/*
                	 * ID is the primarykey for decisioncomponents, therefore find can only return 0 to 1 entities
                	 */
                	if(decCompIngoingArray.length == 1) {
                		decCompIngoing = decCompIngoingArray[0];
                	} else {
                		//entity with ingoingId does not exist
                		decCompIngoing = null;
                	}
                	
                	DecisionComponentEntity decCompOutgoing;
                	DecisionComponentEntity[] decCompOutgoingArray = ao.find(DecisionComponentEntity.class, Query.select().where("ID = ?", link.getOutgoingId()));
                	if(decCompIngoingArray.length == 1) {
                		decCompOutgoing = decCompOutgoingArray[0];
                	} else {
                		//entity with ingoingId does not exist
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
                		} else {
                			// entities to be linked are not in the same project, TODO ignore request
                		}
                	} else {
                		// one of the entities to be linked does not exist, TODO ignore request
                	}
                }
                return null;
            }
        });
	}
	//TODO implement
	@Override
	public void deleteLink(LinkRepresentation link, ApplicationUser user) {
		
	}
	//TODO implement
	@Override
	public List<SimpleDecisionRepresentation> searchUnlinkedDecisionComponents(long id, String projectKey) {
		return null;
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
	public Treant createTreant(Long id, int depth) {
		// TODO Auto-generated method stub
		return null;
	}
}