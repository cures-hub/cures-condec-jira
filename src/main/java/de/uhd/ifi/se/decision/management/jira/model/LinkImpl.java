package de.uhd.ifi.se.decision.management.jira.model;

import org.codehaus.jackson.annotate.JsonProperty;

import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.link.IssueLink;

import de.uhd.ifi.se.decision.management.jira.persistence.LinkEntity;

/**
 * Model class for links between decision knowledge elements
 */
public class LinkImpl implements Link {

	private Long linkId;
	private String linkType;
	private DecisionKnowledgeElement sourceElement;
	private DecisionKnowledgeElement destinationElement;

	public LinkImpl() {
		this.sourceElement = new DecisionKnowledgeElementImpl();
		this.destinationElement = new DecisionKnowledgeElementImpl();
	}

	public LinkImpl(DecisionKnowledgeElement ingoingElement, DecisionKnowledgeElement outgoingElement) {
		this.sourceElement = ingoingElement;
		this.destinationElement = outgoingElement;
	}

	public LinkImpl(IssueLink link) {
		this.linkId = link.getId();
		this.linkType = link.getIssueLinkType().getName();
		Issue sourceIssue = link.getSourceObject();
		if (sourceIssue != null) {
			this.sourceElement = new DecisionKnowledgeElementImpl(sourceIssue);
		}
		Issue destinationIssue = link.getDestinationObject();
		if (destinationIssue != null) {
			this.destinationElement = new DecisionKnowledgeElementImpl(destinationIssue);
		}
	}

	public LinkImpl(LinkEntity link) {
		this();
		this.linkType = link.getLinkType();
	}

	public Long getLinkId() {
		return linkId;
	}

	public void setLinkId(Long linkId) {
		this.linkId = linkId;
	}

	public String getLinkType() {
		return linkType;
	}

	public void setLinkType(String linkType) {
		this.linkType = linkType;
	}

	public long getIngoingId() {
		return sourceElement.getId();
	}

	@JsonProperty("ingoingId")
	public void setIngoingId(long ingoingId) {
		if (this.sourceElement == null) {
			this.sourceElement = new DecisionKnowledgeElementImpl();
		}
		this.sourceElement.setId(ingoingId);
	}

	public DecisionKnowledgeElement getSourceObject() {
		return sourceElement;
	}

	public void setSourceObject(DecisionKnowledgeElement ingoingElement) {
		this.sourceElement = ingoingElement;
	}

	public long getOutgoingId() {
		return destinationElement.getId();
	}

	@JsonProperty("outgoingId")
	public void setOutgoingId(long outgoingId) {
		if (this.destinationElement == null) {
			this.destinationElement = new DecisionKnowledgeElementImpl();
		}
		this.destinationElement.setId(outgoingId);
	}

	public DecisionKnowledgeElement getDestinationObject() {
		return destinationElement;
	}

	public void setDestinationObject(DecisionKnowledgeElement outgoingElement) {
		this.destinationElement = outgoingElement;
	}
}