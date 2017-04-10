# vAPI
This is the vAPI exposed by vSphere Container Service.

## Build Instructions
1. Clone the project in a local directory
2. Install JDK 1.7, set environment variable "JAVA_HOME"
3. Install latest Apache Maven, set environment variable "PATH"
4. Set environment variable "VAPI_PDK": point to main project directory
5. Set environment variable "BUILD_ROOT": point to $VAPI_PDK/build
6. Copy vcs/java/settings.xml to ~/.m2/settings.xml
7. Set the value of "localRepository" in the settings.xml file to a directory path that you want to use as maven local repository
8. To build the project, run "maven clean install" in vcs/java/provider folder
9. To start the server, run build/maven/vcs/vcs-provider/appassembler/bin/vcs-provider
10. To test the server is actually running, open http://localhost:8088/rest in a browser - it should show a json containing rest metadata.

To add your own services to existing VCS vAPI project, please import the project to your Eclipse and follow the existing com.vmware.vcs.VmGroup service as an example:
1. VCS VMODL2 API definition: vcs/vmodl/com/vmware/vcs/VmGroup.vmodl
2. VCS VMODL2 provider implementation: vcs/java/provider/vcs/src/main/...
2. VCS VMODL2 provider unit tests: vcs/java/provider/vcs/src/test/...
