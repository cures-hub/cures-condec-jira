package de.uhd.ifi.se.decision.management.jira.filtering.impl;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;

import org.codehaus.jackson.annotate.JsonProperty;

import com.atlassian.jira.issue.issuetype.IssueType;
import com.atlassian.jira.user.ApplicationUser;

import de.uhd.ifi.se.decision.management.jira.config.JiraIssueTypeGenerator;
import de.uhd.ifi.se.decision.management.jira.filtering.FilterSettings;
import de.uhd.ifi.se.decision.management.jira.filtering.JiraQueryHandler;
import de.uhd.ifi.se.decision.management.jira.model.DocumentationLocation;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeStatus;
import de.uhd.ifi.se.decision.management.jira.model.LinkType;

/**
 * Model class for the filter criteria. The filter settings cover the key of the
 * selected project, the time frame, documentation locations, Jira issue types,
 * and decision knowledge types. The search string can contain a JQL, a filter
 * or a search string specified in the frontend of the plug-in.
 */
public class FilterSettingsImpl implements FilterSettings {

	private String projectKey;
	private String searchString;
	private List<DocumentationLocation> documentationLocations;
	private List<String> namesOfSelectedJiraIssueTypes;
	private List<KnowledgeStatus> issueStatus;
	private List<String> namesOfSelectedLinkTypes;

	@XmlElement
	private long startDate;
	@XmlElement
	private long endDate;

	// This default constructor is necessary for the JSON string to object mapping.
	// Do not delete it!
	public FilterSettingsImpl() {
		this.projectKey = "";
		this.searchString = "";
	}

	public FilterSettingsImpl(String projectKey, String searchString) {
		this.projectKey = projectKey;
		this.searchString = searchString;
		this.namesOfSelectedJiraIssueTypes = getAllJiraIssueTypes();
		this.namesOfSelectedLinkTypes = getAllLinkTypes();
		this.startDate = -1;
		this.endDate = -1;
		this.documentationLocations = DocumentationLocation.getAllDocumentationLocations();
		this.issueStatus = KnowledgeStatus.getAllKnowledgeStatus();
	}

	public FilterSettingsImpl(String projectKey, String query, ApplicationUser user) {
		this(projectKey, query);

		JiraQueryHandler queryHandler = new JiraQueryHandlerImpl(user, projectKey, query);
		this.searchString = queryHandler.getQuery();

		List<String> namesOfJiraIssueTypesInQuery = queryHandler.getNamesOfJiraIssueTypesInQuery();
		if (!namesOfJiraIssueTypesInQuery.isEmpty()) {
			this.namesOfSelectedJiraIssueTypes = namesOfJiraIssueTypesInQuery;
		}

		this.startDate = queryHandler.getCreatedEarliest();
		this.endDate = queryHandler.getCreatedLatest();
	}

	@Override
	public String getProjectKey() {
		return projectKey;
	}

	@Override
	@JsonProperty("projectKey")
	public void setProjectKey(String projectKey) {
		this.projectKey = projectKey;
	}

	@Override
	public String getSearchString() {
		if (this.searchString == null) {
			this.searchString = "";
		}
		return searchString;
	}

	@Override
	@JsonProperty("searchString")
	public void setSearchString(String searchString) {
		this.searchString = searchString;
	}

	@Override
	public long getCreatedEarliest() {
		return startDate;
	}

	@Override
	@JsonProperty("createdEarliest")
	public void setCreatedEarliest(long createdEarliest) {
		this.startDate = createdEarliest;
	}

	@Override
	public long getCreatedLatest() {
		return endDate;
	}

	@Override
	@JsonProperty("createdLatest")
	public void setCreatedLatest(long createdLatest) {
		this.endDate = createdLatest;
	}

	@Override
	public List<DocumentationLocation> getDocumentationLocations() {
		return documentationLocations;
	}

	@Override
	@XmlElement(name = "documentationLocations")
	public List<String> getNamesOfDocumentationLocations() {
		List<String> documentationLocations = new ArrayList<String>();
		for (DocumentationLocation location : getDocumentationLocations()) {
			documentationLocations.add(DocumentationLocation.getName(location));
		}
		return documentationLocations;
	}

	@Override
	@JsonProperty("documentationLocations")
	public void setDocumentationLocations(List<String> namesOfDocumentationLocations) {
		this.documentationLocations = new ArrayList<>();
		if (namesOfDocumentationLocations == null) {
			return;
		}
		for (String location : namesOfDocumentationLocations) {
			this.documentationLocations.add(DocumentationLocation.getDocumentationLocationFromString(location));
		}
	}

	@Override
	@XmlElement(name = "selectedJiraIssueTypes")
	public List<String> getNamesOfSelectedJiraIssueTypes() {
		if (namesOfSelectedJiraIssueTypes == null) {
			namesOfSelectedJiraIssueTypes = getAllJiraIssueTypes();
		}
		return namesOfSelectedJiraIssueTypes;
	}

	@Override
	@JsonProperty("selectedJiraIssueTypes")
	public void setSelectedJiraIssueTypes(List<String> namesOfTypes) {
		namesOfSelectedJiraIssueTypes = namesOfTypes;
	}

	@Override
	@XmlElement(name = "selectedStatus")
	public List<KnowledgeStatus> getSelectedStatus() {
		if (issueStatus == null) {
			issueStatus = KnowledgeStatus.getAllKnowledgeStatus();
		}
		return issueStatus;
	}

	@Override
	@JsonProperty("selectedStatus")
	public void setSelectedStatus(List<String> status) {
		if (issueStatus == null) {
			issueStatus = new ArrayList<>();
		}
		if (status == null || status.size() == 0) {
			return;
		}
		for (String stringStatus : status) {
			issueStatus.add(KnowledgeStatus.getKnowledgeStatus(stringStatus));
		}
	}

	@Override
	@XmlElement(name = "selectedLinkTypes")
	public List<String> getNamesOfSelectedLinkTypes() {
		if (namesOfSelectedLinkTypes == null) {
			namesOfSelectedLinkTypes = getAllLinkTypes();
		}
		return namesOfSelectedLinkTypes;
	}

	@Override
	@JsonProperty("selectedLinkTypes")
	public void setSelectedLinkTypes(List<String> namesOfTypes) {
		namesOfSelectedLinkTypes = namesOfTypes;
	}

	@Override
	@XmlElement(name = "allJiraIssueTypes")
	public List<String> getAllJiraIssueTypes() {
		List<String> allIssueTypes = new ArrayList<String>();
		for (IssueType issueType : JiraIssueTypeGenerator.getJiraIssueTypes(projectKey)) {
			allIssueTypes.add(issueType.getNameTranslation());

		}
		return allIssueTypes;
	}

	@Override
	@XmlElement(name = "allIssueStatus")
	public List<String> getAllStatus() {
		return KnowledgeStatus.toList();
	}

	@Override
	@XmlElement(name = "allLinkTypes")
	public List<String> getAllLinkTypes() {
		return LinkType.toList();
	}

}
