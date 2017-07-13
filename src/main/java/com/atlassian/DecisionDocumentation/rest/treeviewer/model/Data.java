package com.atlassian.DecisionDocumentation.rest.treeviewer.model;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;

/**
 * @author Ewald Rode
 * @description
 */
public class Data {
	@XmlElement
	private String text;
	
	@XmlElement
    private List<Data> children;
	
	@XmlElement(name="data")
	private NodeInfo nodeInfo;

	public Data(){}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public List<Data> getChildren() {
		return children;
	}

	public void setChildren(List<Data> children) {
		this.children = children;
	}

	public NodeInfo getNodeInfo() {
		return nodeInfo;
	}

	public void setNodeInfo(NodeInfo nodeInfo) {
		this.nodeInfo = nodeInfo;
	}
}