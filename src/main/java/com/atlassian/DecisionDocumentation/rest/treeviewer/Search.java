package com.atlassian.DecisionDocumentation.rest.treeviewer;

import java.util.HashSet;

import javax.xml.bind.annotation.XmlElement;

/**
 * 
 * @author Ewald Rode
 * @description
 */
public class Search {
	@XmlElement
    public boolean show_only_matches;
	
	public Search(){
		this.show_only_matches = true;
	}
}
