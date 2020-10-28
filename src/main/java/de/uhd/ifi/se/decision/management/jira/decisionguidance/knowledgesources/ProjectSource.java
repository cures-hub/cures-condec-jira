package de.uhd.ifi.se.decision.management.jira.decisionguidance.knowledgesources;

import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.model.Link;
import de.uhd.ifi.se.decision.management.jira.persistence.KnowledgePersistenceManager;
import de.uhd.ifi.se.decision.management.jira.view.decisionguidance.Recommendation;
import de.uhd.ifi.se.decision.management.jira.view.decisiontable.Argument;
import org.apache.commons.text.similarity.JaccardSimilarity;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ProjectSource extends KnowledgeSource {

	protected KnowledgePersistenceManager knowledgePersistenceManager;

	public ProjectSource(String projectKey) {
		this.projectKey = projectKey;
		this.knowledgeSourceType = KnowledgeSourceType.PROJECT;
		this.isActivated = false;
	}

	public ProjectSource(String projectKey, String projectSourceName, boolean isActivated) {
		this(projectKey);
		this.name = projectSourceName;
		this.isActivated = isActivated;
		try {
			this.knowledgePersistenceManager = KnowledgePersistenceManager.getOrCreate(this.name);
		} catch(IllegalArgumentException e) {
			e.printStackTrace();
		}
	}

	protected List<KnowledgeElement> queryDatabase() {
		return this.knowledgePersistenceManager != null ? this.knowledgePersistenceManager.getKnowledgeElements() : null;
	}


	@Override
	public List<Recommendation> getResults(String inputs) {

		List<Recommendation> recommendations = new ArrayList<>();

		List<KnowledgeElement> knowledgeElements = this.queryDatabase();
		if (knowledgeElements == null || inputs == null) return recommendations;

		//filter all knowledge elements by the type "issue"
		List<KnowledgeElement> issues = knowledgeElements
			.stream()
			.filter(knowledgeElement -> knowledgeElement.getType() == KnowledgeType.ISSUE)
			.collect(Collectors.toList());

		//get all alternatives, which parent contains the pattern"
		issues.forEach(issue -> {
			if (this.calculateSimilarity(issue.getSummary(), inputs.trim()) > 0.5) {
				issue.getLinks()
					.stream()
					.filter(link -> this.matchingIssueTypes(link.getSource(), KnowledgeType.ALTERNATIVE, KnowledgeType.DECISION) ||
						this.matchingIssueTypes(link.getTarget(), KnowledgeType.ALTERNATIVE, KnowledgeType.DECISION)) //TODO workaround, checks both directions since the link direction is sometimes wrong.
					.forEach(child -> {

						Recommendation recommendation = this.createRecommendation(child.getSource(), child.getTarget(), KnowledgeType.ALTERNATIVE, KnowledgeType.DECISION);
						recommendation.addArguments(this.getArguments(child.getSource()));
						recommendation.addArguments(this.getArguments(child.getTarget()));

						if (recommendation != null) {
							int score = calculateScore(inputs, issue, recommendation.getArguments());
							recommendation.setScore(score);
							recommendations.add(recommendation);
						}

					});
			}
		});


		return recommendations.stream().distinct().collect(Collectors.toList());
	}

	@Override
	public List<Recommendation> getResults(KnowledgeElement knowledgeElement) {

		List<Recommendation> recommendations = new ArrayList<>();

		if (knowledgeElement != null) {

			recommendations.addAll(this.getResults(knowledgeElement.getSummary()));

			for (Link link : knowledgeElement.getLinks()) {
				for (KnowledgeElement linkedElement : link.getBothElements()) {
					if (linkedElement.getType().equals(KnowledgeType.ALTERNATIVE) || linkedElement.getType().equals(KnowledgeType.DECISION)) {
						List<Recommendation> recommendationFromAlternative = this.getResults(linkedElement.getSummary());
						recommendations.addAll(recommendationFromAlternative);
					}
				}
			}
		}

		return this.calculateMeanScore(recommendations).stream().distinct().collect(Collectors.toList());
	}

	public List<Recommendation> calculateMeanScore(List<Recommendation> recommendations) {
		List<Recommendation> filteredRecommendations = new ArrayList<>();


		for (Recommendation recommendation : recommendations) {
			Recommendation meanScoreRecommendation = recommendation;
			int numberDuplicates = 0;
			int meanScore = 0;
			for (Recommendation recommendation1 : recommendations) {
				if (recommendation.equals(recommendation1)) {
					numberDuplicates += 1;
					meanScore += recommendation1.getScore();
				}
			}
			meanScoreRecommendation.setScore(meanScore / numberDuplicates);
			filteredRecommendations.add(meanScoreRecommendation);
		}


		return filteredRecommendations;
	}


	private int calculateScore(String keywords, KnowledgeElement parentIssue, List<Argument> arguments) {

		float numberProArguments = 0;
		float numberConArguments = 0;

		for (Argument argument : arguments) {
			if (argument.getType().equals(KnowledgeType.PRO.toString())) numberProArguments += 1;
			if (argument.getType().equals(KnowledgeType.CON.toString())) numberConArguments += 1;
		}

		JaccardSimilarity jaccardSimilarity = new JaccardSimilarity();
		double jc = jaccardSimilarity.apply(keywords, parentIssue.getSummary());

		float argumentWeight = .1f;

		float scoreJC = ((float) jc + ((numberProArguments - numberConArguments) * argumentWeight)) / (1 + arguments.size() * argumentWeight) * 100f;

		return Math.round(scoreJC);
	}

	private double calculateSimilarity(String left, String right) {
		JaccardSimilarity jaccardSimilarity = new JaccardSimilarity();
		double jc = jaccardSimilarity.apply(left.toLowerCase(), right.toLowerCase());
		return jc;
	}

	protected Recommendation createRecommendation(KnowledgeElement source, KnowledgeElement target, KnowledgeType... knowledgeTypes) {
		for (KnowledgeType knowledgeType : knowledgeTypes) {
			if (source.getType() == knowledgeType)
				return new Recommendation(this.name, source.getSummary(), source.getUrl());
			if (target.getType() == knowledgeType)
				return new Recommendation(this.name, target.getSummary(), target.getUrl());
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
