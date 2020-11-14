package de.uhd.ifi.se.decision.management.jira.view.decisionguidance;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Map;

@XmlRootElement(name = "RecommendationEval")
public class RecommendationEvaluation {

	@XmlElement
	protected String recommenderType;

	@XmlElement
	protected String knowledgeSourceName;

	@XmlElement
	protected int numberOfResults;

	@XmlElement
	protected Map<String, Double> metrics;


	public RecommendationEvaluation(String recommenderType, String knowledgeSourceName, int numberOfResults, Map<String, Double> metrics) {
		this.recommenderType = recommenderType;
		this.knowledgeSourceName = knowledgeSourceName;
		this.numberOfResults = numberOfResults;
		this.metrics = metrics;
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


	public Map<String, Double> getMetrics() {
		return metrics;
	}

	public void setMetrics(Map<String, Double> metrics) {
		this.metrics = metrics;
	}
}
