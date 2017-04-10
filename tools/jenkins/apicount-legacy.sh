workdir=$1
vmodldirs=${@:2}

rm -rf $workdir
mkdir -p $workdir
detail_file="$workdir/output.html"
count_file="$workdir/count.html"

export PATH=/build/toolchain/lin64/jdk-1.7.0_72/bin/:$PATH

function downloadVapiPdk () {
    current_build_no=`cat vapi-pdk/buildinfo.txt | grep "Build Number" | cut -f2 -d':' | cut -f2 -d' '`
    build_no=$(curl -s "http://buildapi.eng.vmware.com/ob/build/?product=vapi-core&branch=vapi-main&_format=json&buildstate=succeeded&_order_by=-id&_limit=1" | grep -Po '(?<="id":\W)\d+')
    if [ "$current_build_no" != "$build_no" ]; then
        # vapi-pdk.zip and vapi-pdk-linux64.zip are deprecated and are not used by this script anymore and hence need to be cleaned up from previous runs
        # This can be removed once all jobs are run once
        rm -f vapi-pdk-linux64.zip
        rm -f vapi-pdk.zip
        rm -f vapi-core.zip
        rm -rf vapi-pdk
    fi
    if [ ! -d "vapi-pdk" ]; then
        wget http://build-squid.eng.vmware.com/build/mts/release/bora-$build_no/compcache/vapi-core/ob-$build_no/linux64/vapi-core.zip
        unzip vapi-core.zip -d vapi-pdk
    fi
}

function get_operations {
    cat $1 | grep '"type": "command"' -B 2 | grep -v command | cut -f2 -d':' | sed -e 's/^[ \t]*//' | tr '\n' ' ' | sed -e 's/--/-/g' | tr '-' '\n' | sed -e 's/"//g' | sed 's/^[[:space:]]*//;s/[[:space:]]*$//' | sed -e 's/, /./g' | cut -f1 -d',' | sort | grep "\."
}

downloadVapiPdk

vapi-pdk/metadata-toolkit/bin/metadata-generator --library vapi-pdk/idl-toolkit/vmidl/vapi_stdlib.vmidl --profile cli --product-name demo $vmodldirs
operations=`get_operations build/src/metadata/*_cli.json`

operations_count=`echo $operations | tr ' ' '\n' | wc -l`
echo $operations_count > $count_file

echo "<!DOCTYPE html><html><head><style>
table, th, td
{
border-collapse:collapse;
border:1px solid black;
}
th, td
{
padding:15px;
}
</style></head><body>" > $detail_file

echo "<h2>" >> $detail_file
echo "Number of operations: "$operations_count"<br>" >> $detail_file
echo "</h2>" >> $detail_file

echo "<table>" >> $detail_file
for i in `echo $operations`
do
echo "<tr><td>" >> $detail_file
echo $i | tr '.' ' ' >> $detail_file
echo "</td></tr>" >> $detail_file
done
echo "</table></body></html>" >> $detail_file
