package de.uhd.ifi.se.decision.management.jira.classification.preprocessing;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

class PreTrainedGloveSingleton {

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
                //Skip first entry because that is the itself word.
                for (int i = 1; i < attributes.length; i++) {
                    vector.add(Double.parseDouble(attributes[i]));
                }
                map.put(attributes[0], Arrays.copyOf(vector.toArray(), vector.size(), Double[].class));
                line = br.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    public static PreTrainedGloveSingleton getInstance() {
        return PreTrainedGloveSingleton.getInstance(new File(PreprocessorImpl.DEFAULT_DIR + "glove.6b.50d.csv"));
    }

    // This method is private because at the moment only the 50D vector is used.
    public static PreTrainedGloveSingleton getInstance(File file) {
        if (PreTrainedGloveSingleton.instance == null) {
            PreTrainedGloveSingleton.instance = new PreTrainedGloveSingleton(file);
        }
        return PreTrainedGloveSingleton.instance;
    }

    /**
     * This method gets a word as a parameter and returns a List of Double values representing the relationship between words.
     * If no relationship status is known a Lost of zeroes is returned.
     *
     * @param word holds the string for which a vector has to be determined
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
