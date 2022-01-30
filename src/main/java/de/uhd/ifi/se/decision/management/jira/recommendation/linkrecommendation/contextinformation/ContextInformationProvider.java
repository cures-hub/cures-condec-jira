package de.uhd.ifi.se.decision.management.jira.recommendation.linkrecommendation.contextinformation;

import javax.xml.bind.annotation.XmlElement;

import org.codehaus.jackson.annotate.JsonProperty;

import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeGraph;
import de.uhd.ifi.se.decision.management.jira.recommendation.RecommendationScore;
import de.uhd.ifi.se.decision.management.jira.recommendation.linkrecommendation.LinkRecommendation;

/**
 * Abstract class for different context information providers (=link
 * recommendation rules) to realize context utility functions. For example, the
 * {@link TimeContextInformationProvider} rates relations based on time of
 * creation or modifications of elements.
 * 
 * This abstract class is part of the Decorator design pattern. It is the
 * abstract decorator and the concrete decorators are the subclasses, such as
 * {@link TimeContextInformationProvider},
 * {@link UserContextInformationProvider}, and
 * {@link TextualSimilarityContextInformationProvider}.
 *
 * ConDec's link recommendation is inspired by the following publication: C.
 * Miesbauer and R. Weinreich, "Capturing and Maintaining Architectural
 * Knowledge Using Context Information", 2012 Joint Working IEEE/IFIP Conference
 * on Software Architecture and European Conference on Software Architecture
 */
public abstract class ContextInformationProvider {

	/**
	 * Default activation of link recommendation rule (=context information
	 * provider)
	 */
	protected boolean isActive;
	protected float weightValue;

	public ContextInformationProvider() {
		isActive = true;
		weightValue = 1;
	}

	/**
	 * @return name of the context information provider. Used as the explanation in
	 *         the {@link RecommendationScore}.
	 */
	@XmlElement
	public String getName() {
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

	/**
	 * @return the activation status of the link recommendation rule.
	 */
	@XmlElement
	public boolean isActive() {
		return isActive;
	}

	/**
	 * @param isActive
	 *            activation status of the link recommendation rule.
	 */
	@JsonProperty("isActive")
	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}

	/**
	 * @return the weight value of the link recommendation rule.
	 */
	@XmlElement
	public float getWeightValue() {
		return weightValue;
	}

	/**
	 * @param weightValue
	 *            of the link recommendation rule.
	 */
	@JsonProperty
	public void setWeightValue(float weightValue) {
		this.weightValue = weightValue;
	}

	/**
	 * @return explanation for the link recommendation rule.
	 */
	@XmlElement
	public String getExplanation() {
		return getName();
	}

	/**
	 * @return true if the name of this and the other object is the same.
	 */
	public boolean equals(Object otherRule) {
		return ((ContextInformationProvider) otherRule).getName().equals(this.getName());
	}
}