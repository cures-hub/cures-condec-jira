package de.uhd.ifi.se.decision.management.jira.view.decisionguidance;

import de.uhd.ifi.se.decision.management.jira.view.decisiontable.Argument;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@XmlRootElement(name = "Recommendation")
public class Recommendation {

	@XmlElement
	protected String knowledgeSourceName;

	@XmlElement
	protected String recommendations;

	@XmlElement
	protected String url;

	@XmlElement
	protected int score;

	@XmlElement
	protected List<Argument> arguments;

	public Recommendation() {

	}

	public Recommendation(String knowledgeSourceName, String recommendations, String url) {
		this.knowledgeSourceName = knowledgeSourceName;
		this.recommendations = recommendations;
		this.url = url;
		this.arguments = new ArrayList<>();
	}

	public Recommendation(String knowledgeSourceName, String recommendations, int score, String url) {
		this.knowledgeSourceName = knowledgeSourceName;
		this.recommendations = recommendations;
		this.score = score;
		this.url = url;
		this.arguments = new ArrayList<>();
	}

	public String getKnowledgeSourceName() {
		return knowledgeSourceName;
	}

	public void setKnowledgeSourceName(String knowledgeSourceName) {
		this.knowledgeSourceName = knowledgeSourceName;
	}

	public String getRecommendations() {
		return recommendations;
	}

	public void setRecommendations(String recommendations) {
		this.recommendations = recommendations;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public int getScore() {
		return this.score;
	}

	public void setScore(int score) {
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
		return knowledgeSourceName.equals(that.knowledgeSourceName) &&
			recommendations.equals(that.recommendations);
	}

	@Override
	public int hashCode() {
		return Objects.hash(knowledgeSourceName, recommendations);
	}

}
