package de.uhd.ifi.se.decision.management.jira.decisionguidance.knowledgesources.projectsource;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import de.uhd.ifi.se.decision.management.jira.decisionguidance.Recommendation;
import de.uhd.ifi.se.decision.management.jira.decisionguidance.RecommendationScore;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;

/**
 * Queries another Jira project for a given {@link KnowledgeElement} and its
 * linked elements.
 * 
 * For example, a decision problem with alternatives and arguments already
 * documented can be used to query the other Jira project.
 */
public class ProjectSourceInputKnowledgeElement extends ProjectSourceInput<KnowledgeElement> {

	@Override
	public List<Recommendation> getRecommendations(KnowledgeElement decisionProblem) {
		if (decisionProblem == null) {
			return new ArrayList<>();
		}
		List<Recommendation> recommendations = new ArrayList<>();
		ProjectSourceInputString projectSourceInputString = new ProjectSourceInputString();
		projectSourceInputString.setKnowledgeSource(knowledgeSource);
		recommendations.addAll(projectSourceInputString.getRecommendations(decisionProblem.getSummary()));

		for (KnowledgeElement linkedElement : decisionProblem.getLinkedSolutionOptions()) {
			List<Recommendation> recommendationFromAlternative = projectSourceInputString
					.getRecommendations(linkedElement.getSummary());
			recommendations.addAll(recommendationFromAlternative);
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
					numberDuplicates++;
					meanScore += recommendation1.getScore().getValue();
					meanRecommendationScore.setSubScore(recommendation1.getScore().getSubScores());
				}
			}
			meanRecommendationScore.setValue(meanScore / numberDuplicates);
			meanScoreRecommendation.setScore(meanRecommendationScore);
			filteredRecommendations.add(meanScoreRecommendation);
		}

		return filteredRecommendations;
	}
}
