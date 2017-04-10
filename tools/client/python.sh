#!/bin/sh

# Wrapper script for running a vAPI Python static client

scriptdir=`dirname $0`

. $scriptdir/../env.sh
PYTHONPATH=$PYTHONPATH:$VAPI_RUNTIME_PYTHON $PYTHONBIN "$@"
