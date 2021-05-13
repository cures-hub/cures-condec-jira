package de.uhd.ifi.se.decision.management.jira.quality.consistency.suggestions;

import javax.xml.bind.annotation.XmlElement;

import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.Link;

public class LinkSuggestion extends Link implements Comparable<LinkSuggestion>, Suggestion<KnowledgeElement> {

	private static final long serialVersionUID = 1L;
	private SimilarityScore score;

	public LinkSuggestion(KnowledgeElement baseElement, KnowledgeElement targetElement) {
		super(baseElement, targetElement);
		this.score = new SimilarityScore();
	}

	@XmlElement
	public double getTotalScore() {
		return score.getTotal();
	}

	public SimilarityScore getScore() {
		return score;
	}

	public void addToScore(Double score, String field) {
		this.score.put(field, score);
	}

	@Override
	public int compareTo(LinkSuggestion o) {
		if (o == null) {
			return -1;
		}
		int compareValue = 0;
		if (this.getTotalScore() > o.getTotalScore()) {
			compareValue = 1;
		} else {
			compareValue = -1;
		}
		return compareValue;
	}

	@Override
	public KnowledgeElement getSuggestion() {
		return getTarget();
	}

	@Override
	public SuggestionType getSuggestionType() {
		return SuggestionType.LINK;
	}
}
