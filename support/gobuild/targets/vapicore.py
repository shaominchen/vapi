# Copyright 2014-2015 VMware, Inc.  All rights reserved. -- VMware Confidential

"""
vapi-core gobuild module.
"""
import os

import helpers.vapicorecommon
import helpers.maven
import helpers.target
from helpers.util import MkdirCommand, RMrfCommand
import specs.vapicore
import specs.vapicoredotnet
import specs.vapicorejava
import specs.vapicoremetadata
import specs.vapicoreperl
import specs.vapicorepython
import specs.vapicoreruby
import targets.vapicpp


class VapiJavaCommon(helpers.vapicorecommon.VapiCoreCommon):
   """
   vAPI Java build common
   """

   _gradleVersion = 'gradle-2.0'
   _mavenVersion = '3.2.3'   # 'apache-maven-3.2.3'
   _jdkVersion = 'jdk-1.7.0_79'
   _jdkMacVersion = 'java-osx-10.4-release6'

   def _JavaEnvironment(self, hosttype):
      """
      Return an environment dictionary suitable for use in running Java
      commands in the vapi-core tree.
      """

      # Use the scons environment as a starting point
      env = self._SconsEnvironment(hosttype)

      # Add build environment
      env['BUILDTYPE'] = '%(buildtype)'
      env['BUILD_NUMBER'] = '%(buildnumber)'
      env['PRODUCT_BUILD_NUMBER'] = '%(productbuildnumber)'
      env['CHANGE_NUMBER'] = '%(changenumber)'
      env['BRANCH_NAME'] = '%(branch)'
      env['RELEASETYPE'] = '%(releasetype)'
      env['GOBUILD_TARGET'] = self.GetBuildProductNames()['name']

      for d in self.GetComponentDependencies():
         d = d.replace('-', '_')
         env['GOBUILD_%s_ROOT' % d.upper()] = '%%(gobuild_component_%s_root)' % d

      # Configure the Java and Gradle home directories
      if os.environ.has_key('TCROOT'):
         tcroot = os.environ['TCROOT']
      elif hosttype.startswith('windows'):
         tcroot = 'T:\\'
      else:
         tcroot = '/build/toolchain'
      env['TCROOT'] = tcroot

      env['GRADLE_HOME'] = os.path.join(tcroot, 'noarch', self._gradleVersion)
      if hosttype.startswith('windows'):
         env['JAVA_HOME'] = os.path.join(tcroot, 'win32', self._jdkVersion)
      elif hosttype.startswith('linux'):
         env['JAVA_HOME'] = os.path.join(tcroot, 'lin32', self._jdkVersion)
      elif hosttype.startswith('macosx'):
         env['JAVA_HOME'] = os.path.join(tcroot, 'mac32', self._jdkMacVersion, 'usr')
      else:
         raise helpers.target.GobuildTargetException("Unknown hosttype %s" % hosttype)

      return env

   def GetBuildProductVersion(self, hosttype):
      versionFile = '%s/vapi-core/support/version/vapi-core-version.properties'  % self.options.get('buildroot')
      return helpers.vapicorecommon.VapiCoreCommon.readVersionFromFile(versionFile)

class VapiMavenCommon(VapiJavaCommon, helpers.maven.MavenHelper):
   """
   vAPI Maven build common
   """
   def _MavenEnvironment(self, hosttype):
      """
      Return an environment dictionary suitable for use in running a Maven
      command in the vapi-core tree.
      """
      return self._JavaEnvironment(hosttype)

   def _MavenCommand(self, hosttype, srcroot, targets, opts={}, sysprops={}):

      def q(str):
         return  '"' + str + '"'

      options = {
         '--file': q(os.path.join(srcroot, 'pom.xml')),
         '--global-settings': q(os.path.join(srcroot, 'settings.xml')),
         '--errors': None,
         '--batch-mode': None,
         #'--debug':None
      }
      options.update(opts)

      sysproperties = {
         'maven.repo.local': q('%(buildroot)/build/m2-repository'),
         'skipTests': 'true',
         'srcroot': q('%%(buildroot)/%s' % srcroot),
         'buildroot': q('%(buildroot)/build'),
         'gobuildroot': q('%(buildroot)/build'),
         'OBJDIR': '%(buildtype)',
         'GOBUILD_AUTO_COMPONENTS': 'false',
         }
      sysproperties.update(sysprops)

      # helpers.maven.MavenHelper._Command builds the mvn cmd line, it also passes more
      # sysproperties, e.g. the gobuild comp roots like GOBUILD_VAPI_CORE_IDL_ROOT
      return self._Command(hosttype, targets,  self._mavenVersion, options, **sysproperties)

   # Resuired by helpers.maven.MavenHelper, but not defined above in the hierarchy,
   # probably there is mismatch of revision; let's define it here...
   def GetComponentDependencyAliases(self):
      return self.GetComponentDependencies().keys()


