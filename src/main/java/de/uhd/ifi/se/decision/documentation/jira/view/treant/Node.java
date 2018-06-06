package de.uhd.ifi.se.decision.documentation.jira.view.treant;

import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlElement;

import com.google.common.collect.ImmutableMap;

import de.uhd.ifi.se.decision.documentation.jira.model.DecisionKnowledgeElement;

/**
 * @description Model class for Treant node
 */
public class Node {
	@XmlElement(name = "text")
	private Map<String, String> nodeContent;

	@XmlElement
	private Map<String, String> link;

	@XmlElement(name = "HTMLclass")
	private String htmlClass;

	@XmlElement(name = "HTMLid")
	private long htmlId;

	@XmlElement(name = "innerHTML")
	private String innerHTML;

	@XmlElement
	private List<Node> children;

	public Node() {
	}

	public Node(DecisionKnowledgeElement decisionKnowledgeElement) {
		Map<String, String> nodeContent = ImmutableMap.of("name", decisionKnowledgeElement.getType().toString(),
				"title", decisionKnowledgeElement.getSummary(), "desc", decisionKnowledgeElement.getKey());
		this.setNodeContent(nodeContent);
		this.setHtmlClass(decisionKnowledgeElement.getSuperType().toString().toLowerCase());
		this.setHtmlId(decisionKnowledgeElement.getId());

		//this.setInnerHTML("draggable='true'");
	}

	public Map<String, String> getNodeContent() {
		return nodeContent;
	}

	public void setNodeContent(Map<String, String> nodeContent) {
		this.nodeContent = nodeContent;
	}

	public Map<String, String> getLink() {
		return link;
	}

	public void setLink(Map<String, String> link) {
		this.link = link;
	}

	public String getHtmlClass() {
		return htmlClass;
	}

	public void setHtmlClass(String htmlClass) {
		this.htmlClass = htmlClass;
	}

	public long getHtmlId() {
		return htmlId;
	}

	public void setHtmlId(long htmlId) {
		this.htmlId = htmlId;
	}

	public String getInnerHTML() {
		return innerHTML;
	}

	public void setInnerHTML(String innerHTML) {
		this.innerHTML = innerHTML;
	}

	public List<Node> getChildren() {
		return children;
	}

	public void setChildren(List<Node> children) {
		this.children = children;
	}
}
