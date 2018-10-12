package de.uhd.ifi.se.decision.management.jira.view.treant;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.xml.bind.annotation.XmlElement;

import de.uhd.ifi.se.decision.management.jira.model.*;
import org.codehaus.jackson.annotate.JsonProperty;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.config.properties.APKeys;
import com.atlassian.jira.config.properties.ApplicationProperties;
import com.google.common.collect.ImmutableMap;

import de.uhd.ifi.se.decision.management.jira.extraction.model.Sentence;

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

	public Node(DecisionKnowledgeElement decisionKnowledgeElement, boolean isCollapsed) {
		this();
		KnowledgeType type = decisionKnowledgeElement.getType();
		if (type == KnowledgeType.OTHER) {
			this.nodeContent = ImmutableMap.of("title", decisionKnowledgeElement.getSummary(), "desc",
					decisionKnowledgeElement.getKey());
		} else {
			this.nodeContent = ImmutableMap.of("name", type.toString(), "title", decisionKnowledgeElement.getSummary(),
					"desc", decisionKnowledgeElement.getKey());
		}
		this.htmlClass = decisionKnowledgeElement.getType().getSuperType().toString().toLowerCase(Locale.ENGLISH);
		this.htmlId = decisionKnowledgeElement.getId();
		DecisionKnowledgeProject project = decisionKnowledgeElement.getProject();
		this.link = new HashMap<>();
		if (decisionKnowledgeElement.getDescription() != null
				&& !decisionKnowledgeElement.getDescription().equals("")) {
			this.link.put("title", decisionKnowledgeElement.getDescription());
		}
		if (project.isIssueStrategy()) {
			makeLinkToElement(decisionKnowledgeElement);
		}
		if (isCollapsed) {
			this.collapsed = ImmutableMap.of("collapsed", isCollapsed);
		}
		if (decisionKnowledgeElement instanceof Sentence) {
			if (((Sentence) decisionKnowledgeElement).getArgument().length() == 3) { // Length == 3 means pro or con
				if (((Sentence) decisionKnowledgeElement).getArgument().equalsIgnoreCase("pro")) {
					makeArgument("Pro-argument", "pro", decisionKnowledgeElement);
				} else {
					makeArgument("Con-argument", "contra", decisionKnowledgeElement);
				}
			}
			DecisionKnowledgeElement cutSentence = decisionKnowledgeElement;
			cutSentence.setKey(decisionKnowledgeElement.getKey().split(":")[0]);
			makeLinkToElement(decisionKnowledgeElement);
		}
	}

	public Node(DecisionKnowledgeElement decisionKnowledgeElement, Link link, boolean isCollapsed) {
		this(decisionKnowledgeElement, isCollapsed);
		switch (link.getType()) {
		case "support":
			if (decisionKnowledgeElement.getId() == link.getSourceElement().getId()) {
				makeArgument("Pro-argument", "pro", decisionKnowledgeElement);
			}
			break;
		case "attack":
			if (decisionKnowledgeElement.getId() == link.getSourceElement().getId()) {
				makeArgument("Con-argument", "contra", decisionKnowledgeElement);
			}
			break;
		default:
			break;
		}
	}

	private void makeArgument(String string, String string2, DecisionKnowledgeElement decisionKnowledgeElement) {
		this.nodeContent = ImmutableMap.of("name", string, "title", decisionKnowledgeElement.getSummary(), "desc",
				decisionKnowledgeElement.getKey());
		this.htmlClass = string2;
	}

	private void makeLinkToElement(DecisionKnowledgeElement element) {
		ApplicationProperties applicationProperties = ComponentAccessor.getApplicationProperties();
		this.link.put("href", applicationProperties.getString(APKeys.JIRA_BASEURL) + "/plugins/servlet/decisions-page?" +
				"projectKey=" + element.getProject().getProjectKey()
				+"&issueKey=" + element.getId());
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
