package de.uhd.ifi.se.decision.documentation.jira.model;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Graph {

	private Set<DecisionKnowledgeElement> elements;
	private Set<Link> links;
	private Map<DecisionKnowledgeElement, Set<Link>> linkedElements;

	public Graph() {
		this.elements = new HashSet<>();
		this.links = new HashSet<>();
		this.linkedElements = new HashMap<>();
	}

	public Graph(DecisionKnowledgeElement rootElement) {
		this();
		// TODO Build graph from root element
	}

	public Graph(DecisionKnowledgeElement rootElement, int linkDistance) {
		this(rootElement);
		// TODO Build graph from root element
	}

	public boolean addElement(DecisionKnowledgeElement element) {
		return elements.add(element);
	}

	public boolean addElements(Collection<DecisionKnowledgeElement> elements) {
		return this.elements.addAll(elements);
	}

	public boolean removeElement(DecisionKnowledgeElement element) {
		return elements.remove(element);
	}

	public boolean addLink(Link link) {
		if (!links.add(link))
			return false;

		DecisionKnowledgeElement ingoingElement = link.getIngoingElement();
		DecisionKnowledgeElement outgoingElement = link.getOutgoingElement();

		linkedElements.putIfAbsent(ingoingElement, new HashSet<>());
		linkedElements.putIfAbsent(outgoingElement, new HashSet<>());

		linkedElements.get(ingoingElement).add(link);
		linkedElements.get(outgoingElement).add(link);

		return true;
	}

	public boolean addLink(DecisionKnowledgeElement element1, DecisionKnowledgeElement element2) {
		return false;
	}

	public Set<Link> getLinks() {
		return links;
	}

	public void setLinks(Set<Link> links) {
		this.links = links;
	}

	public Set<DecisionKnowledgeElement> getElements() {
		return elements;
	}

	public void setElements(Set<DecisionKnowledgeElement> elements) {
		this.elements = elements;
	}

	public Map<DecisionKnowledgeElement, Set<Link>> getLinkedElements() {
		return linkedElements;
	}

	public void setLinkedElements(Map<DecisionKnowledgeElement, Set<Link>> linkedElements) {
		this.linkedElements = linkedElements;
	}
}
