{% tocTag %} {% endtocTag %}

# Overview
The PDK provides a generator that can be used to generate documentation for REST style APIs exposed by a vAPI service using its VMODL2 definition. This documentation is in the form of HTML files.

#Using restdoc tool
##Unzip the documentation generator
The `vapi-restdoc-2.6.0.zip` contains a tool named `restdoc` that can be used to produce a HTML documentation for the REST APIs.
<div class="codePart">
    vapi-pdk$ unzip java-toolkit/runtime/vapi-restdoc-2.6.0.zip -d restdoc-toolkit
</div>

##Generate the documentation
<div class="codePart">
    vapi-pdk$ restdoc-toolkit/bin/restdoc --library idl-toolkit/vmidl/vapi_stdlib.vmidl toyvm/vmodl
    <span class="collapseTitle collapsed" data-toggle="collapse" data-target="#files">&nbsp;</span>
    <div id="files" class="collapseContent collapse">
    INFO: Generating restdoc files ...<br>

    INFO: Processing target language: restdoc<br>
    INFO:  > Found 3 restdoc language 'products' templates ...<br>
    INFO:  > Found 1 restdoc language 'structures' templates ...<br>
    INFO:  > Found 1 restdoc language 'nestedStructures' templates ...<br>
    INFO:  > Found 13 restdoc language 'static' templates ...<br>
    INFO: Processing product scope templates ...<br>
    INFO:  > Generating file: build/src/restdoc/index.html ...<br>
    INFO:  > Generating file: build/src/restdoc/landing_operations.html ...<br>
    INFO:  > Generating file: build/src/restdoc/landing_types.html ...<br>
    ...
    </div>
</div>

`build/src/restdoc` folder has the generated documentation for ToyVM service.
