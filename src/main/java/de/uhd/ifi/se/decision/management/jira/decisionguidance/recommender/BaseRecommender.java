package de.uhd.ifi.se.decision.management.jira.decisionguidance.recommender;

import com.atlassian.jira.user.ApplicationUser;
import de.uhd.ifi.se.decision.management.jira.decisionguidance.knowledgesources.KnowledgeSource;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeStatus;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.persistence.KnowledgePersistenceManager;
import de.uhd.ifi.se.decision.management.jira.view.decisionguidance.Recommendation;
import de.uhd.ifi.se.decision.management.jira.view.decisionguidance.RecommendationEvaluation;

import java.util.List;
import java.util.stream.Collectors;

public abstract class BaseRecommender<T> {

	protected List<Recommendation> recommendations;
	protected List<KnowledgeSource> knowledgeSources;


	public BaseRecommender addKnowledgeSource(KnowledgeSource knowledgeSource) {
		this.knowledgeSources.add(knowledgeSource);
		return this;
	}

	public BaseRecommender addKnowledgeSource(List<? extends KnowledgeSource> knowledgeSources) {
		this.knowledgeSources.addAll(knowledgeSources);
		return this;
	}

	public List<KnowledgeSource> getKnowledgeSources() {
		return this.knowledgeSources;
	}

	public abstract List<Recommendation> getRecommendation();

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
		for (Recommendation recommendation : this.recommendations) {
			KnowledgeElement alternative = new KnowledgeElement();

			//Set information
			alternative.setId(id);
			id += 1;
			alternative.setSummary(recommendation.getRecommendations());
			alternative.setType(KnowledgeType.ALTERNATIVE);
			alternative.setDescription("");
			alternative.setProject(projectKey);
			alternative.setDocumentationLocation("s");
			alternative.setStatus(KnowledgeStatus.RECOMMENDED);

			KnowledgeElement insertedElement = manager.insertKnowledgeElement(alternative, user, rootElement);
			insertedElement.setStatus(KnowledgeStatus.RECOMMENDED);
			manager.updateKnowledgeElement(insertedElement, user);
			manager.insertLink(rootElement, insertedElement, user);
		}
	}

	/**
	 * Checks if the knowledge source exists and activates it
	 *
	 * @param knowledgeSources
	 * @param knowledgeSourceName
	 * @return
	 */
	public BaseRecommender withKnowledgeSource(List<? extends KnowledgeSource> knowledgeSources, String knowledgeSourceName) {
		for (KnowledgeSource knowledgeSource : knowledgeSources) {
			if (knowledgeSource.getName().equals(knowledgeSourceName)) {
				knowledgeSource.setActivated(true);
				this.addKnowledgeSource(knowledgeSource);
			}
		}
		return this;
	}

	public abstract BaseRecommender evaluate(T object);

	public abstract RecommendationEvaluation execute();


	public double calculateFScore(int truePositive, int falseNegative, int falsePositive) {
		return truePositive / (truePositive + .5 * (falsePositive + falseNegative));
	}

	public double calculateFScore(double precision, double recall) {
		return 2 * ((precision * recall) / (precision + recall));
	}

}
