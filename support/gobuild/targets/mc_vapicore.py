# Copyright 2013-2016 VMware, Inc.  All rights reserved. -- VMware Confidential

"""
vapi core manifest build gobuild product module.
"""

import helpers.bbq
import helpers.env
import helpers.target
import helpers.vapicorecommon
import os
import specs.vapicore
import specs.vapicpp


class _VapiCoreMc(helpers.target.Target, helpers.bbq.BBQHelper):
   """
   vAPI Core common
   """
   def _Environment(self, hosttype):
      env = helpers.env.SafeEnvironment(hosttype)

      if hosttype.startswith('windows'):
         return env # done

      tcroot = os.environ.get('TCROOT', '/build/toolchain')

      paths = [os.path.join(tcroot, 'lin32', path)
               for path in ['python-2.7.1/bin',
                            'coreutils-5.97/bin',
                            'findutils-4.2.27/bin',
                            'grep-2.5.1a/bin']]
      paths.append(env['PATH'])
      env['PATH'] = os.pathsep.join(paths)

      return env

   def _VapiCoreMcCommand(self, hosttype, product, target):

      return {'desc': 'Compiling vAPI Core %s %s' % (product, target),
              'root': '%(buildroot)',
              'log': '%s.log' % product,
              'command': self._ManifestBBQCommand(hosttype, product, target),
              'env': self._Environment(hosttype),
              }

   def _VapiCoreMcComponentDependencies(self):
      comps = {}
      comps['boost%s_lin32_gcc443' % specs.vapicpp.BOOST_COMP_VERSION] = {
         'branch': specs.vapicpp.BOOST_BRANCH,
         'change': specs.vapicpp.BOOST_CLN,
         'buildtype': specs.vapicpp.BOOST_BUILDTYPE,
         'hosttypes': specs.vapicpp.BOOST_LIN_HOSTTYPES_32
      }
      comps['boost%s_lin64_gcc443' % specs.vapicpp.BOOST_COMP_VERSION] = {
         'branch': specs.vapicpp.BOOST_BRANCH,
         'change': specs.vapicpp.BOOST_CLN,
         'buildtype': specs.vapicpp.BOOST_BUILDTYPE,
         'hosttypes': specs.vapicpp.BOOST_LIN_HOSTTYPES_64
      }
      comps['boost%s_lin32_gcc480' % specs.vapicpp.BOOST_COMP_VERSION] = {
         'branch': specs.vapicpp.BOOST_BRANCH,
         'change': specs.vapicpp.BOOST_CLN,
         'buildtype': specs.vapicpp.BOOST_BUILDTYPE,
         'hosttypes': specs.vapicpp.BOOST_LIN_HOSTTYPES_32
      }
      comps['boost%s_lin64_gcc480' % specs.vapicpp.BOOST_COMP_VERSION] = {
         'branch': specs.vapicpp.BOOST_BRANCH,
         'change': specs.vapicpp.BOOST_CLN,
         'buildtype': specs.vapicpp.BOOST_BUILDTYPE,
         'hosttypes': specs.vapicpp.BOOST_LIN_HOSTTYPES_64
      }
      comps['boost%s_win32_vc90sp1' % specs.vapicpp.BOOST_COMP_VERSION] = {
         'branch': specs.vapicpp.BOOST_BRANCH,
         'change': specs.vapicpp.BOOST_CLN,
         'buildtype': specs.vapicpp.BOOST_BUILDTYPE,
         'hosttypes': specs.vapicpp.BOOST_WIN_HOSTTYPES_32
      }
      comps['boost%s_win64_vc90sp1' % specs.vapicpp.BOOST_COMP_VERSION] = {
         'branch': specs.vapicpp.BOOST_BRANCH,
         'change': specs.vapicpp.BOOST_CLN,
         'buildtype': specs.vapicpp.BOOST_BUILDTYPE,
         'hosttypes': specs.vapicpp.BOOST_WIN_HOSTTYPES_64
      }
      comps['boost%s_win32_vc120' % specs.vapicpp.BOOST_COMP_VERSION] = {
         'branch': specs.vapicpp.BOOST_BRANCH,
         'change': specs.vapicpp.BOOST_CLN,
         'buildtype': specs.vapicpp.BOOST_BUILDTYPE,
         'hosttypes': specs.vapicpp.BOOST_WIN_HOSTTYPES_VC12_32
      }
      comps['boost%s_win64_vc120' % specs.vapicpp.BOOST_COMP_VERSION] = {
         'branch': specs.vapicpp.BOOST_BRANCH,
         'change': specs.vapicpp.BOOST_CLN,
         'buildtype': specs.vapicpp.BOOST_BUILDTYPE,
         'hosttypes': specs.vapicpp.BOOST_WIN_HOSTTYPES_VC12_64
      }
      comps['cayman'] = {
         'branch': specs.vapicpp.CAYMAN_BRANCH,
         'change': specs.vapicpp.CAYMAN_CLN,
         'buildtype': specs.vapicpp.CAYMAN_BUILDTYPE,
         'hosttypes': specs.vapicpp.CAYMAN_HOSTTYPES,
      }
      comps['cayman_esx_glibc'] = {
         'branch'    : specs.vapicpp.CAYMAN_ESX_GLIBC_BRANCH,
         'change'    : specs.vapicpp.CAYMAN_ESX_GLIBC_CLN,
         'buildtype' : specs.vapicpp.CAYMAN_ESX_GLIBC_BUILDTYPE,
         'hosttypes' : specs.vapicpp.CAYMAN_ESX_TOOLCHAIN_HOSTTYPES,
         'cacheflags': specs.vapicpp.CAYMAN_ESX_GLIBC_CACHEFLAGS,
      }
      comps['cayman_esx_toolchain'] = {
         'branch': specs.vapicpp.CAYMAN_ESX_TOOLCHAIN_BRANCH,
         'change': specs.vapicpp.CAYMAN_ESX_TOOLCHAIN_CLN,
         'buildtype': specs.vapicpp.CAYMAN_ESX_TOOLCHAIN_BUILDTYPE,
         'hosttypes': specs.vapicpp.CAYMAN_ESX_TOOLCHAIN_HOSTTYPES,
         'cacheflags': specs.vapicpp.CAYMAN_ESX_TOOLCHAIN_CACHEFLAGS,
      }
      comps['cayman_llvm'] = {
         'branch': specs.vapicpp.CAYMAN_LLVM_BRANCH,
         'change': specs.vapicpp.CAYMAN_LLVM_CLN,
         'buildtype': specs.vapicpp.CAYMAN_LLVM_BUILDTYPE,
         'hosttypes': specs.vapicpp.CAYMAN_LLVM_HOSTTYPES,
      }
      comps['cayman_pion'] = {
         'branch': specs.vapicpp.CAYMAN_PION_BRANCH,
         'change': specs.vapicpp.CAYMAN_PION_CLN,
         'hosttypes': specs.vapicpp.CAYMAN_PION_HOSTTYPES
      }
      comps['cayman_rapidjson'] = {
         'branch': specs.vapicpp.CAYMAN_RAPIDJSON_BRANCH,
         'change': specs.vapicpp.CAYMAN_RAPIDJSON_CLN,
         'buildtype': specs.vapicpp.CAYMAN_RAPIDJSON_BUILDTYPE,
         'hosttypes': specs.vapicpp.CAYMAN_RAPIDJSON_HOSTTYPES,
      }
      comps['cayman_scons'] = {
         'branch': specs.vapicpp.CAYMAN_SCONS_BRANCH,
         'change': specs.vapicpp.CAYMAN_SCONS_CLN,
         'buildtype': specs.vapicpp.CAYMAN_SCONS_BUILDTYPE,
         'hosttypes': specs.vapicpp.CAYMAN_SCONS_HOSTTYPES,
      }
      comps['rd-platform-services'] = {
         'branch': specs.vapicorejava.RD_PLATFORM_SERVICES_BRANCH,
         'change': specs.vapicorejava.RD_PLATFORM_SERVICES_CLN,
         'buildtype': specs.vapicorejava.RD_PLATFORM_SERVICES_BUILDTYPE,
         'files': specs.vapicorejava.RD_PLATFORM_SERVICES_FILES,
      }
      return comps


