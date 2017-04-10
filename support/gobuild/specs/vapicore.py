# Copyright 2014-2015 VMware, Inc.  All rights reserved. -- VMware Confidential

"""
vAPI component spec.
"""
from specs.branch import THIS_BRANCH
from specs.vapi_components_clns import *

VAPI_CORE_IDL_BRANCH = THIS_BRANCH
VAPI_CORE_IDL_CLN = IDL_CLN
VAPI_CORE_IDL_BUILDTYPES = {
   'obj': 'beta',
   'beta': 'beta',
   'release': 'release',
   'opt': 'release',
}
VAPI_CORE_IDL_FILES = {
    'linux64': [r'publish/.*$'],
    'windows2012r2-vs2013-U3': [r'publish/.*$'],
    'macosx': [r'publish/.*$'],
}

VAPI_CORE_JAVA_BRANCH = THIS_BRANCH
VAPI_CORE_JAVA_CLN = JAVA_CLN
VAPI_CORE_JAVA_BUILDTYPES = {
   'obj': 'beta',
   'beta': 'beta',
   'release': 'release',
   'opt': 'release',
}
VAPI_CORE_JAVA_FILES = {
    'linux64': [r'publish/.*$'],
    'windows2012r2-vs2013-U3': [r'publish/.*$'],
    'macosx': [r'publish/.*$'],
}

VAPI_CORE_PYTHON_BRANCH = THIS_BRANCH
VAPI_CORE_PYTHON_CLN = PYTHON_CLN
VAPI_CORE_PYTHON_BUILDTYPES = {
   'obj': 'beta',
   'beta': 'beta',
   'release': 'release',
   'opt': 'release',
}
VAPI_CORE_PYTHON_FILES = {
    'linux64': [r'publish/.*$'],
    'windows2012r2-vs2013-U3': [r'publish/.*$'],
    'macosx': [r'publish/.*$'],
}

VAPI_CORE_METADATA_BRANCH = THIS_BRANCH
VAPI_CORE_METADATA_CLN = METADATA_CLN
VAPI_CORE_METADATA_BUILDTYPES = {
   'obj': 'beta',
   'beta': 'beta',
   'release': 'release',
   'opt': 'release',
}
VAPI_CORE_METADATA_FILES = {
    'linux64': [r'publish/.*$'],
    'windows2012r2-vs2013-U3': [r'publish/.*$'],
    'macosx': [r'publish/.*$'],
}

VAPI_CORE_DOTNET_BRANCH = THIS_BRANCH
VAPI_CORE_DOTNET_CLN = DOTNET_CLN
VAPI_CORE_DOTNET_BUILDTYPES = {
   'obj': 'beta',
   'beta': 'beta',
   'release': 'release',
   'opt': 'release',
}
VAPI_CORE_DOTNET_FILES = {
    'linux64': [r'publish/.*$'],
    'windows2012r2-vs2013-U3': [r'publish/.*$'],
    'macosx': [r'publish/.*$'],
}

VAPI_CORE_RUBY_BRANCH = THIS_BRANCH
VAPI_CORE_RUBY_CLN = RUBY_CLN
VAPI_CORE_RUBY_BUILDTYPES = {
   'obj': 'beta',
   'beta': 'beta',
   'release': 'release',
   'opt': 'release',
}
VAPI_CORE_RUBY_FILES = {
    'linux64': [r'publish/.*$'],
    'windows2012r2-vs2013-U3': [r'publish/.*$'],
    'macosx': [r'publish/.*$'],
}

VAPI_CORE_PERL_BRANCH = THIS_BRANCH
VAPI_CORE_PERL_CLN = PERL_CLN
VAPI_CORE_PERL_BUILDTYPES = {
   'obj': 'beta',
   'beta': 'beta',
   'release': 'release',
   'opt': 'release',
}
VAPI_CORE_PERL_FILES = {
    'linux64': [r'publish/.*$'],
    'windows2012r2-vs2013-U3': [r'publish/.*$'],
    'macosx': [r'publish/.*$'],
}

DCLICORE_BRANCH = THIS_BRANCH
DCLICORE_CLN = DCLI_CLN
DCLICORE_BUILDTYPES = {
   'obj': 'beta',
   'beta': 'beta',
   'release': 'release',
   'opt': 'release',
}
DCLICORE_FILES = {
    'linux64': [r'publish/.*$'],
    'windows2012r2-vs2013-U3': [r'publish/.*$'],
    'macosx': [r'publish/.*$'],
}
