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

	/**
	 * Source of the recommendation, e.g. DBPedia (as {@link RDFSource}) or another Jira project (as {@link ProjectSource}).
	 */
	private KnowledgeSource knowledgeSource;

	/**
	 * URL to this recommendation at its source, e.g. a link to a Wikipedia page or the issue of another Jira project.
	 */
	private String url;

	/**
	 * Pro and Con {@link Argument}s for the recommendation.
	 */
	private List<Argument> arguments;

	/**
	 * Score indicating how fitting the recommendation is.
	 */
	private RecommendationScore score = new RecommendationScore(0, "not scored");

	/**
	 * true if the user has discarded the recommendation, otherwise false.
	 */
	private boolean discarded = false;

	/**
	 * {@link KnowledgeElement} for which the recommendation was given.
	 */
	private KnowledgeElement target;

	@JsonCreator
	public ElementRecommendation() {
		this.arguments = new ArrayList<>();
		this.status = KnowledgeStatus.RECOMMENDED;
		this.type = KnowledgeType.ALTERNATIVE;
	}

	/**
	 * @param summary Textual summary of the recommendation.
	 * @param target Decision issue for which the recommendation is given.
	 * @param knowledgeSource Source of the recommendation.
	 * @param project Project in which the recommendation is given.
	 * @param url URL to the recommendation at its source.
	 */
	public ElementRecommendation(String summary, KnowledgeElement target, KnowledgeSource knowledgeSource, DecisionKnowledgeProject project,
								 String url) {
		this();
		this.setSummary(summary);
		this.target = target;
		this.knowledgeSource = knowledgeSource;
		this.setProject(project);
		this.url = url;
	}

	/**
	 * @param summary Textual summary of the recommendation.
	 * @param target Decision issue for which the recommendation is given.
	 * @param knowledgeSource Source of the recommendation.
	 * @param project Project in which the recommendation is given.
	 */
	public ElementRecommendation(String summary, KnowledgeElement target, KnowledgeSource knowledgeSource, DecisionKnowledgeProject project) {
		this(summary, target, knowledgeSource, project, "");
	}

	/**
	 * @param summary Textual summary of the recommendation.
	 * @param target Decision issue for which the recommendation is given.
	 * @param knowledgeSource Source of the recommendation.
	 * @param url URL to the recommendation at its source.
	 */
	public ElementRecommendation(String summary, KnowledgeElement target, KnowledgeSource knowledgeSource, String url) {
		this(summary, target, knowledgeSource, target.getProject(), url);
	}

	/**
	 * @param knowledgeElement Decision in another Jira project to be recommended.
	 * @param target Decision issue for which the recommendation is given.
	 */
	public ElementRecommendation(KnowledgeElement knowledgeElement, KnowledgeElement target) {
		this(knowledgeElement.getSummary(), target, new ProjectSource(knowledgeElement.getProject().getProjectKey()), knowledgeElement.getUrl());
	}

	/**
	 * @param summary Textual summary of the recommendation.
	 * @param target Decision issue for which the recommendation is given.
	 */
	public ElementRecommendation(String summary, KnowledgeElement target) {
		this(summary, target, new RDFSource(), target.getProject());
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
		if (!recommendations.isEmpty()) {
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

	/**
	 * @param object object to be compared to this instance.
	 * @return true, if object is from the same class, has a {@link ElementRecommendation#knowledgeSource} with the same {@link KnowledgeSource#name}
	 * 	       and the same {@link ElementRecommendation#getSummary()}.
	 */
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
		this.discarded = isDiscarded;
	}

	/**
	 * @return  true if the recommendation has been discarded, otherwise false
	 */
	@Override
	public boolean isDiscarded() {
		return discarded;
	}

	@Override
	public String toString() {
		return this.getSummary();
	}
}
