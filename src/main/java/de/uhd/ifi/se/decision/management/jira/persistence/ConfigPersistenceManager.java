package de.uhd.ifi.se.decision.management.jira.persistence;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atlassian.gzipfilter.org.apache.commons.lang.math.NumberUtils;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.config.IssueTypeManager;
import com.atlassian.jira.issue.issuetype.IssueType;
import com.atlassian.jira.project.Project;
import com.atlassian.sal.api.pluginsettings.PluginSettings;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.atlassian.sal.api.transaction.TransactionCallback;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import de.uhd.ifi.se.decision.management.jira.ComponentGetter;
import de.uhd.ifi.se.decision.management.jira.classification.TextClassificationConfiguration;
import de.uhd.ifi.se.decision.management.jira.decisionguidance.knowledgesources.KnowledgeSource;
import de.uhd.ifi.se.decision.management.jira.decisionguidance.knowledgesources.projectsource.ProjectSource;
import de.uhd.ifi.se.decision.management.jira.decisionguidance.knowledgesources.rdfsource.RDFSource;
import de.uhd.ifi.se.decision.management.jira.decisionguidance.recommender.RecommenderType;
import de.uhd.ifi.se.decision.management.jira.extraction.GitClient;
import de.uhd.ifi.se.decision.management.jira.extraction.versioncontrol.GitRepositoryConfiguration;
import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeProject;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.quality.completeness.DefinitionOfDone;
import de.uhd.ifi.se.decision.management.jira.releasenotes.ReleaseNotesCategory;

/**
 * Stores and reads configuration settings such as whether the ConDec plug-in is
 * activated for a specific project.
 */
public class ConfigPersistenceManager {
	private static final Logger LOGGER = LoggerFactory.getLogger(ConfigPersistenceManager.class);
	private static PluginSettingsFactory pluginSettingsFactory = ComponentAccessor
			.getOSGiComponentInstanceOfType(PluginSettingsFactory.class);
	private static TransactionTemplate transactionTemplate = ComponentAccessor
			.getOSGiComponentInstanceOfType(TransactionTemplate.class);

	public static Collection<String> getEnabledWebhookTypes(String projectKey) {
		IssueTypeManager issueTypeManager = ComponentAccessor.getComponent(IssueTypeManager.class);
		Collection<IssueType> issueTypes = issueTypeManager.getIssueTypes();
		Collection<String> issueTypeNames = new ArrayList<>();
		for (IssueType issueType : issueTypes) {
			if (isWebhookTypeEnabled(projectKey, issueType.getName())) {
				issueTypeNames.add(issueType.getName());
			}
		}
		return issueTypeNames;
	}

	public static String getValue(String parameter) {
		return getValue(parameter, null, true);
	}

	public static String getValue(String projectKey, String parameter) {
		return getValue(parameter, projectKey, false);
	}

	public static String getValue(String parameter, String projectKey, boolean isGlobalSetting) {
		PluginSettings settings;
		if (isGlobalSetting) {
			settings = pluginSettingsFactory.createGlobalSettings();
		} else {
			if (projectKey == null || projectKey.isBlank()) {
				return "";
			}
			settings = pluginSettingsFactory.createSettingsForKey(projectKey);
		}
		if (parameter == null || parameter.isBlank()) {
			return "";
		}
		Object value = transactionTemplate.execute(new TransactionCallback<Object>() {
			@Override
			public Object doInTransaction() {
				return settings.get(ComponentGetter.PLUGIN_KEY + "." + parameter);
			}
		});
		if (value instanceof String) {
			return value.toString();
		}
		return "";
	}

	public static Object getSavedObject(String projectKey, String parameter, Type type) {
		Gson gson = new Gson();
		Object object = null;
		try {
			object = gson.fromJson(getValue(projectKey, parameter), type);
		} catch (Exception e) {
			LOGGER.error("Saved config could not be read: " + e.getMessage());
		}
		return object;
	}

