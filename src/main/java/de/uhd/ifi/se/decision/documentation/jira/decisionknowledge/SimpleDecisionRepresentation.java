package de.uhd.ifi.se.decision.documentation.jira.decisionknowledge;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.atlassian.jira.issue.Issue;

/**
 * @author Ewald Rode
 * @description Model class for decision components
 */
@XmlType(propOrder = { "id", "text" })
public class SimpleDecisionRepresentation {
	@XmlElement
	private Long id;

	@XmlElement
	private String text;

	public SimpleDecisionRepresentation() {
	}

	public SimpleDecisionRepresentation(Issue issue) {
		this.id = issue.getId();
		this.text = issue.getKey() + " / " + issue.getSummary() + " / " + issue.getIssueType().getName();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}
}