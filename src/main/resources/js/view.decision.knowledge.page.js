function initializeDecisionKnowledgePage() {
    for (var index = 0; index < knowledgeTypes.length; index++) {
        var isSelected = "";
        if (knowledgeTypes[index] === "Decision") {
            isSelected = "selected ";
        }
        $("select[name='select-root-element-type']")[0].insertAdjacentHTML("beforeend", "<option " + isSelected + " value='"
            + knowledgeTypes[index] + "'>" + knowledgeTypes[index] + "</option>");
    }
    var createElementButton = document.getElementById("create-element-button");
    var elementInputField = document.getElementById("element-input-field");
    createElementButton.addEventListener("click", function () {
        var summary = elementInputField.value;
        var type = $("select[name='select-root-element-type']").val();
        elementInputField.value = "";
        createDecisionKnowledgeElement(summary, "", type, function (id) {
            updateView(id);
        });
    });

    var depthOfTreeInput = document.getElementById("depth-of-tree-input");
    depthOfTreeInput.addEventListener("input", function () {
        var depthOfTreeWarningLabel = document.getElementById("depth-of-tree-warning");
        if (this.value > 0) {
            depthOfTreeWarningLabel.style.visibility = "hidden";
            updateView();
        } else {
            depthOfTreeWarningLabel.style.visibility = "visible";
        }
    });
    //Add Event Listeners handlers for import end export Tree Buttons
    var downloadElementButton = document.getElementById("download-decisions-button");
    downloadElementButton.addEventListener("click", function () {
        console.log("projectId",getProjectKey());
        getLinkedElements(10209,function(response){
            console.log("linkedToIt",response)
            download("myJson",JSON.stringify(response));

            function download(filename, text) {
                console.log("filename",filename)

                var element = document.createElement('a');
                element.setAttribute('href', 'data:text/plain;charset=utf-8,' + encodeURIComponent(text));
                element.setAttribute('download', filename);

                element.style.display = 'none';
                document.body.appendChild(element);

                element.click();

                document.body.removeChild(element);
            }
        });



    });
    var uploadElementButton = document.getElementById("upload-decisions-button");
    var uploadField= document.getElementById("file-upload-field")
    uploadElementButton.addEventListener("click", function () {
        $(uploadField).trigger("click")
        getDecisionKnowledgeElement(10209,function(response){
            console.log("myresponse",response)
        });

    });
}
function updateView(nodeId) {
	buildTreeViewer();
	if (nodeId === undefined) {
		var rootElement = getCurrentRootElement();
		if (rootElement) {
			selectNodeInTreeViewer(rootElement.id);
		}
	} else {
		selectNodeInTreeViewer(nodeId);
	}
	$('#jstree').on("select_node.jstree", function(error, tree) {
		var node = tree.node.data;
		buildTreant(node.key, true);
	});
}
