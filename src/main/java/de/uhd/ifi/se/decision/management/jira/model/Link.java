package de.uhd.ifi.se.decision.management.jira.model;

import java.util.List;

import org.codehaus.jackson.map.annotate.JsonDeserialize;

/**
 * Interface for links between knowledge elements. The links are directed, i.e.,
 * they are arrows starting from a source element and ending in a destination
 * element.
 */
@JsonDeserialize(as = LinkImpl.class)
public interface Link {

	/**
	 * Get the id of the link. This id is the internal database id.
	 *
	 * @return id of the link.
	 */
	long getId();

	/**
	 * Set the id of the link. This id is the internal database id.
	 *
	 * @param id
	 *            of the link.
	 */
	void setId(long id);

	/**
	 * Get the type of the link.
	 *
	 * @see LinkType
	 * @return type of the link.
	 */
	String getType();

	/**
	 * Set the type of the link.
	 *
	 * @see LinkType
	 * @param type
	 *            of the link.
	 */
	void setType(String type);

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
	 * Get the opposite element of this link.
	 *
	 * @see DecisionKnowledgeElement
	 * @param elementId
	 *            of a decision knowledge element on one side of this link.
	 * 
	 * @return opposite element of this link.
	 */
	DecisionKnowledgeElement getOppositeElement(long elementId);

	/**
	 * Get the opposite element of this link.
	 *
	 * @see DecisionKnowledgeElement
	 * @param element
	 *            a decision knowledge element on one side of this link.
	 * 
	 * @return opposite element of this link.
	 */
	DecisionKnowledgeElement getOppositeElement(DecisionKnowledgeElement element);

	/**
	 * Get both elements that are linked (both sides of the link) as a list.
	 *
	 * @see DecisionKnowledgeElement
	 * 
	 * @return list of linked elements.
	 */
	List<DecisionKnowledgeElement> getBothElements();

	/**
	 * Determine whether the link is an intra (false) or an inter (true) project
	 * link.
	 *
	 * @see DecisionKnowledgeProject
	 * 
	 * @return true if the link connects decision knowledge elements from two
	 *         different projects.
	 */
	boolean isInterProjectLink();

	/**
	 * Determine whether the link connects two existing decision knowledge elements.
	 *
	 * @see DecisionKnowledgeElement
	 * 
	 * @return true if the link connects two existing decision knowledge elements.
	 */
	boolean isValid();

	/**
	 * Get the a new link with swapped source and destination elements.
	 * 
	 * @see DecisionKnowledgeElement
	 * 
	 * @return new link with swapped source and destination elements.
	 */
	Link flip();

	/**
	 * Get the opposite element of this link.
	 *
	 * @see DecisionKnowledgeElement
	 * @see DocumentationLocation
	 * @param elementIdWithPrefix
	 *            of a decision knowledge element on one side of this link including
	 *            the prefix to express documentation location.
	 * 
	 * @return opposite element of this link.
	 */
	DecisionKnowledgeElement getOppositeElement(String elementIdWithPrefix);

	/**
	 * Get a link object with the correct direction: In case the child element is a
	 * pro- or con-argument, the link points from the child towards the parent
	 * element. In case the child element is NOT a pro- or con-argument, the link
	 * points from the parent towards the child element.
	 *
	 * @see DecisionKnowledgeElement
	 * @see LinkType
	 * @param childElement
	 *            a decision knowledge element that is on one end of the link.
	 * @param parentElement
	 *            a decision knowledge element that is on one end of the link.
	 * @param linkType
	 *            the type of the link, either attack, support, or contain.
	 * 
	 * @return a link object.
	 */
	public static Link instantiateDirectedLink(DecisionKnowledgeElement parentElement,
			DecisionKnowledgeElement childElement, LinkType linkType) {
		switch (linkType) {
		case ATTACK:
			return new LinkImpl(childElement, parentElement, linkType);
		case SUPPORT:
			return new LinkImpl(childElement, parentElement, linkType);
		default:
			return new LinkImpl(parentElement, childElement, linkType);
		}
	}

	/**
	 * Get a link object with the correct direction: In case the child element is a
	 * pro- or con-argument, the link points from the child towards the parent
	 * element. In case the child element is NOT a pro- or con-argument, the link
	 * points from the parent towards the child element.
	 *
	 * @see DecisionKnowledgeElement
	 * @see LinkType
	 * @param childElement
	 *            a decision knowledge element that is on one end of the link.
	 * @param parentElement
	 *            a decision knowledge element that is on one end of the link.
	 * 
	 * @return a link object.
	 */
	public static Link instantiateDirectedLink(DecisionKnowledgeElement parentElement,
			DecisionKnowledgeElement childElement) {
		KnowledgeType knowledgeTypeOfChildElement = childElement.getType();
		LinkType linkType = LinkType.getLinkTypeForKnowledgeType(knowledgeTypeOfChildElement);
		return instantiateDirectedLink(parentElement, childElement, linkType);
	}

	/**
	 * Determine if both source and destination element of the link are documented
	 * as JIRA issues.
	 *
	 * @see DecisionKnowledgeElement
	 * 
	 * @return true if both source and destination element of the link are
	 *         documented as JIRA issues.
	 */
	public boolean isIssueLink();

	/**
	 * Determine if the source and/or destination element of the link have an unknown
	 * documentation location.
	 *
	 * @see DecisionKnowledgeElement
	 * 
	 * @return true if the source and/or the destination element of the link have an
	 *         unknown documentation location.
	 */
	public boolean containsUnknownDocumentationLocation();

	String getIdOfSourceElementWithPrefix();

	void setSourceElement(String idWithPrefix);

	String getIdOfDestinationElementWithPrefix();

	void setDestinationElement(String idWithPrefix);

	void setDocumentationLocationOfSourceElement(String documentationLocation);

	void setDocumentationLocationOfDestinationElement(String documentationLocation);
}