package de.uhd.ifi.se.decision.management.jira.decisionguidance.evaluation.metrics;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;

import de.uhd.ifi.se.decision.management.jira.decisionguidance.Recommendation;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;

/**
 * Abstract superclass for evaluation metrics, such as {@link NumberOfTruePositives} and
 * {@link ReciprocalRank}.
 */
public abstract class EvaluationMetric {

	protected List<Recommendation> recommendations;
	protected int topKResults;

	/**
	 * Gold standard/ground truth that is already documented.
	 */
	protected List<KnowledgeElement> documentedSolutionOptions;

	@XmlElement(name = "value")
	public abstract double calculateMetric();

	@XmlElement
	public abstract String getName();

	@XmlElement
	public abstract String getDescription();
}