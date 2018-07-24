package de.uhd.ifi.se.decision.management.jira.extraction.classification;

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



	public static void trainModel() throws Exception {
		//TODO: Finish this
		Instances structure = null;

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
