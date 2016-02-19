# encoding: utf-8
require 'rspec'
require 'marathon-javadriver'

describe "Java webstart" do
  before(:each) do
    @profile = Marathon::JavaDriver::Profile.new :java_webstart
    jnlp_file = File.new(File.join(File.dirname(__FILE__), '../../marathon-test-helpers/swingset3/SwingSet3.jnlp'), 'r')
    @profile.set_JNLP_file(jnlp_file)
  end
  
  after(:each) do
    begin
    @driver.quit if !@driver.nil?
    rescue => e
    end
  end
  
  def create_driver(profile)
    @driver = Selenium::WebDriver.for :javadriver, :profile => profile
  end
  
  it "takes start window title", :name => 'window_title' do
    @profile.set_start_window_title('SwingSet3')
    create_driver(@profile)
    @driver.switch_to.window('SwingSet3')
    tool_options = ENV['JAVA_TOOL_OPTIONS']
    expect(tool_options).to include('-Dstart.window.title="SwingSet3"')
    title = @driver.title
    expect(title).to eq('SwingSet3')
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
  
  it "takes ws arguments", :name => 'ws_arguments' do
    @profile.add_ws_arguments('-verbose')
    command_line = @profile.get_commandline
    expect(command_line).to include('-verbose')
  end
  
  it "takes vm arguments", :name => 'vm_arguments' do
    @profile.add_vm_arguments('-Dx.y.z=hello')
    @profile.get_commandline
    tool_options = ENV['JAVA_TOOL_OPTIONS']
    expect(tool_options).to include('-Dx.y.z=hello')
  end
  
  it "can send keys to text field", :name =>'text_field_text' do
    @profile.set_start_window_title('SwingSet3')
    create_driver(@profile)
    @driver.switch_to.window('SwingSet3')
    @driver.find_element(:css, 'toggle-button[label *= "TextField"]').click
    wait = Selenium::WebDriver::Wait.new(:timeout => 5)
    wait.until {@driver.find_element(:css, 'text-field').displayed?}
    text_field = @driver.find_element(:css, 'text-field')
    text_field.send_keys('ruby')
    expect(text_field.text).to eq('ruby')
  end

  it "can find all the times in combo box", :name =>'combo_box_items' do
    @profile.set_start_window_title('SwingSet3')
    create_driver(@profile)
    @driver.switch_to.window('SwingSet3')
    @driver.find_element(:css, 'toggle-button[accessibleName *=  "JComboBox"]').click
    wait = Selenium::WebDriver::Wait.new(:timeout => 5)
    wait.until {@driver.find_element(:css, 'combo-box[accessibleName ^= "Presets"]').displayed?}
    combo_box = @driver.find_element(:css, 'combo-box[accessibleName ^= "Presets"]')
    combo_box.find_element(:css, 'synth-arrow-button').click
    items = combo_box.find_elements(:css, '.::all-options')
    expect(items.size).to eq(10)
  end
  
  it "can take java executable", :name=> 'java_executable' do
    java_exe = get_java_exe
    @profile.set_java_command(get_java_exe)
    command_line = @profile.get_commandline
    expect(@profile.get_java_command).to include(java_exe)
  end
  
  def get_java_exe() 
    if @profile.windows? then 
      command = 'javaws.exe'
    else 
      command = 'javaws'	 
    end
  end
  
end
