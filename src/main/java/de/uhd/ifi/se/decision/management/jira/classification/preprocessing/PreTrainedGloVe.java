package de.uhd.ifi.se.decision.management.jira.classification.preprocessing;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Global Vectors for Word Representation (GloVe)
 * 
 * GloVe is an unsupervised learning algorithm for obtaining vector
 * representations for words. Training is performed on aggregated global
 * word-word co-occurrence statistics from a corpus, and the resulting
 * representations showcase interesting linear substructures of the word vector
 * space.
 * 
 * https://nlp.stanford.edu/projects/glove/
 */
public class PreTrainedGloVe {
	private static final Logger LOGGER = LoggerFactory.getLogger(PreTrainedGloVe.class);

	private Map<String, double[]> wordToVectorMap;
	private int vectorLength;

	public PreTrainedGloVe() {
		this(new File(Preprocessor.DEFAULT_DIR + "glove.6b.50d.csv"));
	}

	private PreTrainedGloVe(File file) {
		wordToVectorMap = new HashMap<>();
		try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
			String line = bufferedReader.readLine();
			while (line != null) {
				String[] attributes = line.split(" ");
				vectorLength = attributes.length - 1;

				double[] vector = new double[vectorLength];
				// Skip first entry because that is the word itself.
				for (int i = 1; i < attributes.length; i++) {
					vector[i - 1] = Double.parseDouble(attributes[i]);
				}
				wordToVectorMap.put(attributes[0], vector);
				line = bufferedReader.readLine();
			}
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
		}
	}

	/**
	 * This method gets a word as a parameter and returns an array of double values
	 * representing the relationship between words. If no relationship status is
	 * known an array of zeroes is returned.
	 *
	 * @param word
	 *            holds the string for which a vector has to be determined
	 * @return array of double values representing the relationship between words.
	 */
	public double[] getWordVector(String word) {
		double[] gloveResult = wordToVectorMap.get(word);
		if (gloveResult != null) {
			return gloveResult;
		} else {
			return new double[vectorLength];
		}
	}
}
