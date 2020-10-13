package de.uhd.ifi.se.decision.management.jira.decisionguidance.knowledgesources.calculationmethods.projectsource;

import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.persistence.KnowledgePersistenceManager;
import de.uhd.ifi.se.decision.management.jira.view.decisionguidance.Recommendation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ProjectCalculationMethodSubstring extends ProjectCalculationMethod {

	public ProjectCalculationMethodSubstring() {

	}

	public ProjectCalculationMethodSubstring(String projectKey, String projectSourceName) {
		this.projectKey = projectKey;
		this.projectSourceName = projectSourceName;
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
	public List<Recommendation> getResults(String inputs) {

		List<String> keywords = Arrays.asList(inputs.trim().split(" "));

		List<Recommendation> recommendations = new ArrayList<>();

		List<KnowledgeElement> knowledgeElements = this.queryDatabase();
		if (knowledgeElements == null) return recommendations;

		//filter all knowledge elements by the type "issue"
		List<KnowledgeElement> issues = knowledgeElements
			.stream()
			.filter(knowledgeElement -> knowledgeElement.getType() == KnowledgeType.ISSUE)
			.collect(Collectors.toList());

		for (String keyword : keywords) {
			//get all alternatives, which parent contains the pattern"
			issues.forEach(issue -> {
				if (issue.getSummary().contains(keyword)) {
					issue.getLinks()
						.stream()
						.filter(link -> this.matchingIssueTypes(link.getSource(), KnowledgeType.ALTERNATIVE, KnowledgeType.DECISION) ||
							this.matchingIssueTypes(link.getTarget(), KnowledgeType.ALTERNATIVE, KnowledgeType.DECISION)) //TODO workaround, checks both directions since the link direction is sometimes wrong.
						.forEach(child -> {

							Recommendation recommendation = this.createRecommendation(child.getSource(), child.getTarget(), KnowledgeType.ALTERNATIVE, KnowledgeType.DECISION);
							recommendation.addArguments(this.getArguments(child.getSource()));
							recommendation.addArguments(this.getArguments(child.getTarget()));

							if (recommendation != null) {
								int score = calculateScore(keywords, issue);
								recommendation.setScore(score);
								recommendations.add(recommendation);
							}

						});
				}
			});
		}

		return recommendations.stream().distinct().collect(Collectors.toList());
	}

	@Override
	public List<Recommendation> getResults(KnowledgeElement knowledgeElement2) {

		String inputs = "";

		List<String> keywords = Arrays.asList(inputs.trim().split(" "));

		List<Recommendation> recommendations = new ArrayList<>();

		List<KnowledgeElement> knowledgeElements = this.queryDatabase();
		if (knowledgeElements == null) return recommendations;

		//filter all knowledge elements by the type "issue"
		List<KnowledgeElement> issues = knowledgeElements
			.stream()
			.filter(knowledgeElement -> knowledgeElement.getType() == KnowledgeType.ISSUE)
			.collect(Collectors.toList());

		for (String keyword : keywords) {
			//get all alternatives, which parent contains the pattern"
			issues.forEach(issue -> {
				if (issue.getSummary().contains(keyword)) {
					issue.getLinks()
						.stream()
						.filter(link -> this.matchingIssueTypes(link.getSource(), KnowledgeType.ALTERNATIVE, KnowledgeType.DECISION) ||
							this.matchingIssueTypes(link.getTarget(), KnowledgeType.ALTERNATIVE, KnowledgeType.DECISION)) //TODO workaround, checks both directions since the link direction is sometimes wrong.
						.forEach(child -> {

							//	Recommendation recommendation = this.createRecommendation(child.getSource(), child.getTarget(), KnowledgeType.ALTERNATIVE, KnowledgeType.DECISION);
							Recommendation recommendation = new Recommendation("TEST", knowledgeElement2.getSummary(), knowledgeElement2.getUrl());
							recommendation.addArguments(this.getArguments(child.getSource()));
							recommendation.addArguments(this.getArguments(child.getTarget()));

							if (recommendation != null) {
								int score = calculateScore(keywords, issue);
								recommendation.setScore(score);
								recommendations.add(recommendation);
							}

						});
				}
			});
		}

		return recommendations.stream().distinct().collect(Collectors.toList());
	}


	private int calculateScore(List<String> keywords, KnowledgeElement parentIssue) {
		float numberOfKeywords = keywords.size();
		int matchedKeyWord = 0;
		int numberOfIssues = 1;

		for (String keyword : keywords) {
			if (parentIssue.getSummary().contains(keyword)) {
				matchedKeyWord += 1;
			}
		}


//		for (Link link : parentIssue.getLinks()) {
//			if (link.getTarget().getType() == KnowledgeType.ISSUE) {
//				numberOfIssues += 1;
//				for (String keyword : keywords) {
//					if (link.getTarget().getSummary().contains(keyword)) {
//						matchedKeyWord += 1;
//					}
//				}
//			}
//		}

		float score = 0;
		if (numberOfKeywords != 0)
			score = (matchedKeyWord / (numberOfKeywords * numberOfIssues)) * 100;

		return Math.round(score);
	}
}
