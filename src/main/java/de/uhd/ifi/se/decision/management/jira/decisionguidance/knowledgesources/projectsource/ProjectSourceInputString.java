package de.uhd.ifi.se.decision.management.jira.decisionguidance.knowledgesources.projectsource;

import de.uhd.ifi.se.decision.management.jira.decisionguidance.score.RecommendationScore;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.model.Link;
import de.uhd.ifi.se.decision.management.jira.view.decisionguidance.Recommendation;
import de.uhd.ifi.se.decision.management.jira.view.decisiontable.Argument;
import org.apache.commons.text.similarity.JaccardSimilarity;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ProjectSourceInputString extends ProjectSourceInput<String> {

	private static final double THRESHHOLD = 0.85;

	@Override
	public List<Recommendation> getResults(String inputs) {
		List<Recommendation> recommendations = new ArrayList<>();

		this.queryDatabase();
		if (knowledgeElements == null || inputs == null) return recommendations;

		//filter all knowledge elements by the type "issue"
		List<KnowledgeElement> issues = knowledgeElements
			.stream()
			.filter(knowledgeElement -> knowledgeElement.getType() == KnowledgeType.ISSUE)
			.collect(Collectors.toList());

		//get all alternatives, which parent contains the pattern"
		issues.forEach(issue -> {
			if (this.calculateSimilarity(issue.getSummary(), inputs.trim()) > THRESHHOLD) {
				issue.getLinks()
					.stream()
					.filter(link -> this.matchingIssueTypes(link.getSource(), KnowledgeType.ALTERNATIVE, KnowledgeType.DECISION) ||
						this.matchingIssueTypes(link.getTarget(), KnowledgeType.ALTERNATIVE, KnowledgeType.DECISION)) //TODO workaround, checks both directions since the link direction is sometimes wrong.
					.forEach(child -> {

						Recommendation recommendation = this.createRecommendation(child.getSource(), child.getTarget(), KnowledgeType.ALTERNATIVE, KnowledgeType.DECISION);
						recommendation.addArguments(this.getArguments(child.getSource()));
						recommendation.addArguments(this.getArguments(child.getTarget()));

						if (recommendation != null) {
							RecommendationScore score = calculateScore(inputs, issue, recommendation.getArguments());
							recommendation.setScore(score);
							recommendations.add(recommendation);
						}

					});
			}
		});


		return recommendations.stream().distinct().collect(Collectors.toList());
	}

	private RecommendationScore calculateScore(String keywords, KnowledgeElement parentIssue, List<Argument> arguments) {

		RecommendationScore score = new RecommendationScore(0, "Simlarity based on Jaccard-Similiarty");

		JaccardSimilarity jaccardSimilarity = new JaccardSimilarity();
		double jc = jaccardSimilarity.apply(keywords, parentIssue.getSummary());
		score.composeScore(new RecommendationScore((float) jc, "<b>" + keywords + "</b> is similar to <b>" + parentIssue.getSummary() + "</b>"));

		float numberProArguments = 0;
		float numberConArguments = 0;

		for (Argument argument : arguments) {
			if (argument.getType().equals(KnowledgeType.PRO.toString())) {
				numberProArguments += 1;
				score.composeScore(new RecommendationScore(.1f, argument.getType() + " : " + argument.getSummary()));
			}
			if (argument.getType().equals(KnowledgeType.CON.toString())) {
				numberConArguments += 1;
				score.composeScore(new RecommendationScore(-.1f, argument.getType() + " : " + argument.getSummary()));
			}
		}


		float argumentWeight = .1f;

		float scoreJC = ((float) jc + ((numberProArguments - numberConArguments) * argumentWeight)) / (1 + arguments.size() * argumentWeight) * 100f;

		score.setScoreValue(scoreJC);

		return score;
	}

	private double calculateSimilarity(String left, String right) {
		JaccardSimilarity jaccardSimilarity = new JaccardSimilarity();
		double jc = jaccardSimilarity.apply(left.toLowerCase(), right.toLowerCase());
		return jc;
	}

	protected Recommendation createRecommendation(KnowledgeElement source, KnowledgeElement target, KnowledgeType... knowledgeTypes) {
		for (KnowledgeType knowledgeType : knowledgeTypes) {
			if (source.getType() == knowledgeType)
				return new Recommendation(this.knowledgeSource, source.getSummary(), source.getUrl());
			if (target.getType() == knowledgeType)
				return new Recommendation(this.knowledgeSource, target.getSummary(), target.getUrl());
		}

		return null;
	}

	protected boolean matchingIssueTypes(KnowledgeElement knowledgeElement, KnowledgeType... knowledgeTypes) {
		int matchedType = 0;
		for (KnowledgeType knowledgeType : knowledgeTypes) {
			if (knowledgeElement.getType() == knowledgeType) matchedType += 1;
		}
		return matchedType > 0;
	}

	protected List<Argument> getArguments(KnowledgeElement knowledgeElement) {
		List<Argument> arguments = new ArrayList<>();

		for (Link link : knowledgeElement.getLinks()) {
			KnowledgeElement source = link.getSource();
			KnowledgeElement target = link.getTarget();
			if (source.getType().equals(KnowledgeType.PRO) || source.getType().equals(KnowledgeType.CON)) {
				arguments.add(new Argument(source));
			}
			if (target.getType().equals(KnowledgeType.PRO) || target.getType().equals(KnowledgeType.CON)) {
				arguments.add(new Argument(target));
			}
		}

		return arguments;
	}

}
