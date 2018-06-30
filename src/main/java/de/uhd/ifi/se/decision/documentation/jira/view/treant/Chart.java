package de.uhd.ifi.se.decision.documentation.jira.view.treant;

import java.util.Map;

import javax.xml.bind.annotation.XmlElement;

import com.google.common.collect.ImmutableMap;

/**
 * Model class for Treant configuration
 */
public class Chart {
	@XmlElement
	private String container;
	@XmlElement
	private Map<String, String> connectors;
	@XmlElement
	private String rootOrientation;
	@XmlElement
	private int levelSeparation;
	@XmlElement
	private int siblingSeparation;
	@XmlElement
	private int subTreeSeparation;

	@XmlElement
	private Map<String, Boolean> node;

	public Chart() {
		this.container = "#treant-container";
		this.connectors = ImmutableMap.of("type", "straight");
		this.rootOrientation = "NORTH";
		this.levelSeparation = 30;
		this.siblingSeparation = 30;
		this.subTreeSeparation = 30;
		this.node = ImmutableMap.of("collapsable", true);
	}

	public String getContainer() {
		return this.container;
	}

	public Map<String, String> getConnectors() {
		return connectors;
	}

	public String getRootOrientation() {
		return rootOrientation;
	}

	public int getLevelSeparation() {
		return levelSeparation;
	}

	public int getSiblingSeparation() {
		return siblingSeparation;
	}

	public int getSubTreeSeparation() {
		return subTreeSeparation;
	}

	public Map<String, Boolean> getNode() {
		return node;
	}
}