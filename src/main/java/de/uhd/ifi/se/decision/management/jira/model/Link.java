package de.uhd.ifi.se.decision.management.jira.model;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.link.IssueLink;
import com.atlassian.jira.issue.link.IssueLinkManager;

import de.uhd.ifi.se.decision.management.jira.persistence.KnowledgePersistenceManager;
import de.uhd.ifi.se.decision.management.jira.persistence.tables.LinkInDatabase;

/**
 * Models links (=edges/relationships) between knowledge elements. The links are
 * directed, i.e., they are arrows starting from a source element and ending in
 * a destination element.
 */
public class Link extends DefaultWeightedEdge {

	private long id;
	private LinkType type;
	private KnowledgeElement source;
	private KnowledgeElement target;

	protected static final Logger LOGGER = LoggerFactory.getLogger(Link.class);
	private static final long serialVersionUID = 1L;

	public Link() {
		super();
	}

	public Link(KnowledgeElement sourceElement, KnowledgeElement destinationElement) {
		this();
		this.source = sourceElement;
		this.target = destinationElement;
	}

	public Link(KnowledgeElement sourceElement, KnowledgeElement destinationElement, LinkType linkType) {
		this(sourceElement, destinationElement);
		this.type = linkType;
	}

	public Link(long idOfSourceElement, long idOfDestinationElement, DocumentationLocation sourceDocumentationLocation,
			DocumentationLocation destDocumentationLocation) {
		super();
		this.setSourceElement(idOfSourceElement, sourceDocumentationLocation);
		this.setDestinationElement(idOfDestinationElement, destDocumentationLocation);
	}

	public Link(IssueLink jiraIssueLink) {
		super();
		this.id = jiraIssueLink.getId();
		this.type = LinkType.getLinkType(jiraIssueLink.getIssueLinkType().getName());
		Issue sourceJiraIssue = jiraIssueLink.getSourceObject();
		if (sourceJiraIssue != null) {
			this.source = new KnowledgeElement(sourceJiraIssue);
		}
		Issue destinationJiraIssue = jiraIssueLink.getDestinationObject();
		if (destinationJiraIssue != null) {
			this.target = new KnowledgeElement(destinationJiraIssue);
		}
	}

	public Link(LinkInDatabase linkInDatabase) {
		super();
		if (linkInDatabase.getSourceId() > 0) {
			this.setSourceElement(linkInDatabase.getSourceId(), linkInDatabase.getSourceDocumentationLocation());
		}
		if (linkInDatabase.getDestinationId() > 0) {
			this.setDestinationElement(linkInDatabase.getDestinationId(),
					linkInDatabase.getDestDocumentationLocation());
		}
		this.id = linkInDatabase.getId();
		this.type = LinkType.getLinkType(linkInDatabase.getType());
	}

	/**
	 * @return id of the link. This id is the internal database id.
	 */
	public long getId() {
		if (id == 0) {
			id = KnowledgePersistenceManager.getLinkId(this);
		}
		return id;
	}

	/**
	 * Sets the id of the link. This id is the internal database id.
	 *
	 * @param id
	 *            of the link.
	 */
	public void setId(long id) {
		this.id = id;
	}

	/**
	 * @see LinkType
	 * @return type of the link as a String.
	 */
	@XmlElement(name = "type")
	public String getTypeAsString() {
		if (type == null) {
			type = LinkType.getDefaultLinkType();
		}
		if (type == LinkType.OTHER) {
			// get Jira issue link from ID
			IssueLinkManager jiraIssueLinkManager = ComponentAccessor.getComponent(IssueLinkManager.class);
			IssueLink issueLink = jiraIssueLinkManager.getIssueLink(id);
			if (issueLink != null) {
				return issueLink.getIssueLinkType().getName();
			}
		}
		return type.toString();
	}

	/**
	 * @see LinkType
	 * @return type of the link.
	 */
	public LinkType getType() {
		return type;
	}

	/**
	 * Sets the type of the link.
	 *
	 * @see LinkType
	 * @param type
	 *            of the link.
	 */
	public void setType(LinkType type) {
		this.type = type;
	}

