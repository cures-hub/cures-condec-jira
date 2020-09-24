package de.uhd.ifi.se.decision.management.jira.view.decisionguidance;

import de.uhd.ifi.se.decision.management.jira.decisionguidance.knowledgesources.KnowledgeSourceType;

public class DBPediaRecommendation extends Recommendation {

	String keywords;
	String input;

	public DBPediaRecommendation(String knowledgeSourceName, String recommendations, String keywords, String input, String url) {
		super(knowledgeSourceName, recommendations, KnowledgeSourceType.RDF, url);
		this.keywords = keywords;
		this.input = input;
	}

	public String getKeywords() {
		return keywords;
	}

	public void setKeywords(String keywords) {
		this.keywords = keywords;
	}

	public String getInput() {
		return input;
	}

	public void setInput(String input) {
		this.input = input;
	}
}
