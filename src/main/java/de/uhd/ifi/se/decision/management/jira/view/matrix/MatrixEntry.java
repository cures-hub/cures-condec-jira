package de.uhd.ifi.se.decision.management.jira.view.matrix;

import javax.xml.bind.annotation.XmlElement;

public class MatrixEntry {
    @XmlElement
    private Long idOfSourceElement;

    @XmlElement
    private Long idOfDestinationElement;

    @XmlElement
    private String linkType;

    @XmlElement
    private String color;

    public MatrixEntry(Long idOfSourceElement, Long idOfDestinationElement, String linkType) {
        this.idOfSourceElement = idOfSourceElement;
        this.idOfDestinationElement = idOfDestinationElement;
        this.linkType = linkType;
        this.color = getColor(linkType);
    }

    public Long getIdOfSourceElement() {
        return idOfSourceElement;
    }

    public Long getIdOfDestinationElement() {
        return idOfDestinationElement;
    }

    public String getLinkType() {
        return linkType;
    }

    public String getColor() {
        return color;
    }

    private String getColor(String linkType) {
        switch (linkType) {
            case "Constraints": return "Green";
            case "Enables": return "LightGreen";
            case "Forbids": return "Red";
            case "Comprises": return "Blue";
            case "Subsumes": return "LightBlue";
            case "Overrides": return "Yellow";
            case "Replaces": return "Orange";
            case "Relates": return "Purple";
            default: return "";
        }

    }
}
