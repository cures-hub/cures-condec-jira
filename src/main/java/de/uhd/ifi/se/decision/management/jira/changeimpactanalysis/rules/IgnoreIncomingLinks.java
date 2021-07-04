package de.uhd.ifi.se.decision.management.jira.changeimpactanalysis.rules;

import de.uhd.ifi.se.decision.management.jira.filtering.FilterSettings;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.Link;

/**
 * Rule that defines that a change impact is not propagated along an incoming
 * link to an element. With this rule activated, impacts are only propagted
 * along outgoing links from an element.
 * 
 * For example, if decision A -> decision B, the change is propageted from
 * decision A to decision B but not from decision B to decision A.
 */
public class IgnoreIncomingLinks implements ChangePropagationFunction {

	@Override
	public double isChangePropagated(FilterSettings filterSettings, KnowledgeElement currentElement, Link link) {
		return link.isInwardLinkTo(currentElement) ? 0.0 : 1.0;
	}
}
