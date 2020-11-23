package de.uhd.ifi.se.decision.management.jira.decisionguidance.knowledgesources.rdfsource;

import de.uhd.ifi.se.decision.management.jira.decisionguidance.knowledgesources.InputMethod;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.persistence.ConfigPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.view.decisionguidance.Recommendation;
import de.uhd.ifi.se.decision.management.jira.view.decisiontable.Argument;
import org.apache.jena.atlas.lib.Pair;
import org.apache.jena.query.*;
import org.apache.jena.sparql.engine.http.Params;
import org.apache.jena.sparql.engine.http.QueryEngineHTTP;
import scala.language;

import java.util.*;
import java.util.stream.Collectors;

public class RDFSourceInputString implements InputMethod<String> {

	protected String projectKey;
	protected String name;
	protected String service;
	protected String queryString;
	protected String timeout;
	protected int limit;

	public InputMethod setData(String projectKey, String name, String service, String queryName, String timeout, int limit) {
		this.projectKey = projectKey;
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

	// private int calculateScore(String keywords, String inputs) {

	// 	List<String> keywordsList = Arrays.asList(keywords.split("_"));
	// 	List<String> inputsList = Arrays.asList(inputs.split(" "));

	// 	float inputLength = inputsList.size();
	// 	int match = 0;

	// 	for (String keyword : keywordsList) {
	// 		if (inputs.contains(keyword)) match += 1;
	// 	}

	// 	float score = (match / inputLength) * 100;

	// 	return Math.round(score);

	// }

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
		Map<Recommendation, Integer> scoreMap = new HashMap<>();
		List<Recommendation> recommendationWithScore = new ArrayList<>();

		if (inputs == null) return recommendations;


		List<String> keywords = Arrays.asList(inputs.trim().split(" "));
		List<String> combinedKeywords = this.combineKeywords(keywords);


		for (String combinedKeyword : combinedKeywords) {

			String uri = "<http://dbpedia.org/resource/" + combinedKeyword + ">";
			String queryStringWithInput = this.queryString.replaceAll("%variable%", uri).replaceAll("[\\r\\n\\t]", " ");
			queryStringWithInput = String.format("%s LIMIT %d", queryStringWithInput, this.getLimit());


			ResultSet resultSet = this.queryDatabase(queryStringWithInput, this.service, Params.Pair.create("timeout", this.timeout));

			Map<String, List<Argument>> argumentsMap = new HashMap<>();

			while (resultSet != null && resultSet.hasNext()) {
				QuerySolution row = resultSet.nextSolution();


				Recommendation recommendation = new Recommendation(this.name, row.get("?alternative").toString(), row.get("?url").toString());
				recommendations.add(recommendation);

				//TODO keep arguments variable
				List<Argument> arguments = new ArrayList<>();


				if (queryStringWithInput.contains("?license_l")) {
					String licenseString = row.get("?license_l").toString();
					KnowledgeElement license = new KnowledgeElement();
					license.setType(KnowledgeType.ARGUMENT);
					license.setSummary("License: " + licenseString + "\n");
					arguments.add(new Argument(license));
				}

				if (queryStringWithInput.contains("?os_l")) {
					String OSString = row.get("?os_l").toString();
					KnowledgeElement os = new KnowledgeElement();
					os.setType(KnowledgeType.ARGUMENT);
					os.setSummary("Operating System: " + OSString + "\n");
					arguments.add(new Argument(os));
				}

				if (queryStringWithInput.contains("?language_l")) {
					String programmingLanguageString = row.get("?language_l").toString();
					KnowledgeElement language = new KnowledgeElement();
					language.setType(KnowledgeType.ARGUMENT);
					language.setSummary("Programming Language: " + programmingLanguageString + "\n");
					arguments.add(new Argument(language));
				}


				try {
					arguments.addAll(argumentsMap.get(row.get("?alternative").toString()));
				} catch (Exception e) {

				}

				argumentsMap.put(row.get("?alternative").toString(), arguments);


			}

			HashSet<Recommendation> uniqueRecommendation = new HashSet<>(recommendations);
			for (Recommendation recommendation : uniqueRecommendation) {
				List<Argument> arguments = argumentsMap.get(recommendation.getRecommendations()).stream().distinct().collect(Collectors.toList());
				if (arguments != null)
					recommendation.setArguments(arguments);
				scoreMap.put(recommendation, Collections.frequency(recommendations, recommendation));

			}

			if (scoreMap.size() != 0) {
				Comparator<? super Map.Entry<Recommendation, Integer>> maxValueComparator = Comparator.comparing(Map.Entry::getValue);

				int maxValue = scoreMap.entrySet().stream().max(maxValueComparator).get().getValue();


				scoreMap.forEach((recommendation, value) -> {
					recommendation.setScore(this.getScore(maxValue, value));
					recommendationWithScore.add(recommendation);
				});
			}


		}
		return recommendationWithScore;
	}

	private int getScore(int maxValue, int actualValue) {
		float score = (actualValue * 1.0f / maxValue) * 100f;
		return Math.round(score);
	}

	private int getLimit() {
		return ConfigPersistenceManager.getMaxNumberRecommendations(this.projectKey);
	}
}
