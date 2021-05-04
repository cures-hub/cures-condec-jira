package de.uhd.ifi.se.decision.management.jira.quality.completeness;

import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.issuetype.IssueType;
import com.atlassian.jira.user.ApplicationUser;

import de.uhd.ifi.se.decision.management.jira.filtering.FilterSettings;
import de.uhd.ifi.se.decision.management.jira.filtering.FilteringManager;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeGraph;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.persistence.ConfigPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.persistence.singlelocations.JiraIssuePersistenceManager;

/**
 * Calculates the rationale coverage of requirements, code, and other software
 * artifacts (=knowledge elements). For example, calculates how many decisions
 * are linked to a requirement or how many decisions are linked to a code file.
 */
public class RationaleCoverageCalculator {

	private static final Logger LOGGER = LoggerFactory.getLogger(RationaleCompletenessCalculator.class);

	private String projectKey;
	private ApplicationUser user;
	private FilterSettings filterSettings;
	private FilteringManager filteringManager;
	private Map<KnowledgeElement, Map<KnowledgeType, Integer>> linkedElementMap = new HashMap<KnowledgeElement, Map<KnowledgeType, Integer>>();

	public RationaleCoverageCalculator(ApplicationUser user, FilterSettings filterSettings) {
		this.projectKey = filterSettings.getProjectKey();
		this.user = user;
		this.filterSettings = filterSettings;
		this.filteringManager = new FilteringManager(user, filterSettings);
	}

	private void fillLinkedElementMap(KnowledgeElement sourceElement) {
		Map<KnowledgeType, Integer> knowledgeTypeMap = new HashMap<>();
		Set<KnowledgeElement> linkedElements = sourceElement.getLinkedElements(filterSettings.getLinkDistance());
		for (KnowledgeElement linkedElement : linkedElements) {
			if (!knowledgeTypeMap.containsKey(linkedElement.getType())) {
				knowledgeTypeMap.put(linkedElement.getType(), 0);
			}
			knowledgeTypeMap.put(linkedElement.getType(), knowledgeTypeMap.get(linkedElement.getType()) + 1);
		}
		linkedElementMap.put(sourceElement, knowledgeTypeMap);
	}

	public Map<String, String> getKnowledgeElementsWithNeighborsOfOtherType(Set<String> sourceTypes,
																			KnowledgeType knowledgeType) {
		LOGGER.info("RationaleCoverageCalculator getKnowledgeElementsWithNeighborsOfOtherType");

		if (knowledgeType == null) {
			return null;
		}

		KnowledgeGraph graph = filteringManager.getSubgraphMatchingFilterSettings();
		Set<KnowledgeElement> knowledgeElements = new HashSet<>();
		for (String sourceType : sourceTypes) {
			KnowledgeType type = KnowledgeType.getKnowledgeType(sourceType);
			knowledgeElements.addAll(graph.getElements(type));
		}

		int minimumDecisionCoverage = filterSettings.getMinimumDecisionCoverage();

		String withHighLinks = "";
		String withLowLinks = "";
		String withoutLinks = "";

		for (KnowledgeElement knowledgeElement : knowledgeElements) {
			if (!linkedElementMap.containsKey(knowledgeElement)) {
				fillLinkedElementMap(knowledgeElement);
			}
			if (!linkedElementMap.get(knowledgeElement).containsKey(knowledgeType)) {
				withoutLinks += knowledgeElement.getKey() + " ";
			} else if (linkedElementMap.get(knowledgeElement).get(knowledgeType) < minimumDecisionCoverage) {
				withLowLinks += knowledgeElement.getKey() + " ";
			} else if (linkedElementMap.get(knowledgeElement).get(knowledgeType) >= minimumDecisionCoverage) {
				withHighLinks += knowledgeElement.getKey() + " ";
			}
		}

		Map<String, String> result = new LinkedHashMap<>();
		result.put("More than " + minimumDecisionCoverage + " links from selected types to " +
			knowledgeType.toString(), withHighLinks);
		result.put("Less than " + minimumDecisionCoverage + " links from selected types to " +
			knowledgeType.toString(), withLowLinks);
		result.put("No links from selected types to " + knowledgeType.toString(), withoutLinks);
		return result;
	}

	public Map<String, Integer> getNumberOfDecisionKnowledgeElementsForKnowledgeElements(Set<String> sourceTypes,
																						 KnowledgeType knowledgeType) {
		LOGGER.info("RationaleCoverageCalculator getNumberOfDecisionKnowledgeElementsForKnowledgeElements");

		if (knowledgeType == null) {
			return null;
		}

		KnowledgeGraph graph = filteringManager.getSubgraphMatchingFilterSettings();
		Set<KnowledgeElement> knowledgeElements = new HashSet<>();
		for (String sourceType : sourceTypes) {
			KnowledgeType type = KnowledgeType.getKnowledgeType(sourceType);
			knowledgeElements.addAll(graph.getElements(type));
		}

		Map<String, Integer> numberOfElementsReachable = new HashMap<>();
		for (KnowledgeElement knowledgeElement : knowledgeElements) {
			if (!linkedElementMap.containsKey(knowledgeElement)) {
				fillLinkedElementMap(knowledgeElement);
			}
			if (!linkedElementMap.get(knowledgeElement).containsKey(knowledgeType)) {
				numberOfElementsReachable.put(knowledgeElement.getKey(), 0);
			} else {
				numberOfElementsReachable.put(knowledgeElement.getKey(),
					linkedElementMap.get(knowledgeElement).get(knowledgeType));
			}
		}
		return numberOfElementsReachable;
	}
}
