{% tocTag %} {% endtocTag %}

# Overview
The PDK provides a generator that can be used to generate documentation for dcli commands exposed by a vAPI service using its VMODL2 definition. This documentation is in the form of HTML files.

#Using clidoc-generator tool
The `clidoc-generator` is a tool provided by the PDK to produce HTML documentation for dcli.

<div class="codePart">
    vapi-pdk$ metadata-toolkit/bin/clidoc-generator --library idl-toolkit/vmidl/vapi_stdlib.vmidl --output build/toyvm/generated/documentation toyvm/vmodl
    <span class="collapseTitle collapsed" data-toggle="collapse" data-target="#files">&nbsp;</span>
    <div id="files" class="collapseContent collapse">
    INFO: Generating clidoc files ...<br>
    INFO: Processing target language: clidoc<br>
    INFO:  > Found 1 clidoc language 'services' templates ...<br>
    INFO: Processing service scope templates for 'ToyVM' ...<br>
    INFO:  > Generating file: build/toyvm/generated/documentation/clidoc/sample/first/ToyVM.html ...<br>
    </div>
</div>

- Stylesheet used by the HTML documentation file needs to be copied into the correct directory.

<div class="codePart">
    vapi-pdk$ cp tools/docs/dcli/style.css build/toyvm/generated/documentation/clidoc/sample/first
</div>

- `build/toyvm/generated/documentation/clidoc/sample/first/ToyVM.html` is the file that contains the generated documentation for ToyVM service.
