package de.uhd.ifi.se.decision.management.jira.decisionguidance.recommender;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.atlassian.jira.user.ApplicationUser;

import de.uhd.ifi.se.decision.management.jira.decisionguidance.knowledgesources.KnowledgeSource;
import de.uhd.ifi.se.decision.management.jira.decisionguidance.recommender.factory.RecommenderFactory;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeStatus;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.persistence.ConfigPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.persistence.KnowledgePersistenceManager;
import de.uhd.ifi.se.decision.management.jira.view.decisionguidance.Recommendation;

/**
 * Takes the input from the UI and passes it to the configured knowledge
 * sources.
 * 
 * @param <T>
 *            data type of the input, e.g. String, KnowledgeElement
 */
public abstract class BaseRecommender<T> {

	protected T input;
	protected List<Recommendation> recommendations;
	protected List<KnowledgeSource> knowledgeSources;

	public void setInput(T input) {
		this.input = input;
	}

	public BaseRecommender<T> addKnowledgeSource(KnowledgeSource knowledgeSource) {
		knowledgeSources.add(knowledgeSource);
		return this;
	}

	public BaseRecommender<T> addKnowledgeSource(List<? extends KnowledgeSource> knowledgeSources) {
		this.knowledgeSources.addAll(knowledgeSources);
		return this;
	}

	public abstract RecommenderType getRecommenderType();

	public List<KnowledgeSource> getKnowledgeSources() {
		return knowledgeSources;
	}

	public List<KnowledgeSource> getActivatedKnowledgeSources() {
		return knowledgeSources.stream().filter(KnowledgeSource::isActivated).collect(Collectors.toList());
	}

	public List<Recommendation> getRecommendations() {
		for (KnowledgeSource knowledgeSource : this.getActivatedKnowledgeSources()) {
			recommendations.addAll(getRecommendations(knowledgeSource));
		}
		return recommendations;
	}

	public abstract List<Recommendation> getRecommendations(KnowledgeSource knowledgeSource);

	protected List<Recommendation> removeDuplicated(List<? extends Recommendation> recommendations) {
		return recommendations.stream().distinct().collect(Collectors.toList());
	}

	/**
	 * Adds all recommendation to the knowledge graph with the status "recommended".
	 * The recommendations will be appended to the root element
	 *
	 * @param rootElement
	 * @param user
	 * @param projectKey
	 */
	public void addToKnowledgeGraph(KnowledgeElement rootElement, ApplicationUser user, String projectKey) {
		KnowledgePersistenceManager manager = KnowledgePersistenceManager.getOrCreate(projectKey);
		int id = 0;
		for (Recommendation recommendation : recommendations) {
			KnowledgeElement alternative = new KnowledgeElement();

			// Set information
			alternative.setId(id);
			id += 1;
			alternative.setSummary(recommendation.getRecommendation());
			alternative.setType(KnowledgeType.ALTERNATIVE);
			alternative.setDescription("");
			alternative.setProject(projectKey);
			alternative.setDocumentationLocation("s");
			alternative.setStatus(KnowledgeStatus.RECOMMENDED);

			KnowledgeElement insertedElement = manager.insertKnowledgeElement(alternative, user, rootElement);
			manager.insertLink(rootElement, insertedElement, user);
		}
	}

	public static List<Recommendation> getAllRecommendations(String projectKey, KnowledgeElement knowledgeElement,
			String keyword) {
		List<KnowledgeSource> knowledgeSources = ConfigPersistenceManager
				.getAllActivatedKnowledgeSources(projectKey);
		List<BaseRecommender> recommenders = new ArrayList<>();

		for (Map.Entry<String, Boolean> entry : ConfigPersistenceManager.getRecommendationInputAsMap(projectKey)
				.entrySet()) {
			if (entry.getValue()) {
				BaseRecommender recommender = RecommenderFactory
						.getRecommender(RecommenderType.valueOf(entry.getKey()));
				recommender.addKnowledgeSource(knowledgeSources);
				recommenders.add(recommender);
			}
		}

		List<Recommendation> recommendations = new ArrayList<>();
		for (BaseRecommender recommender : recommenders) {
			for (KnowledgeSource knowledgeSource : knowledgeSources) {
				if (RecommenderType.KEYWORD.equals(recommender.getRecommenderType())) // TODO implement a more
					// advanced logic that is
					// extensible
					recommender.setInput(keyword);
				else {
					recommender.setInput(knowledgeElement);
				}

				try {
					recommendations.addAll(recommender.getRecommendations(knowledgeSource));
				} catch (Exception e) {
				}

			}

			// if (checkIfKnowledgeSourceNotConfigured(recommender)) {
			// return Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error",
			// "There is no knowledge source configured! <a
			// href='/jira/plugins/servlet/condec/settings?projectKey="
			// + projectKey + "&category=decisionGuidance'>Configure</a>"))
			// .build();
			// }

			// if (ConfigPersistenceManager.getAddRecommendationDirectly(projectKey))
			// recommender.addToKnowledgeGraph(knowledgeElement,
			// AuthenticationManager.getUser(request), projectKey);
		}
		return recommendations;
	}

}
