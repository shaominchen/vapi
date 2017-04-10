# Copyright 2014 VMware, Inc.  All rights reserved. -- VMware Confidential

"""
ToyVM tutorial gobuild module.
"""

import helpers.target
import specs.toyvm

class ToyVMTutorial(helpers.target.Target):

    def GetBuildProductNames(self):
        """
        Return a dictionary representing the product name (short form
        and long form).
        """
        return {'name': 'toyvm',
                'longname': 'ToyVM tutorial'}

    def GetComponentDependencies(self):
        comps = {}
        comps['vapi-core'] = {
            'branch': specs.toyvm.VAPI_CORE_BRANCH,
            'change': specs.toyvm.VAPI_CORE_CLN,
            'buildtype': specs.toyvm.VAPI_CORE_BUILDTYPES[self.options.get('buildtype')],
            'hosttypes': specs.toyvm.VAPI_CORE_HOSTTYPES,
        }
        return comps

    def GetClusterRequirements(self):
        """
        Return a list of strings representing required host types.
        ToDo: Windows support needs to be added?
        """
        return ['linux64' ]

    def GetRepositories(self, hosttype):
        """
        Return a list of dictionaries representing source code repositories.
        ToDo: Point to a future vapi repo?
            [ PerforceRepo('vapi-core/%(branch)', 'vapi-core'),
        """
        return []

    def GetCommands(self, hosttype):
        """
        Return a list of dictionaries representing commmands to execute
        to build this target.
        ToDo: Need to add building provider and export arch and tcroot in mvn.sh
            arch = os.environ.get('ARCH')
            tcroot = os.environ.get('TCROOT')
            vapi-core = os.environ.get('CORE_ROOT')
            cmd = '%s/toyvm/java/provider/mvn.sh clean install' %(vapi-core)
            env = SafeEnvironment(hosttype)
            env['JAVA_HOME'] = "%s/%s/jdk-1.7.0_72"%(tcroot,arch)
            return [ { 'desc'    : 'Running maven provider build',
                       'root'    : '%(buildroot)/toyvm',
                       'log'     : 'toyvm.log',
                       'command' : cmd,
                       'env'     : env
                   } ]
        """
        return []

    def GetStorageInfo(self, hosttype):
        """
        Return a list of strings representing required host types.
        """
        return []
