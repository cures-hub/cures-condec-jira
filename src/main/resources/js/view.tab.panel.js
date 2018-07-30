function isOdd(num) {
	return num % 2;
}

function dosth(a) {
	var comments = document.getElementsByClassName("comment");
	var commentsBody = document.getElementsByClassName("commentBody");
	for (var i = 0; i < comments.length; i++) {
		if (commentsBody[i].textContent.includes("[" + a.id + "]") && a.checked) {
			commentsBody[i].style.visibility = 'visible';
			comments[i].style.visibility = 'visible';
		}

		if (commentsBody[i].innerHTML.includes("[" + a.id + "]") && !a.checked) {
			commentsBody[i].style.visibility = 'collapse';
			comments[i].style.visibility = 'collapse';
		}
	}
}

function initTabPanel() {
	var exportButton = document.getElementById("export-button");

	exportButton.addEventListener("click", function() {
//		var submitButton = document.getElementById("dialog-submit-button");
//		submitButton.textContent = "Save";
//
//		var extensionButton = document.getElementById("dialog-extension-button");
//		extensionButton.style.visibility = "hidden";

		AJS.dialog2("#dialog").show();
	});
}