	public static String getWebhookSecret(String projectKey) {
		return getValue(projectKey, "webhookSecret");
	}

	public static String getWebhookUrl(String projectKey) {
		return getValue(projectKey, "webhookUrl");
	}

	public static String getDecisionTableCriteriaQuery(String projectKey) {
		return getValue(projectKey, "criteriaQuery");
	}

	public static boolean isActivated(String projectKey) {
		String isActivated = getValue(projectKey, "isActivated");
		return "true".equals(isActivated);
	}

	public static boolean isIssueStrategy(String projectKey) {
		String isIssueStrategy = getValue(projectKey, "isIssueStrategy");
		return "true".equals(isIssueStrategy);
	}

	// TODO Add GitConfig class with isKnowledgeExtractedFromGit attribute
	public static boolean isKnowledgeExtractedFromGit(String projectKey) {
		String isKnowledgeExtractedFromGit = getValue(projectKey, "isKnowledgeExtractedFromGit");
		return "true".equals(isKnowledgeExtractedFromGit);
	}

	// TODO Add GitConfig class with isPostDefaultBranchCommitsActivated attribute
	public static boolean isPostSquashedCommitsActivated(String projectKey) {
		return "true".equals(getValue(projectKey, "isPostSquashedCommitsActivated"));
	}

	// TODO Add GitConfig class with isPostFeatureBranchCommitsActivated attribute
	public static boolean isPostFeatureBranchCommitsActivated(String projectKey) {
		return "true".equals(getValue(projectKey, "isPostFeatureBranchCommitsActivated"));
	}

	// TODO Testing
	public static boolean isKnowledgeTypeEnabled(String projectKey, KnowledgeType knowledgeType) {
		return isKnowledgeTypeEnabled(projectKey, knowledgeType.toString());
	}

	public static boolean isKnowledgeTypeEnabled(String projectKey, String knowledgeType) {
		String isKnowledgeTypeEnabled = getValue(projectKey, knowledgeType);
		return "true".equals(isKnowledgeTypeEnabled);
	}

	public static TextClassificationConfiguration getTextClassificationConfiguration(String projectKey) {
		Type type = new TypeToken<TextClassificationConfiguration>() {
		}.getType();
		TextClassificationConfiguration textClassificationConfiguration = (TextClassificationConfiguration) getSavedObject(
				projectKey, "textClassificationConfiguration", type);
		if (textClassificationConfiguration == null) {
			return new TextClassificationConfiguration();
		}
		return textClassificationConfiguration;
	}

	public static boolean isWebhookEnabled(String projectKey) {
		String isWebhookEnabled = getValue(projectKey, "isWebhookEnabled");
		return "true".equals(isWebhookEnabled);
	}

	public static boolean isWebhookTypeEnabled(String projectKey, String webhookType) {
		if (webhookType == null || webhookType.isBlank()) {
			return false;
		}
		String isWebhookTypeEnabled = getValue(projectKey, "webhookType" + "." + webhookType);
		return "true".equals(isWebhookTypeEnabled);
	}

	public static void setActivated(String projectKey, boolean isActivated) {
		setValue(projectKey, "isActivated", Boolean.toString(isActivated));
	}

	public static void setIssueStrategy(String projectKey, boolean isIssueStrategy) {
		setValue(projectKey, "isIssueStrategy", Boolean.toString(isIssueStrategy));
	}

	public static void setKnowledgeExtractedFromGit(String projectKey, boolean isKnowledgeExtractedFromGit) {
		setValue(projectKey, "isKnowledgeExtractedFromGit", Boolean.toString(isKnowledgeExtractedFromGit));
		if (isKnowledgeExtractedFromGit) {
			// TODO Pull Repo
			GitClient.getOrCreate(projectKey);
		}
	}

	public static void setDecisionTableCriteriaQuery(String projectKey, String query) {
		setValue(projectKey, "criteriaQuery", query);
	}

