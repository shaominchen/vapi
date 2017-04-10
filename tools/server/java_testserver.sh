#!/bin/sh

# Wrapper script for running a Java vAPI server for integration testing

SCRIPTDIR=`dirname $0`
SCRIPTDIR=`cd $SCRIPTDIR && pwd`
TOOLSDIR=`dirname $SCRIPTDIR`
VAPIDIR=`dirname $TOOLSDIR`
VAPI_PDK_BUILDROOT=${VAPI_PDK_BUILDROOT:-$VAPIDIR/build/maven/java-toolkit}

ADD_ARGS=""
if [ "$M2_REPO" != "" ]; then
    ADD_ARGS="-Dmaven.repo.local=$M2_REPO"
fi

$VAPIDIR/java-toolkit/java-runtime/mvn.sh --quiet --projects test exec:java -Dexec.mainClass="com.vmware.vapi.server.Server" -DsystemProperties=log4j.configuration=$VAPIDIR/java-toolkit/java-runtime/test/src/test/resources/log4j_alt.properties -Dexec.classpathScope=test -Dbuildroot=$VAPI_PDK_BUILDROOT $ADD_ARGS -Dexec.args="$@"
