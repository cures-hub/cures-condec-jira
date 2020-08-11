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

	private String projectKey;
	private List<KnowledgeElement> recommendations;
	private static final String SERIVCE = "http://dbpedia.org/sparql";
	private static final String PREFIX = "PREFIX dbo: <http://dbpedia.org/ontology/>" +
		"PREFIX dbr: <http://dbpedia.org/resource/>";
	private static final String QUERYSTRING = PREFIX +
		"SELECT ?country ?capital WHERE { ?country a dbo:Country.	?country dbo:capital ?capital } LIMIT 10";

	@Override
	public List<KnowledgeElement> getResults(String inputs) {

		ResultSet resultSet = this.queryDatabase(this.QUERYSTRING, this.SERIVCE, Params.Pair.create("timeout", "10000"));

		this.recommendations = new ArrayList<>();
		while (resultSet.hasNext()) {
			QuerySolution row = resultSet.nextSolution();
			KnowledgeElement alternative = new KnowledgeElement(10L, row.get("?country").toString(), "blabla", KnowledgeType.ALTERNATIVE, projectKey, "KEY", DocumentationLocation.JIRAISSUETEXT, KnowledgeStatus.IDEA);
			this.recommendations.add(alternative);

		}
		return this.recommendations;
	}

}
