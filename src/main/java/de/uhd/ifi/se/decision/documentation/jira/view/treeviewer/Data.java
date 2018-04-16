package de.uhd.ifi.se.decision.documentation.jira.view.treeviewer;

import de.uhd.ifi.se.decision.documentation.jira.decisionknowledge.DecisionKnowledgeElement;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;

/**
 * @description Model class for Tree Viewer nodes
 */
public class Data {


	@XmlElement
	private String id;

	@XmlElement
	private String text;
	
	@XmlElement
    private List<Data> children;
	
	@XmlElement(name="data")
	private DecisionKnowledgeElement nodeInfo;

	public Data(){}

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