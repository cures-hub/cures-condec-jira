package de.uhd.ifi.se.decision.management.jira.classification.implementation;

import static java.lang.Math.round;
import static java.util.stream.Collectors.toList;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Timestamp;
import java.util.*;
import java.util.stream.IntStream;

import de.uhd.ifi.se.decision.management.jira.classification.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atlassian.gzipfilter.org.apache.commons.lang.ArrayUtils;

import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.model.text.PartOfJiraIssueText;
import de.uhd.ifi.se.decision.management.jira.persistence.KnowledgePersistenceManager;
import de.uhd.ifi.se.decision.management.jira.persistence.impl.JiraIssueTextPersistenceManager;
import smile.validation.Accuracy;
import smile.validation.ClassificationMeasure;
import smile.validation.FMeasure;
import smile.validation.Precision;
import smile.validation.Recall;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ConverterUtils;

/**
 * Class responsible to train the supervised text classifier. For this purpose,
 * the project admin needs to create and select an ARFF file.
 */
public class OnlineFileTrainerImpl implements EvaluableClassifier, OnlineTrainer, FileTrainer {

	private DecisionKnowledgeClassifier classifier;
	protected File directory;
	protected Instances instances;
	protected String projectKey;

	public static File DEFAULT_TRAINING_DATA = new File(DecisionKnowledgeClassifier.DEFAULT_DIR + "defaultTrainingData.arff");

	// private static OnlineClassificationTrainerImpl instance;

	protected static final Logger LOGGER = LoggerFactory.getLogger(OnlineFileTrainerImpl.class);

	public OnlineFileTrainerImpl() {
		this.classifier = DecisionKnowledgeClassifierImpl.getInstance();
		this.directory = new File(DecisionKnowledgeClassifier.DEFAULT_DIR);
		directory.mkdirs();
	}

	public OnlineFileTrainerImpl(String projectKey) {
		this();
		this.projectKey = projectKey;
	}

	public OnlineFileTrainerImpl(String projectKey, String fileName) {
		this(projectKey);
		if ((fileName == null || fileName.isEmpty())) {
			return;
		}
		this.instances = getInstancesFromArffFile(fileName);
	}

	public OnlineFileTrainerImpl(String projectKey, List<DecisionKnowledgeElement> trainingElements) {
		this(projectKey);
		this.setTrainingData(trainingElements);
	}

