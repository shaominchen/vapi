{% tocTag %} {% endtocTag %}

# Overview

Once the new service is up and running, you can use DotNet to connect to the server and invoke its APIs. The tutorial provides instructions to stand up the service by running individual commands, or by using shell script. The underlying steps in both the methods are same as the sub-steps while executing individual commands.

# Using individual commands

## Generating Stubs

The PDK includes a code generator `csharp-generator.bat` that parses the interface definition and generates language binding code.

<div class="codePart">
    vapi-pdk> dotnet-toolkit\bin\csharp-generator.bat --profile client --library idl-toolkit\vmidl\vapi_stdlib.vmidl --output build/toyvm/generated/client toyvm/vmodl
    <span class="collapseTitle collapsed" data-toggle="collapse" data-target="#stubOut">&nbsp;</span>
    <div id="stubOut" class="collapseContent collapse">
INFO: Generating csharp files ...<br>
WARN: U:\workspace\dotnet-toolkit\vapi-pdk-Q4-2014\vapi-pdk\toyvm\vmodl\sample\first\ToyVM.vmodl:171.10: identifier (get) is a reserved word in csharp<br>
INFO: Processing target language: csharp<br>
INFO:  > Found 1 csharp language 'products' templates ...<br>
INFO:  > Found 1 csharp language 'packages' templates ...<br>
INFO:  > Found 4 csharp language 'services' templates ...<br>
INFO:  > Found 1 csharp language 'structures' templates ...<br>
INFO:  > Found 1 csharp language 'enumerations' templates ...<br>
INFO: Processing product scope templates ...<br>
INFO:  > Generating file: build\toyvm\generated\client\csharp\sample\first\util\StructTypeUtil.cs ...<br>
INFO: Processing package scope templates for 'idl.model.IdlPackage(name:sample.first)' ...<br>
INFO:  > Generating file: build\toyvm\generated\client\csharp\sample\first\StructDefinitions.cs ...<br>
INFO: Processing service scope templates for 'ToyVM' ...<br>
INFO:  > Generating file: build\toyvm\generated\client\csharp\sample\first\ToyVM.cs ...<br>
INFO:  > Generating file: build\toyvm\generated\client\csharp\sample\first\ToyVMDefinitions.cs ...<br>
INFO:  > Generating file: build\toyvm\generated\client\csharp\sample\first\ToyVMStub.cs ...<br>
INFO:  > Generating file: build\toyvm\generated\client\csharp\sample\first\ToyVMTypes.cs ...<br>
</div>
</div>
The generator creates a sub-directory named `csharp`. It then creates a C\# package and structure based on the services defined in the IDL.

## Implementing the Client

Make sure that the server is running at <http://localhost:8088/api>. This tutorial's toyvm server exposes that endpoint and this client tries to connect to that. Otherwise update the client code with your server's endpoint location.

The client code uses the generated stub to communicate with the server and invoke methods.

The test code included with the tutorial shows an example of connecting to the service and performing operations:
<div class="codePart">
    vapi-pdk> notepad toyvm\\dotnet\\client\\ToyVmApiClient.cs
    <span class="collapseTitle collapsed" data-toggle="collapse" data-target="#clientFile">&nbsp;</span>
    <div id="clientFile" class="collapseContent collapse">
