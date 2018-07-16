package de.uhd.ifi.se.decision.management.jira.model;

import org.codehaus.jackson.map.annotate.JsonDeserialize;

/**
 * Interface for links between knowledge elements. The links are directed, i.e.,
 * they are arrows starting from a source element and ending in a destination element.
 */
@JsonDeserialize(as = LinkImpl.class)
public interface Link {

	/**
	 * Get the id of the link. This id is the internal database id.
	 *
	 * @return id of the link.
	 */
	long getLinkId();

	/**
	 * Set the id of the link. This id is the internal database id.
	 *
	 * @param id
	 *            of the link.
	 */
	void setLinkId(Long linkId);

	/**
	 * Get the type of the link.
	 *
	 * @see LinkType
	 * @return type of the link.
	 */
	String getLinkType();

	/**
	 * Set the type of the link.
	 *
	 * @see LinkType
	 * @param type
	 *            of the link.
	 */
	void setLinkType(String linkType);

	/**
	 * Get the source element of this link.
	 *
	 * @see DecisionKnowledgeElement
	 * @return source element of this link.
	 */
	DecisionKnowledgeElement getSourceElement();

	/**
	 * Set the source element of this link.
	 *
	 * @see DecisionKnowledgeElement
	 * @param decisionKnowledgeElement
	 *            that is the source element of this link.
	 */
	void setSourceElement(DecisionKnowledgeElement decisionKnowledgeElement);

	/**
	 * Set the source element of this link by its id.
	 *
	 * @see DecisionKnowledgeElement
	 * @param id
	 *            of the source element of this link.
	 */
	void setSourceElement(long id);

	/**
	 * Get the id of the source element of this link.
	 *
	 * @see DecisionKnowledgeElement
	 * @return id of the source element of this link.
	 */
	long getIdOfSourceElement();

	/**
	 * Get the destination element of this link.
	 *
	 * @see DecisionKnowledgeElement
	 * @return destination element of this link.
	 */
	DecisionKnowledgeElement getDestinationElement();

	/**
	 * Set the destination element of this link.
	 *
	 * @see DecisionKnowledgeElement
	 * @param decisionKnowledgeElement
	 *            that is the destination element of this link.
	 */
	void setDestinationElement(DecisionKnowledgeElement decisionKnowledgeElement);

	/**
	 * Set the destination element of this link by its id.
	 *
	 * @see DecisionKnowledgeElement
	 * @param id
	 *            of the destination element of this link.
	 */
	void setDestinationElement(long id);

	/**
	 * Get the id of the destination element of this link.
	 *
	 * @see DecisionKnowledgeElement
	 * @return id of the destination element of this link.
	 */
	long getIdOfDestinationElement();
}