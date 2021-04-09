package de.uhd.ifi.se.decision.management.jira.model;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;

/**
 * Models a solution option for a decision problem (alternative, decision,
 * solution, or claim), {@link KnowledgeType}. Enables to easily retrieve the
 * {@link Argument}s for the solution option
 * ({@link Alternative#getArguments()}).
 */
public class Alternative extends KnowledgeElement {

	private List<Argument> arguments = new ArrayList<>();

	/**
	 * @issue How to create an object of a superclass from an object of its
	 *        subclass?
	 */
	public Alternative(KnowledgeElement alternative) {
		this.project = alternative.getProject();
		this.id = alternative.getId();
		this.type = alternative.getType();
		this.setSummary(alternative.getSummary());
		this.documentationLocation = alternative.getDocumentationLocation();
	}

	@XmlElement
	public List<Argument> getArguments() {
		return arguments;
	}

	public void addArgument(Argument argument) {
		arguments.add(argument);
	}

	@XmlElement
	public String getImage() {
		return KnowledgeType.getIconUrl(this);
	}
}
