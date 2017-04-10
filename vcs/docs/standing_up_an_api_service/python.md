{% tocTag %} {% endtocTag %}

# Overview
After defining the service interface using VMODL2, the next step is to
implement and stand up the service. The tutorial provides instructions
to stand up the service by running individual commands or using shell
script. The underlying steps in the script are the same as the sub-steps
while executing individual commands.
#Using script
Using a simple shell script we can generate the artifacts for running the Python server.
<div class="codePart">
    vapi-pdk$ toyvm/python/build.sh
   <span class="collapseTitle collapsed" data-toggle="collapse" data-target="#buildScript">&nbsp;</span>
   <div id="buildScript" class="collapseContent collapse">
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

<div class="successPart">
  **Success!** You have generated the artifacts required to run the Python server.
</div>

The following commands can then be used to run the server.

**Note**: Before running the server, configure your environment using the instructions [here](../setting_up/configuring_the_environment.md#python).

<div>
  <!-- Nav tabs -->
  <ul class="nav nav-tabs">
      <li class="active">
          <a id="venvTab" href="#venvServerRun" data-toggle="tab">Virtualenv</a>
      </li>
      <li>
          <a id="toolchainTab" href="#tcServerRun" data-toggle="tab">Toolchain</a>
      </li>
  </ul>
  <div class="codePart">
    <!-- Tab panes -->
    <div class="tab-content">
      <div class="tab-pane active" id="venvServerRun">
        vapi-pdk$ . vapienv/bin/activate<br>
        (vapienv) vapi-pdk$ export PYTHONPATH=toyvm/python/provider:build/toyvm/generated/provider/python:$PYTHONPATH<br>
        (vapienv) vapi-pdk$ export VAPI_CONFIG=toyvm/python/conf/wsgi.properties<br>
        (vapienv) vapi-pdk$ twistd -n web -p 8088 --wsgi vmware.vapi.wsgi.application<br>
        <span class="collapseTitle collapsed" data-toggle="collapse" data-target="#venvServerRunOutput">&nbsp;</span>
        <div id="venvServerRunOutput" class="collapseContent collapse">
          ..<br>
          ..<br>
          2015-04-22 17:36:26-0700 [-] Log opened.<br/>
          2015-04-22 17:36:26-0700 [-] twistd 15.1.0 (/dbc/pa-dbc1122/medisettyu/vapicore2/vapi-core/build/vapicore/obj/vapi/venv/bin/python 2.7.1) starting up.<br/>
          2015-04-22 17:36:26-0700 [-] reactor class: twisted.internet.epollreactor.EPollReactor.<br/>
          2015-04-22 17:36:26-0700 [-] Site starting on 8088<br/>
          2015-04-22 17:36:26-0700 [-] Starting factory &lttwisted.web.server.Site instance at 0x4cbccb0&gt<br/>
        </div>
      </div>
      <div class="tab-pane" id="tcServerRun">
        vapi-pdk$ PYTHONPATH=toyvm/python/provider:build/toyvm/generated/provider/python:$PYTHONPATH<br/>
        vapi-pdk$ export VAPI_CONFIG=toyvm/python/conf/wsgi.properties<br/>
        vapi-pdk$ /build/toolchain/lin64/twisted-14.0.0/bin/twistd -n web -p 8088 --wsgi vmware.vapi.wsgi.application<br/>
        <span class="collapseTitle collapsed" data-toggle="collapse" data-target="#tcServerRunOutput">&nbsp;</span>
        <div id="tcServerRunOutput" class="collapseContent collapse">
          ..<br/>
          ..<br/>
          2015-07-01 19:27:01-0700 [-] Log opened.<br/>
          2015-07-01 19:27:01-0700 [-] twistd 14.0.0 (/build/toolchain/lin64/python-2.7.1/bin/python 2.7.1) starting up.<br/>
          2015-07-01 19:27:01-0700 [-] reactor class: twisted.internet.epollreactor.EPollReactor.<br/>
          2015-07-01 19:27:01-0700 [-] Site starting on 8088<br/>
          2015-07-01 19:27:01-0700 [-] Starting factory &lttwisted.web.server.Site instance at 0x4d66950&gt<br/>
        </div>
      </div>
    </div>
  </div>
</div>

<div class="successPart">
**Success!** You have the Python server running now.
</div>

#Using individual commands
##Generating Skeleton
The PDK includes a code generator `python-generator` that parses the
interface definition and generates language binding code.
<div class="codePart">
    vapi-pdk$ python-toolkit/bin/python-generator --profile provider --library idl-toolkit/vmidl/vapi_stdlib.vmidl --output build/toyvm/generated/provider toyvm/vmodl
   <span class="collapseTitle collapsed" data-toggle="collapse" data-target="#skeletonGen">&nbsp;</span>
   <div id="skeletonGen" class="collapseContent collapse">
Generating vAPI Python provider bindings<br>
INFO: Generating python files ...<br>
INFO: Processing target language: python<br>
INFO:  > Found 3 python language 'packages' templates ...<br>
INFO: Processing package scope templates for 'idl.model.IdlPackage(name:sample.first)' ...<br>
INFO:  > Generating file: build/toyvm/generated/provider/python/sample/first_provider.py ...<br>
    </div>
</div>

The generator creates a Python package and module structure based on the
services defined in the IDL. These files are put in a sub-directory
named python under the provided output directory.

For the toyvm VMODL2 interface, since the package is sample.first, it
generates a `first_provider.py`. The file contains two classes: ToyVM
and ToyVMSkeleton. The ToyVM class is pretty much a faithful mapping of
the service interface into Python, and the ToyVMSkeleton class contains
the methods required to expose this interface through the vAPI
infrastructure. Instead of one VMODL2 file, if the package path is or
multiple services of the same package are provided to the generator,
similar classes are generated for all those services defined in that
package. All these classes would be put in the same `first_provider.py`.

####Generating metadata
 The API infrastructure provides metadata services that expose the
information about the API to the clients. This will enable clients to
introspect and discover APIs available on a particular vAPI server. The
PDK provides `metadata-generator` that generates json metadata files
based on the input API definition. The metadata services expose the data
present in these metadata files.
<div class="codePart">
    vapi-pdk$ metadata-toolkit/bin/metadata-generator --library
idl-toolkit/vmidl/vapi_stdlib.vmidl --output build/toyvm toyvm/vmodl
    <span class="collapseTitle collapsed" data-toggle="collapse" data-target="#metaGen">&nbsp;</span>
   <div id="metaGen" class="collapseContent collapse">
INFO: Generating metadata files ...<br>
INFO: Processing target language: metadata<br>
INFO:  > Found 5 metadata language 'products' templates ...<br>
INFO: Processing product scope templates ...<br>
INFO:  > Generating file: build/toyvm/metadata/sample.first_authentication.json ...<br>
INFO:  > Generating file: build/toyvm/metadata/sample.first_authorization.json ...<br>
INFO:  > Generating file: build/toyvm/metadata/sample.first_cli.json ...<br>
INFO:  > Generating file: build/toyvm/metadata/sample.first_metamodel.json ...<br>
INFO:  > Generating file: build/toyvm/metadata/sample.first_routing.json ...<br>
    </div>
</div>

The generated metadata files will be referenced in the service configuration file that details the configuration information for ToyVM and all the metadata services.
**Note**: We pass as argument to the `metadata-generator` , the path to the package that contains the vmodl file. The generator aggregates all the vmodl files in the package while generating the metadata files. The aggregation is based on the `package-info.java` which defines which component the files in the package and the sub-packages belong too. More information about packaging can be obtained at the [vAPI specs page](https://wiki.eng.vmware.com/VAPI/Specs/VMODL2#Packaging).
Library option is provided to enable inter component structure reference. The `vapi_stdlib.vmidl` file lists the elements that are defined in the `com.vmware.vapi.std` component. This is needed because, `ToyVM` service in `sample.first` component defined in `$VAPI-PDK/toyvm/vmodl` directory, is referring the `Error` structures defined in `com.vmware.vapi.std.errors` package of `com.vmware.vapi.std` component.

-   The mapping between APIs and CLIs is governed by a JSON metadata file, `sample.first_cli.json` . This file describes the set of CLI namespaces and commands and how they map to the underlying CLIs; it also contains information about output formatting and other CLI-specific metadata.
    -   It's possible to customize the API-to-CLI mapping to change the output formatting options, rename command-line options or add short options for convenience, etc., but such customizations are beyond the scope of this tutorial; in this example we'll go ahead and use the generated metadata file.

-   The `sample.first_metamodel.json` file contains elements that represent all the information present in the interface definition language (IDL) specification. The main elements are component, package, service, operation, structure and enumeration.

-   Since ToyVM does not using authentication (or) vAPI authorization filter (or) vAPI routing functionality the authentication.json, authorization.json and routing.json files do not contain any useful information and will not be used anywhere. More information about these functionalities can be found here: [Authentication](https://wiki.eng.vmware.com/VAPI/Specs/Design/Authentication "VAPI/Specs/Design/Authentication"), [Authorization](https://wiki.eng.vmware.com/VAPI/Specs/Design/Authorization "VAPI/Specs/Design/Authorization") and [Routing](https://wiki.eng.vmware.com/VAPI/Specs/Router "VAPI/Specs/Router")

##Implementing the Interface
Implementation of the ToyVM service is accomplished by building a class
that implements the generated Python interface and defining a
`register_instance` method for registering the service implementation
with the vAPI runtime.

To bootstrap the implementation process, the code generator can create a
Python provider implementation template. Developers can copy this
template file, rename it as required and fill in the method
implementations.
<div class="codePart">
    vapi-pdk$ python-toolkit/bin/python-generator --profile template --library idl-toolkit/vmidl/vapi_stdlib.vmidl --output build/toyvm/generated/provider toyvm/vmodl/sample/first
    <span class="collapseTitle collapsed" data-toggle="collapse" data-target="#implGen">&nbsp;</span>
    <div id="implGen" class="collapseContent collapse">
Generating vAPI Python provider template<br>
    INFO: Generating python files ...<br>
    INFO: Processing target language: python<br>
    INFO:  > Found 3 python language 'packages' templates ...<br>
    INFO: Processing package scope templates for for 'idl.model.IdlPackage(name:sample.first)' ...<br>
    INFO:  > Generating file: build/tutorial/generated/provider/python/sample/first_impl.py.template ...<br>
   </div>
</div>

**Note**: The template generation is not required step. It is there only to make implementation easier.

##Configuring the Service
To stand up a vAPI-Python server for the ToyVM service, we need to
create a properties file that contains information about which services
to load and which wire protocols to use. The file uses the Python
ConfigParser format, which is similar to the Windows INI format.
<div class="codePart">
  vapi-pdk$ cat toyvm/python/conf/toyvm.properties
  <span class="collapseTitle collapsed" data-toggle="collapse" data-target="#catProps">&nbsp;</span>
  <div id="catProps" class="collapseContent collapse">
```
[endpoint]
provider.type=LocalProvider
provider.name=toyvm
local.interfaces=\
        sample.first.toyvm_impl,\
        com.vmware.vapi.metadata.metamodel_impl,\
        com.vmware.vapi.metadata.cli.command_impl,\
        com.vmware.vapi.metadata.cli.namespace_impl,\
        com.vmware.vapi.metadata.cli.source_impl

protocol=tutorial
protocol.tutorial.msg=json
protocol.tutorial.rpc=wsgi
protocol.tutorial.rpc.server=wsgi
# To use the twisted server, comment out the two lines above and
# uncomment out the two lines below.
#protocol.tutorial.rpc=http://localhost:8088/api
#protocol.tutorial.rpc.server=twisted

[jsonrpc]
prefix=/api

[rest]
prefix=/rest

# File paths in the following two sections follow from $VAPI-PDK directory since
# that is the directory from which ToyVM service will be started.
# <component-name>_[cli/metamodel].json files are JSON metadata files that are used
# to expose the cli/metamodel services for that component.

[com.vmware.vapi.metadata.cli.source_impl]
sources=common,toyvm
sources.common.filepath=metadata/cli/vapi_common_cli.json
sources.common.type=FILE
sources.toyvm.filepath=build/toyvm/metadata/sample.first_cli.json
sources.toyvm.type=FILE

[com.vmware.vapi.metadata.metamodel_impl]
sources=common,toyvm
sources.common.filepath=metadata/api/vapi_common_metamodel.json
sources.common.type=FILE
sources.toyvm.filepath=build/toyvm/metadata/sample.first_metamodel.json
sources.toyvm.type=FILE

# Configuration for ToyVM service
[sample.first.toyvm_impl]
min_cpus = 1
max_cpus = 128
default_num_cpus = 4
default_mem_size = 250

#Logger configurations
[loggers]
keys=root

[handlers]
keys=console

[formatters]
keys=default

[logger_root]
level=INFO
handlers=console

[handler_console]
class=logging.StreamHandler
formatter=default
level=INFO
args=(sys.stdout,)

[formatter_default]
format='%(asctime)s %(levelname)-8s %(name)-15s %(message)s'
```
</div>
</div>
The first part of the configuration file, i.e., the `[endpoint]` section
defines the details of endpoint. It specifies the provider and metadata
services that will be started by the server and the protocol and server
configuration. More details about the LocalProvider are described in
[the
spec](https://wiki.eng.vmware.com/VAPI/Specs/Protocol#Local_Provider "VAPI/Specs/Protocol")
.

The `[com.vmware.vapi.metadata.cli.source_impl]` section points to the
JSON metadata files that are used to expose the ToyVM and vAPI common
component's services through CLI.

The `[com.vmware.vapi.metadata.metamodel_impl]` section point to the
JSON metadata files that are used to expose the metamodel information of
the ToyVM and vAPI common component's services.

The sections `[loggers]`, `[handlers]`, `[formatters]` and the three
sections following it are to configure python logging. The level is set
to INFO but it could be configured to other levels in the properties
file. More details about configuring python logging are
[here](http://docs.python.org/library/logging.html) .

##Running the Service

With the properties file in place, we can start up the service.

 **Note**: Make sure that the [environment](../setting_up/configuring_the_environment.md#python) is configured.
<div class="codePart">
    vapi-pdk$
PYTHONPATH=toyvm/python/provider:build/toyvm/generated/provider/python:$PYTHONPATH
python -m vmware.vapi.server.vapid ./toyvm/python/conf/toyvm.properties
    <span class="collapseTitle collapsed" data-toggle="collapse" data-target="#runServer">&nbsp;</span>
   <div id="runServer" class="collapseContent collapse">
'2014-10-13 16:00:51,293 WARNING  vmware.vapi.server.vapid Could not set the process title because of No module named setproctitle'<br>
'2014-10-13 16:00:51,644 INFO     vmware.vapi.provider.local Registering service: com.vmware.vapi.std.introspection.provider'<br>
'2014-10-13 16:00:51,645 INFO     vmware.vapi.provider.local Registering service: com.vmware.vapi.std.introspection.service'<br>
'2014-10-13 16:00:51,645 INFO     vmware.vapi.provider.local Registering service: com.vmware.vapi.std.introspection.operation'<br>
'2014-10-13 16:00:51,683 INFO     vmware.vapi.provider.local Registering service: sample.first.toy_VM'<br>
'2014-10-13 16:00:51,931 INFO     vmware.vapi.provider.local Registering service: com.vmware.vapi.metadata.metamodel.enumeration'<br>
'2014-10-13 16:00:51,931 INFO     vmware.vapi.provider.local Registering service: com.vmware.vapi.metadata.metamodel.service.operation'<br>
'2014-10-13 16:00:51,931 INFO     vmware.vapi.provider.local Registering service: com.vmware.vapi.metadata.metamodel.package'<br>
'2014-10-13 16:00:51,931 INFO     vmware.vapi.provider.local Registering service: com.vmware.vapi.metadata.metamodel.component'<br>
'2014-10-13 16:00:51,932 INFO     vmware.vapi.provider.local Registering service: com.vmware.vapi.metadata.metamodel.service'<br>
'2014-10-13 16:00:51,932 INFO     vmware.vapi.provider.local Registering service: com.vmware.vapi.metadata.metamodel.source'<br>
'2014-10-13 16:00:51,932 INFO     vmware.vapi.provider.local Registering service: com.vmware.vapi.metadata.metamodel.structure'<br>
'2014-10-13 16:00:51,932 INFO     vmware.vapi.provider.local Registering service: com.vmware.vapi.metadata.metamodel.resource'<br>
'2014-10-13 16:00:51,932 INFO     vmware.vapi.provider.local Registering service: com.vmware.vapi.metadata.metamodel.resource.model'<br>
'2014-10-13 16:00:51,936 INFO     vmware.vapi.provider.local Registering service: com.vmware.vapi.metadata.cli.command'<br>
'2014-10-13 16:00:51,937 INFO     vmware.vapi.provider.local Registering service: com.vmware.vapi.metadata.cli.namespace'<br>
'2014-10-13 16:00:52,012 INFO     vmware.vapi.provider.local Registering service: com.vmware.vapi.metadata.cli.source'<br>
'2014-10-13 16:00:52,017 INFO     vmware.vapi.server.vapid url: http://localhost:8088/api'<br>
'2014-10-13 16:00:52,045 INFO     vmware.vapi.server.vapid transport protocol: http'<br>
'2014-10-13 16:00:52,045 INFO     vmware.vapi.server.vapid msg_protocol json'<br>
'2014-10-13 16:00:52,103 INFO     vmware.vapi.server.vapid rpc server: twisted'<br>
'2014-10-13 16:00:57,280 INFO     vmware.vapi.server.twisted_server Listening on: ('localhost', 8088) None'<br>
'2014-10-13 16:00:57,281 INFO     vmware.vapi.server.twisted_server twisted internet reactor started...'<br>
    </div>
</div>
The log shows the service being successfully registered and the server
listening for connections on the specified endpoint using the specified
protocol.<br/>
**Note:** *toyvm.properties* can be used to start the default twisted server. The server can also be started as a wsgi application using the instructions and properties file as specified in the [first section](#using-script).
