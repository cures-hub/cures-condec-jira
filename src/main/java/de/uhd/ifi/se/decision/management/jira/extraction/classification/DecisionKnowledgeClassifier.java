package de.uhd.ifi.se.decision.management.jira.extraction.classification;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import de.uhd.ifi.se.decision.management.jira.ComponentGetter;
import meka.classifiers.multilabel.LC;
import weka.classifiers.meta.FilteredClassifier;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.tokenizers.NGramTokenizer;
import weka.core.tokenizers.Tokenizer;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.StringToWordVector;

public class DecisionKnowledgeClassifier {

	private FilteredClassifier binaryClassifier;

	private LC fineGrainedClassifier;

	public DecisionKnowledgeClassifier() {
		String pathFineGrained = ComponentGetter.getUrlOfClassifierFolder() + "br.model";
		String path = ComponentGetter.getUrlOfClassifierFolder() + "fc.model";
		InputStream is;
		try {
			is = new URL(path).openStream();
			binaryClassifier = (FilteredClassifier) weka.core.SerializationHelper.read(is);
		} catch (Exception e) {
			binaryClassifier = new FilteredClassifier();
		}
		try {
			is = new URL(pathFineGrained).openStream();
			fineGrainedClassifier = (LC) weka.core.SerializationHelper.read(is);
		} catch (Exception e) {
			fineGrainedClassifier = new LC();
		}

	}

	public List<Double> makeBinaryPredictions(Instances data) {
		List<Double> areRelevant = new ArrayList<Double>();

		try {
			for (int i = 0; i < data.numInstances(); i++) {
				data.get(i).setClassMissing();
				Double n = binaryClassifier.classifyInstance(data.get(i));
				areRelevant.add(n);
			}
		} catch (Exception e) {
			System.err.println("Binary Classification failed");
			return new ArrayList<Double>();
		}

		return areRelevant;
	}

	public List<double[]> classifySentencesFineGrained(Instances structure) {
		List<double[]> results = new ArrayList<double[]>();
		structure.setClassIndex(5);

		// Create and use Filter
		Filter stwv;
		try {
			structure.setClassIndex(5);
			stwv = getSTWV(structure);
			stwv.setInputFormat(structure);
			structure = Filter.useFilter(structure, stwv);
			structure.setClassIndex(5);

			// Classify string instances
			for (int n = 0; n < structure.size(); n++) {
				Instance predictionInstance = structure.get(n);
				double[] predictionResult = fineGrainedClassifier.distributionForInstance(predictionInstance);
				results.add(predictionResult);
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println(e.getStackTrace() + "Fine grained Classification failed");
			return null;
		}

		return results;

	}

	private StringToWordVector getSTWV(Instances structure) throws Exception {
		StringToWordVector stwv = new StringToWordVector();
		stwv.setLowerCaseTokens(true);
		stwv.setIDFTransform(true);
		stwv.setTFTransform(true);
		stwv.setTokenizer(getTokenizer());
		stwv.setWordsToKeep(1000000);
		return stwv;
	}

	private Tokenizer getTokenizer() throws Exception {
		Tokenizer t = new NGramTokenizer();
		String[] options = weka.core.Utils.splitOptions(
				"weka.core.tokenizers.NGramTokenizer -max 3 -min 1 -delimiters \" \\r\\n\\t.,;:\\'\\\"()?!\"");
		t.setOptions(options);
		return t;
	}

	public void setFineGrainedClassifier(LC fineGrainedClassifier) {
		this.fineGrainedClassifier = fineGrainedClassifier;
	}

	public void setBinaryClassifier(FilteredClassifier binaryClassifier) {
		this.binaryClassifier = binaryClassifier;
	}
}
