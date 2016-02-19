# encoding: utf-8
module Marathon
  module JavaDriver 
    class Profile
      require 'uri'

      attr_reader :command_path
      PROP_HOME      = 'marathon.home'
      MARATHON_AGENT = 'marathon.agent'
      LAUNCH_MODE    = {
        :java_command_line => ['javacommand', 'Java Command Line', 'vmargument', 'classpath', 'mainclass', 'appargument'],
        :java_webstart     => ['webstart', 'WebStart Application', 'vmargument', 'wsargument', 'jnlpfile', 'startwindowtitle'],
        :command_line      => ['commandline', 'Command Line', 'command', 'appargument', 'startwindowtitle'],
        :java_applet       => ['applet', 'Applet Application',  'vmargument', 'appleturl', 'startwindowtitle'],
        :executable_jar    => ['executablejar', 'Executable Jar', 'jar', 'appargument', 'startwindowtitle'],
      }

      def initialize(launch_mode)
        @classPath_entries     = Array.new
        @vm_arguments          = Array.new
        @ws_arguments          = Array.new
        @appliaction_arguments = Array.new
        @marathon_launch_mode  = launch_mode
        check_valid_launch_mode(@marathon_launch_mode)
        set_javadriver_command
      end

      def get_commandline
        command_line = []
        if (@marathon_launch_mode == :java_command_line)
          if(@classPath_entries.length != 0)
            command_line << '-cp'
            command_line << get_classPath
          end
          command_line << @main_class
          @appliaction_arguments.each do |app_arg|
            command_line << app_arg
          end
        end

        if (@marathon_launch_mode == :java_webstart)
          @ws_arguments.each do |ws_arg|
            command_line << ws_arg
          end
          command_line << @jnlp_file.path
        end

        if (@marathon_launch_mode == :java_applet)
          command_line << @applet_URL
        end

        if (@marathon_launch_mode == :command_line)
          @appliaction_arguments.each do |app_arg|
            command_line << app_arg
          end
        end

        if (@marathon_launch_mode == :executable_jar)
          command_line << '-jar'
          command_line << @executable_jar
          @appliaction_arguments.each do |app_arg|
            command_line << app_arg
          end
        end
        ENV['JAVA_HOME'] = @java_home
        ENV['JAVA_TOOL_OPTIONS'] = get_tool_options
        command_line
      end

      def get_tool_options
        java_tool_options = StringIO.new
        java_tool_options << '-Dstart.window.title="' + @start_window_title + '" ' unless @start_window_title.nil?
        java_tool_options << '-D' + MARATHON_AGENT + '=' + get_agent_jar_URL + ' '
        java_tool_options << '-Dmarathon.launch.mode=' + LAUNCH_MODE[@marathon_launch_mode].at(0) + ' '
        java_tool_options << '-javaagent:' + get_agent_jar + '=' + @port.to_s + ' '
        @vm_arguments.each do |vm_arg|
          java_tool_options << '"' + vm_arg + '" '
        end
        java_tool_options.string.chomp(' ')
      end

      def get_agent_jar
        agent = ENV['MARATHON_AGENT'] || find_file(ENV['MARATHON_HOME'], '/marathon-java-agent-*.jar')
        raise Exception.new('Can not find marathon agent file. Set MARATHON_HOME or MARATHON_AGENT to point to the right place.') unless agent
        agent
      end

      def get_agent_jar_URL
        prefix = ''
        prefix = '/' if ::Selenium::WebDriver::Platform.windows?
        URI.join('file:///', prefix + get_agent_jar).to_s
      end

      def find_file(marathon_home, name_pattern)
        return nil unless marathon_home
        file = Dir[marathon_home + name_pattern]
        return nil unless file.length == 1
        File.expand_path(file.at(0))
      end

      def add_class_path(*jar_paths)
        check_valid_property('classpath')
        jar_paths.each do |path| 
          file = File.new(path, 'r')
          if File.exist?(file)
            @classPath_entries.push(file)
          end
        end
        self
      end

      def add_class_path_file(*jar_files)
        check_valid_property('classpath')
        jar_files.each do |file| 
          if File.exist?(file)
            @classPath_entries.push(file)
          end
        end
        self
      end

      def get_classPath()
        paths = StringIO.new
        @classPath_entries.each do |file|
          paths << file.path + File::PATH_SEPARATOR
        end
        paths.string
      end

      def add_vm_arguments(*args)
        check_valid_property('vmargument')
        args.each do |arg|
          @vm_arguments.push(arg)
        end
        self
      end

      def add_ws_arguments(*args)
        check_valid_property('wsargument')
        args.each do |arg|
          @ws_arguments.push(arg)
        end
        self
      end

      def add_application_arguments(*args)
        check_valid_property('appargument')
        args.each do |arg|
          @appliaction_arguments.push(arg)
        end
        self
      end

      def set_main_class(main_class)
        check_valid_property('mainclass')
        @main_class = main_class
        self
      end

      def set_JNLP_file(file)
        check_valid_property('jnlpfile')
        @jnlp_file = file
        self
      end

      def set_applet_URL(url)
        check_valid_property('appleturl')
        @applet_URL = url
        self
      end

      def set_start_window_title(title)
        check_valid_property('startwindowtitle')
        @start_window_title = title
        self
      end

      def set_working_directory(dir)
        @working_directory = dir
        self
      end

      def set_command(command)
        check_valid_property('command')
        if file?(command)
          @command_path = command
        else
          @command_path = find_binary(command)
        end
        self
      end

      def set_executable_jar(jar)
        check_valid_property('jar')
	@executable_jar = jar
        self
      end

      def find_binary(*binary_names)
        paths = ENV['PATH'].split(File::PATH_SEPARATOR)
        binary_names.map! { |n| get_binary(n) } if windows?

        binary_names.each do |binary_name|
          paths.each do |path|
            exe = File.join(path, binary_name)
            return exe if file?(exe)
          end
        end

        nil
      end

      def get_binary(binary_name)
        if binary_name.include? ('.bat')
          binary_name
        else
          "#{binary_name}.exe"
        end
      end

      def file? (command)
        File.exists?(command) && File.file?(command) && File.executable?(command)
      end

      def set_java_home(java_home)
        ENV['JAVA_HOME'] = java_home
        self
      end

      def set_marathon_home(marathon_home)
        ENV['MARATHON_HOME'] = marathon_home
        self
      end

      def check_valid_property(property)
        raise Exception.new(property + ' is not valid for ' + LAUNCH_MODE[@marathon_launch_mode].at(0)) if !valid_property? (property)
      end

      def check_valid_launch_mode(launch_mode)
        raise Exception.new(launch_mode + ' is not valid launch mode. Try commandline, webstart, applet, javacommand') unless LAUNCH_MODE.has_key?(launch_mode)
      end

      def valid_property? (property)
        LAUNCH_MODE[@marathon_launch_mode].include? (property)
      end

      def set_java_command(java_command)
        @vm_command = java_command
        self
      end

      def get_java_command()
        @vm_command
      end

      def set_javadriver_command()
        command = 'java' if @marathon_launch_mode == :java_command_line
        command = 'javaws' if @marathon_launch_mode == :java_webstart
        command = 'appletviewer' if @marathon_launch_mode == :java_applet
        command = 'java' if @marathon_launch_mode == :executable_jar
        @command_path = ::Selenium::WebDriver::Platform.find_binary(command) if command
      end

      def set_port(port)
        @port = port
      end 

      def windows?
        ::Selenium::WebDriver::Platform.windows?
      end

      def mac?
        ::Selenium::WebDriver::Platform.mac?
      end

      def linux?
        ::Selenium::WebDriver::Platform.linux?
      end

    end # Profile
  end # JavaDriver
end # Selenium
