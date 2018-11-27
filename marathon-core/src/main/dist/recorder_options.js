window.$recorder_options = {
	id_exclude_regex : [ /.*\d+/ ],
	class_exclude_regex : [ /.*\d+/ ],
	with_extended_info : [],
	css_selector_options : [ {
		css : '*',
		options : {
			id_blacklist : [ /.*\d+/ ],
			class_blacklist : [ /.*\d+/ ],
			selectors : [ 'id', 'class', 'attribute', 'nthchild' ],
			prefix_tag : true,
			quote_attribute_when_needed : true
		}
	} ]
};
