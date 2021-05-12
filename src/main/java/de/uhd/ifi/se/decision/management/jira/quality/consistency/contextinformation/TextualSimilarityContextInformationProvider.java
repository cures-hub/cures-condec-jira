package de.uhd.ifi.se.decision.management.jira.quality.consistency.contextinformation;

import java.util.List;

import org.apache.commons.text.similarity.JaccardSimilarity;

import de.uhd.ifi.se.decision.management.jira.classification.preprocessing.Preprocessor;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.quality.consistency.suggestions.LinkSuggestion;

/**
 * Rates relations comparing textual information of {@link KnowledgeElement}s.
 * It assumes a strong relation if they have the same name or if the name of the
 * element is mentioned in the description of another element.
 */
public class TextualSimilarityContextInformationProvider extends ContextInformationProvider {

	private Preprocessor preprocessor;

	public TextualSimilarityContextInformationProvider() {
		super();
		preprocessor = Preprocessor.getInstance();
	}

	@Override
	public String getId() {
		return "TextualSimilarityCIP_jaccard";
	}

	@Override
	public void assessRelations(KnowledgeElement baseElement, List<KnowledgeElement> knowledgeElements) {
		for (KnowledgeElement elementToTest : knowledgeElements) {
			assessRelation(baseElement, elementToTest);
		}
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

	/**
	 * @param a
	 *            first array of double values
	 * @param b
	 *            second array of double values
	 * @return concatenated array of double values
	 */
	public static String[] concatenate(String[] a, String[] b) {
		int aLen = a.length;
		int bLen = b.length;
		String[] c = new String[aLen + bLen];
		System.arraycopy(a, 0, c, 0, aLen);
		System.arraycopy(b, 0, c, aLen, bLen);
		return c;
	}

}
