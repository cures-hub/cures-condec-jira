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
				}
				showBranchesDiff(branches);
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
		branchLabel.innerText = "Branch " + branch.name + " (" + branch.repoUri + ")";
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

		branchCollapsableContainer.appendChild(createBranchQualityAssessment(branch));
		branchCollapsableContainer.appendChild(createBranchMessageElementsHtml(branch.commitElements));
		branchCollapsableContainer.appendChild(createBranchCodeElementsHtml(branch.codeElements));
		branchContainer.appendChild(branchCollapsableContainer);

		contentHtml.appendChild(branchContainer);
	}

	function createForceRestFetch() {
		forceRestNode = document.createElement("div");
		forceRestNode.innerText = "Suspecting branch list is not up-to date? Click here to try again.";
		forceRestNode.addEventListener("click", () => getBranchesDiff());
		return forceRestNode;
	}

	function getElementAsHTML(element) {
		console.debug("getElementAsHTML");
		var root = document.createElement("p");
		root.className = "messageBox " + element.type.toLowerCase();
		root.style = "padding:5px;";
		root.dataset.ratType = element.type.toLowerCase();

		var link = document.createElement("a");
		link.style = "text-decoration: none; color: black;";
		link.href = element.url;

		var img = document.createElement("img");
		img.src = element.image;
		img.className = "emoticon";

		link.appendChild(img);
		link.insertAdjacentText("beforeend", " " + element.summary);

		root.appendChild(link);
		return root;
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
			fileRatBlockLabel.innerText = blockEntry.value[0];

			var codeElementsHtml = document.createElement("p");

			for (element of codeElements) {
				codeElementsHtml.appendChild(element);
			}
			fileRatElement.appendChild(codeElementsHtml);

			allCodeElementsHTML.appendChild(fileRatBlockLabel);
			allCodeElementsHTML.appendChild(fileRatElement);
		}
		return allCodeElementsHTML;
	}

	function createBranchMessageElementsHtml(elementsFromMessage) {
		/* group rationale in messages by commit hash */
		var commit = "";
		var messageBlockHtml = null;
		var allMessageBlockHtml = document.createElement("div");
		if (elementsFromMessage.length === 0) {
			return allMessageBlockHtml;
		}
		var commitMessagesHeader = document.createElement("h4");
		commitMessagesHeader.innerText = "Decision knowledge elements in commit messages";
		allMessageBlockHtml.appendChild(commitMessagesHeader);
		for (element of elementsFromMessage) {
			if (commit !== element.source) {
				commit = element.source;
				messageBlockHtml = document.createElement("div");
				messageBlockHtml.id = commit;

				messageBlockLabelHtml = document.createElement("i");
				messageBlockLabelHtml.innerText = "Commit message " + commit;
				messageBlockHtml.appendChild(messageBlockLabelHtml);
			}
			var messageElementHtml = getElementAsHTML(element);
			messageElementHtml.title = "Commit " + element.source;
			messageBlockHtml.appendChild(messageElementHtml);
			allMessageBlockHtml.appendChild(messageBlockHtml);
		}
		return allMessageBlockHtml;
	}

	function createBranchCodeElementsHtml(elementsFromCode) {
		lastBranchBlocks = new Map();
		for (element of elementsFromCode) {
			codeElementHtml = getElementAsHTML(element);
			codeElementHtml.title = "Line in file: " + element.startLine;

			if (!lastBranchBlocks.has(element.source)) {
				lastBranchBlocks.set(element.source, []);
			}

			var codeElements = lastBranchBlocks.get(element.source);
			codeElements.push(codeElementHtml);
			lastBranchBlocks.set(element.source, codeElements);
		}

		return appendCodeElements(lastBranchBlocks);
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