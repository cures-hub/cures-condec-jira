package de.uhd.ifi.se.decision.management.jira.view.vis;

import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.annotation.XmlElement;

import org.codehaus.jackson.annotate.JsonIgnore;

import com.google.common.collect.ImmutableMap;

import de.uhd.ifi.se.decision.management.jira.filtering.FilterSettings;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.quality.completeness.DefinitionOfDoneChecker;

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

	public VisNode(KnowledgeElement element, FilterSettings filterSettings) {
		this(element, 0, filterSettings);
	}

	public VisNode(KnowledgeElement element, int level) {
		this(element, level, new FilterSettings());
	}

	public VisNode(KnowledgeElement element, int level, FilterSettings filterSettings) {
		this.element = element;
		this.level = level;
		label = determineLabel(element);
		group = determineGroup(element);
		title = element.getTypeAsString().toUpperCase() + System.lineSeparator() + element.getKey() + ": "
				+ element.getSummary() + System.lineSeparator() + element.getDescription();
		font = ImmutableMap.of("color", "black");
		if (filterSettings.areQualityProblemHighlighted()) {
			String problemExplanation = DefinitionOfDoneChecker.getQualityProblemExplanation(element, filterSettings);
			if (!problemExplanation.isEmpty()) {
				title = problemExplanation;
				font = ImmutableMap.of("color", "crimson");
			}
		}
		color = determineColor(element);
	}

	private String determineLabel(KnowledgeElement element) {
		String summary = element.getSummary();
		if (summary.length() > 99) {
			summary = summary.substring(0, 99) + "...";
		}
		return element.getTypeAsString().toUpperCase() + "\n" + summary;
	}

	private String determineGroup(KnowledgeElement element) {
		return element.getTypeAsString().toLowerCase();
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

	public void setCollapsed() {
		group = "collapsed";
		label = "";
	}

	public void setGroup(String group) {
		this.group = group;
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
