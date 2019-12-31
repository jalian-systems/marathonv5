if (!Element.prototype.matches) {
	Element.prototype.matches = Element.prototype.matchesSelector
			|| Element.prototype.mozMatchesSelector
			|| Element.prototype.msMatchesSelector
			|| Element.prototype.oMatchesSelector
			|| Element.prototype.webkitMatchesSelector
			|| function(s) {
				var matches = (this.document || this.ownerDocument)
						.querySelectorAll(s), i = matches.length;
				while (--i >= 0 && matches.item(i) !== this) {
				}
				return i > -1;
			};
}

if (typeof Object.assign != 'function') {
	Object.assign = function(target) {
		'use strict';
		if (target == null) {
			throw new TypeError('Cannot convert undefined or null to object');
		}

		target = Object(target);
		for (var index = 1; index < arguments.length; index++) {
			var source = arguments[index];
			if (source != null) {
				for ( var key in source) {
					if (Object.prototype.hasOwnProperty.call(source, key)) {
						target[key] = source[key];
					}
				}
			}
		}
		return target;
	};
}

if (typeof Object.closest != 'function') {
	Object.closest = function(s) {
		'use strict';
		var r = this;
		while (r != null && !r.matches(s))
			r = r.parentElement;
		return r;
	};
}

if (!Element.prototype.label) {
	Element.prototype.label = function() {
		var id = this.getAttribute('id');
		if (id != null) {
			var labels = document.querySelectorAll('label');
			for (var i = 0; i < labels.length; i++) {
				var forId = labels[i].getAttribute('for');
				if (forId === id)
					return labels[i].innerText.trim();
			}
		}
		return null;
	}
}

if (!Element.prototype.css) {
	Element.prototype.css = function() {
		var parent = this.parentElement;
		while (parent != null) {
			if ($marathon.isContainer(parent)) {
				break;
			}
			parent = parent.parentElement;
		}
		return this.cssFrom(parent);
	}
}

if (!Element.prototype.cssFrom) {
	Element.prototype.cssFrom = function(root_node) {
		$marathon.csg.setOptions({
			root_node : root_node
		});
		return $marathon.csg.getSelector(this);
	}
}

if (!Element.prototype.xpath) {
	Element.prototype.xpath = function() {
		var path = $marathon.xPath(this, true);
		var count = document.evaluate('count(' + path + ')', document, null,
				XPathResult.NUMBER_TYPE, null).numberValue;
		if (count > 1)
			return $marathon.xPath(this, false);
		return path;
	}
}

if (!Element.prototype.link_text) {
	Element.prototype.link_text = function() {
		if (this.tagName.toLowerCase() === 'a')
			return this.innerText.trim();
		return null;
	}
}

window.matches = function(s) {
	return s.toLowerCase() === 'window';
}

function Marathon(port, paused, insertingScript) {
	var _this = this;
	var csg_options = {
		get_options : function(elm) {
			var opts = $recorder_options.css_selector_options.filter(
					function(option) {
						return elm.matches(option.css);
					}).map(function(option) {
				return option.options;
			});
			opts.unshift({});
			return Object.assign.apply(null, opts);
		}
	};
	this.csg = new CssSelectorGenerator(csg_options);
	this.url = "ws://127.0.0.1:" + port + "/";
	this.identity = null;
	this.omapLoaded = false;
	this.rawRecording = "false";
	this.paused = paused;
	this.insertingScript = insertingScript;
	console.log("Connecting to server @ " + this.url);
	this.resolvers = [ {
		canhandle : function(evt) {
			return true;
		},
		onchange : function(evt) {
			_this.handleChangeEvent(evt);
		},
		onkeypress : function(evt) {
			_this.handleKeyPressEvent(evt);
		},
		onmousedown : function(evt) {
			_this.handleMouseDownEvent(evt);
		},
		onclick : function(evt) {
			_this.handleClickEvent(evt);
		}
	} ];
	this.createSocket(this.url);
}

