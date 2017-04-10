{% tocTag %} {% endtocTag %}

# Overview

[VMODL2](https://wiki.eng.vmware.com/VAPI/Specs/VMODL2) is an API modeling language used for describing vAPI-based services. The tutorial provides a sample VMODL2 service named ToyVM. VMODL2 uses java syntax for defining the APIs.

VMODL2 supports the following constructs for grouping API definitions:
- Components define a collection of packages. Components define a scope for deployment and versioning.
- Packages define a collection of services and data types. All services and data types must be defined in a package scope.
- Organization of different elements in an API will look something like this:

<div class="codePart">
```
  Component (@Component)
      Package (1..N) (keyword package)
         File (1..N)
            Structure (0..N) (keyword class)
            Service (0..N) (keyword interface)
               Structure (0..N) (keyword class)
               Operation (0..N)
```
</div>
&nbsp;

# ToyVM VMODL2
The ToyVM service belongs to the `sample.first` component.

    vapi-pdk$ cat toyvm/vmodl/sample/first/package-info.vmodl

```java
/**
 * The {@name sample.first} {@term package} provides {@term services} to manage
 * a very simplified virtual machine.
 */
@Component
package sample.first;

import vmodl.lang.Component;
```

The ToyVM service is a simple, stateful service with operations that help user create and manage virtual machines.

    vapi-pdk$ cat toyvm/vmodl/sample/first/ToyVM.vmodl

```java
package sample.first;

import vmodl.lang.*;

import com.vmware.vapi.std.errors.AlreadyInDesiredState;
import com.vmware.vapi.std.errors.InvalidArgument;
import com.vmware.vapi.std.errors.NotAllowedInCurrentState;
import com.vmware.vapi.std.errors.NotFound;
import com.vmware.vapi.std.errors.ResourceBusy;
import com.vmware.vapi.std.errors.ResourceInUse;
import com.vmware.vapi.std.errors.ResourceInaccessible;
import com.vmware.vapi.std.errors.UnableToAllocateResource;
import com.vmware.vapi.std.errors.Unsupported;

/**
 * The {@name ToyVM} {@term service} provides {@term operations} to manage a
 * very simplified virtual machine.
 */
@Resource("sample.first.VM")
interface ToyVM {

    /**
     * The {@name PowerState} {@term enumerated type} defines valid power states
     * of the virtual machine. See {@link Info}.
     */
    enum PowerState {
        /** Powered on. */
        POWERED_ON,

        /** Powered off. */
        POWERED_OFF,

        /** Suspended. */
        SUSPENDED,
    }

    /**
     * The {@name DiskBacking} {@term structure} contains virtual disk configuration.
     */
    class DiskBacking {
        /**
         * The {@name Type} {@term enumerated type} defines valid disk backing types.
         */
        enum Type {
            /** No virtual disk attached to the VM. */
            NONE,

            /** Virtual disk is backed by a file. */
            FILE,

            /** Virtual disk is backed by a host or client device. */
            DEVICE,
        }

        /** Disk backing type. */
        @UnionTag Type type;

        /** Path of file backing. */
        @UnionCase(tag="type", value="FILE") String filePath;

        /** Name of device backing. */
        @UnionCase(tag="type", value="DEVICE") String deviceName;

        /** True for a client device backing, false for a host device backing. */
        @UnionCase(tag="type", value="DEVICE") boolean clientDevice;
    }

    /**
     * The {@name Info} {@term structure} contains information about the virtual machine.
     */
    class Info {
        /** Display name. */
        String displayName;

        /** Number of CPUs. */
        long numCpus;

        /** Memory size in megabytes. */
        long memSize;

        /** Disk backing. */
        DiskBacking diskBacking;

        /** Virtual machine power state. */
        PowerState powerState;
    }

    /**
     * The {@name CreateSpec} {@term structure} is the specification used for the
     * virtual machine creation.
     */
    class CreateSpec {
        /** Display name. */
        String displayName;

        /**
         * Number of CPUs.
         *
         * @field.optional If unspecified, defaults to 1.
         */
        Optional<Long> numCpus;

        /**
         * Memory size in megabytes.
         *
         * @field.optional If unspecified, defaults to 32.
         */
        Optional<Long> memSize;

        /**
         * Disk backing.
         *
         * @field.optional If unspecified, defaults to diskless.
         */
        Optional<DiskBacking> diskBacking;
    }

    /**
     * The {@name UpdateSpec} {@term structure} is the specification used for the
     * incremental update of the virtual machine.
     */
    class UpdateSpec {
        /**
         * Display name.
         *
         * @field.optional If unspecified, leaves value unchanged.
         */
        Optional<String> displayName;

        /**
         * Number of CPUs.
         *
         * @field.optional If unspecified, leaves value unchanged.
         */
        Optional<Long> numCpus;

        /**
         * Memory size in megabytes.
         *
         * @field.optional If unspecified, leaves value unchanged.
         */
        Optional<Long> memSize;

        /**
         * Disk backing.
         *
         * @field.optional If unspecified, leaves value unchanged.
         */
        Optional<DiskBacking> diskBacking;
    }

    /**
     * Creates a new virtual machine.
     *
     * @param spec Virtual machine creation specification.
     * @return Identifier of the newly-created virtual machine.
     * @throws InvalidArgument if the number of CPUs is nonpositive or larger than
     *         the maximum supported number of CPUs, or if the memory size is
     *         nonpositive.
     */
    ID create(CreateSpec spec) throws InvalidArgument;

    /**
     * Returns the properties of a virtual machine.
     *
     * @param vmId Identifier of the virtual machine.
     * @return Virtual machine properties.
     * @throws NotFound if the virtual machine associated with {@param.name vmId}
     *         does not exist.
     */
    Info get(ID vmId) throws NotFound;

    /**
     * Updates the properties of a virtual machine.
     *
     * @param vmId Identifier of the virtual machine.
     * @param spec Virtual machine properties.
     * @throws NotFound if the virtual machine associated with {@param.name vmId}
     *         does not exist.
     * @throws InvalidArgument if the number of CPUs is nonpositive or larger than
     *         the maximum supported number of CPUs, or if the memory size is
     *         nonpositive.
     * @throws ResourceBusy if the virtual machine is performing another operation.
     */
    void update(ID vmId, UpdateSpec spec) throws NotFound, InvalidArgument, ResourceBusy;

    /**
     * Deletes a virtual machine.
     *
     * @param vmId Identifier of the virtual machine.
     * @throws NotFound if the virtual machine associated with {@param.name vmId}
     *         does not exist.
     * @throws ResourceInUse if the virtual machine is powered on.
     * @throws ResourceBusy if the virtual machine is performing another operation.
     */
    void delete(ID vmId) throws NotFound, ResourceInUse, ResourceBusy;

    /**
     * Enumerates the set of registered virtual machines.
     *
     * @return Set of virtual machine IDs.
     */
    Set<ID> list();

    /**
     * Powers on a powered-off or suspended virtual machine.
     *
     * @param vmId Identifier of the virtual machine.
     * @throws NotFound if the virtual machine associated with {@param.name vmId}
     *         does not exist.
     * @throws AlreadyInDesiredState if the virtual machine is already powered on
     * @throws Unsupported if the virtual machine does not support being
     *         powered on. For example, marked as a template, serving as a
     *         fault-tolerance secondary virtual machine.
     * @throws UnableToAllocateResource if resources cannot be allocated
     *         for the virtual machine. For example, physical resource allocation
     *         policy cannot be satisfied, insufficient licenses are available
     *         to run the virtual machine.
     * @throws ResourceInaccessible if resources required by the virtual
     *         machine are not accessible. For example, virtual machine configuration
     *         files or virtual disks are on inaccessible storage, no hosts are
     *         available to run the virtual machine.
     * @throws ResourceInUse if resources required by the virtual machine
     *         are in use. For example, virtual machine configuration files or virtual
     *         disks are locked, host containing the virtual machine is an
     *         HA failover host.
     * @throws ResourceBusy if the virtual machine is performing another operation.
     */
    void powerOn(ID vmId) throws NotFound, AlreadyInDesiredState, Unsupported,
                               UnableToAllocateResource, ResourceInaccessible,
                               ResourceInUse, ResourceBusy;

    /**
     * Powers off a powered-on or suspended virtual machine.
     *
     * @param vmId Identifier of the virtual machine.
     * @throws NotFound if the virtual machine associated with {@param.name vmId}
     *         does not exist.
     * @throws AlreadyInDesiredState if the virtual machine is already powered off.
     * @throws NotAllowedInCurrentState if the virtual machine's execution is
     *         blocked waiting for input.
     * @throws ResourceBusy if the virtual machine is performing another operation.
     */
    void powerOff(ID vmId) throws NotFound, AlreadyInDesiredState,
                                NotAllowedInCurrentState, ResourceBusy;

    /**
     * Suspends a powered-on virtual machine.
     *
     * @param vmId Identifier of the virtual machine.
     * @throws NotFound if the virtual machine associated with {@param.name vmId}
     *         does not exist.
     * @throws AlreadyInDesiredState if the virtual machine is already suspended.
     * @throws NotAllowedInCurrentState if the virtual machine is powered off,
     *         or if the virtual machine's execution is blocked waiting for
     *         input.
     * @throws ResourceBusy if the virtual machine is performing another operation.
     */
    void suspend(ID vmId) throws NotFound, AlreadyInDesiredState,
                               NotAllowedInCurrentState, ResourceBusy;

    /**
     * Resets a powered-on virtual machine.
     *
     * @param vmId Identifier of the virtual machine.
     * @throws NotFound if the virtual machine associated with {@param.name vmId}
     *         does not exist.
     * @throws NotAllowedInCurrentState if the virtual machine is powered off or
     *         suspended, or if the virtual machine's execution is blocked
     *         waiting for input.
     * @throws ResourceBusy if the virtual machine is performing another operation.
     */
    void reset(ID vmId) throws NotFound, NotAllowedInCurrentState, ResourceBusy;
}

 ```

# Using eclipse for API authoring

Eclipse can be configured to do syntax highlighting, error checking, provide tooltips, and do autocompletion for '`.vmodl`' files the same way it does for '`.java`' files:

-   Go to: Window \> Preferences
-   From the left panel, go to: General \> Content Types
-   On the top right panel: Expand 'Text'
-   Select 'Java Source File'
-   Click 'Add' button on the right side of lower panel
-   Type in `'*.vmodl'` and press 'OK'
-   Click 'OK' to apply the settings
-   Close and reopen the file in eclipse.

![](https://wiki.eng.vmware.com/wiki/images/b/bd/Eclipse-vmodl-syntax.png)

In order for the compilation to be successful, dependent jars need to be added to the project:

-   Right click on project and select 'Properties'
-   Select 'Java Build Path' from left panel
-   Select 'Libraries'
-   Click 'Add External JARs...' button on the right
-   Traverse to `$VAPI-PDK/idl-toolkit/lib`
-   Select `vmodl-parser-2.1.0.2347904.jar` and `vmodl-taglets-2.1.0.2347904.jar`
-   Select Open
-   Repeat the above three steps and add `$VAPI-PDK/java-toolkit/runtime/vapi-runtime-2.1.0.jar`
-   Click 'OK' to apply the settings
-   When the project is built, it can be seen that the errors in the vmodl file are resolved.

![](https://wiki.eng.vmware.com/wiki/images/b/b1/Eclipse-vmodl-jars.png)

# Next Steps

-   To learn about the best practices of designing new APIs: [Best Practices Guide](https://wiki.eng.vmware.com/VAPI/Modeling/BestPractices) authored by API stewardship committee.
-   To learn about all the constructs available in VMODL2: [VMODL2 Language Guide](https://wiki.eng.vmware.com/VAPI/Specs/VMODL2).
-   To learn from the other public APIs developed by other features teams: [API Change Request Tool](http://acr.eng.vmware.com/?sort>=&order=&max=100&offset=0&\_deletionStatus=&phase.id=4&state.id=3&description=&owner=&assignee=&designatedReviewer=&lastUpdated=&deletionStatus=Not+Deleted&tag.id=0&division.id=0&programManager=&\_action\_list=).
-   To learn about an API that has well-written use cases, follows best practices and provides good samples: [ACR 36 : Reverse Proxy API](http://acr.eng.vmware.com/acr/show/36). This example is provided by API stewardship committee.

