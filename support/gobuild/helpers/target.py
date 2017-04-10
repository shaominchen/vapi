# Copyright 2007 VMware, Inc.  All rights reserved. -- VMware Confidential
#
# Target Module --
#
# Contains helper classes for writing your target.  See the class
# comment for more information

import helpers


def PerforceRepo(name, src, dst):
   if src.startswith('//'):
      raise GobuildTargetException(
         'Source repository cannot be an absolute depot path')
   return {
      'rcs' : 'perforce',
      'name': name,
      'src' : '%s%s' % (helpers.GetPrefix(), src),
      'dst' : dst
   }


class Target(object):
   """
   Target Class

   Basic target.  Used to ensapsulate a reusuable build command.
   """

   GOBUILD_INTERFACE_VERSION = "1.0"

   def SetOptions(self, options):
      """
      Hold onto the log and all of our module specific options for
      later use.
      """
      self.log     = options['gobuild-log']
      self.options = options

   def GetComponentMakeFlags(self):
      """
      Get additional flags that we recommend you pass into make if you are 
      consuming additional components.  For each component, we define
      a GOBUILD_(COMPONENT_NAME)_ROOT flag which points to where the 
      component will live on disk.  You can then reference this flag in
      your makefile to access the bits.

      Sample Usage:
          # build the command to execute
          cmd = 'make OBJDIR=obj ' + self.GetComponentMakeFlags()
      """
      flags = {}
      new_flag_format = []
      if hasattr(self, 'GetComponentDependencies'):
         for d in self.GetComponentDependencies():
            d = d.replace('-', '_')
            flags['GOBUILD_%s_ROOT' % d.upper()] = '%%(gobuild_component_%s_root)' % d
      return ' '.join(['%s="%s"' % (k, v) for k,v in flags.iteritems()])

   def GetComponentSconsFlags(self):
      """
      Get additional flags that we recommend you pass into make if you
      are consuming components. Scons uses a GOBUILD_COMPONENTS flag
      that contains the path to each component, e.g.,

         GOBUILD_COMPONENTS=c1=c1-root,c2=c2-root,...

      Sample Usage:
          # build the command to execute
          cmd = 'scons BUILDTYPE=obj ' + self.GetComponentSconsFlags()
      """
      flags = {}
      new_flag_format = []
      if hasattr(self, 'GetComponentDependencies'):
         components = self.GetComponentDependencies()
         for component, info in components.iteritems():
            if isinstance(info, list):
               for alias_info in info:
                  alias = alias_info.get('alias', component)
                  alias = alias.replace('-', '_')

                  new_flag_format += ['%s=%%(gobuild_component_%s_root)'
                                      % (alias.upper(), alias)]
            else:
               component = component.replace('-', '_')
               new_flag_format += ['%s=%%(gobuild_component_%s_root)'
                                   % (component.upper(), component)]

      flags['GOBUILD_COMPONENTS'] = ','.join(new_flag_format)
      return ' '.join(['%s="%s"' % (k, v) for k,v in flags.iteritems()])

class CompoundTarget(Target):
   """
   CompoundTarget Class

   Bundles many targets together into a single target.  Used in
   complex build scenarios where many steps used from other builds
   are required.  For example, both Workstation and Server require
   building the tools in the exact same way.  This is done by
   making a Tools target (derived from Target) and having the 
   Workstation and Server targets include it by deriving from
   CompoundTarget.

   CompoundTarget's need simply define a GetSubtargets method which
   returns a list of Target classes which they want to build.  The 
   CompoundTarget class will handle the rest, making sure each gets
   built in the order of the sub-targets list.

   Sample:
       class BuildMeFirst(Target):
         ...

       class MainTarget(CompoundTarget):
         ...
         def GetSubtargets(self):
            return [ BuildMeFirst(), BuildMeSecond() ]
   """

   GOBUILD_INTERFACE_VERSION = "1.0"

   def GetClusterRequirements(self):
      return list(set(self._Accumulate('GetClusterRequirements', None)))

   def SetOptions(self, options):
      Target.SetOptions(self, options)

      # Reset subtargets to None in case the target has decided
      # to change its list of subtargets are a result of getting
      # new options.
      if hasattr(self, 'subtargets'):
         delattr(self, 'subtargets')

      self._FanOut('SetOptions', options)

   def GetOptions(self):
      return self._Accumulate('GetOptions', None)

   def GetRepositories(self, hosttype):
      return self._Accumulate('GetRepositories', hosttype)

   def GetCommands(self, hosttype):
      return self._Accumulate('GetCommands', hosttype)

   def GetStorageInfo(self, hosttype):
      return self._Accumulate('GetStorageInfo', hosttype)

   def GetComponentDependencies(self):
      return self._AccumulateDict('GetComponentDependencies', None)

   def GetSubtarget(self, cls):
      if not hasattr(self, 'GetSubtargets'):
         raise GobuildTargetException(SUBTARGETS_UNDEFINED)

      for subtarget in self._GetSubtargets():
         if isinstance(subtarget, cls):
            return subtarget

      return None

   def GetMakeCrossValue(self):
      for subtarget in self._GetSubtargets():
         if hasattr(subtarget, 'GetMakeCrossValue'):
            return subtarget.GetMakeCrossValue()
      return ''

   def _FanOut(self, fn, *args, **options):
      if not hasattr(self, 'GetSubtargets'):
         raise GobuildTargetException, SUBTARGETS_UNDEFINED

      for t in self._GetSubtargets():
         if hasattr(t, fn):
            getattr(t, fn)(*args, **options)

   def _Accumulate(self, fn, hosttype):
      result = list()
      for t in self._GetSubtargets():
         if hosttype and hasattr(t, 'GetClusterRequirements') and hosttype not in t.GetClusterRequirements():
            continue
         if hasattr(t, fn):
            func =  getattr(t, fn)
            result += func(hosttype) if hosttype else func()

      return result

   def _AccumulateDict(self, fn, hosttype):
      result = {}
      for t in self._GetSubtargets():
         if hosttype and hasattr(t, 'GetClusterRequirements') and hosttype not in t.GetClusterRequirements():
            continue
         if not hasattr(t, fn):
            continue
         func = getattr(t, fn)
         merge = func(hosttype) if hosttype else func()
         for m in merge:
            if m in result and result[m] != merge[m]:
               raise GobuildTargetException('Could not resolve merge conflict '
                                            'in CompoundTarget (%s: %s != %s)'
                                            % (m, result[m], merge[m]))
         result.update(merge)
      return result

   def _GetSubtargets(self):
      if not hasattr(self, 'subtargets'):
         if not hasattr(self, 'GetSubtargets'):
            raise GobuildTargetException, SUBTARGETS_UNDEFINED
         self.subtargets = self.GetSubtargets()

      return self.subtargets


class GobuildTargetException:
   """
   Simple exception to throw when things go wrong.
   """
   def __init__(self, error):
      self.error = error

   def __str__(self):
      return self.error

SHORTNAME_UNDEFINED = "You must define shortname in your derived class."
LONGNAME_UNDEFINED = "You must define longname in your derived class."
SUBTARGETS_UNDEFINED = "You must define subtargets in your derived class."
