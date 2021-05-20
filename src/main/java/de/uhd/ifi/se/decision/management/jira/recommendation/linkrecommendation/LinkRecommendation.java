package de.uhd.ifi.se.decision.management.jira.recommendation.linkrecommendation;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonSubTypes;
import org.codehaus.jackson.annotate.JsonTypeInfo;
import org.codehaus.jackson.annotate.JsonTypeInfo.As;
import org.codehaus.jackson.annotate.JsonTypeInfo.Id;

import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeGraph;
import de.uhd.ifi.se.decision.management.jira.model.Link;
import de.uhd.ifi.se.decision.management.jira.recommendation.Recommendation;
import de.uhd.ifi.se.decision.management.jira.recommendation.RecommendationScore;
import de.uhd.ifi.se.decision.management.jira.recommendation.RecommendationType;

/**
 * Models a recommendation of a new link in the {@link KnowledgeGraph} between
 * two {@link KnowledgeElement}s that are currently not linked. The relevance of
 * the recommendation is represented by the {@link RecommendationScore}.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonTypeInfo(use = Id.NAME, include = As.PROPERTY)
@JsonSubTypes({ @JsonSubTypes.Type(value = LinkRecommendation.class, name = "LINK"),
		@JsonSubTypes.Type(value = DuplicateRecommendation.class, name = "DUPLICATE") })
public class LinkRecommendation extends Link implements Recommendation {

	private static final long serialVersionUID = 1L;
	private RecommendationScore score;

	@JsonCreator
	public LinkRecommendation(@JsonProperty("source") KnowledgeElement baseElement,
			@JsonProperty("target") KnowledgeElement targetElement) {
		super(baseElement, targetElement);
		score = new RecommendationScore();
	}

	@Override
	public RecommendationScore getScore() {
		return score;
	}

	@Override
	public RecommendationType getRecommendationType() {
		return RecommendationType.LINK;
	}

	@Override
	public void setScore(RecommendationScore score) {
		this.score = score;
	}
}
