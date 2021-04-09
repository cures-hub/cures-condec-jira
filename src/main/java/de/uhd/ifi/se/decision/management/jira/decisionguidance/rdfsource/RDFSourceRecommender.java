package de.uhd.ifi.se.decision.management.jira.decisionguidance.rdfsource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.sparql.engine.http.QueryEngineHTTP;

import de.uhd.ifi.se.decision.management.jira.decisionguidance.Recommendation;
import de.uhd.ifi.se.decision.management.jira.decisionguidance.RecommendationScore;
import de.uhd.ifi.se.decision.management.jira.decisionguidance.Recommender;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.persistence.ConfigPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.view.decisiontable.Argument;

/**
 * Queries an RDF knowledge source (e.g. DBPedia) for a given String (for
 * keyword-based search).
 * 
 * For example, a decision problem can be input by the user and used to query
 * DBPedia.
 */
public class RDFSourceRecommender extends Recommender<RDFSource> {

	public RDFSourceRecommender(String projectKey, RDFSource rdfSource) {
		super(projectKey, rdfSource);
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
	 *            in SPARQL language (SPARQL Protocol And RDF Query Language).
	 * @return
	 */
	protected ResultSet queryDatabase(String queryString) {
		try {
			Query query = QueryFactory.create(queryString);
			QueryExecution queryExecution = QueryExecutionFactory.sparqlService(knowledgeSource.getService(), query);
			((QueryEngineHTTP) queryExecution).addParam("timeout", knowledgeSource.getTimeout() + "");

			ResultSet resultSet = queryExecution.execSelect();
			return resultSet;
		} catch (Exception e) {
		}
		return null;
	}

	@Override
	public List<Recommendation> getRecommendations(String inputs) {
		if (inputs == null) {
			return new ArrayList<>();
		}
		List<Recommendation> recommendations = new ArrayList<>();
		Map<Recommendation, Integer> scoreMap = new HashMap<>();

		final List<String> keywords = Arrays.asList(inputs.trim().split(" "));
		final List<String> combinedKeywords = this.combineKeywords(keywords);

		for (String combinedKeyword : combinedKeywords) {

			final String uri = "<http://dbpedia.org/resource/" + combinedKeyword + ">";
			String queryStringWithInput = knowledgeSource.getQuery().replaceAll("%variable%", uri)
					.replaceAll("[\\r\\n\\t]", " ");
			queryStringWithInput = String.format("%s LIMIT %d", queryStringWithInput, this.getLimit());

			ResultSet resultSet = queryDatabase(queryStringWithInput);

			while (resultSet != null && resultSet.hasNext()) {
				QuerySolution row = resultSet.nextSolution();

				String label = this.getLabel(row.get("?subject").toString());

				Recommendation recommendation = new Recommendation(knowledgeSource, label, row.get("?url").toString());

				Literal aggregatedNumberOfLinks = row.get("?callret-2").asLiteral();
				int numberOfLinks = aggregatedNumberOfLinks.getInt();

				scoreMap.put(recommendation, numberOfLinks);

				List<Argument> arguments = new ArrayList<>();

				for (Map.Entry<String, String> constraint : knowledgeSource.getConstraintMap().entrySet()) {
					arguments.addAll(getArgument(row.get("?subject").toString(), constraint));
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
				RDFSource.PREFIX
						+ " select distinct ?label where { <%s> rdfs:label ?label.  FILTER(LANG(?label) = 'en'). }",
				resource);

		ResultSet arguments = this.queryDatabase(query);

		String label = "";
		while (arguments != null && arguments.hasNext()) {
			QuerySolution row = arguments.nextSolution();
			label = row.get("?label").toString();
		}
		return label;
	}

	private List<Argument> getArgument(String resource, Map.Entry<String, String> constraint) {

		String query = String.format(RDFSource.PREFIX
				+ " select distinct ?argument where { <%s> %s ?subject. ?subject rdfs:label ?argument. FILTER(LANG(?argument) = 'en').}",
				resource, constraint.getValue());

		ResultSet arguments = queryDatabase(query);

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
		recommendationScore.addSubScore(new RecommendationScore(maxValue, "Recommendation with most links"));
		recommendationScore.addSubScore(new RecommendationScore(actualValue, "This Recommendation number of links"));
		float totalScore = (actualValue * 1.0f / maxValue) * 100f;
		recommendationScore.setValue(totalScore);
		return recommendationScore;
	}

	private int getLimit() {
		return ConfigPersistenceManager.getDecisionGuidanceConfiguration(projectKey).getMaxNumberOfRecommendations();
	}
}
