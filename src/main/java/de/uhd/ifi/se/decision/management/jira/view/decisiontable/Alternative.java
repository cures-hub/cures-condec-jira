package de.uhd.ifi.se.decision.management.jira.view.decisiontable;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;

@XmlRootElement(name = "Alternative")
@XmlAccessorType(XmlAccessType.FIELD)
public class Alternative extends DecisionTableElement {

	@XmlElement
	private long id;

	@XmlElement
	private String summary;
	
	@XmlElement
	private String documentationLocation;
	
	@XmlElement
	private List<Argument> arguments = new ArrayList<>();

	@XmlElement
	private String image;
	
	public Alternative(KnowledgeElement alternative) {
		this.id = alternative.getId();
		this.summary = alternative.getSummary();
		this.documentationLocation = alternative.getDocumentationLocationAsString();
		this.image = KnowledgeType.getIconUrl(alternative);
	}

	public List<Argument> getArguments() {
		return arguments;
	}

	public void addArgument(Argument argument) {
		this.arguments.add(argument);
	}

	public long getId() {
		return this.id;
	}

	public String getSummary() {
		return this.summary;
	}

	public String getDocumentationLocation() {
		return this.documentationLocation;
	}
	
	public String getImage() {
		return this.image;
	}
}
