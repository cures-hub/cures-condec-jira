package de.uhd.ifi.se.decision.management.jira.decisionguidance.evaluationframework.evaluationmethods;

import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.view.decisionguidance.Recommendation;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement(name = "metric")
public abstract class EvaluationMethod {

	protected List<Recommendation> recommendations;
	protected List<KnowledgeElement> solutionOptions;
	protected int topKResults;

	@XmlElement(name = "value")
	public abstract double calculateMetric();

	@XmlElement(name = "name")
	public abstract String getName();

	@XmlElement(name = "description")
	public abstract String getDescription();

}
