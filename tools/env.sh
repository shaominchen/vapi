#!/bin/bash

if [ "-z" "$VAPIDIR" ]; then
    toolsdir=`dirname $0`
    toolsdir=`cd $toolsdir && pwd`
    VAPIDIR=`dirname $toolsdir`
fi

# helper script

# The ||: is necessary when script is used by Jenkins because the latter runs
# the script with 'sh -x' which bails on any statement that does not return 0.
# Failure to grep is such a case. ||: causes the statement to return 0
# regardless of the grep outcome.
IS_VISOR=`uname -a | grep "VMkernel"` || :

if [ "-n" "$IS_VISOR" ]; then
   ARCH="32"
elif [ `uname -m` = "x86_64" ]; then
   ARCH="64"
else
   ARCH="32"
fi

TC_ROOT=${TCROOT:-/build/toolchain}

#
# Check VAPI_PDKROOT
#
if [ -z "$VAPI_PDKROOT" ]; then
   echo "Set VAPI_PDKROOT environment variable to the directory that has the vAPI PDK Bundle"
   exit
fi

#
# Java Dependencies
#

# Toolchain Libraries
# XXX This should come as a toplevel component instead of taking it from apache-axis
LOG4J=$TC_ROOT/noarch/apache-log4j-1.2.16/log4j-1.2.16.jar

# vAPI Libraries
VAPI_RUNTIME_JAVA=$VAPI_PDKROOT/lib/java
VAPI_RUNTIME_JAVA_DEPS=$VAPI_PDKROOT/lib/deps

# Binaries
JAVA=$TC_ROOT/lin$ARCH/jdk-1.6.0_30/bin/java

export JAVA_HOME=${JAVA_HOME:-$TC_ROOT/lin64/jdk-1.7.0_45}
export MAVEN_OPTS=${MAVEN_OPTS:-"-XX:MaxPermSize=200m"}

PYVERSION=${PYVERSION:="2.7"}
if [ "$PYVERSION" = "2.7" ]; then
    . $VAPI_PDKROOT/tools/env/python27.sh
else
    echo $PYVERSION" is not supported. Use tools/python/vapienv.sh instead."
fi
