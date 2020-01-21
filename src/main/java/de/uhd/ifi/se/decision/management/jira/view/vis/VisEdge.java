package de.uhd.ifi.se.decision.management.jira.view.vis;

import javax.xml.bind.annotation.XmlElement;

import de.uhd.ifi.se.decision.management.jira.model.Link;
import de.uhd.ifi.se.decision.management.jira.model.LinkType;

/**
 * Model class for vis.js Edge.
 */
public class VisEdge {

	@XmlElement
	private String from;

	@XmlElement
	private String to;

	@XmlElement
	private String label;

	@XmlElement
	private String id;

	@XmlElement
	private String color;

	public VisEdge(Link link) {
		this.setLabel(link.getType());
		this.setFrom(link.getSource().getId() + "_" + link.getSource().getDocumentationLocationAsString());
		this.setTo(link.getTarget().getId() + "_" + link.getTarget().getDocumentationLocationAsString());
		this.setId(String.valueOf(link.getId()));
		this.setColor(LinkType.getLinkTypeColor(link.getType()));
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}
}
