console.log('Loading... jquery support')

window.$marathon.resolvers.unshift({
    canhandle: function(evt) { return evt.target.matches('input.hasDatepicker, .ui-datepicker-inline, .ui-datepicker-inline *, .ui-datepicker, .ui-datepicker *'); },
    onmousedown: function(evt) {
        if(evt.target.matches('input.hasDatepicker'))
            $marathon.jq_datepicker = evt.target;
    },
    onclick: function(evt) {
        var target = evt.target;
        if(target.matches('.ui-datepicker-inline td[data-handler="selectDay"] a')) {
            while ((target = target.parentElement) && !target.matches('.hasDatepicker'));
            if(target) {
                setTimeout(function() {
                    $marathon.postEvent(target, { type: 'select', value: jQuery(target).datepicker().val(), suffix: 'date'});
                }, 50);
            }
        }
        if(target.matches('.ui-datepicker td[data-handler="selectDay"] a')) {
            var target = $marathon.jq_datepicker;
            if(target) {
                setTimeout(function() {
                    $marathon.postEvent(target, { type: 'select', value: jQuery(target).datepicker().val(), suffix: 'date'});
                }, 50);
            }
        }
    }
});
