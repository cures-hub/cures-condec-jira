package com.atlassian.DecisionDocumentation.rest.treeviewer.model;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;

import javax.xml.bind.annotation.*;

import com.atlassian.DecisionDocumentation.db.strategy.Strategy;
import com.atlassian.jira.project.Project;
import com.google.common.collect.ImmutableMap;

/**
 * @author Ewald Rode
 * @description
 */

@XmlRootElement(name = "message")
@XmlAccessorType(XmlAccessType.FIELD)
public class TreeViewerRepresentation {

	@XmlElement
	private Core core;

	@XmlElement
	private HashSet<String> plugins;

	@XmlElement
	private Map<String, Boolean> search;

	public TreeViewerRepresentation() {}

	public TreeViewerRepresentation(Strategy strategy, Project project) {
		this.core = strategy.createCore(project);
		this.plugins = new HashSet<String>(Arrays.asList("wholerow", "sort", "search", ""));
		this.search = ImmutableMap.of("show_only_matches", true);
	}
	
	public Core getCore() {
		return core;
	}

	public void setCore(Core core) {
		this.core = core;
	}

	public HashSet<String> getPlugins() {
		return plugins;
	}

	public void setPlugins(HashSet<String> plugins) {
		this.plugins = plugins;
	}

	public Map<String, Boolean> getSearch() {
		return search;
	}

	public void setSearch(Map<String, Boolean> search) {
		this.search = search;
	}
}