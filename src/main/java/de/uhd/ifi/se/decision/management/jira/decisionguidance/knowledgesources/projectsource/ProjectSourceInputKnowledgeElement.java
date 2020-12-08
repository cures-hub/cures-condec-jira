package de.uhd.ifi.se.decision.management.jira.decisionguidance.knowledgesources.projectsource;

import de.uhd.ifi.se.decision.management.jira.decisionguidance.score.RecommendationScore;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.model.Link;
import de.uhd.ifi.se.decision.management.jira.view.decisionguidance.Recommendation;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ProjectSourceInputKnowledgeElement extends ProjectSourceInput<KnowledgeElement> {


	@Override
	public List<Recommendation> getResults(KnowledgeElement knowledgeElement) {
		List<Recommendation> recommendations = new ArrayList<>();

		ProjectSourceInputString projectSourceInputString = new ProjectSourceInputString();
		projectSourceInputString.setData(this.knowledgeSource);

		if (knowledgeElement != null) {

			recommendations.addAll(projectSourceInputString.getResults(knowledgeElement.getSummary()));

			for (Link link : knowledgeElement.getLinks()) {
				for (KnowledgeElement linkedElement : link.getBothElements()) {
					if (linkedElement.getType().equals(KnowledgeType.ALTERNATIVE) || linkedElement.getType().equals(KnowledgeType.DECISION)) {
						List<Recommendation> recommendationFromAlternative = projectSourceInputString.getResults(linkedElement.getSummary());
						recommendations.addAll(recommendationFromAlternative);
					}
				}
			}
		}

		return this.calculateMeanScore(recommendations).stream().distinct().collect(Collectors.toList());
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
					meanScore += recommendation1.getScore();
					meanRecommendationScore.setComposedScore(recommendation1.getRecommendationScore().getComposeDScore());
				}
			}
			meanRecommendationScore.setScoreValue(meanScore / numberDuplicates);
			meanScoreRecommendation.setScore(meanRecommendationScore);
			filteredRecommendations.add(meanScoreRecommendation);
		}


		return filteredRecommendations;
	}
}
