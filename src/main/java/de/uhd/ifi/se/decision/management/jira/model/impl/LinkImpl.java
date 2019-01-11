package de.uhd.ifi.se.decision.management.jira.model.impl;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.annotate.JsonProperty;

import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.link.IssueLink;

import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.DocumentationLocation;
import de.uhd.ifi.se.decision.management.jira.model.Link;
import de.uhd.ifi.se.decision.management.jira.model.LinkType;
import de.uhd.ifi.se.decision.management.jira.persistence.AbstractPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.persistence.tables.LinkInDatabase;

/**
 * Model class for links between decision knowledge elements
 */
public class LinkImpl implements Link {

	private long id;
	private String type;
	private DecisionKnowledgeElement sourceElement;
	private DecisionKnowledgeElement destinationElement;

	public LinkImpl() {
	}

	public LinkImpl(DecisionKnowledgeElement sourceElement, DecisionKnowledgeElement destinationElement) {
		this.sourceElement = sourceElement;
		this.destinationElement = destinationElement;
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
		this.setSourceElement(idOfSourceElement, sourceDocumentationLocation);
		this.setDestinationElement(idOfDestinationElement, destDocumentationLocation);
	}

	public LinkImpl(IssueLink issueLink) {
		this.id = issueLink.getId();
		this.type = issueLink.getIssueLinkType().getName();
		Issue sourceIssue = issueLink.getSourceObject();
		if (sourceIssue != null) {
			this.sourceElement = new DecisionKnowledgeElementImpl(sourceIssue);
		}
		Issue destinationIssue = issueLink.getDestinationObject();
		if (destinationIssue != null) {
			this.destinationElement = new DecisionKnowledgeElementImpl(destinationIssue);
		}
	}

	public LinkImpl(LinkInDatabase linkInDatabase) {
		this();
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
		return id;
	}

	@Override
	public void setId(long id) {
		this.id = id;
	}

	@Override
	public String getType() {
		if (type == null || type.equals("")) {
			return "contain";
		}
		return type;
	}

	@Override
	public void setType(String type) {
		this.type = type;
	}

	@Override
	public void setSourceElement(long id, DocumentationLocation documentationLocation) {
		if (this.sourceElement == null) {
			this.sourceElement = AbstractPersistenceManager.getDecisionKnowledgeElement(id, documentationLocation);
		}
		this.sourceElement.setId(id);
		this.sourceElement.setDocumentationLocation(documentationLocation);
	}

	@Override
	public void setSourceElement(long id, String documentationLocation) {
		this.setSourceElement(id, DocumentationLocation.getDocumentationLocationFromIdentifier(documentationLocation));
	}

	@Override
	public DecisionKnowledgeElement getSourceElement() {
		return sourceElement;
	}

	@Override
	public void setSourceElement(DecisionKnowledgeElement sourceElement) {
		this.sourceElement = sourceElement;
	}

	@Override
	public void setDestinationElement(long id, DocumentationLocation documentationLocation) {
		if (this.destinationElement == null) {
			this.destinationElement = AbstractPersistenceManager.getDecisionKnowledgeElement(id, documentationLocation);
		}
		this.destinationElement.setId(id);
		this.destinationElement.setDocumentationLocation(documentationLocation);
	}

	@Override
	public void setDestinationElement(long id, String documentationLocation) {
		this.setDestinationElement(id,
				DocumentationLocation.getDocumentationLocationFromIdentifier(documentationLocation));
	}

	@Override
	public DecisionKnowledgeElement getDestinationElement() {
		return destinationElement;
	}

	@Override
	public void setDestinationElement(DecisionKnowledgeElement destinationElement) {
		this.destinationElement = destinationElement;
	}

	@Override
	@JsonProperty("idOfSourceElement")
	public void setIdOfSourceElement(long id) {
		if (this.sourceElement == null) {
			this.sourceElement = new DecisionKnowledgeElementImpl();
		}
		this.sourceElement.setId(id);
	}

	@Override
	@JsonProperty("idOfDestinationElement")
	public void setIdOfDestinationElement(long id) {
		if (this.destinationElement == null) {
			this.destinationElement = new DecisionKnowledgeElementImpl();
		}
		this.destinationElement.setId(id);
	}

	@Override
	public DecisionKnowledgeElement getOppositeElement(long elementId) {
		if (!this.isValid()) {
			return null;
		}
		if (this.sourceElement.getId() == elementId) {
			return destinationElement;
		}
		if (this.destinationElement.getId() == elementId) {
			return sourceElement;
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
		bothElements.add(destinationElement);
		bothElements.add(sourceElement);
		return bothElements;
	}

	@Override
	public boolean isValid() {
		return sourceElement != null && destinationElement != null;
	}

	@Override
	public boolean isInterProjectLink() {
		try {
			return !sourceElement.getProject().getProjectKey().equals(destinationElement.getProject().getProjectKey());
		} catch (NullPointerException e) {
			return false;
		}
	}

	@Override
	public Link flip() {
		return new LinkImpl(this.getDestinationElement(), this.getSourceElement());
	}

	@Override
	public String toString() {
		String sourceElementIdPrefix = DocumentationLocation.getIdentifier(sourceElement);
		String destinationElementIdPrefix = DocumentationLocation.getIdentifier(destinationElement);
		return sourceElementIdPrefix + sourceElement.getId() + " to " + destinationElementIdPrefix
				+ destinationElement.getId();
	}

	@Override
	@JsonProperty("documentationLocationOfSourceElement")
	public void setDocumentationLocationOfSourceElement(String documentationLocationIdentifier) {
		DocumentationLocation documentationLocation = DocumentationLocation
				.getDocumentationLocationFromIdentifier(documentationLocationIdentifier);
		if (this.sourceElement.getDocumentationLocation() != documentationLocation) {
			this.sourceElement = AbstractPersistenceManager.getDecisionKnowledgeElement(this.sourceElement.getId(),
					documentationLocation);
		}
		this.getSourceElement().setDocumentationLocation(documentationLocation);
	}

	@Override
	@JsonProperty("documentationLocationOfDestinationElement")
	public void setDocumentationLocationOfDestinationElement(String documentationLocationIdentifier) {
		DocumentationLocation documentationLocation = DocumentationLocation
				.getDocumentationLocationFromIdentifier(documentationLocationIdentifier);
		if (this.destinationElement.getDocumentationLocation() != documentationLocation) {
			this.destinationElement = AbstractPersistenceManager
					.getDecisionKnowledgeElement(this.destinationElement.getId(), documentationLocation);
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
		return this.sourceElement.getId() == link.getSourceElement().getId()
				&& this.destinationElement.getId() == link.getDestinationElement().getId();
	}

	public boolean equals(LinkInDatabase linkInDatabase) {
		return this.sourceElement.getId() == linkInDatabase.getSourceId()
				&& this.destinationElement.getId() == linkInDatabase.getDestinationId();
	}
}