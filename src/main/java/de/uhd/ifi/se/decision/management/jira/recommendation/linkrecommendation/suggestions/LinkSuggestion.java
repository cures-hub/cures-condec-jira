package de.uhd.ifi.se.decision.management.jira.recommendation.linkrecommendation.suggestions;

import javax.xml.bind.annotation.XmlElement;

import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.Link;
import de.uhd.ifi.se.decision.management.jira.recommendation.Recommendation;
import de.uhd.ifi.se.decision.management.jira.recommendation.RecommendationScore;

public class LinkSuggestion extends Link implements Recommendation {

	private static final long serialVersionUID = 1L;
	private RecommendationScore score;

	public LinkSuggestion(KnowledgeElement baseElement, KnowledgeElement targetElement) {
		super(baseElement, targetElement);
		score = new RecommendationScore(0, "");
	}

	@XmlElement
	public RecommendationScore getScore() {
		return score;
	}

	@Override
	public SuggestionType getSuggestionType() {
		return SuggestionType.LINK;
	}

	@Override
	public void setScore(RecommendationScore score) {
		this.score = score;
	}
}
