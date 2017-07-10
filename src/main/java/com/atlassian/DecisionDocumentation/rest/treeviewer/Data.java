package com.atlassian.DecisionDocumentation.rest.treeviewer;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;

import com.atlassian.DecisionDocumentation.util.Pair;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.link.IssueLink;

/**
 * 
 * @author Ewald Rode
 * @description
 */
public class Data {

	@XmlElement
    public String text;
	
	@XmlElement
    public List<Data> children;
	
	@XmlElement
    public NodeInfo data;
	
	public Data(Issue issue){
		this.text = issue.getKey() + " / " + issue.getSummary();
		this.data = new NodeInfo(issue);
		this.children = new ArrayList<Data>();
		List<IssueLink> allOutwardIssueLink = ComponentAccessor.getIssueLinkManager().getOutwardLinks(issue.getId());
		List<Issue> outwardIssuesList = new ArrayList<Issue>();
		for (int index = 0; index < allOutwardIssueLink.size(); ++index){
			IssueLink issueLink = allOutwardIssueLink.get(index);
			outwardIssuesList.add(issueLink.getDestinationObject());
		}
		List<IssueLink> allInwardIssueLink = ComponentAccessor.getIssueLinkManager().getInwardLinks(issue.getId());
		List<Issue> inwardIssuesList = new ArrayList<Issue>();
		for (int index = 0; index < allInwardIssueLink.size(); ++index){
			IssueLink issueLink = allInwardIssueLink.get(index);
			inwardIssuesList.add(issueLink.getSourceObject());
		}
		List<Issue> toBeAddedToChildren = new ArrayList<Issue>();
		for (int index = 0; index < inwardIssuesList.size(); ++index){
			if(inwardIssuesList.get(index).getIssueType().getName().equals("Argument")){
				Pair<String, String> newKVP = new Pair<String, String>(issue.getKey(), inwardIssuesList.get(index).getKey());
				Pair<String, String> newKVPReverse = new Pair<String, String>(inwardIssuesList.get(index).getKey(), issue.getKey());
				boolean boolvar = false;
				for(int counter = 0; counter<TreeViewerKVPList.kvpList.size(); ++counter){
					Pair<String, String> globalInst = TreeViewerKVPList.kvpList.get(counter);
					if (newKVP.equals(globalInst)){
						boolvar = true;
					}
				}
				if(!boolvar){
					TreeViewerKVPList.kvpList.add(newKVP);
					TreeViewerKVPList.kvpList.add(newKVPReverse);
					toBeAddedToChildren.add(inwardIssuesList.get(index));
				}
			}
		}
		//if(!issue.getIssueType().getName().equals("Argument")){
			for (int index=0; index<outwardIssuesList.size(); ++index) {
				/*
				 * Erstelle Parent-Child Beziehung und pruefe ob diese bereits in der KeyValuePair-Liste vorhanden ist.
				 * Wenn nein, fuege diesem Knoten Kinder hinzu
				 */
				Pair<String, String> newKVP = new Pair<String, String>(issue.getKey(), outwardIssuesList.get(index).getKey());
				Pair<String, String> newKVPReverse = new Pair<String, String>(outwardIssuesList.get(index).getKey(), issue.getKey());
				boolean boolvar = false;
				for(int counter = 0; counter<TreeViewerKVPList.kvpList.size(); ++counter){
					Pair<String, String> globalInst = TreeViewerKVPList.kvpList.get(counter);
					if (newKVP.equals(globalInst)){
						boolvar = true;
					}
				}
				if(!boolvar){
					TreeViewerKVPList.kvpList.add(newKVP);
					TreeViewerKVPList.kvpList.add(newKVPReverse);
					toBeAddedToChildren.add(outwardIssuesList.get(index));
				}
			}
		//}
		for (Issue issueToBeAdded: toBeAddedToChildren){
			children.add(new Data(issueToBeAdded));
		}
	}
}
