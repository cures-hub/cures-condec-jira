package com.atlassian.DecisionDocumentation.rest.treants;

import javax.xml.bind.annotation.XmlElement;

import com.atlassian.jira.issue.Issue;
/**
 * 
 * @author Ewald Rode
 * @description
 */
public class NodeContent {
	// For the Summary/Name of the issue
	@XmlElement(name = "name")
	private String summary;
	
	// used for the issue-type, unfortunately no other name can be given to the attribute
	@XmlElement(name = "title")
	private String issueType;
	
	public NodeContent(){
		this.summary=null;
		this.issueType=null;
	}
	
	public NodeContent(Issue issue){
		this.summary=issue.getKey() + " / " + issue.getSummary();
		this.issueType=issue.getIssueType().getName();
	}
}
