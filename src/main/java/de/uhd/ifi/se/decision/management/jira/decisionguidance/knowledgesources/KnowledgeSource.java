package de.uhd.ifi.se.decision.management.jira.decisionguidance.knowledgesources;

import de.uhd.ifi.se.decision.management.jira.decisionguidance.resultmethods.InputMethod;
import de.uhd.ifi.se.decision.management.jira.view.decisionguidance.Recommendation;

import java.util.ArrayList;
import java.util.List;

public abstract class KnowledgeSource {

	protected KnowledgeSourceType knowledgeSourceType;

	protected List<Recommendation> recommendations;
	protected String projectKey;
	protected boolean isActivated;
	protected String name;

	InputMethod inputMethod;


	public List<Recommendation> getResults(Object object) {
		getInputMethod();
		setData();
		try {
			return inputMethod.getResults(object);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
		return new ArrayList<>();
	}

	public abstract void setData();

	public abstract void getInputMethod();

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

}
