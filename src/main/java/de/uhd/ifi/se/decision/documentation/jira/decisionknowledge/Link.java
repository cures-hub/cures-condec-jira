package de.uhd.ifi.se.decision.documentation.jira.decisionknowledge;

import com.atlassian.jira.issue.link.IssueLink;

/**
 * @description Model class for links between decision knowledge elements
 */
public class Link implements ILink {
	private String linkType;
	private long ingoingId;
	private long outgoingId;

	public Link() {
	}

	public Link(ILinkEntity link) {
		this.linkType = link.getLinkType();
		this.ingoingId = link.getIngoingId();
		this.outgoingId = link.getOutgoingId();
	}
	
	public Link(IssueLink link) {
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