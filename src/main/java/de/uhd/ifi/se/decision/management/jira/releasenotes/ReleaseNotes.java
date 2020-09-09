package de.uhd.ifi.se.decision.management.jira.releasenotes;

import javax.xml.bind.annotation.XmlElement;

import org.codehaus.jackson.annotate.JsonProperty;

import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeProject;
import de.uhd.ifi.se.decision.management.jira.persistence.tables.ReleaseNotesInDatabase;

/**
 * Models release notes.
 */
public class ReleaseNotes {

	private long id;
	private String title;
	private String content;
	private String projectKey;
	private String startDate;
	private String endDate;

	// This default constructor is necessary for the JSON string to object mapping.
	// Do not delete it!
	public ReleaseNotes() {
	}

	public ReleaseNotes(String title, String content, String projectKey, String startDate, String endDate) {
		this.title = title;
		this.content = content;
		this.projectKey = projectKey;
		this.startDate = startDate;
		this.endDate = endDate;
	}

	public ReleaseNotes(ReleaseNotesInDatabase dbEntry) {
		this(dbEntry.getId(), dbEntry.getTitle(), dbEntry.getProjectKey(), dbEntry.getContent(), dbEntry.getStartDate(),
				dbEntry.getEndDate());
	}

	private ReleaseNotes(long id, String title, String projectKey, String content, String startDate,
			String endDate) {
		this.id = id;
		this.title = title;
		this.projectKey = projectKey;
		this.content = content;
		this.startDate = startDate;
		this.endDate = endDate;
	}

	/**
	 * @return id of the release notes.
	 */
	@XmlElement(name = "id")
	public long getId() {
		return this.id;
	}

	/**
	 * @param id
	 *            of the release notes.
	 */
	@JsonProperty("id")
	public void setId(long id) {
		this.id = id;
	}

	/**
	 * @return title of the release notes.
	 */
	@XmlElement(name = "title")
	public String getTitle() {
		return this.title;
	}

	/**
	 * @param title
	 *            of the release notes.
	 */
	@JsonProperty("title")
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * @return startDate of the release notes.
	 */
	@XmlElement(name = "startDate")
	public String getStartDate() {
		return this.startDate;
	}

	/**
	 * @param startDate
	 *            of the release notes.
	 */
	@JsonProperty("startDate")
	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	/**
	 * @return end date of the release notes.
	 */
	@XmlElement(name = "endDate")
	public String getEndDate() {
		return this.endDate;
	}

	/**
	 * @param endDate
	 *            of the release notes.
	 */
	@JsonProperty("endDate")
	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	/**
	 * @return project key that the release notes belongs to.
	 * @see DecisionKnowledgeProject
	 */
	@XmlElement(name = "projectKey")
	public String getProjectKey() {
		return this.projectKey;
	}

	/**
	 * @param projectKey
	 *            key of Jira project that the release notes belongs to.
	 * @see DecisionKnowledgeProject
	 */
	@JsonProperty("projectKey")
	public void setProjectKey(String projectKey) {
		this.projectKey = projectKey;
	}

	/**
	 * @return content of the release notes. The content is in HTML, txt or md.
	 */
	@XmlElement(name = "content")
	public String getContent() {
		return this.content;
	}

	/**
	 * @param content
	 *            description of the release note. The content is in HTML, txt or
	 *            md.
	 */
	@JsonProperty("content")
	public void setContent(String content) {
		this.content = content;
	}

}