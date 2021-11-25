package de.uhd.ifi.se.decision.management.jira.changeimpactanalysis;

import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;

/**
 * Models a {@link KnowledgeElement} with added change impact scores as
 * calculated by the {@link ChangeImpactAnalysisService}.
 * 
 * @see ChangePropagationRule
 */
public class KnowledgeElementWithImpact {

    private KnowledgeElement element;
    private double impactValue;
    private double parentImpact;
    private double linkTypeWeight;
    private double ruleBasedValue;
    private String impactExplanation;

    public KnowledgeElementWithImpact(KnowledgeElement element, double impactValue,
            double parentImpact, double linkTypeWeight, double ruleBasedValue) {
        this.element = element;
        this.impactValue = impactValue;
        this.parentImpact = parentImpact;
        this.linkTypeWeight = linkTypeWeight;
        this.ruleBasedValue = ruleBasedValue;
        this.impactExplanation = "[ToDo]";
    }

    public KnowledgeElementWithImpact(KnowledgeElement element) {
        this.element = element;
        this.impactValue = 1.0;
        this.parentImpact = 1.0;
        this.linkTypeWeight = 1.0;
        this.ruleBasedValue = 1.0;
        this.impactExplanation = "[ToDo]";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        KnowledgeElementWithImpact other = (KnowledgeElementWithImpact) obj;
        if (element == null) {
            if (other.element != null)
                return false;
        } else if (!element.equals(other.element))
            return false;
        return true;
    }

    public KnowledgeElement getElement() {
        return element;
    }

    public void setElement(KnowledgeElement element) {
        this.element = element;
    }

    public double getImpactValue() {
        return impactValue;
    }

    public void setImpactValue(double impactValue) {
        this.impactValue = impactValue;
    }

    public double getRuleBasedValue() {
        return ruleBasedValue;
    }

    public void setRuleBasedValue(double ruleBasedValue) {
        this.ruleBasedValue = ruleBasedValue;
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

    public String getImpactExplanation() {
        return impactExplanation;
    }

    public void setImpactExplanation(String impactExplanation) {
        this.impactExplanation = impactExplanation;
    }

}
