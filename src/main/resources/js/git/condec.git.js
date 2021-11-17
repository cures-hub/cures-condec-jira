/**
 * Responsible to show the decision knowledge from commit messages and code comments.
 *
 * Requires: conDecGitAPI
 * Required by: 
 *
 * Is referenced in HTML by tabs/knowledgeInGit.vm
 */
(function(global) {

	var ConDecGit = function() {
	};

	/**
	 * Renders git branches in HTML.
	 */
	ConDecGit.prototype.getBranchesDiff = function() {
		var contentHtml = document.getElementById("featureBranches-container");
		contentHtml.innerHTML = "<aui-spinner></aui-spinner>";

		conDecGitAPI.getBranches()
			.then(branches => {
				if (branches === null || branches === undefined) {
					contentHtml.innerText = "Git extraction is disabled.";
					return;
				}
				if (branches.length === 0) {
					contentHtml.innerText = "No feature branches found for this Jira issue.";
				} else {
					contentHtml.innerText = "";
					for (branch of branches) {
						contentHtml.appendChild(createBranchHTML(branch));
					}
				}
			})
			.catch(error => {
				contentHtml.innerText = "An error occurred while fetching REST data: " + error;
			});
	};

	/**
	 * Renders one git branch in HTML.
	 */
	function createBranchHTML(branch) {
		console.debug("createBranchHTML");

		branchContainer = document.createElement("div");
		var branchLabel = document.createElement("h3");
		branchLabel.style = "float:left;";
		branchLabel.innerText = "Branch " + branch.name;
		branchContainer.appendChild(branchLabel);

		var branchExpander = document.createElement("a");
		branchExpander.innerText = "Hide details for branch";
		branchExpander.setAttribute("data-replace-text", "Show details for branch " + branch.name);
		branchExpander.className = "aui-expander-trigger right-aligned";
		branchExpander.setAttribute("aria-controls", branch.id + hash(branch.name));
		branchContainer.appendChild(branchExpander);

		branchCollapsableContainer = document.createElement("div");
		branchCollapsableContainer.className = "aui-expander-content";
		branchCollapsableContainer.setAttribute("aria-expanded", true);
		branchCollapsableContainer.id = branch.id + hash(branch.name);

		branchRepoLink = document.createElement("a");
		branchRepoLink.href = branch.repoUri;
		branchRepoLink.target = "_blank";
		branchRepoLink.innerText = branch.repoUri;
		branchRepoLink.className = "right-aligned";
		branchCollapsableContainer.appendChild(branchRepoLink);

		branchCollapsableContainer.appendChild(createBranchQualityAssessment(branch));
		branchCollapsableContainer.appendChild(createBranchMessageElementsHtml(branch.commitElements));
		branchCollapsableContainer.appendChild(createBranchCodeElementsHtml(branch.codeElements));
		branchContainer.appendChild(branchCollapsableContainer);

		return branchContainer;
	}

	function getElementAsHTML(element) {
		var root = document.createElement("div");
		root.className = "condec-box " + element.type.toLowerCase();

		var img = document.createElement("img");
		img.src = element.image;
		img.className = "emoticon";
		root.appendChild(img);
		root.insertAdjacentText("beforeend", " " + element.summary);

		return root;
	}

	function createBranchCodeElementsHtml(elementsFromCode) {
		var codeFilesHtml = document.createElement("div");
		if (elementsFromCode.length === 0) {
			return codeFilesHtml;
		}
		var commitMessagesHeader = document.createElement("h4");
		commitMessagesHeader.innerText = "Decision Knowledge Captured in Code Comments";
		codeFilesHtml.appendChild(commitMessagesHeader);

		var codeFileName = "";
		var codeFileHtml = null; // for single code file
		for (element of elementsFromCode) {
			if (codeFileName !== element.source) {
				codeFileName = element.source;
				codeFileHtml = document.createElement("div");
				codeFileHtml.className = "condec-box";

				codeFileLabel = document.createElement("i");
				codeFileLabel.innerText = codeFileName + " --- ";
				codeFileHtml.appendChild(codeFileLabel);

				var link = document.createElement("a");
				link.innerHTML = "<span class='aui-icon aui-icon-small aui-iconfont-shortcut'></span> Navigate to Code File in Git";
				link.href = element.url;
				link.target = "_blank";
				codeFileHtml.appendChild(link);
			}
			var codeElementHtml = getElementAsHTML(element);
			codeElementHtml.title = "Line in file: " + element.startLine;

			if (element.qualityProblems.length > 0) {
				var problemElement = document.createElement("p");
				problemElement.className = "quality-problem";
				for (problem of element.qualityProblems) {
					problemElement.title += problem.explanation + "\n";
				}
				codeElementHtml.appendChild(problemElement);
			}

			codeFileHtml.appendChild(codeElementHtml);
			codeFilesHtml.appendChild(codeFileHtml);
		}
		return codeFilesHtml;
	}

	/**
	 * Group rationale in commit messages by commit hash (id).
	 */
	function createBranchMessageElementsHtml(elementsFromCommitMessages) {
		var commitsHtml = document.createElement("div"); // for all commits of branch
		if (elementsFromCommitMessages.length === 0) {
			return commitsHtml;
		}
		var commitMessagesHeader = document.createElement("h4");
		commitMessagesHeader.innerText = "Decision Knowledge Captured in Commit Messages";
		commitsHtml.appendChild(commitMessagesHeader);

		var commitId = ""; // commit hash
		var commitHtml = null; // for single commit
		for (element of elementsFromCommitMessages) {
			if (commitId !== element.source) {
				commitId = element.source;
				commitHtml = document.createElement("div");
				commitHtml.className = "condec-box";
				commitHtml.id = commitId;

				commitLabel = document.createElement("i");
				commitLabel.innerText = "Commit " + commitId + " --- ";
				commitHtml.appendChild(commitLabel);

				var link = document.createElement("a");
				link.innerHTML = "<span class='aui-icon aui-icon-small aui-iconfont-shortcut'></span> Navigate to Commit in Git";
				link.href = element.url;
				link.target = "_blank";
				commitHtml.appendChild(link);
			}
			var messageElementHtml = getElementAsHTML(element);
			messageElementHtml.title = "Commit " + commitId;
			commitHtml.appendChild(messageElementHtml);
			commitsHtml.appendChild(commitHtml);
		}
		return commitsHtml;
	}

	function createBranchQualityAssessment(branch) {
		qualitySummary = document.createElement("div");
		qualitySummary.className = "condec-box";
		if (branch.commitElements.length + branch.codeElements.length === 0) {
			qualitySummary.innerText = "No (new) decision knowledge found in commit messages and comments of changed files!";
			qualitySummary.classList.add("condec-warning");
		} else if (branch.qualityProblems.length === 0) {
			qualitySummary.innerText = "No quality problems found in this branch. Great work!";
			qualitySummary.classList.add("condec-fine");
		} else {
			qualitySummary.innerHTML = "The decision knowledge documentation in this branch has the following quality problems: <ul>";
			for (problem of branch.qualityProblems) {
				qualitySummary.innerHTML += "<li>" + problem.explanation + "</li>";
			}
			qualitySummary.innerHTML += "</ul>Please improve the decision knowledge documentation in code comments in git.";
			qualitySummary.classList.add("dodViolation");
		}
		return qualitySummary;
	}

	function hash(string) {
		var hash = 0;
		for (i = 0; i < string.length; i++) {
			ch = string.charCodeAt(i);
			hash = ((hash << 5) - hash) + ch;
			hash = hash & hash;
		}
		return hash;
	}

	global.conDecGit = new ConDecGit();
})(window);