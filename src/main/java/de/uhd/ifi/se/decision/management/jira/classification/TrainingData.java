package de.uhd.ifi.se.decision.management.jira.classification;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import smile.data.DataFrame;
import smile.data.Tuple;
import smile.data.type.DataType;
import smile.data.type.DataTypes;
import smile.data.type.StructField;
import smile.data.type.StructType;
import smile.io.Read;
import smile.io.Write;

public class TrainingData {
	private static final Logger LOGGER = LoggerFactory.getLogger(TrainingData.class);

	private DataFrame dataFrame;
	private String[] allSentences;
	private String[] relevantSentences;

	public int[] labelsIsRelevant;
	public int[] labelsKnowledgeType;

	public TrainingData(DataFrame dataFrame) {
		this.dataFrame = dataFrame;
		allSentences = new String[dataFrame.size()];
		labelsIsRelevant = new int[dataFrame.size()];
		parseDataFrame(dataFrame);
	}

	/**
	 * Read the defaultTrainingData.csv that comes with the plugin.
	 */
	public TrainingData() {
		this(readDataFrameFromDefaultTrainingDataCSVFile());
	}

	public TrainingData(String fileName) {
		this(readDataFrameFromCSVFile(fileName));
	}

	public TrainingData(File file) {
		this(readDataFrameFromCSVFile(file));
	}

	public TrainingData(List<KnowledgeElement> trainingElements) {
		this(buildDataFrame(trainingElements));
	}

	public DataFrame getDataFrame() {
		return dataFrame;
	}

	public String[] getAllSentences() {
		return allSentences;
	}

	public String[] getRelevantSentences() {
		return relevantSentences;
	}

	private void parseDataFrame(DataFrame dataFrame) {
		List<String> relevantSentencesList = new ArrayList<>();
		List<Integer> labelsKnowledgeTypeList = new ArrayList<>();
		for (int i = 0; i < dataFrame.size(); i++) {
			Tuple currentRow = dataFrame.get(i);

			allSentences[i] = currentRow.getString(5);
			int isRelevant = 0;
			// iterate over the binary attributes for each possible class
			for (int j = 0; j < currentRow.length() - 1; j++) {
				if (currentRow.getInt(j) == 1) {
					isRelevant = 1;
					relevantSentencesList.add(allSentences[i]);
					labelsKnowledgeTypeList.add(j);
				}
			}
			labelsIsRelevant[i] = isRelevant;
		}

		relevantSentences = relevantSentencesList.toArray(new String[relevantSentencesList.size()]);
		labelsKnowledgeType = labelsKnowledgeTypeList.stream().mapToInt(i -> i).toArray();
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
			trainingDataFile = new File(
					TextClassifier.CLASSIFIER_DIRECTORY + createTrainingDataFileName(projectKey));
			trainingDataFile.createNewFile();
			Write.csv(dataFrame, trainingDataFile.toPath(), CSVFormat.DEFAULT);
		} catch (IOException e) {
			LOGGER.error("The training data file could not be saved. Message: " + e.getMessage());
		}
		return trainingDataFile;
	}

	private String createTrainingDataFileName(String projectKey) {
		Date date = new Date();
		Timestamp timestamp = new Timestamp(date.getTime());
		String prefix = "";
		if (projectKey != null) {
			prefix = projectKey;
		}
		return prefix + timestamp.getTime() + ".csv";
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

		File file = FileManager.getAllTrainingFiles().get(0);
		return readDataFrameFromCSVFile(file);
	}

	public static DataFrame readDataFrameFromCSVFile(File trainingDataFile) {
		DataFrame trainingData = null;
		try {
			trainingData = Read.csv(trainingDataFile.getAbsolutePath(), CSVFormat.DEFAULT.withFirstRecordAsHeader(),
					getDataFrameStructure());
		} catch (IOException | URISyntaxException e) {
			LOGGER.error("Data frame could not be loaded from training data file: " + e.getMessage());
		}
		return trainingData;
	}

}
