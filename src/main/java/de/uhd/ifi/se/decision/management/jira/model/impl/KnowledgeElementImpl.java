package de.uhd.ifi.se.decision.management.jira.model.impl;

import java.util.Date;
import java.util.List;
import java.util.Objects;

import javax.xml.bind.annotation.XmlElement;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.config.properties.APKeys;
import com.atlassian.jira.config.properties.ApplicationProperties;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.IssueManager;
import com.atlassian.jira.user.ApplicationUser;
import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeProject;
import de.uhd.ifi.se.decision.management.jira.model.DocumentationLocation;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeGraph;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeStatus;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.model.Link;
import de.uhd.ifi.se.decision.management.jira.model.text.PartOfJiraIssueText;
import de.uhd.ifi.se.decision.management.jira.persistence.DecisionGroupManager;
import de.uhd.ifi.se.decision.management.jira.persistence.KnowledgePersistenceManager;
import de.uhd.ifi.se.decision.management.jira.persistence.impl.GenericLinkManager;
import de.uhd.ifi.se.decision.management.jira.persistence.tables.CodeClassElementInDatabase;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * Model class for knowledge elements
 */
public class KnowledgeElementImpl implements KnowledgeElement {

	protected long id;
	protected DecisionKnowledgeProject project;
	private String summary;
	private String description;
	protected KnowledgeType type;
	private String key;
	private Date created;
	private Date closed;
	protected DocumentationLocation documentationLocation;
	protected KnowledgeStatus status;

	public KnowledgeElementImpl() {
		this.description = "";
		this.summary = "";
		this.type = KnowledgeType.OTHER;
	}

	public KnowledgeElementImpl(long id, String summary, String description, KnowledgeType type, String projectKey,
								String key, DocumentationLocation documentationLocation, KnowledgeStatus status) {
		this.id = id;
		this.summary = summary;
		this.description = description;
		this.type = type;
		this.project = new DecisionKnowledgeProjectImpl(projectKey);
		this.key = key;
		this.documentationLocation = documentationLocation;
		this.status = status;
	}

	public KnowledgeElementImpl(long id, String projectKey, String documentationLocation) {
		this.id = id;
		this.project = new DecisionKnowledgeProjectImpl(projectKey);
		this.documentationLocation = DocumentationLocation
				.getDocumentationLocationFromIdentifier(documentationLocation);
	}

	public KnowledgeElementImpl(long id, String summary, String description, String type, String projectKey, String key,
								String documentationLocation, String status) {
		this(id, summary, description, KnowledgeType.getKnowledgeType(type), projectKey, key,
				DocumentationLocation.getDocumentationLocationFromIdentifier(documentationLocation),
				KnowledgeStatus.getKnowledgeStatus(status));
	}

	public KnowledgeElementImpl(long id, String summary, String description, String type, String projectKey, String key,
								DocumentationLocation documentationLocation, String status) {
		this(id, summary, description, KnowledgeType.getKnowledgeType(type), projectKey, key, documentationLocation,
				KnowledgeStatus.getKnowledgeStatus(status));
	}

	public KnowledgeElementImpl(Issue issue) {
		if (issue != null) {
			this.id = issue.getId();
			this.summary = issue.getSummary();
			this.description = issue.getDescription();
			if (issue.getIssueType() != null) {
				this.type = KnowledgeType.getKnowledgeType(issue.getIssueType().getName());
			}
			if (issue.getProjectObject() != null) {
				this.project = new DecisionKnowledgeProjectImpl(issue.getProjectObject().getKey());
			}
			this.key = issue.getKey();
			this.documentationLocation = DocumentationLocation.JIRAISSUE;
			this.created = issue.getCreated();
			// TODO Manage status for decision knowledge elements stored as entire Jira
			// issues
			this.status = KnowledgeStatus.RESOLVED;
		}
	}

