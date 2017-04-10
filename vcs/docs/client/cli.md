{% tocTag %} {% endtocTag %}

# Overview

In addition to programmatic access, API services are exposed as command-line interfaces (CLIs). This is possible because the ToyVM provider includes the CLI and metamodel services by default.

The CLI metadata files are stored on the server and exposed to clients through the CLI metadata service, enabling clients to connect to the server and dynamically discover the available set of commands. The CLI metadata service is run in the same process as the ToyVM service.

# Using the standalone executable

- The executables are available in the 'dcli' directory in vapi-pdk for linux and windows operating systems. For linux, use 'dcli/lin64/dcli' or 'dcli/lin32/dcli'. For windows, use 'dcli/win32/dcli.exe'.

- For convenience, you can add the 'dcli' executable to the PATH variable. For example, for 64 bit linux do the following.<br/>

```
    vapi-pdk$ export PATH=$(pwd)/dcli/lin64:$PATH
    vapi-pdk$ chmod +x dcli/lin64/dcli
```

- For windows users, go to 'My Computer -> Properties -> Advanced system settings -> Advanced -> Environment Variables -> Path' and add the path to dcli exe to the 'Path' system variable.

Once you add 'dcli' to the PATH, you can invoke it by connecting to a vAPI server.

    $ dcli +server http://localhost:8088/api

    Welcome to VMware Datacenter CLI(DCLI)

    usage: dcli <namespaces> <command>

    To enter interactive shell mode: dcli +interactive
    To specify an alternate server: dcli +server <server>
    For detailed help please use: dcli --help

    Root namespace

    Available Namespaces:

    com      com namespace
    sample   sample namespace

-   The dcli namespace navigation and command execution is very similar to the `esxcli` command line tool.
-   The server URL can be set in an environment variable named `DCLI_SERVER` instead of passing the `server` option
**Note:** ToyVM service's Cpp implementation returns IDs like 1, 2, 3, etc. instead of vm-1, vm-2, vm-3, etc. shown in the following examples.
So, while running DCLI commands, make sure that valid VM IDs are provided - as returned by the create commands.


    $ export DCLI_SERVER=http://localhost:8088/api

    $ dcli sample
    sample namespace

    Available Namespaces:

    first   The first namespace provides namespaces to manage a very simplified virtual machine


    $ dcli sample first
    The first namespace provides namespaces to manage a very simplified virtual machine

    Available Namespaces:

    sample first toyvm    The ToyVM namespace provides commands to manage a very simplified virtual machine


    $ dcli sample first toyvm
    The ToyVM namespace provides commands to manage a very simplified virtual machine

    Available Commands:

    create     Creates a new virtual machine.
    delete     Deletes a virtual machine.
    get        Returns the properties of a virtual machine.
    list       Enumerates the set of registered virtual machines.
    poweroff   Power off a powered-on or suspended virtual machine.
    poweron    Power on a powered-off or suspended virtual machine.
    reset      Reset a powered-on virtual machine.
    suspend    Suspend a powered-on virtual machine.
    update     Updates the properties of a virtual machine.


    $ dcli sample first toyvm create --help
    usage: sample first toyvm create [-h] --display-name DISPLAY_NAME [--num-cpus NUM_CPUS] [--mem-size MEM_SIZE] --disk-backing-type
                                 {NONE,FILE,DEVICE} [--disk-backing-file-path DISK_BACKING_FILE_PATH]
                                 [--disk-backing-device-name DISK_BACKING_DEVICE_NAME]
                                 [--disk-backing-client-device DISK_BACKING_CLIENT_DEVICE]

    Creates a new virtual machine

    Input Arguments:
      -h, --help            show this help message and exit
      --display-name DISPLAY_NAME
                            required: Display name (string)
      --num-cpus NUM_CPUS   Number of CPUs (int)
      --mem-size MEM_SIZE   Memory size in megabytes (int)
      --disk-backing-type {NONE,FILE,DEVICE}
                            required: Disk backing type (string)
      --disk-backing-file-path DISK_BACKING_FILE_PATH
                            Path of file backing (string)
      --disk-backing-device-name DISK_BACKING_DEVICE_NAME
                            Name of device backing (string)
      --disk-backing-client-device DISK_BACKING_CLIENT_DEVICE
                            True for a client device backing, false for a host device backing (bool)

    $ dcli sample first toyvm create --display-name "new toyvm 1" --disk-backing-type NONE
    vm-1

    $ dcli sample first toyvm list
    vm-1

    $ dcli sample first toyvm get --vm-id vm-1
     DisplayName: new toyvm 1
     NumCpus: 4
     MemSize: 250
     DiskBacking:
         Type: NONE
     PowerState: POWERED_OFF

The formatting of the output in dcli can be changed using the +formatter option. Supported formats are simple, table, xml, json, html and csv.

    $ dcli sample first toyvm get --vm-id vm-1 +formatter=table
    |-----------|-------|-------|-----------|-----------|
    |DisplayName|NumCpus|MemSize|DiskBacking|PowerState |
    |-----------|-------|-------|-----------|-----------|
    |new toyvm 1|4      |250    ||----|     |POWERED_OFF|
    |           |       |       ||Type|     |           |
    |           |       |       ||----|     |           |
    |           |       |       ||NONE|     |           |
    |           |       |       ||----|     |           |
    |-----------|-------|-------|-----------|-----------|

    $ dcli sample first toyvm get --vm-id vm-1 +formatter=xml
    <?xml version="1.0"?>
    <output xmlns="http://www.vmware.com/Products/vapi/1.0">
    <root>
     <structure>
      <field name="DisplayName">
       <string>new toyvm 1</string>
      </field>
      <field name="NumCpus">
       <int>4</int>
      </field>
      <field name="MemSize">
       <int>250</int>
      </field>
      <field name="DiskBacking">
       <structure>
        <field name="Type">
         <string>NONE</string>
        </field>
       </structure>
      </field>
      <field name="PowerState">
       <string>POWERED_OFF</string>
      </field>
     </structure>
    </root>
    </output>

    $ dcli sample first toyvm get --vm-id vm-1 +formatter=json
    {"DisplayName": "new toyvm 1", "NumCpus": 4, "MemSize": 250, "DiskBacking": {"Type": "NONE"}, "PowerState": "POWERED_OFF"}

dcli also has an interactive shell mode it supports tab(`^I`) completion. It can be started using the option `+interactive` or `+i`

    $ dcli +interactive
    dcli> ^I
    com     sample

    dcli> sample first toyvm ^I
    create    delete    get       list      poweroff  poweron   reset     suspend   update

    dcli> sample first toyvm create  --^I
    --disk-backing-type           required: Disk backing type (string)
    --display-name                required: Display name (string)
    --disk-backing-client-device  True for a client device backing, false for a host device backing (bool)
    --disk-backing-device-name    Name of device backing (string)
    --disk-backing-file-path      Path of file backing (string)
    --mem-size                    Memory size in megabytes (int)
    --num-cpus                    Number of CPUs (int)

    dcli> sample first toyvm create --display-name "new toyvm 2" --disk-backing-type ^I
    DEVICE  FILE    NONE

    dcli> sample first toyvm create --display-name "new toyvm 2" --disk-backing-type NONE
    vm-2

    dcli> sample first toyvm list
    vm-1, vm-2

    dcli> exit
