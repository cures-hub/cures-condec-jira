package com.atlassian.DecisionDocumentation.rest.treants;

import javax.xml.bind.annotation.XmlElement;
/**
 * 
 * @author Ewald Rode
 * @description Die Art der Verbindung zwischen zwei Knoten
 */
public class Connectors {
	@XmlElement
	private String type;
	
	public Connectors(){
		this.type = "straight";
	}
}
