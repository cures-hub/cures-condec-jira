package de.uhd.ifi.se.decision.management.jira.view.vis;

import javax.xml.bind.annotation.XmlElement;

/**
 * Represents a group (developer) in the chronology view.
 */
public class VisTimeLineGroup {

	@XmlElement
	private long id;

	@XmlElement
	private String content;

	public VisTimeLineGroup(String userName) {
		this.content = userName;
		this.id = userName.hashCode();
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
}
