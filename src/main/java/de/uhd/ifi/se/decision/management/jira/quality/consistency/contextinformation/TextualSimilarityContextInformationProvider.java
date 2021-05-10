package de.uhd.ifi.se.decision.management.jira.quality.consistency.contextinformation;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import de.uhd.ifi.se.decision.management.jira.classification.preprocessing.Preprocessor;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.quality.consistency.suggestions.LinkSuggestion;

/**
 * Rates relations comparing textual information of {@link KnowledgeElement}s.
 * It assumes a strong relation if they have the same name or if the name of the
 * element is mentioned in the description of another element.
 */
public class TextualSimilarityContextInformationProvider extends ContextInformationProvider {

	@Override
	public String getId() {
		return "TextualSimilarityCIP_jaccard";
	}

	@Override
	public void assessRelation(KnowledgeElement baseElement, List<KnowledgeElement> knowledgeElements) {
		Preprocessor preprocessor = Preprocessor.getInstance();
		try {
			String[] stemmedI1Description = preprocessor.getStemmedTokensWithoutStopWords(baseElement.getDescription());
			int uniqueE1Elements = uniqueElements(stemmedI1Description).length;
			this.linkSuggestions = knowledgeElements.parallelStream().map(knowledgeElement -> {
				LinkSuggestion linkSuggestion = new LinkSuggestion(baseElement, knowledgeElement);

				try {
					String[] stemmedI2Description = preprocessor
							.getStemmedTokensWithoutStopWords(knowledgeElement.getDescription());
					String[] concatenatedList = new String[stemmedI1Description.length + stemmedI2Description.length];

					concatenatedList = concatenate(stemmedI1Description, stemmedI2Description);

					int unionCount = uniqueElements(concatenatedList).length;

					// Jaccard similarity: (|A| + |B| - |A u B|) / |A u B|

					linkSuggestion
							.addToScore((uniqueE1Elements + uniqueElements(stemmedI2Description).length - unionCount)
									/ (double) unionCount, this.getName() + ": " + getId());
				} catch (Exception e) {
					linkSuggestion.addToScore(0., this.getName() + ": " + getId());
				}
				return linkSuggestion;

			}).collect(Collectors.toList());

		} catch (Exception e) {
			this.linkSuggestions = knowledgeElements.parallelStream().map(element -> {
				LinkSuggestion ls = new LinkSuggestion(baseElement, element);
				ls.addToScore(0., this.getName());
				return ls;
			}).collect(Collectors.toList());
		}

	}

	private String[] uniqueElements(CharSequence[] list) {
		Set<CharSequence> hashedArray = Set.of(list);
		return hashedArray.toArray(String[]::new);
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
