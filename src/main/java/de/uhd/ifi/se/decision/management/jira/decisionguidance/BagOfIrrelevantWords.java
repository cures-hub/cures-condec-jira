package de.uhd.ifi.se.decision.management.jira.decisionguidance;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

// TODO Move to preprocessor
public class BagOfIrrelevantWords {

	private List<String> irrelevantWords;

	public BagOfIrrelevantWords(String irrelevantWordsString) {
		irrelevantWords = Arrays.asList(irrelevantWordsString.split(";"));
	}

	public boolean checkIfWordIsRelevant(String word) {
		return !irrelevantWords.contains(word.toUpperCase().trim());
	}

	public String cleanSentence(String[] tokens) {
		List<String> cleanedTokens = new ArrayList<>();

		for (String token : tokens) {
			if (checkIfWordIsRelevant(token.toUpperCase())) {
				cleanedTokens.add(token);
			}
		}

		String cleanedSentence = "";
		for (String token : cleanedTokens) {
			cleanedSentence += token + " ";
		}

		return cleanedSentence;
	}

	public List<String> getIrrelevantWords() {
		return irrelevantWords;
	}
}
