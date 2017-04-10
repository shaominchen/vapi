#!/bin/sh

#
# Python Dependencies
#

export TC_ROOT=${TC_ROOT:-/build/toolchain}

# Main Python binary
PYTHONBINDIR=$TC_ROOT/lin32/python-2.7.11-openssl1.0.1r/bin
PYTHONBIN=$TC_ROOT/lin32/python-2.7.11-openssl1.0.1r/bin/python

# Runtime dependencies
DISTRIBUTE=$TC_ROOT/lin32/noarch/distribute-0.6.30/lib/python2.7/site-packages
OPENSSL=$TC_ROOT/lin32/pyopenssl-0.13/lib/python2.7/site-packages
TWISTED=$TC_ROOT/lin32/twisted-12.2.0/lib/python2.7/site-packages
REQUESTS=$TC_ROOT/noarch/requests-2.6.0/lib/python2.7/site-packages
LXML=$TC_ROOT/lin32/lxml-3.3.1/lib/python2.7/site-packages
ZOPE=$TC_ROOT/lin32/zope.interface-4.0.1/lib/python2.7/site-packages
SIX=$TC_ROOT/noarch/python-six-1.7.2/lib/python2.7/site-packages
SETUPTOOLS=$TC_ROOT/noarch/setuptools-5.7/lib/python2.7/site-packages

# Test framework dependencies
COVERAGE_LIB=$TC_ROOT/noarch/coverage-3.7/lib/python2.7/site-packages
NOSE_LIB=$TC_ROOT/noarch/nose-1.3.1/lib/python2.7/site-packages
COV_CORE=$TC_ROOT/noarch/cov-core-1.3/lib/python2.6/site-packages
NOSE_COV=$TC_ROOT/noarch/nose-cov-1.4/lib/python2.6/site-packages
PYLINT=$TC_ROOT/noarch/pylint-1.3.1/bin/pylint
COVERAGE=$TC_ROOT/noarch/coverage-3.7/bin/coverage
NOSE=$TC_ROOT/noarch/nose-1.3.1/bin/nosetests
PEP8=$TC_ROOT/noarch/pep8-1.3.3/lib/python2.6/site-packages/pep8.py
URWID=$TC_ROOT/lin32/urwid-1.1.1/lib/python2.7/site-packages

DCLI_BUILD_DEP_FILE=$(echo $VAPIDIR/build/build/gobuild/obj/dclicore.properties)

# dcli dependencies
PYCRYPTO=$TC_ROOT/lin32/pycrypto-2.6/lib/python2.7/site-packages
PEXPECT=$TC_ROOT/noarch/pexpect-4.0.1/lib/python3.3/site-packages
PTYPROCESS=$TC_ROOT/noarch/ptyprocess-0.5/lib/python3.3/site-packages

get_latest_build() {
#read builds
#echo $builds | tr ' ' '\n' | sort -r | head -1
tr ' ' '\n' | sort -r | head -1
}

# Set vAPI Runtime and test eggs path
if [ -d "build/gradle/python-toolkit" ]
then
    PYTHON_TOOLKIT_PATH=$(echo $VAPIDIR/build/gradle/python-toolkit/publish)
    VAPI_RUNTIME_PYTHON=$(echo $PYTHON_TOOLKIT_PATH/runtime/vapi_runtime-*-py2.7.egg):$(echo $PYTHON_TOOLKIT_PATH/runtime/vapi_common-*-py2.7.egg):$(echo $PYTHON_TOOLKIT_PATH/runtime/vapi_common_client-*-py2.7.egg)
    VAPI_TEST_PYTHON=$(echo $PYTHON_TOOLKIT_PATH/test/vapi_test-*-py2.7.egg):$(echo $PYTHON_TOOLKIT_PATH/test/vapi_test_client-*-py2.7.egg)
elif [ -f "$DCLI_BUILD_DEP_FILE" ]
then
    PYTHON_TOOLKIT_PATH=`grep -o 'GOBUILD_VAPI-CORE-PYTHON_ROOT=[^ ]*' $DCLI_BUILD_DEP_FILE | awk -F= '{print $2}'`
    VAPI_RUNTIME_PYTHON=$(echo $PYTHON_TOOLKIT_PATH/runtime/vapi_runtime-*-py2.7.egg):$(echo $PYTHON_TOOLKIT_PATH/runtime/vapi_common-*-py2.7.egg):$(echo $PYTHON_TOOLKIT_PATH/runtime/vapi_common_client-*-py2.7.egg)
    VAPI_TEST_PYTHON=$(echo $PYTHON_TOOLKIT_PATH/test/vapi_test-*-py2.7.egg):$(echo $PYTHON_TOOLKIT_PATH/test/vapi_test_client-*-py2.7.egg)
else
    VAPI_RUNTIME_PYTHON=$(echo $VAPIDIR/build/package/COMPONENTS/vapi-core-python/ob-*/publish/runtime/vapi_runtime-*-py2.7.egg | get_latest_build):$(echo $VAPIDIR/build/package/COMPONENTS/vapi-core-python/ob-*/publish/runtime/vapi_common-*-py2.7.egg | get_latest_build):$(echo $VAPIDIR/build/package/COMPONENTS/vapi-core-python/ob-*/publish/runtime/vapi_common_client-*-py2.7.egg | get_latest_build)
    VAPI_TEST_PYTHON=$(echo $VAPIDIR/build/package/COMPONENTS/vapi-core-python/ob-*/publish/test/vapi_test-*-py2.7.egg | get_latest_build):$(echo $VAPIDIR/build/package/COMPONENTS/vapi-core-python/ob-*/publish/test/vapi_test_client-*-py2.7.egg | get_latest_build)
