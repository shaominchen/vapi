#!/bin/sh

# Wrapper script for running dcli

scriptdir=`dirname $0`

$scriptdir/python.sh -m vmware.vapi.client.dcli.cli "$@"