	@Override
	// is called after setting training-file
	public boolean train() {
		boolean isTrained = false;
		try {
				/*
		}
			Runnable trainBinary = () -> {
				try {
					trainBinaryClassifier();
				} catch (Exception e) {
					e.printStackTrace();
				}
			};
			new Thread(trainBinary).start();

			Runnable trainFineGrained = () -> {
				try {
					trainFineGrainedClassifier();
				} catch (Exception e) {
					e.printStackTrace();
				}
			};
			new Thread(trainFineGrained).start();
			*/
			Map<String, List> trainingData = this.extractTrainingData(this.getInstances());
			Map preprocessedSentences;
			// if (!this.classifier.getBinaryClassifier().loadFromFile()) {
			preprocessedSentences = this.classifier.preprocess(trainingData.get("sentences"),
					trainingData.get("labelsIsRelevant"));

			this.classifier.trainBinaryClassifier((List<List<Double>>) preprocessedSentences.get("features"),
					(List<Integer>) preprocessedSentences.get("labels"));
			this.classifier.getBinaryClassifier().saveToFile();

			// }

			// if (!this.classifier.getFineGrainedClassifier().loadFromFile()) {
			preprocessedSentences = this.classifier.preprocess(trainingData.get("relevantSentences"),
					trainingData.get("labelKnowledgeType"));

			this.classifier.trainFineGrainedClassifier((List<List<Double>>) preprocessedSentences.get("features"),
					(List<Integer>) preprocessedSentences.get("labels"));
			this.classifier.getFineGrainedClassifier().saveToFile();

			// }

			// this.evaluateTraining();

			isTrained = true;
		} catch (Exception e) {
			LOGGER.error("The classifier could not be trained. Message:" + e.getMessage());
		}
		return isTrained;
	}

/*
	private synchronized void trainBinaryClassifier() throws Exception {
		Map<String, List> trainingData = this.extractTrainingData(this.getInstances());
		Map preprocessedSentences;
		// if (!this.classifier.getBinaryClassifier().loadFromFile()) {
		preprocessedSentences = this.classifier.preprocess(trainingData.get("sentences"),
			trainingData.get("labelsIsRelevant"));

		this.classifier.trainBinaryClassifier((List<List<Double>>) preprocessedSentences.get("features"),
			(List<Integer>) preprocessedSentences.get("labels"));
		this.classifier.getBinaryClassifier().saveToFile();
	}

	private synchronized void trainFineGrainedClassifier() throws Exception {

		Map<String, List> trainingData = this.extractTrainingData(this.getInstances());
		Map preprocessedSentences;
		preprocessedSentences = this.classifier.preprocess(trainingData.get("relevantSentences"),
			trainingData.get("labelKnowledgeType"));

		this.classifier.trainFineGrainedClassifier((List<List<Double>>) preprocessedSentences.get("features"),
			(List<Integer>) preprocessedSentences.get("labels"));
		this.classifier.getFineGrainedClassifier().saveToFile();
	}
*/
	public boolean update(PartOfJiraIssueText sentence) {
		try {
			List<List<Double>> features = this.classifier.preprocess(sentence.getDescription());
			// classifier needs numerical value
			Integer labelIsRelevant = sentence.isRelevant() ? 1 : 0;

			for (List<Double> feature : features) {
				this.classifier.getBinaryClassifier().train(feature.toArray(Double[]::new), labelIsRelevant);
				if (sentence.isRelevant()) {
					this.classifier.getFineGrainedClassifier().train(feature.toArray(Double[]::new),
						sentence.getType());
				}
			}
		} catch (Exception e) {
			LOGGER.error("Could not update classifier. Message: " + e.getMessage());
		}
		return true;
	}

	@Override
	public DecisionKnowledgeClassifier getClassifier() {
		return classifier;
	}

	@Override
	public List<File> getTrainingFiles() {
		List<File> arffFilesOnServer = new ArrayList<File>();
		for (File file : directory.listFiles()) {
			if (file.getName().toLowerCase(Locale.ENGLISH).contains(".arff")) {
				arffFilesOnServer.add(file);
			}
		}
		return arffFilesOnServer;
	}

	@Override
	public List<String> getTrainingFileNames() {
		List<File> arffFilesOnServer = getTrainingFiles();
		List<String> arffFileNames = new ArrayList<String>();
		for (File file : arffFilesOnServer) {
			arffFileNames.add(file.getName());
		}
		return arffFileNames;
	}

	private Instances getInstancesFromArffFile(File arffFile) {
		if (!arffFile.exists()) {
			return null;
		}
		Instances instances = null;
		try {
			ConverterUtils.DataSource dataSource = new ConverterUtils.DataSource(arffFile.getPath());
			instances = dataSource.getDataSet();

			if (instances.classIndex() == -1) {
				// Reset index
				instances.setClassIndex(instances.numAttributes() - 1);
			}
		} catch (Exception e) {
			LOGGER.error("Problem to get the instances from ARFF file. Message:" + e.getMessage());
		}
		return instances;
	}

	public Instances getInstancesFromArffFile(String arffFileName) {
		File arffFile = new File(directory + File.separator + arffFileName);

		return getInstancesFromArffFile(arffFile);
	}

	public Instances getInstances() {
		if (instances == null) {
			this.instances = loadInstances();
		}
		return this.instances;
	}

