package de.uhd.ifi.se.decision.management.jira.decisionguidance.knowledgesources.algorithms.projectsource;

import de.uhd.ifi.se.decision.management.jira.decisionguidance.knowledgesources.algorithms.projectsource.ProjectKnowledgeSourceAlgorithm;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.persistence.KnowledgePersistenceManager;
import de.uhd.ifi.se.decision.management.jira.view.decisionguidance.Recommendation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ProjectSourceSubstringAlgorithm extends ProjectKnowledgeSourceAlgorithm {


	public ProjectSourceSubstringAlgorithm() {

	}

	public ProjectSourceSubstringAlgorithm(String projectKey, String projectSourceName) {
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

	private Recommendation createRecommendation(KnowledgeElement source, KnowledgeElement target, KnowledgeType... knowledgeTypes) {
		for (KnowledgeType knowledgeType : knowledgeTypes) {
			if (source.getType() == knowledgeType)
				return new Recommendation(this.projectSourceName, source.getSummary(), source.getUrl());
			if (target.getType() == knowledgeType)
				return new Recommendation(this.projectSourceName, target.getSummary(), target.getUrl());
		}

		return null;
	}

	private boolean matchingIssueTypes(KnowledgeElement knowledgeElement, KnowledgeType... knowledgeTypes) {
		int matchedType = 0;
		for (KnowledgeType knowledgeType : knowledgeTypes) {
			if (knowledgeElement.getType() == knowledgeType) matchedType += 1;
		}
		return matchedType > 0;
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
