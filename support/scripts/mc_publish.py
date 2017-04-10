# Copyright 2013 VMware, Inc.  All rights reserved. -- VMware Confidential

"""
manifest maven build bootstrap.
"""

import subprocess
import os
import sys
import platform
import shutil

# don't need any pycs
sys.dont_write_bytecode = True

def useCmdArgs(args, env, pos_args=[]):
   target = ''

   for a in args[1:]:
      try:
         key, val = a.split('=', 1)
      except:
         target = a
         pos_args.append(a)
         continue

      if a.startswith('-'):
         pos_args.append(a)

      env[key] = val

   return target


def Copy(src, dst, copy_fn = shutil.copy2):
   print 'Copy [%s] -> [%s]' % (src, dst)
   copy_fn(src, dst)


def DeepCopy(srcRoot, dstRoot,
             dir_filter=lambda root, x: True,
             file_filter=lambda root, x: True,
             copy_fn=Copy,
             mkdirs_fn=os.makedirs):

   if not os.path.exists(dstRoot):
      mkdirs_fn(dstRoot)
   else:
      assert os.path.isdir(dstRoot)

   for subdir, dirs, files in os.walk(srcRoot):
      reldirs = [os.path.relpath(os.path.join(subdir, d), srcRoot)
                 for d in dirs if dir_filter(subdir, d)]

      for reldir in reldirs:
         d = os.path.join(dstRoot, reldir)

         if not os.path.exists(d):
            mkdirs_fn(d)
         else:
            assert os.path.isdir(d)

      relfiles = [os.path.relpath(os.path.join(subdir, f), srcRoot)
                  for f in files if file_filter(subdir, f)]

      for relfile in relfiles:
         copy_fn(os.path.join(srcRoot, relfile),
                 os.path.join(dstRoot, relfile))





##### main #######

# needed on windows
env = {}
env['SystemRoot'] = os.environ.get('SYSTEMROOT', '')
env['PROCESSOR_ARCHITECTURE'] = os.environ.get('PROCESSOR_ARCHITECTURE', '')
env['TMP'] = os.environ.get('TMP', '')
env['TEMP'] = os.environ.get('TEMP', '')

env['TCROOT'] = os.environ.get('TCROOT', '/build/toolchain')


pos_args = []

useCmdArgs(sys.argv, env, pos_args)

source_dir = env.get('INTEGRATE_DIR', '')
if not source_dir:
   print 'INTEGRATE_DIR not defined, skipping step.'
   sys.exit()

publish_dir = env.get('PUBLISH_DIR', '')

if not publish_dir:
   print 'PUBLISH_DIR not defined, skipping step.'
   sys.exit()

dir_mappings = [
    ('%(INTEGRATE_DIR)s/vapi-idl-toolkit', '%(PUBLISH_DIR)s/idl-toolkit'),
    ('%(INTEGRATE_DIR)s/vapi-idl-toolkit/models/', '%(PUBLISH_DIR)s/metadata/api/'),
    ('%(INTEGRATE_DIR)s/vapi-idl-toolkit/models/', '%(PUBLISH_DIR)s/metadata/cli/'),
    ('%(INTEGRATE_DIR)s/vapi-metadata-toolkit/generator', '%(PUBLISH_DIR)s/metadata-toolkit'),
    ('%(INTEGRATE_DIR)s/vapi-cpp-toolkit/generator', '%(PUBLISH_DIR)s/cpp-toolkit/generator'),
    ('%(INTEGRATE_DIR)s/vapi-cpp-toolkit/runtime/vapicpp', '%(PUBLISH_DIR)s/cpp-toolkit/runtime'),
    ('%(INTEGRATE_DIR)s/vapi-cpp-toolkit/runtime/interoperability', '%(PUBLISH_DIR)s/cpp-toolkit/test/interoperability'),
    ('%(INTEGRATE_DIR)s/vapi-cpp-toolkit/runtime/unittests', '%(PUBLISH_DIR)s/cpp-toolkit/test/unittests'),
    ('%(INTEGRATE_DIR)s/vapi-cpp-runtime/runtime/toyvm', '%(PUBLISH_DIR)s/toyvm/cpp'),
    ('%(INTEGRATE_DIR)s/vapi-python-toolkit', '%(PUBLISH_DIR)s/python-toolkit'),
    ('%(INTEGRATE_DIR)s/vapi-dcli', '%(PUBLISH_DIR)s/dcli'),
    ('%(INTEGRATE_DIR)s/vapi-perl-toolkit', '%(PUBLISH_DIR)s/perl-toolkit'),
    ('%(INTEGRATE_DIR)s/vapi-ruby-toolkit', '%(PUBLISH_DIR)s/ruby-toolkit'),
    ('%(INTEGRATE_DIR)s/vapi-java-toolkit/generator', '%(PUBLISH_DIR)s/java-toolkit'),
    ('%(INTEGRATE_DIR)s/vapi-java-toolkit/runtime/lib/deps', '%(PUBLISH_DIR)s/java-toolkit/runtime/deps'),
    ('%(INTEGRATE_DIR)s/vapi-java-toolkit/runtime/lib/java', '%(PUBLISH_DIR)s/java-toolkit/runtime'),
   # TODO add all remaining compilations
   ]

# expand variables
dir_mappings = [(src % env, dst % env) for src, dst in dir_mappings]

for src, dst in dir_mappings:
   DeepCopy(src, dst)

file_mappings = [
    ('%(PUBLISH_DIR)s/metadata/api/com.vmware.vapi.metadata.json', '%(PUBLISH_DIR)s/metadata/api/vapi_common_metamodel.json'),
    ('%(PUBLISH_DIR)s/metadata/cli/com.vmware.vapi.metadata.json', '%(PUBLISH_DIR)s/metadata/cli/vapi_common_cli.json'),
   ]

# expand variables
file_mappings = [(src % env, dst % env) for src, dst in file_mappings]

for src, dst in file_mappings:
   Copy(src, dst, copy_fn=shutil.move)

