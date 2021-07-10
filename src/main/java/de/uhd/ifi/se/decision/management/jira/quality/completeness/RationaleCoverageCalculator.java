package de.uhd.ifi.se.decision.management.jira.quality.completeness;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atlassian.jira.user.ApplicationUser;

import de.uhd.ifi.se.decision.management.jira.filtering.FilterSettings;
import de.uhd.ifi.se.decision.management.jira.filtering.FilteringManager;
import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeProject;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeGraph;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;

/**
 * Calculates the rationale coverage of requirements, code, and other software
 * artifacts (=knowledge elements). For example, calculates how many decisions
 * are linked to a requirement or how many decisions are linked to a code file.
 */
public class RationaleCoverageCalculator {

	@JsonIgnore
	private FilterSettings filterSettings;
	@JsonIgnore
	private FilteringManager filteringManager;
	@JsonIgnore
	private Map<KnowledgeElement, Map<KnowledgeType, Integer>> linkedElementMap = new HashMap<>();

	private Map<String, Integer> decisionsPerSelectedJiraIssue = new HashMap<>();
	private Map<String, Integer> issuesPerSelectedJiraIssue = new HashMap<>();
	private Map<String, String> decisionDocumentedForSelectedJiraIssue = new HashMap<>();
	private Map<String, String> issueDocumentedForSelectedJiraIssue = new HashMap<>();

	@JsonIgnore
	private static final Logger LOGGER = LoggerFactory.getLogger(RationaleCompletenessCalculator.class);

	public RationaleCoverageCalculator(String projectKey) {
		this.filterSettings = new FilterSettings(projectKey, "");
	}

	public RationaleCoverageCalculator(FilterSettings filterSettings, String sourceKnowledgeTypesString) {
		this.filterSettings = filterSettings;
		this.filteringManager = new FilteringManager(filterSettings);

		Set<String> sourceKnowledgeTypes;
		if (sourceKnowledgeTypesString.isEmpty()) {
			sourceKnowledgeTypes = new DecisionKnowledgeProject(filterSettings.getProjectKey())
					.getNamesOfKnowledgeTypes();
		} else {
			sourceKnowledgeTypes = new HashSet<>(Arrays.asList(sourceKnowledgeTypesString.split(",")));
		}

		if (!sourceKnowledgeTypes.isEmpty()) {
			fillRationaleCoverageCalculator(sourceKnowledgeTypes);
		}
	}

