package de.uhd.ifi.se.decision.management.jira.decisionguidance.knowledgesources;

import de.uhd.ifi.se.decision.management.jira.decisionguidance.recommender.RecommenderType;
import de.uhd.ifi.se.decision.management.jira.view.decisionguidance.Recommendation;

import java.util.List;

public abstract class KnowledgeSource {

	protected List<Recommendation> recommendations;
	protected String projectKey;
	protected boolean isActivated;
	protected String name;

	protected InputMethod inputMethod;
	protected RecommenderType recommenderType;


	/**
	 * calculates the concrete result for a knowledgesource
	 * getInputMethod() and setData() must be implemented by the concrete knowledge source
	 *
	 * @param object
	 * @return
	 */
	public List<Recommendation> getResults(Object object) {
		getInputMethod();
		setData();
		return inputMethod.getResults(object);
//		try {
//
//		} catch (Exception e) {
//
//		}
		//return new ArrayList<>();
	}

	public abstract void setData();

	public abstract InputMethod getInputMethod();


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
}
