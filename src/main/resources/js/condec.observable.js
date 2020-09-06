/*
 This module provides the notification method for the ConDec views. This module is the subject/observable 
 according to the observer design pattern. The views/observers subscribe/register to this observable. 
 The views need to implement an updateView function. 
 The updateView functions of the subscribed views are called in the notify function.
 
 Registered/subscribed views/observers can be
 * conDecKnowledgePage
 * conDecTabPanel
 * and many other views that implement an updateView() function
    
 Is required by
 * condec.knowledge.page
 * condec.tab.panel
 * and many other views that implement an updateView() function
 * 
 */
(function(global) {

	var observers = null;

	var ConDecObservable = function () {
		this.observers = [];
	};

	ConDecObservable.prototype.notify = function () {
		this.observers.forEach(function(observer) {
			observer.updateView();
		});
	};

	ConDecObservable.prototype.subscribe = function (observer) {
		this.observers.push(observer);
	};

	global.conDecObservable = new ConDecObservable();
})(window);