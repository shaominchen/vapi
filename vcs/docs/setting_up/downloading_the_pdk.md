{% tocTag %} {% endtocTag %}

# Overview
The tutorial instructions in this book are verified against the build specified in the overview page of the tutorial. However, if you want to try either a last official release build of vapi-pdk, latest stable build (or) latest development build, follow the instructions below to get the build and the tutorial instructions for that build.

# Download the latest official release build
The official release builds of vAPI development kit can be downloaded from [VMware developer center](https://developercenter.vmware.com/group/dp/common-components?id=8901). The official releases are performed on a quarterly basis. These releases are officially supported by the vAPI team. Product teams can use these builds in their product releases, and can request patches for critical bug fixes.

To use the tutorial, select a build from the `Builds` tab. Once you go to the appropriate build page on buildweb, download `vapi-core.zip` from the deliverables tab under `compcache/vapi-core/ob-<build-number>/<host-type>` for appropriate host type (linux64, windows-2008 or windows2012r2-vs2013).

{% highlight %}
If you download the PDK from the developer center, please use the tutorial instructions linked from the README tab.
{% endhighlight %}

# Download the latest stable build
To use the latest features under development, you can use the stable development build. Stable builds are released every two weeks. The releases are documented on [vAPI internal releases wiki](https://wiki.eng.vmware.com/VAPI/PDK/InternalReleases) page. The release wiki page also contains the information about official build, perforce CLN number and release notes. You can also get the latest internal release CLN number from the [Central Recommendation System](https://buildrecommend.eng.vmware.com/api/recommendations/plain/vapi-core/vapi-main/stable/).

Once you select the appropriate build from buildweb, download `vapi-core.zip` from the deliverables tab under `compcache/vapi-core/ob-<build-number>/<host-type>` for appropriate host type (linux64 or windows2012r2-vs2013-U3).

# Download the latest development build
Development builds of the PDK are available on [buildweb](https://buildweb.eng.vmware.com/ob/?product=vapi-core&branch=vapi-main). Select the most recent build. Download `vapi-core.zip` from the appropriate hosttype in the deliverables tab.
The tutorial instructions for every official released build can be found [here](http://vapi-jenkins.eng.vmware.com/job/vAPI_Core_Tutorial_Internal_Release/). Select the job link corresponding to the CLN number of the build from buildweb that you just downloaded.
