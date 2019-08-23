package de.uhd.ifi.se.decision.management.jira.releasenotes.impl;


import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeProject;
import de.uhd.ifi.se.decision.management.jira.model.impl.DecisionKnowledgeProjectImpl;
import de.uhd.ifi.se.decision.management.jira.releasenotes.ReleaseNote;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * Model class for Release Notes
 */
public class ReleaseNoteImpl implements ReleaseNote {

	private long id;
	private String title;
	private String content;
	private DecisionKnowledgeProject project;


	@Override
	public long getId() {
		return this.id;
	}

	@Override
	public void setId(long id) {
		this.id = id;
	}

	@Override
	public String getTitle() {
		return this.title;
	}

	@Override
	public void setTitle(String title) {
		this.title = title;
	}

	@Override
	public DecisionKnowledgeProject getProject() {
		return this.project;
	}

	@Override
	public void setProject(DecisionKnowledgeProject project) {
		this.project = project;
	}

	@Override
	@JsonProperty("projectKey")
	public void setProject(String projectKey) {
		this.project = new DecisionKnowledgeProjectImpl(projectKey);
	}

	@Override
	public String getContent() {
		return this.content;
	}

	@Override
	public void setContent(String content) {
		this.content = content;
	}

}