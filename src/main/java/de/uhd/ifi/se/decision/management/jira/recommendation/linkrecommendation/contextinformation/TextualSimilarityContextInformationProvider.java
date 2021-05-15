package de.uhd.ifi.se.decision.management.jira.recommendation.linkrecommendation.contextinformation;

import java.util.Arrays;

import org.apache.commons.text.similarity.JaroWinklerDistance;
import org.apache.commons.text.similarity.SimilarityScore;

import de.uhd.ifi.se.decision.management.jira.classification.preprocessing.Preprocessor;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.recommendation.RecommendationScore;

/**
 * Rates relations comparing textual information of {@link KnowledgeElement}s.
 * It assumes a strong relation if they have a similar textual content (summary
 * and description).
 */
public class TextualSimilarityContextInformationProvider implements ContextInformationProvider {

	private static final SimilarityScore<Double> similarityScore = new JaroWinklerDistance();

	@Override
	public RecommendationScore assessRelation(KnowledgeElement baseElement, KnowledgeElement elementToTest) {
		return assessRelation(baseElement.getText(), elementToTest.getText());
	}

	public RecommendationScore assessRelation(String textA, String textB) {
		double similarity = calculateSimilarity(textA, textB);
		String explanation = getName() + " (JaroWinklerDistance)";
		return new RecommendationScore((float) similarity, explanation);
	}

	public double calculateSimilarity(String left, String right) {
		if (left == null || right == null) {
			return 0;
		}
		return similarityScore.apply(cleanInput(left), cleanInput(right));
	}

	private String cleanInput(String input) {
		String[] tokens = Preprocessor.getInstance().getStemmedTokensWithoutStopWords(input);
		return Arrays.toString(tokens);
	}
}
