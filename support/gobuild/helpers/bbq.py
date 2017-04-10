# Copyright 2013, 2015 VMware, Inc.  All rights reserved. -- VMware Confidential

"""
Helpers for Python-based targets.
"""

import os
import platform

def GetTCArch():
   sys_map = {'Linux' : 'lin32', 'Windows' : 'win32', 'Darwin' : 'mac32' }
   return sys_map[platform.system()]


class BBQHelper:
   """
   Helper class for targets that build with BBQ manifest system.
   """
   def _ManifestBBQCommand(self, hosttype, product,
                           target='install',
                           bbqcmd='bbq/bbq',
                           srcdir='.',
                           bod='./BOD',
                           arch='x64',
                           options={}):
      """
      Returns a string that can be used to execute the manifest build
      system on the provided product.

      It is only possible to build a whole product using gobuild and
      the manifest system.  To build portions of a product, use the
      manifest system directly.
      """
      arguments = {
         'BUILD_NUMBER': '"%(buildnumber)"',
         'PRODUCT_BUILD_NUMBER': '"%(productbuildnumber)"',
         'CHANGE_NUMBER': '"%(changenumber)"',
         'BRANCH_NAME': '"%(branch)"',
         'RELEASETYPE': '"%(releasetype)"',
         'GOBUILD_AUTO_COMPONENTS': '0',
         'PUBLISH_DIR': '%(buildroot)/publish',
      }

      # the provided options override the defaults
      arguments.update(options)

      # Handle verbosity
      if 'VERBOSE' not in arguments:
         arguments['VERBOSE'] = True

      if self.options.has_key('numcpus'):
         arguments['NUM_CPU'] = self.options['numcpus']

      for d in self.GetComponentDependencies():
         d_no_hyphens = d.replace('-', '_')
         root_key = 'GOBUILD_%s_ROOT' % d_no_hyphens.upper()
         root_value = '%%(gobuild_component_%s_root)' % d_no_hyphens
         arguments[root_key] = root_value

      tcroot = os.environ.get('TCROOT', '/build/toolchain')
      bash =  os.path.join(tcroot, 'lin32', 'bash-4.1', 'bin', 'bash')

      assert(hosttype.startswith("linux"))
      cmd = [bash, bbqcmd,
             "--source %s" % srcdir,
             "--build-output-directory %s" % bod,
             "--obp verbose",
             "--arch %s" % arch,
             "--build %(buildtype) ",
             "--product %s" % product,
             "--",
             "%s" % target,
            ]
      cmd.extend(['%s=%s' % (k, v) for k, v in arguments.iteritems()])

      return ' '.join(cmd)


   def _GetStoreSourceRule(self, hosttype, tree):
      """
      Return the standard storage rules for a python based build.  The
      Linux side is responsible for copying the source files to storage.
      """
      return [ {'type' : 'source',
                'src'  : '%s/' % tree
                } ]


   def _GetStoreBuildRule(self, hosttype, tree):
      """
      Return the standard storage rules for a python based build.  The
      Linux side is responsible for copying the source files to storage.
      """
      return [ { 'type' : 'build',
                 'src'  : 'build'
               } ]
