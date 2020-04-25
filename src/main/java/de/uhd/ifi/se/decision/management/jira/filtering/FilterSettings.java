package de.uhd.ifi.se.decision.management.jira.filtering;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

import com.atlassian.jira.issue.issuetype.IssueType;
import com.atlassian.jira.user.ApplicationUser;

import de.uhd.ifi.se.decision.management.jira.config.JiraIssueTypeGenerator;
import de.uhd.ifi.se.decision.management.jira.model.DocumentationLocation;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeStatus;
import de.uhd.ifi.se.decision.management.jira.model.LinkType;
import de.uhd.ifi.se.decision.management.jira.persistence.DecisionGroupManager;

/**
 * Represents the filter criteria. The filter settings cover the key of the
 * selected project, the time frame, documentation locations, Jira issue types,
 * and decision knowledge types. The search string can contain a query in Jira
 * Query Language (JQL), a {@link JiraFilter} or a search string specified in
 * the frontend of the plug-in.
 */
public class FilterSettings {

	private String projectKey;
	private String searchString;
	private List<DocumentationLocation> documentationLocations;
	private List<String> namesOfSelectedJiraIssueTypes;
	private List<KnowledgeStatus> knowledgeStatus;
	private List<String> namesOfSelectedLinkTypes;
	private List<String> decisionGroups;

	@XmlElement
	private long startDate;
	@XmlElement
	private long endDate;

	@JsonCreator
	public FilterSettings(@JsonProperty("projectKey") String projectKey,
			@JsonProperty("searchString") String searchString) {
		this.projectKey = projectKey;
		this.searchString = searchString;
		this.namesOfSelectedJiraIssueTypes = getAllJiraIssueTypes();
		this.namesOfSelectedLinkTypes = getAllLinkTypes();
		this.startDate = -1;
		this.endDate = -1;
		this.documentationLocations = DocumentationLocation.getAllDocumentationLocations();
		this.knowledgeStatus = KnowledgeStatus.getAllKnowledgeStatus();
		this.decisionGroups = DecisionGroupManager.getAllDecisionGroups(projectKey);
	}

	public FilterSettings(String projectKey, String query, ApplicationUser user) {
		this(projectKey, query);

		JiraQueryHandler queryHandler = new JiraQueryHandler(user, projectKey, query);
		this.searchString = queryHandler.getQuery();

		List<String> namesOfJiraIssueTypesInQuery = queryHandler.getNamesOfJiraIssueTypesInQuery();
		if (!namesOfJiraIssueTypesInQuery.isEmpty()) {
			this.namesOfSelectedJiraIssueTypes = namesOfJiraIssueTypesInQuery;
		}

		this.startDate = queryHandler.getCreatedEarliest();
		this.endDate = queryHandler.getCreatedLatest();
	}

	/**
	 * @return key of the Jira project.
	 */
	public String getProjectKey() {
		return projectKey;
	}

	/**
	 * @param projectKey
	 *            of the Jira project.
	 */
	@JsonProperty("projectKey")
	public void setProjectKey(String projectKey) {
		this.projectKey = projectKey;
	}

	/**
	 * @return search String. This string can also be a Jira Query or a predefined
	 *         filter (e.g. allopenissues).
	 */
	public String getSearchString() {
		if (this.searchString == null) {
			this.searchString = "";
		}
		return searchString;
	}

	/**
	 * @param searchString
	 *            search String. This string can also be a Jira Query or a
	 *            predefined filter (e.g. allopenissues).
	 */
	@JsonProperty("searchString")
	public void setSearchString(String searchString) {
		this.searchString = searchString;
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
		if (documentationLocations == null) {
			documentationLocations = DocumentationLocation.getAllDocumentationLocations();
		}
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
	@XmlElement(name = "selectedJiraIssueTypes")
	public List<String> getNamesOfSelectedJiraIssueTypes() {
		if (namesOfSelectedJiraIssueTypes == null) {
			namesOfSelectedJiraIssueTypes = getAllJiraIssueTypes();
		}
		return namesOfSelectedJiraIssueTypes;
	}

	/**
	 * @param namesOfTypes
	 *            list of names of Jira {@link IssueType}s as Strings.
	 */
	@JsonProperty("selectedJiraIssueTypes")
	public void setSelectedJiraIssueTypes(List<String> namesOfTypes) {
		namesOfSelectedJiraIssueTypes = namesOfTypes;
	}

	/**
	 * @return list of {@link KnowledgeStatus} types to be shown in the knowledge
	 *         graph as strings.
	 */
	@XmlElement(name = "selectedStatus")
	public List<KnowledgeStatus> getSelectedStatus() {
		if (knowledgeStatus == null) {
			knowledgeStatus = KnowledgeStatus.getAllKnowledgeStatus();
		}
		return knowledgeStatus;
	}

	/**
	 * @param status
	 *            list of {@link KnowledgeStatus} types to be shown in the knowledge
	 *            graph as strings.
	 */
	@JsonProperty("selectedStatus")
	public void setSelectedStatus(List<String> status) {
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
	@XmlElement(name = "selectedLinkTypes")
	public List<String> getNamesOfSelectedLinkTypes() {
		if (namesOfSelectedLinkTypes == null) {
			namesOfSelectedLinkTypes = getAllLinkTypes();
		}
		return namesOfSelectedLinkTypes;
	}

	/**
	 * @param namesOfTypes
	 *            list of {@link LinkType}s to be shown in the knowledge graph as
	 *            strings.
	 */
	@JsonProperty("selectedLinkTypes")
	public void setSelectedLinkTypes(List<String> namesOfTypes) {
		namesOfSelectedLinkTypes = namesOfTypes;
	}

	/**
	 * @return list of names of all Jira {@link IssueType}s of the selected project.
	 */
	@XmlElement(name = "allJiraIssueTypes")
	public List<String> getAllJiraIssueTypes() {
		List<String> allIssueTypes = new ArrayList<String>();
		for (IssueType issueType : JiraIssueTypeGenerator.getJiraIssueTypes(projectKey)) {
			allIssueTypes.add(issueType.getNameTranslation());

		}
		return allIssueTypes;
	}

	/**
	 * @return list of names of all {@link KnowledgeStatus}.
	 */
	@XmlElement(name = "allIssueStatus")
	public List<String> getAllStatus() {
		return KnowledgeStatus.toStringList();
	}

	/**
	 * @return list of names of all {@link LinkType}s.
	 */
	@XmlElement(name = "allLinkTypes")
	public List<String> getAllLinkTypes() {
		return LinkType.toStringList();
	}

	/**
	 * @param decGroups
	 *            list of names of all groups.
	 */
	@JsonProperty("selectedDecGroups")
	public void setSelectedDecGroups(List<String> decGroups) {
		decisionGroups = decGroups;
	}

	/**
	 * @return list of names of all groups.
	 */
	@XmlElement(name = "selectedDecGroups")
	public List<String> getSelectedDecGroups() {
		if (decisionGroups == null) {
			decisionGroups = Collections.emptyList();
		}
		return decisionGroups;
	}
}