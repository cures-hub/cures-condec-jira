package de.uhd.ifi.se.decision.management.jira.releasenotes;

import java.util.*;


public enum TaskCriteriaPrioritisation {
	COUNT_DECISION_KNOWLEDGE, PRIORITY, COUNT_COMMENTS, SIZE_SUMMARY, SIZE_DESCRIPTION, DAYS_COMPLETION, EXPERIENCE_RESOLVER, EXPERIENCE_REPORTER;


	@Override
	public String toString() {
		return this.name().toLowerCase(Locale.ENGLISH);
	}


	public static TaskCriteriaPrioritisation getTaskCriteriaPrioritisation(String type) {
		if (type == null) {
			return TaskCriteriaPrioritisation.COUNT_DECISION_KNOWLEDGE;
		}
		switch (type.toLowerCase(Locale.ENGLISH)) {
			case "count_decision_knowledge":
				return TaskCriteriaPrioritisation.COUNT_DECISION_KNOWLEDGE;
			case "priority":
				return TaskCriteriaPrioritisation.PRIORITY;
			case "count_comments":
				return TaskCriteriaPrioritisation.COUNT_COMMENTS;
			case "size_summary":
				return TaskCriteriaPrioritisation.SIZE_SUMMARY;
			case "size_description":
				return TaskCriteriaPrioritisation.SIZE_DESCRIPTION;
			case "days_completion":
				return TaskCriteriaPrioritisation.DAYS_COMPLETION;
			case "experience_resolver":
				return TaskCriteriaPrioritisation.EXPERIENCE_RESOLVER;
			case "experience_reporter":
				return TaskCriteriaPrioritisation.EXPERIENCE_REPORTER;
			default:
				return TaskCriteriaPrioritisation.COUNT_DECISION_KNOWLEDGE;
		}
	}

	/**
	 * @return hashMap of criterias with integer default 0.
	 */
	public static EnumMap<TaskCriteriaPrioritisation, Integer> toIntegerEnumMap() {
		EnumMap<TaskCriteriaPrioritisation, Integer> criteriaTypes = new EnumMap<>(TaskCriteriaPrioritisation.class);
		for (TaskCriteriaPrioritisation criteriaType : TaskCriteriaPrioritisation.values()) {
			criteriaTypes.put(criteriaType, 0);
		}
		return criteriaTypes;
	}

	/**
	 * @return hashMap of criterias with double default 1.0
	 */
	public static EnumMap<TaskCriteriaPrioritisation, Double> toDoubleEnumMap() {
		EnumMap<TaskCriteriaPrioritisation, Double> criteriaTypes = new EnumMap<>(TaskCriteriaPrioritisation.class);
		for (TaskCriteriaPrioritisation criteriaType : TaskCriteriaPrioritisation.values()) {
			criteriaTypes.put(criteriaType, 1.0);
		}
		return criteriaTypes;
	}

	/**
	 * Convert all criterias to strings.
	 *
	 * @return list of criterias  as Strings.
	 */
	public static List<String> toList() {
		List<String> criteriaTypes = new ArrayList<String>();
		for (TaskCriteriaPrioritisation criteriaType : TaskCriteriaPrioritisation.values()) {
			criteriaTypes.add(criteriaType.toString());
		}
		return criteriaTypes;
	}

	/**
	 * @return list of criterias.
	 */
	public static List<TaskCriteriaPrioritisation> getOriginalList() {
		return new ArrayList<TaskCriteriaPrioritisation>(Arrays.asList(TaskCriteriaPrioritisation.values()));
	}

}
