package de.uhd.ifi.se.decision.management.jira.decisionguidance.knowledgesources;

import de.uhd.ifi.se.decision.management.jira.decisionguidance.knowledgesources.projectsource.ProjectSource;
import de.uhd.ifi.se.decision.management.jira.decisionguidance.knowledgesources.rdfsource.RDFSource;

/**
 * Contains basic data to identifiy a knowledge source. Knowledge sources can be
 * Jira projects or DBPedia.
 * 
 * @see ProjectSource
 * @see RDFSource
 */
public abstract class KnowledgeSource {

	protected String name;
	protected boolean isActivated;
	protected String icon;

	/**
	 * @return name of the knowledge source, e.g. the name of a Jira project or a
	 *         topic in DBPedia such as "Frameworks".
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            of the knowledge source, e.g. the name of a Jira project or a
	 *            topic in DBPedia such as "Frameworks".
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return true if the knowledge source is currently active, i.e. it used to
	 *         generate recommendations.
	 */
	public boolean isActivated() {
		return isActivated;
	}

	/**
	 * @param activated
	 *            true if the knowledge source is currently active, i.e. it used to
	 *            generate recommendations.
	 */
	public void setActivated(boolean activated) {
		isActivated = activated;
	}

	/**
	 * @return icon that helps the user to quickly identify the knowledge source.
	 *         Icons need to be available via Atlassian User Interace (AUI), e.g.
	 *         "aui-iconfont-download".
	 */
	public String getIcon() {
		return icon;
	}

	/**
	 * @param icon
	 *            that helps the user to quickly identify the knowledge source.
	 *            Icons need to be available via Atlassian User Interace (AUI), e.g.
	 *            "aui-iconfont-download".
	 */
	public void setIcon(String icon) {
		this.icon = icon;
	}
}
