package de.uhd.ifi.se.decision.management.jira.view.vis;

import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.annotation.XmlElement;

import org.codehaus.jackson.annotate.JsonIgnore;

import com.google.common.collect.ImmutableMap;

import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.view.ToolTip;

/**
 * Model class for a vis.js node.
 */
public class VisNode {

	@JsonIgnore
	private KnowledgeElement element;

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
	private Map<String, String> color;

	public VisNode(KnowledgeElement element) {
		this(element, false, 0, true);
	}

	public VisNode(KnowledgeElement element, boolean areQualityProblemsHightlighted) {
		this(element, false, 0, areQualityProblemsHightlighted);
	}

	public VisNode(KnowledgeElement element, int level) {
		this(element, false, level, true);
	}

	public VisNode(KnowledgeElement element, int level, boolean areQualityProblemsHightlighted) {
		this(element, false, level, areQualityProblemsHightlighted);
	}

	public VisNode(KnowledgeElement element, boolean isCollapsed, int level) {
		this(element, isCollapsed, level, true);
	}

	public VisNode(KnowledgeElement element, boolean isCollapsed, int level, boolean areQualityProblemsHightlighted) {
		this.element = element;
		this.level = level;
		this.label = determineLabel(element, isCollapsed);
		this.group = determineGroup(element, isCollapsed);
		this.title = ToolTip.buildToolTip(element, element.getTypeAsString().toUpperCase() + System.lineSeparator()
				+ element.getKey() + ": " + element.getSummary() + System.lineSeparator() + element.getDescription());
		this.font = determineFont(element, areQualityProblemsHightlighted);
		this.color = determineColor(element);
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

	private Map<String, String> determineFont(KnowledgeElement element, boolean areQualityProblemsHightlighted) {
		if (!areQualityProblemsHightlighted) {
			return ImmutableMap.of("color", "black");
		}
		String color = "black";
		if (!element.fulfillsDefinitionOfDone()) {
			color = "crimson";
		}
		return ImmutableMap.of("color", color);
	}

	public static Map<String, String> determineColor(KnowledgeElement element) {
		Map<String, String> color = new HashMap<>();
		color.put("background", element.getType().getColor());
		color.put("border", "black");
		return color;
	}

	@XmlElement(name = "id")
	public String getVisNodeId() {
		return element.getId() + "_" + element.getDocumentationLocationAsString();
	}

	@XmlElement(name = "elementId")
	public long getElementId() {
		return element.getId();
	}

	@XmlElement(name = "documentationLocation")
	public String getDocumentationLocation() {
		return element.getDocumentationLocationAsString();
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

	public Map<String, String> getFont() {
		return font;
	}

	public String getColor() {
		return color.get("background");
	}

	public Map<String, String> getColorMap() {
		return color;
	}

	public KnowledgeElement getElement() {
		return element;
	}

	public void setElement(KnowledgeElement element) {
		this.element = element;
	}
}
