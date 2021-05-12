package de.uhd.ifi.se.decision.management.jira.quality.consistency.contextinformation;

import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.quality.consistency.suggestions.LinkSuggestion;

/**
 * Rates relations based on time of creation or modifications of elements.
 * Elements with a similar creation or modification time are stronger related
 * than elements with a quite different modification or creation time. Also just
 * recently created decisions are rated higher then others. (Miesbauer and
 * Weinreich, 2012)
 */
public class TimeContextInformationProvider extends ContextInformationProvider {

	@Override
	public String getId() {
		return "TimeCIP_ms";
	}

	@Override
	public double assessRelation(KnowledgeElement baseElement, KnowledgeElement elementToTest) {
		LinkSuggestion linkSuggestion = new LinkSuggestion(baseElement, elementToTest);
		double differenceInWeeks = (baseElement.getCreationDate().getTime() - elementToTest.getCreationDate().getTime())
				/ (1000 * 60 * 60 * 24. * 7.);
		double score = (1. / (Math.abs(differenceInWeeks) + 1.));
		linkSuggestion.addToScore(score, this.getName() + ": " + this.getId());
		this.linkSuggestions.add(linkSuggestion);
		return score;
	}
}
