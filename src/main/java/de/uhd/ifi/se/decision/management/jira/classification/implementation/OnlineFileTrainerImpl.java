package de.uhd.ifi.se.decision.management.jira.classification.implementation;

import static java.util.stream.Collectors.toList;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.stream.IntStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atlassian.gzipfilter.org.apache.commons.lang.ArrayUtils;

import de.uhd.ifi.se.decision.management.jira.classification.DecisionKnowledgeClassifier;
import de.uhd.ifi.se.decision.management.jira.classification.EvaluableClassifier;
import de.uhd.ifi.se.decision.management.jira.classification.FileTrainer;
import de.uhd.ifi.se.decision.management.jira.classification.OnlineTrainer;
import de.uhd.ifi.se.decision.management.jira.classification.TrainingData;
import de.uhd.ifi.se.decision.management.jira.classification.preprocessing.PreprocessedData;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.model.PartOfJiraIssueText;
import de.uhd.ifi.se.decision.management.jira.persistence.KnowledgePersistenceManager;
import de.uhd.ifi.se.decision.management.jira.persistence.singlelocations.JiraIssueTextPersistenceManager;
import smile.data.DataFrame;
import smile.data.Tuple;
import smile.data.type.DataType;
import smile.data.type.StructField;
import smile.data.type.StructType;
import smile.io.Arff;
import smile.io.Read;
import smile.validation.metric.ClassificationMetric;
import smile.validation.metric.FScore;

/**
 * Class responsible to train the supervised text classifier. For this purpose,
 * the project admin needs to create and select an ARFF file.
 */
public class OnlineFileTrainerImpl implements EvaluableClassifier, OnlineTrainer, FileTrainer {
	protected static final Logger LOGGER = LoggerFactory.getLogger(OnlineFileTrainerImpl.class);

	private DecisionKnowledgeClassifier classifier;
	protected File directory;
	protected DataFrame instances;
	protected String projectKey;

	public OnlineFileTrainerImpl() {
		this.classifier = DecisionKnowledgeClassifier.getInstance();
		this.directory = new File(DecisionKnowledgeClassifier.DEFAULT_DIR);
		directory.mkdirs();
	}

	public OnlineFileTrainerImpl(String projectKey) {
		this();
		this.projectKey = projectKey;
	}

	public OnlineFileTrainerImpl(String projectKey, String fileName) {
		this(projectKey);
		if (fileName == null || fileName.isEmpty()) {
			return;
		}
		this.instances = getInstancesFromArffFile(fileName);
	}

	public OnlineFileTrainerImpl(String projectKey, List<KnowledgeElement> trainingElements) {
		this(projectKey);
		this.setTrainingData(trainingElements);
	}

	@Override
	// is called after setting training-file
	public boolean train() {
		boolean isTrained = true;
		try {
			trainBinaryClassifier();
			trainFineGrainedClassifier();
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error("The classifier could not be trained. Message:" + e.getMessage());
			isTrained = false;
		}
		return isTrained;
	}

	private void trainBinaryClassifier() {
		LOGGER.debug("Binary classifier training started.");
		TrainingData trainingData = new TrainingData(getDataFrame());
		PreprocessedData preprocessedSentences = new PreprocessedData(trainingData, false);
		classifier.trainBinaryClassifier(preprocessedSentences);
	}

	private void trainFineGrainedClassifier() {
		LOGGER.debug("Fine-grained classifier training started.");
		TrainingData trainingData = new TrainingData(getDataFrame());
		PreprocessedData preprocessedSentences = new PreprocessedData(trainingData, true);
		classifier.trainFineGrainedClassifier(preprocessedSentences);
		// this.classifier.getFineGrainedClassifier().saveToFile();
	}

