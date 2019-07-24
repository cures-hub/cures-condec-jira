package de.uhd.ifi.se.decision.management.jira.view.vis;

import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeElement;

import javax.xml.bind.annotation.XmlElement;
import java.util.Date;

public class VisTimeLineNode {

	@XmlElement
	private int id;

	@XmlElement
	private String content;

	@XmlElement
	private String start;

	@XmlElement
	private String end;

	public VisTimeLineNode(DecisionKnowledgeElement element) {
		if(element == null){
			return;
		}
		this.id = ((int)element.getId());
		this.content = element.getKey();

		this.start = createDateString(element.getCreated());
		this.end = createDateString(element.getClosed());
	}

	private String createDateString(Date created){
		int year = created.getYear() + 1900;
		int month =created.getMonth();
		int day = created.getDay();
		return year+ "-" + month + "-" + day;
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

	public String getEnd() {
		return end;
	}

	public void setEnd(String end) {
		this.end = end;
	}
}
