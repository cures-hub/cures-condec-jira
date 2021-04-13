package de.uhd.ifi.se.decision.management.jira.quality.completeness;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atlassian.jira.user.ApplicationUser;

import de.uhd.ifi.se.decision.management.jira.filtering.FilterSettings;
import de.uhd.ifi.se.decision.management.jira.filtering.FilteringManager;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeGraph;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;

/**
 * Calculates metrics for the intra-rationale completeness.
 */
public class RationaleCompletenessCalculator {

	protected static final Logger LOGGER = LoggerFactory.getLogger(RationaleCompletenessCalculator.class);

	private String projectKey;
	private FilteringManager filteringManager;

	public RationaleCompletenessCalculator(ApplicationUser user, FilterSettings filterSettings) {
		this.projectKey = filterSettings.getProjectKey();
		this.filteringManager = new FilteringManager(user, filterSettings);
	}

	public Map<String, String> getElementsWithNeighborsOfOtherType(KnowledgeType sourceElementType,
			KnowledgeType targetElementType) {
		LOGGER.info("RequirementsDashboard getElementsWithNeighborsOfOtherType");

		KnowledgeGraph graph = filteringManager.getSubgraphMatchingFilterSettings();
		List<KnowledgeElement> allSourceElements = graph.getElements(sourceElementType);
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