	public boolean update(PartOfJiraIssueText sentence) {
		try {
			double[][] features = classifier.preprocess(sentence.getSummary());
			// classifier needs numerical value
			Integer labelIsRelevant = sentence.isRelevant() ? 1 : 0;

			for (double[] feature : features) {
				classifier.getBinaryClassifier().train(feature, labelIsRelevant);
				if (sentence.isRelevant()) {
					classifier.getFineGrainedClassifier().train(feature,
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
		for (File file : Objects.requireNonNull(directory.listFiles())) {
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

	public DataFrame getInstancesFromArffFile(String arffFileName) {
		File arffFile = new File(directory + File.separator + arffFileName);
		DataFrame trainingData = getDataFrameFromArffFile(arffFile);
		return trainingData;
	}

	public static DataFrame getDataFrameFromArffFile(File arffFile) {
		DataFrame trainingData = null;
		try {
			trainingData = Read.arff(arffFile.getAbsolutePath());
		} catch (IOException | ParseException | URISyntaxException e) {
			LOGGER.error("Data frame could not be loaded from ARFF file.");
		}
		return trainingData;
	}

	public DataFrame getDataFrame() {
		if (instances == null) {
			this.instances = loadInstances();
		}
		return this.instances;
	}

	private DataFrame loadInstances() {

		List<File> trainingFiles = getTrainingFiles();
		DataFrame loadedInstances = getDataFrameFromArffFile(trainingFiles.get(0));
		for (File trainingFile : trainingFiles.subList(1, trainingFiles.size())) {
			loadedInstances.merge(getDataFrameFromArffFile(trainingFile));
		}

		return loadedInstances;
	}

	@Override
	public void setTrainingFile(File file) {
		this.instances = getDataFrameFromArffFile(file);
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

	public DataFrame loadTrainingDataFromJiraIssueText(boolean useOnlyValidatedData) {
		JiraIssueTextPersistenceManager manager = new JiraIssueTextPersistenceManager(projectKey);
		List<KnowledgeElement> partsOfText = manager.getUserValidatedPartsOfText(projectKey);
		if (!useOnlyValidatedData) {
			partsOfText.addAll(manager.getUnvalidatedPartsOfText(projectKey));
		}
		DataFrame instances = buildDataFrame(partsOfText);
		return instances;
	}

	@Override
	public File saveTrainingFile(boolean useOnlyValidatedData) {
		File arffFile = null;
		try {
			arffFile = new File(directory + File.separator + getArffFileName());
			arffFile.createNewFile();
			if (instances == null) {
				instances = loadTrainingDataFromJiraIssueText(useOnlyValidatedData);
			}
			Arff.write(instances, arffFile.toPath(), "RelationName");
		} catch (IOException e) {
			LOGGER.error("The ARFF file could not be saved. Message: " + e.getMessage());
		}
		return arffFile;
	}

	@Override
	public void setTrainingData(List<KnowledgeElement> trainingElements) {
		this.instances = buildDataFrame(trainingElements);
	}

	/**
	 * Creates the training instances for the supervised text classifier. The
	 * instance contains the knowledge type indicated by the value 1 (or 0 for type
	 * OTHER) and the summary of the element.
	 * <p>
	 * Data appearance:
	 *
	 * @param trainingElements
	 *            list of validated decision knowledge elements
	 * @return training dataset for the supervised text classifier. The instances
	 *         that this method returns is the ARFF file that is needed to train the
	 *         classifier.
	 * @relation 'sentences: -C 5'
	 * @attribute isAlternative {0,1}
	 * @attribute isPro {0,1}
	 * @attribute isCon {0,1}
	 * @attribute isDecision {0,1}
	 * @attribute isIssue {0,1}
	 * @attribute sentence string
	 * @data 0, 0, 0, 1, 0 'I am a test sentence that is a decision.' 1,0,0,0,0 'I
	 *       am an alternative for the issue.' 0,0,0,0,1 'And I am the issue for the
	 *       decision and the alternative.'
	 */
	private DataFrame buildDataFrame(List<KnowledgeElement> trainingElements) {
		List<Tuple> rows = new ArrayList<>();
		StructField column1 = new StructField("isAlternative", DataType.of(Byte.class));
		StructField column2 = new StructField("isPro", DataType.of(Byte.class));
		StructField column3 = new StructField("isCon", DataType.of(Byte.class));
		StructField column4 = new StructField("isDecisions", DataType.of(Byte.class));
		StructField column5 = new StructField("isIssue", DataType.of(Byte.class));
		StructField column6 = new StructField("isSentence", DataType.of(String.class));
		StructType structType = new StructType(column1, column2, column3, column4, column5, column6);

		for (KnowledgeElement trainingElement : trainingElements) {
			rows.add(Tuple.of(createTrainingRow(trainingElement), structType));
		}
		return DataFrame.of(rows, structType);
	}

	/**
	 * Creates a training data frame for the supervised text classifier. The data
	 * frame contains the knowledge type indicated by the value 1 (or 0 for type
	 * OTHER) and the summary of the element.
	 *
	 * @param element
	 *            validated decision knowledge element.
	 * @return training row for the supervised text classifier.
	 */
	private Object[] createTrainingRow(KnowledgeElement element) {
		Object[] rowValues = new Object[6];
		switch (element.getType()) {
		case ALTERNATIVE:
			rowValues[0] = 1;
			break;
		case PRO:
			rowValues[1] = 1;
			break;
		case CON:
			rowValues[2] = 1;
			break;
		case DECISION:
			rowValues[3] = 1;
			break;
		case ISSUE:
			rowValues[4] = 1;
			break;
		default:
			break;
		}
		rowValues[5] = element.getSummary();
		return rowValues;
	}

	private List<String> extractStringsFromDke(List<KnowledgeElement> sentences) {
		List<String> extractedStringsFromPoji = new ArrayList<String>();
		for (KnowledgeElement sentence : sentences) {
			extractedStringsFromPoji.add(sentence.getSummary());
		}
		return extractedStringsFromPoji;
	}

	@Override
	public Map<String, Double> evaluateClassifier() throws Exception {
		// create and initialize default measurements list
		List<ClassificationMetric> defaultMeasurements = new ArrayList<>();
		defaultMeasurements.add(new FScore());
		// TODO how to apply to more than binary classification
		// defaultMeasurements.add(new Precision());
		// defaultMeasurements.add(new Accuracy());
		// defaultMeasurements.add(new Recall());

		// load validated Jira Issue texts
		// JiraIssueTextPersistenceManager manager =
		// KnowledgePersistenceManager.getOrCreate(projectKey)
		// .getJiraIssueTextManager();
		List<KnowledgeElement> partsOfText = KnowledgePersistenceManager.getOrCreate(projectKey).getKnowledgeElements();
		// manager.getUserValidatedPartsOfText(projectKey);
		// KnowledgePersistenceManager.getOrCreate(projectKey).getDecisionKnowledgeElements();
		return evaluateClassifier(defaultMeasurements, partsOfText);
	}

	@Override
	public Map<String, Double> evaluateClassifier(List<ClassificationMetric> measurements,
			List<KnowledgeElement> partOfJiraIssueTexts) throws Exception {
		LOGGER.debug("Started evaluation!");
		Map<String, Double> resultsMap = new HashMap<>();
		List<KnowledgeElement> relevantPartOfJiraIssueTexts = partOfJiraIssueTexts.stream()
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
				.map(x -> FineGrainedClassifier.mapKnowledgeTypeToIndex(x.getType())).collect(toList())
				.toArray(new Integer[relevantPartOfJiraIssueTexts.size()]);

		// predict classes
		long start = System.currentTimeMillis();

		boolean[] binaryPredictionsList = classifier.makeBinaryPredictions(sentences);
		Integer[] binaryPredictions = new Integer[sentences.size()];
		for(int i=0; i<binaryPredictionsList.length; i++) {
			binaryPredictions[i] = (binaryPredictionsList[i] == false ? 0 : 1);
		}

		// LOGGER.info(("Time for binary prediction on " + sentences.size() + "
		// sentences took " + (end-start) + " ms.");

		Integer[] fineGrainedPredictions = this.classifier.makeFineGrainedPredictions(relevantSentences).stream()
				.map(x -> FineGrainedClassifier.mapKnowledgeTypeToIndex(x)).collect(toList())
				.toArray(new Integer[relevantSentences.size()]);
		long end = System.currentTimeMillis();

		LOGGER.info("Time for prediction on " + sentences.size() + " sentences took " + (end - start) + " ms.");

		// calculate measurements for each ClassificationMeasure in measurements
		for (ClassificationMetric measurement : measurements) {
			LOGGER.debug("Evaluating: " + measurement.getClass().getSimpleName());
			String binaryKey = measurement.getClass().getSimpleName() + "_binary";
			Double binaryMeasurement = measurement.score(ArrayUtils.toPrimitive(binaryTruths),
					ArrayUtils.toPrimitive(binaryPredictions));
			resultsMap.put(binaryKey, binaryMeasurement);

			for (int classLabel : IntStream.range(0, this.classifier.getFineGrainedClassifier().getNumClasses())
					.toArray()) {
				String fineGrainedKey = measurement.getClass().getSimpleName() + "_fineGrained_"
						+ FineGrainedClassifier.mapIndexToKnowledgeType(classLabel);

				Integer[] currentFineGrainedTruths = mapFineGrainedToBinaryResults(fineGrainedTruths, classLabel);
				Integer[] currentFineGrainedPredictions = mapFineGrainedToBinaryResults(fineGrainedPredictions,
						classLabel);

				Double fineGrainedMeasurement = measurement.score(ArrayUtils.toPrimitive(currentFineGrainedTruths),
						ArrayUtils.toPrimitive(currentFineGrainedPredictions));
				resultsMap.put(fineGrainedKey, fineGrainedMeasurement);
			}

		}
		// return results
		LOGGER.debug("Finished evaluation!");

		return resultsMap;
	}

	private Integer[] mapFineGrainedToBinaryResults(Integer[] array, Integer currentElement) {
		return Arrays.stream(array).map(x -> x.equals(currentElement) ? 1 : 0).toArray(Integer[]::new);

	}

}