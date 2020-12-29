package de.uhd.ifi.se.decision.management.jira.decisionguidance.knowledgesources.projectsource;

import de.uhd.ifi.se.decision.management.jira.decisionguidance.knowledgesources.InputMethod;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeGraph;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.persistence.KnowledgePersistenceManager;
import de.uhd.ifi.se.decision.management.jira.view.decisionguidance.Recommendation;

import java.util.ArrayList;
import java.util.List;

public abstract class ProjectSourceInput<T> implements InputMethod<T, ProjectSource> {

	protected String projectKey;
	protected String name;
	protected KnowledgeGraph knowledgeGraph;
	protected List<KnowledgeElement> knowledgeElements;
	protected ProjectSource knowledgeSource;


	public abstract List<Recommendation> getResults(T input);

	public void queryDatabase() {
		knowledgeGraph = knowledgeGraph.getOrCreate(this.name);
		if (this.knowledgeGraph != null)
			knowledgeElements = this.knowledgeGraph.getElements(KnowledgeType.ISSUE);
		else new ArrayList<>();
	}

	@Override
	public void setData(ProjectSource knowledgeSource) {
		this.knowledgeSource = knowledgeSource;
		this.projectKey = this.knowledgeSource.getProjectKey();
		this.name = this.knowledgeSource.getName();
	}
}
