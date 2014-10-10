// This Source Code Form is subject to the terms of the Mozilla Public
// License, v. 2.0. If a copy of the MPL was not distributed with this
// file, You can obtain one at http://mozilla.org/MPL/2.0/
// package com.example.sarentz.firefoxaccounts;

package com.example.sarentz.firefoxaccounts;

import android.net.Uri;

public class AuthorizationResponse
{
    private final String code;
    private final String state;

    public String getCode() {
        return code;
    }

    public String getState() {
        return state;
    }

    public AuthorizationResponse(String code, String state) {
        this.code = code;
        this.state = state;
    }

    public static AuthorizationResponse fromUrl(String url) {
        // Uri can't deal with the redirect url we get back so we turn it into a 'normal' url
        url = "http://localhost/" + url.substring("urn:ietf:wg:oauth:2.0:oob".length());
        Uri uri = Uri.parse(url);
        return new AuthorizationResponse(uri.getQueryParameter("code"), uri.getQueryParameter("state"));
    }
}