Marathon.prototype.createSocket = function(url) {
	var _this = this;

	var ws = new WebSocket(url);
	ws.onopen = function() {
		console.log("Connected to server @ ", ws);
		ws.onmessage = _this.onMessage;
		_this.post = function(method, data) {
			var message = {
				method : method,
				data : JSON.stringify(data)
			};
			ws.send(JSON.stringify(message));
		}
		ws.onclose = function(evt) {
			console.log("Closed connection to " + url);
			_this.wsClosed = true;
		}
		_this.addEventHandlers();
		_this.assertionWindow = new Marathon.AssertionWindow(function(target,
				selection) {
			$marathon.recordAssertion(target, selection);
		}, function(target, selection) {
			$marathon.recordWait(target, selection);
		});
		_this.assertionWindow.getProperties = function(target) {
			return $marathon.findAssertionProperties(target);
		}
		_this.windowAlert = window.alert
		window.alert = function() {
			_this.windowAlert.apply(window, arguments);
			var event = {
				type : 'alert',
				method : 'alert'
			}
			if (arguments.length > 0)
				event.text = arguments[0];
			event.container = _this.getContainerDetails(window);
			_this.post("alert", event);
			return res;
		};
		_this.windowConfirm = window.confirm;
		window.confirm = function() {
			var res = _this.windowConfirm.apply(window, arguments);
			var event = {
				type : 'alert',
				method : 'confirm'
			}
			event.result = res ? 'OK' : 'Cancel';
			if (arguments.length > 0)
				event.text = arguments[0];
			event.container = _this.getContainerDetails(window);
			_this.post("alert", event);
			return res;
		};
		_this.windowPrompt = window.prompt;
		window.prompt = function() {
			var res = _this.windowPrompt.apply(window, arguments);
			var event = {
				type : 'alert',
				method : 'prompt'
			}
			if (res != null)
				event.result = res;
			if (arguments.length > 0)
				event.text = arguments[0];
			event.container = _this.getContainerDetails(window);
			_this.post("alert", event);
			return res;
		};
		_this.wsClosed = false;
	}

	ws.onclose = function(evt) {
		console.log("Trying reconnect again to " + url);
		_this.createSocket(url);
	};
}

Marathon.prototype.recordAssertion = function(target, selection) {
	if (selection.selection)
		this.postEvent(target, {
			type : 'assert',
			method : 'assert_contains',
			property : selection.property,
			value : selection.selection
		});
	else
		this.postEvent(target, {
			type : 'assert',
			property : selection.property,
			value : selection.value
		});
}

Marathon.prototype.recordComment = function(comment) {
	var record = {};
	record.event = {
		type : 'comment',
		comment : comment
	};
	record.request = 'record-comment';
	this.post('record', record);
}

Marathon.prototype.recordWait = function(target, selection) {
	if (selection.selection)
		this.postEvent(target, {
			type : 'wait',
			method : 'wait_contains',
			property : selection.property,
			value : selection.selection
		});
	else
		this.postEvent(target, {
			type : 'wait',
			property : selection.property,
			value : selection.value
		});
}

Marathon.prototype.reloadScript = function() {
	this.post('reloadScript', {});
}

