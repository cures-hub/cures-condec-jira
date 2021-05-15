package de.uhd.ifi.se.decision.management.jira.recommendation.decisionguidance.projectsource;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import de.uhd.ifi.se.decision.management.jira.model.Argument;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeGraph;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.model.SolutionOption;
import de.uhd.ifi.se.decision.management.jira.persistence.ConfigPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.recommendation.Recommendation;
import de.uhd.ifi.se.decision.management.jira.recommendation.RecommendationScore;
import de.uhd.ifi.se.decision.management.jira.recommendation.decisionguidance.ElementRecommendation;
import de.uhd.ifi.se.decision.management.jira.recommendation.decisionguidance.Recommender;
import de.uhd.ifi.se.decision.management.jira.recommendation.linkrecommendation.contextinformation.TextualSimilarityContextInformationProvider;

/**
 * Queries another Jira project ({@link ProjectSource}) to generate
 * {@link ElementRecommendation}s.
 */
public class ProjectSourceRecommender extends Recommender<ProjectSource> {

	private static final TextualSimilarityContextInformationProvider similarityProvider = new TextualSimilarityContextInformationProvider();

	/**
	 * @param projectKey
	 *            of the current project (not of the external knowledge source).
	 * @param projectSource
	 *            {@link ProjectSource} instance.
	 */
	public ProjectSourceRecommender(String projectKey, ProjectSource projectSource) {
		super(projectKey, projectSource);
	}

	@Override
	public List<Recommendation> getRecommendations(String inputs) {
		if (inputs == null) {
			return new ArrayList<>();
		}
		List<Recommendation> recommendations = new ArrayList<>();
		List<KnowledgeElement> similarElements = findSimilarElements(inputs);
		similarElements.forEach(issue -> {
			issue.getLinkedElements(5).stream()
					.filter(element -> element.hasKnowledgeType(KnowledgeType.ALTERNATIVE, KnowledgeType.DECISION))
					.forEach(element -> {
						ElementRecommendation recommendation = new ElementRecommendation(element);
						recommendation.setKnowledgeSource(knowledgeSource);
						recommendation.addArguments(new SolutionOption(element).getArguments());

						RecommendationScore score = calculateScore(inputs, issue, recommendation.getArguments());
						recommendation.setScore(score);
						recommendations.add(recommendation);
					});
		});

		Recommendation.normalizeRecommendationScore(recommendations);

		return recommendations.stream().distinct().collect(Collectors.toList());
	}

	/**
	 * @return get all knowledge elements that are similar to the given input
	 */
	public List<KnowledgeElement> findSimilarElements(String text) {
		return findSimilarElements(text, KnowledgeGraph.getInstance(knowledgeSource.getProjectKey()).vertexSet());
	}

	/**
	 * @return get all knowledge elements that are similar to the given input
	 */
	public List<KnowledgeElement> findSimilarElements(String text, Collection<KnowledgeElement> inputElements) {
		double similarityThreshold = ConfigPersistenceManager.getDecisionGuidanceConfiguration(projectKey)
				.getSimilarityThreshold();
		return inputElements.stream()
				.filter(issue -> similarityProvider.calculateSimilarity(issue.getSummary(), text) > similarityThreshold)
				.collect(Collectors.toList());
	}

	private RecommendationScore calculateScore(String keywords, KnowledgeElement decisionProblem,
			List<Argument> arguments) {
		RecommendationScore score = new RecommendationScore(0,
				"<b>" + keywords + "</b> is similar to <b>" + decisionProblem.getSummary() + "</b>");
		score.addSubScore(similarityProvider.assessRelation(keywords, decisionProblem.getText()));

		for (Argument argument : arguments) {
			score.addSubScore(getRecommendationScoreForArgument(argument));
		}

		return score;
	}

	private RecommendationScore getRecommendationScoreForArgument(Argument argument) {
		if (argument.getType() == KnowledgeType.PRO || argument.getType() == KnowledgeType.ARGUMENT) {
			return new RecommendationScore(.1f, argument.getType() + " : " + argument.getSummary());
		}
		return new RecommendationScore(-.1f, argument.getType() + " : " + argument.getSummary());
	}
}