	public KnowledgeElementImpl(CodeClassElementInDatabase entry) {
		if (entry != null) {
			this.id = entry.getId();
			this.summary = entry.getFileName();
			String issueKeys = "";
			for (String key : entry.getJiraIssueKeys().split(";")) {
				issueKeys = issueKeys + entry.getProjectKey() + "-" + key + ";";
			}
			this.description = issueKeys;
			this.type = KnowledgeType.getKnowledgeType(null);
			this.project = new DecisionKnowledgeProjectImpl(entry.getProjectKey());
			this.key = entry.getProjectKey() + "-" + entry.getId();
			this.documentationLocation = DocumentationLocation.COMMIT;
			this.status = KnowledgeStatus.getKnowledgeStatus(null);
		}
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
	@XmlElement(name = "groups")
	public List<String> getDecisionGroups() {
		List<String> groups = DecisionGroupManager.getGroupsForElement(this);
		return groups;
	}

	@Override
	public void addDecisionGroups(List<String> decisionGroup) {
		for (String group : decisionGroup) {
			DecisionGroupManager.insertGroup(group, this);
		}
	}

	@Override
	public void addDecisionGroup(String group) {
		DecisionGroupManager.insertGroup(group, this);
	}

	@Override
	public void removeDecisionGroup(String group) {
		DecisionGroupManager.deleteGroupAssignment(group, this);
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
		if (documentationLocation == null || documentationLocation.isBlank()) {
			// TODO Add here persistence strategy chosen in project
			this.documentationLocation = DocumentationLocation.JIRAISSUE;
		}
		this.documentationLocation = DocumentationLocation
				.getDocumentationLocationFromIdentifier(documentationLocation);
	}

	@Override
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
	public Date getCreated() {
		if (created == null) {
			return new Date();
		}
		return this.created;
	}

	@Override
	public void setCreated(Date date) {
		this.created = date;
	}

	@Override
	public Date getClosed() {
		return this.closed;
	}

	@Override
	public void setClosed(Date date) {
		this.closed = date;
	}

	@Override
	public boolean existsInDatabase() {
		KnowledgeElement elementInDatabase = KnowledgePersistenceManager.getOrCreate("").getDecisionKnowledgeElement(id,
				documentationLocation);
		return elementInDatabase != null && elementInDatabase.getId() > 0;
	}

	@Override
	public Issue getJiraIssue() {
		if (documentationLocation == DocumentationLocation.JIRAISSUE) {
			return ComponentAccessor.getIssueManager().getIssueObject(id);
		}
		if (documentationLocation == DocumentationLocation.JIRAISSUETEXT) {
			return ((PartOfJiraIssueText) this).getJiraIssue();
		}
		return null;
	}

	@Override
	public String toString() {
		// return getDocumentationLocation().getIdentifier() + id;
		return this.getDescription();
	}

	@Override
	public ApplicationUser getCreator() {
		KnowledgePersistenceManager persistenceManager = KnowledgePersistenceManager
				.getOrCreate(project.getProjectKey());
		if (getDocumentationLocation() == DocumentationLocation.JIRAISSUE) {
			return persistenceManager.getJiraIssueManager().getCreator(this);
		}
		if (getDocumentationLocation() == DocumentationLocation.JIRAISSUETEXT) {
			return persistenceManager.getJiraIssueTextManager().getCreator(this);
		}
		return null;
	}

	@Override
	public List<Link> getLinks() {
		List<Link> links = GenericLinkManager.getLinksForElement(this);
		if (documentationLocation == DocumentationLocation.JIRAISSUE) {
			links.addAll(KnowledgeGraph.getOrCreate(project).edgesOf(this));
		}
		return links;
	}

	@Override
	public long isLinked() {
		List<Link> links = getLinks();
		if (!links.isEmpty()) {
			return links.get(0).getId();
		}
		return 0;
	}

	@Override
	public KnowledgeStatus getStatus() {
		if (status == null || status == KnowledgeStatus.UNDEFINED) {
			return KnowledgeStatus.getDefaultStatus(getType());
		}
		return status;
	}

	@Override
	public void setStatus(KnowledgeStatus status) {
		this.status = status;
	}

	@Override
	@JsonProperty("status")
	public void setStatus(String status) {
		this.status = KnowledgeStatus.getKnowledgeStatus(status);
	}

	@Override
	@XmlElement(name = "status")
	public String getStatusAsString() {
		return getStatus().toString();
	}

	@Override
	public boolean equals(Object object) {
		if (object == null) {
			return false;
		}
		if (object == this) {
			return true;
		}
		if (!(object instanceof KnowledgeElement)) {
			return false;
		}
		KnowledgeElement element = (KnowledgeElement) object;
		return this.id == element.getId() && this.getDocumentationLocation() == element.getDocumentationLocation();
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, getDocumentationLocation());
	}
}