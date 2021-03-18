package de.uhd.ifi.se.decision.management.jira.quality.completeness;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
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

	public Map<String, String> getCodeFilesWithNeighborsOfOtherType(KnowledgeType knowledgeType) {
		LOGGER.info("CodeCoverageCalculator getCodeFilesWithNeighborsOfOtherType");

		if (knowledgeType == null) {
			return null;
		}

		KnowledgeGraph graph = KnowledgeGraph.getInstance(projectKey);
		List<KnowledgeElement> codeFiles = graph.getElements(KnowledgeType.CODE);

		FilterSettings filterSettingsForType = filterSettings;
		Set<String> types = new HashSet<>();
		types.add(knowledgeType.toString());
		filterSettingsForType.setKnowledgeTypes(types);

		String withLink = "";
		String withoutLink = "";

		for (KnowledgeElement codeFile : codeFiles) {
			// TODO do this without filter settings, so we do not need to create transitive links
			filterSettingsForType.setSelectedElement(codeFile);
			FilteringManager filteringManager = new FilteringManager(filterSettingsForType);
			Set<KnowledgeElement> elementsOfTargetTypeReachable = filteringManager.getElementsMatchingFilterSettings();
			if (elementsOfTargetTypeReachable.size() > 1) {
				withLink += projectKey + '-' + codeFile.getDescription() + " ";
			} else {
				withoutLink += projectKey + '-' + codeFile.getDescription() + " ";
			}
		}

		Map<String, String> result = new LinkedHashMap<String, String>();
		result.put("Links from Code File to " + knowledgeType.toString(), withLink);
		result.put("No links from Code File to " + knowledgeType.toString(), withoutLink);
		return result;
	}


	public Map<String, Integer> getNumberOfDecisionKnowledgeElementsForCodeFiles(KnowledgeType knowledgeType) {
		LOGGER.info("CodeCoverageCalculator getNumberOfDecisionKnowledgeElementsForCodeFiles");

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
			// TODO do this without filter settings, so we do not need to create transitive links
			filterSettingsForType.setSelectedElement(codeFile);
			FilteringManager filteringManager = new FilteringManager(filterSettingsForType);
			Set<KnowledgeElement> elementsOfTargetTypeReachable = filteringManager.getElementsMatchingFilterSettings();
			numberOfElementsReachable.put(projectKey + '-' + codeFile.getDescription(), elementsOfTargetTypeReachable.size() - 1);
		}
		return numberOfElementsReachable;
	}
}
