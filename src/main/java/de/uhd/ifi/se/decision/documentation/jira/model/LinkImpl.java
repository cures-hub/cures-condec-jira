package de.uhd.ifi.se.decision.documentation.jira.model;

import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.link.IssueLink;

import de.uhd.ifi.se.decision.documentation.jira.persistence.LinkEntity;

/**
 * @description Model class for links between decision knowledge elements
 */
public class LinkImpl implements Link {
	private String linkType;
	private long ingoingId;
	private DecisionKnowledgeElement ingoingElement;
	private long outgoingId;
	private DecisionKnowledgeElement outgoingElement;

	public LinkImpl() {
	}

	public LinkImpl(DecisionKnowledgeElement ingoingElement, DecisionKnowledgeElement outgoingElement) {
		this.ingoingId = ingoingElement.getId();
		this.ingoingElement = ingoingElement;
		this.outgoingId = outgoingElement.getId();
		this.outgoingElement = outgoingElement;
	}

	public LinkImpl(IssueLink link) {
		this.linkType = link.getIssueLinkType().getName();
		Issue sourceIssue = link.getSourceObject();
		this.ingoingId = sourceIssue.getId();
		this.ingoingElement = new DecisionKnowledgeElementImpl(sourceIssue);
		Issue destinationIssue = link.getDestinationObject();
		this.outgoingId = destinationIssue.getId();
		this.outgoingElement = new DecisionKnowledgeElementImpl(destinationIssue);
	}

	public LinkImpl(LinkEntity link) {
		this.linkType = link.getLinkType();
		this.ingoingId = link.getIngoingId();
		this.outgoingId = link.getOutgoingId();
	}

	public String getLinkType() {
		return linkType;
	}

	public void setLinkType(String linkType) {
		this.linkType = linkType;
	}

	public long getIngoingId() {
		return ingoingId;
	}

	public DecisionKnowledgeElement getIngoingElement() {
		return ingoingElement;
	}

	public void setIngoingId(long ingoingId) {
		this.ingoingId = ingoingId;
	}

	public long getOutgoingId() {
		return outgoingId;
	}

	public DecisionKnowledgeElement getOutgoingElement() {
		return outgoingElement;
	}

	public void setOutgoingId(long outgoingId) {
		this.outgoingId = outgoingId;
	}

	public void setIngoingElement(DecisionKnowledgeElement ingoingElement) {
		this.ingoingElement = ingoingElement;
	}

	public void setOutgoingElement(DecisionKnowledgeElement outgoingElement) {
		this.outgoingElement = outgoingElement;
	}
}