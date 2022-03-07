package de.uhd.ifi.se.decision.management.jira.recommendation.decisionguidance;

import javax.xml.bind.annotation.XmlElement;

import de.uhd.ifi.se.decision.management.jira.recommendation.decisionguidance.projectsource.ProjectSource;
import de.uhd.ifi.se.decision.management.jira.recommendation.decisionguidance.rdfsource.RDFSource;
import org.codehaus.jackson.annotate.JsonSubTypes;
import org.codehaus.jackson.annotate.JsonTypeInfo;

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
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "className")
@JsonSubTypes({@JsonSubTypes.Type(value = RDFSource.class, name = "RDFSource"), @JsonSubTypes.Type(value = ProjectSource.class, name = "ProjectSource")})
public abstract class KnowledgeSource {

	/**
	 * Name of the KnowledgeSource, e.g. "DBpedia"
	 */
	protected String name;

	/**
	 * Whether recommendations from this KnowledgeSource should be requested or not
	 */
	protected boolean activated;

	/**
	 * @param name {@link KnowledgeSource#name}
	 * @param activated {@link KnowledgeSource#activated}
	 */
	public KnowledgeSource(String name, boolean activated) {
		this.name = name;
		this.activated = activated;
	}

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
		return activated;
	}

	/**
	 * @param activated
	 *            true if the knowledge source is currently active, i.e. it used to
	 *            generate recommendations.
	 */
	public void setActivated(boolean activated) {
		this.activated = activated;
	}

	/**
	 * @return icon that helps the user to quickly identify the knowledge source.
	 *         Icons need to be available via Atlassian User Interace (AUI), e.g.
	 *         "aui-iconfont-download".
	 * @see    <a href="https://aui.atlassian.com/aui/6.0/docs/icons.html">Atlassian
	 *         Icon Documentation</a>
	 */
	@XmlElement
	abstract public String getIcon();

}
