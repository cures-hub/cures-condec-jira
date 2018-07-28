package de.uhd.ifi.se.decision.management.jira.view.treant;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.xml.bind.annotation.XmlElement;

import com.google.common.collect.ImmutableMap;

/**
 * Model class for Treant configuration
 */
public class Chart {
	@XmlElement
	private String container;
	@XmlElement
	private Map connectors;
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
		this.connectors = new ConcurrentHashMap<>();
		this.connectors.put("type", "straight");
		Map style = new ConcurrentHashMap();
		style.put("arrow-end", "classic-wide-long");
		style.put("stroke-width", 2);
		this.connectors.put("style", style);
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