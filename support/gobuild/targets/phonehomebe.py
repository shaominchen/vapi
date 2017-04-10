# Copyright 2012-2013 VMware, Inc.  All rights reserved. -- VMware Confidential

"""
GoBuild module for the PhoneHome Backend
"""

import os

import helpers.antlauncher
import helpers.env
import helpers.target

import specs.phonehomebe


class PhoneHomeBackend(helpers.target.Target, helpers.antlauncher.AntLauncherHelper):
   """ PhoneHome Backend """

   def GetBuildProductNames(self):
      return {'name': 'phonehomebe',
              'longname': self.__doc__.strip()}

   def GetBuildProductVersion(self, hosttype):
      return '2.0'

   def GetComponentDependencies(self):
      comps = {}
      # This is a dependency to the Phone Home Platform
      comps['phplatform'] = {
         'branch': specs.phonehomebe.PH_PLATFORM_BRANCH,
         'change': specs.phonehomebe.PH_PLATFORM_CLN,
         'buildtype': specs.phonehomebe.PH_PLATFORM_BUILDTYPES[self.options.get('buildtype')],
         'files': specs.phonehomebe.PH_PLATFORM_FILES,
      }
      return comps


   def GetClusterRequirements(self):
      # Even though only linux64 deliverables will be used in production,
      # sandboxes should verify the Mac and Windows builds as well.
      if self.options.get('buildtype') == 'release':
         return ['linux64']
      else:
         return ['linux64', 'windows-2008']


   def GetRepositories(self, hosttype):
      return [
         helpers.target.PerforceRepo('phonehome', 'phonehome/%(branch)', 'phonehome'),
      ]

   def GetCommands(self, hosttype):
      product_names = self.GetBuildProductNames()

      env = self._GetEnvironment(hosttype,
                                 antversion='1.8.3',
                                 jdkversion='1.7.0_07')

      command_flags = {
         'BUILD_ROOT' : '"%(buildroot)/build"',
         'COMPONENT_DIST_DIR' : '',
      }

      command = {
         'desc'   : 'Building the ' + product_names['longname'],
         'root'   : '%(buildroot)/phonehome',
         'log'    : product_names['name'] + '.log',
         'env'    : env,
         'command': self._Command(hosttype,
                                  ant_home=env['ANT_HOME'],
                                  java_home=env['JAVA_HOME'],
                                  **command_flags)
      }

      return [
         command,
      ]

   def GetStorageInfo(self, hosttype):
      storageInfo = []
      if hosttype == "linux64":
         storageInfo += [{'type': 'source', 'src': 'phonehome/'}]
      storageInfo += [{'type': 'build', 'src': 'build/'}]
      return storageInfo
