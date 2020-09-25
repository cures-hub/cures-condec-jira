package de.uhd.ifi.se.decision.management.jira.view.decisionguidance;

import de.uhd.ifi.se.decision.management.jira.decisionguidance.knowledgesources.KnowledgeSourceType;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;

import java.util.List;

public class ProjectRecommendation extends Recommendation {

	List<String> keywords;
	KnowledgeElement parentIssue;

	public ProjectRecommendation(String knowledgeSourceName, String recommendations, List<String> keywords, KnowledgeElement parentIssue, String url) {
		super(knowledgeSourceName, recommendations, KnowledgeSourceType.PROJECT, url);
		this.parentIssue = parentIssue;
		this.keywords = keywords;
	}

	public List<String> getKeywords() {
		return keywords;
	}

	public void setKeywords(List<String> keywords) {
		this.keywords = keywords;
	}

	public KnowledgeElement getParentIssue() {
		return parentIssue;
	}

	public void setParentIssue(KnowledgeElement parentIssue) {
		this.parentIssue = parentIssue;
	}
}
