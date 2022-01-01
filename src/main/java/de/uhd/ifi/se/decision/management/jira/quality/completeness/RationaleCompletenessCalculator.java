package de.uhd.ifi.se.decision.management.jira.quality.completeness;

import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
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

	@JsonIgnore
	private FilterSettings filterSettings;

	private RationaleCompletenessMetric issuesSolvedByDecision;
	private RationaleCompletenessMetric decisionsSolvingIssues;
	private RationaleCompletenessMetric proArgumentDocumentedForAlternative;
	private RationaleCompletenessMetric conArgumentDocumentedForAlternative;
	private RationaleCompletenessMetric proArgumentDocumentedForDecision;
	private RationaleCompletenessMetric conArgumentDocumentedForDecision;

	@JsonIgnore
	protected static final Logger LOGGER = LoggerFactory.getLogger(RationaleCompletenessCalculator.class);

	public RationaleCompletenessCalculator(FilterSettings filterSettings) {
		this.filterSettings = filterSettings;

		issuesSolvedByDecision = calculateElementsWithNeighborsOfOtherType(KnowledgeType.ISSUE, KnowledgeType.DECISION);
		decisionsSolvingIssues = calculateElementsWithNeighborsOfOtherType(KnowledgeType.DECISION, KnowledgeType.ISSUE);
		proArgumentDocumentedForAlternative = calculateElementsWithNeighborsOfOtherType(KnowledgeType.ALTERNATIVE,
				KnowledgeType.PRO);
		conArgumentDocumentedForAlternative = calculateElementsWithNeighborsOfOtherType(KnowledgeType.ALTERNATIVE,
				KnowledgeType.CON);
		proArgumentDocumentedForDecision = calculateElementsWithNeighborsOfOtherType(KnowledgeType.DECISION,
				KnowledgeType.PRO);
		conArgumentDocumentedForDecision = calculateElementsWithNeighborsOfOtherType(KnowledgeType.DECISION,
				KnowledgeType.CON);
	}

	private RationaleCompletenessMetric calculateElementsWithNeighborsOfOtherType(KnowledgeType sourceElementType,
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

	@JsonProperty
	public RationaleCompletenessMetric getIssuesSolvedByDecision() {
		return issuesSolvedByDecision;
	}

	@JsonProperty
	public RationaleCompletenessMetric getDecisionsSolvingIssues() {
		return decisionsSolvingIssues;
	}

	@JsonProperty
	public RationaleCompletenessMetric getProArgumentDocumentedForAlternative() {
		return proArgumentDocumentedForAlternative;
	}

	@JsonProperty
	public RationaleCompletenessMetric getConArgumentDocumentedForAlternative() {
		return conArgumentDocumentedForAlternative;
	}

	@JsonProperty
	public RationaleCompletenessMetric getProArgumentDocumentedForDecision() {
		return proArgumentDocumentedForDecision;
	}

	@JsonProperty
	public RationaleCompletenessMetric getConArgumentDocumentedForDecision() {
		return conArgumentDocumentedForDecision;
	}
}