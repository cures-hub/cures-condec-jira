
function hideSelectedDecisionElements(element){
	var decisionElements =["Issue","Decision","Alternative","Pro","Con"]
	var sentences = document.getElementsByClassName(element.id);
	if(element.id != "Relevant"){
		setVisibility(sentences,element.checked);
	}else if(element.id == "Relevant"){
		var sentences = document.getElementsByClassName("isNotRelevant");
		setVisibility(sentences,element.checked);
	}
}

function setVisibility(sentences, checked){
	for (var i = sentences.length - 1; i >= 0; i--) {
		if (checked) {
			sentences[i].style.visibility = 'visible';
		}
		if (!checked) {
			sentences[i].style.visibility = 'collapse';
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
	document.getElementById("dialog").classList.remove("aui-dialog2-medium");
	document.getElementById("dialog").classList.add("aui-dialog2-large");
	buildTreeViewer2(document.getElementById("Relevant").checked);
}

function includeJQ(){
    var startingTime = new Date().getTime();
    // Load the script
    var script = document.createElement("SCRIPT");
    script.src = '//code.jquery.com/jquery-1.9.1.js';
    script.type = 'text/javascript';
    script.onload = function() {
    	var $ = window.jQuery;
    	window.$ = window.jQuery; 
    	console.log($.fn.jquery)
      $(function() {
            buildTreeViewer2(true);
        });
    };
    document.getElementsByTagName("head")[0].appendChild(script);
}


function buildTreeViewer2(showRelevant) {
	console.log("build Tree")
	console.log(jQuery.fn.jquery);

	getTreeViewerWithoutRootElement(showRelevant, function(core) {
		jQueryConDec("#jstree").jstree({
			"core" : core,
			"plugins" : [ "dnd", "contextmenu", "wholerow", "search","sort","state"],
			"search" : {
				"show_only_matches" : true
			},
			"contextmenu" : {
				"items" : customContextMenu
			},
			"sort": sortfunction
			});
		$("#jstree-search-input").keyup(function() {
			var searchString = $(this).val();
			jQueryConDec("#jstree").jstree(true).search(searchString);
		});
	});
	addDragAndDropSupportForTreeViewer();
	document.getElementById("jstree").addEventListener("mousemove",bringContextMenuToFront); 
}

function sortfunction(a, b) {
	a1 = this.get_node(a);
	b1 = this.get_node(b);
	if(a1.id > b1.id){
		return 1;
	}else{
		return -1;
	}
} 

function customContextMenu(node) {
     if (node.li_attr['class'] == "sentence") {
        return contextMenuActionsForSentences;
    }else{
    	return ;
    }  
}

function bringContextMenuToFront(){
	if(document.getElementsByClassName("vakata-context").length > 0){
		document.getElementsByClassName("vakata-context")[0].style.zIndex = 9999;
	}
	
}


function createSentenceLinkToExistingElement(idOfExistingElement, idOfNewElement, knowledgeTypeOfChild) {
	switchLinkTypes(knowledgeTypeOfChild, idOfExistingElement, idOfNewElement, function(linkType, idOfExistingElement,
			idOfNewElement) {
		linkSentences(idOfExistingElement, idOfNewElement, linkType, function() {
		});
	});
}



