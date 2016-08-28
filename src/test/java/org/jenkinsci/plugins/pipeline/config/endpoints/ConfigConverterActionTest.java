/*
 * The MIT License
 *
 * Copyright (c) 2016, CloudBees, Inc.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.jenkinsci.plugins.pipeline.config.endpoints;

import com.gargoylesoftware.htmlunit.HttpMethod;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.util.NameValuePair;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.jenkinsci.plugins.pipeline.config.AbstractConfigTest;
import org.jenkinsci.plugins.pipeline.config.model.BuildCondition;
import org.jenkinsci.plugins.pipeline.config.model.Tools;
import org.junit.Test;
import org.junit.runners.model.Statement;
import org.jvnet.hudson.test.JenkinsRule;

import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class ConfigConverterActionTest extends AbstractConfigTest {

    @Test
    public void doSchema() throws Exception {
        JenkinsRule.WebClient wc = j.createWebClient();
        String rawSchema = wc.goTo(ConfigConverterAction.PIPELINE_CONFIG_URL + "/schema", "application/json").getWebResponse().getContentAsString();
        assertNotNull(rawSchema);
        JSONObject remoteSchema = JSONObject.fromObject(rawSchema);
        assertNotNull(remoteSchema);
        assertFalse(remoteSchema.isEmpty());
        assertFalse(remoteSchema.isNullObject());

        String rawInternalSchema = fileContentsFromResources("ast-schema.json");
        assertNotNull(rawInternalSchema);
        JSONObject internalSchema = JSONObject.fromObject(rawInternalSchema);

        assertNotNull(internalSchema);
        assertFalse(internalSchema.isEmpty());
        assertFalse(internalSchema.isNullObject());

        assertEquals(internalSchema, remoteSchema);
    }

    @Test
    public void testFailedValidateJsonInvalidBuildCondition() throws Exception {
        JenkinsRule.WebClient wc = j.createWebClient();
        WebRequest req = new WebRequest(wc.createCrumbedUrl(ConfigConverterAction.PIPELINE_CONFIG_URL + "/validateJson"), HttpMethod.POST);
        String simpleJson = fileContentsFromResources("json/errors/invalidBuildCondition.json");

        assertNotNull(simpleJson);

        NameValuePair pair = new NameValuePair("json", simpleJson);
        req.setRequestParameters(Collections.singletonList(pair));

        String rawResult = wc.getPage(req).getWebResponse().getContentAsString();
        assertNotNull(rawResult);

        JSONObject result = JSONObject.fromObject(rawResult);
        // TODO: Change this when we get proper JSON errors causing HTTP error codes
        assertEquals("Full result doesn't include status - " + result.toString(2), "ok", result.getString("status"));
        JSONObject resultData = result.getJSONObject("data");
        assertNotNull(resultData);
        assertEquals("Result wasn't a failure - " + result.toString(2), "failure", resultData.getString("result"));

        String expectedError = "Invalid condition 'banana' - valid conditions are " + BuildCondition.getConditionMethods().keySet();
        assertTrue("Errors array (" + resultData.getJSONArray("errors").toString(2) + ") didn't contain expected error '" + expectedError + "'",
                foundExpectedError(expectedError, resultData.getJSONArray("errors")));
    }

    @Test
    public void testFailedValidateJenkinsfileInvalidBuildCondition() throws Exception {
        final String rawJenkinsfile = fileContentsFromResources("errors/invalidBuildCondition.groovy", true);
        // TODO: Get a better approach for skipping JSON-specific errors
        if (rawJenkinsfile != null) {

            JenkinsRule.WebClient wc = j.createWebClient();
            WebRequest req = new WebRequest(wc.createCrumbedUrl(ConfigConverterAction.PIPELINE_CONFIG_URL + "/validateJenkinsfile"), HttpMethod.POST);

            assertNotNull(rawJenkinsfile);

            NameValuePair pair = new NameValuePair("jenkinsfile", rawJenkinsfile);
            req.setRequestParameters(Collections.singletonList(pair));

            String rawResult = wc.getPage(req).getWebResponse().getContentAsString();
            assertNotNull(rawResult);

            JSONObject result = JSONObject.fromObject(rawResult);
            // TODO: Change this when we get proper JSON errors causing HTTP error codes
            assertEquals("Full result doesn't include status - " + result.toString(2), "ok", result.getString("status"));
            JSONObject resultData = result.getJSONObject("data");
            assertNotNull(resultData);
            assertEquals("Result wasn't a failure - " + result.toString(2), "failure", resultData.getString("result"));
        }
    }

    @Test
    public void testFailedValidateJsonUnlistedToolType() throws Exception {
        JenkinsRule.WebClient wc = j.createWebClient();
        WebRequest req = new WebRequest(wc.createCrumbedUrl(ConfigConverterAction.PIPELINE_CONFIG_URL + "/validateJson"), HttpMethod.POST);
        String simpleJson = fileContentsFromResources("json/errors/unlistedToolType.json");

        assertNotNull(simpleJson);

        NameValuePair pair = new NameValuePair("json", simpleJson);
        req.setRequestParameters(Collections.singletonList(pair));

        String rawResult = wc.getPage(req).getWebResponse().getContentAsString();
        assertNotNull(rawResult);

        JSONObject result = JSONObject.fromObject(rawResult);
        // TODO: Change this when we get proper JSON errors causing HTTP error codes
        assertEquals("Full result doesn't include status - " + result.toString(2), "ok", result.getString("status"));
        JSONObject resultData = result.getJSONObject("data");
        assertNotNull(resultData);
        assertEquals("Result wasn't a failure - " + result.toString(2), "failure", resultData.getString("result"));

        String expectedError = "Invalid tool type 'banana'. Valid tool types: " + Tools.getAllowedToolTypes().keySet();

        assertTrue("Errors array (" + resultData.getJSONArray("errors").toString(2) + ") didn't contain expected error '" + expectedError + "'",
                foundExpectedError(expectedError, resultData.getJSONArray("errors")));
    }

    @Test
    public void testFailedValidateJenkinsfileUnlistedToolType() throws Exception {
        final String rawJenkinsfile = fileContentsFromResources("errors/unlistedToolType.groovy", true);
        // TODO: Get a better approach for skipping JSON-specific errors
        if (rawJenkinsfile != null) {

            JenkinsRule.WebClient wc = j.createWebClient();
            WebRequest req = new WebRequest(wc.createCrumbedUrl(ConfigConverterAction.PIPELINE_CONFIG_URL + "/validateJenkinsfile"), HttpMethod.POST);

            assertNotNull(rawJenkinsfile);

            NameValuePair pair = new NameValuePair("jenkinsfile", rawJenkinsfile);
            req.setRequestParameters(Collections.singletonList(pair));

            String rawResult = wc.getPage(req).getWebResponse().getContentAsString();
            assertNotNull(rawResult);

            JSONObject result = JSONObject.fromObject(rawResult);
            // TODO: Change this when we get proper JSON errors causing HTTP error codes
            assertEquals("Full result doesn't include status - " + result.toString(2), "ok", result.getString("status"));
            JSONObject resultData = result.getJSONObject("data");
            assertNotNull(resultData);
            assertEquals("Result wasn't a failure - " + result.toString(2), "failure", resultData.getString("result"));
        }
    }

    private boolean foundExpectedError(String expectedError, JSONArray errors) {
        for (Object e : JSONArray.toCollection(errors, String.class)) {
            if (e instanceof String) {
                if (e.equals(expectedError)) {
                    return true;
                }
            }
        }

        return false;
    }

}
