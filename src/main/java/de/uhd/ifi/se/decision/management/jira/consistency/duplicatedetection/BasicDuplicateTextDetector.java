package de.uhd.ifi.se.decision.management.jira.consistency.duplicatedetection;

import com.atlassian.jira.issue.Issue;
import de.uhd.ifi.se.decision.management.jira.classification.preprocessing.Preprocessor;
import de.uhd.ifi.se.decision.management.jira.consistency.suggestions.DuplicateFragment;

import java.util.ArrayList;
import java.util.List;

public class BasicDuplicateTextDetector implements DuplicateDetectionStrategy {

	private static Preprocessor preprocessor;
	private static final String fieldUsedForDetection = "DESCRIPTION";
	private int minDuplicateLength;

	public BasicDuplicateTextDetector(int minDuplicateLength){
		if (preprocessor == null){
			preprocessor = new Preprocessor();

		}
		this.minDuplicateLength = minDuplicateLength;
	}

	private String cleanMarkdown(String markdown){
		return markdown.replaceAll("[\\|\\[\\]]+", " ").replaceAll("^(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]", "").replaceAll("h[0-9]+", "").replaceAll("[;\\/:*?\"<>&.{},'#!+@-]+", " ").replaceAll("[\n\r]+", " ").replaceAll("[0-9]+", " ");
	}

	@Override
	public List<DuplicateFragment> detectDuplicateTextFragments(Issue i1, Issue i2) throws Exception {
		String s1 = cleanMarkdown(i1.getDescription());
		String s2 = cleanMarkdown(i2.getDescription());
		List<DuplicateFragment> duplicateList = new ArrayList();
		BasicDuplicateTextDetector.preprocessor.preprocess(s1);
		List<CharSequence> preprocessedS1Tokens = BasicDuplicateTextDetector.preprocessor.getTokens();

		BasicDuplicateTextDetector.preprocessor.preprocess(s2);
		s2 = String.join(" ",BasicDuplicateTextDetector.preprocessor.getTokens());

		// iterate over string s1 as list
		int index = 0;
		while (index < preprocessedS1Tokens.size() - minDuplicateLength + 1) {
			int numberOfDuplicateTokens = minDuplicateLength;
			int indexOfDuplicate = 0;
			int lastIndexOfDuplicate = 0;

			boolean duplicateFound = false;
			String stringToSearch = "";
			String lastStringToSearch = "";

			// build DuplicateTextFragment in loop?
			while (indexOfDuplicate != -1) {
				lastIndexOfDuplicate = indexOfDuplicate;
				lastStringToSearch = stringToSearch;
				if (index + numberOfDuplicateTokens > preprocessedS1Tokens.size()) {
					break;
				}
				stringToSearch = generateStringToSearch(preprocessedS1Tokens, index, numberOfDuplicateTokens);
				indexOfDuplicate = s2.indexOf(stringToSearch);
				if (!duplicateFound) {
					duplicateFound = indexOfDuplicate != -1;
				}
				numberOfDuplicateTokens++;
			}
			if (duplicateFound) {
				duplicateList.add(new DuplicateFragment(i1, i2, s2, lastIndexOfDuplicate, lastStringToSearch.length(), BasicDuplicateTextDetector.fieldUsedForDetection));
				index += numberOfDuplicateTokens - 2; // number of duplicate tokens is one more than found tokens
			}

			index++;

		}
		return duplicateList;
	}

	private static String generateStringToSearch(List<CharSequence> preprocessedS1Tokens, int index, int numberOfDuplicateTokens) {
		StringBuilder stringToSearch = new StringBuilder();
		for (int i = index; i < preprocessedS1Tokens.size() && i < index + numberOfDuplicateTokens; i++) {
			stringToSearch.append(preprocessedS1Tokens.get(i)).append(" ");
		}
		return stringToSearch.toString().trim();
	}


}