	private void fillRationaleCoverageCalculator(Set<String> sourceKnowledgeTypes) {
		this.decisionsPerSelectedJiraIssue = calculateNumberOfDecisionKnowledgeElementsForKnowledgeElements(
				sourceKnowledgeTypes, KnowledgeType.DECISION);
		this.issuesPerSelectedJiraIssue = calculateNumberOfDecisionKnowledgeElementsForKnowledgeElements(
				sourceKnowledgeTypes, KnowledgeType.ISSUE);
		this.decisionDocumentedForSelectedJiraIssue = calculateKnowledgeElementsWithNeighborsOfOtherType(
				sourceKnowledgeTypes, KnowledgeType.DECISION);
		this.issueDocumentedForSelectedJiraIssue = calculateKnowledgeElementsWithNeighborsOfOtherType(
				sourceKnowledgeTypes, KnowledgeType.ISSUE);
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

	private String getKnowledgeElementName(KnowledgeElement knowledgeElement) {
		if (knowledgeElement.getType() == KnowledgeType.CODE) {
			return filterSettings.getProjectKey() + '-' + knowledgeElement.getDescription();
		} else {
			return knowledgeElement.getKey();
		}
	}

	private Set<KnowledgeElement> getKnowledgeElementsOfSourceTypes(Set<String> sourceTypes) {
		Set<KnowledgeElement> knowledgeElements = new HashSet<>();
		KnowledgeGraph graph = filteringManager.getFilteredGraph();
		for (String sourceType : sourceTypes) {
			KnowledgeType type = KnowledgeType.getKnowledgeType(sourceType);
			if (type == KnowledgeType.OTHER || type == KnowledgeType.CODE) {
				knowledgeElements.addAll(graph.getElements(sourceType));
			} else {
				knowledgeElements.addAll(graph.getElements(type));
			}
		}

		return knowledgeElements;
	}

	private Map<String, String> calculateKnowledgeElementsWithNeighborsOfOtherType(Set<String> sourceTypes,
			KnowledgeType knowledgeType) {
		LOGGER.info("RationaleCoverageCalculator getKnowledgeElementsWithNeighborsOfOtherType");

		if (knowledgeType == null) {
			return null;
		}

		Set<KnowledgeElement> knowledgeElements = getKnowledgeElementsOfSourceTypes(sourceTypes);

		int minimumDecisionCoverage = filterSettings.getDefinitionOfDone().getMinimumDecisionsWithinLinkDistance();

		StringBuilder withHighLinks = new StringBuilder();
		StringBuilder withLowLinks = new StringBuilder();
		StringBuilder withoutLinks = new StringBuilder();

		for (KnowledgeElement knowledgeElement : knowledgeElements) {
			if (!linkedElementMap.containsKey(knowledgeElement)) {
				fillLinkedElementMap(knowledgeElement);
			}
			if (!linkedElementMap.get(knowledgeElement).containsKey(knowledgeType)) {
				withoutLinks.append(getKnowledgeElementName(knowledgeElement)).append(" ");
			} else if (linkedElementMap.get(knowledgeElement).get(knowledgeType) < minimumDecisionCoverage) {
				withLowLinks.append(getKnowledgeElementName(knowledgeElement)).append(" ");
			} else if (linkedElementMap.get(knowledgeElement).get(knowledgeType) >= minimumDecisionCoverage) {
				withHighLinks.append(getKnowledgeElementName(knowledgeElement)).append(" ");
			}
		}

		Map<String, String> result = new LinkedHashMap<>();
		result.put("More than " + minimumDecisionCoverage + " " + knowledgeType + "s reachable",
				withHighLinks.toString());
		result.put("Less than " + minimumDecisionCoverage + " " + knowledgeType + "s reachable",
				withLowLinks.toString());
		result.put("No " + knowledgeType + "s reachable", withoutLinks.toString());
		return result;
	}

	private Map<String, Integer> calculateNumberOfDecisionKnowledgeElementsForKnowledgeElements(Set<String> sourceTypes,
			KnowledgeType knowledgeType) {
		LOGGER.info("RationaleCoverageCalculator getNumberOfDecisionKnowledgeElementsForKnowledgeElements");

		if (knowledgeType == null) {
			return null;
		}

		Set<KnowledgeElement> knowledgeElements = getKnowledgeElementsOfSourceTypes(sourceTypes);

		Map<String, Integer> numberOfElementsReachable = new HashMap<>();
		for (KnowledgeElement knowledgeElement : knowledgeElements) {
			if (!linkedElementMap.containsKey(knowledgeElement)) {
				fillLinkedElementMap(knowledgeElement);
			}
			if (!linkedElementMap.get(knowledgeElement).containsKey(knowledgeType)) {
				numberOfElementsReachable.put(getKnowledgeElementName(knowledgeElement), 0);
			} else {
				numberOfElementsReachable.put(getKnowledgeElementName(knowledgeElement),
						linkedElementMap.get(knowledgeElement).get(knowledgeType));
			}
		}
		return numberOfElementsReachable;
	}

	public int calculateNumberOfDecisionKnowledgeElementsForKnowledgeElement(KnowledgeElement knowledgeElement,
			KnowledgeType knowledgeType) {
		if (knowledgeElement.getLinks().isEmpty()) {
			if (knowledgeElement.getType() == knowledgeType) {
				return 1;
			} else {
				return 0;
			}
		}

		int numberOfElementsReachable;
		if (!linkedElementMap.containsKey(knowledgeElement)) {
			fillLinkedElementMap(knowledgeElement);
		}
		if (!linkedElementMap.get(knowledgeElement).containsKey(knowledgeType)) {
			numberOfElementsReachable = 0;
		} else {
			numberOfElementsReachable = linkedElementMap.get(knowledgeElement).get(knowledgeType);
		}
		return numberOfElementsReachable;
	}

	@JsonProperty("decisionsPerSelectedJiraIssue")
	public Map<String, Integer> getDecisionsPerSelectedJiraIssue() {
		return decisionsPerSelectedJiraIssue;
	}

	@JsonProperty("issuesPerSelectedJiraIssue")
	public Map<String, Integer> getIssuesPerSelectedJiraIssue() {
		return issuesPerSelectedJiraIssue;
	}

	@JsonProperty("decisionDocumentedForSelectedJiraIssue")
	public Map<String, String> getDecisionDocumentedForSelectedJiraIssue() {
		return decisionDocumentedForSelectedJiraIssue;
	}

	@JsonProperty("issueDocumentedForSelectedJiraIssue")
	public Map<String, String> getIssueDocumentedForSelectedJiraIssue() {
		return issueDocumentedForSelectedJiraIssue;
	}
}
