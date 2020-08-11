package de.uhd.ifi.se.decision.management.jira.view.decisiontable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;

@XmlRootElement(name = "Criteria")
@XmlAccessorType(XmlAccessType.FIELD)
public class Criterion extends DecisionTableElement {
	
	@XmlElement
	private long id;
	
	@XmlElement
	private String url;
	
	@XmlElement
	private String summary;
	
	@XmlElement
	private String documentationLocation;
	
	public Criterion(KnowledgeElement criteria) {
		this.id = criteria.getId();
		this.url = criteria.getUrl();
		this.summary = criteria.getSummary();
		this.documentationLocation = criteria.getDocumentationLocationAsString();
	}
	
	@XmlElement(name = "id")
	public long getId() {
		return id;
	}
	
	@XmlElement(name = "summary")
	public String getSummary() {
		return summary;
	}
	
	@XmlElement(name = "documentationLocation")
	public String getDocumentationLocation() {
		return documentationLocation;
	}

	@Override
	public int hashCode() {
		return super.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		Criterion tmp = (Criterion)obj;
		if (obj == null) {
			return false;
		}
		return tmp.id == this.id;
	}
	
	
}
