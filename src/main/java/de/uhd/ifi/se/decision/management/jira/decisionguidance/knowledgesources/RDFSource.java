package de.uhd.ifi.se.decision.management.jira.decisionguidance.knowledgesources;

import de.uhd.ifi.se.decision.management.jira.decisionguidance.resultmethods.InputMethod;
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
			"PREFIX dbr: <http://dbpedia.org/resource/> SELECT ?alternative ?url WHERE { ?alternative a dbo:Country." +
			"?url dbo:capital ?alternative }";
		this.name = "DBPedia";
		this.timeout = "30000";
		this.limit = 10;
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
		this.limit = 10;
	}

	@Override
	public void setData() {

	}


	@Override
	public InputMethod getInputMethod() {
		this.inputMethod = new InputMethod<String>() {

			protected String name;
			protected String service;
			protected String queryString;
			protected String timeout;
			protected int limit;

			public InputMethod setData(String name, String service, String queryName, String timeout, int limit) {
				this.name = name;
				this.service = service;
				this.queryString = queryName;
				this.timeout = timeout;
				this.limit = limit;
				return this;
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

				} catch (QueryParseException e) {

				}
				return null;
			}

			@Override
			public List<Recommendation> getResults(String inputs) {
				List<Recommendation> recommendations = new ArrayList<>();

				if (inputs == null) return recommendations;


				List<String> keywords = Arrays.asList(inputs.trim().split(" "));
				List<String> combinedKeywords = this.combineKeywords(keywords);


				for (String combinedKeyword : combinedKeywords) {

					String uri = "<http://dbpedia.org/resource/" + combinedKeyword + ">";
					String queryStringWithInput = this.queryString.replaceAll("%variable%", uri).replaceAll("\\r|\\n", " ");
					queryStringWithInput = String.format("%s LIMIT %d", queryStringWithInput, this.limit);


					ResultSet resultSet = this.queryDatabase(queryStringWithInput, this.service, Params.Pair.create("timeout", this.timeout));


					while (resultSet != null && resultSet.hasNext()) {
						QuerySolution row = resultSet.nextSolution();
						int score = this.calculateScore(combinedKeyword, inputs);
						Recommendation recommendation = new Recommendation(this.name, row.get("?alternative").toString(), row.get("?url").toString());
						recommendation.setScore(score);
						recommendations.add(recommendation);

					}

				}
				return recommendations;
			}
		}.setData(this.name, this.service, this.queryString, this.timeout, this.limit);

		return this.inputMethod;
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

	public String getTimeout() {
		return timeout;
	}

	public void setTimeout(String timeout) {
		this.timeout = timeout;
	}

	public int getLimit() {
		return ConfigPersistenceManager.getMaxNumberRecommendations(this.projectKey);
	}

	public void setLimit(int limit) {
		this.limit = limit;
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
