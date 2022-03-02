package de.uhd.ifi.se.decision.management.jira.recommendation.decisionguidance;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.atlassian.jira.user.ApplicationUser;

import de.uhd.ifi.se.decision.management.jira.model.Argument;
import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeProject;
import de.uhd.ifi.se.decision.management.jira.model.DocumentationLocation;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeGraph;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeStatus;
import de.uhd.ifi.se.decision.management.jira.persistence.ConfigPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.persistence.KnowledgePersistenceManager;
import de.uhd.ifi.se.decision.management.jira.persistence.recommendation.DiscardedRecommendationPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.recommendation.Recommendation;
import de.uhd.ifi.se.decision.management.jira.recommendation.decisionguidance.projectsource.ProjectSource;
import de.uhd.ifi.se.decision.management.jira.recommendation.decisionguidance.projectsource.ProjectSourceRecommender;
import de.uhd.ifi.se.decision.management.jira.recommendation.decisionguidance.rdfsource.RDFSource;
import de.uhd.ifi.se.decision.management.jira.recommendation.decisionguidance.rdfsource.RDFSourceRecommender;

/**
 * Takes the input from the UI and passes it to the knowledge sources.
 */
public abstract class Recommender<T extends KnowledgeSource> {

	protected String projectKey;
	protected T knowledgeSource;

	/**
	 * @param projectKey
	 *            of the current project (not of the external knowledge source).
	 * @param knowledgeSource
	 *            {@link KnowledgeSource}, either {@link RDFSource} or
	 *            {@link ProjectSource}.
	 */
	protected Recommender(String projectKey, T knowledgeSource) {
		this.knowledgeSource = knowledgeSource;
		this.projectKey = projectKey;
	}

	/**
	 * @param projectKey
	 *            of a Jira project.
	 * @param knowledgeSource
	 *            object of either {@link RDFSource} or {@link ProjectSource}.
	 * @return concrete Recommender, either {@link RDFSourceRecommender} or
	 *         {@link ProjectSourceRecommender}.
	 */
	public static Recommender<?> getRecommenderForKnowledgeSource(String projectKey, KnowledgeSource knowledgeSource) {
		if (knowledgeSource instanceof ProjectSource) {
			return new ProjectSourceRecommender(projectKey, (ProjectSource) knowledgeSource);
		}
		return new RDFSourceRecommender(projectKey, (RDFSource) knowledgeSource);
	}

	/**
	 * @param keywords
	 *            used to query the {@link KnowledgeSource} (either
	 *            {@link RDFSource} or {@link ProjectSource}).
	 * @return list of {@link ElementRecommendation}s matching the keywords.
	 */
	public abstract List<ElementRecommendation> getRecommendations(String keywords);

	public List<ElementRecommendation> getRecommendations(KnowledgeElement decisionProblem) {
		if (decisionProblem == null) {
			return new ArrayList<>();
		}
		List<ElementRecommendation> recommendations = new ArrayList<>();
		for (KnowledgeElement linkedElement : decisionProblem.getLinkedSolutionOptions()) {
			List<ElementRecommendation> recommendationFromAlternative = getRecommendations(linkedElement.getSummary());
			recommendations.addAll(recommendationFromAlternative);
		}
		return recommendations.stream().distinct().collect(Collectors.toList());
	}

	/**
	 * @param keywords
	 * @param decisionProblem
	 * @return list of {@link ElementRecommendation}s matching the keywords.
	 */
	// TODO: Can it be ElementRecommendation as the documentation says? (Try out replacing Recommendation (as it has previously been) with
	//  ElementRecommendation in this commit)
	public List<ElementRecommendation> getRecommendations(String keywords, KnowledgeElement decisionProblem) {
		System.out.print("Getting Recommendations for decision problem: ");
		System.out.println(decisionProblem.getSummary());
		System.out.print("decisionProblem.getProject(): ");
		System.out.println(decisionProblem.getProject());
		List<ElementRecommendation> recommendations = new ArrayList<>(getRecommendations(decisionProblem));
		List<ElementRecommendation> discardedRecommendations =
				new ArrayList<>(DiscardedRecommendationPersistenceManager.getDiscardedDecisionGuidanceRecommendations(decisionProblem));
		System.out.print("Got following discarded Recommendations:");
		System.out.println(discardedRecommendations);

		if (!keywords.equalsIgnoreCase(decisionProblem.getSummary())) {
			recommendations.addAll(getRecommendations(keywords));
		}
		for (ElementRecommendation recommendation: recommendations) {
			recommendation.setTarget(decisionProblem);
		}

		return getRecommendationsWithDiscardedStatus(recommendations.stream().distinct().collect(Collectors.toList()), discardedRecommendations);
	}

