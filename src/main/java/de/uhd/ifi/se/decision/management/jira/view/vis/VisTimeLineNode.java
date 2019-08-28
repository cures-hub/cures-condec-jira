package de.uhd.ifi.se.decision.management.jira.view.vis;

import java.util.Calendar;
import java.util.Date;

import javax.xml.bind.annotation.XmlElement;

import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeStatus;
import de.uhd.ifi.se.decision.management.jira.persistence.DecisionStatusManager;

public class VisTimeLineNode {

	@XmlElement
	private int id;

	@XmlElement
	private String content;

	@XmlElement
	private String start;

	@XmlElement
	private String className;

	@XmlElement
	private String title;

	@XmlElement
	private long group;

	@XmlElement
	private String documentationLocation;

	private String end;

	public VisTimeLineNode(DecisionKnowledgeElement element) {
		if (element == null) {
			return;
		}
		this.id = ((int) element.getId());
		this.content = createContentString(element);

		this.start = createDateString(element.getCreated());
		this.end = createDateString(element.getClosed());
		this.className = element.getTypeAsString().toLowerCase();
		this.title = element.getDescription();
		this.documentationLocation = element.getDocumentationLocation().getIdentifier();
	}

	private String createDateString(Date created) {
		if (created == null) {
			return "";
		}
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(created);
		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH);
		int day = calendar.get(Calendar.DAY_OF_MONTH);
		return year + "-" + month + "-" + day;
	}

	private String createContentString(DecisionKnowledgeElement element) {
		String contentString = "<img src=" +'"' + element.getType().getIconUrl()+ '"' + "> ";
		KnowledgeStatus elementStatus= DecisionStatusManager.getStatusForElement(element);
		if(elementStatus.equals(KnowledgeStatus.DISCARDED) || elementStatus.equals(KnowledgeStatus.REJECTED) ||
		elementStatus.equals(KnowledgeStatus.UNRESOLVED)){
			return contentString + "<p style=\"color:red\">" + element.getSummary() + "</p>";

		}
		return contentString + element.getSummary();
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

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String getEnd() {
		return end;
	}

	public void setEnd(String end) {
		this.end = end;
	}

	public long getGroup() {
		return group;
	}

	public void setGroup(long group) {
		this.group = group;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
}
