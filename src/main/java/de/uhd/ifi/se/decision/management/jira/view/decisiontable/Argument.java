package de.uhd.ifi.se.decision.management.jira.view.decisiontable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;

@XmlRootElement(name = "Argument")
@XmlAccessorType(XmlAccessType.FIELD)
public class Argument extends DecisionTableElement {

	@XmlElement
	private long id;
	
	@XmlElement
	private String summary;
	
	@XmlElement
	private String documentationLocation;
	
	private String type;
	
	@XmlElement
	private Criteria criteria;
	
	public Argument(KnowledgeElement argument) {
		this.id = argument.getId();
		this.summary = argument.getSummary();
		this.documentationLocation = argument.getDocumentationLocationAsString();
		this.type = argument.getTypeAsString();
	}
	
	@XmlElement(name = "id")
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	@XmlElement(name = "summary")
	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public void setCriteria(Criteria criteria) {
		this.criteria = criteria;
	}

	@XmlElement(name = "criteria")
	public Criteria getCriteria() {
		return this.criteria;
	}

	public void setCriteria(KnowledgeElement criteria) {
		this.criteria = new Criteria(criteria);
	}
	
	@XmlElement(name = "documentationLocation")
	public String getDocumentationLocation() {
		return documentationLocation;
	}

	public void setDocumentationLocation(String documentationLocation) {
		this.documentationLocation = documentationLocation;
	}

	@XmlElement(name = "type")
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	
}
