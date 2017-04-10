# Copyright 2014 VMware, Inc.  All rights reserved. -- VMware Confidential

"""
Support code for building products with gobuild.
"""
import helpers
import helpers.access
import targets.vapicore
import targets.mc_vapicore
import targets.vapicpp
import targets.toyvm
import targets.dclicore
import targets.nsxprovider
import targets.phonehomebe

# The targets dictionary maps target names to classes which
# implement the gobuild Target interface.  The dictionary is not
# strictly necessary, but aids greatly in the implementation of
# GetTarget and GetAllTargets below.  Be sure to remove the samples
# from this list when you add gobuild support to your product
# tree!

TARGETS = {
    # vapi-core is the gobuild component name
    # vapicore is the scons product name
    'mc-vapi-core': targets.mc_vapicore.VapiCoreMcDev,
    'vapicore': targets.vapicore.VapiCore,
    'vapi-core': targets.vapicore.VapiCore,
    'vapi-core-idl': targets.vapicore.VapiCoreIdl,
    'vapi-core-python': targets.vapicore.VapiCorePython,
    'vapi-core-java': targets.vapicore.VapiCoreJava,
    'vapi-core-metadata': targets.vapicore.VapiCoreMetadata,
    'vapi-core-dotnet': targets.vapicore.VapiCoreDotnet,
    'vapi-core-perl': targets.vapicore.VapiCorePerl,
    'vapi-core-ruby': targets.vapicore.VapiCoreRuby,
    'vapicpp-dev': targets.vapicpp.VapiCppDev,
    'vapicpp-emitter': targets.vapicore.VapiCppEmitter,
    'toyvm': targets.toyvm.ToyVMTutorial,
    'dclicore' : targets.dclicore.DcliCore,
    'nsx-vapi-provider': targets.nsxprovider.NSXProvider,
    'phonehomebe':targets.phonehomebe.PhoneHomeBackend
}

# Add an access target for each already-defined target.
for name, cls in TARGETS.copy().iteritems():
    if name.endswith('-access'):
        # This is an access target, don't add an access-access target.
        continue
    accessname = '%s-access' % name
    if accessname in TARGETS:
        # There's already an access target defined.
        continue
    TARGETS[accessname] = helpers.access.MakeAccessTarget(name, cls)


def GetTarget(log, name):
    """
    Return the Target class for the build identified by the target
    'name'.  The log parameter is a standard python logging object
    which can be used to write to the gobuild log if you like.
    """
    objtype = TARGETS.get(name)
    if not objtype:
        return None

    return objtype()


def GetAllTargets(log):
    """
    Return a list of all targets that can be built from this
    branch.
    """
    return TARGETS.keys()


def GetBranch(log):
    """
    Return the name of the branch.
    """
    return helpers.GetBranch()
