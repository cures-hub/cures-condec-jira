package de.uhd.ifi.se.decision.management.jira.classification;

import static java.util.stream.Collectors.toList;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.IntStream;

import org.apache.commons.csv.CSVFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atlassian.gzipfilter.org.apache.commons.lang.ArrayUtils;

import de.uhd.ifi.se.decision.management.jira.classification.preprocessing.PreprocessedData;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeGraph;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.model.PartOfJiraIssueText;
import smile.data.DataFrame;
import smile.data.Tuple;
import smile.data.type.DataType;
import smile.data.type.StructField;
import smile.data.type.StructType;
import smile.io.Read;
import smile.io.Write;
import smile.validation.metric.ClassificationMetric;
import smile.validation.metric.FScore;

/**
 * Class responsible to train the supervised text classifier. For this purpose,
 * the project admin needs to create and select a training data file.
 */
public class ClassifierTrainer implements EvaluableClassifier {
	protected static final Logger LOGGER = LoggerFactory.getLogger(ClassifierTrainer.class);

	protected DataFrame dataFrame;
	protected String projectKey;

	public ClassifierTrainer(String projectKey) {
		new File(DecisionKnowledgeClassifier.CLASSIFIER_DIRECTORY).mkdirs();
		this.projectKey = projectKey;
	}

	public ClassifierTrainer(String projectKey, String fileName) {
		this(projectKey);
		if (fileName == null || fileName.isEmpty()) {
			return;
		}
		this.dataFrame = getDataFrameFromCSVFile(fileName);
	}

	/**
	 * Trains the Classifier with the Data from the Database that was set and
	 * validated from the user. Creates a new model Files that can be used to
	 * classify the comments and description of a Jira issue and Git-commit
	 * messages.
	 */
	// is called after setting training-file
	public boolean train() {
		boolean isTrained = true;
		try {
			trainBinaryClassifier();
			trainFineGrainedClassifier();
		} catch (Exception e) {
			LOGGER.error("The classifier could not be trained:" + e.getMessage());
			isTrained = false;
		}
		return isTrained;
	}

	private void trainBinaryClassifier() {
		LOGGER.debug("Binary classifier training started.");
		TrainingData trainingData = new TrainingData(getDataFrame());
		PreprocessedData preprocessedSentences = new PreprocessedData(trainingData, false);
		DecisionKnowledgeClassifier.getInstance().trainBinaryClassifier(preprocessedSentences);
	}

	private void trainFineGrainedClassifier() {
		LOGGER.debug("Fine-grained classifier training started.");
		TrainingData trainingData = new TrainingData(getDataFrame());
		PreprocessedData preprocessedSentences = new PreprocessedData(trainingData, true);
		DecisionKnowledgeClassifier.getInstance().trainFineGrainedClassifier(preprocessedSentences);
		// this.classifier.getFineGrainedClassifier().saveToFile();
	}

	public boolean update(PartOfJiraIssueText sentence) {
		DecisionKnowledgeClassifier classifier = DecisionKnowledgeClassifier.getInstance();
		try {
			double[][] features = classifier.preprocess(sentence.getSummary());
			// classifier needs numerical value
			Integer labelIsRelevant = sentence.isRelevant() ? 1 : 0;

			for (double[] feature : features) {
				classifier.getBinaryClassifier().update(feature, labelIsRelevant);
				if (sentence.isRelevant()) {
					classifier.getFineGrainedClassifier().train(feature, sentence.getType());
				}
			}
		} catch (Exception e) {
			LOGGER.error("Could not update classifier: " + e.getMessage());
		}
		return true;
	}

	public DataFrame getDataFrameFromCSVFile(String csvFileName) {
		File file = new File(DecisionKnowledgeClassifier.CLASSIFIER_DIRECTORY + csvFileName);
		return getDataFrameFromCSVFile(file);
	}

	public static DataFrame getDataFrameFromCSVFile(File trainingDataFile) {
		DataFrame trainingData = null;
		try {
			trainingData = Read.csv(trainingDataFile.getAbsolutePath(), CSVFormat.DEFAULT.withFirstRecordAsHeader(),
					getDataFrameStructure());
		} catch (IOException | URISyntaxException e) {
			LOGGER.error("Data frame could not be loaded from training data file: " + e.getMessage());
		}
		return trainingData;
	}

	public DataFrame getDataFrame() {
		if (dataFrame == null) {
			dataFrame = loadDataFrame();
		}
		return dataFrame;
	}

	private DataFrame loadDataFrame() {
		List<File> trainingFiles = FileManager.getAllTrainingFiles();
		DataFrame loadedInstances = getDataFrameFromCSVFile(trainingFiles.get(0));
		return loadedInstances;
	}

	/**
	 * Reads training data from a file to train the classifier.
	 *
	 * @param file
	 *            file to train the classifier.
	 */
	public void setTrainingFile(File file) {
		dataFrame = getDataFrameFromCSVFile(file);
	}

