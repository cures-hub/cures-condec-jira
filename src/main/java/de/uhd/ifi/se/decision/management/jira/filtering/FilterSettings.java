package de.uhd.ifi.se.decision.management.jira.filtering;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.xml.bind.annotation.XmlElement;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

import com.atlassian.jira.issue.issuetype.IssueType;
import com.atlassian.jira.user.ApplicationUser;

import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeProject;
import de.uhd.ifi.se.decision.management.jira.model.DocumentationLocation;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeStatus;
import de.uhd.ifi.se.decision.management.jira.model.LinkType;
import de.uhd.ifi.se.decision.management.jira.persistence.DecisionGroupManager;

/**
 * Represents the filter criteria. The filter settings cover the key of the
 * selected project, the time frame, documentation locations, Jira issue types,
 * and decision knowledge types. The search term can contain a query in Jira
 * Query Language (JQL), a {@link JiraFilter} or a search string specified in
 * the frontend of the plug-in.
 */
public class FilterSettings {

	private DecisionKnowledgeProject project;
	private String searchTerm;
	private List<DocumentationLocation> documentationLocations;
	private Set<String> jiraIssueTypes;
	private List<KnowledgeStatus> knowledgeStatus;
	private List<String> linkTypes;
	private List<String> decisionGroups;

	@XmlElement
	private long startDate;
	@XmlElement
	private long endDate;

	@JsonCreator
	public FilterSettings(@JsonProperty("projectKey") String projectKey,
			@JsonProperty("searchTerm") String searchTerm) {
		this.project = new DecisionKnowledgeProject(projectKey);
		this.searchTerm = searchTerm;
		this.jiraIssueTypes = project.getJiraIssueTypeNames();
		this.linkTypes = LinkType.toStringList();
		this.startDate = -1;
		this.endDate = -1;
		this.documentationLocations = DocumentationLocation.getAllDocumentationLocations();
		this.knowledgeStatus = KnowledgeStatus.getAllKnowledgeStatus();
		this.decisionGroups = DecisionGroupManager.getAllDecisionGroups(projectKey);
	}

	public FilterSettings(String projectKey, String query, ApplicationUser user) {
		this(projectKey, query);

		// The JiraQueryHandler parses a Jira query into attributes of this class
		JiraQueryHandler queryHandler = new JiraQueryHandler(user, projectKey, query);
		this.searchTerm = queryHandler.getQuery();

		Set<String> namesOfJiraIssueTypesInQuery = queryHandler.getNamesOfJiraIssueTypesInQuery();
		if (!namesOfJiraIssueTypesInQuery.isEmpty()) {
			this.jiraIssueTypes = namesOfJiraIssueTypesInQuery;
		}

		this.startDate = queryHandler.getCreatedEarliest();
		this.endDate = queryHandler.getCreatedLatest();
	}

	/**
	 * @return key of the Jira project.
	 */
	public String getProjectKey() {
		return project.getProjectKey();
	}

	/**
	 * @param projectKey
	 *            of the Jira project.
	 */
	@JsonProperty("projectKey")
	public void setProjectKey(String projectKey) {
		this.project = new DecisionKnowledgeProject(projectKey);
	}

	/**
	 * @return search term. This string can also be a Jira Query or a predefined
	 *         {@link JiraFilter} (e.g. allopenissues).
	 */
	public String getSearchTerm() {
		if (this.searchTerm == null) {
			this.searchTerm = "";
		}
		return searchTerm;
	}

	/**
	 * @param searchTerm
	 *            search term. This string can also be a Jira Query or a predefined
	 *            {@link JiraFilter} (e.g. allopenissues).
	 */
	@JsonProperty("searchTerm")
	public void setSearchTerm(String searchTerm) {
		this.searchTerm = searchTerm;
	}

	/**
	 * @return earliest creation date of an element to be included in the
	 *         filter/shown in the knowledge graph.
	 */
	public long getCreatedEarliest() {
		return startDate;
	}

	/**
	 * @param createdEarliest
	 *            earliest creation date of an element to be included in the
	 *            filter/shown in the knowledge graph.
	 */
	@JsonProperty("createdEarliest")
	public void setCreatedEarliest(long createdEarliest) {
		this.startDate = createdEarliest;
	}

