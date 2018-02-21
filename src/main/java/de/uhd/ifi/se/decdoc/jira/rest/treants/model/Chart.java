package de.uhd.ifi.se.decdoc.jira.rest.treants.model;

import java.util.Map;

import javax.xml.bind.annotation.XmlElement;

import com.google.common.collect.ImmutableMap;
/**
 * @author Ewald Rode
 * @description model class for treant configuration
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
	
	public Chart(){
		this.container="#treant-container";
		this.connectors = ImmutableMap.of("type", "straight");
		this.rootOrientation = "NORTH";
		this.levelSeparation = 30;
		this.siblingSeparation = 30;
		this.subTreeSeparation = 30;
	}
}
