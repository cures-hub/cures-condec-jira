package de.uhd.ifi.se.decision.documentation.jira.decisionknowledge;

/**
 * @description Interface for links between knowledge elements
 */
public interface ILink {

	public String getLinkType();

	public void setLinkType(String linkType);

	public long getIngoingId();

	public void setIngoingId(long ingoingId);

	public long getOutgoingId();

	public void setOutgoingId(long outgoingId);
}