class VapiCore(VapiMavenCommon,
               targets.vapicpp._VapiCpp):
   """
   vAPI-Core gobuild module
   """

   def GetComponentDependencies(self):
      comps = self._VapiCppComponentDependencies()
      comps['vapi-core-idl'] = {
         'branch': specs.vapicore.VAPI_CORE_IDL_BRANCH,
         'change': specs.vapicore.VAPI_CORE_IDL_CLN,
         'buildtype': specs.vapicore.VAPI_CORE_IDL_BUILDTYPES[self.options.get('buildtype')],
         'files': specs.vapicore.VAPI_CORE_IDL_FILES,
      }
      comps['vapi-core-java'] = {
         'branch': specs.vapicore.VAPI_CORE_JAVA_BRANCH,
         'change': specs.vapicore.VAPI_CORE_JAVA_CLN,
         'buildtype': specs.vapicore.VAPI_CORE_JAVA_BUILDTYPES[self.options.get('buildtype')],
         'files': specs.vapicore.VAPI_CORE_JAVA_FILES,
      }
      comps['vapi-core-python'] = {
         'branch': specs.vapicore.VAPI_CORE_PYTHON_BRANCH,
         'change': specs.vapicore.VAPI_CORE_PYTHON_CLN,
         'buildtype': specs.vapicore.VAPI_CORE_PYTHON_BUILDTYPES[self.options.get('buildtype')],
         'files': specs.vapicore.VAPI_CORE_PYTHON_FILES,
      }
      comps['vapi-core-metadata'] = {
         'branch': specs.vapicore.VAPI_CORE_METADATA_BRANCH,
         'change': specs.vapicore.VAPI_CORE_METADATA_CLN,
         'buildtype': specs.vapicore.VAPI_CORE_METADATA_BUILDTYPES[self.options.get('buildtype')],
         'files': specs.vapicore.VAPI_CORE_METADATA_FILES,
      }
      comps['vapi-core-dotnet'] = {
         'branch': specs.vapicore.VAPI_CORE_DOTNET_BRANCH,
         'change': specs.vapicore.VAPI_CORE_DOTNET_CLN,
         'buildtype': specs.vapicore.VAPI_CORE_DOTNET_BUILDTYPES[self.options.get('buildtype')],
         'files': specs.vapicore.VAPI_CORE_DOTNET_FILES,
      }
      comps['vapi-core-ruby'] = {
         'branch': specs.vapicore.VAPI_CORE_RUBY_BRANCH,
         'change': specs.vapicore.VAPI_CORE_RUBY_CLN,
         'buildtype': specs.vapicore.VAPI_CORE_RUBY_BUILDTYPES[self.options.get('buildtype')],
         'files': specs.vapicore.VAPI_CORE_RUBY_FILES,
      }
      comps['vapi-core-perl'] = {
         'branch': specs.vapicore.VAPI_CORE_PERL_BRANCH,
         'change': specs.vapicore.VAPI_CORE_PERL_CLN,
         'buildtype': specs.vapicore.VAPI_CORE_PERL_BUILDTYPES[self.options.get('buildtype')],
         'files': specs.vapicore.VAPI_CORE_PERL_FILES,
      }
      comps['dclicore'] = {
         'branch': specs.vapicore.DCLICORE_BRANCH,
         'change': specs.vapicore.DCLICORE_CLN,
         'buildtype': specs.vapicore.DCLICORE_BUILDTYPES[self.options.get('buildtype')],
         'files': specs.vapicore.DCLICORE_FILES,
      }
      return comps

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

   def GetBuildProductNames(self):
      return {'name': 'vapi-core',
              'longname': self.__doc__.strip()}

   def GetClusterRequirements(self):
      return ['linux64', 'windows2012r2-vs2013-U3']

   # Resuired by helpers.maven.MavenHelper, but not defined above in the hierarchy,
   # probably there is mismatch of reversion; let's define it here...
   def GetComponentDependencyAliases(self):
      return self.GetComponentDependencies().keys()

   def GetComponentPath(self):
      return '%(buildroot)/component'

   def GetCommands(self, hosttype):
      """
      Return a list of dictionaries representing commmands to execute
      to build this target.
      """

      vapicppProducts = {
         'linux64' : ['esx-cayman', # 32bit, cayman_esx_toolchain, cayman_esx_glibc sysroot
                      #'lin64', # 64bit, $TCROOT/lin32/gcc-4.4.3, RHEL5 sysroot
                      'lin64-cayman' # 64bit, cayman_esx_toolchain, $TCROOT/lin64/glibc-2.11.1-0.17.4 sysroot
                      # other?
                      ],
         'windows2012r2-vs2013-U3': [#'win32_vc120',
                                  'win64_vc120']
      }[hosttype]

      cppCommands = [self._VapiCppCommand(hosttype, product, 'vapicpp-runtime')
                  for product in vapicppProducts]

      comppath = self.GetComponentPath()
      publishpath = '%(buildroot)/publish'

      commands = []

      if hosttype.startswith('linux'):
         sconsflags = {}
         sconsflags['PRODUCT'] = 'vapicore'
         target = 'vapi-core'

         commands.append({'desc': 'Compiling %s target' % target,
                          'root': '',
                          'log': '%s.log' % target,
                          'command': self._SconsCommand(hosttype, target, **sconsflags),
                          'env': self._JavaEnvironment(hosttype)
                          })

         stageDir = '%(buildroot)/vapi-core/build/vapicore/%(buildtype)/'
         pdkStageSrcDir = os.path.join(stageDir, 'vapi')
         pdkStageTgtDir = os.path.join(stageDir, 'vapi-pdk')
         commands.append(MkdirCommand(hosttype, pdkStageTgtDir))
         commands.append(self._CopySourceContent(
             hosttype, pdkStageSrcDir, pdkStageTgtDir))

         commands.append(self._CopySourceContent(
             hosttype, pdkStageTgtDir, publishpath))
         commands.extend(self._Transfer(hosttype,
                                        publishpath,
                                        'vapi-pdk'))
         # XXX Use 'component' instead of GetComponentPath() because Mkdir does
         # not work with '%(buildroot)' on windows
         commands.append(MkdirCommand(hosttype, 'component'))

         commands.extend(cppCommands)
         commands.append(self._CopySourceContent(
             hosttype, publishpath, comppath))
         commands.append(RMrfCommand(hosttype, 'component/*/*.zip',
                                     logfile='rm-zipfiles.log'))

         # Create tgz file of the pdk in the publish folder
         commands.append(self._TarGz(hosttype,
                                     comppath,
                                     '%%(buildroot)/publish/vapi-pdk-%s.tgz' % hosttype,
                                     'vapi-pdk.*'))

      else: # Windows
         commands.extend(self._AcceptTransfer(hosttype,
                                              'vapi-pdk',
                                              self.GetComponentPath()))
         commands.extend(cppCommands)
         commands.append(self._CopySourceContent(
             hosttype, publishpath, comppath))
         # there is a bug in gobuild that prevents the same files to be
         # published from different host types
         commands.append(RMrfCommand(hosttype, 'publish/vapicpp/*/include',
                                     logfile='rm-include.log'))
         commands.append(RMrfCommand(hosttype, 'publish/toyvm/cpp',
                                     logfile='rm-toyvm.log'))
         commands.append(RMrfCommand(hosttype, 'component/*/*.zip',
                                     logfile='rm-zipfiles.log'))


      #TODO: migrate clients to the component zip files

      # Create zip file of the pdk in the publish folder
      commands.append(self._Zip(hosttype,
                                comppath,
                                '%%(buildroot)/publish/vapi-pdk-%s.zip' % hosttype,
                                'vapi-pdk.*'))

      return commands


