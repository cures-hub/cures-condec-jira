package de.uhd.ifi.se.decision.documentation.jira.view.treeviewer;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;

import de.uhd.ifi.se.decision.documentation.jira.model.IDecisionKnowledgeElement;

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

	@XmlElement(name = "data")
	private IDecisionKnowledgeElement nodeInfo;

	public Data() {
	}

	public Data(IDecisionKnowledgeElement decisionKnowledgeElement) {
		this(decisionKnowledgeElement, true);
	}

	public Data(IDecisionKnowledgeElement decisionKnowledgeElement, boolean addChildren) {
		this.setText(decisionKnowledgeElement.getType() + " / " + decisionKnowledgeElement.getSummary());
		this.setId(String.valueOf(decisionKnowledgeElement.getId()));
		this.setNodeInfo(decisionKnowledgeElement);

		if (addChildren == true) {
			List<IDecisionKnowledgeElement> children = decisionKnowledgeElement.getChildren();

			List<Data> childrenToData = new ArrayList<Data>();
			for (IDecisionKnowledgeElement child : children) {
				childrenToData.add(new Data(child, true));
			}
			this.setChildren(childrenToData);
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

	public IDecisionKnowledgeElement getNode() {
		return nodeInfo;
	}

	public void setNodeInfo(IDecisionKnowledgeElement nodeInfo) {
		this.nodeInfo = nodeInfo;
	}
}