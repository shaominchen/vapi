{% tocTag %} {% endtocTag %}

# Overview
Client stubs generated for a language contain language-specific documentation in a format that is considered a standard for that programming language. Documentation can be generated for these client SDKs using Yarddoc.

#Using Yarddoc

## Generating stubs

This step can be ignored if the client stubs were already generated while implementing the client. The PDK includes a code generator `ruby-generator` that parses the interface definition and generates stubs. The documentation is generated based on the generated stubs.

<div class="codePart">
    vapi-pdk$ ruby-toolkit/bin/ruby-generator --profile client --library idl-toolkit/vmidl/vapi_stdlib.vmidl --output build/toyvm/generated/client toyvm/vmodl/
    <span class="collapseTitle collapsed" data-toggle="collapse" data-target="#stubGen">&nbsp;</span>
    <div id="stubGen" class="collapseContent collapse">
INFO: Generating ruby files ...<br>
    <br>
INFO: Processing target language: ruby<br>
INFO:  > Found 2 ruby language 'models' templates ...<br>
INFO:  > Found 2 ruby language 'packages' templates ...<br>
INFO: Processing model scope templates ...<br>
INFO:  > Generating file: build/src/ruby/sample/first/vapi.rb ...<br>
INFO: Processing package scope templates for 'IdlPackage(name:sample.first)' ...<br>
INFO:  > Generating file: build/src/ruby/sample/first.rb ...<br>
    </div>
</div>
    
## Generating documentation
    
[Yarddoc](http://yardoc.org) is used for generating documentation for Ruby.

<div class="codePart">
    vapi-pdk$ yardoc -o build/toyvm/generated/documentation/ruby --title ToyVM build/toyvm/generated/client/ruby
    <span class="collapseTitle collapsed" data-toggle="collapse" data-target="#docGen">&nbsp;</span>
    <div id="docGen" class="collapseContent collapse">
Files:           1<br>
Modules:         2 (    0 undocumented)<br>
Classes:         7 (    3 undocumented)<br>
Constants:       6 (    6 undocumented)<br>
Methods:        92 (   18 undocumented)<br>
 74.77% documented<br>
    </div>
</div>

The HTML files can be found in a directory named 'build/rubydoc'. `index.html` is the start page.

