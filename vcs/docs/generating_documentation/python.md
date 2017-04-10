{% tocTag %} {% endtocTag %}

# Overview
Client stubs generated for a language contain language-specific documentation in a format that is considered a standard for that programming language. Documentation can be generated for these client SDKs using Sphinx.

#Using Sphinx

## Generating stubs

This step can be ignored if the client stubs were already generated while implementing the client. The PDK includes a code generator `python-generator` that parses the interface definition and generates stubs. The documentation is generated based on the generated stubs.

<div class="codePart">
    vapi-pdk$ python-toolkit/bin/python-generator --profile client --library idl-toolkit/vmidl/vapi_stdlib.vmidl --output build/toyvm/generated/client toyvm/vmodl
    <span class="collapseTitle collapsed" data-toggle="collapse" data-target="#stubGen">&nbsp;</span>
    <div id="stubGen" class="collapseContent collapse">
    Generating vAPI Python client bindings<br>
    INFO: Generating python files ...<br>
    INFO: Processing target language: python<br>
    INFO:  > Found 3 python language 'packages' templates ...<br>
    INFO: Processing package scope templates for 'idl.model.IdlPackage(name:sample.first)' ...<br>
    INFO:  > Generating file: build/toyvm/generated/client/python/sample/first_client.py ...<br>
    </div>
</div>

## Generating documentation

The python.sh script can be used to generate documentation. This script uses Sphinx to generate HTML documentation pages and clears all the temporary files generated. Options needed by the script:

-   `-i` : Directory of input source tree.
-   `-o` : Output directory. The HTML files are put in a 'python' sub-directory in the output directory.

Output of python sphing doc generation script.

<div class="codePart">
    vapi-pdk$ tools/docs/python.sh -i build/toyvm/generated/client/python -o build/toyvm/generated/documentation/
    <span class="collapseTitle collapsed" data-toggle="collapse" data-target="#docGen">&nbsp;</span>
    <div id="docGen" class="collapseContent collapse">
    Creating file /dbc/pa-dbc1111/avaithi/vapi-code/bora/build/vapi/obj/vapi/build/toyvm/generated/documentation/temp/toyvm_client.rst.<br>
    Creating file /dbc/pa-dbc1111/avaithi/vapi-code/bora/build/vapi/obj/vapi/build/toyvm/generated/documentation/temp/sample.rst.<br>
    Creating file /dbc/pa-dbc1111/avaithi/vapi-code/bora/build/vapi/obj/vapi/build/toyvm/generated/documentation/temp/conf.py.<br>
    Creating file /dbc/pa-dbc1111/avaithi/vapi-code/bora/build/vapi/obj/vapi/build/toyvm/generated/documentation/temp/index.rst.<br>
    Creating file /dbc/pa-dbc1111/avaithi/vapi-code/bora/build/vapi/obj/vapi/build/toyvm/generated/documentation/temp/Makefile.<br>
    Creating file /dbc/pa-dbc1111/avaithi/vapi-code/bora/build/vapi/obj/vapi/build/toyvm/generated/documentation/temp/make.bat.<br>
    /build/toolchain/noarch/sphinx-1.1.3/bin/sphinx-build -b html -d _build/doctrees  -c sphinx . _build/html<br>
    Making output directory...<br>
    Running Sphinx v1.1.3<br>
    loading pickled environment... not yet created<br>
    building [html]: targets for 4 source files that are out of date<br>
    updating environment: 4 added, 0 changed, 0 removed<br>
    reading sources... [100%] toyvm_client<br>
    looking for now-outdated files... none found<br>
    pickling environment... done<br>
    checking consistency... /dbc/pa-dbc1111/avaithi/vapi-code/bora/build/vapi/obj/vapi/build/toyvm/generated/documentation/temp/sphinx/rst/enumeration.rst:: WARNING: document isn't included in any toctree<br>
    done<br>
    preparing documents... done<br>
    writing output... [100%] toyvm_client<br>
    writing additional files... (2 module code pages) _modules/index<br>
     genindex py-modindex search<br>
    copying static files... WARNING: html_static_path entry '/dbc/pa-dbc1111/avaithi/vapi-code/bora/build/vapi/obj/vapi/build/toyvm/generated/documentation/temp/sphinx/_static' does not exist<br>
    done<br>
    dumping search index... done<br>
    dumping object inventory... done<br>
    build succeeded, 2 warnings.<br>
    <br>
    Build finished. The HTML pages are in _build/html.<br>
    Moving HTML pages to 'python' subdirectory in the destination directory provided.<br>
    </div>
</div>

The generated files after running the script.

<div class="codePart">
    vapi-pdk$ ls build/toyvm/generated/documentation/python/
    <span class="collapseTitle collapsed" data-toggle="collapse" data-target="#files">&nbsp;</span>
    <div id="files" class="collapseContent collapse">
    genindex.html  _modules     py-modindex.html  search.html     _sources _static
    index.html     objects.inv  sample.html       searchindex.js  sphinx   toyvm_client.html
    </div>
</div>

The HTML files can be found in a subdirectory named 'python' in the output directory. `index.html` is the start page.
