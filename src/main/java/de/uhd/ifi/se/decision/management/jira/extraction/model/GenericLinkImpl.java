package de.uhd.ifi.se.decision.management.jira.extraction.model;

import java.util.ArrayList;
import java.util.List;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.Issue;

import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeElementImpl;

public class GenericLinkImpl implements GenericLink {
	
	private long id;
	private String idOfSourceElement;
	private String idOfDestinationElement;
	private String type;
	
	
	public GenericLinkImpl() {
		// TODO Auto-generated constructor stub
	}
	
	public GenericLinkImpl(String idOfDestinationElement, String idOfSourceElement) {
		this();
		this.idOfDestinationElement=idOfDestinationElement;
		this.idOfSourceElement = idOfSourceElement;
	}


	@Override
	public long getId() {
		return this.id;
	}

	@Override
	public void setId(long id) {
		this.id=id;
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
	public DecisionKnowledgeElement getElement(String elementNOTToGet) {
		if(elementNOTToGet.startsWith("s")) { 
			Sentence sentence = null;
			if(this.getIdOfSourceElement().equals(elementNOTToGet)) {
				if(this.getIdOfDestinationElement().startsWith("s")) {
					return new Sentence(Integer.parseInt(this.getIdOfDestinationElement().substring(1)));
				}else {
					Issue issue = ComponentAccessor.getIssueManager().getIssueObject((long)Integer.parseInt(this.getIdOfDestinationElement().substring(1)));
					return new DecisionKnowledgeElementImpl(issue);
				}
			}
			if(this.getIdOfDestinationElement().equals(elementNOTToGet)) {
				if(this.getIdOfSourceElement().startsWith("s")) {
					return new Sentence(Integer.parseInt(this.getIdOfSourceElement().substring(1)));
				}else {
					long id = (long)Integer.parseInt(idOfSourceElement.substring(1));
					Issue issue = ComponentAccessor.getIssueManager().getIssueObject(id);
					return new DecisionKnowledgeElementImpl(issue);
				}
			}
			return sentence;
		}
		if(elementNOTToGet.startsWith("i")) {
			if(this.getIdOfSourceElement().equals(elementNOTToGet)) {
				if(this.getIdOfDestinationElement().startsWith("s")) {
					return new Sentence(Integer.parseInt(this.getIdOfDestinationElement().substring(1)));
				}else {
					Issue issue = ComponentAccessor.getIssueManager().getIssueObject((long)Integer.parseInt(elementNOTToGet.substring(1)));
					return new DecisionKnowledgeElementImpl(issue);
				}
			}
			if(this.getIdOfDestinationElement().equals(elementNOTToGet)) {
				if(this.getIdOfSourceElement().startsWith("s")) {
					return new Sentence(Integer.parseInt(this.getIdOfSourceElement().substring(1)));
				}else {
					long id = (long)Integer.parseInt(idOfSourceElement.substring(1));
					Issue issue = ComponentAccessor.getIssueManager().getIssueObject(id);
					return new DecisionKnowledgeElementImpl(issue);
				}
			}
			
		}
		
		return null;
	}

	@Override
	public List<DecisionKnowledgeElement> getBothElements() {
		List<DecisionKnowledgeElement> bothLinkSides = new ArrayList<>();
		bothLinkSides.add(this.getElement(this.idOfSourceElement));
		bothLinkSides.add(this.getElement(this.idOfDestinationElement));
		return bothLinkSides;
	}

}
