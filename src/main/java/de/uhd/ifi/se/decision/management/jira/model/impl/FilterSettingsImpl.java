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
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;

/**
 * Model class for the filter settings.
 */
public class FilterSettingsImpl implements FilterSettings {

	private String projectKey;
	private String searchString;
	private List<DocumentationLocation> documentationLocations;
	
	// TODO Merge both lists
	private List<String> issueTypes;
	private List<IssueType> selectedJiraIssueTypes;

	@XmlElement
	private long startDate;
	@XmlElement
	private long endDate;

	public FilterSettingsImpl() {
		projectKey = "";
		searchString = "";
	}

	public FilterSettingsImpl(String projectKey, String searchString) {
		this.projectKey = projectKey;
		this.searchString = searchString;
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
		this.setIssueTypes(knowledgeTypes);
	}

	public FilterSettingsImpl(String projectKey, String query, ApplicationUser user) {
		this.selectedJiraIssueTypes = new ArrayList<IssueType>(
				ComponentAccessor.getConstantsManager().getAllIssueTypeObjects());

		this.startDate = -1;
		this.endDate = -1;

		initFilterSettingsFromQuery(projectKey, query, user);

		this.documentationLocations = new ArrayList<>();
		DocumentationLocation[] locations = DocumentationLocation.values();
		for (DocumentationLocation location : locations) {
			this.documentationLocations.add(location);
		}
	}

	public void initFilterSettingsFromQuery(String projectKey, String query, ApplicationUser user) {
		if (!query.matches("\\?jql=(.)+") && !query.matches("\\?filter=(.)+")) {
			return;
		}
		JiraQueryHandler queryHandler = new JiraQueryHandler(user, projectKey, false);

		// if (!queryHandler.getFilterSettings().getIssueTypes().isEmpty()) {
		// this.issueTypesMatchingFilter = new ArrayList<String>();
		// for (KnowledgeType type : queryHandler.getFilterSettings().getIssueTypes()) {
		// this.issueTypesMatchingFilter.add(type.toString());
		// }
		// }
		this.startDate = queryHandler.getFilterSettings().getCreatedEarliest();
		this.endDate = queryHandler.getFilterSettings().getCreatedLatest();
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
	public List<String> getIssueTypes() {
		if (issueTypes == null) {
			issueTypes = new ArrayList<>();
			for (KnowledgeType type : KnowledgeType.getDefaultTypes()) {
				issueTypes.add(type.name());
			}
		}
		return issueTypes;
	}

	@Override
	@JsonProperty("issueTypes")
	public void setIssueTypes(String[] issueTypesArray) {
		issueTypes = new ArrayList<>();
		for (String typeString : issueTypesArray) {
			issueTypes.add(typeString);
		}
	}

	@Override
	public void setIssueTypes(List<String> types) {
		issueTypes = types;
	}

	@Override
	public List<IssueType> getSelectedJiraIssueTypes() {
		return selectedJiraIssueTypes;
	}

	@Override
	public void setSelectedJiraIssueTypes(List<IssueType> selectedJiraIssueTypes) {
		this.selectedJiraIssueTypes = selectedJiraIssueTypes;
	}
	
	@Override
	@XmlElement(name = "selectedJiraIssueTypes")
	public List<String> getNamesOfSelectedJiraIssueTypes() {
		List<String> issueTypesMatchingFilter = new ArrayList<String>();
		for (IssueType type : selectedJiraIssueTypes) {
			issueTypesMatchingFilter.add(type.getName());
		}
		return issueTypesMatchingFilter;
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