	private Instances loadInstances() {
		Instances loadedInstances;

		List<File> trainingFiles = getTrainingFiles();
		loadedInstances = new Instances(getInstancesFromArffFile(trainingFiles.get(0)));
		for (File trainingFile : trainingFiles.subList(1, trainingFiles.size())) {
			loadedInstances.addAll(getInstancesFromArffFile(trainingFile));
		}

		return loadedInstances;
	}


	@Override
	public void setTrainingFile(File file) {
		this.instances = getInstancesFromArffFile(file);
	}

	private String getArffFileName() {
		Date date = new Date();
		Timestamp timestamp = new Timestamp(date.getTime());
		String prefix = "";
		if (projectKey != null) {
			prefix = projectKey;
		}
		return prefix + timestamp.getTime() + ".arff";
	}

	public Instances loadMekaTrainingDataFromJiraIssueText(boolean useOnlyValidatedData) {
		JiraIssueTextPersistenceManager manager = new JiraIssueTextPersistenceManager(projectKey);
		List<DecisionKnowledgeElement> partsOfText = manager.getUserValidatedPartsOfText(projectKey);
		if (!useOnlyValidatedData) {
			partsOfText.addAll(manager.getUnvalidatedPartsOfText(projectKey));
		}
		Instances instances = buildDatasetForMeka(partsOfText);
		return instances;
	}

	@Override
	public File saveTrainingFile(boolean useOnlyValidatedData) {
		File arffFile = null;
		try {
			arffFile = new File(directory + File.separator + getArffFileName());
			arffFile.createNewFile();
			String arffString = createArffString(useOnlyValidatedData);
			PrintWriter writer = new PrintWriter(arffFile, "UTF-8");
			writer.println(arffString);
			writer.close();
		} catch (IOException e) {
			LOGGER.error("The ARFF file could not be saved. Message: " + e.getMessage());
		}
		return arffFile;
	}

	private String createArffString(boolean useOnlyValidatedData) {
		if (instances == null) {
			instances = loadMekaTrainingDataFromJiraIssueText(useOnlyValidatedData);
		}
		return instances.toString();
	}

	@Override
	public void setTrainingData(List<DecisionKnowledgeElement> trainingElements) {
		this.instances = buildDatasetForMeka(trainingElements);
	}

	/**
	 * Creates the training instances for the supervised text classifier. The
	 * instance contains the knowledge type indicated by the value 1 (or 0 for type
	 * OTHER) and the summary of the element.
	 * <p>
	 * Data appearance:
	 *
	 * @param trainingElements list of validated decision knowledge elements
	 * @return training dataset for the supervised text classifier. The instances
	 * that this method returns is the ARFF file that is needed to train the
	 * classifier.
	 * @relation 'sentences: -C 5'
	 * @attribute isAlternative {0,1}
	 * @attribute isPro {0,1}
	 * @attribute isCon {0,1}
	 * @attribute isDecision {0,1}
	 * @attribute isIssue {0,1}
	 * @attribute sentence string
	 * @data 0, 0, 0, 1, 0 'I am a test sentence that is a decision.' 1,0,0,0,0 'I am an
	 * alternative for the issue.' 0,0,0,0,1 'And I am the issue for the
	 * decision and the alternative.'
	 */
	public Instances buildDatasetForMeka(List<DecisionKnowledgeElement> trainingElements) {
		ArrayList<Attribute> wekaAttributes = new ArrayList<Attribute>();

		// Declare Class value with {0,1} as possible values
		wekaAttributes.add(getAttribute("isAlternative"));
		wekaAttributes.add(getAttribute("isPro"));
		wekaAttributes.add(getAttribute("isCon"));
		wekaAttributes.add(getAttribute("isDecision"));
		wekaAttributes.add(getAttribute("isIssue"));

		// Declare text attribute to hold the message (free form text)
		Attribute attribute = new Attribute("sentence", (List<String>) null);

		// Declare the feature vector
		wekaAttributes.add(attribute);

		Instances instances = new Instances("sentences -C 5 ", wekaAttributes, 1000000);

		for (DecisionKnowledgeElement trainingElement : trainingElements) {
			instances.add(createTrainingInstance(trainingElement, attribute));
		}
		instances.setClassIndex(instances.numAttributes() - 1);
		return instances;
	}

