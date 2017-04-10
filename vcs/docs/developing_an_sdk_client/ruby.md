{% tocTag %} {% endtocTag %}

# Overview

Once the new service is up and running, you can use Ruby to connect to the server and invoke its APIs. The tutorial provides instructions to stand up the service by running individual commands, or by using shell script. The underlying steps in both the methods are same as the sub-steps while executing individual commands.

# Using individual commands

## Generating Stubs

The PDK includes a code generator `ruby-generator` that parses the interface definition and generates language binding code.

<div>
    <!-- Nav tabs -->
    <ul class="nav nav-tabs">
        <li class="active">
            <a id="linGenStubTab" href="#linGenStub" data-toggle="tab">Linux</a>
        </li>
        <li>
            <a id="winGenStubTab" href="#winGenStub" data-toggle="tab">Windows</a>
        </li>
    </ul>
    <div class="codePart">
        <!-- Tab panes -->
        <div class="tab-content">
            <div class="tab-pane active" id="linGenStub">
                vapi-pdk$ ruby-toolkit/bin/ruby-generator --profile client --library idl-toolkit/vmidl/vapi\_stdlib.vmidl --output build/toyvm/generated/client toyvm/vmodl
                <span class="collapseTitle collapsed" data-toggle="collapse" data-target="#genStubOut">&nbsp;</span>
            </div>
            <div class="tab-pane" id="winGenStub">
                vapi-pdk> ruby-toolkit\\bin\\ruby-generator.bat --profile client --library idl-toolkit/vmidl/vapi\_stdlib.vmidl --output build/toyvm/generated/client toyvm/vmodl
                <span class="collapseTitle collapsed" data-toggle="collapse" data-target="#genStubOut">&nbsp;</span>
            </div>
        </div>
        <div id='genStubOut' class="collapseContent collapsing">
INFO: Generating ruby files ...<br>
INFO: Processing target language: ruby<br>
INFO:  > Found 2 ruby language 'models' templates ...<br>
INFO:  > Found 2 ruby language 'packages' templates ...<br>
INFO: Processing model scope templates ...<br>
INFO:  > Generating file: build/toyvm/generated/client/ruby/sample/first/vapi.rb ...<br>
INFO: Processing package scope templates for 'idl.model.IdlPackage(name:sample.first)' ...<br>
INFO:  > Generating file: build/toyvm/generated/client/ruby/sample/first.rb ...<br>
        </div>
    </div>
</div>
The generator creates a sub-directory named `ruby`. It then creates a Ruby package and module structure based on the services defined in the IDL.

For the ToyVM VMODL2 interface, since the package is sample.first, it generates `first.rb`. The file contains a class named ToyVM which is the client stub. It also includes usage information that explains how to create a client-side ToyVM instance and execute methods.

## Implementing the Client

Make sure that the server is running at <http://localhost:8088/api>. This tutorial's toyvm server exposes that endpoint and this client tries to connect to that. Otherwise update the client code with your server's endpoint location.

The client code uses the generated stub to communicate with the server and invoke methods.

The test code included with the tutorial shows an example of connecting to the service and performing operations through an interactive Ruby shell:

<div class="codePart">
    vapi-pdk$ cat toyvm/ruby/client/toyvm.rb
    <span class="collapseTitle collapsed" data-toggle="collapse" data-target="#clientFile">&nbsp;</span>
    <div id="clientFile" class="collapseContent collapse">