Marathon.prototype.addEventHandlers = function() {
	var _this = this;
	document.addEventListener('contextmenu', function(evt) {
		if (_this.isAssertionWindowHotkey(evt))
			evt.preventDefault();
	}, true);
	document.addEventListener('mousemove', function(evt) {
		if (_this.paused || _this.insertingScript)
			return;
		if (evt.target.matches('#popUpWindow-top, #popUpWindow-top *'))
			return;
		_this.mouseMoveTarget = evt.target;
	}, true);
	document.addEventListener('keydown', function(evt) {
		if (!evt.isTrusted)
			return;
		if (_this.paused || _this.insertingScript)
			return;
		if (evt.target.matches('#popUpWindow-top, #popUpWindow-top *'))
			return;
		if (evt.altKey && evt.shiftKey && !evt.repeat) {
			evt.preventDefault();
			var target = _this.mouseMoveTarget || evt.target;
			if (evt.code === 'KeyH') {
				_this.postEvent(target, {
					type : 'hover',
					suffix : _this.getSuffix(target)
				});
			}
			if (evt.code === "F8" || evt.code == 'KeyP') {
				_this.printObjectIdentities();
				evt.preventDefault();
				return;
			}
			var val = _this.getProperty(target, 'text');
			if (val == null)
				return;
			var text = val[1];
			if (text && evt.code === 'KeyA') {
				_this.postEvent(target, {
					type : 'assert',
					property : 'text',
					value : text
				});
			}
			if (text && evt.code === 'KeyW') {
				_this.postEvent(target, {
					type : 'wait',
					property : 'text',
					value : text
				});
			}
		}
	}, true);
	document.addEventListener('change', function(evt) {
		if (_this.paused || _this.insertingScript)
			return;
		if (evt.target.matches('#popUpWindow-top, #popUpWindow-top *'))
			return;
		var resolver = _this.resolvers.find(function(resolver) {
			return resolver.canhandle(evt);
		});
		resolver.onchange && resolver.onchange(evt);
	}, true);
	document.addEventListener('mousedown', function(evt) {
		if (_this.paused || _this.insertingScript)
			return;
		if (evt.target.matches('#popUpWindow-top, #popUpWindow-top *'))
			return;
		var resolver = _this.resolvers.find(function(resolver) {
			return resolver.canhandle(evt);
		});
		resolver.onmousedown && resolver.onmousedown(evt);
	}, true);
	document.addEventListener('click', function(evt) {
		if (_this.paused || _this.insertingScript)
			return;
		if (evt.target.matches('#popUpWindow-top, #popUpWindow-top *'))
			return;
		var resolver = _this.resolvers.find(function(resolver) {
			return resolver.canhandle(evt);
		});
		resolver.onclick && resolver.onclick(evt);
	}, true);
	document.addEventListener('keypress', function(evt) {
		if (_this.paused || _this.insertingScript)
			return;
		if (evt.target.matches('#popUpWindow-top, #popUpWindow-top *'))
			return;
		var resolver = _this.resolvers.find(function(resolver) {
			return resolver.canhandle(evt);
		});
		resolver.onkeypress && resolver.onkeypress(evt);
	}, true);
	var MutationObserver = window.MutationObserver
			|| window.WebKitMutationObserver || window.MozMutationObserver;
	var list = document.querySelector('body');

	var observer = new MutationObserver(
			function(mutations) {
				var reloadScript = false;
				mutations
						.forEach(function(mutation) {
							if (mutation.type === 'childList'
									&& mutation.addedNodes) {
								var addedNodes = mutation.addedNodes;
								for (var i = 0; i < addedNodes.length; i++) {
									if (addedNodes[i].tagName
											&& addedNodes[i].tagName
													.toLowerCase() === 'iframe')
										reloadScript = true;
								}
							}
						});
				if (reloadScript && window.parent === window)
					_this.reloadScript();
			});

	observer.observe(list, {
		attributes : true,
		childList : true,
		subtree : true,
		characterData : true
	});
}

Marathon.prototype.getName = function(props) {
	var parts = [];
	for ( var prop in props) {
		if (props.hasOwnProperty(prop)) {
			parts.push(props[prop]);
		}
	}
	return parts.join(':');
}

Marathon.prototype.getContainer = function(target) {
	var container = {};

	var headContainer = container;
	var current = target.parentElement;
	while (current != null) {
		if (this.isContainer(current)) {
			container.container = this.getContainerDetails(current);
			container = container.container;
		}
		current = current.parentElement;
	}

	container.container = this.getContainerDetails(window);
	return headContainer.container;
}

Marathon.prototype.isContainer = function(target) {
	for (var i = 0; i < this.containerRecognitionProperties.length; i++) {
		var np = this.containerRecognitionProperties[i];
		if (target.matches(np.className)) {
			return true;
		}
	}
	return false;
}

