package de.uhd.ifi.se.decision.management.jira.recommendation.linkrecommendation;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeProject;
import de.uhd.ifi.se.decision.management.jira.recommendation.RecommendationScore;
import de.uhd.ifi.se.decision.management.jira.recommendation.linkrecommendation.contextinformation.ComponentContextInformationProvider;
import de.uhd.ifi.se.decision.management.jira.recommendation.linkrecommendation.contextinformation.ContextInformationProvider;
import de.uhd.ifi.se.decision.management.jira.recommendation.linkrecommendation.contextinformation.DecisionGroupContextInformationProvider;
import de.uhd.ifi.se.decision.management.jira.recommendation.linkrecommendation.contextinformation.DecisionProblemContextInformationProvider;
import de.uhd.ifi.se.decision.management.jira.recommendation.linkrecommendation.contextinformation.KnowledgeTypeContextInformationProvider;
import de.uhd.ifi.se.decision.management.jira.recommendation.linkrecommendation.contextinformation.SolutionOptionContextInformationProvider;
import de.uhd.ifi.se.decision.management.jira.recommendation.linkrecommendation.contextinformation.TextualSimilarityContextInformationProvider;
import de.uhd.ifi.se.decision.management.jira.recommendation.linkrecommendation.contextinformation.TimeContextInformationProvider;
import de.uhd.ifi.se.decision.management.jira.recommendation.linkrecommendation.contextinformation.TracingContextInformationProvider;
import de.uhd.ifi.se.decision.management.jira.recommendation.linkrecommendation.contextinformation.UserContextInformationProvider;

/**
 * Contains the configuration details for the link recommendation and duplicate
 * recognition for one Jira project (see {@link DecisionKnowledgeProject}).
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class LinkRecommendationConfiguration {

	private double minProbability;
	private List<ContextInformationProvider> contextInformationProviders;
	private int maxRecommendations;

	/**
	 * Constructs an object with default values.
	 */
	@JsonCreator
	public LinkRecommendationConfiguration() {
		this.minProbability = 0.75;
		this.contextInformationProviders = getAllContextInformationProviders();
		this.maxRecommendations = 5;
	}

	public static List<ContextInformationProvider> getAllContextInformationProviders() {
		List<ContextInformationProvider> contextInformationProviders = new ArrayList<>();
		contextInformationProviders.add(new TextualSimilarityContextInformationProvider());
		contextInformationProviders.add(new TracingContextInformationProvider());
		contextInformationProviders.add(new TimeContextInformationProvider());
		contextInformationProviders.add(new UserContextInformationProvider());
		contextInformationProviders.add(new ComponentContextInformationProvider());
		contextInformationProviders.add(new DecisionGroupContextInformationProvider());
		contextInformationProviders.add(new KnowledgeTypeContextInformationProvider());
		contextInformationProviders.add(new DecisionProblemContextInformationProvider());
		contextInformationProviders.add(new SolutionOptionContextInformationProvider());
		// contextInformationProviders.add(new
		// ActiveElementsContextInformationProvider());
		return contextInformationProviders;
	}

	/**
	 * @return minimum {@link RecommendationScore} for link recommendation. To
	 *         calculate the link recommendations, the knowledge elements of a
	 *         project are used as a basis. Currently four context indicators are
	 *         used for the calculation: User, Time, Links, and Text. For each
	 *         indicator the values are scaled from 0.0 to 1.0. The value 1.0
	 *         indicates the most similar regarding the current context. Lastly, the
	 *         values are divided by the number of examined contexts to get scores
	 *         between 0.0 and 1.0.
	 */
	@XmlElement
	public double getMinProbability() {
		return minProbability;
	}

	/**
	 * @param minProbability
	 *            minimum score for link suggestion. To calculate the link
	 *            suggestions, the knowledge elements of a project are used as a
	 *            basis. Currently four context indicators are used for the
	 *            calculation: User, Time, Links, and Text. For each indicator the
	 *            values are scaled from 0.0 to 1.0. The value 1.0 indicates the
	 *            most similar regarding the current context. Lastly, the values are
	 *            divided by the number of examined contexts to get scores between
	 *            0.0 and 1.0.
	 */
	@JsonProperty
	public void setMinProbability(double minProbability) {
		this.minProbability = minProbability;
	}

	/**
	 * @return link recommendation rules as {@link ContextInformationProvider}
	 *         objects.
	 */
	@XmlElement
	public List<ContextInformationProvider> getContextInformationProviders() {
		return contextInformationProviders;
	}

	/**
	 * @param contextInformationProviders
	 *            link recommendation rules as {@link ContextInformationProvider}
	 *            objects.
	 */
	@JsonProperty
	public void setContextInformationProviders(List<ContextInformationProvider> contextInformationProviders) {
		this.contextInformationProviders = contextInformationProviders;
	}

	@XmlElement
	public int getMaxRecommendations() {
		return maxRecommendations;
	}

	@JsonProperty
	public void setMaxRecommendations(int maxRecommendations) {
		this.maxRecommendations = maxRecommendations;
	}
}