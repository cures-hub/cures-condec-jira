package de.uhd.ifi.se.decision.management.jira.view.decisiontable;

import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.model.Link;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

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
	private Criterion criterion;

	@XmlElement
	private String image;

	public Argument(KnowledgeElement argument) {
		this.id = argument.getId();
		this.summary = argument.getSummary();
		this.documentationLocation = argument.getDocumentationLocationAsString();
		this.type = argument.getTypeAsString();
		this.image = KnowledgeType.getIconUrl(argument);
	}

	public Argument(KnowledgeElement argument, Link link) {
		this(argument);
		this.image = KnowledgeType.getIconUrl(argument, link.getTypeAsString());
	}

	public long getId() {
		return id;
	}

	public String getSummary() {
		return summary;
	}

	public Criterion getCriterion() {
		return this.criterion;
	}

	public void setCriterion(KnowledgeElement criterion) {
		this.criterion = new Criterion(criterion);
	}

	public String getDocumentationLocation() {
		return documentationLocation;
	}

	public String getType() {
		return type;
	}

	public String getImage() {
		return this.image;
	}
	
}
