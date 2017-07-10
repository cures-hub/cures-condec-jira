package com.atlassian.DecisionDocumentation.rest.treeviewer;

import javax.xml.bind.annotation.XmlElement;

/**
 * 
 * @author Ewald Rode
 * @description
 */
public class Themes {
	
	@XmlElement
    public boolean icons;
	
	public Themes(){
		/* Es sollen neben den Knoten keine Icons angezeigt werden*/
		this.icons = false;
	}
}
