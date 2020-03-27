package de.uhd.ifi.se.decision.management.jira.view.treant;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.xml.bind.annotation.XmlElement;

import com.google.common.collect.ImmutableMap;
import de.uhd.ifi.se.decision.management.jira.ComponentGetter;
import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeProject;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.model.Link;
import org.codehaus.jackson.annotate.JsonProperty;
   
/**
 * Model class for Treant node
 */
public class TreantNode {
	@XmlElement(name = "text")
	@JsonProperty("text")
	private Map<String, String> nodeContent;

	@XmlElement
	private Map<String, Map<String, String>> connectors;

	@XmlElement
	private Map<String, String> link;

	@XmlElement
	private String image;

	@XmlElement(name = "HTMLclass")
	@JsonProperty("HTMLclass")
	private String htmlClass;

	@XmlElement(name = "HTMLid")
	@JsonProperty("HTMLid")
	private long htmlId;

	@XmlElement(name = "innerHTML")
	@JsonProperty("innerHTML")
	private String innerHTML;

	@XmlElement
	private List<TreantNode> children;

	@XmlElement
	private Map<String, Boolean> collapsed;

	public TreantNode() {
		this.connectors = ImmutableMap.of("style", ImmutableMap.of("stroke", "#000000"));
	}

	public TreantNode(KnowledgeElement decisionKnowledgeElement, boolean isCollapsed, boolean isHyperlinked) {
		this();
		if (decisionKnowledgeElement == null || decisionKnowledgeElement.getSummary() == null) {
			return;
		}
		this.nodeContent = ImmutableMap.of("title", decisionKnowledgeElement.getSummary(), "documentationLocation",
				decisionKnowledgeElement.getDocumentationLocationAsString(), "status",
				decisionKnowledgeElement.getStatusAsString(), "desc", decisionKnowledgeElement.getKey());
		this.htmlClass = decisionKnowledgeElement.getType().getSuperType().toString().toLowerCase(Locale.ENGLISH);
		this.htmlId = decisionKnowledgeElement.getId();
		DecisionKnowledgeProject project = decisionKnowledgeElement.getProject();
		this.link = new HashMap<String, String>();
		if (decisionKnowledgeElement.getDescription() != null && !decisionKnowledgeElement.getDescription().isBlank()) {
			this.link.put("title", decisionKnowledgeElement.getDescription());
		}
		if (project.isIssueStrategy() && isHyperlinked) {
			this.link.put("href", decisionKnowledgeElement.getUrl());
			this.link.put("target", "_blank");
		}
		if (isCollapsed) {
			this.collapsed = ImmutableMap.of("collapsed", true);
		}
		this.image = KnowledgeType.getIconUrl(decisionKnowledgeElement);
	}

	public static String getIcon(KnowledgeElement element) {
		return ComponentGetter.getUrlOfImageFolder() + element.getType().toString() + ".png";
	}

	public TreantNode(KnowledgeElement decisionKnowledgeElement, Link link, boolean isCollapsed,
					  boolean isHyperlinked) {
		this(decisionKnowledgeElement, isCollapsed, isHyperlinked);
		this.image = KnowledgeType.getIconUrl(decisionKnowledgeElement, link.getType());
		switch (link.getType()) {
			case "support":
				if (decisionKnowledgeElement.getId() == link.getSource().getId()) {
					this.htmlClass = "pro";
				}
				break;
			case "attack":
				if (decisionKnowledgeElement.getId() == link.getSource().getId()) {
					this.htmlClass = "con";
				}
				break;
			default:
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

	public List<TreantNode> getChildren() {
		return children;
	}

	public void setChildren(List<TreantNode> children) {
		this.children = children;
	}

	public Map<String, Map<String, String>> getConnectors() {
		return connectors;
	}

	public void setConnectors(Map<String, Map<String, String>> connectors) {
		this.connectors = connectors;
	}

	public Map<String, Boolean> getCollapsed() {
		return collapsed;
	}

	public void setCollapsed(Map<String, Boolean> collapsed) {
		this.collapsed = collapsed;
	}
}
