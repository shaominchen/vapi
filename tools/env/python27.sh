#!/bin/sh

#
# Python Dependencies
#

# Main Python binary
PYVERSION=python-2.7.11-openssl1.0.1r
PYTHONBINDIR=$TC_ROOT/lin32/$PYVERSION/bin
PYTHONBIN=$TC_ROOT/lin32/$PYVERSION/bin/python

PYUPGRADE=$TC_ROOT/lin32/$PYVERSION/bin/2to3

# Runtime dependencies
OPENSSL=$TC_ROOT/lin32/pyopenssl-0.13/lib/python2.7/site-packages
TWISTED=$TC_ROOT/lin32/twisted-12.2.0/lib/python2.7/site-packages
REQUESTS=$TC_ROOT/noarch/requests-2.2.1/lib/python2.7/site-packages
LXML=$TC_ROOT/lin32/lxml-3.3.1/lib/python2.7/site-packages
ZOPE=$TC_ROOT/lin32/zope.interface-4.0.1/lib/python2.7/site-packages
SIX=$TC_ROOT/noarch/python-six-1.7.2/lib/python2.7/site-packages
SETUPTOOLS=$TC_ROOT/noarch/setuptools-5.7/lib/python2.7/site-packages

# Test framework dependencies
COVERAGE_LIB=$TC_ROOT/noarch/coverage-3.7/lib/python2.7/site-packages
NOSE_LIB=$TC_ROOT/noarch/nose-1.3.1/lib/python2.7/site-packages
COV_CORE=$TC_ROOT/noarch/cov-core-1.3/lib/python2.6/site-packages
NOSE_COV=$TC_ROOT/noarch/nose-cov-1.4/lib/python2.6/site-packages
PYLINT=$TC_ROOT/noarch/pylint-0.26.0/bin/pylint
COVERAGE=$TC_ROOT/noarch/coverage-3.7/bin/coverage
NOSE=$TC_ROOT/noarch/nose-1.3.1/bin/nosetests
PEP8=$TC_ROOT/noarch/pep8-1.3.3/lib/python2.6/site-packages/pep8.py

# dcli dependencies
PYCRYPTO=$TC_ROOT/lin32/pycrypto-2.6/lib/python2.7/site-packages
PEXPECT=$TC_ROOT/noarch/pexpect-2.3/lib/python2.7/site-packages
PTYPROCESS=$TC_ROOT/noarch/ptyprocess-0.5/lib/python3.3/site-packages

mkdir -p $VAPI_PDKROOT/build/artifactory-cache
# Download dependencies - Werkzeug, prompt-toolkit, wcwidth, pygments if not present in artifactory-cache
if [ ! -f "$VAPI_PDKROOT/build/artifactory-cache/prompt_toolkit-1.0.0-py2-none-any.whl" ]; then
    wget -q -O $VAPI_PDKROOT/build/artifactory-cache/prompt_toolkit-1.0.0-py2-none-any.whl https://build-artifactory.eng.vmware.com/artifactory/api/pypi/pypi-remote/packages/52/ce/e40b97d91dad3ac99d29b555dbded6060cd919a3e416fd66b9ddb4bebe16/prompt_toolkit-1.0.0-py2-none-any.whl
fi
PROMPT_TOOLKIT=$(echo $VAPI_PDKROOT/build/artifactory-cache/prompt_toolkit-1.0.0-py2-none-any.whl)

if [ ! -f "$VAPI_PDKROOT/build/artifactory-cache/wcwidth-0.1.6-py2.py3-none-any.whl" ]; then
    wget -q -O $VAPI_PDKROOT/build/artifactory-cache/wcwidth-0.1.6-py2.py3-none-any.whl https://build-artifactory.eng.vmware.com/artifactory/api/pypi/pypi-remote/packages/90/c8/d28358bffe21c93691cf90bb7231d9c7bb9686afb9fd8c84b7866a2a1004/wcwidth-0.1.6-py2.py3-none-any.whl
fi
WCWIDTH=$(echo $VAPI_PDKROOT/build/artifactory-cache/wcwidth-0.1.6-py2.py3-none-any.whl)