class VapiGradleCommon(VapiJavaCommon):
   """
   vAPI Gradle build common
   """
   def GetClusterRequirements(self):
      return ['linux64']

   def GetStorageInfo(self, hosttype):
      storinfo = []
      if hosttype.startswith('linux'):
         storinfo += [{'type': 'source', 'src': 'vapi-core'}]
      storinfo += [{'type': 'build', 'src': 'build'}]
      return storinfo

   def _GradleCommand(self, hosttype, srcroot, target, skiptasks=[]):
      def q(str):
         return  '"' + str + '"'

      gradleDir = os.path.join('noarch', self._gradleVersion)
      gradle = None
      if hosttype.startswith('windows'):
         gradle = os.path.join('%GRADLE_HOME%', 'bin', 'gradle.bat')
      elif hosttype.startswith('linux') or hosttype.startswith('macosx'):
         gradle = os.path.join('$GRADLE_HOME', 'bin', 'gradle')
      else:
         raise helpers.target.GobuildTargetException("Unknown hosttype %s" % hosttype)

      properties = {
         'base.dir': q('%%(buildroot)/%s' % srcroot),
         'build.root': q('%(buildroot)/build'),
         'build.publish': q('%(buildroot)/publish'),
         'vapi.core.version.properties': q('%(buildroot)/vapi-core/support/version/vapi-core-version.properties')
         }

      options = {
         'build-file': q(os.path.join(srcroot, 'build.gradle')),
         'gradle-user-home': q('%(buildroot)/gradle-home'),
         'project-cache-dir': q('%(buildroot)/gradle-cache'),
         }

      cmd = [gradle]
      cmd += ['-D%s=%s' % (k, v) for (k, v) in properties.iteritems()]
      cmd += ['--%s %s' % (k, v) for (k, v) in options.iteritems()]
      cmd += ['--exclude-task test']
      cmd += ['-x %s' % (v) for v in skiptasks]
      cmd += [target]

      return ' '.join(cmd)

   def _GradleEnvironment(self, hosttype):
      """
      Return an environment dictionary suitable for use in running a gradle
      command in the vapi-core tree.
      """
      return self._JavaEnvironment(hosttype)