	// TODO Testing
	public static void setPostSquashedCommits(String projectKey, Boolean checked) {
		setValue(projectKey, "isPostSquashedCommitsActivated", Boolean.toString(checked));
	}

	// TODO Testing
	public static void setPostFeatureBranchCommits(String projectKey, Boolean checked) {
		setValue(projectKey, "isPostFeatureBranchCommitsActivated", Boolean.toString(checked));
	}

	public static void setGitRepositoryConfiguration(String projectKey, GitRepositoryConfiguration gitConf) {
		List<GitRepositoryConfiguration> gitConfs = new ArrayList<GitRepositoryConfiguration>();
		gitConfs.add(gitConf);
		setGitRepositoryConfigurations(projectKey, gitConfs);
	}

	public static void setGitRepositoryConfigurations(String projectKey,
			List<GitRepositoryConfiguration> gitRepositoryConfigurations) {
		Type type = new TypeToken<List<GitRepositoryConfiguration>>() {
		}.getType();
		saveObject(projectKey, "gitRepositoryConfigurations", gitRepositoryConfigurations, type);
	}

	@SuppressWarnings("unchecked")
	public static List<GitRepositoryConfiguration> getGitRepositoryConfigurations(String projectKey) {
		Type type = new TypeToken<List<GitRepositoryConfiguration>>() {
		}.getType();
		List<GitRepositoryConfiguration> gitRepositoryConfigurations = (List<GitRepositoryConfiguration>) getSavedObject(
				projectKey, "gitRepositoryConfigurations", type);
		if (gitRepositoryConfigurations == null) {
			return new ArrayList<GitRepositoryConfiguration>();
		}
		return gitRepositoryConfigurations;
	}

	public static void setKnowledgeTypeEnabled(String projectKey, String knowledgeType,
			boolean isKnowledgeTypeEnabled) {
		setValue(projectKey, knowledgeType, Boolean.toString(isKnowledgeTypeEnabled));
	}

	public static void setTextClassifierActivated(String projectKey, boolean isActivated) {
		TextClassificationConfiguration textClassificationConfiguration = getTextClassificationConfiguration(
				projectKey);
		textClassificationConfiguration.setActivated(isActivated);
		saveTextClassificationConfiguration(projectKey, textClassificationConfiguration);
	}

	public static void setTrainingFileForClassifier(String projectKey, String trainingFileName) {
		TextClassificationConfiguration textClassificationConfiguration = getTextClassificationConfiguration(
				projectKey);
		textClassificationConfiguration.setSelectedGroundTruthFile(trainingFileName);
		saveTextClassificationConfiguration(projectKey, textClassificationConfiguration);
	}

	public static void saveTextClassificationConfiguration(String projectKey,
			TextClassificationConfiguration textClassificationConfiguration) {
		Type type = new TypeToken<TextClassificationConfiguration>() {
		}.getType();
		saveObject(projectKey, "textClassificationConfiguration", textClassificationConfiguration, type);
	}

	public static void setValue(String parameter, String value) {
		PluginSettings settings = pluginSettingsFactory.createGlobalSettings();
		settings.put(ComponentGetter.PLUGIN_KEY + "." + parameter, value);
	}

	public static void setValue(String projectKey, String parameter, Object value) {
		if (projectKey == null || value == null) {
			return;
		}
		PluginSettings settings = pluginSettingsFactory.createSettingsForKey(projectKey);
		settings.put(ComponentGetter.PLUGIN_KEY + "." + parameter, value);
	}

	public static void setValue(String projectKey, String parameter, String value) {
		if (projectKey == null || value == null) {
			return;
		}
		PluginSettings settings = pluginSettingsFactory.createSettingsForKey(projectKey);
		settings.put(ComponentGetter.PLUGIN_KEY + "." + parameter, value);
	}

	public static void saveObject(String projectKey, String parameter, Object value, Type type) {
		Gson gson = new Gson();
		setValue(projectKey, parameter, gson.toJson(value, type));
	}

