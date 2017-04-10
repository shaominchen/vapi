package com.vmware.vcs.impl;

import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.json.JSONException;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.vmware.vcs.VmGroup;

import com.vmware.vapi.bindings.StubFactory;
import com.vmware.vapi.protocol.JsonProtocolConnectionFactory;
import com.vmware.vapi.protocol.ProtocolConnection;

@ContextConfiguration(locations = {"classpath:spring/test.xml" })
public class VmGroupImplTest extends AbstractTestNGSpringContextTests {

    @Test
    public void testVapi() {
        JsonProtocolConnectionFactory pcf = new JsonProtocolConnectionFactory();
        ProtocolConnection conneciton = pcf.getInsecureConnection("http", "http://localhost:8088/api");
        StubFactory sf = new StubFactory(conneciton.getApiProvider());
        VmGroup vmGroup = sf.createStub(VmGroup.class);
        Assert.assertEquals(vmGroup.list().size(), 0);
    }

    @Test(dependsOnMethods = { "testVapi" })
    public void testCreateVm() throws ClientProtocolException, IOException, JSONException {
        CloseableHttpClient client = HttpClients.createDefault();
        String url = "http://localhost:8088/rest/com/vmware/vcs/vm-group";

        HttpPost httpRequest = new HttpPost(url);
        httpRequest.addHeader("Content-Type", "application/json");
        httpRequest.setEntity(new StringEntity("{ \"spec\": { \"name\": \"RESTful VM-GROUP\" } }"));

        HttpResponse response = client.execute(httpRequest);
        String content = IOUtils.toString(response.getEntity().getContent());

        Assert.assertEquals(response.getStatusLine().getStatusCode(), 200);
        JSONAssert.assertEquals("{\"value\":[ \"vg-1\" ]}", content, false);
    }

    @Test(dependsOnMethods = { "testCreateVm" })
    public void testUpdateVm() throws ClientProtocolException, IOException  {
        CloseableHttpClient client = HttpClients.createDefault();
        String url = "http://localhost:8088/rest/com/vmware/vcs/vm-group/vg-1";

        HttpPatch httpRequest = new HttpPatch(url);
        httpRequest.addHeader("Content-Type", "application/json");
        httpRequest.setEntity(new StringEntity("{ \"spec\": { \"description\": \"RESTful VM-GROUP DESCRIPTION\" } }"));

        HttpResponse response = client.execute(httpRequest);

        Assert.assertEquals(response.getStatusLine().getStatusCode(), 200);
    }

    @Test(dependsOnMethods = { "testUpdateVm" })
    public void testGetVmInfo() throws ClientProtocolException, IOException, JSONException {
        CloseableHttpClient client = HttpClients.createDefault();
        String url = "http://localhost:8088/rest/com/vmware/vcs/vm-group/vg-1";

        HttpGet httpRequest = new HttpGet(url);
        httpRequest.addHeader("Content-Type", "application/json");

        HttpResponse response = client.execute(httpRequest);
        String content = IOUtils.toString(response.getEntity().getContent());

        Assert.assertEquals(response.getStatusLine().getStatusCode(), 200);
        JSONAssert.assertEquals("{\n" +
            "    \"name\": \"RESTful VM-GROUP\", \n" +
            "    \"description\": \"RESTful VM-GROUP DESCRIPTION\", \n" +
            "    \"default_datastore\": \"\", \n" +
            "}", content, false);
    }

    @Test(dependsOnMethods = { "testGetVmInfo" })
    public void testDeleteVm() throws ClientProtocolException, IOException, JSONException {
        CloseableHttpClient client = HttpClients.createDefault();
        String url = "http://localhost:8088/rest/com/vmware/vcs/vm-group/vg-1";

        HttpDelete httpRequest = new HttpDelete(url);
        httpRequest.addHeader("Content-Type", "application/json");

        HttpResponse response = client.execute(httpRequest);

        Assert.assertEquals(response.getStatusLine().getStatusCode(), 200);
    }

    @Test(dependsOnMethods = { "testDeleteVm" })
    public void testListVms() throws IOException, JSONException {
        CloseableHttpClient client = HttpClients.createDefault();
        String url = "http://localhost:8088/rest/com/vmware/vcs/vm-group";

        HttpRequestBase httpRequest = new HttpGet(url);
        httpRequest.addHeader("Content-Type", "application/json");
        HttpResponse response = client.execute(httpRequest);

        String content = IOUtils.toString(response.getEntity().getContent());
        Assert.assertEquals(response.getStatusLine().getStatusCode(), 200);
        JSONAssert.assertEquals("{\"value\":[]}", content, false);
    }

