package de.uhd.ifi.se.decision.management.jira.recommendation.decisionguidance;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.xml.bind.annotation.XmlElement;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import de.uhd.ifi.se.decision.management.jira.model.Argument;
import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeProject;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeStatus;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.recommendation.Recommendation;
import de.uhd.ifi.se.decision.management.jira.recommendation.RecommendationScore;
import de.uhd.ifi.se.decision.management.jira.recommendation.RecommendationType;
import de.uhd.ifi.se.decision.management.jira.recommendation.decisionguidance.projectsource.ProjectSource;
import de.uhd.ifi.se.decision.management.jira.recommendation.decisionguidance.rdfsource.RDFSource;

/**
 * Models a recommendation of a solution option for a decision problem. The
 * recommendation is generated from an external {@link KnowledgeSource}, such as
 * DBPedia ({@link RDFSource} or another Jira project ({@link ProjectSource}).
 * 
 * The recommendation can contain a list of arguments (pro and cons) that either
 * support or attack this recommended solution option. The relevance of the
 * recommendation is represented by the {@link RecommendationScore}.
 */
@JsonIgnoreProperties(ignoreUnknown = true) //, value = "knowledgeSource")
public class ElementRecommendation extends KnowledgeElement implements Recommendation {

	private KnowledgeSource knowledgeSource;
	private String url;
	private List<Argument> arguments;
	private RecommendationScore score = new RecommendationScore(0, "not scored");
	private boolean isDiscarded = false;
	private KnowledgeElement target;

	@JsonCreator
	public ElementRecommendation() {
		this.arguments = new ArrayList<>();
		this.status = KnowledgeStatus.RECOMMENDED;
		this.type = KnowledgeType.ALTERNATIVE;
	}

	public ElementRecommendation(KnowledgeSource knowledgeSource, String summary, String url) {
		this();
		this.project = new DecisionKnowledgeProject("");
		this.knowledgeSource = knowledgeSource;
		this.setSummary(summary);
		this.url = url;
	}

	public ElementRecommendation(String summary, KnowledgeElement target) {
		this();
		this.project = new DecisionKnowledgeProject("");
		this.setSummary(summary);
		this.target = target;
	}

	public ElementRecommendation(KnowledgeElement knowledgeElement) {
		this();
		this.project = knowledgeElement.getProject();
		this.setSummary(knowledgeElement.getSummary());
		this.url = knowledgeElement.getUrl();
	}

	/**
	 * Normalizes the score values of all recommendations. Finds the best
	 * recommendation score and sets this score to 100%.
	 *
	 * @param recommendations
	 *            list of {@link ElementRecommendation}s.
	 * @return recommendations with normalized scores against the best
	 *         recommendation in the range of [0, 1].
	 */
	public static List<ElementRecommendation> normalizeRecommendationScore(List<ElementRecommendation> recommendations) {
		if (recommendations.size() > 0) {
			float maxValue = recommendations.stream().map(Recommendation::getScore).map(RecommendationScore::getValue).max(Float::compare).get();

			for (ElementRecommendation recommendation : recommendations) {
				recommendation.getScore().normalizeTo(maxValue);
			}
		}
		return recommendations;
	}

	/**
	 * @return {@link KnowledgeSource} that this recommendation is taken from.
	 * @see ProjectSource
	 * @see RDFSource
	 */
	@XmlElement
	public KnowledgeSource getKnowledgeSource() {
		return knowledgeSource;
	}

	/**
	 * @param knowledgeSource
	 *            {@link KnowledgeSource} that this recommendation is taken from.
	 * @see ProjectSource
	 * @see RDFSource
	 */
	@JsonIgnore
	public void setKnowledgeSource(KnowledgeSource knowledgeSource) {
		this.knowledgeSource = knowledgeSource;
	}

	/**
	 * @return URL to the source location of the recommendation, e.g., to a
	 *         Wikipedia page or to a Jira issue of another Jira project.
	 */
	@XmlElement
	public String getUrl() {
		return url;
	}

	/**
	 * @param url
	 *            to the source location of the recommendation, e.g., to a Wikipedia
	 *            page or to a Jira issue of another Jira project.
	 */
	public void setUrl(String url) {
		this.url = url;
	}

	@Override
	public RecommendationScore getScore() {
		return score;
	}

	@Override
	public void setScore(RecommendationScore score) {
		this.score = score;
	}

	/**
	 * @return arguments (pro and cons) that either support or attack the
	 *         recommended solution option.
	 */
	@XmlElement
	public List<Argument> getArguments() {
		return arguments;
	}

	/**
	 * @param arguments
	 *            (pro and cons) that either support or attack the recommended
	 *            solution option.
	 */
	public void setArguments(List<Argument> arguments) {
		this.arguments = arguments;
	}

	/**
	 * @return {@link KnowledgeElement} based on which this Recommendation was given
	 */
	@XmlElement
	public KnowledgeElement getTarget() {
		return this.target;
	}

	/**
	 * @param target
	 *            Set {@link KnowledgeElement} for which the recommendation is given
	 */
	public void setTarget(KnowledgeElement target) {
		this.target = target;
	}

	/**
	 * @param arguments
	 *            (pro and cons) that either support or attack the recommended
	 *            solution option.
	 */
	public void addArguments(List<Argument> arguments) {
		this.arguments.addAll(arguments);
		this.arguments = this.arguments.stream().distinct().collect(Collectors.toList());
	}

	/**
	 * @param argument
	 *            (pro or con) that either supports or attacks the recommended
	 *            solution option.
	 */
	public void addArgument(Argument argument) {
		arguments.add(argument);
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}
		if (object == null || getClass() != object.getClass()) {
			return false;
		}
		ElementRecommendation otherRecommendation = (ElementRecommendation) object;
		return this.knowledgeSource.getName().equals(otherRecommendation.knowledgeSource.getName())
				&& this.getSummary().equals(otherRecommendation.getSummary());
	}

	@Override
	public int hashCode() {
		return Objects.hash(knowledgeSource.getName(), getSummary());
	}

	@Override
	public RecommendationType getRecommendationType() {
		return RecommendationType.EXTERNAL;
	}

	/**
	 * @param isDiscarded
	 *            Store whether a recommendation is discarded or not
	 */
	@Override
	public void setDiscarded(boolean isDiscarded) {
		this.isDiscarded = isDiscarded;
	}

	/**
	 * @return  true if the recommendation has been discarded, otherwise false
	 */
	@Override
	public boolean isDiscarded() {
		return isDiscarded;
	}
}