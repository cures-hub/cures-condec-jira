package de.uhd.ifi.se.decision.management.jira.decisionguidance;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.xml.bind.annotation.XmlElement;

import de.uhd.ifi.se.decision.management.jira.decisionguidance.knowledgesources.KnowledgeSource;
import de.uhd.ifi.se.decision.management.jira.decisionguidance.knowledgesources.projectsource.ProjectSource;
import de.uhd.ifi.se.decision.management.jira.decisionguidance.knowledgesources.rdfsource.RDFSource;
import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeProject;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.view.decisiontable.Argument;

/**
 * Models a recommendation of a solution option for a decision problem. The
 * recommendation is generated from an external {@link KnowledgeSource}, such as
 * DBPedia ({@link RDFSource} or another Jira project ({@link ProjectSource}).
 * 
 * The recommendation can contain a list of arguments (pro and cons) that either
 * support or attack this recommended solution option.
 */
public class Recommendation extends KnowledgeElement {

	private KnowledgeSource knowledgeSource;
	private String url;
	private List<Argument> arguments;
	private RecommendationScore score;

	public Recommendation() {
		this.arguments = new ArrayList<>();
	}

	public Recommendation(KnowledgeSource knowledgeSource, String summary, String url) {
		this();
		this.project = new DecisionKnowledgeProject("");
		this.knowledgeSource = knowledgeSource;
		this.setSummary(summary);
		this.url = url;
	}

	public Recommendation(KnowledgeElement knowledgeElement) {
		this.setSummary(knowledgeElement.getSummary());
		this.url = knowledgeElement.getUrl();
		this.project = knowledgeElement.getProject();
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

	/**
	 * @return score that represents the predicted relevance of a recommendation,
	 *         i.e., how likely it is that the user accepts the recommendation. The
	 *         score can be used to rank/sort the recommendations.
	 */
	@XmlElement
	public RecommendationScore getScore() {
		return score;
	}

	/**
	 * @param score
	 *            that represents the predicted relevance of a recommendation, i.e.,
	 *            how likely it is that the user accepts the recommendation. The
	 *            score can be used to rank/sort the recommendations.
	 */
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
		Recommendation otherRecommendation = (Recommendation) object;
		return this.knowledgeSource.getName().equals(otherRecommendation.knowledgeSource.getName())
				&& this.getSummary().equals(otherRecommendation.getSummary());
	}

	@Override
	public int hashCode() {
		return Objects.hash(knowledgeSource.getName(), getSummary());
	}

}
