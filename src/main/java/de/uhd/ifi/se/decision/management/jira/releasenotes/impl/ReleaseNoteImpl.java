package de.uhd.ifi.se.decision.management.jira.releasenotes.impl;


import de.uhd.ifi.se.decision.management.jira.persistence.tables.ReleaseNotesInDatabase;
import de.uhd.ifi.se.decision.management.jira.releasenotes.ReleaseNote;
import de.uhd.ifi.se.decision.management.jira.releasenotes.TargetGroup;
import de.uhd.ifi.se.decision.management.jira.releasenotes.TaskCriteriaPrioritisation;
import org.codehaus.jackson.annotate.JsonProperty;

import javax.xml.bind.annotation.XmlElement;

/**
 * Model class for Release Notes
 */
public class ReleaseNoteImpl implements ReleaseNote {

	private long id;
	private String title;
	private String content;
	private String projectKey;

	// This default constructor is necessary for the JSON string to object mapping.
	// Do not delete it!
	public ReleaseNoteImpl() {
	}
	public ReleaseNoteImpl(String title, String content,String projectKey){
		this.title=title;
		this.content=content;
		this.projectKey=projectKey;
	}
	public ReleaseNoteImpl(ReleaseNotesInDatabase dbEntry) {
		this(dbEntry.getId(), dbEntry.getTitle(), dbEntry.getProjectKey(), dbEntry.getContent());
	}

	private ReleaseNoteImpl(long id, String title, String projectKey, String content) {
		this.id = id;
		this.title = title;
		this.projectKey = projectKey;
		this.content = content;
	}


	@Override
	@XmlElement(name = "id")
	public long getId() {
		return this.id;
	}

	@Override
	@JsonProperty("id")
	public void setId(long id) {
		this.id = id;
	}

	@Override
	@XmlElement(name = "title")
	public String getTitle() {
		return this.title;
	}

	@Override
	@JsonProperty("title")
	public void setTitle(String title) {
		this.title = title;
	}

	@Override
	@XmlElement(name = "projectKey")
	public String getProjectKey() {
		return this.projectKey;
	}

	@Override
	@JsonProperty("projectKey")
	public void setProjectKey(String projectKey) {
		this.projectKey = projectKey;
	}

	@Override
	@XmlElement(name = "content")
	public String getContent() {
		return this.content;
	}

	@Override
	@JsonProperty("content")
	public void setContent(String content) {
		this.content = content;
	}

}