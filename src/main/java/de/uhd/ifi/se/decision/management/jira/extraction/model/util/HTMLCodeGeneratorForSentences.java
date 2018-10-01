package de.uhd.ifi.se.decision.management.jira.extraction.model.util;

import de.uhd.ifi.se.decision.management.jira.extraction.model.Sentence;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;

public class HTMLCodeGeneratorForSentences {

	private Sentence sentence;

	public String getCodedElement(Sentence sentence) {
		this.sentence = sentence;

		if (sentence.isRelevant() && sentence.isPlainText()) {
			return "<span class=\"sentence " + sentence.getKnowledgeTypeString() + "\"  id  = ui" + sentence.getId()
					+ ">" + this.getOpeningTagSpan() + "<span class = sentenceBody>" + sentence.getBody() + "</span>"
					+ this.getClosingTagSpan() + "</span>";
		}
		if (!sentence.isRelevant() && sentence.isPlainText()) {
			return "<span class=\"sentence isNotRelevant\"  id  = ui" + sentence.getId() + ">"
					+ this.getOpeningTagSpan() + "<span class = sentenceBody>" + sentence.getBody() + "</span>"
					+ this.getClosingTagSpan() + "</span>";
		}
		if (!sentence.isRelevant() && !sentence.isPlainText()) {
			return this.getSpecialBodyWithHTMLCodes();
		}
		return "<span class=\"sentence " + sentence.getKnowledgeTypeString() + "\"  id  = ui" + sentence.getId() + ">"
				+ this.getOpeningTagSpan() + "<span class = sentenceBody>" + sentence.getBody() + "</span>"
				+ this.getClosingTagSpan() + "</span>";

	}

	public String getOpeningTagSpan() {
		if (sentence.getType() == null || sentence.getType() == KnowledgeType.OTHER || !sentence.isRelevant()) {
			return "<span class =tag></span>";
		}
		String typeText = sentence.getType().toString();
		if (sentence.getType().equals(KnowledgeType.ARGUMENT)) {
			typeText = sentence.getArgument();
		}
		return "<span class =tag>[" + typeText + "]</span>";
	}

	public String getClosingTagSpan() {
		if (sentence.getType() == null || sentence.getType() == KnowledgeType.OTHER || !sentence.isRelevant()) {
			return "<span class =tag></span>";
		}
		String typeText = sentence.getType().toString();
		if (sentence.getType().equals(KnowledgeType.ARGUMENT)) {
			typeText = sentence.getArgument();
		}
		return "<span class =tag>[/" + typeText + "]</span>";
	}

	public String getSpecialBodyWithHTMLCodes() {
		// quotes are replaced on js side
		if (sentence.getBody().contains("{quote}")) {
			return sentence.getBody();
		}
		// code and noformats need to be escaped in a special way
		return "<div class=\"preformatted panel\" style=\"border-width: 1px;\"><div class=\"preformattedContent panelContent\">"
				+ "<pre> " + sentence.getBody().replace("\"", "\\\"").replaceAll("&", "&amp").replaceAll("<", "&lt")
						.replaceAll(">", "&gt")
				+ "</pre></div></div>";
	}

}
