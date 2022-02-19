package de.uhd.ifi.se.decision.management.jira.recommendation.linkrecommendation.contextinformation;

import javax.xml.bind.annotation.XmlElement;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonSubTypes;
import org.codehaus.jackson.annotate.JsonTypeInfo;
import org.codehaus.jackson.annotate.JsonTypeInfo.As;
import org.codehaus.jackson.annotate.JsonTypeInfo.Id;

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
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonTypeInfo(use = Id.NAME, include = As.PROPERTY)
@JsonSubTypes({ @JsonSubTypes.Type(value = ActiveElementsContextInformationProvider.class, //
		name = "Recommend elements that are included in the same sprint"),
		@JsonSubTypes.Type(value = ComponentContextInformationProvider.class, //
				name = "Recommend elements that are assigned to the same component as the source element"),
		@JsonSubTypes.Type(value = DecisionGroupContextInformationProvider.class, //
				name = "Recommend elements that are assigned to the same decision group as the source element"),
		@JsonSubTypes.Type(value = TextualSimilarityContextInformationProvider.class, //
				name = "Recommend elements that are textual similar to the source element"),
		@JsonSubTypes.Type(value = TimeContextInformationProvider.class, //
				name = "Recommend elements that are timely coupled to the source element"),
		@JsonSubTypes.Type(value = TracingContextInformationProvider.class, //
				name = "Recommend elements that can be traced to the source element"),
		@JsonSubTypes.Type(value = UserContextInformationProvider.class, //
				name = "Recommend elements that have the same author as the source element"),
		@JsonSubTypes.Type(value = KnowledgeTypeContextInformationProvider.class, //
				name = "Recommend elements that are the same knowledge type as the source element"),
		@JsonSubTypes.Type(value = DecisionProblemContextInformationProvider.class, //
				name = "Recommend elements that are decision problems"),
		@JsonSubTypes.Type(value = SolutionOptionContextInformationProvider.class, //
				name = "Recommend elements that are solution options") })
public abstract class ContextInformationProvider {

	/**
	 * Default activation of link recommendation rule (=context information
	 * provider)
	 */
	protected boolean isActive;
	protected float weightValue;

	/**
	 * @issue Which link recommendation rules (context information providers) should
	 *        be activated by default and with which weights?
	 * @decision The default activation and default weight differ for every link
	 *           recommendation rule! For example, the
	 *           TextualSimilarityContextInformationProvider is activated per
	 *           default with a weight value of 2 to increase its importance.
	 */
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
	 * @return description of the link recommendation rule.
	 */
	@XmlElement
	public String getDescription() {
		return "N/A";
	}

	/**
	 * @return true if the name of this and the other object is the same.
	 */
	public boolean equals(Object otherRule) {
		return ((ContextInformationProvider) otherRule).getName().equals(this.getName());
	}
}