class VapiCoreMcDev(_VapiCoreMc):
   """
   vAPI Core Linux/Windows component
   """
   def GetRepositories(self, hosttype):
      repos = [
               helpers.target.PerforceRepo('vapi-core', 'vapi-core/%(branch)', 'vapi-core'),
               helpers.target.PerforceRepo('mc', 'mc/main', 'mc'),
               helpers.target.PerforceRepo('manifest', 'manifest/main', 'manifest'),
               helpers.target.PerforceRepo('bbq', 'bbq/main', 'bbq'),
               { 'rcs' : 'perforce', 'name': 'gmsl', 'src' : '//contrib/gmsl/gmsl-1-1-5', 'dst' : 'gmsl' },
              ]
      return repos

   def GetStorageInfo(self, hosttype):
      storages = []
      if hosttype.startswith('linux'):
         # The Linux side is responsible for copying the source files to storage.
         storages.append({'type': 'source', 'src': 'vapi-core'})
      storages.append({'type': 'build', 'src': 'vapi-core/build'})
      return storages

   def GetBuildProductVersion(self, hosttype):
      versionFile = '%s/vapi-core/support/version/vapi-core-version.properties'  % self.options.get('buildroot')
      return helpers.vapicorecommon.VapiCoreCommon.readVersionFromFile(versionFile)

   def GetClusterRequirements(self):
      return ['linux64',] # 'windows-2008', 'windows2012r2-vs2013']

   def GetBuildProductNames(self):
      return { 'name':      'mc-vapi-core',
               'longname' : 'vAPI Core dev'}

   def GetComponentDependencies(self):
      return self._VapiCoreMcComponentDependencies()

   def GetCommands(self, hosttype):
      target = 'install'
      product = 'vapi-core'

      #product_map = {
         #'linux64' : ['esx-cayman', # 32bit, cayman_esx_toolchain, cayman_esx_glibc sysroot
                      #'lin64', # 64bit, $TCROOT/lin32/gcc-4.4.3, RHEL5 sysroot
                      #'lin64-cayman' # 64bit, cayman_esx_toolchain, $TCROOT/lin64/glibc-2.11.1-0.17.4 sysroot
                      ## other?
                      #],
         #'windows-2008': ['win32', 'win64'],
         #'windows2012r2-vs2013': ['win32_vc120', 'win64_vc120']
      #}

      #products = product_map[hosttype]

      return [self._VapiCoreMcCommand(hosttype, product, target)]

   def GetComponentPath(self):
      return '' # None
