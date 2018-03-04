package de.uhd.ifi.se.decision.documentation.jira.decisionknowledge;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.atlassian.jira.issue.Issue;

/**
 * @author Ewald Rode
 * @description Model class for decision knowledge elements
 */
@XmlType(propOrder = { "id", "text" })
public class DecisionKnowledgeElement implements IDecisionKnowledgeElement {
	@XmlElement
	private long id;
	private String name;
	private String description;
	private String type;
	private String projectKey;
	private String key;
	private String summary;

	@XmlElement
	private String text;

	public DecisionKnowledgeElement() {

	}

	public DecisionKnowledgeElement(long id, String name, String description, String type, String projectKey,
			String key, String summary) {
		this.id = id;
		this.name = name;
		this.description = description;
		this.type = type;
		this.projectKey = projectKey;
		this.summary = summary;
		this.key = projectKey + "-" + id;
		this.text = this.getText();
	}

	public DecisionKnowledgeElement(Issue issue) {
		this.id = issue.getId();
		this.name = issue.getSummary();
		this.description = issue.getDescription();
		this.type = issue.getIssueType().getName();
		this.projectKey = issue.getProjectObject().getKey();
		this.summary = issue.getSummary();
		this.key = issue.getKey();
		this.text = this.getText();
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getProjectKey() {
		return projectKey;
	}

	public void setProjectKey(String projectKey) {
		this.projectKey = projectKey;
	}

	public String getKey() {
		return this.key;
	}
	
	public void setKey(String key) {
		this.key = key;
	}

	public String getText() {
		return this.type + " / " + this.name;
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	};
}