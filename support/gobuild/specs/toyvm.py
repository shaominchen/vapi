# Copyright 2014 VMware, Inc.  All rights reserved. -- VMware Confidential

"""
toyvm component spec.
"""

from specs.branch import THIS_BRANCH

# vapi-core
VAPI_CORE_BRANCH = THIS_BRANCH
VAPI_CORE_CLN = 4573875
VAPI_CORE_BUILDTYPES = {
   'obj': 'beta',
   'beta': 'beta',
   'release': 'release',
   'opt': 'release',
}
VAPI_CORE_HOSTTYPES = {
  'linux': 'linux64',
  'linux64': 'linux64',
  'windows': 'windows2012r2-vs2013-U3',
  'windows2012r2-vs2013-U3': 'windows2012r2-vs2013-U3',
}
