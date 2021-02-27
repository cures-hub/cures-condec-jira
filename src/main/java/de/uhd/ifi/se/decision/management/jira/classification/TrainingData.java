package de.uhd.ifi.se.decision.management.jira.classification;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.csv.CSVFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;

import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import smile.data.DataFrame;
import smile.data.Tuple;
import smile.data.type.DataType;
import smile.data.type.DataTypes;
import smile.data.type.StructField;
import smile.data.type.StructType;
import smile.io.Read;
import smile.io.Write;

/**
 * Organizes the data read from csv file (see
 * {@link #readDataFrameFromCSVFile(File)} or created from the current
 * {@link KnowledgeGraph) in such a way that it can be used to train and
 * evaluate the {@link TextClassifier}.
 * 
 * Is also responsible for reading and saving training data from/to .csv files.
 * 
 * @see #getAllSentences()
 * @see #getRelevanceLabelsForAllSentences()
 * 
 * @see #getRelevantSentences()
 * @see #getKnowledgeTypeLabelsForRelevantSentences()
 */
public class TrainingData {
	private static final Logger LOGGER = LoggerFactory.getLogger(TrainingData.class);

	private DataFrame dataFrame;
	private Map<String, Integer> allSentenceRelevanceMap;
	private Map<String, Integer> relevantSentenceKnowledgeTypeLabelMap;
	private String fileName;

	/**
	 * Organizes the data in such a way that it can be used to train and evaluate
	 * the {@link TextClassifier}.
	 * 
	 * @param dataFrame
	 *            {@link DataFrame} read from csv file (see
	 *            {@link #readDataFrameFromCSVFile(File)} or created from the
	 *            current {@link KnowledgeGraph).
	 */
	public TrainingData(DataFrame dataFrame) {
		this.dataFrame = dataFrame;
		allSentenceRelevanceMap = new LinkedHashMap<>();
		relevantSentenceKnowledgeTypeLabelMap = new LinkedHashMap<>();
		parseDataFrame(dataFrame);
	}

	/**
	 * Reads the defaultTrainingData.csv that comes with the plugin.
	 */
	public TrainingData() {
		this(readDataFrameFromDefaultTrainingDataCSVFile());
		this.fileName = "defaultTrainingData";
	}

	/**
	 * Reads the given file by its name.
	 * 
	 * @param fileName
	 *            of a .csv file with training data. The file must be stored in the
	 *            {@link TextClassifier#CLASSIFIER_DIRECTORY}.
	 */
	public TrainingData(String fileName) {
		this(readDataFrameFromCSVFile(fileName));
		this.fileName = fileName;
	}

	/**
	 * Reads the given file.
	 * 
	 * @param file
	 *            a .csv file with training data. The file must be stored in the
	 *            {@link TextClassifier#CLASSIFIER_DIRECTORY}.
	 */
	public TrainingData(File file) {
		this(readDataFrameFromCSVFile(file));
		this.fileName = file.getName();
	}

	/**
	 * Creates a {@link TrainingData} object including a {@link DataFrame} from the
	 * given {@link KnowledgeElement}s.
	 * 
	 * @param fileName
	 *            of a .csv file with training data. The file must be stored in the
	 *            {@link TextClassifier#CLASSIFIER_DIRECTORY}.
	 */
	public TrainingData(List<KnowledgeElement> trainingElements) {
		this(buildDataFrame(trainingElements));
	}

	/**
	 * @return {@link DataFrame} read from csv file (see
	 *         {@link #readDataFrameFromCSVFile(File)} or created from the current
	 *         {@link KnowledgeGraph).
	 */
	public DataFrame getDataFrame() {
		return dataFrame;
	}

	/**
	 * @return both relevant and irrelevant sentences wrt. decision knowledge.
	 */
	public String[] getAllSentences() {
		String[] allSenteces = new String[allSentenceRelevanceMap.keySet().size()];
		return allSentenceRelevanceMap.keySet().toArray(allSenteces);
	}

	/**
	 * @return all relevant sentences wrt. decision knowledge.
	 */
	public String[] getRelevantSentences() {
		String[] relevantSenteces = new String[relevantSentenceKnowledgeTypeLabelMap.keySet().size()];
		return relevantSentenceKnowledgeTypeLabelMap.keySet().toArray(relevantSenteces);
	}

	/**
	 * @return relevance labels for the all sentences in the same order as returned
	 *         by {@link #getAllSentences()}.
	 */
	public int[] getRelevanceLabelsForAllSentences() {
		return allSentenceRelevanceMap.values().stream().mapToInt(i -> i).toArray();
	}

	/**
	 * @return knowledge type labels for the relevant sentences in the same order as
	 *         returned by {@link #getRelevantSentences()}.
	 * 
	 * @see FineGrainedClassifier#mapIndexToKnowledgeType(int)
	 */
	public int[] getKnowledgeTypeLabelsForRelevantSentences() {
		return relevantSentenceKnowledgeTypeLabelMap.values().stream().mapToInt(i -> i).toArray();
	}

