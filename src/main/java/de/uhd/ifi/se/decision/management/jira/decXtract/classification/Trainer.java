package de.uhd.ifi.se.decision.management.jira.decXtract.classification;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import de.uhd.ifi.se.decision.management.jira.decXtract.model.Comment;
import de.uhd.ifi.se.decision.management.jira.decXtract.model.Sentence;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayesMultinomial;
import weka.classifiers.functions.SMO;
import weka.classifiers.meta.FilteredClassifier;
import weka.core.Instances;
import weka.core.Debug.Random;
import weka.core.tokenizers.NGramTokenizer;
import weka.core.tokenizers.Tokenizer;
import weka.filters.unsupervised.attribute.StringToWordVector;

public class Trainer {

	private static Tokenizer getTokenizer() throws Exception {
		Tokenizer t = new NGramTokenizer();
		String[] options = weka.core.Utils.splitOptions("weka.core.tokenizers.NGramTokenizer -max 3 -min 1 -delimiters \" \\r\\n\\t.,;:\\'\\\"()?!\"");
		t.setOptions(options);
		return t;
	}

	private static StringToWordVector getSTWV() throws Exception {
		StringToWordVector stwv = new StringToWordVector();
	    stwv.setLowerCaseTokens(true);
	    stwv.setIDFTransform(true);
	    stwv.setTFTransform(true);
	    stwv.setTokenizer(getTokenizer());
	    stwv.setWordsToKeep(1000000);
	    return stwv;
	}

	private static Classifier getNBM() throws Exception {
		NaiveBayesMultinomial nbm = new NaiveBayesMultinomial();
		nbm.setBatchSize("100");
		return  nbm;
	}

	private static Classifier getClassifier() throws Exception {
		SMO nb = new SMO();
		return nb;
	}

	public static Instances readDataFromARFF(String path) {
		BufferedReader reader = null;
		Instances structure = null;
		try {
			reader = new BufferedReader(new FileReader(path));
		} catch (FileNotFoundException e) {
			System.err.println("File: "+path +" not found");
			return null;
		}
		try {
			structure = new Instances(reader);
		} catch (IOException e) {
			System.err.println("File: "+path+" not readable");
			return null;
		}
	    structure.setClassIndex(structure.numAttributes() - 1);
	    return structure;
	}

	public static void writeARFFfile(ArrayList<Comment> comments) {

		//String options = Arrays.toString(fc.getOptions());
		String heading = "@relation " + "sentence" + "\n\n";
		String a1 = "@attribute sentence string\n";
		String a2 = "@attribute isRelevant {0,1}\n\n";
		String d = "@data\n";
		String data ="";

		for(Comment comment: comments) {
			for(Sentence sentence: comment.getSentences()) {
				String s = sentence.getBody();
				if(s.length() > 1) {
					data = data + "'" +s+"',?\n";

				}
			}

		}
		String conc = heading + a1+a2+d+data;
		// System.out.println("Wrote to: " + System.getProperty("user.dir"));
		FileWriter fw = null;
		BufferedWriter bw = null;
		try {
			fw = new FileWriter("./data.arff");
			bw = new BufferedWriter(fw);
			bw.write(conc);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
			try {
				if (bw != null)	bw.close();
				if (fw != null)	fw.close();

			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}



	public static void trainModel() throws Exception {

		Instances structure = readDataFromARFF("./lucene_sentence_isRelevant.arff");

		FilteredClassifier fc = new FilteredClassifier();
	    fc.setFilter(getSTWV());
	    fc.setClassifier(getClassifier());

	    //Initialize Evaluation
	    Evaluation rate = new Evaluation(structure);
	    Random seed = new Random(1);
	    Instances datarandom = new Instances(structure);
	    datarandom.randomize(seed);

	    //Initialize cross validation
	    int folds = 10;
	    datarandom.stratify(folds);
	    rate.crossValidateModel(fc, structure, folds, seed);

	    //Build CLassifier
	    fc.buildClassifier(structure);

	    System.out.println(fc);
	}
}
