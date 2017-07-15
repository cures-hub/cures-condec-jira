package com.atlassian.DecisionDocumentation.db.strategy.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atlassian.DecisionDocumentation.db.DecisionComponentEntity;
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
		//return decComponent.getID(); TODO still needed? Delete, maybe
	}
	//TODO implement
	@Override
	public void deleteDecisionComponent(DecisionRepresentation dec, ApplicationUser user) {
		
	}
	//TODO implement
	@Override
	public void createLink(LinkRepresentation link, ApplicationUser user) {
		
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
		ao.executeInTransaction(new TransactionCallback<Void>() // (1)
        {
            @Override
            public Void doInTransaction()
            {
                for (DecisionComponentEntity decComponent : ao.find(DecisionComponentEntity.class)) // (2)
                {
                	LOGGER.error(decComponent.getProjectKey());
                	LOGGER.error(decComponent.getType());
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
	
	private Data createData(DecisionComponentEntity decComponent) {
		LOGGER.error("createData in AoStrategy");
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
		
		//List<Data> children = new ArrayList<Data>();
		/*
		List<IssueLink> allOutwardIssueLink = ComponentAccessor.getIssueLinkManager().getOutwardLinks(issue.getId());
		List<Issue> outwardIssuesList = new ArrayList<Issue>();
		for (int i = 0; i < allOutwardIssueLink.size(); ++i){
			IssueLink issueLink = allOutwardIssueLink.get(i);
			outwardIssuesList.add(issueLink.getDestinationObject());
		}
		List<IssueLink> allInwardIssueLink = ComponentAccessor.getIssueLinkManager().getInwardLinks(issue.getId());
		List<Issue> inwardIssuesList = new ArrayList<Issue>();
		for (int i = 0; i < allInwardIssueLink.size(); ++i){
			IssueLink issueLink = allInwardIssueLink.get(i);
			inwardIssuesList.add(issueLink.getSourceObject());
		}
		List<Issue> toBeAddedToChildren = new ArrayList<Issue>();
		for (int i = 0; i < inwardIssuesList.size(); ++i){
			if(inwardIssuesList.get(i).getIssueType().getName().equals("Argument")){
				Pair<String, String> newKVP = new Pair<String, String>(issue.getKey(), inwardIssuesList.get(i).getKey());
				Pair<String, String> newKVPReverse = new Pair<String, String>(inwardIssuesList.get(i).getKey(), issue.getKey());
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
					toBeAddedToChildren.add(inwardIssuesList.get(i));
				}
			}
		}
		for (int i=0; i<outwardIssuesList.size(); ++i) {
			Pair<String, String> newKVP = new Pair<String, String>(issue.getKey(), outwardIssuesList.get(i).getKey());
			Pair<String, String> newKVPReverse = new Pair<String, String>(outwardIssuesList.get(i).getKey(), issue.getKey());
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
				toBeAddedToChildren.add(outwardIssuesList.get(i));
			}
		}
		for (Issue issueToBeAdded: toBeAddedToChildren){
			children.add(createData(issueToBeAdded));
		}
		data.setChildren(children);
		*/
		return data;
	}

	@Override
	public Treant createTreant(Long id, int depth) {
		// TODO Auto-generated method stub
		return null;
	}
}