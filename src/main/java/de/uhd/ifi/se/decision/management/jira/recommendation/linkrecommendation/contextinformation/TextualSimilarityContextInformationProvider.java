package de.uhd.ifi.se.decision.management.jira.recommendation.linkrecommendation.contextinformation;

import java.util.Arrays;

import org.apache.commons.text.similarity.JaroWinklerDistance;
import org.apache.commons.text.similarity.SimilarityScore;

import de.uhd.ifi.se.decision.management.jira.classification.preprocessing.Preprocessor;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.recommendation.RecommendationScore;

/**
 * Rates relations comparing textual information of {@link KnowledgeElement}s.
 * It assumes a strong relation if they have the same name or if the name of the
 * element is mentioned in the description of another element.
 */
public class TextualSimilarityContextInformationProvider implements ContextInformationProvider {

	private static final SimilarityScore<Double> similarityScore = new JaroWinklerDistance();

	@Override
	public RecommendationScore assessRelation(KnowledgeElement baseElement, KnowledgeElement elementToTest) {
		double similarity = calculateSimilarity(baseElement.getDescription(), elementToTest.getDescription());
		return new RecommendationScore((float) similarity, getName() + " (JaroWinklerDistance)");
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
