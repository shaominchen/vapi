# Copyright 2015 VMware, Inc.  All rights reserved. -- VMware Confidential

from specs.branch import THIS_BRANCH
from specs.vapi_components_clns import (IDL_CLN, JAVA_CLN)

"""
vAPI dotnet-toolkit component spec
"""

# vapi-core-idl dependency
idlToolkitFiles = [
    r'publish/.*$',
]
VAPI_CORE_IDL_BRANCH = THIS_BRANCH
VAPI_CORE_IDL_CLN = IDL_CLN
VAPI_CORE_IDL_BUILDTYPES = {
    'obj': 'beta',
    'beta': 'beta',
    'release': 'release',
    'opt': 'release',
}

VAPI_CORE_IDL_FILES = {
    'windows-vs2012': idlToolkitFiles,
    'windows': idlToolkitFiles,
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
    'windows-vs2012': [r'publish/.*$'],
    'windows': [r'publish/.*$'],
}

