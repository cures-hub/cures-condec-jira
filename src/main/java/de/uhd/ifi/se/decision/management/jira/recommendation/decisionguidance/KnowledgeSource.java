package de.uhd.ifi.se.decision.management.jira.recommendation.decisionguidance;

import javax.xml.bind.annotation.XmlElement;

import de.uhd.ifi.se.decision.management.jira.recommendation.decisionguidance.projectsource.ProjectSource;
import de.uhd.ifi.se.decision.management.jira.recommendation.decisionguidance.rdfsource.RDFSource;

/**
 * Contains basic data to identify a knowledge source. Knowledge sources can be
 * Jira projects or DBPedia.
 * 
 * The knowledge sources are used to recommend solution options for decision
 * problems to the developers for decision guidance.
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
	@XmlElement
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
	@XmlElement
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
