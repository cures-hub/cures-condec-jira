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

	private FilterSettings filterSettings;
	private RationaleCompletenessMetric issuesSolvedByDecision;
	private RationaleCompletenessMetric decisionsSolvingIssues;
	private RationaleCompletenessMetric proArgumentDocumentedForAlternative;
	private RationaleCompletenessMetric conArgumentDocumentedForAlternative;
	private RationaleCompletenessMetric proArgumentDocumentedForDecision;
	private RationaleCompletenessMetric conArgumentDocumentedForDecision;

	protected static final Logger LOGGER = LoggerFactory.getLogger(RationaleCompletenessCalculator.class);

	public RationaleCompletenessCalculator(FilterSettings filterSettings) {
		this.filterSettings = filterSettings;
		issuesSolvedByDecision = calculateCompletenessMetric(KnowledgeType.ISSUE, KnowledgeType.DECISION);
		decisionsSolvingIssues = calculateCompletenessMetric(KnowledgeType.DECISION, KnowledgeType.ISSUE);
		proArgumentDocumentedForAlternative = calculateCompletenessMetric(KnowledgeType.ALTERNATIVE, KnowledgeType.PRO);
		conArgumentDocumentedForAlternative = calculateCompletenessMetric(KnowledgeType.ALTERNATIVE, KnowledgeType.CON);
		proArgumentDocumentedForDecision = calculateCompletenessMetric(KnowledgeType.DECISION, KnowledgeType.PRO);
		conArgumentDocumentedForDecision = calculateCompletenessMetric(KnowledgeType.DECISION, KnowledgeType.CON);
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
		LOGGER.info("RequirementsDashboard getElementsWithNeighborsOfOtherType");

		KnowledgeGraph graph = new FilteringManager(filterSettings).getFilteredGraph();
		List<KnowledgeElement> allSourceElements = graph.getElements(sourceElementType);
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
		return issuesSolvedByDecision;
	}

	@XmlElement
	public RationaleCompletenessMetric getDecisionsSolvingIssues() {
		return decisionsSolvingIssues;
	}

	@XmlElement
	public RationaleCompletenessMetric getProArgumentDocumentedForAlternative() {
		return proArgumentDocumentedForAlternative;
	}

	@XmlElement
	public RationaleCompletenessMetric getConArgumentDocumentedForAlternative() {
		return conArgumentDocumentedForAlternative;
	}

	@XmlElement
	public RationaleCompletenessMetric getProArgumentDocumentedForDecision() {
		return proArgumentDocumentedForDecision;
	}

	@XmlElement
	public RationaleCompletenessMetric getConArgumentDocumentedForDecision() {
		return conArgumentDocumentedForDecision;
	}
}