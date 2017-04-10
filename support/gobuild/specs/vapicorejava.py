# Copyright 2013-2015 VMware, Inc.  All rights reserved. -- VMware Confidential

from specs.branch import THIS_BRANCH
from specs.branch import VMKERNEL_MAIN_BRANCH
from specs.vapi_components_clns import IDL_CLN

"""
vAPI java-toolkit component spec.
"""

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

# rd-platform-services (for samltoken)
RD_PLATFORM_SERVICES_BRANCH = VMKERNEL_MAIN_BRANCH
RD_PLATFORM_SERVICES_CLN = 3816049
RD_PLATFORM_SERVICES_BUILDTYPE = 'release'

platformServicesWinFiles = [
   r'publish/win64/jars/.*',
]

platformServicesLinuxFiles = [
   r'publish/lin64/jars/.*',
]

RD_PLATFORM_SERVICES_FILES = {
   'linux64': platformServicesLinuxFiles,
   'linux': platformServicesLinuxFiles,
   'windows': platformServicesWinFiles,
   'windows-vs2012': platformServicesWinFiles,
}

RD_PLATFORM_SERVICES_HOSTTYPES = {
    "windows-vs2012": "windows-2008",
    "windows": "windows-2008",
    "linux64": "linux64",
    "linux": "linux64"
}
