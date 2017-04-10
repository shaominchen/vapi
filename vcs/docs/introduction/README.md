#Overview
vAPI is about providing a unified programmatic interface to our products and services. Using a single implementation that could be based on Java, C++, or Python, it offers a variety of access methods desired by developers and administrators.
![](../images/vapiarch.jpg)

##High-level features
- Provider development kit for Java, Python & C++.
- REST API and documentation generation.
- SDK generation for Java, Python, C++, .Net, Ruby & Perl.
- DCLI - cross platform command line tool and man page generation.
- Auto-generation of low level PowerCLI cmdlets.
- Pluggable authentication - builtin support for SAML, OAuth2, User/Password based authentication.
- Pluggable authorization.

![](../images/codegen.png)

##Providing new APIs
To expose new APIs using vAPI framework, a provider has to
- Define APIs using VMODL2, the interface definition language used by vAPI.
- Generate skeletons for the supported language.
- Implement the services.

The framework provides tools to automatically generate artifacts to expose these APIs in all the SDKs, CLIs and REST.

##Consuming APIs
APIs exposed using vAPI can be consumed using
- SDK: Download the SDK for [Java](../developing_an_sdk_client/java.md), [Python](../developing_an_sdk_client/python.md), [C++](../developing_an_sdk_client/cpp.md), [.NET](../developing_an_sdk_client/dotnet.md), [Ruby](../developing_an_sdk_client/ruby.md) and Perl that contains stubs and generated API reference documentation for the target language.
- [REST](../client/rest.md): Use any off the shelf REST API client to invoke the APIs.
- [DCLI](../client/cli.md): Download the executable for your platform and run commands or write shell scripts using them.
- [PowerCLI](../client/powercli.md): Install VMware PowerCLI and use the vAPI plugin to access the APIs using commandlets.
