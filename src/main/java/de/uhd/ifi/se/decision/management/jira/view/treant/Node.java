package de.uhd.ifi.se.decision.management.jira.view.treant;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.xml.bind.annotation.XmlElement;

import org.codehaus.jackson.annotate.JsonProperty;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.config.properties.APKeys;
import com.atlassian.jira.config.properties.ApplicationProperties;
import com.google.common.collect.ImmutableMap;

import de.uhd.ifi.se.decision.management.jira.ComponentGetter;
import de.uhd.ifi.se.decision.management.jira.extraction.model.Sentence;
import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeProject;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.model.Link;

/**
 * Model class for Treant node
 */
public class Node {
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
	private List<Node> children;

	@XmlElement
	private Map<String, Boolean> collapsed;

	public Node() {
		this.connectors = ImmutableMap.of("style", ImmutableMap.of("stroke", "#000000"));
		// this.connectors = new ConcurrentHashMap<String, Map<String, String>>();
		// Map<String, String> connectorStyle = new ConcurrentHashMap<String, String>();
		// this.connectorStyle.put("stroke", "#000000");
		// this.connectorStyle.put("stroke-width", "2");
		// this.connectorStyle.put("arrow-start", "block-wide-long");
		// this.connectors.put("style", connectorStyle);
	}

	public Node(DecisionKnowledgeElement decisionKnowledgeElement, boolean isCollapsed, boolean isHyperlinked) {
		this();
		this.nodeContent = ImmutableMap.of("title", decisionKnowledgeElement.getSummary(), "desc",
				decisionKnowledgeElement.getKey(), "documentationLocation",
				decisionKnowledgeElement.getDocumentationLocationAsString());
		this.htmlClass = decisionKnowledgeElement.getType().getSuperType().toString().toLowerCase(Locale.ENGLISH);
		this.htmlId = decisionKnowledgeElement.getId();
		DecisionKnowledgeProject project = decisionKnowledgeElement.getProject();
		this.link = new HashMap<String, String>();
		if (decisionKnowledgeElement.getDescription() != null
				&& !decisionKnowledgeElement.getDescription().equals("")) {
			this.link.put("title", decisionKnowledgeElement.getDescription());
		}
		if (project.isIssueStrategy() && isHyperlinked) {
			makeLinkToElement(decisionKnowledgeElement.getKey());
		}
		if (isCollapsed) {
			this.collapsed = ImmutableMap.of("collapsed", isCollapsed);
		}
		this.image = KnowledgeType.getIconUrl(decisionKnowledgeElement);
	}

	public static String getIcon(DecisionKnowledgeElement element) {
		return ComponentGetter.getUrlOfImageFolder() + element.getType().toString() + ".png";
	}

	public Node(DecisionKnowledgeElement decisionKnowledgeElement, Link link, boolean isCollapsed,
			boolean isHyperlinked) {
		this(decisionKnowledgeElement, isCollapsed, isHyperlinked);
		this.image = KnowledgeType.getIconUrl(decisionKnowledgeElement, link.getType());
		switch (link.getType()) {
		case "support":
			if (decisionKnowledgeElement.getId() == link.getSourceElement().getId()) {
				this.htmlClass = "pro";
			}
			break;
		case "attack":
			if (decisionKnowledgeElement.getId() == link.getSourceElement().getId()) {
				this.htmlClass = "contra";
			}
			break;
		default:
			break;
		}
		if (decisionKnowledgeElement instanceof Sentence && isHyperlinked) {
			makeLinkToElement(decisionKnowledgeElement.getKey().split(":")[0]);
		}
	}

	private void makeLinkToElement(String key) {
		ApplicationProperties applicationProperties = ComponentAccessor.getApplicationProperties();
		this.link.put("href", applicationProperties.getString(APKeys.JIRA_BASEURL) + "/browse/" + key);
		this.link.put("target", "_blank");
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

	public Map<String, Boolean> getCollapsed() {
		return collapsed;
	}

	public void setCollapsed(Map<String, Boolean> collapsed) {
		this.collapsed = collapsed;
	}
}
