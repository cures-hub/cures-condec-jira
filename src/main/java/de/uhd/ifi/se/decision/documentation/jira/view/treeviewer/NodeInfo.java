package de.uhd.ifi.se.decision.documentation.jira.view.treeviewer;

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
	private String type;

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

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
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