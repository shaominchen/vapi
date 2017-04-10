#!/bin/sh

set -e  # Exit if any command fails

JAVADIR=`dirname $0`
JAVADIR=`cd $JAVADIR && pwd`
TUTORIALDIR=`dirname $JAVADIR`
PDKDIR=`dirname $TUTORIALDIR`
STDVMODL=$PDKDIR/idl-toolkit/vmidl/vapi_stdlib.vmidl
VMODLDIR=$TUTORIALDIR/vmodl

IMPLDIR=$JAVADIR/provider/toyvm/src/main/java/sample/first/impl
UTILDIR=$JAVADIR/provider/toyvm/src/main/java/sample/first/util
CLIENTDIR=$JAVADIR/client/toyvm/src/main/java/sample/first/client

BLDDIR=$PDKDIR/build/toyvm
GENDIR=$BLDDIR/generated
PROVIDER_GENDIR=$GENDIR/provider/java/sample/first
CLIENT_GENDIR=$GENDIR/client/java/sample/first
CLASSESDIR=$BLDDIR/classes

DEPENDENCIES=$PDKDIR/toyvm/java/deps/\\*
echo $DEPENDENCIES

echo "Creating classes and generation directories..."
mkdir -p $CLASSESDIR
mkdir -p $GENDIR/provider
mkdir -p $GENDIR/client

echo
#Generate json files used by various metadata services
$PDKDIR/metadata-toolkit/bin/metadata-generator --library $STDVMODL --output $BLDDIR $VMODLDIR
echo
#Generate provider skeleton
$PDKDIR/java-toolkit/bin/java-generator --profile provider --library $STDVMODL --output $GENDIR/provider $VMODLDIR
echo
#Generate client stub
$PDKDIR/java-toolkit/bin/java-generator --profile client --library $STDVMODL --output $GENDIR/client $VMODLDIR
echo

echo "Compiling provider code's language bindings..."
javac -g -d $CLASSESDIR -cp $DEPENDENCIES $PROVIDER_GENDIR/ToyVM*.java
echo "Compiling provider's implementation..."
javac -g -d $CLASSESDIR -cp $CLASSESDIR:$DEPENDENCIES $IMPLDIR/ToyVM*.java $UTILDIR/*.java

echo "Compiling language bindings for client..."
javac -g -d $CLASSESDIR -cp $DEPENDENCIES $CLIENT_GENDIR/ToyVM*.java
echo "Compiling client's implementation..."
javac -g -d $CLASSESDIR -cp $CLASSESDIR:$DEPENDENCIES $CLIENTDIR/ToyVM*.java
