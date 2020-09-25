package de.uhd.ifi.se.decision.management.jira.decisionguidance.knowledgesources.algorithms;

import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.model.Link;
import de.uhd.ifi.se.decision.management.jira.persistence.KnowledgePersistenceManager;
import de.uhd.ifi.se.decision.management.jira.view.decisionguidance.Recommendation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ProjectSourceSubstringAlgorithm implements KnowledgeSourceAlgorithm {

	private String projectKey;
	private String projectSourceName;
	private String inputs;
	private KnowledgePersistenceManager knowledgePersistenceManager;


	public ProjectSourceSubstringAlgorithm(String projectKey, String projectSourceName, String input) {
		this.projectKey = projectKey;
		this.projectSourceName = projectSourceName;
		this.inputs = input;
		try {
			this.knowledgePersistenceManager = KnowledgePersistenceManager.getOrCreate(this.projectKey);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
	}


	protected List<KnowledgeElement> queryDatabase() {
		return this.knowledgePersistenceManager != null ? this.knowledgePersistenceManager.getKnowledgeElements() : null;
	}

	@Override
	public List<Recommendation> getResults() {

		List<String> keywords = Arrays.asList(inputs.trim().split(" "));

		List<Recommendation> recommendations = new ArrayList<>();

		List<KnowledgeElement> knowledgeElements = this.queryDatabase();

		if (knowledgeElements != null) {

			//filter all knowledge elements by the type "issue"
			List<KnowledgeElement> issues = knowledgeElements
				.stream()
				.filter(knowledgeElement -> knowledgeElement.getType() == KnowledgeType.ISSUE)
				.collect(Collectors.toList());

			for (String keyword : keywords) {
				//get all alternatives, which parent contains the pattern"
				issues.forEach(issue -> {
					if (issue.getSummary().contains(keyword)) {
						issue.getLinks().stream()
							.filter(link -> link.getTarget().getType() == KnowledgeType.ALTERNATIVE)
							.forEach(child -> {
								int score = calculateScore(keywords, issue);
								Recommendation recommendation =
									new Recommendation(this.projectSourceName, child.getTarget().getSummary(), child.getTarget().getUrl());
								recommendation.setScore(score);
								recommendations.add(recommendation);
							});
					}
				});
			}
		}
		return recommendations.stream().distinct().collect(Collectors.toList());
	}

	private int calculateScore(List<String> keywords, KnowledgeElement parentIssue) {
		float numberOfKeywords = keywords.size();
		int matchedKeyWord = 0;
		int numberOfIssues = 0;

		for (Link link : parentIssue.getLinks()) {
			if (link.getTarget().getType() == KnowledgeType.ISSUE) {
				numberOfIssues += 1;
				for (String keyword : keywords) {
					if (link.getTarget().getSummary().contains(keyword)) {
						matchedKeyWord += 1;
					}
				}
			}
		}

		float score = 0;
		if (numberOfKeywords != 0)
			score = (matchedKeyWord / (numberOfKeywords * numberOfIssues)) * 100;

		return Math.round(score);
	}
}
