package de.uhd.ifi.se.decision.management.jira.changeimpactanalysis.rules;

import de.uhd.ifi.se.decision.management.jira.filtering.FilterSettings;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeGraph;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.model.Link;

/**
 * Rule that defines that a change impact is not propagated after a
 * {@link KnowledgeElement} with the same {@link KnowledgeType} was reached.
 * 
 * For example, if a change is made in an epic, the change is not propagated
 * beyond other epics in the {@link KnowledgeGraph}.
 */
public class StopAtSameElementType implements ChangePropagationFunction {

	@Override
	public double isChangePropagated(FilterSettings filterSettings, KnowledgeElement currentElement, Link link) {
		KnowledgeElement traversedElement = link.getOppositeElement(currentElement);
		return traversedElement.getTypeAsString().equals(filterSettings.getSelectedElement().getTypeAsString()) ? 0.0
				: 1.0;
	}
}
