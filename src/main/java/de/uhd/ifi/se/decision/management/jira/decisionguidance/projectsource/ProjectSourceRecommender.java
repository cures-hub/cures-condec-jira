package de.uhd.ifi.se.decision.management.jira.decisionguidance.projectsource;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.text.similarity.JaroWinklerDistance;
import org.apache.commons.text.similarity.SimilarityScore;

import de.uhd.ifi.se.decision.management.jira.classification.preprocessing.Preprocessor;
import de.uhd.ifi.se.decision.management.jira.decisionguidance.BagOfIrrelevantWords;
import de.uhd.ifi.se.decision.management.jira.decisionguidance.Recommendation;
import de.uhd.ifi.se.decision.management.jira.decisionguidance.RecommendationScore;
import de.uhd.ifi.se.decision.management.jira.decisionguidance.Recommender;
import de.uhd.ifi.se.decision.management.jira.model.Argument;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeGraph;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.model.SolutionOption;
import de.uhd.ifi.se.decision.management.jira.persistence.ConfigPersistenceManager;

/**
 * Queries another Jira project ({@link ProjectSource}) to generate
 * {@link Recommendation}s.
 */
public class ProjectSourceRecommender extends Recommender<ProjectSource> {

	private static final SimilarityScore<Double> similarityScore = new JaroWinklerDistance();

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
						Recommendation recommendation = new Recommendation(element);
						recommendation.setKnowledgeSource(knowledgeSource);
						recommendation.addArguments(new SolutionOption(element).getArguments());

						RecommendationScore score = calculateScore(inputs, issue, recommendation.getArguments());
						recommendation.setScore(score);
						recommendations.add(recommendation);
					});
		});

		return recommendations.stream().distinct().collect(Collectors.toList());
	}

	/**
	 * @return get all knowledge elements that are similar to the given input
	 */
	public List<KnowledgeElement> findSimilarElements(String text) {
		return findSimilarElements(text, KnowledgeGraph.getInstance(projectKey).vertexSet());
	}

	/**
	 * @return get all knowledge elements that are similar to the given input
	 */
	public List<KnowledgeElement> findSimilarElements(String text, Collection<KnowledgeElement> inputElements) {
		double similarityThreshold = ConfigPersistenceManager.getDecisionGuidanceConfiguration(projectKey)
				.getSimilarityThreshold();
		return inputElements.stream()
				.filter(issue -> calculateSimilarity(issue.getSummary(), text) > similarityThreshold)
				.collect(Collectors.toList());
	}

	private RecommendationScore calculateScore(String keywords, KnowledgeElement decisionProblem,
			List<Argument> arguments) {

		RecommendationScore score = new RecommendationScore(0,
				"Similarity based on " + similarityScore.getClass().getName());

		double jc = calculateSimilarity(keywords, decisionProblem.getSummary());
		score.addSubScore(new RecommendationScore((float) jc,
				"<b>" + keywords + "</b> is similar to <b>" + decisionProblem.getSummary() + "</b>"));

		float numberProArguments = 0;
		float numberConArguments = 0;

		for (Argument argument : arguments) {
			if (argument.getType() == KnowledgeType.PRO || argument.getType() == KnowledgeType.ARGUMENT) {
				numberProArguments += 1;
				score.addSubScore(new RecommendationScore(.1f, argument.getType() + " : " + argument.getSummary()));
			}
			if (argument.getType() == KnowledgeType.CON) {
				numberConArguments += 1;
				score.addSubScore(new RecommendationScore(-.1f, argument.getType() + " : " + argument.getSummary()));
			}
		}

		float argumentWeight = .1f; // TODO make the weight of an argument changeable in the UI

		float scoreJC = ((float) jc + (numberProArguments - numberConArguments) * argumentWeight)
				/ (1 + arguments.size() * argumentWeight) * 100f; // TODO find better formula

		score.setValue(scoreJC);

		return score;
	}

	public double calculateSimilarity(String left, String right) {
		return similarityScore.apply(cleanInput(left), cleanInput(right));
	}

	private String cleanInput(String input) {
		String[] tokens = Preprocessor.getInstance().getStemmedTokensWithoutStopWords(input);
		BagOfIrrelevantWords bagOfIrrelevantWords = new BagOfIrrelevantWords(
				ConfigPersistenceManager.getDecisionGuidanceConfiguration(projectKey).getIrrelevantWords());
		return bagOfIrrelevantWords.cleanSentence(tokens);
	}

}
