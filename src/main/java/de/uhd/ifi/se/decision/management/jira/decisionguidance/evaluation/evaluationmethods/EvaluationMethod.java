package de.uhd.ifi.se.decision.management.jira.decisionguidance.evaluation.evaluationmethods;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;

import de.uhd.ifi.se.decision.management.jira.decisionguidance.Recommendation;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;

public abstract class EvaluationMethod {

	protected List<Recommendation> recommendations;
	protected List<KnowledgeElement> solutionOptions;
	protected int topKResults;

	@XmlElement(name = "value")
	public abstract double calculateMetric();

	@XmlElement
	public abstract String getName();

	@XmlElement
	public abstract String getDescription();

}
