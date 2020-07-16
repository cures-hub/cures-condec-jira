package de.uhd.ifi.se.decision.management.jira.consistency.suggestions;

import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;

public class LinkSuggestion implements Comparable<LinkSuggestion>, Suggestion<KnowledgeElement> {

	private KnowledgeElement baseElement;
	private KnowledgeElement targetElement;
	private SimilarityScore score;

	public LinkSuggestion(KnowledgeElement baseElement, KnowledgeElement targetElement) {
		this.baseElement = baseElement;
		this.targetElement = targetElement;
		this.score = new SimilarityScore();
	}


	public KnowledgeElement getBaseIssue() {
		return baseElement;
	}

	public KnowledgeElement getTargetElement() {
		return targetElement;
	}

	public Double getTotalScore() {
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
		if (o == null){
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
		return getTargetElement();
	}

	@Override
	public SuggestionType getType() {
		return SuggestionType.LINK;
	}
}
