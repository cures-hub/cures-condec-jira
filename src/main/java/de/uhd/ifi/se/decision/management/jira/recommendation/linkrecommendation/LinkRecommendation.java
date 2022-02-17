package de.uhd.ifi.se.decision.management.jira.recommendation.linkrecommendation;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeGraph;
import de.uhd.ifi.se.decision.management.jira.model.Link;
import de.uhd.ifi.se.decision.management.jira.model.LinkType;
import de.uhd.ifi.se.decision.management.jira.recommendation.Recommendation;
import de.uhd.ifi.se.decision.management.jira.recommendation.RecommendationScore;
import de.uhd.ifi.se.decision.management.jira.recommendation.RecommendationType;

/**
 * Models a recommendation of a new link in the {@link KnowledgeGraph} between
 * two {@link KnowledgeElement}s that are currently not linked. The relevance of
 * the recommendation is represented by the {@link RecommendationScore}.
 * 
 * Can also represent a potential duplicate relationship if
 * {@link #recommendationType} is {@link RecommendationType#DUPLICATE}.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class LinkRecommendation extends Link implements Recommendation {

	private static final long serialVersionUID = 1L;
	private RecommendationScore score;
	private boolean isDiscarded;
	private RecommendationType recommendationType;

	@JsonCreator
	public LinkRecommendation(@JsonProperty("source") KnowledgeElement baseElement,
			@JsonProperty("target") KnowledgeElement targetElement) {
		super(baseElement, targetElement);
		score = new RecommendationScore();
		setType(LinkType.RECOMMENDED);
		recommendationType = RecommendationType.LINK;
	}

	@Override
	public RecommendationScore getScore() {
		return score;
	}

	@Override
	public RecommendationType getRecommendationType() {
		return recommendationType;
	}

	/**
	 * @param recommendationType
	 *            either {@link RecommendationType#LINK} or
	 *            {@link RecommendationType#DUPLICATE}.
	 */
	@JsonProperty
	public void setRecommendationType(RecommendationType recommendationType) {
		this.recommendationType = recommendationType;
	}

	@Override
	public void setScore(RecommendationScore score) {
		this.score = score;
	}

	@Override
	public void setDiscarded(boolean isDiscarded) {
		this.isDiscarded = isDiscarded;
	}

	@Override
	public boolean isDiscarded() {
		return isDiscarded;
	}
}
