package de.uhd.ifi.se.decision.management.jira.view.decisionguidance;

import de.uhd.ifi.se.decision.management.jira.decisionguidance.knowledgesources.KnowledgeSource;
import de.uhd.ifi.se.decision.management.jira.decisionguidance.score.RecommendationScore;
import de.uhd.ifi.se.decision.management.jira.view.decisiontable.Argument;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@XmlRootElement(name = "Recommendation")
public class Recommendation {

	protected KnowledgeSource knowledgeSource;

	@XmlElement
	protected String recommendation;

	@XmlElement
	protected String url;

	@XmlElement
	protected RecommendationScore score;

	@XmlElement
	protected List<Argument> arguments;

	public Recommendation() {

	}

	public Recommendation(KnowledgeSource knowledgeSource, String recommendation, String url) {
		this.knowledgeSource = knowledgeSource;
		this.recommendation = recommendation;
		this.url = url;
		this.arguments = new ArrayList<>();
	}

	public Recommendation(KnowledgeSource knowledgeSource, String recommendation, RecommendationScore score, String url) {
		this.knowledgeSource = knowledgeSource;
		this.recommendation = recommendation;
		this.score = score;
		this.url = url;
		this.arguments = new ArrayList<>();
	}

	@XmlElement(name = "knowledgeSourceName")
	public String getKnowledgeSourceName() {
		return this.knowledgeSource.getName();
	}

	@XmlElement(name ="icon")
	public String getIcon() {
		return this.knowledgeSource.getIcon();
	}

	public String getRecommendation() {
		return recommendation;
	}

	public void setRecommendations(String recommendations) {
		this.recommendation = recommendations;
	}

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
		return this.score.getTotalScore();
	}

	public void setScore(RecommendationScore score) {
		this.score = score;
	}

	public List<Argument> getArguments() {
		return arguments;
	}

	public void setArguments(List<Argument> arguments) {
		this.arguments = arguments;
	}

	public void addArguments(List<Argument> arguments) {
		if (this.arguments == null) this.arguments = new ArrayList<>();
		this.arguments.addAll(arguments);
		this.arguments = this.arguments.stream().distinct().collect(Collectors.toList());
	}

	public void addArgument(Argument argument) {
		if (this.arguments == null) this.arguments = new ArrayList<>();
		this.arguments.add(argument);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Recommendation that = (Recommendation) o;
		return this.knowledgeSource.getName().equals(that.knowledgeSource.getName()) &&
			recommendation.equals(that.recommendation);
	}

	@Override
	public int hashCode() {
		return Objects.hash(knowledgeSource.getName(), recommendation);
	}

}
