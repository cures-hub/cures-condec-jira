package de.uhd.ifi.se.decision.management.jira.recommendation.linkrecommendation;

import javax.xml.bind.annotation.XmlElement;

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
public class LinkRecommendation extends Link implements Recommendation {

	private static final long serialVersionUID = 1L;
	private RecommendationScore score;

	public LinkRecommendation(KnowledgeElement baseElement, KnowledgeElement targetElement) {
		super(baseElement, targetElement);
		score = new RecommendationScore(0, "");
	}

	@XmlElement
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
