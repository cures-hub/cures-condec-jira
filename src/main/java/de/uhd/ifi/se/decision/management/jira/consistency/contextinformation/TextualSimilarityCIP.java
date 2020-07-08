package de.uhd.ifi.se.decision.management.jira.consistency.contextinformation;

import com.atlassian.jira.issue.Issue;
import de.uhd.ifi.se.decision.management.jira.classification.preprocessing.Preprocessor;
import de.uhd.ifi.se.decision.management.jira.consistency.suggestions.LinkSuggestion;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

public class TextualSimilarityCIP implements ContextInformationProvider {
	private String id = "TextualSimilarityCIP_jaccard";
	private String name = "TextualSimilarityCIP";
	private Collection<LinkSuggestion> linkSuggestions;
	private Preprocessor pp;

	public TextualSimilarityCIP() {
		pp = new Preprocessor();
		this.linkSuggestions = new ArrayList();
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
	public void assessRelation(Issue baseIssue, List<Issue> issuesToTest) {
		for (Issue issueToTest : issuesToTest){
			LinkSuggestion linkSuggestion = new LinkSuggestion(baseIssue, issueToTest);

			try {
				pp.preprocess(baseIssue.getDescription().toLowerCase());
				List<CharSequence> stemmedI1Description = pp.getTokens();

				pp.preprocess(issueToTest.getDescription().toLowerCase());
				List<CharSequence> stemmedI2Description = pp.getTokens();
				List<CharSequence> concatenatedList = new ArrayList<>();
				concatenatedList.addAll(stemmedI1Description);
				concatenatedList.addAll(stemmedI2Description);

				int unionCount = uniqueElements(concatenatedList).length;

				// Jaccard similarity: (|A| + |B| - |A u B|) / |A u B|

				linkSuggestion.addToScore(
					(uniqueElements(stemmedI1Description).length + uniqueElements(stemmedI2Description).length - unionCount)
					/ (double) unionCount,
					this.getName());
			} catch (Exception e) {
				linkSuggestion.addToScore(0., this.getName());
			}
			this.linkSuggestions.add(linkSuggestion);
		}

	}

	private String[] uniqueElements(List<CharSequence> list) {
		HashSet<CharSequence> hashedArray = new HashSet<CharSequence>(list);
		return hashedArray.toArray(String[]::new);
	}

}
