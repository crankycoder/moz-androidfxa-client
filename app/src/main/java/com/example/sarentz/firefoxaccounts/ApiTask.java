package com.example.sarentz.firefoxaccounts;

import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;

public class ApiTask extends AsyncTask<String, Void, ApiResponse>
{
    private final String accessToken;
    private final WeakReference<MainActivity> mainActivityReference;

    public ApiTask(String accessToken, MainActivity mainActivity) {
        this.accessToken = accessToken;
        this.mainActivityReference = new WeakReference<MainActivity>(mainActivity);
    }

    @Override
    protected ApiResponse doInBackground(String... strings) {
        final String url = "https://tmp.sateh.com/api/hello?name=Android";
        final AndroidHttpClient client = AndroidHttpClient.newInstance("Android");
        final HttpGet getRequest = new HttpGet(url);

        getRequest.addHeader("Authorization", "Bearer " + accessToken);

        try {
            HttpResponse response = client.execute(getRequest);
            final int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != HttpStatus.SC_OK) {
                Log.w("AuthorizationTask", "Error " + statusCode + " while calling " + url);
                return null;
            }


            StringBuilder message = new StringBuilder();
            HttpEntity entity = response.getEntity();
            InputStream content = entity.getContent();
            BufferedReader reader = new BufferedReader(new InputStreamReader(content));
            String line;
            while ((line = reader.readLine()) != null) {
                message.append(line);
            }

            return new ApiResponse(message.toString());
        } catch (IOException e) {
            getRequest.abort();
            Log.w("ApiTask", "Error while calling backend: " + e.toString());
        } finally {
            client.close();
        }

        return null;
    }

    @Override
    protected void onPostExecute(ApiResponse response) {
        if (!isCancelled()) {
            MainActivity mainActivity = mainActivityReference.get();
            if (mainActivity != null) {
                mainActivity.showApiResponse(response);
            }
        }
    }
}
