#{{{ Marathon
require_fixture 'default'
#}}} Marathon

def test

    java_recorded_version = '1.8.0_91'

    with_window("Ensemble") {
        select("page-tree", "[\"/SAMPLES/Concurrency/Service\"]")
        select("page-tree", "[\"/SAMPLES/Canvas/Fireworks\"]")
        select("page-tree", "[\"/SAMPLES/Controls/Accordion\"]")
    }

end
