
function hideSelectedDecisionElements(element){
	var decisionElements =["isIssue","isDecision","isAlternative","isPro","isCon"]
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
	document.getElementById("dialog-content").id = "jstree";
	buildTreeViewer2();


	//fillTree();
}

function buildTreeViewer2() {
	resetTreeViewer();
	getTreeViewerWithoutRootElement( function(core) {
		$("#jstree").jstree({
			"core" : core,
			"plugins" : [ "dnd", "contextmenu", "wholerow", "sort", "search" ],
			"search" : {
				"show_only_matches" : true
			},
			"contextmenu" : {
				"items" : contextMenuActions
			}
		});
		$("#jstree-search-input").keyup(function() {
			var searchString = $(this).val();
			$("#jstree").jstree(true).search(searchString);
		});
	});
	addDragAndDropSupportForTreeViewer();
	changeHoverStyle();
}


function fillTree(){
	var index = 1;
	$('#jstree').on('ready.jstree', function (e, data) {
		do{//loop through all comments
			comment = document.getElementById("comment"+index);
			if(comment){
				var sentences = comment.getElementsByClassName("sentence");
				for (var i = sentences.length - 1; i >= 0; i--) { //loop through all sentences in comments
					$('#jstree').jstree('create_node', $("#jstree"), { "text":sentences[i].innerHTML, "id":index+"-"+i }, "first", false, false);
				}
				index = index+1;
			}
		}while(document.getElementById("comment"+index));
	});
}

function createNode(parent_node, new_node_id, new_node_text, position) {
	$('#jstree').jstree('create_node', $(parent_node), { "text":new_node_text, "id":new_node_id }, position, false, false);

}
