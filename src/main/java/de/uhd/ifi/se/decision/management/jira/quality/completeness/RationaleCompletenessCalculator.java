package de.uhd.ifi.se.decision.management.jira.quality.completeness;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlElement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.uhd.ifi.se.decision.management.jira.filtering.FilterSettings;
import de.uhd.ifi.se.decision.management.jira.filtering.FilteringManager;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeGraph;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;

/**
 * Calculates metrics for the intra-rationale completeness: e.g., are there
 * arguments for the decisions?
 */
public class RationaleCompletenessCalculator {

	private KnowledgeGraph filteredGraph;

	protected static final Logger LOGGER = LoggerFactory.getLogger(RationaleCompletenessCalculator.class);

	public RationaleCompletenessCalculator(FilterSettings filterSettings) {
		filteredGraph = new FilteringManager(filterSettings).getFilteredGraph();
	}

	/**
	 * @param sourceElementType
	 *            {@link KnowledgeType}, e.g. issue.
	 * @param targetElementType
	 *            {@link KnowledgeType}, e.g. decision.
	 * @return intra-rationale completeness between elements of the source and
	 *         target type. The elements are part of the knowledge graph filtered by
	 *         the {@link FilterSettings}. For example, enables to answer the
	 *         question, how many issues are solved by a decision.
	 */
	private Map<String, List<KnowledgeElement>> calculateCompletenessMetric(KnowledgeType sourceElementType,
			KnowledgeType targetElementType) {

		List<KnowledgeElement> allSourceElements = filteredGraph.getElements(sourceElementType);
		Map<String, List<KnowledgeElement>> resultMap = new LinkedHashMap<>();
		List<KnowledgeElement> elementsWithDoDCheckSuccess = new ArrayList<>();
		List<KnowledgeElement> elementsWithDoDCheckFail = new ArrayList<>();

		for (KnowledgeElement sourceElement : allSourceElements) {
			if (sourceElement.hasNeighborOfType(targetElementType)) {
				elementsWithDoDCheckSuccess.add(sourceElement);
			} else {
				elementsWithDoDCheckFail.add(sourceElement);
			}
		}

		resultMap.put(sourceElementType + " has " + targetElementType, elementsWithDoDCheckSuccess);
		resultMap.put(sourceElementType + " has no " + targetElementType, elementsWithDoDCheckFail);

		return resultMap;

	}

	@XmlElement
	public Map<String, List<KnowledgeElement>> getIssuesSolvedByDecision() {
		return calculateCompletenessMetric(KnowledgeType.ISSUE, KnowledgeType.DECISION);
	}

	@XmlElement
	public Map<String, List<KnowledgeElement>> getDecisionsSolvingIssues() {
		return calculateCompletenessMetric(KnowledgeType.DECISION, KnowledgeType.ISSUE);
	}

	@XmlElement
	public Map<String, List<KnowledgeElement>> getProArgumentDocumentedForAlternative() {
		return calculateCompletenessMetric(KnowledgeType.ALTERNATIVE, KnowledgeType.PRO);
	}

	@XmlElement
	public Map<String, List<KnowledgeElement>> getConArgumentDocumentedForAlternative() {
		return calculateCompletenessMetric(KnowledgeType.ALTERNATIVE, KnowledgeType.CON);
	}

	@XmlElement
	public Map<String, List<KnowledgeElement>> getProArgumentDocumentedForDecision() {
		return calculateCompletenessMetric(KnowledgeType.DECISION, KnowledgeType.PRO);
	}

	@XmlElement
	public Map<String, List<KnowledgeElement>> getConArgumentDocumentedForDecision() {
		return calculateCompletenessMetric(KnowledgeType.DECISION, KnowledgeType.CON);
	}
}