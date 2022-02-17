package de.uhd.ifi.se.decision.management.jira.changeimpactanalysis;

import java.util.HashMap;
import java.util.Map;

import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;

/**
 * Models a {@link KnowledgeElement} with added change impact scores as
 * calculated by the {@link ChangeImpactAnalysisService}.
 * 
 * @see ChangePropagationRule
 */
public class KnowledgeElementWithImpact extends KnowledgeElement {

    private double impactValue;
    private double parentImpact;
    private double linkTypeWeight;
    private double propagationRuleValue;
    private Map<String, Double> propagationRules;
    private String impactExplanation;

    public KnowledgeElementWithImpact(KnowledgeElement element, double impactValue,
            double parentImpact, double linkTypeWeight, double propagationRuleValue,
            Map<String, Double> propagationRule, String impactExplanation) {
        this(element);
        this.impactValue = impactValue;
        this.parentImpact = parentImpact;
        this.linkTypeWeight = linkTypeWeight;
        this.propagationRuleValue = propagationRuleValue;
        this.propagationRules = propagationRule;
        this.impactExplanation = impactExplanation;
    }

    public KnowledgeElementWithImpact(KnowledgeElement element) {
        this.project = element.getProject();
		this.id = element.getId();
        this.setDescription(element.getDescription());
		this.setSummary(element.getSummary());
		this.documentationLocation = element.getDocumentationLocation();
		this.type = element.getType();
        this.setKey(element.getKey());

        this.impactValue = 1.0;
        this.parentImpact = 1.0;
        this.linkTypeWeight = 1.0;
        this.propagationRuleValue = 1.0;
        this.propagationRules = new HashMap<>();
        this.impactExplanation = "";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!super.equals(obj))
            return false;
        if (getClass() != obj.getClass())
            return false;
        return true;
    }
    
    public double getImpactValue() {
        return impactValue;
    }

    public void setImpactValue(double impactValue) {
        this.impactValue = impactValue;
    }

    public double getRuleBasedValue() {
        return propagationRuleValue;
    }

    public void setRuleBasedValue(double propagationRuleValue) {
        this.propagationRuleValue = propagationRuleValue;
    }

    public double getLinkTypeWeight() {
        return linkTypeWeight;
    }

    public void setLinkTypeWeight(double linkTypeWeight) {
        this.linkTypeWeight = linkTypeWeight;
    }

    public double getParentImpact() {
        return parentImpact;
    }

    public void setParentImpact(double parentImpact) {
        this.parentImpact = parentImpact;
    }

    public Map<String, Double> getPropagationRules() {
        return propagationRules;
    }

    public void setPropagationRules(Map<String, Double> propagationRules) {
        this.propagationRules = propagationRules;
    }

    public String getImpactExplanation() {
        return impactExplanation;
    }

    public void setImpactExplanation(String impactExplanation) {
        this.impactExplanation = impactExplanation;
    }
}
