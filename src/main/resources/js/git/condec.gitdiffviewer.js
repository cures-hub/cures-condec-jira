/*
TODO: do not pollute global javascript scope!

Known issues:
	branch sorting is random
	msg.key.positin contains bad data, it is cursor:length
 */

/*
 api data transform examples:
 branches[0].elements.map(function(e) {var n = {}; n.s = e.summary.substr(0,10); n.t = e.type; n.src = e.key.source; return n})
 */

var contentHtml;
var lastBranch, lastBranchIdx;
var lastBranchElementsFromMessages, lastBranchElementsFromFiles;
var lastBranchBlocks = new Map();

var NEWER_FILE_NOT_EXIST = "-";
var OLDER_FILE_NOT_EXIST = "File did not exist";
var RATIONALE_IN_NEWER_FILE_NOT_EXIST = "Rationale got removed";
var RATIONALE_IN_OLDER_FILE_NOT_EXIST = "No rationale did exist before";
var RATIONALE_NO_CHANGES_TEXT = "{file} - no rationale changed in below section:";
var NO_QUALITY_PROBLEMS_IN_BRANCH = "No quality problems found in this branch.";
var NO_QUALITY_PROBLEMS_FOR_NO_RATIONALE_IN_BRANCH = "No rationale found in messages and changed files!";

var BRANCHES_XHR_ERROR_MSG = "An unspecified error occurred while fetching REST data, please try again.";

var url;
var branches = [];

function getBranchesDiff() {
	contentHtml = document.getElementById("featureBranches-container");
	contentHtml.innerText = "Loading ...";

	conDecGitAPI.getElementsFromBranchesOfJiraIssue(conDecAPI.getIssueKey())
		.then((branches) => {
			showBranchesDiff(branches);
		})
		.catch((error) => showError(error));
}

function showError(error) {
	console.debug("showError");
	contentHtml.innerText = BRANCHES_XHR_ERROR_MSG;
	console.log(error);
	appendForceRestFetch(contentHtml);
}

function getElementAsHTML(element, isFromMessage) {
	console.debug("getElementAsHTML");
	root = document.createElement("p");
	desc = document.createElement("p");
	loc = document.createElement("p");

	var locationText = "";

	locationTextShort = element.startLine;
	if (isFromMessage) {
		root.className = "messageBox rationale " + element.type.toLowerCase();
		locationText = "Commit message " + element.keyData.source + " at position (sequence # in text, rationale length) "
			+ locationTextShort;
	} else {
		root.className = "rationale " + element.type.toLowerCase();
		locationText = "Code comment section at position (start line, end line, sequence # in comment) "
			+ locationTextShort;
	}

	desc.className = "content";
	desc.innerText = element.summary + element.description;
	loc.className = "loc";
	loc.innerText = locationText;

	root.title = locationText;
	root.dataset.ratType = element.type.toLowerCase();

	root.setAttribute("id",
		btoa(lastBranch.branchName + "-" + element.source));

    img = document.createElement("img");
    img.src = element.image;
	root.appendChild(img);
	root.appendChild(desc);
	root.appendChild(loc);

	return root;
}

function getFileLocationShort(fileDecKnowKey) {
	console.debug("getFileLocationShort");
	var PATH_DEPTH = 2;
	shortNameArr = [];
	longNameArr = fileDecKnowKey.split("/");
	while (shortNameArr.unshift(longNameArr.pop()) && shortNameArr.length < PATH_DEPTH) {
		/* NOP: unshifting of array ellemts is done in while construct */
	}
	return shortNameArr.join("/");
}

function getEmptyElementAsHTML() {
	console.debug("getEmptyElementAsHTML");
	var emptyE = document.createElement("p");
	emptyE.className = "empty";
	emptyE.innerText = RATIONALE_IN_OLDER_FILE_NOT_EXIST;
	return emptyE;
}

function getCodeElementsFromSide(blockData) {
	console.debug("getCodeElementsFromSide");
	var codeElements = document.createElement("p");
	var rationaleElements = blockData.codeElements;
	codeElements.className = "fileA";

	if (rationaleElements.length > 0) {
		for (var r = 0; r < rationaleElements.length; r++) {
			codeElement = rationaleElements[r];
			codeElements.appendChild(codeElement);
		}
	} else {
		codeElement = getEmptyElementAsHTML();
		codeElements.appendChild(codeElement);
	}
	return codeElements;
}

