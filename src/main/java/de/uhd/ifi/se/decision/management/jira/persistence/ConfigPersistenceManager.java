package de.uhd.ifi.se.decision.management.jira.persistence;

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
import de.uhd.ifi.se.decision.management.jira.decisionguidance.knowledgesources.KnowledgeSource;
import de.uhd.ifi.se.decision.management.jira.decisionguidance.knowledgesources.ProjectSource;
import de.uhd.ifi.se.decision.management.jira.decisionguidance.knowledgesources.RDFSource;
import de.uhd.ifi.se.decision.management.jira.decisionguidance.recommender.RecommenderType;
import de.uhd.ifi.se.decision.management.jira.extraction.GitClient;
import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeProject;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.quality.completeness.DefinitionOfDone;
import de.uhd.ifi.se.decision.management.jira.releasenotes.ReleaseNotesCategory;
import scala.annotation.meta.getter;

import java.lang.reflect.Type;
import java.util.*;

/**
 * Stores and reads configuration settings such as whether the ConDec plug-in is
 * activated for a specific project.
 */
public class ConfigPersistenceManager {
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
		return gson.fromJson(getValue(projectKey, parameter), type);
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

	public static boolean isIconParsing(String projectKey) {
		String isIconParsing = getValue(projectKey, "isIconParsing");
		return "true".equals(isIconParsing);
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

	public static boolean isClassifierEnabled(String projectKey) {
		return getValue(projectKey, "setClassiferForIssueComments").equals("true");
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

	public static void setIconParsing(String projectKey, boolean isIconParsing) {
		setValue(projectKey, "isIconParsing", Boolean.toString(isIconParsing));
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

	public static void setGitUris(String projectKey, String gitUris) {
		setValue(projectKey, "gitUris", gitUris);
	}

	public static List<String> getGitUris(String projectKey) {
		String value = getValue(projectKey, "gitUris");
		if (value == "") {
			return new ArrayList<String>();
		}
		List<String> uris = Arrays.asList(value.split(";;"));
		return uris;
	}

	// TODO Add GitConfig
	public static void setDefaultBranches(String projectKey, String defaultBranches) {
		setValue(projectKey, "defaultBranches", defaultBranches);
	}

	// TODO Add GitConfig
	public static void setAuthMethods(String projectKey, String authMethods) {
		setValue(projectKey, "authMethods", authMethods);
	}

	// TODO Add GitConfig
	public static void setUsernames(String projectKey, String usernames) {
		setValue(projectKey, "usernames", usernames);
	}

	// TODO Add GitConfig
	public static void setTokens(String projectKey, String tokens) {
		setValue(projectKey, "tokens", tokens);
	}

	public static Map<String, String> getDefaultBranches(String projectKey) {
		Map<String, String> defaultBranches = new HashMap<String, String>();
		String value = getValue(projectKey, "gitUris");
		List<String> uris = Arrays.asList(value.split(";;"));
		value = getValue(projectKey, "defaultBranches");
		if (value == null || value.isBlank()) {
			for (String uri : uris) {
				defaultBranches.put(uri, "master");
			}
			return defaultBranches;
		}
		List<String> branches = Arrays.asList(value.split(";;"));
		for (int i = 0; i < uris.size(); i++) {
			if (branches.size() <= i) {
				defaultBranches.put(uris.get(i), "master");
			} else {
				defaultBranches.put(uris.get(i), branches.get(i));
			}
		}
		return defaultBranches;
	}

	public static String getDefaultBranch(String projectKey, String repoUri) {
		return getDefaultBranches(projectKey).get(repoUri);
	}

	public static Map<String, String> getAuthMethods(String projectKey) {
		Map<String, String> authMethods = new HashMap<String, String>();
		String value = getValue(projectKey, "gitUris");
		List<String> uris = Arrays.asList(value.split(";;"));
		value = getValue(projectKey, "authMethods");
		if (value == null || value.isBlank()) {
			for (String uri : uris) {
				authMethods.put(uri, "NONE");
			}
			return authMethods;
		}
		List<String> branches = Arrays.asList(value.split(";;"));
		for (int i = 0; i < uris.size(); i++) {
			if (branches.size() <= i) {
				authMethods.put(uris.get(i), "NONE");
			} else {
				authMethods.put(uris.get(i), branches.get(i));
			}
		}
		return authMethods;
	}

	public static String getAuthMethod(String projectKey, String repoUri) {
		return getAuthMethods(projectKey).get(repoUri);
	}

	public static Map<String, String> getUsernames(String projectKey) {
		Map<String, String> usernames = new HashMap<String, String>();
		String value = getValue(projectKey, "gitUris");
		List<String> uris = Arrays.asList(value.split(";;"));
		value = getValue(projectKey, "usernames");
		if (value == null || value.isBlank()) {
			for (String uri : uris) {
				usernames.put(uri, "");
			}
			return usernames;
		}
		List<String> branches = Arrays.asList(value.split(";;"));
		for (int i = 0; i < uris.size(); i++) {
			if (branches.size() <= i) {
				usernames.put(uris.get(i), "");
			} else {
				usernames.put(uris.get(i), branches.get(i));
			}
		}
		return usernames;
	}

	public static String getUsername(String projectKey, String repoUri) {
		return getUsernames(projectKey).get(repoUri);
	}

	public static Map<String, String> getTokens(String projectKey) {
		Map<String, String> tokens = new HashMap<String, String>();
		String value = getValue(projectKey, "gitUris");
		List<String> uris = Arrays.asList(value.split(";;"));
		value = getValue(projectKey, "tokens");
		if (value == null || value.isBlank()) {
			for (String uri : uris) {
				tokens.put(uri, "");
			}
			return tokens;
		}
		List<String> branches = Arrays.asList(value.split(";;"));
		for (int i = 0; i < uris.size(); i++) {
			if (branches.size() <= i) {
				tokens.put(uris.get(i), "");
			} else {
				tokens.put(uris.get(i), branches.get(i));
			}
		}
		return tokens;
	}

	public static String getToken(String projectKey, String repoUri) {
		return getTokens(projectKey).get(repoUri);
	}

	public static void setKnowledgeTypeEnabled(String projectKey, String knowledgeType,
											   boolean isKnowledgeTypeEnabled) {
		setValue(projectKey, knowledgeType, Boolean.toString(isKnowledgeTypeEnabled));
	}

	// TODO Testing
	public static void setUseClassifierForIssueComments(String projectKey, boolean isActivated) {
		setValue(projectKey, "setClassiferForIssueComments", Boolean.toString(isActivated));
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

	public static void setArffFileForClassifier(String projectKey, String arffFileName) {
		setValue(projectKey, "arffFileName", arffFileName);
	}

	public static String getArffFileForClassifier(String projectKey) {
		return getValue(projectKey, "arffFileName");
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

	public static void setRDFKnowledgeSource(String projectKey, RDFSource rdfSource) {
		List<RDFSource> rdfSourceList = new ArrayList<>();
		Type type = new TypeToken<List<RDFSource>>() {
		}.getType();
		if (rdfSource != null) {
			try {
				rdfSourceList = (List<RDFSource>) getSavedObject(projectKey, "rdfsource.list", type);
				if (rdfSourceList == null)
					rdfSourceList = new ArrayList<>();
			} catch (JsonSyntaxException e) {
				rdfSourceList = new ArrayList<>();
				saveObject(projectKey, "rdfsource.list", rdfSourceList, type);
			}

			rdfSource.setActivated(true); // default: activated
			rdfSourceList.add(rdfSource);

			saveObject(projectKey, "rdfsource.list", rdfSourceList, type);

		}

	}

	public static List<RDFSource> getRDFKnowledgeSource(String projectKey) {
		List<RDFSource> rdfSourceList = new ArrayList<>();
		if (projectKey == null)
			return rdfSourceList;

		Type type = new TypeToken<List<RDFSource>>() {
		}.getType();
		try {
			rdfSourceList = (List<RDFSource>) getSavedObject(projectKey, "rdfsource.list", type);
		} catch (JsonSyntaxException e) {
		} finally {
			// TODO Fix: finally block does not complete normally
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

	public static void setAddRecommendationDirectly(String projectKey, Boolean addRecommendationDirectly) {
		setValue(projectKey, "addRecommendationDirectly", addRecommendationDirectly.toString());
	}

	public static boolean getAddRecommendationDirectly(String projectKey) {
		return Boolean.valueOf(getValue(projectKey, "addRecommendationDirectly"));
	}

	public static void setRecommendationInput(String projectKey, String recommendationInput) {
		setValue(projectKey, "recommendationInput", recommendationInput);
	}

	public static RecommenderType getRecommendationInput(String projectKey) {
		String value = getValue(projectKey, "recommendationInput");
		if (!value.isBlank()) return RecommenderType.getTypeByString(value);
		return RecommenderType.getDefault();
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
		DefinitionOfDone definitionOfDone = null;
		try {
			definitionOfDone = (DefinitionOfDone) getSavedObject(projectKey, "definitionOfDone", type);
		} catch (JsonSyntaxException e) {
		}
		if (definitionOfDone == null) {
			definitionOfDone = new DefinitionOfDone();
			setDefinitionOfDone(projectKey, definitionOfDone);
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
