# -*- encoding: utf-8 -*-

root = File.expand_path(File.dirname(__FILE__))
if root != Dir.pwd
  raise "cwd must be #{root} when reading gemspec"
end

Gem::Specification.new do |s|
  s.name    = "marathon-javadriver"
  s.version = "4.1.2.0"

  s.authors     = ["Jalian Systems Pvt. Ltd."]
  s.email       = "info@jaliansystems.com"
  s.description = "Marathon java driver adds support for launching Java/Swing applications to Selenium webdriver"
  s.summary     = "Built on top of WebDriver for testing Swing applications"
  s.homepage    = "http://marathontesting.com"
  s.licenses    = ["Apache"]

  s.required_rubygems_version = Gem::Requirement.new("> 1.3.1") if s.respond_to? :required_rubygems_version=
  s.required_ruby_version     = Gem::Requirement.new(">= 1.9.2")

  s.files         = Dir[root + '/**/*'].reject { |e| e =~ /\.gem|ruby\.iml|build\.desc|\.jar|spec\/|swingset3\// }.map { |e| e.sub(root + '/', '') }
  s.require_paths = ["lib"]

  s.add_runtime_dependency "selenium-webdriver", ["~> 2.48.1"]
end
