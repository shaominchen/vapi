# Copyright 2014 VMware, Inc.  All rights reserved. -- VMware Confidential

import os

import helpers.env
import helpers.target
from helpers.util import MkdirCommand, UnzipCommand
from ConfigParser import ConfigParser
from StringIO import StringIO


class VapiCoreCommon(helpers.target.Target):
   """
   vapi-core Product Module base class --

   Things which depend on the classical vapi-core build should drive from
   this class.  It does many things for you so you don't have to do
   them yourself:

   1) Implements the minimal GetRepositories for compiling stuff out
      of vapi-core.

   2) Adds all the flags to the make command line for doing cross-platform
      builds, as well as the standard flags we're all acustomed  to
      (e.g. MAKE_RELEASE, BUILD_NUMBER, OBJDIR, etc.)
   """

   @staticmethod
   def readVersionFromFile(pathToVersionPropertiesFile):
      config = ConfigParser()
      f = open(pathToVersionPropertiesFile)
      iniString = StringIO('[config]\n' + f.read())
      config.readfp(iniString)
      major = config.get('config','versionMajor')
      minor = config.get('config','versionMinor')
      patch = config.get('config','versionPatch')
      return '%s.%s.%s' % (major, minor, patch)

   def _MakeEnvironment(self, hosttype, branch = '%(branch)'):
      """
      Return an environment dictionary suitable for use in running a make
      command in the vapi-core tree.  We need to adjust our path to only include
      %TCROOT%/win32/cygwin-1.5.19-4/bin, %TCROOT%/win32/coreutils-5.3.0/bin,
      and the system directory for Windows.
      """
      env = os.environ.copy()
      if hosttype.startswith('windows'):
         path = []
         if env.get('TCROOT'):
            path += [r'%s\win32\cygwin-1.5.19-4\bin' % env['TCROOT'],
                     r'%s\win32\coreutils-5.3.0\bin' % env['TCROOT']]
         if env.get('SYSTEMROOT'):
            path += [ os.path.join(env['SYSTEMROOT'], 'system32'),
                      env['SYSTEMROOT']]
         env['PATH'] = ';'.join(path)

      return env


   def _MakeCommand(self, hosttype, target, **options):
      """
      Return a dictionary representing a command to invoke make in vapi-core
      with the standard flags required for it to succeed.  There are a lot
      of them (sigh).
      """

      def q(str):
         return  '"' + str + '"'

      defaults = {
         'GOBUILD_AUTO_COMPONENTS': '',

         'SCAN_FOR_VIRUSES'       : '1',
         'COPY_RELEASE_BINARIES'  : '1',
         'SIGN_RELEASE_BINARIES'  : '1',
         'SHARED_BUILD_MACHINE'   : '1',

         'WDK_USE_PREFAST'        : '1',
         'LD_RELEASE'             : '1',

         'OBJDIR'                 : q('%(buildtype)'),
         'BUILD_NUMBER'           : q('%(buildnumber)'),
         'BUILDKIND_FOR_VERSION_TAG_ONLY' : q('%(buildkind_for_version_tag_only)'),
         'PRODUCT_BUILD_NUMBER'   : q('%(productbuildnumber)'),
         'CHANGE_NUMBER'          : q('%(changenumber)'),
         'BRANCH_NAME'            : q('%(branch)'),

         'RELEASE_BINARIES'       : q('%(buildroot)/publish'),
         'RELEASE_PACKAGES'       : q('%(buildroot)/publish'),
         'PUBLISH_DIR'            : q('%(buildroot)/publish'),
         'BUILDLOG_DIR'           : q('%(logdir)'),
      }

      # Override the defaults above with the options passed in by
      # the client
      defaults.update(options)

      # If the user has overridden the number of cpus to use for this
      # build, stick that on the command line, too.
      if self.options.has_key('numcpus'):
         self.log.debug('Overriding num cpus (%s)' % self.options['numcpus'])
         defaults['NUM_CPU'] = self.options['numcpus']

      # Handle verbosity
      if self.options.get('verbose') and 'VERBOSE' not in defaults:
         defaults['VERBOSE'] = 2

      # Handle officialkey
      if self.options.has_key('officialkey'):
         if self.options.get('officialkey'):
            self.log.debug("Build will use official key")
            defaults['OFFICIALKEY'] = '1'
         else:
            self.log.debug("Build will not use official key")
            defaults['OFFICIALKEY'] = ''

      # Override the virus scanner, if passed in by the user.
      # This should really be fixed in the Makefiles.
      # Under discussion to rip out later.
      if hosttype.startswith('windows'):
         if self.options.has_key('virusscanner'):
            self.log.debug('Overriding VIRUS_SCAN in Makefiles with VIRUS_SCAN=%s' % \
               self.options['virusscanner'])
            defaults['VIRUS_SCAN'] = q(self.options['virusscanner'])

      # Create the command line to invoke make
      make = None
      if hosttype.startswith('windows'):
         if os.environ.has_key('TCROOT'):
            make = '%s/win32/make-3.81/make.exe' % os.environ['TCROOT']
      elif hosttype.startswith('linux'):
         make = '/build/toolchain/lin32/make-3.81/bin/make'
      elif hosttype.startswith('macosx'):
         make = '/build/toolchain/mac32/make-3.81/bin/make'
      else:
         raise helpers.target.GobuildTargetException("Unknown hosttype %s" % hosttype)
      cmd = '%s %s ' % (make, target)
      cmd += self.GetComponentMakeFlags()
      for k in sorted(defaults.keys()):
         v = defaults[k]
         cmd += ' ' + str(k)
         if v is not None:
            cmd += '=' + str(v)

      return cmd

   def _MakeDevCommand(self, hosttype, target, **options):
      """
      Return a dictionary representing a command to invoke make in vapi-core
      with the standard flags required for it to succeed.  There are a lot
      of them (sigh).
      """

      def q(str):
         return  '"' + str + '"'

      defaults = {
         'OBJDIR'                 : q('%(buildtype)'),
         'BUILD_NUMBER'           : q('%(buildnumber)'),
         'BUILDKIND_FOR_VERSION_TAG_ONLY' : q('%(buildkind_for_version_tag_only)'),
         'PRODUCT_BUILD_NUMBER'   : q('%(productbuildnumber)'),
         'CHANGE_NUMBER'          : q('%(changenumber)'),
         'BRANCH_NAME'            : q('%(branch)'),
         'GOBUILD_AUTO_COMPONENTS_REQUEST': '1',
         'GOBUILD_AUTO_COMPONENTS_WAIT' : '1',
         'SHARED_BUILD_MACHINE'   : '1',
         'PUBLISH_DIR'            : q('%(buildroot)/publish'),
         'BUILDLOG_DIR'           : q('%(logdir)'),
      }

      # Override the defaults above with the options passed in by
      # the client
      defaults.update(options)

      # If the user has overridden the number of cpus to use for this
      # build, stick that on the command line, too.
      if self.options.has_key('numcpus'):
         self.log.debug('Overriding num cpus (%s)' % self.options['numcpus'])
         defaults['NUM_CPU'] = self.options['numcpus']

      # Handle verbosity
      if self.options.get('verbose') and 'VERBOSE' not in defaults:
         defaults['VERBOSE'] = 2

      # Create the command line to invoke make
      make = None
      if hosttype.startswith('windows'):
         if os.environ.has_key('TCROOT'):
            make = '%s/win32/make-3.81/make.exe' % os.environ['TCROOT']
      elif hosttype.startswith('linux'):
         make = '/build/toolchain/lin32/make-3.81/bin/make'
      elif hosttype.startswith('macosx'):
         make = '/build/toolchain/mac32/make-3.81/bin/make'
      else:
         raise helpers.target.GobuildTargetException("Unknown hosttype %s" % hosttype)
      cmd = '%s %s ' % (make, target)
      for k in sorted(defaults.keys()):
         v = defaults[k]
         cmd += ' ' + str(k)
         if v is not None:
            cmd += '=' + str(v)

      return cmd


   def _GetMakeStorageInfo(self, hosttype, tree):
      """
      Return the standard storage rules for a make based build.  The
      Linux side is responsible for copying the source files to storage.
      """
      sourcerule = { 'type' : 'source',
                     'src'  : tree,
                   }

      buildrule = { 'type' : 'build',
                    'src'  : '%s/build' % tree,
                  }

      if hosttype.startswith('windows'):
         return [ buildrule ]

      return [ sourcerule, buildrule ]


   def _SconsEnvironment(self, hosttype):
      """
      Return an environment dictionary suitable for use in running a scons
      command in the vapi-core tree.
      """
      env = {}
      if hosttype.startswith('linux'):
         for k in (
            'DBC_ENV',
            'TERM',
         ):
            if os.environ.has_key(k):
               env[k] = os.environ[k]

      if hosttype.startswith('windows'):
         for k in (
            'BUILDAPPS',
            'HOME',
            'HOMEPATH',
            'NUMBER_OF_PROCESSORS',
            'OS',
            'SYSTEM',
            'SYSTEMDRIVE',
            'TCROOT',
            'USERNAME',
            'SYSTEMROOT',
         ):
            if os.environ.has_key(k):
               env[k] = os.environ[k]

         systemRoot = os.environ.get('SYSTEMROOT', 'C:\\Windows')
         env['PATH'] = ';'.join([os.path.join(systemRoot, 'system32'),
                                 systemRoot])

      return env


   def _SconsCommand(self, hosttype, target, **options):
      """
      Return a dictionary representing a command to invoke scons in vapi-core
      with the standard flags required for it to succeed.
      """

      def q(str):
         return  '"' + str + '"'

      defaults = {
         'BUILDTYPE'              : q('%(buildtype)'),
         'BUILD_NUMBER'           : q('%(buildnumber)'),
         'BUILDKIND_FOR_VERSION_TAG_ONLY' : q('%(buildkind_for_version_tag_only)'),
         'PRODUCT_BUILD_NUMBER'   : q('%(productbuildnumber)'),
         'CHANGE_NUMBER'          : q('%(changenumber)'),
         'BRANCH_NAME'            : q('%(branch)'),
         'RELEASE_PACKAGES_DIR'   : q('%(buildroot)/publish'),
         'BUILDLOG_DIR'           : q('%(logdir)'),
         'GOBUILD_AUTO_COMPONENTS': 'False',
         'RELEASETYPE'            : q('%(releasetype)'),
      }

      # Override the defaults above with the options passed in by
      # the client
      defaults.update(options)

      if self.options.get('numcpus'):
         defaults['NUM_CPU'] = self.options['numcpus']

      if hosttype == 'linux64' and self.options.get('distcc-dir'):
         defaults['DISTCC_DIR'] = self.options['distcc-dir']
         if self.options.get('distcc-numcpus'):
            defaults['NUM_CPU'] = self.options['distcc-numcpus']

      # Handle verbosity
      if self.options.get('verbose') and 'VERBOSE' not in defaults:
         defaults['VERBOSE'] = True

      # Create the command line to invoke scons
      cmd = os.path.join('scons', 'bin', 'scons')
      cmd += ' %s ' % target
      cmd += self.GetComponentSconsFlags()

      for k in sorted(defaults.keys()):
         v = defaults[k]
         cmd += ' ' + str(k)
         if v is not None:
            cmd += '=' + str(v)
      cmd  += ' --debug=stacktrace'
      return cmd

   def _SconsDevCommand(self, hosttype, target, **options):
      """
      Return a dictionary representing a command to invoke scons in vapi-core
      with the standard flags required for it to succeed.
      """

      def q(str):
         return  '"' + str + '"'

      defaults = {
         'BUILDTYPE'              : q('%(buildtype)'),
         'BUILD_NUMBER'           : q('%(buildnumber)'),
         'BUILDKIND_FOR_VERSION_TAG_ONLY' : q('%(buildkind_for_version_tag_only)'),
         'CHANGE_NUMBER'          : q('%(changenumber)'),
         'BRANCH_NAME'            : q('%(branch)'),
         'RELEASE_PACKAGES_DIR'   : q('%(buildroot)/publish'),
         'BUILDLOG_DIR'           : q('%(logdir)'),
         'GOBUILD_AUTO_COMPONENTS_REQUEST': 'True',
         'GOBUILD_AUTO_COMPONENTS_WAIT': 'True',
         'RELEASETYPE'            : q('%(releasetype)'),
      }

      # Override the defaults above with the options passed in by
      # the client
      defaults.update(options)

      if self.options.get('numcpus'):
         defaults['NUM_CPU'] = self.options['numcpus']

      if hosttype == 'linux64' and self.options.get('distcc-dir'):
         defaults['DISTCC_DIR'] = self.options['distcc-dir']
         if self.options.get('distcc-numcpus'):
            defaults['NUM_CPU'] = self.options['distcc-numcpus']

      # Handle verbosity
      if self.options.get('verbose') and 'VERBOSE' not in defaults:
         defaults['VERBOSE'] = True

      # Create the command line to invoke scons
      cmd = os.path.join('scons', 'bin', 'scons')
      cmd += ' %s ' % target

      for k in sorted(defaults.keys()):
         v = defaults[k]
         cmd += ' ' + str(k)
         if v is not None:
            cmd += '=' + str(v)

      cmd += ' --debug=stacktrace'
      return cmd

   def _GetSconsStorageInfo(self, hosttype, tree):
      """
      Return the standard storage rules for a scons based build.  The
      Linux side is responsible for copying the source files to storage.
      """
      sourcerule = {'type' : 'source',
                    'src'  : tree,
                   }
      buildrule = { 'type' : 'build',
                    'src'  : '%s/build' % tree,
                  }

      if hosttype.startswith('linux'):
         return [ sourcerule, buildrule ]

      return [ buildrule ]

   def _Zip(self, hosttype, source, target, excludes=None, folderList=None):
      tcroot = self._GetTcroot(hosttype)

      if hosttype.startswith('linux'):
         zipbin = os.path.join(tcroot, 'lin32', 'zip-2.32', 'bin', 'zip')
         zipcmd = '%s -y -o -r %s'
      elif hosttype.startswith('windows'):
         zipbin = os.path.join(tcroot, 'win32', 'zip-2.32', 'zip.exe')
         zipcmd = '%s -o -r %s'
      else:
         raise Exception('unsupported hosttype %s' % hosttype)

      folderListAsStr = self._folderListToSrting(folderList)
      zipcmd += folderListAsStr

      if excludes:
         zipcmd += ' -x "%s"' % excludes

      return {'desc': 'Zipping %s to %s' % (source, target),
              'root': source,
              'log': '%s.log' % os.path.basename(target),
              'command': zipcmd % (zipbin, target),
              'env': helpers.env.SafeEnvironment(hosttype),
              }

   def _folderListToSrting(self, folderList):
      folderListAsStr = ''
      if folderList is None:
         folderList = ['.']
      for folder in folderList:
         folderListAsStr += ' %s' % folder
      return folderListAsStr

   def _GetTcroot(self, hosttype):
      if os.environ.has_key('TCROOT'):
         tcroot = os.environ['TCROOT']
      elif hosttype.startswith('windows'):
         tcroot = 'T:\\'
      else:
         tcroot = '/build/toolchain'
      return tcroot

   def _TarGz(self, hosttype, source, target, excludes=None, folderList=None):
      tcroot = self._GetTcroot(hosttype)
      folderListAsStr = self._folderListToSrting(folderList)

      if hosttype.startswith('linux'):
         tarbin = os.path.join(tcroot, 'lin32', 'tar-1.23', 'bin', 'tar')
         gzipbin = os.path.join(tcroot, 'lin32', 'gzip-1.3.5', 'bin', 'gzip')
         excludesString = '--exclude="%s"' % excludes if excludes else ''
         tarcmd = '%s cvf - %s %s | %s > %s' % (tarbin, folderListAsStr, excludesString, gzipbin, target)
      elif hosttype.startswith('windows'):
         sevenZipBin = os.path.join(tcroot, 'win32', '7zip-9.20', '7za.exe')
         excludesString = ' -x!"%s"' % excludes if excludes else ''
         tarcmd = '%s a -ttar -so tmp-archive.tar %s %s | %s a -si %s' % (sevenZipBin, folderListAsStr, excludesString, sevenZipBin, target)
      else:
         raise Exception('unsupported hosttype %s' % hosttype)

      return {'desc': 'Tarring %s to %s' % (source, target),
              'root': source,
              'log': '%s.log' % os.path.basename(target),
              'command': tarcmd,
              'env': helpers.env.SafeEnvironment(hosttype),
              }

   def _CopySourceContent(self, hosttype, source, target):
      tcroot = self._GetTcroot(hosttype)
      if hosttype.startswith('linux'):
         copybin = os.path.join(tcroot, 'lin32', 'coreutils-5.97', 'bin', 'cp')
      elif hosttype.startswith('windows'):
         copybin = os.path.join(tcroot, 'win32', 'coreutils-5.3.0', 'bin', 'cp.exe')
      else:
         raise Exception('unsupported hosttype %s' % hosttype)
      copycmd = '%s -r %s/* %s'

      return {'desc': 'Copying %s to %s' % (source, target),
              'root': source,
              'log': '%s.log' % os.path.basename(target),
              'command': copycmd % (copybin, source, target),
              'env': helpers.env.SafeEnvironment(hosttype),
              }

   def _Transfer(self, hosttype, source, target):
      gotransfer = '%%(gobuildc) %%(buildid) %s gobuildc/registry'

      options = {
         'CPANDCS' : gotransfer % 'cpandcs',
         'GOBUILDC_REGISTRY_DIR' : 'gobuildc/registry',
         'GOBUILDC_PERSISTENT_DIR' : 'gobuildc/' + hosttype,
         }

      # adjust
      transferZip = os.path.join('%(buildroot)',
                                 options['GOBUILDC_PERSISTENT_DIR'],
                                 target + '.zip')

      return [MkdirCommand(hosttype, options['GOBUILDC_PERSISTENT_DIR']),
              self._Zip(hosttype, source, transferZip),
              {'desc': 'Transferring %s' % target,
               'root': '',
               'log': 'transfer.log',
               'command': ' '.join((options['CPANDCS'],
                                    transferZip,
                                    options['GOBUILDC_REGISTRY_DIR'])),
               'env': helpers.env.SafeEnvironment(hosttype),
               }]

   def _AcceptTransfer(self, hosttype, source, target):
      gotransfer = '%%(gobuildc) %%(buildid) %s gobuildc/registry'

      options = {
         'CPANDWAIT' : gotransfer % 'cpandwait',
         'GOBUILDC_REGISTRY_DIR' : 'gobuildc/registry',
         'GOBUILDC_PERSISTENT_DIR' : 'gobuildc/' + hosttype,
         }

      # adjust
      transferZip = source + '.zip'

      command = ' '.join((options['CPANDWAIT'],
                          options['GOBUILDC_REGISTRY_DIR'] + '/' +  transferZip,
                          options['GOBUILDC_PERSISTENT_DIR'] + '/' + transferZip))

      return [MkdirCommand(hosttype, options['GOBUILDC_PERSISTENT_DIR']),
              {'desc': 'Transferring %s' % source,
               'root': '',
               'log': 'transfer.log',
               'command': command,
               'env': helpers.env.SafeEnvironment(hosttype),
               },
              MkdirCommand(hosttype, 'component'),
              UnzipCommand(hosttype,
                           os.path.join('%(buildroot)',
                                        options['GOBUILDC_PERSISTENT_DIR'],
                                        transferZip),
                           target)]
