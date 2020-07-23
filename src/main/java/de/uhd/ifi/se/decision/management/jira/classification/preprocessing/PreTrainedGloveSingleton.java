package de.uhd.ifi.se.decision.management.jira.classification.preprocessing;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class PreTrainedGloveSingleton {
	private static final Logger LOGGER = LoggerFactory.getLogger(PreTrainedGloveSingleton.class);

	private static PreTrainedGloveSingleton instance;
	private Map<String, Double[]> map;
	private Integer dimensions;

	private PreTrainedGloveSingleton(File file) {
		this.dimensions = 50;
		this.map = new HashMap<>();
		try (BufferedReader br = new BufferedReader(new FileReader(file))) {
			String line = br.readLine();
			while (line != null) {
				String[] attributes = line.split(" ");

				List<Double> vector = new ArrayList<Double>();
				// Skip first entry because that is the itself word.
				for (int i = 1; i < attributes.length; i++) {
					vector.add(Double.parseDouble(attributes[i]));
				}
				map.put(attributes[0], Arrays.copyOf(vector.toArray(), vector.size(), Double[].class));
				line = br.readLine();
			}
		} catch (IOException e) {
			LOGGER.error(e.getMessage());
		}

	}

	public static PreTrainedGloveSingleton getInstance() {
		if (instance == null){
			File file = new File(Preprocessor.DEFAULT_DIR + "glove.6b.50d.csv");
			if (!file.exists()) {
				return null;
			}
			return PreTrainedGloveSingleton.getInstance(new File(Preprocessor.DEFAULT_DIR + "glove.6b.50d.csv"));
		}
		return instance;
	}

	// This method is private because at the moment only the 50D vector is used.
	public static PreTrainedGloveSingleton getInstance(File file) {
		if (PreTrainedGloveSingleton.instance == null) {
			PreTrainedGloveSingleton.instance = new PreTrainedGloveSingleton(file);
		}
		return PreTrainedGloveSingleton.instance;
	}

	/**
	 * This method gets a word as a parameter and returns a List of Double values
	 * representing the relationship between words. If no relationship status is
	 * known a Lost of zeroes is returned.
	 *
	 * @param word
	 *            holds the string for which a vector has to be determined
	 * @return
	 */
	public List<Double> getWordVector(String word) {
		Double[] gloveResult = this.map.get(word);
		if (gloveResult != null) {
			return Arrays.asList(gloveResult);
		} else {
			return new ArrayList<Double>(Collections.nCopies(this.dimensions, 0.0));
		}

	}
}
