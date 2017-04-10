#!/bin/sh

# Shell script that runs gradle from toolchain and puts the
# build outputs under buildroot

#fyi: we don't really want to know where /this/ script is located, but rather the concrete
# script that is delegating to this one, which is within a specific language toolkit dir.
HERE=`cd $(dirname $0); pwd`

TOOLKIT_DIR=${TOOLKIT_DIR:=$HERE}
TOOLKIT_NAME=`basename $TOOLKIT_DIR`
VAPIDIR=`dirname $TOOLKIT_DIR`

UNAME_S=`uname -s`
case $UNAME_S in
Darwin)
    ARCH=mac32
    JDK_VERSION=jdk1.7.0_15.jdk
    JAVA_HOME=`/usr/libexec/java_home`
    ;;
Win*)
    ARCH=win32
    ;;
*)
    ARCH=lin32
    ;;
esac

echo ARCH: ${ARCH:=lin32}
echo TCROOT: ${TCROOT:=/build/toolchain}
export TCROOT
echo JAVA_HOME: ${JAVA_HOME:=$TCROOT/$ARCH/jdk-1.7.0_79}
export JAVA_HOME
echo GRADLE_HOME: ${GRADLE_HOME:=$TCROOT/noarch/gradle-2.0}
export GRADLE_HOME
echo BUILDROOT: ${BUILDROOT:=$VAPIDIR/build/gradle}
export BUILDROOT
echo BUILDTHIS: ${BUILDTHIS:=$BUILDROOT/$TOOLKIT_NAME}
export BUILDTHIS
echo BUILDFILE: ${BUILDFILE:=$TOOLKIT_DIR/build.gradle}
export BUILDFILE

#FYI: the target must be set prior to calling this common script
echo GOBUILD_TARGET: ${GOBUILD_TARGET:=vapi-tbd}
export GOBUILD_TARGET
echo OBJDIR: ${OBJDIR:=beta}
export OBJDIR

if [ -z $GOBUILD_LOCALCACHE_DIR ]; then
    export GOBUILD_LOCALCACHE_DIR=$VAPIDIR/build/package/COMPONENTS
fi
export GOBUILD_AUTO_COMPONENTS=True
export GOBUILD_AUTO_COMPONENTS_HOSTTYPE=generic

exec $GRADLE_HOME/bin/gradle \
    -Dbase.dir="$TOOLKIT_DIR" \
    -Dbuild.root="$BUILDTHIS" \
    -Dbuild.publish="$BUILDTHIS/publish" \
    -Dvapi.core.version.properties="$VAPIDIR/support/version/vapi-core-version.properties" \
    --gradle-user-home "$BUILDROOT/gradle-home" \
    --project-cache-dir "$BUILDROOT/gradle-cache" \
    --build-file "$BUILDFILE" \
    "$@"