class VapiCoreIdl(VapiGradleCommon):
   """
   vAPI IDL toolkit gobuild module
   """
   def GetBuildProductNames(self):
      return {'name': 'vapi-core-idl',
              'longname': self.__doc__.strip()}

   def GetComponentDependencies(self):
      comps = {}
      return comps

   def GetRepositories(self, hosttype):
      repos = [
               helpers.target.PerforceRepo('vapi-core-idl',
                                           'vapi-core/%(branch)/idl-toolkit',
                                           'vapi-core/idl-toolkit'),
               helpers.target.PerforceRepo('vapi-core-support-version',
                                           'vapi-core/%(branch)/support/version',
                                           'vapi-core/support/version'),
              ]
      return repos

   def GetCommands(self, hosttype):
      """
      Return a list of dictionaries representing commmands to execute
      to build this target.
      """
      srcroot = 'vapi-core/idl-toolkit'
      return [{'desc': 'Compiling vAPI IDL toolkit',
               'root': '',
               'log': 'vapi-core-idl.log',
               'command': self._GradleCommand(hosttype, srcroot, 'publish'),
               'env': self._GradleEnvironment(hosttype)
             }]


class VapiCorePython(VapiGradleCommon):
   """
   vAPI Python toolkit gobuild module
   """
   def GetBuildProductNames(self):
      return {'name': 'vapi-core-python',
              'longname': self.__doc__.strip()}

   def GetComponentDependencies(self):
      comps = {}
      comps['vapi-core-idl'] = {
         'branch': specs.vapicorepython.VAPI_CORE_IDL_BRANCH,
         'change': specs.vapicorepython.VAPI_CORE_IDL_CLN,
         'buildtype': specs.vapicorepython.VAPI_CORE_IDL_BUILDTYPES[self.options.get('buildtype')],
         'files': specs.vapicorepython.VAPI_CORE_IDL_FILES,
      }
      comps['cayman_python'] = {
         'branch': specs.vapicorepython.CAYMAN_PYTHON_BRANCH,
         'change': specs.vapicorepython.CAYMAN_PYTHON_CLN,
         'buildtype': specs.vapicorepython.CAYMAN_PYTHON_BUILDTYPE,
         'hosttypes': specs.vapicorepython.CAYMAN_PYTHON_HOSTTYPES,
      }
      comps['cayman_setuptools'] = {
         'branch': specs.vapicorepython.CAYMAN_SETUPTOOLS_BRANCH,
         'change': specs.vapicorepython.CAYMAN_SETUPTOOLS_CLN,
         'buildtype': specs.vapicorepython.CAYMAN_SETUPTOOLS_BUILDTYPE,
         'hosttypes': specs.vapicorepython.CAYMAN_SETUPTOOLS_HOSTTYPES,
      }
      comps['iup-platform-external'] = {
         'branch': specs.vapicorepython.IUP_PLATFORM_EXTERNAL_BRANCH,
         'change': specs.vapicorepython.IUP_PLATFORM_EXTERNAL_CLN,
         'buildtype': specs.vapicorepython.IUP_PLATFORM_EXTERNAL_BUILDTYPE,
         'files': specs.vapicorepython.IUP_PLATFORM_EXTERNAL_FILES,
      }
      return comps

   def GetRepositories(self, hosttype):
      repos = [
               helpers.target.PerforceRepo('vapi-core-python',
                                           'vapi-core/%(branch)/python-toolkit',
                                           'vapi-core/python-toolkit'),
               helpers.target.PerforceRepo('vapi-core-support-gradle',
                                           'vapi-core/%(branch)/support',
                                           'vapi-core/support'),
              ]
      return repos

   def GetCommands(self, hosttype):
      """
      Return a list of dictionaries representing commmands to execute
      to build this target.
      """
      srcroot = 'vapi-core/python-toolkit'
      return [{'desc': 'Compiling vAPI Python toolkit',
               'root': '',
               'log': 'python-toolkit.log',
               'command': self._GradleCommand(hosttype, srcroot, 'publish'),
               'env': self._GradleEnvironment(hosttype)
             }]


