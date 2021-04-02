package de.uhd.ifi.se.decision.management.jira.decisionguidance.knowledgesources.rdfsource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.jena.atlas.lib.Pair;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.sparql.engine.http.Params;
import org.apache.jena.sparql.engine.http.QueryEngineHTTP;

import com.google.common.base.Splitter;

import de.uhd.ifi.se.decision.management.jira.decisionguidance.knowledgesources.InputMethod;
import de.uhd.ifi.se.decision.management.jira.decisionguidance.score.RecommendationScore;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.persistence.ConfigPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.view.decisionguidance.Recommendation;
import de.uhd.ifi.se.decision.management.jira.view.decisiontable.Argument;

/**
 * Queries an RDF knowledge source (e.g. DBPedia) for a given String (for
 * keyword-based search).
 * 
 * For example, a decision problem can be input by the user and used to query
 * DBPedia.
 */
public class RDFSourceInputString implements InputMethod<String, RDFSource> {

	protected RDFSource knowledgeSource;
	protected String projectKey;
	protected String name;
	protected String service;
	protected String queryString;
	protected String timeout;
	protected int limit;
	protected Map<String, String> constraints;

	private static final String PREFIX = "PREFIX dbo: <http://dbpedia.org/ontology/>"
			+ "PREFIX dct: <http://purl.org/dc/terms/>" + "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>"
			+ "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>" + "PREFIX foaf: <http://xmlns.com/foaf/0.1/>";

	@Override
	public void setKnowledgeSource(RDFSource knowledgeSource) {
		this.knowledgeSource = knowledgeSource;
		this.projectKey = knowledgeSource.getProjectKey();
		this.name = knowledgeSource.getName();
		this.service = knowledgeSource.getService();
		this.queryString = knowledgeSource.getQueryString();
		this.timeout = knowledgeSource.getTimeout();
		this.limit = knowledgeSource.getLimit();
		try {
			this.constraints = Splitter.on("&").withKeyValueSeparator("=").split(knowledgeSource.getConstraint());
		} catch (IllegalArgumentException e) {
			this.constraints = new HashMap<>();
		}
	}

	private List<String> combineKeywords(List<String> keywords) {

		List<String> combinedKeywords = new ArrayList<>();
		combinedKeywords.addAll(keywords);

		StringBuilder stringBuilder = new StringBuilder();
		String first = keywords.get(0);

		stringBuilder.append(first);
		for (String second : keywords) {
			if (!first.equals(second)) {
				stringBuilder.append("_").append(second);
				combinedKeywords.add(stringBuilder.toString());
			}
		}

		stringBuilder.setLength(0);

		return combinedKeywords;
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
		} catch (Exception e) {
		}
		return null;
	}

	@Override
	public List<Recommendation> getRecommendations(String inputs) {
		List<Recommendation> recommendations = new ArrayList<>();
		Map<Recommendation, Integer> scoreMap = new HashMap<>();

		if (inputs == null)
			return recommendations;

		final List<String> keywords = Arrays.asList(inputs.trim().split(" "));
		final List<String> combinedKeywords = this.combineKeywords(keywords);

		for (String combinedKeyword : combinedKeywords) {

			final String uri = "<http://dbpedia.org/resource/" + combinedKeyword + ">";
			String queryStringWithInput = this.queryString.replaceAll("%variable%", uri).replaceAll("[\\r\\n\\t]", " ");
			queryStringWithInput = String.format("%s LIMIT %d", queryStringWithInput, this.getLimit());

			ResultSet resultSet = queryDatabase(queryStringWithInput, service, new Params.Pair("timeout", timeout));

			while (resultSet != null && resultSet.hasNext()) {
				QuerySolution row = resultSet.nextSolution();

				String label = this.getLabel(row.get("?subject").toString());

				Recommendation recommendation = new Recommendation(this.knowledgeSource, label,
						row.get("?url").toString());

				Literal aggregatedNumberOfLinks = row.get("?callret-2").asLiteral();
				int numberOfLinks = aggregatedNumberOfLinks.getInt();

				scoreMap.put(recommendation, numberOfLinks);

				List<Argument> arguments = new ArrayList<>();

				for (Map.Entry<String, String> constraint : this.constraints.entrySet()) {
					arguments.addAll(this.getArgument(row.get("?subject").toString(), constraint));
				}

				recommendation.addArguments(arguments);
				recommendations.add(recommendation);
			}
		}

		return this.getRecommendationWithScore(scoreMap);
	}

	private List<Recommendation> getRecommendationWithScore(Map<Recommendation, Integer> scoreMap) {
		List<Recommendation> recommendationWithScore = new ArrayList<>();
		if (scoreMap.size() != 0) {
			Comparator<? super Map.Entry<Recommendation, Integer>> maxValueComparator = Comparator
					.comparing(Map.Entry::getValue);

			int maxValue = scoreMap.entrySet().stream().max(maxValueComparator).get().getValue();

			scoreMap.forEach((recommendation, value) -> {
				recommendation.setScore(this.getScore(maxValue, value));
				recommendationWithScore.add(recommendation);
			});
		}
		return recommendationWithScore;
	}

	private String getLabel(String resource) {

		String query = String.format(
				PREFIX + " select distinct ?label where { <%s> rdfs:label ?label.  FILTER(LANG(?label) = 'en'). }",
				resource);

		ResultSet arguments = this.queryDatabase(query, service, new Params.Pair("timeout", this.timeout));

		String label = "";
		while (arguments != null && arguments.hasNext()) {
			QuerySolution row = arguments.nextSolution();
			label = row.get("?label").toString();
		}
		return label;
	}

	private List<Argument> getArgument(String resource, Map.Entry<String, String> constraint) {

		String query = String.format(PREFIX
				+ " select distinct ?argument where { <%s> %s ?subject. ?subject rdfs:label ?argument. FILTER(LANG(?argument) = 'en').}",
				resource, constraint.getValue());

		ResultSet arguments = queryDatabase(query, service, new Params.Pair("timeout", this.timeout));

		List<Argument> argumentsList = new ArrayList<>();

		while (arguments != null && arguments.hasNext()) {
			QuerySolution row = arguments.nextSolution();
			String argumentSummary = constraint.getKey() + " : " + row.get("?argument").toString();
			KnowledgeElement knowledgeElement = new KnowledgeElement();
			knowledgeElement.setType(KnowledgeType.PRO);
			knowledgeElement.setDocumentationLocation("s");
			knowledgeElement.setSummary(argumentSummary);
			argumentsList.add(new Argument(knowledgeElement));

		}
		return argumentsList;
	}

	private RecommendationScore getScore(int maxValue, int actualValue) {
		RecommendationScore recommendationScore = new RecommendationScore(0.0f, "Recommendation with most links");
		recommendationScore.composeScore(new RecommendationScore(maxValue, "Recommendation with most links"));
		recommendationScore.composeScore(new RecommendationScore(actualValue, "This Recommendation number of links"));
		float totalScore = (actualValue * 1.0f / maxValue) * 100f;
		recommendationScore.setTotalScore(totalScore);
		return recommendationScore;
	}

	private int getLimit() {
		return ConfigPersistenceManager.getDecisionGuidanceConfiguration(projectKey).getMaxNumberOfRecommendations();
	}
}