	/**
	 * Sets the type of the link.
	 *
	 * @see LinkType
	 * @param type
	 *            of the link as a String, e.g. "relates".
	 */
	@JsonProperty("type")
	public void setType(String type) {
		this.type = LinkType.getLinkType(type);
	}

	@XmlElement(name = "color")
	public String getColor() {
		return LinkType.getLinkTypeColor(this.getTypeAsString());
	}

	/**
	 * Set the source element of this link by its id and documentation location.
	 *
	 * @see KnowledgeElement
	 * @see DocumentationLocation
	 * @param id
	 *            of the source element of this link.
	 * @param documentationLocation
	 *            of the decision knowledge element.
	 */
	public void setSourceElement(long id, DocumentationLocation documentationLocation) {
		if (this.source == null) {
			this.source = KnowledgePersistenceManager.getOrCreate("").getKnowledgeElement(id, documentationLocation);
		}
		if (this.source == null) {
			this.source = new KnowledgeElement();
		}
		this.source.setId(id);
		this.source.setDocumentationLocation(documentationLocation);
	}

	/**
	 * Sets the source element of this link by its id and documentation location.
	 *
	 * @see KnowledgeElement
	 * @see DocumentationLocation
	 * @param id
	 *            of the source element of this link.
	 * @param documentationLocation
	 *            of the decision knowledge element as a String, e.g. "i" for JIRA
	 *            issue.
	 */
	public void setSourceElement(long id, String documentationLocation) {
		this.setSourceElement(id, DocumentationLocation.getDocumentationLocationFromIdentifier(documentationLocation));
	}

	/**
	 * @see KnowledgeElement
	 * @return source element of this link (=edge).
	 */
	@Override
	public KnowledgeElement getSource() {
		if (source == null) {
			source = (KnowledgeElement) super.getSource();
		}
		return source;
	}

	/**
	 * Sets the source element of this link.
	 *
	 * @see KnowledgeElement
	 * @param sourceElement
	 *            that is the source element of this link.
	 */
	public void setSourceElement(KnowledgeElement sourceElement) {
		this.source = sourceElement;
	}

	/**
	 * Sets the destination element of this link by its id and documentation
	 * location.
	 *
	 * @see KnowledgeElement
	 * @see DocumentationLocation
	 * @param id
	 *            of the destination element of this link.
	 * @param documentationLocation
	 *            of the decision knowledge element.
	 */
	public void setDestinationElement(long id, DocumentationLocation documentationLocation) {
		if (this.target == null) {
			this.target = KnowledgePersistenceManager.getOrCreate("").getKnowledgeElement(id, documentationLocation);
		}
		if (this.target == null) {
			this.target = new KnowledgeElement();
		}
		this.target.setId(id);
		this.target.setDocumentationLocation(documentationLocation);
	}

	/**
	 * Sets the destination element of this link by its id and documentation
	 * location.
	 *
	 * @see KnowledgeElement
	 * @see DocumentationLocation
	 * @param id
	 *            of the destination element of this link.
	 * @param documentationLocation
	 *            of the decision knowledge element as a String, e.g. "i" for JIRA
	 *            issue.
	 */
	public void setDestinationElement(long id, String documentationLocation) {
		this.setDestinationElement(id,
				DocumentationLocation.getDocumentationLocationFromIdentifier(documentationLocation));
	}

	/**
	 * @see KnowledgeElement
	 * @return target (=destination) element of this link (=edge).
	 */
	@Override
	public KnowledgeElement getTarget() {
		if (target == null) {
			target = (KnowledgeElement) super.getTarget();
		}
		return target;
	}

	/**
	 * Sets the destination element of this link.
	 *
	 * @see KnowledgeElement
	 * @param destinationElement
	 *            that is the destination element of this link.
	 */
	public void setDestinationElement(KnowledgeElement destinationElement) {
		this.target = destinationElement;
	}

