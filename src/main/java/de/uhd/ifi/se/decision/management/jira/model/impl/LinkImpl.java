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
import de.uhd.ifi.se.decision.management.jira.model.Node;
import de.uhd.ifi.se.decision.management.jira.persistence.PersistenceManager;
import de.uhd.ifi.se.decision.management.jira.persistence.impl.AbstractPersistenceManagerForSingleLocation;
import de.uhd.ifi.se.decision.management.jira.persistence.tables.LinkInDatabase;

/**
 * Model class for links between decision knowledge elements
 */
public class LinkImpl extends DefaultWeightedEdge implements Link {

	private long id;
	private String type;
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
			id = PersistenceManager.getLinkId(this);
		}
		return id;
	}

	@Override
	public void setId(long id) {
		this.id = id;
	}

	@Override
	public String getType() {
		if (type == null || type.equals("")) {
			return "Relates";
		}
		return type;
	}

	@Override
	public void setType(String type) {
		this.type = type;
	}

	@Override
	public void setSourceElement(long id, DocumentationLocation documentationLocation) {
		if (this.source == null) {
			this.source = PersistenceManager.getDecisionKnowledgeElement(id, documentationLocation);
		}
		this.source.setId(id);
		this.source.setDocumentationLocation(documentationLocation);
	}

	@Override
	public void setSourceElement(long id, String documentationLocation) {
		this.setSourceElement(id, DocumentationLocation.getDocumentationLocationFromIdentifier(documentationLocation));
	}

	@Override
	public DecisionKnowledgeElement getSourceElement() {
		if (source == null) {
			return (DecisionKnowledgeElement) super.getSource();
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
			this.target = PersistenceManager.getDecisionKnowledgeElement(id, documentationLocation);
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
	public DecisionKnowledgeElement getDestinationElement() {
		if (target == null) {
			return (DecisionKnowledgeElement) super.getTarget();
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
		return new LinkImpl(this.getDestinationElement(), this.getSourceElement());
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
			this.source = PersistenceManager.getDecisionKnowledgeElement(this.source.getId(), documentationLocation);
		}
		this.getSourceElement().setDocumentationLocation(documentationLocation);
	}

	@Override
	@JsonProperty("documentationLocationOfDestinationElement")
	public void setDocumentationLocationOfDestinationElement(String documentationLocationIdentifier) {
		DocumentationLocation documentationLocation = DocumentationLocation
				.getDocumentationLocationFromIdentifier(documentationLocationIdentifier);
		if (this.target.getDocumentationLocation() != documentationLocation) {
			this.target = PersistenceManager.getDecisionKnowledgeElement(this.target.getId(), documentationLocation);
		}
		this.getDestinationElement().setDocumentationLocation(documentationLocation);
	}

	@Override
	public boolean isIssueLink() {
		return this.getSourceElement().getDocumentationLocation() == DocumentationLocation.JIRAISSUE
				&& this.getDestinationElement().getDocumentationLocation() == DocumentationLocation.JIRAISSUE;
	}

	@Override
	public boolean containsUnknownDocumentationLocation() {
		return this.getDestinationElement().getDocumentationLocation() == DocumentationLocation.UNKNOWN
				|| this.getSourceElement().getDocumentationLocation() == DocumentationLocation.UNKNOWN;
	}

	@Override
	public Node getSource() {
		if (super.getSource() == null) {
			return this.source;
		}
		return (Node) super.getSource();
	}

	@Override
	public Node getTarget() {
		if (super.getTarget() == null) {
			return target;
		}
		return (Node) super.getTarget();
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
		return this.getId() == link.getId();
	}

	public boolean equals(LinkInDatabase linkInDatabase) {
		return this.source.getId() == linkInDatabase.getSourceId()
				&& this.target.getId() == linkInDatabase.getDestinationId();
	}

	public void setDefaultDocumentationLocation(String projectKey) {
		AbstractPersistenceManagerForSingleLocation defaultPersistenceManager = PersistenceManager
				.getOrCreate(projectKey).getDefaultPersistenceManager();
		String defaultDocumentationLocation = DocumentationLocation
				.getIdentifier(defaultPersistenceManager.getDocumentationLocation());
		if (this.getDestinationElement().getDocumentationLocation() == DocumentationLocation.UNKNOWN) {
			this.setDocumentationLocationOfDestinationElement(defaultDocumentationLocation);
		}
		if (this.getSourceElement().getDocumentationLocation() == DocumentationLocation.UNKNOWN) {
			this.setDocumentationLocationOfSourceElement(defaultDocumentationLocation);
		}
	}
}