function appendCodeElements(brNode) {
	console.debug("appendCodeElements");
	blockLinesIterator = lastBranchBlocks.entries();
	while ((blockEntry = blockLinesIterator.next())) {
		if (blockEntry.done) {
			break;
		}
		var blockKey = blockEntry.value[0];
		var blockData = blockEntry.value[1];

		var fileRatElement = document.createElement("p");

		/* Start: decode blockKey */
		var block = blockKey.split(" ");
		var blockIsDiffType = false;
		var blockEntry = "";
		if (block.length >= 3) {
			blockIsDiffType = block[0].indexOf("1") === 0;
			blockEntry = block[2];
		}
		/* End: decode blockKey */

		var fileRatBlockLabel = document.createElement("p");
		fileRatBlockLabel.dataset.blockSequence = blockData.sequence;
		/* rationale changed? */
		if (blockIsDiffType) {
			/* get A side rationale elements */
			var codeElements = getCodeElementsFromSide(blockData);
			fileRatElement.appendChild(codeElements);

			/* add diff edit label */
			fileRatBlockLabel.innerText = blockData.filename + " - " + blockEntry;
		} else {
			fileRatBlockLabel.innerText = RATIONALE_NO_CHANGES_TEXT.replace("{file}", blockData.filename);
		}

		fileRatBlockLabel.className = "fileNonDiffBlockLabel";
		fileRatElement.className = "fileNonDiffBlock";

		/* get B side rationale elements */
		codeElements = getCodeElementsFromSide(blockData);
		fileRatElement.appendChild(codeElements);

		brNode.appendChild(fileRatBlockLabel);
		brNode.appendChild(fileRatElement);
	}
}

function getBlock(element, counter) {
	var block = {};
	block.diffType = true;
	block.entry = " " + element.source;

	block.toString = function() {
		return "1 - " + block.entry;
	};
	return block;
}

