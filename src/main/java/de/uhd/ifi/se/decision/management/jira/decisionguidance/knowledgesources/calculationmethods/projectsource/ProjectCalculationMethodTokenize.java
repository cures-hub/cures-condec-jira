package de.uhd.ifi.se.decision.management.jira.decisionguidance.knowledgesources.calculationmethods.projectsource;

import de.uhd.ifi.se.decision.management.jira.classification.preprocessing.Preprocessor;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.persistence.KnowledgePersistenceManager;
import de.uhd.ifi.se.decision.management.jira.view.decisionguidance.Recommendation;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ProjectCalculationMethodTokenize extends ProjectCalculationMethod {

	public ProjectCalculationMethodTokenize() {

	}

	public ProjectCalculationMethodTokenize(String projectKey, String projectSourceName) {
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

		List<KnowledgeElement> knowledgeElements = this.queryDatabase();
		if (knowledgeElements == null) return recommendations;


		//filter all knowledge elements by the type "issue"
		List<KnowledgeElement> issues = knowledgeElements
			.stream()
			.filter(knowledgeElement -> knowledgeElement.getType() == KnowledgeType.ISSUE)
			.collect(Collectors.toList());


		try {
			Preprocessor preprocessor = new Preprocessor();
			String s1 = inputs;
			s1 = cleanMarkdown(s1);
			preprocessor.preprocess(s1);
			List<CharSequence> preprocessedS1Tokens = preprocessor.getTokens();

			issues.forEach(issue -> {
				try {
					String s2 = issue.getDescription();
					s2 = cleanMarkdown(s2);
					preprocessor.preprocess(s2);
					List<CharSequence> preprocessedS2Tokens = preprocessor.getTokens();

					int fragmentLength = 5;
					double MIN_SIMILARITY = 85;


					int index = 0;
					// Iterate over text.
					while (index < preprocessedS1Tokens.size() - fragmentLength + 1) {
						int internalIndex = 0;
						// Get Lists of text based on the fragmentLength
						List<CharSequence> sequenceToCheck = preprocessedS1Tokens.subList(index, index + fragmentLength);
						List<CharSequence> sequenceToCheckAgainst = preprocessedS2Tokens.subList(internalIndex,
							Math.min(internalIndex + fragmentLength, preprocessedS2Tokens.size()));

						while (calculateScore(sequenceToCheck, sequenceToCheckAgainst) <= MIN_SIMILARITY
							&& internalIndex < preprocessedS2Tokens.size() - fragmentLength + 1) {
							sequenceToCheckAgainst = preprocessedS2Tokens.subList(internalIndex,
								Math.min(internalIndex + fragmentLength, preprocessedS2Tokens.size()));
							internalIndex++;
						}

						if (calculateScore(sequenceToCheck, sequenceToCheckAgainst) >= MIN_SIMILARITY) {

							final int score = calculateScore(sequenceToCheck, sequenceToCheckAgainst);
							issue.getLinks()
								.stream()
								.filter(link -> this.matchingIssueTypes(link.getSource(), KnowledgeType.ALTERNATIVE, KnowledgeType.DECISION) ||
									this.matchingIssueTypes(link.getTarget(), KnowledgeType.ALTERNATIVE, KnowledgeType.DECISION)) //TODO workaround, checks both directions since the link direction is sometimes wrong.
								.forEach(child -> {

									Recommendation recommendation = this.createRecommendation(child.getSource(), child.getTarget(), KnowledgeType.ALTERNATIVE, KnowledgeType.DECISION);
									recommendation.addArguments(this.getArguments(child.getSource()));
									recommendation.addArguments(this.getArguments(child.getTarget()));

									if (recommendation != null) {
										recommendation.setScore(score);
										recommendations.add(recommendation);
									}
								});
						}
						index++;
					}


				} catch (Exception e) {
					e.printStackTrace();
				}
			});

		} catch (Exception e) {
			e.printStackTrace();
		}

		return recommendations;
	}

	private String cleanMarkdown(String markdown) {
		return markdown.replaceAll("[{(color)]+[:#0-9]*}", "")
			.replaceAll("(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]", "URL")
			.replaceAll("[|\\[\\]]+", " ").replaceAll("h[0-9]+", "").replaceAll("[;/:*?\"<>&.{},'#!+@-]+", " ")
			.replaceAll("[\n\r]+", " ").replaceAll("[0-9]+", "NUMBER").replaceAll("(-){2,}", "");
	}

	private int calculateScore(List<CharSequence> sequenceToCheck, List<CharSequence> sequenceToCheckAgainst) {
		double count = 0.;
		for (CharSequence toCheck : sequenceToCheck) {
			count += sequenceToCheckAgainst.contains(toCheck) ? 1 : 0;
		}
		return ((Double) (count / (sequenceToCheck.size()))).intValue() * 100;
	}
}
