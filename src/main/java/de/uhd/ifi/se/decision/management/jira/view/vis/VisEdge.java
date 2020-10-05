package de.uhd.ifi.se.decision.management.jira.view.vis;

import java.util.HashMap;
import java.util.Map;

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
	private long id;

	@XmlElement
	private Map<String, String> color;

	public VisEdge(Link link) {
		this.setLabel(link.getTypeAsString());
		this.setFrom(link.getSource().getId() + "_" + link.getSource().getDocumentationLocationAsString());
		this.setTo(link.getTarget().getId() + "_" + link.getTarget().getDocumentationLocationAsString());
		this.setId(link.getId());
		this.setColor(LinkType.getLinkTypeColor(link.getTypeAsString()));
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

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getColor() {
		return color.get("color");
	}

	public void setColor(String color) {
		this.color = new HashMap<>();
		this.color.put("color", color);
		this.color.put("inherit", "false");
	}
}
