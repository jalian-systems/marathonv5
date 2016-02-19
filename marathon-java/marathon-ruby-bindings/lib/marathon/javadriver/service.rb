# encoding: utf-8
module Marathon
  module JavaDriver

    #
    # @api private
    #

    class Service
      START_TIMEOUT = 20
      STOP_TIMEOUT  = 5
      DEFAULT_PORT  = 8910
      MISSING_TEXT  = "Unable to find javadriver executable."

      attr_reader :uri

      def self.default_service(executable_path, port = nil)
        new executable_path, port || ::Selenium::WebDriver::PortProber.above(DEFAULT_PORT)
      end

      def initialize(executable_path, port)
        @uri        = URI.parse "http://#{::Selenium::WebDriver::Platform.localhost}:#{port}"
        @executable = executable_path
      end

      def start(args = [], profile)
        if @process && @process.alive?
          raise "already started: #{@uri.inspect} #{@executable.inspect}"
        end

        @profile = profile
        @process = create_process(args)
        @process.start

        socket_poller = ::Selenium::WebDriver::SocketPoller.new ::Selenium::WebDriver::Platform.localhost, @uri.port, START_TIMEOUT

        unless socket_poller.connected?
          raise Error::WebDriverError, "unable to connect to javadriver @ #{@uri} after #{START_TIMEOUT} seconds"
        end

        ::Selenium::WebDriver::Platform.exit_hook { stop } # make sure we don't leave the server running
      end

      def stop
        return if @process.nil? || @process.exited?

        Net::HTTP.start(uri.host, uri.port) do |http|
          http.open_timeout = STOP_TIMEOUT / 2
          http.read_timeout = STOP_TIMEOUT / 2

          http.get("/shutdown")
        end

        @process.poll_for_exit STOP_TIMEOUT
        rescue ChildProcess::TimeoutError
          # ok, force quit
          @process.stop STOP_TIMEOUT

          if ::Selenium::WebDriver::Platform.jruby? && !$DEBUG
            @process.io.close rescue nil
          end
      end

      def create_process(args)
        @profile.set_port(uri.port)
        command_line = @profile.get_commandline
        vm_command = @profile.get_java_command 
        @executable = vm_command if !vm_command.nil?
        server_command = [@executable, *command_line, *args]
        process = ChildProcess.build(*server_command.compact)

        if $DEBUG == true
          process.io.inherit!
        elsif ::Selenium::WebDriver::Platform.jruby?
          # apparently we need to read the output for javadriver to work on jruby
          process.io.stdout = process.io.stderr = File.new(::Selenium::WebDriver::Platform.null_device, 'w')
        end

        process
      end

    end # Service
  end # JavaDriver
end # Service