	public static void setWebhookEnabled(String projectKey, boolean isWebhookEnabled) {
		setValue(projectKey, "isWebhookEnabled", Boolean.toString(isWebhookEnabled));
	}

	public static void setWebhookSecret(String projectKey, String webhookSecret) {
		setValue(projectKey, "webhookSecret", webhookSecret);
	}

	public static void setWebhookType(String projectKey, String webhookType, boolean isWebhookTypeEnabled) {
		if (webhookType == null || webhookType.isBlank()) {
			return;
		}
		setValue(projectKey, "webhookType" + "." + webhookType, Boolean.toString(isWebhookTypeEnabled));
	}

	public static void setWebhookUrl(String projectKey, String webhookUrl) {
		setValue(projectKey, "webhookUrl", webhookUrl);
	}

	public static void setReleaseNoteMapping(String projectKey, ReleaseNotesCategory category,
			List<String> selectedIssueNames) {
		String joinedIssueNames = String.join(",", selectedIssueNames);
		setValue(projectKey, "releaseNoteMapping" + "." + category, joinedIssueNames);
	}

	public static List<String> getReleaseNoteMapping(String projectKey, ReleaseNotesCategory category) {
		String joinedIssueNames = getValue(projectKey, "releaseNoteMapping" + "." + category);
		return Arrays.asList(joinedIssueNames.split(","));
	}

	/* **************************************/
	/*										*/
	/* Configuration for Consistency */
	/*										*/
	/* **************************************/

	public static void setFragmentLength(String projectKey, int fragmentLength) {
		setValue(projectKey, "fragmentLength", Integer.toString(fragmentLength));
	}

	public static int getFragmentLength(String projectKey) {
		return NumberUtils.toInt(getValue(projectKey, "fragmentLength"), 21);
	}

	public static void setMinLinkSuggestionScore(String projectKey, double minLinkSuggestionProbability) {
		setValue(projectKey, "minLinkSuggestionProbability", Double.toString(minLinkSuggestionProbability));
	}

	public static double getMinLinkSuggestionScore(String projectKey) {
		return NumberUtils.toDouble(getValue(projectKey, "minLinkSuggestionProbability"), 0.3);
	}

	/* **************************************/
	/*										*/
	/* Configuration for Decision Guidance */
	/*										*/
	/* **************************************/

	public static void setMaxNumberRecommendations(String projectKey, int maxNumberRecommendation) {
		setValue(projectKey, "maxNumberRecommendations", Integer.toString(maxNumberRecommendation));
	}

	public static int getMaxNumberRecommendations(String projectKey) {
		return NumberUtils.toInt(getValue(projectKey, "maxNumberRecommendations"), 100);
	}

	public static void setSimilarityThreshold(String projectKey, double threshold) {
		setValue(projectKey, "similarityThreshold", Double.toString(threshold));
	}

	public static double getSimilarityThreshold(String projectKey) {
		return NumberUtils.toDouble(getValue(projectKey, "similarityThreshold"), 0.85);
	}

	public static void setIrrelevantWords(String projectKey, String words) {
		setValue(projectKey, "bagOfIrrelevantWords", words);
	}

	public static String getIrrelevantWords(String projectKey) {
		return getValue(projectKey, "bagOfIrrelevantWords");
	}

	@SuppressWarnings("unchecked")
	public static void setRDFKnowledgeSource(String projectKey, RDFSource rdfSource) {
		List<RDFSource> rdfSourceList = new ArrayList<>();
		Type type = new TypeToken<List<RDFSource>>() {
		}.getType();
		if (rdfSource != null) {
			try {
				rdfSourceList = (List<RDFSource>) getSavedObject(projectKey, "rdfsource.list", type);
				if (rdfSourceList == null)
					rdfSourceList = new ArrayList<>();
			} catch (Exception e) {
				rdfSourceList = new ArrayList<>();
				saveObject(projectKey, "rdfsource.list", rdfSourceList, type);
			}

			rdfSource.setActivated(true); // default: activated
			rdfSourceList.add(rdfSource);

			saveObject(projectKey, "rdfsource.list", rdfSourceList, type);

		}

	}

