# Copyright 2015 VMware, Inc.  All rights reserved. -- VMware Confidential

from specs.branch import THIS_BRANCH
from specs.vapi_components_clns import (IDL_CLN, JAVA_CLN)

"""
vAPI ruby-toolkit component spec
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
    'linux64': idlToolkitFiles,
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
    'linux64': [r'publish/.*$'],
    'windows2012r2-vs2013-U3': [r'publish/.*$'],
    'macosx': [r'publish/.*$'],
}

