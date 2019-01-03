package de.uhd.ifi.se.decision.management.jira.model;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.annotate.JsonProperty;

import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.link.IssueLink;

import de.uhd.ifi.se.decision.management.jira.persistence.AbstractPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.persistence.GenericLinkManager;
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
			LinkType linkType) {
		this(sourceElement, destinationElement);
		this.type = linkType.toString();
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

	public LinkImpl(String idOfSourceElementWithPrefix, String idOfDestinationElementWithPrefix) {
		this.type = "";
		this.setDestinationElement(idOfDestinationElementWithPrefix);
		this.setSourceElement(idOfSourceElementWithPrefix);
	}

	public LinkImpl(String idOfSourceElementWithPrefix, String idOfDestinationElementWithPrefix, String type) {
		this(idOfSourceElementWithPrefix, idOfDestinationElementWithPrefix);
		setType(type);
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
	public String getIdOfSourceElementWithPrefix() {
		String idPrefix = DocumentationLocation.getIdentifier(sourceElement);
		return idPrefix + this.sourceElement.getId();
	}

	@Override
	@JsonProperty("idOfSourceElement")
	@Deprecated
	public void setSourceElement(String idWithPrefix) {
		if (Character.isDigit(idWithPrefix.charAt(0))) {
			setSourceElement(Long.parseLong(idWithPrefix), "");
			return;
		}
		long id = GenericLinkManager.getId(idWithPrefix);
		String documentationLocationIdentifier = idWithPrefix.substring(0, 1);
		DocumentationLocation documentationLocation = DocumentationLocation
				.getDocumentationLocationFromIdentifier(documentationLocationIdentifier);
		if (this.sourceElement == null) {
			this.sourceElement = AbstractPersistenceManager.getDecisionKnowledgeElement(id, documentationLocation);
		}
		this.sourceElement.setId(id);
		this.sourceElement.setDocumentationLocation(documentationLocation);
	}

	@Override
	@Deprecated
	public String getIdOfDestinationElementWithPrefix() {
		String idPrefix = DocumentationLocation.getIdentifier(destinationElement);
		return idPrefix + this.destinationElement.getId();
	}

	@Override
	@JsonProperty("idOfDestinationElement")
	@Deprecated
	public void setDestinationElement(String idWithPrefix) {
		if (Character.isDigit(idWithPrefix.charAt(0))) {
			setDestinationElement(Long.parseLong(idWithPrefix), "");
			return;
		}
		long id = GenericLinkManager.getId(idWithPrefix);
		String documentationLocationIdentifier = idWithPrefix.substring(0, 1);
		DocumentationLocation documentationLocation = DocumentationLocation
				.getDocumentationLocationFromIdentifier(documentationLocationIdentifier);
		if (this.destinationElement == null) {
			this.destinationElement = AbstractPersistenceManager.getDecisionKnowledgeElement(id, documentationLocation);
		}
		this.destinationElement.setId(id);
		this.destinationElement.setDocumentationLocation(documentationLocation);
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
	public LinkImpl flip() {
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
	public void setDocumentationLocationOfSourceElement(String documentationLocation) {
		this.getSourceElement().setDocumentationLocation(documentationLocation);
	}

	@Override
	@JsonProperty("documentationLocationOfDestinationElement")
	public void setDocumentationLocationOfDestinationElement(String documentationLocation) {
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
}