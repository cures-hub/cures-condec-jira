package de.uhd.ifi.se.decision.management.jira.decisionguidance.knowledgesources;

import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.persistence.ConfigPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.view.decisionguidance.Recommendation;
import org.apache.jena.atlas.lib.Pair;
import org.apache.jena.query.*;
import org.apache.jena.sparql.engine.http.Params;
import org.apache.jena.sparql.engine.http.QueryEngineHTTP;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class RDFSource extends KnowledgeSource {

	protected String service;
	protected String queryString;
	protected String timeout;
	protected int limit;

	public RDFSource() {

	}

	/**
	 * @param projectKey
	 */
	public RDFSource(String projectKey) {
		this.projectKey = projectKey;
		this.service = "http://dbpedia.org/sparql";
		this.queryString = "PREFIX dbo: <http://dbpedia.org/ontology/> " +
			"PREFIX dbr: <http://dbpedia.org/resource/> SELECT ?alternative ?url WHERE { ?alternative a dbo:Country.	?url dbo:capital ?alternative }";
		this.name = "DBPedia";
		this.timeout = "30000";
		this.isActivated = true;
	}

	/**
	 * @param projectKey
	 * @param service
	 * @param queryString
	 * @param name
	 * @param timeout
	 */
	public RDFSource(String projectKey, String service, String queryString, String name, String timeout) {
		this.projectKey = projectKey;
		this.service = service;
		this.queryString = queryString;
		this.name = name;
		this.timeout = timeout;
		this.isActivated = true;
	}

	/**
	 * @param queryString
	 * @param service
	 * @param params
	 * @return
	 */
	protected ResultSet queryDatabase(String queryString, String service, Pair<String, String>... params) {
		try {
			Query query = QueryFactory.create(queryString);

			// Remote execution.
			QueryExecution queryExecution = QueryExecutionFactory.sparqlService(service, query);
			// Add Paramaters
			for (Pair<String, String> parameter : params) {
				((QueryEngineHTTP) queryExecution).addParam(parameter.getLeft(), parameter.getRight());
			}

			// Execute.
			ResultSet resultSet = queryExecution.execSelect();

			return resultSet;
		} catch (QueryBuildException e) {
			e.printStackTrace();
		} catch (QueryParseException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public List<Recommendation> getResults(String inputs) {

		this.recommendations = new ArrayList<>();
		if (!this.isActivated) return this.recommendations;


		if (inputs == null) inputs = "";


		List<String> keywords = Arrays.asList(inputs.trim().split(" "));
		List<String> combinedKeywords = this.combineKeywords(keywords);


		for (String combinedKeyword : combinedKeywords) {

			String uri = "<http://dbpedia.org/resource/" + combinedKeyword + ">";
			String queryStringWithInput = this.queryString.replaceAll("%variable%", uri).replaceAll("\\r|\\n", " ");
			queryStringWithInput = String.format("%s LIMIT %d", queryStringWithInput, this.getLimit());


			ResultSet resultSet = this.queryDatabase(queryStringWithInput, this.service, Params.Pair.create("timeout", this.timeout));


			while (resultSet != null && resultSet.hasNext()) {
				QuerySolution row = resultSet.nextSolution();
				int score = this.calculateScore(combinedKeyword, inputs);
				Recommendation recommendation = new Recommendation(this.name, row.get("?alternative").toString(), row.get("?url").toString());
				recommendation.setScore(score);
				this.recommendations.add(recommendation);

			}

		}
		return this.recommendations;
	}

	@Override
	public List<Recommendation> getResults(KnowledgeElement knowledgeElement) {
		if (knowledgeElement != null)
			return this.getResults(knowledgeElement.getSummary());
		else return new ArrayList<>();
	}

	private List<String> combineKeywords(List<String> keywords) {

		List<String> combinedKeywords = new ArrayList<>();
		combinedKeywords.addAll(keywords);

		StringBuilder stringBuilder = new StringBuilder();

		for (String first : keywords) {
			stringBuilder.append(first);
			for (String second : keywords) {
				if (!first.equals(second)) {
					stringBuilder.append("_").append(second);
					combinedKeywords.add(stringBuilder.toString());
				}
			}

			stringBuilder.setLength(0);
			break;
		}

		return combinedKeywords;
	}

	private int calculateScore(String keywords, String inputs) {

		List<String> keywordsList = Arrays.asList(keywords.split("_"));
		List<String> inputsList = Arrays.asList(inputs.split(" "));

		float inputLength = inputsList.size();
		int match = 0;

		for (String keyword : keywordsList) {
			if (inputs.contains(keyword)) match += 1;
		}

		float score = (match / inputLength) * 100;

		return Math.round(score);

	}

	public String getProjectKey() {
		return projectKey;
	}

	public void setProjectKey(String projectKey) {
		this.projectKey = projectKey;
	}

	public String getService() {
		return service;
	}

	public void setService(String service) {
		this.service = service;
	}

	public String getQueryString() {
		return queryString;
	}

	public void setQueryString(String queryString) {
		this.queryString = queryString;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	public String getTimeout() {
		return timeout;
	}

	public void setTimeout(String timeout) {
		this.timeout = timeout;
	}

	@Override
	public boolean isActivated() {
		return isActivated;
	}

	@Override
	public void setActivated(boolean activated) {
		isActivated = activated;
	}

	public int getLimit() {
		return ConfigPersistenceManager.getMaxNumberRecommendations(this.projectKey);
	}

	@Override
	public String toString() {
		return this.name.replace(' ', '-');
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		RDFSource rdfSource = (RDFSource) o;
		return name.equals(rdfSource.name);
	}

	@Override
	public int hashCode() {
		return Objects.hash(name);
	}
}
