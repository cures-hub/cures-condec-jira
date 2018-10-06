package de.uhd.ifi.se.decision.management.jira.extraction.model.impl;

import java.util.ArrayList;
import java.util.List;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.Issue;
import de.uhd.ifi.se.decision.management.jira.extraction.model.GenericLink;
import de.uhd.ifi.se.decision.management.jira.extraction.model.Sentence;
import de.uhd.ifi.se.decision.management.jira.extraction.persistence.LinkBetweenDifferentEntitiesEntity;
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

	public GenericLinkImpl(LinkBetweenDifferentEntitiesEntity aoElement) {
		this();
		this.idOfDestinationElement = aoElement.getIdOfDestinationElement();
		this.idOfSourceElement = aoElement.getIdOfSourceElement();
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
	public DecisionKnowledgeElement getOpposite(String currentElementId) {
		if (currentElementId.startsWith("s")) {
			if (this.getIdOfSourceElement().equals(currentElementId)) {
				if (this.getIdOfDestinationElement().startsWith("s")) {
					return new SentenceImpl(getDesitantionIdAsLong());
				}
				if (this.getIdOfDestinationElement().startsWith("i")) {
					Issue issue = ComponentAccessor.getIssueManager().getIssueObject(getDesitantionIdAsLong());
					return new DecisionKnowledgeElementImpl(issue);
				}
			}
			if (this.getIdOfDestinationElement().equals(currentElementId)) {
				if (this.getIdOfSourceElement().startsWith("s")) {
					return new SentenceImpl(getSourceIdAsLong());
				}
				if (this.getIdOfSourceElement().startsWith("i")) {
					Issue issue = ComponentAccessor.getIssueManager().getIssueObject(getSourceIdAsLong());
					return new DecisionKnowledgeElementImpl(issue);
				}
			}
		}
		if (currentElementId.startsWith("i")) {
			if (this.getIdOfSourceElement().equals(currentElementId)) {
				if (this.getIdOfDestinationElement().startsWith("s")) {
					return new SentenceImpl(getDesitantionIdAsLong());
				} 
				if (this.getIdOfDestinationElement().startsWith("i")) {
					Issue issue = ComponentAccessor.getIssueManager()
							.getIssueObject((long) Integer.parseInt(currentElementId.substring(1)));
					return new DecisionKnowledgeElementImpl(issue);
				}
			}
			if (this.getIdOfDestinationElement().equals(currentElementId)) {
				if (this.getIdOfSourceElement().startsWith("s")) {
					return new SentenceImpl(getSourceIdAsLong());
				} 
				if (this.getIdOfSourceElement().startsWith("i")) {
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
		if (source instanceof Sentence) {
			if (!((Sentence) source).getProjectKey().equals(target.getProject().getProjectKey())) {
				return false;
			}
		}
		return true;
	}

}
