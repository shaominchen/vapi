{% tocTag %} {% endtocTag %}

# Overview
Client stubs generated for a language contain language-specific documentation in a format that is considered a standard for that programming language. Documentation can be generated for these client SDKs using maven or using the javadoc command.

# Using maven
If maven is used to build, documentation is generated as a side effect as the project installation step. Just documentation can be generated using this command:<br>
***Note***: Before running the maven command, configure your environment using the instructions [here](../setting_up/configuring_the_environment.md#java)
<div class="codePart">
    vapi-pdk$ cd toyvm/java/client  
    client$ mvn javadoc:jar
</div>
-   After execution completes, the jar containing documentation HTML can be found in a sub-folder in the build root: `$BUILD_ROOT/maven/toyvm/toyvm-client/toyvm-client-2.6.0-javadoc.jar`
-   After extracting the contents, the documentation can be navigated by opening up the first page - `index.html`

# Using javadoc command

## Generating stubs

Documentation is generated based on the generated client stubs. This step can be ignored if the client stubs were already generated while implementing the client. If not, the code generator can be used - The PDK includes a code generator `java-generator` that parses the interface definition and generates client stubs. 

<div class="codePart">
    vapi-pdk$ java-toolkit/bin/java-generator --profile client --output build/toyvm/generated/client --library idl-toolkit/vmidl/vapi_stdlib.vmidl toyvm/vmodl
    <span class="collapseTitle collapsed" data-toggle="collapse" data-target="#stubGen">&nbsp;</span>
    <div id="stubGen" class="collapseContent collapse">
    INFO: Generating java files ...<br>
    INFO: Processing target language: java<br>
    INFO:  > Found 1 java language 'models' templates ...<br>
    INFO:  > Found 2 java language 'packages' templates ...<br>
    INFO:  > Found 8 java language 'services' templates ...<br>
    INFO:  > Found 1 java language 'structures' templates ...<br>
    INFO:  > Found 1 java language 'enumerations' templates ...<br>
    INFO: Processing model scope templates ...<br>
    INFO:  > Generating file: build/toyvm/generated/client/java/sample/first/util/StructTypeUtil.java ...<br>
    INFO: Processing package scope templates for 'idl.model.IdlPackage(name:sample.first)' ...<br>
    INFO:  > Generating file: build/toyvm/generated/client/java/sample/first/StructDefinitions.java ...<br>
    INFO:  > Generating file: build/toyvm/generated/client/java/sample/first/package-info.java ...<br>
    INFO: Processing service scope templates for 'ToyVM' ...<br>
    INFO:  > Generating file: build/toyvm/generated/client/java/sample/first/ToyVM.java ...<br>
    INFO:  > Generating file: build/toyvm/generated/client/java/sample/first/ToyVMDefinitions.java ...<br>
    INFO:  > Generating file: build/toyvm/generated/client/java/sample/first/ToyVMStub.java ...<br>
    INFO:  > Generating file: build/toyvm/generated/client/java/sample/first/ToyVMTypes.java ...<br>
    </div>
</div>
&nbsp;

## Generating documentation

Documentation for the client can be generated using the `javadoc` utility. Arguments and options for the command:

-   `-classpath` : The vAPI runtime jar is added to the classpath because the errors are defined in it
-   `-d` : Output directory.
-   `-sourcepath` : Source tree for the input packages.
-   Last couple of arguments are the packages for which documentation needs to be generated.

Output of the javadoc command

<div class="codePart">
    vapi-pdk$ javadoc -classpath toyvm/java/deps/\\\* -d build/toyvm/generated/documentation/java -sourcepath toyvm/java/client/toyvm/src/main/java:build/toyvm/generated/client/java  sample.first sample.first.client
    <span class="collapseTitle collapsed" data-toggle="collapse" data-target="#docGen">&nbsp;</span>
    <div id="docGen" class="collapseContent collapse">
    Loading source files for package sample.first...<br>
    Constructing Javadoc information...<br>
    Standard Doclet version 1.7.0_65<br>
    Building tree for all the packages and classes...<br>
    Generating build/toyvm/generated/documentation/java/sample/first/StructDefinitions.html...<br>
    Generating build/toyvm/generated/documentation/java/sample/first/ToyVM.html...<br>
    ...<br>
    warnings<br>
    ...<br>
    Generating build/toyvm/generated/documentation/java/sample/first/ToyVMDefinitions.html...<br>
    Generating build/toyvm/generated/documentation/java/sample/first/ToyVMStub.html...<br>
    ...<br>
    warnings<br>
    ...<br>
    Generating build/toyvm/generated/documentation/java/sample/first/ToyVMTypes.html...<br>
    Generating build/toyvm/generated/documentation/java/sample/first/ToyVMTypes.CreateSpec.html...<br>
    Generating build/toyvm/generated/documentation/java/sample/first/ToyVMTypes.DiskBacking.html...<br>
    Generating build/toyvm/generated/documentation/java/sample/first/ToyVMTypes.DiskBacking.Type.html...<br>
    Generating build/toyvm/generated/documentation/java/sample/first/ToyVMTypes.DiskBacking.Type.Values.html...<br>
    Generating build/toyvm/generated/documentation/java/sample/first/ToyVMTypes.Info.html...<br>
    Generating build/toyvm/generated/documentation/java/sample/first/ToyVMTypes.PowerState.html...<br>
    Generating build/toyvm/generated/documentation/java/sample/first/ToyVMTypes.PowerState.Values.html...<br>
    Generating build/toyvm/generated/documentation/java/sample/first/ToyVMTypes.UpdateSpec.html...<br>
    Generating build/toyvm/generated/documentation/java/sample/first/package-frame.html...<br>
    Generating build/toyvm/generated/documentation/java/sample/first/package-summary.html...<br>
    Generating build/toyvm/generated/documentation/java/sample/first/package-tree.html...<br>
    Generating build/toyvm/generated/documentation/java/constant-values.html...<br>
    Generating build/toyvm/generated/documentation/java/serialized-form.html...<br>
    Building index for all the packages and classes...<br>
    Generating build/toyvm/generated/documentation/java/overview-tree.html...<br>
    Generating build/toyvm/generated/documentation/java/index-all.html...<br>
    Generating build/toyvm/generated/documentation/java/deprecated-list.html...<br>
    Building index for all classes...<br>
    Generating build/toyvm/generated/documentation/java/allclasses-frame.html...<br>
    Generating build/toyvm/generated/documentation/java/allclasses-noframe.html...<br>
    Generating build/toyvm/generated/documentation/java/index.html...<br>
    Generating build/toyvm/generated/documentation/java/help-doc.html...<br>
    108 warnings
    </div>
</div>
&nbsp;

<div class="codePart">
    vapi-pdk$ ls build/toyvm/generated/documentation/java
    <span class="collapseTitle collapsed" data-toggle="collapse" data-target="#listFiles">&nbsp;</span>
    <div id="listFiles" class="collapseContent collapse">
```
allclasses-frame.html    index-all.html         package-list
allclasses-noframe.html  index.html             resources
constant-values.html     overview-frame.html    sample
deprecated-list.html     overview-summary.html  serialized-form.html
help-doc.html            overview-tree.html     stylesheet.css
```
</div>
</div>
The HTML files can be found in the output directory. `index.html` is the start page.

