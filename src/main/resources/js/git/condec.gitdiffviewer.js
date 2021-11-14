var contentHtml;
var lastBranch, lastBranchIdx;
var lastBranchElementsFromMessages, lastBranchElementsFromFiles;
var lastBranchBlocks = new Map();

function getBranchesDiff() {
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
		.catch((error) => showError(error));
}

function showBranchesDiff(branches) {
	console.debug("showBranchesDiff");
	contentHtml.innerText = "";

	for (branchIdx = 0; branchIdx < branches.length; branchIdx++) {
		lastBranch = conDecLinkBranchCandidates.extractPositions(branches[branchIdx]);
		lastBranchIdx = branchIdx;
		lastBranchBlocks = new Map();

		lastBranchElementsFromMessages = lastBranch.commitElements;
		lastBranchElementsFromFiles = lastBranch.codeElements;

		showBranchDiff(lastBranch, branchIdx);

		/* assess relations between rationale and their problems */
		conDecLinkBranchCandidates.init(lastBranchElementsFromMessages, lastBranch.branchName, branchIdx,
			"messages");
		/* render results in HTML */
		conDecLinkBranchCandidates.attachProblemsToElementsInHTML();

		conDecLinkBranchCandidates.init(lastBranchElementsFromFiles, lastBranch.branchName, branchIdx, "files");
		/* render results in HTML */
		conDecLinkBranchCandidates.attachProblemsToElementsInHTML();
	}
}

function createForceRestFetch() {
	forceRestNode = document.createElement("div");
	forceRestNode.innerText = "Suspecting branch list is not up-to date? Click here to try again.";
	forceRestNode.addEventListener("click", () => getBranchesDiff());
	return forceRestNode;
}

function showError(error) {
	console.debug("showError");
	contentHtml.innerText = "An unspecified error occurred while fetching REST data, please try again.";
	console.log(error);
	contentHtml.appendChild(createForceRestFetch());
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
	link.insertAdjacentText("beforeend", element.summary);
	
	root.appendChild(link);	
	return root;
}

function getCodeElementsFromSide(blockData) {
	console.debug("getCodeElementsFromSide");
	var codeElements = document.createElement("p");
	var rationaleElements = blockData.codeElements;

	for (var r = 0; r < rationaleElements.length; r++) {
		codeElement = rationaleElements[r];
		codeElements.appendChild(codeElement);
	}

	return codeElements;
}

function appendCodeElements() {
	console.debug("appendCodeElements");
	blockLinesIterator = lastBranchBlocks.entries();
	var allCodeElementsHTML = document.createElement("div");
	while (blockEntry = blockLinesIterator.next()) {
		if (blockEntry.done) {
			break;
		}
		var blockData = blockEntry.value[1];

		var fileRatElement = document.createElement("p");

		var fileRatBlockLabel = document.createElement("h4");
		fileRatBlockLabel.innerText = blockData.filename;
		

		fileRatBlockLabel.dataset.blockSequence = blockData.sequence;

		var codeElements = getCodeElementsFromSide(blockData);
		fileRatElement.appendChild(codeElements);

		codeElements = getCodeElementsFromSide(blockData);
		fileRatElement.appendChild(codeElements);

		allCodeElementsHTML.appendChild(fileRatBlockLabel);
		allCodeElementsHTML.appendChild(fileRatElement);
	}
	return allCodeElementsHTML;
}

function getBlock(element) {
	var block = {};
	block.diffType = true;
	block.entry = " " + element.source;

	block.toString = function() {
		return "1 - " + block.entry;
	};
	return block;
}

function createBranchMessageElementsHtml(elementsFromMessage) {
	/* group rationale in messages by commit hash */
	var msgCommitIsh = "";
	var messageBlockHtml = null;
	var allMessageBlockHtml = document.createElement("div");
	for (m = 0; m < elementsFromMessage.length; m++) {
		if (msgCommitIsh !== elementsFromMessage[m].source) {
			msgCommitIsh = elementsFromMessage[m].source;
			if (messageBlockHtml) {
				/* add previous message */
				allMessageBlockHtml.appendChild(messageBlockHtml);
			}
			messageBlockHtml = document.createElement("p");
			messageBlockHtml.id = "branchGroup-" + lastBranchIdx + "-message-" + msgCommitIsh;
			messageBlockHtml.className = "messageBox";

			messageBlockLabelHtml = document.createElement("div");
			messageBlockLabelHtml.innerText = "Commit message " + msgCommitIsh;
			messageBlockLabelHtml.className = "commitMessageLabel";
			messageBlockHtml.appendChild(messageBlockLabelHtml);
		}
		if (messageBlockHtml) {
			var messageElementHtml = getElementAsHTML(elementsFromMessage[m]);
			messageElementHtml.title = "Commit " + elementsFromMessage[m].source;
			messageBlockHtml.appendChild(messageElementHtml);
		}
	}
	allMessageBlockHtml.appendChild(messageBlockHtml);
	return allMessageBlockHtml;
}

function createBranchCodeElementsHtml(elementsFromCode) {
	for (c = 0; c < elementsFromCode.length; c++) {
		codeElementHtml = getElementAsHTML(elementsFromCode[c]);
		codeElementHtml.title = "Line in file: " + elementsFromCode[c].startLine;

		var block = getBlock(elementsFromCode[c]);

		var blockKey = block.toString();

		if (!lastBranchBlocks.has(blockKey)) {
			blockData = {
				codeElements: [],
				filename: ""
			};
			lastBranchBlocks.set(blockKey, blockData);
		}

		var blockData = lastBranchBlocks.get(blockKey);
		blockData.filename = elementsFromCode[c].source;
		blockData.codeElements.push(codeElementHtml);
		lastBranchBlocks.set(blockKey, blockData);
	}
	return appendCodeElements();
}

function createBranchQualityAssessment() {
	qualitySummary = document.createElement("p");
	qualitySummary.className = "qualitySummary";
	if ((lastBranchElementsFromMessages && lastBranchElementsFromMessages.length > 0)
		|| (lastBranchElementsFromFiles && lastBranchElementsFromFiles.length > 0)) {
		qualitySummary.innerText = "No quality problems found in this branch.";
		qualitySummary.classList.add("noProblems");
	} else {
		qualitySummary.innerText = "No rationale found in messages and changed files!";
		qualitySummary.classList.add("noRationale");
	}
	return qualitySummary;
}

/**
 * Renders one git branch in HTML.
 */
function showBranchDiff(branch) {
	console.debug("showBranchDiff");

	branchContainer = document.createElement("div");
	branchLabel = document.createElement("h3");
	branchLabel.innerText = branch.branchName;
	branchContainer.appendChild(branchLabel);

	branchContainer.appendChild(createBranchQualityAssessment());
	branchContainer.appendChild(createBranchMessageElementsHtml(branch.commitElements));
	branchContainer.appendChild(createBranchCodeElementsHtml(branch.codeElements));

	contentHtml.appendChild(branchContainer);
}