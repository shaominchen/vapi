# Copyright 2014 VMware, Inc.  All rights reserved. -- VMware Confidential

"""
nsxprovider component spec.
"""

VAPI_BRANCH='vapi-2015-q1'

# vapi-core
vapiCoreFiles = [
    r'publish/idl-toolkit/.*$',
    r'publish/lib/.*$',
    r'publish/metadata/.*$',
    r'publish/java-toolkit/.*$',
    r'publish/metadata-toolkit/.*$',
    r'publish/python-toolkit/.*$',
    r'publish/tools/.*$',
    r'publish/vmodl/.*$',
]

VAPI_CORE_BRANCH = VAPI_BRANCH
VAPI_CORE_CLN = 3470938
VAPI_CORE_BUILDTYPES = {
   'obj': 'beta',
   'beta': 'beta',
   'release': 'release',
   'opt': 'release',
}

VAPI_CORE_FILES = {
    'linux': vapiCoreFiles,
    'linux64': vapiCoreFiles,
    'windows': vapiCoreFiles,
    'windows2012r2-vs2013-U3': vapiCoreFiles,
}

# vapi-core
dcliCoreFiles = [
    r'publish/vmware-*',
]

DCLI_CORE_BRANCH = VAPI_BRANCH
DCLI_CORE_CLN = 3444687
DCLI_CORE_BUILDTYPES = {
   'obj': 'beta',
   'beta': 'beta',
   'release': 'release',
   'opt': 'release',
}

DCLI_CORE_FILES = {
    'linux': dcliCoreFiles,
    'linux64': dcliCoreFiles,
    'windows': dcliCoreFiles,
    'windows2012r2-vs2013-U3': dcliCoreFiles,
}
