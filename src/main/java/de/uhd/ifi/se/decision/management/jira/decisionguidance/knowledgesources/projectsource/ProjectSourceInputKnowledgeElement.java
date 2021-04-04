package de.uhd.ifi.se.decision.management.jira.decisionguidance.knowledgesources.projectsource;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import de.uhd.ifi.se.decision.management.jira.decisionguidance.Recommendation;
import de.uhd.ifi.se.decision.management.jira.decisionguidance.score.RecommendationScore;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.model.Link;

/**
 * Queries another Jira project for a given {@link KnowledgeElement} and its
 * linked elements.
 * 
 * For example, a decision problem with alternatives and arguments already
 * documented can be used to query DBPedia.
 */
public class ProjectSourceInputKnowledgeElement extends ProjectSourceInput<KnowledgeElement> {

	@Override
	public List<Recommendation> getRecommendations(KnowledgeElement knowledgeElement) {
		if (knowledgeElement == null) {
			return new ArrayList<>();
		}
		List<Recommendation> recommendations = new ArrayList<>();
		ProjectSourceInputString projectSourceInputString = new ProjectSourceInputString();
		projectSourceInputString.setKnowledgeSource(knowledgeSource);
		recommendations.addAll(projectSourceInputString.getRecommendations(knowledgeElement.getSummary()));

		for (Link link : knowledgeElement.getLinks()) {
			for (KnowledgeElement linkedElement : link.getBothElements()) {
				if (linkedElement.getType().equals(KnowledgeType.ALTERNATIVE)
						|| linkedElement.getType().equals(KnowledgeType.DECISION)) {
					List<Recommendation> recommendationFromAlternative = projectSourceInputString
							.getRecommendations(linkedElement.getSummary());
					recommendations.addAll(recommendationFromAlternative);
				}
			}
		}
		return calculateMeanScore(recommendations).stream().distinct().collect(Collectors.toList());
	}

	public List<Recommendation> calculateMeanScore(List<Recommendation> recommendations) {
		List<Recommendation> filteredRecommendations = new ArrayList<>();

		for (Recommendation recommendation : recommendations) {
			RecommendationScore meanRecommendationScore = new RecommendationScore(0, "Mean Score");
			Recommendation meanScoreRecommendation = recommendation;
			int numberDuplicates = 0;
			int meanScore = 0;
			for (Recommendation recommendation1 : recommendations) {
				if (recommendation.equals(recommendation1)) {
					numberDuplicates += 1;
					meanScore += recommendation1.getScore().getTotalScore();
					meanRecommendationScore
							.setComposedScore(recommendation1.getScore().getComposedScores());
				}
			}
			meanRecommendationScore.setTotalScore(meanScore / numberDuplicates);
			meanScoreRecommendation.setScore(meanRecommendationScore);
			filteredRecommendations.add(meanScoreRecommendation);
		}

		return filteredRecommendations;
	}
}
