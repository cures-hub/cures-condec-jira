package de.uhd.ifi.se.decision.management.jira.extraction.model.impl;

import java.util.ArrayList;
import java.util.List;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.Issue;

import de.uhd.ifi.se.decision.management.jira.extraction.model.GenericLink;
import de.uhd.ifi.se.decision.management.jira.extraction.model.Sentence;
import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeElementImpl;
import de.uhd.ifi.se.decision.management.jira.model.LinkImpl;
import de.uhd.ifi.se.decision.management.jira.persistence.GenericLinkManager;

public class GenericLinkImpl extends LinkImpl implements GenericLink {

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

	public GenericLinkImpl(String idOfDestinationElement, String idOfSourceElement, String type) {
		this(idOfDestinationElement, idOfSourceElement);
		setType(type);
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
		bothLinkSides.add(this.getOpposite(this.idOfSourceElement));
		bothLinkSides.add(this.getOpposite(this.idOfDestinationElement));
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
			return source.getProject().getProjectKey().equals(target.getProject().getProjectKey());
		} catch (NullPointerException e) {
			return false;
		}
	}
}