class VapiCoreJava(VapiGradleCommon, VapiMavenCommon):
   """
   vAPI Java toolkit gobuild module
   """
   def GetBuildProductNames(self):
      return {'name': 'vapi-core-java',
              'longname': self.__doc__.strip()}

   def GetComponentDependencies(self):
      comps = {}
      comps['vapi-core-idl'] = {
         'branch': specs.vapicorejava.VAPI_CORE_IDL_BRANCH,
         'change': specs.vapicorejava.VAPI_CORE_IDL_CLN,
         'buildtype': specs.vapicorejava.VAPI_CORE_IDL_BUILDTYPES[self.options.get('buildtype')],
         'files': specs.vapicorejava.VAPI_CORE_IDL_FILES,
      }
      comps['rd-platform-services'] = {
         'branch': specs.vapicorejava.RD_PLATFORM_SERVICES_BRANCH,
         'change': specs.vapicorejava.RD_PLATFORM_SERVICES_CLN,
         'buildtype': specs.vapicorejava.RD_PLATFORM_SERVICES_BUILDTYPE,
         'files': specs.vapicorejava.RD_PLATFORM_SERVICES_FILES,
      }
      return comps

   def GetRepositories(self, hosttype):
      repos = [
               helpers.target.PerforceRepo('vapi-core-java',
                                           'vapi-core/%(branch)/java-toolkit',
                                           'vapi-core/java-toolkit'),
               helpers.target.PerforceRepo('vapi-core-support-gradle',
                                           'vapi-core/%(branch)/support',
                                           'vapi-core/support'),
              ]
      return repos

   def GetCommands(self, hosttype):
      """
      Return a list of dictionaries representing commmands to execute
      to build this target.
      """

      def q(str):
         return  '"' + str + '"'

      env = self._GradleEnvironment(hosttype)
      env['SRCROOT'] = '../..'
      srcroot = 'vapi-core/java-toolkit/generator'
      mvnSrcroot = 'vapi-core/java-toolkit/java-runtime'

      options = {
         # Gradle build is invoked explicitly, so disable its integration
         # in the Maven build
         '--projects': '!gradle-int'
      }

      sysproperties = {
         'build.publish': q('%(buildroot)/publish'),
         'java-toolkit.generator.bin.dir': q('%(buildroot)/publish/bin'),
         'java.runtime.publish.dir': q('%(buildroot)/publish/runtime'),
         'java.runtime.deps.publish.dir': q('%(buildroot)/publish/runtime/deps'),
         'uber.provider.assemble.dir': q('%(buildroot)/publish/uber-provider/java')
      }

      # 2 commands to build:
      #   1. gradle build for vAPI Java generator
      #   2. maven build of vAPI Java runtime
      return [{'desc': 'Compiling vAPI Java toolkit',
               'root': '',
               'log': 'java-toolkit-gradle.log',
               'command': self._GradleCommand(hosttype, srcroot, 'publish'),
               'env': env
             },
             {'desc': 'Compiling vAPI Java runtime',
              'root': '',
              'log': 'java-runtime-maven.log',
              'command': self._MavenCommand(hosttype, mvnSrcroot, ['install'], options, sysproperties),
              'env': self._MavenEnvironment(hosttype)
             }]


