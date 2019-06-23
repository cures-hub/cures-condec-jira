var contentHtml;
var lastBranch, br, brText, el, loc, desc, img, msgE, codeE, messageElementHtml;
var lastBranchDiffLines =  new Map();
var NEWER_FILE_NOT_EXIST = "file got deleted"
var OLDER_FILE_NOT_EXIST = "file did not exist"
var RATIONALE_IN_NEWER_FILE_NOT_EXIST = "rationale got removed"
var RATIONALE_IN_OLDER_FILE_NOT_EXIST = "no rationale did exist before"


var branches = []

function getBranchesDiff() {
	contentHtml = document.getElementById("featureBranches-container");
	contentHtml.innerText = "Loading ..."
	/*
		var issueId = AJS.$("meta[name='ajs-issue-key']").attr("content");
		if (issueId === undefined) {
			issueId = this.getIssueKey();
		}*/
	var issue = JIRA.Issue.getIssueKey();
	AJS.$.ajax(
	{ url: AJS.contextPath()
	 + "/rest/decisions/latest/view/elementsFromBranchesOfJiraIssue.json?issueKey="
	 + issue
	, type: 'get'
	, dataType: 'json'
	, async: false
	, success: showBranchesDiff
	, error: showError
	});
}

function showError(error) {
	console.debug("showError")
	contentHtml.innerText = "error"
	console.log(error)
}

function getMessageElements(elements) {
	console.debug("getMessageElements")
	return  elements.filter(function(e) { return e.key.sourceTypeCommitMessage} )
}

function getCodeElements(elements) {
	console.debug("getCodeElements")
	return  elements.filter(function(e) { return e.key.sourceTypeCodeFile} )
}

function getJiraBaseUri() {
	return "http://localhost:2990/jira/";
}

function getIcon(type) {
	console.debug("getIcon")
	if (type == "pro" || type == "con")
		type = "argument_"+type
	img = document.createElement("img")
	path = getJiraBaseUri()
	+ "download/resources/de.uhd.ifi.se.decision.management.jira:stylesheet-and-icon-resources/"
	img.src = path+type+".png"
	return img;
}

function getElementAsHTML(element,isFromMessage) {
	console.debug("getElementAsHTML")
	el = document.createElement("p")
	desc = document.createElement("p")
	loc = document.createElement("p")

	var locationText = ""

	if (isFromMessage) {
		el.className = "messageBox "+element.type.toLowerCase()
		locationText = "Message: "
			+ element.key.source
			+ " at position (start line,positionInText) "
			+ element.key.position;
	}
	else {
		el.className = element.type.toLowerCase()
		locationText = element.key.position;
	}
	desc.className = "content";
	desc.innerText = element.summary + element.description;
	loc.className = "loc"
	loc.innerText = locationText
	loc.title = locationText

	el.appendChild(getIcon(element.type.toLowerCase()))
	el.appendChild(desc)
	el.appendChild(loc)

	return el
}

function getFileLocationShort(fileDecKnowKey) {
	console.debug("getFileLocationShort")
	var PATH_DEPTH = 2
	shortNameArr = []
	longNameArr = fileDecKnowKey.split("/")
	while (shortNameArr.unshift(longNameArr.pop()) && shortNameArr.length<PATH_DEPTH) {
	}
	return shortNameArr.join("/");
}

function getEmptyElementAsHTML(forNewerFile) {
	console.debug("getEmptyElementAsHTML")
	var emptyE = document.createElement("p")
	emptyE.className = "empty"
	if (forNewerFile) {
		emptyE.innerText = RATIONALE_IN_NEWER_FILE_NOT_EXIST
	} else {
		emptyE.innerText = RATIONALE_IN_OLDER_FILE_NOT_EXIST
	}
	return emptyE
}

function getFilenameForHTML(diffLine, forNewerFile) {
	console.debug("getFilenameForHTML")
	var filename
	if (forNewerFile) {
		filename = diffLine.filenameB
	} else{
		filename = diffLine.filenameA
	}

	if (filename.trim() !== "") {
		return filename;
	} else {
		if (forNewerFile) {
			return NEWER_FILE_NOT_EXIST
		} else {
			return OLDER_FILE_NOT_EXIST
		}
	}
}
function sortRationaleDiffOfFile(rationale) {
/* rationale should appear in the order it was found in code */
	rationale.sort(function(a,b) {
		// different files
		if (a.key.source<b.key.source) {
			return -1
		}
		if (a.key.source>b.key.source) {
			return 1
		}
		// same file different lines
		if (a.key.positionStartLine<b.key.positionStartLine) {
			return -1
		}
		if (a.key.positionStartLine>b.key.positionStartLine) {
			return 1
		}

		// same file same line different position on line
		if (a.key.positionCursor<b.key.positionCursor) {
			return -1
		}
		if (a.key.positionCursor<b.key.positionCursor) {
			return 1
		}

		// same file same line same position on line
		return 0
	});
	return rationale
}

