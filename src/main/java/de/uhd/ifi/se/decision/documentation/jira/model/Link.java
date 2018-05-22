package de.uhd.ifi.se.decision.documentation.jira.model;

import org.codehaus.jackson.map.annotate.JsonDeserialize;

/**
 * @description Interface for links between knowledge elements
 */
@JsonDeserialize(as = LinkImpl.class)
public interface Link {

	public Long getLinkId();

	public void setLinkId(Long linkId);

	public String getLinkType();

	public void setLinkType(String linkType);

	public long getIngoingId();

	public void setIngoingId(long ingoingId);

	public DecisionKnowledgeElement getSourceObject();

	public void setSourceObject(DecisionKnowledgeElement decisionKnowledgeElement);

	public long getOutgoingId();

	public void setOutgoingId(long outgoingId);

	public DecisionKnowledgeElement getDestinationObject();

	public void setDestinationObject(DecisionKnowledgeElement decisionKnowledgeElement);
}