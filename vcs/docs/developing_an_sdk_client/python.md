{% tocTag %} {% endtocTag %}

# Overview

Once the new service is up and running, you can use Python to connect to the server and invoke its APIs. This tutorial provides instructions to stand up a client service by running individual commands, or by using a shell script. The underlying steps in both methods are the same as the sub-steps while executing individual commands.

# Using individual commands

## Generating Stubs

The PDK includes a code generator `python-generator` that parses the interface definition and generates language bindings source code.

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
                vapi-pdk$ python-toolkit/bin/python-generator --profile client --library idl-toolkit/vmidl/vapi\_stdlib.vmidl --output build/toyvm/generated/client toyvm/vmodl
                <span class="collapseTitle collapsed" data-toggle="collapse" data-target="#genStubOut">&nbsp;</span>
            </div>
            <div class="tab-pane" id="winGenStub">
                vapi-pdk> python-toolkit\\bin\\python-generator.bat --profile client --library idl-toolkit/vmidl/vapi\_stdlib.vmidl --output build/toyvm/generated/client toyvm/vmodl
                <span class="collapseTitle collapsed" data-toggle="collapse" data-target="#genStubOut">&nbsp;</span>
            </div>
        </div>
        <div id='genStubOut' class="collapseContent collapsing">
Generating vAPI Python client bindings<br>
INFO: Generating python files ...<br>
INFO: Processing target language: python<br>
INFO: > Found 3 python language 'packages' templates ...<br>
INFO: Processing package scope templates for 'idl.model.IdlPackage(name:sample.first)' ...<br>
INFO: > Generating file: build/toyvm/generated/client/python/sample/first_client.py ...<br>
        </div>
    </div>
</div>
The generator creates a sub-directory named python and a Python package and module structure based on the services defined in the IDL definitions.

For the ToyVM VMODL2 interface, since the package is sample.first, it generates `first_client.py`. The file contains two classes: ToyVM and ToyVMStub. The ToyVM class is pretty much a faithful mapping of the service interface into Python (the same interface that is generated when building a Python provider) which is used by client code to invoke API operations. The ToyVMStub class contains support code for communicating with the server. The `first_client.py` file also includes usage information on how to create a client-side ToyVM instance and execute methods.

## Implementing the Client

Make sure that the server is running at <http://localhost:8088/api>. This tutorial's toyvm server exposes that endpoint and this client tries to connect to that. Otherwise update the client code with your server's endpoint location.

The client code uses the generated stub to communicate with the server and invoke methods. The `get_connector` method is used to create a connection to the server using the specified protocol and endpoint, and the connector is used to instantiate the interface stub.

The test code included with the tutorial shows an example of connecting to the service and performing operations through an interactive Python shell:
<div class="codePart">
    vapi-pdk$ cat toyvm/python/client/toyvm_client.py
    <span class="collapseTitle collapsed" data-toggle="collapse" data-target="#clientFile">&nbsp;</span>
    <div id="clientFile" class="collapseContent collapse">
```python
#!/usr/bin/env python

"""
Example client using the ToyVM service with static bindings
"""

import logging

from com.vmware.vapi.std.errors_client import AlreadyInDesiredState
from sample.first_client import ToyVM
from vmware.vapi.lib.connect import get_connector
from vmware.vapi.stdlib.client.factories import StubConfigurationFactory

logging.basicConfig(level=logging.INFO)

def main():
    """
    Entry point for the client
    """
    connector = get_connector('http', 'json', url='http://localhost:8088/api')
    stub_config = StubConfigurationFactory.new_std_configuration(connector)
    toyvm = ToyVM(stub_config)

    # Creating VMs
    vm1 = toyvm.create(ToyVM.CreateSpec(display_name='new toyvm 1'))
    vm2 = toyvm.create(ToyVM.CreateSpec(display_name='new toyvm 2'))
    logging.info('VMs created: %s', toyvm.list())

    # Getting VM info
    logging.info('Initial config of %s: %s', vm1, toyvm.get(vm1))
    # VM config update
    toyvm.update(vm1, ToyVM.UpdateSpec(
                    num_cpus=3,
                    disk_backing=ToyVM.DiskBacking(
                        type=ToyVM.DiskBacking.Type.FILE,
                        file_path='xyz')))
    logging.info('Updated config of %s: %s\n', vm1, toyvm.get(vm1))

    logging.info('Initial config of %s: %s', vm2, toyvm.get(vm2))
    # VM config update
    toyvm.update(vm2, ToyVM.UpdateSpec(
                    mem_size=500,
                    disk_backing=ToyVM.DiskBacking(
                        type=ToyVM.DiskBacking.Type.DEVICE,
                        device_name='dev1',
                        client_device=False)))
    # Turning on VM
    toyvm.power_on(vm2)
    logging.info('Updated config and powered on %s: %s', vm2, toyvm.get(vm2))

    # Trying to turn on a VM that is already turned on
    try:
        toyvm.power_on(vm2)
        logging.info('Succeeded but should have failed')
    except AlreadyInDesiredState:
        logging.info('"%s" is already powered on', toyvm.get(vm1).display_name)

    toyvm.delete(vm1)
    toyvm.delete(vm2)
    logging.info('Deleted VMs')

if __name__ == '__main__':
     main()
```
</div>
</div>
**Note**: All operations in the Python static bindings require users to specify the arguments as keyword arguments. The generated client side bindings code provides the list of keyword arguments that an operation takes in the docstrings.

