package de.uhd.ifi.se.decision.management.jira.recommendation.linkrecommendation.suggestions;

import javax.xml.bind.annotation.XmlElement;

import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.Link;
import de.uhd.ifi.se.decision.management.jira.recommendation.decisionguidance.RecommendationScore;

public class LinkSuggestion extends Link implements Comparable<LinkSuggestion>, Suggestion<KnowledgeElement> {

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

	public void addToScore(double value, String field) {
		score.addSubScore(new RecommendationScore((float) value, field));
	}

	@Override
	public KnowledgeElement getSuggestion() {
		return getTarget();
	}

	@Override
	public SuggestionType getSuggestionType() {
		return SuggestionType.LINK;
	}

	@Override
	public int compareTo(LinkSuggestion o) {
		if (o == null) {
			return -1;
		}
		int compareValue = 0;
		if (this.getScore().getValue() > o.getScore().getValue()) {
			compareValue = 1;
		} else {
			compareValue = -1;
		}
		return compareValue;
	}
}
