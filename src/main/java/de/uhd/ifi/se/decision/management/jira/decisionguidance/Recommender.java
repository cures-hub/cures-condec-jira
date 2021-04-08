package de.uhd.ifi.se.decision.management.jira.decisionguidance;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.atlassian.jira.user.ApplicationUser;

import de.uhd.ifi.se.decision.management.jira.model.DocumentationLocation;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeStatus;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.persistence.ConfigPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.persistence.KnowledgePersistenceManager;

/**
 * Takes the input from the UI and passes it to the knowledge sources.
 */
public abstract class Recommender {

	protected KnowledgeSource knowledgeSource;

	public KnowledgeSource getKnowledgeSource() {
		return knowledgeSource;
	}

	public abstract void setKnowledgeSource(KnowledgeSource knowledgeSource);

	public abstract List<Recommendation> getRecommendations(String keywords);

	public List<Recommendation> getRecommendations(String keywords, KnowledgeElement decisionProblem) {
		List<Recommendation> recommendations = new ArrayList<>();
		recommendations.addAll(getRecommendations(keywords));
		recommendations.addAll(getRecommendations(decisionProblem));
		return recommendations;
	}

	public List<Recommendation> getRecommendations(KnowledgeElement decisionProblem) {
		if (decisionProblem == null) {
			return new ArrayList<>();
		}
		List<Recommendation> recommendations = new ArrayList<>();
		for (KnowledgeElement linkedElement : decisionProblem.getLinkedSolutionOptions()) {
			List<Recommendation> recommendationFromAlternative = getRecommendations(linkedElement.getSummary());
			recommendations.addAll(recommendationFromAlternative);
		}

		return recommendations.stream().distinct().collect(Collectors.toList());
	}

	public static List<Recommendation> getAllRecommendations(String projectKey, KnowledgeElement decisionProblem,
			String keywords) {
		DecisionGuidanceConfiguration config = ConfigPersistenceManager.getDecisionGuidanceConfiguration(projectKey);
		List<KnowledgeSource> knowledgeSources = config.getAllActivatedKnowledgeSources();
		return getAllRecommendations(knowledgeSources, decisionProblem, keywords);
	}

	public static List<Recommendation> getAllRecommendations(List<KnowledgeSource> knowledgeSources,
			KnowledgeElement decisionProblem, String keywords) {
		List<Recommendation> recommendations = new ArrayList<>();
		for (KnowledgeSource knowledgeSource : knowledgeSources) {
			Recommender recommender = RecommenderFactory.getRecommender(knowledgeSource);
			recommender.knowledgeSource = knowledgeSource;
			recommendations.addAll(recommender.getRecommendations(keywords, decisionProblem));
		}
		return recommendations.stream().distinct().collect(Collectors.toList());
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

	/**
	 * Adds all recommendation to the knowledge graph with the status "recommended".
	 * The recommendations will be appended to the root element
	 *
	 * @param rootElement
	 * @param user
	 * @param projectKey
	 */
	public static void addToKnowledgeGraph(KnowledgeElement rootElement, ApplicationUser user, String projectKey,
			List<Recommendation> recommendations) {
		KnowledgePersistenceManager manager = KnowledgePersistenceManager.getOrCreate(projectKey);
		int id = 0;
		for (Recommendation recommendation : recommendations) {
			KnowledgeElement alternative = new KnowledgeElement();

			// Set information
			alternative.setId(id++);
			alternative.setSummary(recommendation.getSummary());
			alternative.setType(KnowledgeType.ALTERNATIVE);
			alternative.setDescription("");
			alternative.setProject(projectKey);
			alternative.setDocumentationLocation(DocumentationLocation.JIRAISSUETEXT);
			alternative.setStatus(KnowledgeStatus.RECOMMENDED);

			KnowledgeElement insertedElement = manager.insertKnowledgeElement(alternative, user, rootElement);
			manager.insertLink(rootElement, insertedElement, user);
		}
	}

}
