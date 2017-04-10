export TITLE=$1
PACKAGE=$2
VMODL=${@:3}

# Common exports
export VAPI_PDKROOT=$(pwd)
export VAPI_PDK=$(pwd)
export TCROOT=/build/toolchain
export PATH=$TCROOT/lin64/jdk-1.7.0_72/bin/:$PATH

VAPI_PDK_VERSION=2.6.0

# Java
export CLASSPATH=$TCROOT/noarch/apache-log4j-1.2.16/log4j-1.2.16.jar:$TCROOT/noarch/vfabric-tc-server-standard-2.8.2/tomcat-7.0.35.B.RELEASE/lib/*:$TCROOT/noarch/vfabric-tc-server-standard-2.8.2/tomcat-7.0.35.B.RELEASE/bin/*:$TCROOT/noarch/jetty-6.1.24/lib/*:$TCROOT/noarch/jetty-6.1.24/lib/ext/*:$TCROOT/noarch/apache-httpcore-4.3.2/lib/httpcore-4.3.2.jar:$TCROOT/noarch/apache-httpcore-4.3.2/lib/httpcore-nio-4.3.2.jar:$TCROOT/noarch/apache-httpclient-4.3.3/lib/httpclient-4.3.3.jar:$TCROOT/noarch/apache-httpcomponents-asyncclient-4.0.1/lib/httpasyncclient-4.0.1.jar:$TCROOT/noarch/apache-httpclient-4.3.3/lib/commons-logging-1.1.3.jar:$TCROOT/noarch/jackson-1.9.2/*:$TCROOT/noarch/rabbitmq-java-client-2.7.0/*:$TCROOT/noarch/slf4j-1.6.6/slf4j-api-1.6.6.jar:$TCROOT/noarch/slf4j-1.6.6/slf4j-log4j12-1.6.6.jar:$TCROOT/noarch/apache-commons-codec-1.6/commons-codec-1.6.jar:$TCROOT/noarch/apache-commons-lang-3.1/commons-lang3-3.1.jar:$CLASSPATH
java-toolkit/bin/java-generator --profile client --library idl-toolkit/vmidl/vapi_stdlib.vmidl $VMODL
javadoc -classpath lib/java/vapi-runtime-$VAPI_PDK_VERSION.jar:$CLASSPATH -d build/java/docs -sourcepath build/src/java -subpackages $PACKAGE

# Python
python-toolkit/bin/python-generator --profile client --library idl-toolkit/vmidl/vapi_stdlib.vmidl $VMODL
tools/docs/python.sh -i build/src/python -o build/python/docs

# CLI
metadata-toolkit/bin/clidoc-generator --library idl-toolkit/vmidl/vapi_stdlib.vmidl --output build/cli/ $VMODL
python tools/docs/dcli/dcli.py build/cli/clidoc/

# REST
rm -rf restdoc-toolkit
unzip lib/java/vapi-restdoc-$VAPI_PDK_VERSION.zip -d restdoc-toolkit
chmod u+x restdoc-toolkit/bin/restdoc
export VAPI_STD_VMODL='idl-toolkit/models/vapi/std/errors idl-toolkit/models/vapi/std/DynamicID.vmodl idl-toolkit/models/vapi/std/LocalizableMessage.vmodl idl-toolkit/models/vapi/std/AuthenticationScheme.vmodl'
restdoc-toolkit/bin/restdoc --library idl-toolkit/vmidl/vapi_stdlib.vmidl $VAPI_STD_VMODL $VMODL

# Ruby
ruby-toolkit/bin/ruby-generator --profile client --library idl-toolkit/vmidl/vapi_stdlib.vmidl $VMODL
yardoc -o build/rubydoc --title $TITLE build/src/ruby

# .NET
dotnet-toolkit/bin/csharp-generator --library idl-toolkit/vmidl/vapi_stdlib.vmidl $VMODL
doxygen -u tools/docs/dotnet/doxygen_config_file
mkdir -p build/dotnet/docs
doxygen -s tools/docs/dotnet/doxygen_config_file

# Perl
rm -rf perl-toolkit/perl-docs/Doxygen/codeloc/runtime
rm -rf perl-toolkit/perl-docs/Doxygen/codeloc/sdk
perl-toolkit/bin/perl-generator --library idl-toolkit/vmidl/vapi_stdlib.vmidl $VMODL
mkdir -p perl-toolkit/perl-docs/Doxygen/codeloc/sdk
cp -R build/src/perl/ perl-toolkit/perl-docs/Doxygen/codeloc/sdk
mkdir -p perl-toolkit/perl-docs/Doxygen/codeloc/runtime
cp -R perl-toolkit/perl-runtime/Com perl-toolkit/perl-docs/Doxygen/codeloc/runtime/Com
chmod u+x perl-toolkit/perl-docs/Doxygen/doxygen_script.sh
cp tools/docs/perl/doxygen_config_file perl-toolkit/perl-docs/Doxygen/doxy_config_file
./perl-toolkit/perl-docs/Doxygen/doxygen_script.sh /build/toolchain $(pwd)/perl-toolkit/perl-docs/Doxygen/codeloc/ build/perldoc build/temp $(pwd)/perl-toolkit/perl-docs/Doxygen/

# C++
vapicpp-emitter/bin/cpp-generator --library idl-toolkit/vmidl/vapi_stdlib.vmidl $VMODL
doxygen -u tools/docs/cpp/doxygen_config_file
mkdir -p build/cpp/docs
doxygen -s tools/docs/cpp/doxygen_config_file
