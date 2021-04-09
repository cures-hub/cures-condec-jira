package de.uhd.ifi.se.decision.management.jira.view.decisiontable;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;

import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;

public class Alternative extends KnowledgeElement {

	private List<Argument> arguments = new ArrayList<>();

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
