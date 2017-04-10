# Copyright 2014 VMware, Inc.  All rights reserved. -- VMware Confidential

"""
dclicore gobuild module.
"""

import helpers.target
import helpers.vapicorecommon
import os
import specs.vapicore
import specs.dclicore
from helpers.vapicorecommon import VapiCoreCommon

class DcliCore(helpers.vapicorecommon.VapiCoreCommon):
   """
   dclicore gobuild module
   """

   def GetClusterRequirements(self):
      return ['linux64', 'windows2012r2-vs2013-U3', 'macosx-lion']

   def GetComponentDependencies(self):
      comps = {}

      comps['vapi-core-idl'] = {
         'branch': specs.vapicore.VAPI_CORE_IDL_BRANCH,
         'change': specs.vapicore.VAPI_CORE_IDL_CLN,
         'buildtype': specs.vapicore.VAPI_CORE_IDL_BUILDTYPES[self.options.get('buildtype')],
         'files': specs.vapicore.VAPI_CORE_IDL_FILES,
      }
      comps['vapi-core-python'] = {
         'branch': specs.dclicore.VAPI_CORE_PYTHON_BRANCH,
         'change': specs.dclicore.VAPI_CORE_PYTHON_CLN,
         'buildtype': specs.dclicore.VAPI_CORE_PYTHON_BUILDTYPE,
         'files': specs.dclicore.VAPI_CORE_PYTHON_FILES,
      }

      return comps

   def GetComponentPath(self):
      return 'publish'

   def GetCommands(self, hosttype):
      """
      Return a list of dictionaries representing commands to execute
      to build this target
      """
      sconsflags = {}
      sconsflags['PRODUCT'] = 'dclicore'
      target = 'dcli-all'
      return [{'desc': 'Building %s target' % target,
               'root': '',
               'log': '%s.log' % target,
               'command': self._SconsCommand(hosttype, target, **sconsflags),
               'env': self._SconsEnvironment(hosttype)
              }]

   def GetBuildProductNames(self):
      return {'name': 'dcli-all',
              'longname': self.__doc__.strip()}

   def GetBuildProductVersion(self, hosttype):
      versionFile = '%s/vapi-core/support/version/vapi-core-version.properties'  % self.options.get('buildroot')
      return VapiCoreCommon.readVersionFromFile(versionFile)

   def GetRepositories(self, hosttype):
      repos = [
               helpers.target.PerforceRepo('vapi-core', 'vapi-core/%(branch)', 'vapi-core'),
               helpers.target.PerforceRepo('scons', 'scons/vmkernel-main', 'scons'),
              ]
      return repos

   def GetStorageInfo(self, hosttype):
      storinfo = []
      if hosttype.startswith('linux'):
         storinfo += [{'type': 'source', 'src': 'vapi-core'}]
      storinfo += [{'type': 'build', 'src': 'vapi-core/build'}]
      return storinfo


