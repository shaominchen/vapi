# vAPI
This is the vAPI (a.k.a. VMODL2) exposed by vSphere Container Service.

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
10. To test the server is actually running, open https://localhost:8443/rest in a browser - it should show a json containing rest metadata.

To exercise the deployed REST APIs, it's recommended to use Postman client. To use Postman, download "Postman for Chrome", import the workflow available in vcs/tools/postman.json file. This workflow has included all the VmGroup REST API requests. Once the import is successful, you should be able to invoke VmGroup REST APIs by clicking some buttons in Postman client.

To add your own services to existing VCS vAPI project, please import the project to your Eclipse and follow the existing com.vmware.vcs.VmGroup service as an example:
1. VCS vAPI API definition: vcs/vmodl/com/vmware/vcs/VmGroup.vmodl
2. VCS vAPI provider implementation: vcs/java/provider/vcs/src/main/...
2. VCS vAPI provider unit tests: vcs/java/provider/vcs/src/test/...

For more detailed instructions, please refer to vAPI PDK Tutorial: http://vapi-jenkins.eng.vmware.com/job/vAPI_2016_Q3_Tutorial/vAPI_PDK_Tutorial/index.html
