package de.uhd.ifi.se.decision.management.jira.recommendation.linkrecommendation.contextinformation;

import java.util.ArrayList;
import java.util.List;

import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.recommendation.Recommendation;
import de.uhd.ifi.se.decision.management.jira.recommendation.RecommendationScore;

/**
 * Superclass for different context information providers to realize context
 * utility functions. For example, the {@link TimeContextInformationProvider}
 * rates relations based on time of creation or modifications of elements.
 * 
 * This abstract class is part of the Decorator design pattern. It is the
 * abstract decorator and the concrete decorators are the subclasses, such as
 * {@link TimeContextInformationProvider},
 * {@link UserContextInformationProvider}, and
 * {@link TextualSimilarityContextInformationProvider}.
 *
 * @implSpec C. Miesbauer and R. Weinreich, "Capturing and Maintaining
 *           Architectural Knowledge Using Context Information", 2012 Joint
 *           Working IEEE/IFIP Conference on Software Architecture and European
 *           Conference on Software Architecture
 */
public abstract class ContextInformationProvider {

	protected List<Recommendation> linkSuggestions;

	public ContextInformationProvider() {
		this.linkSuggestions = new ArrayList<>();
	}

	/**
	 * @return name of the context information provider
	 */
	public String getName() {
		return this.getClass().getSimpleName();
	}

	public List<Recommendation> getLinkSuggestions() {
		return linkSuggestions;
	}

	/**
	 * Calculates the relationship between one {@link KnowledgeElement} to a list of
	 * other {@link KnowledgeElement}s. Higher values indicate a higher similarity.
	 * The value is called Context Relationship Indicator in the paper.
	 *
	 * @param baseElement
	 * @param knowledgeElements
	 * @return value of relationship in [0, inf]
	 */
	public void assessRelations(KnowledgeElement baseElement, List<KnowledgeElement> knowledgeElements) {
		for (KnowledgeElement elementToTest : knowledgeElements) {
			assessRelation(baseElement, elementToTest);
		}
	}

	public abstract RecommendationScore assessRelation(KnowledgeElement baseElement, KnowledgeElement knowledgeElement);

}
