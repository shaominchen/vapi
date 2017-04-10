# Copyright 2013-2015 VMware, Inc.  All rights reserved. -- VMware Confidential

from specs.branch import THIS_BRANCH
from specs.vapi_components_clns import IDL_CLN

"""
vAPI python-toolkit component spec.
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

CAYMAN_PYTHON_BRANCH = 'vmware-master'
CAYMAN_PYTHON_CLN = '7d1fb6e9e476614f196c8fb58ecb071327729ea7'
CAYMAN_PYTHON_BUILDTYPE = 'release'
CAYMAN_PYTHON_HOSTTYPES = {
    'linux64': 'linux64'
}

CAYMAN_SETUPTOOLS_BRANCH = 'vmware-master'
CAYMAN_SETUPTOOLS_CLN = 'ef73387ef3d12af57b2d0f5ade7fe4c43a84197d'
CAYMAN_SETUPTOOLS_BUILDTYPE = 'release'
CAYMAN_SETUPTOOLS_HOSTTYPES = {
    'linux64': 'linux64'
}

IUP_PLATFORM_EXTERNAL_BRANCH = 'master'
IUP_PLATFORM_EXTERNAL_CLN = 'f8e3e0c1f0feba364c94f205d29d88fe709aafe2'
IUP_PLATFORM_EXTERNAL_BUILDTYPE = 'release'
IUP_PLATFORM_EXTERNAL_FILES = {
    'linux64': [r'publish/Werkzeug-0.9.6-py2.7.egg']
}
