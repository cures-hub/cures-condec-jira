package de.uhd.ifi.se.decision.management.jira.quality.consistency.contextinformation;

import org.apache.commons.text.similarity.JaccardSimilarity;

import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.quality.consistency.suggestions.LinkSuggestion;

/**
 * Rates relations comparing textual information of {@link KnowledgeElement}s.
 * It assumes a strong relation if they have the same name or if the name of the
 * element is mentioned in the description of another element.
 */
public class TextualSimilarityContextInformationProvider extends ContextInformationProvider {

	public TextualSimilarityContextInformationProvider() {
		super();
	}

	@Override
	public String getId() {
		return "TextualSimilarityCIP_jaccard";
	}

	@Override
	public double assessRelation(KnowledgeElement baseElement, KnowledgeElement elementToTest) {
		LinkSuggestion linkSuggestion = new LinkSuggestion(baseElement, elementToTest);
		double similarity = calculateSimilarity(baseElement.getDescription(), elementToTest.getDescription());
		linkSuggestion.addToScore(similarity, this.getName() + ": " + getId());
		this.linkSuggestions.add(linkSuggestion);
		return similarity;
	}

	public double calculateSimilarity(String left, String right) {
		return new JaccardSimilarity().apply(left, right);
	}
}