class VapiCoreMetadata(VapiGradleCommon):
   """
   vAPI Metadata toolkit gobuild module
   """
   def GetBuildProductNames(self):
      return {'name': 'vapi-core-metadata',
              'longname': self.__doc__.strip()}

   def GetComponentDependencies(self):
      comps = {}
      comps['vapi-core-idl'] = {
         'branch': specs.vapicoremetadata.VAPI_CORE_IDL_BRANCH,
         'change': specs.vapicoremetadata.VAPI_CORE_IDL_CLN,
         'buildtype': specs.vapicoremetadata.VAPI_CORE_IDL_BUILDTYPES[self.options.get('buildtype')],
         'files': specs.vapicoremetadata.VAPI_CORE_IDL_FILES,
      }
      return comps

   def GetRepositories(self, hosttype):
      repos = [
               helpers.target.PerforceRepo('vapi-core-metadata',
                                           'vapi-core/%(branch)/metadata-toolkit',
                                           'vapi-core/metadata-toolkit'),
               helpers.target.PerforceRepo('vapi-core-support-gradle',
                                           'vapi-core/%(branch)/support',
                                           'vapi-core/support'),
              ]
      return repos

   def GetCommands(self, hosttype):
      """
      Return a list of dictionaries representing commmands to execute
      to build this target.
      """
      srcroot = 'vapi-core/metadata-toolkit'
      return [{'desc': 'Compiling vAPI Metadata toolkit',
               'root': '',
               'log': 'metadata-toolkit.log',
               'command': self._GradleCommand(hosttype, srcroot, 'publish'),
               'env': self._GradleEnvironment(hosttype)
             }]


