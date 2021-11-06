package de.uhd.ifi.se.decision.management.jira.releasenotes;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeProject;
import de.uhd.ifi.se.decision.management.jira.persistence.tables.ReleaseNotesInDatabase;

/**
 * Models one release notes with its typical attributes (title, content, ...).
 * The content contains explicit decision knowledge (i.e. decision problems and
 * decisions relevant for the release).
 */
public class ReleaseNotes implements Comparable<ReleaseNotes> {

	private long id;
	private String title;
	private String content;
	private String projectKey;
	private String startDate;
	private String endDate;
	private List<ReleaseNotesEntry> improvements;
	private List<ReleaseNotesEntry> bugFixes;
	private List<ReleaseNotesEntry> newFeatures;

	public ReleaseNotes() {
		this("", "", "", "", "");
	}

	@JsonCreator
	public ReleaseNotes(@JsonProperty("title") String title, @JsonProperty("content") String content,
			@JsonProperty("projectKey") String projectKey, @JsonProperty("startDate") String startDate,
			@JsonProperty("endDate") String endDate) {
		this.title = title;
		this.content = content;
		this.projectKey = projectKey;
		this.startDate = startDate;
		this.endDate = endDate;
		setImprovements(new ArrayList<>());
		setBugFixes(new ArrayList<>());
		setNewFeatures(new ArrayList<>());
	}

	public ReleaseNotes(ReleaseNotesInDatabase databaseEntry) {
		this(databaseEntry.getTitle(), databaseEntry.getContent(), databaseEntry.getProjectKey(),
				databaseEntry.getStartDate(), databaseEntry.getEndDate());
		this.id = databaseEntry.getId();
	}

	/**
	 * @return id of the release notes.
	 */
	@XmlElement
	public long getId() {
		return id;
	}

	/**
	 * @param id
	 *            of the release notes.
	 */
	@JsonProperty
	public void setId(long id) {
		this.id = id;
	}

	/**
	 * @return title of the release notes.
	 */
	@XmlElement
	public String getTitle() {
		return title;
	}

	/**
	 * @param title
	 *            of the release notes.
	 */
	@JsonProperty
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * @return startDate of the release notes.
	 */
	@XmlElement
	public String getStartDate() {
		return startDate;
	}

	/**
	 * @param startDate
	 *            of the release notes.
	 */
	@JsonProperty
	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	/**
	 * @return end date of the release notes.
	 */
	@XmlElement
	public String getEndDate() {
		return endDate;
	}

	/**
	 * @param endDate
	 *            of the release notes.
	 */
	@JsonProperty
	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	/**
	 * @return key of the Jira project that the release notes belongs to.
	 * @see DecisionKnowledgeProject
	 */
	@XmlElement
	public String getProjectKey() {
		return projectKey;
	}

	/**
	 * @param projectKey
	 *            key of the Jira project that the release notes belongs to.
	 * @see DecisionKnowledgeProject
	 */
	@JsonProperty
	public void setProjectKey(String projectKey) {
		this.projectKey = projectKey;
	}

	/**
	 * @return content of the release notes. The content is in HTML, txt or md
	 *         format.
	 */
	@XmlElement
	public String getContent() {
		return content;
	}

	/**
	 * @param content
	 *            description of the release notes. The content is in HTML, txt or
	 *            md format.
	 */
	@JsonProperty
	public void setContent(String content) {
		this.content = content;
	}

	@XmlElement
	public List<ReleaseNotesEntry> getImprovements() {
		return improvements;
	}

	@JsonProperty
	public void setImprovements(List<ReleaseNotesEntry> improvements) {
		this.improvements = improvements;
	}

	@XmlElement
	public List<ReleaseNotesEntry> getBugFixes() {
		return bugFixes;
	}

	@JsonProperty
	public void setBugFixes(List<ReleaseNotesEntry> bugFixes) {
		this.bugFixes = bugFixes;
	}

	@XmlElement
	public List<ReleaseNotesEntry> getNewFeatures() {
		return newFeatures;
	}

	@JsonProperty
	public void setNewFeatures(List<ReleaseNotesEntry> newFeatures) {
		this.newFeatures = newFeatures;
	}

	@Override
	public int compareTo(ReleaseNotes otherReleaseNotes) {
		return otherReleaseNotes.getStartDate().compareTo(getStartDate());
	}
}