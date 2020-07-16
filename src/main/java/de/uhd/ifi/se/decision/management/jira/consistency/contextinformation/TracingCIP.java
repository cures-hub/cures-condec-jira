package de.uhd.ifi.se.decision.management.jira.consistency.contextinformation;

import de.uhd.ifi.se.decision.management.jira.consistency.suggestions.LinkSuggestion;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.Link;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class TracingCIP implements ContextInformationProvider {
	private final String id = "TracingCIP_BFS";
	private final String name = "TracingCIP";
	private Collection<LinkSuggestion> linkSuggestions;

	public TracingCIP() {
		this.linkSuggestions = new ArrayList<>();
	}

	@Override
	public String getId() {
		return this.id;
	}

	@Override
	public String getName() {
		return this.name;
	}


	@Override
	public void assessRelation(KnowledgeElement baseElement, List<KnowledgeElement> knowledgeElements) {
		for (KnowledgeElement issueToTest : knowledgeElements) {
			LinkSuggestion linkSuggestion = new LinkSuggestion(baseElement, issueToTest);

			Map<String, Integer> distanceMap = new HashMap<String, Integer>();
			distanceMap.put(baseElement.getKey(), 0);
			Integer distance = searchBreadthFirst(baseElement, issueToTest, distanceMap).get(issueToTest.getKey());
			// A null value means the nodes are not connected.
			Double value = 0.;
			if (distance != null) {
				value = 1. / (distance + 1);
			}
			// Prevent a division by zero exception.
			linkSuggestion.addToScore(value, this.getName());
			this.linkSuggestions.add(linkSuggestion);

		}

	}

	private boolean wasVisited(String node, Map<String, ?> distanceMap) {
		return distanceMap.keySet().contains(node);
	}

	private Map<String, Integer> searchBreadthFirst(KnowledgeElement startNode, KnowledgeElement endNode, Map<String, Integer> distanceMap) {
		Set<KnowledgeElement> currentNodesToCheck = new HashSet<>();
		currentNodesToCheck.add(startNode);
		Set<KnowledgeElement> nextNodesToCheck = new HashSet<>();

		int maxIterations = 7;
		int iteration = 1;

		// Halt  the loop if there are either:
		// 1.: no more new nodes (=issues) to check
		// 2.: the end node was already visited
		// 3.: the maximum iterations are reached.
		while (!currentNodesToCheck.isEmpty() &&
			!wasVisited(endNode.getKey(), distanceMap) &&
			iteration < maxIterations) {
			for (KnowledgeElement nodeToCheck : currentNodesToCheck) {
				List<Link> links =  nodeToCheck.getLinks();
				//Collection<IssueLink> issueLinks = this.issueLinkManager.getIssueLinks(nodeToCheck.getId());

				Collection<KnowledgeElement> linkedKnowledge = getElementsForLinks(links);
				for (KnowledgeElement knowledgeElement : linkedKnowledge) {
					if (!wasVisited(knowledgeElement.getKey(), distanceMap) && !nextNodesToCheck.contains(knowledgeElement)) {
						nextNodesToCheck.add(knowledgeElement);
						distanceMap.put(knowledgeElement.getKey(), iteration);
					}
				}
			}
			currentNodesToCheck = nextNodesToCheck;
			nextNodesToCheck = new HashSet<>();

			iteration++;

		}

		return distanceMap;
	}

	private Set<KnowledgeElement> getElementsForLinks(Collection<Link> elementLinks) {
		Set<KnowledgeElement> issues = new HashSet<>();
		for (Link issueLink : elementLinks) {
			issues.addAll(issueLink.getBothElements());
		}
		return issues;
	}

	@Override
	public Collection<LinkSuggestion> getLinkSuggestions() {
		return linkSuggestions;
	}
}
