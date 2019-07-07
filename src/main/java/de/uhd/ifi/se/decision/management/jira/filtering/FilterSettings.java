package de.uhd.ifi.se.decision.management.jira.filtering;

import java.util.List;

import org.codehaus.jackson.map.annotate.JsonDeserialize;

import com.atlassian.jira.issue.issuetype.IssueType;

import de.uhd.ifi.se.decision.management.jira.filtering.impl.FilterSettingsImpl;
import de.uhd.ifi.se.decision.management.jira.model.DocumentationLocation;

/**
 * Interface for the filter settings. The filter settings cover the key of the
 * selected project, the time frame, documentation locations, JIRA issue types,
 * and decision knowledge types. The search string can contain a JQL, a filter
 * or a search string form the frontend.
 */
@JsonDeserialize(as = FilterSettingsImpl.class)
public interface FilterSettings {

	/**
	 * Get the key of the project. The project is a JIRA project that is extended
	 * with settings for this plug-in, for example, whether the plug-in is activated
	 * for the project.
	 *
	 * @return key of the JIRA project.
	 */
	String getProjectKey();

	/**
	 * Set the key of the project. The project is a JIRA project that is extended
	 * with settings for this plug-in, for example, whether the plug-in is activated
	 * for the project.
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
	void setNamesOfSelectedJiraIssueTypesArray(List<String> types);

	/**
	 * TODO This method should only return JIRA issue types of the current project.
	 * Returns the names of all JIRA issue types available.
	 *
	 * @return list of names of JIRA {@link IssueType}s.
	 */
	public List<String> getAllJiraIssueTypes();
}
