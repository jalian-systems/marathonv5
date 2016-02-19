# encoding: utf-8
require 'rspec'
require 'marathon-javadriver'

describe "Command line" do
  before(:each) do
    @profile = Marathon::JavaDriver::Profile.new :executable_jar
    @profile.set_executable_jar(get_jar)
  end
  
  def create_driver(profile)
    @driver = Selenium::WebDriver.for :javadriver, :profile => profile
  end

  after(:each) do
    begin
    @driver.quit if !@driver.nil?
    rescue => e
    end
  end
  
  it "can take app arguments in profile", :name=>'app_arguments' do
    create_driver(@profile)
    @profile.add_application_arguments("Argument")
    command_line = @profile.get_commandline
    expect(command_line).to include('Argument')
  end
  
  it "can take start window title", :name => 'window_title' do
    @profile.set_start_window_title('SwingSet3')
    create_driver(@profile)
    @driver.switch_to.window('SwingSet3')
    tool_options = ENV['JAVA_TOOL_OPTIONS']
    expect(tool_options).to include('-Dstart.window.title="SwingSet3"')
    title = @driver.title
    expect(title).to eq('SwingSet3')
  end
  
  it "can send keys to text field", :name =>'text_field_text' do
    create_driver(@profile)
    @driver.find_element(:css, 'toggle-button[label *= "TextField"]').click
    wait = Selenium::WebDriver::Wait.new(:timeout => 5)
    wait.until {@driver.find_element(:css, 'text-field').displayed?}
    text_field = @driver.find_element(:css, 'text-field')
    text_field.send_keys('ruby')
    expect(text_field.text).to eq('ruby')
  end

  it "can find all the times in combo box", :name =>'combo_box_items' do
    create_driver(@profile)
    @driver.find_element(:css, 'toggle-button[accessibleName *=  "JComboBox"]').click
    wait = Selenium::WebDriver::Wait.new(:timeout => 5)
    wait.until {@driver.find_element(:css, 'combo-box[accessibleName ^= "Presets"]').displayed?}
    combo_box = @driver.find_element(:css, 'combo-box[accessibleName ^= "Presets"]')
    combo_box.find_element(:css, 'synth-arrow-button').click
    items = combo_box.find_elements(:css, '.::all-options')
    expect(items.size).to eq(10)
  end
  
  it "takes title with regex patter", :name => 'regex_title' do
    @profile.set_start_window_title('/S.*3')
    create_driver(@profile)
    @driver.switch_to.window('SwingSet3')
    tool_options = ENV['JAVA_TOOL_OPTIONS']
    expect(tool_options).to include('/S.*3')
    title = @driver.title
    expect(title).to eq('SwingSet3')
  end
  
  def get_jar()
    f = File.new(File.join(File.dirname(__FILE__), '../../marathon-test-helpers/swingset3/SwingSet3.jar'), 'r')
    command = File.expand_path(f)
  end
  
end
