package de.uhd.ifi.se.decision.management.jira.decXtract.classification;
/*package de.uhd.ifi.se.decision.management.jira.textclassifier;

import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.bayes.NaiveBayesMultinomial;
import weka.classifiers.bayes.NaiveBayesMultinomialUpdateable;
import weka.classifiers.bayes.NaiveBayesUpdateable;
import weka.classifiers.functions.SGD;
import weka.classifiers.functions.SMO;
import weka.classifiers.meta.FilteredClassifier;
import weka.classifiers.trees.J48;
import weka.core.Attribute;
import weka.core.Debug.Random;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ArffLoader;
import weka.core.converters.ArffSaver;
import weka.core.converters.ConverterUtils.DataSource;
import weka.core.tokenizers.NGramTokenizer;
import weka.core.tokenizers.Tokenizer;
import weka.filters.Filter;
import weka.filters.supervised.attribute.AddClassification;
import weka.filters.unsupervised.attribute.ClassAssigner;
import weka.filters.unsupervised.attribute.StringToWordVector;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Iterator;
import java.util.Scanner;

public class Classifier {


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

	private static weka.classifiers.Classifier getNBM() throws Exception {
		NaiveBayesMultinomial nbm = new NaiveBayesMultinomial();
		nbm.setBatchSize("100");
		return  nbm;
	}

	private static weka.classifiers.Classifier getClassifier() throws Exception {
		SMO nb = new SMO();
		return nb;
	}

	private static void printStats(Evaluation rate) {

	    System.out.println(rate.toSummaryString());
	    System.out.println("Correct % = " + String.format( "%.2f", rate.pctCorrect()));
	    System.out.println("Incorrect % = " + String.format( "%.2f", rate.pctIncorrect())+"\n");
	    double[][] cf = rate.confusionMatrix();
	    System.out.println(cf[0][0] +"\t"+cf[0][1]+"\n"+cf[1][0]+"\t"+cf[1][1]+"\n");
	    System.out.println("Recall 0: " +String.format( "%.2f", rate.recall(0)));
	    System.out.println("Recall 1: " +String.format( "%.2f",  rate.recall(1)));

	}

	public static double[] predictText(Instances pred,weka.classifiers.Classifier classifier) {

	    int one=0,zero=0;
	    Evaluation eval;
		try {
			eval = new Evaluation(pred);
		} catch (Exception e1) {
			return null;
		}
	    double pre[] = new double[pred.numInstances()];
	    for (int i = 0; i < pred.numInstances(); i++) {
	        try {
	            pre[i] = eval.evaluateModelOnce(classifier, pred.instance(i));
	            if(pre[i]==1.) {
			one++;
	            }else {zero++;}
	        } catch (Exception e) {
	            pre[i] = Integer.MAX_VALUE;
	        }
	    }
	    System.out.println("Relevant::\t"+one+"\nIrrelevant:\t"+zero);
	    return pre;
	}


	public static Instances readDataFromARFF(String path) {
		BufferedReader reader = null;
		Instances structure = null;
		try {
			reader = new BufferedReader(
					new FileReader(path));
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
	public void work() {
		System.out.println("hiwwwwer");
		Instances structure = readDataFromARFF("./main/java/de/uhd/ifi/se/decision/management/jira/textclassifier/lucene_sentence_isRelevant.arff");

		try {
	    FilteredClassifier fc =null;
	    File f = new File("./main/java/de/uhd/ifi/se/decision/management/jira/textclassifier/fc.model");
	    //If a model exists, load it, do not build a new one.
	    if(f.isFile()) {

				fc = (FilteredClassifier) weka.core.SerializationHelper.read("./fc.model");


		} else {
			fc = new FilteredClassifier();
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
		    printStats(rate);

		    //write model to file system
		    weka.core.SerializationHelper.write("./fc.model", fc);
		    f = new File("./fc.model");
		}

		Instances pred =  readDataFromARFF("./classifier/3973.arff");
		System.out.println(pred);
	    double[] predictions = predictText(pred, fc);
	    Scanner sc = new Scanner(System.in);


	    for(int i = 0; i < pred.numInstances(); i++) {
		pred.get(i).setClassValue(predictions[i]);
	    }
	    System.out.println(pred);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


	}




}
*/