	/**
	 * For a list of new recommendations update the attribute isDiscarded for those previously discarded and add previously
	 * discarded ones that are not in the list of new recommendations.
	 *
	 * @param newRecommendations Newly given recommendations for a decision problem to be compared with the previously discarded ones.
	 * @param discardedRecommendations Previously discarded recommendations for the same decision problem as newRecommendations.
	 * @return list of {@link ElementRecommendation}s matching the containing all previously discarded ones, all with the correct attribute value
	 *         'isDiscarded'.
	 */
	public List<ElementRecommendation> getRecommendationsWithDiscardedStatus(List<ElementRecommendation> newRecommendations,
																			 List<ElementRecommendation> discardedRecommendations) {
		List<ElementRecommendation> discardedButNotInNewRecommendations = new ArrayList<>();
		for (ElementRecommendation discardedRecommendation : discardedRecommendations) {
			boolean isNewlyGiven = false;
			for (ElementRecommendation newRecommendation : newRecommendations) {
				if (newRecommendation.getSummary().equals(discardedRecommendation.getSummary())) {
					isNewlyGiven = true;
					newRecommendation.setDiscarded(discardedRecommendation.isDiscarded());
				}
			}
			if (!isNewlyGiven) {
				discardedButNotInNewRecommendations.add(discardedRecommendation);
			}
		}
		newRecommendations.addAll(discardedButNotInNewRecommendations);
		return newRecommendations;
	}

	public static List<Recommendation> getAllRecommendations(String projectKey, KnowledgeElement decisionProblem,
			String keywords) {
		DecisionGuidanceConfiguration config = ConfigPersistenceManager.getDecisionGuidanceConfiguration(projectKey);
		List<KnowledgeSource> knowledgeSources = config.getAllActivatedKnowledgeSources();
		return getAllRecommendations(projectKey, knowledgeSources, decisionProblem, keywords);
	}

	public static List<Recommendation> getAllRecommendations(String projectKey, List<KnowledgeSource> knowledgeSources,
			KnowledgeElement decisionProblem, String keywords) {
		List<Recommendation> recommendations = new ArrayList<>();
		for (KnowledgeSource knowledgeSource : knowledgeSources) {
			Recommender<?> recommender = Recommender.getRecommenderForKnowledgeSource(projectKey, knowledgeSource);
			recommendations.addAll(recommender.getRecommendations(keywords, decisionProblem));
		}
		return recommendations.stream().distinct().collect(Collectors.toList());
	}

	/**
	 * Adds all recommendations to the knowledge graph with the status
	 * "recommended". The recommendations will be appended to the decision problem
	 * and written into a Jira issue description or a comment.
	 *
	 * @param decisionProblem
	 *            to which the recommended solution options should be linked in the
	 *            {@link KnowledgeGraph}.
	 * @param user
	 *            authenticated Jira {@link ApplicationUser}.
	 * @param recommendations
	 *            list of recommended solution options
	 *            ({@link ElementRecommendation}s) and recommended arguments that
	 *            should be linked in the {@link KnowledgeGraph}.
	 */
	public static void addToKnowledgeGraph(KnowledgeElement decisionProblem, ApplicationUser user,
			List<Recommendation> recommendations) {
		KnowledgeElement parentElement = decisionProblem;
		for (Recommendation recommendation : recommendations) {
			ElementRecommendation solutionOptionRecommendation = (ElementRecommendation) recommendation;
			parentElement = addToJiraIssue(solutionOptionRecommendation, parentElement, user);

			for (Argument argumentRecommendation : solutionOptionRecommendation.getArguments()) {
				parentElement = addToJiraIssue(argumentRecommendation, parentElement, user);
			}
		}
	}

	private static KnowledgeElement addToJiraIssue(KnowledgeElement newElement, KnowledgeElement parentElement,
			ApplicationUser user) {
		DecisionKnowledgeProject project = parentElement.getProject();
		newElement.setProject(project);
		newElement.setDocumentationLocation(DocumentationLocation.JIRAISSUETEXT);
		newElement.setStatus(KnowledgeStatus.RECOMMENDED);
		if (parentElement.getJiraIssue() != null) {
			return KnowledgePersistenceManager.getInstance(project).insertKnowledgeElement(newElement, user,
					parentElement);
		}
		return parentElement;
	}

	/**
	 * @return either {@link RDFSource} or {@link ProjectSource}.
	 */
	public T getKnowledgeSource() {
		return knowledgeSource;
	}

	/**
	 * @param knowledgeSource
	 *            either {@link RDFSource} or {@link ProjectSource}.
	 */
	public void setKnowledgeSource(T knowledgeSource) {
		this.knowledgeSource = knowledgeSource;
	}
}
