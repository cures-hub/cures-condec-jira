package de.uhd.ifi.se.decision.management.jira.extraction.classification;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import de.uhd.ifi.se.decision.management.jira.ComponentGetter;
import meka.classifiers.multilabel.LC;
import weka.classifiers.meta.FilteredClassifier;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.tokenizers.NGramTokenizer;
import weka.core.tokenizers.Tokenizer;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.StringToWordVector;

/**
 * Class to initialize the binary and fine grained supervised classifiers to
 * identify decision knowledge in natural language texts.
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

	public List<Boolean> makeBinaryPredictions(List<String> stringsToBeClassified) {
		Instances datasetForBinaryClassification = createDatasetForBinaryClassification(stringsToBeClassified);
		return makeBinaryPredictions(datasetForBinaryClassification);
	}

	private Instances createDatasetForBinaryClassification(List<String> stringsToBeClassified) {
		List<Attribute> wekaAttributes = createBinaryAttributes();
		Instances datasetForBinaryClassification = new Instances("sentences", (ArrayList<Attribute>) wekaAttributes,
				1000000);

		datasetForBinaryClassification.setClassIndex(datasetForBinaryClassification.numAttributes() - 1);
		for (String string : stringsToBeClassified) {
			DenseInstance newInstance = new DenseInstance(2);
			newInstance.setValue(wekaAttributes.get(0), string);
		}
		return datasetForBinaryClassification;
	}

	private List<Attribute> createBinaryAttributes() {
		List<Attribute> wekaAttributes = new ArrayList<Attribute>();
		wekaAttributes.add(new Attribute("sentence", (List<String>) null));
		wekaAttributes.add(new Attribute("isRelevant", createClassAttributeList()));
		return wekaAttributes;
	}

	private List<String> createClassAttributeList() {
		// Declare Class value with {0,1} as possible values
		List<String> relevantAttribute = new ArrayList<String>();
		relevantAttribute.add("0");
		relevantAttribute.add("1");
		return relevantAttribute;
	}

	public List<Boolean> makeBinaryPredictions(Instances data) {
		List<Boolean> binaryPredictionResults = new ArrayList<Boolean>();

		try {
			for (int i = 0; i < data.numInstances(); i++) {
				data.get(i).setClassMissing();
				double predictionResult = binaryClassifier.classifyInstance(data.get(i));
				binaryPredictionResults.add(isRelevant(predictionResult));
			}
		} catch (Exception e) {
			System.err.println("Binary classification failed.");
			return new ArrayList<Boolean>();
		}

		return binaryPredictionResults;
	}

	/**
	 * Determine whether the prediction result indicates that the text is decision
	 * knowledge, i.e., relevant.
	 * 
	 * @param predictionResult
	 *            1.0 if the text is decision knowledge. Values less than 1
	 *            represent irrelevant text.
	 */
	private boolean isRelevant(double predictionResult) {
		if (predictionResult == 1.) {
			return true;
		}
		return false;
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
