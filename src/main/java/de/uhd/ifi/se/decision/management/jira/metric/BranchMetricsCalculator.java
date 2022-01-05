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

public class BranchMetricsCalculator {

	private Diff branchesForProject;

	public BranchMetricsCalculator(FilterSettings filterSettings) {
		String projectKey = filterSettings.getProjectKey();
		if (ConfigPersistenceManager.getGitConfiguration(projectKey).isActivated()) {
			GitClient gitClient = GitClient.getInstance(projectKey);
			branchesForProject = gitClient.getDiffForFeatureBranchWithName(projectKey);
			branchesForProject.addAll(gitClient.getDiffOfEntireDefaultBranchFromKnowledgeGraph());
		}
	}

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

	@XmlElement
	public Map<Integer, Diff> getNumberOfIssuesMap() {
		return getNumberOfElementsOfTypeMap(KnowledgeType.ISSUE);
	}

	@XmlElement
	public Map<Integer, Diff> getNumberOfDecisionsMap() {
		return getNumberOfElementsOfTypeMap(KnowledgeType.DECISION);
	}

	@XmlElement
	public Map<Integer, Diff> getNumberOfAlternativesMap() {
		return getNumberOfElementsOfTypeMap(KnowledgeType.ALTERNATIVE);
	}

	@XmlElement
	public Map<Integer, Diff> getNumberOfProsMap() {
		return getNumberOfElementsOfTypeMap(KnowledgeType.PRO);
	}

	@XmlElement
	public Map<Integer, Diff> getNumberOfConsMap() {
		return getNumberOfElementsOfTypeMap(KnowledgeType.CON);
	}

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

	public Diff getBranchesForProject() {
		return branchesForProject;
	}
}
