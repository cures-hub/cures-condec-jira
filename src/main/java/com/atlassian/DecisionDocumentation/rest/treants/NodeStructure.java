package com.atlassian.DecisionDocumentation.rest.treants;

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
public class NodeStructure {
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
	
	public NodeStructure(){
		this.nodeContent = null;
		this.link = null;
		this.children = null;
	}
	
	public NodeStructure(Issue issue, int depth){
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
		List<IssueLink> allOutwardIssueLink = ComponentAccessor.getIssueLinkManager().getOutwardLinks(issue.getId());
		this.children = new ArrayList<Node>();
		/*
		 * kvpList speichert die KeyValuePairs aller Parent-Child Beziehungen um nachvollziehen zu koennen, welche Knoten bereits in den Baum aufgenommen wurden,
		 * dies ist insbesondere noetig um Endlos-SChleifen vorzubeugen
		 */
		TreantKeyValuePairList.kvpList = new ArrayList<Pair<String, String>>();
		if (allOutwardIssueLink != null){
			if(allOutwardIssueLink.size()>0){
				for (int i=0; i<allOutwardIssueLink.size(); i++) {
					IssueLink issueLink = allOutwardIssueLink.get(i);
					Issue issueLinkDestination = issueLink.getDestinationObject();
					Pair<String,String> kvp = new Pair<String,String>(issue.getKey(), issueLinkDestination.getKey());
					Pair<String,String> kvp2 = new Pair<String,String>(issueLinkDestination.getKey(), issue.getKey());
					TreantKeyValuePairList.kvpList.add(kvp);
					TreantKeyValuePairList.kvpList.add(kvp2);
				}
			}
		}
		List<IssueLink> allInwardIssueLink = ComponentAccessor.getIssueLinkManager().getInwardLinks(issue.getId());
		if (allInwardIssueLink != null){
			if(allInwardIssueLink.size()>0){
				for (int i=0; i<allInwardIssueLink.size(); i++) {
					IssueLink issueLink = allInwardIssueLink.get(i);
					Issue issueLinkDestination = issueLink.getSourceObject();
					Pair<String,String> kvp = new Pair<String,String>(issue.getKey(), issueLinkDestination.getKey());
					Pair<String,String> kvp2 = new Pair<String,String>(issueLinkDestination.getKey(), issue.getKey());
					TreantKeyValuePairList.kvpList.add(kvp);
					TreantKeyValuePairList.kvpList.add(kvp2);
				}
			}
		}
		if (allOutwardIssueLink != null){
			if(allOutwardIssueLink.size()>0){
				for (int i=0; i<allOutwardIssueLink.size(); i++) {
					IssueLink issueLink = allOutwardIssueLink.get(i);
					Issue issueLinkDestination = issueLink.getDestinationObject();
					this.children.add(new Node(issueLinkDestination, depth, 1));
				}
			}
		}
		if (allInwardIssueLink != null){
			if(allInwardIssueLink.size()>0){
				for (int i=0; i<allInwardIssueLink.size(); i++) {
					IssueLink issueLink = allInwardIssueLink.get(i);
					Issue issueLinkDestination = issueLink.getSourceObject();
					this.children.add(new Node(issueLinkDestination, depth, 1));
				}
			}
		}
	}
}