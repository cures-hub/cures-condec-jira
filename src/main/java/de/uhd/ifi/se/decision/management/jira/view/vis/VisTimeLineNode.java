package de.uhd.ifi.se.decision.management.jira.view.vis;

import javax.xml.bind.annotation.XmlElement;

public class VisTimeLineNode {

	@XmlElement
	private int id;

	@XmlElement
	private String content;

	@XmlElement
	private String start;

	public VisTimeLineNode(int id, String content, String startTime) {
		this.id = id;
		this.content = content;
		this.start = startTime;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getStart() {
		return start;
	}

	public void setStart(String start) {
		this.start = start;
	}
}
