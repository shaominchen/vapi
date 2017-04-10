#!/bin/sh

# Wrapper script for running a Python vAPI metadata server

scriptdir=`dirname $0`

$scriptdir/python.sh $scriptdir/metadata.properties
