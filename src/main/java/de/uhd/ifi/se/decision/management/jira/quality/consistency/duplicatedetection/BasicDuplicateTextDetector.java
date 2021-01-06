package de.uhd.ifi.se.decision.management.jira.quality.consistency.duplicatedetection;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.uhd.ifi.se.decision.management.jira.classification.preprocessing.Preprocessor;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.quality.consistency.suggestions.DuplicateSuggestion;

public class BasicDuplicateTextDetector implements DuplicateDetectionStrategy {

	private Preprocessor preprocessor;
	private static final String fieldUsedForDetection = "DESCRIPTION";
	private int fragmentLength;
	private static final double MIN_SIMILARITY = 0.85;

	public BasicDuplicateTextDetector(int fragmentLength) {
		preprocessor = Preprocessor.getInstance();

		this.fragmentLength = fragmentLength;
	}

	private String cleanMarkdown(String markdown) {
		return markdown.replaceAll("[{(color)]+[:#0-9]*}", "")
				.replaceAll("(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]", "URL")
				.replaceAll("[|\\[\\]]+", " ").replaceAll("h[0-9]+", "").replaceAll("[;/:*?\"<>&.{},'#!+@-]+", " ")
				.replaceAll("[\n\r]+", " ").replaceAll("[0-9]+", "NUMBER").replaceAll("(-){2,}", "");
	}

	@Override
	public List<DuplicateSuggestion> detectDuplicates(KnowledgeElement baseElement, KnowledgeElement compareElement)
			throws Exception {
		String s1 = baseElement.getDescription();
		String s2 = compareElement.getDescription();
		List<DuplicateSuggestion> duplicateList = new ArrayList<>();
		if (s1 != null && s2 != null) {
			s1 = cleanMarkdown(s1);
			s2 = cleanMarkdown(s2);
			String[] preprocessedS1Tokens = preprocessor.getStemmedTokens(s1);
			String[] preprocessedS2Tokens = preprocessor.getStemmedTokens(s1);

			int index = 0;
			// Iterate over text.
			while (index < preprocessedS1Tokens.length - fragmentLength + 1) {
				int internalIndex = 0;
				// Get Lists of text based on the fragmentLength
				CharSequence[] sequenceToCheck = Arrays.copyOfRange(preprocessedS1Tokens, index,
						index + fragmentLength);
				CharSequence[] sequenceToCheckAgainst = Arrays.copyOfRange(preprocessedS2Tokens, internalIndex,
						Math.min(internalIndex + fragmentLength, preprocessedS2Tokens.length));

				while (calculateScore(sequenceToCheck, sequenceToCheckAgainst) <= MIN_SIMILARITY
						&& internalIndex < preprocessedS2Tokens.length - fragmentLength + 1) {
					sequenceToCheckAgainst = Arrays.copyOfRange(preprocessedS2Tokens, internalIndex,
							Math.min(internalIndex + fragmentLength, preprocessedS2Tokens.length));
					internalIndex++;
				}

				if (calculateScore(sequenceToCheck, sequenceToCheckAgainst) >= MIN_SIMILARITY) {
					// sequenceToCheck.remove(sequenceToCheck.size()-1);
					String preprocessedDuplicateSummary = String.join(" ", sequenceToCheckAgainst);
					duplicateList.add(new DuplicateSuggestion(baseElement, compareElement, preprocessedDuplicateSummary,
							0, preprocessedDuplicateSummary.length(),
							// calculateScore(sequenceToCheck, sequenceToCheckAgainst),
							fieldUsedForDetection));
					return duplicateList;
				}
				index++;
			}

		}
		return duplicateList;
	}

	// Check if the words are present in the same sequence with minor deviation k
	// allowed.
	private double calculateScore(CharSequence[] sequenceToCheck, CharSequence[] sequenceToCheckAgainst) {
		double count = 0.;
		for (CharSequence toCheck : sequenceToCheck) {
			count += Arrays.stream(sequenceToCheckAgainst).anyMatch(seq -> ((String) seq).contains(toCheck)) ? 1 : 0;
		}
		return count / sequenceToCheck.length;
	}

	// private String generateStringToSearch(List<CharSequence> tokens, int index, int numberOfDuplicateTokens) {
	// 	StringBuilder stringToSearch = new StringBuilder();
	// 	for (int i = index; i < tokens.size() && i < index + numberOfDuplicateTokens; i++) {
	// 		stringToSearch.append(tokens.get(i)).append(" ");
	// 	}
	// 	return stringToSearch.toString().trim();
	// }

}
