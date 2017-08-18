package com.atlassian.DecisionDocumentation.rest.treeviewer.model;

import java.util.HashSet;
import java.util.Map;

import javax.xml.bind.annotation.XmlElement;

/**
 * 
 * @author Ewald Rode
 * @description model class for treeviewer configuration
 */
public class Core {
	
	@XmlElement
	private boolean multiple;
	
	@XmlElement
	private boolean check_callback;
	
	@XmlElement
	private Map<String, Boolean> themes;
	
	@XmlElement
	private HashSet<Data> data;

	public Core(){}
	
	public boolean isMultiple() {
		return multiple;
	}

	public void setMultiple(boolean multiple) {
		this.multiple = multiple;
	}

	public boolean isCheck_callback() {
		return check_callback;
	}

	public void setCheck_callback(boolean check_callback) {
		this.check_callback = check_callback;
	}

	public Map<String, Boolean> getThemes() {
		return themes;
	}

	public void setThemes(Map<String, Boolean> themes) {
		this.themes = themes;
	}
	
	public HashSet<Data> getData() {
		return data;
	}

	public void setData(HashSet<Data> data) {
		this.data = data;
	}
}
