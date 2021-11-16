/**
 * Responsible to show the decision knowledge from commit messages and code comments.
 *
 * Requires: conDecAPI
 * Required by: 
 *
 * Is referenced in HTML by tabs/knowledgeInGit.vm
 */
(function(global) {

	var ConDecGit = function() {
		this.contentHtml = "";
	};

	ConDecGit.prototype.getBranchesDiff = function() {
		contentHtml = document.getElementById("featureBranches-container");
		contentHtml.innerText = "Loading ...";

		conDecGitAPI.getElementsFromBranchesOfJiraIssue(conDecAPI.getIssueKey())
			.then((branches) => {
				if (branches === null || branches === undefined) {
					contentHtml.innerText = "Git extraction is disabled.";
					return;
				}
				if (branches.length === 0) {
					contentHtml.innerText = "No feature branches found for this Jira issue.";
				} else {
					contentHtml.innerText = "";
					showBranchesDiff(branches);
				}
				contentHtml.appendChild(createForceRestFetch());
			})
			.catch((error) => {
				contentHtml.innerText = "An error occurred while fetching REST data: " + error;
				contentHtml.appendChild(createForceRestFetch());
			});
	};

	/**
	 * Renders all git branches in HTML.
	 */
	function showBranchesDiff(branches) {
		console.debug("showBranchesDiff");

		for (var branch of branches) {
			showBranchDiff(branch);

			/* assess relations between rationale and their problems */
			conDecLinkBranchCandidates.init(branch.commitElements, branch.name, branch.id,
				"messages");
			/* render results in HTML */
			conDecLinkBranchCandidates.attachProblemsToElementsInHTML();

			conDecLinkBranchCandidates.init(branch.codeElements, branch.name, branch.id, "files");
			/* render results in HTML */
			conDecLinkBranchCandidates.attachProblemsToElementsInHTML();
		}
	}

	/**
	 * Renders one git branch in HTML.
	 */
	function showBranchDiff(branch) {
		console.debug("showBranchDiff");

		branchContainer = document.createElement("div");
		var branchLabel = document.createElement("h3");
		branchLabel.style = "float:left;";
		branchLabel.innerText = "Branch " + branch.name;
		branchContainer.appendChild(branchLabel);

		var branchExpander = document.createElement("a");
		branchExpander.innerText = "Hide details for branch";
		branchExpander.setAttribute("data-replace-text", "Show details for branch");
		branchExpander.className = "aui-expander-trigger right-aligned";
		branchExpander.setAttribute("aria-controls", branch.id);
		branchContainer.appendChild(branchExpander);

		branchCollapsableContainer = document.createElement("div");
		branchCollapsableContainer.className = "aui-expander-content";
		branchCollapsableContainer.setAttribute("aria-expanded", true);
		branchCollapsableContainer.id = branch.id;

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

		contentHtml.appendChild(branchContainer);
	}

	function createForceRestFetch() {
		forceRestNode = document.createElement("div");
		forceRestNode.innerText = "Suspecting branch list is not up-to date? Click here to try again.";
		forceRestNode.addEventListener("click", () => conDecGit.getBranchesDiff());
		return forceRestNode;
	}

	function getElementAsHTML(element) {
		var root = document.createElement("div");
		root.className = "messageBox " + element.type.toLowerCase();
		root.style = "padding:5px;";

		var img = document.createElement("img");
		img.src = element.image;
		img.className = "emoticon";
		root.appendChild(img);
		root.insertAdjacentText("beforeend", " " + element.summary);
		return root;
	}

	function createBranchCodeElementsHtml(elementsFromCode) {
		lastBranchBlocks = new Map();
		for (element of elementsFromCode) {
			codeElementHtml = getElementAsHTML(element);
			codeElementHtml.title = "Line in file: " + element.startLine;

			if (!lastBranchBlocks.has(element)) {
				lastBranchBlocks.set(element, []);
			}

			var codeElements = lastBranchBlocks.get(element);
			codeElements.push(codeElementHtml);
			lastBranchBlocks.set(element, codeElements);
		}

		return appendCodeElements(lastBranchBlocks);
	}

	function appendCodeElements(lastBranchBlocks) {
		console.debug("appendCodeElements");
		blockLinesIterator = lastBranchBlocks.entries();
		var allCodeElementsHTML = document.createElement("div");
		var codeFilesHeader = document.createElement("h4");
		codeFilesHeader.innerText = "Decision knowledge elements in code comments";
		allCodeElementsHTML.appendChild(codeFilesHeader);
		while (blockEntry = blockLinesIterator.next()) {
			if (blockEntry.done) {
				break;
			}
			var codeElements = blockEntry.value[1];

			var fileRatElement = document.createElement("p");

			var fileRatBlockLabel = document.createElement("i");
			fileRatBlockLabel.innerText = blockEntry.value[0].source;
			
			

			var codeElementsHtml = document.createElement("p");

			for (element of codeElements) {
				codeElementsHtml.appendChild(element);
			}
			fileRatElement.appendChild(codeElementsHtml);

			allCodeElementsHTML.appendChild(fileRatBlockLabel);
			
			var link = document.createElement("a");
			link.innerHTML = "<span class='aui-icon aui-icon-small aui-iconfont-shortcut'></span>";
			link.title = "Navigate to Code File in Git";
			link.href = blockEntry.value[0].url;
			link.target = "_blank";
			AJS.$(link).tooltip({gravity: 'w'});
			allCodeElementsHTML.appendChild(link);
			
			allCodeElementsHTML.appendChild(fileRatElement);
		}
		return allCodeElementsHTML;
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
		commitMessagesHeader.innerText = "Decision knowledge elements in commit messages";
		commitsHtml.appendChild(commitMessagesHeader);
		
		var commitId = ""; // commit hash
		var commitHtml = null; // for single commit
		for (element of elementsFromCommitMessages) {
			if (commitId !== element.source) {
				commitId = element.source;
				commitHtml = document.createElement("div");
				commitHtml.id = commitId;

				messageBlockLabelHtml = document.createElement("i");
				messageBlockLabelHtml.innerText = "Commit " + commitId + " ";
				commitHtml.appendChild(messageBlockLabelHtml);
				
				var link = document.createElement("a");
				link.innerHTML = "<span class='aui-icon aui-icon-small aui-iconfont-shortcut'></span>";
				link.title = "Navigate to Commit in Git";
				link.href = element.url;
				link.target = "_blank";
				AJS.$(link).tooltip({gravity: 'w'});
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
		qualitySummary.className = "qualitySummary";
		if (branch.commitElements.length > 0 && branch.codeElements.length > 0) {
			qualitySummary.innerText = "No quality problems found in this branch.";
			qualitySummary.classList.add("noProblems");
		} else {
			qualitySummary.innerText = "No rationale found in messages and changed files!";
			qualitySummary.classList.add("noRationale");
		}
		return qualitySummary;
	}

	global.conDecGit = new ConDecGit();
})(window);