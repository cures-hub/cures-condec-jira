package de.uhd.ifi.se.decision.management.jira.git.model;

import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TreeMap;

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
public class DecisionKnowledgeElementInCodeComment extends KnowledgeElement
		implements Comparable<DecisionKnowledgeElementInCodeComment> {

	private ChangedFile codeFile;
	private int startLine;
	private List<QualityProblem> qualityProblems;

	public DecisionKnowledgeElementInCodeComment() {
		documentationLocation = DocumentationLocation.CODE;
		qualityProblems = new ArrayList<>();
	}

	@XmlElement
	public String getImage() {
		return URLEncoder.encode(getType().getIconUrl(), Charset.defaultCharset());
	}

	@XmlElement
	public int getStartLine() {
		return startLine;
	}

	@XmlElement
	public String getUrl() {
		String urlAsString = getRepoUri().replace(".git", "") + "/search?q=filename:" + getCodeFileName();
		return URLEncoder.encode(urlAsString, Charset.defaultCharset());
	}

	public String getRepoUri() {
		return codeFile.getRepoUri();
	}

	public void setStartLine(int startLine) {
		this.startLine = startLine;
		setKey(startLine + "");
	}

	public void setCodeFile(ChangedFile codeFile) {
		this.codeFile = codeFile;
		setProject(codeFile.getProject());
		setCreationDate(codeFile.getCreationDate());
		TreeMap<Date, String> updateMap = new TreeMap<Date, String>();
		updateMap.put(codeFile.getLatestUpdatingDate(), codeFile.getCreatorName());
		setUpdateDateAndAuthor(updateMap);
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

	@Override
	public int compareTo(DecisionKnowledgeElementInCodeComment otherElement) {
		return (int) (otherElement.getId() - id);
	}
}