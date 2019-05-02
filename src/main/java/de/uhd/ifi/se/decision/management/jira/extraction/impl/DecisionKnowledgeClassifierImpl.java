package de.uhd.ifi.se.decision.management.jira.extraction.impl;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

import de.uhd.ifi.se.decision.management.jira.extraction.ClassificationTrainer;
import de.uhd.ifi.se.decision.management.jira.extraction.DecisionKnowledgeClassifier;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import meka.classifiers.multilabel.LC;
import weka.classifiers.meta.FilteredClassifier;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.SerializationHelper;
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

	public DecisionKnowledgeClassifierImpl(FilteredClassifier binaryClassifier, LC fineGrainedClassifier) {
		this.binaryClassifier = binaryClassifier;
		this.fineGrainedClassifier = fineGrainedClassifier;
	}

	public DecisionKnowledgeClassifierImpl() {
		this.binaryClassifier = loadDefaultBinaryClassifier();
		this.fineGrainedClassifier = loadDefaultFineGrainedClassifier();
	}

	private FilteredClassifier loadDefaultBinaryClassifier() {
		File binaryClassifierFile = new File(DecisionKnowledgeClassifier.DEFAULT_DIR + "binaryClassifier.model");
		if (!binaryClassifierFile.exists()) {
			ClassificationTrainer.trainDefaultClassifier();
		}
		FilteredClassifier binaryClassifier;
		try {
			binaryClassifier = (FilteredClassifier) SerializationHelper.read(new FileInputStream(binaryClassifierFile));
		} catch (Exception e) {
			binaryClassifier = new FilteredClassifier();
		}
		return binaryClassifier;
	}

	private LC loadDefaultFineGrainedClassifier() {
		File fineGrainedClassifierFile = new File(
				DecisionKnowledgeClassifier.DEFAULT_DIR + "fineGrainedClassifier.model");
		if (!fineGrainedClassifierFile.exists()) {
			ClassificationTrainer.trainDefaultClassifier();
		}
		LC fineGrainedClassifier;
		try {
			fineGrainedClassifier = (LC) SerializationHelper.read(new FileInputStream(fineGrainedClassifierFile));
		} catch (Exception e) {
			fineGrainedClassifier = new LC();
		}
		return fineGrainedClassifier;
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
		StringToWordVector sringToWordVector;
		try {
			instances.setClassIndex(5);
			sringToWordVector = DecisionKnowledgeClassifier.getStringToWordVector();
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