	/**
	 * Sets the id of the source element of this link. Instantiates a new decision
	 * knowledge element if the source element is null.
	 *
	 * @see KnowledgeElement
	 * @param id
	 *            of the destination element of this link.
	 */
	@JsonProperty("idOfSourceElement")
	public void setIdOfSourceElement(long id) {
		if (this.source == null) {
			this.source = new KnowledgeElement();
		}
		this.source.setId(id);
	}

	/**
	 * Sets the id of the destination element of this link. Instantiates a new
	 * decision knowledge element if the destination element is null.
	 *
	 * @see KnowledgeElement
	 * @param id
	 *            of the destination element of this link.
	 */
	@JsonProperty("idOfDestinationElement")
	public void setIdOfDestinationElement(long id) {
		if (this.target == null) {
			this.target = new KnowledgeElement();
		}
		this.target.setId(id);
	}

	/**
	 * @param elementId
	 *            of a decision knowledge element on one side of this link.
	 * 
	 * @return opposite {@link KnowledgeElement} of this link.
	 */
	public KnowledgeElement getOppositeElement(long elementId) {
		if (!this.isValid()) {
			return null;
		}
		if (this.source.getId() == elementId) {
			return target;
		}
		return source;
	}

	/**
	 * @see KnowledgeElement
	 * @param element
	 *            a decision knowledge element on one side of this link.
	 * 
	 * @return opposite element of this link.
	 */
	public KnowledgeElement getOppositeElement(KnowledgeElement element) {
		return getOppositeElement(element.getId());
	}

	/**
	 * @see KnowledgeElement
	 * @return both elements that are linked (both sides of the link) as a list.
	 */
	public List<KnowledgeElement> getBothElements() {
		List<KnowledgeElement> bothElements = new ArrayList<KnowledgeElement>();
		bothElements.add(target);
		bothElements.add(source);
		return bothElements;
	}

	/**
	 * @see KnowledgeElement
	 * @return true if the link connects two existing decision knowledge elements.
	 */
	@JsonIgnore
	public boolean isValid() {
		return source.getId() != 0 && target.getId() != 0;
	}

	/**
	 * Determines whether the link is an intra (false) or an inter (true) project
	 * link.
	 *
	 * @see DecisionKnowledgeProject
	 * 
	 * @return true if the link connects decision knowledge elements from two
	 *         different projects.
	 */
	@JsonIgnore
	public boolean isInterProjectLink() {
		try {
			return !source.getProject().getProjectKey().equals(target.getProject().getProjectKey());
		} catch (NullPointerException e) {
			LOGGER.error("Link is not valid. Message: " + e.getMessage());
			return false;
		}
	}

	/**
	 * @see KnowledgeElement
	 * @return new link with swapped source and destination elements.
	 */
	public Link flip() {
		return new Link(this.getTarget(), this.getSource());
	}

	@Override
	public String toString() {
		String sourceElementIdPrefix = DocumentationLocation.getIdentifier(source);
		String destinationElementIdPrefix = DocumentationLocation.getIdentifier(target);
		return sourceElementIdPrefix + source.getId() + " to " + destinationElementIdPrefix + target.getId();
	}

	/**
	 * Sets the documentation location of the source element of this link. Retrieves
	 * a new decision knowledge element from database if the source element
	 * currently has a different documentation location.
	 *
	 * @see KnowledgeElement
	 * @see DocumentationLocation
	 * @param documentationLocationIdentifier
	 *            of the source element of this link, e.g., "i" for JIRA issue.
	 */
	@JsonProperty("documentationLocationOfSourceElement")
	public void setDocumentationLocationOfSourceElement(String documentationLocationIdentifier) {
		DocumentationLocation documentationLocation = DocumentationLocation
				.getDocumentationLocationFromIdentifier(documentationLocationIdentifier);
		this.getSource().setDocumentationLocation(documentationLocation);
	}