Marathon.prototype.getContainerDetails = function(target) {
	var container = {};
	if (this.identity !== null && target === window) {
		container.attributes = Object.assign({}, this.identity.attributes);
		Object.assign(container.attributes, this.findGeneralProperties(target));
		container.urp = Object.assign({}, this.identity.urp);
		if (container.attributes.suggestedName && !container.attributes.title)
			container.attributes.title = container.attributes.suggestedName;
	} else {
		var title = this.getName(this.findMatchingProperties(
				'containerNamingProperties', target, false));
		if (!title || title == '')
			title = this.getName(this.findMatchingProperties(
					'namingProperties', target, false));
		container.attributes = this.findGeneralProperties(target);
		container.attributes.title = title;
		container.attributes.suggestedName = title;
		container.urp = this.findMatchingProperties('recognitionProperties',
				target);
	}
	container.containerURP = this.findMatchingProperties(
			'containerRecognitionProperties', target, false);
	if (container.containerURP == null) {
		container.containerURP = {
			css : target.css()
		};
	}
	if (target === window) {
		if (container.urp == null)
			container.urp = {};
		container.urp.title = title;
	}
	if (target === window) {
		if (window.parent === window) {
			container.container_type = "window";
		} else {
			container.container_type = "frame";
		}
	} else {
		container.container_type = target.tagName.toLowerCase();
	}
	if (target === window && target.parent !== target) {
		container.container = target.parent.$marathon
				.getContainerDetails(target.parent)
	}
	return container;
}

Marathon.prototype.getSuffix = function(target) {
	if (target.tagName.toLowerCase() === "input") {
		return (target.getAttribute('type') === null ? "text" : target
				.getAttribute('type'));
	} else {
		return target.tagName.toLowerCase();
	}
}

Marathon.prototype.handleChangeEvent = function(evt) {
	if (!evt.isTrusted)
		return;
	if (this.rawRecording === "true")
		return;
	var target = evt.target;
	var v = this.value(target);
	if (v != null)
		this.postEvent(target, {
			type : 'select',
			value : v,
			suffix : this.getSuffix(target)
		});
}

Marathon.prototype.shouldIgnoreClick = function(target) {
	var values = [
			'input[type="text"]',
			'input[type="password"]',
			'input[type="color"]',
			'input[type="date"]',
			'input[type="datetime"]',
			'input[type="datetime-local"]',
			'input[type="number"]',
			'input[type="range"]',
			'input[type="search"]',
			'input[type="tel"]',
			'input[type="time"]',
			'input[type="url"]',
			'input[type="week"]',
			'input[type="email"]',
			'input[type="file"]',
			'input[type="checkbox"]',
			'input[type="radio"]',
			'input[type="month"]',
			'select',
			'option',
			'textarea',
			function() {
				return this.matches('label')
						&& this.getAttribute('for') !== null;
			},
			function() {
				return this.matches('input')
						&& this.getAttribute('type') === null;
			} ];

	var matched = values.find(function(v) {
		return typeof v === 'function' ? v.call(target) : target.matches(v);
	});
	if (matched)
		return true;
	return false;
}

Marathon.prototype.handleMouseDownEvent = function(evt) {
	if (!evt.isTrusted)
		return;
	this.currentMouseDownTarget = evt.target;
	this.postMouseEvent(evt, true);
}

Marathon.prototype.handleKeyPressEvent = function(evt) {
	if (!evt.isTrusted || this.rawRecording === "false")
		return;
	var modifier = "";
	if (evt.altKey)
		modifier = modifier + "Alt+";
	if (evt.ctrlKey)
		modifier = modifier + "Ctrl+";
	if (evt.shiftKey)
		modifier = modifier + "Shift+";
	if (evt.metaKey)
		modifier = modifier + "Meta+";
	if (modifier.length > 0)
		modifier = modifier.slice(0, modifier.length - 1)
	if (modifier.length > 0)
		$marathon.postEvent(evt.target, {
			type : 'key_raw',
			keyCode : evt.key.toUpperCase(),
			modifiersEx : modifier
		});
	else
		$marathon.postEvent(evt.target, {
			type : 'key_raw',
			keyChar : evt.key
		});
}

Marathon.prototype.handleClickEvent = function(evt) {
	if (!evt.isTrusted)
		return;
	if (this.currentMouseDownTarget
			&& this.currentMouseDownTarget === evt.target)
		return;
	this.postMouseEvent(evt, false);
}

