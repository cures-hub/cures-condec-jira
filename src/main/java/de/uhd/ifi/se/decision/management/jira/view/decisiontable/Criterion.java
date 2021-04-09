package de.uhd.ifi.se.decision.management.jira.view.decisiontable;

import javax.xml.bind.annotation.XmlElement;

import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;

public class Criterion extends KnowledgeElement {

	private String url;

	public Criterion(KnowledgeElement criterion) {
		this.project = criterion.getProject();
		this.id = criterion.getId();
		this.url = criterion.getUrl();
		this.setSummary(criterion.getSummary());
		this.documentationLocation = criterion.getDocumentationLocation();
	}

	@XmlElement
	public String getUrl() {
		return this.url;
	}
}
