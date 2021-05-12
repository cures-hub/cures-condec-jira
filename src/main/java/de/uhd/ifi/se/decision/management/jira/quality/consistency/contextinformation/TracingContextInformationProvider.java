package de.uhd.ifi.se.decision.management.jira.quality.consistency.contextinformation;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.Link;
import de.uhd.ifi.se.decision.management.jira.quality.consistency.suggestions.LinkSuggestion;

/**
 * Uses existing tracing links between {@link KnowledgeElement}s for rating a
 * relation. {@link Link}s can be created manually or automatically during
 * development. This provider assumes that a {@link KnowledgeElement} that
 * traces to another element has a close relation to this element. (Miesbauer
 * and Weinreich, 2012)
 */
public class TracingContextInformationProvider extends ContextInformationProvider {

	@Override
	public String getId() {
		return "TracingCIP_BFS";
	}

	@Override
	public double assessRelation(KnowledgeElement baseElement, KnowledgeElement knowledgeElement) {
		LinkSuggestion linkSuggestion = new LinkSuggestion(baseElement, knowledgeElement);
		Integer distance = search(baseElement, knowledgeElement);
		// A null value means the nodes are not connected.
		Double value = 0.;
		if (distance != null) {
			value = 1. / (distance + 1);
		}
		// Prevent a division by zero exception.
		linkSuggestion.addToScore(value, this.getName());
		this.linkSuggestions.add(linkSuggestion);
		return value;
	}

	private boolean wasVisited(String node, Map<String, ?> distanceMap) {
		return distanceMap.keySet().contains(node);
	}

	/**
	 * This method uses the breadth first algorithm to search for the shortest path
	 * between 2 nodes
	 *
	 * @param startNode
	 * @param endNode
	 * @return
	 */
	private Integer search(KnowledgeElement startNode, KnowledgeElement endNode) {
		Map<String, Integer> distanceMap = new HashMap<String, Integer>();
		distanceMap.put(startNode.getKey(), 0);
		Set<KnowledgeElement> currentNodesToCheck = new HashSet<>();
		currentNodesToCheck.add(startNode);
		Set<KnowledgeElement> nextNodesToCheck = new HashSet<>();

		int maxIterations = 7;
		int iteration = 1;

		// Halt the loop if there are either:
		// 1.: no more new nodes (=issues) to check
		// 2.: the end node was already visited
		// 3.: the maximum iterations are reached.
		while (!currentNodesToCheck.isEmpty() && !wasVisited(endNode.getKey(), distanceMap)
				&& iteration < maxIterations) {
			for (KnowledgeElement nodeToCheck : currentNodesToCheck) {
				Set<Link> links = nodeToCheck.getLinks();
				// Collection<IssueLink> issueLinks =
				// this.issueLinkManager.getIssueLinks(nodeToCheck.getId());

				Collection<KnowledgeElement> linkedKnowledge = getElementsForLinks(links);
				for (KnowledgeElement knowledgeElement : linkedKnowledge) {
					if (!wasVisited(knowledgeElement.getKey(), distanceMap)
							&& !nextNodesToCheck.contains(knowledgeElement)) {
						nextNodesToCheck.add(knowledgeElement);
						distanceMap.put(knowledgeElement.getKey(), iteration);
					}
				}
			}
			currentNodesToCheck = nextNodesToCheck;
			nextNodesToCheck = new HashSet<>();

			iteration++;

		}

		return distanceMap.get(endNode.getKey());
	}

	private Set<KnowledgeElement> getElementsForLinks(Collection<Link> elementLinks) {
		Set<KnowledgeElement> knowledgeElements = new HashSet<>();
		for (Link issueLink : elementLinks) {
			knowledgeElements.addAll(issueLink.getBothElements());
		}
		return knowledgeElements;
	}
}
