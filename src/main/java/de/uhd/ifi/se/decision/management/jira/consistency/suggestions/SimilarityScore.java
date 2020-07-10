package de.uhd.ifi.se.decision.management.jira.consistency.suggestions;

import org.codehaus.jackson.annotate.JsonProperty;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class SimilarityScore {
	@JsonProperty
	private Map<String, Double> scores;


	public SimilarityScore() {
		this.scores = new HashMap<>();
	}

	public Map<String, Double> getScores() {
		return scores;
	}

	public Double getTotal() {
		return this.scores.values().stream().reduce(0., Double::sum);
	}

	public void put(String key, Double score) {
		this.scores.put(key, score);
		this.normalizeScores(key);
	}

	private void normalizeScores(String newKey) {
		Set<String> keys = this.scores.keySet();
		if (keys.size() == 1) {
			return;
		}
		for (String key : keys) {
			Double oldValue = scores.get(key);
			if (!key.equals(newKey)){
				oldValue = oldValue * (keys.size() - 1);
			}
			scores.put(key, oldValue / keys.size());
		}
	}
}
