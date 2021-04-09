package de.uhd.ifi.se.decision.management.jira.model;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;

/**
 * Models a solution option for a decision problem (i.e. an alternative, a
 * decision, a solution, or a claim). Enables to easily retrieve the
 * {@link Argument}s for the solution option
 * ({@link SolutionOption#getArguments()}).
 * 
 * @see KnowledgeType
 */
public class SolutionOption extends KnowledgeElement {

	public SolutionOption() {
		super();
	}

	/**
	 * @issue How to create an object of a superclass from an object of its
	 *        subclass?
	 */
	public SolutionOption(KnowledgeElement alternative) {
		this();
		this.project = alternative.getProject();
		this.id = alternative.getId();
		this.type = alternative.getType();
		this.setSummary(alternative.getSummary());
		this.documentationLocation = alternative.getDocumentationLocation();
	}

	/**
	 * @return linked arguments (pros, cons). Assumes that this knowledge element is
	 *         a solution option (=alternative/decision/solution/claim).
	 */
	@XmlElement
	public List<Argument> getArguments() {
		List<Argument> arguments = new ArrayList<>();
		for (KnowledgeElement element : getLinkedElements(1)) {
			if (element.getType() == KnowledgeType.PRO || element.getType() == KnowledgeType.CON
					|| element.getType() == KnowledgeType.ARGUMENT) {
				arguments.add(new Argument(element));
			}
		}
		return arguments;
	}

	@XmlElement
	public String getImage() {
		return KnowledgeType.getIconUrl(this);
	}
}
