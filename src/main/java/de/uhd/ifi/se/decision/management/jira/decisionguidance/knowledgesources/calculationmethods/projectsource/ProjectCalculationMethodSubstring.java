package de.uhd.ifi.se.decision.management.jira.decisionguidance.knowledgesources.calculationmethods.projectsource;

import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.persistence.KnowledgePersistenceManager;
import de.uhd.ifi.se.decision.management.jira.view.decisionguidance.Recommendation;
import de.uhd.ifi.se.decision.management.jira.view.decisiontable.Argument;
import org.apache.commons.text.similarity.JaccardSimilarity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ProjectCalculationMethodSubstring extends ProjectCalculationMethod {

	public ProjectCalculationMethodSubstring() {

	}

	public ProjectCalculationMethodSubstring(String projectKey, String projectSourceName) {
		this.projectKey = projectKey;
		this.projectSourceName = projectSourceName;
		try {
			this.knowledgePersistenceManager = KnowledgePersistenceManager.getOrCreate(this.projectKey);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
	}

	protected List<KnowledgeElement> queryDatabase() {
		return this.knowledgePersistenceManager != null ? this.knowledgePersistenceManager.getKnowledgeElements() : null;
	}


	@Override
	public List<Recommendation> getResults(String inputs) {

		List<Recommendation> recommendations = new ArrayList<>();

		List<String> keywords = Arrays.asList(inputs.trim().split(" "));

		List<KnowledgeElement> knowledgeElements = this.queryDatabase();
		if (knowledgeElements == null) return recommendations;

		//filter all knowledge elements by the type "issue"
		List<KnowledgeElement> issues = knowledgeElements
			.stream()
			.filter(knowledgeElement -> knowledgeElement.getType() == KnowledgeType.ISSUE)
			.collect(Collectors.toList());

		for (String keyword : keywords) {
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
		}

		return recommendations.stream().distinct().collect(Collectors.toList());
	}

	//Todo user other attributes to get a recommendation
	@Override
	public List<Recommendation> getResults(KnowledgeElement knowledgeElement) {
		if (knowledgeElement != null) {
			String inputs = knowledgeElement.getSummary();
			return this.getResults(inputs);
		}
		return new ArrayList<>();
	}


	private int calculateScore(String keywords, KnowledgeElement parentIssue, List<Argument> arguments) {

		int numberProArguments = 0;
		int numberConArguments = 0;

		for (Argument argument : arguments) {
			if (argument.getType().equals(KnowledgeType.PRO.toString())) numberProArguments += 1;
			if (argument.getType().equals(KnowledgeType.CON.toString())) numberConArguments += 1;
		}

		JaccardSimilarity jaccardSimilarity = new JaccardSimilarity();
		double jc = jaccardSimilarity.apply(keywords, parentIssue.getSummary());

		float scoreJC = ((float) jc + ((numberProArguments - numberConArguments))) / (1 + arguments.size()) * 100f;

		return Math.round(scoreJC);
	}

	private double calculateSimilarity(String left, String right) {
		JaccardSimilarity jaccardSimilarity = new JaccardSimilarity();
		double jc = jaccardSimilarity.apply(left.toLowerCase(), right.toLowerCase());
		return jc;
	}

}
