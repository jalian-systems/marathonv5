1. Building the GEM
   Run `gem build marathon-javadriver.gemspec`. This should build the gem in the current folder.
2. Installing the GEM
   Run `gem install ./<gem-name>`.
3. Running the tests
   a. set MARATHON_HOME to the marathon installation folder. Alternatively set MARATHON_AGENT to point to the marathon-java-agent jar file.
   b. run `rspec`. This command should run all tests from the spec folder.

