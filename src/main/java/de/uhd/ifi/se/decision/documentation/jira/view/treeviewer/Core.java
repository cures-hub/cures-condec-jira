package de.uhd.ifi.se.decision.documentation.jira.view.treeviewer;

import java.util.HashSet;
import java.util.Map;

import javax.xml.bind.annotation.XmlElement;

/**
 * @author Ewald Rode
 * @description model class for treeviewer configuration
 */
public class Core {
	
	@XmlElement
	private boolean multiple;
	
	@XmlElement(name = "check_callback")
	private boolean checkCallback;
	
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

	public boolean isCheckCallback() {
		return checkCallback;
	}

	public void setCheckCallback(boolean checkCallback) {
		this.checkCallback = checkCallback;
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