## Running the Client

Client code can be run directly:

Note: Make sure that the [environment](../setting_up/configuring_the_environment.md) is configured.

<div>
    <!-- Nav tabs -->
    <ul class="nav nav-tabs">
        <li class="active">
            <a id="linGenStubTab" href="#linClientRun" data-toggle="tab">Linux</a>
        </li>
        <li>
            <a id="winGenStubTab" href="#winClientRun" data-toggle="tab">Windows</a>
        </li>
    </ul>
    <div class="codePart">
        <!-- Tab panes -->
        <div class="tab-content">
            <div class="tab-pane active" id="linClientRun">
                vapi-pdk$ PYTHONPATH=build/toyvm/generated/client/python:$PYTHONPATH python ./toyvm/python/client/toyvm\_client.py
                <span class="collapseTitle collapsed" data-toggle="collapse" data-target="#clientRunOut">&nbsp;</span>
            </div>
            <div class="tab-pane" id="winClientRun">
                vapi-pdk> set PYTHONPATH=build\\toyvm\\generated\\client\\python;%PYTHONPATH%<br>
                vapi-pdk> python ./toyvm/python/client/toyvm\_client.py
                <span class="collapseTitle collapsed" data-toggle="collapse" data-target="#clientRunOut">&nbsp;</span>
            </div>
        </div>
        <div id='clientRunOut' class="collapseContent collapsing">
INFO:root:VMs created: set([u'vm-1', u'vm-2'])<br>
INFO:root:Initial config of vm-1: {display_name : new toyvm 1, disk_backing : {device_name : None, client_device : None, type : NONE, file_path : None}, mem_size : 250, num_cpus : 4, power_state : POWERED_OFF}<br>
INFO:root:Updated config of vm-1: {display_name : new toyvm 1, disk_backing : {device_name : None, client_device : None, type : FILE, file_path : xyz}, mem_size : 250, num_cpus : 3, power_state : POWERED_OFF}<br>
<br>
INFO:root:Initial config of vm-2: {display_name : new toyvm 2, disk_backing : {device_name : None, client_device : None, type : NONE, file_path : None}, mem_size : 250, num_cpus : 4, power_state : POWERED_OFF}<br>
INFO:root:Updated config and powered on vm-2: {display_name : new toyvm 2, disk_backing : {device_name : dev1, client_device : False, type : DEVICE, file_path : None}, mem_size : 500, num_cpus : 4, power_state : POWERED_ON}<br>
INFO:root:"new toyvm 1" is already powered on<br>
INFO:root:Deleted VMs<br>
        </div>
    </div>
</div>
# Using shell script

There is a script for each language that does all the steps listed above. We can execute the script in the following way:
<div class="codePart">
    vapi-pdk$ toyvm/python/build.sh
    <span class="collapseTitle collapsed" data-toggle="collapse" data-target="#scriptOut">&nbsp;</span>
    <div id="scriptOut" class="collapseContent collapse">
INFO: Generating metadata files ...<br>
<br>
INFO: Processing target language: metadata<br>
INFO:  > Found 5 metadata language 'products' templates ...<br>
INFO: Processing product scope templates ...<br>
INFO:  > Generating file: /dbc/pa-dbc1111/avaithi/vapi-main/vapi-core/build/vapicore/obj/vapi/build/toyvm/metadata/sample.first_authentication.json ...<br>
INFO:  > Generating file: /dbc/pa-dbc1111/avaithi/vapi-main/vapi-core/build/vapicore/obj/vapi/build/toyvm/metadata/sample.first_authorization.json ...<br>
INFO:  > Generating file: /dbc/pa-dbc1111/avaithi/vapi-main/vapi-core/build/vapicore/obj/vapi/build/toyvm/metadata/sample.first_cli.json ...<br>
INFO:  > Generating file: /dbc/pa-dbc1111/avaithi/vapi-main/vapi-core/build/vapicore/obj/vapi/build/toyvm/metadata/sample.first_metamodel.json ...<br>
INFO:  > Generating file: /dbc/pa-dbc1111/avaithi/vapi-main/vapi-core/build/vapicore/obj/vapi/build/toyvm/metadata/sample.first_routing.json ...<br>
<br>
INFO: Generating python files ...<br>
<br>
INFO: Processing target language: python<br>
INFO:  > Found 3 python language 'packages' templates ...<br>
INFO: Processing package scope templates for 'idl.model.IdlPackage(name:sample.first)' ...<br>
INFO:  > Generating file: /dbc/pa-dbc1111/avaithi/vapi-main/vapi-core/build/vapicore/obj/vapi/build/toyvm/generated/provider/python/sample/first_provider.py ...<br>
<br>
INFO: Generating python files ...<br>
<br>
INFO: Processing target language: python<br>
INFO:  > Found 3 python language 'packages' templates ...<br>
INFO: Processing package scope templates for 'idl.model.IdlPackage(name:sample.first)' ...<br>
INFO:  > Generating file: /dbc/pa-dbc1111/avaithi/vapi-main/vapi-core/build/vapicore/obj/vapi/build/toyvm/generated/client/python/sample/first_client.py ...<br>
</div>
</div>
Note: This script generates the files needed for the provider too.
After this build step, the client can be run by following the steps listed in the [previous section](#running-the-client).