	/**
	 * Sets the documentation location of the destination element of this link.
	 * Retrieves a new decision knowledge element from database if the destination
	 * element currently has a different documentation location.
	 *
	 * @see KnowledgeElement
	 * @see DocumentationLocation
	 * @param documentationLocationIdentifier
	 *            of the destination element of this link, e.g., "i" for JIRA issue.
	 */
	@JsonProperty("documentationLocationOfDestinationElement")
	public void setDocumentationLocationOfDestinationElement(String documentationLocationIdentifier) {
		DocumentationLocation documentationLocation = DocumentationLocation
				.getDocumentationLocationFromIdentifier(documentationLocationIdentifier);
		this.getTarget().setDocumentationLocation(documentationLocation);
	}

	/**
	 * @see KnowledgeElement
	 * 
	 * @return true if both source and destination element of the link are
	 *         documented as Jira issues.
	 */
	@JsonIgnore
	public boolean isIssueLink() {
		return this.getSource().getDocumentationLocation() == DocumentationLocation.JIRAISSUE
				&& this.getTarget().getDocumentationLocation() == DocumentationLocation.JIRAISSUE;
	}

	/**
	 * @see KnowledgeElement
	 * @return true if the source and/or the destination element of the link have an
	 *         unknown documentation location.
	 */
	public boolean containsUnknownDocumentationLocation() {
		return this.getTarget().getDocumentationLocation() == DocumentationLocation.UNKNOWN
				|| this.getSource().getDocumentationLocation() == DocumentationLocation.UNKNOWN;
	}

	/**
	 * @return weight of this link (=edge).
	 */
	@Override
	public double getWeight() {
		return super.getWeight();
	}

	@Override
	public boolean equals(Object object) {
		if (object == null) {
			return false;
		}
		if (object == this) {
			return true;
		}
		if (!(object instanceof Link)) {
			return false;
		}
		Link link = (Link) object;
		return this.getSource().equals(link.getSource()) && this.getTarget().equals(link.getTarget());
	}

	/**
	 * Gets a link object with the correct direction: In case the child element is a
	 * pro- or con-argument, the link points from the child towards the parent
	 * element. In case the child element is NOT a pro- or con-argument, the link
	 * points from the parent towards the child element.
	 *
	 * @see KnowledgeElement
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
	public static Link instantiateDirectedLink(KnowledgeElement parentElement, KnowledgeElement childElement,
			LinkType linkType) {
		if ((linkType == LinkType.SUPPORT || linkType == LinkType.ATTACK)
				&& parentElement.getType().replaceProAndConWithArgument() != KnowledgeType.ARGUMENT) {
			// link direction needs to be swapped because an argument supports or attacks a
			// solution option (decision or alternative)
			return new Link(childElement, parentElement, linkType);
		}
		return new Link(parentElement, childElement, linkType);
	}

	/**
	 * Gets a link object with the correct direction: In case the child element is a
	 * pro- or con-argument, the link points from the child towards the parent
	 * element. In case the child element is NOT a pro- or con-argument, the link
	 * points from the parent towards the child element.
	 *
	 * @see KnowledgeElement
	 * @see LinkType
	 * @param childElement
	 *            a decision knowledge element that is on one end of the link.
	 * @param parentElement
	 *            a decision knowledge element that is on one end of the link.
	 * 
	 * @return a link object.
	 */
	public static Link instantiateDirectedLink(KnowledgeElement parentElement, KnowledgeElement childElement) {
		if (parentElement.getType().replaceProAndConWithArgument() == KnowledgeType.ARGUMENT) {
			LinkType linkType = LinkType.getLinkTypeForKnowledgeType(parentElement.getType());
			return instantiateDirectedLink(parentElement, childElement, linkType);
		}
		LinkType linkType = LinkType.getLinkTypeForKnowledgeType(childElement.getType());
		return instantiateDirectedLink(childElement, parentElement, linkType);
	}

	/**
	 * @see LinkInDatabase
	 * @param linkInDatabase
	 *            link stored in database
	 * @return true if link object has the same source and destination id as a
	 *         database object.
	 */
	public boolean equals(LinkInDatabase linkInDatabase) {
		return this.source.getId() == linkInDatabase.getSourceId()
				&& this.target.getId() == linkInDatabase.getDestinationId();
	}

	@Override
	public int hashCode() {
		return getSource().hashCode() + getTarget().hashCode();
	}
}