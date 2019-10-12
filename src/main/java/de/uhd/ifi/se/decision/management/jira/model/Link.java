package de.uhd.ifi.se.decision.management.jira.model;

import java.util.List;

import org.codehaus.jackson.map.annotate.JsonDeserialize;

import de.uhd.ifi.se.decision.management.jira.model.impl.LinkImpl;
import de.uhd.ifi.se.decision.management.jira.persistence.tables.LinkInDatabase;

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
	 * Set the source element of this link by its id and documentation location.
	 *
	 * @see DecisionKnowledgeElement
	 * @see DocumentationLocation
	 * @param id
	 *            of the source element of this link.
	 * @param documenationLocation
	 *            of the decision knowledge element.
	 */
	void setSourceElement(long id, DocumentationLocation documentationLocation);

	/**
	 * Set the source element of this link by its id and documentation location.
	 *
	 * @see DecisionKnowledgeElement
	 * @see DocumentationLocation
	 * @param id
	 *            of the source element of this link.
	 * @param documenationLocation
	 *            of the decision knowledge element as a String, e.g. "i" for JIRA
	 *            issue.
	 */
	void setSourceElement(long id, String documentationLocation);

	/**
	 * Set the id of the source element of this link. Instantiates a new decision
	 * knowledge element if the source element is null.
	 *
	 * @see DecisionKnowledgeElement
	 * @param id
	 *            of the destination element of this link.
	 */
	void setIdOfSourceElement(long id);

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
	 * Set the destination element of this link by its id and documentation
	 * location.
	 *
	 * @see DecisionKnowledgeElement
	 * @see DocumentationLocation
	 * @param id
	 *            of the destination element of this link.
	 * @param documenationLocation
	 *            of the decision knowledge element.
	 */
	void setDestinationElement(long id, DocumentationLocation documentationLocation);

	/**
	 * Set the destination element of this link by its id and documentation
	 * location.
	 *
	 * @see DecisionKnowledgeElement
	 * @see DocumentationLocation
	 * @param id
	 *            of the destination element of this link.
	 * @param documenationLocation
	 *            of the decision knowledge element as a String, e.g. "i" for JIRA
	 *            issue.
	 */
	void setDestinationElement(long id, String documentationLocation);

	/**
	 * Set the id of the destination element of this link. Instantiates a new
	 * decision knowledge element if the destination element is null.
	 *
	 * @see DecisionKnowledgeElement
	 * @param id
	 *            of the destination element of this link.
	 */
	void setIdOfDestinationElement(long id);

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
		return new LinkImpl(parentElement, childElement, linkType.getName());
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
	 * Determine if the source and/or destination element of the link have an
	 * unknown documentation location.
	 *
	 * @see DecisionKnowledgeElement
	 * 
	 * @return true if the source and/or the destination element of the link have an
	 *         unknown documentation location.
	 */
	public boolean containsUnknownDocumentationLocation();

	/**
	 * Set the documentation location of the source element of this link. Retrieves
	 * a new decision knowledge element from database if the source element
	 * currently has a different documentation location.
	 *
	 * @see DecisionKnowledgeElement
	 * @see DocumentationLocation
	 * @param documentationLocation
	 *            of the source element of this link, e.g., "i" for JIRA issue.
	 */
	void setDocumentationLocationOfSourceElement(String documentationLocation);

	/**
	 * Set the documentation location of the destination element of this link.
	 * Retrieves a new decision knowledge element from database if the destination
	 * element currently has a different documentation location.
	 *
	 * @see DecisionKnowledgeElement
	 * @see DocumentationLocation
	 * @param documentationLocation
	 *            of the destination element of this link, e.g., "i" for JIRA issue.
	 */
	void setDocumentationLocationOfDestinationElement(String documentationLocation);

	/**
	 * Determine whether a link object has the same source and destination id as a
	 * database object.
	 * 
	 * @see LinkInDatabase
	 * 
	 * @param linkInDatabase
	 *            link stored in database
	 * @return true if objects have the same source and destination id.
	 */
	boolean equals(LinkInDatabase linkInDatabase);
}