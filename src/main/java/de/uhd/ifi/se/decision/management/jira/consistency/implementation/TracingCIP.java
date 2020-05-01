package de.uhd.ifi.se.decision.management.jira.consistency.implementation;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.link.IssueLinkManager;
import de.uhd.ifi.se.decision.management.jira.consistency.ContextInformationProvider;

import java.util.*;

public class TracingCIP implements ContextInformationProvider {
	private String id = "TracingCIP_BFS";
	private String name = "TracingCIP";
	private IssueLinkManager issueLinkManager = ComponentAccessor.getIssueLinkManager();

	@Override
	public String getId() {
		return this.id;
	}

	@Override
	public String getName() {
		return this.name;
	}


	@Override
	public double assessRelation(Issue i1, Issue i2) {
		Map<Issue, Integer> distanceMap = new HashMap<Issue, Integer>();
		distanceMap.put(i1, 0);
		Integer distance = searchBreadthFirst(i1, i2, distanceMap).get(i2);
		// A null value means the nodes are not connected.
		if (distance == null) {
			return 0;
		}
		// Prevent a division by zero exception.
		if (distance == 0) {
			return 1;
		} else {
			return 1. / distance;
		}
	}

	private boolean wasVisited(Issue node, Map<Issue, ?> distanceMap) {
		return distanceMap.keySet().contains(node);
	}

	private Map<Issue, Integer> searchBreadthFirst(Issue startNode, Issue endNode, Map<Issue, Integer> distanceMap) {
		Set<Issue> currentNodesToCheck = new HashSet<>();
		currentNodesToCheck.add(startNode);
		Set<Issue> nextNodesToCheck = new HashSet<>();

		int maxIterations = 10;
		int iteration = 1;

		// Halt  the loop if there are either:
		// 1.: no more new nodes (=issues) to check
		// 2.: the end node was already visited
		// 3.: the maximum iterations are reached.
		while (!currentNodesToCheck.isEmpty() &&
			!wasVisited(endNode, distanceMap) &&
			iteration < maxIterations) {
			for (Issue nodeToCheck : currentNodesToCheck) {
				Collection<Issue> linkedIssues = this.issueLinkManager.getLinkCollectionOverrideSecurity(nodeToCheck).getAllIssues();
				for (Issue linkedIssue : linkedIssues) {
					if (!wasVisited(linkedIssue, distanceMap) && !nextNodesToCheck.contains(linkedIssue)) {
						nextNodesToCheck.add(linkedIssue);
						distanceMap.put(linkedIssue, iteration);
					}
				}
			}
			currentNodesToCheck = nextNodesToCheck;
			nextNodesToCheck = new HashSet<>();

			iteration++;

		}

		return distanceMap;
	}

}