class VapiCppEmitter(VapiGradleCommon):
   """
   vAPI-C++ emitter - cpp-toolkit gobuild module
   """
   def GetBuildProductNames(self):
      return {'name': 'vapicpp-emitter',
              'longname': self.__doc__.strip()}

   def GetComponentDependencies(self):
      comps = {}
      comps['vapi-core-idl'] = {
         'branch': specs.vapicore.VAPI_CORE_IDL_BRANCH,
         'change': specs.vapicore.VAPI_CORE_IDL_CLN,
         'buildtype': specs.vapicore.VAPI_CORE_IDL_BUILDTYPES[self.options.get('buildtype')],
         'files': specs.vapicore.VAPI_CORE_IDL_FILES,
      }
      return comps

   def GetRepositories(self, hosttype):
      repos = [
               helpers.target.PerforceRepo('vapicpp-emitter',
                                           'vapi-core/%(branch)/cpp-toolkit',
                                           'vapi-core/cpp-toolkit'),
               helpers.target.PerforceRepo('vapicpp-emitter-support',
                                           'vapi-core/%(branch)/support',
                                           'vapi-core/support'),
              ]
      return repos

   def GetCommands(self, hosttype):
      """
      Return a list of dictionaries representing commmands to execute
      to build this target.
      """
      env = self._GradleEnvironment(hosttype)
      #TODO 1: flatten the gradle build to a single app build
      #TODO 2: relocate the cpp-generator under the vapicpp/src directory
      #env['SRCROOT'] = '../../..'
      #1 srcroot = 'vapi-core/cpp-toolkit/generator'
      #2 srcroot = 'vapi-core/vapicpp/src/generator'
      srcroot = 'vapi-core/cpp-toolkit'
      return [{'desc': 'Compiling vAPI-C++ emitter',
               'root': '',
               'log': 'cpp-toolkit.log',
               'command': self._GradleCommand(hosttype, srcroot, 'publish'),
               'env': env
             }]


class VapiCoreDotnet(VapiGradleCommon):
   """
   vAPI .NET toolkit gobuild module
   """
   def GetBuildProductNames(self):
      return {'name': 'vapi-core-dotnet',
              'longname': self.__doc__.strip()}

   def GetClusterRequirements(self):
      return ['windows-vs2012']

   def GetComponentDependencies(self):
      comps = {}
      comps['vapi-core-idl'] = {
         'branch': specs.vapicoredotnet.VAPI_CORE_IDL_BRANCH,
         'change': specs.vapicoredotnet.VAPI_CORE_IDL_CLN,
         'buildtype': specs.vapicoredotnet.VAPI_CORE_IDL_BUILDTYPES[
             self.options.get('buildtype')],
         'files': specs.vapicoredotnet.VAPI_CORE_IDL_FILES,
      }
      comps['vapi-core-java'] = {
         'branch': specs.vapicoredotnet.VAPI_CORE_JAVA_BRANCH,
         'change': specs.vapicoredotnet.VAPI_CORE_JAVA_CLN,
         'buildtype': specs.vapicoredotnet.VAPI_CORE_JAVA_BUILDTYPES[self.options.get('buildtype')],
         'files': specs.vapicoredotnet.VAPI_CORE_JAVA_FILES,
      }
      return comps

   def GetRepositories(self, hosttype):
      repos = [
               helpers.target.PerforceRepo('vapi-core-dotnet',
                                           'vapi-core/%(branch)/dotnet-toolkit',
                                           'vapi-core/dotnet-toolkit'),
               helpers.target.PerforceRepo('vapi-core-support-gradle',
                                           'vapi-core/%(branch)/support',
                                           'vapi-core/support'),
              ]
      return repos

   def GetCommands(self, hosttype):
      """
      Return a list of dictionaries representing commmands to execute
      to build this target.
      """
      srcroot = 'vapi-core/dotnet-toolkit'
      return [{'desc': 'Compiling vAPI .NET toolkit',
               'root': '',
               'log': 'dotnet-toolkit.log',
               'command': self._GradleCommand(hosttype, srcroot, 'publish'),
               'env': self._GradleEnvironment(hosttype)
             }]


