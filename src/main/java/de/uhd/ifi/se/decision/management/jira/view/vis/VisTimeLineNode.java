package de.uhd.ifi.se.decision.management.jira.view.vis;

import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeElement;

import javax.xml.bind.annotation.XmlElement;

public class VisTimeLineNode {

	@XmlElement
	private int id;

	@XmlElement
	private String content;

	@XmlElement
	private String start;

	public VisTimeLineNode(DecisionKnowledgeElement element) {
		this.id = ((int)element.getId());
		this.content = element.getKey();
		int year = element.getCreated().getYear();
		int month = element.getCreated().getMonth();
		int day = element.getCreated().getDay();
		this.start = year+ "-" + month + "-" + day;
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