fi

# Set idl toolkit path
if [ -z "$IDL_TOOLKIT_DIR" ]
then
    if [ -f "$DCLI_BUILD_DEP_FILE" ]
    then
        IDL_TOOLKIT_PATH=`grep -o 'GOBUILD_VAPI-CORE-IDL_ROOT=[^ ]*' $DCLI_BUILD_DEP_FILE | awk -F= '{print $2}'`
        export IDL_TOOLKIT_DIR=$(echo $IDL_TOOLKIT_PATH/..)
    else
        export IDL_TOOLKIT_DIR=$(echo $VAPIDIR/build/package/COMPONENTS/vapi-core-idl/ob-* | get_latest_build)
    fi
fi

# Set vAPI clients(dcli) egg path
if [ -d "$VAPIDIR/build/dclicore" ]; then
    VAPI_CLIENTS_PYTHON=$(echo $VAPIDIR/build/dclicore/obj/publish/lin64/vapi_clients-*-py2.7.egg | get_latest_build)
else
    VAPI_CLIENTS_PYTHON=$(echo $VAPIDIR/build/package/COMPONENTS/dclicore/ob-*/publish/lin64/vapi_clients-*-py2.7.egg | get_latest_build)
fi

# Download dependencies - Werkzeug, prompt-toolkit, wcwidth, pygments if not present in artifactory-cache
if [ ! -f "$VAPIDIR/build/artifactory-cache/prompt_toolkit-1.0.0-py2-none-any.whl" ]; then
    wget -q -O $VAPIDIR/build/artifactory-cache/prompt_toolkit-1.0.0-py2-none-any.whl https://build-artifactory.eng.vmware.com/artifactory/api/pypi/pypi-remote/packages/52/ce/e40b97d91dad3ac99d29b555dbded6060cd919a3e416fd66b9ddb4bebe16/prompt_toolkit-1.0.0-py2-none-any.whl
fi
PROMPT_TOOLKIT=$(echo $VAPIDIR/build/artifactory-cache/prompt_toolkit-1.0.0-py2-none-any.whl)

if [ ! -f "$VAPIDIR/build/artifactory-cache/wcwidth-0.1.6-py2.py3-none-any.whl" ]; then
    wget -q -O $VAPIDIR/build/artifactory-cache/wcwidth-0.1.6-py2.py3-none-any.whl https://build-artifactory.eng.vmware.com/artifactory/api/pypi/pypi-remote/packages/90/c8/d28358bffe21c93691cf90bb7231d9c7bb9686afb9fd8c84b7866a2a1004/wcwidth-0.1.6-py2.py3-none-any.whl
fi
WCWIDTH=$(echo $VAPIDIR/build/artifactory-cache/wcwidth-0.1.6-py2.py3-none-any.whl)

if [ ! -f "$VAPIDIR/build/artifactory-cache/Pygments-2.1.3-py2.py3-none-any.whl" ]; then
    wget -q -O $VAPIDIR/build/artifactory-cache/Pygments-2.1.3-py2.py3-none-any.whl https://build-artifactory.eng.vmware.com/artifactory/api/pypi/pypi-remote/packages/9e/d0/d692085518c6a2dc875fe421c866fb6a08e0d9796ca507803c1e545fa116/Pygments-2.1.3-py2.py3-none-any.whl
fi
PYGMENTS=$(echo $VAPIDIR/build/artifactory-cache/Pygments-2.1.3-py2.py3-none-any.whl)

if [ ! -f "$VAPIDIR/build/artifactory-cache/Werkzeug-0.11.4-py2.py3-none-any.whl" ]; then
    wget -q -O $VAPIDIR/build/artifactory-cache/Werkzeug-0.11.4-py2.py3-none-any.whl http://build-artifactory.eng.vmware.com/artifactory/pypi-virtual/packages/2.7/W/Werkzeug/Werkzeug-0.11.4-py2.py3-none-any.whl
fi
WERKZEUG=$(echo $VAPIDIR/build/artifactory-cache/Werkzeug-0.11.4-py2.py3-none-any.whl)

# vAPI Libraries and its dependencies
PY_LIBRARIES=$URWID:$OPENSSL:$TWISTED:$PYCRYPTO:$PEXPECT:$REQUESTS:$LXML:$ZOPE:$SIX:$SETUPTOOLS:$DISTRIBUTE:$NOSE_LIB:$COV_CORE:$NOSE_COV:$COVERAGE_LIB:$WERKZEUG:$PROMPT_TOOLKIT:$WCWIDTH:$PYGMENTS:$PTYPROCESS

export PYTHONPATH=$VAPI_RUNTIME_PYTHON:$VAPI_CLIENTS_PYTHON:$PY_LIBRARIES:$VAPI_TEST_PYTHON

echo $PYTHONPATH
# Prevent Python from writing .pyc and .pyo files
export PYTHONDONTWRITEBYTECODE=1
export DCLI_LOGFILE=/tmp/dcli.log
export http_proxy=

# Set the bin path to make sure that coverage and python are executed from toolchain
export PATH=$COVERAGE:$PYTHONBINDIR:$PATH
