package de.uhd.ifi.se.decision.management.jira.quality.completeness;

import java.util.ArrayList;
import java.util.Set;
import java.util.stream.Collectors;

import javax.xml.bind.annotation.XmlElement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.uhd.ifi.se.decision.management.jira.filtering.FilterSettings;
import de.uhd.ifi.se.decision.management.jira.filtering.FilteringManager;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;

/**
 * Calculates the rationale coverage of requirements, code, and other software
 * artifacts (=knowledge elements). For example, calculates how many decisions
 * are linked to a requirement or how many decisions are linked to a code file.
 */
public class RationaleCoverageCalculator {
	private FilterSettings filterSettings;
	private RationaleCoverageMetric decisionCoverageMetric;
	private RationaleCoverageMetric issueCoverageMetric;

	private static final Logger LOGGER = LoggerFactory.getLogger(RationaleCompletenessCalculator.class);

	public RationaleCoverageCalculator(FilterSettings filterSettings) {
		this.filterSettings = filterSettings;
		this.decisionCoverageMetric = calculateCoverage(KnowledgeType.DECISION);
		this.issueCoverageMetric = calculateCoverage(KnowledgeType.ISSUE);
	}

	private RationaleCoverageMetric calculateCoverage(KnowledgeType knowledgeType) {
		LOGGER.info("RationaleCoverageCalculator calculateKnowledgeElementsWithNeighborsOfOtherType");

		RationaleCoverageMetric metric = new RationaleCoverageMetric(knowledgeType);
		metric.setMinimumRequiredCoverage(filterSettings.getDefinitionOfDone().getMinimumDecisionsWithinLinkDistance());
		Set<KnowledgeElement> elementsToBeCovered = getElementsToBeCovered();

		for (KnowledgeElement knowledgeElement : elementsToBeCovered) {
			Set<KnowledgeElement> reachableElementsOfTargetType = getReachableElementsOfType(knowledgeElement,
					knowledgeType);
			Integer numberOfReachableElementsOfTargetType = reachableElementsOfTargetType.size();

			if (!metric.getCoverageMap().containsKey(numberOfReachableElementsOfTargetType)) {
				metric.getCoverageMap().put(numberOfReachableElementsOfTargetType, new ArrayList<>());
			}
			metric.getCoverageMap().get(numberOfReachableElementsOfTargetType).add(knowledgeElement);
		}
		filterSettings.setSelectedElementObject(null);

		return metric;
	}

	public Set<KnowledgeElement> getElementsToBeCovered() {
		Set<String> knowledgeTypesInFilter = filterSettings.getKnowledgeTypes();
		filterSettings.setKnowledgeTypes(filterSettings.getKnowledgeTypesToBeCoveredWithRationale());
		FilteringManager filteringManager = new FilteringManager(filterSettings);
		Set<KnowledgeElement> elementsToBeCoveredWithRationale = filteringManager.getElementsMatchingFilterSettings();
		filterSettings.setKnowledgeTypes(knowledgeTypesInFilter);
		return elementsToBeCoveredWithRationale;
	}

	public Set<KnowledgeElement> getReachableElementsOfType(KnowledgeElement sourceElement, KnowledgeType targetType) {
		filterSettings.setSelectedElementObject(sourceElement);
		FilteringManager filteringManager = new FilteringManager(filterSettings);
		Set<KnowledgeElement> reachableElements = filteringManager.getElementsMatchingFilterSettings();
		return reachableElements.stream().filter(element -> element.getType() == targetType)
				.collect(Collectors.toSet());
	}

	@XmlElement
	public RationaleCoverageMetric getDecisionCoverageMetric() {
		return decisionCoverageMetric;
	}

	@XmlElement
	public RationaleCoverageMetric getIssueCoverageMetric() {
		return issueCoverageMetric;
	}
}
