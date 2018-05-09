package de.uhd.ifi.se.decision.documentation.jira.model;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Graph {

	private Set<DecisionKnowledgeElement> elements;
	private Set<Link> links;
	private Map<DecisionKnowledgeElement, Set<Link>> linkedElements;

	public Graph() {
		this.elements = new HashSet<DecisionKnowledgeElement>();
		this.links = new HashSet<Link>();
		this.linkedElements = new HashMap<DecisionKnowledgeElement, Set<Link>>();
	}

	public Graph(DecisionKnowledgeElement rootElement) {
		this();
		List<Link> outwardLinks = rootElement.getOutwardLinks();
		this.addLinks(outwardLinks);
		List<Link> inwardLinks = rootElement.getInwardLinks();
		this.addLinks(inwardLinks);
	}

	public Graph(DecisionKnowledgeElement rootElement, int linkDistance) {
		this(rootElement);
		// TODO Build graph from root element
	}

	public boolean addElement(DecisionKnowledgeElement element) {
		if(element == null){
			return false;
		}
		return elements.add(element);
	}

	public boolean addElements(Collection<DecisionKnowledgeElement> elements) {
		if(elements == null){
			return false;
		}
		return this.elements.addAll(elements);
	}

	public boolean removeElement(DecisionKnowledgeElement element) {
		if(element == null){
			return false;
		}
		return elements.remove(element);
	}

	public boolean addLink(Link link) {
		if(link == null){
			return false;
		}
		if (!links.add(link))
			return false;

		DecisionKnowledgeElement ingoingElement = link.getIngoingElement();
		DecisionKnowledgeElement outgoingElement = link.getOutgoingElement();

		linkedElements.putIfAbsent(ingoingElement, new HashSet<Link>());
		linkedElements.putIfAbsent(outgoingElement, new HashSet<Link>());

		linkedElements.get(ingoingElement).add(link);
		linkedElements.get(outgoingElement).add(link);

		return true;
	}

	public boolean addLink(DecisionKnowledgeElement ingoingElement, DecisionKnowledgeElement outgoingElement) {
		if(ingoingElement == null || outgoingElement == null){
			return false;
		}
		return addLink(new LinkImpl(ingoingElement, outgoingElement));
	}

	public boolean addLinks(List<Link> links) {
		if(links == null || links.size() == 0){
			return false;
		}
		for (Link link : links) {
			this.addLink(link);
		}
		return true;
	}

	public Set<Link> getLinks() {
		return links;
	}

	public void setLinks(Set<Link> links) {
		if(links == null){
			return;
		}
		this.links = links;
	}

	public Set<DecisionKnowledgeElement> getElements() {
		return elements;
	}

	public void setElements(Set<DecisionKnowledgeElement> elements) {
		if(elements == null){
			return;
		}
		this.elements = elements;
	}

	public Map<DecisionKnowledgeElement, Set<Link>> getLinkedElements() {
		return linkedElements;
	}

	public void setLinkedElements(Map<DecisionKnowledgeElement, Set<Link>> linkedElements) {
		if(linkedElements == null){
			return;
		}
		this.linkedElements = linkedElements;
	}
}