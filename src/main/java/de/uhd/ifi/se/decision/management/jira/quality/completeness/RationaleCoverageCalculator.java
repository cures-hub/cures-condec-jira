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
		this.decisionCoverageMetric = calculateKnowledgeElementsWithNeighborsOfOtherType(KnowledgeType.DECISION);
		this.issueCoverageMetric = calculateKnowledgeElementsWithNeighborsOfOtherType(KnowledgeType.ISSUE);
	}

	private RationaleCoverageMetric calculateKnowledgeElementsWithNeighborsOfOtherType(KnowledgeType knowledgeType) {
		LOGGER.info("RationaleCoverageCalculator calculateKnowledgeElementsWithNeighborsOfOtherType");

		RationaleCoverageMetric metric = new RationaleCoverageMetric(knowledgeType);

		if (knowledgeType == null) {
			return null;
		}

		metric.setMinimumRequiredCoverage(filterSettings.getDefinitionOfDone().getMinimumDecisionsWithinLinkDistance());

		FilteringManager filteringManager = new FilteringManager(filterSettings);
		Set<KnowledgeElement> sourceElements = filteringManager.getElementsMatchingFilterSettings();

		FilterSettings filterSettingsForTargetType = new FilterSettings(filterSettings.getProjectKey(),
				filterSettings.getSearchTerm());
		filterSettingsForTargetType.setKnowledgeTypes(Set.of(knowledgeType.toString()));
		filterSettingsForTargetType.setCreateTransitiveLinks(true);

		for (KnowledgeElement knowledgeElement : sourceElements) {
			filterSettingsForTargetType.setSelectedElementObject(knowledgeElement);
			filteringManager.setFilterSettings(filterSettingsForTargetType);
			Set<KnowledgeElement> reachableElements = filteringManager.getElementsMatchingFilterSettings();
			Set<KnowledgeElement> reachableElementsOfTargetType = reachableElements.stream()
					.filter(element -> element.getType() == knowledgeType).collect(Collectors.toSet());

			Integer numberOfReachableElementsOfTargetType = reachableElementsOfTargetType.size();

			if (!metric.getCoverageMap().containsKey(numberOfReachableElementsOfTargetType)) {
				metric.getCoverageMap().put(numberOfReachableElementsOfTargetType, new ArrayList<>());
			}
			metric.getCoverageMap().get(numberOfReachableElementsOfTargetType).add(knowledgeElement);
		}

		return metric;
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
