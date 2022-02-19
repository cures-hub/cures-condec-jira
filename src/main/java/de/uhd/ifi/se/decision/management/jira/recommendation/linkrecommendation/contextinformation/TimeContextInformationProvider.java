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
 *
 * @issue Which time interval should be used to assume whether two knowledge
 *        elements are timely coupled?
 * @decision We assume that two elements are timely coupled if they were both
 *           modified within a time interval of 30 minutes!
 * @pro Easy solution to keep it simple and stupid.
 * @con Arbitrary number that should be evaluated.
 * @alternative We could enable the rationale manager to configure the interval
 *              for timely coupling.
 * @con Might not bring a big advantage for the results of link recommendation
 *      and CIA. Needs to be evaluated first.
 */
public class TimeContextInformationProvider extends ContextInformationProvider {

	public static int deltaTime = 30 * 60 * 1000; // ms for 30 minutes

	public TimeContextInformationProvider() {
		super();
		isActive = false;
	}

	@Override
	public RecommendationScore assessRelation(KnowledgeElement baseElement, KnowledgeElement elementToTest) {
		Set<Date> coupledUpdates = new HashSet<>();

		for (Date rootElementUpdate : baseElement.getUpdateDateAndAuthor().keySet()) {
			coupledUpdates.addAll(elementToTest.getUpdateDateAndAuthor().keySet().stream()
					.filter(updateDate -> areDatesSimilar(updateDate, rootElementUpdate, deltaTime))
					.collect(Collectors.toSet()));
		}
		float score = 0.3f * coupledUpdates.size();
		if (score > 1.0) {
			score = 1.0f;
		}
		return new RecommendationScore(score, getName() + " (ms)");
	}

	/**
	 * @param date1
	 *            first date object.
	 * @param date2
	 *            second date object.
	 * @param deltaTime
	 *            time interval in ms.
	 * @return true if the first date is either before or after the second date
	 *         within the time interval.
	 */
	public static boolean areDatesSimilar(Date date1, Date date2, int deltaTime) {
		return date1.getTime() >= (date2.getTime() - deltaTime) && date1.getTime() <= (date2.getTime() + deltaTime);
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