package de.uhd.ifi.se.decision.documentation.jira.model;

import de.uhd.ifi.se.decision.documentation.jira.persistence.PersistenceStrategy;
import de.uhd.ifi.se.decision.documentation.jira.persistence.StrategyProvider;
import de.uhd.ifi.se.decision.documentation.jira.view.treant.Node;

import java.util.*;

public class Graph {

	private PersistenceStrategy strategy;

	private Set<DecisionKnowledgeElement> elements;
	private Set<Link> links;
	private Map<DecisionKnowledgeElement, Set<Link>> linkedElements;
	private List<Long> containedLinkIds;

	private Node nodeStructure;

	public Graph(String projectKey) {
		StrategyProvider strategyProvider = new StrategyProvider();
		strategy = strategyProvider.getStrategy(projectKey);
		this.elements = new HashSet<DecisionKnowledgeElement>();
		this.links = new HashSet<Link>();
		this.linkedElements = new HashMap<DecisionKnowledgeElement, Set<Link>>();
	}

	public Graph(String projectKey, String rootElement) {
		this(projectKey);
		DecisionKnowledgeElement decisionRootElement = strategy.getDecisionKnowledgeElement(rootElement);
		List<Link> outwardLinks = decisionRootElement.getOutwardLinks();
		this.addLinks(outwardLinks);
		List<Link> inwardLinks = decisionRootElement.getInwardLinks();
		this.addLinks(inwardLinks);
	}

	public Graph(String projectKey, String rootElement, int linkDistance) {
		this(projectKey, rootElement);
		DecisionKnowledgeElement decisionRootElement = strategy.getDecisionKnowledgeElement(rootElement);
		nodeStructure = createNodeStructure(decisionRootElement,linkDistance,0);
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

	public Node getNodeStructure() {
		return nodeStructure;
	}

	private Node createNodeStructure(DecisionKnowledgeElement decisionKnowledgeElement, int depth, int currentDepth) {
		Node node = new Node(decisionKnowledgeElement);

		if (currentDepth + 1 < depth) {
			List<Node> nodes = new ArrayList<Node>();
			List<DecisionKnowledgeElement> children = strategy.getChildren(decisionKnowledgeElement);

			for (DecisionKnowledgeElement child : children) {
				nodes.add(createNodeStructure(child, depth, currentDepth + 1));
			}
			node.setChildren(nodes);
		}
		return node;
	}
}