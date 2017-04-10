# Copyright 2015 VMware, Inc. All rights reserved. VMware Confidential.

_DEFAULT_BUILDTYPE_MAPPING = {
   'obj': 'beta',
   'beta': 'beta',
   'release': 'release',
   'opt': 'beta',
}

_HOSTTYPES = ['linux', 'linux64', 'windows', 'windows-2008']

PH_PLATFORM_BRANCH = "platform-rel"
PH_PLATFORM_CLN = "157027"
PH_PLATFORM_BUILDTYPES = _DEFAULT_BUILDTYPE_MAPPING
PH_PLATFORM_FILES = dict.fromkeys(_HOSTTYPES, ['publish/.*'])