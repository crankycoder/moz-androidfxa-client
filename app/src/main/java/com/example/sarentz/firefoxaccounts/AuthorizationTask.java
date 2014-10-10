// This Source Code Form is subject to the terms of the Mozilla Public
// License, v. 2.0. If a copy of the MPL was not distributed with this
// file, You can obtain one at http://mozilla.org/MPL/2.0/
// package com.example.sarentz.firefoxaccounts;

package com.example.sarentz.firefoxaccounts;

import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;

public class AuthorizationTask extends AsyncTask<AuthorizationResponse,Void, AuthorizationResult>
{
    private final WeakReference<LoginActivity> loginActivityReference;

    public AuthorizationTask(LoginActivity loginActivity) {
        this.loginActivityReference = new WeakReference<LoginActivity>(loginActivity);
    }

    @Override
    protected AuthorizationResult doInBackground(AuthorizationResponse... authorizationResponses) {
        final String url = "http://192.168.0.2:5000/oauth?code=" + authorizationResponses[0].getCode() + "&state=" + authorizationResponses[0].getState();
        final AndroidHttpClient client = AndroidHttpClient.newInstance("Android");
        final HttpGet getRequest = new HttpGet(url);

        try {
            HttpResponse response = client.execute(getRequest);
            final int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != HttpStatus.SC_OK) {
                Log.w("AuthorizationTask", "Error " + statusCode + " while calling " + url);
                return null;
            }

            StringBuilder json = new StringBuilder();
            HttpEntity entity = response.getEntity();
            InputStream content = entity.getContent();
            BufferedReader reader = new BufferedReader(new InputStreamReader(content));
            String line;
            while ((line = reader.readLine()) != null) {
                json.append(line);
            }

            JSONObject responseObject = new JSONObject(json.toString());

            return new AuthorizationResult(responseObject.getJSONObject("profile").getString("email"),
                    responseObject.getJSONObject("token").getString("access_token"));
        } catch (IOException e) {
            getRequest.abort();
            Log.w("AuthorizationTask", "Error while calling backend: " + e.toString());
        } catch (JSONException e) {
            Log.w("AuthorizationTask", "Error while parsing response JSON: " + e.toString());
        } finally {
            client.close();
        }

        return null;
    }

    @Override
    protected void onPostExecute(AuthorizationResult authorizationResult) {
        if (!isCancelled()) {
            LoginActivity loginActivity = loginActivityReference.get();
            if (loginActivity != null) {
                loginActivity.processAuthorizationResult(authorizationResult);
            }
        }
    }
}
