{% tocTag %} {% endtocTag %}

# Overview

The PDK depends on several third-party libraries that are currently not bundled as part of the PDK package and must be downloaded and installed separately. Within VMware, these libraries are available in the toolchain directory

# General

The tutorial instructions are verified for Linux/Windows platform. Please use one of these as the development environment for using the tutorial.

Set the following environment variables according to their description:

-   `VAPI_PDK`: This is the main PDK directory (vapi-pdk) from which all the commands in this tutorial are executed from. Set to path of unzipped folder of PDK.
-   `PATH`: The skeleton, stub and metadata code generation tools require JDK 1.7 version to be in the PATH.
-   `TCROOT` (Optional): VMware's toolchain root directory. Set this if you want to use the dependent libraries from toolchain. Otherwise, it is not required, and you have to install/download the specific dependencies yourself. For example, you can use JDK from the VMware toolchain (or) use the JDK in your development machine.

{% tabs %}
[
  {
    "displayName": "Linux",
    "panelContentFilePath": "commands/linuxGeneral.txt"
  },
  {
    "displayName": "Windows",
    "panelContentFilePath": "commands/windowsGeneral.txt"
  }
]
{% endtabs %}

# Java

This section will describe the dependencies required for building and running Java based servers and clients. JDK 1.7 version is used by vAPI Java runtime libraries.

{% tabs %}
[
  {
    "displayName": "Linux",
    "panelContentFilePath": "commands/java/linuxJdk.txt"
  },
  {
    "displayName": "Windows",
    "panelContentFilePath": "commands/java/windowsJdk.txt"
  }
]
{% endtabs %}


## Maven
The Java server and client can be built and run in several different ways. We recommend using `maven`. If you want to use maven based instructions for building java provider and java client code, then you have to set the following environment variables to use `maven` from VMware toolchain (or) you can install maven directly.

{% tabs %}
[
  {
    "displayName": "Linux",
    "panelContentFilePath": "commands/java/linuxMaven.txt"
  },
  {
    "displayName": "Windows",
    "panelContentFilePath": "commands/java/windowsMaven.txt"
  }
]
{% endtabs %}

VMware hosts a maven repository that has many VMware produced jars as well as 3rd party jars. To make maven use this repository, copy the maven settings file provided with the PDK into the appropriate directory. The maven build installs vAPI java libraries into the local repository specified by the settings file.

{% tabs %}
[
  {
    "displayName": "Linux",
    "panelContentFilePath": "commands/java/linuxMvnSetting.txt"
  },
  {
    "displayName": "Windows",
    "panelContentFilePath": "commands/java/windowsMvnSetting.txt"
  }
]
{% endtabs %}

* On **Windows**, your `%HOMEPATH%` variable might be set to a network share in which case you need to findout where maven searches for user `settings.xml`. Execute `mvn -X` which will print where you need to copy `toyvm\java\settings.xml`.
* After copying the settings.xml file, set the value of `localRepository` in the settings.xml file to a directory path that you want to use as maven local repository.

# Python

Dependencies of vAPI python runtime are

