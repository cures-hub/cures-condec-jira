package de.uhd.ifi.se.decision.management.jira.quality.consistency.contextinformation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.quality.consistency.suggestions.LinkSuggestion;

/**
 * Rates relations based on time of creation or modifications of elements.
 * Elements with a similar creation or modification time are stronger related
 * than elements with a quite different modification or creation time. Also just
 * recently created decisions are rated higher then others. (Miesbauer and
 * Weinreich, 2012)
 */
public class TimeContextInformationProvider implements ContextInformationProvider {
	private String id = "TimeCIP_ms";
	private String name = "TimeCIP";
	private Collection<LinkSuggestion> linkSuggestions;

	public TimeContextInformationProvider() {
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
		for (KnowledgeElement elementToTest : knowledgeElements) {
			LinkSuggestion linkSuggestion = new LinkSuggestion(baseElement, elementToTest);
			double differenceInWeeks = (baseElement.getCreationDate().getTime()
					- elementToTest.getCreationDate().getTime()) / (1000 * 60 * 60 * 24. * 7.);
			double score = (1. / (Math.abs(differenceInWeeks) + 1.));
			linkSuggestion.addToScore(score, this.getName() + ": " + this.getId());
			this.linkSuggestions.add(linkSuggestion);
		}
	}
}
