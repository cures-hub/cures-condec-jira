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
import de.uhd.ifi.se.decision.management.jira.recommendation.RecommendationScore;
import de.uhd.ifi.se.decision.management.jira.recommendation.decisionguidance.ElementRecommendation;
import de.uhd.ifi.se.decision.management.jira.recommendation.decisionguidance.Recommender;
import de.uhd.ifi.se.decision.management.jira.recommendation.linkrecommendation.contextinformation.TextualSimilarityContextInformationProvider;

/**
 * Queries another Jira project ({@link ProjectSource}) to generate
 * {@link ElementRecommendation}s.
 */
@SuppressWarnings("PMD.FieldNamingConventions")  // For static code analysis: Per convention in the project,
//                                                  'similarityProvider' is not named like a constant
public class ProjectSourceRecommender extends Recommender<ProjectSource> {

	/**
	 * Provider used to evaluate the similarity of different texts.
	 */
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
	public List<ElementRecommendation> getRecommendations(String inputs) {
		List<ElementRecommendation> recommendations = new ArrayList<>();
		if (inputs != null) {
			List<KnowledgeElement> similarElements = findSimilarElements(inputs);
			similarElements.forEach(issue -> {
				issue.getLinkedElements(5).stream().filter(element -> element.hasKnowledgeType(KnowledgeType.ALTERNATIVE, KnowledgeType.DECISION))
						.forEach(element -> {
							ElementRecommendation recommendation = new ElementRecommendation(element);
							recommendation.setKnowledgeSource(knowledgeSource);
							recommendation.addArguments(new SolutionOption(element).getArguments());

							RecommendationScore score = calculateScore(inputs, issue, recommendation.getArguments());
							recommendation.setScore(score);
							recommendations.add(recommendation);
						});
			});
			ElementRecommendation.normalizeRecommendationScore(recommendations);
		}
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
		RecommendationScore score = similarityProvider.assessRelation(keywords, decisionProblem.getText());

		for (Argument argument : arguments) {
			score.addSubScore(getRecommendationScoreForArgument(argument));
		}

		return score;
	}

	private RecommendationScore getRecommendationScoreForArgument(Argument argument) {
		RecommendationScore score;
		if (argument.getType() == KnowledgeType.PRO || argument.getType() == KnowledgeType.ARGUMENT) {
			score = new RecommendationScore(.1f, argument.getType() + " : " + argument.getSummary());
		} else {
			score = new RecommendationScore(-.1f, argument.getType() + " : " + argument.getSummary());
		}
		return score;
	}
}
