package de.uhd.ifi.se.decision.documentation.jira.model;

import com.atlassian.jira.issue.link.IssueLink;

import de.uhd.ifi.se.decision.documentation.jira.persistence.LinkEntity;

/**
 * @description Model class for links between decision knowledge elements
 */
public class LinkImpl implements Link {
	private String linkType;
	private long ingoingId;
	private long outgoingId;

	public LinkImpl() {
	}

	public LinkImpl(LinkEntity link) {
		this.linkType = link.getLinkType();
		this.ingoingId = link.getIngoingId();
		this.outgoingId = link.getOutgoingId();
	}
	
	public LinkImpl(IssueLink link) {
		this.linkType = link.getIssueLinkType().getName();
		this.ingoingId = link.getSourceObject().getId();
		this.outgoingId = link.getDestinationObject().getId();
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

	public void setIngoingId(long ingoingId) {
		this.ingoingId = ingoingId;
	}

	public long getOutgoingId() {
		return outgoingId;
	}

	public void setOutgoingId(long outgoingId) {
		this.outgoingId = outgoingId;
	}
}