package de.uhd.ifi.se.decision.management.jira.view.vis;

import java.util.Calendar;
import java.util.Date;

import javax.xml.bind.annotation.XmlElement;

import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeElement;

public class VisTimeLineNode {

	@XmlElement
	private int id;

	@XmlElement
	private String content;

	@XmlElement
	private String start;

	private String end;

	public VisTimeLineNode(DecisionKnowledgeElement element) {
		if (element == null) {
			return;
		}
		this.id = ((int) element.getId());
		this.content = element.getKey();

		this.start = createDateString(element.getCreated());
		this.end = createDateString(element.getClosed());
	}

	private String createDateString(Date created) {
		if (created == null) {
			return "";
		}
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(created);
		int year = calendar.get(Calendar.YEAR) + 1900;
		int month = calendar.get(Calendar.MONTH);
		int day = calendar.get(Calendar.DAY_OF_MONTH);
		return year + "-" + month + "-" + day;
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
