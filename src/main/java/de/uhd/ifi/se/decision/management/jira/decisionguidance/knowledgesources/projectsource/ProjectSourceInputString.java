package de.uhd.ifi.se.decision.management.jira.decisionguidance.knowledgesources.projectsource;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.text.similarity.JaroWinklerDistance;
import org.apache.commons.text.similarity.SimilarityScore;

import de.uhd.ifi.se.decision.management.jira.classification.preprocessing.Preprocessor;
import de.uhd.ifi.se.decision.management.jira.decisionguidance.Recommendation;
import de.uhd.ifi.se.decision.management.jira.decisionguidance.RecommendationScore;
import de.uhd.ifi.se.decision.management.jira.decisionguidance.knowledgesources.BagOfIrrelevantWords;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.persistence.ConfigPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.view.decisiontable.Argument;

/**
 * Queries another Jira project for a given String (for keyword-based search).
 * 
 * For example, a decision problem can be input by the user and used to query
 * the other Jira project.
 */
public class ProjectSourceInputString extends ProjectSourceInput<String> {

	private static final SimilarityScore<Double> similarityScore = new JaroWinklerDistance();

	@Override
	public List<Recommendation> getRecommendations(String inputs) {
		List<Recommendation> recommendations = new ArrayList<>();

		double similarityThreshold = ConfigPersistenceManager.getDecisionGuidanceConfiguration(projectKey)
				.getSimilarityThreshold();

		this.queryDatabase();
		if (knowledgeElements == null || inputs == null)
			return recommendations;

		// get all issues that are similar to the given input
		knowledgeElements.forEach(issue -> {
			if (calculateSimilarity(issue.getSummary(), inputs.trim()) <= similarityThreshold) {
				return;
			}
			issue.getLinkedElements(5).stream()
					.filter(element -> element.hasKnowledgeType(KnowledgeType.ALTERNATIVE, KnowledgeType.DECISION))
					.forEach(element -> {
						Recommendation recommendation = new Recommendation(element);
						recommendation.setKnowledgeSource(knowledgeSource);
						recommendation.addArguments(element.getLinkedArguments());

						RecommendationScore score = calculateScore(inputs, issue, recommendation.getLinkedArguments());
						recommendation.setScore(score);
						recommendations.add(recommendation);
					});
		});

		return recommendations.stream().distinct().collect(Collectors.toList());
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
			if (argument.getType().equals(KnowledgeType.PRO.toString())) {
				numberProArguments += 1;
				score.addSubScore(new RecommendationScore(.01f, argument.getType() + " : " + argument.getSummary()));
			}
			if (argument.getType().equals(KnowledgeType.CON.toString())) {
				numberConArguments += 1;
				score.addSubScore(new RecommendationScore(-.01f, argument.getType() + " : " + argument.getSummary()));
			}
		}

		float argumentWeight = .1f; // TODO make the weight of an argument changeable in the UI

		float scoreJC = ((float) jc + ((numberProArguments - numberConArguments) * argumentWeight))
				/ (1 + arguments.size() * argumentWeight) * 100f; // TODO find better formula

		score.setValue(scoreJC);

		return score;
	}

	private Double calculateSimilarity(String left, String right) {
		return similarityScore.apply(cleanInput(left), cleanInput(right));
	}

	private String cleanInput(String input) {
		String[] tokens = Preprocessor.getInstance().getStemmedTokensWithoutStopWords(input);
		BagOfIrrelevantWords bagOfIrrelevantWords = new BagOfIrrelevantWords(
				ConfigPersistenceManager.getDecisionGuidanceConfiguration(projectKey).getIrrelevantWords());
		return bagOfIrrelevantWords.cleanSentence(tokens);
	}

}
