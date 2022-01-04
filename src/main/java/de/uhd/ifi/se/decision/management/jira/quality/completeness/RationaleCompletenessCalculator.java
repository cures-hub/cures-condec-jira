package de.uhd.ifi.se.decision.management.jira.quality.completeness;

import java.util.List;

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
	private RationaleCompletenessMetric calculateCompletenessMetric(KnowledgeType sourceElementType,
			KnowledgeType targetElementType) {

		List<KnowledgeElement> allSourceElements = filteredGraph.getElements(sourceElementType);
		RationaleCompletenessMetric metric = new RationaleCompletenessMetric(sourceElementType, targetElementType);

		for (KnowledgeElement sourceElement : allSourceElements) {
			if (sourceElement.hasNeighborOfType(targetElementType)) {
				metric.addCompleteElement(sourceElement);
			} else {
				metric.addIncompleteElement(sourceElement);
			}
		}

		return metric;
	}

	@XmlElement
	public RationaleCompletenessMetric getIssuesSolvedByDecision() {
		return calculateCompletenessMetric(KnowledgeType.ISSUE, KnowledgeType.DECISION);
	}

	@XmlElement
	public RationaleCompletenessMetric getDecisionsSolvingIssues() {
		return calculateCompletenessMetric(KnowledgeType.DECISION, KnowledgeType.ISSUE);
	}

	@XmlElement
	public RationaleCompletenessMetric getProArgumentDocumentedForAlternative() {
		return calculateCompletenessMetric(KnowledgeType.ALTERNATIVE, KnowledgeType.PRO);
	}

	@XmlElement
	public RationaleCompletenessMetric getConArgumentDocumentedForAlternative() {
		return calculateCompletenessMetric(KnowledgeType.ALTERNATIVE, KnowledgeType.CON);
	}

	@XmlElement
	public RationaleCompletenessMetric getProArgumentDocumentedForDecision() {
		return calculateCompletenessMetric(KnowledgeType.DECISION, KnowledgeType.PRO);
	}

	@XmlElement
	public RationaleCompletenessMetric getConArgumentDocumentedForDecision() {
		return calculateCompletenessMetric(KnowledgeType.DECISION, KnowledgeType.CON);
	}
}