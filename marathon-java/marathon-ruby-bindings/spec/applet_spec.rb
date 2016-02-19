# encoding: utf-8
require 'rspec'
require 'marathon-javadriver'

describe "Java applet" do
  before(:each) do
    @profile = Marathon::JavaDriver::Profile.new :java_applet
    f = File.new(File.join(File.dirname(__FILE__), '../../marathon-test-helpers/swingset3/applet.html'), 'r')
    @profile.set_applet_URL(File.expand_path(f))
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
    @profile.set_start_window_title('Applet Viewer: SwingSet3Init.class')
    create_driver(@profile)
    tool_options = ENV['JAVA_TOOL_OPTIONS']
    expect(tool_options).to include('-Dstart.window.title="Applet Viewer: SwingSet3Init.class"')
    title = @driver.title
    expect(title).to eq('Applet Viewer: SwingSet3Init.class')
  end
  
  it "takes title with regex patter", :name => 'regex_title' do
    @profile.set_start_window_title('/.*SwingSet3Init.class')
    create_driver(@profile)
    tool_options = ENV['JAVA_TOOL_OPTIONS']
    expect(tool_options).to include('/.*SwingSet3Init.class')
    title = @driver.title
    expect(title).to eq('Applet Viewer: SwingSet3Init.class')
  end
  
  it "takes vm arguments", :name => 'vm_arguments' do
    @profile.add_vm_arguments('-Dx.y.z=hello')
    @profile.get_commandline
    tool_options = ENV['JAVA_TOOL_OPTIONS']
    expect(tool_options).to include('-Dx.y.z=hello')
  end
  
  it "can find elements by css", :name => 'find_element' do
    create_driver(@profile)
    wait = Selenium::WebDriver::Wait.new(:timeout => 5)
    wait.until {@driver.find_element(:css, 'button').displayed?}
    button = @driver.find_element(:css, 'button')
    button.click
    expect(button.attribute('text')).to eq('Go')
  end
  
  it "can take java executable", :name=> 'java_executable' do
    java_exe = get_java_exe
    @profile.set_java_command(get_java_exe)
    command_line = @profile.get_commandline
    expect(@profile.get_java_command).to include(java_exe)
  end
  
  def get_java_exe() 
    if @profile.windows? then 
      command = 'appletviewer.exe'
    else 
      command = 'appletviewer'
    end
  end
  
 end
