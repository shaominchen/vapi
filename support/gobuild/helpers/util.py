# Copyright 2008 VMware, Inc.  All rights reserved. -- VMware Confidential
"""
Contains miscellaneous helper functions.
"""
import os
import re

from helpers.target import GobuildTargetException


def ExtractMacro(filename, macro):
    """
    Return the string value of the macro `macro' defined in `filename'.
    """
    # Simple regex is far from a complete C preprocessor but is useful
    # in many cases
    regexp = re.compile(r'^\s*#\s*define\s+%s\s+"(.+[.].+[.].+)"\s*$' % macro)
    try:
        with open(filename) as fh:
            for line in fh:
                m = regexp.match(line)
                if m:
                    return m.group(1)
    except EnvironmentError:
        pass
    return ''


def MkdirCommand(hosttype, path, root='', logfile=None):
    """
    Return a dict that describes a command to create a directory.
    """
    if not logfile:
        logfile = 'mkdir-%s.log' % os.path.basename(path)

    if hosttype.startswith('linux'):
        mkdir = '/build/toolchain/lin32/coreutils-5.97/bin/mkdir'
    elif hosttype.startswith('macosx'):
        mkdir = '/build/toolchain/mac32/coreutils-5.97/bin/mkdir'
    elif hosttype.startswith('windows'):
        tcroot = os.environ.get('TCROOT', 'C:/TCROOT-not-set')
        mkdir = '%s/win32/coreutils-5.3.0/bin/mkdir.exe' % tcroot
    else:
        raise GobuildTargetException("Unknown hosttype %s" % hosttype)

    return {
        'desc': 'Creating directory %s' % path,
        'root': root,
        'log': logfile,
        'command': '%s -p -v %s' % (mkdir, path),
        'env': {},
    }

def UnzipCommand(hosttype, source, target):
    """
    Return a dict that describes an unzip command.
    """
    if hosttype.startswith('windows'):
        tcroot = os.environ.get('TCROOT', 'C:/TCROOT-not-set')
        unzip = '%s/win32/unzip-6.0/unzip.exe' % tcroot
    else:
        raise GobuildTargetException("Unsupported hosttype %s" % hosttype)

    return {
        'desc': 'Unzipping %s to %s' % (source, target),
        'root': target,
        'log': 'unzip.log',
        'command': '%s -o %s' % (unzip, source),
        'env': {},
    }

def RMrfCommand(hosttype, path, root='', logfile=None):
    """
    Return a dict that describes a command to remove path recurcively
    """
    if not logfile:
        logfile = 'rm-%s.log' % os.path.basename(path)

    if hosttype.startswith('linux'):
        rm = '/build/toolchain/lin32/coreutils-5.97/bin/rm'
    elif hosttype.startswith('macosx'):
        rm = '/build/toolchain/mac32/coreutils-5.97/bin/rm'
    elif hosttype.startswith('windows'):
        tcroot = os.environ.get('TCROOT', 'C:/TCROOT-not-set')
        rm = '%s/win32/bin/rm.exe' % tcroot
    else:
        raise GobuildTargetException("Unknown hosttype %s" % hosttype)

    return {
        'desc': 'Removing %s' % path,
        'root': root,
        'log': logfile,
        'command': '%s -v -r -f %s' % (rm, path),
        'env': {},
    }

