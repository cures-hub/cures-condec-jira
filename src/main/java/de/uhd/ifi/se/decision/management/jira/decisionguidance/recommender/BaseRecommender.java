package de.uhd.ifi.se.decision.management.jira.decisionguidance.recommender;

import java.util.List;
import java.util.stream.Collectors;

import com.atlassian.jira.user.ApplicationUser;

import de.uhd.ifi.se.decision.management.jira.decisionguidance.knowledgesources.KnowledgeSource;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeStatus;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.persistence.KnowledgePersistenceManager;
import de.uhd.ifi.se.decision.management.jira.view.decisionguidance.Recommendation;

/**
 * Takes the input from the UI and passes it to the configured knowledge sources
 * 
 * @param <T>
 *            Datatype of the input e.g. String, KnowledgeElement
 */
public abstract class BaseRecommender<T> {

	protected T input;
	protected List<Recommendation> recommendations;
	protected List<KnowledgeSource> knowledgeSources;

	public void setInput(T input) {
		this.input = input;
	}

	public BaseRecommender<T> addKnowledgeSource(KnowledgeSource knowledgeSource) {
		this.knowledgeSources.add(knowledgeSource);
		return this;
	}

	public BaseRecommender<T> addKnowledgeSource(List<? extends KnowledgeSource> knowledgeSources) {
		this.knowledgeSources.addAll(knowledgeSources);
		return this;
	}

	public abstract RecommenderType getRecommenderType();

	public List<KnowledgeSource> getKnowledgeSources() {
		return this.knowledgeSources;
	}

	public List<KnowledgeSource> getActivatedKnowledgeSources() {
		return this.knowledgeSources.stream().filter(KnowledgeSource::isActivated).collect(Collectors.toList());
	}

	public List<Recommendation> getRecommendation() {
		for (KnowledgeSource knowledgeSource : this.getActivatedKnowledgeSources()) {
			this.recommendations.addAll(getResultFromKnowledgeSource(knowledgeSource));
		}
		return this.recommendations;
	}

	public abstract List<Recommendation> getResultFromKnowledgeSource(KnowledgeSource knowledgeSource);

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

}
