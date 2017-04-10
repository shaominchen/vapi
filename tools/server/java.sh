#!/bin/sh

# Wrapper script for running a Java vAPI server

scriptdir=`dirname $0`
. $scriptdir/../env.sh

$JAVA -cp $LOG4J:$PROTOBUF_JAVA:$VAPI_RUNTIME_JAVA/*:$VAPI_RUNTIME_JAVA_DEPS/*:$RABBITMQ:${CLASSPATH:-} com.vmware.vapi.server.Server "$@"