function appendBranchMessageElementsHtml(elementsFromMessage, parentNode) {
	if (elementsFromMessage !== null && elementsFromMessage.length > 0) {
		/* group rationale in messages by commit hash */
		var msgCommitIsh = "";
		var messageBlockHtml = null;
		for (m = 0; m < elementsFromMessage.length; m++) {
			if (msgCommitIsh !== elementsFromMessage[m].keyData.source) {
				msgCommitIsh = elementsFromMessage[m].keyData.source;
				if (messageBlockHtml) {
					/* add previous message */
					parentNode.appendChild(messageBlockHtml);
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
				var messageElementHtml = getElementAsHTML(elementsFromMessage[m], true);
				messageBlockHtml.appendChild(messageElementHtml);
			}
		}
		parentNode.appendChild(messageBlockHtml);
	}
}

function appendBranchCodeElementsHtml(elementsFromCode, parentNode) {
	var blockCounter = 0;
	var previousBlock = null;

	for (c = 0; c < elementsFromCode.length; c++) {
		codeElementHtml = getElementAsHTML(elementsFromCode[c], false);

		var block = getBlock(elementsFromCode[c], blockCounter);

		if (previousBlock === null) {
			previousBlock = block;
		}
		if (block.diffType !== previousBlock.diffType || block.entry !== previousBlock.entry) {
			blockCounter++;
			block.sequence = blockCounter;
		}
		previousBlock = block;

		var blockKey = block.toString();

		if (!lastBranchBlocks.has(blockKey)) {
			blockData = {
				codeElements: [],
				filename: "",
				sequence: blockCounter
			};
			lastBranchBlocks.set(blockKey, blockData);
		}

		var blockData = lastBranchBlocks.get(blockKey);
		blockData.filename = elementsFromCode[c].source;
		blockData.codeElements.push(codeElementHtml);
		lastBranchBlocks.set(blockKey, blockData);
	}
	appendCodeElements(parentNode);
}

function appendBranchLabel(parentNode, branch) {
	branchLabel = document.createElement("p");
	branchLabel.className = "branchLabel";
	branchLabel.innerText = branch.branchName;
	parentNode.appendChild(branchLabel);
}

function appendBranchQualityAssessment(parentNode, index) {
	qualitySummary = document.createElement("p");
	qualitySummary.id = "branchGroup-" + index + "-qualitySummary";
	qualitySummary.className = "qualitySummary";
	if ((lastBranchElementsFromMessages && lastBranchElementsFromMessages.length > 0)
		|| (lastBranchElementsFromFiles && lastBranchElementsFromFiles.length > 0)) {
		qualitySummary.innerText = NO_QUALITY_PROBLEMS_IN_BRANCH;
		qualitySummary.classList.add("noProblems");
	} else {
		qualitySummary.innerText = NO_QUALITY_PROBLEMS_FOR_NO_RATIONALE_IN_BRANCH;
		qualitySummary.classList.add("noRationale");
	}
	parentNode.appendChild(qualitySummary);
}

/*
 * render feature branch in HTML
 */
function showBranchDiff(data, index) {
	console.debug("showBranchDiff");
	if (!data) {
		return alert("received empty invalid data");
	}

	branchContainer = document.createElement("p");
	branchContainer.id = "branchGroup-" + index;
	branchContainer.className = "branchGroup";

	/* show user the branch name */
	appendBranchLabel(branchContainer, data);
	/* show user the quality assessment for rationale observed in modified files */
	appendBranchQualityAssessment(branchContainer, index);
	/* show user the rationale observed in modified files */	
	appendBranchMessageElementsHtml(data.commitElements, branchContainer);
	appendBranchCodeElementsHtml(data.codeElements, branchContainer);

	/* append branch HTMl to parent HTML container */
	contentHtml.appendChild(branchContainer);
}

function appendForceRestFetch(parentNode) {
	forceRestNode = document.createElement("p");
	forceRestNode.className = "condec-rest-force";
	forceRestNode.innerText = "Suspecting branch list is not up-to date? Click here to try again.";
	forceRestNode.addEventListener("click", function(e) {
		getBranchesDiff(true);
	});
	parentNode.appendChild(forceRestNode);
}

function showBranchesDiff(branches) {
	console.debug("showBranchesDiff");
	if (branches === null || branches === undefined) {
		contentHtml.innerText = "Git extraction is disabled.";
		return;
	}
	branches.timestamp = Date.now();
	localStorage.setItem(url, JSON.stringify(branches, null, 1));
	contentHtml = document.getElementById("featureBranches-container");
	contentHtml.innerText = "";

	if (branches.length > 0) {
		/* branches come not sorted from rest */
		branches = branches.sort(function(a, b) {
			if (a.branchName < b.branchName) {
				return -1;
			} else {
				return 1;
			}
		});

		for (branchIdx = 0; branchIdx < branches.length; branchIdx++) {
			lastBranch = conDecLinkBranchCandidates.extractPositions(branches[branchIdx]);
			lastBranchIdx = branchIdx;
			lastBranchBlocks = new Map();

			/* these elements are sorted by commit age and occurrence in message */
			lastBranchElementsFromMessages = lastBranch.commitElements;
			/* these elements are not sorted */
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

		var branchLabels = contentHtml.getElementsByClassName("branchLabel");
		var messageLabels = contentHtml.getElementsByClassName("commitMessageLabel");
		var blockForNonDiffElements = contentHtml.getElementsByClassName("fileNonDiffBlockLabel");
		var elementNodes = contentHtml.getElementsByClassName("rationale");

		addSelectionHelperForContainer(branchLabels);
		attachClickEventsOnBranchLabels(branchLabels);

		addSelectionHelperForContainer(messageLabels);
		attachClickEventsOnCommitMessageLabels(messageLabels);

		attachClickEventsOnBlockLabels(blockForNonDiffElements);

		addInvisibleButCopyableElementTypeTags(elementNodes, null); /*
																	 * 2nd
																	 * argument
																	 * "messageBox"
																	 */
		attachClickEventsOnRationale(elementNodes, null); /*
															 * 2nd argument
															 * "messageBox"
															 */

	} else {
		contentHtml.innerText = "No feature branches found for this issue.";
	}
	appendForceRestFetch(contentHtml);
}

/* BEGIN: UI decoration section */
/*
 * could be done with 2 times fewer lines with jQuery, but if possible lets stay
 * away from jQuery for such simple things.
 */

function attachClickEventsOnRationale(elements) {
	var expander = function(event) {
		var content = event.target.parentElement;
		if (!content && !content.classList) {
			console.error("Problem in attachClickEventsOnRationale");
			return;
		}
		if (content.classList.contains("detailed")) {
			content.classList.remove("detailed");
		} else {
			content.classList.add("detailed");
		}
	};
	attachClickEvents(elements, expander, "click to hide/show details");
}

function attachClickEventsOnBlockLabels(labels) {
	var hider = function(event) {
		var content = event.target.nextElementSibling;
		if (!content && !content.classList) {
			console.error("no sibling found for " + event.target.id);
			return;
		}
	};
	attachClickEvents(labels, hider, "click to hide/show");
}

function attachClickEvents(elements, handler, hoverTitle) {
	if (elements && elements.length > 0) {
		for (var i = 0; i < elements.length; i++) {
			var element = elements[i];
			element.addEventListener("click", handler);
			element.title = hoverTitle;
		}
	}
}

function attachClickEventsOnCommitMessageLabels(labels) {
	attachClickEventsRollingUpParents(labels);
}

function attachClickEventsOnBranchLabels(labels) {
	attachClickEventsRollingUpParents(labels);
}

function attachClickEventsRollingUpParents(labels) {
	var hider = function(event) {
		var parentContainer = event.target.parentElement;
		if (!parentContainer && !parentContainer.classList) {
			console.error("Parent not found for " + event.target.id);
			return;
		}
		if (parentContainer.classList.contains("rolledUp")) {
			parentContainer.classList.remove("rolledUp");
		} else {
			parentContainer.classList.add("rolledUp");
		}
	};

	if (labels && labels.length > 0) {
		for (var i = 0; i < labels.length; i++) {
			var label = labels[i];
			label.addEventListener("click", hider);
			label.title = "click to roll up/show";
		}
	}
}
function addInvisibleButCopyableElementTypeTags(elementNodes, classFilter) {
	if (elementNodes && elementNodes.length > 0) {
		for (var i = 0; i < elementNodes.length; i++) {
			var rationaleNode = elementNodes[i];
			if (!classFilter || rationaleNode.classList.contains(classFilter)) {
				contentList = rationaleNode.getElementsByClassName("content");
				/* read type from html */
				rationaleType = rationaleNode.dataset.ratType;
				if (contentList && rationaleType) {
					rationaleTextParagraphNode = contentList[0];
					if (rationaleTextParagraphNode) {
						/* render tags */
						var startTag = document.createElement("span");
						var endTag = document.createElement("span");

						startTag.innerText = "[" + rationaleType + "]";
						endTag.innerText = "[/" + rationaleType + "]";
						rationaleTextParagraphNode.prepend(startTag); /*
																		 * not
																		 * supported
																		 * by
																		 * IE,
																		 * EdgeMobile
																		 */
						rationaleTextParagraphNode.appendChild(endTag);
					}
				}
			}
		}
	}
}

/*
 * for each label element adds a sibling node with onclick function for
 * selecting contents of parent container
 */
function addSelectionHelperForContainer(labels) {
	var contentSelector = function(event) {
		console.log("contentSelector");
		var parentContainer = event.target.parentElement;
		if (!parentContainer) {
			console.error("Parent not found for " + event.target.id);
			return;
		}
		var selObj = window.getSelection();
		selObj.removeAllRanges();
		var range = document.createRange();
		range.selectNode(parentContainer);
		selObj.addRange(range);
	};

	if (labels && labels.length > 0) {
		for (var i = 0; i < labels.length; i++) {
			var label = labels[i];
			var container = label.parentElement;

			var selectorNode = document.createElement("div");
			selectorNode.className = "selector";
			selectorNode.title = "Select container contents";
			selectorNode.addEventListener("click", contentSelector);
			container.appendChild(selectorNode);
		}
	}
}

function listRatClasses() {
	Array.from(document.getElementsByClassName("rationale")).map(function(e) {
		console.log(e.className);
	})
}