package de.uhd.ifi.se.decision.management.jira.recommendation.decisionguidance.rdfsource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Literal;

import de.uhd.ifi.se.decision.management.jira.model.Argument;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.persistence.ConfigPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.recommendation.Recommendation;
import de.uhd.ifi.se.decision.management.jira.recommendation.RecommendationScore;
import de.uhd.ifi.se.decision.management.jira.recommendation.decisionguidance.ElementRecommendation;
import de.uhd.ifi.se.decision.management.jira.recommendation.decisionguidance.Recommender;

/**
 * Queries an {@link RDFSource} (e.g. DBpedia) to generate
 * {@link ElementRecommendation}s.
 */
public class RDFSourceRecommender extends Recommender<RDFSource> {

	/**
	 * @param projectKey
	 *            of the current project (not of the external knowledge source).
	 * @param rdfSource
	 *            {@link RDFSource} instance.
	 */
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
	 * @return {@link ResultSet} for the query.
	 */
	protected ResultSet queryDatabase(String queryString) {
		try {
			Query query = QueryFactory.create(queryString);
			QueryExecution queryExecution = QueryExecution.service(knowledgeSource.getService()).query(query)
					.timeout(knowledgeSource.getTimeout(), TimeUnit.MILLISECONDS).build();

			ResultSet resultSet = queryExecution.execSelect();
			return resultSet;
		} catch (Exception e) {
		}
		return null;
	}

	@Override
	public List<ElementRecommendation> getRecommendations(String inputs) {
		if (inputs == null) {
			return new ArrayList<>();
		}
		// TODO: Remove as soon as the decision guidance has been improved such that it is normal to get recommendations
		if (inputs.contains("get_dummy_decision_guidance_recommendations")) {
			List<ElementRecommendation> recommendations = new ArrayList<>();
			for (int i = 0; i < 10; i++) {
				recommendations.add(new ElementRecommendation("Dummy recommendation No. " +
					String.valueOf(i), knowledgeSource, "wikipedia.org"));
			}
			return recommendations;
		}
		List<ElementRecommendation> recommendations = new ArrayList<>();

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

				ElementRecommendation recommendation = new ElementRecommendation(label, knowledgeSource,
						row.get("?url").toString());

				Literal aggregatedNumberOfLinks = row.get("?callret-2").asLiteral();
				int numberOfLinks = aggregatedNumberOfLinks.getInt();

				recommendation.setScore(new RecommendationScore(numberOfLinks, "Number of links"));

				List<Argument> arguments = new ArrayList<>();
				for (Map.Entry<String, String> constraint : knowledgeSource.getConstraintMap().entrySet()) {
					arguments.addAll(getArgument(row.get("?subject").toString(), constraint));
				}

				recommendation.addArguments(arguments);
				recommendations.add(recommendation);
			}
		}
		return ElementRecommendation.normalizeRecommendationScore(recommendations);
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

	private int getLimit() {
		return ConfigPersistenceManager.getDecisionGuidanceConfiguration(projectKey).getMaxNumberOfRecommendations();
	}
}