	@SuppressWarnings({ "unchecked", "finally" })
	public static List<RDFSource> getRDFKnowledgeSource(String projectKey) {
		List<RDFSource> rdfSourceList = new ArrayList<>();
		List<RDFSource> temp = new ArrayList<>();
		if (projectKey == null)
			return rdfSourceList;

		Type type = new TypeToken<List<RDFSource>>() {
		}.getType();
		try {
			temp = (List<RDFSource>) getSavedObject(projectKey, "rdfsource.list", type);
			if (temp != null) {
				for (RDFSource source : temp) {
					source.setLimit(getMaxNumberRecommendations(projectKey));
					rdfSourceList.add(source);
				}
			}
		} catch (JsonSyntaxException e) {
		} finally {
			return rdfSourceList == null ? new ArrayList<>() : rdfSourceList;
		}
	}

	public static void updateKnowledgeSource(String projectKey, String knowledgeSourceName, RDFSource rdfSource) {
		List<RDFSource> rdfSourceList = getRDFKnowledgeSource(projectKey);
		for (int i = 0; i < rdfSourceList.size(); ++i) {
			if (rdfSourceList.get(i).getName().equals(knowledgeSourceName)) {
				rdfSourceList.set(i, rdfSource);
				break;
			}
		}
		Type listType = new TypeToken<List<RDFSource>>() {
		}.getType();
		saveObject(projectKey, "rdfsource.list", rdfSourceList, listType);
	}

	public static void deleteKnowledgeSource(String projectKey, String knowledgeSourceName) {
		List<RDFSource> rdfSourceList = getRDFKnowledgeSource(projectKey);
		rdfSourceList.removeIf(rdfSource -> knowledgeSourceName.equals(rdfSource.getName()));
		Type listType = new TypeToken<List<RDFSource>>() {
		}.getType();
		saveObject(projectKey, "rdfsource.list", rdfSourceList, listType);
	}

	public static void deleteAllKnowledgeSource(String projectKey) {
		List<RDFSource> rdfSourceList = new ArrayList<>();
		Type listType = new TypeToken<List<RDFSource>>() {
		}.getType();
		saveObject(projectKey, "rdfsource.list", rdfSourceList, listType);
	}

	public static void setRDFKnowledgeSourceActivation(String projectKey, String rdfSourceName, boolean isActivated) {
		List<RDFSource> rdfSourceList = getRDFKnowledgeSource(projectKey);
		Type listType = new TypeToken<List<RDFSource>>() {
		}.getType();

		for (int i = 0; i < rdfSourceList.size(); ++i) {
			if (rdfSourceName.equals(rdfSourceList.get(i).getName())) {
				rdfSourceList.get(i).setActivated(isActivated);
				break;
			}
		}

		saveObject(projectKey, "rdfsource.list", rdfSourceList, listType);
	}

	public static void setProjectSource(String projectKey, String projectSourceKey, boolean isActivated) {
		setValue(projectKey, "projectSource." + projectSourceKey, Boolean.toString(isActivated));
	}

	public static boolean getProjectSource(String projectKey, String projectSourceKey) {
		return Boolean.valueOf(getValue(projectKey, "projectSource." + projectSourceKey));
	}

