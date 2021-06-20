package de.uhd.ifi.se.decision.management.jira.view.treeviewer;

import com.google.common.collect.ImmutableMap;
import de.uhd.ifi.se.decision.management.jira.filtering.FilterSettings;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.model.Link;
import de.uhd.ifi.se.decision.management.jira.model.PartOfJiraIssueText;
import de.uhd.ifi.se.decision.management.jira.persistence.ConfigPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.quality.completeness.DefinitionOfDone;
import de.uhd.ifi.se.decision.management.jira.quality.completeness.DefinitionOfDoneChecker;

import javax.xml.bind.annotation.XmlElement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

	public TreeViewerNode(KnowledgeElement knowledgeElement, boolean noColors) {
		this();

		FilterSettings filterSettings = new FilterSettings(knowledgeElement.getProject().getProjectKey(), "");
		DefinitionOfDone definitionOfDone = ConfigPersistenceManager.getDefinitionOfDone(knowledgeElement.getProject().getProjectKey());
		filterSettings.setLinkDistance(definitionOfDone.getMaximumLinkDistanceToDecisions());
		filterSettings.setMinimumDecisionCoverage(definitionOfDone.getMinimumDecisionsWithinLinkDistance());

		this.id = "tv" + knowledgeElement.getId();
		this.text = knowledgeElement.getSummary();
		this.icon = KnowledgeType.getIconUrl(knowledgeElement);
		this.element = knowledgeElement;
		this.a_attr = ImmutableMap.of("title", buildToolTip(knowledgeElement, filterSettings));
		this.li_attr = ImmutableMap.of("class", "issue");
		if (knowledgeElement instanceof PartOfJiraIssueText) {
			this.li_attr = ImmutableMap.of("class", "sentence", "sid", "s" + knowledgeElement.getId());
		}
		if (!noColors) {
			String textColor = "";
			if (!DefinitionOfDoneChecker.checkDefinitionOfDone(knowledgeElement, filterSettings)) {
				textColor = "crimson";
			}
			if (!textColor.isBlank()) {
				if (a_attr == null) {
					a_attr = ImmutableMap.of("style", "color:" + textColor);
				} else {
					a_attr = new ImmutableMap.Builder<String, String>().putAll(a_attr).put("style", "color:" + textColor)
						.build();
				}
			}
		}
	}

	public TreeViewerNode(KnowledgeElement knowledgeElement, Link link, boolean colorNodes) {
		this(knowledgeElement, colorNodes);
		this.icon = KnowledgeType.getIconUrl(knowledgeElement, link.getTypeAsString());
	}

	private String buildToolTip(KnowledgeElement knowledgeElement, FilterSettings filterSettings) {
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