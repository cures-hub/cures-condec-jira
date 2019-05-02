package de.uhd.ifi.se.decision.management.jira.view.vis;


import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeElement;
import org.json.JSONPropertyName;

import javax.xml.bind.annotation.XmlElement;

/**
* Model class for vis.js Node
*/

public class VisNode {
	@XmlElement
	private String id;

	@XmlElement
	private String label;

	@XmlElement
	private String title;

	@XmlElement
	private String group;

	public VisNode(DecisionKnowledgeElement element, boolean collapsed){
		this.setId(element.getId()+ "_" + element.getDocumentationLocationAsString());
		if (!collapsed) {
			this.setGroup(element.getTypeAsString().toLowerCase());
			this.setLabel("<b>" + element.getTypeAsString().toUpperCase() + "</b> \n" + element.getSummary());
			this.setTitle(element.getDescription());
		} else {
			this.setGroup("collapsed");
			this.setLabel("");
			this.setTitle(element.getTypeAsString() + ": " + element.getSummary());
		}
	}

	public VisNode(DecisionKnowledgeElement element, String type, boolean collapsed) {
		this(element,collapsed);
		if(!collapsed) {
			this.setGroup(type.toLowerCase());
		}
		/*this.setId(element.getId()+ "_" + element.getDocumentationLocationAsString());
		if (!collapsed) {
			this.setGroup(type.toLowerCase());
			this.setLabel("<b>" + element.getTypeAsString().toUpperCase() + "</b> \n" + element.getSummary());
			this.setTitle(element.getDescription());
		} else {
			this.setGroup("collapsed");
			this.setLabel("");
			this.setTitle(element.getSummary());
		}*/
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getGroup() {
		return group;
	}

	public void setGroup(String group) {
		this.group = group;
	}
}
