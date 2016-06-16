#{{{ Marathon
require_fixture 'default'
#}}} Marathon

def test

    java_recorded_version = '1.8.0_91'

    with_window("SwingSet3") {
        select("JFrame", "true")
        select("JWindow", "true")
        select("JTabbedPane", "true")
    }

end
