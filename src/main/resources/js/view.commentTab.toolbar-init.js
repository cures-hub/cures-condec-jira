/*
 *This class manages the insertion of knowledge types into the rich text exitor. Also the transition between stlyed (Rich text) and plan text with taggs.
 */
require([ "jquery", "jira/util/formatter", "jira/editor/registry" ], function($, formatter, editorRegistry) {
	var DEFAULT_PLACEHOLDER = "knowledge element";
	var lastItem;

	editorRegistry.on('register', function(entry) {
		var $otherDropdown = $(entry.toolbar).find('.wiki-edit-other-picker-trigger');

		$otherDropdown.one('click', function(dropdownClickEvent) {

			var speechItem = getDropDownContent(dropdownClickEvent).querySelector('.wiki-edit-speech-item');
			var issueItem = $(getHTMLLiItem("Issue")).insertAfter(speechItem).on('click', function() {
				entry.applyIfTextMode(addWikiMarkupIssue).applyIfTextMode(addRenderedContentIssue);
			});

			var alternativeItem = $(getHTMLLiItem("Alternative")).insertAfter(issueItem).on('click', function() {
				entry.applyIfTextMode(addWikiMarkupAlternative).applyIfVisualMode(addRenderedContentAlternative);
			});

			var decisionItem = $(getHTMLLiItem("Decision")).insertAfter(alternativeItem).on('click', function() {
				entry.applyIfTextMode(addWikiMarkupDecision).applyIfVisualMode(addRenderedContentDecision);
			});

			var proItem = $(getHTMLLiItem("Pro")).insertAfter(decisionItem).on('click', function() {
				entry.applyIfTextMode(addWikiMarkupPro).applyIfVisualMode(addRenderedContentPro);
			});

			var conItem = $(getHTMLLiItem("Con")).insertAfter(proItem).on('click', function() {
				entry.applyIfTextMode(addWikiMarkupCon).applyIfVisualMode(addRenderedContentCon);
			});

			entry.onUnregister(function cleanup() {
				issueItem.remove();
				alternativeItem.remove();
				decisionItem.remove();
				proItem.remove();
				conItem.remove();
			});
		});
	});

	function getHTMLLiItem(knowledgeType) {
		return '<li><a href="#" class="li-' + knowledgeType + '-dropdown-entry" data-operation="' + knowledgeType
				+ '">' + knowledgeType + '</a></li>';
	}

	function addWikiMarkup(entry, knowledgeType) {
		var wikiEditor = $(entry.textArea).data('wikiEditor');
		var content = wikiEditor.manipulationEngine.getSelection().text || DEFAULT_PLACEHOLDER;
		wikiEditor.manipulationEngine.replaceSelectionWith("{" + knowledgeType + "}" + content + "{" + knowledgeType
				+ "}");
	}

	function addRenderedContent(entry, knowledgeTypeColor, knowledgeType) {
		entry.rteInstance.then(function(rteInstance) {
			var tinyMCE = rteInstance.editor;
			if (tinyMCE && !tinyMCE.isHidden()) {
				var content = tinyMCE.selection.getContent() || DEFAULT_PLACEHOLDER;
				tinyMCE.selection.setContent('{' + knowledgeType + '}' + content + '{' + knowledgeType + '}');
			}
		});
	}

	function getDropDownContent(dropdownClickEvent) {
		var dropdownContentId = dropdownClickEvent.currentTarget.getAttribute('aria-owns');
		return document.getElementById(dropdownContentId);
	}

	function addWikiMarkupIssue(entry) {
		addWikiMarkup(entry, "issue");
	}

	function addRenderedContentIssue(entry) {
		addRenderedContent(entry, "F2F5A9", "issue");
	}

	function addWikiMarkupAlternative(entry) {
		addWikiMarkup(entry, "alternative");
	}

	function addRenderedContentAlternative(entry) {
		addRenderedContent(entry, "f1ccf9", "alternative");
	}

	function addWikiMarkupDecision(entry) {
		addWikiMarkup(entry, "decision");
	}

	function addRenderedContentDecision(entry) {
		addRenderedContent(entry, "c5f2f9", "decision");
	}

	function addWikiMarkupPro(entry) {
		addWikiMarkup(entry, "pro");
	}

	function addRenderedContentPro(entry) {
		addRenderedContent(entry, "b9f7c0", "pro");
	}

	function addWikiMarkupCon(entry) {
		addWikiMarkup(entry, "con");
	}

	function addRenderedContentCon(entry) {
		addRenderedContent(entry, "ffdeb5", "con");
	}

});