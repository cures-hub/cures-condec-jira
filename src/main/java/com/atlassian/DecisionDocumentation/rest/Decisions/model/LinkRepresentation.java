package com.atlassian.DecisionDocumentation.rest.Decisions.model;
/**
 * 
 * @author Ewald Rode
 * @description model class for links between decision components
 */
public class LinkRepresentation {
	private String linkType;
	private long ingoingId;
	private long outgoingId;
	
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
