package de.uhd.ifi.se.decision.management.jira.quality.consistency.contextinformation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import de.uhd.ifi.se.decision.management.jira.classification.preprocessing.Preprocessor;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.quality.consistency.suggestions.LinkSuggestion;

public class TextualSimilarityCIP implements ContextInformationProvider {
	private String id = "TextualSimilarityCIP_jaccard";
	private String name = "TextualSimilarityCIP";
	private Collection<LinkSuggestion> linkSuggestions;
	private Preprocessor pp;

	public TextualSimilarityCIP() {
		pp = Preprocessor.getInstance();
		this.linkSuggestions = new ArrayList<>();
	}

	@Override
	public String getId() {
		return this.id;
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public Collection<LinkSuggestion> getLinkSuggestions() {
		return this.linkSuggestions;
	}

	@Override
	public void assessRelation(KnowledgeElement baseElement, List<KnowledgeElement> knowledgeElements) {
		try {
			pp.preprocess(baseElement.getDescription());
			List<CharSequence> stemmedI1Description = pp.getTokens();
			int uniqueE1Elements = uniqueElements(stemmedI1Description).length;
			this.linkSuggestions = knowledgeElements.parallelStream().map(knowledgeElement -> {
				LinkSuggestion linkSuggestion = new LinkSuggestion(baseElement, knowledgeElement);

				try {

					pp.preprocess(knowledgeElement.getDescription());
					List<CharSequence> stemmedI2Description = pp.getTokens();
					List<CharSequence> concatenatedList = new ArrayList<>();
					concatenatedList.addAll(stemmedI1Description);
					concatenatedList.addAll(stemmedI2Description);

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

	private String[] uniqueElements(List<CharSequence> list) {
		HashSet<CharSequence> hashedArray = new HashSet<CharSequence>(list);
		return hashedArray.toArray(String[]::new);
	}

}
