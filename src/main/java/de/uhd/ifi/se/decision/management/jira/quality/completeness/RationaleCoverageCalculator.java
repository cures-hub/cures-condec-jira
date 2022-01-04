package de.uhd.ifi.se.decision.management.jira.quality.completeness;

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
 * calculates how many decisions are linked to a requirement or how many
 * decisions are linked to a code file within a certain link distance in the
 * {@link KnowledgeGraph}.
 */
public class RationaleCoverageCalculator {
	private FilterSettings filterSettings;
	private Set<KnowledgeElement> elementsToBeCoveredWithRationale;

	public RationaleCoverageCalculator(FilterSettings filterSettings) {
		this.filterSettings = filterSettings;
		elementsToBeCoveredWithRationale = getElementsToBeCovered();
	}

	/**
	 * @return map with the decision coverage as keys and elements that have the
	 *         respective coverage as map values.
	 */
	@XmlElement
	public Map<Integer, List<KnowledgeElement>> getDecisionCoverageMetric() {
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
	public Map<Integer, List<KnowledgeElement>> getIssueCoverageMetric() {
		return calculateCoverage(KnowledgeType.ISSUE);
	}

	private Map<Integer, List<KnowledgeElement>> calculateCoverage(KnowledgeType knowledgeType) {
		Map<Integer, List<KnowledgeElement>> metric = new LinkedHashMap<>();

		for (KnowledgeElement knowledgeElement : elementsToBeCoveredWithRationale) {
			Set<KnowledgeElement> reachableElementsOfTargetType = getReachableElementsOfType(knowledgeElement,
					knowledgeType);
			Integer numberOfReachableElementsOfTargetType = reachableElementsOfTargetType.size();

			if (!metric.containsKey(numberOfReachableElementsOfTargetType)) {
				metric.put(numberOfReachableElementsOfTargetType, new ArrayList<>());
			}
			metric.get(numberOfReachableElementsOfTargetType).add(knowledgeElement);
		}
		filterSettings.setSelectedElementObject(null);

		return metric;
	}

	public Set<KnowledgeElement> getReachableElementsOfType(KnowledgeElement sourceElement, KnowledgeType targetType) {
		filterSettings.setSelectedElementObject(sourceElement);
		FilteringManager filteringManager = new FilteringManager(filterSettings);
		Set<KnowledgeElement> reachableElements = filteringManager.getElementsMatchingFilterSettings();
		return reachableElements.stream().filter(element -> element.getType() == targetType)
				.collect(Collectors.toSet());
	}

	private Set<KnowledgeElement> getElementsToBeCovered() {
		Set<String> knowledgeTypesInFilter = filterSettings.getKnowledgeTypes();
		filterSettings.setKnowledgeTypes(filterSettings.getKnowledgeTypesToBeCoveredWithRationale());
		FilteringManager filteringManager = new FilteringManager(filterSettings);
		Set<KnowledgeElement> elementsToBeCoveredWithRationale = filteringManager.getElementsMatchingFilterSettings();
		filterSettings.setKnowledgeTypes(knowledgeTypesInFilter);
		return elementsToBeCoveredWithRationale;
	}
}
