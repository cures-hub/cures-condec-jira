package de.uhd.ifi.se.decision.management.jira.changeimpactanalysis.rules;

import de.uhd.ifi.se.decision.management.jira.filtering.FilterSettings;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeGraph;
import de.uhd.ifi.se.decision.management.jira.model.Link;

/**
 * Rule that defines that a change impact is stronger propagated if the
 * traversed {@link KnowledgeElement} in the {@link KnowledgeGraph} has a larger
 * amount of distinct update authors.
 */
public class AmountOfDistinctAuthors implements ChangePropagationFunction {
    
    /**
	 * @issue Should the amount of distinct authors for the purpose of CIA be filtered?
	 * @alternative Don't filter the authors, use all authors who applied any kind of update onto an artifact.
     * @pro Low implementation effort.
     * @con Authors who only applied minor updates and thereby shouldn't be included,
     *      e.g. if they only fixed a typo, are counted as distinct authors.
	 * @alternative Filter the authors based on what kind of update they applied onto an artifact.
	 * @con Difficult to determine how they should be filtered. Both normal JIRA issues and Code Files
     *      would need to be filtered in their own distinct way.
	 */
    @Override
    public double isChangePropagated(FilterSettings filterSettings, KnowledgeElement nextElement, Link link) {
        return 1.0 - (0.1 / nextElement.getUpdateDateAndAuthor().values().stream().distinct().count());
    }
}
