{% tocTag %} {% endtocTag %}

#Overview
After defining the service interface using VMODL2, the next step is to implement and stand up the service. This part of the tutorial provides instructions to stand up the service.

#Using scons
`scons` is used to build the ToyVM C++ server artifacts. The scons scripts generate the skeletons from VMODL2 API definitions, compile these files and generate the executables.

***Note***: Before running the scons command, configure your environment using the instructions [here](../setting_up/configuring_the_environment.md#c).

<div class="codePart">
    vapi-pdk$ cd toyvm/cpp<br>
    vapi-pdk/toyvm/cpp$ ./components/cayman_scons/bin/scons install<br>
    <span class="collapseTitle collapsed" data-toggle="collapse" data-target="#buildOutput">&nbsp;</span>
    <div id="buildOutput" class="collapseContent collapse">
...<br>
...<br>
Install file: "build/provider/provider" as "build/install/provider"<br>
Install file: "components/cayman_pion/lin64-cayman/lib/libpion.so" as "build/install/libpion.so"<br>
Install file: "/build/toolchain/lin64/openssl-1.0.1j/lib/libcrypto.so.1.0.1" as "build/install/libcrypto.so.1.0.1"<br>
Install file: "/build/toolchain/lin64/openssl-1.0.1j/lib/libssl.so.1.0.1" as "build/install/libssl.so.1.0.1"<br>
Install file: "components/cayman_esx_toolchain/usr/x86_64-vmk-linux-gnu/lib64/libstdc++.so.6" as "build/install/libstdc++.so.6"<br>
Install file: "/dbc/pa-dbc1122/medisettyu/vapiq12015/pdk/vapicpp/vapi-core/lin64-cayman/lib-debug/libvapi-core.so.2" as "build/install/libvapi-core.so.2"<br>
Install file: "/dbc/pa-dbc1122/medisettyu/vapiq12015/pdk/vapicpp/vapi-json/lin64-cayman/lib-debug/libvapi-json.so.2" as "build/install/libvapi-json.so.2"<br>
Install file: "/dbc/pa-dbc1122/medisettyu/vapiq12015/pdk/vapicpp/vapi-xml/lin64-cayman/lib-debug/libvapi-xml.so.2" as "build/install/libvapi-xml.so.2"<br>
Install file: "/dbc/pa-dbc1122/medisettyu/vapiq12015/pdk/vapicpp/vapi-std-bindings/lin64-cayman/lib-debug/libvapi-std-bindings.so.2" as "build/install/libvapi-std-bindings.so.2"<br>
scons: done building targets.<br>
</div>
</div>

<div class="successPart">
**Success!** You have built the required binaries to run the C++ server.
</div>

The following command can then be used to run the ToyVM service on the server:
<div class="codePart">
    vapi-pdk/toyvm/cpp$ cd build/install<br>
    vapi-pdk/toyvm/cpp/build/install$ LD_LIBRARY_PATH=. ./provider<br>
    <span class="collapseTitle collapsed" data-toggle="collapse" data-target="#serverOutput">&nbsp;</span>
    <div id="serverOutput" class="collapseContent collapse">
    using port: 8088<br>
    Loading Cli metadata file: metadata/toyvm_cli.json<br>
    Loading Metamodel metadata file: metadata/toyvm_metamodel.json<br>
    Loading Authn metadata file: metadata/toyvm_authentication.json<br>
    listening on: 0.0.0.0:8088<br>
</div>
</div>

<div class="successPart">
**Success!** You have the C++ server running now.
</div>

#Using cmake
`cmake` is used to build the ToyVM C++ server artifacts. The cmake scripts generate the skeletons from VMODL2 API definitions, compile these files and generate the executables.

The cmake based build allows to configure the location of the dependencies as
well as the used compilers and flags.

Compiling using cmake consists of two steps:
- generating the build
- running the build to produce binaries


***Note***: Before running the cmake command, configure your environment using the instructions [here](../setting_up/configuring_the_environment.md#c).

The build solution is generated using cmake:

<div>
    <!-- Nav tabs -->
    <ul class="nav nav-tabs">
        <li class="active">
            <a id="linCMakeConfigTab" href="#linCMakeConfig" data-toggle="tab">Linux</a>
        </li>
        <li>
            <a id="winCMakeConfigTab" href="#winCMakeConfig" data-toggle="tab">Windows</a>
        </li>
    </ul>
    <div class="codePart">
        <!-- Tab panes -->
        <div class="tab-content">
            <div class="tab-pane active" id="linCMakeConfig">
               vapi-pdk$ mkdir toyvm/cpp_obj<br>
               vapi-pdk$ cd toyvm/cpp_obj<br>
               vapi-pdk/toyvm/cpp_obj$ $VAPI_PDK/toyvm/cpp/components/cayman_cmake_lin32/bin/cmake \<br>
                  -DCMAKE_INSTALL_PREFIX=$VAPI_PDK/toyvm/publish \<br>
                  -DCMAKE_C_COMPILER="$VAPI_PDK/toyvm/cpp/components/cayman_esx_toolchain/usr/bin/x86_64-vmk-linux-gnu-gcc" \<br>
                  -DCMAKE_CXX_COMPILER="$VAPI_PDK/toyvm/cpp/components/cayman_esx_toolchain/usr/bin/x86_64-vmk-linux-gnu-g++" \<br>
                  -DCMAKE_C_FLAGS="--sysroot=/build/toolchain/lin64/glibc-2.11.1-0.17.4" \<br>
                  -DCMAKE_CXX_FLAGS="--sysroot=/build/toolchain/lin64/glibc-2.11.1-0.17.4 -std=c++11 -fPIC -Wno-deprecated -Wno-unused-local-typedefs" \<br>
                  -DBOOST_ROOT=$VAPI_PDK/toyvm/cpp/components//boost1550_lin64_gcc480/ \<br>
                  -DPION_ROOT=$VAPI_PDK/toyvm/cpp/components//cayman_pion/ \<br>
                  -DVMWOPENSSL_ROOT=/build/toolchain/lin64/openssl-1.0.1j \<br>
                  -DVMWOPENSSL_PRODUCT=lin64-cayman \<br>
                  -DZLIB_ROOT=/build/toolchain/lin64/zlib-1.2.3-3 \<br>
                  -DPION_PRODUCT=lin64-cayman \<br>
                  -DVAPICPP_ROOT=$VAPI_PDK/vapicpp \<br>
                  -DVAPICPP_PRODUCT=lin64-cayman \<br>
                  -DVAPICPP_GENERATOR_ROOT=$VAPI_PDK/vapicpp-emitter \<br>
                  -DVAPICPP_VMODLPARSER_ROOT=$VAPI_PDK/vapicpp-emitter \<br>
                  -DVAPICPP_METADATAGENERATOR_ROOT=$VAPI_PDK/metadata-toolkit \<br>
                  -DTOYVM_VMODL2_ROOT=$VAPI_PDK/toyvm/vmodl \<br>
                  -DVAPI_IDL_TOOLKIT_ROOT=$VAPI_PDK/idl-toolkit \<br>
                  -DSHELL_EXECUTABLE=/bin/bash \<br>
                  $VAPI_PDK/toyvm/cpp/<br>
               <span class="collapseTitle collapsed" data-toggle="collapse" data-target="#linCmakeGenOutput">&nbsp;</span>
               <div id="linCmakeGenOutput" class="collapseContent collapse">
               ...<br>
               ...<br>
               -- Configuring done<br>
               -- Generating done<br>
               -- Build files have been written to:<br>
            </div>
            </div>
            <div class="tab-pane" id="winCMakeConfig">
               vapi-pdk> mkdir toyvm\cpp_obj<br>
               vapi-pdk> cd toyvm\cpp_obj<br>
               vapi-pdk\toyvm\cpp_obj> %VAPI_PDK%\toyvm\cpp\components\cayman_cmake_win32\bin\cmake ^<br>
                  -DCMAKE_INSTALL_PREFIX=%VAPI_PDK%\toyvm\publish ^<br>
                  -DCMAKE_BUILD_TYPE=Release ^<br>
                  -G "NMake Makefiles" ^<br>
                  -DBOOST_ROOT=%VAPI_PDK%\toyvm\cpp\components\\boost1550_win64_vc120 ^<br>
                  -DPION_ROOT=%VAPI_PDK%\toyvm\cpp\components\\cayman_pion ^<br>
                  -DVMWOPENSSL_ROOT=%TCROOT%\win64\openssl-1.0.1j-vc110 ^<br>
                  -DVMWOPENSSL_PRODUCT=win64_vc120 ^<br>
                  -DZLIB_ROOT=%TCROOT%\win64\zlib-1.2.3-6-vc90sp1 ^<br>
                  -DPION_PRODUCT=win64_vc120 ^<br>
                  -DVAPICPP_ROOT=%VAPI_PDK%\vapicpp ^<br>
                  -DVAPICPP_PRODUCT=win64_vc120 ^<br>
                  -DVAPICPP_GENERATOR_ROOT=%VAPI_PDK%\vapicpp-emitter ^<br>
                  -DVAPICPP_VMODLPARSER_ROOT=%VAPI_PDK%\vapicpp-emitter ^<br>
                  -DVAPICPP_METADATAGENERATOR_ROOT=%VAPI_PDK%/metadata-toolkit ^<br>
                  -DTOYVM_VMODL2_ROOT=%VAPI_PDK%\toyvm\vmodl ^<br>
                  -DVAPI_IDL_TOOLKIT_ROOT=%VAPI_PDK%\idl-toolkit ^<br>
                  -DCMAKE_VERBOSE_MAKEFILE=TRUE ^<br>
                  %VAPI_PDK%\toyvm\cpp<br>
               <span class="collapseTitle collapsed" data-toggle="collapse" data-target="#winCmakeGenOutput">&nbsp;</span>
               <div id="winCmakeGenOutput" class="collapseContent collapse">
               ...<br>
               ...<br>
               -- Configuring done<br>
               -- Generating done<br>
               -- Build files have been written to:<br>
            </div>
            </div>
        </div>
    </div>
</div>


<div class="successPart">
**Success!** You have generated the build solution that can build the C++ server
</div>

The C++ server binaries can be built with the generated solution:

<div>
    <!-- Nav tabs -->
    <ul class="nav nav-tabs">
        <li class="active">
            <a id="linCMakeBuildTab" href="#linCMakeBuild" data-toggle="tab">Linux</a>
        </li>
        <li>
            <a id="winCMakeBuildTab" href="#winCMakeBuild" data-toggle="tab">Windows</a>
        </li>
    </ul>
    <div class="codePart">
        <!-- Tab panes -->
        <div class="tab-content">
            <div class="tab-pane active" id="linCMakeBuild">
               vapi-pdk/toyvm/cpp_obj$ make install
               <span class="collapseTitle collapsed" data-toggle="collapse" data-target="#linCmakeBuildOutput">&nbsp;</span>
               <div id="linCmakeBuildOutput" class="collapseContent collapse">
               ...<br>
               ...<br>
                [88%] Built target toyvm_metadata<br>
                Scanning dependencies of target ToyVmClient<br>
                [100%] Building CXX object<br>
                client/CMakeFiles/ToyVmClient.dir/main.cpp.o<br>
                Linking CXX executable ToyVmClient<br>
                [100%] Built target ToyVmClient<br>
                Install the project...<br>
                </div>
            </div>
            <div class="tab-pane" id="winCMakeBuild">
               vapi-pdk\toyvm\cpp_obj> nmake install
               <span class="collapseTitle collapsed" data-toggle="collapse" data-target="#winCmakeBuildOutput">&nbsp;</span>
               <div id="winCmakeBuildOutput" class="collapseContent collapse">
               ...<br>
               ...<br>
                [88%] Built target toyvm_metadata<br>
                Scanning dependencies of target ToyVmClient<br>
                [100%] Building CXX object<br>
                client/CMakeFiles/ToyVmClient.dir/main.cpp.o<br>
                Linking CXX executable ToyVmClient<br>
                [100%] Built target ToyVmClient<br>
                Install the project...<br>
                </div>
            </div>
        </div>
    </div>
</div>


<div class="successPart">
**Success!** You have built the required binaries to run the C++ server.
</div>

The following command can then be used to run the ToyVM service on the server:
<div>
    <!-- Nav tabs -->
    <ul class="nav nav-tabs">
        <li class="active">
            <a id="linCMakeRunTab" href="#linCMakeRun" data-toggle="tab">Linux</a>
        </li>
        <li>
            <a id="winCMakeRunTab" href="#winCMakeRun" data-toggle="tab">Windows</a>
        </li>
    </ul>
    <div class="codePart">
        <!-- Tab panes -->
        <div class="tab-content">
            <div class="tab-pane active" id="linCMakeRun">
               vapi-pdk/toyvm/cpp_obj$ ./provider/ToyVmServer
               <span class="collapseTitle collapsed" data-toggle="collapse" data-target="#linCMakeServerOutput">&nbsp;</span>
               <div id="linCMakeServerOutput" class="collapseContent collapse">
               using port: 8088<br>
               Loading Cli metadata file: metadata/toyvm_cli.json<br>
               Loading Metamodel metadata file: metadata/toyvm_metamodel.json<br>
               Loading Authn metadata file: metadata/toyvm_authentication.json<br>
               listening on: 0.0.0.0:8088
               </div>
            </div>
            <div class="tab-pane" id="winCMakeRun">
               vapi-pdk> %VAPI_PDK%\toyvm\publish\bin\ToyVmServer.exe
               <span class="collapseTitle collapsed" data-toggle="collapse" data-target="#winCMakeServerOutput">&nbsp;</span>
               <div id="winCMakeServerOutput" class="collapseContent collapse">
               using port: 8088<br>
               Loading Cli metadata file: metadata\toyvm_cli.json<br>
               Loading Metamodel metadata file: metadata\toyvm_metamodel.json<br>
               Loading Authn metadata file: metadata\toyvm_authentication.json<br>
               listening on: 0.0.0.0:8088
               </div>
            </div>
        </div>
    </div>
</div>

<div class="successPart">
**Success!** You have the C++ server running now.
</div>

#Using individual commands
This section explains in detail the steps that are performed by the scons scripts.

##Generating Skeleton
The toyvm C++ bindings are produced and compiled via `scons` as a separate shared library with the following script:
<div class="codePart">
    vapi-pdk/toyvm/cpp$ cat scons/toyvm-bindings.sc
</div>

`toyvm-bindings.sc` scons file is using the `cpp-generator` to generate the skeletons. To compile the VMODL2 file into C++ bindings from the command line, the following command can be used:

<div class="codePart">
    vapi-pdk$ chmod 755 vapicpp-emitter/bin/cpp-generator<br>
    vapi-pdk$ cd toyvm/cpp<br>
    vapi-pdk/toyvm/cpp$ ../../vapicpp-emitter/bin/cpp-generator --name sample.first --library ../../idl-toolkit/models/vapi_stdlib.vmidl --output build/toyvm-bindings ../vmodl
</div>

- A `build/toyvm-bindings/cpp` directory is created as a result if this command which contains the generated bindings.
- Passing component name with `--name` is mandatory as vAPI-C++ bindings export/import symbols per component.
- Passing the `vapi-stdlib.vmidl` with `--library` is also mandatory as the ToyVM VMODL2 uses standard errors.

##Generating the metadata files
The toyvm Metadata files are produced via `scons` with the following script:
<div class="codePart">
    vapi-pdk/toyvm/cpp$ cat scons/toyvm-metadata.sc
</div>

`toyvm-metadata.sc` scons file is using the `metadata-generator` to generate the metadata json files. To generate the metadata from the command line, the following command can be used:

<div class="codePart">
    vapi-pdk$ chmod 755 metadata-toolkit/bin/metadata-generator<br>
    vapi-pdk$ cd toyvm/cpp<br>
    vapi-pdk/toyvm/cpp$ ../../metadata-toolkit/bin/metadata-generator --name toyvm --library ../../idl-toolkit/models/vapi_stdlib.vmidl --output build/install ../vmodl
</div>

- A `build/install/metadata` directory is created as a result if this command which contains the generated json files.
- Passing component name with `--name` is mandatory and defines the basename of the generated files.
- Passing the `vapi-stdlib.vmidl` with `--library` is also mandatory as the ToyVM VMODL2 uses standard errors.

##Implementing the Interface

The generated templates contain a lot of files. The two headers that are needed by every async service implementation are AsyncServiceInterface[Fwd].h and Structures[Fwd].h.

The header of the ToyVM service contains the service interface. It is at:
<div class="codePart">
    vapi-pdk/toyvm/cpp$ cat provider/ToyVmImpl.h
</div>

- All classes generated for this service reside in namespace Vapi::[Async::]<service_namespace>::<service_name>::Service, e.g Vapi::[Async::]Sample::First::ToyVm::Service. This way no matter how services are organized in packages, there is no way that one service will be able to 'see' the structures of another.
- The headers are split into common, stub and skeleton directories. Client side uses common + stub directories and provider side uses common + skeleton directories.
- The headers ending with Fwd contain only forward declarations.
- The header ./common/Structures.h contains all the structures defined inside the ToyVM service interface. They are not generated inside the interface itself to make them easier to use and forward declare. All the structures are header-only.
- The header ./common/Methods.h contains the information about the types of the input arguments, the return type and the definitions for all the methods in the service.

The ToyVM service implementation is in:
<div class="codePart">
    vapi-pdk/toyvm/cpp$ cat provider/ToyVmImpl.cpp
</div>

The implementation is fairly trivial. The callbacks are explained in the [vAPI-C++ specs wiki](https://wiki.eng.vmware.com/VAPI/Bindings/Cpp#Services_.26_Operations). They are instances of the vAPI AsyncResult<T> class.

The ToyVM service uses its own error messages. They are declared in:
<div class="codePart">
    vapi-pdk/toyvm/cpp$ cat provider/MessageResolver.cpp
</div>

The implementation is compiled as part of the provider binary using the following `scons` file:
<div class="codePart">
    vapi-pdk/toyvm/cpp$ cat scons/provider.sc
</div>

The implementation is compiled as part of the provider binary using the following `cmake` file:
<div class="codePart">
    vapi-pdk/toyvm/cpp$ cat provider/CMakeLists.txt
</div>

Some services have lots of methods. Writing their definitions manually in a header/source file is time consuming. The `vapicpp-emitter` provides a boilerplate implementation of the service impl header/source file via the `--profile impl` option.

<div class="codePart">
    vapi-pdk/toyvm/cpp$ ../../vapicpp-emitter/bin/cpp-generator --profile impl --name sample.first --library ../../idl-toolkit/models/vapi_stdlib.vmidl --output build/toyvm-bindings ../vmodl
</div>

There is an async HTTP transport implementation that comes as part of the runtime and is compatible with Java and python.

##Implementing a Server
The code used to start a server consists of following steps:
-   Create the services that are to be exposed through the ApiProvider(LocalProvider).
-   Wrap the services into ApiInterfaces through their respective binders.
-   Create the metadata container instances and load the corresponding metadata json files. (optional)
-   Create the metadata services and wrap them into ApiInterfaces through the Metadata ProviderHelper. (optional)
-   Create a LocalProvider instance and register the ApiInterfaces.
-   Attach the introspection support to the LocalProvider through the Introspection ProviderHelper. (optional)
-   Create an io\_service, a tcp::acceptor and an endpoint.
-   Create a ServerRequestHandler and register it for the required http resource.
-   Start accepting requests.

The above steps are implemented in the following file:
<div class="codePart">
    vapi-pdk/toyvm/cpp$ cat provider/main.cpp
</div>

The following `scons` file is used to compile the server:
<div class="codePart">
    vapi-pdk/toyvm/cpp$ cat scons/provider.sc
</div>

The following `cmake` file is used to compile the server:
<div class="codePart">
    vapi-pdk/toyvm/cpp$ cat provider/CMakeLists.txt
</div>

