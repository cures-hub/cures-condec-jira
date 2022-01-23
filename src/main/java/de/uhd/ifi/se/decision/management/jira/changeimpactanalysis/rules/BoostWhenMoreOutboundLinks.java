package de.uhd.ifi.se.decision.management.jira.changeimpactanalysis.rules;

import de.uhd.ifi.se.decision.management.jira.filtering.FilterSettings;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeGraph;
import de.uhd.ifi.se.decision.management.jira.model.Link;

/**
 * Rule that defines that a change impact is stronger propagated if the
 * traversed {@link KnowledgeElement} in the {@link KnowledgeGraph} has more
 * outbound elements than inbound elements.
 */
public class BoostWhenMoreOutboundLinks implements ChangePropagationFunction {

	@Override
	public double isChangePropagated(FilterSettings filterSettings, KnowledgeElement nextElement, Link link) {
		// TODO
		float ruleWeight = 1;

		int outwardLinks = 0;
		int inwardLinks = 0;
		for (Link elementLink : nextElement.getLinks()) {
			if (elementLink.isOutwardLinkFrom(nextElement)) {
				outwardLinks = outwardLinks + 1;
			} else {
				inwardLinks = inwardLinks + 1;
			}
		}
		if (inwardLinks == 1 && outwardLinks == 0) {
			return 1.0;
		} else if (outwardLinks == 0) {
			return Math.pow(2, ((-1 * (double) inwardLinks) / 4)) * (2 - ruleWeight) >= 1.0 ? 1.0
					: Math.pow(2, ((-1 * (double) inwardLinks) / 4)) * (2 - ruleWeight);
		} else {
			return (double) outwardLinks / (inwardLinks + outwardLinks) * (2 - ruleWeight) >= 1.0 ? 1.0
					: (double) outwardLinks / (inwardLinks + outwardLinks) * (2 - ruleWeight);
		}
	}
}
