package de.uhd.ifi.se.decision.management.jira.view.vis;

import java.util.Map;

import javax.xml.bind.annotation.XmlElement;

import com.google.common.collect.ImmutableMap;

import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeStatus;

/**
 * Model class for a vis.js node.
 */
public class VisNode {

	@XmlElement
	private String id;

	@XmlElement
	private String label;

	@XmlElement
	private String title;

	@XmlElement
	private String group;

	@XmlElement
	private int level;

	@XmlElement
	private Map<String, String> font;

	@XmlElement
	private int cid;

	public VisNode(KnowledgeElement element, boolean isCollapsed, int level, int cid) {
		// TODO Add two attributes for id and docuLocu or even provide the whole
		// knowledge element
		this.id = element.getId() + "_" + element.getDocumentationLocationAsString();
		this.level = level;
		this.cid = cid;
		this.label = determineLabel(element, isCollapsed);
		this.group = determineGroup(element, isCollapsed);
		this.title = "<b>" + element.getTypeAsString().toUpperCase() + " <br> " + element.getKey() + ":</b> "
				+ element.getSummary() + "<br> <i>" + element.getDescription() + "</i>";
		this.font = determineFont(element);
	}

	private String determineLabel(KnowledgeElement element, boolean isCollapsed) {
		if (isCollapsed) {
			return "";
		}
		String summary = element.getSummary();
		if (summary.length() > 99) {
			summary = summary.substring(0, 99) + "...";
		}
		return element.getTypeAsString().toUpperCase() + "\n" + summary;
	}

	private String determineGroup(KnowledgeElement element, boolean isCollapsed) {
		if (isCollapsed) {
			return "collapsed";
		}
		return element.getTypeAsString().toLowerCase();
	}

	private Map<String, String> determineFont(KnowledgeElement element) {
		KnowledgeStatus status = element.getStatus();
		String color = status.getColor();
		if (!color.isBlank()) {
			return ImmutableMap.of("color", color);
		}
		return ImmutableMap.of("color", "black");
	}

	public String getId() {
		return id;
	}

	public String getLabel() {
		return label;
	}

	public String getTitle() {
		return title;
	}

	public String getGroup() {
		return group;
	}

	public int getLevel() {
		return level;
	}

	public int getCid() {
		return cid;
	}

	public Map<String, String> getFont() {
		return font;
	}
}
