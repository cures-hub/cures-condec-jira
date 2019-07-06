package de.uhd.ifi.se.decision.management.jira.filtering;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.issuetype.IssueType;
import com.atlassian.jira.user.ApplicationUser;

import de.uhd.ifi.se.decision.management.jira.model.DocumentationLocation;
import de.uhd.ifi.se.decision.management.jira.model.FilterSettings;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.model.impl.FilterSettingsImpl;

// TODO Delete class and replace with FilterSettings directly
@XmlRootElement(name = "issueTypesForDropdown")
@XmlAccessorType(XmlAccessType.FIELD)
public class FilterDataProvider {
	@XmlElement
	private List<String> allIssueTypes;
	@XmlElement
	private List<String> issueTypesMatchingFilter;
	@XmlElement
	private long startDate;
	@XmlElement
	private long endDate;
	@XmlElement
	private List<String> documentationLocations;

	public FilterDataProvider(String projectKey, String query, ApplicationUser user) {
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
			this.documentationLocations.add(DocumentationLocation.getName(location));
		}
	}

	public void initFilterSettingsFromQuery(String projectKey, String query, ApplicationUser user) {
		if (!query.matches("\\?jql=(.)+") && !query.matches("\\?filter=(.)+")) {
			return;
		}
		FilterSettings filterSettings = new FilterSettingsImpl(projectKey, query);
		GraphFiltering filter = new GraphFiltering(filterSettings, user, false);
		QueryHandler queryHandler = new QueryHandler(user, projectKey, false);
		filter.getJiraIssuesFromQuery(query);

		if (!queryHandler.getFilterSettings().getIssueTypes().isEmpty()) {
			this.issueTypesMatchingFilter = new ArrayList<String>();
			for (KnowledgeType type : queryHandler.getFilterSettings().getIssueTypes()) {
				this.issueTypesMatchingFilter.add(type.toString());
			}
		}
		this.startDate = queryHandler.getFilterSettings().getCreatedEarliest();
		this.endDate = queryHandler.getFilterSettings().getCreatedLatest();
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

	public List<String> getDocumentationLocations() {
		return this.documentationLocations;
	}

	public void setDocumentationLocations(List<String> documentationLocations) {
		this.documentationLocations = documentationLocations;
	}
}