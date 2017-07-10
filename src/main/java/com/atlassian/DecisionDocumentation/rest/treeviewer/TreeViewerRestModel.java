package com.atlassian.DecisionDocumentation.rest.treeviewer;

import java.util.HashSet;
import java.util.List;

import javax.xml.bind.annotation.*;

import com.atlassian.jira.issue.Issue;

/**
 * 
 * @author Ewald Rode
 * @description
 */
@XmlRootElement(name = "message")
@XmlAccessorType(XmlAccessType.FIELD)
public class TreeViewerRestModel {

	@XmlElement(name = "core")
	public TreeStructure treeStructure;

	@XmlElement
	public HashSet<String> plugins;

	@XmlElement
	public Search search;

	public TreeViewerRestModel() {
		this.treeStructure = null;
		this.plugins = new HashSet<String>();
		this.search = null;
	}

	public TreeViewerRestModel(List<Issue> issueList) {
		this.treeStructure = new TreeStructure(issueList);
		this.plugins = new HashSet<String>();
		this.plugins.add("wholerow");
		this.plugins.add("sort");
		this.plugins.add("search");
		this.search = new Search();
	}
}