function getCodeElementsFromSide(diffLine, newerSide) {
	console.debug("getCodeElementsFromSide")
	var codeElements = document.createElement("p")
	var fileName = document.createElement("p")
	var rationaleElements

	if (newerSide) {
		codeElements.className = "fileB"
		rationaleElements = diffLine.B
	} else {
		codeElements.className = "fileA"
		rationaleElements = diffLine.A
	}
	fileName.className = "filename"

	fileName.innerText = getFilenameForHTML(diffLine,newerSide)
	codeElements.appendChild(fileName)


	if (rationaleElements.length>0) {
		for (var r = 0 ; r < rationaleElements.length; r++) {
			codeElement = rationaleElements[r]
			codeElements.appendChild(codeElement)
		}
	}
	else {
		codeElement = getEmptyElementAsHTML(newerSide)
		codeElements.appendChild(codeElement)
	}
	return codeElements
}

function appendDiffElements(brNode) {
	console.debug("appendDiffElements")
	diffLinesIterator = lastBranchDiffLines.entries()
	while (diffLineEntry = diffLinesIterator.next()) {
		if (diffLineEntry.done) {
			break
		}
		var diffLineKey = diffLineEntry.value[0]
		var diffLine = diffLineEntry.value[1]

		var fileRatEditLineLabel = document.createElement("p")
		var fileRatEditLine = document.createElement("p")
		fileRatEditLineLabel.innerText = diffLineKey
		fileRatEditLineLabel.className = "fileDiffBlockLabel"
		fileRatEditLine.className = "fileDiffBlock"

		// get A side rationale elements
		var codeElements = getCodeElementsFromSide(diffLine, false)
		fileRatEditLine.appendChild(codeElements)

		// get B side rationale elements
		codeElements = getCodeElementsFromSide(diffLine, true)
		fileRatEditLine.appendChild(codeElements)

		brNode.appendChild(fileRatEditLineLabel)
		brNode.appendChild(fileRatEditLine)
	}
}

function appendBranchElements(brNode,elements) {
	console.debug("appendBranchElements")
	msgE = getMessageElements(elements)
	codeE = getCodeElements(elements)

	if (msgE!=null) {
	//brNode.appendChild(getMessagesSectionLabel)
		for ( m = 0; m< msgE.length; m++){
			messageElementHtml = getElementAsHTML(msgE[m],true)
			brNode.appendChild(messageElementHtml)
		}
	}

	if (codeE!=null) {
		codeE = sortRationaleDiffOfFile(codeE)

		for ( c = 0; c < codeE.length; c++){
			codeElementBHtml = getElementAsHTML(codeE[c],false)

			diffLineKey = codeE[c].key.diffEntrySequence
				+ codeE[c].key.diffEntry

			if (!lastBranchDiffLines.has(diffLineKey)) {
				diffLine = { A: [], B: [], filenameA: "", filenameB: "" }
				lastBranchDiffLines.set(diffLineKey,diffLine)
			}

			var diffLine =lastBranchDiffLines.get(diffLineKey)
			if (codeE[c].key.codeFileA) {
				diffLine.filenameA = codeE[c].key.source
				diffLine.A.push(codeElementBHtml)
			}
			else if (codeE[c].key.codeFileB) {
				diffLine.filenameB = codeE[c].key.source
				diffLine.B.push(codeElementBHtml)
			}
			lastBranchDiffLines.set(diffLineKey,diffLine)
		}
		appendDiffElements(brNode)
	}
}

function showBranchDiff(data,index) {
	console.debug("showBranchDiff")
	console.log(data,index)
	if (data == null)  {
		return alert("received empty invalid data")
	}
	contentHtml.innerText = ""

	br = document.createElement("p")
	brText = document.createElement("p")

	br.id = "branchGroup"+index
	br.className = "branchGroup"
	brText.className = "text"

	brText.innerText = data.branchName
	br.appendChild(brText)

	contentHtml.appendChild(br)
	appendBranchElements(br, data.elements)
}

function extractPositions(branchData) {
	elements = branchData.elements.map(function(e) {
		positionComponents = e.key.position.split(":")
		positionComponentsNumber = positionComponents.length
		if (positionComponentsNumber == 2 || positionComponentsNumber == 3) {
			e.key.positionStartLine = parseInt(positionComponents[0])
			e.key.positionCursor = parseInt(positionComponents[positionComponentsNumber-1])
		}
		if (positionComponentsNumber==3) {
			e.key.positionEndLine = parseInt(positionComponents[positionComponentsNumber-2])
		}
		return e
	})
	branchData.elements = elements
	return branchData
}

function showBranchesDiff(data) {
	console.debug("showBranchesDiff")
	contentHtml = document.getElementById("featureBranches-container");
	console.log(data)
	for (b=0;b<data.branches.length;b++)
	{
		lastBranch = extractPositions(data.branches[b])
		branches.push(lastBranch)
		showBranchDiff(lastBranch,b)
	}
}

//showBranchDiff(lastBranch)
//getBranchesDiff()
