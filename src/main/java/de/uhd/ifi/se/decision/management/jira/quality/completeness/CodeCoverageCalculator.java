package de.uhd.ifi.se.decision.management.jira.quality.completeness;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.uhd.ifi.se.decision.management.jira.filtering.FilterSettings;
import de.uhd.ifi.se.decision.management.jira.filtering.FilteringManager;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeGraph;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;

public class CodeCoverageCalculator {

	private static final Logger LOGGER = LoggerFactory.getLogger(RationaleCompletenessCalculator.class);

	private String projectKey;
	private FilterSettings filterSettings;

	public CodeCoverageCalculator(String projectKey, FilterSettings filterSettings) {
		this.projectKey = projectKey;
		this.filterSettings = filterSettings;
	}

	public Map<String, Integer> getNumberOfDecisionKnowledgeElementsForCodeFiles(KnowledgeType knowledgeType) {
		LOGGER.info("RationaleCoverageCalculator getNumberOfDecisionKnowledgeElementsForCodeFiles");

		if (knowledgeType == null) {
			return null;
		}

		KnowledgeGraph graph = KnowledgeGraph.getInstance(projectKey);
		List<KnowledgeElement> codeFiles = graph.getElements(KnowledgeType.CODE);

		FilterSettings filterSettingsForType = filterSettings;
		Set<String> types = new HashSet<>();
		types.add(knowledgeType.toString());
		filterSettingsForType.setKnowledgeTypes(types);

		Map<String, Integer> numberOfElementsReachable = new HashMap<String, Integer>();
		for (KnowledgeElement codeFile : codeFiles) {
			filterSettingsForType.setSelectedElement(codeFile);
			FilteringManager filteringManager = new FilteringManager(filterSettingsForType);
			Set<KnowledgeElement> elementsOfTargetTypeReachable = filteringManager.getElementsMatchingFilterSettings();
			numberOfElementsReachable.put(codeFile.getKey().replace(':', '-'), elementsOfTargetTypeReachable.size() - 1);
		}
		return numberOfElementsReachable;
	}
}
