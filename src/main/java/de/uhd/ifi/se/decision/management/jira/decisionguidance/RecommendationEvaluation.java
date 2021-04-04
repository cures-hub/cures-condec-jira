package de.uhd.ifi.se.decision.management.jira.decisionguidance;

import de.uhd.ifi.se.decision.management.jira.decisionguidance.evaluationframework.evaluationmethods.EvaluationMethod;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement(name = "RecommendationEval")
public class RecommendationEvaluation {

	@XmlElement
	protected String recommenderType;

	@XmlElement
	protected String knowledgeSourceName;

	@XmlElement
	protected int numberOfResults;


	@XmlElement
	protected List<EvaluationMethod> metrics;


	public RecommendationEvaluation(String recommenderType, String knowledgeSourceName, int numberOfResults, List<EvaluationMethod> metric2) {
		this.recommenderType = recommenderType;
		this.knowledgeSourceName = knowledgeSourceName;
		this.numberOfResults = numberOfResults;
		this.metrics = metric2;
	}

	public String getKnowledgeSourceName() {
		return knowledgeSourceName;
	}

	public void setKnowledgeSourceName(String knowledgeSourceName) {
		this.knowledgeSourceName = knowledgeSourceName;
	}

	public int getNumberOfResults() {
		return numberOfResults;
	}

	public void setNumberOfResults(int numberOfResults) {
		this.numberOfResults = numberOfResults;
	}

	public String getRecommenderType() {
		return recommenderType;
	}

	public void setRecommenderType(String recommenderType) {
		this.recommenderType = recommenderType;
	}


	public List<EvaluationMethod> getMetrics() {
		return metrics;
	}

}