Marathon.prototype.postMouseEvent = function(evt, delayed) {
	if (this.isAssertionWindowHotkey(evt))
		return;
	var target = evt.target;
	if (this.shouldIgnoreClick(target) === true
			&& this.rawRecording === "false")
		return;
	if (delayed) {
		setTimeout(function() {
			$marathon.postEvent(target, {
				type : 'click',
				clickCount : evt.detail == 0 ? 1 : evt.detail,
				button : evt.button + 1,
				modifiersEx : "",
				x : 0,
				y : 0,
				suffix : $marathon.getSuffix(target)
			});
		}, 0);
	} else
		$marathon.postEvent(target, {
			type : 'click',
			clickCount : evt.detail == 0 ? 1 : evt.detail,
			button : evt.button + 1,
			modifiersEx : "",
			x : 0,
			y : 0,
			suffix : $marathon.getSuffix(target)
		});
}

Marathon.prototype.isAssertionWindowHotkey = function(evt) {
	var clicked = "";
	if (evt.ctrlKey)
		clicked = clicked + "C";
	if (evt.altKey)
		clicked = clicked + "A";
	if (evt.metaKey)
		clicked = clicked + "M";
	if (evt.shiftKey)
		clicked = clicked + "S";
	clicked = clicked + evt.which;
	return clicked === this.modifierString;
}

Marathon.prototype.postEvent = function(target, event) {
	var identity = this.getObjectIdentity(target);
	var record = identity.identity;
	if (identity.target !== null) {
		target = identity.target.target;
		event.cellinfo = identity.target.cellinfo;
	}
	record.event = event;
	record.request = 'record-action';
	record.container = this.getContainer(target);
	this.post('record', record);
}

Marathon.prototype.getSuggestedName = function(target) {
	return this.getName(this.findMatchingProperties('namingProperties', target,
			false));
}

Marathon.prototype.needsExtendedInfo = function(element) {
	if (window.$recorder_options && $recorder_options.with_extended_info) {
		return $recorder_options.with_extended_info.find(function(matcher) {
			if (typeof (matcher) == 'string')
				return element.matches(matcher);
			return matcher(element);
		});
	}
}

Marathon.prototype.computeTarget = function(target) {
	var current = target.parentElement;
	while (current != null) {
		if (this.needsExtendedInfo(current)) {
			return {
				target : current,
				cellinfo : target.cssFrom(current)
			};
		}
		current = current.parentElement;
	}

	return null;
}

Marathon.prototype.getObjectIdentity = function(target) {
	var identity = {};
	var computedTarget = this.computeTarget(target);
	if (computedTarget !== null)
		target = computedTarget.target;
	identity.attributes = this.findGeneralProperties(target);
	identity.urp = this.findMatchingProperties('recognitionProperties', target);
	identity.attributes.suggestedName = this.getSuggestedName(target);
	return {
		identity : identity,
		target : computedTarget
	};
}

Marathon.prototype.printObjectIdentities = function() {
	var all = document.querySelector("body").querySelectorAll('*');

	for (var i = 0, max = all.length; i < max; i++) {
		this.recordComment(JSON
				.stringify(this.getObjectIdentity(all[i]).identity.attributes));
	}
}

Marathon.prototype.setContainerIdentity = function(jsonStr) {
	this.identity = JSON.parse(jsonStr).identity;
}

Marathon.prototype.value = function(target) {
	var values = [
			'input[type="text"]',
			'input[type="password"]',
			'input[type="color"]',
			'input[type="date"]',
			'input[type="datetime"]',
			'input[type="datetime-local"]',
			'input[type="number"]',
			'input[type="range"]',
			'input[type="search"]',
			'input[type="tel"]',
			'input[type="time"]',
			'input[type="url"]',
			'input[type="week"]',
			'input[type="email"]',
			'input[type="file"]',
			'input[type="month"]',
			function() {
				return this.matches('input')
						&& this.getAttribute('type') === null;
			} ];

	var matched = values.find(function(v) {
		return typeof v === 'function' ? v.call(target) : target.matches(v);
	});
	if (matched)
		return target.value;

	if (target.matches('input[type="checkbox"]')
			|| target.matches('input[type="radio"]'))
		return "" + target.checked;

	if (target.matches('select')) {
		var result = [];
		for (var i = 0; i < target.options.length; i++) {
			if (target.options[i].selected)
				result.push(target.options[i].text);
		}
		return JSON.stringify(result);
	}

	if (target.matches('textarea'))
		return target.value;

	console.log('Unhandled getValue', target);
	return null;
}

