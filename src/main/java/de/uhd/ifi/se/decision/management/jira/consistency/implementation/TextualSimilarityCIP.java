package de.uhd.ifi.se.decision.management.jira.consistency.implementation;

import com.atlassian.jira.issue.Issue;
import de.uhd.ifi.se.decision.management.jira.classification.preprocessing.Preprocessor;
import de.uhd.ifi.se.decision.management.jira.consistency.ContextInformationProvider;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class TextualSimilarityCIP implements ContextInformationProvider {
	private String id = "TextualSimilarityCIP_jaccard";
	private String name = "TextualSimilarityCIP";

	private Preprocessor pp;

	public TextualSimilarityCIP() {
		pp = new Preprocessor();
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
	public double assessRelation(Issue i1, Issue i2) {
		try {
			pp.preprocess(i1.getDescription().toLowerCase());
			List<CharSequence> stemmedI1Description = pp.getTokens();

			pp.preprocess(i2.getDescription().toLowerCase());
			List<CharSequence> stemmedI2Description = pp.getTokens();
			List<CharSequence> concatenatedList = new ArrayList<>();
			concatenatedList.addAll(stemmedI1Description);
			concatenatedList.addAll(stemmedI2Description);

			int unionCount = uniqueElements(concatenatedList).length;

			// Jaccard similarity: (|A| + |B| - |A u B|) / |A u B|
			return (uniqueElements(stemmedI1Description).length + uniqueElements(stemmedI2Description).length - unionCount) / (double) unionCount;
		} catch (Exception e) {
			return 0;
		}

	}

	private String[] uniqueElements(List<CharSequence> list) {
		HashSet<CharSequence> hashedArray = new HashSet<CharSequence>(list);
		return hashedArray.toArray(String[]::new);
	}

}
