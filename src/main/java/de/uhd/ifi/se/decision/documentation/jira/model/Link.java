package de.uhd.ifi.se.decision.documentation.jira.model;

import org.codehaus.jackson.map.annotate.JsonDeserialize;

/**
 * Interface for links between knowledge elements
 */
@JsonDeserialize(as = LinkImpl.class)
public interface Link {

	Long getLinkId();

	void setLinkId(Long linkId);

	String getLinkType();

	void setLinkType(String linkType);

	long getIngoingId();

	void setIngoingId(long ingoingId);

	DecisionKnowledgeElement getSourceObject();

	void setSourceObject(DecisionKnowledgeElement decisionKnowledgeElement);

	long getOutgoingId();

	void setOutgoingId(long outgoingId);

	DecisionKnowledgeElement getDestinationObject();

	void setDestinationObject(DecisionKnowledgeElement decisionKnowledgeElement);
}