package de.uhd.ifi.se.decision.management.jira.view.decisionguidance;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.xml.bind.annotation.XmlElement;

import de.uhd.ifi.se.decision.management.jira.decisionguidance.knowledgesources.KnowledgeSource;
import de.uhd.ifi.se.decision.management.jira.decisionguidance.knowledgesources.projectsource.ProjectSource;
import de.uhd.ifi.se.decision.management.jira.decisionguidance.knowledgesources.rdfsource.RDFSource;
import de.uhd.ifi.se.decision.management.jira.decisionguidance.score.RecommendationScore;
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

	@XmlElement
	protected RecommendationScore score;

	public Recommendation() {

	}

	public Recommendation(KnowledgeSource knowledgeSource, String recommendation, String url) {
		this.project = new DecisionKnowledgeProject("");
		this.knowledgeSource = knowledgeSource;
		this.setSummary(recommendation);
		this.url = url;
		this.arguments = new ArrayList<>();
	}

	public Recommendation(KnowledgeSource knowledgeSource, String recommendation, RecommendationScore score,
			String url) {
		this(knowledgeSource, recommendation, url);
		this.score = score;
	}

	@XmlElement
	public KnowledgeSource getKnowledgeSource() {
		return knowledgeSource;
	}

	public void setKnowledgeSource(KnowledgeSource knowledgeSource) {
		this.knowledgeSource = knowledgeSource;
	}

	@XmlElement
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public RecommendationScore getRecommendationScore() {
		return this.score;
	}

	public float getScore() {
		return score.getTotalScore();
	}

	public void setScore(RecommendationScore score) {
		this.score = score;
	}

	@XmlElement
	public List<Argument> getArguments() {
		return arguments;
	}

	public void setArguments(List<Argument> arguments) {
		this.arguments = arguments;
	}

	public void addArguments(List<Argument> arguments) {
		if (this.arguments == null)
			this.arguments = new ArrayList<>();
		this.arguments.addAll(arguments);
		this.arguments = this.arguments.stream().distinct().collect(Collectors.toList());
	}

	public void addArgument(Argument argument) {
		if (arguments == null) {
			arguments = new ArrayList<>();
		}
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
		Recommendation recommendation = (Recommendation) object;
		return this.knowledgeSource.getName().equals(recommendation.knowledgeSource.getName())
				&& this.getSummary().equals(recommendation.getSummary());
	}

	@Override
	public int hashCode() {
		return Objects.hash(knowledgeSource.getName(), getSummary());
	}

}
