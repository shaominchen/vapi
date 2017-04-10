#!/bin/bash

JENKINS_URL="http://vapi-jenkins.eng.vmware.com"
JOBS="IDL DCLI Python Java Java_Performance Mix Cpp_Runtime"

TMP_DIR="/tmp/jenkinsJobs"
read -p "Enter username: " USERNAME
read -s -p "Enter password for user $USERNAME: " PASSWORD
CURL="curl -u $USERNAME:$PASSWORD -s "
echo ''

SRC_BRANCH="vAPI_Core"
DST_BRANCH="vAPI_Clone"

mkdir -p $TMP_DIR

for job in $JOBS
do
    echo "---------------------------------------------------------------------"
    SRC_PROJECT=${SRC_BRANCH}_${job}
    DST_PROJECT=${DST_BRANCH}_${job}

    SRC_CONFIG_URL="$JENKINS_URL/job/$SRC_PROJECT/config.xml"
    DST_CONFIG_URL="$JENKINS_URL/job/$DST_PROJECT/config.xml"

    SRC_CONFIG_XML="$TMP_DIR/$SRC_PROJECT-config.xml"
    DST_CONFIG_XML="$TMP_DIR/$DST_PROJECT-config.xml"

    # check for existing job
    echo "Checking if job $DST_PROJECT already exists"
    `$CURL -f -o /dev/null $DST_CONFIG_URL`
    if [ $? -eq 0 ]; then
        echo "$DST_PROJECT already exists on the jenkins server. Quitting..."
        continue
    fi

    # download the config.xml for the job, continue if it doesn't exist
    echo "Downloading base project config.xml ($SRC_CONFIG_URL)"
    `$CURL -o $SRC_CONFIG_XML $SRC_CONFIG_URL`
    grep "Error 404" "$SRC_CONFIG_XML" > /dev/null
    if [ $? -eq 0 ]; then
        echo "Error: $SRC_CONFIG_URL is not a valid project url"
        continue
    fi

    # replace the branch name in the config.xml file.
    SRC_BRANCH_STR=`echo $SRC_BRANCH | tr '[:upper:]' '[:lower:]' | tr '_' '-'`
    DST_BRANCH_STR=`echo $DST_BRANCH |  tr '[:upper:]' '[:lower:]' | tr '_' '-'`
    echo "Replacing branch name in config.xml from $SRC_BRANCH_STR to $DST_BRANCH_STR"
    sed "s/$SRC_BRANCH_STR/$DST_BRANCH_STR/g" "$SRC_CONFIG_XML" > "$DST_CONFIG_XML"

    echo "Creating the clone job"
    `$CURL -o /dev/null --data " " "$JENKINS_URL/createItem?name=${DST_PROJECT}&mode=copy&from=${SRC_PROJECT}"`

    # upload new job data
    echo "Upload the modified config.xml to the cloned job"
    `$CURL -o /dev/null --data-binary "@$DST_CONFIG_XML" "$JENKINS_URL/job/$DST_PROJECT/config.xml"`

    echo "Enabling the job"
    `$CURL -o /dev/null --data enable "$JENKINS_URL/job/$DST_PROJECT/enable"`
    done

    rm -rf $TMP_DIR