    @Test
    public void testNavigation() throws ClientProtocolException, IOException, JSONException {
        CloseableHttpClient client = HttpClients.createDefault();
        String url = "http://localhost:8088/rest";

        HttpRequestBase httpRequest = new HttpGet(url);
        httpRequest.addHeader("Content-Type", "application/json");
        HttpResponse response = client.execute(httpRequest);

        String content = IOUtils.toString(response.getEntity().getContent());
        Assert.assertEquals(response.getStatusLine().getStatusCode(), 200);
        JSONAssert.assertEquals("{\n" +
            "    \"resources\": {\n" +
            "      \"method\": \"GET\",\n" +
            "      \"href\": \"http:\\/\\/localhost:8088\\/rest\\/com\\/vmware\\/vapi\\/rest\\/navigation\\/resource\",\n" +
            "      \"metadata\": {\n" +
            "        \"method\": \"GET\",\n" +
            "        \"href\": \"http:\\/\\/localhost:8088\\/rest\\/com\\/vmware\\/vapi\\/metadata\\/metamodel\\/service\\/operation\\/com.vmware.vapi.rest.navigation.resource\\/list\"\n" +
            "      }\n" +
            "    },\n" +
            "    \"components\": {\n" +
            "      \"method\": \"GET\",\n" +
            "      \"href\": \"http:\\/\\/localhost:8088\\/rest\\/com\\/vmware\\/vapi\\/rest\\/navigation\\/component\",\n" +
            "      \"metadata\": {\n" +
            "        \"method\": \"GET\",\n" +
            "        \"href\": \"http:\\/\\/localhost:8088\\/rest\\/com\\/vmware\\/vapi\\/metadata\\/metamodel\\/service\\/operation\\/com.vmware.vapi.rest.navigation.component\\/list\"\n" +
            "      }\n" +
            "    }\n" +
            "}", content, false);
    }

    @Test
    public void testListComponents() throws ClientProtocolException, IOException, JSONException {
        CloseableHttpClient client = HttpClients.createDefault();
        String url = "http://localhost:8088/rest/com/vmware/vapi/rest/navigation/component";

        HttpRequestBase httpRequest = new HttpGet(url);
        httpRequest.addHeader("Content-Type", "application/json");
        HttpResponse response = client.execute(httpRequest);

        String content = IOUtils.toString(response.getEntity().getContent());
        Assert.assertEquals(response.getStatusLine().getStatusCode(), 200);
        JSONAssert.assertEquals("{\n" +
            "  \"value\": [\n" +
            "    {\n" +
            "      \"services\": {\n" +
            "        \"method\": \"GET\",\n" +
            "        \"href\": \"http:\\/\\/localhost:8088\\/rest\\/com\\/vmware\\/vapi\\/rest\\/navigation\\/service?component_id=rest_navigation\",\n" +
            "        \"metadata\": {\n" +
            "          \"method\": \"GET\",\n" +
            "          \"href\": \"http:\\/\\/localhost:8088\\/rest\\/com\\/vmware\\/vapi\\/metadata\\/metamodel\\/service\\/operation\\/com.vmware.vapi.rest.navigation.service\\/list\"\n" +
            "        }\n" +
            "      },\n" +
            "      \"name\": \"rest_navigation\",\n" +
            "      \"documentation\": \"\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"services\": {\n" +
            "        \"method\": \"GET\",\n" +
            "        \"href\": \"http:\\/\\/localhost:8088\\/rest\\/com\\/vmware\\/vapi\\/rest\\/navigation\\/service?component_id=com.vmware.vapi\",\n" +
            "        \"metadata\": {\n" +
            "          \"method\": \"GET\",\n" +
            "          \"href\": \"http:\\/\\/localhost:8088\\/rest\\/com\\/vmware\\/vapi\\/metadata\\/metamodel\\/service\\/operation\\/com.vmware.vapi.rest.navigation.service\\/list\"\n" +
            "        }\n" +
            "      },\n" +
            "      \"name\": \"com.vmware.vapi\",\n" +
            "      \"documentation\": \"\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"services\": {\n" +
            "        \"method\": \"GET\",\n" +
            "        \"href\": \"http:\\/\\/localhost:8088\\/rest\\/com\\/vmware\\/vapi\\/rest\\/navigation\\/service?component_id=com.vmware.vcs\",\n" +
            "        \"metadata\": {\n" +
            "          \"method\": \"GET\",\n" +
            "          \"href\": \"http:\\/\\/localhost:8088\\/rest\\/com\\/vmware\\/vapi\\/metadata\\/metamodel\\/service\\/operation\\/com.vmware.vapi.rest.navigation.service\\/list\"\n" +
            "        }\n" +
            "      },\n" +
            "      \"name\": \"com.vmware.vcs\",\n" +
            "      \"documentation\": \"\"\n" +
            "    }\n" +
            "  ]\n" +
            "}", content, false);
    }

    @Test
    public void testListServices() throws ClientProtocolException, IOException, JSONException {
        CloseableHttpClient client = HttpClients.createDefault();
        String url = "http://localhost:8088/rest/com/vmware/vapi/rest/navigation/service?component_id=com.vmware.vcs";

        HttpRequestBase httpRequest = new HttpGet(url);
        httpRequest.addHeader("Content-Type", "application/json");
        HttpResponse response = client.execute(httpRequest);

        String content = IOUtils.toString(response.getEntity().getContent());
        Assert.assertEquals(response.getStatusLine().getStatusCode(), 200);
        JSONAssert.assertEquals("{\n" +
            "  \"value\": [\n" +
            "    {\n" +
            "      \"name\": \"com.vmware.vcs.vm_group\",\n" +
            "      \"documentation\": \"The {@name VmGroup} {@term service} provides {@term operations} to manage vmgroups for vSphere Container Service.\",\n" +
            "      \"method\": \"OPTIONS\",\n" +
            "      \"href\": \"http:\\/\\/localhost:8088\\/rest\\/com\\/vmware\\/vcs\\/vm-group\",\n" +
            "      \"metadata\": {\n" +
            "        \"method\": \"GET\",\n" +
            "        \"href\": \"http:\\/\\/localhost:8088\\/rest\\/com\\/vmware\\/vapi\\/metadata\\/metamodel\\/service\\/operation\\/com.vmware.vapi.rest.navigation.options\\/get\"\n" +
            "      }\n" +
            "    }\n" +
            "  ]\n" +
            "}", content, false);
    }
}
