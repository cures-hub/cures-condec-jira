package de.uhd.ifi.se.decision.management.jira.extraction.model;

import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;

public class HTMLCodeGeneratorForSentences {
	
	private Ssentence sentence;
	
	public HTMLCodeGeneratorForSentences(Ssentence sentence) {
		this.sentence = sentence;		
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
