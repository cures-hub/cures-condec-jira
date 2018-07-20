package de.uhd.ifi.se.decision.management.jira.view.treeviewer;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;

import de.uhd.ifi.se.decision.management.jira.ComponentGetter;
import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.Link;

/**
 * Model class for Tree Viewer nodes
 */
public class Data {

	@XmlElement
	private String id;

	@XmlElement
	private String text;

	@XmlElement
	private List<Data> children;

	@XmlElement(name = "data")
	private DecisionKnowledgeElement nodeInfo;

	@XmlElement
	private String icon;

	public Data() {
	}

	public Data(DecisionKnowledgeElement decisionKnowledgeElement) {
		this.id = String.valueOf(decisionKnowledgeElement.getId());
		this.text = decisionKnowledgeElement.getSummary();
		this.icon = ComponentGetter.getUrlOfImageFolder() + decisionKnowledgeElement.getType().toString() + ".png";
		this.nodeInfo = decisionKnowledgeElement;
	}

	public Data(DecisionKnowledgeElement decisionKnowledgeElement, Link link) {
		this(decisionKnowledgeElement);
		switch (link.getLinkType()) {
		case "support":
			if (decisionKnowledgeElement.getId() == link.getIdOfSourceElement()) {
				this.icon = ComponentGetter.getUrlOfImageFolder() + "argument_pro.png";
			}
			break;
		case "attack":
			if (decisionKnowledgeElement.getId() == link.getIdOfSourceElement()) {
				this.icon = ComponentGetter.getUrlOfImageFolder() + "argument_con.png";
			}
			break;
		default:
			break;
		}
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public List<Data> getChildren() {
		return children;
	}

	public void setChildren(List<Data> children) {
		this.children = children;
	}

	public DecisionKnowledgeElement getNode() {
		return nodeInfo;
	}

	public void setNodeInfo(DecisionKnowledgeElement nodeInfo) {
		this.nodeInfo = nodeInfo;
	}
}