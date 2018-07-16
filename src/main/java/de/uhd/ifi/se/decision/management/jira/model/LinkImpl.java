package de.uhd.ifi.se.decision.management.jira.model;

import org.codehaus.jackson.annotate.JsonProperty;

import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.link.IssueLink;

import de.uhd.ifi.se.decision.management.jira.persistence.LinkEntity;

/**
 * Model class for links between decision knowledge elements
 */
public class LinkImpl implements Link {

	private long linkId;
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

	@Override
	public long getLinkId() {
		return linkId;
	}

	@Override
	public void setLinkId(Long linkId) {
		this.linkId = linkId;
	}

	@Override
	public String getLinkType() {
		return linkType;
	}

	@Override
	public void setLinkType(String linkType) {
		this.linkType = linkType;
	}

	@Override
	public long getIdOfSourceElement() {
		return sourceElement.getId();
	}

	@Override
	@JsonProperty("ingoingId")
	public void setIdOfSourceElement(long id) {
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
	public long getIdOfDestinationElement() {
		return destinationElement.getId();
	}

	@Override
	@JsonProperty("outgoingId")
	public void setIdOfDestinationElement(long id) {
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