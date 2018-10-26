package de.uhd.ifi.se.decision.management.jira.model;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.annotate.JsonProperty;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.link.IssueLink;

import de.uhd.ifi.se.decision.management.jira.extraction.model.Sentence;
import de.uhd.ifi.se.decision.management.jira.extraction.model.impl.SentenceImpl;
import de.uhd.ifi.se.decision.management.jira.extraction.persistence.LinkBetweenDifferentEntitiesEntity;
import de.uhd.ifi.se.decision.management.jira.persistence.GenericLinkManager;

/**
 * Model class for links between decision knowledge elements
 */
public class LinkImpl implements Link {

	private long id;
	private String type;
	private DecisionKnowledgeElement sourceElement;
	private DecisionKnowledgeElement destinationElement;
	private String typeOfSourceElement;
	private String typeOfDestinationElement;

	public LinkImpl() {
		this.sourceElement = new DecisionKnowledgeElementImpl();
		this.destinationElement = new DecisionKnowledgeElementImpl();
	}

	public LinkImpl(DecisionKnowledgeElement sourceElement, DecisionKnowledgeElement destinationElement) {
		this.sourceElement = sourceElement;
		this.destinationElement = destinationElement;
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
	
    public LinkImpl(String idOfDestinationElement, String idOfSourceElement) {
    	this.type = "";
        this.typeOfDestinationElement = idOfDestinationElement;
        this.typeOfSourceElement = idOfSourceElement;
    }

    public LinkImpl(String idOfDestinationElement, String idOfSourceElement, String type) {
        this(idOfDestinationElement, idOfSourceElement);
        setType(type);
    }

	public LinkImpl(LinkBetweenDifferentEntitiesEntity link) {
		this();
		this.id = link.getId();
		this.type = link.getType();
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
	@JsonProperty("idOfSourceElement")
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
	@JsonProperty("idOfDestinationElement")
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
	public String getIdOfSourceElement() {
		return this.typeOfSourceElement;
	}

	@Override
	public void setIdOfSourceElement(String idOfSourceElement) {
		this.typeOfSourceElement = idOfSourceElement;
	}

	@Override
	public String getIdOfDestinationElement() {
		return this.typeOfDestinationElement;
	}

	@Override
	public void setIdOfDestinationElement(String idOfDestinationElement) {
		this.typeOfDestinationElement = idOfDestinationElement;
	}

	public String toString() {
		return this.typeOfSourceElement + " to " + this.typeOfDestinationElement;
	}
	
	@Override
	public DecisionKnowledgeElement getOpposite(String currentElementId) {
		if (this.getIdOfSourceElement().equals(currentElementId)) {
			return handleOppositeLink(this.getIdOfDestinationElement());
		}
		if (this.getIdOfDestinationElement().equals(currentElementId)) {
			return handleOppositeLink(this.getIdOfSourceElement());
		}
		return null;
	}

	private DecisionKnowledgeElement handleOppositeLink(String oppositeId) {
		if (oppositeId.startsWith("s")) {
			return new SentenceImpl(cutId(oppositeId));
		}
		if (oppositeId.startsWith("i")) {
			Issue issue = ComponentAccessor.getIssueManager().getIssueObject(cutId(oppositeId));
			return new DecisionKnowledgeElementImpl(issue);
		}
		if (oppositeId.startsWith("a")) {
			return GenericLinkManager.getIssueFromAOTable(cutId(oppositeId));
		}
		return null;
	}

	@Override
	public List<DecisionKnowledgeElement> getBothElements() throws NullPointerException {
		List<DecisionKnowledgeElement> bothLinkSides = new ArrayList<>();
		bothLinkSides.add(this.getOpposite(this.typeOfSourceElement));
		bothLinkSides.add(this.getOpposite(this.typeOfDestinationElement));
		return bothLinkSides;
	}

	private long cutId(String id) {
		return (long) Integer.parseInt(id.substring(1));
	}

	@Override
	public boolean isValid() {
		DecisionKnowledgeElement source;
		DecisionKnowledgeElement target;
		try {
			source = this.getBothElements().get(0);
			target = this.getBothElements().get(1);
		} catch (NullPointerException e) {
			return false;
		}
		// I don't like this, I'd rather do an if, but codacy cries in this case.
		// This checks if both ends are in the same project.
		return !(source instanceof Sentence
				&& (!((Sentence) source).getProjectKey().equals(target.getProject().getProjectKey())));
	}

	@Override
	public boolean isInterProjectLink() {
		try {
			DecisionKnowledgeElement source = this.getBothElements().get(0);
			DecisionKnowledgeElement target = this.getBothElements().get(1);
			return !source.getProject().getProjectKey().equals(target.getProject().getProjectKey());
		} catch (NullPointerException e) {
			return false;
		}
	}
}