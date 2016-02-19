# encoding: utf-8
require 'rspec'
require 'marathon-javadriver'

describe "Java command line" do
  before(:each) do
    @profile = Marathon::JavaDriver::Profile.new :java_command_line
    f = File.new(File.join(File.dirname(__FILE__), '../../marathon-test-helpers/swingset3/SwingSet3.jar'), 'r')
    @profile.add_class_path_file(f)
    @profile.set_main_class('com.sun.swingset3.SwingSet3')
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

  it "can give count of elements found", :name => 'toggle_count' do
    create_driver(@profile)
    size = @driver.find_elements(:css, 'toggle-button').size
    expect(size).to be > 0
  end

  it "can give label of found element from attribute", :name => 'toggle_button_label' do
    create_driver(@profile)
    toggle_button = @driver.find_element(:css, 'toggle-button[label *= "JFrame"]')  
    expect(toggle_button.attribute('label')).to eq('JFrame ')
  end  

  it "can click menu", :name => 'menu_click' do
    create_driver(@profile)
    @driver.find_element(:css, 'menu[text = "View"]').click
    look_feel_menu = @driver.find_element(:css, 'menu[text = "Look and Feel"]')
    expect(look_feel_menu).not_to be_nil
  end

  it "can click button", :name =>'button_click' do 
    create_driver(@profile)
    @driver.find_element(:css, 'toggle-button[label *= "JButton"]').click
    wait = Selenium::WebDriver::Wait.new(:timeout => 5)
    wait.until {@driver.find_element(:css, 'button[text = "Do it"]').displayed?}
    do_button = @driver.find_element(:css, 'button[text = "Do it"]')
    do_button.click
    expect(do_button.text).to eq('Do it again')
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
  
  it "can get table header", :name =>'table_header' do
    create_driver(@profile)
    @driver.find_element(:css, 'toggle-button[accessibleName *= "JTable"]').click
    wait = Selenium::WebDriver::Wait.new(:timeout => 5)
    wait.until {@driver.find_element(:css, 'table::header').displayed?}
    header = @driver.find_element(:css, 'table::header')
    category = header.find_element(:css, '.::nth-item(2)')
    category.click
    expect(category.attribute('text')).to eq('Award Category')
  end
  
  it "can take vm arguments in profile", :name =>'vm_arguments' do
    @profile.add_vm_arguments('-version')
    @profile.get_commandline
    tool_options = ENV['JAVA_TOOL_OPTIONS']
    expect(tool_options).to include('-version')
  end
  
  it "can take app arguments in profile", :name=>'app_arguments' do
    @profile.add_application_arguments("Argument")
    command_line = @profile.get_commandline
    expect(command_line).to include('Argument')
  end
  
  it "can take java executable", :name=> 'java_executable' do
    java_exe = get_java_exe
    @profile.set_java_command(get_java_exe)
    command_line = @profile.get_commandline
    expect(@profile.get_java_command).to include(java_exe)
  end
  
  def get_java_exe() 
    if @profile.windows? then 
      command = 'java.exe'
    else 
      command = 'java'	 
    end
  end
  
end
