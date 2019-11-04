package de.uhd.ifi.se.decision.management.jira.view.vis;

import com.atlassian.jira.user.ApplicationUser;

import javax.xml.bind.annotation.XmlElement;

public class VisTimeLineGroup {

	@XmlElement
	private long id;

	@XmlElement
	private String content;

	public VisTimeLineGroup(ApplicationUser user) {
		this.content = user.getName();
		this.id = user.getId();
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
