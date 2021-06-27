package de.uhd.ifi.se.decision.management.jira.view.treeviewer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlElement;

import com.google.common.collect.ImmutableMap;

import de.uhd.ifi.se.decision.management.jira.filtering.FilterSettings;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.model.Link;
import de.uhd.ifi.se.decision.management.jira.model.PartOfJiraIssueText;
import de.uhd.ifi.se.decision.management.jira.view.ToolTip;

/**
 * Model class for Tree Viewer nodes
 */
public class TreeViewerNode {

	@XmlElement
	private String id;

	@XmlElement
	private String text;

	@XmlElement
	private List<TreeViewerNode> children;

	@XmlElement(name = "data")
	private KnowledgeElement element;

	@XmlElement
	private String icon;

	@XmlElement(name = "a_attr")
	private Map<String, String> a_attr;

	@XmlElement(name = "li_attr")
	private Map<String, String> li_attr;

	public TreeViewerNode() {
		children = new ArrayList<>();
	}

	public TreeViewerNode(KnowledgeElement knowledgeElement, FilterSettings filterSettings) {
		this();
		id = "tv" + knowledgeElement.getId();
		text = knowledgeElement.getSummary();
		icon = KnowledgeType.getIconUrl(knowledgeElement);
		element = knowledgeElement;
		if (element.getDescription() != null) {
			a_attr = ImmutableMap.of("title", knowledgeElement.getDescription());
		}
		a_attr = ImmutableMap.of("title",
				knowledgeElement.getDescription() != null ? knowledgeElement.getDescription() : "");
		li_attr = ImmutableMap.of("class", "issue");
		if (knowledgeElement instanceof PartOfJiraIssueText) {
			li_attr = ImmutableMap.of("class", "sentence", "sid", "s" + knowledgeElement.getId());
		}
		if (filterSettings.areQualityProblemHighlighted()) {
			List<String> qualityProblems = knowledgeElement.getQualityProblems();
			if (!qualityProblems.isEmpty()) {
				a_attr = ImmutableMap.of("title", ToolTip.buildToolTip(qualityProblems), "style", "color:crimson");
			}
		}
	}

	public TreeViewerNode(KnowledgeElement knowledgeElement, Link link, FilterSettings filterSettings) {
		this(knowledgeElement, filterSettings);
		icon = KnowledgeType.getIconUrl(knowledgeElement, link.getTypeAsString());
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

	public List<TreeViewerNode> getChildren() {
		return children;
	}

	public void setChildren(List<TreeViewerNode> children) {
		this.children = children;
	}

	public KnowledgeElement getElement() {
		return element;
	}

	public String getIcon() {
		return icon;
	}

	public Map<String, String> getAttr() {
		return a_attr;
	}

	public void setAttr(Map<String, String> a_attr) {
		this.a_attr = a_attr;
	}

	public Map<String, String> getLiAttr() {
		return li_attr;
	}

	public void setLiAttr(Map<String, String> li_attr) {
		this.li_attr = li_attr;
	}
}