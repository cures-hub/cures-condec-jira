package de.uhd.ifi.se.decision.management.jira.model.impl;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.codehaus.jackson.annotate.JsonProperty;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.issuetype.IssueType;
import com.atlassian.jira.user.ApplicationUser;

import de.uhd.ifi.se.decision.management.jira.filtering.QueryHandler;
import de.uhd.ifi.se.decision.management.jira.model.DocumentationLocation;
import de.uhd.ifi.se.decision.management.jira.model.FilterSettings;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;

/**
 * Model class for the filter settings.
 */
@XmlRootElement(name = "issueTypesForDropdown")
@XmlAccessorType(XmlAccessType.FIELD)
public class FilterSettingsImpl implements FilterSettings {

	private String projectKey;
	private String searchString;
	private long createdEarliest;
	private long createdLatest;
	private List<DocumentationLocation> documentationLocations;
	private List<KnowledgeType> issueTypes;
	
	@XmlElement
	private List<String> allIssueTypes;
	@XmlElement
	private List<String> issueTypesMatchingFilter;
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
		this.createdEarliest = createdEarliest;
		this.createdLatest = createdLatest;
	}

	public FilterSettingsImpl(String projectKey, String searchString, long createdEarliest, long createdLatest,
			String[] documentationLocations) {
		this(projectKey, searchString, createdEarliest, createdLatest);
		this.setDocumentationLocation(documentationLocations);
	}

	public FilterSettingsImpl(String projectKey, String searchString, long createdEarliest, long createdLatest,
			String[] documentationLocations, String[] knowledgeTypes) {
		this(projectKey, searchString, createdEarliest, createdLatest);
		this.setDocumentationLocation(documentationLocations);
		this.setIssueTypes(knowledgeTypes);
	}
	

	public FilterSettingsImpl(String projectKey, String query, ApplicationUser user) {
		this.allIssueTypes = new ArrayList<String>();
		this.issueTypesMatchingFilter = new ArrayList<String>();
		for (IssueType issueType : ComponentAccessor.getConstantsManager().getAllIssueTypeObjects()) {
			this.allIssueTypes.add(issueType.getName());
			this.issueTypesMatchingFilter.add(issueType.getName());
		}

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
		QueryHandler queryHandler = new QueryHandler(user, projectKey, false);

		if (!queryHandler.getFilterSettings().getIssueTypes().isEmpty()) {
			this.issueTypesMatchingFilter = new ArrayList<String>();
			for (KnowledgeType type : queryHandler.getFilterSettings().getIssueTypes()) {
				this.issueTypesMatchingFilter.add(type.toString());
			}
		}
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
		return createdEarliest;
	}

	@Override
	@JsonProperty("createdEarliest")
	public void setCreatedEarliest(long createdEarliest) {
		this.createdEarliest = createdEarliest;
	}

	@Override
	public long getCreatedLatest() {
		return createdLatest;
	}

	@Override
	@JsonProperty("createdLatest")
	public void setCreatedLatest(long createdLatest) {
		this.createdLatest = createdLatest;
	}

	@Override
	public List<DocumentationLocation> getDocumentationLocation() {
		if (this.documentationLocations == null) {
			this.documentationLocations = DocumentationLocation.getAllDocumentationLocations();
		}
		return documentationLocations;
	}
	
	@XmlElement(name="documentationLocations")
	public List<String> getDocumentationLocations() {
		List<String> documentationLocations = new ArrayList<String>();
		DocumentationLocation[] locations = DocumentationLocation.values();
		for (DocumentationLocation location : locations) {
			documentationLocations.add(DocumentationLocation.getName(location));
		}
		return documentationLocations;
	}

	@Override
	@JsonProperty("documentationLocationList")
	public void setDocumentationLocation(String[] documentationLocationArray) {
		documentationLocations = new ArrayList<>();
		for (String location : documentationLocationArray) {
			documentationLocations.add(DocumentationLocation.getDocumentationLocationFromString(location));
		}
	}

	@Override
	public List<KnowledgeType> getIssueTypes() {
		if (issueTypes == null) {
			issueTypes = new ArrayList<>();
			for (KnowledgeType type : KnowledgeType.getDefaultTypes()) {
				issueTypes.add(type);
			}
		}
		return issueTypes;
	}

	@Override
	@JsonProperty("issueTypes")
	public void setIssueTypes(String[] issueTypesArray) {
		issueTypes = new ArrayList<>();
		for (String typeString : issueTypesArray) {
			issueTypes.add(KnowledgeType.getKnowledgeType(typeString));
		}
	}

	@Override
	public void setIssueTypes(List<KnowledgeType> types) {
		issueTypes = types;
	}
	
	public List<String> getAllIssueTypes() {
		return allIssueTypes;
	}

	public void setAllIssueTypes(List<String> allIssueTypes) {
		this.allIssueTypes = allIssueTypes;
	}

	public List<String> getIssueTypesMatchingFilter() {
		return issueTypesMatchingFilter;
	}

	public void setIssueTypesMatchingFilter(List<String> issueTypesMatchingFilter) {
		this.issueTypesMatchingFilter = issueTypesMatchingFilter;
	}

	public long getStartDate() {
		return startDate;
	}

	public void setStartDate(long startDate) {
		this.startDate = startDate;
	}

	public long getEndDate() {
		return endDate;
	}

	public void setEndDate(long endDate) {
		this.endDate = endDate;
	}
}
