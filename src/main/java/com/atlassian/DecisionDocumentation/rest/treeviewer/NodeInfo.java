package com.atlassian.DecisionDocumentation.rest.treeviewer;

import javax.xml.bind.annotation.XmlElement;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.config.properties.APKeys;
import com.atlassian.jira.issue.Issue;

/**
 * 
 * @author Ewald Rode
 * @description
 */
public class NodeInfo {
	@XmlElement
	public String id;
	
	@XmlElement
	public String key;
	
	@XmlElement
	public String selfUrl;
	
	@XmlElement
	public String issueType;
	
	@XmlElement
	public String description;
	
	@XmlElement
	public String summary;
	
	public NodeInfo(){
		this.id = "";
		this.key ="";
		this.selfUrl = "";
		this.issueType = "";
		this.description ="";
		this.summary ="";
	}
	
	@SuppressWarnings("deprecation")
	public NodeInfo(Issue issue){
		this.id = Long.toString(issue.getId());
		this.key = issue.getKey();
		this.selfUrl = ComponentAccessor.getApplicationProperties().getString(APKeys.JIRA_BASEURL) + "/rest/api/2/issue/" + issue.getId();
		this.issueType = issue.getIssueTypeObject().getName();
		this.description =issue.getDescription();
		this.summary =issue.getSummary();
	}
}
