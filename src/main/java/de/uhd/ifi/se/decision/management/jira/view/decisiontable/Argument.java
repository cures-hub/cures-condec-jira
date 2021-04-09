package de.uhd.ifi.se.decision.management.jira.view.decisiontable;

import javax.xml.bind.annotation.XmlElement;

import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.model.Link;

public class Argument extends KnowledgeElement {

	private KnowledgeElement criterion;
	private String image;

	/**
	 * @issue How to create an object of a superclass from an object of its
	 *        subclass?
	 */
	public Argument(KnowledgeElement argument) {
		this.project = argument.getProject();
		this.id = argument.getId();
		this.setSummary(argument.getSummary());
		this.documentationLocation = argument.getDocumentationLocation();
		this.type = argument.getType();
		this.image = KnowledgeType.getIconUrl(argument);
	}

	public Argument(KnowledgeElement argument, Link link) {
		this(argument);
		this.image = KnowledgeType.getIconUrl(argument, link.getTypeAsString());
	}

	@XmlElement
	public KnowledgeElement getCriterion() {
		return criterion;
	}

	public void setCriterion(KnowledgeElement criterion) {
		this.criterion = criterion;
	}

	@XmlElement
	public String getImage() {
		return image;
	}
}