	/**
	 * Creates a Attribute which defines the binary Value
	 *
	 * @param name
	 * @return Attribute
	 */
	private static Attribute getAttribute(String name) {
		ArrayList<String> rationaleAttribute = new ArrayList<String>();
		rationaleAttribute.add("0");
		rationaleAttribute.add("1");
		return new Attribute(name, rationaleAttribute);
	}

	/**
	 * Creates a training instance for the supervised text classifier. The instance
	 * contains the knowledge type indicated by the value 1 (or 0 for type OTHER)
	 * and the summary of the element.
	 *
	 * @param element   validated decision knowledge element.
	 * @param attribute text attribute.
	 * @return training instance for the supervised text classifier.
	 */
	private DenseInstance createTrainingInstance(DecisionKnowledgeElement element, Attribute attribute) {
		DenseInstance instance = initInstance();
		switch (element.getType()) {
			case ALTERNATIVE:
				instance.setValue(0, 1);
				break;
			case PRO:
				instance.setValue(1, 1);
				break;
			case CON:
				instance.setValue(2, 1);
				break;
			case DECISION:
				instance.setValue(3, 1);
				break;
			case ISSUE:
				instance.setValue(4, 1);
				break;
			default:
				break;
		}
		instance.setValue(attribute, element.getSummary());
		return instance;
	}

	private DenseInstance initInstance() {
		DenseInstance instance = new DenseInstance(6);
		instance.setValue(0, 0);
		instance.setValue(1, 0);
		instance.setValue(2, 0);
		instance.setValue(3, 0);
		instance.setValue(4, 0);
		return instance;
	}



	private Map<String, List> extractTrainingData(Instances trainingData) {
		Map extractedTrainingData = new HashMap();
		List sentences = new ArrayList();
		List relevantSentences = new ArrayList();
		List labelsIsRelevant = new ArrayList();
		List labelsKnowledgeType = new ArrayList();
		// TODO: can we use the names instead of indices?
		// iterate over all instances

		for (int i = 0; i < trainingData.size(); i++) {
			Instance currInstance = trainingData.get(i);
			// last attribute is the sentence that needs to be classified
			sentences.add(currInstance.stringValue(currInstance.numAttributes() - 1));

			Integer isRelevant = 0;
			Integer fineGrainedLabel = -1;
			// iterate over the binary attributes for each possible class
			for (int j = 0; j < currInstance.numAttributes() - 1; j++) {
				if (round(currInstance.value(j)) == 1) {
					isRelevant = 1;
					labelsKnowledgeType.add(j);
					relevantSentences.add(currInstance.stringValue(currInstance.numAttributes() - 1));

				}
			}
			labelsIsRelevant.add(isRelevant);
		}

		extractedTrainingData.put("sentences", sentences);
		extractedTrainingData.put("relevantSentences", relevantSentences);

		extractedTrainingData.put("labelsIsRelevant", labelsIsRelevant);
		extractedTrainingData.put("labelKnowledgeType", labelsKnowledgeType);

		return extractedTrainingData;
	}

	private List<String> extractStringsFromDke(List<DecisionKnowledgeElement> sentences) {
		List<String> extractedStringsFromPoji = new ArrayList<String>();
		for (DecisionKnowledgeElement sentence : sentences) {
			extractedStringsFromPoji.add(sentence.getDescription());
		}
		return extractedStringsFromPoji;
	}

