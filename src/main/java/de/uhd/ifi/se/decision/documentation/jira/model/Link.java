package de.uhd.ifi.se.decision.documentation.jira.model;

import org.codehaus.jackson.map.annotate.JsonDeserialize;

/**
 * @description Interface for links between knowledge elements
 */
@JsonDeserialize(as = LinkImpl.class)
public interface Link {

	public String getLinkType();

	public void setLinkType(String linkType);

	public long getIngoingId();

	public DecisionKnowledgeElement getIngoingElement();

	public void setIngoingElement(DecisionKnowledgeElement decisionKnowledgeElement);

	public long getOutgoingId();

	public DecisionKnowledgeElement getOutgoingElement();

	public void setOutgoingElement(DecisionKnowledgeElement decisionKnowledgeElement);
}