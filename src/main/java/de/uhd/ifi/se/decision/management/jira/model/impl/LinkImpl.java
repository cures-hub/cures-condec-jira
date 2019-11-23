package de.uhd.ifi.se.decision.management.jira.model.impl;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.annotate.JsonProperty;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.link.IssueLink;

import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.DocumentationLocation;
import de.uhd.ifi.se.decision.management.jira.model.Link;
import de.uhd.ifi.se.decision.management.jira.model.LinkType;
import de.uhd.ifi.se.decision.management.jira.persistence.KnowledgePersistenceManager;
import de.uhd.ifi.se.decision.management.jira.persistence.impl.AbstractPersistenceManagerForSingleLocation;
import de.uhd.ifi.se.decision.management.jira.persistence.tables.LinkInDatabase;

/**
 * Model class for links between decision knowledge elements
 */
public class LinkImpl extends DefaultWeightedEdge implements Link {

	private long id;
	private String type; // TODO Use LinkType istead of String
	private DecisionKnowledgeElement source;
	private DecisionKnowledgeElement target;

	protected static final Logger LOGGER = LoggerFactory.getLogger(LinkImpl.class);
	private static final long serialVersionUID = 1L;

	public LinkImpl() {
		super();
	}

	public LinkImpl(DecisionKnowledgeElement sourceElement, DecisionKnowledgeElement destinationElement) {
		this();
		this.source = sourceElement;
		this.target = destinationElement;
	}

	public LinkImpl(DecisionKnowledgeElement sourceElement, DecisionKnowledgeElement destinationElement,
			String linkType) {
		this(sourceElement, destinationElement);
		this.type = linkType;
	}

	public LinkImpl(DecisionKnowledgeElement sourceElement, DecisionKnowledgeElement destinationElement,
			LinkType linkType) {
		this(sourceElement, destinationElement, linkType.toString());
	}

	public LinkImpl(long idOfSourceElement, long idOfDestinationElement,
			DocumentationLocation sourceDocumentationLocation, DocumentationLocation destDocumentationLocation) {
		super();
		this.setSourceElement(idOfSourceElement, sourceDocumentationLocation);
		this.setDestinationElement(idOfDestinationElement, destDocumentationLocation);
	}

	public LinkImpl(IssueLink issueLink) {
		super();
		this.id = issueLink.getId();
		this.type = issueLink.getIssueLinkType().getName();
		Issue sourceIssue = issueLink.getSourceObject();
		if (sourceIssue != null) {
			this.source = new DecisionKnowledgeElementImpl(sourceIssue);
		}
		Issue destinationIssue = issueLink.getDestinationObject();
		if (destinationIssue != null) {
			this.target = new DecisionKnowledgeElementImpl(destinationIssue);
		}
	}

	public LinkImpl(LinkInDatabase linkInDatabase) {
		super();
		if (linkInDatabase.getSourceId() > 0) {
			this.setSourceElement(linkInDatabase.getSourceId(), linkInDatabase.getSourceDocumentationLocation());
		}
		if (linkInDatabase.getDestinationId() > 0) {
			this.setDestinationElement(linkInDatabase.getDestinationId(),
					linkInDatabase.getDestDocumentationLocation());
		}
		this.id = linkInDatabase.getId();
		this.type = linkInDatabase.getType();
	}

	@Override
	public long getId() {
		if (id <= 0) {
			id = KnowledgePersistenceManager.getLinkId(this);
		}
		return id;
	}

	@Override
	public void setId(long id) {
		this.id = id;
	}

	@Override
	public String getType() {
		if (type == null || type.isBlank()) {
			return "Relates";
		}
		return type;
	}

	@Override
	public void setType(LinkType type) {
		this.type = type.toString();
	}

	@Override
	@JsonProperty("type")
	public void setType(String type) {
		this.type = type;
	}

	@Override
	public void setSourceElement(long id, DocumentationLocation documentationLocation) {
		if (this.source == null) {
			this.source = KnowledgePersistenceManager.getOrCreate("").getDecisionKnowledgeElement(id,
					documentationLocation);
		}
		this.source.setId(id);
		this.source.setDocumentationLocation(documentationLocation);
	}

	@Override
	public void setSourceElement(long id, String documentationLocation) {
		this.setSourceElement(id, DocumentationLocation.getDocumentationLocationFromIdentifier(documentationLocation));
	}

	@Override
	public DecisionKnowledgeElement getSource() {
		if (source == null) {
			source = (DecisionKnowledgeElement) super.getSource();
		}
		return source;
	}

	@Override
	public void setSourceElement(DecisionKnowledgeElement sourceElement) {
		this.source = sourceElement;
	}

	@Override
	public void setDestinationElement(long id, DocumentationLocation documentationLocation) {
		if (this.target == null) {
			this.target = KnowledgePersistenceManager.getOrCreate("").getDecisionKnowledgeElement(id,
					documentationLocation);
		}
		this.target.setId(id);
		this.target.setDocumentationLocation(documentationLocation);
	}

	@Override
	public void setDestinationElement(long id, String documentationLocation) {
		this.setDestinationElement(id,
				DocumentationLocation.getDocumentationLocationFromIdentifier(documentationLocation));
	}

