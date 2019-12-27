console.log("Loading gwt.js...");

(function () {
    var gwt_last_datebox = null;

    var gwt_components = {
        list: [function () { return this.closest('div[__idx]'); }],
        grid: [function () { return this.closest('tr[__gwt_row]'); }],
        datebox: [function () { gwt_last_datebox = this; return false; }],
        datepicker: [function () { return this.matches('.gwt-DatePicker *'); }],
        tree: [function () { return this.matches('[role="tree"] *'); }],
    };

    var gwt_last_match = {};

    var canhandle = function (evt) {
        return ['list', 'grid', 'tree', 'datepicker', 'datebox'].find(function (t) {
            var match = gwt_components[t].find(function (fn) {
                return fn.call(evt.target);
            });
            if (match) {
                gwt_last_match.type = t;
                gwt_last_match.selector = match;
                return true;
            }
            return false;
        });
    };

    function findGWTListTarget(target) {
        var target = target.closest('div[__idx]');
        var index = target.getAttribute('__idx');
        target = target.parentElement.parentElement;
        return { target: target.parentElement.parentElement, identity: { index: index }, suffix: 'gwt_list' };
    }

    function findGWTGridTarget(target) {
        var td = target.closest('td');
        var table = td.closest('table');
        var grid = findGrid(table);
        var column = findColumn(grid, td);
        var tr = td.closest('tr[__gwt_row]');
        var identity = { row: tr.getAttribute('__gwt_row'), subrow: tr.getAttribute('__gwt_subrow') };
        if (column)
            identity.column = column;
        else
            identity.cellIndex = td.cellIndex;
        return { target: grid, suffix: 'gwt_grid', identity: identity };
    }

    function findTreePath(treeitem) {
        var current = treeitem;
        var path = [];
        while (current) {
            path.push(findText(current));
            current = current.parentElement.closest('[role="treeitem"]');
        }
        return path.reverse().join("/");
    }

    function findText(current) {
        // Get individual text nodes and concat with ':'
        var text_parts = [];
        findTextParts(current.querySelector('div'), text_parts);
        return text_parts.join(":");
    }

    function findTextParts(current, text_parts) {
        if(current.nodeName === '#text')
            text_parts.push(current.nodeValue.replace(/^\s*|\s(?=\s)|\s*$/g, "").replace(/[\/:]/g, "_"))
        else {
            for(var i = 0; i < current.childNodes.length; i++)
                findTextParts(current.childNodes[i], text_parts);
        }
    }
    
    function findGWTTreeTarget(target) {
        var treeitem = target.closest('[role="treeitem"]');
        var tree = target.closest('[role="tree"]');
        var identity = { path: findTreePath(treeitem) };
        return { target: tree, suffix: 'gwt_tree', identity: identity };
    }

    function findColumn(grid, td) {
        var ths = grid.querySelectorAll('thead th');
        var found = false;
        for (var i = 0; i < ths.length; i++) {
            if (Math.abs(td.offsetLeft - ths[i].offsetLeft) < 10 &&
                Math.abs(td.offsetWidth - ths[i].offsetWidth) < 10 &&
                ths[i].innerText.trim().length > 0)
                found = ths[i].innerText.trim();
        }
        return found;
    }

    function findGrid(table) {
        var grid = table;
        while (grid != null && !grid.querySelector('thead'))
            grid = grid.parentElement;
        return grid;
    }

    function findGWTTarget(target) {
        if (gwt_last_match.type === 'list') {
            return findGWTListTarget(target);
        } else if (gwt_last_match.type === 'grid') {
            return findGWTGridTarget(target);
        } else if (gwt_last_match.type === 'datepicker') {
            return { target: target.closest('.gwt-DatePicker'), suffix: 'gwt_datepicker', identity: false };
        } else if (gwt_last_match.type === 'tree') {
            return findGWTTreeTarget(target.closest('[role="treeitem"]'));
        }
        console.log("Unknown type: " + gwt_last_match.type);
        return false;
    }

    function handleDatePickerClick(evt) {
        if (!evt.target.matches('.datePickerDay') || evt.target.matches('.datePickerDayIsFiller'))
            return;
        var suffix = "gwt_datepicker";
        var target = findGWTTarget(evt.target);
        var month = target.target.querySelector('.datePickerMonth');
        if (month.querySelector('select')) {
            month = target.target.querySelector('.datePickerMonth select');
        }
        if (month.tagName.toLowerCase() === 'select') {
            var year = target.target.querySelector('.datePickerYear select');
            var mm = "MM", yy = "YYYY";
            for (var i = 0; i < month.options.length; i++) {
                if (month.options[i].selected)
                    mm = month.options[i].text;
            }
            for (var i = 0; i < year.options.length; i++) {
                if (year.options[i].selected)
                    yy = year.options[i].text;
            }
            if (target.target.closest('.dateBoxPopup')) {
                target.target = gwt_last_datebox;
                suffix = 'gwt_datebox';
            }
            $marathon.postEvent(target.target, { type: 'select', value: mm + " " + yy + " " + evt.target.innerText.trim(), suffix: suffix });
        } else {
            if (target.target.closest('.dateBoxPopup')) {
                console.log('last_datebox', gwt_last_datebox);
                target.target = gwt_last_datebox;
                suffix = 'gwt_datebox';
            }
            $marathon.postEvent(target.target, { type: 'select', value: month.innerText.trim() + " " + evt.target.innerText.trim(), suffix: suffix });
        }
    }

    var onclick = function (evt) {
        if ($marathon.shouldIgnoreClick(evt.target) && !evt.target.matches('input[type="checkbox"]'))
            return;
        if (gwt_last_match.type === 'datepicker') {
            handleDatePickerClick(evt);
            return;
        }
        var target = findGWTTarget(evt.target);
        if (target) {
            if (evt.target.matches('input[type="checkbox"]')) {
                $marathon.postEvent(target.target, { type: 'select', cellinfo: JSON.stringify(target.identity), value: $marathon.value(evt.target), suffix: target.suffix });
            } else {
                $marathon.postDelayedEvent(target.target, {
                    type: 'click',
                    cellinfo: JSON.stringify(target.identity),
                    clickCount: evt.detail == 0 ? 1 : evt.detail,
                    button: evt.button + 1,
                    modifiersEx: "",
                    x: 0, y: 0,
                    suffix: target.suffix
                });
            }
        }
    };

    var onchange = function (evt) {
        console.log(evt);
        if (evt.target.matches('input[type="checkbox"]') || gwt_last_match.type === 'datepicker') {
            // Handled in click
            return;
        }
        var target = findGWTTarget(evt.target);
        var value = $marathon.value(evt.target);
        console.log('select-target', target, 'value', value);
        if (value) {
            $marathon.postEvent(target.target, { type: 'select', cellinfo: JSON.stringify(target.identity), value: value, suffix: target.suffix });
        }
    };

    var onmousedown = function (evt) {
        console.log('gwt_mousedown', evt);
    };

    $marathon.resolvers.unshift({
        canhandle: canhandle,
        onclick: onclick,
        onchange: onchange,
        onmousedown: onmousedown
    });
})();
