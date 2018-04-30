package de.uhd.ifi.se.decision.documentation.jira.view.treants;

import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlElement;

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
