package de.uhd.ifi.se.decision.management.jira.quality.completeness;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atlassian.jira.user.ApplicationUser;

import de.uhd.ifi.se.decision.management.jira.filtering.FilterSettings;
import de.uhd.ifi.se.decision.management.jira.filtering.FilteringManager;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeGraph;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;

// TODO: Integrate into RationaleCoverageCalculator class and remove this class
public class CodeCoverageCalculator {

	private static final Logger LOGGER = LoggerFactory.getLogger(RationaleCompletenessCalculator.class);

	private FilteringManager filteringManager;
	private String projectKey;
	private int linkDistance;
	private Map<KnowledgeElement, Map<KnowledgeType, Integer>> linkedElementMap = new HashMap<KnowledgeElement, Map<KnowledgeType, Integer>>();

	public CodeCoverageCalculator(ApplicationUser user, FilterSettings filterSettings) {
		this.filteringManager = new FilteringManager(user, filterSettings);
		this.projectKey = filterSettings.getProjectKey();
		this.linkDistance = filterSettings.getLinkDistance();
	}

	private void fillLinkedElementMap(KnowledgeElement codeFile) {
		Map<KnowledgeType, Integer> knowledgeTypeMap = new HashMap<KnowledgeType, Integer>();
		Set<KnowledgeElement> linkedElements = codeFile.getLinkedElements(linkDistance);
		for (KnowledgeElement linkedElement : linkedElements) {
			if (!knowledgeTypeMap.containsKey(linkedElement.getType())) {
				knowledgeTypeMap.put(linkedElement.getType(), 0);
			}
			knowledgeTypeMap.put(linkedElement.getType(), knowledgeTypeMap.get(linkedElement.getType()) + 1);
		}
		linkedElementMap.put(codeFile, knowledgeTypeMap);
	}

	public Map<String, String> getCodeFilesWithNeighborsOfOtherType(KnowledgeType knowledgeType) {
		LOGGER.info("CodeCoverageCalculator getCodeFilesWithNeighborsOfOtherType");

		if (knowledgeType == null) {
			return null;
		}

		KnowledgeGraph graph = filteringManager.getSubgraphMatchingFilterSettings();
		List<KnowledgeElement> codeFiles = graph.getElements(KnowledgeType.CODE);

		String withLink = "";
		String withoutLink = "";

		for (KnowledgeElement codeFile : codeFiles) {
			if (!linkedElementMap.containsKey(codeFile)) {
				fillLinkedElementMap(codeFile);
			}
			if (!linkedElementMap.get(codeFile).containsKey(knowledgeType)) {
				withoutLink += projectKey + '-' + codeFile.getDescription() + " ";
			} else {
				withLink += projectKey + '-' + codeFile.getDescription() + " ";
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

		KnowledgeGraph graph = filteringManager.getSubgraphMatchingFilterSettings();
		List<KnowledgeElement> codeFiles = graph.getElements(KnowledgeType.CODE);

		Map<String, Integer> numberOfElementsReachable = new HashMap<String, Integer>();
		for (KnowledgeElement codeFile : codeFiles) {
			if (!linkedElementMap.containsKey(codeFile)) {
				fillLinkedElementMap(codeFile);
			}
			if (!linkedElementMap.get(codeFile).containsKey(knowledgeType)) {
				numberOfElementsReachable.put(projectKey + '-' + codeFile.getDescription(), 0);
			} else {
				numberOfElementsReachable.put(projectKey + '-' + codeFile.getDescription(),
						linkedElementMap.get(codeFile).get(knowledgeType));
			}
		}
		return numberOfElementsReachable;
	}
}
