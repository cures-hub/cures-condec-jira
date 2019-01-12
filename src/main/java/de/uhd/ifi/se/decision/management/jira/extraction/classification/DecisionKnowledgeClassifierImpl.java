package de.uhd.ifi.se.decision.management.jira.extraction.classification;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import de.uhd.ifi.se.decision.management.jira.ComponentGetter;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
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
 * Class to identify decision knowledge in natural language texts using a binary
 * and fine grained supervised classifiers.
 */
public class DecisionKnowledgeClassifierImpl implements DecisionKnowledgeClassifier {

	private FilteredClassifier binaryClassifier;
	private LC fineGrainedClassifier;

	/**
	 * The knowledge types need to be present in the weka classifier. They do not
	 * relate to tags like {Issue}.
	 */
	private static final String[] KNOWLEDGE_TYPES = { "isAlternative", "isPro", "isCon", "isDecision", "isIssue" };

	public DecisionKnowledgeClassifierImpl() {
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

	@Override
	public List<Boolean> makeBinaryPredictions(List<String> stringsToBeClassified) {
		Instances datasetForBinaryClassification = createDatasetForBinaryClassification(stringsToBeClassified);
		return makeBinaryPredictions(datasetForBinaryClassification);
	}

	@Override
	public List<KnowledgeType> makeFineGrainedPredictions(List<String> stringsToBeClassified) {
		Instances data = createDatasetForFineGrainedClassification(stringsToBeClassified);
		return makeFineGrainedPredictions(data);
	}

	@Override
	public void setFineGrainedClassifier(LC fineGrainedClassifier) {
		this.fineGrainedClassifier = fineGrainedClassifier;
	}

	@Override
	public void setBinaryClassifier(FilteredClassifier binaryClassifier) {
		this.binaryClassifier = binaryClassifier;
	}

	private Instances createDatasetForBinaryClassification(List<String> stringsToBeClassified) {
		List<Attribute> wekaAttributes = createBinaryAttributes();
		Instances datasetForBinaryClassification = new Instances("sentences", (ArrayList<Attribute>) wekaAttributes,
				1000000);

		datasetForBinaryClassification.setClassIndex(datasetForBinaryClassification.numAttributes() - 1);
		for (String string : stringsToBeClassified) {
			DenseInstance instance = new DenseInstance(2);
			instance.setValue(wekaAttributes.get(0), string);
			datasetForBinaryClassification.add(instance);
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

	private List<Boolean> makeBinaryPredictions(Instances instances) {
		List<Boolean> binaryPredictionResults = new ArrayList<Boolean>();

		try {
			for (Instance instance : instances) {
				instance.setClassMissing();
				double predictionResult = binaryClassifier.classifyInstance(instance);
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
	 * @return true if text is relevant decision knowledge.
	 */
	public static boolean isRelevant(double predictionResult) {
		return predictionResult == 1.;
	}

	public List<KnowledgeType> makeFineGrainedPredictions(Instances instances) {
		List<KnowledgeType> fineGrainedPredictionResults = new ArrayList<KnowledgeType>();
		instances.setClassIndex(5);

		// Create and use filter
		Filter sringToWordVector;
		try {
			instances.setClassIndex(5);
			sringToWordVector = getStringToWordVector();
			sringToWordVector.setInputFormat(instances);
			Instances filteredInstances = Filter.useFilter(instances, sringToWordVector);
			filteredInstances.setClassIndex(5);

			// Classify string instances
			for (Instance instance : filteredInstances) {
				double[] predictionResult = fineGrainedClassifier.distributionForInstance(instance);
				fineGrainedPredictionResults.add(getType(predictionResult));
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println(e.getStackTrace() + "Fine grained classification failed.");
			return null;
		}

		return fineGrainedPredictionResults;
	}

	private Instances createDatasetForFineGrainedClassification(List<String> stringsToBeClassified) {
		List<Attribute> wekaAttributes = new ArrayList<Attribute>();

		// Declare Class value with {0,1} as possible values
		for (int i = 0; i < KNOWLEDGE_TYPES.length; i++) {
			wekaAttributes.add(new Attribute(KNOWLEDGE_TYPES[i], createClassAttributeList(), i));
		}

		// Declare text attribute to hold the message (free form text)
		Attribute attributeText = new Attribute("sentence", (List<String>) null, 5);

		// Declare the feature vector
		wekaAttributes.add(attributeText);
		Instances data = new Instances("sentences: -C 5 ", (ArrayList<Attribute>) wekaAttributes, 1000000);

		for (String string : stringsToBeClassified) {
			Instance instance = new DenseInstance(6);
			instance.setValue(attributeText, string);
			data.add(instance);
		}
		return data;
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

	/**
	 * Get the knowledge type of the text if it is relevant decision knowledge. Uses
	 * an array of estimated values for relevance. For example: double[]
	 * classification = { 1.0, 0.0, 0.0, 0.0, 0.0 } for alternative. The order is
	 * important: alternative, decision, issue, pro, and con.
	 * 
	 * @see KnowledgeType
	 * @param prediction
	 *            1.0 if the text is decision knowledge with a certain type. Values
	 *            less than 1 represent irrelevant text.
	 * @return knowledge type of the text.
	 */
	public static KnowledgeType getType(double[] prediction) {
		if (prediction[0] == 1.) {
			return KnowledgeType.ALTERNATIVE;
		} else if (prediction[3] == 1.) {
			return KnowledgeType.DECISION;
		} else if (prediction[4] == 1.) {
			return KnowledgeType.ISSUE;
		} else if (prediction[1] == 1.) {
			return KnowledgeType.PRO;
		} else if (prediction[2] == 1.) {
			return KnowledgeType.CON;
		}
		return KnowledgeType.OTHER;
	}
}
