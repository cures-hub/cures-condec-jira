package de.uhd.ifi.se.decision.management.jira.classification.preprocessing;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

class PreTrainedGloveSingleton {

    private static PreTrainedGloveSingleton instance;
    private Map<String, Double[]> map;
    private Integer dimensions;
    private static String GLOVE_FILE_PATH = "src" + File.separator + "main" + File.separator + "resources"
            + File.separator + "classifier" + File.separator
            + "language_models" + File.separator;
    ;

    private PreTrainedGloveSingleton(Integer dimensions) {
        this.dimensions = dimensions;
        this.map = new HashMap<>();
        String fullFilename = GLOVE_FILE_PATH + "glove.6b." + dimensions + "d.csv";
        try (BufferedReader br = Files.newBufferedReader(Paths.get(fullFilename),
                StandardCharsets.UTF_8)) {
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
        return PreTrainedGloveSingleton.getInstance(50);
    }

    // This method is private because at the moment only the 50D vector is used.
    private static PreTrainedGloveSingleton getInstance(Integer dimensions) {
        if (PreTrainedGloveSingleton.instance == null) {
            PreTrainedGloveSingleton.instance = new PreTrainedGloveSingleton(dimensions);
        }
        return PreTrainedGloveSingleton.instance;
    }

    /**
     *  This method gets a word as a parameter and returns a List of Double values representing the relationship between words.
     *  If no relationship status is known a Lost of zeroes is returned.
     *
     * @param word holds the string for which a vector has to be determined
     * @return
     */
    public List<Double> getWordVector(String word) {
        try{
            return Arrays.asList(this.map.get(word));
        } catch (Exception e){
            return new ArrayList<Double>(Collections.nCopies(this.dimensions, 0.0));
        }

    }
}
