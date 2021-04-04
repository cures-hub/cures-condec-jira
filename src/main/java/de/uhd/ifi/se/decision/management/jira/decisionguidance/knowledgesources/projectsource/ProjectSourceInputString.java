package de.uhd.ifi.se.decision.management.jira.decisionguidance.knowledgesources.projectsource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.text.similarity.JaroWinklerDistance;
import org.apache.commons.text.similarity.SimilarityScore;

import de.uhd.ifi.se.decision.management.jira.decisionguidance.Recommendation;
import de.uhd.ifi.se.decision.management.jira.decisionguidance.knowledgesources.BagOfIrrelevantWords;
import de.uhd.ifi.se.decision.management.jira.decisionguidance.score.RecommendationScore;
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

	private double THRESHOLD;
	private static final JaroWinklerDistance similarityScore = new JaroWinklerDistance();

	@Override
	public List<Recommendation> getRecommendations(String inputs) {
		List<Recommendation> recommendations = new ArrayList<>();

		THRESHOLD = ConfigPersistenceManager.getDecisionGuidanceConfiguration(projectKey).getSimilarityThreshold();

		this.queryDatabase();
		if (knowledgeElements == null || inputs == null)
			return recommendations;

		// get all issues that are similar to the given input
		knowledgeElements.forEach(issue -> {
			if (this.calculateSimilarity(similarityScore, issue.getSummary(), inputs.trim()) > THRESHOLD) {

				issue.getLinkedElements(5).stream().filter(
						element -> this.isMatchingIssueType(element, KnowledgeType.ALTERNATIVE, KnowledgeType.DECISION))
						.forEach(child -> {

							Recommendation recommendation = this.createRecommendation(child, KnowledgeType.ALTERNATIVE,
									KnowledgeType.DECISION);
							recommendation.addArguments(this.getArguments(child));

							if (recommendation != null) {
								RecommendationScore score = calculateScore(inputs, issue,
										recommendation.getArguments());
								recommendation.setScore(score);
								recommendations.add(recommendation);
							}

						});
			}
		});

		return recommendations.stream().distinct().collect(Collectors.toList());
	}

	private RecommendationScore calculateScore(String keywords, KnowledgeElement parentIssue,
			List<Argument> arguments) {

		RecommendationScore score = new RecommendationScore(0, "Similarity based on " + similarityScore.toString());

		double jc = this.calculateSimilarity(similarityScore, keywords, parentIssue.getSummary());
		score.composeScore(new RecommendationScore((float) jc,
				"<b>" + keywords + "</b> is similar to <b>" + parentIssue.getSummary() + "</b>"));

		float numberProArguments = 0;
		float numberConArguments = 0;

		for (Argument argument : arguments) {
			if (argument.getType().equals(KnowledgeType.PRO.toString())) {
				numberProArguments += 1;
				score.composeScore(new RecommendationScore(.01f, argument.getType() + " : " + argument.getSummary()));
			}
			if (argument.getType().equals(KnowledgeType.CON.toString())) {
				numberConArguments += 1;
				score.composeScore(new RecommendationScore(-.01f, argument.getType() + " : " + argument.getSummary()));
			}
		}

		float argumentWeight = .1f; // TODO make the weight of an argument changeable in the UI

		float scoreJC = ((float) jc + ((numberProArguments - numberConArguments) * argumentWeight))
				/ (1 + arguments.size() * argumentWeight) * 100f; // TODO find better formula

		score.setTotalScore(scoreJC);

		return score;
	}

	private <T> T calculateSimilarity(SimilarityScore<T> similarityScore, String left, String right) {
		T score = similarityScore.apply(cleanInput(left), cleanInput(right));
		return score;
	}

	private String cleanInput(String input) {
		List<String> inputTokens = Arrays.asList(input.split(" "));
		BagOfIrrelevantWords bagOfIrrelevantWords = new BagOfIrrelevantWords(
				ConfigPersistenceManager.getDecisionGuidanceConfiguration(projectKey).getIrrelevantWords());
		return bagOfIrrelevantWords.cleanSentence(inputTokens);
	}

	protected Recommendation createRecommendation(KnowledgeElement element, KnowledgeType... knowledgeTypes) {
		for (KnowledgeType knowledgeType : knowledgeTypes) {
			if (element.getType() == knowledgeType)
				return new Recommendation(this.knowledgeSource, element.getSummary(), element.getUrl());
		}

		return null;
	}

	protected boolean isMatchingIssueType(KnowledgeElement knowledgeElement, KnowledgeType... knowledgeTypes) {
		int numberOfMatchingTypes = 0;
		for (KnowledgeType knowledgeType : knowledgeTypes) {
			if (knowledgeElement.getType() == knowledgeType)
				numberOfMatchingTypes += 1;
		}

		return numberOfMatchingTypes > 0;
	}

	protected List<Argument> getArguments(KnowledgeElement knowledgeElement) {
		List<Argument> arguments = new ArrayList<>();

		for (KnowledgeElement element : knowledgeElement.getLinkedElements(1)) {
			if (element.getType().equals(KnowledgeType.PRO) || element.getType().equals(KnowledgeType.CON)) {
				arguments.add(new Argument(element));
			}
		}

		return arguments;
	}

}
