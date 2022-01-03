package de.uhd.ifi.se.decision.management.jira.quality.completeness;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlElement;

import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;

/**
 * Represents the rationale coverage of e.g. requirements, code, and other
 * software artifacts (=knowledge elements) regarding a specific decision
 * knowledge type, e.g. {@link KnowledgeType#ISSUE} or
 * {@link KnowledgeType#DECISION}.
 */
public class RationaleCoverageMetric {

	private KnowledgeType targetElementType;
	private int minimumRequiredCoverage;
	private Map<Integer, List<KnowledgeElement>> coverageMap = new LinkedHashMap<>();

	public RationaleCoverageMetric(KnowledgeType targetElementType) {
		this.targetElementType = targetElementType;
		this.coverageMap = new LinkedHashMap<>();
	}

	@XmlElement
	public String getTargetElementType() {
		return targetElementType.toString();
	}

	@XmlElement
	public int getMinimumRequiredCoverage() {
		return minimumRequiredCoverage;
	}

	public void setMinimumRequiredCoverage(int minimumRequiredCoverage) {
		this.minimumRequiredCoverage = minimumRequiredCoverage;
	}

	@XmlElement
	public Map<Integer, List<KnowledgeElement>> getCoverageMap() {
		return coverageMap;
	}
}