package com.atlassian.DecisionDocumentation.rest.treants;

import javax.xml.bind.annotation.XmlElement;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.config.properties.APKeys;
import com.atlassian.jira.issue.Issue;
/**
 * 
 * @author Ewald Rode
 * @description Hyperlink-Adresse eines Knotens
 */
public class Link {

	@XmlElement
	private String href;
	
	public Link(){
		this.href="";
	}
	
	public Link(Issue issue){
		this.href= ComponentAccessor.getApplicationProperties().getString(APKeys.JIRA_BASEURL) + "/browse/" + issue.getKey();
	}
}