Marathon.prototype.getProperty = function(target, props) {
	if (props === 'tag_name') {
		if (target.tagName)
			return [ 'tag_name', target.tagName.toLowerCase() ];
		return null;
	} else if (props === 'text') {
		var text = target.innerText;
		if (text && text.trim() && text.trim().length < 30)
			return [ 'text', text.trim() ];
		return null;
	}
	var user_given_values = props.match(/([^:]*):(.*)/)
	if (user_given_values !== null) {
		user_given_values.shift();
		return user_given_values;
	}
	var aProps = props.split('.');
	var ret = target;
	aProps.forEach(function(prop) {
		if (ret != null)
			ret = $marathon.getImmediateProperty(ret, prop);
	});
	if (!ret || !ret.toString().trim())
		return null;
	return [ props, ret.toString() ];
}

Marathon.prototype.getImmediateProperty = function(target, prop) {
	var ret = null;
	if (target.getAttribute
			&& (ret = $marathon.getAttribute(target, prop)) != null)
		return ret;
	if (prop == 'id' || prop == 'class')
		return ret;
	if (target[prop] && !this.isFunction(target[prop]))
		return target[prop];
	if (target[prop] && this.isFunction(target[prop])
			&& (ret = target[prop].call(target)) != null)
		return ret;
	return ret;
}

Marathon.prototype.findGeneralProperties = function(target) {
	var propmap = {};
	this.generalProperties.forEach(function(prop) {
		var val = $marathon.getProperty(target, prop);
		if (val != null)
			propmap[val[0]] = val[1];
	});
	if (target.attributes)
		for (var i = 0, atts = target.attributes, n = atts.length; i < n; i++) {
			propmap[atts[i].nodeName] = atts[i].nodeValue;
		}
	return propmap;
}

Marathon.prototype.findAssertionProperties = function(target) {
	var propmap = {};
	if (target.attributes)
		for (var i = 0, atts = target.attributes, n = atts.length; i < n; i++) {
			propmap[atts[i].nodeName] = atts[i].nodeValue;
		}
	propmap['tag_name'] = target.tagName.toLowerCase();
	var text = target.innerText;
	if (text)
		propmap['text'] = text.trim();
	if (target.value)
		propmap['value'] = target.value.trim();
	if (target.matches('input[type="checkbox"]')
			|| target.matches('input[type="radio"]')) {
		propmap['selected?'] = '' + target.checked;
	}
	propmap['enabled?'] = target.disabled ? '' + false : '' + true;
	return propmap;
}

Marathon.prototype.isUnique = function(target, props) {
	var others = document.getElementsByTagName(target.tagName);
	for (var i = 0; i < others.length; i++) {
		if (target == others[i])
			continue;
		var other = others[i];
		var matched = true;
		for ( var prop in props) {
			if (prop === 'css' || prop === 'xpath') {
				matched = false;
				break;
			}
			if (matched && props.hasOwnProperty(prop)) {
				var otherValue = this.getProperty(other, prop);
				if (otherValue === null || otherValue[1] !== props[prop])
					matched = false;
			}
		}
		if (matched)
			return false;
	}
	return true;
}

Marathon.prototype.findMatchingProperties = function(properties, target, unique) {
	unique = typeof unique !== 'undefined' ? unique : true;
	for (var i = 0; i < this[properties].length; i++) {
		var np = this[properties][i];
		if (target.matches(np.className)) {
			var arrName = {};
			var rb = true;
			np.properties.forEach(function(prop) {
				if (rb) {
					var val = $marathon.getProperty(target, prop);
					if (val == null) {
						rb = false;
					} else
						arrName[val[0]] = val[1];
				}
			});
			if (rb && (!unique || this.isUnique(target, arrName)))
				return arrName;
		}
	}
	return null;
}

Marathon.prototype.isFunction = function(obj) {
	return !!(obj && obj.constructor && obj.call && obj.apply);
};

Marathon.prototype.setRawRecording = function(obj) {
	this.rawRecording = obj.value;
}

