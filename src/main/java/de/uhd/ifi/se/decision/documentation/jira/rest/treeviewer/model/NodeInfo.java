package de.uhd.ifi.se.decision.documentation.jira.rest.treeviewer.model;

import javax.xml.bind.annotation.XmlElement;

/**
 * @author Ewald Rode
 * @description model class for treeviewer nodes
 */
public class NodeInfo {
	@XmlElement
	private String id;
	
	@XmlElement
	private String key;
		
	@XmlElement
	private String issueType;
	
	@XmlElement
	private String description;
	
	@XmlElement
	private String summary;

	public NodeInfo(){}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}
	
	public String getIssueType() {
		return issueType;
	}

	public void setIssueType(String issueType) {
		this.issueType = issueType;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}
}
