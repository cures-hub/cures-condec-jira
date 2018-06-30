package de.uhd.ifi.se.decision.documentation.jira.view.treant;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.xml.bind.annotation.XmlElement;

import com.google.common.collect.ImmutableMap;

import de.uhd.ifi.se.decision.documentation.jira.model.DecisionKnowledgeElement;
import de.uhd.ifi.se.decision.documentation.jira.model.Link;

/**
 * Model class for Treant node
 */
public class Node {
	@XmlElement(name = "text")
	private Map<String, String> nodeContent;

	@XmlElement
	private Map<String, Map<String, String>> connectors;

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
		this.connectors = ImmutableMap.of("style", ImmutableMap.of("stroke", "#000000"));
	}

	public Node(DecisionKnowledgeElement decisionKnowledgeElement) {
		this();
		this.nodeContent = ImmutableMap.of("name", decisionKnowledgeElement.getType().toString(),
				"title", decisionKnowledgeElement.getSummary(), "desc", decisionKnowledgeElement.getKey());
		this.htmlClass = decisionKnowledgeElement.getSuperType().toString().toLowerCase(Locale.ENGLISH);
		this.htmlId = decisionKnowledgeElement.getId();
	}

	public Node(DecisionKnowledgeElement decisionKnowledgeElement, Link link) {
		this(decisionKnowledgeElement);
		switch (link.getLinkType()) {
		case "support":
			this.nodeContent = ImmutableMap.of("name", "Supporting Argument",
					"title", decisionKnowledgeElement.getSummary(), "desc", decisionKnowledgeElement.getKey());
			this.htmlClass = "support";
			break;
		case "attack":
			this.nodeContent = ImmutableMap.of("name", "Attacking Argument",
					"title", decisionKnowledgeElement.getSummary(), "desc", decisionKnowledgeElement.getKey());
			this.htmlClass = "attack";
			break;
		}
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

	public Map<String, Map<String, String>> getConnectors() {
		return connectors;
	}

	public void setConnectors(Map<String, Map<String, String>> connectors) {
		this.connectors = connectors;
	}
}