if [ ! -f "$VAPI_PDKROOT/build/artifactory-cache/Pygments-2.1.3-py2.py3-none-any.whl" ]; then
    wget -q -O $VAPI_PDKROOT/build/artifactory-cache/Pygments-2.1.3-py2.py3-none-any.whl https://build-artifactory.eng.vmware.com/artifactory/api/pypi/pypi-remote/packages/9e/d0/d692085518c6a2dc875fe421c866fb6a08e0d9796ca507803c1e545fa116/Pygments-2.1.3-py2.py3-none-any.whl
fi
PYGMENTS=$(echo $VAPI_PDKROOT/build/artifactory-cache/Pygments-2.1.3-py2.py3-none-any.whl)

if [ ! -f "$VAPI_PDKROOT/build/artifactory-cache/Werkzeug-0.11.4-py2.py3-none-any.whl" ]; then
    wget -q -O $VAPI_PDKROOT/build/artifactory-cache/Werkzeug-0.11.4-py2.py3-none-any.whl http://build-artifactory.eng.vmware.com/artifactory/pypi-virtual/packages/2.7/W/Werkzeug/Werkzeug-0.11.4-py2.py3-none-any.whl
fi
WERKZEUG=$(echo $VAPI_PDKROOT/build/artifactory-cache/Werkzeug-0.11.4-py2.py3-none-any.whl)

# Sphinx documentation dependencies
SPHINX_APIDOC=/build/toolchain/noarch/sphinx-1.1.3/bin/sphinx-apidoc
MAKE=/build/toolchain/lin32/make-3.82/bin/make
SPHINX_BUILD=/build/toolchain/noarch/sphinx-1.1.3/bin/sphinx-build
SPHINX=/build/toolchain/noarch/sphinx-1.1.3/lib/python2.6/site-packages
DOCUTILS=/build/toolchain/noarch/docutils-0.10/lib/python2.6/site-packages
JINJA=/build/toolchain/noarch/jinja2-2.5.5/lib/python2.6/site-packages
SPHINX_PYGMENTS=/build/toolchain/noarch/pygments-1.6/lib/python2.6/site-packages
SPHINX_LIBS=$SPHINX:$DOCUTILS:$JINJA:$SPHINX_PYGMENTS

# vAPI Libraries and its dependencies
PY_LIBRARIES=$OPENSSL:$TWISTED:$PYCRYPTO:$PEXPECT:$REQUESTS:$LXML:$ZOPE:$SIX:$SETUPTOOLS:$WERKZEUG:$PROMPT_TOOLKIT:$WCWIDTH:$PYGMENTS:$PTYPROCESS


VAPI_RUNTIME_PYTHON=$(echo $VAPI_PDKROOT/python-toolkit/runtime/vapi_runtime-*-py2.7.egg):$(echo $VAPI_PDKROOT/python-toolkit/runtime/vapi_common-*-py2.7.egg):$(echo $VAPI_PDKROOT/python-toolkit/runtime/vapi_common_client-*-py2.7.egg):$(echo $VAPI_PDKROOT/dcli/lin64/vapi_clients-*-py2.7.egg):$PY_LIBRARIES
# XXX Need to track down why pylint when run through pyutil.sh requires the following egg import order
VAPI_RUNTIME_PYTHON_ALT=$(echo $VAPI_PDKROOT/python-toolkit/runtime/vapi_common_client-*-py2.7.egg):$(echo $VAPI_PDKROOT/python-toolkit/runtime/vapi_common-*-py2.7.egg):$(echo $VAPI_PDKROOT/python-toolkit/runtime/vapi_runtime-*-py2.7.egg):$(echo $VAPI_PDKROOT/dcli/lin64/vapi_clients-*-py2.7.egg):$PY_LIBRARIES
VAPI_TEST_PYTHON=$(echo $VAPI_PDKROOT/python-toolkit/test/vapi_test-*-py2.7.egg):$(echo $VAPI_PDKROOT/python-toolkit/test/vapi_test_client-*-py2.7.egg)

# Resources for tests
export TEST_RESOURCES=$VAPI_PDKROOT/python-toolkit/test/metadata

