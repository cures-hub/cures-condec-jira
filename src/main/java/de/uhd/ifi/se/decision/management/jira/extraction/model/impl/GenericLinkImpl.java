package de.uhd.ifi.se.decision.management.jira.extraction.model.impl;

import java.util.ArrayList;
import java.util.List;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.Issue;

import de.uhd.ifi.se.decision.management.jira.extraction.model.GenericLink;
import de.uhd.ifi.se.decision.management.jira.extraction.model.Sentence;
import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeElementImpl;

public class GenericLinkImpl implements GenericLink {

	private long id;
	private String idOfSourceElement;
	private String idOfDestinationElement;
	private String type;

	public GenericLinkImpl() {
		this.type = "";
	}

	public GenericLinkImpl(String idOfDestinationElement, String idOfSourceElement) {
		this();
		this.idOfDestinationElement = idOfDestinationElement;
		this.idOfSourceElement = idOfSourceElement;
	}

	@Override
	public long getId() {
		return this.id;
	}

	@Override
	public void setId(long id) {
		this.id = id;
	}

	@Override
	public String getType() {
		return this.type;
	}

	@Override
	public void setType(String type) {
		this.type = type;
	}

	@Override
	public String getIdOfSourceElement() {
		return this.idOfSourceElement;
	}

	@Override
	public void setIdOfSourceElement(String idOfSourceElement) {
		this.idOfSourceElement = idOfSourceElement;
	}

	@Override
	public String getIdOfDestinationElement() {
		return this.idOfDestinationElement;
	}

	@Override
	public void setIdOfDestinationElement(String idOfDestinationElement) {
		this.idOfDestinationElement = idOfDestinationElement;
	}

	public String toString() {
		return this.idOfSourceElement + " to " + this.idOfDestinationElement;
	}

	@Override
	public DecisionKnowledgeElement getOpposite(String oppositeElementId) {
		if (oppositeElementId.startsWith("s")) {
			if (this.getIdOfSourceElement().equals(oppositeElementId)) {
				if (this.getIdOfDestinationElement().startsWith("s")) {
					return new SentenceImpl(getDesitantionIdAsLong());
				} else {
					Issue issue = ComponentAccessor.getIssueManager().getIssueObject(getDesitantionIdAsLong());
					return new DecisionKnowledgeElementImpl(issue);
				}
			}
			if (this.getIdOfDestinationElement().equals(oppositeElementId)) {
				if (this.getIdOfSourceElement().startsWith("s")) {
					return new SentenceImpl(getSourceIdAsLong());
				} else {
					Issue issue = ComponentAccessor.getIssueManager().getIssueObject(getSourceIdAsLong());
					return new DecisionKnowledgeElementImpl(issue);
				}
			}
		}
		if (oppositeElementId.startsWith("i")) {
			if (this.getIdOfSourceElement().equals(oppositeElementId)) {
				if (this.getIdOfDestinationElement().startsWith("s")) {
					return new SentenceImpl(getDesitantionIdAsLong());
				} else {
					Issue issue = ComponentAccessor.getIssueManager()
							.getIssueObject((long) Integer.parseInt(oppositeElementId.substring(1)));
					return new DecisionKnowledgeElementImpl(issue);
				}
			}
			if (this.getIdOfDestinationElement().equals(oppositeElementId)) {
				if (this.getIdOfSourceElement().startsWith("s")) {
					return new SentenceImpl(getSourceIdAsLong());
				} else {
					Issue issue = ComponentAccessor.getIssueManager().getIssueObject(getSourceIdAsLong());
					return new DecisionKnowledgeElementImpl(issue);
				}
			}
		}
		return null;
	}

	@Override
	public List<DecisionKnowledgeElement> getBothElements() throws NullPointerException {
		List<DecisionKnowledgeElement> bothLinkSides = new ArrayList<>();
		bothLinkSides.add(this.getOpposite(this.idOfSourceElement));
		bothLinkSides.add(this.getOpposite(this.idOfDestinationElement));
		return bothLinkSides;
	}

	private long getDesitantionIdAsLong() {
		return (long) Integer.parseInt(this.getIdOfDestinationElement().substring(1));
	}

	private long getSourceIdAsLong() {
		return (long) Integer.parseInt(this.getIdOfSourceElement().substring(1));
	}

}
