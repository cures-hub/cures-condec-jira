package de.uhd.ifi.se.decision.management.jira.recommendation.linkrecommendation.contextinformation;

import org.apache.commons.text.similarity.JaccardSimilarity;

import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.recommendation.RecommendationScore;
import de.uhd.ifi.se.decision.management.jira.recommendation.linkrecommendation.LinkRecommendation;

/**
 * Rates relations comparing textual information of {@link KnowledgeElement}s.
 * It assumes a strong relation if they have the same name or if the name of the
 * element is mentioned in the description of another element.
 */
public class TextualSimilarityContextInformationProvider extends ContextInformationProvider {

	@Override
	public RecommendationScore assessRelation(KnowledgeElement baseElement, KnowledgeElement elementToTest) {
		LinkRecommendation linkSuggestion = new LinkRecommendation(baseElement, elementToTest);
		double similarity = calculateSimilarity(baseElement.getDescription(), elementToTest.getDescription());
		linkSuggestion.addToScore(similarity, getName() + " (Jaccard)");
		this.linkSuggestions.add(linkSuggestion);
		return new RecommendationScore((float) similarity, getName() + " (Jaccard)");
	}

	public double calculateSimilarity(String left, String right) {
		if (left == null || right == null) {
			return 0;
		}
		return new JaccardSimilarity().apply(left, right);
	}
}
