package de.uhd.ifi.se.decision.management.jira.recommendation.linkrecommendation.contextinformation;

import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeGraph;
import de.uhd.ifi.se.decision.management.jira.recommendation.RecommendationScore;
import de.uhd.ifi.se.decision.management.jira.recommendation.linkrecommendation.LinkRecommendation;

/**
 * Interface for different context information providers to realize context
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
public interface ContextInformationProvider {

	/**
	 * @return name of the context information provider. Used as the explanation in
	 *         the {@link RecommendationScore}.
	 */
	default String getName() {
		return getClass().getSimpleName();
	}

	/**
	 * Predicts the relationship between one {@link KnowledgeElement} to a second
	 * {@link KnowledgeElement}. Higher values indicate a higher similarity. The
	 * value is called Context Relationship Indicator in the paper by Miesbauer and
	 * Weinreich.
	 *
	 * @param baseElement
	 *            {@link KnowledgeElement} for that new links should be recommended
	 *            (see {@link LinkRecommendation}).
	 * @param otherElement
	 *            another {@link KnowledgeElement} in the {@link KnowledgeGraph}
	 *            that is not directly linked.
	 * @return {@link RecommendationScore} including the predicted value of
	 *         relationship in [0, inf] and an explanation.
	 */
	public abstract RecommendationScore assessRelation(KnowledgeElement baseElement, KnowledgeElement otherElement);

}