-   Python 2.7, 3.3 or 3.4
-   [six](https://pypi.python.org/pypi/six) - Python 2 and 3 compatibility library.
-   Optional: [pyOpenSSL](https://pypi.python.org/pypi/pyOpenSSL) (Required if you need SSL communication)
-   Optional: [Twisted](https://pypi.python.org/pypi/Twisted) (Required if you are using the default twisted based web server as the server)
-   Optional: [requests](https://pypi.python.org/pypi/requests) (Required if you want to use requests instead of default http client libraries)

To run the python server and client, you can either install the above packages manually in your operating system by using the steps outlined in virtualenv or you can use instructions to use the libraries in toolchain.

## virtualenv

Virtualenv enables multiple side-by-side installations of Python, one for each project. It doesn’t actually install separate copies of Python, but it does provide a clever way to keep different project environments isolated. If you need to work with different version of python, [pyenv](https://github.com/yyuu/pyenv) makes it simpler to install and manage various python installations.

### Install virtualenv
It is possible that your package manager contains `virtualenv`. If you use Ubuntu, try:
{% panel %}
{
  "panelContentFilePath": "commands/python/installVirtualEnv.txt"
}
{% endpanel %}

If the above doesn't work and if you are on Mac OS X or Linux, chances are that one of the following two commands will work for you:
- `sudo easy_install virtualenv`
- `sudo pip install virtualenv`

### Install dependencies
When using `pip` to install packages, dependencies are installed automatically.

{% panel %}
{
  "panelContentFilePath": "commands/python/dependenciesVirtualEnv.txt",
  "collapsedContentFilePath": "outputs/python/dependenciesVirtualEnv.txt"
}
{% endpanel %}

**Note**: vAPI Python server is wsgi-compatible, so you can use any web server that is [wsgi compatible](http://wsgi.readthedocs.org/en/latest/servers.html). `vmware.vapi.wsgi.application` is the wsgi application that can be used with the wsgi server.

## Toolchain

Using toolchain is not a recommended way forward. However, if for some reason the above steps don't work for you. You can use the libraries from toolchain.

{% tabs %}
[
  {
    "displayName": "Linux - 64 bit",
    "panelContentFilePath": "commands/python/tcLinux64.txt"
  },
  {
    "displayName": "Linux - 32 bit",
    "panelContentFilePath": "commands/python/tcLinux32.txt"
  },
  {
    "displayName": "Windows - 32 bit",
    "panelContentFilePath": "commands/python/tcWindows32.txt"
  }
]
{% endtabs %}

#C++
The vAPI-C++ deliverables are available at `$VAPI_PDK/toyvm/cpp`.
It is composed of several components - vapi-core, vapi-http, vapi-introspection, vapi-json, vapi-providerpool, vapi-std-bindings. Each component contains several directories:

-   The `include` directory contains includes; the interface.
-   The `bin` directory on Windows contains the dynamic library.
-   The `lib` directory on Linux contains the stripped dynamic and static libraries, on Windows contains the static library.
-   The `lib-debug` directory on Linux contains the unstripped dynamic and static libraries.

Components that are implemented as header only libraries have only the `include` directory.

***Note***: Under windows Visual Studio should be installed on the machine and all commands should be run from the Visual Studio Command Prompt.

##Third party components
Create a directory for the required third party components:
<div>
    <!-- Nav tabs -->
    <ul class="nav nav-tabs">
        <li class="active">
            <a id="linCppMkdirTab" href="linCppMkdir" data-toggle="tab">Linux</a>
        </li>
        <li>
            <a id="winCppMkdirTab" href="#winCppMkdir" data-toggle="tab">Windows</a>
        </li>
    </ul>
    <div class="codePart">
        <!-- Tab panes -->
        <div class="tab-content">
            <div class="tab-pane active" id="linCppMkdir">
               vapi-pdk$ cd toyvm/cpp<br>
               vapi-pdk/toyvm/cpp$ mkdir components<br>
            </div>
            <div class="tab-pane" id="winCppMkdir">
               vapi-pdk> cd toyvm\cpp<br>
               vapi-pdk/toyvm/cpp> mkdir components<br>
            </div>
        </div>
    </div>
</div>

####boost
Download the latest build of 'boost1550_lin64_gcc480' or 'boost1550_win64_vc120'  from `main` branch from gobuild.
<div>
    <!-- Nav tabs -->
    <ul class="nav nav-tabs">
        <li class="active">
            <a id="linBoostTab" href="#linBoost" data-toggle="tab">Linux</a>
        </li>
        <li>
            <a id="winBoostTab" href="#winBoost" data-toggle="tab">Windows</a>
        </li>
    </ul>
    <div class="codePart">
        <!-- Tab panes -->
        <div class="tab-content">
            <div class="tab-pane active" id="linBoost">
               vapi-pdk/toyvm/cpp$ wget http://build-squid.eng.vmware.com/build/mts/release/bora-2919628/compcache/boost1550_lin64_gcc480/ob-2919628/linux64/boost1550_lin64_gcc480.zip<br>
               vapi-pdk/toyvm/cpp$ unzip boost1550_lin64_gcc480.zip -d components/boost1550_lin64_gcc480 && rm -f boost1550_lin64_gcc480.zip<br>
            </div>
            <div class="tab-pane" id="winBoost">
               vapi-pdk/toyvm/cpp> wget http://build-squid.eng.vmware.com/build/mts/release/bora-2919631/compcache/boost1550_win64_vc120/ob-2919631/windows2012r2-vs2013/boost1550_win64_vc120.zip<br>
               vapi-pdk/toyvm/cpp> unzip boost1550_win64_vc120.zip -d components/boost1550_win64_vc120 && rm -f boost1550_win64_vc120.zip<br>
            </div>
        </div>
    </div>
</div>

#### cmake
Download the latest build of  cmake (`cayman_cmake_lin32` or `cayman_cmake_win32`) from `2.8.12.1` branch from gobuild.
<div>
    <!-- Nav tabs -->
    <ul class="nav nav-tabs">
        <li class="active">
            <a id="linCMakeTab" href="#linCMake" data-toggle="tab">Linux</a>
        </li>
        <li>
            <a id="winCMakeTab" href="#winCMake" data-toggle="tab">Windows</a>
        </li>
    </ul>
    <div class="codePart">
        <!-- Tab panes -->
        <div class="tab-content">
            <div class="tab-pane active" id="linCMake">
               vapi-pdk/toyvm/cpp$ wget http://build-squid.eng.vmware.com/build/mts/release/bora-2840615/compcache/cayman_cmake_lin32/ob-2840615/linux/cayman_cmake_lin32.zip<br>
               vapi-pdk/toyvm/cpp$ unzip cayman_cmake_lin32.zip -d components/cayman_cmake_lin32 && rm -f cayman_cmake_lin32.zip<br>
            </div>
            <div class="tab-pane" id="winCMake">
               vapi-pdk/toyvm/cpp> wget http://build-squid.eng.vmware.com/build/mts/release/bora-2812301/compcache/cayman_cmake_win32/ob-2812301/windows/cayman_cmake_win32.zip<br>
               vapi-pdk/toyvm/cpp> unzip cayman_cmake_win32.zip -d components/cayman_cmake_win32 && rm -f cayman_cmake_win32.zip<br>
            </div>
        </div>
    </div>
</div>

####scons
Download the latest build of `cayman_scons` from `release-2.2.0` branch from gobuild.
<div class="codePart">
  vapi-pdk/toyvm/cpp$ wget http://build-squid.eng.vmware.com/build/mts/release/bora-972767/compcache/cayman_scons/ob-972767/linux/cayman_scons.zip<br>
  vapi-pdk/toyvm/cpp$ unzip cayman_scons.zip -d components/cayman_scons && rm -f cayman_scons.zip<br>
</div>

####pion
Download the latest build of `cayman_pion` from `vmware-develop` branch from gobuild.
<div>
    <!-- Nav tabs -->
    <ul class="nav nav-tabs">
        <li class="active">
            <a id="linPionTab" href="#linPion" data-toggle="tab">Linux</a>
        </li>
        <li>
            <a id="winPionTab" href="#winPion" data-toggle="tab">Windows</a>
        </li>
    </ul>
    <div class="codePart">
        <!-- Tab panes -->
        <div class="tab-content">
            <div class="tab-pane active" id="linPion">
               vapi-pdk/toyvm/cpp$ wget http://build-squid.eng.vmware.com/build/mts/release/bora-2452668/compcache/cayman_pion/ob-2452668/linux64/cayman_pion.zip<br>
               vapi-pdk/toyvm/cpp$ unzip cayman_pion.zip -d components/cayman_pion && rm -f cayman_pion.zip<br>
            </div>
            <div class="tab-pane" id="winPion">
               vapi-pdk/toyvm/cpp> wget http://build-squid.eng.vmware.com/build/mts/release/bora-2452668/compcache/cayman_pion/ob-2452668/windows2012r2-vs2013/cayman_pion.zip<br>
               vapi-pdk/toyvm/cpp> unzip cayman_pion.zip -d components/cayman_pion && rm -f cayman_pion.zip<br>
            </div>
        </div>
    </div>
</div>

####cayman_esx_toolchain
Download the latest build of `cayman_esx_toolchain` from `vmware-gcc48` branch from gobuild.
<div>
    <!-- Nav tabs -->
    <ul class="nav nav-tabs">
        <li class="active">
            <a id="linGccToolchainTab" href="#linGccToolchain" data-toggle="tab">Linux</a>
        </li>
    </ul>
    <div class="codePart">
        <!-- Tab panes -->
        <div class="tab-content">
            <div class="tab-pane active" id="linGccToolchain">
               vapi-pdk/toyvm/cpp$ wget http://build-squid.eng.vmware.com/build/mts/release/bora-2537876/compcache/cayman_esx_toolchain/ob-2537876/linux64/cayman_esx_toolchain.zip<br>
               vapi-pdk/toyvm/cpp$ unzip cayman_esx_toolchain.zip -d components/cayman_esx_toolchain && rm -f cayman_esx_toolchain.zip<br>
            </div>
        </div>
    </div>
</div>
