#!/bin/bash
test -e "${1}" && ABSPATH=`cd \`dirname "${1}"\`; pwd`"/"`basename "${1}"`
test -n "${ABSPATH}" && echo ${ABSPATH} && exit 0

## Comment this next line to hide the error message when an invalid path is given as the argument
echo Error: Could not find a file or directory at \"${1}\"\!; exit 1
