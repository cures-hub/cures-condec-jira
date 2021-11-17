package de.uhd.ifi.se.decision.management.jira.git.model;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;

import de.uhd.ifi.se.decision.management.jira.git.GitClient;
import de.uhd.ifi.se.decision.management.jira.git.parser.CodeCommentParser;
import de.uhd.ifi.se.decision.management.jira.git.parser.RationaleFromCodeCommentParser;
import de.uhd.ifi.se.decision.management.jira.model.DocumentationLocation;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.quality.completeness.QualityProblem;

/**
 * Represents a decision knowledge element documented in a {@link CodeComment}.
 * 
 * @see DocumentationLocation#CODE
 * @see CodeCommentParser
 * @see RationaleFromCodeCommentParser
 * @see GitClient#getRationaleElementsFromCodeComments(org.eclipse.jgit.lib.Ref)
 */
public class DecisionKnowledgeElementInCodeComment extends KnowledgeElement {

	private ChangedFile codeFile;
	private int startLine;
	private List<QualityProblem> qualityProblems;

	public DecisionKnowledgeElementInCodeComment() {
		this.documentationLocation = DocumentationLocation.CODE;
		this.qualityProblems = new ArrayList<>();
	}

	@XmlElement
	public String getImage() {
		return getType().getIconUrl();
	}

	@XmlElement
	public int getStartLine() {
		return startLine;
	}

	@XmlElement
	public String getUrl() {
		return codeFile.getRepoUri().replace(".git", "") + "/search?q=filename:" + getCodeFileName();
	}

	public void setStartLine(int startLine) {
		this.startLine = startLine;
		setKey(startLine + "");
	}

	public void setCodeFile(ChangedFile codeFile) {
		this.codeFile = codeFile;
		setProject(codeFile.getProject());
		setCreationDate(codeFile.getCreationDate());
		setUpdatingDate(codeFile.getUpdatingDate());
		setCreator(codeFile.getCreatorName());
	}

	@XmlElement(name = "source")
	public String getCodeFileName() {
		return codeFile.getName();
	}

	@XmlElement
	public List<QualityProblem> getQualityProblems() {
		return qualityProblems;
	}

	public void setQualityProblems(List<QualityProblem> qualityProblems) {
		this.qualityProblems = qualityProblems;
	}
}