class VapiCorePerl(VapiGradleCommon):
   """
   vAPI Perl toolkit gobuild module
   """
   def GetBuildProductNames(self):
      return {'name': 'vapi-core-perl',
              'longname': self.__doc__.strip()}

   def GetComponentDependencies(self):
      comps = {}
      comps['vapi-core-idl'] = {
         'branch': specs.vapicoreperl.VAPI_CORE_IDL_BRANCH,
         'change': specs.vapicoreperl.VAPI_CORE_IDL_CLN,
         'buildtype': specs.vapicoreperl.VAPI_CORE_IDL_BUILDTYPES[self.options.get('buildtype')],
         'files': specs.vapicoreperl.VAPI_CORE_IDL_FILES,
      }
      comps['vapi-core-java'] = {
         'branch': specs.vapicoreperl.VAPI_CORE_JAVA_BRANCH,
         'change': specs.vapicoreperl.VAPI_CORE_JAVA_CLN,
         'buildtype': specs.vapicoreperl.VAPI_CORE_JAVA_BUILDTYPES[self.options.get('buildtype')],
         'files': specs.vapicoreperl.VAPI_CORE_JAVA_FILES,
      }
      return comps

   def GetRepositories(self, hosttype):
      repos = [
               helpers.target.PerforceRepo('vapi-core-perl',
                                           'vapi-core/%(branch)/perl-toolkit',
                                           'vapi-core/perl-toolkit'),
               helpers.target.PerforceRepo('vapi-core-support-gradle',
                                           'vapi-core/%(branch)/support',
                                           'vapi-core/support'),
              ]
      return repos

   def GetCommands(self, hosttype):
      """
      Return a list of dictionaries representing commmands to execute
      to build this target.
      """
      srcroot = 'vapi-core/perl-toolkit'
      return [{'desc': 'Compiling vAPI Perl toolkit',
               'root': '',
               'log': 'perl-toolkit.log',
               'command': self._GradleCommand(hosttype, srcroot, 'publish'),
               'env': self._GradleEnvironment(hosttype)
             }]


class VapiCoreRuby(VapiGradleCommon):
   """
   vAPI Ruby toolkit gobuild module
   """
   def GetBuildProductNames(self):
      return {'name': 'vapi-core-ruby',
              'longname': self.__doc__.strip()}

   def GetComponentDependencies(self):
      comps = {}
      comps['vapi-core-idl'] = {
         'branch': specs.vapicoreruby.VAPI_CORE_IDL_BRANCH,
         'change': specs.vapicoreruby.VAPI_CORE_IDL_CLN,
         'buildtype': specs.vapicoreruby.VAPI_CORE_IDL_BUILDTYPES[self.options.get('buildtype')],
         'files': specs.vapicoreruby.VAPI_CORE_IDL_FILES,
      }
      comps['vapi-core-java'] = {
         'branch': specs.vapicoreruby.VAPI_CORE_JAVA_BRANCH,
         'change': specs.vapicoreruby.VAPI_CORE_JAVA_CLN,
         'buildtype': specs.vapicoreruby.VAPI_CORE_JAVA_BUILDTYPES[self.options.get('buildtype')],
         'files': specs.vapicoreruby.VAPI_CORE_JAVA_FILES,
      }
      return comps

   def GetRepositories(self, hosttype):
      repos = [
               helpers.target.PerforceRepo('vapi-core-ruby',
                                           'vapi-core/%(branch)/ruby-toolkit',
                                           'vapi-core/ruby-toolkit'),
               helpers.target.PerforceRepo('vapi-core-support-gradle',
                                           'vapi-core/%(branch)/support',
                                           'vapi-core/support'),
              ]
      return repos

   def GetCommands(self, hosttype):
      """
      Return a list of dictionaries representing commmands to execute
      to build this target.
      """
      env = self._GradleEnvironment(hosttype)
      env['GEM_HOME'] = '%(buildroot)/gem-home'
      env['LC_ALL'] = 'en_US.UTF-8'
      srcroot = 'vapi-core/ruby-toolkit'
      return [{'desc': 'Compiling vAPI Ruby toolkit',
               'root': '',
               'log': 'ruby-toolkit.log',
               'command': self._GradleCommand(hosttype, srcroot, 'publish'),
               'env': env
             }]