Marathon.prototype.setRecordingPause = function(obj) {
	this.paused = obj.value == 'true';
}

Marathon.prototype.setInsertingScript = function(obj) {
	this.insertingScript = obj.value == 'true';
}

Marathon.prototype.onMessage = function(evt) {
	var jsonEvt = JSON.parse(evt.data);
	$marathon[jsonEvt.method].call($marathon, JSON.parse(jsonEvt.data));
}

Marathon.prototype.toString = function() {
	return "Marathon[" + this.url + "]";
}

Marathon.prototype.setContextMenuTriggers = function(jsonTriggers) {
	var modifiers = jsonTriggers.menuModifiers.split('+');
	var ms = "";
	if (modifiers.indexOf("Ctrl") != -1)
		ms = ms + "C";
	if (modifiers.indexOf("Alt") != -1)
		ms = ms + "A";
	if (modifiers.indexOf("Meta") != -1)
		ms = ms + "M";
	if (modifiers.indexOf("Shift") != -1)
		ms = ms + "S";
	if (modifiers.indexOf("Button1") != -1)
		ms = ms + "1";
	if (modifiers.indexOf("Button2") != -1)
		ms = ms + "2";
	if (modifiers.indexOf("Button3") != -1)
		ms = ms + "3";
	this.modifierString = ms;
}

Marathon.prototype.setObjectMapConfig = function(jsonObjectMap) {
	var lists = [ 'namingProperties', 'recognitionProperties',
			'containerNamingProperties', 'containerRecognitionProperties' ];

	var generalProperties = new Set(jsonObjectMap['generalProperties']);
	var _this = this;
	lists.forEach(function(list) {
		var nplist = [];
		jsonObjectMap[list].forEach(function(np) {
			np.propertyLists.map(function(propertyList) {
				propertyList.className = np.className;
				nplist.push(propertyList);
				propertyList.properties.forEach(function(p) {
					if (p.match(/([^:]*):(.*)/) === null)
						generalProperties.add(p);
				});
			});
		});
		nplist = nplist.sort(function(a, b) {
			if (a.className.toLowerCase() === b.className.toLowerCase())
				return b.priority - a.priority;
			if (a.className === '*')
				return 1;
			if (b.className === '*')
				return -1;
			return a.className.toLowerCase().localeCompare(
					b.className.toLowerCase());
		});
		_this[list] = nplist;
	});
	generalProperties.add('tag_name');
	generalProperties.add('link_text');
	_this['generalProperties'] = Array.from(generalProperties);
	_this.omapLoaded = true;
	if (window.parent === window)
		_this.post('focusedWindow', {
			container : _this.getContainerDetails(window)
		});
}

/*
 * Copyright (C) 2011 Google Inc. All rights reserved. Copyright (C) 2007, 2008
 * Apple Inc. All rights reserved. Copyright (C) 2008 Matt Lilek
 * <webkit@mattlilek.com> Copyright (C) 2009 Joseph Pecoraro
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer. 2. Redistributions in
 * binary form must reproduce the above copyright notice, this list of
 * conditions and the following disclaimer in the documentation and/or other
 * materials provided with the distribution. 3. Neither the name of Apple
 * Computer, Inc. ("Apple") nor the names of its contributors may be used to
 * endorse or promote products derived from this software without specific prior
 * written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY APPLE AND ITS CONTRIBUTORS "AS IS" AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL APPLE OR ITS CONTRIBUTORS BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

/**
 * @param {!WebInspector.DOMNode}
 *            node
 * @param {boolean=}
 *            optimized
 * @return {string}
 */
Marathon.prototype.xPath = function(node, optimized) {
	if (node.nodeType === Node.DOCUMENT_NODE)
		return "/";

	var steps = [];
	var contextNode = node;
	while (contextNode) {
		var step = $marathon._xPathValue(contextNode, optimized);
		if (!step)
			break; // Error - bail out early.
		steps.push(step);
		if (optimized && step.optimized)
			break;
		contextNode = contextNode.parentNode;
	}

	steps.reverse();
	return (steps.length && steps[0].optimized ? "" : "/") + steps.join("/");
}

