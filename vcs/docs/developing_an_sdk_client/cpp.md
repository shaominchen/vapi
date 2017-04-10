{% tocTag %} {% endtocTag %}

#Overview
Once the new provider service is up and running, you can use C++ to connect to the server and invoke its APIs. This tutorial provides instructions to stand up a client.

#Using scons
`scons` is used to build the ToyVM C++ client artifacts. The scons scripts generate the stubs from VMODL2 API definitions, compile these files and generate the executables.

***Note***: Before running the scons command, configure your environment using the instructions [here](../setting_up/configuring_the_environment.md#c).

<div class="codePart">
    vapi-pdk$ cd toyvm/cpp<br>
    vapi-pdk/toyvm/cpp$ ./components/cayman_scons/bin/scons install
</div>
The following command can then be used to run the client:
<div class="codePart">
    vapi-pdk/toyvm/cpp$ LD_LIBRARY_PATH=./build/install ./build/install/client
    <span class="collapseTitle collapsed" data-toggle="collapse" data-target="#clientOutputScons">&nbsp;</span>
    <div id="clientOutputScons" class="collapseContent collapse">

          *** Create progress: (0, 2, 1)<br>
          *** Create completed. Result:<br>
         "vm-1"<br>
          *** Get vm-1 completed. Result:<br>
         {<br>
             "STRUCTURE": {<br>
                 "sample.first.toy_VM.info": {<br>
                     "disk_backing": {<br>
                         "STRUCTURE": {<br>
                             "sample.first.toy_VM.disk_backing": {<br>
                                 "client_device": {<br>
                                     "OPTIONAL": null<br>
                                 },<br>
                                 "device_name": {<br>
                                     "OPTIONAL": null<br>
                                 },<br>
                                 "file_path": {<br>
                                     "OPTIONAL": null<br>
                                 },<br>
                                 "type": "NONE"<br>
                             }<br>
                         }<br>
                     },<br>
                     "display_name": "vm",<br>
                     "mem_size": 512,<br>
                     "num_cpus": 2,<br>
                     "power_state": "POWERED_OFF"<br>
                 }<br>
             }<br>
         }<br>
          *** Update vm-1 progress: (0, 3, 1)<br>
          *** Update vm-1 progress: (0, 3, 2)<br>
          *** Update vm-1 completed.<br>
         ...<br>

</div>
</div>

<div class="successPart">
**Success!** You have the C++ client running.
</div>

#Using cmake
`cmake` is used to build the ToyVM C++ client artifacts. The cmake scripts generate the stubs from VMODL2 API definitions, compile these files and generate the executables.

***Note***: Before running the cmake command, configure your environment using the instructions [here](../setting_up/configuring_the_environment.md#c).

The ToyVM C++ client artifacts are built as part of the same process as the server.  See the instructions [here](../standing_up_an_api_service/cpp.md#using-cmake)

The following command can then be used to run the client:
<div>
    <!-- Nav tabs -->
    <ul class="nav nav-tabs">
        <li class="active">
            <a id="linCMakeRunClientTab" href="#linCMakeRunClient" data-toggle="tab">Linux</a>
        </li>
        <li>
            <a id="winCMakeRunClientTab" href="#winCMakeRunClient" data-toggle="tab">Windows</a>
        </li>
    </ul>
    <div class="codePart">
        <!-- Tab panes -->
        <div class="tab-content">
            <div class="tab-pane active" id="linCMakeRunClient">
                   vapi-pdk$ cd toyvm/cpp_obj<br>
                   vapi-pdk/toyvm/cpp_obj$ ./client/ToyVmClient
                   <span class="collapseTitle collapsed" data-toggle="collapse" data-target="#linClientOutputCMake">&nbsp;</span>
                   <div id="linClientOutputCMake" class="collapseContent collapse">

                *** Create progress: (0, 2, 1)<br>
                *** Create completed. Result:<br>
               "vm-1"<br>
                *** Get vm-1 completed. Result:<br>
               {<br>
                   "STRUCTURE": {<br>
                       "sample.first.toy_VM.info": {<br>
                           "disk_backing": {<br>
                               "STRUCTURE": {<br>
                                   "sample.first.toy_VM.disk_backing": {<br>
                                       "client_device": {<br>
                                           "OPTIONAL": null<br>
                                       },<br>
                                       "device_name": {<br>
                                           "OPTIONAL": null<br>
                                       },<br>
                                       "file_path": {<br>
                                           "OPTIONAL": null<br>
                                       },<br>
                                       "type": "NONE"<br>
                                   }<br>
                               }<br>
                           },<br>
                           "display_name": "vm",<br>
                           "mem_size": 512,<br>
                           "num_cpus": 2,<br>
                           "power_state": "POWERED_OFF"<br>
                       }<br>
                   }<br>
               }<br>
                *** Update vm-1 progress: (0, 3, 1)<br>
                *** Update vm-1 progress: (0, 3, 2)<br>
                *** Update vm-1 completed.<br>
               ...<br>

            </div>
            </div>
            <div class="tab-pane" id="winCMakeRunClient">
                   vapi-pdk> toyvm\publish\bin\ToyVmClient.exe
                   <span class="collapseTitle collapsed" data-toggle="collapse" data-target="#winClientOutputCMake">&nbsp;</span>
                   <div id="winClientOutputCMake" class="collapseContent collapse">

                *** Create progress: (0, 2, 1)<br>
                *** Create completed. Result:<br>
               "vm-1"<br>
                *** Get vm-1 completed. Result:<br>
               {<br>
                   "STRUCTURE": {<br>
                       "sample.first.toy_VM.info": {<br>
                           "disk_backing": {<br>
                               "STRUCTURE": {<br>
                                   "sample.first.toy_VM.disk_backing": {<br>
                                       "client_device": {<br>
                                           "OPTIONAL": null<br>
                                       },<br>
                                       "device_name": {<br>
                                           "OPTIONAL": null<br>
                                       },<br>
                                       "file_path": {<br>
                                           "OPTIONAL": null<br>
                                       },<br>
                                       "type": "NONE"<br>
                                   }<br>
                               }<br>
                           },<br>
                           "display_name": "vm",<br>
                           "mem_size": 512,<br>
                           "num_cpus": 2,<br>
                           "power_state": "POWERED_OFF"<br>
                       }<br>
                   }<br>
               }<br>
                *** Update vm-1 progress: (0, 3, 1)<br>
                *** Update vm-1 progress: (0, 3, 2)<br>
                *** Update vm-1 completed.<br>
               ...<br>
            </div>
            </div>
        </div>
    </div>
</div>

<div class="successPart">
**Success!** You have the C++ client running.
</div>

#Using individual commands
##Generating Stubs
The toyvm C++ bindings are produced and compiled via `scons` as a separate shared library with the following script:

<div class="codePart">
    $ cat scons/toyvm-bindings.sc
</div>
`toyvm-bindings.sc` scons file is using the `cpp-generator` to generate the stubs. To compile the VMODL2 file into C++ stubs from the command line, the following command can be used:

<div class="codePart">
    vapi-pdk$ chmod 755 vapicpp-emitter/bin/cpp-generator<br>
    vapi-pdk$ cd toyvm/cpp<br>
    vapi-pdk/toyvm/cpp$ ../../vapicpp-emitter/bin/cpp-generator --name sample.first --library ../../idl-toolkit/models/vapi_stdlib.vmidl --output build/toyvm-bindings ../vmodl
</div>
- A `build/toyvm-bindings/cpp` directory is created as a result if this command which contains the generated bindings.
- Passing product name with `--name` is mandatory as vAPI-C++ bindings export/import symbols per product.
- Passing the `vapi-stdlib.vmidl` with `--library` is also mandatory as the toyvm VMODL uses standard errors.

##Implementing the Client
The following are the key steps used while writing a client:
-   Create a ApiProvider creator class which:
    -   Creates an async http client transport by passing the io\_service, host & port.
    -   Creates an protocol specific AsyncApiProvider that can connect to a protocol handler on the other side(e.g. JsonAsyncApiProvider).
-   Create a ApiProviderPool passing it the ApiProvider creator so it can create providers on demand.
-   Construct an ApiStub that will execute our requests.
-   Construct a specialized AsyncServiceStub for the service - ToyVmSvc::AsyncServiceStub
-   Invoke methods from the created service stub object passing a callback to be executed with the result.

The client code itself can be found here:
<div class="codePart">
    vapi-pdk/toyvm/cpp$ cat client/main.cpp
</div>
The following `scons` file is used to compile the client:
<div class="codePart">
    vapi-pdk/toyvm/cpp$ cat scons/client.sc
</div>
##Building the Client

Before the client can be run, SCons needs to install it. SCons will create a 'build/install' directory if `target` is omitted or if `target` is `install` and copy executables and dependencies there.
<div class="codePart">
    vapi-pdk/toyvm/cpp$ ./components/cayman_scons/bin/scons install
</div>
&nbsp;