```ruby
#!/usr/bin/env ruby

# Example client using the ToyVM service with static bindings

require 'vapi'
require 'sample/first/vapi'
require 'com/vmware/vapi/std/errors'
require 'logger'

module Tutorial

    def self.log
        @@log ||= init_logger
    end

    def self.init_logger
        result = Logger.new(STDOUT)
        result.progname = 'vAPI Tutorial'
        result.level = Logger::INFO
        result
    end

    class Client
        TOY_VM = Sample::First::ToyVM

        attr_reader :vapi_url, :vapi_config, :toyvm

        def initialize
            @vapi_url = "http://localhost:8088/api"
            @vapi_config = VAPI::Bindings::VapiConfig.new(vapi_url)
            @toyvm = TOY_VM.new(vapi_config)
        end

        # Entry point for the client
        def main
            # Creating VMs
            vm1 = toyvm.create(TOY_VM::CreateSpec.new(:display_name => 'new toyvm 1'))
            vm2 = toyvm.create(TOY_VM::CreateSpec.new(:display_name => 'new toyvm 2'))
            names = toyvm.list().inspect
            Tutorial.log.info('VMs created: %s' % names)

            # Getting VM info
            Tutorial.log.info('Initial config of %s: %s' % [vm1, toyvm.get(vm1)])
            # VM config update
            toyvm.update(vm1,
                TOY_VM::UpdateSpec.new(
                    :num_cpus => 3,
                    :disk_backing => TOY_VM::DiskBacking.new(
                        :type => TOY_VM::DiskBacking::Type::FILE,
                        :file_path => 'xyz')))
            Tutorial.log.info('Updated config of %s: %s\n' % [vm1, toyvm.get(vm1)])

            Tutorial.log.info('Initial config of %s: %s' % [vm2, toyvm.get(vm2)])
            # VM config update
            toyvm.update(vm2,
                TOY_VM::UpdateSpec.new(
                    :mem_size => 500,
                    :disk_backing => TOY_VM::DiskBacking.new(
                        :type => TOY_VM::DiskBacking::Type::DEVICE,
                        :device_name => 'dev1',
                        :client_device => false)))
            # Turning on VM
            toyvm.power_on(vm2)
            Tutorial.log.info('Updated config and powered on %s: %s' % [vm2, toyvm.get(vm2)])

            # Trying to turn on a VM that is already turned on
            begin
                toyvm.power_on(vm2)
                Tutorial.log.info('Succeeded but should have failed')
            rescue Com::Vmware::Vapi::Std::Errors::AlreadyInDesiredState
                Tutorial.log.info('"%s" is already powered on' % toyvm.get(vm1).display_name)
            end

            toyvm.delete(vm1)
            toyvm.delete(vm2)
            Tutorial.log.info('Deleted VMs')
        end
    end
end

Tutorial::Client.new.main() if __FILE__ == $0
```
</div>
</div>
&nbsp;

## Running the Client

Client code can be run directly:

<div class="codePart">
    vapi-pdk$ ruby -I ruby-toolkit/runtime/lib -I build/toyvm/generated/client/ruby toyvm/ruby/client/toyvm.rb
    <span class="collapseTitle collapsed" data-toggle="collapse" data-target="#clientRun">&nbsp;</span>
    <div id="clientRun" class="collapseContent collapse">
I, [2014-12-03T15:27:28.863525 #2344]  INFO -- vAPI Tutorial: VMs created: #<Set: {"vm-1", "vm-2"}><br>
I, [2014-12-03T15:27:28.886076 #2344]  INFO -- vAPI Tutorial: Initial config of vm-1: #<Sample::First::ToyVM::Info:0x892b71c><br>
I, [2014-12-03T15:27:28.896937 #2344]  INFO -- vAPI Tutorial: Updated config of vm-1: #<Sample::First::ToyVM::Info:0x892ce8c>\n<br>
I, [2014-12-03T15:27:28.899140 #2344]  INFO -- vAPI Tutorial: Initial config of vm-2: #<Sample::First::ToyVM::Info:0x8ecd6ac><br>
I, [2014-12-03T15:27:28.905193 #2344]  INFO -- vAPI Tutorial: Updated config and powered on vm-2: #<Sample::First::ToyVM::Info:0x8e529e8><br>
E, [2014-12-03T15:27:28.908317 #2344] ERROR -- vAPI: VapiError (Com::Vmware::Vapi::Std::Errors::AlreadyInDesiredState)<br>
I, [2014-12-03T15:27:28.910060 #2344]  INFO -- vAPI Tutorial: "new toyvm 1" is already powered on<br>
I, [2014-12-03T15:27:28.912412 #2344]  INFO -- vAPI Tutorial: Deleted VMs<br>
</div>
</div>
&nbsp;