	public Map<String, Double> evaluateClassifier() throws Exception {
		// create and initialize default measurements list
		List<ClassificationMeasure> defaultMeasurements = new ArrayList<>();
		defaultMeasurements.add(new FMeasure());
		// TODO how to apply to more than binary classification
		defaultMeasurements.add(new Precision());
		defaultMeasurements.add(new Accuracy());
		defaultMeasurements.add(new Recall());

		// load validated Jira Issue texts
		JiraIssueTextPersistenceManager manager = KnowledgePersistenceManager.getOrCreate(projectKey)
			.getJiraIssueTextManager();
		List<DecisionKnowledgeElement> partsOfText = manager.getUserValidatedPartsOfText(projectKey);
		return evaluateClassifier(defaultMeasurements, partsOfText);
	}

	public Map<String, Double> evaluateClassifier(List<ClassificationMeasure> measurements,
												  List<DecisionKnowledgeElement> partOfJiraIssueTexts) throws Exception {
		Map<String, Double> resultsMap = new HashMap<>();
		List<DecisionKnowledgeElement> relevantPartOfJiraIssueTexts = partOfJiraIssueTexts.stream()
			.filter(x -> !x.getType().equals(KnowledgeType.OTHER)).collect(toList());

		// format data
		List<String> sentences = this.extractStringsFromDke(partOfJiraIssueTexts);
		List<String> relevantSentences = this.extractStringsFromDke(relevantPartOfJiraIssueTexts);
		// extract true values
		Integer[] binaryTruths = partOfJiraIssueTexts.stream()
			// when type equals other then it is irrelevant
			.map(x -> x.getType().equals(KnowledgeType.OTHER) ? 0 : 1).collect(toList())
			.toArray(new Integer[partOfJiraIssueTexts.size()]);

		Integer[] fineGrainedTruths = relevantPartOfJiraIssueTexts.stream()
			.map(x -> classifier.getFineGrainedClassifier().mapKnowledgeTypeToIndex(x.getType())).collect(toList())
			.toArray(new Integer[relevantPartOfJiraIssueTexts.size()]);

		// predict classes
		Integer[] binaryPredictions = this.classifier.makeBinaryPredictions(sentences).stream().map(x -> x ? 1 : 0)
			.collect(toList()).toArray(new Integer[sentences.size()]);

		Integer[] fineGrainedPredictions = this.classifier.makeFineGrainedPredictions(relevantSentences).stream()
			.map(x -> this.classifier.getFineGrainedClassifier().mapKnowledgeTypeToIndex(x)).collect(toList())
			.toArray(new Integer[relevantSentences.size()]);

		// calculate measurements for each ClassificationMeasure in measurements
		for (ClassificationMeasure measurement : measurements) {
			String binaryKey = measurement.getClass().getSimpleName() + "_binary";
			Double binaryMeasurement = measurement.measure(ArrayUtils.toPrimitive(binaryTruths),
				ArrayUtils.toPrimitive(binaryPredictions));
			resultsMap.put(binaryKey, binaryMeasurement);

			for (int classLabel : IntStream.range(0, this.classifier.getFineGrainedClassifier().getNumClasses())
				.toArray()) {
				String fineGrainedKey = measurement.getClass().getSimpleName() + "_fineGrained_"
					+ this.classifier.getFineGrainedClassifier().mapIndexToKnowledgeType(classLabel);

				Integer[] currentFineGrainedTruths = mapFineGrainedToBinaryResults(fineGrainedTruths, classLabel);
				Integer[] currentFineGrainedPredictions = mapFineGrainedToBinaryResults(fineGrainedPredictions,
					classLabel);

				Double fineGrainedMeasurement = measurement.measure(ArrayUtils.toPrimitive(currentFineGrainedTruths),
					ArrayUtils.toPrimitive(currentFineGrainedPredictions));
				resultsMap.put(fineGrainedKey, fineGrainedMeasurement);
			}

		}
		// return results
		return resultsMap;
	}

	private Integer[] mapFineGrainedToBinaryResults(Integer[] array, Integer currentElement) {
		return Arrays.stream(array).map(x -> x.equals(currentElement) ? 1 : 0).toArray(Integer[]::new);

	}

}
