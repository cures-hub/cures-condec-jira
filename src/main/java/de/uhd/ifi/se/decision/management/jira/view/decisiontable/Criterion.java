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

	public long getId() {
		return id;
	}

	public String getSummary() {
		return summary;
	}

	public String getDocumentationLocation() {
		return documentationLocation;
	}

	public String getUrl() {
		return this.url;
	}

	@Override
	public boolean equals(Object obj) {
		Criterion tmp = (Criterion) obj;
		if (obj == null) {
			return false;
		}
		return tmp.id == this.id;
	}

}
