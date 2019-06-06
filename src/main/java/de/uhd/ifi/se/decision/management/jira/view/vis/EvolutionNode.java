package de.uhd.ifi.se.decision.management.jira.view.vis;

import javax.xml.bind.annotation.XmlElement;

public class EvolutionNode {

	@XmlElement
	private int id;

	@XmlElement
	private String content;

	@XmlElement
	private String start;

	public EvolutionNode(int id,String content, String startTime){
		this.id = id;
		this.content =content;
		this.start = startTime;
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