/**
 * @param {!WebInspector.DOMNode}
 *            node
 * @param {boolean=}
 *            optimized
 * @return {?Marathon.DOMNodePathStep}
 */
Marathon.prototype._xPathValue = function(node, optimized) {
	var ownValue;
	var ownIndex = $marathon._xPathIndex(node);
	if (ownIndex === -1)
		return null; // Error.

	switch (node.nodeType) {
	case Node.ELEMENT_NODE:
		if (optimized && $marathon.getAttribute(node, "id"))
			return new $marathon.DOMNodePathStep("//*[@id=\""
					+ $marathon.getAttribute(node, "id") + "\"]", true);
		ownValue = node.localName;
		break;
	case Node.ATTRIBUTE_NODE:
		ownValue = "@" + node.tagName.toLowerCase();
		break;
	case Node.TEXT_NODE:
	case Node.CDATA_SECTION_NODE:
		ownValue = "text()";
		break;
	case Node.PROCESSING_INSTRUCTION_NODE:
		ownValue = "processing-instruction()";
		break;
	case Node.COMMENT_NODE:
		ownValue = "comment()";
		break;
	case Node.DOCUMENT_NODE:
		ownValue = "";
		break;
	default:
		ownValue = "";
		break;
	}

	if (ownIndex > 0)
		ownValue += "[" + ownIndex + "]";

	return new $marathon.DOMNodePathStep(ownValue,
			node.nodeType === Node.DOCUMENT_NODE);
}

/**
 * @param {!WebInspector.DOMNode}
 *            node
 * @return {number}
 */
Marathon.prototype._xPathIndex = function(node) {
	// Returns -1 in case of error, 0 if no siblings matching the same
	// expression, <XPath index among the same expression-matching sibling
	// nodes> otherwise.
	function areNodesSimilar(left, right) {
		if (left === right)
			return true;

		if (left.nodeType === Node.ELEMENT_NODE
				&& right.nodeType === Node.ELEMENT_NODE)
			return left.localName === right.localName;

		if (left.nodeType === right.nodeType)
			return true;

		// XPath treats CDATA as text nodes.
		var leftType = left.nodeType === Node.CDATA_SECTION_NODE ? Node.TEXT_NODE
				: left.nodeType;
		var rightType = right.nodeType === Node.CDATA_SECTION_NODE ? Node.TEXT_NODE
				: right.nodeType;
		return leftType === rightType;
	}

	var siblings = node.parentNode ? node.parentNode.children : null;
	if (!siblings)
		return 0; // Root node - no siblings.
	var hasSameNamedElements;
	for (var i = 0; i < siblings.length; ++i) {
		if (areNodesSimilar(node, siblings[i]) && siblings[i] !== node) {
			hasSameNamedElements = true;
			break;
		}
	}
	if (!hasSameNamedElements)
		return 0;
	var ownIndex = 1; // XPath indices start with 1.
	for (var i = 0; i < siblings.length; ++i) {
		if (areNodesSimilar(node, siblings[i])) {
			if (siblings[i] === node)
				return ownIndex;
			++ownIndex;
		}
	}
	return -1; // An error occurred: |node| not found in parent's children.
}

/**
 * @constructor
 * @param {string}
 *            value
 * @param {boolean}
 *            optimized
 */
Marathon.prototype.DOMNodePathStep = function(value, optimized) {
	this.value = value;
	this.optimized = optimized || false;
}

Marathon.prototype.DOMNodePathStep.prototype.toString = function() {
	return this.value;
}

Marathon.prototype.getAttribute = function(node, attr) {
	var val = node.getAttribute(attr);
	if (val && window.$recorder_options && attr == 'id'
			&& $recorder_options.id_exclude_regex) {
		if ($recorder_options.id_exclude_regex.find(function(regex) {
			return val.match(regex);
		}))
			val = null;
	} else if (val && window.$recorder_options && attr == 'class'
			&& $recorder_options.class_exclude_regex) {
		var cnames = val.split(/\s+/g).filter(function(cn) {
			return !$recorder_options.class_exclude_regex.find(function(regex) {
				return cn.match(regex);
			});
		});
		if (cnames.length == 0)
			val = null;
		val = cnames.join(' ');
	}
	return val;
}
