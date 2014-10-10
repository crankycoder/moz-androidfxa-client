// This Source Code Form is subject to the terms of the Mozilla Public
// License, v. 2.0. If a copy of the MPL was not distributed with this
// file, You can obtain one at http://mozilla.org/MPL/2.0/
// package com.example.sarentz.firefoxaccounts;

package com.example.sarentz.firefoxaccounts;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.webkit.ConsoleMessage;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class LoginActivity extends Activity
{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        final WebView webView = (WebView) findViewById(R.id.webView);
        webView.loadUrl("http://192.168.0.2:5000/login");

        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);

        webView.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, String url){
                if (url.startsWith("urn:ietf:wg:oauth:2.0:oob")) {
                    webView.loadData("Logging you in. This is still the webview. While we are talking to our backend server, we can show some progress here in the same style as the previous content.", "text/html", "UTF-8");
                    startVerificationFlow(url);
                } else {
                    view.loadUrl(url);
                }
                return false;
            }
        });

        webView.setWebChromeClient(new MyWebChromeClient());
    }

    void startVerificationFlow(String url) {
        AuthorizationResponse authorizationResponse = AuthorizationResponse.fromUrl(url);
        if (authorizationResponse != null) {
            AuthorizationTask authorizationTask = new AuthorizationTask(this);
            authorizationTask.execute(authorizationResponse);
        }
    }

    public void processAuthorizationResult(AuthorizationResult authorizationResult) {
        SharedPreferences preferences = getApplicationContext().getSharedPreferences("MySettings", 0);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("email", authorizationResult.getEmail());
        editor.putString("accessToken", authorizationResult.getAccessToken());
        editor.apply();

        final WebView webView = (WebView) findViewById(R.id.webView);
        webView.loadData("Logging in complete!", "text/html", "UTF-8");

        Intent resultData = new Intent();
        setResult(Activity.RESULT_OK, resultData);
        finish();
    }

    class MyWebChromeClient extends WebChromeClient
    {
        @Override
        public boolean onConsoleMessage(ConsoleMessage cm) {
            Log.d("CONTENT", String.format("%s @ %d: %s", cm.message(), cm.lineNumber(), cm.sourceId()));
            return true;
        }
    }
}
