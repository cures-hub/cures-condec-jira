package de.uhd.ifi.se.decision.management.jira.model.impl;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.annotate.JsonProperty;

import de.uhd.ifi.se.decision.management.jira.model.DocumentationLocation;
import de.uhd.ifi.se.decision.management.jira.model.FilterSettings;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;

/**
 * Model class for the filter settings.
 */
public class FilterSettingsImpl implements FilterSettings {

	private String projectKey;
	private String searchString;
	private long createdEarliest;
	private long createdLatest;
	private List<DocumentationLocation> documentationLocations;
	private List<KnowledgeType> issueTypes;

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
}
