package de.uhd.ifi.se.decision.management.jira.model;

import java.util.List;
import java.util.Objects;

import javax.xml.bind.annotation.XmlElement;

import org.codehaus.jackson.annotate.JsonProperty;

import com.atlassian.jira.issue.Issue;

/**
 * Model class for decision knowledge elements
 */
public class DecisionKnowledgeElementImpl implements DecisionKnowledgeElement {

	private long id;
	private String summary;
	private String description;
	private KnowledgeType type;
	private String projectKey;
	private DecisionKnowledgeProject project;
	private String key;

	public DecisionKnowledgeElementImpl() {

	}

	public DecisionKnowledgeElementImpl(long id, String summary, String description, KnowledgeType type,
			String projectKey, String key) {
		this.id = id;
		this.summary = summary;
		this.description = description;
		this.type = type;
		this.projectKey = projectKey;
		this.key = key;
	}

	public DecisionKnowledgeElementImpl(long id, String summary, String description, String type, String projectKey,
			String key) {
		this(id, summary, description, KnowledgeType.getKnowledgeType(type), projectKey, key);
	}

	public DecisionKnowledgeElementImpl(Issue issue) {
		this.id = issue.getId();
		this.summary = issue.getSummary();
		this.description = issue.getDescription();
		this.type = KnowledgeType.getKnowledgeType(issue.getIssueType().getName());
		this.projectKey = issue.getProjectObject().getKey();
		this.key = issue.getKey();
	}

	@Override
	@XmlElement(name = "id")
	public long getId() {
		return id;
	}

	@Override
	public void setId(long id) {
		this.id = id;
	}

	@Override
	@XmlElement(name = "summary")
	public String getSummary() {
		return summary;
	}

	@Override
	public void setSummary(String summary) {
		this.summary = summary;
	}

	@Override
	@XmlElement(name = "description")
	public String getDescription() {
		return description;
	}

	@Override
	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public KnowledgeType getType() {
		return type;
	}

	@XmlElement(name = "type")
	public String getTypeAsString() {
		return type.toString();
	}

	@Override
	public void setType(KnowledgeType type) {
		this.type = type;
	}

	@Override
	@JsonProperty("type")
	public void setType(String type) {
		this.type = KnowledgeType.getKnowledgeType(type);
	}

	@Override
	public String getProjectKey() {
		return projectKey;
	}

	@Override
	public DecisionKnowledgeProject getProject() {
		if (this.project == null) {
			this.project = new DecisionKnowledgeProjectImpl(projectKey);
		}
		return this.project;
	}

	@Override
	public void setProjectKey(String projectKey) {
		this.projectKey = projectKey;
	}

	@Override
	@XmlElement(name = "key")
	public String getKey() {
		if (this.key == null) {
			return this.projectKey + "-" + this.id;
		}
		return this.key;
	}

	@Override
	public void setKey(String key) {
		this.key = key;
	}

	@Override
	public KnowledgeType getSuperType() {
		return this.type.getSuperType();
	}

	@Override
	public List<DecisionKnowledgeElement> getLinkedElements() {
		return this.getProject().getPersistenceStrategy().getLinkedElements(this);
	}

	@Override
	public List<Link> getOutwardLinks() {
		return this.getProject().getPersistenceStrategy().getOutwardLinks(this);
	}

	@Override
	public List<Link> getInwardLinks() {
		return this.getProject().getPersistenceStrategy().getInwardLinks(this);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, summary);
	}

	@Override
	public boolean equals(Object object) {
		if (object == null) {
			return false;
		}
		if (object == this) {
			return true;
		}
		if (!(object instanceof DecisionKnowledgeElement)) {
			return false;
		}
		DecisionKnowledgeElement element = (DecisionKnowledgeElement) object;
		return this.id == element.getId();
	}
}