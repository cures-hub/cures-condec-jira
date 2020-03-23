package de.uhd.ifi.se.decision.management.jira.view.treeviewer;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlElement;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.Issue;
import com.google.common.collect.ImmutableMap;

import de.uhd.ifi.se.decision.management.jira.ComponentGetter;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.model.Link;
import de.uhd.ifi.se.decision.management.jira.model.impl.KnowledgeElementImpl;
import de.uhd.ifi.se.decision.management.jira.model.text.PartOfJiraIssueText;

/**
 * Model class for Tree Viewer nodes
 */
public class Data {

    @XmlElement
    private String id;

    @XmlElement
    private String text;

    @XmlElement
    private List<Data> children;

    @XmlElement(name = "data")
    private KnowledgeElement element;

    @XmlElement
    private String icon;

    @XmlElement(name = "a_attr")
    private Map<String, String> a_attr;

    @XmlElement(name = "li_attr")
    private Map<String, String> li_attr;

    public Data() {
    }

    public Data(KnowledgeElement decisionKnowledgeElement) {
	this.id = "tv" + String.valueOf(decisionKnowledgeElement.getId());
	this.text = decisionKnowledgeElement.getSummary();
	this.icon = KnowledgeType.getIconUrl(decisionKnowledgeElement);
	this.element = decisionKnowledgeElement;
	if (decisionKnowledgeElement.getDescription() != null && !decisionKnowledgeElement.getDescription().isBlank()
		&& !decisionKnowledgeElement.getDescription().equals("undefined")) {
	    this.a_attr = ImmutableMap.of("title", decisionKnowledgeElement.getDescription());
	}
	this.li_attr = ImmutableMap.of("class", "issue");
	if (decisionKnowledgeElement instanceof PartOfJiraIssueText) {
	    this.li_attr = ImmutableMap.of("class", "sentence", "sid", "s" + decisionKnowledgeElement.getId());
	}
    }

    public Data(File file, List<String> issueKeys, String projectKey) {
	KnowledgeElement classElement = new KnowledgeElementImpl();
	classElement.setSummary(file.getName());
	classElement.setKey(issueKeys.get(0).split("-")[0] + "-" + file.getName());
	this.id = "tv" + String.valueOf(classElement.getId());
	this.text = classElement.getSummary();
	this.icon = ComponentGetter.getUrlOfImageFolder() + "class.png";
	this.element = classElement;
	String elements = "";
	for (String key : issueKeys) {
	    if (!key.endsWith("-")) {
		Issue jiraIssue = ComponentAccessor.getIssueManager().getIssueObject(key);
		if (jiraIssue != null) {
		    elements = elements + jiraIssue.getKey() + ";";
		}
	    }
	}
	classElement.setDescription(elements);
	this.element = classElement;
	if (classElement.getDescription() != null && !classElement.getDescription().isBlank()
		&& !classElement.getDescription().equals("undefined")) {
	    this.a_attr = ImmutableMap.of("title", classElement.getDescription());
	}
	this.li_attr = ImmutableMap.of("class", "issue");
    }

    public Data(KnowledgeElement decisionKnowledgeElement, Link link) {
	this(decisionKnowledgeElement);
	this.icon = KnowledgeType.getIconUrl(decisionKnowledgeElement, link.getType());
    }

    public String getId() {
	return id;
    }

    public void setId(String id) {
	this.id = id;
    }

    public String getText() {
	return text;
    }

    public List<Data> getChildren() {
	if (children == null) {
	    return new ArrayList<Data>();
	}
	return children;
    }

    public void setChildren(List<Data> children) {
	this.children = children;
    }

    public Object getElement() {
	return element;
    }

    public String getIcon() {
	return icon;
    }
}