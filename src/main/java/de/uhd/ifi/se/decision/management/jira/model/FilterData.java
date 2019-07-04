package de.uhd.ifi.se.decision.management.jira.model;

import de.uhd.ifi.se.decision.management.jira.model.impl.FilterDataImpl;
import org.codehaus.jackson.map.annotate.JsonDeserialize;

import java.util.List;

/**
 * Interface for a filter data and its search string. The filter data
 * is from the filter in the views from the ConDec Plugin. The search string
 * can contain a JQL, a filter or a search string form the frontend.
 */
@JsonDeserialize(as = FilterDataImpl.class)
public interface FilterData {

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
	 * @param projectKey of the JIRA project.
	 */
	void setProjectKey(String projectKey);

	/**
	 * Returns the search string. This string can be a jql, a filter string.
	 *
	 * @return search String
	 */
	String getSearchString();

	/**
	 * Set the search string of the filter.
	 *
	 * @param searchString
	 */
	void setSearchString(String searchString);

	/**
	 * Returns the earliest date when a element is created in milliseconds
	 * as long.
	 *
	 * @return date as long in milliseconds
	 */
	long getCreatedEarliest();

	/**
	 * Set the earliest date when a element is created as a String.
	 *
	 * @param createdEarliest String with a long
	 */
	void setCreatedEarliest(long createdEarliest);

	/**
	 * Returns the latest date when a element is created in milliseconds
	 * as long.
	 *
	 * @return date as long in milliseconds
	 */
	long getCreatedLatest();


	/**
	 * Set the latest date when a element is created as a String.
	 *
	 * @param createdLatest String with a long
	 */
	void setCreatedLatest(long createdLatest);


	/**
	 * Returns a list of documentation locations where the data can
	 * be saved.
	 *
	 * @return list of documentation locations
	 */
	List<DocumentationLocation> getDocumentationLocation();

	/**
	 * Set the documentation locations where the data is stored
	 *
	 * @param documentationLocationArray whit the locations as string
	 */
	void setDocumentationLocation(String[] documentationLocationArray);

	/**
	 * Gets the selected knowledge types from the filter
	 *
	 * @return list of knowledge types of the filter
	 */
	List<KnowledgeType> getIssueTypes();

	/**
	 * Set the issue types that are used in the filter from the
	 * issueTypeString to the knowledge type list.
	 *
	 * @param issueTypesArray from the json
	 */
	void setIssueTypes(String[] issueTypesArray);


	/**
	 * Set the issuetypes that are used in the filter from a
	 * list of Knowledge Types.
	 *
	 * @param types
	 */
	void setIssueTypes(List<KnowledgeType> types);

}
