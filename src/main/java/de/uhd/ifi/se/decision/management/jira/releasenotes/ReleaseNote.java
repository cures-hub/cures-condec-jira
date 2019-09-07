package de.uhd.ifi.se.decision.management.jira.releasenotes;

import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeProject;
import de.uhd.ifi.se.decision.management.jira.releasenotes.impl.ReleaseNoteImpl;
import org.codehaus.jackson.map.annotate.JsonDeserialize;

/**
 * Interface for release notes
 */
@JsonDeserialize(as = ReleaseNoteImpl.class)
public interface ReleaseNote {

	/**
	 * Get the id of the release note.
	 * @return id of the release note.
	 */
	long getId();

	/**
	 * Set the id of the release note.
	 * @param id of the release note.
	 */
	void setId(long id);

	/**
	 * Get the title of the release note.
	 *
	 * @return title of the release note.
	 */
	String getTitle();

	/**
	 * Set the summary of the release note.
	 *
	 * @param title of the release note.
	 */
	void setTitle(String title);

	/**
	 * Get the startDate of the release note.
	 *
	 * @return startDate of the release note.
	 */
	String getStartDate();


	/**
	 * Set the startDate of the release note.
	 *
	 * @param startDate of the release note.
	 */
	void setStartDate(String startDate);


	/**
	 * Get the end Date of the release note.
	 *
	 * @return timeRange of the release note.
	 */
	String getEndDate();


	/**
	 * Set the end date of the release note.
	 *
	 * @param endDate of the release note.
	 */
	void setEndDate(String endDate);

	/**
	 * Get the project key that the release note belongs to. The project
	 * is a JIRA project that is extended with settings for this plug-in, for
	 * example, whether the plug-in is activated for the project.
	 *
	 * @see DecisionKnowledgeProject
	 */
	String getProjectKey();

	/**
	 * Set the project that the release note belongs to via its key.
	 * The project is a JIRA project that is extended with settings for this
	 * plug-in, for example, whether the plug-in is activated for the project.
	 *
	 * @param projectKey key of JIRA project.
	 * @see DecisionKnowledgeProject
	 */
	void setProjectKey(String projectKey);

	/**
	 * Get the content of the release note.
	 * The content is in HTML, txt or md.
	 *
	 * @return content of the release note.
	 */
	String getContent();

	/**
	 * Set the description of the release note. The content
	 * is in HTML, txt or md.
	 *
	 * @param content of the release note.
	 */
	void setContent(String content);

}