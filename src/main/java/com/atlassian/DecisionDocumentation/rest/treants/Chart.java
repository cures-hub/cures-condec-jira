package com.atlassian.DecisionDocumentation.rest.treants;

import javax.xml.bind.annotation.XmlElement;
/**
 * 
 * @author Ewald Rode
 * @description
 */
public class Chart {
	/*id des div-Elements, indem der Baum dargestellt werden soll*/
	@XmlElement
	private String container;
	/*Die Art der Verbindung zwischen zwei Knoten*/
	@XmlElement
	private Connectors connectors;
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
		this.connectors = new Connectors();
		this.rootOrientation = "NORTH";
		this.levelSeparation = 30;
		this.siblingSeparation = 30;
		this.subTreeSeparation = 30;
	}
}
