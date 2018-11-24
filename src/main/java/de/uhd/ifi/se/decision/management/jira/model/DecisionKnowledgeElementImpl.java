package de.uhd.ifi.se.decision.management.jira.model;

import java.util.Date;
import java.util.List;
import java.util.Objects;

import javax.xml.bind.annotation.XmlElement;

import org.codehaus.jackson.annotate.JsonProperty;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.IssueManager;

import de.uhd.ifi.se.decision.management.jira.extraction.model.Sentence;
import de.uhd.ifi.se.decision.management.jira.persistence.DecisionKnowledgeElementInDatabase;

/**
 * Model class for decision knowledge elements
 */
public class DecisionKnowledgeElementImpl implements DecisionKnowledgeElement {

	private long id;
	private String summary;
	private String description;
	protected KnowledgeType type;
	protected DocumentationLocation documentationLocation;
	private DecisionKnowledgeProject project;
	private String key;
	private Date created;

	public DecisionKnowledgeElementImpl() {
		//prevent nullPointers later
		this.description="";
		this.summary="";
	}

	public DecisionKnowledgeElementImpl(long id, String summary, String description, KnowledgeType type,
			String projectKey, String key) {
		this.id = id;
		this.summary = summary;
		this.description = description;
		this.type = type;
		this.project = new DecisionKnowledgeProjectImpl(projectKey);
		this.key = key;
	}

	public DecisionKnowledgeElementImpl(long id, String summary, String description, String type, String projectKey,
			String key) {
		this(id, summary, description, KnowledgeType.getKnowledgeType(type), projectKey, key);
	}

	public DecisionKnowledgeElementImpl(Issue issue) {
		if(issue != null) {
			this.id = issue.getId();
			this.summary = issue.getSummary();
			this.description = issue.getDescription();
			this.type = KnowledgeType.getKnowledgeType(issue.getIssueType().getName());
			this.project = new DecisionKnowledgeProjectImpl(issue.getProjectObject().getKey());
			this.key = issue.getKey();
			this.documentationLocation = DocumentationLocation.JIRAISSUE;
			this.created = issue.getCreated();
		}
	}

	public DecisionKnowledgeElementImpl(DecisionKnowledgeElementInDatabase entity) {
		this(entity.getId(), entity.getSummary(), entity.getDescription(), entity.getType(), entity.getProjectKey(),
				entity.getKey());
		this.documentationLocation = DocumentationLocation.ACTIVEOBJECT;
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

	@Override
	@XmlElement(name = "type")
	public String getTypeAsString() {
		if(type == KnowledgeType.OTHER) {
			if(this instanceof Sentence) {
				return ((Sentence)this).getKnowledgeTypeString();
			}
			IssueManager issueManager = ComponentAccessor.getIssueManager();
			Issue issue = issueManager.getIssueByCurrentKey(this.getKey());
			return issue.getIssueType().getName();
		}
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
	public DecisionKnowledgeProject getProject() {
		return this.project;
	}

	@Override
	public void setProject(DecisionKnowledgeProject project) {
		this.project = project;
	}

	@Override
	@JsonProperty("projectKey")
	public void setProject(String projectKey) {
		this.project = new DecisionKnowledgeProjectImpl(projectKey);
	}

	@Override
	@XmlElement(name = "key")
	public String getKey() {
		if (this.key == null) {
			return this.project.getProjectKey() + "-" + this.id;
		}
		return this.key;
	}

	@Override
	public void setKey(String key) {
		this.key = key;
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
	public DocumentationLocation getDocumentationLocation() {
		return this.documentationLocation;
	}

	@Override
	public void setDocumentationLocation(DocumentationLocation documentationLocation) {
		this.documentationLocation = documentationLocation;
	}

	@Override
	@JsonProperty("documentationLocation")
	public void setDocumentationLocation(String documentationLocation) {
		this.documentationLocation = DocumentationLocation.getDocumentationType(documentationLocation);
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

	@Override
	public Date getCreated() {
		return this.created;
	}

	@Override
	public void setCreated(Date date) {
		this.created = date;
	}
}