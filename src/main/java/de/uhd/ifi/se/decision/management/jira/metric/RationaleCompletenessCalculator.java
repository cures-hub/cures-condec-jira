package de.uhd.ifi.se.decision.management.jira.metric;

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
 * Calculates metrics for the intra-rationale completeness. This enables to
 * answer questions such as "Are there arguments for the decisions?" or "How
 * many issues (=decision problems) are solved by a decision?".
 * 
 * @see #getIssuesSolvedByDecisionMap()
 * @see #getDecisionsSolvingIssuesMap()
 * @see #getProArgumentDocumentedForDecisionMap()
 * @see #getConArgumentDocumentedForDecisionMap()
 * @see #getProArgumentDocumentedForAlternativeMap()
 * @see #getConArgumentDocumentedForAlternativeMap()
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
	 * @return map with two keys "has" and "has no" direct links between source
	 *         element and target element and the respective elements as map values.
	 *         Enables to assess the intra-rationale completeness between elements
	 *         of the source and target type. The elements are part of the knowledge
	 *         graph filtered by the {@link FilterSettings}.
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

	/**
	 * @return map with two keys "issue has decision" and "issue has no decision"
	 *         and the respective issues as map values. Enables to answer the
	 *         question "How many issues (=decision problems) are solved by a
	 *         decision?".
	 */
	@XmlElement
	public Map<String, List<KnowledgeElement>> getIssuesSolvedByDecisionMap() {
		return calculateCompletenessMetric(KnowledgeType.ISSUE, KnowledgeType.DECISION);
	}

	/**
	 * @return map with two keys "decision has issue" and "decision has no issue"
	 *         and the respective decisions as map values. Enables to answer the
	 *         question "For how many decisions is the issue (=decision problem)
	 *         documented?".
	 */
	@XmlElement
	public Map<String, List<KnowledgeElement>> getDecisionsSolvingIssuesMap() {
		return calculateCompletenessMetric(KnowledgeType.DECISION, KnowledgeType.ISSUE);
	}

	/**
	 * @return map with two keys "alternative has pro" and "alternative has no pro"
	 *         and the respective alternatives as map values. Enables to answer the
	 *         question "How many alternatives have at least one pro-argument
	 *         documented?".
	 */
	@XmlElement
	public Map<String, List<KnowledgeElement>> getProArgumentDocumentedForAlternativeMap() {
		return calculateCompletenessMetric(KnowledgeType.ALTERNATIVE, KnowledgeType.PRO);
	}

	/**
	 * @return map with two keys "alternative has con" and "alternative has no con"
	 *         and the respective alternatives as map values. Enables to answer the
	 *         question "How many alternatives have at least one con-argument
	 *         documented?".
	 */
	@XmlElement
	public Map<String, List<KnowledgeElement>> getConArgumentDocumentedForAlternativeMap() {
		return calculateCompletenessMetric(KnowledgeType.ALTERNATIVE, KnowledgeType.CON);
	}

	/**
	 * @return map with two keys "decision has pro" and "decision has no pro" and
	 *         the respective decisions as map values. Enables to answer the
	 *         question "How many decisions have at least one pro-argument
	 *         documented?".
	 */
	@XmlElement
	public Map<String, List<KnowledgeElement>> getProArgumentDocumentedForDecisionMap() {
		return calculateCompletenessMetric(KnowledgeType.DECISION, KnowledgeType.PRO);
	}

	/**
	 * @return map with two keys "decision has con" and "decision has no con" and
	 *         the respective decisions as map values. Enables to answer the
	 *         question "How many decisions have at least one con-argument
	 *         documented?".
	 */
	@XmlElement
	public Map<String, List<KnowledgeElement>> getConArgumentDocumentedForDecisionMap() {
		return calculateCompletenessMetric(KnowledgeType.DECISION, KnowledgeType.CON);
	}

	/**
	 * @return map with two keys "issue has alternative" and "issue has no
	 *         alternative" and the respective alternatives as map values. Enables
	 *         to answer the question "How many issues have at least one alternative
	 *         documented?".
	 */
	@XmlElement
	public Map<String, List<KnowledgeElement>> getAlternativeDocumentedForIssueMap() {
		return calculateCompletenessMetric(KnowledgeType.ISSUE, KnowledgeType.ALTERNATIVE);
	}
}