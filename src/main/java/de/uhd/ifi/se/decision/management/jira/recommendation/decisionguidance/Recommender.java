package de.uhd.ifi.se.decision.management.jira.recommendation.decisionguidance;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.atlassian.jira.issue.Issue;
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

	/**
	 * Key of the {@link DecisionKnowledgeProject} in which recommendations are given.
	 */
	protected String projectKey;

	/**
	 * Source of the recommendations, e.g. an {@link RDFSource} based on DBpedia.
	 */
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
		Recommender<?> recommenderForKnowledgeSource;
		if (knowledgeSource instanceof ProjectSource) {
			recommenderForKnowledgeSource = new ProjectSourceRecommender(projectKey, (ProjectSource) knowledgeSource);
		} else {
			recommenderForKnowledgeSource = new RDFSourceRecommender(projectKey, (RDFSource) knowledgeSource);
		}
		return recommenderForKnowledgeSource;
	}

	/**
	 * @param keywords
	 *            used to query the {@link KnowledgeSource} (either
	 *            {@link RDFSource} or {@link ProjectSource}).
	 * @return list of {@link ElementRecommendation}s matching the keywords.
	 */
	public abstract List<ElementRecommendation> getRecommendations(String keywords);

	/**
	 * Get recommendations based on the textual summaries of linked solution options to the
	 * given decision problem.
	 *
	 * @param decisionProblem
	 *            Decision problem with linked solution options, based on which new
	 *            recommendations should be given.
	 * @return Recommendations based on solution options that are already linked to the given
	 *         decision problem.
	 */
	public List<ElementRecommendation> getRecommendations(KnowledgeElement decisionProblem) {
		List<ElementRecommendation> recommendations = new ArrayList<>();
		if (decisionProblem != null) {
			for (KnowledgeElement linkedElement : decisionProblem.getLinkedSolutionOptions()) {
				List<ElementRecommendation> recommendationFromAlternative = getRecommendations(linkedElement.getSummary());
				recommendations.addAll(recommendationFromAlternative);
			}
		}
		return recommendations.stream().distinct().collect(Collectors.toList());
	}

	/**
	 * @param keywords
	 * @param decisionProblem
	 * @return list of {@link ElementRecommendation}s matching the keywords.
	 */
	public List<ElementRecommendation> getRecommendations(String keywords, KnowledgeElement decisionProblem) {
		List<ElementRecommendation> recommendations = new ArrayList<>(getRecommendations(decisionProblem));
		List<ElementRecommendation> discardedRecommendations =
				new ArrayList<>(DiscardedRecommendationPersistenceManager.getDiscardedDecisionGuidanceRecommendations(decisionProblem));

		if (!keywords.equalsIgnoreCase(decisionProblem.getSummary())) {
			recommendations.addAll(getRecommendations(keywords));
		}
		for (ElementRecommendation recommendation: recommendations) {
			recommendation.setTarget(decisionProblem);
		}

		return getRecommendationsWithDiscardedStatus(recommendations.stream().distinct().collect(Collectors.toList()), discardedRecommendations);
	}

	/**
	 * For a list of new recommendations update the attribute discarded for those previously discarded and add previously
	 * discarded ones that are not in the list of new recommendations.
	 *
	 * @param newRecommendations Newly given recommendations for a decision problem to be compared with the
	 *                           previously discarded ones.
	 * @param discardedRecommendations Previously discarded recommendations for the same decision problem as
	 *                                 new Recommendations.
	 * @return list of {@link ElementRecommendation}s matching the containing all previously discarded ones, all with
	 *         the correct attribute value for {@link ElementRecommendation#isDiscarded()}
	 */
	public List<ElementRecommendation> getRecommendationsWithDiscardedStatus(List<ElementRecommendation> newRecommendations,
																			 List<ElementRecommendation> discardedRecommendations) {
		for (ElementRecommendation discardedRecommendation : discardedRecommendations) {
			for (ElementRecommendation newRecommendation : newRecommendations) {
				if (newRecommendation.getSummary().equals(discardedRecommendation.getSummary())) {
					newRecommendation.setDiscarded(true);
				}
			}
		}
		return newRecommendations;
	}

	/**
	 * Get all available recommendations for the given decision problem and optional keywords based on the config
	 * ({@link ConfigPersistenceManager#getDecisionGuidanceConfiguration(String)}.
	 *
	 * @param projectKey Key of the project in which the recommendations are given.
	 * @param decisionProblem Issue to which the recommendations are given.
	 * @param keywords Optional input to further specify the decision problem for better recommendations.
	 * @return Available recommendations (from all {@link KnowledgeSource}s activated in the config)
	 */
	public static List<Recommendation> getAllRecommendations(String projectKey, KnowledgeElement decisionProblem,
			String keywords) {
		DecisionGuidanceConfiguration config = ConfigPersistenceManager.getDecisionGuidanceConfiguration(projectKey);
		List<KnowledgeSource> knowledgeSources = config.getAllActivatedKnowledgeSources();
		return getAllRecommendations(projectKey, knowledgeSources, decisionProblem, keywords);
	}

	/**
	 * Get all available recommendations for the given decision problem and optional keywords from the given knowledge
	 * sources.
	 *
	 * @param projectKey Key of the project in which the recommendations are given.
	 * @param knowledgeSources Sources from which recommendations are requested.
	 * @param decisionProblem Issue to which the recommendations are given.
	 * @param keywords Optional input to further specify the decision problem for better recommendations.
	 * @return Recommendations from all given knowledge sources.
	 */
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
	 * Adds all recommendations to the knowledge graph with the status "recommended". The recommendations will be
	 * appended to the decision problem and written into a Jira issue description or a comment.
	 *
	 * @param decisionProblem
	 *            to which the recommended solution options should be linked in the {@link KnowledgeGraph}.
	 * @param user
	 *            authenticated Jira {@link ApplicationUser}.
	 * @param recommendations
	 *            list of recommended solution options ({@link ElementRecommendation}s) and recommended arguments that
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

	/**
	 * Add an accepted recommendation or argument to an {@link Issue} as comment.
	 *
	 * @param newElement Recommendation or argument to be added.
	 * @param parentElement Element to the issue of which the new element should be added.
	 * @param user Authenticated Jira ApplicationUser.
	 * @return The newly created element, which is next parent element if additional elements should be added. If the
	 *         given parent element does not have a {@link KnowledgeElement#getJiraIssue()}, the given parent element
	 *         is returned.
	 */
	private static KnowledgeElement addToJiraIssue(KnowledgeElement newElement, KnowledgeElement parentElement,
			ApplicationUser user) {
		KnowledgeElement newParent = parentElement;
		DecisionKnowledgeProject project = parentElement.getProject();
		newElement.setProject(project);
		newElement.setDocumentationLocation(DocumentationLocation.JIRAISSUETEXT);
		newElement.setStatus(KnowledgeStatus.RECOMMENDED);
		if (parentElement.getJiraIssue() != null) {
			newParent = KnowledgePersistenceManager.getInstance(project).insertKnowledgeElement(newElement, user,
					parentElement);
		}
		return newParent;
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

	/**
	 * @return {@link Recommender#projectKey}
	 */
	public String getProjectKey() {
		return projectKey;
	}

	/**
	 * @param projectKey {@link Recommender#projectKey}
	 */
	public void setProjectKey(String projectKey) {
		this.projectKey = projectKey;
	}
}