```c#
namespace sample.first.client
{
    using sample.first;
    using System;
    using vmware.vapi.bindings;
    using vmware.vapi.protocol;
    using vmware.vapi.std.errors;

    public class ToyVmApiClient
    {
        public static void Main(string[] args)
        {
            // Get connection to vAPI endpoint
            var protocolFactory = new ProtocolConnectionFactory();
            var connection = protocolFactory.GetInsecureConnection(
                Protocol.Http, "http://127.0.0.1:8088/api");

            // Create ToyVM service
            var stubFactory = new StubFactory(
                connection.GetApiProvider());
            var toyVmSvc = stubFactory.CreateStub<ToyVM>();

            // Create VMs
            var createSpec = new ToyVMTypes.CreateSpec();
            createSpec.SetDisplayName("new toyvm 1");
            var vm1 = toyVmSvc.Create(createSpec);
            createSpec.SetDisplayName("new toyvm 2");
            var vm2 = toyVmSvc.Create(createSpec);
            Console.WriteLine("VMs created: [{0}]",
                string.Join(", ", toyVmSvc.List()));

            // Get vm1 info
            Console.WriteLine("Initial config of {0}: {1}", vm1,
                toyVmSvc.Get(vm1));

            // Update vm1 config
            var backing = new ToyVMTypes.DiskBacking();
            backing.SetType(ToyVMTypes.DiskBacking.Type.FILE);
            backing.SetFilePath("xyz");
            var updateSpec = new ToyVMTypes.UpdateSpec();
            updateSpec.SetNumCpus(3);
            updateSpec.SetDiskBacking(backing);
            toyVmSvc.Update(vm1, updateSpec);
            Console.WriteLine("Updated config of {0}: {1}", vm1,
                toyVmSvc.Get(vm1));

            // Get vm2 info
            Console.WriteLine("Initial config of {0}: {1}", vm2,
                toyVmSvc.Get(vm2));

            // Update vm2 config
            backing.SetType(ToyVMTypes.DiskBacking.Type.DEVICE);
            backing.SetDeviceName("device 1");
            backing.SetClientDevice(false);
            backing.SetFilePath(null);
            updateSpec.SetMemSize(500);
            toyVmSvc.Update(vm2, updateSpec);
            Console.WriteLine("Updated config of {0}: {1}", vm2,
                toyVmSvc.Get(vm2));

            // Power on vm2
            toyVmSvc.PowerOn(vm2);
            Console.WriteLine("Updated config of {0}: {1}", vm2,
                toyVmSvc.Get(vm2));

            // Try to power on a VM that is already powered on
            try
            {
                toyVmSvc.PowerOn(vm2);
                Console.WriteLine("Succeeded but it should have failed!");
            }
            catch (AggregateException ae)
            {
                var exception = ae.InnerException;
                if (exception is AlreadyInDesiredState)
                {
                    Console.WriteLine("'{0}' is already powered on.",
                        toyVmSvc.Get(vm2).GetDisplayName());
                }
                else if (exception is UnableToAllocateResource)
                {
                    Console.WriteLine("Reducing memory requirements of " +
                        "'{0}' due to insufficient resources.",
                        toyVmSvc.Get(vm2).GetDisplayName());
                    updateSpec = new ToyVMTypes.UpdateSpec();
                    updateSpec.SetMemSize(250);
                    try
                    {
                        toyVmSvc.Update(vm2, updateSpec);
                        toyVmSvc.PowerOn(vm2);
                    }
                    catch (Error error)
                    {
                        foreach (var message in error.GetMessages())
                            Console.WriteLine(message.GetDefaultMessage());
                    }
                }
                else if (exception is Error)
                {
                    // Handles other vAPI standard errors.
                    var error = exception as Error;
                    foreach (var message in error.GetMessages())
                        Console.WriteLine(message.GetDefaultMessage());
                }
                else
                {
                    throw exception;
                }
            }

            toyVmSvc.Delete(vm1);
            toyVmSvc.Delete(vm2);
            Console.WriteLine("Deleted VMs");

            connection.Disconnect();
        }
    }
}
```
</div>
</div>
&nbsp;

## Compiling the Client

You need the .NET Framework 4.5 C\# compiler in order to compile the generated stubs.

<div class="codePart">
    vapi-pdk> C:\Windows\Microsoft.NET\Framework64\v4.0.30319\csc.exe /target:library /reference:dotnet-toolkit\runtime\vapi-runtime-2.6.0.dll /out:build\toyvm\ToyVm.dll /recurse:build\toyvm\generated\\\*.*
    <span class="collapseTitle collapsed" data-toggle="collapse" data-target="#clientCompile">&nbsp;</span>
    <div id="clientCompile" class="collapseContent collapse">
Microsoft (R) Visual C# Compiler version 4.0.30319.17929<br>
for Microsoft (R) .NET Framework 4.5<br>
Copyright (C) Microsoft Corporation. All rights reserved.
</div>
</div>
This should generate `build\toyvm\ToyVm.dll`. We can now compile the client application using the dll file of the stubs and generate an executable.

<div class="codePart">
    vapi-pdk> C:\Windows\Microsoft.NET\Framework64\v4.0.30319\csc.exe /target:exe /reference:build\toyvm\ToyVm.dll,dotnet-toolkit\runtime\vapi-runtime-2.6.0.dll /out:build\toyvm\ToyVmApiClient.exe toyvm\dotnet\client\ToyVmApiClient.cs
    <span class="collapseTitle collapsed" data-toggle="collapse" data-target="#clientCompileExe">&nbsp;</span>
    <div id="clientCompileExe" class="collapseContent collapse">
Microsoft (R) Visual C# Compiler version 4.0.30319.17929<br>
for Microsoft (R) .NET Framework 4.5<br>
Copyright (C) Microsoft Corporation. All rights reserved.
</div>
</div>
This should generate `build\toyvm\ToyVmApiClient.exe`.

## Running the Client

To run the client code, first copy the vAPI .NET runtime DLL to `build\toyvm` directory.

<div class="codePart">
    vapi-pdk> copy dotnet-toolkit\runtime\vapi-runtime-2.6.0.dll build\toyvm\
</div>
Then, the client code can be run directly:

<div class="codePart">
    vapi-pdk> build\toyvm\ToyVmApiClient.exe
    <span class="collapseTitle collapsed" data-toggle="collapse" data-target="#clientRun">&nbsp;</span>
    <div id="clientRun" class="collapseContent collapse">
VMs created: [vm-1, vm-2]<br>
Initial config of vm-1: sample.first.toy_VM.info {'display_name': new toyvm 1, 'num_cpus': 4, 'mem_size': 250, 'disk_backing': sample.first.toy_VM.disk_backing {<br>
    'type': NONE, 'file_path': , 'device_name': , 'client_device':}, 'power_state': POWERED_OFF}<br>
