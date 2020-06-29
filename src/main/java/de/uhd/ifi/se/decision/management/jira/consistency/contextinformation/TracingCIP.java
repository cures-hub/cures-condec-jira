package de.uhd.ifi.se.decision.management.jira.consistency.contextinformation;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.link.IssueLink;
import com.atlassian.jira.issue.link.IssueLinkManager;
import de.uhd.ifi.se.decision.management.jira.consistency.contextinformation.ContextInformationProvider;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class TracingCIP implements ContextInformationProvider {
	private final String id = "TracingCIP_BFS";
	private final String name = "TracingCIP";
	private IssueLinkManager issueLinkManager;

	public TracingCIP(IssueLinkManager issueLinkManager) {
		this.issueLinkManager = issueLinkManager;
	}
	public TracingCIP() {
		this.issueLinkManager = ComponentAccessor.getIssueLinkManager();
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
	public double assessRelation(Issue i1, Issue i2) {
		Map<String, Integer> distanceMap = new HashMap<String, Integer>();
		distanceMap.put(i1.getKey(), 0);
		Integer distance = searchBreadthFirst(i1, i2, distanceMap).get(i2.getKey());
		// A null value means the nodes are not connected.
		if (distance == null) {
			return 0;
		}
		// Prevent a division by zero exception.
		return 1. / (distance + 1);
	}

	private boolean wasVisited(String node, Map<String, ?> distanceMap) {
		return distanceMap.keySet().contains(node);
	}

	private Map<String, Integer> searchBreadthFirst(Issue startNode, Issue endNode, Map<String, Integer> distanceMap) {
		Set<Issue> currentNodesToCheck = new HashSet<>();
		currentNodesToCheck.add(startNode);
		Set<Issue> nextNodesToCheck = new HashSet<>();

		int maxIterations = 7;
		int iteration = 1;

		// Halt  the loop if there are either:
		// 1.: no more new nodes (=issues) to check
		// 2.: the end node was already visited
		// 3.: the maximum iterations are reached.
		while (!currentNodesToCheck.isEmpty() &&
			!wasVisited(endNode.getKey(), distanceMap) &&
			iteration < maxIterations) {
			for (Issue nodeToCheck : currentNodesToCheck) {
				Collection<IssueLink> issueLinks = this.issueLinkManager.getIssueLinks(nodeToCheck.getId().longValue());

				Collection<Issue> linkedIssues = getAllIssuesForIssueLinks(issueLinks);
				for (Issue linkedIssue : linkedIssues) {
					if (!wasVisited(linkedIssue.getKey(), distanceMap) && !nextNodesToCheck.contains(linkedIssue)) {
						nextNodesToCheck.add(linkedIssue);
						distanceMap.put(linkedIssue.getKey(), iteration);
					}
				}
			}
			currentNodesToCheck = nextNodesToCheck;
			nextNodesToCheck = new HashSet<>();

			iteration++;

		}

		return distanceMap;
	}

	private Set<Issue> getAllIssuesForIssueLinks(Collection<IssueLink> issueLinks) {
		Set<Issue> issues = new HashSet<>();
		for (IssueLink issueLink : issueLinks){
			issues.add(ComponentAccessor.getIssueManager().getIssueObject(issueLink.getSourceId()));
			issues.add(ComponentAccessor.getIssueManager().getIssueObject(issueLink.getDestinationId()));
		}
		return issues;
	}

}
