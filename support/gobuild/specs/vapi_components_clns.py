"""
Common location for change numbers of all the vapi_core sub components.
This reduces the number of places change numbers  must be updated when
there is a spec bump.  The upshots:

* Less overhead
* Less risk
* Less chance of targets consuming different clns of the same component
"""

IDL_CLN = 4691503

JAVA_CLN = 4691519
PYTHON_CLN = 4691519
METADATA_CLN = 4691519
CPP_EMITTER_CLN = 4691519

DOTNET_CLN = 4691534
PERL_CLN = 4691534
RUBY_CLN = 4691534
DCLI_CLN = 4691534