	private String getTrainingDataFileName() {
		Date date = new Date();
		Timestamp timestamp = new Timestamp(date.getTime());
		String prefix = "";
		if (projectKey != null) {
			prefix = projectKey;
		}
		return prefix + timestamp.getTime() + ".csv";
	}

	/**
	 * Creates a new file for the current project that can be used to train the
	 * classifier and saves it on the server in the JIRA home directory in the
	 * data/condec-plugin/project-key folder.
	 *
	 * @param useOnlyValidatedData
	 *            Boolean flag to indicated whether to use all or only
	 *            user-validated data.
	 * @return ARFF file that was created and saved on the server or null if it
	 *         could not be saved.
	 */
	public File saveTrainingFile() {
		File trainingDataFile = null;
		try {
			trainingDataFile = new File(
					DecisionKnowledgeClassifier.CLASSIFIER_DIRECTORY + getTrainingDataFileName());
			trainingDataFile.createNewFile();
			DataFrame dataFrame = buildDataFrame(getKnowledgeElementsValidForTraining());
			Write.csv(dataFrame, trainingDataFile.toPath(), CSVFormat.DEFAULT);
		} catch (IOException e) {
			LOGGER.error("The training data file could not be saved. Message: " + e.getMessage());
		}
		return trainingDataFile;
	}

	public List<KnowledgeElement> getKnowledgeElementsValidForTraining() {
		Set<KnowledgeElement> allElementsInGraph = KnowledgeGraph.getOrCreate(projectKey).vertexSet();
		List<KnowledgeElement> knowledgeElements = new ArrayList<>();
		for (KnowledgeElement element : allElementsInGraph) {
			if (!element.getType().canBeDocumentedInJiraIssueText() && element.getType() != KnowledgeType.OTHER) {
				continue;
			}
			if (element instanceof PartOfJiraIssueText && !((PartOfJiraIssueText) element).isValidated()) {
				continue;
			}
			knowledgeElements.add(element);
		}
		return knowledgeElements;
	}

	/**
	 * @param trainingElements
	 *            list of validated decision knowledge elements.
	 * @return training dataframe for the supervised text classifier. The dataframe
	 *         contains the knowledge type indicated by the value 1 (or 0 for type
	 *         OTHER) and the summary of the element.
	 * 
	 *         0, 0, 0, 1, 0 'I am a test sentence that is a decision.' 1,0,0,0,0 'I
	 *         am an alternative for the issue.' 0,0,0,0,1 'And I am the issue for
	 *         the decision and the alternative.'
	 */
	public static DataFrame buildDataFrame(List<KnowledgeElement> trainingElements) {
		List<Tuple> rows = new ArrayList<>();
		StructType structType = getDataFrameStructure();

		for (KnowledgeElement trainingElement : trainingElements) {
			rows.add(Tuple.of(createTrainingRow(trainingElement), structType));
		}
		return DataFrame.of(rows, structType);
	}

	private static StructType getDataFrameStructure() {
		StructField column1 = new StructField("isAlternative", DataType.of(Integer.class));
		StructField column2 = new StructField("isPro", DataType.of(Integer.class));
		StructField column3 = new StructField("isCon", DataType.of(Integer.class));
		StructField column4 = new StructField("isDecisions", DataType.of(Integer.class));
		StructField column5 = new StructField("isIssue", DataType.of(Integer.class));
		StructField column6 = new StructField("sentence", DataType.of(String.class));
		return new StructType(column1, column2, column3, column4, column5, column6);
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
	public static Object[] createTrainingRow(KnowledgeElement element) {
		Object[] rowValues = new Object[6];
		for (int i = 0; i < rowValues.length - 1; i++) {
			rowValues[i] = 0;
		}
		int index = FineGrainedClassifier.mapKnowledgeTypeToIndex(element.getType());
		if (index > -1) {
			rowValues[index] = 1;
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
	public Map<String, Double> evaluateClassifier() {
		// create and initialize default measurements list
		List<ClassificationMetric> defaultMeasurements = new ArrayList<>();
		defaultMeasurements.add(new FScore());

		List<KnowledgeElement> elements = getKnowledgeElementsValidForTraining();
		return evaluateClassifier(defaultMeasurements, elements);
	}

	@Override
	public Map<String, Double> evaluateClassifier(List<ClassificationMetric> measurements,
			List<KnowledgeElement> partOfJiraIssueTexts) {
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

		boolean[] binaryPredictionsList = DecisionKnowledgeClassifier.getInstance().makeBinaryPredictions(sentences);
		Integer[] binaryPredictions = new Integer[sentences.size()];
		for (int i = 0; i < binaryPredictionsList.length; i++) {
			binaryPredictions[i] = binaryPredictionsList[i] ? 1 : 0;
		}

		// LOGGER.info(("Time for binary prediction on " + sentences.size() + "
		// sentences took " + (end-start) + " ms.");

		Integer[] fineGrainedPredictions = DecisionKnowledgeClassifier.getInstance()
				.makeFineGrainedPredictions(relevantSentences).stream()
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

			for (int classLabel : IntStream
					.range(0, DecisionKnowledgeClassifier.getInstance().getFineGrainedClassifier().getNumClasses())
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