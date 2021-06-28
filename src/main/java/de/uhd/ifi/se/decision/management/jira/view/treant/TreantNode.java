package de.uhd.ifi.se.decision.management.jira.view.treant;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.xml.bind.annotation.XmlElement;

import org.codehaus.jackson.annotate.JsonProperty;

import com.google.common.collect.ImmutableMap;

import de.uhd.ifi.se.decision.management.jira.filtering.FilterSettings;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.model.Link;
import de.uhd.ifi.se.decision.management.jira.quality.completeness.DefinitionOfDoneChecker;
import de.uhd.ifi.se.decision.management.jira.view.ToolTip;

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
		this.children = new ArrayList<>();
	}

	public TreantNode(KnowledgeElement knowledgeElement, boolean isCollapsed, FilterSettings filterSettings,
			boolean isHyperlinked) {
		this();

		if (knowledgeElement == null || knowledgeElement.getSummary() == null) {
			return;
		}

		String title = knowledgeElement.getSummary();
		if (title.length() > 25 && !title.contains(" ")) {
			title = title.substring(0, 24) + "...";
		}

		nodeContent = ImmutableMap.of("title", title, "documentationLocation",
				knowledgeElement.getDocumentationLocationAsString(), "status", knowledgeElement.getStatusAsString(),
				"desc", knowledgeElement.getKey());
		htmlClass = knowledgeElement.getType().getSuperType().toString().toLowerCase(Locale.ENGLISH);
		link = new HashMap<>();
		if (knowledgeElement.getDescription() != null) {
			link.put("title", knowledgeElement.getDescription());
		}
		if (filterSettings.areQualityProblemHighlighted()) {
			List<String> qualityProblems = DefinitionOfDoneChecker.getQualityProblems(knowledgeElement, filterSettings);
			if (!qualityProblems.isEmpty()) {
				link.put("title", ToolTip.buildToolTip(qualityProblems));
				htmlClass += " dodViolation";
			}
		}
		htmlId = knowledgeElement.getId();
		if (isHyperlinked) {
			link.put("href", knowledgeElement.getUrl());
			link.put("target", "_blank");
		}
		if (isCollapsed) {
			collapsed = ImmutableMap.of("collapsed", true);
		}
		image = KnowledgeType.getIconUrl(knowledgeElement);
	}

	public TreantNode(KnowledgeElement knowledgeElement, Link link, boolean isCollapsed, FilterSettings filterSettings,
			boolean isHyperlinked) {
		this(knowledgeElement, isCollapsed, filterSettings, isHyperlinked);
		image = KnowledgeType.getIconUrl(knowledgeElement, link.getTypeAsString());
		switch (link.getTypeAsString()) {
		case "support":
			if (knowledgeElement.getId() == link.getSource().getId()) {
				htmlClass = htmlClass.replaceFirst("\\S+", "pro");
			}
			break;
		case "attack":
			if (knowledgeElement.getId() == link.getSource().getId()) {
				htmlClass = htmlClass.replaceFirst("\\S+", "con");
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
}
