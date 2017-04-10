# Copyright 2008 VMware, Inc.  All rights reserved. -- VMware Confidential

"""
Helper for ant-based targets which want to invoke ant through the
org.apache.tools.ant.launch.Launcher class instead through the script file
(this allows for choosing specific JDK version).
"""

import os

import helpers.env

class AntLauncherHelper:

   ANT_LAUNCHER_CLASS = 'org.apache.tools.ant.launch.Launcher'

   def _GetEnvironment(self, hosttype, antversion, jdkversion):

      env = helpers.env.SafeEnvironment(hosttype)

      path = []
      if hosttype.startswith('windows'):
         tcroot = os.environ.get("TCROOT", "C:/TCROOT-not-set")
         tcroot_os_prefix = 'win64'
      elif hosttype.startswith('linux'):
         tcroot = '/build/toolchain'
         tcroot_os_prefix = 'lin64'
         path += ['/bin']
      else:
         raise ValueError('AntLauncherHelper does not support hosttype %s' % hosttype)

      env['TCROOT'] = tcroot

      env['JAVA_HOME'] = os.path.join(tcroot, tcroot_os_prefix,
                                      'jdk-' + jdkversion)

      env['ANT_HOME'] = os.path.join(tcroot, 'noarch',
                                     'apache-ant-' + antversion)

      path += [env['PATH']]
      env['PATH'] = os.pathsep.join(path)

      return env

   def _Command(self, hosttype, ant_home, java_home,
                target='build', buildfile='build.xml', **flags):

      q = lambda s: '"%s"' % s

      defaults = {
         'GOBUILD_OFFICIAL_BUILD' :       '1',
         'GOBUILD_AUTO_COMPONENTS':       'false',
         'OBJDIR'                 :       q('%(buildtype)'),
         'RELTYPE'                :       q('%(releasetype)'),
         'BUILD_NUMBER'           :       q('%(buildnumber)'),
         'PRODUCT_BUILD_NUMBER'   :       q('%(productbuildnumber)'),
         'CHANGE_NUMBER'          :       q('%(changenumber)'),
         'BRANCH_NAME'            :       q('%(branch)'),
         'PUBLISH_DIR'            :       q('%(buildroot)/publish'),
         'REMOTE_COPY_SCRIPT'     :       q('%(gobuildc) %(buildid)'),
      }

      # Add a GOBUILD_*_ROOT flag for every component we depend on.
      if hasattr(self, 'GetComponentDependencies'):
         for dependency in self.GetComponentDependencies():
            dependency = dependency.replace('-', '_')
            defaults['GOBUILD_%s_ROOT' % dependency.upper()] = \
                  '%%(gobuild_component_%s_root)' % dependency


      # Override the defaults above with the options passed in by
      # the client.
      defaults.update(flags)

      java_exe = os.path.join(java_home, 'bin', 'java')
      if hosttype.startswith('windows'):
         java_exe += ".exe"

      ant_launcher_jar = os.path.join(ant_home, 'lib', 'ant-launcher.jar')

      command = [java_exe,
                 '-Dant.home=%s' % ant_home,
                 '-classpath', ant_launcher_jar,
                 self.ANT_LAUNCHER_CLASS, '-v',
                 '-f', buildfile, target]

      # Add all flags as properties
      for k in sorted(defaults.keys()):
         v = defaults[k]
         param = ' -D' + str(k)
         if v is not None:
            param += '=' + str(v)
         command.append(param)

      return ' '.join(command)
