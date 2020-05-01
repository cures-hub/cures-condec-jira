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
			pp.preprocess(i1.getDescription());
			List<String> lemmatizedI1Description = pp.getTokens();

			pp.preprocess(i2.getDescription());
			List<String> lemmatizedI2Description = pp.getTokens();
			List<String> concatenatedList = new ArrayList<>();
			concatenatedList.addAll(lemmatizedI1Description);
			concatenatedList.addAll(lemmatizedI2Description);

			int numIdenticalElements = uniqueElements(concatenatedList).length;
			int numTotalElements = uniqueElements(lemmatizedI1Description).length + uniqueElements(lemmatizedI2Description).length;

			// Jaccard Similarity:
			return (numTotalElements - numIdenticalElements) / (double) numIdenticalElements;
		} catch (Exception e) {
			return 0;
		}

	}

	private String[] uniqueElements(List<String> list) {
		HashSet<String> hashedArray = new HashSet<String>(list);
		return hashedArray.toArray(String[]::new);
	}

}
