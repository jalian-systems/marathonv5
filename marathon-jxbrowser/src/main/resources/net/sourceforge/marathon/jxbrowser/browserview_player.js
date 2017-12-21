Element.prototype.matches = Element.prototype.matchesSelector
		|| Element.prototype.mozMatchesSelector
		|| Element.prototype.msMatchesSelector
		|| Element.prototype.oMatchesSelector
		|| Element.prototype.webkitMatchesSelector ;

if (!Element.prototype.matches) {
	Element.prototype.matches = function(s) {
				var matches = (this.document || this.ownerDocument)
						.querySelectorAll(s), i = matches.length;
				while (--i >= 0 && matches.item(i) !== this) {
				}
				return i > -1;
			};
}

function WebViewPlayer() {
}

WebViewPlayer.prototype.click = function(selector) {
	var el = document.querySelector(selector);
	if(el == null)
		return ;
	el.scrollIntoView();

	var evt = new MouseEvent("click", {
	    bubbles: true,
	    cancelable: true,
	    view: window
    });
    el.dispatchEvent(evt);
}

WebViewPlayer.prototype.select = function(selector, value) {
	var target = document.querySelector(selector);
	if(target == null)
		return false;
	target.scrollIntoView();
	var values = [ 'input[type="text"]', 'input[type="password"]', 'input[type="color"]',
	               'input[type="date"]', 'input[type="datetime"]', 'input[type="datetime-local"]',
	               'input[type="number"]', 'input[type="range"]', 'input[type="search"]',
	               'input[type="tel"]', 'input[type="time"]', 'input[type="url"]',
	               'input[type="week"]', 'input[type="email"]', 'input[type="file"]',
	               'input[type="month"]',
	               function() { return this.matches('input') && this.getAttribute('type') === null ;}];
		
	var matched = values.find(function(v) { return typeof v === 'function' ? v.call(target) : target.matches(v); });
	if(matched) {
		target.value = value ;
		return true;
	} else if(target.matches('input[type="checkbox"]') || target.matches('input[type="radio"]')) {
		target.checked = (value === "true");
		return true;
	} else if(target.matches('select')) {
	   var selected = JSON.parse(value);
	   selected_values = [];
	   for(var i = 0; i < target.options.length; i++) {
	   	 if(selected.includes(target.options[i].text))
	   	   selected_values.push(target.options[i].value);
	   }
	   target.value = selected_values;
	   return true;
	} else if(target.matches('textarea')) {		
		target.value = value;	
	   return true;
	} else {
		return true;
	}
};

WebViewPlayer.prototype.text = function(selector) {
	if(document.querySelector(selector).innerText)
		return document.querySelector(selector).innerText.trim();
	return null;
};

WebViewPlayer.prototype.label = function(selector) {
		var id = document.querySelector(selector).getAttribute('id');
		if(id) {
			var labels = document.querySelectorAll('label');
			for(var i = 0; i < labels.length; i++) {
				var forId = labels[i].getAttribute('for');
				if(forId === id)
					return labels[i].innerText.trim();
			}
		}
		return null;
};

WebViewPlayer.prototype.attributes = function(selector) {
	var e = document.querySelector(selector);
	var r = {};
	e.getAttributeNames().forEach(function(attr) {
		r[attr] = e.getAttribute(attr);
	});
	return JSON.stringify(r);
};

WebViewPlayer.prototype.value = function(selector) {
	if(document.querySelector(selector).value !== undefined)
		return String(document.querySelector(selector).value).trim();
	return null;
};

$marathon_player = new WebViewPlayer();
