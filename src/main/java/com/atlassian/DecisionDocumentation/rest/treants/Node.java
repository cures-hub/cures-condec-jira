package com.atlassian.DecisionDocumentation.rest.treants;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;

import com.atlassian.DecisionDocumentation.rest.treeviewer.Data;
import com.atlassian.DecisionDocumentation.rest.treeviewer.TreeViewerKVPList;
import com.atlassian.DecisionDocumentation.util.Pair;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.link.IssueLink;
/**
 * 
 * @author Ewald Rode
 * @description
 */
public class Node {
	/*Inhalt eines Knotens*/
	@XmlElement(name = "text")
	private NodeContent nodeContent;
	/*Hyperlink-Adresse*/
	@XmlElement
	private Link link;
	
	@XmlElement
	private String HTMLclass;
	
	@XmlElement
	List<Node> children;
	
	public Node(Issue issue, int depth, int currentDepth){
		this.nodeContent = new NodeContent(issue);
		this.link = new Link(issue);
		String issueType = issue.getIssueType().getName().toLowerCase();
		if (issueType.equals("constraint")||issueType.equals("assumption")||issueType.equals("implication")||issueType.equals("context")){
			this.HTMLclass="context";
		} else if (issueType.equals("problem")||issueType.equals("issue")||issueType.equals("goal")){
			this.HTMLclass="problem";
		} else if (issueType.equals("solution")||issueType.equals("claim")||issueType.equals("alternative")){
			this.HTMLclass="solution";
		} else if (issueType.equals("argument")||issueType.equals("assessment")){
			this.HTMLclass="rationale";
		}
		if(currentDepth<depth){
			this.children = new ArrayList<Node>();
			List<Issue> toBeAddedToChildren = new ArrayList<Issue>();
			List<IssueLink> allOutwardIssueLink = ComponentAccessor.getIssueLinkManager().getOutwardLinks(issue.getId());
			if(allOutwardIssueLink != null){
				//this.children = new ArrayList<Node>();
				for (int i=0; i<allOutwardIssueLink.size(); ++i) {
					IssueLink issueLink = allOutwardIssueLink.get(i);
					Issue issueLinkDestination = issueLink.getDestinationObject();
					/*
					 * Erstelle Parent-Child Beziehung und pruefe ob diese bereits in der KeyValuePair-Liste vorhanden ist.
					 * Wenn nein, fuege diesem Knoten Kinder hinzu
					 */
					
					Pair<String, String> newKVP = new Pair<String, String>(issue.getKey(), issueLinkDestination.getKey());
					Pair<String, String> newKVPReverse = new Pair<String, String>(issueLinkDestination.getKey(), issue.getKey());
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
						toBeAddedToChildren.add(issueLinkDestination);
					}
				}
			}
			
			List<IssueLink> allInwardIssueLink = ComponentAccessor.getIssueLinkManager().getInwardLinks(issue.getId());
			if(allInwardIssueLink != null){
				for (int i=0; i<allInwardIssueLink.size(); ++i) {
					IssueLink issueLink = allInwardIssueLink.get(i);
					Issue issueLinkDestination = issueLink.getSourceObject();
					/*
					 * Erstelle Parent-Child Beziehung und pruefe ob diese bereits in der KeyValuePair-Liste vorhanden ist.
					 * Wenn nein, fuege diesem Knoten Kinder hinzu
					 */
					Pair<String, String> newKVP = new Pair<String, String>(issue.getKey(), issueLinkDestination.getKey());
					Pair<String, String> newKVPReverse = new Pair<String, String>(issueLinkDestination.getKey(), issue.getKey());
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
						toBeAddedToChildren.add(issueLinkDestination);
					}
				}
			}
			for (int index = 0; index < toBeAddedToChildren.size(); ++index){
				this.children.add(new Node(toBeAddedToChildren.get(index), depth, currentDepth+1));
			}
			
		}
	}
}
