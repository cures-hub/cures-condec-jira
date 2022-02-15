package de.uhd.ifi.se.decision.management.jira.recommendation.linkrecommendation.contextinformation;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.recommendation.RecommendationScore;

/**
 * Rates relations based on time of creation or modifications of elements.
 * Elements with a similar creation or modification time are stronger related
 * than elements with a quite different modification or creation time.
 */
public class TimeContextInformationProvider extends ContextInformationProvider {

	public TimeContextInformationProvider() {
		super();
		isActive = false;
	}

	@Override
	public RecommendationScore assessRelation(KnowledgeElement baseElement, KnowledgeElement elementToTest) {
		Set<Date> coupledUpdates = new HashSet<>();
		for (Date rootElementUpdate : baseElement.getUpdateDateAndAuthor().keySet()) {
			// 600000ms equals 10 minutes, as such when an element was updated either 10
			// minutes before or after the source element coupling will be assumed
			coupledUpdates = elementToTest.getUpdateDateAndAuthor().keySet().stream()
					.filter(updateDate -> updateDate.getTime() > (rootElementUpdate.getTime() - 600000)
							&& updateDate.getTime() < (rootElementUpdate.getTime() + 600000))
					.collect(Collectors.toSet());
		}
		double score = 0.0;
		if (!coupledUpdates.isEmpty()) {
			score = 0.3 * coupledUpdates.size();
		}
		return score >= 1.0 ? new RecommendationScore((float) 1.0, getName() + " (ms)")
			: new RecommendationScore((float) score, getName() + " (ms)");
	}

	@Override
	public String getExplanation() {
		return "Assumes that knowledge elements created/updated during a similar time are related.";
	}

	@Override
	public String getDescription() {
		return "Recommend elements that are timely coupled to the source element.";
	}
}