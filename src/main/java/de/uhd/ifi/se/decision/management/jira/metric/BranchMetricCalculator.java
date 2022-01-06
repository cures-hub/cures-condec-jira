package de.uhd.ifi.se.decision.management.jira.metric;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.xml.bind.annotation.XmlElement;

import de.uhd.ifi.se.decision.management.jira.filtering.FilterSettings;
import de.uhd.ifi.se.decision.management.jira.git.GitClient;
import de.uhd.ifi.se.decision.management.jira.git.model.Diff;
import de.uhd.ifi.se.decision.management.jira.git.model.DiffForSingleRef;
import de.uhd.ifi.se.decision.management.jira.git.parser.JiraIssueKeyFromCommitMessageParser;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.persistence.ConfigPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.quality.QualityProblem;
import de.uhd.ifi.se.decision.management.jira.quality.QualityProblemType;

/**
 * Calculates decision knowledge-related and general metrics on the git branches
 * of a Jira project. Branches are represented as a {@link Diff} object.
 * 
 * @see #getBranchStatusMap()
 * @see #getQualityProblemMap()
 * @see #getNumberOfDecisionsMap()
 * @see #getNumberOfIssuesMap()
 * @see #getNumberOfAlternativesMap()
 * @see #getNumberOfProsMap()
 * @see #getNumberOfConsMap()
 * @see #getJiraIssueMap()
 * 
 * @see GitClient
 * @see Diff
 */
public class BranchMetricCalculator {

	private Diff branchesForProject;

	public BranchMetricCalculator(FilterSettings filterSettings) {
		String projectKey = filterSettings.getProjectKey();
		if (ConfigPersistenceManager.getGitConfiguration(projectKey).isActivated()) {
			GitClient gitClient = GitClient.getInstance(projectKey);
			branchesForProject = gitClient.getDiffForFeatureBranchWithName(projectKey);
			branchesForProject.addAll(gitClient.getDiffOfEntireDefaultBranchFromKnowledgeGraph());
		}
	}

	/**
	 * @return map with three keys "Incorrect", "Good", and "No Rationale" and the
	 *         respective branches as map values. Branches are represented as a
	 *         {@link Diff} object.
	 */
	@XmlElement
	public Map<String, Diff> getBranchStatusMap() {
		Diff incorrectBranches = new Diff();
		Diff correctBranches = new Diff();
		Diff branchesWithoutRationale = new Diff();
		for (DiffForSingleRef branch : branchesForProject) {
			if (branch.getDecisionKnowledgeElements().isEmpty()) {
				branchesWithoutRationale.add(branch);
			} else if (branch.getQualityProblems().isEmpty()) {
				correctBranches.add(branch);
			} else {
				incorrectBranches.add(branch);
			}
		}
		Map<String, Diff> branchStatusMap = new LinkedHashMap<>();
		branchStatusMap.put("Incorrect", incorrectBranches);
		branchStatusMap.put("Good", correctBranches);
		branchStatusMap.put("No Rationale", branchesWithoutRationale);
		return branchStatusMap;
	}

	/**
	 * @return map with {@link QualityProblemType}s as keys and the respective
	 *         branches as map values. Branches are represented as a {@link Diff}
	 *         object.
	 */
	@XmlElement
	public Map<String, Diff> getQualityProblemMap() {
		Map<String, Diff> qualityProblemMap = new LinkedHashMap<>();
		for (DiffForSingleRef branch : branchesForProject) {
			for (QualityProblem problem : branch.getQualityProblems()) {
				if (!qualityProblemMap.containsKey(problem.getExplanation())) {
					qualityProblemMap.put(problem.getExplanation(), new Diff());
				}
				qualityProblemMap.get(problem.getExplanation()).add(branch);
			}
		}
		return qualityProblemMap;
	}

	/**
	 * @return map with number of issues as keys and the respective branches as map
	 *         values. Branches are represented as a {@link Diff} object.
	 */
	@XmlElement
	public Map<Integer, Diff> getNumberOfIssuesMap() {
		return getNumberOfElementsOfTypeMap(KnowledgeType.ISSUE);
	}

	/**
	 * @return map with number of decisions as keys and the respective branches as
	 *         map values. Branches are represented as a {@link Diff} object.
	 */
	@XmlElement
	public Map<Integer, Diff> getNumberOfDecisionsMap() {
		return getNumberOfElementsOfTypeMap(KnowledgeType.DECISION);
	}

	/**
	 * @return map with number of alternatives as keys and the respective branches
	 *         as map values. Branches are represented as a {@link Diff} object.
	 */
	@XmlElement
	public Map<Integer, Diff> getNumberOfAlternativesMap() {
		return getNumberOfElementsOfTypeMap(KnowledgeType.ALTERNATIVE);
	}

	/**
	 * @return map with number of pro-arguments as keys and the respective branches
	 *         as map values. Branches are represented as a {@link Diff} object.
	 */
	@XmlElement
	public Map<Integer, Diff> getNumberOfProsMap() {
		return getNumberOfElementsOfTypeMap(KnowledgeType.PRO);
	}

	/**
	 * @return map with number of con-arguments as keys and the respective branches
	 *         as map values. Branches are represented as a {@link Diff} object.
	 */
	@XmlElement
	public Map<Integer, Diff> getNumberOfConsMap() {
		return getNumberOfElementsOfTypeMap(KnowledgeType.CON);
	}

	/**
	 * @return map with number of decision knowledge elements of a specific
	 *         {@link KnowledgeType} as keys and the respective branches as map
	 *         values. Branches are represented as a {@link Diff} object.
	 */
	private Map<Integer, Diff> getNumberOfElementsOfTypeMap(KnowledgeType type) {
		Map<Integer, Diff> numberOfElementsOfTypeMap = new LinkedHashMap<>();
		for (DiffForSingleRef branch : branchesForProject) {
			int numberOfIssues = branch.getDecisionKnowledgeElementsOfType(type).size();
			if (!numberOfElementsOfTypeMap.containsKey(numberOfIssues)) {
				numberOfElementsOfTypeMap.put(numberOfIssues, new Diff());
			}
			numberOfElementsOfTypeMap.get(numberOfIssues).add(branch);
		}
		return numberOfElementsOfTypeMap;
	}

	/**
	 * @return map with Jira issue keys as keys and the respective branches that
	 *         reference the Jira issue in the branch name as map values. Branches
	 *         are represented as a {@link Diff} object.
	 */
	@XmlElement
	public Map<String, Diff> getJiraIssueMap() {
		Map<String, Diff> qualityProblemMap = new LinkedHashMap<>();
		for (DiffForSingleRef branch : branchesForProject) {
			for (String key : JiraIssueKeyFromCommitMessageParser.getJiraIssueKeys(branch.getName())) {
				if (!qualityProblemMap.containsKey(key)) {
					qualityProblemMap.put(key, new Diff());
				}
				qualityProblemMap.get(key).add(branch);
			}
		}
		return qualityProblemMap;
	}

	/**
	 * @return all branches of a Jira project as a {@link Diff} object.
	 */
	public Diff getBranchesForProject() {
		return branchesForProject;
	}
}
