package de.uhd.ifi.se.decision.management.jira.decisionguidance.knowledgesources;

import java.util.List;

import de.uhd.ifi.se.decision.management.jira.decisionguidance.Recommendation;
import de.uhd.ifi.se.decision.management.jira.decisionguidance.knowledgesources.projectsource.ProjectSource;
import de.uhd.ifi.se.decision.management.jira.decisionguidance.knowledgesources.projectsource.ProjectSourceInputKnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.decisionguidance.knowledgesources.projectsource.ProjectSourceInputString;
import de.uhd.ifi.se.decision.management.jira.decisionguidance.knowledgesources.rdfsource.RDFSource;
import de.uhd.ifi.se.decision.management.jira.decisionguidance.knowledgesources.rdfsource.RDFSourceInputKnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.decisionguidance.knowledgesources.rdfsource.RDFSourceInputString;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;

/**
 * Calculates the recommendations based on a given input.
 * 
 * @param <T>
 *            type of input that is used to query the knowledge source, e.g.
 *            String or {@link KnowledgeElement}.
 * @param <E>
 *            type of the knowledge source, e.g. {@link RDFSource} or
 *            {@link ProjectSource}.
 */
public interface InputMethod<T, E extends KnowledgeSource> {

	/**
	 * @param input
	 * @return a list of {@link Recommendation}s.
	 */
	List<Recommendation> getRecommendations(T input);

	/**
	 * Sets the knowledge source that contains the data to calculate
	 * recommendations.
	 * 
	 * @param knowledgeSource
	 *            subclass of {@link KnowledgeSource}, e.g. {@link RDFSource} or
	 *            {@link ProjectSource}.
	 */
	void setKnowledgeSource(E knowledgeSource);

	static InputMethod getIssueBasedIn(KnowledgeSource knowledgeSource) {
		InputMethod inputMethod = null;
		if (knowledgeSource instanceof ProjectSource) {
			inputMethod = new ProjectSourceInputKnowledgeElement();
		} else {
			inputMethod = new RDFSourceInputKnowledgeElement();
		}
		inputMethod.setKnowledgeSource(knowledgeSource);
		return inputMethod;
	}

	static InputMethod getKeywordBasedIn(KnowledgeSource knowledgeSource) {
		InputMethod inputMethod = null;
		if (knowledgeSource instanceof ProjectSource) {
			inputMethod = new ProjectSourceInputString();
		} else {
			inputMethod = new RDFSourceInputString();
		}
		inputMethod.setKnowledgeSource(knowledgeSource);
		return inputMethod;
	}

}