Updated config of vm-1: sample.first.toy_VM.info {'display_name': new toyvm 1, 'num_cpus': 3, 'mem_size': 250, 'disk_backing': sample.first.toy_VM.disk_backing {<br>
    'type': FILE, 'file_path': xyz, 'device_name': , 'client_device':}, 'power_state': POWERED_OFF}<br>
Initial config of vm-2: sample.first.toy_VM.info {'display_name': new toyvm 2, 'num_cpus': 4, 'mem_size': 250, 'disk_backing': sample.first.toy_VM.disk_backing {<br>
    'type': NONE, 'file_path': , 'device_name': , 'client_device':}, 'power_state': POWERED_OFF}<br>
Updated config of vm-2: sample.first.toy_VM.info {'display_name': new toyvm 2, 'num_cpus': 3, 'mem_size': 500, 'disk_backing': sample.first.toy_VM.disk_backing {<br>
    'type': DEVICE, 'file_path': , 'device_name': device 1, 'client_device': False}, 'power_state': POWERED_OFF}<br>
Updated config of vm-2: sample.first.toy_VM.info {'display_name': new toyvm 2, 'num_cpus': 3, 'mem_size': 500, 'disk_backing': sample.first.toy_VM.disk_backing {<br>
    'type': DEVICE, 'file_path': , 'device_name': device 1, 'client_device': False}, 'power_state': POWERED_ON}<br>
'new toyvm 2' is already powered on.<br>
Deleted VMs<br>
</div>
</div>
&nbsp;

# Using shell script

There is a script for that does all the steps listed above. To execute it run:

<div class="codePart">
    vapi-pdk> toyvm\\dotnet\\build.bat
    <span class="collapseTitle collapsed" data-toggle="collapse" data-target="#scriptRun">&nbsp;</span>
    <div id="scriptRun" class="collapseContent collapse">
Source VMODL         :U:\workspace\dotnet-toolkit\vapi-pdk-Q4-2014\vapi-pdk\toyvm\vmodl<br>
.NET Toolkit         :U:\workspace\dotnet-toolkit\vapi-pdk-Q4-2014\vapi-pdk\dotnet-toolkit<br>
INFO: Generating csharp files ...<br>
<br>
WARN: U:\workspace\dotnet-toolkit\vapi-pdk-Q4-2014\vapi-pdk\toyvm\vmodl\sample\first\ToyVM.vmodl:171.10: identifier (get) is a reserved word in csharp<br>
INFO: Processing target language: csharp<br>
INFO:  > Found 1 csharp language 'products' templates ...<br>
INFO:  > Found 1 csharp language 'packages' templates ...<br>
INFO:  > Found 4 csharp language 'services' templates ...<br>
INFO:  > Found 1 csharp language 'structures' templates ...<br>
INFO:  > Found 1 csharp language 'enumerations' templates ...<br>
INFO: Processing product scope templates ...<br>
INFO:  > Generating file: U:\workspace\dotnet-toolkit\vapi-pdk-Q4-2014\vapi-pdk\toyvm\dotnet\client\toyvm\csharp\sample\first\util\StructTypeUtil.cs ...<br>
INFO: Processing package scope templates for 'idl.model.IdlPackage(name:sample.first)' ...<br>
INFO:  > Generating file: U:\workspace\dotnet-toolkit\vapi-pdk-Q4-2014\vapi-pdk\toyvm\dotnet\client\toyvm\csharp\sample\first\StructDefinitions.cs ...<br>
INFO: Processing service scope templates for 'ToyVM' ...<br>
INFO:  > Generating file: U:\workspace\dotnet-toolkit\vapi-pdk-Q4-2014\vapi-pdk\toyvm\dotnet\client\toyvm\csharp\sample\first\ToyVM.cs ...<br>
INFO:  > Generating file: U:\workspace\dotnet-toolkit\vapi-pdk-Q4-2014\vapi-pdk\toyvm\dotnet\client\toyvm\csharp\sample\first\ToyVMDefinitions.cs ...<br>
INFO:  > Generating file: U:\workspace\dotnet-toolkit\vapi-pdk-Q4-2014\vapi-pdk\toyvm\dotnet\client\toyvm\csharp\sample\first\ToyVMStub.cs ...<br>
INFO:  > Generating file: U:\workspace\dotnet-toolkit\vapi-pdk-Q4-2014\vapi-pdk\toyvm\dotnet\client\toyvm\csharp\sample\first\ToyVMTypes.cs ...<br>
<br>
ToyVM .NET bindings  :U:\workspace\dotnet-toolkit\vapi-pdk-Q4-2014\vapi-pdk\toyvm\dotnet\\client\ToyVm.dll<br>
ToyVM .NET client    :U:\workspace\dotnet-toolkit\vapi-pdk-Q4-2014\vapi-pdk\toyvm\dotnet\\client\ToyVmApiClient.exe<br>
</div>
</div>
After this build step, the client can be run directly: `toyvm\dotnet\client\ToyVmApiClient.exe`.
