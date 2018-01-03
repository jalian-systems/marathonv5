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
	var el = this.findElement(selector);
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

WebViewPlayer.prototype.findElement = function(selector) {
	if(selector.indexOf('xpath:') == 0)
		return document.evaluate(selector.substring(6), document, null, XPathResult.FIRST_ORDERED_NODE_TYPE, null).singleNodeValue;
	else if(selector.indexOf('id:') == 0)
		return document.getElementById(selector.substring(3));
	else if(selector.indexOf('tagname:') == 0) {
		var els = document.getElementsByTagName(selector.substring(8));
		if(els.length > 0)
			return els[0];
		return null;
	} else if(selector.indexOf('name:') == 0) {
		var els = document.getElementsByName(selector.substring(5));
		if(els.length > 0)
			return els[0];
		return null;
	} else if(selector.indexOf('classname:') == 0) {
		var els = document.getElementsByClassName(selector.substring(10));
		if(els.length > 0)
			return els[0];
		return null;
	} else {
		return document.querySelector(selector);
	}
}

WebViewPlayer.prototype.exists = function(selector) {
	var target = this.findElement(selector);
	if(target == null)
		return false;
	return true;
}

WebViewPlayer.prototype.select = function(selector, value) {
	var target = this.findElement(selector);
	if(target == null)
		return false;
	var values = [ 'input[type="text"]', 'input[type="password"]', 'input[type="color"]',
	               'input[type="date"]', 'input[type="datetime"]', 'input[type="datetime-local"]',
	               'input[type="number"]', 'input[type="range"]', 'input[type="search"]',
	               'input[type="tel"]', 'input[type="time"]', 'input[type="url"]',
	               'input[type="week"]', 'input[type="email"]', 'input[type="file"]',
	               'input[type="month"]',
	               function() { return this.matches('input') && this.getAttribute('type') === null ;}];
		
	var matched = values.find(function(v) { return typeof v === 'function' ? v.call(target) : target.matches(v); });
	target.scrollIntoView();
	if(matched || target.matches('textarea')) {
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
	} else {
		return true;
	}
};

WebViewPlayer.prototype.text = function(selector) {
	if(this.value(selector) != null)
		return this.value(selector);
	if(this.findElement(selector).innerText)
		return this.findElement(selector).innerText.trim();
	return null;
};

WebViewPlayer.prototype.label = function(selector) {
		var id = this.findElement(selector).getAttribute('id');
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
	var e = this.findElement(selector);
	var r = {};
	var attrs = e.attributes;
	for(var i = 0; i < attrs.length; i++)
	  r[attrs[i].name] = attrs[i].value;
	return JSON.stringify(r);
};

WebViewPlayer.prototype.value = function(selector) {
	if(this.findElement(selector).value !== undefined)
		return String(this.findElement(selector).value).trim();
	return null;
};

$marathon_player = new WebViewPlayer();
