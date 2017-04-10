#!/bin/bash

SCRIPTDIR=`dirname $0`
TOOLSDIR=`dirname $SCRIPTDIR`

# If VAPI_PDKROOT is not set then get value from VAPI_PDK
if [ -z "$VAPI_PDKROOT" ]; then
   if [ -n "$VAPI_PDK" ]; then
      export VAPI_PDKROOT=$VAPI_PDK
   else
     echo "Set VAPI_PDK environment variable to the directory that has the vAPI PDK Bundle"
     exit
   fi
else
   if [ -n "$VAPI_PDK" ]; then
      # If both are set then, make sure they are the same
      if [ "$VAPI_PDKROOT" != "$VAPI_PDK" ]; then
        echo "VAPI_PDKROOT and VAPI_PDK values are conflicting. Set VAPI_PDK environment variable to the directory that has the vAPI PDK Bundle. VAPI_PDKROOT need not be set"
        exit
      fi
   fi
fi

. $TOOLSDIR/env.sh

display_help() {
    echo ""
    echo "Sphinx documentation generator"
    echo ""
    echo "    -i for specifying the directory that has the generated python client code"
    echo "    -o for specifying the output directory"
    echo ""
}

while getopts i:o: opt
do
   case $opt in
      i)
         SRCDIR="$OPTARG"
         ;;
      o)
         DSTDIR="$OPTARG"
         ;;
      \?)
         echo "Invalid option: -$OPTARG" >&2
         display_help
         ;;
   esac
done

# Path to the top-level package to process
if [ -z "$SRCDIR" ]; then
   echo "Set SRCDIR environment variable to the directory that has the generated python client code"
   exit 1
fi
SRCDIR=`$TOOLSDIR/abspath.sh $SRCDIR`

# Location of generated output files
# Path to the top-level package to process
if [ -z "$DSTDIR" ]; then
   echo "Set DSTDIR environment variable to the output directory"
   exit 1
fi
mkdir -p $DSTDIR
DSTDIR=`$TOOLSDIR/abspath.sh $DSTDIR`

mkdir $DSTDIR/temp
TMPDIR=$DSTDIR/temp

# Title of the documentation
TITLE=${TITLE:="vAPI"}

# Author of the documentation
AUTHOR=${AUTHOR:="VMware, Inc."}

# Version of the release
VERSION=${VERSION:=1.0}

# Configure PYTHONPATH to find Sphinx libraries
export PYTHONPATH=$SRCDIR:$SPHINX_LIBS:$VAPI_RUNTIME_PYTHON:$PYTHONPATH
export PYTHONDONTWRITEBYTECODE=1

# Generate reST files for the module being processed
$SPHINX_APIDOC -d 10 -o $TMPDIR -f -F -H "$TITLE" -A "$AUTHOR" -V $VERSION -R $VERSION $SRCDIR

cp -R $TOOLSDIR/sphinx $TMPDIR

cd $TMPDIR
make SPHINXOPTS='-c 'sphinx SPHINXBUILD=$SPHINX_BUILD html

echo "Moving HTML pages to 'python' subdirectory in the destination directory provided."
mv $TMPDIR/_build/html $DSTDIR/python
rm -rf $TMPDIR

