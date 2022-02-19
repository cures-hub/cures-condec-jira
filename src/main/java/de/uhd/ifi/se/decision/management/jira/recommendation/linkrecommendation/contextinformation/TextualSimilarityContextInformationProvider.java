package de.uhd.ifi.se.decision.management.jira.recommendation.linkrecommendation.contextinformation;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.text.similarity.JaroWinklerDistance;
import org.apache.commons.text.similarity.SimilarityScore;

import de.uhd.ifi.se.decision.management.jira.classification.preprocessing.Preprocessor;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.recommendation.RecommendationScore;

/**
 * Rates relations comparing textual information of {@link KnowledgeElement}s.
 * It assumes a strong relation if they have a similar textual content (summary
 * and description).
 * 
 * @issue How can we measure the textual similarity of two texts?
 * @decision We tokenize the text, stem the tokens, and remove stop words! Then,
 *           we calculate a similarity score using Jaro-Winkler similarity and
 *           number of same tokens to measure the textual similarity of two
 *           texts!
 * @pro Easy calculation and reuse of the preprocessing steps of ConDec's
 *      automatic text classifier.
 * @con Does not take text semantics into account.
 * @alternative We could use vectorization of words using GloVE as for the
 *              automatic text classifier to measure the textual similarity of
 *              two texts.
 * @con Takes more computation effort.
 */
public class TextualSimilarityContextInformationProvider extends ContextInformationProvider {

	private static final SimilarityScore<Double> similarityScore = new JaroWinklerDistance();
	private static Preprocessor preprocessor = Preprocessor.getInstance();

	public TextualSimilarityContextInformationProvider() {
		super();
		weightValue = 2;
	}

	@Override
	public RecommendationScore assessRelation(KnowledgeElement baseElement, KnowledgeElement elementToTest) {
		return assessRelation(baseElement.getText(), elementToTest.getText());
	}

	/**
	 * Predicts the relationship between one text to another text. Higher values
	 * indicate a higher similarity. The value is called Context Relationship
	 * Indicator in the paper by Miesbauer and Weinreich.
	 *
	 * @param textA
	 *            e.g. summary and description of a {@link KnowledgeElement}.
	 * @param textB
	 *            e.g. summary and description of another {@link KnowledgeElement}.
	 * @return {@link RecommendationScore} including the predicted value of
	 *         relationship in [0, inf] and an explanation.
	 */
	public RecommendationScore assessRelation(String textA, String textB) {
		double similarity = calculateSimilarity(textA, textB);
		String explanation = getDescription() + " (Jaro-Winkler Similarity and number of same tokens)";
		RecommendationScore score = new RecommendationScore((float) similarity, explanation);
		if (similarity > 0.94) {
			score.setPotentialDuplicate(true);
			score.setExplanation(score.getExplanation() + " This element might be a potential duplicate!");
		}
		return score;
	}

	/**
	 * Preprocesses the text as stemmed tokens without stop words. Then, measures
	 * Jaro-Winkler similarity and number of intersecting words.
	 * 
	 * @param left
	 *            text first text to be compared.
	 * @param right
	 *            second text to be compared.
	 * @return similarity score based on Jaro-Winkler similarity and number of
	 *         intersecting words.
	 */
	public double calculateSimilarity(String left, String right) {
		if (left == null || right == null) {
			return 0;
		}
		String[] leftTokens = preprocessor.getStemmedTokensWithoutStopWords(left);
		String[] rightTokens = preprocessor.getStemmedTokensWithoutStopWords(right);
		int lengthOfLongestText = leftTokens.length > rightTokens.length ? leftTokens.length : rightTokens.length;
		if (lengthOfLongestText == 0) {
			return 0;
		}
		Set<String> intersection = new HashSet<>(Arrays.asList(leftTokens));
		intersection.retainAll(new HashSet<>(Arrays.asList(rightTokens)));
		return similarityScore.apply(Arrays.toString(leftTokens), Arrays.toString(rightTokens))
				+ intersection.size() * 0.42 / lengthOfLongestText;
	}

	@Override
	public String getExplanation() {
		return "Assumes that textual similar knowledge elements are related.";
	}

	@Override
	public String getDescription() {
		return "Recommend elements that are textual similar to the source element";
	}
}
