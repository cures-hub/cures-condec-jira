package de.uhd.ifi.se.decision.management.jira.decisionguidance.knowledgesources;

import de.uhd.ifi.se.decision.management.jira.model.DocumentationLocation;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeStatus;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.sparql.engine.http.Params;

import java.util.ArrayList;
import java.util.List;

public class DBPediaSource extends RDFSource {

	private List<KnowledgeElement> recommendations;


	public DBPediaSource(String projectKey) {
		super(projectKey);
	}

	@Override
	public List<KnowledgeElement> getResults(String inputs) {

		ResultSet resultSet = this.queryDatabase(this.queryString, this.service, Params.Pair.create("timeout", this.timeout));

		this.recommendations = new ArrayList<>();
		while (resultSet.hasNext()) {
			QuerySolution row = resultSet.nextSolution();
			KnowledgeElement alternative = new KnowledgeElement(10L, row.get("?country").toString(), "blabla", KnowledgeType.ALTERNATIVE, this.projectKey, "KEY", DocumentationLocation.JIRAISSUETEXT, KnowledgeStatus.IDEA);
			this.recommendations.add(alternative);

		}
		return this.recommendations;
	}

}
