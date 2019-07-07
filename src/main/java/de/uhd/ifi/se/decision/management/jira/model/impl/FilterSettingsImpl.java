package de.uhd.ifi.se.decision.management.jira.model.impl;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;

import org.codehaus.jackson.annotate.JsonProperty;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.issuetype.IssueType;
import com.atlassian.jira.user.ApplicationUser;

import de.uhd.ifi.se.decision.management.jira.filtering.JiraQueryHandler;
import de.uhd.ifi.se.decision.management.jira.model.DocumentationLocation;
import de.uhd.ifi.se.decision.management.jira.model.FilterSettings;

/**
 * Model class for the filter settings.
 */
public class FilterSettingsImpl implements FilterSettings {

	private String projectKey;
	private String searchString;
	private List<DocumentationLocation> documentationLocations;
	private List<String> namesOfSelectedJiraIssueTypes;

	@XmlElement
	private long startDate;
	@XmlElement
	private long endDate;

	public FilterSettingsImpl(String projectKey, String searchString) {
		this.projectKey = projectKey;
		this.searchString = searchString;
		this.namesOfSelectedJiraIssueTypes = getAllJiraIssueTypes();
		this.startDate = -1;
		this.endDate = -1;
		
		// TODO Add method in enum class
		this.documentationLocations = new ArrayList<>();
		DocumentationLocation[] locations = DocumentationLocation.values();
		for (DocumentationLocation location : locations) {
			this.documentationLocations.add(location);
		}
	}

	public FilterSettingsImpl(String projectKey, String searchString, long createdEarliest, long createdLatest) {
		this(projectKey, searchString);
		this.startDate = createdEarliest;
		this.endDate = createdLatest;
	}

	public FilterSettingsImpl(String projectKey, String searchString, long createdEarliest, long createdLatest,
			String[] documentationLocations) {
		this(projectKey, searchString, createdEarliest, createdLatest);
		this.setDocumentationLocations(documentationLocations);
	}

	public FilterSettingsImpl(String projectKey, String searchString, long createdEarliest, long createdLatest,
			String[] documentationLocations, String[] knowledgeTypes) {
		this(projectKey, searchString, createdEarliest, createdLatest);
		this.setDocumentationLocations(documentationLocations);
		this.setNamesOfSelectedJiraIssueTypes(knowledgeTypes);
	}

	public FilterSettingsImpl(String projectKey, String query, ApplicationUser user) {		
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
		if (this.documentationLocations == null) {
			this.documentationLocations = DocumentationLocation.getAllDocumentationLocations();
		}
		return documentationLocations;
	}

	@Override
	@XmlElement(name = "documentationLocations")
	public List<String> getNamesOfDocumentationLocations() {
		List<String> documentationLocations = new ArrayList<String>();
		DocumentationLocation[] locations = DocumentationLocation.values();
		for (DocumentationLocation location : locations) {
			documentationLocations.add(DocumentationLocation.getName(location));
		}
		return documentationLocations;
	}

	@Override
	@JsonProperty("documentationLocationList")
	public void setDocumentationLocations(String[] documentationLocationArray) {
		documentationLocations = new ArrayList<>();
		for (String location : documentationLocationArray) {
			documentationLocations.add(DocumentationLocation.getDocumentationLocationFromString(location));
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
	@JsonProperty("issueTypes")
	public void setNamesOfSelectedJiraIssueTypes(String[] namesOfSelectedTypes) {
		namesOfSelectedJiraIssueTypes = new ArrayList<>();
		for (String typeString : namesOfSelectedTypes) {
			namesOfSelectedJiraIssueTypes.add(typeString);
		}
	}

	@Override
	public void setNamesOfSelectedJiraIssueTypes(List<String> types) {
		namesOfSelectedJiraIssueTypes = types;
	}

	@Override
	@XmlElement(name = "allJiraIssueTypes")
	public List<String> getAllJiraIssueTypes() {
		List<String> allIssueTypes = new ArrayList<String>();
		for (IssueType issueType : ComponentAccessor.getConstantsManager().getAllIssueTypeObjects()) {
			allIssueTypes.add(issueType.getName());

		}
		return allIssueTypes;
	}
}
