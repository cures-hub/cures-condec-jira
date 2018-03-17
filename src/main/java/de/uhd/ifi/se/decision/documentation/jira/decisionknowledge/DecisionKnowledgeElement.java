package de.uhd.ifi.se.decision.documentation.jira.decisionknowledge;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import org.codehaus.jackson.annotate.JsonProperty;

import com.atlassian.jira.issue.Issue;

import java.util.List;

/**
 * @description Model class for decision knowledge elements
 */
@XmlType(propOrder = { "id", "text" })
public class DecisionKnowledgeElement implements IDecisionKnowledgeElement {
	@XmlElement
	private long id;
	private String summary; // name of element
	private String description;
	private KnowledgeType type;
	private String projectKey;
	private String key;
	private List<DecisionKnowledgeElement> children;

	public DecisionKnowledgeElement() {

	}

	public DecisionKnowledgeElement(long id, String summary, String description, KnowledgeType type, String projectKey,
			String key) {
		this.id = id;
		this.summary = summary;
		this.description = description;
		this.type = type;
		this.projectKey = projectKey;
		this.key = key;
	}

	public DecisionKnowledgeElement(Issue issue) {
		this.id = issue.getId();
		this.summary = issue.getSummary();
		this.description = issue.getDescription();
		this.type = KnowledgeType.getKnowledgeType(issue.getIssueType().getName());
		this.projectKey = issue.getProjectObject().getKey();
		this.key = issue.getKey();
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}
	
	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public KnowledgeType getType() {
		return type;
	}

	public void setType(KnowledgeType type) {
		this.type = type;
	}

	@JsonProperty("type")
	public void setType(String type) {
		this.type = KnowledgeType.getKnowledgeType(type);
	}

	public String getProjectKey() {
		return projectKey;
	}

	public void setProjectKey(String projectKey) {
		this.projectKey = projectKey;
	}

	public String getKey() {
		if (this.key == null) {
			return this.projectKey + "-" + this.id;
		}
		return this.key;
	}

	public void setKey(String key) {
		this.key = key;
	}
	
	public List<DecisionKnowledgeElement> getChildren() {
		return children;
	}

	public void setChildren(List<DecisionKnowledgeElement> children) {
		this.children = children;
	}

	@XmlElement(name = "text")
	public String getText() {
		return this.type.toString().substring(0, 1).toUpperCase() + this.type.toString().substring(1) + " / "
				+ this.summary;
	}
}