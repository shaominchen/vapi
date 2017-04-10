#!/bin/sh

# Wrapper script for running a Python vAPI server

scriptdir=`dirname $0`

. $scriptdir/../env.sh
PYTHONPATH=$PYTHONPATH:$VAPI_RUNTIME_PYTHON $PYTHONBIN -m vmware.vapi.server.vapid "$@"
