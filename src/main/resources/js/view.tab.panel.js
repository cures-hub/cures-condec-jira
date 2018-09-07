
function hideSelectedDecisionElements(element){
	var decisionElements =["Issue","Decision","Alternative","Pro","Con"]
	var sentences = document.getElementsByClassName(element.id);
	if(element.id != "isRelevant"){
		for (var i = sentences.length - 1; i >= 0; i--) {
			if ( element.checked) {
				sentences[i].style.visibility = 'visible';
			}
			console.log(sentences.innerHTML);
			if ( !element.checked) {
				sentences[i].style.visibility = 'collapse';
			}

		}
	}else if(element.id == "isRelevant"){

		var sentences = document.getElementsByClassName("isNotRelevant");

		for (var i = sentences.length - 1; i >= 0; i--) {
			if (element.checked) {
				sentences[i].style.visibility = 'visible';
			}
			console.log(sentences.innerHTML);
			if ( !element.checked) {
				sentences[i].style.visibility = 'collapse';
			}
		}

	}

}
function callDialogFromView() {
	var submitButton = document.getElementById("dialog-submit-button");
	submitButton.textContent = "Save";
	submitButton.onclick = function() {
		AJS.dialog2("#dialog").hide();
	};



	setUpDialog();
	var header = document.getElementById("dialog-header");
	header.textContent = "Edit and Link Decision Knowledge in Issue Comments";
}


function callDialog2(){

	callDialogFromView();
	closeDialog();
	callDialogFromView();
	closeDialog();
	callDialogFromView();
	document.getElementById("dialog-content").innerHTML = "<div id =header2> </div> <div id =jstree> </div> ";
	document.getElementById("header2").innerHTML = "<input class=text medium-long-field id=jstree-search-input placeholder=Search decision knowledge />";
	buildTreeViewer2(document.getElementById("Relevant").checked);

}

function buildTreeViewer2(showRelevant) {
	resetTreeViewer();
	getTreeViewerWithoutRootElement(showRelevant, function(core) {
		$("#jstree").jstree({
			"core" : core,
			"plugins" : [ "dnd", "contextmenu", "wholerow", "search","sort"],
			"search" : {
				"show_only_matches" : true
			},
			"contextmenu" : {
				"items" : contextMenuActionsForSentences
			},
			"sort": function(a, b) {
		        a1 = this.get_node(a);
		        b1 = this.get_node(b);
		        if(a1.id > b1.id){
		        	return 1;
		        }else{
		        	return -1;
		        }
		         } 
			});
		$("#jstree-search-input").keyup(function() {
			var searchString = $(this).val();
			$("#jstree").jstree(true).search(searchString);
		});
	});
	addSentenceDragAndDropSupportForTreeViewer();
	document.getElementById("jstree").addEventListener("mousemove",bringContextMenuToFront); 
}

function bringContextMenuToFront(){
	if(document.getElementsByClassName("vakata-context").length > 0){
		document.getElementsByClassName("vakata-context")[0].style.zIndex = 9999;
	}
	
}

function addSentenceDragAndDropSupportForTreeViewer() {
	$("#jstree").on('move_node.jstree', function(object, nodeInContext) {
		var node = nodeInContext.node;
		var parentNode = getTreeViewerNodeById(nodeInContext.parent);
		var oldParentNode = getTreeViewerNodeById(nodeInContext.old_parent);

		var nodeId = node.data.id;
		if (oldParentNode === "#" && parentNode !== "#") {
			createSentenceLinkToExistingElement(parentNode.data.id, nodeId);
		}
		if (parentNode === "#" && oldParentNode !== "#") {
			deleteSentenceLink(oldParentNode.data.id, nodeId, function() {});
		}
		if (parentNode !== '#' && oldParentNode !== '#') {
			deleteSentenceLink(oldParentNode.data.id, nodeId, function() {
				createSentenceLinkToExistingElement(parentNode.data.id, nodeId);
			});
		}
	});
}

function createSentenceLinkToExistingElement(idOfExistingElement, idOfNewElement, knowledgeTypeOfChild) {
	switchLinkTypes(knowledgeTypeOfChild, idOfExistingElement, idOfNewElement, function(linkType, idOfExistingElement,
			idOfNewElement) {
		linkSentences(idOfExistingElement, idOfNewElement, linkType, function() {
		});
	});
}


function replaceQuotes(comment){
	console.log("hier")
	return comment.replaceFirst("\{quote\}","<blockquote>").replaceFirst("\{quote\}","</blockquote>")
}

