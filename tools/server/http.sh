#!/bin/sh

# Wrapper script for launching HTTP service

scriptdir=`dirname $0`
. $scriptdir/../env.sh

# Making sure that cgi process loads the python binary from toolchain
# so that it can find mimeparse
export PATH=/build/toolchain/lin$ARCH/python-2.6.1/bin:$PATH

export VDDL_CGI=1
export http_proxy=

display_help() {
    echo ""
    echo "vAPI HTTP Service"
    echo ""
    echo "    -h for specifying vAPI server IP address"
    echo "    -p for specifying vAPI server port"
    echo "    -s for specifying HTTP service port"
    echo ""
}

while getopts ":h:p:s:" opt; do
  case $opt in
    h)
      VAPI_HOST=$OPTARG
      ;;
    p)
      VAPI_PORT=$OPTARG
      ;;
    s)
      HTTP_PORT=$OPTARG
      ;;
    \?)
      echo "Invalid option: -$OPTARG" >&2
      display_help
      ;;
  esac
done

if [ -z "$VAPI_HOST" -o -z "$VAPI_PORT" -o -z "$HTTP_PORT" ]; then
   display_help
   exit
fi

export VAPI_HOST
export VAPI_PORT
PYTHONPATH=$VAPI_DEP_PROTOBUF_PYTHON:$VAPI_RUNTIME_PYTHON $VAPI_DEP_PYTHONBIN -m vmware.vapi.http.httpd-cgi -p $HTTP_PORT -d $VAPI_PDKROOT/cgi-bin -C '/service'
