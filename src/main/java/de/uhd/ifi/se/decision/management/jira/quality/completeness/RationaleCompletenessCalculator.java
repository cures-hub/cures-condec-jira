package de.uhd.ifi.se.decision.management.jira.quality.completeness;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.uhd.ifi.se.decision.management.jira.filtering.FilterSettings;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeGraph;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;

/**
 * Calculates metrics for the intra-rationale completeness.
 */
public class RationaleCompletenessCalculator {

	protected static final Logger LOGGER = LoggerFactory.getLogger(RationaleCompletenessCalculator.class);

	private String projectKey;

	public RationaleCompletenessCalculator(String projectKey) {
		this.projectKey = projectKey;
	}

	public RationaleCompletenessCalculator(FilterSettings filterSettings) {
		this(filterSettings.getProjectKey());
	}

	public Map<String, String> getElementsWithNeighborsOfOtherType(KnowledgeType sourceElementType,
			KnowledgeType targetElementType) {
		LOGGER.info("RequirementsDashboard getElementsWithNeighborsOfOtherType");

		List<KnowledgeElement> allSourceElements = KnowledgeGraph.getOrCreate(projectKey)
				.getElements(sourceElementType);
		String sourceElementsWithTargetTypeLinked = "";
		String sourceElementsWithoutTargetTypeLinked = "";

		for (KnowledgeElement sourceElement : allSourceElements) {
			if (sourceElement.hasNeighborOfType(targetElementType)) {
				sourceElementsWithTargetTypeLinked += sourceElement.getKey() + " ";
			} else {
				sourceElementsWithoutTargetTypeLinked += sourceElement.getKey() + " ";
			}
		}

		Map<String, String> havingLinkMap = new LinkedHashMap<String, String>();
		havingLinkMap.put(sourceElementType.toString() + " has " + targetElementType.toString(),
				sourceElementsWithTargetTypeLinked.trim());
		havingLinkMap.put(sourceElementType.toString() + " has no " + targetElementType.toString(),
				sourceElementsWithoutTargetTypeLinked.trim());
		return havingLinkMap;
	}

}