	/**
	 * @return latest creation date of an element to be included in the filter/shown
	 *         in the knowledge graph.
	 */
	public long getCreatedLatest() {
		return endDate;
	}

	/**
	 * @param createdLatest
	 *            latest creation date of an element to be included in the
	 *            filter/shown in the knowledge graph.
	 */
	@JsonProperty("createdLatest")
	public void setCreatedLatest(long createdLatest) {
		this.endDate = createdLatest;
	}

	/**
	 * @return list of {@link DocumentationLocation}s to be shown in the knowledge
	 *         graph.
	 */
	public List<DocumentationLocation> getDocumentationLocations() {
		return documentationLocations;
	}

	/**
	 * @return list of {@link DocumentationLocation}s to be shown in the knowledge
	 *         graph as Strings.
	 */
	@XmlElement(name = "documentationLocations")
	public List<String> getNamesOfDocumentationLocations() {
		List<String> documentationLocations = new ArrayList<String>();
		for (DocumentationLocation location : getDocumentationLocations()) {
			documentationLocations.add(DocumentationLocation.getName(location));
		}
		return documentationLocations;
	}

	/**
	 * @param documentationLocations
	 *            {@link DocumentationLocation}s to be shown in the knowledge graph
	 *            as Strings.
	 */
	@JsonProperty("documentationLocations")
	public void setDocumentationLocations(List<String> namesOfDocumentationLocations) {
		this.documentationLocations = new ArrayList<DocumentationLocation>();
		if (namesOfDocumentationLocations == null) {
			this.documentationLocations = DocumentationLocation.getAllDocumentationLocations();
			return;
		}
		for (String location : namesOfDocumentationLocations) {
			this.documentationLocations.add(DocumentationLocation.getDocumentationLocationFromString(location));
		}
	}

	/**
	 * @return list of knowledge types to be shown in the knowledge graph.
	 */
	@XmlElement(name = "jiraIssueTypes")
	public Set<String> getJiraIssueTypes() {
		if (jiraIssueTypes == null && project != null) {
			jiraIssueTypes = project.getJiraIssueTypeNames();
		}
		return jiraIssueTypes;
	}

	/**
	 * @param namesOfTypes
	 *            list of names of Jira {@link IssueType}s as Strings.
	 */
	@JsonProperty("jiraIssueTypes")
	public void setJiraIssueTypes(Set<String> namesOfTypes) {
		jiraIssueTypes = namesOfTypes;
	}

	/**
	 * @return list of {@link KnowledgeStatus} types to be shown in the knowledge
	 *         graph as strings.
	 */
	@XmlElement(name = "status")
	public List<KnowledgeStatus> getStatus() {
		return knowledgeStatus;
	}

	/**
	 * @param status
	 *            list of {@link KnowledgeStatus} types to be shown in the knowledge
	 *            graph as strings.
	 */
	@JsonProperty("status")
	public void setStatus(List<String> status) {
		knowledgeStatus = new ArrayList<KnowledgeStatus>();
		if (status == null) {
			for (KnowledgeStatus eachStatus : KnowledgeStatus.values()) {
				knowledgeStatus.add(eachStatus);
			}
			return;
		}
		for (String stringStatus : status) {
			knowledgeStatus.add(KnowledgeStatus.getKnowledgeStatus(stringStatus));
		}
	}

	/**
	 * @return list of {@link LinkType}s to be shown in the knowledge graph as
	 *         strings.
	 */
	@XmlElement(name = "linkTypes")
	public List<String> getLinkTypes() {
		return linkTypes;
	}

	/**
	 * @param namesOfTypes
	 *            list of {@link LinkType}s to be shown in the knowledge graph as
	 *            strings.
	 */
	@JsonProperty("linkTypes")
	public void setLinkTypes(List<String> namesOfTypes) {
		linkTypes = namesOfTypes;
	}

	/**
	 * @param decGroups
	 *            list of names of all groups.
	 */
	@JsonProperty("groups")
	public void setDecisionGroups(List<String> decGroups) {
		decisionGroups = decGroups;
	}

	/**
	 * @return list of names of all groups.
	 */
	@XmlElement(name = "groups")
	public List<String> getDecisionGroups() {
		return decisionGroups;
	}
}