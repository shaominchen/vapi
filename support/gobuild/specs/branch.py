# Copyright 2014 VMware, Inc.  All rights reserved. -- VMware Confidential
"""
Common location for defining branch variables.  Whenever possible a target
should use the variables defined here. This reduced the number of places
branch variables must be updated when the component branch changes.  The
upshot:

* Less overhead
* Less risk
* Less chance of targets consuming components from different locations

Targets can still point to <COMPONENT>_BRANCH variables in a target specific
spec file if there is a business reason to diverge from the common location.
"""

THIS_BRANCH='vapi-2016-q3'

VMKERNEL_MAIN_BRANCH='vmkernel-main'
