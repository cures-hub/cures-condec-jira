package de.uhd.ifi.se.decision.management.jira.model;

import org.codehaus.jackson.annotate.JsonProperty;

import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.link.IssueLink;

import de.uhd.ifi.se.decision.management.jira.extraction.persistence.LinkBetweenDifferentEntitiesEntity;
import de.uhd.ifi.se.decision.management.jira.persistence.LinkEntity;

/**
 * Model class for links between decision knowledge elements
 */
public class LinkImpl implements Link {

	private long id;
	private String type;
	private DecisionKnowledgeElement sourceElement;
	private DecisionKnowledgeElement destinationElement;

	public LinkImpl() {
		this.sourceElement = new DecisionKnowledgeElementImpl();
		this.destinationElement = new DecisionKnowledgeElementImpl();
	}

	public LinkImpl(DecisionKnowledgeElement sourceElement, DecisionKnowledgeElement destinationElement) {
		this.sourceElement = sourceElement;
		this.destinationElement = destinationElement;
	}

	public LinkImpl(long idOfSourceElement, long idOfDestinationElement) {
		this.setSourceElement(idOfSourceElement);
		this.setDestinationElement(idOfDestinationElement);
	}

	public LinkImpl(IssueLink issueLink) {
		this.id = issueLink.getId();
		this.type = issueLink.getIssueLinkType().getName();
		Issue sourceIssue = issueLink.getSourceObject();
		if (sourceIssue != null) {
			this.sourceElement = new DecisionKnowledgeElementImpl(sourceIssue);
		}
		Issue destinationIssue = issueLink.getDestinationObject();
		if (destinationIssue != null) {
			this.destinationElement = new DecisionKnowledgeElementImpl(destinationIssue);
		}
	}

	public LinkImpl(LinkBetweenDifferentEntitiesEntity link) {
		this();
		this.id = link.getId();
		this.type = link.getType();
	}
	
	@Override
	public long getId() {
		return id;
	}

	@Override
	public void setId(long id) {
		this.id = id;
	}

	@Override
	public String getType() {
		return type;
	}

	@Override
	public void setType(String type) {
		this.type = type;
	}

	@Override
	@JsonProperty("idOfSourceElement")
	public void setSourceElement(long id) {
		if (this.sourceElement == null) {
			this.sourceElement = new DecisionKnowledgeElementImpl();
		}
		this.sourceElement.setId(id);
	}

	@Override
	public DecisionKnowledgeElement getSourceElement() {
		return sourceElement;
	}

	@Override
	public void setSourceElement(DecisionKnowledgeElement sourceElement) {
		this.sourceElement = sourceElement;
	}

	@Override
	@JsonProperty("idOfDestinationElement")
	public void setDestinationElement(long id) {
		if (this.destinationElement == null) {
			this.destinationElement = new DecisionKnowledgeElementImpl();
		}
		this.destinationElement.setId(id);
	}

	@Override
	public DecisionKnowledgeElement getDestinationElement() {
		return destinationElement;
	}

	@Override
	public void setDestinationElement(DecisionKnowledgeElement destinationElement) {
		this.destinationElement = destinationElement;
	}
}