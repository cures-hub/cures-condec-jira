package de.uhd.ifi.se.decision.management.jira.view.vis;

import de.uhd.ifi.se.decision.management.jira.model.Link;

import javax.xml.bind.annotation.XmlElement;

/**
 * Model class for vis.js Edge
 */

public class VisEdge {

	@XmlElement
	private String from;

	@XmlElement
	private String to;

	@XmlElement
	private String label;

	@XmlElement
	private String arrows;

	@XmlElement
	private String id;

	public VisEdge(Link link) {
		this.setLabel(link.getType());
		this.setFrom(link.getSourceElement().getId()+ "_" + link.getSourceElement().getDocumentationLocationAsString());
		this.setTo(link.getDestinationElement().getId()+ "_" + link.getDestinationElement().getDocumentationLocationAsString());
		this.setId(String.valueOf(link.getId()));
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

	public String getArrows() {
		return arrows;
	}

	public void setArrows(String arrows) {
		this.arrows = arrows;
	}

	public String getId() {return id;}

	public void setId(String id) {this.id = id;}
}
