package de.uhd.ifi.se.decision.management.jira.view.vis;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import javax.xml.bind.annotation.XmlElement;

import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeStatus;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;

public class VisTimeLineNode {

	@XmlElement
	private int id;

	@XmlElement
	private String content;

	@XmlElement
	private String start;

	@XmlElement
	private String end;

	@XmlElement
	private String className;

	@XmlElement
	private String title;

	@XmlElement
	private long group;

	@XmlElement
	private String documentationLocation;

	private static final DateFormat DATEFORMAT = new SimpleDateFormat("yyyy-MM-dd");

	public VisTimeLineNode(KnowledgeElement element, boolean isPlacedAtCreationDate, boolean isPlacedAtUpdatingDate) {
		if (element == null) {
			return;
		}
		this.id = ((int) element.getId());
		this.content = createContentString(element);
		if (isPlacedAtCreationDate) {
			this.start = DATEFORMAT.format(element.getCreationDate());
		} else {
			this.start = DATEFORMAT.format(element.getUpdatingDate());
		}
		if (isPlacedAtCreationDate && isPlacedAtUpdatingDate) {
			this.end = DATEFORMAT.format(element.getUpdatingDate());
		}
		this.className = element.getTypeAsString().toLowerCase();
		this.title = element.getDescription();
		this.documentationLocation = element.getDocumentationLocation().getIdentifier();
	}

	public VisTimeLineNode(KnowledgeElement element, long group, boolean isPlacedAtCreationDate,
			boolean isPlacedAtUpdatingDate) {
		this(element, isPlacedAtCreationDate, isPlacedAtUpdatingDate);
		this.group = group;
	}

	private String createContentString(KnowledgeElement element) {
		String contentString = "<img src=" + '"' + KnowledgeType.getIconUrl(element) + '"' + "> ";
		KnowledgeStatus elementStatus = element.getStatus();
		if (elementStatus == KnowledgeStatus.DISCARDED || elementStatus == KnowledgeStatus.REJECTED
				|| elementStatus == KnowledgeStatus.UNRESOLVED) {
			return contentString + "<p style=\"color:red; display:inline\">" + element.getSummary() + "</p>";
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
