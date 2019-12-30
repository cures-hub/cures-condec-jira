package de.uhd.ifi.se.decision.management.jira.filtering;

import java.util.List;

import org.codehaus.jackson.map.annotate.JsonDeserialize;

import com.atlassian.jira.issue.issuetype.IssueType;

import de.uhd.ifi.se.decision.management.jira.filtering.impl.FilterSettingsImpl;
import de.uhd.ifi.se.decision.management.jira.model.DocumentationLocation;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeStatus;
import de.uhd.ifi.se.decision.management.jira.model.LinkType;

/**
 * Interface for the filter criteria. The filter settings cover the key of the
 * selected project, the time frame, documentation locations, Jira issue types,
 * and decision knowledge types. The search string can contain a query in Jira
 * Query Language (JQL), a {@link JiraFilter} or a search string specified in
 * the frontend of the plug-in.
 */
@JsonDeserialize(as = FilterSettingsImpl.class)
public interface FilterSettings {

	/**
	 * Returns the key of the JIRA project.
	 *
	 * @return key of the JIRA project.
	 */
	String getProjectKey();

	/**
	 * Sets the key of the JIRA project.
	 *
	 * @param projectKey
	 *            of the JIRA project.
	 */
	void setProjectKey(String projectKey);

	/**
	 * Returns the search string. This string can be a jql, a filter string.
	 *
	 * @return search String
	 */
	String getSearchString();

	/**
	 * Sets the search string of the filter.
	 *
	 * @param searchString
	 */
	void setSearchString(String searchString);

	/**
	 * Returns the earliest date when a element is created in milliseconds as long.
	 *
	 * @return date as long in milliseconds
	 */
	long getCreatedEarliest();

	/**
	 * Sets the earliest date when a element is created as a String.
	 *
	 * @param createdEarliest
	 *            String with a long
	 */
	void setCreatedEarliest(long createdEarliest);

	/**
	 * Returns the latest date when a element is created in milliseconds as long.
	 *
	 * @return date as long in milliseconds
	 */
	long getCreatedLatest();

	/**
	 * Set the latest date when a element is created as a String.
	 *
	 * @param createdLatest
	 *            String with a long
	 */
	void setCreatedLatest(long createdLatest);

	/**
	 * Returns a list of documentation locations to be shown in the knowledge graph.
	 *
	 * @see DocumentationLocation
	 * @return list of documentation locations.
	 */
	List<DocumentationLocation> getDocumentationLocations();

	/**
	 * Returns the names of the documentation locations to be shown in the knowledge
	 * graph.
	 *
	 * @see DocumentationLocation
	 * @return list of names of documentation locations.
	 */
	List<String> getNamesOfDocumentationLocations();

	/**
	 * Set the documentation locations where the data is stored
	 *
	 * @param documentationLocations
	 *            whit the locations as string
	 */
	void setDocumentationLocations(List<String> documentationLocations);

	/**
	 * Gets the selected knowledge types from the filter
	 *
	 * @return list of knowledge types of the filter
	 */
	List<String> getNamesOfSelectedJiraIssueTypes();

	/**
	 * Returns the names of the JIRA issue types to be shown in the knowledge graph
	 * as a list.
	 *
	 * @return list of names of JIRA {@link IssueType}s.
	 */
	void setSelectedJiraIssueTypes(List<String> types);

	/**
	 * Gets the selected {@link KnowledgeStatus} types from the filter
	 * 
	 * @return list of {@link KnowledgeStatus} types as string
	 */
	List<KnowledgeStatus> getSelectedStatus();

	/**
	 * Sets the issue status that a selected of a Filter
	 * 
	 * @param linkTypes
	 */
	void setSelectedLinkTypes(List<String> linkTypes);

	/**
	 * Gets the selected link types from the filter
	 * 
	 * @return list of link types as string
	 */
	List<String> getNamesOfSelectedLinkTypes();

	/**
	 * Sets the link types that a selected of a Filter
	 * 
	 * @param linkTypes
	 */
	void setSelectedStatus(List<String> linkTypes);

	/**
	 * Returns the names of all JIRA issue types of the selected project.
	 *
	 * @return list of names of JIRA {@link IssueType}s.
	 */
	List<String> getAllJiraIssueTypes();

	/**
	 * Returns the names of all {@link KnowledgeStatus}
	 * 
	 * @return list of names of JIRA {@link KnowledgeStatus}.
	 */
	List<String> getAllStatus();

	/**
	 * Returns the names of all link types
	 * 
	 * @return list of names of {@link LinkType}s.
	 */
	List<String> getAllLinkTypes();

}
