# Copyright 2014 VMware, Inc.  All rights reserved. -- VMware Confidential

"""
NSX - provider gobuild module.
"""
import os

import helpers.maven
import helpers.target
import specs.nsxprovider



class NSXProvider(helpers.target.Target, helpers.maven.MavenHelper):
    """
    JAVA properties
    """

    _mavenVersion = '3.0.3'   # 'apache-maven-3.0.3'
    _jdkVersion = 'jdk-1.7.0_72'
    _jdkMacVersion = 'java-osx-10.4-release6'

    def _JavaEnvironment(self, hosttype):
       """
       Return an environment dictionary suitable for use in running Java
       commands in the nsxprovider tree.
       """

       env = {}
       if hosttype.startswith('linux'):
          for k in (
             'DBC_ENV',
             'TERM',
          ):
             if os.environ.has_key(k):
                env[k] = os.environ[k]

       # Add build environment
       env['BUILDTYPE'] = '%(buildtype)'
       env['BUILD_NUMBER'] = '%(buildnumber)'
       env['PRODUCT_BUILD_NUMBER'] = '%(productbuildnumber)'
       env['CHANGE_NUMBER'] = '%(changenumber)'
       env['BRANCH_NAME'] = '%(branch)'
       env['RELEASETYPE'] = '%(releasetype)'
       env['GOBUILD_TARGET'] = self.GetBuildProductNames()['name']

       # Configure the Java home directory
       tcroot = self._GetTcroot(hosttype)
       env['TCROOT'] = tcroot

       if hosttype.startswith('windows'):
          env['JAVA_HOME'] = os.path.join(tcroot, 'win32', self._jdkVersion)
       elif hosttype.startswith('linux'):
          env['JAVA_HOME'] = os.path.join(tcroot, 'lin32', self._jdkVersion)
       elif hosttype.startswith('macosx'):
          env['JAVA_HOME'] = os.path.join(tcroot, 'mac32', self._jdkMacVersion, 'usr')
       else:
          raise helpers.target.GobuildTargetException("Unknown hosttype %s" % hosttype)

       return env

    """
    Maven properties
    """
    def _MavenEnvironment(self, hosttype):
       """
       Return an environment dictionary suitable for use in running a Maven
       command in the nsxprovider tree.
       """
       return self._JavaEnvironment(hosttype)

    def _MavenCommand(self, hosttype, srcroot, targets):

       def q(str):
          return  '"' + str + '"'

       options = {
          '--file': q(os.path.join(srcroot, 'pom.xml')),
          '--global-settings': q(os.path.join(srcroot, 'settings.xml')),
          '--errors': None,
          '--batch-mode': None,
       }

       sysproperties = {
          'maven.repo.local': q('%(buildroot)/build/m2-repository'),
          'skipTests': 'true',
          'srcroot': q('%%(buildroot)/%s' % srcroot),
          'buildroot': q('%(buildroot)/build'),
          'gobuildroot': q('%(buildroot)/build'),
          'OBJDIR': '%(buildtype)',
          'GOBUILD_AUTO_COMPONENTS': 'false',
          'GOBUILD_TARGET': 'nsxprovider',
          'build.publish': q('%(buildroot)/publish'),
          }

       # helpers.maven.MavenHelper._Command builds the mvn cmd line, it also passes more
       # sysproperties, e.g. the gobuild comp roots like GOBUILD_VAPI_CORE_IDL_ROOT
       return self._Command(hosttype, targets,  self._mavenVersion, options, **sysproperties)

    def _GetTcroot(self, hosttype):
       if os.environ.has_key('TCROOT'):
          tcroot = os.environ['TCROOT']
       elif hosttype.startswith('windows'):
          tcroot = 'T:\\'
       else:
          tcroot = '/build/toolchain'
       return tcroot

    """
    Zip folder
    """
    def _Zip(self, hosttype, source, target, excludes=None):
       tcroot = self._GetTcroot(hosttype)

       if hosttype.startswith('linux'):
          zipbin = os.path.join(tcroot, 'lin32', 'zip-2.32', 'bin', 'zip')
          zipcmd = '%s -y -o -r %s .'
       elif hosttype.startswith('windows'):
          zipbin = os.path.join(tcroot, 'win32', 'zip-2.32', 'zip.exe')
          zipcmd = '%s -o -r %s .'
       else:
          raise Exception('unsupported hosttype %s' % hosttype)

       if excludes:
          zipcmd += ' -x %s' % excludes

       return {'desc': 'Zipping %s to %s' % (source, target),
               'root': source,
               'log': '%s.log' % os.path.basename(target),
               'command': zipcmd % (zipbin, target),
               'env': helpers.env.SafeEnvironment(hosttype),
               }

    """
    Target methods
    """
    def GetBuildProductNames(self):
        return {'name': 'nsx-vapi-provider',
                'longname': 'NSX vAPI Integration'}

    def GetComponentDependencies(self):
        comps = {}
        comps['vapi-core'] = {
            'branch': specs.nsxprovider.VAPI_CORE_BRANCH,
            'change': specs.nsxprovider.VAPI_CORE_CLN,
            'buildtype': specs.nsxprovider.VAPI_CORE_BUILDTYPES[self.options.get('buildtype')],
            'files': specs.nsxprovider.VAPI_CORE_FILES,
        }
        comps['dclicore'] = {
            'branch': specs.nsxprovider.DCLI_CORE_BRANCH,
            'change': specs.nsxprovider.DCLI_CORE_CLN,
            'buildtype': specs.nsxprovider.DCLI_CORE_BUILDTYPES[self.options.get('buildtype')],
            'files': specs.nsxprovider.DCLI_CORE_FILES,
        }
        return comps

    def GetComponentDependencyAliases(self):
        return self.GetComponentDependencies().keys()

    def GetClusterRequirements(self):
        """
        Return a list of strings representing required host types.
        ToDo: Windows support needs to be added?
        """
        return ['linux64' ]

    def GetRepositories(self, hosttype):
      repos = [
               helpers.target.PerforceRepo('nsx-parent',
                                           'vapi-core/%(branch)/demo/nsx-parent',
                                           'vapi-core/demo/nsx-parent'),
               helpers.target.PerforceRepo('nsx-gobuild',
                                           'vapi-core/%(branch)/demo/nsx-gobuild',
                                           'vapi-core/demo/nsx-gobuild'),
               helpers.target.PerforceRepo('nsx-yaml-converter',
                                           'vapi-core/%(branch)/demo/nsx-yaml-converter',
                                           'vapi-core/demo/nsx-yaml-converter'),
               helpers.target.PerforceRepo('nsx-provider',
                                           'vapi-core/%(branch)/demo/nsx-provider',
                                           'vapi-core/demo/nsx-provider'),
               helpers.target.PerforceRepo('nsx-integration-tests',
                                           'vapi-core/%(branch)/demo/nsx-integration-tests',
                                           'vapi-core/demo/nsx-integration-tests'),
               helpers.target.PerforceRepo('nsx-sdk',
                                           'vapi-core/%(branch)/demo/nsx-sdk',
                                           'vapi-core/demo/nsx-sdk'),
               helpers.target.PerforceRepo('vapi-core-support',
                                           'vapi-core/%(branch)/support',
                                           'vapi-core/support'),
              ]
      return repos

    def GetCommands(self, hosttype):
        mvnSrcroot = 'vapi-core/demo/nsx-parent'
        commands = [{'desc': 'Compiling NSX vAPI Integration',
               'root': '',
               'log': 'nsxprovider-maven.log',
               'command': self._MavenCommand(hosttype, mvnSrcroot, ['install']),
               'env': self._MavenEnvironment(hosttype)
             }]
        commands.append(self._Zip(hosttype,
                                '%(buildroot)/build/nsx-parent/vmodl',
                                '%(buildroot)/publish/vmodl.zip',
                                None))

        return commands

    def GetStorageInfo(self, hosttype):
      storinfo = []
      if hosttype.startswith('linux'):
         storinfo += [{'type': 'source', 'src': 'vapi-core'}]
      storinfo += [{'type': 'build', 'src': 'build'}]
      return storinfo
