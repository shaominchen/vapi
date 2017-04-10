{% tocTag %} {% endtocTag %}

# Overview
API services are exposed as low level commandlets in PowerCLI. PowerCLI provides cmdlets to work with all API services as of PowerCLI 6.0, these cmdlets are included in the VMware.VimAutomation.Cis.Core module.

The instructions in this page are only for illustration and they do not work directly against a ToyVM server. The server needs to use HTTPS and have ability to create sessions for PowerCLI plugin to work.

# Downloading and installing PowerCLI

-   Go to the [vim4ps powershell-box](https://buildweb.eng.vmware.com/ob/?product=vim4ps&branch=powershell-box&buildtype=release) download page on Buildweb.
-   Click on the latest build number
-   Select 'Deliverables'
-   Click on the publish/VMware-PowerCLI-X.x.x-xxxxx.exe file to download the install file.
-   Once downloaded ensure you meet the minimun requirements on your windows system ([.Net 4.5 Recommended from here](http://www.microsoft.com/en-us/download/details.aspx?id=30653))
-   Launch Windows PowerShell and change the execution policy to allow it to run scripts (Set-ExecutionPolicy RemoteSigned)
-   Double click the PowerCLI install .exe file and continue through the install wizard until PowerCLI has been installed.

# PowerCLI usage of API Services

Connection and initial discovery
--------------------------------

-   You should now have a PowerCLI icon on the desktop of your machine, double click it to open a new shell.
-   Ensure the PowerCLI Install was successfull and the modules have been loaded into the session:

<div class="codePart">
    PowerCLI C:\> get-module *core*
```
ModuleType Version    Name                                ExportedCommands
---------- -------    ----                                ----------------
Binary     6.0.0.0    VMware.VimAutomation.Cis.Core       {Connect-CisServer...
Manifest   6.0.0.0    VMware.VimAutomation.Core           {Add-PassthroughDe...
```
</div>
-   List the commands for working with API Servies

<div class="codePart">
    PowerCLI C:\> get-command -Module VMware.VimAutomation.Cis.Core
```
CommandType Name                 ModuleName
----------- ----                 ----------
Cmdlet      Connect-CisServer    VMware.VimAutomation.Cis.Core
Cmdlet      Disconnect-CisServer VMware.VimAutomation.Cis.Core
Cmdlet      Get-CisService       VMware.VimAutomation.Cis.Core
```
</div>
-   Firstly we must connect to the API Endpoint by using the Connect-CisServer cmdlet as below:

<div class="codePart">
    PowerCLI C:\> Connect-CisServer 10.20.125.0 -Port 8092 -user ANYTHING -Password
```
 ...
 Name User Port

---- ---- ----

10.20.125.0 admin 8092
```
</div>
-   List the services available for use with this vAPI Endpoint, using the Select Name will give us just the name property

<div class="codePart">
    PowerCLI C:\> Get-CisService | Select Name
```
Name
----
com.vmware.vapi.metadata.metamodel.resource.model
com.vmware.vapi.metadata.metamodel.source
com.vmware.vapi.std.introspection.provider
com.vmware.vapi.metadata.metamodel.structure
com.vmware.vapi.rest.navigation.resource
com.vmware.vapi.rest.navigation.root
com.vmware.vapi.std.introspection.service
com.vmware.vapi.rest.navigation.resources
com.vmware.vapi.metadata.metamodel.resource
com.vmware.vapi.rest.navigation.service
com.vmware.vapi.rest.navigation.options
com.vmware.vapi.metadata.metamodel.enumeration
com.vmware.vapi.metadata.cli.namespace
sample.first.toy_VM
com.vmware.cis.session
com.vmware.vapi.metadata.cli.source
com.vmware.vapi.metadata.metamodel.package
com.vmware.vapi.metadata.metamodel.metadata_identifier
com.vmware.vapi.metadata.metamodel.service.operation
com.vmware.vapi.rest.navigation.component
com.vmware.vapi.metadata.cli.command
com.vmware.vapi.metadata.metamodel.service
com.vmware.vapi.metadata.metamodel.component
com.vmware.vapi.std.introspection.operation
```
</div>
-   Assign the API service we want to work with to a variable to make it easier to use

<div class="codePart">
PowerCLI C:\> $toyvm = Get-CisService sample.first.toy_VM
</div>
-   List the available properties and methods available for us to use as part of the object with the built in PowerShell cmdlet Get-Member

<div class="codePart">
    PowerCLI C:\> $toyvm | Get-Member
```
  TypeName: System.Management.Automation.PSCustomObject

Name        MemberType   Definition
----        ----------   ----------
create      CodeMethod   id create(sample.first.toy_VM.create_spec spec)
delete      CodeMethod   void delete(id vm_id)
get         CodeMethod   sample.first.toy_VM.info get(id vm_id)
list        CodeMethod   HashSet<id> list()
power_off   CodeMethod   void power_off(id vm_id)
power_on    CodeMethod   void power_on(id vm_id)
reset       CodeMethod   void reset(id vm_id)
suspend     CodeMethod   void suspend(id vm_id)
update      CodeMethod   void update(id vm_id, sample.first.toy_VM.update_sp...
Equals      Method       bool Equals(System.Object obj)
GetHashCode Method       int GetHashCode()
GetType     Method       type GetType()
ToString    Method       string ToString()
Constants   NoteProperty System.Management.Automation.PSCustomObject Constants=
Help        NoteProperty System.Management.Automation.PSCustomObject Help=@{...
Name        NoteProperty System.String Name=sample.first.toy_VM
```
</div>
Creating a VM
-------------

We can create an object which represents the create spec by using the Help method and information and storing it in the \#createSpec variable, once completed we can see the properties we need to add to the spec by running the variable

<div class="codePart">
    PowerCLI C:\> $createSpec = $toyvm.Help.create.spec.CreateInstance()<br>
    PowerCLI C:\> $createspec
```
Help         : @{Documentation=The {@name CreateSpec} {@term structure} is the
               specification used for the virtual machine creation.;
               display_name=; num_cpus=; mem_size=; disk_backing=}
display_name :
num_cpus     : -9223372036854775808
mem_size     : -9223372036854775808
disk_backing : @{Help=; type=<set this to an enumeration member>; file_path=;
               device_name=; client_device=False}
```
</div>
-   Next we need to assign entries to the properties of our client side object of the correct type (these can be viewed with Get-Member) and then send the object with the create method on the $toyvm Object we created earlier. You will notice the Disk\_backing needs to be created as a separate part of the object as it is more complex.

<div class="codePart">
    PowerCLI C:\> $createSpec = $toyvm.Help.create.spec.CreateInstance()
```
$createspec.display_name = "ToyVM2"
$createspec.num_cpus = [System.int64] 1
$createspec.mem_size = [System.Int64]10248
$diskspec = $createSpec.Disk_backing
   $diskspec.client_device = $null
   $diskspec.device_name = $null
   $diskspec.file_path = $null
   $diskspec.type = "NONE"
$createSpec.Disk_backing = $diskspec
$toyvm.create($createSpec)

Value
-----
skvm5pxwl9o4-vm-2
```
</div>
Making these usable via PowerShell functions
--------------------------------------------

##List the Toy VMs
```
Function Get-ToyVM {
   $toyvm = Get-CisService sample.first.toy_VM
   $AllVMs = $toyvm.list()
   $AllVMs | Foreach {
       $CurrentVM = $toyvm.get($_.value)
       $Obj= New-Object PSObject -Property @{
           Name=$CurrentVM.display_name;
           NumCPU=$CurrentVM.num_cpus;
           Mem=$CurrentVM.mem_size;
           PowerState=$CurrentVM.power_state;
           Disk=$CurrentVM.disk_backing
       }
       $Obj
   }
}
```

##Create a New ToyVM
```
Function New-ToyVM {
   Param (
       $Name,
       $CPU,
       $Mem
   )
   $toyvm = Get-CisService sample.first.toy_VM
   $createSpec = $toyvm.Help.create.spec.CreateInstance()
   $createspec.display_name = $Name
   $createspec.num_cpus = [System.int64] $CPU
   $createspec.mem_size = [System.Int64] $Mem
   $diskspec = $createSpec.Disk_backing
       $diskspec.client_device = $null
       $diskspec.device_name = $null
       $diskspec.file_path = $null
       $diskspec.type = "NONE"
   $createSpec.Disk_backing = $diskspec
   $NewVM = $toyvm.create($createSpec)
   $CurrentVM = $toyvm.get($NewVM.value)
       $Obj= New-Object PSObject -Property @{
           Name=$CurrentVM.display_name;
           NumCPU=$CurrentVM.num_cpus;
           Mem=$CurrentVM.mem_size;
           PowerState=$CurrentVM.power_state;
           Disk=$CurrentVM.disk_backing
       }
       $Obj
}
```

##Power On a ToyVM
```
Function Start-ToyVM {
   Param (
       $Name
   )
   $toyvm = Get-CisService sample.first.toy_VM
   $AllVMs = $toyvm.list()
   $AllVMs | foreach {
       $VM = $toyvm.get($_.value)
       If ($VM.display_name -eq $Name) {
           $toyvm.power_on($_.Value)
           $CurrentVM = $toyvm.get($_.value)
           $Obj= New-Object PSObject -Property @{
               Name=$CurrentVM.display_name;
               NumCPU=$CurrentVM.num_cpus;
               Mem=$CurrentVM.mem_size;
               PowerState=$CurrentVM.power_state;
               Disk=$CurrentVM.disk_backing
           }
           $Obj
       }
   }
}
```
