package de.uhd.ifi.se.decision.management.jira.view.treant;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.xml.bind.annotation.XmlElement;

import de.uhd.ifi.se.decision.management.jira.filtering.FilterSettings;
import de.uhd.ifi.se.decision.management.jira.persistence.ConfigPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.quality.completeness.DefinitionOfDone;
import de.uhd.ifi.se.decision.management.jira.quality.completeness.DefinitionOfDoneChecker;
import org.codehaus.jackson.annotate.JsonProperty;

import com.google.common.collect.ImmutableMap;

import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.model.Link;

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

	public TreantNode(KnowledgeElement knowledgeElement, boolean isCollapsed, boolean isHyperlinked) {
		this();

		if (knowledgeElement == null || knowledgeElement.getSummary() == null) {
			return;
		}

		String title;
		if (knowledgeElement.getSummary().length() > 25 && !knowledgeElement.getSummary().contains(" ")) {
			title = knowledgeElement.getSummary().substring(0, 24) + "...";
		} else {
			title = knowledgeElement.getSummary();
		}
		this.nodeContent = ImmutableMap.of("title", title, "documentationLocation",
				knowledgeElement.getDocumentationLocationAsString(), "status", knowledgeElement.getStatusAsString(),
				"desc", knowledgeElement.getKey());
		this.htmlClass = knowledgeElement.getType().getSuperType().toString().toLowerCase(Locale.ENGLISH);
		this.htmlId = knowledgeElement.getId();
		this.link = new HashMap<>();
		this.link.put("title", buildToolTip(knowledgeElement));
		if (isHyperlinked) {
			this.link.put("href", knowledgeElement.getUrl());
			this.link.put("target", "_blank");
		}
		if (isCollapsed) {
			this.collapsed = ImmutableMap.of("collapsed", true);
		}
		this.image = KnowledgeType.getIconUrl(knowledgeElement);
	}

	public TreantNode(KnowledgeElement knowledgeElement, Link link, boolean isCollapsed, boolean isHyperlinked) {
		this(knowledgeElement, isCollapsed, isHyperlinked);
		this.image = KnowledgeType.getIconUrl(knowledgeElement, link.getTypeAsString());
		switch (link.getTypeAsString()) {
		case "support":
			if (knowledgeElement.getId() == link.getSource().getId()) {
				this.htmlClass = "pro";
			}
			break;
		case "attack":
			if (knowledgeElement.getId() == link.getSource().getId()) {
				this.htmlClass = "con";
			}
			break;
		default:
			break;
		}
	}

	private String buildToolTip(KnowledgeElement knowledgeElement) {
		FilterSettings filterSettings = new FilterSettings(knowledgeElement.getProject().getProjectKey(), "");
		DefinitionOfDone definitionOfDone = ConfigPersistenceManager.getDefinitionOfDone(knowledgeElement.getProject().getProjectKey());
		filterSettings.setLinkDistance(definitionOfDone.getMaximumLinkDistanceToDecisions());
		filterSettings.setMinimumDecisionCoverage(definitionOfDone.getMinimumDecisionsWithinLinkDistance());

		String text = "";
		List<String> failedDefinitionOfDoneCheckCriteriaCriteria =
			DefinitionOfDoneChecker.getFailedDefinitionOfDoneCheckCriteria(knowledgeElement, filterSettings);
		List<String> failedCompletenessCheckCriteria =
			DefinitionOfDoneChecker.getFailedCompletenessCheckCriteria(knowledgeElement);
		if (failedDefinitionOfDoneCheckCriteriaCriteria.contains("doesNotHaveMinimumCoverage")) {
			text = text.concat("Minimum decision coverage is not reached." + System.lineSeparator() + System.lineSeparator());
		}
		if (failedDefinitionOfDoneCheckCriteriaCriteria.contains("hasIncompleteKnowledgeLinked")) {
			text = text.concat("Linked decision knowledge is incomplete." + System.lineSeparator() + System.lineSeparator());
		}
		if (!failedCompletenessCheckCriteria.isEmpty()) {
			text = text.concat("Failed knowledge completeness criteria:" + System.lineSeparator());
			text = text.concat(String.join(System.lineSeparator(), failedCompletenessCheckCriteria));
		}
		if (text.isBlank() && knowledgeElement.getDescription() != null
			&& !knowledgeElement.getDescription().isBlank() && !knowledgeElement.getDescription().equals("undefined")) {
			text = knowledgeElement.getDescription();
		}
		text = text.strip();
		return text;
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