	/**
	 * Parses the {@link DataFrame} so that it can be used to train and evaluate the
	 * {@link TextClassifier}.
	 * 
	 * @param dataFrame
	 *            {@link DataFrame} read from csv file (see
	 *            {@link #readDataFrameFromCSVFile(File)} or created from the
	 *            current {@link KnowledgeGraph).
	 */
	private void parseDataFrame(DataFrame dataFrame) {
		if (dataFrame == null) {
			return;
		}
		for (int i = 0; i < dataFrame.size(); i++) {
			Tuple currentRow = dataFrame.get(i);
			String currentSentence = currentRow.getString(5);

			int isRelevant = 0;
			// iterate over the binary attributes for each possible class
			for (int j = 0; j < currentRow.length() - 1; j++) {
				if (currentRow.getInt(j) == 1) {
					isRelevant = 1;
					relevantSentenceKnowledgeTypeLabelMap.put(currentSentence, j);
				}
			}
			allSentenceRelevanceMap.put(currentSentence, isRelevant);
		}
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
	private static DataFrame buildDataFrame(List<KnowledgeElement> trainingElements) {
		List<Tuple> rows = new ArrayList<>();
		StructType structType = getDataFrameStructure();

		for (KnowledgeElement trainingElement : trainingElements) {
			rows.add(Tuple.of(createTrainingRow(trainingElement), structType));
		}
		return DataFrame.of(rows, structType);
	}

	public File saveToFile(String projectKey) {
		File trainingDataFile = null;
		try {
			trainingDataFile = new File(TextClassifier.CLASSIFIER_DIRECTORY + createTrainingDataFileName(projectKey));
			trainingDataFile.createNewFile();
			Write.csv(dataFrame, trainingDataFile.toPath(), CSVFormat.DEFAULT);
		} catch (IOException e) {
			LOGGER.error("The training data file could not be saved. Message: " + e.getMessage());
		}
		return trainingDataFile;
	}

	private String createTrainingDataFileName(String projectKey) {
		Date date = new Date();
		String prefix = "";
		if (projectKey != null) {
			prefix = projectKey;
		}
		return prefix + "-" + new SimpleDateFormat("yyyy-MM-dd-HH-mm").format(date) + ".csv";
	}

	private static StructType getDataFrameStructure() {
		StructField column1 = new StructField("isAlternative", DataTypes.IntegerType);
		StructField column2 = new StructField("isPro", DataTypes.IntegerType);
		StructField column3 = new StructField("isCon", DataTypes.IntegerType);
		StructField column4 = new StructField("isDecisions", DataTypes.IntegerType);
		StructField column5 = new StructField("isIssue", DataTypes.IntegerType);
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

	public static DataFrame readDataFrameFromCSVFile(String csvFileName) {
		File file = new File(TextClassifier.CLASSIFIER_DIRECTORY + csvFileName);
		if (!file.exists()) {
			return readDataFrameFromDefaultTrainingDataCSVFile();
		}
		return readDataFrameFromCSVFile(file);
	}

	public static DataFrame readDataFrameFromDefaultTrainingDataCSVFile() {
		List<File> trainingFiles = FileManager.getAllTrainingFiles();
		if (trainingFiles.isEmpty()) {
			return null;
		}
		File file = trainingFiles.get(0);
		return readDataFrameFromCSVFile(file);
	}

	public static DataFrame readDataFrameFromCSVFile(File trainingDataFile) {
		DataFrame trainingData = null;
		try {
			trainingData = Read.csv(trainingDataFile.getAbsolutePath(), CSVFormat.DEFAULT.withFirstRecordAsHeader(),
					getDataFrameStructure());
		} catch (Exception e) {
			LOGGER.error("Data frame could not be loaded from training data file: " + e.getMessage());
		}
		return trainingData;
	}

	/**
	 * @return list of knowledge elements created from training data. Only the
	 *         summary and the type is set!
	 */
	public List<KnowledgeElement> getKnowledgeElements() {
		List<KnowledgeElement> elements = new ArrayList<>();
		for (Map.Entry<String, Integer> entry : allSentenceRelevanceMap.entrySet()) {
			if (entry.getValue().equals(0)) {
				KnowledgeElement element = new KnowledgeElement();
				element.setSummary(entry.getKey());
				elements.add(element);
			}
		}
		elements.addAll(getDecisionKnowledgeElements());
		return elements;
	}

	/**
	 * @return list of decision knowledge (rationale) elements created from training
	 *         data. Only the summary and the type is set!
	 */
	public List<KnowledgeElement> getDecisionKnowledgeElements() {
		List<KnowledgeElement> elements = new ArrayList<>();
		for (Map.Entry<String, Integer> entry : relevantSentenceKnowledgeTypeLabelMap.entrySet()) {
			KnowledgeType type = FineGrainedClassifier.mapIndexToKnowledgeType(entry.getValue());
			KnowledgeElement element = new KnowledgeElement();
			element.setSummary(entry.getKey());
			element.setType(type);
			elements.add(element);
		}
		return elements;
	}

	@Override
	public String toString() {
		return dataFrame.toString(dataFrame.size());
	}

	/**
	 * @issue How to balance the training data?
	 * @param k
	 * @return
	 */
	public static Map<TrainingData, TrainingData> splitForKFoldCrossValidation(int k, List<KnowledgeElement> elements) {
		Map<TrainingData, TrainingData> splitData = new HashMap<>();
		Collections.shuffle(elements);
		int chunkSize = (int) Math.ceil(elements.size() / k);
		List<List<KnowledgeElement>> parts = Lists.partition(elements, chunkSize);
		for (int i = 0; i < k; i++) {
			List<KnowledgeElement> evaluationElements = parts.get(i);
			List<KnowledgeElement> trainingElements = new ArrayList<>();
			for (int j = 0; j < parts.size(); j++) {
				if (j != i) {
					// part is not already used as evaluation data
					trainingElements.addAll(parts.get(j));
				}
			}
			TrainingData trainingData = new TrainingData(trainingElements);
			TrainingData evaluationData = new TrainingData(evaluationElements);
			splitData.put(trainingData, evaluationData);
		}
		return splitData;
	}

	public String getFileName() {
		return fileName != null ? fileName : "";
	}

}
