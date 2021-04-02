package de.uhd.ifi.se.decision.management.jira.decisionguidance;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.codehaus.jackson.annotate.JsonProperty;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.project.Project;

import de.uhd.ifi.se.decision.management.jira.decisionguidance.knowledgesources.KnowledgeSource;
import de.uhd.ifi.se.decision.management.jira.decisionguidance.knowledgesources.projectsource.ProjectSource;
import de.uhd.ifi.se.decision.management.jira.decisionguidance.knowledgesources.rdfsource.RDFSource;
import de.uhd.ifi.se.decision.management.jira.decisionguidance.recommender.RecommenderType;
import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeProject;

public class DecisionGuidanceConfiguration {

	private boolean isRecommendationAddedToKnowledgeGraph;
	private int maxNumberOfRecommendations;
	private double similarityThreshold;
	private String irrelevantWords;
	private List<RDFSource> rdfKnowledgeSources;
	private List<ProjectSource> projectKnowledgeSources;
	private Set<RecommenderType> inputTypes;

	public DecisionGuidanceConfiguration() {
		this.setRecommendationAddedToKnowledgeGraph(false);
		this.setMaxNumberOfRecommendations(100);
		this.setSimilarityThreshold(0.85);
		this.setIrrelevantWords("");
		this.rdfKnowledgeSources = new ArrayList<>();
		this.projectKnowledgeSources = new ArrayList<>();
		this.inputTypes = new HashSet<>();
	}

	public boolean isRecommendationAddedToKnowledgeGraph() {
		return isRecommendationAddedToKnowledgeGraph;
	}

	@JsonProperty
	public void setRecommendationAddedToKnowledgeGraph(boolean isRecommendationAddedToKnowledgeGraph) {
		this.isRecommendationAddedToKnowledgeGraph = isRecommendationAddedToKnowledgeGraph;
	}

	public int getMaxNumberOfRecommendations() {
		return maxNumberOfRecommendations;
	}

	@JsonProperty
	public void setMaxNumberOfRecommendations(int maxNumberOfRecommendations) {
		this.maxNumberOfRecommendations = maxNumberOfRecommendations;
	}

	public double getSimilarityThreshold() {
		return similarityThreshold;
	}

	@JsonProperty
	public void setSimilarityThreshold(double similarityThreshold) {
		this.similarityThreshold = similarityThreshold;
	}

	public String getIrrelevantWords() {
		return irrelevantWords;
	}

	@JsonProperty
	public void setIrrelevantWords(String irrelevantWords) {
		this.irrelevantWords = irrelevantWords;
	}

	public List<RDFSource> getRdfKnowledgeSources() {
		return rdfKnowledgeSources;
	}

	@JsonProperty
	public void setRdfKnowledgeSources(List<RDFSource> rdfKnowledgeSources) {
		this.rdfKnowledgeSources = rdfKnowledgeSources;
	}

	public void addRdfKnowledgeSource(RDFSource rdfSource) {
		if (rdfSource == null) {
			return;
		}
		rdfSource.setActivated(true); // default: activated
		rdfKnowledgeSources.add(rdfSource);
	}

	public void deleteKnowledgeSource(String knowledgeSourceName) {
		rdfKnowledgeSources.removeIf(rdfSource -> knowledgeSourceName.equals(rdfSource.getName()));
	}

	public void updateKnowledgeSource(String knowledgeSourceName, RDFSource rdfSource) {
		for (int i = 0; i < rdfKnowledgeSources.size(); ++i) {
			if (rdfKnowledgeSources.get(i).getName().equals(knowledgeSourceName)) {
				rdfKnowledgeSources.set(i, rdfSource);
				break;
			}
		}
	}

	public void setRdfKnowledgeSourceActivation(String rdfSourceName, boolean isActivated) {
		for (int i = 0; i < rdfKnowledgeSources.size(); ++i) {
			if (rdfSourceName.equals(rdfKnowledgeSources.get(i).getName())) {
				rdfKnowledgeSources.get(i).setActivated(isActivated);
				break;
			}
		}
	}

	public List<ProjectSource> getProjectKnowledgeSources() {
		projectKnowledgeSources.removeIf(projectSource -> !projectSource.isActivated());
		return projectKnowledgeSources;
	}

	public List<ProjectSource> getProjectSourcesForActiveProjects() {
		List<ProjectSource> projectSources = new ArrayList<>();
		for (Project project : ComponentAccessor.getProjectManager().getProjects()) {
			DecisionKnowledgeProject jiraProject = new DecisionKnowledgeProject(project);
			if (!jiraProject.isActivated()) {
				continue;
			}
			ProjectSource projectSource = getProjectSource(jiraProject.getProjectKey());
			if (projectSource == null) {
				projectSource = new ProjectSource(jiraProject.getProjectKey(), jiraProject.getProjectName(), false);
			}
			projectSources.add(projectSource);
		}
		return projectSources;
	}

	@JsonProperty
	public void setProjectKnowledgeSources(List<ProjectSource> projectKnowledgeSources) {
		this.projectKnowledgeSources = projectKnowledgeSources;
	}

	public ProjectSource getProjectSource(String projectSourceKey) {
		for (ProjectSource projectSource : projectKnowledgeSources) {
			if (projectSource.getProjectKey().equals(projectSourceKey)) {
				return projectSource;
			}
		}
		return null;
	}

	public void setProjectSource(String projectSourceKey, boolean isActivated) {
		for (ProjectSource projectSource : projectKnowledgeSources) {
			if (projectSource.getProjectKey().equals(projectSourceKey)) {
				projectSource.setActivated(isActivated);
				return;
			}
		}
		projectKnowledgeSources.add(new ProjectSource(projectSourceKey, projectSourceKey, isActivated));
	}

	public Set<RecommenderType> getInputTypes() {
		return inputTypes;
	}

	@JsonProperty
	public void setInputTypes(Set<RecommenderType> inputTypes) {
		this.inputTypes = inputTypes;
	}

	public void setRecommendationInput(String recommendationInput, boolean isActivated) {
		RecommenderType type = RecommenderType.valueOf(recommendationInput);
		if (isActivated) {
			this.inputTypes.add(type);
		} else {
			this.inputTypes.remove(type);
		}
	}

	public List<KnowledgeSource> getAllKnowledgeSources() {
		List<KnowledgeSource> knowledgeSources = new ArrayList<>();

		knowledgeSources.addAll(rdfKnowledgeSources);
		knowledgeSources.addAll(projectKnowledgeSources);
		// New KnowledgeSources could be added here.
		return knowledgeSources;
	}

	public List<KnowledgeSource> getAllActivatedKnowledgeSources() {
		List<KnowledgeSource> knowledgeSources = getAllKnowledgeSources();
		knowledgeSources.removeIf(knowledgeSource -> !knowledgeSource.isActivated());
		return knowledgeSources;
	}

}
