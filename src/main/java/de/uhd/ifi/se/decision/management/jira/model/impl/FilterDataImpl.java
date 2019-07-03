package de.uhd.ifi.se.decision.management.jira.model.impl;

import com.atlassian.jira.user.ApplicationUser;
import de.uhd.ifi.se.decision.management.jira.model.DocumentationLocation;
import de.uhd.ifi.se.decision.management.jira.model.FilterData;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import org.codehaus.jackson.annotate.JsonProperty;

import java.util.ArrayList;
import java.util.List;

/**
 * Model class for filter data
 */
public class FilterDataImpl implements FilterData {

	private String projectKey;
	private String searchString;
	private long createdEarliest;
	private long createdLatest;
	private List<DocumentationLocation> documentationLocationList;
	private List<KnowledgeType>  issueTypes;

	public FilterDataImpl(String projectKey,String searchString){
		this.projectKey = projectKey;
		this.searchString = searchString;
	}

	public FilterDataImpl(String projectKey, String searchString, long createdEarliest, long createdLatest){
		this(projectKey, searchString);
		this.createdEarliest = createdEarliest;
		this.createdLatest = createdLatest;
	}

	public FilterDataImpl(String projectKey, String searchString , long createdEarliest, long createdLatest, List<DocumentationLocation> documentationLocations){
		this(projectKey, searchString, createdEarliest, createdLatest);
		this.documentationLocationList = documentationLocations;
	}

	public FilterDataImpl(String projectKey, String searchString, long createdEarliest, long createdLatest,
	                      List<DocumentationLocation> documentationLocations, List<KnowledgeType> knowledgeTypes){
		this(projectKey, searchString, createdEarliest, createdLatest, documentationLocations);
		this.issueTypes = knowledgeTypes;
	}

	@Override
	public String getProjectKey() {
		return projectKey;
	}

	@Override
	public void setProjectKey(String projectKey) {
		this.projectKey = projectKey;
	}

	@Override
	public String getSearchString() {
		return searchString;
	}

	@Override
	public void setSearchString(String searchString) {
		this.searchString = searchString;
	}

	@Override
	public long getCreatedEarliest() {
		return createdEarliest;
	}

	@Override
	public void setCreatedEarliest(String earliest) {
		this.createdEarliest = Long.parseLong(earliest);
	}

	@Override
	public long getCreatedLatest() {
		return createdLatest;
	}

	@Override
	public void setCreatedLatest(String latest) {
		this.createdLatest = Long.parseLong(latest);
	}

	@Override
	public List<DocumentationLocation> getDocumentationLocation() {
		return documentationLocationList;
	}

	@Override
	@JsonProperty("documentationLocationArray")
	public void setDocumentationLocation(String[] documentationLocationArray) {
		documentationLocationList = new ArrayList<>();
		for(String location: documentationLocationArray){
			documentationLocationList.add(DocumentationLocation.getDocumentationLocationFromString(location));
		}
	}

	@Override
	public List<KnowledgeType> getIssueTypes() {
		return issueTypes;
	}

	@Override
	public void setIssueTypes(String[] issueTypesArray) {
		issueTypes = new ArrayList<>();
		for (String typeString : issueTypesArray) {
			issueTypes.add(KnowledgeType.getKnowledgeType(typeString));
		}
	}
}
