package de.uhd.ifi.se.decision.management.jira.metric;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.xml.bind.annotation.XmlElement;

import de.uhd.ifi.se.decision.management.jira.filtering.FilterSettings;
import de.uhd.ifi.se.decision.management.jira.filtering.FilteringManager;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeGraph;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;

/**
 * Calculates the coverage of requirements, code, and other software artifacts
 * (=knowledge elements) with a specific decision knowledge type, e.g.
 * {@link KnowledgeType#ISSUE} or {@link KnowledgeType#DECISION}. For example,
 * calculates how many decisions are reachable from a requirement or how many
 * decisions are reachable from a code file within a certain link distance in
 * the {@link KnowledgeGraph}.
 */
public class RationaleCoverageCalculator {
	private FilterSettings filterSettings;
	private Set<KnowledgeElement> elementsToBeCoveredWithRationale;

	/**
	 * @param filterSettings
	 *            {@link FilterSettings} used to filter the {@link KnowledgeGraph}.
	 *            For example, the {@link FilterSettings} can be used to specify
	 *            that the coverage of decisions should only include decisions with
	 *            status "decided" and no decisions with status "rejected" or
	 *            "challenged".
	 */
	public RationaleCoverageCalculator(FilterSettings filterSettings) {
		this.filterSettings = filterSettings;
		elementsToBeCoveredWithRationale = getElementsToBeCovered();
	}

	/**
	 * @return map with the decision coverage as keys and elements that have the
	 *         respective coverage as map values.
	 */
	@XmlElement
	public Map<Integer, List<KnowledgeElement>> getDecisionCoverageMap() {
		return calculateCoverage(KnowledgeType.DECISION);
	}

	/**
	 * @return map with the decision problem coverage as keys and elements that have
	 *         the respective coverage as map values. The status in the
	 *         {@link FilterSettings} can be used to specify whether the decision
	 *         problems should be resolved or unresolved. Per default, both resolved
	 *         and unresolved decision problems (issues) are included.
	 */
	@XmlElement
	public Map<Integer, List<KnowledgeElement>> getIssueCoverageMap() {
		return calculateCoverage(KnowledgeType.ISSUE);
	}

	/**
	 * @param knowledgeType
	 *            decision knowledge type (see {@link KnowledgeType}) for that the
	 *            coverage should be calculated (e.g. {@link KnowledgeType#DECISION}
	 *            for decision coverage).
	 * @return map with the rationale coverage as keys and elements that have the
	 *         respective coverage as map values.
	 */
	private Map<Integer, List<KnowledgeElement>> calculateCoverage(KnowledgeType knowledgeType) {
		Map<Integer, List<KnowledgeElement>> metric = new LinkedHashMap<>();

		FilterSettings clonedFilterSettings = filterSettings.clone();
		clonedFilterSettings.setLinkDistance(filterSettings.getDefinitionOfDone().getMaximumLinkDistanceToDecisions());
		for (KnowledgeElement knowledgeElement : elementsToBeCoveredWithRationale) {
			Set<KnowledgeElement> reachableElementsOfTargetType = getReachableElementsOfType(knowledgeElement,
					knowledgeType, clonedFilterSettings);
			Integer numberOfReachableElementsOfTargetType = reachableElementsOfTargetType.size();

			if (!metric.containsKey(numberOfReachableElementsOfTargetType)) {
				metric.put(numberOfReachableElementsOfTargetType, new ArrayList<>());
			}
			metric.get(numberOfReachableElementsOfTargetType).add(knowledgeElement);
		}

		return metric;
	}

	/**
	 * @param sourceElement
	 *            start/selected element to traverse the filtered
	 *            {@link KnowledgeGraph}.
	 * @param targetType
	 *            decision knowledge type (see {@link KnowledgeType}) for that the
	 *            coverage should be calculated (e.g. {@link KnowledgeType#DECISION}
	 *            for decision coverage).
	 * @param filterSettings
	 *            {@link FilterSettings} to filter the {@link KnowledgeGraph}. The
	 *            source element is the selected element.
	 * @return reachable {@link KnowledgeElement}s of target type, e.g. decisions
	 *         from source element.
	 */
	public static Set<KnowledgeElement> getReachableElementsOfType(KnowledgeElement sourceElement,
			KnowledgeType targetType, FilterSettings filterSettings) {
		filterSettings.setSelectedElementObject(sourceElement);
		FilteringManager filteringManager = new FilteringManager(filterSettings);
		Set<KnowledgeElement> reachableElements = filteringManager.getElementsMatchingFilterSettings();
		return reachableElements.stream().filter(element -> element.getType() == targetType)
				.collect(Collectors.toSet());
	}

	/**
	 * @return {@link KnowledgeElement}s that are not decision knowledge, e.g.
	 *         requirements, code files, and work items. For these elements, the
	 *         rationale coverage should be calculated. Decision knowledge is not
	 *         included because there are dedicated metrics for intra-rationale
	 *         completeness (see {@link RationaleCompletenessCalculator}).
	 */
	private Set<KnowledgeElement> getElementsToBeCovered() {
		FilterSettings clonedFilterSettings = filterSettings.clone();
		clonedFilterSettings.setKnowledgeTypes(filterSettings.getKnowledgeTypesToBeCoveredWithRationale());
		FilteringManager filteringManager = new FilteringManager(clonedFilterSettings);
		return filteringManager.getElementsMatchingFilterSettings();
	}
}
