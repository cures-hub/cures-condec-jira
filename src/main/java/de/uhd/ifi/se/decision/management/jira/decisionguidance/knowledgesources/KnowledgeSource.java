package de.uhd.ifi.se.decision.management.jira.decisionguidance.knowledgesources;

/**
 * A Knowledge Source contains all the configured data from the user and is
 * stored in the system
 */
public abstract class KnowledgeSource {

	protected String projectKey;
	protected boolean isActivated;
	protected String name;
	protected String icon;

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

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}
}