	public static List<ProjectSource> getProjectSourcesForActiveProjects(String projectKey) {
		List<ProjectSource> projectSources = new ArrayList<>();
		if (projectKey == null)
			return projectSources;

		Project currentProject = ComponentAccessor.getProjectManager().getProjectByCurrentKey(projectKey);
		if (currentProject != null) {
			for (Project project : ComponentAccessor.getProjectManager().getProjects()) {
				DecisionKnowledgeProject jiraProject = new DecisionKnowledgeProject(project);
				boolean projectSourceActivation = ConfigPersistenceManager.getProjectSource(projectKey,
						jiraProject.getProjectKey());
				if (jiraProject.isActivated()) {
					ProjectSource projectSource = new ProjectSource(projectKey, jiraProject.getProjectKey(),
							projectSourceActivation);
					projectSources.add(projectSource);
				}
			}
		}
		return projectSources;
	}

	public static List<KnowledgeSource> getAllKnowledgeSources(String projectKey) {
		List<KnowledgeSource> knowledgeSources = new ArrayList<>();

		knowledgeSources.addAll(getRDFKnowledgeSource(projectKey));
		knowledgeSources.addAll(getProjectSourcesForActiveProjects(projectKey));
		// New KnowledgeSources could be added here.

		return knowledgeSources;
	}

	public static List<KnowledgeSource> getAllActivatedKnowledgeSources(String projectKey) {
		List<KnowledgeSource> knowledgeSources = new ArrayList<>();

		knowledgeSources.addAll(getRDFKnowledgeSource(projectKey));
		knowledgeSources.addAll(getProjectSourcesForActiveProjects(projectKey));
		// New KnowledgeSources could be added here.

		knowledgeSources.removeIf(knowledgeSource -> !knowledgeSource.isActivated());
		return knowledgeSources;
	}

	public static void setAddRecommendationDirectly(String projectKey, Boolean addRecommendationDirectly) {
		setValue(projectKey, "addRecommendationDirectly", addRecommendationDirectly.toString());
	}

	public static boolean getAddRecommendationDirectly(String projectKey) {
		return Boolean.valueOf(getValue(projectKey, "addRecommendationDirectly"));
	}

	public static void setRecommendationInput(String projectKey, String recommendationInput, boolean isActivated) {
		setValue(projectKey, "recommendationInput." + recommendationInput, String.valueOf(isActivated));
	}

	public static boolean getRecommendationInput(String projectKey, String recommenderInput) {
		String value = getValue(projectKey, "recommendationInput." + recommenderInput);
		return Boolean.valueOf(value);
	}

	public static Map<String, Boolean> getRecommendationInputAsMap(String projectKey) {
		Map<String, Boolean> recommenderTypes = new HashMap<>();
		for (RecommenderType recommenderType : RecommenderType.values()) {
			recommenderTypes.put(recommenderType.toString(),
					Boolean.valueOf(getValue(projectKey, "recommendationInput." + recommenderType.toString())));
		}
		return recommenderTypes;
	}

	/* **************************************/
	/*										*/
	/* Configuration for Rationale Backlog */
	/*										*/
	/* **************************************/

	public static void setDefinitionOfDone(String projectKey, DefinitionOfDone definitionOfDone) {
		Type type = new TypeToken<DefinitionOfDone>() {
		}.getType();
		saveObject(projectKey, "definitionOfDone", definitionOfDone, type);
	}

	public static DefinitionOfDone getDefinitionOfDone(String projectKey) {
		Type type = new TypeToken<DefinitionOfDone>() {
		}.getType();
		DefinitionOfDone definitionOfDone = (DefinitionOfDone) getSavedObject(projectKey, "definitionOfDone", type);
		if (definitionOfDone == null) {
			return new DefinitionOfDone();
		}
		return definitionOfDone;
	}

	/* **********************************************************/
	/*										 					*/
	/* Configuration for quality = completeness + consistency */
	/*															*/
	/* **********************************************************/

	public static void setActivationStatusOfQualityEvent(String projectKey, String eventKey, boolean isActivated) {
		setValue(projectKey, eventKey, Boolean.toString(isActivated));
	}

	public static boolean getActivationStatusOfQualityEvent(String projectKey, String eventKey) {
		return "true".equals(getValue(projectKey, eventKey));
	}

}