	@Override
	public DecisionKnowledgeElement getTarget() {
		if (target == null) {
			target = (DecisionKnowledgeElement) super.getTarget();
		}
		return target;
	}

	@Override
	public void setDestinationElement(DecisionKnowledgeElement destinationElement) {
		this.target = destinationElement;
	}

	@Override
	@JsonProperty("idOfSourceElement")
	public void setIdOfSourceElement(long id) {
		if (this.source == null) {
			this.source = new DecisionKnowledgeElementImpl();
		}
		this.source.setId(id);
	}

	@Override
	@JsonProperty("idOfDestinationElement")
	public void setIdOfDestinationElement(long id) {
		if (this.target == null) {
			this.target = new DecisionKnowledgeElementImpl();
		}
		this.target.setId(id);
	}

	@Override
	public DecisionKnowledgeElement getOppositeElement(long elementId) {
		if (!this.isValid()) {
			return null;
		}
		if (this.source.getId() == elementId) {
			return target;
		}
		if (this.target.getId() == elementId) {
			return source;
		}
		return null;
	}

	@Override
	public DecisionKnowledgeElement getOppositeElement(DecisionKnowledgeElement element) {
		return getOppositeElement(element.getId());
	}

	@Override
	public List<DecisionKnowledgeElement> getBothElements() throws NullPointerException {
		List<DecisionKnowledgeElement> bothElements = new ArrayList<DecisionKnowledgeElement>();
		bothElements.add(target);
		bothElements.add(source);
		return bothElements;
	}

	@Override
	public boolean isValid() {
		return source.existsInDatabase() && target.existsInDatabase();
	}

	@Override
	public boolean isInterProjectLink() {
		try {
			return !source.getProject().getProjectKey().equals(target.getProject().getProjectKey());
		} catch (NullPointerException e) {
			LOGGER.error("Link is not valid. Message: " + e.getMessage());
			return false;
		}
	}

	@Override
	public Link flip() {
		return new LinkImpl(this.getTarget(), this.getSource());
	}

	@Override
	public String toString() {
		String sourceElementIdPrefix = DocumentationLocation.getIdentifier(source);
		String destinationElementIdPrefix = DocumentationLocation.getIdentifier(target);
		return sourceElementIdPrefix + source.getId() + " to " + destinationElementIdPrefix + target.getId();
	}

	@Override
	@JsonProperty("documentationLocationOfSourceElement")
	public void setDocumentationLocationOfSourceElement(String documentationLocationIdentifier) {
		DocumentationLocation documentationLocation = DocumentationLocation
				.getDocumentationLocationFromIdentifier(documentationLocationIdentifier);
		if (this.source.getDocumentationLocation() != documentationLocation) {
			this.source = KnowledgePersistenceManager.getOrCreate("").getDecisionKnowledgeElement(this.source.getId(),
					documentationLocation);
		}
		this.getSource().setDocumentationLocation(documentationLocation);
	}

	@Override
	@JsonProperty("documentationLocationOfDestinationElement")
	public void setDocumentationLocationOfDestinationElement(String documentationLocationIdentifier) {
		DocumentationLocation documentationLocation = DocumentationLocation
				.getDocumentationLocationFromIdentifier(documentationLocationIdentifier);
		if (this.target.getDocumentationLocation() != documentationLocation) {
			this.target = KnowledgePersistenceManager.getOrCreate("").getDecisionKnowledgeElement(this.target.getId(),
					documentationLocation);
		}
		this.getTarget().setDocumentationLocation(documentationLocation);
	}

	@Override
	public boolean isIssueLink() {
		return this.getSource().getDocumentationLocation() == DocumentationLocation.JIRAISSUE
				&& this.getTarget().getDocumentationLocation() == DocumentationLocation.JIRAISSUE;
	}

	@Override
	public boolean containsUnknownDocumentationLocation() {
		return this.getTarget().getDocumentationLocation() == DocumentationLocation.UNKNOWN
				|| this.getSource().getDocumentationLocation() == DocumentationLocation.UNKNOWN;
	}

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

	@Override
	public boolean equals(LinkInDatabase linkInDatabase) {
		return this.source.getId() == linkInDatabase.getSourceId()
				&& this.target.getId() == linkInDatabase.getDestinationId();
	}

	@Override
	public void setDefaultDocumentationLocation(String projectKey) {
		AbstractPersistenceManagerForSingleLocation defaultPersistenceManager = KnowledgePersistenceManager
				.getOrCreate(projectKey).getDefaultPersistenceManager();
		String defaultDocumentationLocation = DocumentationLocation
				.getIdentifier(defaultPersistenceManager.getDocumentationLocation());
		if (this.getTarget().getDocumentationLocation() == DocumentationLocation.UNKNOWN) {
			this.setDocumentationLocationOfDestinationElement(defaultDocumentationLocation);
		}
		if (this.getSource().getDocumentationLocation() == DocumentationLocation.UNKNOWN) {
			this.setDocumentationLocationOfSourceElement(defaultDocumentationLocation);
		}
	}

	@Override
	public int hashCode() {
		return (getSource().hashCode() + getTarget().hashCode());
	}
}