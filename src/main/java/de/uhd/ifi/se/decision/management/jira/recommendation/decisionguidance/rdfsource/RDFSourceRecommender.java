package de.uhd.ifi.se.decision.management.jira.recommendation.decisionguidance.rdfsource;

import java.util.*;
import java.util.concurrent.TimeUnit;

import de.uhd.ifi.se.decision.management.jira.classification.preprocessing.Preprocessor;
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

	private static List<String> combineKeywords(List<String> keywords) {
		List<String> combinedKeywords = new ArrayList<>();
		combinedKeywords.addAll(keywords);

		StringBuilder stringBuilder = new StringBuilder();
		String first = keywords.get(0);

		stringBuilder.append(first);
		for (String second : keywords) {
			if (!first.equals(second)) {
				stringBuilder.append('_').append(second);
				combinedKeywords.add(stringBuilder.toString());
			}
		}

		stringBuilder.setLength(0);

		return combinedKeywords;
	}

	/**
	 * Get search terms to be included in potential RDFsource URIs (e.g. "Which database system
	 * should we use? Should we use something from IBM?" -> {"database", "system",
	 * "database_system", "IBM"}.
	 *
	 * @param input Text based on which the search terms are generated.
	 * @return Search terms based on the noun chunks of the given text.
	 */
	public static Set<String> getSearchTerms(String input) {
		System.out.println("Getting search terms for...");
		System.out.println(input);
		Preprocessor preprocessor = Preprocessor.getInstance();
		String[] chunks = preprocessor.getNounChunksForText(input);
		System.out.println("Retrieved chunks:");
		for (String chunk : chunks) {
			System.out.println(chunk);
		}

		String[] cleanedChunks = preprocessor.removeStopWordsFromTexts(chunks);
		System.out.println("Cleaned chunks:");
		for (String chunk : cleanedChunks) {
			System.out.println(chunk);
		}

		List<String> searchTerms = new ArrayList<>();
		for (String chunk: cleanedChunks) {
			List<String> keywords = Arrays.asList(chunk.trim().split(" "));
			searchTerms.addAll(combineKeywords(keywords));
		}
		System.out.println("Returning following search terms:");
		for (String term : searchTerms) {
			System.out.println(term);
		}
		return new HashSet<>(searchTerms);
	}

	/**
	 * @param queryString
	 *            in SPARQL language (SPARQL Protocol And RDF Query Language).
	 * @return {@link ResultSet} for the query.
	 */
	@SuppressWarnings("PMD.NullAssignment")  // For static code analysis: If the query fails, null is expected
	protected ResultSet queryDatabase(String queryString) {
		ResultSet resultSet;
		try {
			Query query = QueryFactory.create(queryString);
			QueryExecution queryExecution = QueryExecution.service(knowledgeSource.getService()).query(query)
					.timeout(knowledgeSource.getTimeout(), TimeUnit.MILLISECONDS).build();

			resultSet = queryExecution.execSelect();
		} catch (Exception e) {
			resultSet = null;
		}
		return resultSet;
	}

	/**
	 * Add dummy recommendations to a given list of recommendations for testing and demonstration purposes.
	 *
	 * @param recommendations List of recommendation to which the dummy recommendations are added.
	 */
	private void addDummyRecommendations(List<ElementRecommendation> recommendations) {
		for (int i = 0; i < 10; i++) {
			recommendations.add(new ElementRecommendation("Dummy recommendation No. " + i,
					knowledgeSource, "wikipedia.org"));
		}
	}

	@SuppressWarnings({"PMD.CloseResource",  // For static code analysis: ResultSet does not have a close method
			"PMD.OnlyOneReturn",  // Multiple returns improve readability and reduce cognitive complexity in this case
	})
	@Override
	public List<ElementRecommendation> getRecommendations(String inputs) {
		List<ElementRecommendation> recommendations = new ArrayList<>();
		if (inputs == null) {
			return recommendations;
		}

		if (inputs.contains("get_dummy_decision_guidance_recommendations")) {
			addDummyRecommendations(recommendations);
			return recommendations;
		}

		Set<String> searchTerms = getSearchTerms(inputs);

		for (String combinedKeyword : searchTerms) {

			final String uri = "<http://dbpedia.org/resource/" + combinedKeyword + ">";
			String queryStringWithInput = knowledgeSource.getQuery().replaceAll("%variable%", uri).replaceAll("[\\r\\n\\t]", " ");
			queryStringWithInput = String.format("%s LIMIT %d", queryStringWithInput, this.getLimit());

			ResultSet resultSet = queryDatabase(queryStringWithInput);

			while (resultSet != null && resultSet.hasNext()) {
				QuerySolution row = resultSet.nextSolution();

				String label = this.getLabel(row.get("?subject").toString());

				ElementRecommendation recommendation = new ElementRecommendation(label, knowledgeSource, row.get("?url").toString());

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

	@SuppressWarnings("PMD.CloseResource")  // For static code analysis: ResultSet does not have a close method
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

	@SuppressWarnings("PMD.CloseResource")  // For static code analysis: ResultSet does not have a close method
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
