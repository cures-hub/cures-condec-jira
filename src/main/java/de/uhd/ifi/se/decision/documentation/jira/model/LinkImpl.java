package de.uhd.ifi.se.decision.documentation.jira.model;

import org.codehaus.jackson.annotate.JsonProperty;

import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.link.IssueLink;

import de.uhd.ifi.se.decision.documentation.jira.persistence.LinkEntity;

/**
 * @description Model class for links between decision knowledge elements
 */
public class LinkImpl implements Link {
	private String linkType;
	private DecisionKnowledgeElement ingoingElement;
	private DecisionKnowledgeElement outgoingElement;

	public LinkImpl() {
		this.ingoingElement = new DecisionKnowledgeElementImpl();
		this.outgoingElement = new DecisionKnowledgeElementImpl();
	}

	public LinkImpl(DecisionKnowledgeElement ingoingElement, DecisionKnowledgeElement outgoingElement) {
		this.ingoingElement = ingoingElement;
		this.outgoingElement = outgoingElement;
	}

	public LinkImpl(IssueLink link) {
		this.linkType = link.getIssueLinkType().getName();
		Issue sourceIssue = link.getSourceObject();
		this.ingoingElement = new DecisionKnowledgeElementImpl(sourceIssue);
		Issue destinationIssue = link.getDestinationObject();
		this.outgoingElement = new DecisionKnowledgeElementImpl(destinationIssue);
	}

	public LinkImpl(LinkEntity link) {
		this();
		this.linkType = link.getLinkType();
	}

	public String getLinkType() {
		return linkType;
	}

	public void setLinkType(String linkType) {
		this.linkType = linkType;
	}


	public long getIngoingId() {
		return ingoingElement.getId();
	}

	@JsonProperty("ingoingId")
	public void setIngoingId(long ingoingId) {
		this.ingoingElement.setId(ingoingId);
	}

	public DecisionKnowledgeElement getIngoingElement() {
		return ingoingElement;
	}


	public long getOutgoingId() {
		return outgoingElement.getId();
	}

	@JsonProperty("outgoingId")
	public void setOutgoingId(long outgoingId) {
		this.ingoingElement.setId(outgoingId);
	}

	public DecisionKnowledgeElement getOutgoingElement() {
		return outgoingElement;
	}

	public void setIngoingElement(DecisionKnowledgeElement ingoingElement) {
		this.ingoingElement = ingoingElement;
	}

	public void setOutgoingElement(DecisionKnowledgeElement outgoingElement) {
		this.outgoingElement = outgoingElement;
	}
}