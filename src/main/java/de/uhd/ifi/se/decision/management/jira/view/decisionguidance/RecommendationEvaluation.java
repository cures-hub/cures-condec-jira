package de.uhd.ifi.se.decision.management.jira.view.decisionguidance;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "RecommendationEval")
public class RecommendationEvaluation {

	@XmlElement
	protected String recommenderType;

	@XmlElement
	protected String knowledgeSourceName;

	@XmlElement
	protected int numberOfResults;

	@XmlElement
	protected double fScore;


	public RecommendationEvaluation() {

	}

	public RecommendationEvaluation(String recommenderType, String knowledgeSourceName, int numberOfResults, double fScore) {
		this.recommenderType = recommenderType;
		this.knowledgeSourceName = knowledgeSourceName;
		this.numberOfResults = numberOfResults;
		this.fScore = fScore;
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

	public double getFScore() {
		return fScore;
	}

	public void setFScore(double fScore) {
		this.fScore = fScore;
	}

	public String getRecommenderType() {
		return recommenderType;
	}

	public void setRecommenderType(String recommenderType) {
		this.recommenderType = recommenderType;
	}
}
