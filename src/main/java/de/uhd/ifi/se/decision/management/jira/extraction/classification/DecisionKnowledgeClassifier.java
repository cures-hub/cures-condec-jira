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

/**
 * Class to initialize the binary and fine grained supervised classifiers to identify
 * decision knowledge in natural language texts.
 */
public class DecisionKnowledgeClassifier {

	private FilteredClassifier binaryClassifier;
	private LC fineGrainedClassifier;

	public DecisionKnowledgeClassifier() {
		String pathToBinaryModel = ComponentGetter.getUrlOfClassifierFolder() + "fc.model";
		String pathToFineGrainedModel = ComponentGetter.getUrlOfClassifierFolder() + "br.model";
		InputStream inputStream;
		try {
			inputStream = new URL(pathToBinaryModel).openStream();
			binaryClassifier = (FilteredClassifier) weka.core.SerializationHelper.read(inputStream);
			inputStream.close();
		} catch (Exception e) {
			binaryClassifier = new FilteredClassifier();
		}
		try {
			inputStream = new URL(pathToFineGrainedModel).openStream();
			fineGrainedClassifier = (LC) weka.core.SerializationHelper.read(inputStream);
			inputStream.close();
		} catch (Exception e) {
			fineGrainedClassifier = new LC();
		}
	}

	public List<Double> makeBinaryPredictions(Instances data) {
		List<Double> binaryPredictionResults = new ArrayList<Double>();

		try {
			for (int i = 0; i < data.numInstances(); i++) {
				data.get(i).setClassMissing();
				Double predictionResult = binaryClassifier.classifyInstance(data.get(i));
				binaryPredictionResults.add(predictionResult);
			}
		} catch (Exception e) {
			System.err.println("Binary classification failed.");
			return new ArrayList<Double>();
		}

		return binaryPredictionResults;
	}

	public List<double[]> makeFineGrainedPredictions(Instances data) {
		List<double[]> fineGrainedPredictionResults = new ArrayList<double[]>();
		data.setClassIndex(5);

		// Create and use filter
		Filter sringToWordVector;
		try {
			data.setClassIndex(5);
			sringToWordVector = getStringToWordVector();
			sringToWordVector.setInputFormat(data);
			data = Filter.useFilter(data, sringToWordVector);
			data.setClassIndex(5);

			// Classify string instances
			for (int n = 0; n < data.size(); n++) {
				Instance predictionInstance = data.get(n);
				double[] predictionResult = fineGrainedClassifier.distributionForInstance(predictionInstance);
				fineGrainedPredictionResults.add(predictionResult);
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println(e.getStackTrace() + "Fine grained classification failed.");
			return null;
		}

		return fineGrainedPredictionResults;

	}

	private StringToWordVector getStringToWordVector() throws Exception {
		StringToWordVector stringToWordVector = new StringToWordVector();
		stringToWordVector.setLowerCaseTokens(true);
		stringToWordVector.setIDFTransform(true);
		stringToWordVector.setTFTransform(true);
		stringToWordVector.setTokenizer(getTokenizer());
		stringToWordVector.setWordsToKeep(1000000);
		return stringToWordVector;
	}

	private Tokenizer getTokenizer() throws Exception {
		Tokenizer tokenizer = new NGramTokenizer();
		String[] options = weka.core.Utils.splitOptions(
				"weka.core.tokenizers.NGramTokenizer -max 3 -min 1 -delimiters \" \\r\\n\\t.,;:\\'\\\"()?!\"");
		tokenizer.setOptions(options);
		return tokenizer;
	}

	public void setFineGrainedClassifier(LC fineGrainedClassifier) {
		this.fineGrainedClassifier = fineGrainedClassifier;
	}

	public void setBinaryClassifier(FilteredClassifier binaryClassifier) {
		this.binaryClassifier = binaryClassifier;
	}
}
