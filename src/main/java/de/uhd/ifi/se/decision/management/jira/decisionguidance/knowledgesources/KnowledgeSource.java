package de.uhd.ifi.se.decision.management.jira.decisionguidance.knowledgesources;

import java.util.List;

import de.uhd.ifi.se.decision.management.jira.decisionguidance.recommender.RecommenderType;
import de.uhd.ifi.se.decision.management.jira.view.decisionguidance.Recommendation;

/**
 * A Knowledge Source contains all the configured data from the user and is
 * stored in the system
 */
public abstract class KnowledgeSource {

	protected String projectKey;
	protected boolean isActivated;
	protected String name;
	protected String icon;

	// TODO Remove
	protected InputMethod inputMethod;
	protected RecommenderType recommenderType;

	/**
	 * calculates the concrete result for a knowledgesource getInputMethod() and
	 * setData() must be implemented by the concrete knowledge source
	 *
	 * @param object
	 * @return
	 */
	public List<Recommendation> getRecommendations(Object object) {
		getInputMethod();
		inputMethod.setKnowledgeSource(this);
		return inputMethod.getRecommendations(object);
	}

	public abstract <T> InputMethod<T, KnowledgeSource> getInputMethod();

	public String getProjectKey() {
		return projectKey;
	}

	public void setProjectKey(String projectKey) {
		this.projectKey = projectKey;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isActivated() {
		return isActivated;
	}

	public void setActivated(boolean activated) {
		isActivated = activated;
	}

	public void setInputMethod(InputMethod inputMethod) {
		this.inputMethod = inputMethod;
	}

	public void setRecommenderType(RecommenderType recommenderType) {
		this.recommenderType = recommenderType;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}
}
