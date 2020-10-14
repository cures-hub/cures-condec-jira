package de.uhd.ifi.se.decision.management.jira.decisionguidance.recommender;

import com.atlassian.jira.user.ApplicationUser;
import de.uhd.ifi.se.decision.management.jira.decisionguidance.knowledgesources.KnowledgeSource;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeStatus;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.persistence.KnowledgePersistenceManager;
import de.uhd.ifi.se.decision.management.jira.view.decisionguidance.Recommendation;

import java.util.List;
import java.util.stream.Collectors;

public abstract class BaseRecommender {

	List<KnowledgeSource> knowledgeSources;
	List<Recommendation> recommendations;

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

	abstract public List<Recommendation> getRecommendation();

	/**
	 * Checks if the knowledge source exists and activates it
	 *
	 * @param knowledgeSources
	 * @param knowledgeSourceName
	 * @return
	 */
	public BaseRecommender addKnowledgeSourceForEvaluation(List<? extends KnowledgeSource> knowledgeSources, String knowledgeSourceName) {
		for (KnowledgeSource knowledgeSource : knowledgeSources) {
			if (knowledgeSource.getName().equals(knowledgeSourceName)) {
				knowledgeSource.setActivated(true);
				this.addKnowledgeSource(knowledgeSource);
			}
		}
		return this;
	}

	protected List<Recommendation> removeDuplicated(List<? extends Recommendation> recommendations) {
		return recommendations.stream().distinct().collect(Collectors.toList());
	}

	public List<Recommendation> evaluate() {
		return this.getRecommendation();
	}

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

			KnowledgeElement insertedElement = manager.insertKnowledgeElement(alternative, user, rootElement);
			insertedElement.setStatus(KnowledgeStatus.RECOMMENDED);
			manager.updateIssueStatus(insertedElement, user);
			manager.insertLink(rootElement, insertedElement, user);
		}

	}

}
