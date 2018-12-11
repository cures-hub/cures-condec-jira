package de.uhd.ifi.se.decision.management.jira.model;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.annotate.JsonProperty;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.link.IssueLink;

import de.uhd.ifi.se.decision.management.jira.extraction.persistence.ActiveObjectsManager;
import de.uhd.ifi.se.decision.management.jira.persistence.GenericLinkManager;
import de.uhd.ifi.se.decision.management.jira.persistence.LinkInDatabase;

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

	public LinkImpl(long idOfSourceElement, long idOfDestinationElement) {
		this.setSourceElement(idOfSourceElement);
		this.setDestinationElement(idOfDestinationElement);
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
		this(idOfDestinationElementWithPrefix, idOfSourceElementWithPrefix);
		setType(type);
	}

	public LinkImpl(LinkInDatabase linkInDatabase) {
		this();
		if (linkInDatabase.getIdOfSourceElement() != null) {
			this.setSourceElement(linkInDatabase.getIdOfSourceElement());
		}
		if (linkInDatabase.getIdOfDestinationElement() != null) {
			this.setDestinationElement(linkInDatabase.getIdOfDestinationElement());
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
		return type;
	}

	@Override
	public void setType(String type) {
		this.type = type;
	}

	@Override
	public void setSourceElement(long id) {
		if (this.sourceElement == null) {
			this.sourceElement = new DecisionKnowledgeElementImpl();
		}
		this.sourceElement.setId(id);
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
	public void setDestinationElement(long id) {
		if (this.destinationElement == null) {
			this.destinationElement = new DecisionKnowledgeElementImpl();
		}
		this.destinationElement.setId(id);
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
	public void setSourceElement(String idWithPrefix) {
		if (Character.isDigit(idWithPrefix.charAt(0))) {
			setSourceElement(Long.parseLong(idWithPrefix));
			return;
		}
		if (this.sourceElement == null) {
			this.sourceElement = instantiateElement(idWithPrefix);
		}
		long id = GenericLinkManager.getId(idWithPrefix);
		this.sourceElement.setId(id);
		this.sourceElement.setDocumentationLocation(idWithPrefix.substring(0, 1));
	}

	private DecisionKnowledgeElement instantiateElement(String elementIdWithPrefix) {
		if (elementIdWithPrefix.startsWith("s")) {
			return ActiveObjectsManager.getElementFromAO(GenericLinkManager.getId(elementIdWithPrefix));
		}
		if (elementIdWithPrefix.startsWith("i")) {
			Issue issue = ComponentAccessor.getIssueManager()
					.getIssueObject(GenericLinkManager.getId(elementIdWithPrefix));
			return new DecisionKnowledgeElementImpl(issue);
		}
		if (elementIdWithPrefix.startsWith("a")) {
			return GenericLinkManager.getIssueFromAOTable(GenericLinkManager.getId(elementIdWithPrefix));
		}
		return new DecisionKnowledgeElementImpl();
	}

	@Override
	public String getIdOfDestinationElementWithPrefix() {
		String idPrefix = DocumentationLocation.getIdentifier(destinationElement);
		return idPrefix + this.destinationElement.getId();
	}

	@Override
	@JsonProperty("idOfDestinationElement")
	public void setDestinationElement(String idWithPrefix) {
		if (Character.isDigit(idWithPrefix.charAt(0))) {
			setDestinationElement(Long.parseLong(idWithPrefix));
			return;
		}
		if (this.destinationElement == null) {
			this.destinationElement = instantiateElement(idWithPrefix);
		}
		long id = GenericLinkManager.getId(idWithPrefix);
		this.destinationElement.setId(id);
		this.destinationElement.setDocumentationLocation(idWithPrefix.substring(0, 1));
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
	public DecisionKnowledgeElement getOppositeElement(String elementIdWithPrefix) {
		long elementId = GenericLinkManager.getId(elementIdWithPrefix);
		return getOppositeElement(elementId);
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
		return new LinkImpl(this.getIdOfDestinationElementWithPrefix(), this.getIdOfSourceElementWithPrefix());
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
}