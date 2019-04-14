package de.uhd.ifi.se.decision.management.jira.model.impl;

import java.util.Date;
import java.util.List;
import java.util.Objects;

import javax.xml.bind.annotation.XmlElement;

import org.codehaus.jackson.annotate.JsonProperty;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.config.properties.APKeys;
import com.atlassian.jira.config.properties.ApplicationProperties;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.IssueManager;

import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeProject;
import de.uhd.ifi.se.decision.management.jira.model.DocumentationLocation;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.model.Link;
import de.uhd.ifi.se.decision.management.jira.persistence.AbstractPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.persistence.tables.DecisionKnowledgeElementInDatabase;

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
		this.description = "";
		this.summary = "";
		this.type = KnowledgeType.OTHER;
	}

	public DecisionKnowledgeElementImpl(long id, String summary, String description, KnowledgeType type,
			String projectKey, String key, DocumentationLocation documentationLocation) {
		this.id = id;
		this.summary = summary;
		this.description = description;
		this.type = type;
		this.project = new DecisionKnowledgeProjectImpl(projectKey);
		this.key = key;
		this.documentationLocation = documentationLocation;
	}

	public DecisionKnowledgeElementImpl(long id, String summary, String description, String type, String projectKey,
			String key, String documentationLocation) {
		this(id, summary, description, KnowledgeType.getKnowledgeType(type), projectKey, key,
				DocumentationLocation.getDocumentationLocationFromIdentifier(documentationLocation));
	}

	public DecisionKnowledgeElementImpl(long id, String summary, String description, String type, String projectKey,
			String key, DocumentationLocation documentationLocation) {
		this(id, summary, description, KnowledgeType.getKnowledgeType(type), projectKey, key, documentationLocation);
	}

	public DecisionKnowledgeElementImpl(Issue issue) {
		if (issue != null) {
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
				entity.getKey(), DocumentationLocation.ACTIVEOBJECT);
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
		if (type != null) {
			return type;
		}
		return KnowledgeType.OTHER;
	}

	@Override
	@XmlElement(name = "type")
	public String getTypeAsString() {
		if (this.getType() == KnowledgeType.OTHER
				&& this.getDocumentationLocation() == DocumentationLocation.JIRAISSUE) {
			IssueManager issueManager = ComponentAccessor.getIssueManager();
			Issue issue = issueManager.getIssueByCurrentKey(this.getKey());
			return issue.getIssueType().getName();
		}
		return this.getType().toString();
	}

	@Override
	public void setType(KnowledgeType type) {
		if (type == null) {
			this.type = KnowledgeType.OTHER;
		}
		this.type = type;
	}

	@Override
	@JsonProperty("type")
	public void setType(String typeAsString) {
		KnowledgeType type = KnowledgeType.getKnowledgeType(typeAsString);
		this.setType(type);
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
		if (this.key == null && this.project != null) {
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
		return this.getProject().getPersistenceStrategy().getAdjacentElements(this);
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
	@XmlElement(name = "documentationLocation")
	public String getDocumentationLocationAsString() {
		if (documentationLocation != null) {
			return this.documentationLocation.getIdentifier();
		}
		return "";
	}

	@Override
	@JsonProperty("documentationLocation")
	public void setDocumentationLocation(String documentationLocation) {
		if (documentationLocation == null || documentationLocation.equals("")) {
			// TODO Add here persistence strategy chosen in project
			this.documentationLocation = DocumentationLocation.JIRAISSUE;
		}
		this.documentationLocation = DocumentationLocation
				.getDocumentationLocationFromIdentifier(documentationLocation);
	}

	@XmlElement(name = "url")
	public String getUrl() {
		String key = this.getKey();
		if (this.getDocumentationLocation() == DocumentationLocation.JIRAISSUETEXT) {
			key = key.split(":")[0];
		}
		ApplicationProperties applicationProperties = ComponentAccessor.getApplicationProperties();
		return applicationProperties.getString(APKeys.JIRA_BASEURL) + "/browse/" + key;
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

	@Override
	public boolean existsInDatabase() {
		DecisionKnowledgeElement elementInDatabase = AbstractPersistenceManager.getDecisionKnowledgeElement(id,
				documentationLocation);
		return elementInDatabase.getId() > 0;
	}
	
	@Override
	public String toString() {
		return this.getDescription();
	}
}